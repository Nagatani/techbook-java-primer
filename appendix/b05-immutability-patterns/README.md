# 不変性の設計パターンと実装テクニック

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
**実際の影響**: 金融システムで残高計算エラー、ECサイトで在庫数不整合

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

### ビジネスへの深刻な影響

**実際の障害事例:**
- **某ECサイト**: GC停止時間の長期化によりタイムアウト多発、売上機会損失
- **金融機関**: 残高計算バグにより不正な取引が発生、金融庁への報告事案
- **ゲーム会社**: プレイヤー状態の同期問題によりデータ破損、ユーザー信頼失墜

**不変性パターンによる改善効果:**
- **並行安全性**: 同期化不要で100%安全な並行アクセス
- **予測可能性**: 状態変更がないためシステム動作を予測しやすい
- **デバッグ効率**: 副作用がないためバグ調査時間を大幅短縮
- **パフォーマンス**: ロック不要により高いスループットを実現

## サンプルコード構成

### 1. 基本的な不変性パターン
- `ImmutabilityPatternsDemo.java`: 完全不変性の実装デモンストレーション
  - 完全な不変クラスの設計と実装
  - ビルダーパターンとの組み合わせ
  - オブジェクトプーリングによる最適化
  - 遅延評価パターンの実装
  - 並行安全性の確保と検証

### 2. 永続的データ構造とCopy-on-Write
- `PersistentDataStructuresDemo.java`: 構造共有による効率的な不変データ構造
  - Copy-on-Writeマップの実装
  - 永続的リスト（構造共有）
  - Trie構造ベースの永続的ハッシュマップ
  - カスタム不変ArrayList
  - パフォーマンス比較と最適化

### 3. 関数型プログラミングでの不変性
- `FunctionalPatternsDemo.java`: レンズパターンと関数型状態管理
  - レンズパターンによる深いネスト更新
  - Redux風の不変状態管理
  - 関数合成とメモ化
  - 非同期状態管理パターン
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

### JVMフラグを使用した並行性テスト
```bash
# 並行アクセステスト
java -XX:+PrintGCDetails -Xms256m -Xmx512m \
     com.example.immutability.ImmutabilityPatternsDemo

# 大量データでのメモリ効率テスト
java -Xms1g -Xmx2g -XX:+UseG1GC \
     com.example.immutability.PersistentDataStructuresDemo
```

## 学習ポイント

### 1. 完全な不変性の実現

#### 設計原則
```java
public final class ImmutablePerson {
    private final String name;
    private final LocalDate birthDate;
    private final List<String> nicknames;
    
    // コンストラクタでの防御的コピー
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
- **防御的コピー**: コンストラクタとゲッターでの完全な隔離
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
// アクション定義
public sealed interface Action permits AddTodo, RemoveTodo, SetLoading {}

// リデューサー
public static AppState reduce(AppState state, Action action) {
    return switch (action) {
        case AddTodo(var text) -> state.withTodos(appendTodo(state.getTodos(), text));
        case RemoveTodo(var index) -> state.withTodos(removeTodoAt(state.getTodos(), index));
        case SetLoading(var loading) -> state.withLoading(loading);
    };
}
```

## パフォーマンス特性

### 操作コストの比較

| データ構造 | 読み取り | 更新 | メモリ効率 | 並行性 |
|------------|----------|------|------------|--------|
| ArrayList | O(1) | O(1) | 優秀 | 要同期 |
| ImmutableArrayList | O(1) | O(n) | 劣る | 完全安全 |
| HashMap | O(1) | O(1) | 優秀 | 要同期 |
| CopyOnWriteMap | O(1) | O(n) | 中程度 | 完全安全 |
| LinkedList | O(n) | O(1) | 中程度 | 要同期 |
| PersistentList | O(n) | O(1) | 優秀 | 完全安全 |

### ベンチマーク結果例

```
=== Data Structure Performance Comparison ===

--- ArrayList vs ImmutableArrayList ---
ArrayList:          45 ms
ImmutableArrayList: 2,340 ms
Ratio: 52.00x

--- HashMap vs CopyOnWriteMap ---
HashMap:        23 ms
CopyOnWriteMap: 1,890 ms
Ratio: 82.17x

--- LinkedList vs PersistentList ---
LinkedList:     12 ms
PersistentList: 28 ms
Ratio: 2.33x
```

## 実践的な最適化ガイドライン

### 1. 適切なパターンの選択

#### 不変オブジェクトを使用すべき場合
```java
// ✅ 適切な使用例
public final class ConfigurationData {
    private final Map<String, String> properties;
    private final List<String> allowedHosts;
    
    // 設定データは変更頻度が低く、安全性が重要
}

// ❌ 不適切な使用例  
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
// ✅ 読み込み重視のシナリオ
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

#### ファクトリーパターンとの統合
```java
public class ImmutableCollections {
    public static <T> ImmutableList<T> copyOf(Collection<T> collection) {
        if (collection instanceof ImmutableList) {
            return (ImmutableList<T>) collection; // 既に不変なら再利用
        }
        return new ImmutableList<>(collection);
    }
}
```

## 高度なトピック

### 1. 関数型プログラミングとの統合

#### モナドパターン
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

System.out.printf("Heap usage: %d MB / %d MB%n",
    heapUsage.getUsed() / (1024 * 1024),
    heapUsage.getMax() / (1024 * 1024));
```

#### ガベージコレクションの最適化
```bash
# G1GCによる大きなヒープの効率的な管理
-XX:+UseG1GC -XX:MaxGCPauseMillis=200

# 不変オブジェクト向けの世代別GC最適化
-XX:NewRatio=2 -XX:SurvivorRatio=8
```

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
// ❌ 問題のあるコード
for (int i = 0; i < 10000; i++) {
    list = list.append(i); // 毎回O(n)コピー
}

// ✅ 改善されたコード
List<Integer> mutableList = new ArrayList<>();
for (int i = 0; i < 10000; i++) {
    mutableList.add(i);
}
ImmutableList<Integer> list = ImmutableList.copyOf(mutableList);
```

#### 問題3: 深いネスト構造の更新
```java
// ❌ 複雑なネスト更新
Person updated = person.withAddress(
    person.getAddress().withCity(
        person.getAddress().getCity().toUpperCase()
    )
);

// ✅ レンズパターンの活用
Person updated = personCityLens.modify(person, String::toUpperCase);
```

このプロジェクトを通じて、不変性パターンの理論と実践を深く理解し、堅牢で保守しやすいJavaアプリケーションの設計能力を身につけることができます。不変性は複雑さを管理し、並行プログラミングを安全にする強力な手法ですが、パフォーマンスとのトレードオフを理解して適切に活用することが重要です。