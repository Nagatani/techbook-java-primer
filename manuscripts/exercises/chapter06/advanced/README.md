# 第6章 応用課題：高度な不変性設計パターン

## 概要

本課題では、実践的な不変性設計パターンを学習します。複雑なデータ構造の不変化、効率的な更新戦略、関数型プログラミングスタイルの導入など、プロダクションレベルで使用される高度な技術を習得します。

## 学習目標

- 複雑なオブジェクトグラフの不変性管理
- Copy-on-Writeパターンの実装
- 永続データ構造の基礎
- レンズパターンによる不変オブジェクトの更新
- パフォーマンスを考慮した不変性設計

## 課題1：不変コレクションの実装

### 要求仕様

効率的な不変リストを実装してください。

#### ImmutableList インターフェース

```java
public interface ImmutableList<E> {
    E get(int index);
    int size();
    boolean isEmpty();
    boolean contains(E element);
    
    // 新しいリストを返すメソッド
    ImmutableList<E> add(E element);
    ImmutableList<E> remove(int index);
    ImmutableList<E> set(int index, E element);
    ImmutableList<E> addAll(Collection<? extends E> elements);
    
    // 関数型操作
    <R> ImmutableList<R> map(Function<? super E, ? extends R> mapper);
    ImmutableList<E> filter(Predicate<? super E> predicate);
    <R> R reduce(R identity, BiFunction<R, ? super E, R> accumulator);
}
```

### 実装例：構造共有を使った効率的な実装

```java
public final class PersistentList<E> implements ImmutableList<E> {
    private final Object[] elements;
    private final int size;
    
    // 空のリスト用のシングルトン
    private static final PersistentList<?> EMPTY = new PersistentList<>(new Object[0], 0);
    
    private PersistentList(Object[] elements, int size) {
        this.elements = elements;
        this.size = size;
    }
    
    @SuppressWarnings("unchecked")
    public static <E> ImmutableList<E> empty() {
        return (ImmutableList<E>) EMPTY;
    }
    
    public static <E> ImmutableList<E> of(E... elements) {
        if (elements.length == 0) {
            return empty();
        }
        return new PersistentList<>(elements.clone(), elements.length);
    }
    
    @Override
    public ImmutableList<E> add(E element) {
        // 構造共有：既存の配列を可能な限り再利用
        if (size < elements.length) {
            // 配列に空きがある場合
            Object[] newElements = elements.clone();
            newElements[size] = element;
            return new PersistentList<>(newElements, size + 1);
        } else {
            // 配列を拡張する必要がある場合
            Object[] newElements = Arrays.copyOf(elements, size + 1);
            newElements[size] = element;
            return new PersistentList<>(newElements, size + 1);
        }
    }
    
    @Override
    public ImmutableList<E> remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        if (size == 1) {
            return empty();
        }
        
        Object[] newElements = new Object[size - 1];
        System.arraycopy(elements, 0, newElements, 0, index);
        System.arraycopy(elements, index + 1, newElements, index, size - index - 1);
        
        return new PersistentList<>(newElements, size - 1);
    }
    
    @Override
    public <R> ImmutableList<R> map(Function<? super E, ? extends R> mapper) {
        Object[] mapped = new Object[size];
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            E element = (E) elements[i];
            mapped[i] = mapper.apply(element);
        }
        return new PersistentList<>(mapped, size);
    }
    
    @Override
    public ImmutableList<E> filter(Predicate<? super E> predicate) {
        List<E> filtered = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked")
            E element = (E) elements[i];
            if (predicate.test(element)) {
                filtered.add(element);
            }
        }
        
        if (filtered.size() == size) {
            return this; // 変更なし
        }
        
        return new PersistentList<>(filtered.toArray(), filtered.size());
    }
}
```

### より高度な実装：Trieベースの永続ベクター

```java
public final class PersistentVector<E> implements ImmutableList<E> {
    private static final int SHIFT = 5;
    private static final int SIZE = 1 << SHIFT;
    private static final int MASK = SIZE - 1;
    
    private final int size;
    private final int shift;
    private final Object[] root;
    private final Object[] tail;
    
    // Trieノードの作成と更新
    private static Object[] doAssoc(int level, Object[] node, int i, Object val) {
        Object[] newNode = node.clone();
        if (level == 0) {
            newNode[i & MASK] = val;
        } else {
            int subidx = (i >>> level) & MASK;
            newNode[subidx] = doAssoc(level - SHIFT, 
                (Object[]) node[subidx], i, val);
        }
        return newNode;
    }
    
    @Override
    public ImmutableList<E> set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        
        if (index >= tailoff()) {
            // tail内の更新
            Object[] newTail = tail.clone();
            newTail[index & MASK] = element;
            return new PersistentVector<>(size, shift, root, newTail);
        }
        
        // trie内の更新
        return new PersistentVector<>(size, shift, 
            doAssoc(shift, root, index, element), tail);
    }
    
    private int tailoff() {
        return size - tail.length;
    }
}
```

## 課題2：イミュータブルな状態管理システム

### 要求仕様

Reduxスタイルの状態管理システムを実装してください。

```java
// 状態を表す不変クラス
public final class AppState {
    private final ImmutableMap<String, User> users;
    private final ImmutableList<Product> products;
    private final ShoppingCart cart;
    private final UserPreferences preferences;
    
    // レンズを使った更新
    public AppState updateUser(String userId, Function<User, User> updater) {
        User oldUser = users.get(userId);
        if (oldUser == null) {
            return this;
        }
        
        User newUser = updater.apply(oldUser);
        if (oldUser.equals(newUser)) {
            return this;
        }
        
        return new AppState(
            users.put(userId, newUser),
            products,
            cart,
            preferences
        );
    }
}
```

### レンズパターンの実装

```java
// レンズ：不変オブジェクトの特定の部分にアクセス・更新するための抽象
public interface Lens<S, A> {
    A get(S s);
    S set(S s, A a);
    
    default S modify(S s, Function<A, A> f) {
        return set(s, f.apply(get(s)));
    }
    
    // レンズの合成
    default <B> Lens<S, B> compose(Lens<A, B> other) {
        return new Lens<S, B>() {
            @Override
            public B get(S s) {
                return other.get(Lens.this.get(s));
            }
            
            @Override
            public S set(S s, B b) {
                return Lens.this.set(s, other.set(Lens.this.get(s), b));
            }
        };
    }
}

// 使用例
public final class AppStateLenses {
    // ユーザーのレンズ
    public static Lens<AppState, ImmutableMap<String, User>> users() {
        return new Lens<AppState, ImmutableMap<String, User>>() {
            @Override
            public ImmutableMap<String, User> get(AppState s) {
                return s.getUsers();
            }
            
            @Override
            public AppState set(AppState s, ImmutableMap<String, User> users) {
                return new AppState(users, s.getProducts(), s.getCart(), s.getPreferences());
            }
        };
    }
    
    // 特定ユーザーのレンズ
    public static Lens<AppState, User> user(String userId) {
        return users().compose(mapLens(userId));
    }
    
    // ユーザー名のレンズ
    public static Lens<User, String> userName() {
        return new Lens<User, String>() {
            @Override
            public String get(User user) {
                return user.getName();
            }
            
            @Override
            public User set(User user, String name) {
                return user.withName(name);
            }
        };
    }
}

// 使用例
public class StateUpdateExample {
    public void updateUserName(AppState state, String userId, String newName) {
        // レンズの合成により、深くネストしたプロパティを簡単に更新
        Lens<AppState, String> userNameLens = 
            AppStateLenses.user(userId).compose(AppStateLenses.userName());
        
        AppState newState = userNameLens.set(state, newName);
    }
}
```

## 課題3：イベントソーシングシステム

### 要求仕様

不変イベントを使ったイベントソーシングシステムを実装してください。

```java
// イベントの基底クラス
public abstract class DomainEvent {
    private final String aggregateId;
    private final LocalDateTime timestamp;
    private final long version;
    
    protected DomainEvent(String aggregateId, long version) {
        this.aggregateId = Objects.requireNonNull(aggregateId);
        this.timestamp = LocalDateTime.now();
        this.version = version;
    }
    
    // すべてfinalメソッド
    public final String getAggregateId() { return aggregateId; }
    public final LocalDateTime getTimestamp() { return timestamp; }
    public final long getVersion() { return version; }
}

// 具体的なイベント
public final class AccountCreatedEvent extends DomainEvent {
    private final String accountNumber;
    private final String holderName;
    private final BigDecimal initialBalance;
    
    // 不変性を保証
}

public final class MoneyDepositedEvent extends DomainEvent {
    private final BigDecimal amount;
    private final String description;
}

// イベントストア
public final class EventStore {
    private final ImmutableMap<String, ImmutableList<DomainEvent>> events;
    
    public EventStore append(DomainEvent event) {
        String aggregateId = event.getAggregateId();
        ImmutableList<DomainEvent> existingEvents = 
            events.getOrDefault(aggregateId, ImmutableList.empty());
        
        // バージョンチェック
        if (!existingEvents.isEmpty()) {
            DomainEvent lastEvent = existingEvents.get(existingEvents.size() - 1);
            if (event.getVersion() != lastEvent.getVersion() + 1) {
                throw new ConcurrentModificationException(
                    "Expected version " + (lastEvent.getVersion() + 1) + 
                    " but got " + event.getVersion());
            }
        }
        
        return new EventStore(
            events.put(aggregateId, existingEvents.add(event))
        );
    }
    
    // アグリゲートの再構築
    public <T> T reconstruct(String aggregateId, 
                            Supplier<T> initialState,
                            BiFunction<T, DomainEvent, T> eventApplier) {
        ImmutableList<DomainEvent> aggregateEvents = 
            events.getOrDefault(aggregateId, ImmutableList.empty());
        
        return aggregateEvents.reduce(initialState.get(), eventApplier);
    }
}
```

### 実装例：銀行口座のイベントソーシング

```java
public final class BankAccountAggregate {
    private final String accountId;
    private final String holderName;
    private final BigDecimal balance;
    private final boolean active;
    private final long version;
    
    // イベントから状態を再構築
    public static BankAccountAggregate fromEvents(List<DomainEvent> events) {
        return events.stream()
            .reduce(null, BankAccountAggregate::apply, (a, b) -> b);
    }
    
    private static BankAccountAggregate apply(BankAccountAggregate state, DomainEvent event) {
        if (event instanceof AccountCreatedEvent) {
            AccountCreatedEvent e = (AccountCreatedEvent) event;
            return new BankAccountAggregate(
                e.getAggregateId(),
                e.getHolderName(),
                e.getInitialBalance(),
                true,
                e.getVersion()
            );
        } else if (event instanceof MoneyDepositedEvent) {
            MoneyDepositedEvent e = (MoneyDepositedEvent) event;
            return state.withBalance(state.balance.add(e.getAmount()))
                       .withVersion(e.getVersion());
        } else if (event instanceof MoneyWithdrawnEvent) {
            MoneyWithdrawnEvent e = (MoneyWithdrawnEvent) event;
            return state.withBalance(state.balance.subtract(e.getAmount()))
                       .withVersion(e.getVersion());
        }
        // ... その他のイベント
        
        return state;
    }
    
    // コマンドの実行
    public List<DomainEvent> handle(Command command) {
        if (command instanceof CreateAccountCommand) {
            return handleCreateAccount((CreateAccountCommand) command);
        } else if (command instanceof DepositMoneyCommand) {
            return handleDeposit((DepositMoneyCommand) command);
        }
        // ... その他のコマンド
        
        throw new UnsupportedOperationException("Unknown command: " + command);
    }
    
    private List<DomainEvent> handleDeposit(DepositMoneyCommand cmd) {
        // ビジネスルールの検証
        if (!active) {
            throw new IllegalStateException("Account is not active");
        }
        
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        // イベントの生成
        return Collections.singletonList(
            new MoneyDepositedEvent(accountId, version + 1, cmd.getAmount(), cmd.getDescription())
        );
    }
}
```

## 課題4：Copy-on-Writeコレクション

### 要求仕様

スレッドセーフなCopy-on-Writeマップを実装してください。

```java
public final class CopyOnWriteMap<K, V> {
    private volatile ImmutableMap<K, V> map;
    
    public CopyOnWriteMap() {
        this.map = ImmutableMap.empty();
    }
    
    public V get(K key) {
        return map.get(key);
    }
    
    public void put(K key, V value) {
        ImmutableMap<K, V> oldMap;
        ImmutableMap<K, V> newMap;
        
        do {
            oldMap = map;
            newMap = oldMap.put(key, value);
        } while (!compareAndSet(oldMap, newMap));
    }
    
    private boolean compareAndSet(ImmutableMap<K, V> expect, ImmutableMap<K, V> update) {
        // 実際の実装ではAtomicReferenceやVarHandleを使用
        synchronized (this) {
            if (map == expect) {
                map = update;
                return true;
            }
            return false;
        }
    }
    
    // 条件付き更新
    public boolean computeIfPresent(K key, BiFunction<K, V, V> remappingFunction) {
        ImmutableMap<K, V> oldMap;
        ImmutableMap<K, V> newMap;
        
        do {
            oldMap = map;
            V oldValue = oldMap.get(key);
            
            if (oldValue == null) {
                return false;
            }
            
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue == null) {
                newMap = oldMap.remove(key);
            } else if (newValue.equals(oldValue)) {
                return true; // 変更なし
            } else {
                newMap = oldMap.put(key, newValue);
            }
        } while (!compareAndSet(oldMap, newMap));
        
        return true;
    }
}
```

## 評価ポイント

1. **高度な不変性パターンの実装**（30点）
   - 構造共有の適切な実装
   - 永続データ構造の理解
   - Copy-on-Writeの正しい実装

2. **パフォーマンスの考慮**（25点）
   - 不要なコピーの回避
   - メモリ効率の最適化
   - 適切なアルゴリズムの選択

3. **関数型プログラミングスタイル**（25点）
   - レンズパターンの実装
   - 高階関数の活用
   - 副作用の排除

4. **並行性への対応**（20点）
   - スレッドセーフな実装
   - ロックフリーアルゴリズムの理解
   - 原子性の保証

## 発展学習の提案

1. **Clojureのデータ構造**：
   - Persistent Hash Map
   - Persistent Vector
   - Transient collections

2. **関数型言語の概念**：
   - Zipper
   - Finger Tree
   - RRB-Tree

3. **リアクティブプログラミング**：
   - Immutable Stateの管理
   - Event Streamの処理
   - Time-travel debugging

4. **分散システムでの不変性**：
   - CRDTの実装
   - Event Sourcingの拡張
   - Distributed Snapshotting

これらの応用課題を通じて、実践的な不変性設計パターンと高度な実装技術を身につけてください。