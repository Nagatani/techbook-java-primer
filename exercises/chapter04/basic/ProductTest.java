/**
 * 第4章 基本課題3: ProductTest クラス
 * 
 * Productクラスをテストするためのクラスです。
 * 商品データの管理と在庫操作を確認してください。
 * 
 * 実行例:
 * === 商品管理システム ===
 * 商品ID: 001
 * 商品名: ノートPC
 * 価格: 80,000円（税込: 88,000円）
 * 在庫数: 10個
 * 
 * 入荷処理: 5個を入荷しました。
 * 現在の在庫: 15個
 * 
 * 出荷処理: 3個を出荷しました。
 * 現在の在庫: 12個
 * 
 * エラー: 在庫が不足しています。（要求: 20個, 在庫: 12個）
 */
public class ProductTest {
    public static void main(String[] args) {
        System.out.println("=== 商品管理システム ===");
        
        // TODO: Productオブジェクトを作成してください
        // Product product = new Product(1, "ノートPC", 80000, 10);
        // product.showProductInfo();
        // System.out.println();
        
        // TODO: 入荷処理を行ってください
        // product.restock(5);
        // System.out.println();
        
        // TODO: 出荷処理を行ってください
        // product.ship(3);
        // System.out.println();
        
        // TODO: 在庫不足のエラーを確認してください
        // product.ship(20);
        // System.out.println();
        
        // TODO: 価格変更を行ってください
        // product.setPrice(85000);
        // System.out.println();
        
        // TODO: 更新後の商品情報を表示してください
        // System.out.println("更新後の商品情報:");
        // product.showProductInfo();
        // System.out.println();
        
        // TODO: getterメソッドを使ってデータを取得してください
        // System.out.println("getterメソッドでの取得:");
        // System.out.println("商品ID: " + product.getId());
        // System.out.println("商品名: " + product.getName());
        // System.out.println("価格: " + product.getPrice() + "円");
        // System.out.println("在庫数: " + product.getStock() + "個");
        // System.out.println("税込価格: " + String.format("%.0f", product.getTaxIncludedPrice()) + "円");
    }
}