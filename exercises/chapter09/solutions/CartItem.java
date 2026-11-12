package chapter09.solutions;

/**
 * カートの中の商品と数量（課題2の解答例）
 */
public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * 小計（単価×数量）を返す
     */
    public double getSubtotal() {
        return product.getPrice() * quantity;
    }

    @Override
    public String toString() {
        return product.getName() + " " + (int) product.getPrice() + "円 × " + quantity
                + " = " + (int) getSubtotal() + "円";
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void increaseQuantity(int amount) {
        quantity += amount;
    }
}
