<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 2/12
行範囲: 250 - 492
作成日時: 2025-08-02 14:34:01

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### 構造的問題の分析

- ①　データの散在
        + 学生に関する情報（名前、年齢、GPA）が独立した変数として存在し、これらが1つの概念（学生）を表すという関係性がコード上で明確でない
- ②　パラメータ渡しの煩雑さ
        + 関連するデータを処理するたびに、すべてのパラメータを個別に渡します
        + データの追加や変更時に多くの関数の引数リストを修正します
- ③　責任の分散
        + 学生データの検証、変更、表示など、学生に関する処理が複数の場所に散らばり、どこで何をしているかが把握しにくい

#### オブジェクト指向の例

オブジェクト指向では、関連するデータと処理を1つのクラスに統合します。
これにより、概念の一貫性と責任の明確化を実現します。

<span class="listing-number">**サンプルコード3-2**</span>

```java
public class Student {
    private String name;  // ①
    private int age;      // ①
    private double gpa;   // ①
    
    public Student(String name, int age, double gpa) {  // ②
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    public void printInfo() {  // ③
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}

public class ObjectOrientedExample {
    public static void main(String[] args) {
        Student student = new Student("田中太郎", 20, 3.5);  // ④
        student.printInfo();  // ⑤
    }
}
```

実行結果：
```
名前: 田中太郎, 年齢: 20, GPA: 3.5
```

#### オブジェクト指向の利点の実現

- ①　データのカプセル化
        + 学生に関するすべての属性がprivateフィールドとして一箇所に集約され、外部からの直接アクセスを制限する
- ②　初期化の保証
        + コンストラクタにより、オブジェクト生成時に必要なデータがすべて設定されることを保証する
- ③　振る舞いの局所化
        + 学生データの表示処理がStudentクラス内部に定義され、データとその操作の責任が明確になる
- ④　概念の一体性
        + `Student`オブジェクトとして学生という概念を直接的にプログラムで表現する
- ⑤　インターフェイスの簡潔性
        + 外部からは単純にメソッドを呼び出すだけで、内部の複雑さを隠蔽

### オブジェクト指向の3つの基本原則

#### カプセル化

##### データ保護と制御されたアクセスの実現

カプセル化は、オブジェクトの内部データを外部から直接アクセスできないように保護します。
入力値の検証やビジネスルールの適用を含むメソッドを通じてのみ操作を許可する仕組みです。

<span class="listing-number">**サンプルコード3-3**</span>

```java
public class BankAccount {
    private double balance;  // ①
    
    public void deposit(double amount) {  // ②
        if (amount > 0) {
            balance += amount;
        }
    }
    
    public double getBalance() {  // ③
        return balance;
    }
}
```

使用例と実行結果：
```java
// BankAccountTest.java
public class BankAccountTest {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();
        System.out.println("初期残高: " + account.getBalance() + "円");
        
        account.deposit(10000);
        System.out.println("10000円入金後: " + account.getBalance() + "円");
        
        account.deposit(-5000);  // 負の値は無視される
        System.out.println("負の値入金後: " + account.getBalance() + "円");
    }
}
```

実行結果：
```
初期残高: 0.0円
10000円入金後: 10000.0円
負の値入金後: 10000.0円
```

##### カプセル化の効果

- ①　データの隠蔽
        + `private`修飾子により残高データを直接操作できないようにし、不正な値の設定を防止
- ②　制御された変更
        + 入金処理では金額の妥当性を検証してから残高を更新し、負の値の入金を防ぐ
- ③　安全な読み取り
        + 残高の確認は可能だが、直接的な変更はできないため、データの整合性を保証

<div class="figure">

![カプセル化によるデータ保護とアクセス制御](images/diagrams/figure03-02.png)

</div>

#### 継承

##### コードの再利用と階層構造の構築

継承は、既存のクラス（親クラス）の機能を新しいクラス（子クラス）が引き継ぎ、さらに独自の機能を追加する仕組みです。

<span class="listing-number">**サンプルコード3-4**</span>

```java
public class Product {
    protected String productId;  // ① protectedにより同一パッケージまたはサブクラスからアクセス可能
    protected String name;       // ①
    protected int price;         // ①
    
    public void displayInfo() {  // ②
        System.out.println("商品名: " + name + ", 価格: " + price + "円");
    }
}

public class Book extends Product {  // ③
    private String author;     // ④
    private String isbn;       // ④
    
    public void displayBookInfo() {  // ⑤
        displayInfo();  // ②を呼び出し
        System.out.println("著者: " + author + ", ISBN: " + isbn);
    }
}
```

使用例と実行結果：
```java
// InheritanceTest.java
public class InheritanceTest {
    public static void main(String[] args) {
        Book book = new Book();
        book.productId = "B001";
        book.name = "Java入門";
        book.price = 2800;
        book.author = "田中太郎";
        book.isbn = "978-4-12345-678-9";
        
        book.displayBookInfo();
    }
}
```

実行結果：
```
商品名: Java入門, 価格: 2800円
著者: 田中太郎, ISBN: 978-4-12345-678-9
```

##### 継承による機能拡張

- ①　共通属性の継承
        + 商品ID、名前、価格など、すべての商品に共通する属性を親クラスで定義する
- ②　共通機能の継承
        + 基本的な商品情報の表示機能を親クラスで実装し、子クラスでも利用可能となる
- ③　is-a関係の表現
        + 「本は商品である」という概念的関係をextends文で明確に表現する
- ④　専用属性の追加
        + 本特有の情報（著者、ISBN）を子クラスで独自に定義
- ⑤　機能の組み合わせ
        + 親クラスの機能を活用しつつ、子クラス独自の機能を追加する

#### ポリモーフィズム

##### 同一インターフェイスによる多様な実装の統一的扱い

ポリモーフィズム（多態性）は、同じインターフェイスを通じて異なる実装を統一的に扱える仕組みです。
新しい実装を追加してもクライアントコードを変更する必要がありません。

<span class="listing-number">**サンプルコード3-5**</span>

```java
public interface PaymentMethod {  // ①
    void processPayment(double amount);
}

public class CreditCardPayment implements PaymentMethod {  // ②
    private String cardNumber;
    
    public void processPayment(double amount) {  // ③
        System.out.println("クレジットカードで" + amount + "円を決済しました");
    }
}

public class BankTransferPayment implements PaymentMethod {  // ②
    private String accountNumber;
    
    public void processPayment(double amount) {  // ④
        System.out.println("銀行振込で" + amount + "円を送金しました");
    }
}
```

使用例と実行結果：
```java
// PolymorphismTest.java
public class PolymorphismTest {
    public static void main(String[] args) {
        PaymentMethod[] payments = {
            new CreditCardPayment(),
            new BankTransferPayment()
        };
        
        double amount = 5000;
        for (PaymentMethod payment : payments) {
            payment.processPayment(amount);
        }
    }
}
```

実行結果：
```
クレジットカードで5000.0円を決済しました
銀行振込で5000.0円を送金しました
```



<!-- 
================
チャンク 2/12 の終了
校正ステータス: [ ] 未完了 / [x] 完了
================
-->
