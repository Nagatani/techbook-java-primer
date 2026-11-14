package com.example.immutability;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 永続的データ構造とCopy-on-Writeパターンのデモンストレーション
 * 構造共有による効率的な不変データ構造の実装
 */
public class PersistentDataStructuresDemo {
    
    /**
     * Copy-on-Writeマップの実装
     */
    public static class CopyOnWriteMap<K, V> {
        private volatile Map<K, V> map = new HashMap<>();
        
        public V get(K key) {
            return map.get(key);  // 読み込みは高速
        }
        
        public boolean containsKey(K key) {
            return map.containsKey(key);
        }
        
        public Set<K> keySet() {
            return new HashSet<>(map.keySet()); // 防御的コピー
        }
        
        public Collection<V> values() {
            return new ArrayList<>(map.values()); // 防御的コピー
        }
        
        public synchronized V put(K key, V value) {
            Map<K, V> newMap = new HashMap<>(map);  // コピー作成
            V oldValue = newMap.put(key, value);
            map = newMap;  // 参照の原子的な置き換え
            return oldValue;
        }
        
        public synchronized V remove(K key) {
            Map<K, V> newMap = new HashMap<>(map);
            V oldValue = newMap.remove(key);
            map = newMap;
            return oldValue;
        }
        
        public synchronized void putAll(Map<? extends K, ? extends V> m) {
            Map<K, V> newMap = new HashMap<>(map);
            newMap.putAll(m);
            map = newMap;
        }
        
        public synchronized void clear() {
            map = new HashMap<>();
        }
        
        public Map<K, V> snapshot() {
            return new HashMap<>(map);  // 現在の状態のスナップショット
        }
        
        public int size() {
            return map.size();
        }
        
        public boolean isEmpty() {
            return map.isEmpty();
        }
        
        @Override
        public String toString() {
            return map.toString();
        }
    }
    
    /**
     * 永続的リスト（構造共有）の実装
     */
    public static class PersistentList<T> {
        private static class Node<T> {
            final T value;
            final Node<T> next;
            
            Node(T value, Node<T> next) {
                this.value = value;
                this.next = next;
            }
        }
        
        private final Node<T> head;
        private final int size;
        
        private PersistentList(Node<T> head, int size) {
            this.head = head;
            this.size = size;
        }
        
        public static <T> PersistentList<T> empty() {
            return new PersistentList<>(null, 0);
        }
        
        @SafeVarargs
        public static <T> PersistentList<T> of(T... elements) {
            PersistentList<T> list = empty();
            for (int i = elements.length - 1; i >= 0; i--) {
                list = list.prepend(elements[i]);
            }
            return list;
        }
        
        // O(1) で新しい要素を追加（構造を共有）
        public PersistentList<T> prepend(T value) {
            return new PersistentList<>(new Node<>(value, head), size + 1);
        }
        
        // O(1) で先頭要素を取得
        public Optional<T> head() {
            return head == null ? Optional.empty() : Optional.of(head.value);
        }
        
        // O(1) で残りのリストを取得（構造を共有）
        public PersistentList<T> tail() {
            if (head == null) {
                throw new IllegalStateException("Empty list has no tail");
            }
            return new PersistentList<>(head.next, size - 1);
        }
        
        // O(n) で要素を末尾に追加
        // 末尾に足すと、すべてのノードを作り直す必要がある（構造を共有できない）
        public PersistentList<T> append(T value) {
            List<T> elements = new ArrayList<>();
            Node<T> current = head;
            while (current != null) {
                elements.add(current.value);
                current = current.next;
            }
            elements.add(value);
            
            PersistentList<T> result = empty();
            for (int i = elements.size() - 1; i >= 0; i--) {
                result = result.prepend(elements.get(i));
            }
            return result;
        }
        
        // O(n) で指定位置の要素を取得
        public Optional<T> get(int index) {
            if (index < 0 || index >= size) {
                return Optional.empty();
            }
            
            Node<T> current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return Optional.of(current.value);
        }
        
        // リストの変換
        public <U> PersistentList<U> map(Function<T, U> mapper) {
            if (isEmpty()) {
                return empty();
            }
            return tail().map(mapper).prepend(mapper.apply(head.value));
        }
        
        // リストのフィルタリング
        public PersistentList<T> filter(Function<T, Boolean> predicate) {
            if (isEmpty()) {
                return this;
            }
            
            PersistentList<T> filteredTail = tail().filter(predicate);
            return predicate.apply(head.value) 
                ? filteredTail.prepend(head.value)
                : filteredTail;
        }
        
        public boolean isEmpty() {
            return head == null;
        }
        
        public int size() {
            return size;
        }
        
        public boolean contains(T value) {
            Node<T> current = head;
            while (current != null) {
                if (Objects.equals(current.value, value)) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }
        
        // イテレーター実装
        public Stream<T> stream() {
            return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(
                    new Iterator<T>() {
                        private Node<T> current = head;
                        
                        @Override
                        public boolean hasNext() {
                            return current != null;
                        }
                        
                        @Override
                        public T next() {
                            if (!hasNext()) {
                                throw new NoSuchElementException();
                            }
                            T value = current.value;
                            current = current.next;
                            return value;
                        }
                    },
                    Spliterator.ORDERED | Spliterator.IMMUTABLE
                ),
                false
            );
        }
        
        public List<T> toList() {
            return stream().collect(Collectors.toList());
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof PersistentList)) return false;
            
            PersistentList<?> other = (PersistentList<?>) obj;
            if (size != other.size) return false;
            
            Node<T> thisCurrent = head;
            Node<?> otherCurrent = other.head;
            
            while (thisCurrent != null && otherCurrent != null) {
                if (!Objects.equals(thisCurrent.value, otherCurrent.value)) {
                    return false;
                }
                thisCurrent = thisCurrent.next;
                otherCurrent = otherCurrent.next;
            }
            
            return thisCurrent == null && otherCurrent == null;
        }
        
        @Override
        public int hashCode() {
            int hash = 1;
            Node<T> current = head;
            while (current != null) {
                hash = 31 * hash + Objects.hashCode(current.value);
                current = current.next;
            }
            return hash;
        }
        
        @Override
        public String toString() {
            return stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ", "[", "]"));
        }
    }
    
    /**
     * 簡易的な永続的ハッシュマップ（Trie構造）
     */
    public static class PersistentHashMap<K, V> {
        private static final int SHIFT = 5;
        private static final int BUCKET_SIZE = 1 << SHIFT; // 32
        private static final int MASK = BUCKET_SIZE - 1;
        
        private interface Node<K, V> {
            V find(int shift, int hash, K key);
            Node<K, V> insert(int shift, int hash, K key, V value);
            Node<K, V> remove(int shift, int hash, K key);
            boolean isEmpty();
            int size();
        }
        
        private static class LeafNode<K, V> implements Node<K, V> {
            final int hash;
            final K key;
            final V value;
            
            LeafNode(int hash, K key, V value) {
                this.hash = hash;
                this.key = key;
                this.value = value;
            }
            
            @Override
            public V find(int shift, int hash, K key) {
                return this.hash == hash && Objects.equals(this.key, key) ? value : null;
            }
            
            @Override
            public Node<K, V> insert(int shift, int hash, K key, V value) {
                if (this.hash == hash && Objects.equals(this.key, key)) {
                    return new LeafNode<>(hash, key, value);
                }
                
                // ハッシュ値が完全に同じ別のキーは、ビットで振り分けられないので衝突ノードにまとめる
                if (this.hash == hash) {
                    return new CollisionNode<K, V>(hash, List.of(this))
                        .insert(shift, hash, key, value);
                }
                
                // ハッシュ値の一部が同じときは分岐ノードを作成
                return BranchNode.<K, V>empty()
                    .insert(shift, this.hash, this.key, this.value)
                    .insert(shift, hash, key, value);
            }
            
            @Override
            public Node<K, V> remove(int shift, int hash, K key) {
                return this.hash == hash && Objects.equals(this.key, key) ? null : this;
            }
            
            @Override
            public boolean isEmpty() {
                return false;
            }
            
            @Override
            public int size() {
                return 1;
            }
        }
        
        /**
         * ハッシュ値が完全に同じキーをまとめて持つノード
         */
        private static class CollisionNode<K, V> implements Node<K, V> {
            final int hash;
            final List<LeafNode<K, V>> leaves;
            
            CollisionNode(int hash, List<LeafNode<K, V>> leaves) {
                this.hash = hash;
                this.leaves = List.copyOf(leaves);
            }
            
            @Override
            public V find(int shift, int hash, K key) {
                for (LeafNode<K, V> leaf : leaves) {
                    if (Objects.equals(leaf.key, key)) {
                        return leaf.value;
                    }
                }
                return null;
            }
            
            @Override
            public Node<K, V> insert(int shift, int hash, K key, V value) {
                if (hash != this.hash) {
                    // 別のハッシュ値のキーとは、分岐ノードで振り分ける
                    BranchNode<K, V> branch = BranchNode.empty();
                    Node<K, V>[] children = branch.children.clone();
                    children[(this.hash >>> shift) & MASK] = this;
                    return new BranchNode<>(children).insert(shift, hash, key, value);
                }
                List<LeafNode<K, V>> newLeaves = new ArrayList<>();
                for (LeafNode<K, V> leaf : leaves) {
                    if (!Objects.equals(leaf.key, key)) {
                        newLeaves.add(leaf);
                    }
                }
                newLeaves.add(new LeafNode<>(hash, key, value));
                return new CollisionNode<>(hash, newLeaves);
            }
            
            @Override
            public Node<K, V> remove(int shift, int hash, K key) {
                List<LeafNode<K, V>> newLeaves = new ArrayList<>();
                for (LeafNode<K, V> leaf : leaves) {
                    if (!Objects.equals(leaf.key, key)) {
                        newLeaves.add(leaf);
                    }
                }
                if (newLeaves.size() == leaves.size()) return this;
                if (newLeaves.size() == 1) return newLeaves.get(0);
                return new CollisionNode<>(hash, newLeaves);
            }
            
            @Override
            public boolean isEmpty() {
                return leaves.isEmpty();
            }
            
            @Override
            public int size() {
                return leaves.size();
            }
        }
        
        private static class BranchNode<K, V> implements Node<K, V> {
            final Node<K, V>[] children;
            final int population;
            
            @SuppressWarnings("unchecked")
            BranchNode(Node<K, V>[] children) {
                this.children = children;
                this.population = (int) Arrays.stream(children)
                    .filter(Objects::nonNull)
                    .count();
            }
            
            @SuppressWarnings("unchecked")
            static <K, V> BranchNode<K, V> empty() {
                return new BranchNode<>(new Node[BUCKET_SIZE]);
            }
            
            @Override
            public V find(int shift, int hash, K key) {
                // 深さごとにハッシュ値の別の5ビットを使って子を選ぶ
                int index = (hash >>> shift) & MASK;
                Node<K, V> child = children[index];
                return child == null ? null : child.find(shift + SHIFT, hash, key);
            }
            
            @Override
            public Node<K, V> insert(int shift, int hash, K key, V value) {
                int index = (hash >>> shift) & MASK;
                Node<K, V> child = children[index];
                Node<K, V> newChild = child == null 
                    ? new LeafNode<>(hash, key, value)
                    : child.insert(shift + SHIFT, hash, key, value);
                
                if (newChild == child) {
                    return this;
                }
                
                Node<K, V>[] newChildren = Arrays.copyOf(children, children.length);
                newChildren[index] = newChild;
                return new BranchNode<>(newChildren);
            }
            
            @Override
            public Node<K, V> remove(int shift, int hash, K key) {
                int index = (hash >>> shift) & MASK;
                Node<K, V> child = children[index];
                if (child == null) return this;
                
                Node<K, V> newChild = child.remove(shift + SHIFT, hash, key);
                if (newChild == child) return this;
                
                Node<K, V>[] newChildren = Arrays.copyOf(children, children.length);
                newChildren[index] = newChild;
                
                BranchNode<K, V> newBranch = new BranchNode<>(newChildren);
                
                // 子ノードが1つだけの場合は最適化を検討
                if (newBranch.population == 1) {
                    for (Node<K, V> remainingChild : newChildren) {
                        if (remainingChild != null) {
                            return remainingChild instanceof LeafNode ? remainingChild : newBranch;
                        }
                    }
                }
                
                return newBranch;
            }
            
            @Override
            public boolean isEmpty() {
                return population == 0;
            }
            
            @Override
            public int size() {
                return Arrays.stream(children)
                    .filter(Objects::nonNull)
                    .mapToInt(Node::size)
                    .sum();
            }
        }
        
        private final Node<K, V> root;
        private final int size;
        
        private PersistentHashMap(Node<K, V> root, int size) {
            this.root = root;
            this.size = size;
        }
        
        public static <K, V> PersistentHashMap<K, V> empty() {
            return new PersistentHashMap<>(null, 0);
        }
        
        public V get(K key) {
            if (root == null) return null;
            return root.find(0, key.hashCode(), key);
        }
        
        // 値にnullを入れない前提（nullの値は「キーなし」と区別できない）
        public boolean containsKey(K key) {
            return get(key) != null;
        }
        
        public PersistentHashMap<K, V> put(K key, V value) {
            int hash = key.hashCode();
            boolean existed = containsKey(key);
            
            Node<K, V> newRoot = root == null 
                ? new LeafNode<>(hash, key, value)
                : root.insert(0, hash, key, value);
            
            return new PersistentHashMap<>(newRoot, existed ? size : size + 1);
        }
        
        public PersistentHashMap<K, V> remove(K key) {
            if (root == null) return this;
            
            boolean existed = containsKey(key);
            if (!existed) return this;
            
            Node<K, V> newRoot = root.remove(0, key.hashCode(), key);
            return new PersistentHashMap<>(newRoot, size - 1);
        }
        
        public int size() {
            return size;
        }
        
        public boolean isEmpty() {
            return size == 0;
        }
        
        @Override
        public String toString() {
            return "{size=" + size + "}";
        }
    }
    
    /**
     * カスタム不変ArrayList
     */
    public static final class ImmutableArrayList<E> extends AbstractList<E> 
                                            implements RandomAccess {
        private final Object[] elements;
        
        @SafeVarargs
        public ImmutableArrayList(E... elements) {
            this.elements = Arrays.copyOf(elements, elements.length);
        }
        
        public ImmutableArrayList(Collection<? extends E> c) {
            this.elements = c.toArray();
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public E get(int index) {
            Objects.checkIndex(index, elements.length);
            return (E) elements[index];
        }
        
        @Override
        public int size() {
            return elements.length;
        }
        
        // 変更操作は例外をスロー
        @Override
        public E set(int index, E element) {
            throw new UnsupportedOperationException("ImmutableArrayList cannot be modified");
        }
        
        // withメソッドで新しいインスタンスを返す
        public ImmutableArrayList<E> with(int index, E element) {
            Objects.checkIndex(index, elements.length);
            Object[] newElements = Arrays.copyOf(elements, elements.length);
            newElements[index] = element;
            @SuppressWarnings("unchecked")
            E[] typedArray = (E[]) newElements;
            return new ImmutableArrayList<>(typedArray);
        }
        
        public ImmutableArrayList<E> append(E element) {
            Object[] newElements = Arrays.copyOf(elements, elements.length + 1);
            newElements[elements.length] = element;
            @SuppressWarnings("unchecked")
            E[] typedArray = (E[]) newElements;
            return new ImmutableArrayList<>(typedArray);
        }
        
        @Override
        public E remove(int index) {
            throw new UnsupportedOperationException("ImmutableArrayList cannot be modified");
        }
        
        public ImmutableArrayList<E> removeAt(int index) {
            Objects.checkIndex(index, elements.length);
            
            Object[] newElements = new Object[elements.length - 1];
            System.arraycopy(elements, 0, newElements, 0, index);
            System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);
            @SuppressWarnings("unchecked")
            E[] typedArray = (E[]) newElements;
            return new ImmutableArrayList<>(typedArray);
        }
        
        public ImmutableArrayList<E> subList(int fromIndex, int toIndex) {
            Objects.checkFromToIndex(fromIndex, toIndex, elements.length);
            
            Object[] newElements = Arrays.copyOfRange(elements, fromIndex, toIndex);
            @SuppressWarnings("unchecked")
            E[] typedArray = (E[]) newElements;
            return new ImmutableArrayList<>(typedArray);
        }
    }
    
    /**
     * パフォーマンステスト
     */
    public static class PerformanceComparison {
        
        public static void compareDataStructures() {
            IO.println("=== Data Structure Performance Comparison ===");
            IO.println("(Timings vary greatly by environment. Use JMH for accurate measurements.)");
            
            int iterations = 10_000;
            
            // ArrayList vs ImmutableArrayList
            compareArrayLists(iterations);
            
            // HashMap vs CopyOnWriteMap
            compareMaps(iterations);
            
            // LinkedList vs PersistentList
            compareLists(iterations);
        }
        
        private static void compareArrayLists(int iterations) {
            IO.println("\n--- ArrayList vs ImmutableArrayList ---");
            
            long mutableTime = measureTime(() -> {
                List<Integer> list = new ArrayList<>();
                for (int i = 0; i < iterations; i++) {
                    list.add(i);
                }
                return list;
            });
            
            // appendのたびに配列全体をコピーする
            long immutableTime = measureTime(() -> {
                ImmutableArrayList<Integer> list = new ImmutableArrayList<>();
                for (int i = 0; i < iterations; i++) {
                    list = list.append(i);
                }
                return list;
            });
            
            printResult("ArrayList:         ", mutableTime, "ImmutableArrayList:", immutableTime);
        }
        
        private static void compareMaps(int iterations) {
            IO.println("\n--- HashMap vs CopyOnWriteMap ---");
            
            long hashMapTime = measureTime(() -> {
                Map<Integer, String> map = new HashMap<>();
                for (int i = 0; i < iterations; i++) {
                    map.put(i, "value" + i);
                }
                return map;
            });
            
            // putのたびにマップ全体をコピーする
            long cowMapTime = measureTime(() -> {
                CopyOnWriteMap<Integer, String> map = new CopyOnWriteMap<>();
                for (int i = 0; i < iterations; i++) {
                    map.put(i, "value" + i);
                }
                return map;
            });
            
            printResult("HashMap:       ", hashMapTime, "CopyOnWriteMap:", cowMapTime);
        }
        
        private static void compareLists(int iterations) {
            IO.println("\n--- LinkedList vs PersistentList ---");
            
            long linkedListTime = measureTime(() -> {
                LinkedList<Integer> list = new LinkedList<>();
                for (int i = 0; i < iterations; i++) {
                    list.addFirst(i);
                }
                return list;
            });
            
            // 先頭への追加は既存のノードを共有するのでO(1)
            long persistentListTime = measureTime(() -> {
                PersistentList<Integer> list = PersistentList.empty();
                for (int i = 0; i < iterations; i++) {
                    list = list.prepend(i);
                }
                return list;
            });
            
            printResult("LinkedList:    ", linkedListTime, "PersistentList:", persistentListTime);
        }
        
        private static void printResult(String label1, long time1, String label2, long time2) {
            IO.println("%s %d μs".formatted(label1, time1 / 1000));
            IO.println("%s %d μs".formatted(label2, time2 / 1000));
            if (time1 <= 0 || time2 <= 0) {
                IO.println("Ratio: n/a (too fast to measure)");
            } else {
                IO.println("Ratio: %.2fx".formatted((double) time2 / time1));
            }
        }
        
        // 暖機のために数回実行してから、1回分の時間を測る
        private static long measureTime(Supplier<Object> task) {
            for (int i = 0; i < 3; i++) {
                task.get();
            }
            long start = System.nanoTime();
            task.get();
            return System.nanoTime() - start;
        }
        
        @FunctionalInterface
        interface Supplier<T> {
            T get();
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        IO.println("Persistent Data Structures and Copy-on-Write Demo");
        IO.println("=================================================");
        
        // Copy-on-Writeマップのデモ
        demonstrateCopyOnWriteMap();
        
        // 永続的リストのデモ
        demonstratePersistentList();
        
        // 永続的ハッシュマップのデモ
        demonstratePersistentHashMap();
        
        // 不変ArrayListのデモ
        demonstrateImmutableArrayList();
        
        // パフォーマンス比較
        PerformanceComparison.compareDataStructures();
        
        IO.println("\nKey Insights:");
        IO.println("✓ Copy-on-Write provides thread-safe updates with fast reads");
        IO.println("✓ Persistent data structures enable efficient immutable operations");
        IO.println("✓ Structural sharing minimizes memory overhead");
        IO.println("✓ Trade-off between update performance and immutability benefits");
        IO.println("✓ Choose data structure based on usage patterns");
        
        IO.println("\nUse Cases:");
        IO.println("• Copy-on-Write: Read-heavy concurrent scenarios");
        IO.println("• Persistent Lists: Functional programming, undo operations");
        IO.println("• Persistent Maps: Configuration management, versioning");
        IO.println("• Immutable Arrays: Mathematical computations, caching");
        IO.println("• All: When thread safety and immutability are priorities");
    }
    
    private static void demonstrateCopyOnWriteMap() {
        IO.println("\n=== Copy-on-Write Map Demo ===");
        
        CopyOnWriteMap<String, Integer> cowMap = new CopyOnWriteMap<>();
        
        // 書き込み操作
        cowMap.put("apple", 5);
        cowMap.put("banana", 3);
        cowMap.put("orange", 8);
        
        IO.println("Initial map: " + cowMap);
        
        // スナップショットの取得
        Map<String, Integer> snapshot1 = cowMap.snapshot();
        
        // 追加の更新
        cowMap.put("apple", 10); // 既存キーの更新
        cowMap.put("grape", 12); // 新しいキー
        
        IO.println("Updated map: " + cowMap);
        IO.println("Snapshot remains unchanged: " + snapshot1);
        
        // 読み込みはロックなしで、ほかのスレッドの書き込みと同時に行ってよい
        // （ここでは表示の順序をそろえるため、キーを並べ替えて1つのスレッドで読む）
        IO.println("Read access:");
        cowMap.keySet().stream()
            .sorted()
            .forEach(key -> IO.println("  " + key + " = " + cowMap.get(key)));
    }
    
    private static void demonstratePersistentList() {
        IO.println("\n=== Persistent List Demo ===");
        
        // 空のリストから開始
        PersistentList<String> list1 = PersistentList.empty();
        IO.println("Empty list: " + list1);
        
        // 要素の追加（新しいリストが返される）
        PersistentList<String> list2 = list1.prepend("C").prepend("B").prepend("A");
        IO.println("List2 (A,B,C): " + list2);
        IO.println("List1 still empty: " + list1);
        
        // 構造共有のデモ
        PersistentList<String> list3 = list2.prepend("X");
        PersistentList<String> list4 = list2.prepend("Y");
        
        IO.println("List3 (X,A,B,C): " + list3);
        IO.println("List4 (Y,A,B,C): " + list4);
        IO.println("List2 unchanged: " + list2);
        
        // 関数型操作
        PersistentList<String> uppercased = list2.map(String::toUpperCase);
        IO.println("Uppercased: " + uppercased);
        
        PersistentList<String> filtered = list2.filter(s -> s.compareTo("B") >= 0);
        IO.println("Filtered (>= B): " + filtered);
        
        // ファクトリメソッド
        PersistentList<Integer> numbers = PersistentList.of(1, 2, 3, 4, 5);
        IO.println("Numbers: " + numbers);
        
        // Stream API との統合
        List<Integer> doubled = numbers.stream()
            .map(n -> n * 2)
            .collect(Collectors.toList());
        IO.println("Doubled (as ArrayList): " + doubled);
    }
    
    private static void demonstratePersistentHashMap() {
        IO.println("\n=== Persistent Hash Map Demo ===");
        
        PersistentHashMap<String, Integer> map1 = PersistentHashMap.empty();
        IO.println("Empty map: " + map1);
        
        // 要素の追加
        PersistentHashMap<String, Integer> map2 = map1
            .put("first", 1)
            .put("second", 2)
            .put("third", 3);
        
        IO.println("Map2 size: " + map2.size());
        IO.println("Get 'second': " + map2.get("second"));
        IO.println("Contains 'first': " + map2.containsKey("first"));
        
        // 更新（新しいマップが返される）
        PersistentHashMap<String, Integer> map3 = map2.put("second", 20);
        
        IO.println("Map2 'second': " + map2.get("second")); // 元の値
        IO.println("Map3 'second': " + map3.get("second")); // 更新された値
        
        // 削除
        PersistentHashMap<String, Integer> map4 = map3.remove("first");
        IO.println("Map3 size: " + map3.size()); // 削除前
        IO.println("Map4 size: " + map4.size()); // 削除後
        
        // 大量データでのテスト
        PersistentHashMap<Integer, String> largeMap = PersistentHashMap.empty();
        for (int i = 0; i < 1000; i++) {
            largeMap = largeMap.put(i, "value" + i);
        }
        IO.println("Large map size: " + largeMap.size());
        IO.println("Random access test: " + largeMap.get(500));
        
        int found = 0;
        for (int i = 0; i < 1000; i++) {
            if (("value" + i).equals(largeMap.get(i))) {
                found++;
            }
        }
        IO.println("Keys found: " + found + " / 1000");
        
        // ハッシュ値が同じ別のキー（"Aa"と"BB"はhashCodeが同じ）
        PersistentHashMap<String, Integer> collision = PersistentHashMap.<String, Integer>empty()
            .put("Aa", 1)
            .put("BB", 2);
        IO.println("Same hash keys: Aa=" + collision.get("Aa") + ", BB=" + collision.get("BB")
                   + ", size=" + collision.size());
    }
    
    private static void demonstrateImmutableArrayList() {
        IO.println("\n=== Immutable ArrayList Demo ===");
        
        // 初期化
        ImmutableArrayList<String> list1 = new ImmutableArrayList<>("A", "B", "C");
        IO.println("Initial list: " + list1);
        
        // 要素の更新
        ImmutableArrayList<String> list2 = list1.with(1, "X");
        IO.println("Updated list: " + list2);
        IO.println("Original unchanged: " + list1);
        
        // 要素の追加
        ImmutableArrayList<String> list3 = list2.append("D");
        IO.println("Appended list: " + list3);
        
        // 要素の削除
        ImmutableArrayList<String> list4 = list3.removeAt(0);
        IO.println("After removing index 0: " + list4);
        
        // 部分リスト
        ImmutableArrayList<String> subList = list3.subList(1, 3);
        IO.println("Sublist [1,3): " + subList);
        
        // 通常のList操作
        IO.println("Size: " + list3.size());
        IO.println("Get index 2: " + list3.get(2));
        IO.println("Contains 'X': " + list3.contains("X"));
        
        // Stream API
        String joined = list3.stream()
            .map(s -> s.toLowerCase())
            .collect(Collectors.joining("-"));
        IO.println("Joined lowercase: " + joined);
        
        // 変更操作は例外
        try {
            list3.set(0, "Y");
        } catch (UnsupportedOperationException e) {
            IO.println("Expected exception: " + e.getMessage());
        }
    }
}