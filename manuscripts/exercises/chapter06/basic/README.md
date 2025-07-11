# 第6章 基礎課題：不変性とfinalキーワードの実践

## 概要

本課題では、不変性（Immutability）とfinalキーワードの基本概念を実践的に学習します。不変オブジェクトの設計、finalキーワードの3つの用途、防御的コピーの実装を通じて、安全で予測可能なプログラムの作成方法を身につけます。

## 学習目標

- finalキーワードの3つの用途（変数、メソッド、クラス）の理解と実践
- 完全な不変オブジェクトの設計と実装
- 防御的コピーによるデータ保護の実装
- 不変性がもたらすスレッドセーフティの理解

## 課題1：不変なPointクラス

### 要求仕様

2次元座標を表す完全に不変な`Point`クラスを実装してください。

#### 基本要件

```java
public final class Point {
    // フィールド（すべてfinal）
    // - x: X座標
    // - y: Y座標
    
    // コンストラクタ
    // - 座標を初期化
    
    // メソッド
    // - getX(), getY(): 座標の取得（setterは作らない）
    // - distance(Point other): 他の点との距離を計算
    // - translate(double dx, double dy): 新しいPointを返す
    // - rotate(double angle): 原点周りに回転した新しいPointを返す
    // - equals(), hashCode(): 適切に実装
    // - toString(): "(x, y)"形式で表示
}
```

### 実装のヒント

```java
public final class Point {
    private final double x;
    private final double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    // 不変性を保つため、新しいインスタンスを返す
    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy);
    }
    
    public Point rotate(double angle) {
        double rad = Math.toRadians(angle);
        double newX = x * Math.cos(rad) - y * Math.sin(rad);
        double newY = x * Math.sin(rad) + y * Math.cos(rad);
        return new Point(newX, newY);
    }
    
    // equalsとhashCodeは必ず両方実装
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return Double.compare(point.x, x) == 0 && 
               Double.compare(point.y, y) == 0;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
```

### テストケース

```java
public class PointTest {
    public static void main(String[] args) {
        // 不変性のテスト
        Point p1 = new Point(3.0, 4.0);
        Point p2 = p1.translate(1.0, 2.0);
        
        System.out.println("p1: " + p1); // (3.0, 4.0) - 変更されない
        System.out.println("p2: " + p2); // (4.0, 6.0) - 新しいインスタンス
        
        // 距離の計算
        Point origin = new Point(0.0, 0.0);
        System.out.println("Distance: " + p1.distance(origin)); // 5.0
        
        // 回転
        Point p3 = new Point(1.0, 0.0);
        Point p4 = p3.rotate(90);
        System.out.println("Rotated: " + p4); // (0.0, 1.0)に近い値
        
        // 等価性
        Point p5 = new Point(3.0, 4.0);
        System.out.println("p1 equals p5: " + p1.equals(p5)); // true
    }
}
```

## 課題2：不変なPersonクラス（防御的コピー）

### 要求仕様

可変オブジェクトを含む不変な`Person`クラスを実装してください。

```java
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public final class Person {
    // フィールド
    // - name (String): 名前
    // - birthDate (LocalDate): 生年月日
    // - addresses (List<Address>): 住所リスト
    // - phoneNumbers (Map<String, String>): 電話番号（種別→番号）
    
    // 要件
    // 1. すべてのフィールドをfinalにする
    // 2. 可変オブジェクト（List, Map）に対して防御的コピーを実装
    // 3. getterでも防御的コピーを返す
    // 4. Addressクラスも不変にする
}
```

### Addressクラス

```java
public final class Address {
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;
    
    public Address(String street, String city, String postalCode, String country) {
        this.street = Objects.requireNonNull(street, "street cannot be null");
        this.city = Objects.requireNonNull(city, "city cannot be null");
        this.postalCode = Objects.requireNonNull(postalCode, "postalCode cannot be null");
        this.country = Objects.requireNonNull(country, "country cannot be null");
    }
    
    // getterメソッド（setterは作らない）
    // equals, hashCode, toString
}
```

### Personクラスの実装

```java
public final class Person {
    private final String name;
    private final LocalDate birthDate;
    private final List<Address> addresses;
    private final Map<String, String> phoneNumbers;
    
    public Person(String name, LocalDate birthDate, 
                  List<Address> addresses, Map<String, String> phoneNumbers) {
        this.name = Objects.requireNonNull(name);
        this.birthDate = Objects.requireNonNull(birthDate);
        
        // 防御的コピー（入力の検証も含む）
        this.addresses = new ArrayList<>();
        for (Address addr : addresses) {
            this.addresses.add(Objects.requireNonNull(addr));
        }
        
        this.phoneNumbers = new HashMap<>();
        for (Map.Entry<String, String> entry : phoneNumbers.entrySet()) {
            this.phoneNumbers.put(
                Objects.requireNonNull(entry.getKey()),
                Objects.requireNonNull(entry.getValue())
            );
        }
    }
    
    // getterでも防御的コピーを返す
    public List<Address> getAddresses() {
        return new ArrayList<>(addresses);
    }
    
    public Map<String, String> getPhoneNumbers() {
        return new HashMap<>(phoneNumbers);
    }
    
    // 年齢計算（LocalDateは不変なので安全）
    public int getAge() {
        return Period.between(birthDate, LocalDate.now()).getYears();
    }
    
    // 新しい住所を追加した新しいPersonを返す
    public Person withAdditionalAddress(Address newAddress) {
        List<Address> newAddresses = new ArrayList<>(this.addresses);
        newAddresses.add(Objects.requireNonNull(newAddress));
        return new Person(name, birthDate, newAddresses, phoneNumbers);
    }
}
```

## 課題3：不変なBankAccountクラス

### 要求仕様

スレッドセーフな不変の銀行口座クラスを実装してください。

```java
public final class ImmutableBankAccount {
    // フィールド
    // - accountNumber (String): 口座番号
    // - holderName (String): 口座名義人
    // - balance (BigDecimal): 残高
    // - transactions (List<Transaction>): 取引履歴
    // - createdAt (LocalDateTime): 作成日時
    
    // メソッド
    // - deposit(BigDecimal amount): 入金後の新しい口座を返す
    // - withdraw(BigDecimal amount): 出金後の新しい口座を返す
    // - getTransactionHistory(): 取引履歴のコピーを返す
    
    // 内部クラス
    // - Transaction: 不変な取引記録
}
```

### 実装例

```java
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public final class ImmutableBankAccount {
    private final String accountNumber;
    private final String holderName;
    private final BigDecimal balance;
    private final List<Transaction> transactions;
    private final LocalDateTime createdAt;
    
    // トランザクションを表す不変クラス
    public static final class Transaction {
        private final String id;
        private final TransactionType type;
        private final BigDecimal amount;
        private final LocalDateTime timestamp;
        private final BigDecimal balanceAfter;
        
        public enum TransactionType {
            DEPOSIT, WITHDRAWAL, INITIAL
        }
        
        public Transaction(TransactionType type, BigDecimal amount, BigDecimal balanceAfter) {
            this.id = UUID.randomUUID().toString();
            this.type = type;
            this.amount = amount;
            this.timestamp = LocalDateTime.now();
            this.balanceAfter = balanceAfter;
        }
        
        // getterメソッド
        // equals, hashCode, toString
    }
    
    // 初期口座作成用コンストラクタ
    public ImmutableBankAccount(String accountNumber, String holderName, BigDecimal initialBalance) {
        this.accountNumber = Objects.requireNonNull(accountNumber);
        this.holderName = Objects.requireNonNull(holderName);
        this.balance = Objects.requireNonNull(initialBalance);
        this.createdAt = LocalDateTime.now();
        
        // 初期取引を記録
        this.transactions = new ArrayList<>();
        this.transactions.add(new Transaction(
            Transaction.TransactionType.INITIAL, 
            initialBalance, 
            initialBalance
        ));
    }
    
    // プライベートコンストラクタ（内部での新インスタンス作成用）
    private ImmutableBankAccount(String accountNumber, String holderName, 
                                BigDecimal balance, List<Transaction> transactions, 
                                LocalDateTime createdAt) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
        this.transactions = new ArrayList<>(transactions);
        this.createdAt = createdAt;
    }
    
    public ImmutableBankAccount deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要があります");
        }
        
        BigDecimal newBalance = balance.add(amount);
        List<Transaction> newTransactions = new ArrayList<>(transactions);
        newTransactions.add(new Transaction(
            Transaction.TransactionType.DEPOSIT,
            amount,
            newBalance
        ));
        
        return new ImmutableBankAccount(
            accountNumber, holderName, newBalance, newTransactions, createdAt
        );
    }
    
    public ImmutableBankAccount withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("出金額は正の値である必要があります");
        }
        
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("残高不足です");
        }
        
        BigDecimal newBalance = balance.subtract(amount);
        List<Transaction> newTransactions = new ArrayList<>(transactions);
        newTransactions.add(new Transaction(
            Transaction.TransactionType.WITHDRAWAL,
            amount,
            newBalance
        ));
        
        return new ImmutableBankAccount(
            accountNumber, holderName, newBalance, newTransactions, createdAt
        );
    }
    
    // 防御的コピーを返す
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);
    }
}
```

## 課題4：finalメソッドとfinalクラスの活用

### 要求仕様

セキュリティが重要な認証トークンクラスを実装してください。

```java
public final class AuthToken {
    // このクラスは継承不可（final class）
    
    private final String token;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final Set<String> permissions;
    
    // finalメソッド（オーバーライド不可）
    public final boolean isValid() {
        return LocalDateTime.now().isBefore(expiresAt);
    }
    
    public final boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    // その他の実装
}
```

### 高度な実装例：ビルダーパターンとの組み合わせ

```java
public final class AuthToken {
    private final String token;
    private final String userId;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final Set<String> permissions;
    private final Map<String, String> claims;
    
    private AuthToken(Builder builder) {
        this.token = generateToken();
        this.userId = Objects.requireNonNull(builder.userId);
        this.issuedAt = LocalDateTime.now();
        this.expiresAt = issuedAt.plus(builder.duration);
        this.permissions = Collections.unmodifiableSet(
            new HashSet<>(builder.permissions)
        );
        this.claims = Collections.unmodifiableMap(
            new HashMap<>(builder.claims)
        );
    }
    
    // Builderクラス（不変オブジェクトの構築を支援）
    public static final class Builder {
        private String userId;
        private Duration duration = Duration.ofHours(1);
        private final Set<String> permissions = new HashSet<>();
        private final Map<String, String> claims = new HashMap<>();
        
        public Builder(String userId) {
            this.userId = userId;
        }
        
        public Builder withDuration(Duration duration) {
            this.duration = Objects.requireNonNull(duration);
            return this;
        }
        
        public Builder withPermission(String permission) {
            permissions.add(Objects.requireNonNull(permission));
            return this;
        }
        
        public Builder withClaim(String key, String value) {
            claims.put(
                Objects.requireNonNull(key),
                Objects.requireNonNull(value)
            );
            return this;
        }
        
        public AuthToken build() {
            return new AuthToken(this);
        }
    }
    
    // finalメソッド群
    public final boolean isValid() {
        return LocalDateTime.now().isBefore(expiresAt);
    }
    
    public final boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    public final boolean hasAllPermissions(Collection<String> requiredPermissions) {
        return permissions.containsAll(requiredPermissions);
    }
    
    // トークンの更新（新しいインスタンスを返す）
    public final AuthToken refresh(Duration additionalTime) {
        if (!isValid()) {
            throw new IllegalStateException("期限切れのトークンは更新できません");
        }
        
        return new Builder(userId)
            .withDuration(Duration.between(issuedAt, expiresAt).plus(additionalTime))
            .build();
    }
    
    private static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
```

## 評価ポイント

1. **不変性の完全な実装**（35点）
   - すべてのフィールドがfinal
   - setterメソッドがない
   - 状態変更は新しいインスタンスを返す

2. **防御的コピーの適切な実装**（25点）
   - コンストラクタでの防御的コピー
   - getterでの防御的コピー
   - 深いコピーの実装

3. **finalキーワードの適切な使用**（20点）
   - final変数の正しい初期化
   - finalメソッドの適切な選択
   - finalクラスの適切な使用

4. **スレッドセーフティ**（20点）
   - 不変性によるスレッドセーフの実現
   - 同期化が不要な設計
   - 並行アクセスでの安全性

## 発展学習の提案

1. **パフォーマンスの考慮**：
   - オブジェクトプーリングの実装
   - フライウェイトパターンの適用
   - 遅延評価の実装

2. **関数型プログラミングスタイル**：
   - メソッドチェーンの実装
   - 流暢なインターフェース
   - モナド的な操作

3. **永続データ構造**：
   - 構造共有による効率化
   - Copy-on-Writeの実装
   - Trieベースのコレクション

4. **不変コレクション**：
   - ImmutableListの実装
   - ImmutableMapの実装
   - GuavaやVavrの活用

これらの基礎課題を通じて、不変性とfinalキーワードの重要性を理解し、安全で保守性の高いプログラムを作成する技術を身につけてください。