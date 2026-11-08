// ProductTest.java
public class ProductTest {
    public static void main(String[] args) {
        Product product = new Product("P001", "ノートPC", 98000);
        
        product.showInfo();
        System.out.println("---");
        
        product.addStock(10);
        product.showInfo();
    }
}
