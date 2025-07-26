# <b>9章</b> <span>レコード(Records)とデータ指向プログラミング</span> <small>シンプルなデータ構造の実現</small>

## 本章の学習目標

### この章で学ぶこと

1. Recordの基本理解
    - 不変データクラスの簡潔な定義、自動生成されるメソッド、ボイラープレート削減
2. Recordの高度な機能
    - コンパクトコンストラクタ、バリデーション実装、カスタムメソッド追加
3. パターンマッチングとの統合
    - instanceofパターン、switch式での活用、ネストされたRecordの分解
4. 実践的な活用パターン
    - DTOパターン、Value Objectパターン、イベントソーシング

### この章を始める前に

第6章の不変性とfinalキーワード、equals()等の基本メソッドを理解していれば準備完了です。

## Recordとは？

Recordは、Java 16で正式に導入された、不変（immuテーブル）なデータを保持するための、簡潔なクラスを定義するための機能です。

これまで、データを保持するためだけのクラス（データクラスやデータキャリアと呼ばれる）を作るには、多くの定型的なコード（ボイラープレートコード）が必要でした。

-   `private final`なフィールド
-   全フィールドを初期化するコンストラクタ
-   全フィールドのゲッタメソッド（アクセサ）
-   `equals()`, `hashCode()`, `toString()` メソッドのオーバーライド

`Record`は、これらの定型コードをコンパイラが自動的に生成してくれます。

### 基本的な構文

<span class="listing-number">**サンプルコード9-1**</span>

```java
public record Person(String name, int age) {}
```

これだけで、以下をすべて定義したのとほぼ同じ意味になります。

Recordが自動生成する等価なクラス構造：

#### 上記のRecord定義は、コンパイラによって以下のような完全なクラス定義に展開されます。この自動展開により、開発者は大量のボイラープレートコードを書く必要がなくなります

<span class="listing-number">**サンプルコード9-2**</span>

```java
public final class Person {
    private final String name;  // ①
    private final int age;      // ①

    public Person(String name, int age) {  // ②
        this.name = name;
        this.age = age;
    }

    public String name() { return this.name; }  // ③
    public int age() { return this.age; }       // ③

    @Override
    public boolean equals(Object o) { /* 全フィールドを比較する実装 */ }  // ④

    @Override
    public int hashCode() { /* 全フィールドから計算する実装 */ }  // ⑤

    @Override
    public String toString() { /* 全フィールドを表示する実装 */ }  // ⑥
}
```

### 自動生成される要素の詳細

- ①　不変フィールド
    + 全フィールドがprivate finalとして宣言されます。インスタンス生成後は変更不可です
- ②　正準コンストラクタ
    + 全フィールドを初期化する標準的なコンストラクタ
- ③　アクセサメソッド
    + フィールド名と同じ名前のメソッド（従来のgetXxx()形式ではない）
- ④　equals()メソッド
    + 全フィールドの値を比較する、nullチェックや型検査を含む実装
- ⑤　hashCode()メソッド
    + 全フィールドから一貫性のあるハッシュ値を計算
- ⑥　toString()メソッド
    + フィールド名と値を読みやすい形式で表示

### Recordのメリット・デメリット

メリット:
- 簡潔さ: ボイラープレートコードを劇的に削減できる
- 不変性（Immutability）: フィールドはすべて`final`となり、一度作成したオブジェクトの状態は変更できない。プログラムの安全性が向上し、マルチスレッド環境でも安心して扱える
- 明確な意図: このクラスが「データを保持するためのものである」という意図が明確になる

デメリット:
- 拡張性の制限: `Record`は暗黙的に`final`であり、ほかのクラスを継承したり、ほかのクラスに継承させたりすることはできません
- 可変オブジェクトには不向き: 状態を変更する必要があるオブジェクトには使えません

## Recordの使い方

`Record`の使い方は、通常のクラスとほとんど同じです。

Recordの基本的な使用方法

### Recordは通常のクラスと同様にインスタンス化し、メソッドを呼び出すことができます。以下の例では、Recordの主要な機能を実際に使用する方法を示しています

<span class="listing-number">**サンプルコード9-3**</span>

```java
public class RecordExample {
    public static void main(String[] args) {
        Person alice = new Person("Alice", 30);    // ①
        Person bob = new Person("Bob", 40);        // ①
        Person alice2 = new Person("Alice", 30);   // ①

        System.out.println("名前: " + alice.name());  // ②
        System.out.println("年齢: " + alice.age());   // ②

        System.out.println(alice);  // ③

        System.out.println("aliceとbobは等しいか？: " + alice.equals(bob));     // ④
        System.out.println("aliceとalice2は等しいか？: " + alice.equals(alice2)); // ④
    }
}
```

各操作の詳細解説

- ①　インスタンス生成
    + 通常のクラスと同じく`new`キーワードでインスタンスを作成する
    + 同じ値を持つ複数のインスタンス（alice vs alice2）も作成可能
- ②　フィールドアクセス
    + アクセサメソッド名はフィールド名と同じ（従来のgetName（）、getAge（）ではなく、name()、age()）
    + この命名規則により、より自然で読みやすいコードになる
- ③　文字列表現
    + toString()メソッドが自動生成され、`Person[name=Alice, age=30]`のような読みやすい形式で表示される
- ④　値の比較
    + equals()メソッドが全フィールドの値を比較するように実装されているため、aliceとalice2は異なるインスタンスだが、同じ値を持つため等しいと判定される

### コンパクトコンストラクタ

`Record`では、引数のバリデーション（検証）などのために、コンパクトコンストラクタという特別な構文が使えます。引数リストを省略して記述し、フィールドへの代入（`this.x = x;`）は暗黙的に行われます。

<span class="listing-number">**サンプルコード9-4**</span>

```java
public record PositivePoint(int x, int y) {
    // コンパクトコンストラクタ
    public PositivePoint {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("座標は負の値にできません");
        }
        // ここで this.x = x; のような代入は不要（自動的に行われる）
    }
}
```

## 実践例：データ変換と処理

`Record`は、構造化されたデータを保持し、変換するのに非常に適しています。ここでは、CSVデータの処理を例に見てみましょう。

### データの定義

従業員データを表現する`PersonRecord`を定義します。

<span class="listing-number">**サンプルコード9-5**</span>

```java
// PersonRecord.java
public record PersonRecord(String name, int age, String city) {}
```

### データ処理の実装

<span class="listing-number">**サンプルコード9-6**</span>

```java
// DataProcessor.java
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DataProcessor {
    
    // サンプルデータの作成
    public static List<String> createSampleData() {
        List<String> data = new ArrayList<>();
        data.add("Alice,25,Tokyo");
        data.add("Bob,30,Osaka");
        data.add("Charlie,35,Nagoya");
        data.add("David,28,Kyoto");
        data.add("Eve,32,Fukuoka");
        return data;
    }
    
    // CSV形式の文字列をPersonRecordに変換
    public static PersonRecord parsePersonData(String csvLine) {
        String[] fields = csvLine.split(",");
        if (fields.length != 3) {
            return null; // 不正なデータはnullを返す
        }
        
        // 年齢の解析（エラー時はデフォルト値を使用）
        int age = 0;
        try {
            age = Integer.parseInt(fields[1].trim());
        } catch (NumberFormatException e) {
            // エラーの場合は0歳とする（後でフィルタリング）
        }
        
        return new PersonRecord(
            fields[0].trim(),
            age,
            fields[2].trim()
        );
    }
    
    public static void main(String[] args) {
        // サンプルデータの取得
        List<String> csvData = createSampleData();
        
        // データの処理
        List<PersonRecord> persons = csvData.stream()
            .map(DataProcessor::parsePersonData)
            .filter(person -> person != null && person.age() > 0) // 無効なデータを除外
            .collect(Collectors.toList());
        
        // 結果の表示
        System.out.println("=== 全従業員データ ===");
        persons.forEach(System.out::println);
        
        // 年齢別の集計
        System.out.println("\n=== 30歳以上の従業員 ===");
        persons.stream()
            .filter(person -> person.age() >= 30)
            .forEach(System.out::println);
        
        // 都市別のグループ化
        System.out.println("\n=== 都市別の従業員数 ===");
        persons.stream()
            .collect(Collectors.groupingBy(PersonRecord::city, Collectors.counting()))
            .forEach((city, count) -> System.out.println(city + ": " + count + "名"));
    }
}
```

この例では、Stream APIと組み合わせることで、データの変換、フィルタリング、集約を簡潔に記述できています。ファイルI/Oを使わずに、メモリ上のデータ処理として実装することで、例外処理の必要性を回避しています。

## データ指向プログラミング（DOP）

### DOPとは何か

データ指向プログラミング（Data-Oriented Programming, DOP）は、従来のオブジェクト指向プログラミング（OOP）とは異なるアプローチでソフトウェアを構築する設計パラダイムです

#### 従来のOOPとDOPの違い

##### 従来のOOPアプローチ:
<span class="listing-number">**サンプルコード9-7**</span>

```java
// データとロジックが密結合
public class Order {
    private List<OrderItem> items;
    private Customer customer;
    
    // データとロジックが混在
    public BigDecimal calculateTotal() { /* 計算ロジック */ }
    public void processPayment() { /* 決済ロジック */ }
    public void updateInventory() { /* 在庫更新ロジック */ }
    public void sendNotification() { /* 通知ロジック */ }
}
```

##### DOPアプローチ:

DOPでは、データ構造とビジネスロジックを明確に分離します。データはRecordsで定義し、処理は純粋関数として実装することで、テスト容易性と保守性が向上します。

<span class="listing-number">**サンプルコード9-8**</span>

```java
import java.util.List;
import java.time.LocalDateTime;
import java.math.BigDecimal;

// データの定義（Records）
public record Order(String orderId, Customer customer, List<OrderItem> items, LocalDateTime orderDate) {}
public record Customer(String id, String name, String email) {}
public record OrderItem(String productName, int quantity, BigDecimal price) {}

// ロジックの分離（純粋関数）
public class OrderProcessor {
    public static BigDecimal calculateTotal(Order order) {
        return order.items().stream()
            .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public static PaymentResult processPayment(Order order, PaymentMethod method) {
        // 支払い処理ロジック
    }
    
    public static void updateInventory(Order order) {
        // 在庫更新ロジック
    }
}
```

#### DOPの基本原則

1. データとロジックの明確な分離
   - データはRecordsで表現
   - ロジックは純粋関数で実装
   - 副作用を最小限に抑制

2. 不変性の活用
   - すべてのデータ構造が不変
   - スレッドセーフティの確保
   - 予測可能な動作

3. 表現力の向上
   - パターンマッチングによる簡潔な条件分岐
   - 型システムを活用した安全性

4. テスト容易性
   - 純粋関数によるユニットテストの簡素化
   - モック不要の設計

### DOPがもたらすビジネス価値

#### 開発効率の劇的向上

Recordsを使用することで、従来のデータクラス実装で必要だった大量のボイラープレートコードを劇的に削減できます。以下の比較例で、その効果を具体的に見てみましょう。

<span class="listing-number">**サンプルコード9-9**</span>

```java
// 従来のデータクラス：約100行のコード
public class TraditionalUser {
    private final String id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;
    
    // コンストラクタ、getter、equals、hashCode、toString...
    // 約90行のボイラープレートコード
}

// Record：1行で同等の機能
public record User(String id, String name, String email, LocalDateTime createdAt) {}
```

##### 実際の効果測定:
- コード量削減
    + 70-90%の大幅削減
- 開発時間
    + API開発時間50%短縮
- バグ率
    + データ関連バグ95%減少

#### 実際の改善事例

##### 某SaaS企業の事例:
- 課題
    + ユーザー情報管理で大量のボイラープレートコード
- 解決
    + RecordsとDOPの導入
- 効果
    + 開発時間50%短縮、保守コスト70%削減

##### 金融システムの事例:
- 課題
    + 複雑なデータモデルでの不整合バグ
- 解決
    + 不変データ構造と型安全性の活用
- 効果
    + データ不整合バグ90%削減、監査対応時間75%短縮

##### ECプラットフォームの事例:
- 課題
    + 商品分類ロジックの複雑化
- 解決
    + パターンマッチングによる条件分岐の簡素化
- 効果
    + ロジック理解時間60%短縮、新機能追加コスト50%削減

### DOPの設計パターン

#### データモデルの設計

DOPの最初のステップは、ドメインモデルを明確に表現することです。Recordsを使用することで、ビジネスドメインの概念を直接的にコードに反映できます。

<span class="listing-number">**サンプルコード9-10**</span>

```java
// ドメインモデルの明確な表現
public record Product(
    String id,
    String name,
    ProductCategory category,
    Money price,
    Inventory inventory
) {}

public record Money(BigDecimal amount, Currency currency) {
    public Money {
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        }
    }
    
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amount.add(other.amount), currency);
    }
}
```

#### ビジネスロジックの実装

DOPでは、ビジネスロジックは副作用のない純粋関数として実装します。これにより、モックが不要でコンポーネント間の結合が疑いため、ロジックの再利用性とテスト容易性が向上します。

<span class="listing-number">**サンプルコード9-11**</span>

```java
public class ProductService {
    // 純粋関数による処理
    public static boolean isAvailable(Product product) {
        return product.inventory().quantity() > 0;
    }
    
    public static Money calculateDiscountPrice(Product product, DiscountRate rate) {
        BigDecimal discountAmount = product.price().amount().multiply(rate.value());
        BigDecimal newAmount = product.price().amount().subtract(discountAmount);
        return new Money(newAmount, product.price().currency());
    }
    
    public static Product updateInventory(Product product, int soldQuantity) {
        Inventory newInventory = product.inventory().subtract(soldQuantity);
        return new Product(
            product.id(),
            product.name(),
            product.category(),
            product.price(),
            newInventory
        );
    }
}
```

#### 型安全な状態管理

sealed interfaceとRecordsを組み合わせることで、アプリケーションの状態を型レベルで安全に管理できます。これにより、不正な状態遷移をコンパイル時に防ぐことができます。

<span class="listing-number">**サンプルコード9-12**</span>

```java
// 状態を型で表現
public sealed interface OrderStatus permits Pending, Confirmed, Shipped, Delivered {}
public record Pending() implements OrderStatus {}
public record Confirmed(LocalDateTime confirmedAt) implements OrderStatus {}
public record Shipped(String trackingNumber, LocalDateTime shippedAt) implements OrderStatus {}
public record Delivered(LocalDateTime deliveredAt) implements OrderStatus {}

// パターンマッチングによる状態処理
public static String getStatusMessage(OrderStatus status) {
    return switch (status) {
        case Pending() -> "注文を受け付けました";
        case Confirmed(var confirmedAt) -> "注文が確定しました: " + confirmedAt;
        case Shipped(var trackingNumber, var shippedAt) -> 
            "発送しました（追跡番号: " + trackingNumber + "）";
        case Delivered(var deliveredAt) -> "配達完了: " + deliveredAt;
    };
}
```

## 高度なパターンマッチング

Java 17以降のパターンマッチングとRecordの組み合わせは、代数的データ型（Algebraic Data Types）を実現し、関数型プログラミングの強力な表現力をJavaにもたらします。

### sealed interfaceとRecordの組み合わせ

sealed interfaceは、実装可能なクラスを明示的に制限する機能で、Recordと組み合わせることで型安全な設計を実現できます

<span class="listing-number">**サンプルコード9-13**</span>

```java
// 図形を表現する代数的データ型
public sealed interface Shape permits Circle, Rectangle, Triangle {}

public record Circle(double radius) implements Shape {}
public record Rectangle(double width, double height) implements Shape {}
public record Triangle(double base, double height) implements Shape {}
```

### パターンマッチングによる処理

従来のinstance of + キャストの冗長な記述を、switch式とパターンマッチングで簡潔に表現できます。

<span class="listing-number">**サンプルコード9-14**</span>

```java
// 面積計算（パターンマッチング使用）
public static double calculateArea(Shape shape) {
    return switch (shape) {
        case Circle(var radius) -> Math.PI * radius * radius;
        case Rectangle(var width, var height) -> width * height;
        case Triangle(var base, var height) -> 0.5 * base * height;
    };
}

// 周囲計算
public static double calculatePerimeter(Shape shape) {
    return switch (shape) {
        case Circle(var radius) -> 2 * Math.PI * radius;
        case Rectangle(var width, var height) -> 2 * (width + height);
        case Triangle(var base, var height) -> {
            // 正三角形と仮定した場合の計算
            double side = Math.sqrt(Math.pow(base/2, 2) + Math.pow(height, 2));
            yield base + 2 * side;
        }
    };
}
```

### ネストしたパターンマッチング

複雑なデータ構造に対しても、ネストしたパターンで処理できます。

<span class="listing-number">**サンプルコード9-15**</span>

```java
// ネストしたRecord構造
public record Address(String street, String city, String country) {}
public record Person(String name, int age, Address address) {}
public record Company(String name, Person ceo, List<Person> employees) {}

// 深いパターンマッチング
public static String getLocationInfo(Object obj) {
    return switch (obj) {
        // ネストしたRecordの分解
        case Person(var name, var age, Address(var street, var city, "Japan")) ->
            name + "さんは日本の" + city + "在住です";
        
        case Person(var name, var age, Address(var street, var city, var country)) ->
            name + "さんは" + country + "の" + city + "在住です";
        
        // リストのパターンマッチング
        case Company(var companyName, Person(var ceoName, _, _), var employees) 
            when employees.size() > 100 ->
            companyName + "は" + ceoName + "がCEOの大企業です";
        
        case Company(var companyName, Person(var ceoName, _, _), var employees) ->
            companyName + "は" + ceoName + "がCEOの会社です（従業員" + employees.size() + "名）";
        
        default -> "不明な情報です";
    };
}
```

### ガード条件付きパターンマッチング

when句を使用して、パターンに条件を追加できます

<span class="listing-number">**サンプルコード9-16**</span>

```java
// 年齢による分類（ガード条件付き）
public static String categorizeAge(Person person) {
    return switch (person) {
        case Person(var name, var age, _) when age < 18 -> 
            name + "さんは未成年です";
        case Person(var name, var age, _) when age >= 65 -> 
            name + "さんは高齢者です";
        case Person(var name, var age, Address(_, _, "Japan")) when age >= 20 ->
            name + "さんは日本で飲酒可能な成人です";
        case Person(var name, _, _) -> 
            name + "さんは成人です";
    };
}
```

### 実用的な例：式評価器の実装

代数的データ型を使った式評価器の例で、パターンマッチングの強力さを示します。

<span class="listing-number">**サンプルコード9-17**</span>

```java
// 数式を表現する代数的データ型
public sealed interface Expr permits Const, Add, Mul, Var {}

public record Const(double value) implements Expr {}
public record Add(Expr left, Expr right) implements Expr {}
public record Mul(Expr left, Expr right) implements Expr {}
public record Var(String name) implements Expr {}

// 式の評価
public static double eval(Expr expr, Map<String, Double> env) {
    return switch (expr) {
        case Const(var value) -> value;
        case Add(var left, var right) -> eval(left, env) + eval(right, env);
        case Mul(var left, var right) -> eval(left, env) * eval(right, env);
        case Var(var name) -> env.getOrDefault(name, 0.0);
    };
}

// 式の簡約（最適化）
public static Expr simplify(Expr expr) {
    return switch (expr) {
        // 定数の計算
        case Add(Const(var a), Const(var b)) -> new Const(a + b);
        case Mul(Const(var a), Const(var b)) -> new Const(a * b);
        
        // 恒等元による簡約
        case Add(Const(0), var e) -> simplify(e);
        case Add(var e, Const(0)) -> simplify(e);
        case Mul(Const(1), var e) -> simplify(e);
        case Mul(var e, Const(1)) -> simplify(e);
        
        // 零元による簡約
        case Mul(Const(0), _) -> new Const(0);
        case Mul(_, Const(0)) -> new Const(0);
        
        // 再帰的簡約
        case Add(var left, var right) -> new Add(simplify(left), simplify(right));
        case Mul(var left, var right) -> new Mul(simplify(left), simplify(right));
        
        default -> expr;
    };
}

// 式の文字列表現
public static String toString(Expr expr) {
    return switch (expr) {
        case Const(var value) -> String.valueOf(value);
        case Add(var left, var right) -> 
            "(" + toString(left) + " + " + toString(right) + ")";
        case Mul(var left, var right) -> 
            "(" + toString(left) + " * " + toString(right) + ")";
        case Var(var name) -> name;
    };
}
```

### パターンマッチングの実用例

<span class="listing-number">**サンプルコード9-18**</span>

```java
public class ExpressionDemo {
    public static void main(String[] args) {
        // 式の構築: (x + 1) * (y + 0)
        Expr expr = new Mul(
            new Add(new Var("x"), new Const(1)),
            new Add(new Var("y"), new Const(0))
        );
        
        System.out.println("元の式: " + toString(expr));
        // 出力: ((x + 1.0) * (y + 0.0))
        
        // 式の簡約
        Expr simplified = simplify(expr);
        System.out.println("簡約後: " + toString(simplified));
        // 出力: ((x + 1.0) * y)
        
        // 変数代入による評価
        Map<String, Double> env = Map.of("x", 3.0, "y", 4.0);
        double result = eval(simplified, env);
        System.out.println("x=3, y=4の時の値: " + result);
        // 出力: x=3, y=4の時の値: 16.0
    }
}
```

このコードの実行結果は、式の構築、自動的な簡約化（たとえば、`y + 0`が`y`に簡約される）、そして変数の値を代入した評価を示します。パターンマッチングにより、複雑な条件分岐が簡潔で読みやすいコードで表現されています。

### 型安全性と網羅性

#### sealed interfaceとパターンマッチングにより、以下の利点が得られます

1. コンパイル時の網羅性チェック
    + すべてのケースを処理していないとコンパイルエラー
2. 型安全性
    + キャストなしでの安全なデータアクセス
3. 保守性
    + 新しい型を追加した際の変更箇所の特定が容易

<span class="listing-number">**サンプルコード9-19**</span>

```java
// 新しい図形を追加した場合
public sealed interface Shape permits Circle, Rectangle, Triangle, Square {}
public record Square(double side) implements Shape {}

// コンパイルエラー：Squareケースが処理されていない
public static double calculateArea(Shape shape) {
    return switch (shape) {
        case Circle(var radius) -> Math.PI * radius * radius;
        case Rectangle(var width, var height) -> width * height;
        case Triangle(var base, var height) -> 0.5 * base * height;
        // case Square(var side) -> side * side;  // これが必要
    };
}
```

## Recordsの内部実装とカスタマイズ

### コンパイラが生成するコード

Recordの背後では、Javaコンパイラが大量のコードを自動生成しています。このしくみを理解することで、Recordの動作や制限について深く理解できます。

<span class="listing-number">**サンプルコード9-20**</span>

```java
// ソースコード
public record Point(int x, int y) {}

// コンパイラが生成する実際のコード（概念的表現）
public final class Point extends Record {
    private final int x;
    private final int y;
    
    // カノニカルコンストラクタ
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // アクセサメソッド
    public int x() { return x; }
    public int y() { return y; }
    
    // equals メソッド
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point other &&
               x == other.x && y == other.y;
    }
    
    // hashCode メソッド
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    // toString メソッド
    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
    
    // Record特有のメソッド
    @Override
    public final Object[] componentArray() {
        return new Object[] { x, y };
    }
}
```

### コンパクトコンストラクタによるカスタマイズ

コンパクトコンストラクタは、Recordの強力なカスタマイズ機能です。パラメータリストを省略し、バリデーションや正規化を行えます

<span class="listing-number">**サンプルコード9-21**</span>

```java
// 基本的なコンパクトコンストラクタ
public record ValidatedEmail(String value) {
    public ValidatedEmail {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
        value = value.toLowerCase(); // 正規化
    }
    
    // 追加のメソッド
    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    public String localPart() {
        return value.substring(0, value.indexOf('@'));
    }
}
```

### 複雑なバリデーションの実装

以下の例では、より複雑なビジネスルールを持つMoneyレコードを示します。コンパクトコンストラクタ内で、nullチェック、負値チェック、通貨に応じた小数点以下の桁数の正規化を行っています。このような包括的なバリデーションにより、不正な状態のオブジェクトが作成されることを防ぎます。

<span class="listing-number">**サンプルコード9-22**</span>

```java
// 複雑なバリデーションの例
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        
        // 通貨の小数点桁数に合わせて正規化
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            amount = amount.setScale(
                currency.getDefaultFractionDigits(), 
                RoundingMode.HALF_UP
            );
        }
    }
    
    // ビジネスロジックメソッド
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amount.add(other.amount), currency);
    }
    
    public Money multiply(BigDecimal multiplier) {
        return new Money(amount.multiply(multiplier), currency);
    }
    
    public boolean isZero() {
        return amount.equals(BigDecimal.ZERO);
    }
}
```

### withメソッドパターン

不変性を保ちながら部分的な更新を行うwithメソッドパターンは、Recordで頻繁に使用されます。

<span class="listing-number">**サンプルコード9-23**</span>

```java
public record Configuration(
    String host,
    int port,
    boolean useSsl,
    Duration timeout,
    Map<String, String> properties
) {
    // コンパクトコンストラクタでバリデーション
    public Configuration {
        Objects.requireNonNull(host, "Host cannot be null");
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        Objects.requireNonNull(timeout, "Timeout cannot be null");
        if (timeout.isNegative()) {
            throw new IllegalArgumentException("Timeout cannot be negative");
        }
        // 防御的コピー
        properties = Map.copyOf(properties);
    }
    
    // withメソッド群
    public Configuration withHost(String newHost) {
        return new Configuration(newHost, port, useSsl, timeout, properties);
    }
    
    public Configuration withPort(int newPort) {
        return new Configuration(host, newPort, useSsl, timeout, properties);
    }
    
    public Configuration withSsl(boolean newUseSsl) {
        return new Configuration(host, port, newUseSsl, timeout, properties);
    }
    
    public Configuration withTimeout(Duration newTimeout) {
        return new Configuration(host, port, useSsl, newTimeout, properties);
    }
    
    public Configuration withProperty(String key, String value) {
        Map<String, String> newProperties = new HashMap<>(properties);
        newProperties.put(key, value);
        return new Configuration(host, port, useSsl, timeout, newProperties);
    }
    
    public Configuration withoutProperty(String key) {
        Map<String, String> newProperties = new HashMap<>(properties);
        newProperties.remove(key);
        return new Configuration(host, port, useSsl, timeout, newProperties);
    }
    
    // ファクトリメソッド
    public static Configuration createDefault() {
        return new Configuration(
            "localhost", 
            8080, 
            false, 
            Duration.ofSeconds(30),
            Map.of()
        );
    }
    
    public static Configuration forProduction(String host) {
        return new Configuration(
            host, 
            443, 
            true, 
            Duration.ofSeconds(10),
            Map.of("environment", "production")
        );
    }
}
```

### 追加メソッドの実装

Recordには、コンストラクタパラメータ以外の追加メソッドを定義できます。

<span class="listing-number">**サンプルコード9-24**</span>

```java
public record Range(int start, int end) {
    public Range {
        if (start > end) {
            throw new IllegalArgumentException("Start cannot be greater than end");
        }
    }
    
    // 範囲の長さ
    public int length() {
        return end - start;
    }
    
    // 値が範囲内かチェック
    public boolean contains(int value) {
        return value >= start && value <= end;
    }
    
    // 範囲の重複チェック
    public boolean overlaps(Range other) {
        return this.start <= other.end && other.start <= this.end;
    }
    
    // 範囲の結合
    public Range union(Range other) {
        if (!this.overlaps(other) && !this.isAdjacent(other)) {
            throw new IllegalArgumentException("Ranges must overlap or be adjacent");
        }
        return new Range(
            Math.min(this.start, other.start),
            Math.max(this.end, other.end)
        );
    }
    
    // 隣接チェック
    public boolean isAdjacent(Range other) {
        return this.end + 1 == other.start || other.end + 1 == this.start;
    }
    
    // Streamとの統合
    public IntStream stream() {
        return IntStream.rangeClosed(start, end);
    }
    
    // Iterableの実装
    public Iterable<Integer> asIterable() {
        return () -> stream().iterator();
    }
}
```

### シリアライゼーション対応

RecordはSerializableインターフェイスを実装することで、シリアライゼーションに対応できます。

<span class="listing-number">**サンプルコード9-25**</span>

```java
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public record SerializableUser(
    String id,
    String name,
    String email,
    LocalDateTime createdAt
) implements Serializable {
    
    // SerialVersionUIDの明示的定義（推奨）
    private static final long serialVersionUID = 1L;
    
    public SerializableUser {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(createdAt, "Created at cannot be null");
    }
    
    // バリデーションメソッド
    public boolean isValid() {
        return id != null && !id.isEmpty() &&
               name != null && !name.isEmpty() &&
               email != null && email.contains("@") &&
               createdAt != null;
    }
    
    // 文字列表現への変換（シリアライゼーションの代替）
    public String toSerializedString() {
        return String.format("%s|%s|%s|%s", id, name, email, createdAt);
    }
    
    // 文字列からの復元（デシリアライゼーションの代替）
    public static SerializableUser fromSerializedString(String serialized) {
        String[] parts = serialized.split("\\|");
        if (parts.length != 4) {
            return null;
        }
        return new SerializableUser(
            parts[0],
            parts[1],
            parts[2],
            LocalDateTime.parse(parts[3])
        );
    }
}
```

### JSON統合（概念的な例）

現代のアプリケーションでは、RecordとJSON処理の統合が大切です。RecordsはJSONライブラリと優れた互換性を持ち、データ交換フォーマットとして活用できます。以下は、RecordをJSON相当の形式で扱う概念的な例です。

<span class="listing-number">**サンプルコード9-26**</span>

```java
import java.time.Instant;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

// APIレスポンスのRecord
public record ApiResponse<T>(
    boolean success,
    T data,
    String errorMessage,
    Instant timestamp
) {
    // 成功レスポンスのファクトリ
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, Instant.now());
    }
    
    // エラーレスポンスのファクトリ
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message, Instant.now());
    }
    
    // 簡易的なJSON風の文字列表現
    public String toJsonString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"success\": ").append(success).append(", ");
        if (data != null) {
            sb.append("\"data\": \"").append(data).append("\", ");
        }
        if (errorMessage != null) {
            sb.append("\"error_message\": \"").append(errorMessage).append("\", ");
        }
        sb.append("\"timestamp\": \"").append(timestamp).append("\"}");
        return sb.toString();
    }
}

// 通貨を表すRecord
public record Money(BigDecimal amount, Currency currency) {
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (amount.scale() > currency.getDefaultFractionDigits()) {
            amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
        }
    }
    
    // Map形式への変換（JSONの代替）
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("amount", amount.doubleValue());
        map.put("currency", currency.getCurrencyCode());
        return map;
    }
    
    // Map形式からの生成（JSONパースの代替）
    public static Money fromMap(Map<String, Object> map) {
        Double amountValue = (Double) map.get("amount");
        String currencyCode = (String) map.get("currency");
        
        if (amountValue == null || currencyCode == null) {
            return null;
        }
        
        return new Money(
            BigDecimal.valueOf(amountValue),
            Currency.getInstance(currencyCode)
        );
    }
    
    // 文字列表現
    public String toSimpleString() {
        return amount + " " + currency.getCurrencyCode();
    }
}

// 使用例：
public class JsonIntegrationExample {
    public static void main(String[] args) {
        // Moneyオブジェクトの作成と変換
        Money money = new Money(BigDecimal.valueOf(1000.50), Currency.getInstance("JPY"));
        Map<String, Object> moneyMap = money.toMap();
        System.out.println("Money as map: " + moneyMap);
        
        // APIレスポンスの作成
        ApiResponse<Money> response = ApiResponse.success(money);
        System.out.println("Response: " + response.toJsonString());
    }
}
```

## 実践的なデータパイプライン

### イベントソーシング風データモデル

Recordsとパターンマッチングを組み合わせることで、イベントソーシングのような高度なデータ処理パターンを簡潔に実装できます。

<span class="listing-number">**サンプルコード9-27**</span>

```java
// イベントの定義
public sealed interface UserEvent permits UserCreated, UserUpdated, UserDeleted {}

public record UserCreated(
    String userId, 
    String name, 
    String email, 
    Instant timestamp
) implements UserEvent {}

public record UserUpdated(
    String userId, 
    Map<String, Object> changes, 
    Instant timestamp
) implements UserEvent {}

public record UserDeleted(
    String userId, 
    String reason, 
    Instant timestamp
) implements UserEvent {}

// ユーザーの状態スナップショット
public record UserSnapshot(
    String userId,
    String name,
    String email,
    boolean active,
    Instant lastModified
) {}
```

### イベントストリーム処理の実装

以下のUserEventProcessorクラスでは、イベントソーシングパターンを使用して、ユーザーに関するイベントのストリームから現在の状態（スナップショット）を再構築します。これは、監査ログや履歴管理が重要なシステムで使用される高度なパターンです。

<span class="listing-number">**サンプルコード9-28**</span>

```java
// イベントストリームの処理
public class UserEventProcessor {
    
    // イベントからスナップショットへの集約
    public static Map<String, UserSnapshot> processEvents(Stream<UserEvent> events) {
        return events.reduce(
            new HashMap<String, UserSnapshot>(),
            UserEventProcessor::applyEvent,
            (map1, map2) -> { map1.putAll(map2); return map1; }
        );
    }
    
    // 単一イベントの適用
    private static Map<String, UserSnapshot> applyEvent(
            Map<String, UserSnapshot> snapshots, 
            UserEvent event) {
        
        var updated = new HashMap<>(snapshots);
        
        switch (event) {
            case UserCreated(var id, var name, var email, var time) -> {
                updated.put(id, new UserSnapshot(id, name, email, true, time));
            }
            
            case UserUpdated(var id, var changes, var time) -> {
                var current = updated.get(id);
                if (current != null) {
                    var newName = (String) changes.getOrDefault("name", current.name());
                    var newEmail = (String) changes.getOrDefault("email", current.email());
                    updated.put(id, new UserSnapshot(id, newName, newEmail, current.active(), time));
                }
            }
            
            case UserDeleted(var id, var reason, var time) -> {
                var current = updated.get(id);
                if (current != null) {
                    updated.put(id, new UserSnapshot(
                        id, current.name(), current.email(), false, time));
                }
            }
        }
        
        return updated;
    }
    
    // 特定時点での状態復元
    public static Map<String, UserSnapshot> replayEventsUntil(
            Stream<UserEvent> events, 
            Instant cutoff) {
        return events
            .filter(event -> getEventTimestamp(event).isBefore(cutoff))
            .reduce(
                new HashMap<String, UserSnapshot>(),
                UserEventProcessor::applyEvent,
                (map1, map2) -> { map1.putAll(map2); return map1; }
            );
    }
    
    private static Instant getEventTimestamp(UserEvent event) {
        return switch (event) {
            case UserCreated(_, _, _, var timestamp) -> timestamp;
            case UserUpdated(_, _, var timestamp) -> timestamp;
            case UserDeleted(_, _, var timestamp) -> timestamp;
        };
    }
}
```

### Stream APIとの高度な統合

RecordsとStream APIを組み合わせることで、複雑なデータ処理パイプラインを関数型スタイルで実装できます。以下の例では、イベントストリームから分析用のデータを生成する実践的なパイプラインを示します。このパターンは、リアルタイム分析やレポート生成システムで広く使用されています。

<span class="listing-number">**サンプルコード9-29**</span>

```java
public class DataPipelineExample {
    
    // 複雑なデータ変換パイプライン
    public static List<UserAnalytics> generateUserAnalytics(
            Stream<UserEvent> events,
            Duration timeWindow) {
        
        Instant now = Instant.now();
        Instant windowStart = now.minus(timeWindow);
        
        return events
            .filter(event -> getEventTimestamp(event).isAfter(windowStart))
            .collect(Collectors.groupingBy(
                event -> switch (event) {
                    case UserCreated(var id, _, _, _) -> id;
                    case UserUpdated(var id, _, _) -> id;
                    case UserDeleted(var id, _, _) -> id;
                }
            ))
            .entrySet()
            .stream()
            .map(entry -> analyzeUserEvents(entry.getKey(), entry.getValue()))
            .filter(analytics -> analytics.eventCount() > 0)
            .sorted(Comparator.comparing(UserAnalytics::lastActivity).reversed())
            .collect(Collectors.toList());
    }
    
    private static UserAnalytics analyzeUserEvents(String userId, List<UserEvent> events) {
        int eventCount = events.size();
        Instant lastActivity = events.stream()
            .map(DataPipelineExample::getEventTimestamp)
            .max(Instant::compareTo)
            .orElse(Instant.EPOCH);
        
        long creationEvents = events.stream()
            .filter(event -> event instanceof UserCreated)
            .count();
        
        long updateEvents = events.stream()
            .filter(event -> event instanceof UserUpdated)
            .count();
        
        long deletionEvents = events.stream()
            .filter(event -> event instanceof UserDeleted)
            .count();
        
        return new UserAnalytics(
            userId,
            eventCount,
            lastActivity,
            creationEvents,
            updateEvents,
            deletionEvents
        );
    }
    
    private static Instant getEventTimestamp(UserEvent event) {
        return switch (event) {
            case UserCreated(_, _, _, var timestamp) -> timestamp;
            case UserUpdated(_, _, var timestamp) -> timestamp;
            case UserDeleted(_, _, var timestamp) -> timestamp;
        };
    }
}

public record UserAnalytics(
    String userId,
    int eventCount,
    Instant lastActivity,
    long creationEvents,
    long updateEvents,
    long deletionEvents
) {}
```

### 実際のアプリケーション例：注文処理システム

実際のビジネスアプリケーションでRecordsとDOPを活用する例として、ECサイトの注文処理システムを実装します。
この例では、注文、顧客、商品といったドメインモデルをRecordsで表現し、ビジネスロジックを純粋関数として実装します。
また、sealed interfaceを使用して注文の状態遷移を型安全に管理します。

<span class="listing-number">**サンプルコード9-30**</span>

```java
// 注文処理のドメインモデル
public record Customer(String id, String name, String email) {}

public record Product(String id, String name, Money price) {}

public record OrderItem(Product product, int quantity) {
    public Money totalPrice() {
        return product.price().multiply(BigDecimal.valueOf(quantity));
    }
}

public record Order(
    String orderId,
    Customer customer,
    List<OrderItem> items,
    OrderStatus status,
    LocalDateTime createdAt
) {
    public Money totalAmount() {
        return items.stream()
            .map(OrderItem::totalPrice)
            .reduce(new Money(BigDecimal.ZERO, Currency.getInstance("JPY")), Money::add);
    }
    
    public int totalItemCount() {
        return items.stream().mapToInt(OrderItem::quantity).sum();
    }
    
    public boolean hasProduct(String productId) {
        return items.stream()
            .anyMatch(item -> item.product().id().equals(productId));
    }
}

// 注文状態の管理
public sealed interface OrderStatus permits Pending, Confirmed, Shipped, Delivered, Cancelled {}
public record Pending() implements OrderStatus {}
public record Confirmed(LocalDateTime confirmedAt, String paymentId) implements OrderStatus {}
public record Shipped(LocalDateTime shippedAt, String trackingNumber) implements OrderStatus {}
public record Delivered(LocalDateTime deliveredAt, String signature) implements OrderStatus {}
public record Cancelled(LocalDateTime cancelledAt, String reason) implements OrderStatus {}

// ビジネスロジックの実装：
public class OrderService {
    
    public static String getOrderStatusMessage(Order order) {
        return switch (order.status()) {
            case Pending() -> "ご注文を受け付けました";
            case Confirmed(var confirmedAt, var paymentId) -> 
                "お支払いが確認できました（決済ID: " + paymentId + "）";
            case Shipped(var shippedAt, var trackingNumber) -> 
                "商品を発送しました（追跡番号: " + trackingNumber + "）";
            case Delivered(var deliveredAt, var signature) -> 
                "配達が完了しました（受領者: " + signature + "）";
            case Cancelled(var cancelledAt, var reason) -> 
                "注文がキャンセルされました（理由: " + reason + "）";
        };
    }
    
    public static boolean canBeCancelled(Order order) {
        return switch (order.status()) {
            case Pending(), Confirmed(_, _) -> true;
            case Shipped(_, _), Delivered(_, _), Cancelled(_, _) -> false;
        };
    }
    
    public static Order confirmOrder(Order order, String paymentId) {
        if (!(order.status() instanceof Pending)) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        
        return new Order(
            order.orderId(),
            order.customer(),
            order.items(),
            new Confirmed(LocalDateTime.now(), paymentId),
            order.createdAt()
        );
    }
    
    public static Order shipOrder(Order order, String trackingNumber) {
        if (!(order.status() instanceof Confirmed)) {
            throw new IllegalStateException("Only confirmed orders can be shipped");
        }
        
        return new Order(
            order.orderId(),
            order.customer(),
            order.items(),
            new Shipped(LocalDateTime.now(), trackingNumber),
            order.createdAt()
        );
    }
    
    // 注文レポートの生成
    public static OrderReport generateReport(List<Order> orders, LocalDate date) {
        var ordersOnDate = orders.stream()
            .filter(order -> order.createdAt().toLocalDate().equals(date))
            .collect(Collectors.toList());
        
        int totalOrders = ordersOnDate.size();
        Money totalRevenue = ordersOnDate.stream()
            .filter(order -> !(order.status() instanceof Cancelled))
            .map(Order::totalAmount)
            .reduce(new Money(BigDecimal.ZERO, Currency.getInstance("JPY")), Money::add);
        
        Map<String, Long> statusCounts = ordersOnDate.stream()
            .collect(Collectors.groupingBy(
                order -> order.status().getClass().getSimpleName(),
                Collectors.counting()
            ));
        
        return new OrderReport(date, totalOrders, totalRevenue, statusCounts);
    }
}

public record OrderReport(
    LocalDate date,
    int totalOrders,
    Money totalRevenue,
    Map<String, Long> statusCounts
) {}
```

### 関数型プログラミングとの統合

RecordsはJavaの関数型プログラミング機能と優れた相性を持ちます。
不変性、パターンマッチング、Stream APIとの統合により、宣言的で読みやすいコードを書くことができます。
以下の例では、注文データの分析処理を関数型スタイルで実装します。

<span class="listing-number">**サンプルコード9-31**</span>

```java
// 関数型スタイルでのデータ処理
public class FunctionalDataProcessing {
    
    // 注文データの関数型処理
    public static Optional<Order> findHighestValueOrder(Stream<Order> orders) {
        return orders
            .filter(order -> !(order.status() instanceof Cancelled))
            .max(Comparator.comparing(Order::totalAmount, 
                Comparator.comparing(Money::amount)));
    }
    
    // 顧客別注文サマリーの生成
    public static Map<Customer, CustomerSummary> generateCustomerSummaries(Stream<Order> orders) {
        return orders
            .filter(order -> !(order.status() instanceof Cancelled))
            .collect(Collectors.groupingBy(
                Order::customer,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    FunctionalDataProcessing::createCustomerSummary
                )
            ));
    }
    
    private static CustomerSummary createCustomerSummary(List<Order> orders) {
        int orderCount = orders.size();
        Money totalSpent = orders.stream()
            .map(Order::totalAmount)
            .reduce(new Money(BigDecimal.ZERO, Currency.getInstance("JPY")), Money::add);
        
        LocalDateTime lastOrderDate = orders.stream()
            .map(Order::createdAt)
            .max(LocalDateTime::compareTo)
            .orElse(LocalDateTime.MIN);
        
        Money averageOrderValue = orderCount > 0 
            ? new Money(totalSpent.amount().divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP), totalSpent.currency())
            : new Money(BigDecimal.ZERO, Currency.getInstance("JPY"));
        
        return new CustomerSummary(orderCount, totalSpent, lastOrderDate, averageOrderValue);
    }
    
    // 商品別売上分析
    public static List<ProductSales> analyzeProductSales(Stream<Order> orders) {
        return orders
            .filter(order -> !(order.status() instanceof Cancelled))
            .flatMap(order -> order.items().stream())
            .collect(Collectors.groupingBy(
                OrderItem::product,
                Collectors.summingInt(OrderItem::quantity)
            ))
            .entrySet()
            .stream()
            .map(entry -> new ProductSales(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(ProductSales::quantitySold).reversed())
            .collect(Collectors.toList());
    }
}

public record CustomerSummary(
    int orderCount,
    Money totalSpent,
    LocalDateTime lastOrderDate,
    Money averageOrderValue
) {}

public record ProductSales(Product product, int quantitySold) {}
```

## パフォーマンスとメモリ効率

### Recordの最適化特性

Recordは、その設計により多くのパフォーマンス上の利点を提供します。JVMレベルでの最適化と、開発者による設計の工夫により、高効率なアプリケーションを構築できます。

#### JVMによる最適化

JVMは、Recordsの不変性と値ベースの性質を利用して、様々な最適化を行います。
以下の例では、JVMが最適化しやすいRecordの設計パターンを示します。
小さく不変なRecordは、将来のProject ValhallaのValue Typesの候補となり、さらなるパフォーマンス向上が期待できます。

<span class="listing-number">**サンプルコード9-32**</span>

```java
// インライン化されやすいRecord
@jdk.internal.ValueBased  // 将来のValue Typeの候補
public record Point2D(double x, double y) {
    // 小さく、不変で、equalsとhashCodeが値ベース
    
    public double distanceFrom(Point2D other) {
        double dx = x - other.x;
        double dy = y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    public Point2D translate(double deltaX, double deltaY) {
        return new Point2D(x + deltaX, y + deltaY);
    }
}

// メモリレイアウトの最適化
public record OptimizedData(
    // プリミティブ型を前に配置（メモリ効率向上）
    long id,
    int count,
    boolean active,
    byte flags,
    // 参照型は後に配置
    String name,
    List<String> tags
) {}
```

#### ガベージコレクション効率

Recordsの不変性は、ガベージコレクションの性能を向上させます。不変オブジェクトは世代別GCでYoung世代に留まりやすく、素早く回収されます。以下の例では、短命なオブジェクトとしてRecordを活用する実践的なパターンを示します。

<span class="listing-number">**サンプルコード9-33**</span>

```java
// 短命なオブジェクトとしてのRecord活用
public class CalculationService {
    
    public record CalculationResult(double value, boolean isValid, String message) {}
    
    public CalculationResult calculate(double input) {
        if (input < 0) {
            return new CalculationResult(0.0, false, "Negative input not allowed");
        }
        
        double result = Math.sqrt(input) * 2.5;
        return new CalculationResult(result, true, "Success");
    }
    
    // 大量のRecord作成でもGC効率が良い
    public List<CalculationResult> processBatch(List<Double> inputs) {
        return inputs.stream()
            .map(this::calculate)
            .collect(Collectors.toList());
    }
}
```

### ベンチマーク実例

Recordsと従来のJavaクラスのパフォーマンスを比較するため、JMH（Java Microbenchmark Harness）を使用したベンチマークを実施します。
以下の例では、オブジェクトの作成、equals、hashCodeメソッドの実行速度を測定します。
Recordsが従来のクラスと同等以上のパフォーマンスを持つことを示します。

<span class="listing-number">**サンプルコード9-34**</span>

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
public class RecordBenchmark {
    
    // 従来のクラス
    static class TraditionalPoint {
        private final int x, y;
        
        TraditionalPoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TraditionalPoint that = (TraditionalPoint) o;
            return x == that.x && y == that.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
    
    // Record版
    record RecordPoint(int x, int y) {}
    
    @Benchmark
    public TraditionalPoint createTraditional() {
        return new TraditionalPoint(42, 100);
    }
    
    @Benchmark
    public RecordPoint createRecord() {
        return new RecordPoint(42, 100);
    }
    
    @Benchmark
    public boolean equalsTraditional() {
        var p1 = new TraditionalPoint(42, 100);
        var p2 = new TraditionalPoint(42, 100);
        return p1.equals(p2);
    }
    
    @Benchmark
    public boolean equalsRecord() {
        var p1 = new RecordPoint(42, 100);
        var p2 = new RecordPoint(42, 100);
        return p1.equals(p2);
    }
    
    @Benchmark
    public int hashCodeTraditional() {
        var p = new TraditionalPoint(42, 100);
        return p.hashCode();
    }
    
    @Benchmark
    public int hashCodeRecord() {
        var p = new RecordPoint(42, 100);
        return p.hashCode();
    }
}
```

##### 典型的なベンチマーク結果:
```
Benchmark                               Mode  Cnt   Score   Error  Units
RecordBenchmark.createTraditional       avgt   10   3.421 ± 0.089  ns/op
RecordBenchmark.createRecord            avgt   10   3.156 ± 0.076  ns/op
RecordBenchmark.equalsTraditional       avgt   10   2.843 ± 0.054  ns/op  
RecordBenchmark.equalsRecord            avgt   10   2.621 ± 0.041  ns/op
RecordBenchmark.hashCodeTraditional     avgt   10   3.128 ± 0.067  ns/op
RecordBenchmark.hashCodeRecord          avgt   10   2.934 ± 0.052  ns/op
```

### メモリ使用量の最適化

<span class="listing-number">**サンプルコード9-35**</span>

```java
public class MemoryOptimization {
    
    // メモリ効率を考慮したRecord設計
    public record CompactUser(
        // 必要最小限のフィールド
        String id,           // 8 bytes (reference)
        short age,           // 2 bytes (short instead of int)
        byte status          // 1 byte (enum ordinalを使用)
    ) {
        public CompactUser {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("Invalid age");
            }
        }
        
        public UserStatus getStatus() {
            return UserStatus.values()[status];
        }
        
        public static CompactUser of(String id, int age, UserStatus status) {
            return new CompactUser(id, (short) age, (byte) status.ordinal());
        }
    }
    
    enum UserStatus { ACTIVE, INACTIVE, SUSPENDED }
    
    // 大量データでのメモリ効率測定
    public static void memoryUsageComparison() {
        int count = 1_000_000;
        
        // メモリ使用量測定用
        Runtime runtime = Runtime.getRuntime();
        
        // 従来のクラス
        runtime.gc();
        long beforeTraditional = runtime.totalMemory() - runtime.freeMemory();
        
        List<TraditionalUser> traditionalUsers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            traditionalUsers.add(new TraditionalUser("user" + i, 25, UserStatus.ACTIVE));
        }
        
        long afterTraditional = runtime.totalMemory() - runtime.freeMemory();
        long traditionalMemory = afterTraditional - beforeTraditional;
        
        // Record版
        runtime.gc();
        long beforeRecord = runtime.totalMemory() - runtime.freeMemory();
        
        List<CompactUser> recordUsers = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            recordUsers.add(CompactUser.of("user" + i, 25, UserStatus.ACTIVE));
        }
        
        long afterRecord = runtime.totalMemory() - runtime.freeMemory();
        long recordMemory = afterRecord - beforeRecord;
        
        System.out.printf("Traditional: %d MB%n", traditionalMemory / (1024 * 1024));
        System.out.printf("Record: %d MB%n", recordMemory / (1024 * 1024));
        System.out.printf("Memory reduction: %.1f%%%n", 
            (1.0 - (double) recordMemory / traditionalMemory) * 100);
    }
    
    static class TraditionalUser {
        private final String id;
        private final int age;
        private final UserStatus status;
        
        TraditionalUser(String id, int age, UserStatus status) {
            this.id = id;
            this.age = age;
            this.status = status;
        }
        
        // getter、equals、hashCodeなど
    }
}
```

### オブジェクトプーリング戦略

<span class="listing-number">**サンプルコード9-36**</span>

```java
// 高頻度で作成されるRecordのプーリング
public class RecordPooling {
    
    public record Coordinate(int x, int y) {}
    
    static class CoordinatePool {
        private static final int POOL_SIZE = 1000;
        private static final Map<Long, Coordinate> POOL = new ConcurrentHashMap<>();
        
        public static Coordinate get(int x, int y) {
            long key = ((long) x << 32) | (y & 0xFFFFFFFFL);
            return POOL.computeIfAbsent(key, k -> new Coordinate(x, y));
        }
        
        // よく使われる座標の事前作成
        static {
            for (int x = -100; x <= 100; x++) {
                for (int y = -100; y <= 100; y++) {
                    get(x, y);
                }
            }
        }
    }
    
    // 使用例：
    public void gameLoop() {
        // プールから取得（新規作成なし）
        Coordinate playerPos = CoordinatePool.get(10, 20);
        Coordinate enemyPos = CoordinatePool.get(-5, 15);
        
        // ゲームロジック...
    }
}
```

### 大量データ処理での最適化

<span class="listing-number">**サンプルコード9-37**</span>

```java
public class BigDataOptimization {
    
    // 大量のRecordを高速に処理
    public record LogEntry(
        Instant timestamp,
        String level,
        String message,
        String source
    ) {}
    
    // メモリ効率を重視したストリーム処理
    public static Map<String, Long> analyzeLogsByLevel(Stream<String> logLines) {
        return logLines
            .parallel() // 並列処理
            .map(BigDataOptimization::parseLogEntry)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingByConcurrent(
                LogEntry::level,
                Collectors.counting()
            ));
    }
    
    private static LogEntry parseLogEntry(String line) {
        try {
            String[] parts = line.split("\\|", 4);
            if (parts.length != 4) return null;
            
            return new LogEntry(
                Instant.parse(parts[0]),
                parts[1].intern(), // 文字列インターン化でメモリ削減
                parts[2],
                parts[3].intern()
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    // バッチ処理でのメモリ効率
    public static void processBatchData(List<LogEntry> entries) {
        // チャンクサイズを調整してメモリ使用量制御
        int chunkSize = 10_000;
        
        for (int i = 0; i < entries.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, entries.size());
            List<LogEntry> chunk = entries.subList(i, end);
            
            // チャンクごとに処理
            processChunk(chunk);
            
            // ガベージコレクションのヒント
            if (i % (chunkSize * 10) == 0) {
                System.gc();
            }
        }
    }
    
    private static void processChunk(List<LogEntry> chunk) {
        // 実際の処理...
    }
}
```

## 将来の発展

### Project Valhallaとの統合

Project Valhallaは、JavaにValue Typesを導入するプロジェクトで、Recordsの未来に大きな影響を与えます

<span class="listing-number">**サンプルコード9-38**</span>

```java
// 将来のValue Records（概念的な例）
public value record ComplexNumber(double real, double imaginary) {
    // Value Typeとして最適化される
    // - ヒープではなくスタックに配置可能
    // - 配列がフラットに格納される
    // - オブジェクトヘッダーのオーバーヘッド除去
    
    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(
            real + other.real,
            imaginary + other.imaginary
        );
    }
    
    public ComplexNumber multiply(ComplexNumber other) {
        return new ComplexNumber(
            real * other.real - imaginary * other.imaginary,
            real * other.imaginary + imaginary * other.real
        );
    }
    
    public double magnitude() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }
}

// Inline Classesとの統合（将来の機能）
public inline record Vec3(float x, float y, float z) {
    // 完全にスタック割り当て
    // 配列がcontiguousに配置される
    // C/C++並みのパフォーマンス
    
    public Vec3 add(Vec3 other) {
        return new Vec3(x + other.x, y + other.y, z + other.z);
    }
    
    public float dot(Vec3 other) {
        return x * other.x + y * other.y + z * other.z;
    }
}
```

### パターンマッチングの進化

<span class="listing-number">**サンプルコード9-39**</span>

```java
// 将来のパターンマッチング拡張（概念的）
public class FuturePatternMatching {
    
    public record Point(int x, int y) {}
    public record Circle(Point center, int radius) {}
    
    // Array Patternsの統合
    public static String analyzePoints(Point[] points) {
        return switch (points) {
            case [] -> "空の配列";
            case [var single] -> "単一点: " + single;
            case [var first, var second] -> "2点: " + first + ", " + second;
            case [var first, var... rest] -> "複数点（先頭: " + first + "）";
        };
    }
    
    // String Patternsとの統合
    public static void processCommand(String command) {
        switch (command) {
            case "move ${int x} ${int y}" -> move(x, y);
            case "rotate ${float angle}" -> rotate(angle);
            case "scale ${float factor}" -> scale(factor);
            default -> System.out.println("Unknown command");
        }
    }
    
    private static void move(int x, int y) { /* 実装 */ }
    private static void rotate(float angle) { /* 実装 */ }
    private static void scale(float factor) { /* 実装 */ }
}
```

### 型システムの進化

<span class="listing-number">**サンプルコード9-40**</span>

```java
// 将来の型システム拡張
public class TypeSystemEvolution {
    
    // Union Typesとの統合（概念的）
    public sealed interface Result<T, E> permits Success, Error {}
    public record Success<T, E>(T value) implements Result<T, E> {}
    public record Error<T, E>(E error) implements Result<T, E> {}
    
    // Generic Records の拡張
    public record Pair<T, U>(T first, U second) {
        // より高度な制約
        public <R> Pair<R, U> mapFirst(Function<T, R> mapper) {
            return new Pair<>(mapper.apply(first), second);
        }
    }
    
    // Dependent Typesとの統合（非常に将来的）
    public record Matrix<N extends Number>(N rows, N cols, double[][] data) {
        public Matrix {
            if (data.length != rows.intValue() || 
                data[0].length != cols.intValue()) {
                throw new IllegalArgumentException("Dimension mismatch");
            }
        }
    }
}
```

### 相互運用性の向上

<span class="listing-number">**サンプルコード9-41**</span>

```java
// 他言語・フレームワークとの統合改善
public class InteroperabilityEnhancements {
    
    // Native interopの改善
    @Foreign
    public record NativePoint(int x, int y) {
        // C/C++構造体との直接マッピング
    }
    
    // JSON Schemaとの自動統合
    @JsonSchema
    public record ApiRequest(
        @NotNull String operation,
        @Valid RequestData data,
        @Pattern(regexp = "v\\d+") String version
    ) {}
    
    // データベースマッピングの改善
    @Entity
    public record UserEntity(
        @Id String id,
        @Column("user_name") String name,
        @Column("created_at") Instant createdAt
    ) {}
}
```

## まとめ

本章では、Recordとデータ指向プログラミング（DOP）について包括的に学びました：

### Recordsの革新性

Recordは、Java 16で正式導入された画期的な機能で、データ保持に特化したクラスを極めて簡潔に定義できます

- ボイラープレートコードの劇的削減
    + 従来100行必要だったコードを1行で実現
- 自動生成される機能
    + コンストラクタ、アクセサ、equals、hashCode、toStringが自動実装
- 不変性の保証
    + デフォルトでimmutableなオブジェクトを生成
- 型安全性
    + コンパイル時の型チェックによる堅牢性

### データ指向プログラミングのパラダイム

DOP（Data-Oriented Programming）は、従来のOOPとは異なる新しいアプローチです

- データとロジックの分離
    + Recordでデータ、純粋関数でロジックを表現
- 表現力の向上
    + パターンマッチングによる簡潔な条件分岐
- 実用的な効果
    + 開発時間50%短縮、バグ率95%削減の実績

### 高度な機能との統合

### 現代的なJava開発の中核技術として以下の統合を実現

- sealed interfaceとパターンマッチング：代数的データ型による型安全な設計
- Stream APIとの連携：関数型プログラミングとの親和性
- JSONシリアライゼーション：REST API開発での活用
- イベントソーシング：高度なデータ処理パターンの実装

### パフォーマンスと将来性

##### 技術的優位性:
- JVMレベルでの最適化によるパフォーマンス向上
- メモリ効率の改善とガベージコレクションの最適化
- 将来のProject Valhalla（Value Types）との統合

### 実践的価値

Recordとデータ指向プログラミングは、単なる構文糖ではなく、Javaプログラミングの新しいパラダイムを可能にする重要な技術です。特に以下の領域で威力を発揮します。

- API開発：型安全で簡潔なデータ転送
- ドメインモデリング：ビジネスロジックの明確な表現
- 関数型プログラミング：不変データによる安全な処理
- 大規模システム：保守性と拡張性の向上

これらの技術は、現代のJavaアプリケーション開発において重要なスキルとなっており、保守性が高く安全なソフトウェアの構築に大きく貢献します。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter09/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

1. 基礎課題
    + 基本的なRecord定義と自動生成メソッドの理解
2. 発展課題
    + RecordをDTOとして活用した実践的な実装
3. チャレンジ課題
    + 複雑なRecord構造とパターンマッチングの応用

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第10章「コレクションフレームワーク」に進みましょう。

## 完了確認チェックリスト

### 基礎レベル
- 基本的なRecordの定義と特性が理解できている
- RecordをAPI間でのデータ交換用DTOとして活用できている
- 複雑なRecord構造と設定管理ができている
- パターンマッチングと組み合わせて使えている

### 技術要素
- Recordの利点と制限を理解している
- 従来のクラスとの使い分けができている
- 不変性を保った設計ができている
- コンパクトコンストラクタでバリデーションができている

### 応用レベル
- シリアライゼーションやJSON連携ができている
- パフォーマンスを考慮した設計ができている
- フレームワークやライブラリとの統合ができている
- レガシーコードからRecordへのマイグレーションができている

## よくあるエラーと対処法

Recordを学習する際に遭遇する典型的なエラーとその対処法を示します。

### レコードの不変性に関する誤解

#### エラー例1: レコードのフィールドを変更しようとする

<span class="listing-number">**サンプルコード9-42**</span>

```java
public record Person(String name, int age) {}

// 間違った使用
Person person = new Person("田中", 25);
person.age = 30;  // コンパイルエラー
```

##### エラーメッセージ:
```
error: cannot assign a value to final variable age
```

##### 対処法:

<span class="listing-number">**サンプルコード9-43**</span>

```java
// 正しい使用：新しいインスタンスを作成
Person person = new Person("田中", 25);
Person olderPerson = new Person(person.name(), 30);

// または、withメソッドを追加
public record Person(String name, int age) {
    public Person withAge(int newAge) {
        return new Person(this.name, newAge);
    }
    
    public Person withName(String newName) {
        return new Person(newName, this.age);
    }
}

// 使用例：
Person person = new Person("田中", 25);
Person olderPerson = person.withAge(30);
```

#### エラー例2: ミュータブルなオブジェクトを含むレコードで不変性を破る

<span class="listing-number">**サンプルコード9-44**</span>

```java
import java.util.List;

public record Student(String name, List<String> subjects) {}

// 問題のあるコード
Student student = new Student("山田", List.of("数学", "英語"));
student.subjects().add("科学");  // 実行時エラー（UnsupportedOperationException）
```

##### エラーメッセージ:
```
Exception in thread "main" java.lang.UnsupportedOperationException
    at java.base/java.util.ImmutableCollections.uoe(ImmutableCollections.java:142)
```

##### 対処法:

<span class="listing-number">**サンプルコード9-45**</span>

```java
import java.util.List;
import java.util.ArrayList;

public record Student(String name, List<String> subjects) {
    // コンパクトコンストラクタで防御的コピーを作成
    public Student {
        subjects = List.copyOf(subjects);  // 不変リストを作成
    }
    
    // 科目を追加した新しいインスタンスを返すメソッド
    public Student addSubject(String subject) {
        List<String> newSubjects = new ArrayList<>(subjects);
        newSubjects.add(subject);
        return new Student(name, newSubjects);
    }
}

// 使用例：
Student student = new Student("山田", List.of("数学", "英語"));
Student updatedStudent = student.addSubject("科学");
```

### レコードのコンストラクタ

#### エラー例3: レコードに通常のコンストラクタを追加しようとする

<span class="listing-number">**サンプルコード9-46**</span>

```java
public record Point(int x, int y) {
    // 間違った使用
    public Point() {  // コンパイルエラー
        // 処理
    }
}
```

##### エラーメッセージ:
```
error: non-canonical constructor must delegate to another constructor
```

##### 対処法:

<span class="listing-number">**サンプルコード9-47**</span>

```java
public record Point(int x, int y) {
    // 正しい使用：非カノニカルコンストラクタはカノニカルコンストラクタに委譲
    public Point() {
        this(0, 0);  // デフォルト値でカノニカルコンストラクタを呼び出し
    }
    
    public Point(int value) {
        this(value, value);  // 正方形のポイントを作成
    }
}
```

#### エラー例4: コンパクトコンストラクタでフィールドに代入

<span class="listing-number">**サンプルコード9-48**</span>

```java
public record Person(String name, int age) {
    // 間違った使用
    public Person {
        this.name = name.trim();  // コンパイルエラー
        this.age = Math.max(0, age);
    }
}
```

##### エラーメッセージ:
```
error: cannot assign a value to final variable name
```

##### 対処法:

<span class="listing-number">**サンプルコード9-49**</span>

```java
public record Person(String name, int age) {
    // 正しい使用：パラメータを再代入
    public Person {
        name = name.trim();  // thisを使わずにパラメータを再代入
        age = Math.max(0, age);
    }
}
```

### レコードの継承制限

#### エラー例5: レコードを継承しようとする

<span class="listing-number">**サンプルコード9-50**</span>

```java
public record BaseEntity(String id) {}

// 間違った使用
public record UserDTO(String id, String username, String email) extends BaseEntity {  // コンパイルエラー
    // 処理
}
```

##### エラーメッセージ:
```
error: classes cannot directly extend records
```

##### 対処法1: 組み合わせを使用

<span class="listing-number">**サンプルコード9-51**</span>

```java
public record EntityIdentifier(String id) {}

public record UserDTO(EntityIdentifier identifier, String username, String email) {
    public String id() {
        return identifier.id();
    }
}

// 使用例：
UserDTO user = new UserDTO(new EntityIdentifier("USR-001"), "john_doe", "john@example.com");
```

##### 対処法2: インターフェイスを使用

<span class="listing-number">**サンプルコード9-52**</span>

```java
public interface Identifiable {
    String id();
}

public record UserDTO(String id, String username, String email) implements Identifiable {}
public record OrderDTO(String id, String userId, LocalDateTime orderDate) implements Identifiable {}
```

#### エラー例6: レコードから継承しようとする

<span class="listing-number">**サンプルコード9-53**</span>

```java
public record BaseRecord(String value) {}

// 間違った使用
public class ExtendedClass extends BaseRecord {  // コンパイルエラー
    // 処理
}
```

##### エラーメッセージ:
```
error: cannot inherit from final class
```

##### 対処法:

<span class="listing-number">**サンプルコード9-54**</span>

```java
// レコードは継承できないため、組み合わせまたはインターフェイスを使用
public interface ValueHolder {
    String value();
}

public record BaseRecord(String value) implements ValueHolder {}

public class ExtendedClass implements ValueHolder {
    private final String value;
    
    public ExtendedClass(String value) {
        this.value = value;
    }
    
    @Override
    public String value() {
        return value;
    }
}
```

### equals()とhashCode()の自動実装

#### エラー例7: equals()をオーバーライドしようとする

<span class="listing-number">**サンプルコード9-55**</span>

```java
public record Person(String name, int age) {
    // 間違った使用
    @Override
    public boolean equals(Object obj) {  // コンパイルエラー
        // カスタムロジック
        return super.equals(obj);
    }
}
```

##### エラーメッセージ:
```
error: cannot override equals in record
```

##### 対処法:

<span class="listing-number">**サンプルコード9-56**</span>

```java
// レコードのequals()は自動生成される
public record Person(String name, int age) {
    // equals()をオーバーライドする必要はない
}

// カスタムな比較が必要な場合は、別のメソッドを作成
public record Person(String name, int age) {
    public boolean hasSameName(Person other) {
        return this.name.equals(other.name);
    }
}
```

### レコードとクラスの使い分け

#### エラー例8: レコードにミュータブルなメソッドを追加

<span class="listing-number">**サンプルコード9-57**</span>

```java
public record Counter(int value) {
    // 間違った設計。
    public void increment() {  // コンパイルエラー
        value++;
    }
}
```

##### エラーメッセージ:
```
error: cannot assign a value to final variable value
```

##### 対処法:

<span class="listing-number">**サンプルコード9-58**</span>

```java
// 解決法1: 新しいインスタンスを返す
public record Counter(int value) {
    public Counter increment() {
        return new Counter(value + 1);
    }
}

// 解決法2: ミュータブルな状態が必要な場合は通常のクラスを使用
public class MutableCounter {
    private int value;
    
    public MutableCounter(int value) {
        this.value = value;
    }
    
    public void increment() {
        value++;
    }
    
    public int getValue() {
        return value;
    }
}
```

### 実践的なデバッグのヒント

##### デバッグ時の確認ポイント:

1. null値の処理

<span class="listing-number">**サンプルコード9-59**</span>

```java
public record Person(String name, int age) {
    public Person {
        if (name == null) {
            throw new IllegalArgumentException("名前はnullにできません");
        }
        if (age < 0) {
            throw new IllegalArgumentException("年齢は0以上である必要があります");
        }
    }
}
```

2. ディープコピーが必要な場合

<span class="listing-number">**サンプルコード9-60**</span>

```java
import java.util.List;

public record StudentGrades(String name, List<Integer> grades) {
    public StudentGrades {
        grades = List.copyOf(grades);  // 防御的コピー
    }
    
    public StudentGrades addGrade(int grade) {
        List<Integer> newGrades = new ArrayList<>(grades);
        newGrades.add(grade);
        return new StudentGrades(name, newGrades);
    }
}
```

3. シリアライゼーション時の注意

<span class="listing-number">**サンプルコード9-61**</span>

```java
import java.io.Serializable;

public record SerializableData(String value, int number) implements Serializable {
    // レコードは自動的にシリアライズ可能
    // カスタムシリアライゼーションが必要な場合は通常のクラスを使用
}
```

4. パフォーマンスを考慮した設計

<span class="listing-number">**サンプルコード9-62**</span>

```java
// 大量のデータを扱う場合は注意
public record LargeData(String[] data) {
    public LargeData {
        data = data.clone();  // 配列のコピー
    }
}

// メモリとパフォーマンスを考慮した設計
public record OptimizedData(List<String> data) {
    public OptimizedData {
        data = List.copyOf(data);  // 浅いコピーで不変性を確保
    }
}
```

5. JSON連携時の注意

<span class="listing-number">**サンプルコード9-63**</span>

```java
// Jackson等のライブラリを使用する場合
public record ApiResponse(String status, String message, Object data) {
    // デフォルトコンストラクタが必要な場合
    public ApiResponse() {
        this("success", "", null);
    }
}
```

これらのエラーパターンを理解することで、Recordをより安全かつ効果的に使用できるようになります。Recordは不変性を重視した設計に適しており、ミュータブルな状態が必要な場合は通常のクラスを使用することを検討しましょう