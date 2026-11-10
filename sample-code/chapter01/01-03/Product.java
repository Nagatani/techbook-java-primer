// Product.java
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
        IO.println(name + ": " + getPriceWithTax() + "円（税込）");
    }
}
