/**
 * かごに入った1種類の商品（商品名・単価・数量）
 */
public class CartItem {
    private final String name;
    private final int unitPrice;
    private int quantity;

    public CartItem(String name, int unitPrice, int quantity) {
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    void addQuantity(int amount) {
        quantity += amount;
    }

    /** 単価 × 数量 */
    public int price() {
        return unitPrice * quantity;
    }
}
