package chapter08.basic;

/**
 * ショッピングカートシステムのデモプログラム
 * 
 * HashMapとオブジェクトの組み合わせの使い方を学習します。
 */
public class ShoppingCartDemo {
    public static void main(String[] args) {
        System.out.println("=== ショッピングカートシステムデモ ===\n");
        
        // TODO: 商品を作成してください
        System.out.println("--- 商品在庫 ---");
        // Product laptop = new Product("P001", "ノートパソコン", 80000, 5);
        // Product mouse = new Product("P002", "マウス", 2000, 10);
        // Product keyboard = new Product("P003", "キーボード", 5000, 8);
        // 商品情報を表示
        
        // TODO: ショッピングカートを作成してください
        System.out.println("\n--- カート作成 ---");
        // ShoppingCart cart = new ShoppingCart("山田太郎");
        
        // TODO: 商品をカートに追加してください
        System.out.println("\n--- 商品追加 ---");
        // cart.addItem(laptop, 1);
        // cart.addItem(mouse, 2);
        // 追加結果を表示
        
        // TODO: カートの内容を表示してください
        System.out.println("\n--- カート内容 ---");
        // cart.displayCart();
        
        // TODO: 同じ商品を追加（数量が増えることを確認）
        System.out.println("\n--- 同じ商品を追加 ---");
        // cart.addItem(mouse, 3);
        // cart.displayCart();
        
        // TODO: 数量を更新してください
        System.out.println("\n--- 数量更新 ---");
        // cart.updateQuantity("P002", 1);
        // cart.displayCart();
        
        // TODO: 在庫以上の注文を試みる
        System.out.println("\n--- 在庫チェック ---");
        // boolean result = cart.addItem(laptop, 10);
        // 結果を表示
        
        // TODO: 商品を削除してください
        System.out.println("\n--- 商品削除 ---");
        // cart.removeItem("P001");
        // cart.displayCart();
        
        // TODO: カートの統計情報を表示
        System.out.println("\n--- カート統計 ---");
        // System.out.println("総アイテム数: " + cart.getTotalItemCount());
        // System.out.println("合計金額: " + cart.getTotalAmount() + "円");
        
        // TODO: カートをクリア
        System.out.println("\n--- カートクリア ---");
        // cart.clearCart();
        // System.out.println("カートは空？: " + cart.isEmpty());
    }
}