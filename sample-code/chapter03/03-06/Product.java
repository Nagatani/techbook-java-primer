public class Product {
    // ① protectedにより同一パッケージまたはサブクラスからアクセス可能
    protected String productId;
    protected String name;       // ①
    protected int price;         // ①
    
    public void displayInfo() {  // ②
        IO.println("商品名: " + name + ", 価格: " + price + "円");
    }
}
