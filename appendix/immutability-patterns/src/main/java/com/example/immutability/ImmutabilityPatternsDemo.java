package com.example.immutability;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 不変性の設計パターンとビルダーパターンの実装デモンストレーション
 * スレッドセーフな不変オブジェクトの設計と防御的コピーの実装
 */
public class ImmutabilityPatternsDemo {
    
    /**
     * 完全な不変クラスの実装例
     */
    public static final class ImmutablePerson {
        private final String name;
        private final LocalDate birthDate;
        private final List<String> nicknames;
        private final Map<String, String> attributes;
        
        // コンストラクタでのディープコピー
        public ImmutablePerson(String name, LocalDate birthDate, 
                              List<String> nicknames, Map<String, String> attributes) {
            this.name = Objects.requireNonNull(name, "name cannot be null");
            this.birthDate = Objects.requireNonNull(birthDate, "birthDate cannot be null");
            
            // 防御的コピー
            this.nicknames = nicknames == null 
                ? List.of() 
                : List.copyOf(nicknames);
            
            this.attributes = attributes == null 
                ? Map.of() 
                : Map.copyOf(attributes);
        }
        
        public String getName() {
            return name;
        }
        
        public LocalDate getBirthDate() {
            return birthDate;
        }
        
        // ゲッターでも防御的コピー（必要な場合）
        public List<String> getNicknames() {
            return nicknames; // Java 9+ のList.copyOf()は既に不変
        }
        
        public Map<String, String> getAttributes() {
            return attributes; // Java 9+ のMap.copyOf()は既に不変
        }
        
        // withメソッドパターン
        public ImmutablePerson withName(String newName) {
            return new ImmutablePerson(newName, birthDate, nicknames, attributes);
        }
        
        public ImmutablePerson withNickname(String nickname) {
            List<String> newNicknames = new ArrayList<>(nicknames);
            newNicknames.add(nickname);
            return new ImmutablePerson(name, birthDate, newNicknames, attributes);
        }
        
        public ImmutablePerson withAttribute(String key, String value) {
            Map<String, String> newAttributes = new HashMap<>(attributes);
            newAttributes.put(key, value);
            return new ImmutablePerson(name, birthDate, nicknames, newAttributes);
        }
        
        // equals, hashCode, toString の実装
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ImmutablePerson)) return false;
            ImmutablePerson other = (ImmutablePerson) obj;
            return Objects.equals(name, other.name) &&
                   Objects.equals(birthDate, other.birthDate) &&
                   Objects.equals(nicknames, other.nicknames) &&
                   Objects.equals(attributes, other.attributes);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(name, birthDate, nicknames, attributes);
        }
        
        @Override
        public String toString() {
            return String.format("ImmutablePerson{name='%s', birthDate=%s, nicknames=%s, attributes=%s}",
                               name, birthDate, nicknames, attributes);
        }
    }
    
    /**
     * 注文状態を表すEnum
     */
    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
    
    /**
     * 注文項目を表す不変クラス
     */
    public static final class OrderItem {
        private final String productId;
        private final String productName;
        private final int quantity;
        private final BigDecimal price;
        
        public OrderItem(String productId, String productName, int quantity, BigDecimal price) {
            this.productId = Objects.requireNonNull(productId);
            this.productName = Objects.requireNonNull(productName);
            this.quantity = quantity;
            this.price = Objects.requireNonNull(price);
            
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Price cannot be negative");
            }
        }
        
        public String getProductId() { return productId; }
        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public BigDecimal getPrice() { return price; }
        
        public BigDecimal getTotalPrice() {
            return price.multiply(BigDecimal.valueOf(quantity));
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof OrderItem)) return false;
            OrderItem other = (OrderItem) obj;
            return quantity == other.quantity &&
                   Objects.equals(productId, other.productId) &&
                   Objects.equals(productName, other.productName) &&
                   Objects.equals(price, other.price);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(productId, productName, quantity, price);
        }
        
        @Override
        public String toString() {
            return String.format("OrderItem{productId='%s', productName='%s', quantity=%d, price=%s}",
                               productId, productName, quantity, price);
        }
    }
    
    /**
     * ビルダーパターンと組み合わせた不変注文クラス
     */
    public static final class ImmutableOrder {
        private final String orderId;
        private final LocalDateTime orderDate;
        private final List<OrderItem> items;
        private final BigDecimal totalAmount;
        private final OrderStatus status;
        
        private ImmutableOrder(Builder builder) {
            this.orderId = builder.orderId;
            this.orderDate = builder.orderDate;
            this.items = List.copyOf(builder.items);
            this.totalAmount = builder.totalAmount;
            this.status = builder.status;
        }
        
        public String getOrderId() { return orderId; }
        public LocalDateTime getOrderDate() { return orderDate; }
        public List<OrderItem> getItems() { return items; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public OrderStatus getStatus() { return status; }
        
        public static Builder builder() {
            return new Builder();
        }
        
        // 既存のオブジェクトからビルダーを作成
        public Builder toBuilder() {
            return new Builder()
                .orderId(orderId)
                .orderDate(orderDate)
                .items(new ArrayList<>(items))
                .totalAmount(totalAmount)
                .status(status);
        }
        
        public static class Builder {
            private String orderId;
            private LocalDateTime orderDate;
            private List<OrderItem> items = new ArrayList<>();
            private BigDecimal totalAmount = BigDecimal.ZERO;
            private OrderStatus status = OrderStatus.PENDING;
            
            public Builder orderId(String orderId) {
                this.orderId = orderId;
                return this;
            }
            
            public Builder orderDate(LocalDateTime orderDate) {
                this.orderDate = orderDate;
                return this;
            }
            
            public Builder items(List<OrderItem> items) {
                this.items = new ArrayList<>(items);
                calculateTotalAmount();
                return this;
            }
            
            public Builder addItem(OrderItem item) {
                this.items.add(item);
                this.totalAmount = totalAmount.add(item.getTotalPrice());
                return this;
            }
            
            public Builder totalAmount(BigDecimal totalAmount) {
                this.totalAmount = totalAmount;
                return this;
            }
            
            public Builder status(OrderStatus status) {
                this.status = status;
                return this;
            }
            
            private void calculateTotalAmount() {
                this.totalAmount = items.stream()
                    .map(OrderItem::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            
            public ImmutableOrder build() {
                // バリデーション
                Objects.requireNonNull(orderId, "orderId is required");
                Objects.requireNonNull(orderDate, "orderDate is required");
                if (items.isEmpty()) {
                    throw new IllegalStateException("Order must have at least one item");
                }
                
                return new ImmutableOrder(this);
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ImmutableOrder)) return false;
            ImmutableOrder other = (ImmutableOrder) obj;
            return Objects.equals(orderId, other.orderId) &&
                   Objects.equals(orderDate, other.orderDate) &&
                   Objects.equals(items, other.items) &&
                   Objects.equals(totalAmount, other.totalAmount) &&
                   status == other.status;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(orderId, orderDate, items, totalAmount, status);
        }
        
        @Override
        public String toString() {
            return String.format("ImmutableOrder{orderId='%s', orderDate=%s, items=%d, totalAmount=%s, status=%s}",
                               orderId, orderDate, items.size(), totalAmount, status);
        }
    }
    
    /**
     * 不変オブジェクトのプーリング例
     */
    public static final class ImmutablePoint {
        private static final Map<String, ImmutablePoint> POOL = new ConcurrentHashMap<>();
        private static final int POOL_SIZE_LIMIT = 1000;
        
        private final int x;
        private final int y;
        
        private ImmutablePoint(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        // ファクトリメソッドでプーリング
        public static ImmutablePoint of(int x, int y) {
            // よく使われる値は事前にキャッシュ
            if (x >= -128 && x <= 127 && y >= -128 && y <= 127) {
                String key = x + "," + y;
                return POOL.computeIfAbsent(key, k -> {
                    if (POOL.size() >= POOL_SIZE_LIMIT) {
                        // プールサイズ制限を超えた場合は新規作成
                        return new ImmutablePoint(x, y);
                    }
                    return new ImmutablePoint(x, y);
                });
            }
            
            // 範囲外の値は新規作成
            return new ImmutablePoint(x, y);
        }
        
        public int getX() { return x; }
        public int getY() { return y; }
        
        public ImmutablePoint move(int deltaX, int deltaY) {
            return of(x + deltaX, y + deltaY);
        }
        
        public double distanceTo(ImmutablePoint other) {
            int dx = x - other.x;
            int dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
        
        // equals と hashCode は必須
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ImmutablePoint)) return false;
            ImmutablePoint other = (ImmutablePoint) obj;
            return x == other.x && y == other.y;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
        
        @Override
        public String toString() {
            return String.format("Point(%d, %d)", x, y);
        }
        
        public static void clearPool() {
            POOL.clear();
        }
        
        public static int getPoolSize() {
            return POOL.size();
        }
    }
    
    /**
     * 遅延評価される不変フィールド
     */
    public static final class LazyImmutable {
        private final String name;
        private volatile String expensiveField;  // 遅延初期化
        
        public LazyImmutable(String name) {
            this.name = Objects.requireNonNull(name);
        }
        
        public String getName() {
            return name;
        }
        
        // ダブルチェックロッキングで遅延初期化
        public String getExpensiveField() {
            String result = expensiveField;
            if (result == null) {
                synchronized (this) {
                    result = expensiveField;
                    if (result == null) {
                        expensiveField = result = computeExpensiveField();
                    }
                }
            }
            return result;
        }
        
        private String computeExpensiveField() {
            // 高コストな計算のシミュレーション
            try {
                Thread.sleep(10); // 10ms の処理時間
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Computed: " + name.toUpperCase() + " [expensive]";
        }
        
        @Override
        public String toString() {
            return String.format("LazyImmutable{name='%s', expensiveField=%s}",
                               name, expensiveField == null ? "not computed" : "computed");
        }
    }
    
    /**
     * 並行安全性のデモンストレーション
     */
    public static class ConcurrencySafetyDemo {
        private volatile ImmutableOrder currentOrder;
        
        public void updateOrder(ImmutableOrder newOrder) {
            this.currentOrder = newOrder;
        }
        
        public ImmutableOrder getCurrentOrder() {
            return currentOrder;
        }
        
        public ImmutableOrder addItemToCurrentOrder(OrderItem item) {
            ImmutableOrder current = currentOrder;
            if (current == null) {
                throw new IllegalStateException("No current order");
            }
            
            ImmutableOrder updated = current.toBuilder()
                .addItem(item)
                .build();
            
            updateOrder(updated);
            return updated;
        }
    }
    
    /**
     * パフォーマンステスト
     */
    public static class PerformanceTest {
        
        public static void compareImmutableVsMutable() {
            System.out.println("=== Performance Comparison: Immutable vs Mutable ===");
            
            int iterations = 10_000;
            
            // 不変オブジェクトのテスト
            long immutableTime = measureTime(() -> {
                ImmutablePoint point = ImmutablePoint.of(0, 0);
                for (int i = 0; i < iterations; i++) {
                    point = point.move(1, 1);
                }
                return point;
            });
            
            // 可変オブジェクトのテスト
            long mutableTime = measureTime(() -> {
                MutablePoint point = new MutablePoint(0, 0);
                for (int i = 0; i < iterations; i++) {
                    point.move(1, 1);
                }
                return point;
            });
            
            System.out.printf("Immutable operations: %d ms%n", immutableTime);
            System.out.printf("Mutable operations:   %d ms%n", mutableTime);
            System.out.printf("Immutable is %.2fx %s%n", 
                            (double) Math.max(immutableTime, mutableTime) / Math.min(immutableTime, mutableTime),
                            immutableTime > mutableTime ? "slower" : "faster");
        }
        
        private static long measureTime(Supplier<Object> task) {
            long start = System.currentTimeMillis();
            task.get();
            return System.currentTimeMillis() - start;
        }
        
        @FunctionalInterface
        interface Supplier<T> {
            T get();
        }
        
        // 比較用の可変クラス
        static class MutablePoint {
            private int x, y;
            
            MutablePoint(int x, int y) {
                this.x = x;
                this.y = y;
            }
            
            void move(int deltaX, int deltaY) {
                this.x += deltaX;
                this.y += deltaY;
            }
            
            int getX() { return x; }
            int getY() { return y; }
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        System.out.println("Immutability Patterns and Implementation Techniques Demo");
        System.out.println("======================================================");
        
        // 不変Personオブジェクトのデモ
        demonstrateImmutablePerson();
        
        // ビルダーパターンのデモ
        demonstrateBuilderPattern();
        
        // オブジェクトプーリングのデモ
        demonstrateObjectPooling();
        
        // 遅延評価のデモ
        demonstrateLazyEvaluation();
        
        // 並行安全性のデモ
        demonstrateConcurrencySafety();
        
        // パフォーマンステスト
        PerformanceTest.compareImmutableVsMutable();
        
        System.out.println("\n🎯 Key Insights:");
        System.out.println("✓ Immutable objects are inherently thread-safe");
        System.out.println("✓ Builder pattern provides flexible object construction");
        System.out.println("✓ Object pooling can optimize memory usage for common values");
        System.out.println("✓ Lazy evaluation defers expensive computations");
        System.out.println("✓ 'with' methods enable functional-style updates");
        
        System.out.println("\n⚡ Best Practices:");
        System.out.println("• Use defensive copying in constructors and getters");
        System.out.println("• Implement proper equals(), hashCode(), and toString()");
        System.out.println("• Consider object pooling for frequently used values");
        System.out.println("• Use builder pattern for complex object construction");
        System.out.println("• Leverage immutability for better concurrency performance");
    }
    
    private static void demonstrateImmutablePerson() {
        System.out.println("\n=== Immutable Person Demo ===");
        
        List<String> nicknames = Arrays.asList("John", "Johnny");
        Map<String, String> attributes = new HashMap<>();
        attributes.put("city", "Tokyo");
        attributes.put("profession", "Developer");
        
        ImmutablePerson person = new ImmutablePerson(
            "John Doe",
            LocalDate.of(1990, 1, 1),
            nicknames,
            attributes
        );
        
        System.out.println("Original person: " + person);
        
        // withメソッドで新しいインスタンスを作成
        ImmutablePerson updatedPerson = person
            .withName("Jane Doe")
            .withNickname("Janie")
            .withAttribute("city", "Osaka");
        
        System.out.println("Updated person:  " + updatedPerson);
        System.out.println("Original unchanged: " + person.getName());
        
        // 元のコレクションを変更しても影響なし（変更は失敗するが、不変オブジェクトに影響なし）
        try {
            nicknames.add("Hack"); // 元のリストを変更しようとする
        } catch (UnsupportedOperationException e) {
            System.out.println("Expected: Cannot modify original list");
        }
        try {
            attributes.put("city", "Kyoto"); // 元のマップを変更しようとする  
        } catch (UnsupportedOperationException e) {
            System.out.println("Expected: Cannot modify original map");
        }
        
        System.out.println("After external modification: " + person);
    }
    
    private static void demonstrateBuilderPattern() {
        System.out.println("\n=== Builder Pattern Demo ===");
        
        OrderItem item1 = new OrderItem("P001", "Laptop", 1, new BigDecimal("1500.00"));
        OrderItem item2 = new OrderItem("P002", "Mouse", 2, new BigDecimal("25.00"));
        
        ImmutableOrder order = ImmutableOrder.builder()
            .orderId("ORD-001")
            .orderDate(LocalDateTime.now())
            .addItem(item1)
            .addItem(item2)
            .status(OrderStatus.CONFIRMED)
            .build();
        
        System.out.println("Created order: " + order);
        System.out.println("Total amount: " + order.getTotalAmount());
        
        // 既存の注文から新しい注文を作成
        ImmutableOrder shippedOrder = order.toBuilder()
            .status(OrderStatus.SHIPPED)
            .build();
        
        System.out.println("Shipped order: " + shippedOrder);
    }
    
    private static void demonstrateObjectPooling() {
        System.out.println("\n=== Object Pooling Demo ===");
        
        System.out.println("Initial pool size: " + ImmutablePoint.getPoolSize());
        
        // プールされる範囲の座標
        ImmutablePoint p1 = ImmutablePoint.of(10, 20);
        ImmutablePoint p2 = ImmutablePoint.of(10, 20); // 同じインスタンスが返される
        
        System.out.println("p1 == p2: " + (p1 == p2)); // true（同じインスタンス）
        System.out.println("Pool size after creating pooled points: " + ImmutablePoint.getPoolSize());
        
        // プール範囲外の座標
        ImmutablePoint p3 = ImmutablePoint.of(1000, 2000);
        ImmutablePoint p4 = ImmutablePoint.of(1000, 2000); // 新しいインスタンス
        
        System.out.println("p3 == p4: " + (p3 == p4)); // false（異なるインスタンス）
        System.out.println("p3.equals(p4): " + p3.equals(p4)); // true（値は同じ）
        
        // 移動操作
        ImmutablePoint moved = p1.move(5, 5);
        System.out.println("Original: " + p1 + ", Moved: " + moved);
        System.out.println("Distance: " + p1.distanceTo(moved));
        
        ImmutablePoint.clearPool();
        System.out.println("Pool size after clear: " + ImmutablePoint.getPoolSize());
    }
    
    private static void demonstrateLazyEvaluation() {
        System.out.println("\n=== Lazy Evaluation Demo ===");
        
        LazyImmutable lazy = new LazyImmutable("Example");
        System.out.println("Before accessing expensive field: " + lazy);
        
        long start = System.currentTimeMillis();
        String expensive = lazy.getExpensiveField();
        long firstAccess = System.currentTimeMillis() - start;
        
        System.out.println("First access (computed): " + expensive);
        System.out.println("First access time: " + firstAccess + " ms");
        
        start = System.currentTimeMillis();
        expensive = lazy.getExpensiveField();
        long secondAccess = System.currentTimeMillis() - start;
        
        System.out.println("Second access (cached): " + expensive);
        System.out.println("Second access time: " + secondAccess + " ms");
        System.out.println("After accessing expensive field: " + lazy);
    }
    
    private static void demonstrateConcurrencySafety() {
        System.out.println("\n=== Concurrency Safety Demo ===");
        
        ConcurrencySafetyDemo demo = new ConcurrencySafetyDemo();
        
        // 初期注文の作成
        ImmutableOrder initialOrder = ImmutableOrder.builder()
            .orderId("ORD-CONCURRENT")
            .orderDate(LocalDateTime.now())
            .addItem(new OrderItem("P001", "Book", 1, new BigDecimal("20.00")))
            .build();
        
        demo.updateOrder(initialOrder);
        System.out.println("Initial order: " + demo.getCurrentOrder());
        
        // 複数スレッドから安全にアクセス
        OrderItem newItem = new OrderItem("P002", "Pen", 3, new BigDecimal("5.00"));
        ImmutableOrder updatedOrder = demo.addItemToCurrentOrder(newItem);
        
        System.out.println("Updated order: " + updatedOrder);
        System.out.println("Original order unchanged: " + initialOrder.getItems().size() + " items");
        System.out.println("Updated order has: " + updatedOrder.getItems().size() + " items");
    }
}