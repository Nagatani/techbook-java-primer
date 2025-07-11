package com.example.testing;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * テストピラミッドの概念と各レイヤーのテスト戦略をデモンストレーション
 * 単体テスト、統合テスト、E2Eテストの役割分担を学習
 */
public class TestPyramidDemo {
    
    /**
     * テスト対象のビジネスロジッククラス群
     */
    
    // エンティティクラス
    static class Order {
        private String id;
        private String customerId;
        private List<OrderItem> items;
        private OrderStatus status;
        private double totalAmount;
        
        public Order(String id, String customerId) {
            this.id = id;
            this.customerId = customerId;
            this.items = new ArrayList<>();
            this.status = OrderStatus.DRAFT;
        }
        
        public void addItem(OrderItem item) {
            items.add(item);
            recalculateTotal();
        }
        
        public void removeItem(String productId) {
            items.removeIf(item -> item.getProductId().equals(productId));
            recalculateTotal();
        }
        
        private void recalculateTotal() {
            totalAmount = items.stream()
                    .mapToDouble(OrderItem::getSubtotal)
                    .sum();
        }
        
        public void confirm() {
            if (items.isEmpty()) {
                throw new IllegalStateException("Cannot confirm empty order");
            }
            if (status != OrderStatus.DRAFT) {
                throw new IllegalStateException("Order already processed");
            }
            status = OrderStatus.CONFIRMED;
        }
        
        // Getters
        public String getId() { return id; }
        public String getCustomerId() { return customerId; }
        public List<OrderItem> getItems() { return new ArrayList<>(items); }
        public OrderStatus getStatus() { return status; }
        public double getTotalAmount() { return totalAmount; }
        
        void setStatus(OrderStatus status) { this.status = status; }
    }
    
    static class OrderItem {
        private String productId;
        private String productName;
        private int quantity;
        private double unitPrice;
        
        public OrderItem(String productId, String productName, int quantity, double unitPrice) {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
            
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
        
        public double getSubtotal() {
            return quantity * unitPrice;
        }
        
        // Getters
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
    }
    
    enum OrderStatus {
        DRAFT, CONFIRMED, PAID, SHIPPED, DELIVERED, CANCELLED
    }
    
    // リポジトリインターフェイス
    interface OrderRepository {
        void save(Order order);
        Order findById(String id);
        List<Order> findByCustomerId(String customerId);
        void delete(String id);
    }
    
    // 外部サービスインターフェイス
    interface PaymentService {
        PaymentResult processPayment(String customerId, double amount);
    }
    
    interface InventoryService {
        boolean isAvailable(String productId, int quantity);
        void reserveItems(List<OrderItem> items);
        void releaseReservation(String orderId);
    }
    
    interface EmailService {
        void sendOrderConfirmation(String customerId, Order order);
    }
    
    static class PaymentResult {
        private final boolean success;
        private final String transactionId;
        private final String errorMessage;
        
        public PaymentResult(boolean success, String transactionId, String errorMessage) {
            this.success = success;
            this.transactionId = transactionId;
            this.errorMessage = errorMessage;
        }
        
        public boolean isSuccess() { return success; }
        public String getTransactionId() { return transactionId; }
        public String getErrorMessage() { return errorMessage; }
        
        public static PaymentResult success(String transactionId) {
            return new PaymentResult(true, transactionId, null);
        }
        
        public static PaymentResult failure(String errorMessage) {
            return new PaymentResult(false, null, errorMessage);
        }
    }
    
    // ビジネスロジック層
    static class OrderService {
        private final OrderRepository orderRepository;
        private final PaymentService paymentService;
        private final InventoryService inventoryService;
        private final EmailService emailService;
        
        public OrderService(OrderRepository orderRepository,
                          PaymentService paymentService,
                          InventoryService inventoryService,
                          EmailService emailService) {
            this.orderRepository = orderRepository;
            this.paymentService = paymentService;
            this.inventoryService = inventoryService;
            this.emailService = emailService;
        }
        
        public Order createOrder(String customerId) {
            String orderId = generateOrderId();
            Order order = new Order(orderId, customerId);
            orderRepository.save(order);
            return order;
        }
        
        public void addItemToOrder(String orderId, String productId, String productName, 
                                 int quantity, double unitPrice) {
            Order order = orderRepository.findById(orderId);
            if (order == null) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }
            
            if (!inventoryService.isAvailable(productId, quantity)) {
                throw new IllegalStateException("Insufficient inventory for product: " + productId);
            }
            
            OrderItem item = new OrderItem(productId, productName, quantity, unitPrice);
            order.addItem(item);
            orderRepository.save(order);
        }
        
        public Order processOrder(String orderId) {
            Order order = orderRepository.findById(orderId);
            if (order == null) {
                throw new IllegalArgumentException("Order not found: " + orderId);
            }
            
            try {
                // 在庫確保
                inventoryService.reserveItems(order.getItems());
                
                // 決済処理
                PaymentResult paymentResult = paymentService.processPayment(
                    order.getCustomerId(), order.getTotalAmount());
                
                if (!paymentResult.isSuccess()) {
                    inventoryService.releaseReservation(orderId);
                    throw new PaymentFailedException("Payment failed: " + paymentResult.getErrorMessage());
                }
                
                // 注文確定
                order.confirm();
                order.setStatus(OrderStatus.PAID);
                orderRepository.save(order);
                
                // 確認メール送信
                emailService.sendOrderConfirmation(order.getCustomerId(), order);
                
                return order;
                
            } catch (Exception e) {
                // 失敗時のクリーンアップ
                inventoryService.releaseReservation(orderId);
                throw e;
            }
        }
        
        public List<Order> getOrderHistory(String customerId) {
            return orderRepository.findByCustomerId(customerId);
        }
        
        private String generateOrderId() {
            return "ORD-" + System.currentTimeMillis() + "-" + 
                   ThreadLocalRandom.current().nextInt(1000, 9999);
        }
    }
    
    static class PaymentFailedException extends RuntimeException {
        public PaymentFailedException(String message) {
            super(message);
        }
    }
    
    // テスト用のモック実装
    static class InMemoryOrderRepository implements OrderRepository {
        private final Map<String, Order> orders = new HashMap<>();
        
        @Override
        public void save(Order order) {
            orders.put(order.getId(), order);
        }
        
        @Override
        public Order findById(String id) {
            return orders.get(id);
        }
        
        @Override
        public List<Order> findByCustomerId(String customerId) {
            return orders.values().stream()
                    .filter(order -> order.getCustomerId().equals(customerId))
                    .toList();
        }
        
        @Override
        public void delete(String id) {
            orders.remove(id);
        }
        
        public void clear() {
            orders.clear();
        }
    }
    
    static class MockPaymentService implements PaymentService {
        private boolean shouldSucceed = true;
        private String errorMessage = "";
        
        @Override
        public PaymentResult processPayment(String customerId, double amount) {
            if (shouldSucceed) {
                return PaymentResult.success("TXN-" + System.currentTimeMillis());
            } else {
                return PaymentResult.failure(errorMessage);
            }
        }
        
        public void setFailureMode(String errorMessage) {
            this.shouldSucceed = false;
            this.errorMessage = errorMessage;
        }
        
        public void setSuccessMode() {
            this.shouldSucceed = true;
            this.errorMessage = "";
        }
    }
    
    static class MockInventoryService implements InventoryService {
        private final Map<String, Integer> inventory = new HashMap<>();
        private final Set<String> reservations = new HashSet<>();
        
        @Override
        public boolean isAvailable(String productId, int quantity) {
            return inventory.getOrDefault(productId, 0) >= quantity;
        }
        
        @Override
        public void reserveItems(List<OrderItem> items) {
            for (OrderItem item : items) {
                if (!isAvailable(item.getProductId(), item.getQuantity())) {
                    throw new IllegalStateException("Insufficient inventory: " + item.getProductId());
                }
            }
            // 予約処理（簡略化）
            for (OrderItem item : items) {
                reservations.add(item.getProductId());
            }
        }
        
        @Override
        public void releaseReservation(String orderId) {
            // 予約解除（簡略化）
            reservations.clear();
        }
        
        public void setInventory(String productId, int quantity) {
            inventory.put(productId, quantity);
        }
    }
    
    static class MockEmailService implements EmailService {
        private final List<String> sentEmails = new ArrayList<>();
        
        @Override
        public void sendOrderConfirmation(String customerId, Order order) {
            String email = "Order confirmation for " + order.getId() + " sent to " + customerId;
            sentEmails.add(email);
            System.out.println("Email sent: " + email);
        }
        
        public List<String> getSentEmails() {
            return new ArrayList<>(sentEmails);
        }
        
        public void clear() {
            sentEmails.clear();
        }
    }
    
    /**
     * 単体テストレベル（テストピラミッドの基盤）
     */
    public static class UnitTestLevel {
        
        /**
         * エンティティクラスの単体テスト例
         */
        public static void testOrderEntity() {
            System.out.println("=== Unit Test Level: Order Entity ===");
            
            // テスト1: 正常なケース
            System.out.println("Test 1: Normal order creation and item addition");
            Order order = new Order("ORD-001", "CUST-001");
            
            assertEquals("ORD-001", order.getId());
            assertEquals("CUST-001", order.getCustomerId());
            assertEquals(OrderStatus.DRAFT, order.getStatus());
            assertEquals(0.0, order.getTotalAmount());
            assertTrue(order.getItems().isEmpty());
            
            // アイテム追加
            OrderItem item1 = new OrderItem("PROD-001", "Product 1", 2, 10.0);
            OrderItem item2 = new OrderItem("PROD-002", "Product 2", 1, 15.0);
            
            order.addItem(item1);
            assertEquals(20.0, order.getTotalAmount());
            assertEquals(1, order.getItems().size());
            
            order.addItem(item2);
            assertEquals(35.0, order.getTotalAmount());
            assertEquals(2, order.getItems().size());
            
            System.out.println("✓ Order total correctly calculated: " + order.getTotalAmount());
            
            // テスト2: 境界値テスト
            System.out.println("\nTest 2: Edge cases");
            
            try {
                new OrderItem("PROD-003", "Product 3", 0, 10.0); // 数量0
                System.out.println("✗ Should have thrown exception for quantity 0");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Correctly rejected quantity 0: " + e.getMessage());
            }
            
            try {
                new OrderItem("PROD-004", "Product 4", 1, -5.0); // 負の価格
                System.out.println("✗ Should have thrown exception for negative price");
            } catch (IllegalArgumentException e) {
                System.out.println("✓ Correctly rejected negative price: " + e.getMessage());
            }
            
            // テスト3: 状態遷移テスト
            System.out.println("\nTest 3: State transition");
            
            try {
                Order emptyOrder = new Order("ORD-002", "CUST-002");
                emptyOrder.confirm(); // 空の注文を確定
                System.out.println("✗ Should have thrown exception for empty order confirmation");
            } catch (IllegalStateException e) {
                System.out.println("✓ Correctly rejected empty order confirmation: " + e.getMessage());
            }
            
            order.confirm();
            assertEquals(OrderStatus.CONFIRMED, order.getStatus());
            System.out.println("✓ Order status correctly updated to CONFIRMED");
        }
        
        /**
         * ビジネスロジックの単体テスト例
         */
        public static void testOrderServiceUnit() {
            System.out.println("\n=== Unit Test Level: Order Service (with mocks) ===");
            
            // モックの準備
            InMemoryOrderRepository mockRepo = new InMemoryOrderRepository();
            MockPaymentService mockPayment = new MockPaymentService();
            MockInventoryService mockInventory = new MockInventoryService();
            MockEmailService mockEmail = new MockEmailService();
            
            OrderService orderService = new OrderService(mockRepo, mockPayment, mockInventory, mockEmail);
            
            // テスト1: 注文作成
            System.out.println("\nTest 1: Order creation");
            Order order = orderService.createOrder("CUST-001");
            
            assertNotNull(order);
            assertNotNull(order.getId());
            assertEquals("CUST-001", order.getCustomerId());
            assertEquals(OrderStatus.DRAFT, order.getStatus());
            
            // リポジトリに保存されていることを確認
            Order savedOrder = mockRepo.findById(order.getId());
            assertNotNull(savedOrder);
            assertEquals(order.getId(), savedOrder.getId());
            
            System.out.println("✓ Order created and saved: " + order.getId());
            
            // テスト2: アイテム追加（正常ケース）
            System.out.println("\nTest 2: Add item to order (success case)");
            mockInventory.setInventory("PROD-001", 10);
            
            orderService.addItemToOrder(order.getId(), "PROD-001", "Product 1", 2, 10.0);
            
            Order updatedOrder = mockRepo.findById(order.getId());
            assertEquals(1, updatedOrder.getItems().size());
            assertEquals(20.0, updatedOrder.getTotalAmount());
            
            System.out.println("✓ Item added successfully, total: " + updatedOrder.getTotalAmount());
            
            // テスト3: 在庫不足エラー
            System.out.println("\nTest 3: Add item with insufficient inventory");
            mockInventory.setInventory("PROD-002", 1);
            
            try {
                orderService.addItemToOrder(order.getId(), "PROD-002", "Product 2", 5, 15.0);
                System.out.println("✗ Should have thrown exception for insufficient inventory");
            } catch (IllegalStateException e) {
                System.out.println("✓ Correctly rejected insufficient inventory: " + e.getMessage());
            }
            
            // テスト4: 注文処理（成功ケース）
            System.out.println("\nTest 4: Process order (success case)");
            mockPayment.setSuccessMode();
            mockInventory.setInventory("PROD-001", 10);
            
            Order processedOrder = orderService.processOrder(order.getId());
            
            assertEquals(OrderStatus.PAID, processedOrder.getStatus());
            assertEquals(1, mockEmail.getSentEmails().size());
            
            System.out.println("✓ Order processed successfully");
            System.out.println("✓ Confirmation email sent: " + mockEmail.getSentEmails().get(0));
            
            // テスト5: 決済失敗ケース
            System.out.println("\nTest 5: Process order (payment failure)");
            Order failOrder = orderService.createOrder("CUST-002");
            orderService.addItemToOrder(failOrder.getId(), "PROD-001", "Product 1", 1, 10.0);
            
            mockPayment.setFailureMode("Insufficient funds");
            
            try {
                orderService.processOrder(failOrder.getId());
                System.out.println("✗ Should have thrown PaymentFailedException");
            } catch (PaymentFailedException e) {
                System.out.println("✓ Correctly handled payment failure: " + e.getMessage());
            }
        }
    }
    
    /**
     * 統合テストレベル（複数コンポーネントの連携）
     */
    public static class IntegrationTestLevel {
        
        /**
         * サービス層の統合テスト例
         */
        public static void testOrderServiceIntegration() {
            System.out.println("\n=== Integration Test Level: Service Layer ===");
            
            // より現実的な実装を使用（ただし外部依存は除く）
            InMemoryOrderRepository repository = new InMemoryOrderRepository();
            MockPaymentService paymentService = new MockPaymentService();
            MockInventoryService inventoryService = new MockInventoryService();
            MockEmailService emailService = new MockEmailService();
            
            OrderService orderService = new OrderService(repository, paymentService, inventoryService, emailService);
            
            // テスト1: エンドツーエンドのワークフロー
            System.out.println("\nTest 1: End-to-end order workflow");
            
            // 在庫設定
            inventoryService.setInventory("PROD-001", 10);
            inventoryService.setInventory("PROD-002", 5);
            
            // 注文作成
            Order order = orderService.createOrder("CUST-001");
            System.out.println("Step 1: Order created - " + order.getId());
            
            // アイテム追加
            orderService.addItemToOrder(order.getId(), "PROD-001", "Product 1", 2, 25.0);
            orderService.addItemToOrder(order.getId(), "PROD-002", "Product 2", 1, 15.0);
            
            Order orderWithItems = repository.findById(order.getId());
            assertEquals(65.0, orderWithItems.getTotalAmount());
            System.out.println("Step 2: Items added, total: " + orderWithItems.getTotalAmount());
            
            // 注文処理
            paymentService.setSuccessMode();
            Order processedOrder = orderService.processOrder(order.getId());
            
            assertEquals(OrderStatus.PAID, processedOrder.getStatus());
            System.out.println("Step 3: Order processed, status: " + processedOrder.getStatus());
            
            // メール送信確認
            List<String> emails = emailService.getSentEmails();
            assertEquals(1, emails.size());
            assertTrue(emails.get(0).contains(order.getId()));
            System.out.println("Step 4: Email sent - " + emails.get(0));
            
            System.out.println("✓ Complete workflow executed successfully");
            
            // テスト2: トランザクション的な動作（失敗時のロールバック）
            System.out.println("\nTest 2: Transaction rollback on failure");
            
            repository.clear();
            emailService.clear();
            
            Order failOrder = orderService.createOrder("CUST-002");
            orderService.addItemToOrder(failOrder.getId(), "PROD-001", "Product 1", 1, 50.0);
            
            // 決済失敗をシミュレート
            paymentService.setFailureMode("Card declined");
            
            try {
                orderService.processOrder(failOrder.getId());
                System.out.println("✗ Should have thrown PaymentFailedException");
            } catch (PaymentFailedException e) {
                // 失敗後の状態確認
                Order failedOrder = repository.findById(failOrder.getId());
                assertEquals(OrderStatus.DRAFT, failedOrder.getStatus()); // 状態は変更されていない
                assertEquals(0, emailService.getSentEmails().size()); // メールは送信されていない
                
                System.out.println("✓ Transaction correctly rolled back on payment failure");
                System.out.println("✓ Order status remains: " + failedOrder.getStatus());
                System.out.println("✓ No confirmation email sent");
            }
            
            // テスト3: 複数顧客の注文履歴
            System.out.println("\nTest 3: Multiple customer order history");
            
            repository.clear();
            paymentService.setSuccessMode();
            
            // 顧客1の注文
            Order order1 = orderService.createOrder("CUST-001");
            orderService.addItemToOrder(order1.getId(), "PROD-001", "Product 1", 1, 10.0);
            orderService.processOrder(order1.getId());
            
            Order order2 = orderService.createOrder("CUST-001");
            orderService.addItemToOrder(order2.getId(), "PROD-002", "Product 2", 2, 15.0);
            orderService.processOrder(order2.getId());
            
            // 顧客2の注文
            Order order3 = orderService.createOrder("CUST-002");
            orderService.addItemToOrder(order3.getId(), "PROD-001", "Product 1", 3, 10.0);
            orderService.processOrder(order3.getId());
            
            // 履歴確認
            List<Order> cust1History = orderService.getOrderHistory("CUST-001");
            List<Order> cust2History = orderService.getOrderHistory("CUST-002");
            
            assertEquals(2, cust1History.size());
            assertEquals(1, cust2History.size());
            
            System.out.println("✓ Customer 1 has " + cust1History.size() + " orders");
            System.out.println("✓ Customer 2 has " + cust2History.size() + " orders");
            
            // 各顧客の注文が正しく分離されていることを確認
            assertTrue(cust1History.stream().allMatch(o -> o.getCustomerId().equals("CUST-001")));
            assertTrue(cust2History.stream().allMatch(o -> o.getCustomerId().equals("CUST-002")));
            
            System.out.println("✓ Order history correctly isolated by customer");
        }
    }
    
    /**
     * E2Eテストレベル（システム全体の動作）
     */
    public static class E2ETestLevel {
        
        /**
         * エンドツーエンドのシナリオテスト
         */
        public static void testCompleteOrderScenario() {
            System.out.println("\n=== E2E Test Level: Complete Order Scenario ===");
            
            // 実際のシステムに近い設定
            InMemoryOrderRepository repository = new InMemoryOrderRepository();
            MockPaymentService paymentService = new MockPaymentService();
            MockInventoryService inventoryService = new MockInventoryService();
            MockEmailService emailService = new MockEmailService();
            
            OrderService orderService = new OrderService(repository, paymentService, inventoryService, emailService);
            
            // シナリオ: オンラインショッピングの完全なフロー
            System.out.println("\nScenario: Complete online shopping flow");
            
            // 初期データ設定
            inventoryService.setInventory("LAPTOP-001", 5);
            inventoryService.setInventory("MOUSE-001", 20);
            inventoryService.setInventory("KEYBOARD-001", 10);
            
            // ユーザーがサイトにアクセスし、ショッピングを開始
            System.out.println("Step 1: Customer starts shopping");
            Order order = orderService.createOrder("CUSTOMER-12345");
            System.out.println("Created order: " + order.getId());
            
            // 商品をカートに追加
            System.out.println("\nStep 2: Adding items to cart");
            orderService.addItemToOrder(order.getId(), "LAPTOP-001", "Gaming Laptop", 1, 1299.99);
            orderService.addItemToOrder(order.getId(), "MOUSE-001", "Wireless Mouse", 1, 49.99);
            orderService.addItemToOrder(order.getId(), "KEYBOARD-001", "Mechanical Keyboard", 1, 129.99);
            
            Order cartOrder = repository.findById(order.getId());
            System.out.println("Cart total: $" + cartOrder.getTotalAmount());
            System.out.println("Items in cart: " + cartOrder.getItems().size());
            
            // 商品を一つ削除
            System.out.println("\nStep 3: Removing an item from cart");
            cartOrder.removeItem("KEYBOARD-001");
            repository.save(cartOrder);
            
            Order updatedCart = repository.findById(order.getId());
            System.out.println("Updated cart total: $" + updatedCart.getTotalAmount());
            System.out.println("Items remaining: " + updatedCart.getItems().size());
            
            // チェックアウト処理
            System.out.println("\nStep 4: Checkout process");
            paymentService.setSuccessMode();
            
            Order finalOrder = orderService.processOrder(order.getId());
            
            // 結果検証
            assertEquals(OrderStatus.PAID, finalOrder.getStatus());
            assertEquals(1349.98, finalOrder.getTotalAmount(), 0.01);
            
            List<String> confirmationEmails = emailService.getSentEmails();
            assertEquals(1, confirmationEmails.size());
            
            System.out.println("✓ Order successfully processed");
            System.out.println("✓ Final status: " + finalOrder.getStatus());
            System.out.println("✓ Final amount: $" + finalOrder.getTotalAmount());
            System.out.println("✓ Confirmation sent: " + confirmationEmails.get(0));
            
            // 顧客が注文履歴を確認
            System.out.println("\nStep 5: Customer checks order history");
            List<Order> orderHistory = orderService.getOrderHistory("CUSTOMER-12345");
            assertEquals(1, orderHistory.size());
            assertEquals(finalOrder.getId(), orderHistory.get(0).getId());
            
            System.out.println("✓ Order appears in customer history");
            System.out.println("✓ E2E scenario completed successfully");
        }
        
        /**
         * 異常系のE2Eテスト
         */
        public static void testErrorScenarios() {
            System.out.println("\n=== E2E Error Scenarios ===");
            
            InMemoryOrderRepository repository = new InMemoryOrderRepository();
            MockPaymentService paymentService = new MockPaymentService();
            MockInventoryService inventoryService = new MockInventoryService();
            MockEmailService emailService = new MockEmailService();
            
            OrderService orderService = new OrderService(repository, paymentService, inventoryService, emailService);
            
            // シナリオ1: 在庫切れでの購入試行
            System.out.println("\nScenario 1: Out of stock purchase attempt");
            
            inventoryService.setInventory("LIMITED-ITEM", 1);
            
            Order order1 = orderService.createOrder("CUST-001");
            orderService.addItemToOrder(order1.getId(), "LIMITED-ITEM", "Limited Edition", 1, 99.99);
            
            // 別の顧客が同じ商品を購入（在庫を消費）
            Order order2 = orderService.createOrder("CUST-002");
            orderService.addItemToOrder(order2.getId(), "LIMITED-ITEM", "Limited Edition", 1, 99.99);
            paymentService.setSuccessMode();
            orderService.processOrder(order2.getId());
            
            // 最初の顧客が購入しようとするが在庫がない
            inventoryService.setInventory("LIMITED-ITEM", 0);
            
            try {
                orderService.processOrder(order1.getId());
                System.out.println("✗ Should have failed due to out of stock");
            } catch (Exception e) {
                System.out.println("✓ Correctly failed due to inventory shortage: " + e.getMessage());
            }
            
            // シナリオ2: 決済失敗後の復旧
            System.out.println("\nScenario 2: Payment failure and recovery");
            
            repository.clear();
            emailService.clear();
            inventoryService.setInventory("PRODUCT-A", 10);
            
            Order order = orderService.createOrder("CUST-003");
            orderService.addItemToOrder(order.getId(), "PRODUCT-A", "Product A", 2, 50.0);
            
            // 最初の決済試行（失敗）
            paymentService.setFailureMode("Insufficient funds");
            
            try {
                orderService.processOrder(order.getId());
                System.out.println("✗ Should have failed due to payment issue");
            } catch (PaymentFailedException e) {
                System.out.println("✓ First payment attempt failed: " + e.getMessage());
            }
            
            // 決済方法を変更して再試行（成功）
            paymentService.setSuccessMode();
            Order recoveredOrder = orderService.processOrder(order.getId());
            
            assertEquals(OrderStatus.PAID, recoveredOrder.getStatus());
            assertEquals(1, emailService.getSentEmails().size());
            
            System.out.println("✓ Second payment attempt succeeded");
            System.out.println("✓ Order recovered and completed");
        }
    }
    
    // テストユーティリティ
    private static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
    
    private static void assertEquals(double expected, double actual, double delta) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual + " (delta: " + delta + ")");
        }
    }
    
    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected condition to be true");
        }
    }
    
    private static void assertNotNull(Object object) {
        if (object == null) {
            throw new AssertionError("Expected object to be non-null");
        }
    }
    
    /**
     * メインメソッド - テストピラミッドの各レベルを実行
     */
    public static void main(String[] args) {
        System.out.println("Testing Pyramid Demonstration");
        System.out.println("==============================");
        
        try {
            // レベル1: 単体テスト（高速、大量）
            UnitTestLevel.testOrderEntity();
            UnitTestLevel.testOrderServiceUnit();
            
            // レベル2: 統合テスト（中速、中量）
            IntegrationTestLevel.testOrderServiceIntegration();
            
            // レベル3: E2Eテスト（低速、少量）
            E2ETestLevel.testCompleteOrderScenario();
            E2ETestLevel.testErrorScenarios();
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎉 All tests passed! Test pyramid executed successfully.");
            System.out.println("\nTest Pyramid Summary:");
            System.out.println("📊 Unit Tests: Fast, numerous, focused on individual components");
            System.out.println("🔗 Integration Tests: Medium speed, testing component interactions");
            System.out.println("🌐 E2E Tests: Slow, few, but testing complete user scenarios");
            System.out.println("\nEach level serves a specific purpose in ensuring code quality!");
            
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}