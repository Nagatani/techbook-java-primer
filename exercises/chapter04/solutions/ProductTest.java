/**
 * 第4章 演習問題3: ProductTestクラスの解答例
 */
public class ProductTest {
    public static void main(String[] args) {
        System.out.println("=== Product クラスのテスト ===");
        
        // 商品の作成
        Product product = new Product(1, "ノートPC", 80000.0, 10);
        
        System.out.println("初期状態:");
        product.displayInfo();
        
        // 入荷処理のテスト
        System.out.println("\n--- 入荷処理 ---");
        product.addStock(5);
        
        // 出荷処理のテスト
        System.out.println("\n--- 出荷処理 ---");
        product.removeStock(3);
        
        // 在庫不足のテスト
        System.out.println("\n--- 在庫不足テスト ---");
        product.removeStock(20);
        
        // 在庫状態のテスト
        System.out.println("\n--- 在庫状態 ---");
        System.out.println("在庫あり: " + product.isInStock());
        System.out.println("在庫僅少: " + product.isLowStock());
        System.out.println("品切れ: " + product.isOutOfStock());
        
        // 異常ケースのテスト
        System.out.println("\n--- 異常ケース ---");
        product.setPrice(-1000);  // 負の価格
        product.addStock(-5);     // 負の入荷
        product.removeStock(-3);  // 負の出荷
        
        System.out.println("\n最終状態:");
        product.displayInfo();
    }
}