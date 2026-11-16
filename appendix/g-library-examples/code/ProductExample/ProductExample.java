public class ProductExample {
    public static void main(String[] args) {
        Product product = new Product(15000.0);
        product.setName("ノートPC");
        IO.println("Product: " + product);
        IO.println("Price: " + product.getPrice());
    }
}
