import java.util.ArrayList;
import java.util.List;

/**
 * 買い物かご。テストを1つずつ書きながら（TDDで）作ったクラス
 */
public class ShoppingCart {
    /** 割引が適用される小計の下限（円） */
    public static final int DISCOUNT_THRESHOLD = 5000;
    /** 割引率（%） */
    public static final int DISCOUNT_PERCENT = 10;

    private final List<CartItem> items = new ArrayList<>();

    /** 商品をかごに入れる。同じ名前の商品がすでにあれば数量を足す */
    public void add(String name, int unitPrice, int quantity) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("商品名が空です");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("単価が負です: " + unitPrice);
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量は1以上にしてください: " + quantity);
        }
        CartItem item = find(name);
        if (item == null) {
            items.add(new CartItem(name, unitPrice, quantity));
        } else {
            item.addQuantity(quantity);
        }
    }

    /** 商品をかごから取り除く。かごになければfalseを返す */
    public boolean remove(String name) {
        CartItem item = find(name);
        if (item == null) {
            return false;
        }
        items.remove(item);
        return true;
    }

    /** 商品の数（数量の合計） */
    public int itemCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    /** 割引前の合計（小計） */
    public int subtotal() {
        int sum = 0;
        for (CartItem item : items) {
            sum += item.price();
        }
        return sum;
    }

    /** 割引額。小計が5000円以上なら10%（1円未満は切り捨て） */
    public int discount() {
        int subtotal = subtotal();
        if (subtotal < DISCOUNT_THRESHOLD) {
            return 0;
        }
        return subtotal * DISCOUNT_PERCENT / 100;
    }

    /** 支払う金額 */
    public int total() {
        return subtotal() - discount();
    }

    private CartItem find(String name) {
        for (CartItem item : items) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    }
}
