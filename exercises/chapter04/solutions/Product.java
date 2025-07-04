/**
 * 第4章 演習問題3: Productクラスの解答例
 * 
 * 【学習ポイント】
 * - 商品データの適切な管理
 * - 在庫管理システムの実装
 * - ビジネスロジックの実装
 * - エラーハンドリング
 */
public class Product {
    private int id;
    private String name;
    private double price;
    private int stock;
    private static final double TAX_RATE = 0.1;  // 消費税率10%
    
    public Product(int id, String name, double price, int stock) {
        this.id = id;
        setName(name);
        setPrice(price);
        setStock(stock);
    }
    
    // getter/setterメソッド
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            this.name = "未設定";
        } else {
            this.name = name.trim();
        }
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            System.out.println("エラー: 価格は0円以上である必要があります。");
            return;
        }
        this.price = price;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        if (stock < 0) {
            System.out.println("エラー: 在庫数は0個以上である必要があります。");
            return;
        }
        this.stock = stock;
    }
    
    // 税込価格の計算
    public double getTaxIncludedPrice() {
        return price * (1 + TAX_RATE);
    }
    
    // 入荷処理
    public void addStock(int quantity) {
        if (quantity <= 0) {
            System.out.println("エラー: 入荷数は正の値である必要があります。");
            return;
        }
        
        stock += quantity;
        System.out.println(quantity + "個を入荷しました。現在の在庫: " + stock + "個");
    }
    
    // 出荷処理
    public boolean removeStock(int quantity) {
        if (quantity <= 0) {
            System.out.println("エラー: 出荷数は正の値である必要があります。");
            return false;
        }
        
        if (stock < quantity) {
            System.out.println("エラー: 在庫が不足しています。（要求: " + quantity + "個, 在庫: " + stock + "個）");
            return false;
        }
        
        stock -= quantity;
        System.out.println(quantity + "個を出荷しました。現在の在庫: " + stock + "個");
        return true;
    }
    
    // 商品情報の表示
    public void displayInfo() {
        System.out.println("商品ID: " + String.format("%03d", id));
        System.out.println("商品名: " + name);
        System.out.printf("価格: %.0f円（税込: %.0f円）%n", price, getTaxIncludedPrice());
        System.out.println("在庫数: " + stock + "個");
    }
    
    // 在庫の状態判定
    public boolean isInStock() {
        return stock > 0;
    }
    
    public boolean isLowStock() {
        return stock > 0 && stock <= 5;  // 5個以下を在庫僅少とする
    }
    
    public boolean isOutOfStock() {
        return stock == 0;
    }
    
    @Override
    public String toString() {
        return String.format("Product{id=%d, name='%s', price=%.0f, stock=%d}", 
                           id, name, price, stock);
    }
}