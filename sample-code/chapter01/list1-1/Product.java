/**
 * リスト1-1
 * Productクラス
 * 
 * 元ファイル: chapter01-introduction.md (254行目)
 */

public class Product {
    private String name;  // ①
    private int price;    // ②
    
    public Product(String name, int price) { // ③
        this.name = name;
        this.price = price;
    }
    
    public int getPriceWithTax() {           // ④
        return (int)(price * 1.1);
    }
    
    public void display() {                  // ⑤
        System.out.println(name + ": " + getPriceWithTax() + "円（税込）");
    }
}

public class Main {
    public static void main(String[] args) {
        Product pencil = new Product("鉛筆", 100); // ⑥
        pencil.display();                            // ⑦
    }
}