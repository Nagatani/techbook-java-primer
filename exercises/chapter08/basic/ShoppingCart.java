package chapter08.basic;

import java.util.*;

/**
 * ショッピングカート
 * 
 * HashMapを使用してカート内の商品を管理します。
 */
public class ShoppingCart {
    // 商品IDをキーとするカートアイテムのマップ
    private Map<String, CartItem> items;
    // カートの所有者名
    private String customerName;
    
    /**
     * コンストラクタ
     * @param customerName カートの所有者名
     */
    public ShoppingCart(String customerName) {
        // TODO: フィールドを初期化してください
        
    }
    
    /**
     * 商品をカートに追加する
     * TODO: 既に同じ商品がカートにある場合は数量を増やしてください
     * @param product 追加する商品
     * @param quantity 数量
     * @return 追加に成功した場合true（在庫不足の場合false）
     */
    public boolean addItem(Product product, int quantity) {
        // TODO: 実装してください
        // ヒント:
        // 1. 在庫チェック
        // 2. 既存アイテムのチェック
        // 3. 新規追加または数量更新
        return false;
    }
    
    /**
     * 商品をカートから削除する
     * TODO: 指定された商品をカートから完全に削除してください
     * @param productId 削除する商品のID
     * @return 削除に成功した場合true
     */
    public boolean removeItem(String productId) {
        // TODO: 実装してください
        return false;
    }
    
    /**
     * 商品の数量を更新する
     * TODO: 指定された商品の数量を変更してください
     * @param productId 商品ID
     * @param newQuantity 新しい数量
     * @return 更新に成功した場合true
     */
    public boolean updateQuantity(String productId, int newQuantity) {
        // TODO: 実装してください
        // 注意: newQuantityが0の場合は商品を削除
        return false;
    }
    
    /**
     * カート内の全商品を表示する
     * TODO: カート内の全商品と合計金額を表示してください
     */
    public void displayCart() {
        System.out.println("=== ショッピングカート (" + customerName + ") ===");
        // TODO: 実装してください
        // 各商品の情報と最後に合計金額を表示
    }
    
    /**
     * カートの合計金額を計算する
     * TODO: 全アイテムの小計の合計を計算してください
     * @return 合計金額
     */
    public double getTotalAmount() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * カート内のアイテム数を取得する
     * TODO: 商品の種類数ではなく、総数量を返してください
     * @return カート内の総アイテム数
     */
    public int getTotalItemCount() {
        // TODO: 実装してください
        return 0;
    }
    
    /**
     * カートをクリアする
     * TODO: カート内の全商品を削除してください
     */
    public void clearCart() {
        // TODO: 実装してください
    }
    
    /**
     * カートが空かどうかを判定する
     * @return カートが空の場合true
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * 特定の商品がカートに含まれているか確認する
     * @param productId 商品ID
     * @return 含まれている場合true
     */
    public boolean containsProduct(String productId) {
        return items.containsKey(productId);
    }
}