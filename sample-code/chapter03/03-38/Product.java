public class Product {
    // privateフィールド - 直接アクセスできない
    private double price;
    private int stock;
    
    // コンストラクタで初期値を設定
    public Product(double price, int stock) {
        // 初期値の検証も可能
        if (price < 0) {
            this.price = 0;
        } else {
            this.price = price;
        }
        
        if (stock < 0) {
            this.stock = 0;
        } else {
            this.stock = stock;
        }
    }
    
    // publicメソッドを通じて安全にアクセス
    public void updatePrice(double newPrice) {
        if (newPrice >= 0) {
            price = newPrice;
            IO.println("価格を更新しました: " + price + "円");
        } else {
            IO.println("エラー: 価格は0円以上にしてください");
        }
    }
    
    public void addStock(int amount) {
        if (amount > 0) {
            stock += amount;
            IO.println("在庫を追加しました。現在の在庫: " + stock);
        }
    }
    
    public boolean sell(int amount) {
        if (amount > 0 && stock >= amount) {
            stock -= amount;
            IO.println("販売しました。残り在庫: " + stock);
            return true;
        }
        IO.println("在庫が不足しています");
        return false;
    }
}
