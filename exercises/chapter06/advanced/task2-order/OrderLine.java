import java.util.Objects;

/**
 * 注文の1行（商品名・単価・個数）。不変クラス
 */
public final class OrderLine {
    private final String product;
    private final int unitPrice;
    private final int quantity;

    public OrderLine(String product, int unitPrice, int quantity) {
        if (product == null || product.isBlank()) {
            throw new IllegalArgumentException("商品名を指定してください");
        }
        if (unitPrice <= 0 || quantity <= 0) {
            throw new IllegalArgumentException("単価と個数は1以上にしてください");
        }
        this.product = product;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public String getProduct() {
        return product;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int subtotal() {
        return unitPrice * quantity;
    }

    // 個数を変えた新しい行を返す
    public OrderLine withQuantity(int newQuantity) {
        return new OrderLine(product, unitPrice, newQuantity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OrderLine other = (OrderLine) obj;
        return unitPrice == other.unitPrice && quantity == other.quantity
                && product.equals(other.product);
    }

    @Override
    public int hashCode() {
        return Objects.hash(product, unitPrice, quantity);
    }

    @Override
    public String toString() {
        return product + " " + unitPrice + "円 × " + quantity;
    }
}
