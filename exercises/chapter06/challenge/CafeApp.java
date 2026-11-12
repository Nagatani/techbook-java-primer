import java.util.List;

public class CafeApp {
    public static void main(String[] args) {
        IO.println("=== ドリンクを組み立てる ===");
        Drink latte = new Drink.Builder("ラテ", 450).build();
        Drink mocha = new Drink.Builder("モカ", 500)
                .size(Drink.SIZE_L)
                .shots(1)
                .milk(Drink.MILK_SOY)
                .addTopping("ホイップ")
                .addTopping("チョコソース")
                .iced(true)
                .build();
        IO.println("標準のまま: " + latte);
        IO.println("指定あり:   " + mocha);

        IO.println();
        IO.println("=== 一部だけ変えた1杯を作る ===");
        Drink smallMocha = mocha.toBuilder().size(Drink.SIZE_S).iced(false).build();
        IO.println("変えた1杯: " + smallMocha);
        IO.println("元の1杯:   " + mocha);

        IO.println();
        IO.println("=== ビルダーを使い回す ===");
        Drink.Builder builder = new Drink.Builder("抹茶ラテ", 480).addTopping("白玉");
        Drink first = builder.build();
        builder.addTopping("あずき");
        Drink second = builder.build();
        IO.println("1杯目: " + first);
        IO.println("2杯目: " + second);

        IO.println();
        IO.println("=== 受け取ったリストを書き換える ===");
        List<String> toppings = mocha.getToppings();
        toppings.clear();
        IO.println("受け取ったリスト: " + toppings + "、モカのトッピング: " + mocha.getToppings());

        IO.println();
        IO.println("=== 注文 ===");
        CafeOrder empty = new CafeOrder("佐藤");
        CafeOrder order = empty
                .withDrink(latte)
                .withDrink(mocha)
                .withDrink(new Drink.Builder("ラテ", 450).build());
        order.printReceipt();
        IO.println("  標準のラテ: " + order.countOf(latte) + "杯");
        IO.println("  最初の注文は空のまま: " + empty.getDrinks().size() + "杯");
    }
}
