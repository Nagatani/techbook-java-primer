/**
 * リスト3-4
 * Productクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (367行目)
 */

public class Product {
    protected String productId;  // ①
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