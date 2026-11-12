package chapter09.solutions;

/**
 * 商品を表すクラス（課題2の解答例）
 */
public class Product {
    private String productId;
    private String name;
    private double price;
    private int stock;

    public Product(String productId, String name, double price, int stock) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    @Override
    public String toString() {
        return productId + ": " + name + "（" + (int) price + "円、在庫" + stock + "個）";
    }

    /**
     * 在庫を減らす。在庫が足りないときは減らさずにfalseを返す
     */
    public boolean reduceStock(int quantity) {
        if (quantity <= 0 || quantity > stock) {
            return false;
        }
        stock -= quantity;
        return true;
    }

    public void addStock(int quantity) {
        if (quantity > 0) {
            stock += quantity;
        }
    }

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
