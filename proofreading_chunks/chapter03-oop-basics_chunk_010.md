<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 10/15
行範囲: 1687 - 1882
作成日時: 2025-08-03 01:46:20

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### このプログラムから学ぶオブジェクト指向の重要な概念

1. オブジェクトの独立性
    + book1とbook2は同じBookクラスから作られているが、それぞれ独立したデータを持っている。一方のオブジェクトの状態を変更しても、他方には影響しません
2. new演算子の役割
    + `new Book(...)`は、メモリ上にBookオブジェクト用の領域を確保し、コンストラクタを呼び出してオブジェクトを初期化する。これにより、クラスという「型」から具体的な「実体」が作られる
3. メッセージパッシング
    + `book1.displayInfo()`のような記法は、book1オブジェクトに「自分の情報を表示して」というメッセージを送っていると解釈できる。オブジェクトは自分の責任範囲で処理を実行する
4. 状態の変更と永続性
    + `book1.setPrice(2500.0)`により、book1オブジェクトの内部状態が変更される。この変更は、そのオブジェクトが存在する限り維持される
5. 型安全性
    + BookTestクラスでは、Bookオブジェクトに対してBookクラスで定義されたメソッドのみ呼び出せるため、不正な操作を防げる

#### 実行時の動作理解
このプログラムを実行すると、メモリ上に2つの独立したBookオブジェクトが作成され、それぞれが異なる本の情報を保持します。
price変更後のbook1は新しい価格情報を保持し続け、オブジェクトの「状態を持つ」という特性を実感できます。

## publicとprivate - アクセス修飾子の基本

ここまでの例では、フィールドやメソッドの前に`public`や`private`というキーワードを使ってきました。これらは「アクセス修飾子」と呼ばれ、クラスのメンバー（フィールドやメソッド）へのアクセスを制御する重要な仕組みです。

### アクセス修飾子の基本的な使い分け

3章では、もっとも基本的な2つのアクセス修飾子について学習します。

1. public修飾子
    - どこからでもアクセス可能
    - 他のクラスから自由に使用できる
    - 主にメソッドで使用（クラスの機能を外部に公開）
2. private修飾子
    - 同じクラス内からのみアクセス可能
    - 外部から直接アクセスできない
    - 主にフィールドで使用（データを保護）

<span class="listing-number">**サンプルコード3-18**</span>

```java
public class Student {
    // privateフィールド - クラス内部でのみアクセス可能
    private String name;
    private int age;
    private double gpa;
    
    // publicコンストラクタ - 外部からオブジェクト作成可能
    public Student(String name, int age, double gpa) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    // publicメソッド - 外部から呼び出し可能
    public void introduce() {
        System.out.println("私は" + name + "です。" + age + "歳です。");
    }
    
    // privateメソッド - クラス内部でのみ使用
    private boolean isAdult() {
        return age >= 20;
    }
    
    // publicメソッドからprivateメソッドを呼び出す
    public void checkStatus() {
        if (isAdult()) {
            System.out.println("成人です");
        } else {
            System.out.println("未成年です");
        }
    }
}
```

#### 使用例と実行結果

```java
// StudentAccessTest.java
public class StudentAccessTest {
    public static void main(String[] args) {
        Student student = new Student("山田太郎", 22, 3.8);
        
        // publicメソッドは呼び出せる
        student.introduce();
        student.checkStatus();
        
        // privateフィールドには直接アクセスできない
        // System.out.println(student.name); // コンパイルエラー
        
        // privateメソッドも直接呼び出せない
        // student.isAdult(); // コンパイルエラー
    }
}
```

#### 実行結果

```
私は山田太郎です。22歳です。
成人です
```

### なぜprivateを使うのか？

フィールドをprivateにする理由を具体例で見てみましょう。

<span class="listing-number">**サンプルコード3-19**</span>

```java
public class Product {
    // publicフィールドの問題点
    public double price;     // 誰でも直接変更できる
    public int stock;        // 在庫数も自由に変更可能
}

// 使用例（問題のある使い方）
public class BadExample {
    public static void main(String[] args) {
        Product product = new Product();
        
        // 問題1: 負の価格を設定できてしまう
        product.price = -100.0;
        
        // 問題2: 在庫数を不正に操作できる
        product.stock = -50;
        
        // 問題3: データの整合性が保てない
        product.stock = 1000000;  // 現実的でない在庫数
    }
}
```

これはpublicフィールドの問題点を示す例です。

#### privateを使った改善例

<span class="listing-number">**サンプルコード3-36**</span>

```java
public class Product {
    // privateフィールド - 直接アクセスできない
    private double price;
    private int stock;
    
    // コンストラクタで初期値を設定
    public Product(double price, int stock) {
        // 初期値の検証も可能
        if (price < 0) {
            this.price = 0;
        } else {
            this.price = price;
        }
        
        if (stock < 0) {
            this.stock = 0;
        } else {
            this.stock = stock;
        }
    }
    
    // publicメソッドを通じて安全にアクセス
    public void updatePrice(double newPrice) {
        if (newPrice >= 0) {
            price = newPrice;
            System.out.println("価格を更新しました: " + price + "円");
        } else {
            System.out.println("エラー: 価格は0円以上である必要です");
        }
    }
    
    public void addStock(int amount) {
        if (amount > 0) {
            stock += amount;
            System.out.println("在庫を追加しました。現在の在庫: " + stock);
        }
    }
    
    public boolean sell(int amount) {
        if (amount > 0 && stock >= amount) {
            stock -= amount;
            System.out.println("販売しました。残り在庫: " + stock);
            return true;
        }
        System.out.println("在庫が不足しています");
        return false;
    }
}
```

#### 使用例と実行結果

```java
// ImprovedProductTest.java
public class ImprovedProductTest {
    public static void main(String[] args) {
        Product product = new Product(1500.0, 10);
        
        // 正常な操作
        product.updatePrice(1800.0);
        product.addStock(5);
        product.sell(3);
        


<!-- 
================
チャンク 10/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
