import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * CompletableFutureによる効率的な非同期処理
 * 高度な非同期プログラミングパターンを実装
 */
public class CompletableFutureExample {
    
    private static final Logger logger = Logger.getLogger(CompletableFutureExample.class.getName());
    
    /**
     * ユーザー情報を表すクラス
     */
    public static class User {
        private final String id;
        private final String name;
        private final String email;
        
        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        
        @Override
        public String toString() {
            return String.format("User[id=%s, name=%s, email=%s]", id, name, email);
        }
    }
    
    /**
     * 注文情報を表すクラス
     */
    public static class Order {
        private final String orderId;
        private final String userId;
        private final double amount;
        private final List<String> items;
        
        public Order(String orderId, String userId, double amount, List<String> items) {
            this.orderId = orderId;
            this.userId = userId;
            this.amount = amount;
            this.items = new ArrayList<>(items);
        }
        
        public String getOrderId() { return orderId; }
        public String getUserId() { return userId; }
        public double getAmount() { return amount; }
        public List<String> getItems() { return new ArrayList<>(items); }
        
        @Override
        public String toString() {
            return String.format("Order[id=%s, userId=%s, amount=%.2f, items=%s]", 
                orderId, userId, amount, items);
        }
    }
    
    /**
     * 非同期データサービス（外部API呼び出しをシミュレート）
     */
    public static class AsyncDataService {
        private final ExecutorService executor;
        private final Random random = new Random();
        
        public AsyncDataService(ExecutorService executor) {
            this.executor = executor;
        }
        
        /**
         * ユーザー情報を非同期で取得
         */
        public CompletableFuture<User> getUserAsync(String userId) {
            return CompletableFuture.supplyAsync(() -> {
                simulateDelay(100, 300);
                
                if (random.nextDouble() < 0.1) { // 10%の確率で失敗
                    throw new RuntimeException("ユーザー取得エラー: " + userId);
                }
                
                return new User(userId, "User-" + userId, userId + "@example.com");
            }, executor);
        }
        
        /**
         * 注文履歴を非同期で取得
         */
        public CompletableFuture<List<Order>> getOrderHistoryAsync(String userId) {
            return CompletableFuture.supplyAsync(() -> {
                simulateDelay(200, 500);
                
                if (random.nextDouble() < 0.05) { // 5%の確率で失敗
                    throw new RuntimeException("注文履歴取得エラー: " + userId);
                }
                
                List<Order> orders = new ArrayList<>();
                int orderCount = random.nextInt(5) + 1;
                for (int i = 0; i < orderCount; i++) {
                    orders.add(new Order(
                        "order-" + userId + "-" + i,
                        userId,
                        random.nextDouble() * 1000,
                        Arrays.asList("item" + i, "item" + (i + 1))
                    ));
                }
                return orders;
            }, executor);
        }
        
        /**
         * 推奨商品を非同期で取得
         */
        public CompletableFuture<List<String>> getRecommendationsAsync(String userId) {
            return CompletableFuture.supplyAsync(() -> {
                simulateDelay(150, 400);
                
                List<String> recommendations = new ArrayList<>();
                int count = random.nextInt(3) + 2;
                for (int i = 0; i < count; i++) {
                    recommendations.add("recommended-item-" + i + "-for-" + userId);
                }
                return recommendations;
            }, executor);
        }
        
        /**
         * 在庫情報を非同期で確認
         */
        public CompletableFuture<Boolean> checkInventoryAsync(String itemId) {
            return CompletableFuture.supplyAsync(() -> {
                simulateDelay(50, 150);
                return random.nextBoolean(); // 50%の確率で在庫あり
            }, executor);
        }
        
        /**
         * 価格情報を非同期で取得
         */
        public CompletableFuture<Double> getPriceAsync(String itemId) {
            return CompletableFuture.supplyAsync(() -> {
                simulateDelay(80, 200);
                return random.nextDouble() * 100 + 10; // 10-110の価格
            }, executor);
        }
        
        private void simulateDelay(int minMs, int maxMs) {
            try {
                int delay = random.nextInt(maxMs - minMs) + minMs;
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("処理が中断されました", e);
            }
        }
    }
    
    /**
     * 複合的なユーザー情報を表すクラス
     */
    public static class UserProfile {
        private final User user;
        private final List<Order> orderHistory;
        private final List<String> recommendations;
        
        public UserProfile(User user, List<Order> orderHistory, List<String> recommendations) {
            this.user = user;
            this.orderHistory = new ArrayList<>(orderHistory);
            this.recommendations = new ArrayList<>(recommendations);
        }
        
        public User getUser() { return user; }
        public List<Order> getOrderHistory() { return new ArrayList<>(orderHistory); }
        public List<String> getRecommendations() { return new ArrayList<>(recommendations); }
        
        @Override
        public String toString() {
            return String.format("UserProfile[user=%s, orders=%d, recommendations=%d]", 
                user, orderHistory.size(), recommendations.size());
        }
    }
    
    /**
     * 商品情報を表すクラス
     */
    public static class ProductInfo {
        private final String itemId;
        private final boolean inStock;
        private final double price;
        
        public ProductInfo(String itemId, boolean inStock, double price) {
            this.itemId = itemId;
            this.inStock = inStock;
            this.price = price;
        }
        
        public String getItemId() { return itemId; }
        public boolean isInStock() { return inStock; }
        public double getPrice() { return price; }
        
        @Override
        public String toString() {
            return String.format("ProductInfo[id=%s, inStock=%s, price=%.2f]", 
                itemId, inStock, price);
        }
    }
    
    /**
     * 非同期処理のサンプル実装
     */
    public static class AsyncOperations {
        private final AsyncDataService dataService;
        
        public AsyncOperations(AsyncDataService dataService) {
            this.dataService = dataService;
        }
        
        /**
         * ユーザープロフィールを非同期で取得（複数のAPIを並列実行）
         */
        public CompletableFuture<UserProfile> getUserProfileAsync(String userId) {
            CompletableFuture<User> userFuture = dataService.getUserAsync(userId);
            CompletableFuture<List<Order>> ordersFuture = dataService.getOrderHistoryAsync(userId);
            CompletableFuture<List<String>> recommendationsFuture = dataService.getRecommendationsAsync(userId);
            
            return CompletableFuture.allOf(userFuture, ordersFuture, recommendationsFuture)
                .thenApply(v -> new UserProfile(
                    userFuture.join(),
                    ordersFuture.join(),
                    recommendationsFuture.join()
                ));
        }
        
        /**
         * 複数ユーザーのプロフィールを並列取得
         */
        public CompletableFuture<List<UserProfile>> getMultipleUserProfilesAsync(List<String> userIds) {
            List<CompletableFuture<UserProfile>> futures = userIds.stream()
                .map(this::getUserProfileAsync)
                .collect(Collectors.toList());
            
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
        }
        
        /**
         * 商品情報を非同期で取得（在庫と価格を並列取得）
         */
        public CompletableFuture<ProductInfo> getProductInfoAsync(String itemId) {
            CompletableFuture<Boolean> inventoryFuture = dataService.checkInventoryAsync(itemId);
            CompletableFuture<Double> priceFuture = dataService.getPriceAsync(itemId);
            
            return inventoryFuture.thenCombine(priceFuture, 
                (inStock, price) -> new ProductInfo(itemId, inStock, price));
        }
        
        /**
         * 複数商品の情報を並列取得
         */
        public CompletableFuture<List<ProductInfo>> getMultipleProductInfoAsync(List<String> itemIds) {
            List<CompletableFuture<ProductInfo>> futures = itemIds.stream()
                .map(this::getProductInfoAsync)
                .collect(Collectors.toList());
            
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList()));
        }
        
        /**
         * エラーハンドリングを含む非同期処理
         */
        public CompletableFuture<UserProfile> getUserProfileWithFallbackAsync(String userId) {
            return getUserProfileAsync(userId)
                .exceptionally(throwable -> {
                    logger.log(Level.WARNING, "ユーザープロフィール取得エラー: " + userId, throwable);
                    // フォールバック値を返す
                    return new UserProfile(
                        new User(userId, "Unknown", "unknown@example.com"),
                        Collections.emptyList(),
                        Collections.emptyList()
                    );
                });
        }
        
        /**
         * タイムアウト付き非同期処理
         */
        public CompletableFuture<UserProfile> getUserProfileWithTimeoutAsync(String userId, long timeoutMs) {
            return getUserProfileAsync(userId)
                .completeOnTimeout(
                    new UserProfile(
                        new User(userId, "Timeout", "timeout@example.com"),
                        Collections.emptyList(),
                        Collections.emptyList()
                    ),
                    timeoutMs,
                    TimeUnit.MILLISECONDS
                );
        }
        
        /**
         * 段階的な非同期処理（ユーザー取得 → 注文履歴取得 → 推奨商品取得）
         */
        public CompletableFuture<UserProfile> getUserProfileSequentialAsync(String userId) {
            return dataService.getUserAsync(userId)
                .thenCompose(user -> 
                    dataService.getOrderHistoryAsync(userId)
                        .thenCompose(orders -> 
                            dataService.getRecommendationsAsync(userId)
                                .thenApply(recommendations -> 
                                    new UserProfile(user, orders, recommendations)
                                )
                        )
                );
        }
        
        /**
         * 条件分岐を含む非同期処理
         */
        public CompletableFuture<String> getRecommendationMessageAsync(String userId) {
            return dataService.getUserAsync(userId)
                .thenCompose(user -> {
                    if (user.getName().startsWith("VIP")) {
                        return CompletableFuture.completedFuture("VIP向け特別オファーがあります！");
                    } else {
                        return dataService.getRecommendationsAsync(userId)
                            .thenApply(recommendations -> 
                                "おすすめ商品: " + String.join(", ", recommendations));
                    }
                });
        }
        
        /**
         * 複数の非同期処理の最初の完了を待つ
         */
        public CompletableFuture<String> getFirstAvailableRecommendationAsync(List<String> userIds) {
            List<CompletableFuture<String>> futures = userIds.stream()
                .map(userId -> dataService.getRecommendationsAsync(userId)
                    .thenApply(recommendations -> 
                        userId + ": " + String.join(", ", recommendations)))
                .collect(Collectors.toList());
            
            return CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(result -> (String) result);
        }
    }
    
    /**
     * パフォーマンス測定クラス
     */
    public static class PerformanceBenchmark {
        
        public static void compareAsyncVsSync(AsyncDataService dataService) {
            System.out.println("=== 非同期 vs 同期処理のパフォーマンス比較 ===");
            
            List<String> userIds = Arrays.asList("user1", "user2", "user3", "user4", "user5");
            AsyncOperations asyncOps = new AsyncOperations(dataService);
            
            // 同期的な処理
            long syncStart = System.currentTimeMillis();
            List<UserProfile> syncResults = new ArrayList<>();
            for (String userId : userIds) {
                try {
                    UserProfile profile = asyncOps.getUserProfileAsync(userId).get();
                    syncResults.add(profile);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "同期処理エラー", e);
                }
            }
            long syncTime = System.currentTimeMillis() - syncStart;
            
            // 非同期処理
            long asyncStart = System.currentTimeMillis();
            try {
                List<UserProfile> asyncResults = asyncOps.getMultipleUserProfilesAsync(userIds).get();
                long asyncTime = System.currentTimeMillis() - asyncStart;
                
                System.out.println("同期処理時間: " + syncTime + "ms (" + syncResults.size() + " 件)");
                System.out.println("非同期処理時間: " + asyncTime + "ms (" + asyncResults.size() + " 件)");
                System.out.println("性能向上: " + String.format("%.1fx", (double) syncTime / asyncTime));
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "非同期処理エラー", e);
            }
        }
    }
    
    /**
     * デモ用メイン関数
     */
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        AsyncDataService dataService = new AsyncDataService(executor);
        AsyncOperations asyncOps = new AsyncOperations(dataService);
        
        try {
            // 基本的な非同期処理のデモ
            System.out.println("=== 基本的な非同期処理 ===");
            CompletableFuture<UserProfile> profileFuture = asyncOps.getUserProfileAsync("demo-user");
            UserProfile profile = profileFuture.get(2, TimeUnit.SECONDS);
            System.out.println("取得したプロフィール: " + profile);
            System.out.println();
            
            // 複数商品情報の並列取得
            System.out.println("=== 複数商品情報の並列取得 ===");
            List<String> itemIds = Arrays.asList("item1", "item2", "item3");
            CompletableFuture<List<ProductInfo>> productsFuture = asyncOps.getMultipleProductInfoAsync(itemIds);
            List<ProductInfo> products = productsFuture.get(3, TimeUnit.SECONDS);
            products.forEach(System.out::println);
            System.out.println();
            
            // エラーハンドリング
            System.out.println("=== エラーハンドリング ===");
            CompletableFuture<UserProfile> fallbackFuture = asyncOps.getUserProfileWithFallbackAsync("error-user");
            UserProfile fallbackProfile = fallbackFuture.get(2, TimeUnit.SECONDS);
            System.out.println("フォールバック結果: " + fallbackProfile);
            System.out.println();
            
            // パフォーマンス比較
            PerformanceBenchmark.compareAsyncVsSync(dataService);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "デモ実行エラー", e);
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}