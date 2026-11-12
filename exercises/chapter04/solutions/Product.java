/**
 * 商品を表すクラス（課題3の解答例）
 */
public class Product {
    // privateフィールドを宣言する
    private int id;
    private String name;
    private double price;
    private int stock;
    
    // コンストラクタ
    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }
    
    // 各フィールドのgetterメソッド
    public int getId() {
        return id;
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
    
    // 税込価格を計算するメソッド
    public double getTaxIncludedPrice() {
        return price * 1.1;
    }
    
    // 入荷メソッド
    public void restock(int quantity) {
        if (quantity > 0) {
            stock += quantity;
            IO.println("入荷処理: " + quantity + "個を入荷しました。");
            IO.println("現在の在庫: " + stock + "個");
        } else {
            IO.println("エラー: 入荷数は0より大きい必要があります。");
        }
    }
    
    // 出荷メソッド
    public void ship(int quantity) {
        if (quantity > 0) {
            if (stock >= quantity) {
                stock -= quantity;
                IO.println("出荷処理: " + quantity + "個を出荷しました。");
                IO.println("現在の在庫: " + stock + "個");
            } else {
                IO.println("エラー: 在庫が不足しています。（要求: " + quantity + "個, 在庫: " + stock + "個）");
            }
        } else {
            IO.println("エラー: 出荷数は0より大きい必要があります。");
        }
    }
    
    // 価格設定メソッド（妥当性検証付き）
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
            IO.println("価格を" + "%,.0f".formatted(price) + "円に変更しました。");
        } else {
            IO.println("エラー: 価格は0円以上である必要があります。");
        }
    }
    
    // 商品情報表示メソッド
    public void showProductInfo() {
        IO.println("商品ID: " + "%03d".formatted(id));
        IO.println("商品名: " + name);
        IO.println("価格: " + "%,.0f".formatted(price) + "円（税込: " + "%,.0f".formatted(getTaxIncludedPrice()) + "円）");
        IO.println("在庫数: " + stock + "個");
    }
}