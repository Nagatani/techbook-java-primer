<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 6/12
行範囲: 1143 - 1389
作成日時: 2025-08-02 14:34:01

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

### 例1：図書管理システム

図書館の蔵書管理システムを想定したBookクラスの例です。このクラスでは、本の基本情報と貸出状況を管理し、貸出・返却処理を安全に行う仕組みを提供します。

<span class="listing-number">**サンプルコード3-13**</span>

```java
public class Book {
    private String isbn;
    private String title;
    private String author;
    private boolean isAvailable;
    
    public Book(String isbn, String title, String author) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }
    
    public boolean borrow() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }
    
    public void returnBook() {
        isAvailable = true;
    }
    
    public String getInfo() {
        String status = isAvailable ? "貸出可能" : "貸出中";
        return String.format("%s - %s (%s) [%s]", isbn, title, author, status);
    }
}
```

使用例と実行結果：
```java
// BookLibraryTest.java
public class BookLibraryTest {
    public static void main(String[] args) {
        Book book = new Book("978-4-12345-678-9", "Java入門", "田中太郎");
        
        System.out.println(book.getInfo());
        
        // 本を借りる
        if (book.borrow()) {
            System.out.println("貸出処理が完了しました");
        }
        System.out.println(book.getInfo());
        
        // もう一度借りようとする
        if (!book.borrow()) {
            System.out.println("この本は貸出中です");
        }
        
        // 本を返却
        book.returnBook();
        System.out.println("返却処理が完了しました");
        System.out.println(book.getInfo());
    }
}
```

実行結果：
```
978-4-12345-678-9 - Java入門 (田中太郎) [貸出可能]
貸出処理が完了しました
978-4-12345-678-9 - Java入門 (田中太郎) [貸出中]
この本は貸出中です
返却処理が完了しました
978-4-12345-678-9 - Java入門 (田中太郎) [貸出可能]
```

このBookクラスでは、boolean型のisAvailableフィールドで貸出状況を管理しています。
borrowメソッドでは二重貸出を防ぎ、returnBookメソッドでは確実に返却処理を行うことで、データの整合性を保っています。

### 例2：ショッピングカート

ECサイトのショッピングカート機能を実装したクラスです。商品の追加、合計金額の計算（税込）、カート内容の表示など、ショッピングに必要な基本機能を提供します。

<span class="listing-number">**サンプルコード3-14**</span>

```java
import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private List<Item> items;
    private double taxRate;
    
    public ShoppingCart(double taxRate) {
        this.items = new ArrayList<>();
        this.taxRate = taxRate;
    }
    
    public void addItem(String name, double price, int quantity) {
        items.add(new Item(name, price, quantity));
    }
    
    public double calculateTotal() {
        double subtotal = 0;
        for (Item item : items) {
            subtotal += item.getSubtotal();
        }
        return subtotal * (1 + taxRate);
    }
    
    public void displayCart() {
        System.out.println("=== ショッピングカート ===");
        for (Item item : items) {
            System.out.println(item);
        }
        System.out.printf("合計（税込）: %.2f円%n", calculateTotal());
    }
    
    // 内部クラス
    private class Item {
        private String name;
        private double price;
        private int quantity;
        
        public Item(String name, double price, int quantity) {
            this.name = name;
            this.price = price;
            this.quantity = quantity;
        }
        
        public double getSubtotal() {
            return price * quantity;
        }
        
        @Override
        public String toString() {
            return String.format("%s - %.2f円 × %d = %.2f円", 
                name, price, quantity, getSubtotal());
        }
    }
}
```

使用例と実行結果：
```java
// ShoppingCartTest.java
public class ShoppingCartTest {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart(0.10); // 消費税10%
        
        cart.addItem("ノートPC", 98000, 1);
        cart.addItem("マウス", 3500, 2);
        cart.addItem("キーボード", 8500, 1);
        
        cart.displayCart();
    }
}
```

実行結果：
```
=== ショッピングカート ===
ノートPC - 98000.00円 × 1 = 98000.00円
マウス - 3500.00円 × 2 = 7000.00円
キーボード - 8500.00円 × 1 = 8500.00円
合計（税込）: 125050.00円
```

## まとめ

このパートでは、オブジェクト指向の基本概念を実際のコードを通じて学習しました。重要なポイントは以下のとおりです。

1. データと処理の一体化
    + クラスは関連するデータと処理をまとめる
2. カプセル化
    + 内部実装を隠蔽し、必要なインターフェイスのみを公開
3. オブジェクトの生成と操作
    + `new`キーワードでインスタンスを生成し、メソッドを通じて操作
4. 現実世界のモデリング
    + 実世界の概念をクラスとして表現










## オブジェクト指向の重要な用語と概念

ここからは、オブジェクト指向プログラミングで使用される重要な用語と概念を、実践的な例を交えて説明します。

### オブジェクトとは

オブジェクトは、コンピュータのメモリ上に展開された、プログラム内の「状態」と「振る舞い」を統合した存在です。「状態」とは、オブジェクトが保持するデータを指し、変数やフィールドとして実装されます。一方、「振る舞い」は、オブジェクトが実行できる処理内容であり、メソッド（関数）として実装されます。

重要なのは、これらの状態と振る舞いが別々に存在するのではなく、密接な関連を持って一体化される点です。たとえば、銀行口座オブジェクトであれば、残高（状態）と入金・出金操作（振る舞い）が1つのオブジェクトとして管理されることで、データの整合性を保ちやすくなります。

### クラスとは

オブジェクトはメモリ上に展開されて使用されます。
そのオブジェクトは、どんな状態を持って、どんな振る舞いをするのかを定義するのがクラスです。

よくある表現として、オブジェクトの設計図をクラスという言い方がされます。

### インスタンス（実体）とは
オブジェクトは、メモリ上に展開されて使用されます。
その状態をインスタンスと呼び、メモリ上に展開して使用できるようにすることをインスタンス化（実体化）と言います。

### クラス、インスタンス、オブジェクトの関係

オブジェクト指向プログラミングでは、「クラス」「インスタンス」「オブジェクト」という用語が頻繁に登場し、初学者にとって混乱の原因となることがあります。これらの関係を明確に理解することが重要です。

クラスは、オブジェクトの設計図です。建築にたとえれば、家の設計図にあたります。クラスは、どのようなデータ（フィールド）を持ち、どのような操作（メソッド）ができるかを定義します。

インスタンスは、クラスをもとに実際に作成された具体的な実体です。設計図から実際に建てられた家に相当します。1つのクラスから複数のインスタンスを作成でき、それぞれが独立したデータを保持します。

オブジェクトという用語は、文脈によって意味が変わることに注意が必要です。多くの場合、オブジェクトはインスタンスを指しますが、より幅広い概念を表す場合や、クラス自体を指す場合もあります。「オブジェクト指向」という場合のオブジェクトは、より抽象的な概念を表しています。

※厳密には、インスタンス化をしなくても内部的に使用できる状態や振る舞いというのも存在します。それらは「静的（static）な○○」と呼ばれ、インスタンス化を明示的に行わなくても、プログラム実行時にJVMによってクラスがロードされ、メモリへ展開されて使用できるようになっています。対義語として「動的（dynamic）な○○」という言い方もあり、そちらはインスタンス化しないと使用できません。

> 補足: JVMのクラスローディング機構により、staticメンバーはクラスがはじめて参照されたときに初期化されます。また、JITコンパイラによって実行時に最適化され、頻繁に使用されるstaticメソッドは高速に実行されます。

### クラスはどのように書くか

何らかのデータと、それに対する処理をまとめて書けるとよいです。

「クラスは、C言語における構造体（struct）に、関数をつけられるようにしたもの」のようにイメージすると理解しやすいです。

### 役割ごとに分割する利点

役割ごとに分割することで、大規模なプログラムを作る際に管理しやすくなることがあります。
ただし、これは1つのアプローチに過ぎません。
WebアプリケーションではMVCパターン（Model-View-Controller）、マイクロサービスではドメイン分割など、システムの特性に応じた分割方法が用いられます。
管理しやすいとは、複雑でなく、バグの発見・修正が容易なことを指します。

### 手続き型 vs オブジェクト指向

ここでは、手続き型プログラミングとオブジェクト指向プログラミングの根本的な違いを、学生情報を管理するプログラムの例を通じて理解します。この違いは、データの管理方法と責任の所在に関する重要な設計思想の違いを表しています。

#### 手続き型プログラミングの特徴と課題

手続き型プログラミングでは、データ構造と処理が分離されており、グローバルな関数がデータを操作します。この方式は単純で理解しやすい反面、大規模なシステムでは以下のような問題が生じます。



<!-- 
================
チャンク 6/12 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
