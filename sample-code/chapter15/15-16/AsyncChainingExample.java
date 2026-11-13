import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsyncChainingExample {
    
    // ユーザー情報を取得
    static CompletableFuture<User> fetchUser(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            IO.println("Fetching user: " + userId);
            // データベースアクセスをシミュレート
            sleep(1000);
            return new User(userId, "User-" + userId);
        });
    }
    
    // ユーザーの注文履歴を取得
    static CompletableFuture<List<Order>> fetchOrders(User user) {
        return CompletableFuture.supplyAsync(() -> {
            IO.println("Fetching orders for: " + user.getName());
            sleep(1500);
            return List.of(
                new Order("Order-1", 1000),
                new Order("Order-2", 2000)
            );
        });
    }
    
    // 注文の合計金額を計算
    static int calculateTotal(List<Order> orders) {
        IO.println("Calculating total...");
        return orders.stream()
            .mapToInt(Order::getAmount)
            .sum();
    }
    
    public static void main(String[] args) throws Exception {
        // 非同期処理を連鎖させる
        CompletableFuture<Integer> totalFuture = fetchUser("USER001")
            .thenCompose(user -> fetchOrders(user))  // 別の非同期処理に連鎖
            .thenApply(orders -> calculateTotal(orders))  // 前の処理を終えたスレッドで続けて変換
            .exceptionally(ex -> {
                System.err.println("Error occurred: " + ex.getMessage());
                return 0;  // デフォルト値を返す
            });
        
        IO.println("Total amount: " + totalFuture.get());
    }
    
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
