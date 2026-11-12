import java.util.LinkedHashMap;
import java.util.Map;

/**
 * レジ。登録した割引ルールを順に当てはめて、レシートを表示する
 */
public class Register {
    // 登録した順に表示したいのでLinkedHashMapを使う
    private final Map<String, DiscountRule> rules = new LinkedHashMap<>();

    public void addRule(String name, DiscountRule rule) {
        rules.put(name, rule);
    }

    public int checkout(Cart cart) {
        cart.forEachItem(item -> IO.println("  " + item.name() + " " + item.price() + "円"));
        int total = cart.total();
        IO.println("  小計 " + total + "円");

        int discountTotal = 0;
        for (Map.Entry<String, DiscountRule> entry : rules.entrySet()) {
            int amount = entry.getValue().discount(cart);
            if (amount > 0) {
                IO.println("  %s -%d円".formatted(entry.getKey(), amount));
                discountTotal += amount;
            }
        }
        int pay = Math.max(total - discountTotal, 0);
        IO.println("  お支払い " + pay + "円");
        return pay;
    }
}
