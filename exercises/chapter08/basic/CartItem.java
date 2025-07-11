package chapter08.basic;

/**
 * カート内の商品アイテムを表すクラス
 * 
 * 商品とその数量を保持します。
 */
public class CartItem {
    // 商品
    private Product product;
    // 数量
    private int quantity;
    
    /**
     * コンストラクタ
     * @param product 商品
     * @param quantity 数量
     */
    public CartItem(Product product, int quantity) {
        // TODO: フィールドを初期化してください
        
    }
    
    /**
     * このアイテムの小計を計算する
     * TODO: 商品価格 × 数量で計算してください
     * @return 小計金額
     */
    public double getSubtotal() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * カートアイテム情報を文字列で返す
     * TODO: 商品名、単価、数量、小計を含む情報を返してください
     */
    @Override
    public String toString() {
        // TODO: 実装してください
        return "";
    }
    
    // ゲッターメソッド
    public Product getProduct() {
        return product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    // セッターメソッド
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    /**
     * 数量を増やす
     * @param amount 増やす数量
     */
    public void increaseQuantity(int amount) {
        // TODO: 実装してください
    }
}