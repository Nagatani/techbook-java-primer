import java.util.ArrayList;
import java.util.List;

/**
 * カフェの1回の注文。ドリンクのリストを持つ不変クラス
 */
public final class CafeOrder {
    public static final int SET_COUNT = 3;
    public static final int SET_DISCOUNT = 100;

    private final String customer;
    private final List<Drink> drinks;

    public CafeOrder(String customer) {
        this(customer, new ArrayList<>());
    }

    private CafeOrder(String customer, List<Drink> drinks) {
        if (customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("名前を指定してください");
        }
        this.customer = customer;
        this.drinks = new ArrayList<>(drinks);
    }

    public List<Drink> getDrinks() {
        return new ArrayList<>(drinks);
    }

    // ドリンクを足した新しい注文を返す
    public CafeOrder withDrink(Drink drink) {
        List<Drink> newDrinks = new ArrayList<>(drinks);
        newDrinks.add(drink);
        return new CafeOrder(customer, newDrinks);
    }

    // 同じ内容（equalsで等しい）のドリンクの杯数
    public int countOf(Drink drink) {
        int count = 0;
        for (Drink d : drinks) {
            if (d.equals(drink)) {
                count++;
            }
        }
        return count;
    }

    public int subtotal() {
        int sum = 0;
        for (Drink d : drinks) {
            sum += d.price();
        }
        return sum;
    }

    // SET_COUNT杯以上でSET_DISCOUNT円引き
    public int discount() {
        return drinks.size() >= SET_COUNT ? SET_DISCOUNT : 0;
    }

    public int total() {
        return subtotal() - discount();
    }

    public void printReceipt() {
        IO.println("  " + customer + "様のご注文");
        for (int i = 0; i < drinks.size(); i++) {
            IO.println("    " + (i + 1) + ". " + drinks.get(i));
        }
        IO.println("    小計 " + subtotal() + "円、割引 " + discount() + "円、合計 " + total() + "円");
    }
}
