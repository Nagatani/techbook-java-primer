/**
 * 1杯分の注文。メニュー・サイズ・割引の組み合わせを持つ
 */
public class Order {
    private final Menu menu;
    private final Size size;
    private final Discount discount;

    public Order(Menu menu, Size size, Discount discount) {
        this.menu = menu;
        this.size = size;
        this.discount = discount;
    }

    public Menu getMenu() {
        return menu;
    }

    public Size getSize() {
        return size;
    }

    // 割引後の価格。計算はそれぞれの列挙子に任せる
    public int price() {
        return discount.apply(menu.priceFor(size));
    }

    @Override
    public String toString() {
        return menu.getDisplayName() + "（" + size.getLabel() + " " + size.getMilliliters() + "ml）"
                + " " + discount.getDescription() + " " + price() + "円";
    }
}
