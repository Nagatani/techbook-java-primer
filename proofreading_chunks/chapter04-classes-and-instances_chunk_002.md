<!-- 
校正チャンク情報
================
元ファイル: chapter04-classes-and-instances.md
チャンク: 2/10
行範囲: 222 - 426
作成日時: 2025-08-03 02:32:41

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->


#### 使用例と実行結果

```java
// VehicleTest.java
public class VehicleTest {
    public static void main(String[] args) {
        Car car = new Car();
        car.initialize();
        
        // protectedメンバーは別パッケージから直接アクセスできない
        // System.out.println(car.engineType); // コンパイルエラー
    }
}
```

#### 実行結果

```
Engine started: V6
```

##### `public`の使用例

<span class="listing-number">**サンプルコード4-5**</span>

```java
public class MathUtils {
    public static final double PI = 3.14159;  // 公開定数
    
    public static int add(int a, int b) {     // 公開ユーティリティメソッド
        return a + b;
    }
    
    public boolean isPositive(int number) {   // 公開インスタンスメソッド
        return number > 0;
    }
}

class MathUtilsTest {
    public static void main(String[] args) {
        // publicな定数へのアクセス
        System.out.println("PI = " + MathUtils.PI);
        
        // publicなstaticメソッドの呼び出し
        int sum = MathUtils.add(10, 20);
        System.out.println("10 + 20 = " + sum);
        
        // publicなインスタンスメソッドの呼び出し
        MathUtils utils = new MathUtils();
        System.out.println("5は正の数？ " + utils.isPositive(5));
        System.out.println("-3は正の数？ " + utils.isPositive(-3));
    }
}
```

#### 実行結果

```
PI = 3.14159
10 + 20 = 30
5は正の数？ true
-3は正の数？ false
```

### getter/setterメソッドのベストプラクティス

getter/setterメソッド（アクセサメソッドとも呼ばれます）は、カプセル化の実装で中心的な役割を果たします。
フィールドの値を取得・設定するだけでなく、データの整合性を保証し、将来の変更に対する柔軍性を提供します。
以下の例では、プライベートフィールドへの安全なアクセスを提供する標準パターンを示します。

<span class="listing-number">**サンプルコード4-6**</span>

```java
public class Product {
    private String name;
    private double price;
    
    // getter：値を取得
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    // setter：データ検証付きで値を設定
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("商品名は必須です");
        }
        this.name = name;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("価格は負の値にできません");
        }
        this.price = price;
    }
}

class ProductTest {
    public static void main(String[] args) {
        Product product = new Product();
        
        try {
            // 正常なデータ設定
            product.setName("ノートPC");
            product.setPrice(98000);
            System.out.println("商品名: " + product.getName());
            System.out.println("価格: " + product.getPrice() + "円");
            
            // 不正なデータの検証
            product.setName("");  // 例外が発生
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
        
        try {
            product.setPrice(-1000);  // 例外が発生
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
```

#### 実行結果

```
商品名: ノートPC
価格: 98000.0円
エラー: 商品名は必須です
エラー: 価格は負の値にできません
```

### データ検証の重要性

プログラミングで、データの状態を常に有効に保つことはきわめて重要です。オブジェクト指向では、これをカプセル化とsetterメソッドによる検証で実現します。setterメソッドは単なる値の代入ではなく、オブジェクトの不変条件（invariant）を守るゲートキーパーとしての役割を担います。範囲チェック、nullチェック、ビジネスルールの検証を実装すると、バグの早期発見と予防が可能になり、システム全体の信頼性が向上します。以下の例では、実務でよく使用される検証パターンを示します。

<span class="listing-number">**サンプルコード4-7**</span>

```java
public class Employee {
    private String name;
    private int age;
    private double salary;
    
    public void setAge(int age) {
        if (age < 18 || age > 100) {
            throw new IllegalArgumentException("年齢は18歳以上100歳以下で入力してください");
        }
        this.age = age;
    }
    
    public void setSalary(double salary) {
        if (salary < 0) {
            throw new IllegalArgumentException("給与は負の値にできません");
        }
        this.salary = salary;
    }
}
```

これはデータ検証パターンの例です。

## 設計原則とソフトウェアアーキテクチャ

ソフトウェア設計では、SOLID原則と呼ばれる5つの基本原則があります。これらはオブジェクト指向設計の文脈で生まれましたが、単一責任原則（モジュールは1つの責任のみを持つべき）や開放閉鎖原則（拡張に開かれ、修正に閉じているべき）などは、他のプログラミングパラダイムでも応用可能な普遍的な原則です。

カプセル化は単にデータを隠す技術ではなく、変更の影響を局所化し、再利用性とテスト容易性を高める重要な設計技術です。

## 実践的なクラス設計例

### 銀行口座クラスの段階的な改善

先ほどの問題のあるBankAccountV0を、カプセル化の原則にしたがって段階的に改善していきましょう。

#### ステップ1: 基本的なカプセル化（BankAccountV1）

まず、フィールドをprivateにして、publicメソッドを通じてのみアクセスできるようにします。

<span class="listing-number">**サンプルコード4-8**</span>

```java
public class BankAccountV1 {
    private String accountNumber;
    private double balance;
    
    public BankAccountV1(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        balance += amount;
    }
    
    public void withdraw(double amount) {
        balance -= amount;
    }
    
    public double getBalance() {
        return balance;
    }
}
```


<!-- 
================
チャンク 2/10 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
