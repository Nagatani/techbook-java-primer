# 第6章 不変性とfinal - 解答例

## 概要

この章では、Javaにおける不変性（Immutability）とfinal修飾子の実践的な使用方法について学習します。オブジェクト指向プログラミングにおいて、不変性は安全で予測可能なコードを書くための重要な概念です。

## 学習内容

### 1. 不変性の基礎概念
- **不変オブジェクト（Immutable Object）**：作成後に状態を変更できないオブジェクト
- **Value Equality**：値による等価性の実現
- **Thread Safety**：不変オブジェクトは本質的にスレッドセーフ

### 2. final修飾子の活用
- **final変数**：再代入の防止
- **final クラス**：継承の防止
- **final メソッド**：オーバーライドの防止

### 3. 不変クラスの設計原則
- すべてのフィールドをfinal、privateにする
- セッターメソッドを提供しない
- ミュータブルオブジェクトの防御的コピー
- 継承の防止（finalクラス）

## 解答例の詳細

### 1. ImmutablePerson.java
**目的**: 完全に不変なPersonクラスの実装

**重要なポイント**:
- `final`クラスで継承を防ぐ
- すべてのフィールドを`final`、`private`にする
- ミュータブルなフィールド（List）の防御的コピー
- withメソッドパターンによる変更操作
- Builderパターンによる複雑な構築

**実装の段階**:
```java
// 基本レベル：シンプルな不変クラス
public final class ImmutablePerson {
    private final String name;
    private final LocalDate birthDate;
    // ...
}

// 応用レベル：防御的コピーとwithメソッド
public ImmutablePerson withName(String newName) {
    return new ImmutablePerson(newName, this.birthDate, this.hobbies);
}

// 発展レベル：Builderパターン
public static class Builder {
    // ビルダーによる段階的構築
}
```

**よくある間違いと対策**:
```java
// ❌ 間違い：ミュータブルなフィールドをそのまま返す
public List<String> getHobbies() {
    return hobbies;  // 外部から変更可能
}

// ✅ 正解：不変ビューを返す
public List<String> getHobbies() {
    return Collections.unmodifiableList(hobbies);
}
```

### 2. Constants.java
**目的**: 定数の適切な定義と管理

**重要なポイント**:
- `public static final`による定数の定義
- 定数の命名規則（UPPER_SNAKE_CASE）
- 不変コレクションの作成
- 定数クラスの設計（プライベートコンストラクタ）

**実装の段階**:
```java
// 基本レベル：プリミティブ定数
public static final int MAX_RETRY_COUNT = 3;
public static final String DEFAULT_ENCODING = "UTF-8";

// 応用レベル：オブジェクト定数
public static final BigDecimal TAX_RATE = new BigDecimal("0.10");
public static final List<String> SUPPORTED_LANGUAGES = 
    Collections.unmodifiableList(Arrays.asList("Java", "Python"));

// 発展レベル：内部定数クラス
public static final class Database {
    private Database() {}
    public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/";
}
```

**設計パターン**:
- **定数クラス**：関連する定数のグループ化
- **内部定数クラス**：名前空間の整理
- **ユーティリティメソッド**：定数を使用した計算や判定

### 3. ValueObject.java
**目的**: ドメインモデルにおける値オブジェクトの実装

**重要なポイント**:
- 値による等価性（Value Equality）
- バリデーションの実装
- 複合値オブジェクトの設計
- ドメインロジックの実装

**実装の段階**:
```java
// 基本レベル：シンプルな値オブジェクト
final class Money {
    private final int amount;
    private final String currency;
    
    public Money add(Money other) {
        // 新しいインスタンスを返す
        return new Money(this.amount + other.amount, this.currency);
    }
}

// 応用レベル：バリデーション付き値オブジェクト
final class Email {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    public Email(String address) {
        if (!EMAIL_PATTERN.matcher(address).matches()) {
            throw new IllegalArgumentException("無効なメールアドレス");
        }
        this.address = address;
    }
}

// 発展レベル：複合値オブジェクト
final class Address {
    private final PostalCode postalCode;
    private final String prefecture;
    // 複数の値オブジェクトを組み合わせ
}
```

**値オブジェクトの利点**:
- **型安全性**：プリミティブ型による混同を防ぐ
- **ドメインロジック**：値に関する処理を集約
- **バリデーション**：作成時点での妥当性保証

### 4. ThreadSafeCounter.java
**目的**: スレッドセーフな不変性の実現

**重要なポイント**:
- Atomic クラスによるスレッドセーフ操作
- 統計情報の不変スナップショット
- 複数の同期化手法の比較
- パフォーマンスとスレッドセーフティのバランス

**実装の段階**:
```java
// 基本レベル：AtomicIntegerによる実装
public final class ThreadSafeCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    
    public int increment() {
        return count.incrementAndGet();
    }
}

// 応用レベル：統計情報の追加
private final AtomicLong totalIncrements = new AtomicLong(0);
public CounterStats getStats() {
    return new CounterStats(count.get(), totalIncrements.get());
}

// 発展レベル：複数の同期化手法
final class SynchronizedCounter {
    private synchronized int increment() { /* ... */ }
}

final class ReadWriteLockCounter {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    // 読み取り専用操作の最適化
}
```

**スレッドセーフティの実現方法**:
- **AtomicInteger**: 最も効率的、単純な操作向け
- **synchronized**: 複雑な操作の原子性保証
- **ReadWriteLock**: 読み取り頻度が高い場合の最適化

## テストの重要性

### 1. 不変性のテスト
```java
@Test
void testImmutability() {
    // 元のリストを変更しても影響を受けない
    hobbies.add("新しい趣味");
    assertEquals(Arrays.asList("読書", "映画鑑賞"), person.getHobbies());
}
```

### 2. スレッドセーフティのテスト
```java
@Test
void testConcurrentIncrement() throws InterruptedException {
    final int THREAD_COUNT = 10;
    final int OPERATIONS_PER_THREAD = 100;
    
    // マルチスレッドでの操作
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
    // テスト実装...
}
```

### 3. 境界値・異常値のテスト
```java
@Test
void testInvalidEmail() {
    assertThrows(IllegalArgumentException.class, () -> {
        new Email("invalid-email");
    });
}
```

## 実践的な活用場面

### 1. ドメインモデル
```java
// 値オブジェクトによる型安全性
public class Order {
    private final Money totalAmount;
    private final Email customerEmail;
    private final Address deliveryAddress;
}
```

### 2. 設定管理
```java
// 定数による設定の集約
public class DatabaseConfig {
    public static final String URL = Constants.Database.DEFAULT_URL;
    public static final int MAX_CONNECTIONS = Constants.Database.MAX_CONNECTIONS;
}
```

### 3. 並行処理
```java
// スレッドセーフな状態管理
public class StatisticsService {
    private final ThreadSafeCounter requestCounter = new ThreadSafeCounter();
    
    public void recordRequest() {
        requestCounter.increment();
    }
}
```

## 学習のポイント

### 1. 不変性の利点
- **予測可能性**: 状態が変わらないため、コードの動作を理解しやすい
- **スレッドセーフティ**: 不変オブジェクトは本質的にスレッドセーフ
- **キャッシュ可能**: ハッシュコードなどを安全にキャッシュできる
- **副作用の排除**: 意図しない状態変更を防げる

### 2. パフォーマンス考慮
- 不変オブジェクトは新しいインスタンスを作成するため、メモリ使用量が増加
- 適切な設計により、パフォーマンスの問題を最小限に抑える
- Builderパターンやwithメソッドによる効率的な構築

### 3. 設計の原則
- **単一責任の原則**: 値オブジェクトは特定の値に関する処理のみを持つ
- **Tell, Don't Ask**: 値オブジェクトにロジックを委譲
- **防御的プログラミング**: 不正な状態を作成時点で防ぐ

## 次章への学習導線

第7章「抽象クラスとインターフェイス」では、不変性と組み合わせて：
- 不変なインターフェイスの設計
- 抽象クラスでの不変性の実現
- SOLID原則と不変性の関係

これらの概念を組み合わせることで、より堅牢で保守性の高いオブジェクト指向設計を習得できます。

## まとめ

不変性とfinal修飾子は、Javaにおいて安全で予測可能なコードを書くための基本的な技術です。この章で学んだ内容を実際のプロジェクトで活用し、保守性の高いコードの作成を心がけましょう。

**重要な原則**:
1. **可能な限り不変にする** - 変更が必要な場合のみミュータブルにする
2. **防御的コピーを忘れない** - ミュータブルなオブジェクトを扱う際は慎重に
3. **バリデーションを適切に実装する** - 不正な状態を作成時点で防ぐ
4. **テストを充実させる** - 不変性とスレッドセーフティを確実に検証する