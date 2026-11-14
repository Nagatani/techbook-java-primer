package com.example.immutability;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

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
            // Map.copyOfのマップは順序が決まっていないので、表示のときはキーの順に並べる
            return String.format("ImmutablePerson{name='%s', birthDate=%s, nicknames=%s, attributes=%s}",
                               name, birthDate, nicknames, new TreeMap<>(attributes));
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
        private static final int POOL_SIZE_LIMIT = 1000; // プールに入れる数の上限
        
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
                ImmutablePoint pooled = POOL.get(key);
                if (pooled != null) {
                    return pooled;
                }
                if (POOL.size() >= POOL_SIZE_LIMIT) {
                    // 上限に達したらプールには入れず、新しく作るだけにする
                    return new ImmutablePoint(x, y);
                }
                return POOL.computeIfAbsent(key, k -> new ImmutablePoint(x, y));
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
        // 不変オブジェクトそのものは安全に共有できるが、「今の注文」を差し替える操作は
        // 読み取り→新しい注文の作成→書き込みの3段階になる。
        // volatileだけでは、2つのスレッドが同時に差し替えると片方の変更が失われる。
        // AtomicReferenceのupdateAndGetは、途中でほかのスレッドが差し替えていたらやり直す
        private final AtomicReference<ImmutableOrder> currentOrder = new AtomicReference<>();
        
        public void updateOrder(ImmutableOrder newOrder) {
            currentOrder.set(newOrder);
        }
        
        public ImmutableOrder getCurrentOrder() {
            return currentOrder.get();
        }
        
        public ImmutableOrder addItemToCurrentOrder(OrderItem item) {
            return currentOrder.updateAndGet(current -> {
                if (current == null) {
                    throw new IllegalStateException("No current order");
                }
                return current.toBuilder()
                    .addItem(item)
                    .build();
            });
        }
    }
    
    /**
     * パフォーマンステスト
     */
    public static class PerformanceTest {
        
        public static void compareImmutableVsMutable() {
            IO.println("=== Performance Comparison: Immutable vs Mutable ===");
            IO.println("(Timings vary greatly by environment. Use JMH for accurate measurements.)");
            
            int iterations = 10_000;
            
            // 不変オブジェクト：moveのたびに新しいオブジェクトを作る
            Supplier<Object> immutableTask = () -> {
                ImmutablePoint point = ImmutablePoint.of(0, 0);
                for (int i = 0; i < iterations; i++) {
                    point = point.move(1, 1);
                }
                return point;
            };
            
            // 可変オブジェクト：同じオブジェクトを書き換える
            Supplier<Object> mutableTask = () -> {
                MutablePoint point = new MutablePoint(0, 0);
                for (int i = 0; i < iterations; i++) {
                    point.move(1, 1);
                }
                return point;
            };
            
            long immutableTime = measureTime(immutableTask);
            long mutableTime = measureTime(mutableTask);
            
            IO.println("Immutable operations: %d μs".formatted(immutableTime / 1000));
            IO.println("Mutable operations:   %d μs".formatted(mutableTime / 1000));
            if (immutableTime <= 0 || mutableTime <= 0) {
                IO.println("Immutable / mutable: n/a (too fast to measure)");
            } else {
                IO.println("Immutable / mutable: %.2fx".formatted((double) immutableTime / mutableTime));
            }
        }
        
        // 暖機のために何回か実行してから、10回の平均を測る
        private static long measureTime(Supplier<Object> task) {
            for (int i = 0; i < 20; i++) {
                task.get();
            }
            long start = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                task.get();
            }
            return (System.nanoTime() - start) / 10;
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
        IO.println("Immutability Patterns and Implementation Techniques Demo");
        IO.println("======================================================");
        
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
        
        IO.println("\nKey Insights:");
        IO.println("✓ Immutable objects are inherently thread-safe");
        IO.println("✓ Builder pattern provides flexible object construction");
        IO.println("✓ Object pooling can optimize memory usage for common values");
        IO.println("✓ Lazy evaluation defers expensive computations");
        IO.println("✓ 'with' methods enable functional-style updates");
        
        IO.println("\nBest Practices:");
        IO.println("• Use defensive copying in constructors and getters");
        IO.println("• Implement proper equals(), hashCode(), and toString()");
        IO.println("• Consider object pooling for frequently used values");
        IO.println("• Use builder pattern for complex object construction");
        IO.println("• Leverage immutability for better concurrency performance");
    }
    
    private static void demonstrateImmutablePerson() {
        IO.println("\n=== Immutable Person Demo ===");
        
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
        
        IO.println("Original person: " + person);
        
        // withメソッドで新しいインスタンスを作成
        ImmutablePerson updatedPerson = person
            .withName("Jane Doe")
            .withNickname("Janie")
            .withAttribute("city", "Osaka");
        
        IO.println("Updated person:  " + updatedPerson);
        IO.println("Original unchanged: " + person.getName());
        
        // 元のコレクションを変更しても、コンストラクターでコピーしているので影響しない
        try {
            nicknames.add("Hack"); // Arrays.asListのリストは要素を追加できない
        } catch (UnsupportedOperationException e) {
            IO.println("Expected: Cannot add to the list from Arrays.asList");
        }
        attributes.put("city", "Kyoto"); // 元のHashMapは変更できる
        IO.println("Original map was changed: " + new TreeMap<>(attributes));
        
        IO.println("After external modification: " + person);
    }
    
    private static void demonstrateBuilderPattern() {
        IO.println("\n=== Builder Pattern Demo ===");
        
        OrderItem item1 = new OrderItem("P001", "Laptop", 1, new BigDecimal("1500.00"));
        OrderItem item2 = new OrderItem("P002", "Mouse", 2, new BigDecimal("25.00"));
        
        ImmutableOrder order = ImmutableOrder.builder()
            .orderId("ORD-001")
            .orderDate(LocalDateTime.of(2026, 4, 1, 10, 30)) // 実行結果が毎回同じになるよう固定する
            .addItem(item1)
            .addItem(item2)
            .status(OrderStatus.CONFIRMED)
            .build();
        
        IO.println("Created order: " + order);
        IO.println("Total amount: " + order.getTotalAmount());
        
        // 既存の注文から新しい注文を作成
        ImmutableOrder shippedOrder = order.toBuilder()
            .status(OrderStatus.SHIPPED)
            .build();
        
        IO.println("Shipped order: " + shippedOrder);
    }
    
    private static void demonstrateObjectPooling() {
        IO.println("\n=== Object Pooling Demo ===");
        
        IO.println("Initial pool size: " + ImmutablePoint.getPoolSize());
        
        // プールされる範囲の座標
        ImmutablePoint p1 = ImmutablePoint.of(10, 20);
        ImmutablePoint p2 = ImmutablePoint.of(10, 20); // 同じインスタンスが返される
        
        IO.println("p1 == p2: " + (p1 == p2)); // true（同じインスタンス）
        IO.println("Pool size after creating pooled points: " + ImmutablePoint.getPoolSize());
        
        // プール範囲外の座標
        ImmutablePoint p3 = ImmutablePoint.of(1000, 2000);
        ImmutablePoint p4 = ImmutablePoint.of(1000, 2000); // 新しいインスタンス
        
        IO.println("p3 == p4: " + (p3 == p4)); // false（異なるインスタンス）
        IO.println("p3.equals(p4): " + p3.equals(p4)); // true（値は同じ）
        
        // 移動操作
        ImmutablePoint moved = p1.move(5, 5);
        IO.println("Original: " + p1 + ", Moved: " + moved);
        IO.println("Distance: " + p1.distanceTo(moved));
        
        ImmutablePoint.clearPool();
        IO.println("Pool size after clear: " + ImmutablePoint.getPoolSize());
    }
    
    private static void demonstrateLazyEvaluation() {
        IO.println("\n=== Lazy Evaluation Demo ===");
        
        LazyImmutable lazy = new LazyImmutable("Example");
        IO.println("Before accessing expensive field: " + lazy);
        
        long start = System.currentTimeMillis();
        String expensive = lazy.getExpensiveField();
        long firstAccess = System.currentTimeMillis() - start;
        
        IO.println("First access (computed): " + expensive);
        IO.println("First access time: " + firstAccess + " ms");
        
        start = System.currentTimeMillis();
        expensive = lazy.getExpensiveField();
        long secondAccess = System.currentTimeMillis() - start;
        
        IO.println("Second access (cached): " + expensive);
        IO.println("Second access time: " + secondAccess + " ms");
        IO.println("After accessing expensive field: " + lazy);
    }
    
    private static void demonstrateConcurrencySafety() {
        IO.println("\n=== Concurrency Safety Demo ===");
        
        ConcurrencySafetyDemo demo = new ConcurrencySafetyDemo();
        
        // 初期注文の作成
        ImmutableOrder initialOrder = ImmutableOrder.builder()
            .orderId("ORD-CONCURRENT")
            .orderDate(LocalDateTime.of(2026, 4, 1, 10, 30))
            .addItem(new OrderItem("P001", "Book", 1, new BigDecimal("20.00")))
            .build();
        
        demo.updateOrder(initialOrder);
        IO.println("Initial order: " + demo.getCurrentOrder());
        
        // 4つのスレッドから、それぞれ25個の品目を同時に追加する
        OrderItem newItem = new OrderItem("P002", "Pen", 3, new BigDecimal("5.00"));
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < 4; t++) {
            Thread thread = new Thread(() -> {
                for (int i = 0; i < 25; i++) {
                    demo.addItemToCurrentOrder(newItem);
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        ImmutableOrder updatedOrder = demo.getCurrentOrder();
        IO.println("Updated order: " + updatedOrder);
        IO.println("Original order unchanged: " + initialOrder.getItems().size() + " items");
        IO.println("Updated order has: " + updatedOrder.getItems().size() + " items (expected 101)");
    }
}
