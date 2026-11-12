/**
 * カートの1行。商品と個数を持つ
 */
public record CartItem(Product product, int quantity) {
    public static final int MAX_QUANTITY = 99;

    public CartItem {
        if (quantity < 1 || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("個数は1〜" + MAX_QUANTITY + "にしてください: " + quantity);
        }
    }

    // 個数だけを変えた新しいオブジェクトを返す（withメソッド）
    public CartItem withQuantity(int newQuantity) {
        return new CartItem(product, newQuantity);
    }

    public Money subtotal() {
        return product.price().times(quantity);
    }
}
