import java.util.ArrayList;
import java.util.List;

/**
 * ピザの注文の内容と値段（画面に依存しないロジック）
 */
public class PizzaOrder {
    public enum Size {
        S("S", 800), M("M", 1200), L("L", 1600);

        private final String label;
        private final int price;

        Size(String label, int price) {
            this.label = label;
            this.price = price;
        }

        public String getLabel() {
            return label;
        }

        public int getPrice() {
            return price;
        }
    }

    public enum Topping {
        CHEESE("チーズ増量", 200), MUSHROOM("きのこ", 150), CORN("コーン", 100), BACON("ベーコン", 250);

        private final String label;
        private final int price;

        Topping(String label, int price) {
            this.label = label;
            this.price = price;
        }

        public String getLabel() {
            return label;
        }

        public int getPrice() {
            return price;
        }
    }

    private Size size = Size.M;
    private final List<Topping> toppings = new ArrayList<>();

    public void setSize(Size size) {
        this.size = size;
    }

    public void setTopping(Topping topping, boolean selected) {
        toppings.remove(topping);
        if (selected) {
            toppings.add(topping);
        }
    }

    public int getTotal() {
        int total = size.getPrice();
        for (Topping t : toppings) {
            total += t.getPrice();
        }
        return total;
    }

    public String describe() {
        StringBuilder sb = new StringBuilder(size.getLabel() + "サイズ");
        for (Topping t : Topping.values()) { // 選んだ順ではなく、決まった順で並べる
            if (toppings.contains(t)) {
                sb.append("＋").append(t.getLabel());
            }
        }
        return sb.toString();
    }
}
