/**
 * 第8章 発展課題: コーヒーショップの注文（解答例）
 *
 * メニュー・サイズ・割引を列挙型で表し、注文を集計します。
 *
 * 学習ポイント:
 * - フィールド・コンストラクター・メソッドを持つ列挙型（Size、Menu）
 * - 列挙子ごとに抽象メソッドを実装する列挙型（Discount）
 * - values()による列挙子の一覧と、記号から列挙子を探すstaticメソッド
 * - EnumSetとEnumMapによる列挙子の集合と対応表
 */
public class CoffeeShopApp {
    public static void main(String[] args) {
        CoffeeShop shop = new CoffeeShop();

        IO.println("=== メニュー ===");
        for (Menu menu : Menu.values()) {
            String line = "  " + menu.getDisplayName();
            for (Size size : Size.values()) {
                line += " " + size.getLabel() + ":" + menu.priceFor(size) + "円";
            }
            IO.println(line);
        }

        IO.println();
        IO.println("=== 割引の計算（Tサイズのカフェラテ " + Menu.LATTE.priceFor(Size.TALL) + "円） ===");
        for (Discount discount : Discount.values()) {
            IO.println("  " + discount.getDescription() + " → " + discount.apply(Menu.LATTE.priceFor(Size.TALL)) + "円");
        }

        IO.println();
        IO.println("=== 注文 ===");
        shop.markSoldOut(Menu.MATCHA_LATTE);
        IO.println(shop.order(new Order(Menu.LATTE, Size.TALL, Discount.STUDENT)));
        IO.println(shop.order(new Order(Menu.COFFEE, Size.fromLabel("G"), Discount.MY_BOTTLE)));
        IO.println(shop.order(new Order(Menu.MATCHA_LATTE, Size.SHORT, Discount.NONE)));
        IO.println(shop.order(new Order(Menu.COFFEE, Size.SHORT, Discount.NONE)));
        IO.println(shop.order(new Order(Menu.COFFEE, Size.SHORT, Discount.SECOND_CUP)));
        // 文字列から列挙子を得るvalueOf（列挙子の名前と完全に一致させる）
        IO.println(shop.order(new Order(Menu.valueOf("LATTE"), Size.fromLabel("S"), Discount.NONE)));

        IO.println();
        IO.println("=== 集計 ===");
        shop.printReport();
    }
}
