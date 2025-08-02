<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 3/15
行範囲: 375 - 549
作成日時: 2025-08-02 22:58:07

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

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

ポリモーフィズム（多態性）は、同じインターフェイスを通じて異なる実装を統一的に扱える仕組みです。これにより、新しい実装を追加してもクライアントコードを変更する必要がありません。

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

##### ポリモーフィズムの仕組み

- ①　統一インターフェイス
    + すべての決済方法が実装すべき共通のメソッド仕様を定義
- ②　多様な実装
    + 同じインターフェイスに対して、具体的な決済手段ごとに異なる実装を提供する
- ③　クレジットカード固有処理
    + カード決済に特化した具体的な処理を実装
- ④　銀行振込の固有処理
    + 銀行振込に特化した具体的な処理を実装

この設計により、`PaymentMethod`型の変数で異なる決済方法を統一的に扱えるため、新しい決済方法（新しいカード会社の決済、決済サービス、仮想通貨決済など）を追加しても既存のコードに影響を与えません。




## クラスの作成

オブジェクト指向プログラミングでは、クラスという設計図を作成し、その設計図からオブジェクト（インスタンス）を生成します。

### クラスの基本構造

クラスは主に以下の要素で構成されます。
1. フィールド（属性）- オブジェクトが持つデータ
2. メソッド（操作）- オブジェクトができる処理
3. コンストラクタ - オブジェクトを初期化する特別なメソッド

これらの要素について、順に詳しく見ていきましょう。

### フィールド（属性）の基礎

フィールドは、オブジェクトが持つデータを表現します。各オブジェクトは自分専用のフィールドの値を持ちます。

<span class="listing-number">**サンプルコード3-6**</span>

```java
public class Student {
    // フィールドの宣言
    String name;           // 名前
    int age;              // 年齢
    double height;        // 身長（cm）
    boolean isActive;     // 在籍中かどうか
    
    // 初期値を持つフィールド
    String school = "東京大学";    // 学校名（初期値付き）
    int grade = 1;                // 学年（初期値: 1年生）
}
```

これはクラス構造の例です。

#### フィールドの重要なポイント
- フィールドは通常、クラスの最初に宣言する
- 各フィールドには型（String、int、doubleなど）が必要である
- 初期値を設定しない場合、デフォルト値が自動的に設定される
- フィールド名は意味がわかりやすい名前にする



<!-- 
================
チャンク 3/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
