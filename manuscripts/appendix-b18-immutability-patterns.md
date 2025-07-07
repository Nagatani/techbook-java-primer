# 付録B.18: 不変性の設計パターンと実装テクニック

## 概要

本付録では、不変オブジェクトの設計パターンと高度な実装テクニックについて詳細に解説します。不変性は並行プログラミングやファンクショナルプログラミングにおいて重要な概念であり、バグの少ない堅牢なシステム設計の基礎となります。

**対象読者**: 不変性の基本を理解し、高度な設計パターンに興味がある開発者  
**前提知識**: 第6章「不変性とfinal」の内容、基本的なデザインパターン  
**関連章**: 第6章、第16章（マルチスレッド）、第11章（関数型プログラミング）

---

## 不変オブジェクトの設計原則

### 完全な不変性の実現

```java
// 不変クラスの完全な実装
public final class ImmutablePerson {
    private final String name;
    private final LocalDate birthDate;
    private final List<String> nicknames;
    private final Map<String, String> attributes;
    
    // コンストラクタでのディープコピー
    public ImmutablePerson(String name, LocalDate birthDate, 
                          List<String> nicknames, Map<String, String> attributes) {
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.birthDate = Objects.requireNonNull(birthDate, "birthDate cannot be null");
        
        // 防御的コピー
        this.nicknames = nicknames == null 
            ? List.of() 
            : List.copyOf(nicknames);
        
        this.attributes = attributes == null 
            ? Map.of() 
            : Map.copyOf(attributes);
    }
    
    // ゲッターでも防御的コピー（必要な場合）
    public List<String> getNicknames() {
        return nicknames; // Java 9+ のList.copyOf()は既に不変
    }
    
    public Map<String, String> getAttributes() {
        return attributes; // Java 9+ のMap.copyOf()は既に不変
    }
    
    // withメソッドパターン
    public ImmutablePerson withName(String newName) {
        return new ImmutablePerson(newName, birthDate, nicknames, attributes);
    }
    
    public ImmutablePerson withNickname(String nickname) {
        List<String> newNicknames = new ArrayList<>(nicknames);
        newNicknames.add(nickname);
        return new ImmutablePerson(name, birthDate, newNicknames, attributes);
    }
    
    public ImmutablePerson withAttribute(String key, String value) {
        Map<String, String> newAttributes = new HashMap<>(attributes);
        newAttributes.put(key, value);
        return new ImmutablePerson(name, birthDate, nicknames, newAttributes);
    }
    
    // equals, hashCode, toString の実装
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ImmutablePerson)) return false;
        ImmutablePerson other = (ImmutablePerson) obj;
        return Objects.equals(name, other.name) &&
               Objects.equals(birthDate, other.birthDate) &&
               Objects.equals(nicknames, other.nicknames) &&
               Objects.equals(attributes, other.attributes);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, birthDate, nicknames, attributes);
    }
}
```

### ビルダーパターンとの組み合わせ

```java
public final class ImmutableOrder {
    private final String orderId;
    private final LocalDateTime orderDate;
    private final List<OrderItem> items;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    
    private ImmutableOrder(Builder builder) {
        this.orderId = builder.orderId;
        this.orderDate = builder.orderDate;
        this.items = List.copyOf(builder.items);
        this.totalAmount = builder.totalAmount;
        this.status = builder.status;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    // 既存のオブジェクトからビルダーを作成
    public Builder toBuilder() {
        return new Builder()
            .orderId(orderId)
            .orderDate(orderDate)
            .items(new ArrayList<>(items))
            .totalAmount(totalAmount)
            .status(status);
    }
    
    public static class Builder {
        private String orderId;
        private LocalDateTime orderDate;
        private List<OrderItem> items = new ArrayList<>();
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private OrderStatus status = OrderStatus.PENDING;
        
        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }
        
        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }
        
        public Builder items(List<OrderItem> items) {
            this.items = new ArrayList<>(items);
            return this;
        }
        
        public Builder addItem(OrderItem item) {
            this.items.add(item);
            this.totalAmount = totalAmount.add(item.getPrice());
            return this;
        }
        
        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }
        
        public ImmutableOrder build() {
            // バリデーション
            Objects.requireNonNull(orderId, "orderId is required");
            Objects.requireNonNull(orderDate, "orderDate is required");
            if (items.isEmpty()) {
                throw new IllegalStateException("Order must have at least one item");
            }
            
            return new ImmutableOrder(this);
        }
    }
}
```

---

## Copy-on-Writeパターン

### 基本的なCopy-on-Write実装

```java
public class CopyOnWriteMap<K, V> {
    private volatile Map<K, V> map = new HashMap<>();
    
    public V get(K key) {
        return map.get(key);  // 読み込みは高速
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
    
    public Map<K, V> snapshot() {
        return new HashMap<>(map);  // 現在の状態のスナップショット
    }
}
```

### 効率的なCopy-on-Write with 構造共有

```java
// Persistent Data Structure（構造共有）の実装例
public class PersistentList<T> {
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
    
    public boolean isEmpty() {
        return head == null;
    }
    
    public int size() {
        return size;
    }
    
    // イテレータ実装
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
}
```

---

## イミュータブルコレクション

### カスタムイミュータブルコレクション

```java
public final class ImmutableArrayList<E> extends AbstractList<E> 
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
    public E get(int index) {
        @SuppressWarnings("unchecked")
        E element = (E) elements[index];
        return element;
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
        Object[] newElements = Arrays.copyOf(elements, elements.length);
        newElements[index] = element;
        return new ImmutableArrayList<>(newElements);
    }
    
    public ImmutableArrayList<E> append(E element) {
        Object[] newElements = Arrays.copyOf(elements, elements.length + 1);
        newElements[elements.length] = element;
        return new ImmutableArrayList<>(newElements);
    }
    
    public ImmutableArrayList<E> remove(int index) {
        if (index < 0 || index >= elements.length) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }
        
        Object[] newElements = new Object[elements.length - 1];
        System.arraycopy(elements, 0, newElements, 0, index);
        System.arraycopy(elements, index + 1, newElements, index, elements.length - index - 1);
        return new ImmutableArrayList<>(newElements);
    }
}
```

### Trie（トライ木）ベースの永続的マップ

```java
// 簡略化されたPersistent Hash Map（概念的実装）
public class PersistentHashMap<K, V> {
    private static final int SHIFT = 5;
    private static final int BUCKET_SIZE = 1 << SHIFT; // 32
    private static final int MASK = BUCKET_SIZE - 1;
    
    private interface Node<K, V> {
        V find(int hash, K key);
        Node<K, V> insert(int shift, int hash, K key, V value);
        Node<K, V> remove(int shift, int hash, K key);
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
        public V find(int hash, K key) {
            return this.hash == hash && Objects.equals(this.key, key) ? value : null;
        }
        
        @Override
        public Node<K, V> insert(int shift, int hash, K key, V value) {
            if (this.hash == hash && Objects.equals(this.key, key)) {
                return new LeafNode<>(hash, key, value);
            }
            
            // 衝突時は分岐ノードを作成
            return BranchNode.<K, V>empty()
                .insert(shift, this.hash, this.key, this.value)
                .insert(shift, hash, key, value);
        }
        
        @Override
        public Node<K, V> remove(int shift, int hash, K key) {
            return this.hash == hash && Objects.equals(this.key, key) ? null : this;
        }
    }
    
    private static class BranchNode<K, V> implements Node<K, V> {
        final Node<K, V>[] children;
        
        @SuppressWarnings("unchecked")
        BranchNode(Node<K, V>[] children) {
            this.children = children;
        }
        
        static <K, V> BranchNode<K, V> empty() {
            return new BranchNode<>(new Node[BUCKET_SIZE]);
        }
        
        @Override
        public V find(int hash, K key) {
            int index = (hash >>> shift) & MASK;
            Node<K, V> child = children[index];
            return child == null ? null : child.find(hash, key);
        }
        
        @Override
        public Node<K, V> insert(int shift, int hash, K key, V value) {
            int index = (hash >>> shift) & MASK;
            Node<K, V> child = children[index];
            Node<K, V> newChild = child == null 
                ? new LeafNode<>(hash, key, value)
                : child.insert(shift + SHIFT, hash, key, value);
            
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
            return new BranchNode<>(newChildren);
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
        return root.find(key.hashCode(), key);
    }
    
    public PersistentHashMap<K, V> put(K key, V value) {
        int hash = key.hashCode();
        Node<K, V> newRoot = root == null 
            ? new LeafNode<>(hash, key, value)
            : root.insert(0, hash, key, value);
        
        return new PersistentHashMap<>(newRoot, size + (containsKey(key) ? 0 : 1));
    }
    
    public boolean containsKey(K key) {
        return get(key) != null;
    }
}
```

---

## 関数型プログラミングでの不変性

### レンズパターン

```java
// レンズパターンで深くネストしたオブジェクトの更新
public class Lens<T, V> {
    private final Function<T, V> getter;
    private final BiFunction<T, V, T> setter;
    
    public Lens(Function<T, V> getter, BiFunction<T, V, T> setter) {
        this.getter = getter;
        this.setter = setter;
    }
    
    public V get(T target) {
        return getter.apply(target);
    }
    
    public T set(T target, V value) {
        return setter.apply(target, value);
    }
    
    public T modify(T target, Function<V, V> modifier) {
        return set(target, modifier.apply(get(target)));
    }
    
    // レンズの合成
    public <W> Lens<T, W> compose(Lens<V, W> other) {
        return new Lens<>(
            target -> other.get(getter.apply(target)),
            (target, value) -> setter.apply(target, other.set(getter.apply(target), value))
        );
    }
}

// 使用例
@Value
@With
class Address {
    String street;
    String city;
    String zipCode;
}

@Value
@With
class Person {
    String name;
    Address address;
}

public class LensExample {
    // Personのaddressフィールドへのレンズ
    static final Lens<Person, Address> personAddressLens = new Lens<>(
        Person::getAddress,
        (person, address) -> person.withAddress(address)
    );
    
    // AddressのcityフィールドへのレンズS
    static final Lens<Address, String> addressCityLens = new Lens<>(
        Address::getCity,
        (address, city) -> address.withCity(city)
    );
    
    // 合成されたレンズ：Person -> City
    static final Lens<Person, String> personCityLens = 
        personAddressLens.compose(addressCityLens);
    
    public static void main(String[] args) {
        Person person = new Person("Alice", 
            new Address("123 Main St", "OldCity", "12345"));
        
        // 深くネストしたフィールドの更新
        Person updated = personCityLens.set(person, "NewCity");
        
        System.out.println("Original: " + person.getAddress().getCity());
        System.out.println("Updated: " + updated.getAddress().getCity());
    }
}
```

### イミュータブルな状態管理

```java
// Redux風の状態管理
public final class State<T> {
    private final T value;
    private final List<Consumer<T>> listeners;
    
    private State(T value, List<Consumer<T>> listeners) {
        this.value = value;
        this.listeners = List.copyOf(listeners);
    }
    
    public static <T> State<T> of(T initialValue) {
        return new State<>(initialValue, List.of());
    }
    
    public T getValue() {
        return value;
    }
    
    public State<T> update(Function<T, T> updater) {
        T newValue = updater.apply(value);
        State<T> newState = new State<>(newValue, listeners);
        
        // リスナーに通知
        listeners.forEach(listener -> listener.accept(newValue));
        
        return newState;
    }
    
    public State<T> subscribe(Consumer<T> listener) {
        List<Consumer<T>> newListeners = new ArrayList<>(listeners);
        newListeners.add(listener);
        return new State<>(value, newListeners);
    }
}

// アプリケーション状態の例
@Value
class AppState {
    List<String> todos;
    boolean loading;
    Optional<String> error;
    
    static AppState initial() {
        return new AppState(List.of(), false, Optional.empty());
    }
}

// Reduxスタイルのアクション
sealed interface Action permits AddTodo, RemoveTodo, SetLoading, SetError {}
record AddTodo(String text) implements Action {}
record RemoveTodo(int index) implements Action {}
record SetLoading(boolean loading) implements Action {}
record SetError(String error) implements Action {}

// リデューサー
public class AppReducer {
    public static AppState reduce(AppState state, Action action) {
        return switch (action) {
            case AddTodo(var text) -> 
                new AppState(
                    append(state.getTodos(), text),
                    state.isLoading(),
                    state.getError()
                );
            
            case RemoveTodo(var index) -> 
                new AppState(
                    removeAt(state.getTodos(), index),
                    state.isLoading(),
                    state.getError()
                );
            
            case SetLoading(var loading) -> 
                new AppState(
                    state.getTodos(),
                    loading,
                    state.getError()
                );
            
            case SetError(var error) -> 
                new AppState(
                    state.getTodos(),
                    false,
                    Optional.of(error)
                );
        };
    }
    
    private static <T> List<T> append(List<T> list, T item) {
        List<T> newList = new ArrayList<>(list);
        newList.add(item);
        return List.copyOf(newList);
    }
    
    private static <T> List<T> removeAt(List<T> list, int index) {
        List<T> newList = new ArrayList<>(list);
        newList.remove(index);
        return List.copyOf(newList);
    }
}
```

---

## パフォーマンス最適化

### オブジェクトプーリングとフライウェイト

```java
// 不変オブジェクトのプーリング
public final class ImmutablePoint {
    private static final Map<String, ImmutablePoint> POOL = new ConcurrentHashMap<>();
    private static final int POOL_SIZE_LIMIT = 1000;
    
    private final int x;
    private final int y;
    
    private ImmutablePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // ファクトリメソッドでプーリング
    public static ImmutablePoint of(int x, int y) {
        // よく使われる値は事前にキャッシュ
        if (x >= -128 && x <= 127 && y >= -128 && y <= 127) {
            String key = x + "," + y;
            return POOL.computeIfAbsent(key, k -> new ImmutablePoint(x, y));
        }
        
        // 範囲外の値は新規作成
        return new ImmutablePoint(x, y);
    }
    
    // equals と hashCode は必須
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ImmutablePoint)) return false;
        ImmutablePoint other = (ImmutablePoint) obj;
        return x == other.x && y == other.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
```

### 遅延評価と不変性

```java
// 遅延評価される不変フィールド
public final class LazyImmutable {
    private final String name;
    private volatile String expensiveField;  // 遅延初期化
    
    public LazyImmutable(String name) {
        this.name = Objects.requireNonNull(name);
    }
    
    public String getName() {
        return name;
    }
    
    // ダブルチェックロッキングで遅延初期化
    public String getExpensiveField() {
        String result = expensiveField;
        if (result == null) {
            synchronized (this) {
                result = expensiveField;
                if (result == null) {
                    expensiveField = result = computeExpensiveField();
                }
            }
        }
        return result;
    }
    
    private String computeExpensiveField() {
        // 高コストな計算
        return "Computed: " + name.toUpperCase();
    }
}
```

---

## まとめ

不変性の設計パターンと実装テクニックにより：

1. **スレッドセーフティ**: 同期化なしで並行アクセス可能
2. **予測可能性**: 状態変更がないため動作が予測しやすい
3. **キャッシュ効率**: 安全にキャッシュ可能
4. **関数型プログラミング**: 純粋関数と組み合わせやすい
5. **構造共有**: メモリ効率的な永続的データ構造

これらの技術は、特に並行処理が必要なシステムや、関数型プログラミングスタイルを採用したシステムにおいて重要です。ただし、パフォーマンスとメモリ使用量のトレードオフを考慮し、適切に使用することが重要です。