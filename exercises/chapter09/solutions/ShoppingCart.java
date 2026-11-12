package chapter09.solutions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ショッピングカート（課題2の解答例）
 *
 * 商品IDをキーにしたMapで、商品と数量を管理します。
 * LinkedHashMapで作るので、表示は商品を入れた順になります（HashMapでは順序が決まらない）。
 */
public class ShoppingCart {
    private Map<String, CartItem> items;
    private String customerName;

    public ShoppingCart(String customerName) {
        this.customerName = customerName;
        this.items = new LinkedHashMap<>();
    }

    /**
     * 商品をカートに入れる。同じ商品がすでにあれば数量を増やす。
     * カートの数量の合計が在庫を超えるときは入れずにfalseを返す
     */
    public boolean addItem(Product product, int quantity) {
        if (quantity <= 0) {
            return false;
        }
        CartItem item = items.get(product.getProductId());
        int current = 0;
        if (item != null) {
            current = item.getQuantity();
        }
        if (current + quantity > product.getStock()) {
            return false;
        }
        if (item == null) {
            items.put(product.getProductId(), new CartItem(product, quantity));
        } else {
            item.increaseQuantity(quantity);
        }
        return true;
    }

    /**
     * 商品をカートから除く。removeは除いた値（なければnull）を返す
     */
    public boolean removeItem(String productId) {
        return items.remove(productId) != null;
    }

    /**
     * 数量を変える。0を指定したらカートから除く
     */
    public boolean updateQuantity(String productId, int newQuantity) {
        CartItem item = items.get(productId);
        if (item == null || newQuantity < 0 || newQuantity > item.getProduct().getStock()) {
            return false;
        }
        if (newQuantity == 0) {
            items.remove(productId);
        } else {
            item.setQuantity(newQuantity);
        }
        return true;
    }

    public void displayCart() {
        IO.println("=== ショッピングカート (" + customerName + ") ===");
        for (CartItem item : items.values()) {
            IO.println(item);
        }
        IO.println("合計: " + (int) getTotalAmount() + "円");
    }

    public double getTotalAmount() {
        double total = 0.0;
        for (CartItem item : items.values()) {
            total += item.getSubtotal();
        }
        // 第12章のStream APIを使うと、次のように書ける
        // return items.values().stream().mapToDouble(CartItem::getSubtotal).sum();
        return total;
    }

    /**
     * 商品の種類の数ではなく、数量の合計を返す
     */
    public int getTotalItemCount() {
        int count = 0;
        for (CartItem item : items.values()) {
            count += item.getQuantity();
        }
        return count;
    }

    public void clearCart() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public boolean containsProduct(String productId) {
        return items.containsKey(productId);
    }
}
