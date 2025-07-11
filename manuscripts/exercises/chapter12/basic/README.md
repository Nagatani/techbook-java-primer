# 第12章 基礎課題：レコード(Records)

## 概要
本章で学んだRecordクラスの基本的な使い方を練習します。不変データ構造の定義、自動生成されるメソッドの理解、基本的なRecordの活用方法を身につけましょう。

## 課題一覧

### 課題1: 基本的なRecordの定義
`BasicRecords.java`を作成し、以下のRecordを定義してください：

1. **Pointレコード**
   ```java
   // 2次元座標を表すRecord
   public record Point(double x, double y) {}
   ```
   以下のメソッドを実装：
   - 原点からの距離を計算する`distance()`メソッド
   - 別のPointまでの距離を計算する`distanceTo(Point other)`メソッド

2. **Personレコード**
   ```java
   // 人物情報を表すRecord
   public record Person(String name, int age, String email) {}
   ```
   コンパクトコンストラクタで以下のバリデーションを実装：
   - nameが空でないこと
   - ageが0以上120以下であること
   - emailに"@"が含まれていること

3. **Bookレコード**
   ```java
   // 書籍情報を表すRecord
   public record Book(String isbn, String title, String author, int year) {}
   ```
   以下のメソッドを追加：
   - 書籍情報を整形して返す`formatInfo()`メソッド

### 課題2: RecordとDTOパターン
`RecordDTO.java`を作成し、以下を実装してください：

1. **APIレスポンス用のRecord**
   ```java
   public record UserResponse(
       String id,
       String username,
       String email,
       LocalDateTime createdAt
   ) {}
   ```

2. **リクエスト用のRecord**
   ```java
   public record CreateUserRequest(
       String username,
       String email,
       String password
   ) {
       // パスワードの強度チェックを実装
   }
   ```

3. **変換メソッドの実装**
   - UserエンティティからUserResponseへの変換
   - CreateUserRequestからUserエンティティへの変換

### 課題3: ネストしたRecordの活用
`NestedRecords.java`を作成し、以下を実装してください：

1. **住所情報を含む顧客Record**
   ```java
   public record Address(
       String street,
       String city,
       String zipCode
   ) {}
   
   public record Customer(
       String id,
       String name,
       Address address,
       List<String> phoneNumbers
   ) {}
   ```

2. **注文情報の階層構造**
   ```java
   public record OrderItem(
       String productId,
       int quantity,
       BigDecimal unitPrice
   ) {}
   
   public record Order(
       String orderId,
       Customer customer,
       List<OrderItem> items,
       LocalDateTime orderDate
   ) {
       // 合計金額を計算するメソッドを実装
   }
   ```

### 課題4: Recordの比較と活用
`RecordComparison.java`を作成し、以下を実装してください：

1. **自動生成されるメソッドの確認**
   - equals()メソッドの動作確認
   - hashCode()メソッドの動作確認
   - toString()メソッドの出力確認

2. **Recordのコレクション操作**
   ```java
   List<Person> people = List.of(
       new Person("Alice", 25, "alice@example.com"),
       new Person("Bob", 30, "bob@example.com"),
       new Person("Charlie", 25, "charlie@example.com")
   );
   ```
   - 年齢でグループ化
   - 名前でソート
   - Setに格納して重複を除去

## 実装のヒント

### コンパクトコンストラクタの使用
```java
public record Person(String name, int age) {
    public Person {
        // コンパクトコンストラクタ内でバリデーション
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
    }
}
```

### Recordへのメソッド追加
```java
public record Rectangle(double width, double height) {
    // 面積を計算するメソッド
    public double area() {
        return width * height;
    }
    
    // 正方形かどうかを判定
    public boolean isSquare() {
        return width == height;
    }
}
```

## 提出前チェックリスト
- [ ] すべてのRecordが適切に定義されている
- [ ] バリデーションが適切に実装されている
- [ ] 自動生成されるメソッドの動作を確認している
- [ ] Javadocコメントが記述されている

## 評価基準
- Recordの基本的な構文を理解しているか
- コンパクトコンストラクタを適切に使用できているか
- Recordの不変性を理解しているか
- DTOパターンでRecordを適切に活用できているか