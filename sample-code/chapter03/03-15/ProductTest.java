// ProductTest.java
public class ProductTest {
    public static void main(String[] args) {
        Product product = new Product("P001", "ノートPC", 98000);
        
        product.showInfo();
        IO.println("---");
        
        product.addStock(10);
        product.showInfo();
    }
}
