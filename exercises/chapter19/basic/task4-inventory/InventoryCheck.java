import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Inventoryの動作確認
 *
 * 画面の代わりに、受け取ったイベントを記録するだけのリスナーを登録して確かめます。
 */
public class InventoryCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== Inventoryの動作確認 ===");
        Inventory inventory = new Inventory();
        inventory.register("ノート", 10);
        inventory.register("ボールペン", 5);

        List<String> received = new ArrayList<>();
        StockListener recorder = e ->
                received.add(e.getItem() + ":" + e.getOldQuantity() + "→" + e.getNewQuantity());
        inventory.addStockListener(recorder);
        List<Integer> changes = new ArrayList<>();
        inventory.addStockListener(e -> changes.add(e.getChange()));

        inventory.receive("ノート", 3);
        check("入荷すると数が増える", 13, inventory.getQuantity("ノート"));
        check("入荷を通知する", List.of("ノート:10→13"), received);
        check("2つ目のリスナーにも届く", List.of(3), changes);

        check("在庫の範囲なら出荷できる", true, inventory.ship("ボールペン", 4));
        check("出荷を通知する（変化は負の数）", List.of(3, -4), changes);
        check("在庫を超える出荷はできない", false, inventory.ship("ボールペン", 2));
        check("出荷できないときは通知しない", 2, received.size());
        check("登録していない商品は入荷しない", 0, inventory.getQuantity("消しゴム"));

        inventory.removeStockListener(recorder);
        inventory.receive("ボールペン", 1);
        check("外したリスナーには届かない", 2, received.size());
        check("外していないリスナーには届く", 3, changes.size());
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (Objects.equals(expected, actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
