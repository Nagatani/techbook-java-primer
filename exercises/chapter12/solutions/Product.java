package chapter12.solutions;

/**
 * 商品のデータ（課題2の解答例）
 */
public class Product {
    private String name;
    private String category;
    private double price;
    private int quantity;

    public Product(String name, String category, double price, int quantity) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }

    /**
     * 売上金額（価格×数量）
     */
    public double getTotalSales() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return name + "（" + category + "、" + price + "ドル × " + quantity + "個）";
    }
}
