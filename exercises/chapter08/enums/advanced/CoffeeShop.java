import java.util.EnumMap;
import java.util.EnumSet;

/**
 * コーヒーショップのレジ。売り切れのメニューをEnumSetで、
 * メニューごと・サイズごとの杯数をEnumMapで管理する
 */
public class CoffeeShop {
    private final EnumSet<Menu> soldOut = EnumSet.noneOf(Menu.class);
    private final EnumMap<Menu, Integer> cupsByMenu = new EnumMap<>(Menu.class);
    private final EnumMap<Menu, Integer> salesByMenu = new EnumMap<>(Menu.class);
    private final EnumMap<Size, Integer> cupsBySize = new EnumMap<>(Size.class);

    public CoffeeShop() {
        // すべての列挙子を0で用意しておく
        for (Menu menu : Menu.values()) {
            cupsByMenu.put(menu, 0);
            salesByMenu.put(menu, 0);
        }
        for (Size size : Size.values()) {
            cupsBySize.put(size, 0);
        }
    }

    public void markSoldOut(Menu menu) {
        soldOut.add(menu);
    }

    public String order(Order order) {
        Menu menu = order.getMenu();
        if (soldOut.contains(menu)) {
            return menu.getDisplayName() + "は売り切れです";
        }
        cupsByMenu.put(menu, cupsByMenu.get(menu) + 1);
        salesByMenu.put(menu, salesByMenu.get(menu) + order.price());
        cupsBySize.put(order.getSize(), cupsBySize.get(order.getSize()) + 1);
        return "注文: " + order;
    }

    public void printReport() {
        int total = 0;
        IO.println("メニュー別（EnumMapは列挙子の宣言順に並ぶ）:");
        for (Menu menu : Menu.values()) {
            IO.println("  " + menu.getDisplayName() + " " + cupsByMenu.get(menu) + "杯 " + salesByMenu.get(menu) + "円");
            total += salesByMenu.get(menu);
        }
        IO.println("サイズ別: " + cupsBySize);
        IO.println("売上合計: " + total + "円");

        // 1杯も売れなかったメニュー
        EnumSet<Menu> notSold = EnumSet.noneOf(Menu.class);
        for (Menu menu : Menu.values()) {
            if (cupsByMenu.get(menu) == 0) {
                notSold.add(menu);
            }
        }
        IO.println("売れなかったメニュー: " + notSold);
        IO.println("売り切れ: " + soldOut + "、販売中: " + EnumSet.complementOf(soldOut));
    }
}
