# 付録B.05 不変性の設計パターンと実装テクニック

Javaにおける不変性の設計パターン、Copy-on-Write、永続的データ構造、関数型プログラミングでの不変性パターンについて学習できるプロジェクトです。

## 概要

不変性は並行プログラミングやファンクショナルプログラミングにおいて重要な概念であり、バグの少ない堅牢なシステム設計の基礎となります。このプロジェクトでは、実際のビジネス課題を解決する不変性パターンの実装方法を学習できます。

## なぜ不変性パターンが重要なのか

### 実際の並行性バグと解決

**問題1: 共有状態による競合状態**
```java
// 可変オブジェクトによる典型的な並行性バグ
public class MutableCounter {
    private int count = 0;  // 複数スレッドから変更される
    
    public void increment() {
        count++;  // 非原子的操作：読み取り→加算→書き込み
    }
    
    public int getCount() {
        return count;  // 不正確な値が返される可能性
    }
}
```
複数のスレッドが同時に`increment()`を呼ぶと、加算が失われて回数が合わなくなります。

**問題2: 防御的コピーの欠如**
```java
// 不適切な実装：内部状態の漏洩
public class MutableOrder {
    private final List<OrderItem> items = new ArrayList<>();
    
    public List<OrderItem> getItems() {
        return items;  // 危険：内部状態への直接参照
    }
}

// 使用側で意図しない変更が発生
MutableOrder order = new MutableOrder();
List<OrderItem> items = order.getItems();
items.clear();  // 注文内容が予期せず削除される
```

### 不変性パターンで得られること

- 並行安全性
    + 状態が変わらないオブジェクトは、同期なしで複数のスレッドから読める
- 予測可能性
    + 一度作ったオブジェクトの状態が変わらないので、動作を追いやすい
- デバッグのしやすさ
    + どこかで書き換えられたという種類のバグが起きない
- 注意点
    + 「今の状態を指す参照」を差し替える操作は、別に原子的にする必要がある（`AtomicReference`など）

## サンプルコード構成

### 1. 基本的な不変性パターン
- `ImmutabilityPatternsDemo.java`: 完全不変性の実装デモンストレーション
  - 完全な不変クラスの設計と実装
  - ビルダーパターンとの組み合わせ
  - オブジェクトプーリングによる最適化
  - 遅延評価パターンの実装
  - `AtomicReference`による参照の差し替えと、複数スレッドからの更新の確認

### 2. 永続的データ構造とCopy-on-Write
- `PersistentDataStructuresDemo.java`: 構造共有による効率的な不変データ構造
  - Copy-on-Writeマップの実装
  - 永続的リスト（構造共有）
  - Trie構造ベースの永続的ハッシュマップ
  - カスタム不変ArrayList
  - 可変のデータ構造との時間の比較

### 3. 関数型プログラミングでの不変性
- `FunctionalPatternsDemo.java`: レンズパターンと関数型状態管理
  - レンズパターンによる深いネスト更新
  - Redux風の不変状態管理（sealedインターフェイスとrecordによるアクション）
  - 関数合成とメモ化
  - `AtomicReference`を使った非同期状態管理
  - アクションベースの状態変更

## 実行方法

### コンパイルと実行
```bash
javac -d . src/main/java/com/example/immutability/*.java

# 基本的な不変性パターンのデモ
java com.example.immutability.ImmutabilityPatternsDemo

# 永続的データ構造のデモ
java com.example.immutability.PersistentDataStructuresDemo

# 関数型パターンのデモ
java com.example.immutability.FunctionalPatternsDemo
```

### GCのログを見ながら実行
```bash
# 不変オブジェクトを多く作ったときのGCの様子を表示
java -Xlog:gc -Xms256m -Xmx512m \
     com.example.immutability.ImmutabilityPatternsDemo
```

## 学習ポイント

### 1. 完全な不変性の実現

#### 設計原則
```java
public final class ImmutablePerson {
    private final String name;
    private final LocalDate birthDate;
    private final List<String> nicknames;
    
    // コンストラクターでの防御的コピー
    public ImmutablePerson(String name, LocalDate birthDate, List<String> nicknames) {
        this.name = Objects.requireNonNull(name);
        this.birthDate = Objects.requireNonNull(birthDate);
        this.nicknames = List.copyOf(nicknames); // 防御的コピー
    }
    
    // withメソッドパターン
    public ImmutablePerson withName(String newName) {
        return new ImmutablePerson(newName, birthDate, nicknames);
    }
}
```

#### 重要な実装ポイント
- **final修飾子の使用**: クラス、フィールドの変更を防止
- **防御的コピー**: コンストラクターで受け取ったコレクションをコピーし、外から変更されないようにする（`List.copyOf`の結果は変更できないので、ゲッターでそのまま返してよい）
- **withメソッド**: 関数型スタイルでの更新パターン
- **equals/hashCodeの実装**: 値による等価性の保証

### 2. Copy-on-Writeパターン

#### 基本実装
```java
public class CopyOnWriteMap<K, V> {
    private volatile Map<K, V> map = new HashMap<>();
    
    public V get(K key) {
        return map.get(key);  // 読み込みは高速
    }
    
    public synchronized V put(K key, V value) {
        Map<K, V> newMap = new HashMap<>(map);  // コピー作成
        V oldValue = newMap.put(key, value);
        map = newMap;  // 原子的な置き換え
        return oldValue;
    }
}
```

#### 適用シナリオ
- **読み込み重視**: 読み込みが書き込みより圧倒的に多い
- **スナップショット**: 特定時点での状態保存が必要
- **並行安全性**: 複数スレッドからの安全なアクセス

### 3. 永続的データ構造

#### 構造共有の実装
```java
public class PersistentList<T> {
    private static class Node<T> {
        final T value;
        final Node<T> next;
    }
    
    // O(1) で新しい要素を追加（構造を共有）
    public PersistentList<T> prepend(T value) {
        return new PersistentList<>(new Node<>(value, head), size + 1);
    }
}
```

#### メモリ効率の特徴
```
Original:    A → B → C → D
New List 1:  X → A → B → C → D  (A,B,C,Dを共有)
New List 2:  Y → A → B → C → D  (A,B,C,Dを共有)
```

### 4. レンズパターン

#### 深いネスト更新
```java
// Person -> Address -> City への更新
static final Lens<Person, String> personCityLens = 
    personAddressLens.compose(addressCityLens);

// 使用例
Person updated = personCityLens.set(person, "NewCity");
```

#### 関数型操作
```java
// 条件付き更新
Person conditionalUpdate = personAgeLens.modifyIf(
    person, 
    age -> age >= 18, 
    age -> age + 1
);
```

### 5. 不変状態管理

#### Reduxパターン
```java
// アクション定義（sealedで種類を限り、各アクションはrecordにする）
public sealed interface Action
        permits AddTodo, RemoveTodo, SetLoading, SetError, ClearError {}

public record AddTodo(String text) implements Action {}
public record RemoveTodo(int index) implements Action {}
public record SetLoading(boolean loading) implements Action {}
public record SetError(String error) implements Action {}
public record ClearError() implements Action {}

// リデューサー（すべてのアクションを網羅しているので、defaultは要らない）
public static AppState reduce(AppState state, Action action) {
    return switch (action) {
        case AddTodo(var text) -> state.withTodos(appendTodo(state.todos(), text));
        case RemoveTodo(var index) -> state.withTodos(removeTodoAt(state.todos(), index));
        case SetLoading(var loading) -> state.withLoading(loading);
        case SetError(var error) -> state.withError(Optional.of(error)).withLoading(false);
        case ClearError() -> state.withError(Optional.empty());
    };
}
```

## パフォーマンス特性

### 操作コストの比較

| データ構造 | 読み取り | 更新 | メモリ効率 | 並行性 |
|------------|----------|------|------------|--------|
| ArrayList | O(1) | O(1)（setと末尾への追加） | 優秀 | 要同期 |
| ImmutableArrayList | O(1) | O(n) | 劣る | 完全安全 |
| HashMap | O(1) | O(1) | 優秀 | 要同期 |
| CopyOnWriteMap | O(1) | O(n) | 中程度 | 完全安全 |
| LinkedList | O(n) | O(1)（先頭・末尾） | 中程度 | 要同期 |
| PersistentList | O(n) | O(1)（先頭への追加） | 優秀 | 完全安全 |

### 実行結果の形式

`PersistentDataStructuresDemo`は、次のような形式で測定結果を表示します（数値は省略しています）。
測定値はCPU・OS・JVMの状態によって大きく変わり、実行するたびにも変わります。
このデモは傾向を体験するためのもので、正確に測るにはJMH（Java Microbenchmark Harness）を使います。

```
=== Data Structure Performance Comparison ===
(Timings vary greatly by environment. Use JMH for accurate measurements.)

--- ArrayList vs ImmutableArrayList ---
ArrayList:          ... μs
ImmutableArrayList: ... μs
Ratio: ...x

--- HashMap vs CopyOnWriteMap ---
HashMap:        ... μs
CopyOnWriteMap: ... μs
Ratio: ...x

--- LinkedList vs PersistentList ---
LinkedList:     ... μs
PersistentList: ... μs
Ratio: ...x
```

更新のたびに全体をコピーするImmutableArrayListとCopyOnWriteMapは、要素が増えるほど大きく遅くなります。
先頭に追加するだけのPersistentListは、LinkedListと同じ程度の時間で済みます。
時間が短すぎて測れなかったときは、比の代わりに`n/a (too fast to measure)`と表示します。

## 実践的な最適化ガイドライン

### 1. 適切なパターンの選択

#### 不変オブジェクトを使用すべき場合
```java
// ○ 適切な使用例
public final class ConfigurationData {
    private final Map<String, String> properties;
    private final List<String> allowedHosts;
    
    // 設定データは変更頻度が低く、安全性が重要
}

// × 不適切な使用例  
public final class PerformanceCounter {
    private final long value;
    
    // 頻繁に変更される値には不向き
    public PerformanceCounter increment() {
        return new PerformanceCounter(value + 1); // 毎回新規作成
    }
}
```

#### Copy-on-Writeを使用すべき場合
```java
// ○ 読み込み重視のシナリオ
public class EventSubscribers {
    private final CopyOnWriteMap<String, List<EventListener>> subscribers;
    
    // リスナーの追加は稀、イベント配信は頻繁
    public void fireEvent(String eventType, Object data) {
        List<EventListener> listeners = subscribers.get(eventType);
        // 安全に並行読み取り可能
    }
}
```

### 2. メモリ最適化テクニック

#### オブジェクトプーリング
```java
public static ImmutablePoint of(int x, int y) {
    // よく使われる値はキャッシュ
    if (x >= -128 && x <= 127 && y >= -128 && y <= 127) {
        return POOL.computeIfAbsent(x + "," + y, k -> new ImmutablePoint(x, y));
    }
    return new ImmutablePoint(x, y);
}
```

#### 遅延評価
```java
// ダブルチェックロッキングでは、フィールドにvolatileが必要
private volatile String expensiveField;

public String getExpensiveField() {
    if (expensiveField == null) {
        synchronized (this) {
            if (expensiveField == null) {
                expensiveField = computeExpensiveField();
            }
        }
    }
    return expensiveField;
}
```

### 3. 設計パターンとの組み合わせ

#### ビルダーパターンとの統合
```java
public static class Builder {
    // ビルダーは可変、最終的に不変オブジェクトを生成
    public ImmutableOrder build() {
        // バリデーションとimmutableオブジェクトの生成
        return new ImmutableOrder(this);
    }
}
```

#### ファクトリパターンとの統合
```java
public class ImmutableCollections {
    public static <T> ImmutableArrayList<T> copyOf(Collection<T> collection) {
        if (collection instanceof ImmutableArrayList<T> list) {
            return list; // 既に不変なら再利用
        }
        return new ImmutableArrayList<>(collection);
    }
}
```

標準ライブラリの`List.copyOf`も、渡されたリストが変更できないリストなら、コピーせずにそのまま返すことがあります。

## 高度なトピック

### 1. 関数型プログラミングとの統合

#### モナドパターン
`Optional`と同じ考え方の型を自分で作るなら、次のようになります（このプロジェクトのコードには含めていません）。

```java
public class Maybe<T> {
    public <U> Maybe<U> map(Function<T, U> mapper) {
        return isEmpty() ? empty() : of(mapper.apply(value));
    }
    
    public <U> Maybe<U> flatMap(Function<T, Maybe<U>> mapper) {
        return isEmpty() ? empty() : mapper.apply(value);
    }
}
```

#### 関数合成
```java
Function<String, String> processText = FunctionComposition.compose(
    String::trim,
    String::toUpperCase,
    s -> "PREFIX_" + s
);
```

### 2. 並行プログラミングでの活用

#### スレッドセーフなキャッシュ
```java
public class ImmutableCache<K, V> {
    private final CopyOnWriteMap<K, V> cache = new CopyOnWriteMap<>();
    
    public V computeIfAbsent(K key, Function<K, V> computer) {
        V value = cache.get(key);
        if (value == null) {
            synchronized (this) {
                value = cache.get(key);
                if (value == null) {
                    value = computer.apply(key);
                    cache.put(key, value);
                }
            }
        }
        return value;
    }
}
```

#### アクターモデルとの統合
```java
public class ImmutableActor {
    private volatile ImmutableState state;
    
    // アクターでは、メッセージを1つのスレッドが順に処理する。
    // receiveを複数のスレッドから同時に呼ぶなら、AtomicReferenceのupdateAndGetを使う
    public void receive(Message message) {
        state = state.handle(message); // 常に新しい状態を生成
    }
}
```

### 3. パフォーマンス監視とチューニング

#### メモリ使用量の監視
```java
// JVM メモリ使用量の追跡
MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();

IO.println("Heap usage: %d MB / %d MB".formatted(
    heapUsage.getUsed() / (1024 * 1024),
    heapUsage.getMax() / (1024 * 1024)));
```

#### ガベージコレクションの最適化
```bash
# G1GC（Java 25の既定）で、停止時間の目標を指定する
-XX:MaxGCPauseMillis=200
```

短命な不変オブジェクトを多く作っても、世代別GCは若い世代をまとめて回収するので、ふつうは特別な設定は要りません。
G1では若い世代の大きさを自動で調整するため、`-XX:NewRatio`などで固定しないのがふつうです。

## トラブルシューティング

### よくある問題と対策

#### 問題1: OutOfMemoryError
```bash
# 症状: 大量の不変オブジェクト生成によるメモリ不足
# 対策: ヒープサイズの調整とプーリングの活用
-Xmx4g -XX:+UseG1GC

# オブジェクトプーリングの導入
ImmutablePoint.of(x, y); // キャッシュされた値を再利用
```

#### 問題2: 更新性能の劣化
```java
// 問題のあるコード（ImmutableArrayList）
for (int i = 0; i < 10000; i++) {
    list = list.append(i); // 毎回O(n)コピー
}

// 改善されたコード：可変のリストで組み立ててから、最後に1回だけ不変にする
List<Integer> mutableList = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    mutableList.add(i);
}
ImmutableArrayList<Integer> list = new ImmutableArrayList<>(mutableList);
```

#### 問題3: 深いネスト構造の更新
```java
// × 複雑なネスト更新
Person updated = person.withAddress(
    person.getAddress().withCity(
        person.getAddress().getCity().toUpperCase()
    )
);

// ○ レンズパターンの活用
Person updated = personCityLens.modify(person, String::toUpperCase);
```

このプロジェクトを通じて、不変性パターンの理論と実践を深く理解し、堅牢で保守しやすいJavaアプリケーションの設計能力を身につけることができます。不変性は複雑さを管理し、並行プログラミングを安全にする強力な手法ですが、更新のたびにオブジェクトを作るコストとのトレードオフを理解して適切に活用することが重要です。