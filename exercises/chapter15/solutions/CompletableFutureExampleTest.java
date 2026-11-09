import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import java.util.*;
import java.util.concurrent.*;

/**
 * CompletableFutureExampleクラスのテストクラス
 */
public class CompletableFutureExampleTest {
    
    private ExecutorService executor;
    private CompletableFutureExample.AsyncDataService dataService;
    private CompletableFutureExample.AsyncOperations asyncOps;
    
    @BeforeEach
    public void setUp() {
        executor = Executors.newFixedThreadPool(5);
        dataService = new CompletableFutureExample.AsyncDataService(executor);
        asyncOps = new CompletableFutureExample.AsyncOperations(dataService);
    }
    
    @AfterEach
    public void tearDown() {
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
    
    @Test
    public void testUserCreation() {
        CompletableFutureExample.User user = new CompletableFutureExample.User("123", "John", "john@example.com");
        
        assertEquals("123", user.getId());
        assertEquals("John", user.getName());
        assertEquals("john@example.com", user.getEmail());
        
        String userString = user.toString();
        assertTrue(userString.contains("123"));
        assertTrue(userString.contains("John"));
        assertTrue(userString.contains("john@example.com"));
    }
    
    @Test
    public void testOrderCreation() {
        List<String> items = Arrays.asList("item1", "item2");
        CompletableFutureExample.Order order = new CompletableFutureExample.Order("order1", "user1", 100.50, items);
        
        assertEquals("order1", order.getOrderId());
        assertEquals("user1", order.getUserId());
        assertEquals(100.50, order.getAmount(), 0.01);
        assertEquals(2, order.getItems().size());
        assertTrue(order.getItems().contains("item1"));
        assertTrue(order.getItems().contains("item2"));
        
        // リストの変更不可性をテスト
        List<String> retrievedItems = order.getItems();
        retrievedItems.add("item3"); // これは元のリストに影響しない
        assertEquals(2, order.getItems().size());
    }
    
    @Test
    public void testUserProfileCreation() {
        CompletableFutureExample.User user = new CompletableFutureExample.User("1", "User1", "user1@example.com");
        List<CompletableFutureExample.Order> orders = Arrays.asList(
            new CompletableFutureExample.Order("order1", "1", 100.0, Arrays.asList("item1"))
        );
        List<String> recommendations = Arrays.asList("rec1", "rec2");
        
        CompletableFutureExample.UserProfile profile = 
            new CompletableFutureExample.UserProfile(user, orders, recommendations);
        
        assertEquals(user, profile.getUser());
        assertEquals(1, profile.getOrderHistory().size());
        assertEquals(2, profile.getRecommendations().size());
        assertTrue(profile.getRecommendations().contains("rec1"));
        
        String profileString = profile.toString();
        assertTrue(profileString.contains("orders=1"));
        assertTrue(profileString.contains("recommendations=2"));
    }
    
    @Test
    public void testProductInfoCreation() {
        CompletableFutureExample.ProductInfo product = 
            new CompletableFutureExample.ProductInfo("item1", true, 29.99);
        
        assertEquals("item1", product.getItemId());
        assertTrue(product.isInStock());
        assertEquals(29.99, product.getPrice(), 0.01);
        
        String productString = product.toString();
        assertTrue(productString.contains("item1"));
        assertTrue(productString.contains("true"));
        assertTrue(productString.contains("29.99"));
    }
    
    @Test
    public void testAsyncUserRetrieval() throws Exception {
        CompletableFuture<CompletableFutureExample.User> userFuture = 
            dataService.getUserAsync("test-user");
        
        CompletableFutureExample.User user = userFuture.get(2, TimeUnit.SECONDS);
        
        assertNotNull(user);
        assertEquals("test-user", user.getId());
        assertTrue(user.getName().contains("User-test-user"));
        assertTrue(user.getEmail().contains("test-user@example.com"));
    }
    
    @Test
    public void testAsyncOrderHistoryRetrieval() throws Exception {
        CompletableFuture<List<CompletableFutureExample.Order>> ordersFuture = 
            dataService.getOrderHistoryAsync("test-user");
        
        List<CompletableFutureExample.Order> orders = ordersFuture.get(2, TimeUnit.SECONDS);
        
        assertNotNull(orders);
        assertTrue(orders.size() > 0);
        assertTrue(orders.size() <= 5); // ランダムで1-5個
        
        for (CompletableFutureExample.Order order : orders) {
            assertEquals("test-user", order.getUserId());
            assertTrue(order.getOrderId().contains("test-user"));
            assertTrue(order.getAmount() > 0);
        }
    }
    
    @Test
    public void testAsyncRecommendationsRetrieval() throws Exception {
        CompletableFuture<List<String>> recommendationsFuture = 
            dataService.getRecommendationsAsync("test-user");
        
        List<String> recommendations = recommendationsFuture.get(2, TimeUnit.SECONDS);
        
        assertNotNull(recommendations);
        assertTrue(recommendations.size() >= 2);
        assertTrue(recommendations.size() <= 4); // ランダムで2-4個
        
        for (String recommendation : recommendations) {
            assertTrue(recommendation.contains("test-user"));
        }
    }
    
    @Test
    public void testAsyncInventoryCheck() throws Exception {
        CompletableFuture<Boolean> inventoryFuture = 
            dataService.checkInventoryAsync("test-item");
        
        Boolean inStock = inventoryFuture.get(1, TimeUnit.SECONDS);
        
        assertNotNull(inStock);
        // ランダムなので結果の真偽値は問わない
    }
    
    @Test
    public void testAsyncPriceRetrieval() throws Exception {
        CompletableFuture<Double> priceFuture = 
            dataService.getPriceAsync("test-item");
        
        Double price = priceFuture.get(1, TimeUnit.SECONDS);
        
        assertNotNull(price);
        assertTrue(price >= 10.0);
        assertTrue(price <= 110.0);
    }
    
    @Test
    public void testGetUserProfileAsync() throws Exception {
        CompletableFuture<CompletableFutureExample.UserProfile> profileFuture = 
            asyncOps.getUserProfileAsync("test-user");
        
        CompletableFutureExample.UserProfile profile = profileFuture.get(3, TimeUnit.SECONDS);
        
        assertNotNull(profile);
        assertNotNull(profile.getUser());
        assertEquals("test-user", profile.getUser().getId());
        assertNotNull(profile.getOrderHistory());
        assertNotNull(profile.getRecommendations());
    }
    
    @Test
    public void testGetMultipleUserProfilesAsync() throws Exception {
        List<String> userIds = Arrays.asList("user1", "user2", "user3");
        CompletableFuture<List<CompletableFutureExample.UserProfile>> profilesFuture = 
            asyncOps.getMultipleUserProfilesAsync(userIds);
        
        List<CompletableFutureExample.UserProfile> profiles = profilesFuture.get(5, TimeUnit.SECONDS);
        
        assertNotNull(profiles);
        assertEquals(3, profiles.size());
        
        for (int i = 0; i < profiles.size(); i++) {
            CompletableFutureExample.UserProfile profile = profiles.get(i);
            assertNotNull(profile);
            assertEquals("user" + (i + 1), profile.getUser().getId());
        }
    }
    
    @Test
    public void testGetProductInfoAsync() throws Exception {
        CompletableFuture<CompletableFutureExample.ProductInfo> productFuture = 
            asyncOps.getProductInfoAsync("test-item");
        
        CompletableFutureExample.ProductInfo product = productFuture.get(2, TimeUnit.SECONDS);
        
        assertNotNull(product);
        assertEquals("test-item", product.getItemId());
        assertTrue(product.getPrice() > 0);
    }
    
    @Test
    public void testGetMultipleProductInfoAsync() throws Exception {
        List<String> itemIds = Arrays.asList("item1", "item2", "item3");
        CompletableFuture<List<CompletableFutureExample.ProductInfo>> productsFuture = 
            asyncOps.getMultipleProductInfoAsync(itemIds);
        
        List<CompletableFutureExample.ProductInfo> products = productsFuture.get(3, TimeUnit.SECONDS);
        
        assertNotNull(products);
        assertEquals(3, products.size());
        
        for (int i = 0; i < products.size(); i++) {
            CompletableFutureExample.ProductInfo product = products.get(i);
            assertNotNull(product);
            assertEquals("item" + (i + 1), product.getItemId());
        }
    }
    
    @Test
    public void testGetUserProfileWithFallbackAsync() throws Exception {
        // 正常ケース（エラーが発生しない場合）
        CompletableFuture<CompletableFutureExample.UserProfile> profileFuture = 
            asyncOps.getUserProfileWithFallbackAsync("normal-user");
        
        CompletableFutureExample.UserProfile profile = profileFuture.get(3, TimeUnit.SECONDS);
        
        assertNotNull(profile);
        assertNotNull(profile.getUser());
        
        // フォールバックがあるので、例外は発生しない
        // （実際のエラー発生は確率的なので、複数回試行する必要があるが、テストでは省略）
    }
    
    @Test
    public void testGetUserProfileWithTimeoutAsync() throws Exception {
        // 短いタイムアウトでテスト
        CompletableFuture<CompletableFutureExample.UserProfile> profileFuture = 
            asyncOps.getUserProfileWithTimeoutAsync("timeout-user", 1); // 1ms
        
        CompletableFutureExample.UserProfile profile = profileFuture.get(2, TimeUnit.SECONDS);
        
        assertNotNull(profile);
        // タイムアウト時のフォールバック値かどうかを確認
        assertTrue(profile.getUser().getName().equals("Timeout") || 
                  profile.getUser().getName().contains("User-timeout-user"));
    }
    
    @Test
    public void testGetUserProfileSequentialAsync() throws Exception {
        CompletableFuture<CompletableFutureExample.UserProfile> profileFuture = 
            asyncOps.getUserProfileSequentialAsync("sequential-user");
        
        CompletableFutureExample.UserProfile profile = profileFuture.get(5, TimeUnit.SECONDS);
        
        assertNotNull(profile);
        assertNotNull(profile.getUser());
        assertEquals("sequential-user", profile.getUser().getId());
    }
    
    @Test
    public void testGetRecommendationMessageAsync() throws Exception {
        // 通常ユーザーのテスト
        CompletableFuture<String> messageFuture = 
            asyncOps.getRecommendationMessageAsync("normal-user");
        
        String message = messageFuture.get(3, TimeUnit.SECONDS);
        
        assertNotNull(message);
        assertTrue(message.contains("おすすめ商品") || message.contains("VIP"));
    }
    
    @Test
    public void testGetFirstAvailableRecommendationAsync() throws Exception {
        List<String> userIds = Arrays.asList("user1", "user2", "user3");
        CompletableFuture<String> firstFuture = 
            asyncOps.getFirstAvailableRecommendationAsync(userIds);
        
        String firstResult = firstFuture.get(3, TimeUnit.SECONDS);
        
        assertNotNull(firstResult);
        assertTrue(firstResult.contains("user"));
        assertTrue(firstResult.contains("recommended-item"));
    }
    
    @Test
    public void testCompletableFutureExceptionHandling() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            throw new RuntimeException("Test exception");
        });
        
        CompletableFuture<String> handled = future.exceptionally(throwable -> {
            return "Handled: " + throwable.getMessage();
        });
        
        assertDoesNotThrow(() -> {
            String result = handled.get(1, TimeUnit.SECONDS);
            assertTrue(result.contains("Handled"));
            assertTrue(result.contains("Test exception"));
        });
    }
    
    @Test
    public void testCompletableFutureCombining() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "World");
        
        CompletableFuture<String> combined = future1.thenCombine(future2, 
            (s1, s2) -> s1 + " " + s2);
        
        String result = combined.get(1, TimeUnit.SECONDS);
        assertEquals("Hello World", result);
    }
    
    @Test
    public void testCompletableFutureChaining() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "initial")
            .thenApply(s -> s.toUpperCase())
            .thenApply(s -> s + "_PROCESSED");
        
        String result = future.get(1, TimeUnit.SECONDS);
        assertEquals("INITIAL_PROCESSED", result);
    }
    
    @Test
    public void testAsyncOperationsPerformance() throws Exception {
        long start = System.currentTimeMillis();
        
        // 複数の非同期操作を並列実行
        List<CompletableFuture<CompletableFutureExample.ProductInfo>> futures = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            futures.add(asyncOps.getProductInfoAsync("item" + i));
        }
        
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0]));
        
        allFutures.get(3, TimeUnit.SECONDS);
        
        long duration = System.currentTimeMillis() - start;
        
        // 並列実行により、1秒以下で完了することを期待
        // （各操作は最大500ms程度だが、並列実行により短縮される）
        assertTrue(duration < 2000, "並列実行により処理時間が短縮されるはず: " + duration + "ms");
    }
}