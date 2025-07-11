package chapter08.basic;

/**
 * 商品を表すクラス
 * 
 * ショッピングカートシステムで使用する商品情報を保持します。
 */
public class Product {
    // 商品ID
    private String productId;
    // 商品名
    private String name;
    // 価格
    private double price;
    // 在庫数
    private int stock;
    
    /**
     * コンストラクタ
     * @param productId 商品ID
     * @param name 商品名
     * @param price 価格
     * @param stock 在庫数
     */
    public Product(String productId, String name, double price, int stock) {
        // TODO: 各フィールドを初期化してください
        
    }
    
    /**
     * 商品情報を文字列で返す
     * TODO: 商品情報を見やすい形式で返してください
     */
    @Override
    public String toString() {
        // TODO: 実装してください
        return "";
    }
    
    /**
     * 在庫を減らす
     * TODO: 指定された数量だけ在庫を減らしてください
     * @param quantity 減らす数量
     * @return 在庫が十分にあり、減らすことができた場合true
     */
    public boolean reduceStock(int quantity) {
        // TODO: 実装してください
        // 在庫が足りない場合はfalseを返す
        return false;
    }
    
    /**
     * 在庫を増やす
     * @param quantity 増やす数量
     */
    public void addStock(int quantity) {
        // TODO: 実装してください
    }
    
    // ゲッターメソッド
    public String getProductId() {
        return productId;
    }
    
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    public int getStock() {
        return stock;
    }
}