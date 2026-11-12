/**
 * Productを試すプログラム（課題3の解答例）
 */
public class ProductTest {
    public static void main(String[] args) {
        IO.println("=== 商品管理システム ===");
        
        // Productオブジェクトを作成する
        Product product = new Product(1, "ノートPC", 80000, 10);
        product.showProductInfo();
        IO.println();
        
        // 入荷処理を行ってください
        product.restock(5);
        IO.println();
        
        // 出荷処理を行ってください
        product.ship(3);
        IO.println();
        
        // 在庫不足のエラーを確認する
        product.ship(20);
        IO.println();
        
        // 価格変更を行ってください
        product.setPrice(85000);
        IO.println();
        
        // 更新後の商品情報を表示する
        IO.println("更新後の商品情報:");
        product.showProductInfo();
        IO.println();
        
        // getterメソッドを使ってデータを取得する
        IO.println("getterメソッドでの取得:");
        IO.println("商品ID: " + product.getId());
        IO.println("商品名: " + product.getName());
        IO.println("価格: " + product.getPrice() + "円");
        IO.println("在庫数: " + product.getStock() + "個");
        IO.println("税込価格: " + "%,.0f".formatted(product.getTaxIncludedPrice()) + "円");
    }
}