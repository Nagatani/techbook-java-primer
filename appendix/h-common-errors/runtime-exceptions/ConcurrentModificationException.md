# ConcurrentModificationException

## 概要

`ConcurrentModificationException`は、コレクションの反復処理中に、そのコレクションの構造を変更（要素の追加・削除）した際に発生する例外です。これは必ずしもマルチスレッド環境でのみ発生するわけではなく、シングルスレッドでも発生します。

## 発生原因

1. **拡張for文中の構造変更**: 反復処理中の要素削除・追加
2. **複数イテレータの同時使用**: 同じコレクションに対する複数のイテレータ
3. **マルチスレッド環境**: 同期化されていないコレクションへの並行アクセス
4. **ネストした反復処理**: 内側のループで外側のコレクションを変更

## 典型的な発生パターン

### パターン1：拡張for文での削除

```java
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

// 間違い：拡張for文中での削除
for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);  // ConcurrentModificationException
    }
}
```

### パターン2：条件付き削除

```java
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 85);
scores.put("Bob", 70);
scores.put("Charlie", 60);

// 間違い：反復処理中の削除
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
    if (entry.getValue() < 75) {
        scores.remove(entry.getKey());  // ConcurrentModificationException
    }
}
```

### パターン3：ネストしたループ

```java
List<List<String>> groups = new ArrayList<>();
// groupsにデータを追加...

for (List<String> group : groups) {
    for (String member : group) {
        if (member.startsWith("remove")) {
            groups.remove(group);  // 外側のコレクションを変更
        }
    }
}
```

### パターン4：複数イテレータ

```java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
Iterator<Integer> iter1 = numbers.iterator();
Iterator<Integer> iter2 = numbers.iterator();

iter1.next();
iter1.remove();  // iter1で削除
iter2.next();    // ConcurrentModificationException（iter2は変更を検知）
```

## 解決策

### 1. イテレータのremove()メソッド

```java
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

Iterator<String> iter = list.iterator();
while (iter.hasNext()) {
    String item = iter.next();
    if (item.equals("B")) {
        iter.remove();  // イテレータ経由で安全に削除
    }
}
```

### 2. removeIf()メソッド（Java 8以降）

```java
// Listの場合
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));
list.removeIf(item -> item.equals("B"));

// Mapの場合
Map<String, Integer> scores = new HashMap<>();
scores.put("Alice", 85);
scores.put("Bob", 70);
scores.put("Charlie", 60);
scores.entrySet().removeIf(entry -> entry.getValue() < 75);
```

### 3. 新しいコレクションの作成

```java
// フィルタリングして新しいリストを作成
List<String> original = Arrays.asList("A", "B", "C", "D");
List<String> filtered = original.stream()
    .filter(item -> !item.equals("B"))
    .collect(Collectors.toList());

// または従来の方法
List<String> newList = new ArrayList<>();
for (String item : original) {
    if (!item.equals("B")) {
        newList.add(item);
    }
}
```

### 4. インデックスを使った削除（逆順）

```java
List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

// 後ろから削除することでインデックスのずれを防ぐ
for (int i = list.size() - 1; i >= 0; i--) {
    if (list.get(i).equals("B")) {
        list.remove(i);
    }
}
```

### 5. ConcurrentCollectionの使用

```java
// マルチスレッド環境での解決策
ConcurrentHashMap<String, Integer> concurrentMap = new ConcurrentHashMap<>();
concurrentMap.put("Alice", 85);
concurrentMap.put("Bob", 70);

// 並行アクセスに対して安全
for (Map.Entry<String, Integer> entry : concurrentMap.entrySet()) {
    if (entry.getValue() < 75) {
        concurrentMap.remove(entry.getKey());  // 安全に削除可能
    }
}
```

## 高度な解決策

### 1. CopyOnWriteコレクション

```java
// 読み取りが多く、書き込みが少ない場合に適している
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
list.addAll(Arrays.asList("A", "B", "C", "D"));

// 反復処理中の変更が可能（ただしパフォーマンスに注意）
for (String item : list) {
    if (item.equals("B")) {
        list.remove(item);  // 例外は発生しない
    }
}
```

### 2. 遅延削除パターン

```java
public class LazyRemovalList<E> {
    private final List<E> list;
    private final Set<E> toRemove = new HashSet<>();
    
    public LazyRemovalList(List<E> list) {
        this.list = new ArrayList<>(list);
    }
    
    public void markForRemoval(E element) {
        toRemove.add(element);
    }
    
    public void applyRemovals() {
        list.removeAll(toRemove);
        toRemove.clear();
    }
    
    public void process() {
        for (E element : list) {
            if (shouldRemove(element)) {
                markForRemoval(element);  // 即座に削除せず、マークのみ
            }
        }
        applyRemovals();  // 反復処理後に一括削除
    }
}
```

### 3. イミュータブルコレクション

```java
// Guavaライブラリの例
ImmutableList<String> immutableList = ImmutableList.of("A", "B", "C", "D");

// 変更操作は新しいコレクションを返す
ImmutableList<String> filtered = immutableList.stream()
    .filter(item -> !item.equals("B"))
    .collect(ImmutableList.toImmutableList());
```

## デバッグのコツ

### 1. fail-fastイテレータの理解

```java
public void demonstrateFailFast() {
    List<String> list = new ArrayList<>(Arrays.asList("A", "B", "C"));
    
    Iterator<String> iter = list.iterator();
    
    // modCountの変更を監視
    System.out.println("要素を追加前");
    list.add("D");  // modCountが増加
    
    try {
        iter.next();  // modCountの不一致を検出
    } catch (ConcurrentModificationException e) {
        System.out.println("期待通りの例外: " + e);
    }
}
```

### 2. デバッグログの追加

```java
public void processWithDebug(List<String> list) {
    logger.debug("処理開始 - リストサイズ: {}", list.size());
    
    List<String> toRemove = new ArrayList<>();
    
    for (String item : list) {
        logger.debug("処理中: {}", item);
        if (shouldRemove(item)) {
            toRemove.add(item);
            logger.debug("削除予定に追加: {}", item);
        }
    }
    
    logger.debug("削除実行 - 削除数: {}", toRemove.size());
    list.removeAll(toRemove);
    logger.debug("処理完了 - リストサイズ: {}", list.size());
}
```

### 3. スレッドセーフティの確認

```java
public class ThreadSafetyChecker {
    private final List<String> list = Collections.synchronizedList(new ArrayList<>());
    
    public void checkThreadSafety() {
        // スレッド1: 要素追加
        Thread adder = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                list.add("Item" + i);
                Thread.yield();
            }
        });
        
        // スレッド2: 反復処理
        Thread iterator = new Thread(() -> {
            try {
                for (String item : list) {
                    System.out.println(item);
                    Thread.sleep(1);
                }
            } catch (Exception e) {
                logger.error("反復処理中にエラー", e);
            }
        });
        
        adder.start();
        iterator.start();
    }
}
```

## ベストプラクティス

1. **適切なメソッドの選択**: removeIf()やイテレータのremove()を優先
2. **イミュータブルの活用**: 可能な限り不変コレクションを使用
3. **並行コレクションの使用**: マルチスレッド環境では専用クラスを使用
4. **防御的コピー**: 反復処理前にコレクションのコピーを作成
5. **ドキュメント化**: 並行アクセスの可能性がある場合は明記

## パフォーマンスの考慮

```java
// パフォーマンス比較
public void performanceComparison() {
    List<Integer> list = new ArrayList<>();
    for (int i = 0; i < 100000; i++) {
        list.add(i);
    }
    
    // 方法1: removeIf（最速）
    long start1 = System.nanoTime();
    list.removeIf(n -> n % 2 == 0);
    long time1 = System.nanoTime() - start1;
    
    // 方法2: Iterator（中速）
    Iterator<Integer> iter = list.iterator();
    long start2 = System.nanoTime();
    while (iter.hasNext()) {
        if (iter.next() % 2 == 0) {
            iter.remove();
        }
    }
    long time2 = System.nanoTime() - start2;
    
    // 方法3: 新しいリスト作成（メモリ使用量大）
    long start3 = System.nanoTime();
    List<Integer> newList = list.stream()
        .filter(n -> n % 2 != 0)
        .collect(Collectors.toList());
    long time3 = System.nanoTime() - start3;
}
```

## 関連項目

- [IllegalStateException](IllegalStateException.md)
- [UnsupportedOperationException](UnsupportedOperationException.md)
- コレクションフレームワークガイド
- 並行プログラミングガイド