# 付録B.10: ハッシュテーブルとレッドブラックツリーの内部実装

## 概要

本付録では、Javaコレクションフレームワークの中核を成すHashMapとTreeMapの内部実装について詳細に解説します。これらのデータ構造がどのように高性能を実現しているか、衝突処理やツリーのバランシングなど、実装の詳細を理解することで、適切なデータ構造の選択と最適化が可能になります。

**対象読者**: コレクションフレームワークの基本を理解し、内部実装に興味がある開発者  
**前提知識**: 第8章「コレクションフレームワーク」の内容、基本的なアルゴリズムとデータ構造  
**関連章**: 第8章、第16章（ConcurrentHashMapの実装）

---

## HashMapの内部構造

### 基本的なハッシュテーブル

```java
// HashMapの簡略化された内部構造
public class SimpleHashMap<K, V> {
    // ノードの定義
    static class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;
        
        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
    
    // バケット配列
    Node<K, V>[] table;
    int size;
    final float loadFactor;
    int threshold;
    
    @SuppressWarnings("unchecked")
    public SimpleHashMap(int initialCapacity, float loadFactor) {
        this.loadFactor = loadFactor;
        this.threshold = (int)(initialCapacity * loadFactor);
        this.table = (Node<K, V>[]) new Node[initialCapacity];
    }
    
    // ハッシュ値の計算（Java 8以降の実装）
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    
    // バケットインデックスの計算
    static int indexFor(int hash, int length) {
        return hash & (length - 1); // lengthは2の累乗
    }
}
```

### Java 8での改良：ツリー化

```java
// Java 8以降のHashMapではチェーンが長くなるとツリー化
static final int TREEIFY_THRESHOLD = 8;  // ツリー化の閾値
static final int UNTREEIFY_THRESHOLD = 6; // リスト化の閾値
static final int MIN_TREEIFY_CAPACITY = 64; // ツリー化に必要な最小容量

// TreeNodeの定義（簡略版）
static final class TreeNode<K, V> extends Node<K, V> {
    TreeNode<K, V> parent;
    TreeNode<K, V> left;
    TreeNode<K, V> right;
    TreeNode<K, V> prev;
    boolean red;
    
    TreeNode(int hash, K key, V val, Node<K, V> next) {
        super(hash, key, val, next);
    }
    
    // 赤黒木としての操作メソッド
    final TreeNode<K, V> root() {
        for (TreeNode<K, V> r = this, p;;) {
            if ((p = r.parent) == null)
                return r;
            r = p;
        }
    }
    
    // ツリー内での検索
    final TreeNode<K, V> find(int h, Object k, Class<?> kc) {
        TreeNode<K, V> p = this;
        do {
            int ph, dir; K pk;
            TreeNode<K, V> pl = p.left, pr = p.right, q;
            if ((ph = p.hash) > h)
                p = pl;
            else if (ph < h)
                p = pr;
            else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                return p;
            else if (pl == null)
                p = pr;
            else if (pr == null)
                p = pl;
            else if ((kc != null || (kc = comparableClassFor(k)) != null) &&
                     (dir = compareComparables(kc, k, pk)) != 0)
                p = (dir < 0) ? pl : pr;
            else if ((q = pr.find(h, k, kc)) != null)
                return q;
            else
                p = pl;
        } while (p != null);
        return null;
    }
}
```

### リサイズとリハッシュ

```java
// リサイズ処理の実装
final Node<K, V>[] resize() {
    Node<K, V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    
    // 新しい容量の計算
    if (oldCap > 0) {
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // 閾値を2倍に
    }
    
    // 新しいテーブルの作成
    @SuppressWarnings("unchecked")
    Node<K, V>[] newTab = (Node<K, V>[])new Node[newCap];
    table = newTab;
    
    // 要素の再配置
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K, V> e;
            if ((e = oldTab[j]) != null) {
                oldTab[j] = null;
                if (e.next == null)
                    // 単一ノード
                    newTab[e.hash & (newCap - 1)] = e;
                else if (e instanceof TreeNode)
                    // ツリーノードの分割
                    ((TreeNode<K, V>)e).split(this, newTab, j, oldCap);
                else {
                    // リンクリストの保持順序を維持した分割
                    Node<K, V> loHead = null, loTail = null;
                    Node<K, V> hiHead = null, hiTail = null;
                    Node<K, V> next;
                    do {
                        next = e.next;
                        // 元のインデックスに残る要素
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null)
                                loHead = e;
                            else
                                loTail.next = e;
                            loTail = e;
                        }
                        // 新しいインデックスに移動する要素
                        else {
                            if (hiTail == null)
                                hiHead = e;
                            else
                                hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```

---

## TreeMapとレッドブラックツリー

### レッドブラックツリーの性質

```java
// レッドブラックツリーの基本構造
public class RedBlackTree<K extends Comparable<K>, V> {
    private static final boolean RED = true;
    private static final boolean BLACK = false;
    
    private class Node {
        K key;
        V value;
        Node left, right, parent;
        boolean color;
        
        Node(K key, V value, boolean color, Node parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }
    }
    
    private Node root;
    private int size = 0;
    
    // レッドブラックツリーの不変条件
    // 1. ルートは黒
    // 2. 赤ノードの子は黒
    // 3. すべての葉（null）から根までの黒ノード数は同じ
    // 4. 新規ノードは赤で挿入
}
```

### 挿入操作と修復

```java
// 挿入後の修復処理
private void fixAfterInsertion(Node x) {
    x.color = RED;
    
    while (x != null && x != root && x.parent.color == RED) {
        if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
            Node y = rightOf(parentOf(parentOf(x)));
            if (colorOf(y) == RED) {
                // ケース1: 叔父が赤
                setColor(parentOf(x), BLACK);
                setColor(y, BLACK);
                setColor(parentOf(parentOf(x)), RED);
                x = parentOf(parentOf(x));
            } else {
                if (x == rightOf(parentOf(x))) {
                    // ケース2: 叔父が黒、xが右の子
                    x = parentOf(x);
                    rotateLeft(x);
                }
                // ケース3: 叔父が黒、xが左の子
                setColor(parentOf(x), BLACK);
                setColor(parentOf(parentOf(x)), RED);
                rotateRight(parentOf(parentOf(x)));
            }
        } else {
            // 対称的なケース
            Node y = leftOf(parentOf(parentOf(x)));
            if (colorOf(y) == RED) {
                setColor(parentOf(x), BLACK);
                setColor(y, BLACK);
                setColor(parentOf(parentOf(x)), RED);
                x = parentOf(parentOf(x));
            } else {
                if (x == leftOf(parentOf(x))) {
                    x = parentOf(x);
                    rotateRight(x);
                }
                setColor(parentOf(x), BLACK);
                setColor(parentOf(parentOf(x)), RED);
                rotateLeft(parentOf(parentOf(x)));
            }
        }
    }
    root.color = BLACK;
}

// 左回転
private void rotateLeft(Node p) {
    if (p != null) {
        Node r = p.right;
        p.right = r.left;
        if (r.left != null)
            r.left.parent = p;
        r.parent = p.parent;
        if (p.parent == null)
            root = r;
        else if (p.parent.left == p)
            p.parent.left = r;
        else
            p.parent.right = r;
        r.left = p;
        p.parent = r;
    }
}
```

### 削除操作と修復

```java
// 削除後の修復処理（より複雑）
private void fixAfterDeletion(Node x) {
    while (x != root && colorOf(x) == BLACK) {
        if (x == leftOf(parentOf(x))) {
            Node sib = rightOf(parentOf(x));
            
            if (colorOf(sib) == RED) {
                // ケース1: 兄弟が赤
                setColor(sib, BLACK);
                setColor(parentOf(x), RED);
                rotateLeft(parentOf(x));
                sib = rightOf(parentOf(x));
            }
            
            if (colorOf(leftOf(sib)) == BLACK &&
                colorOf(rightOf(sib)) == BLACK) {
                // ケース2: 兄弟が黒、両方の子も黒
                setColor(sib, RED);
                x = parentOf(x);
            } else {
                if (colorOf(rightOf(sib)) == BLACK) {
                    // ケース3: 兄弟が黒、左の子が赤、右の子が黒
                    setColor(leftOf(sib), BLACK);
                    setColor(sib, RED);
                    rotateRight(sib);
                    sib = rightOf(parentOf(x));
                }
                // ケース4: 兄弟が黒、右の子が赤
                setColor(sib, colorOf(parentOf(x)));
                setColor(parentOf(x), BLACK);
                setColor(rightOf(sib), BLACK);
                rotateLeft(parentOf(x));
                x = root;
            }
        } else {
            // 対称的なケース
            // ...
        }
    }
    setColor(x, BLACK);
}
```

---

## ConcurrentHashMapの実装

### セグメント化とロック分割

```java
// Java 7までのセグメント方式（概念的）
class SegmentedConcurrentHashMap<K, V> {
    static class Segment<K, V> extends ReentrantLock {
        volatile HashEntry<K, V>[] table;
        int count;
        int modCount;
        float loadFactor;
        
        V put(K key, int hash, V value, boolean onlyIfAbsent) {
            lock();
            try {
                int index = hash & (table.length - 1);
                HashEntry<K, V> first = table[index];
                // 挿入処理
                return null;
            } finally {
                unlock();
            }
        }
    }
    
    final Segment<K, V>[] segments;
    
    public V put(K key, V value) {
        int hash = hash(key);
        int segmentIndex = (hash >>> segmentShift) & segmentMask;
        return segments[segmentIndex].put(key, hash, value, false);
    }
}
```

### Java 8の改良：CASベース実装

```java
// Java 8以降のCAS操作を使った実装
public class ModernConcurrentHashMap<K, V> {
    // ノード定義
    static class Node<K, V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K, V> next;
        
        Node(int hash, K key, V val, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.val = val;
            this.next = next;
        }
    }
    
    // CAS操作
    static final <K, V> boolean casTabAt(Node<K, V>[] tab, int i,
                                        Node<K, V> c, Node<K, V> v) {
        return U.compareAndSwapObject(tab, ((long)i << ASHIFT) + ABASE, c, v);
    }
    
    // 挿入操作
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) throw new NullPointerException();
        int hash = spread(key.hashCode());
        int binCount = 0;
        
        for (Node<K, V>[] tab = table;;) {
            Node<K, V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                // CASで新規ノードを挿入
                if (casTabAt(tab, i, null,
                           new Node<K, V>(hash, key, value, null)))
                    break;
            }
            else if ((fh = f.hash) == MOVED)
                // リサイズ中
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        // 通常のチェーン処理
                        // ...
                    }
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }
}
```

---

## パフォーマンス特性と最適化

### ハッシュ関数の重要性

```java
// 良いハッシュ関数の例
public class OptimizedHashCode {
    // 文字列用の効率的なハッシュ
    static int stringHash(String s) {
        int h = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            h = 31 * h + s.charAt(i);
        }
        return h;
    }
    
    // 複合オブジェクト用のハッシュ
    static class Point {
        final int x, y;
        
        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public int hashCode() {
            // Objects.hashの実装
            int result = 1;
            result = 31 * result + x;
            result = 31 * result + y;
            return result;
        }
    }
}

// ハッシュ分布の測定
public class HashDistributionTest {
    public static void measureDistribution(int capacity) {
        int[] buckets = new int[capacity];
        Random random = new Random();
        
        // ランダムなキーでテスト
        for (int i = 0; i < capacity * 10; i++) {
            String key = "key" + random.nextInt(capacity * 5);
            int hash = key.hashCode();
            int index = hash & (capacity - 1);
            buckets[index]++;
        }
        
        // 分布の統計
        int min = Arrays.stream(buckets).min().orElse(0);
        int max = Arrays.stream(buckets).max().orElse(0);
        double avg = Arrays.stream(buckets).average().orElse(0);
        double stdDev = calculateStdDev(buckets, avg);
        
        System.out.printf("分布統計 - 最小: %d, 最大: %d, 平均: %.2f, 標準偏差: %.2f%n",
                         min, max, avg, stdDev);
    }
}
```

### メモリ効率の考慮

```java
// コンパクトなマップ実装
public class CompactHashMap<K, V> extends AbstractMap<K, V> {
    // 小さなマップ用の配列実装
    private static final int ARRAY_THRESHOLD = 8;
    
    private Object[] keys;
    private Object[] values;
    private int size;
    
    public CompactHashMap() {
        this.keys = new Object[ARRAY_THRESHOLD];
        this.values = new Object[ARRAY_THRESHOLD];
    }
    
    @Override
    public V put(K key, V value) {
        // 線形探索（小さなサイズでは効率的）
        for (int i = 0; i < size; i++) {
            if (key.equals(keys[i])) {
                @SuppressWarnings("unchecked")
                V oldValue = (V) values[i];
                values[i] = value;
                return oldValue;
            }
        }
        
        // 閾値を超えたらHashMapに変換
        if (size >= ARRAY_THRESHOLD) {
            convertToHashMap();
        }
        
        keys[size] = key;
        values[size] = value;
        size++;
        return null;
    }
}
```

---

## ベンチマークと選択指針

### パフォーマンス測定

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MapBenchmark {
    private static final int SIZE = 10000;
    
    private HashMap<Integer, String> hashMap;
    private TreeMap<Integer, String> treeMap;
    private ConcurrentHashMap<Integer, String> concurrentMap;
    
    @Setup
    public void setup() {
        hashMap = new HashMap<>();
        treeMap = new TreeMap<>();
        concurrentMap = new ConcurrentHashMap<>();
        
        for (int i = 0; i < SIZE; i++) {
            String value = "value" + i;
            hashMap.put(i, value);
            treeMap.put(i, value);
            concurrentMap.put(i, value);
        }
    }
    
    @Benchmark
    public String hashMapGet() {
        return hashMap.get(SIZE / 2);
    }
    
    @Benchmark
    public String treeMapGet() {
        return treeMap.get(SIZE / 2);
    }
    
    @Benchmark
    public String concurrentMapGet() {
        return concurrentMap.get(SIZE / 2);
    }
}
```

### 選択基準

1. **HashMap**: 
   - 順序不要で最速アクセスが必要
   - 単一スレッド環境
   - O(1)の平均性能

2. **TreeMap**: 
   - ソート順序が必要
   - 範囲検索が必要
   - O(log n)の保証された性能

3. **ConcurrentHashMap**: 
   - マルチスレッド環境
   - 高い並行性が必要
   - わずかなオーバーヘッドを許容

---

## まとめ

コレクションの内部実装を理解することで：

1. **適切な選択**: 用途に応じた最適なデータ構造の選択
2. **性能予測**: 操作の計算量と実際の性能の理解
3. **問題診断**: パフォーマンス問題の原因特定
4. **カスタム実装**: 特殊な要件に対応した独自実装

これらの知識は、大規模なアプリケーション開発において、性能問題を回避し、スケーラブルなシステムを構築するために不可欠です。