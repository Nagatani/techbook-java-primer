import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 注文を受ける窓口。窓口ごとに1つのスレッドで実行するタスク
 */
public class OrderWindow implements Runnable {
    private final String prefix;
    private final Kitchen kitchen;
    private final String[] customers;
    private final Menu[] menus;
    private final List<CompletableFuture<Order>> futures = new ArrayList<>();

    public OrderWindow(String prefix, Kitchen kitchen, String[] customers, Menu[] menus) {
        this.prefix = prefix;
        this.kitchen = kitchen;
        this.customers = customers;
        this.menus = menus;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < customers.length; i++) {
                Order order = new Order(prefix + (i + 1), customers[i], menus[i]);
                futures.add(kitchen.placeOrder(order));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /** このタスクを実行したスレッドの終わりをjoin()で待ってから呼ぶ */
    public List<CompletableFuture<Order>> getFutures() {
        return futures;
    }
}
