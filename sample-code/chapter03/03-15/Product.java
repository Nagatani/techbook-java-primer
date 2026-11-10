public class Product {
    // フィールドの宣言
    private String productId;  // 商品ID
    private String name;       // 商品名  
    private double price;      // 価格
    private int stock;         // 在庫数
    
    // コンストラクタでフィールドを初期化
    public Product(String productId, String name, double price) {
        this.productId = productId;  // thisは現在のオブジェクトを指す
        this.name = name;
        this.price = price;
        this.stock = 0;              // 初期在庫は0
    }
    
    // フィールドを使った処理
    public void addStock(int quantity) {
        if (quantity > 0) {
            stock += quantity;  // 在庫数を更新
            IO.println(quantity + "個入荷しました");
        }
    }
    
    // フィールドの値を表示
    public void showInfo() {
        IO.println("商品ID: " + productId);
        IO.println("商品名: " + name);
        IO.println("価格: ¥" + price);
        IO.println("在庫数: " + stock + "個");
    }
}
