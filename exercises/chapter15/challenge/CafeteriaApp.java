import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class CafeteriaApp {
    private static final int COOKS = 3;
    private static final int STOVES = 2;
    private static final int QUEUE_CAPACITY = 4;

    public static void main(String[] args) throws InterruptedException {
        IO.println("=== 学食の注文処理 ===");
        IO.println("コック" + COOKS + "人、コンロ" + STOVES + "台、注文の待ち行列は" + QUEUE_CAPACITY + "件まで");
        Kitchen kitchen = new Kitchen(COOKS, STOVES, QUEUE_CAPACITY);

        // 2つの窓口が同時に注文を受ける
        OrderWindow windowA = new OrderWindow("A", kitchen,
                new String[] {"佐藤", "鈴木", "高橋", "田中", "伊藤", "渡辺"},
                new Menu[] {Menu.RAMEN, Menu.CURRY, Menu.UDON, Menu.RAMEN, Menu.SET_MEAL, Menu.UDON});
        OrderWindow windowB = new OrderWindow("B", kitchen,
                new String[] {"山本", "中村", "小林", "加藤", "吉田", "山田"},
                new Menu[] {Menu.UDON, Menu.RAMEN, Menu.CURRY, Menu.SET_MEAL, Menu.RAMEN, Menu.CURRY});
        Thread threadA = new Thread(windowA, "窓口A");
        Thread threadB = new Thread(windowB, "窓口B");
        threadA.start();
        threadB.start();
        threadA.join();
        threadB.join();

        // すべての注文ができあがるのを待つ
        List<CompletableFuture<Order>> all = new ArrayList<>(windowA.getFutures());
        all.addAll(windowB.getFutures());
        CompletableFuture.allOf(all.toArray(new CompletableFuture<?>[0])).join();

        IO.println("");
        IO.println("=== できあがった注文 ===");
        printReceipts(windowA);
        printReceipts(windowB);

        // 受付を終えてから来た客の注文は断られる
        kitchen.close();
        IO.println("");
        IO.println("=== 受付終了後 ===");
        try {
            kitchen.placeOrder(new Order("A7", "松本", Menu.CURRY));
            IO.println("  A7 松本: 受け付けました");
        } catch (IllegalStateException e) {
            IO.println("  A7 松本: " + e.getMessage());
        }

        printSales(kitchen);
    }

    private static void printReceipts(OrderWindow window) {
        for (CompletableFuture<Order> future : window.getFutures()) {
            Order order = future.join();
            IO.println("  %s %s %s".formatted(order.ticket(), order.customer(), order.menu().getLabel()));
        }
    }

    private static void printSales(Kitchen kitchen) {
        IO.println("");
        IO.println("=== 売上 ===");
        int count = 0;
        int total = 0;
        for (Menu menu : Menu.values()) {
            int sold = kitchen.getSales(menu);
            IO.println("  %s: %d食 %,d円".formatted(menu.getLabel(), sold, sold * menu.getPrice()));
            count += sold;
            total += sold * menu.getPrice();
        }
        IO.println("  合計: %d食 %,d円".formatted(count, total));

        IO.println("");
        IO.println("=== 確認 ===");
        IO.println("  受け付けた注文: " + kitchen.getAccepted() + "件、作り終えた注文: " + kitchen.getFinished() + "件");
        IO.println("  同時に使ったコンロは" + STOVES + "台以内: "
                + (kitchen.getMaxStovesInUse() <= STOVES ? "はい" : "いいえ"));
    }
}
