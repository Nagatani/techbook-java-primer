<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 7/15
行範囲: 1104 - 1302
作成日時: 2025-08-02 22:58:07

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

> **重要**: 戻り値の型だけが異なるメソッドはオーバーロードできません。次のコードはコンパイルエラーになります。
> ```java
> public int calculate() { return 1; }
> public double calculate() { return 1.0; }  // エラー：戻り値の型だけでは区別できない
> ```

##### コンストラクタのオーバーロード

コンストラクタもメソッドと同様にオーバーロードできます。これにより、オブジェクトの作成時にさまざまな初期化方法を提供できます：

<span class="listing-number">**サンプルコード3-35**</span>

```java
public class Person {
    private String name;
    private int age;
    
    // デフォルトコンストラクタ
    public Person() {
        this("名無し", 0);
    }
    
    // 名前のみを指定するコンストラクタ
    public Person(String name) {
        this(name, 0);
    }
    
    // すべての属性を指定するコンストラクタ
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

これはコンストラクタオーバーロードの例です。

## 実用的なクラス設計例

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



<!-- 
================
チャンク 7/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
