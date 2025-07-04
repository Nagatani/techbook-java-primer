/**
 * 第4章 基本課題3: Productクラスのデータ管理
 * 
 * 課題: 商品を表すProductクラスを作成し、適切なデータ管理を実装してください。
 * 
 * 要求仕様:
 * - privateフィールド: id（int）, name（String）, price（double）, stock（int）
 * - 在庫管理メソッド（入荷、出荷）
 * - 価格の税込計算
 * - 商品情報の表示
 * 
 * 評価ポイント:
 * - 複数のprivateフィールドの管理
 * - ビジネスロジックの実装
 * - エラーハンドリング
 */
public class Product {
    // TODO: privateフィールドを宣言してください
    // private int id;
    // private String name;
    // private double price;
    // private int stock;
    
    // TODO: コンストラクタを実装してください
    // public Product(int id, String name, double price, int stock) {
    //     this.id = id;
    //     this.name = name;
    //     this.price = price;
    //     this.stock = stock;
    // }
    
    // TODO: 各フィールドのgetterメソッドを実装してください
    // public int getId() {
    //     return id;
    // }
    
    // public String getName() {
    //     return name;
    // }
    
    // public double getPrice() {
    //     return price;
    // }
    
    // public int getStock() {
    //     return stock;
    // }
    
    // TODO: 税込価格を計算するメソッドを実装してください
    // public double getTaxIncludedPrice() {
    //     return price * 1.1;
    // }
    
    // TODO: 入荷メソッドを実装してください
    // public void restock(int quantity) {
    //     if (quantity > 0) {
    //         stock += quantity;
    //         System.out.println("入荷処理: " + quantity + "個を入荷しました。");
    //         System.out.println("現在の在庫: " + stock + "個");
    //     } else {
    //         System.out.println("エラー: 入荷数は0より大きい必要があります。");
    //     }
    // }
    
    // TODO: 出荷メソッドを実装してください
    // public void ship(int quantity) {
    //     if (quantity > 0) {
    //         if (stock >= quantity) {
    //             stock -= quantity;
    //             System.out.println("出荷処理: " + quantity + "個を出荷しました。");
    //             System.out.println("現在の在庫: " + stock + "個");
    //         } else {
    //             System.out.println("エラー: 在庫が不足しています。（要求: " + quantity + "個, 在庫: " + stock + "個）");
    //         }
    //     } else {
    //         System.out.println("エラー: 出荷数は0より大きい必要があります。");
    //     }
    // }
    
    // TODO: 価格設定メソッドを実装してください（妥当性検証付き）
    // public void setPrice(double price) {
    //     if (price >= 0) {
    //         this.price = price;
    //         System.out.println("価格を" + String.format("%.0f", price) + "円に変更しました。");
    //     } else {
    //         System.out.println("エラー: 価格は0円以上である必要があります。");
    //     }
    // }
    
    // TODO: 商品情報表示メソッドを実装してください
    // public void showProductInfo() {
    //     System.out.println("商品ID: " + String.format("%03d", id));
    //     System.out.println("商品名: " + name);
    //     System.out.println("価格: " + String.format("%.0f", price) + "円（税込: " + String.format("%.0f", getTaxIncludedPrice()) + "円）");
    //     System.out.println("在庫数: " + stock + "個");
    // }
}