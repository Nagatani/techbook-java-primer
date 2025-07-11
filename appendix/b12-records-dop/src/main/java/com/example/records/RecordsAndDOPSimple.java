package com.example.records;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * RecordsとData-Oriented Programming (DOP)の基本デモンストレーション
 * Java 14互換バージョン
 */
public class RecordsAndDOPSimple {
    
    // ========== 基本的なRecordの定義 ==========
    
    /**
     * シンプルなUserレコード
     */
    public record User(String id, String name, String email, LocalDateTime createdAt) {
        // コンパクトコンストラクタで検証
        public User {
            Objects.requireNonNull(id, "id cannot be null");
            Objects.requireNonNull(name, "name cannot be null");
            Objects.requireNonNull(email, "email cannot be null");
            Objects.requireNonNull(createdAt, "createdAt cannot be null");
            
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                throw new IllegalArgumentException("Invalid email format: " + email);
            }
        }
        
        // 追加メソッド
        public String displayName() {
            return name + " <" + email + ">";
        }
        
        public boolean isNewUser() {
            return createdAt.isAfter(LocalDateTime.now().minusDays(7));
        }
    }
    
    // ========== データ指向プログラミング（DOP） ==========
    
    /**
     * DOPアプローチでの図形処理（簡略版）
     */
    public static class ShapeProcessing {
        // データは単純なレコード
        public interface Shape {}
        
        public record Circle(double radius) implements Shape {
            public Circle {
                if (radius <= 0) throw new IllegalArgumentException("Radius must be positive");
            }
        }
        
        public record Rectangle(double width, double height) implements Shape {
            public Rectangle {
                if (width <= 0 || height <= 0) {
                    throw new IllegalArgumentException("Width and height must be positive");
                }
            }
        }
        
        public record Triangle(double a, double b, double c) implements Shape {
            public Triangle {
                if (a <= 0 || b <= 0 || c <= 0) {
                    throw new IllegalArgumentException("All sides must be positive");
                }
                if (a + b <= c || b + c <= a || c + a <= b) {
                    throw new IllegalArgumentException("Invalid triangle sides");
                }
            }
        }
        
        // 操作は独立した関数（instanceof パターンマッチング使用）
        public static double area(Shape shape) {
            if (shape instanceof Circle) {
                Circle c = (Circle) shape;
                return Math.PI * c.radius() * c.radius();
            } else if (shape instanceof Rectangle) {
                Rectangle r = (Rectangle) shape;
                return r.width() * r.height();
            } else if (shape instanceof Triangle) {
                Triangle t = (Triangle) shape;
                double s = (t.a() + t.b() + t.c()) / 2;
                return Math.sqrt(s * (s - t.a()) * (s - t.b()) * (s - t.c()));
            }
            throw new IllegalArgumentException("Unknown shape type");
        }
        
        public static double perimeter(Shape shape) {
            if (shape instanceof Circle) {
                Circle c = (Circle) shape;
                return 2 * Math.PI * c.radius();
            } else if (shape instanceof Rectangle) {
                Rectangle r = (Rectangle) shape;
                return 2 * (r.width() + r.height());
            } else if (shape instanceof Triangle) {
                Triangle t = (Triangle) shape;
                return t.a() + t.b() + t.c();
            }
            throw new IllegalArgumentException("Unknown shape type");
        }
        
        public static String describe(Shape shape) {
            if (shape instanceof Circle) {
                Circle c = (Circle) shape;
                return String.format("Circle with radius %.2f", c.radius());
            } else if (shape instanceof Rectangle) {
                Rectangle r = (Rectangle) shape;
                return String.format("Rectangle %.2f x %.2f", r.width(), r.height());
            } else if (shape instanceof Triangle) {
                Triangle t = (Triangle) shape;
                return String.format("Triangle with sides %.2f, %.2f, %.2f", t.a(), t.b(), t.c());
            }
            throw new IllegalArgumentException("Unknown shape type");
        }
    }
    
    // ========== ビジネスドメインモデル ==========
    
    /**
     * ECサイトのドメインモデル（DOPスタイル）
     */
    public static class ECommerceDomain {
        // 顧客タイプ
        public enum CustomerType { REGULAR, PREMIUM, VIP }
        
        // 注文ステータス
        public enum OrderStatus { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
        
        // ドメインモデル（純粋なデータ）
        public record Customer(String id, String name, CustomerType type, BigDecimal creditLimit) {}
        
        public record Product(String id, String name, BigDecimal price, int stock, Set<String> categories) {}
        
        public record OrderItem(String productId, int quantity, BigDecimal unitPrice) {
            public OrderItem {
                if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
                if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Unit price cannot be negative");
                }
            }
            
            public BigDecimal totalPrice() {
                return unitPrice.multiply(BigDecimal.valueOf(quantity));
            }
        }
        
        public record Order(
            String orderId, 
            String customerId, 
            List<OrderItem> items, 
            OrderStatus status,
            LocalDateTime createdAt
        ) {
            public Order {
                if (items == null || items.isEmpty()) {
                    throw new IllegalArgumentException("Order must have at least one item");
                }
                // 不変リストとして保存
                items = List.copyOf(items);
            }
        }
        
        // ビジネスロジック（純粋関数）
        public static class OrderService {
            public static BigDecimal calculateTotal(Order order) {
                return order.items().stream()
                    .map(OrderItem::totalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            
            public static BigDecimal applyDiscount(Order order, Customer customer) {
                var total = calculateTotal(order);
                BigDecimal discountRate;
                
                switch (customer.type()) {
                    case REGULAR:
                        discountRate = BigDecimal.ZERO;
                        break;
                    case PREMIUM:
                        discountRate = new BigDecimal("0.10");
                        break;
                    case VIP:
                        discountRate = new BigDecimal("0.20");
                        break;
                    default:
                        discountRate = BigDecimal.ZERO;
                }
                
                return total.multiply(BigDecimal.ONE.subtract(discountRate));
            }
            
            public static Order updateStatus(Order order, OrderStatus newStatus) {
                return new Order(
                    order.orderId(), 
                    order.customerId(), 
                    order.items(), 
                    newStatus,
                    order.createdAt()
                );
            }
            
            public static ValidationResult validateOrder(Order order, Map<String, Product> inventory) {
                var errors = new ArrayList<String>();
                
                for (var item : order.items()) {
                    var product = inventory.get(item.productId());
                    if (product == null) {
                        errors.add("Product not found: " + item.productId());
                    } else if (product.stock() < item.quantity()) {
                        errors.add("Insufficient stock for " + product.name() + 
                                 ": requested " + item.quantity() + ", available " + product.stock());
                    }
                }
                
                return errors.isEmpty() 
                    ? ValidationResult.success()
                    : ValidationResult.failure(errors);
            }
        }
        
        public record ValidationResult(boolean valid, List<String> errors) {
            public static ValidationResult success() {
                return new ValidationResult(true, List.of());
            }
            
            public static ValidationResult failure(List<String> errors) {
                return new ValidationResult(false, List.copyOf(errors));
            }
        }
    }
    
    // ========== カスタム検証付きレコード ==========
    
    /**
     * 検証ロジック付きのEmailレコード
     */
    public record ValidatedEmail(String value) {
        private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        
        // コンパクトコンストラクタで検証と正規化
        public ValidatedEmail {
            if (value == null || !value.matches(EMAIL_REGEX)) {
                throw new IllegalArgumentException("Invalid email: " + value);
            }
            value = value.toLowerCase();  // 正規化
        }
        
        public String domain() {
            return value.substring(value.indexOf('@') + 1);
        }
        
        public String localPart() {
            return value.substring(0, value.indexOf('@'));
        }
        
        public boolean isBusinessEmail() {
            var domain = domain();
            return !domain.endsWith("gmail.com") && 
                   !domain.endsWith("yahoo.com") && 
                   !domain.endsWith("hotmail.com");
        }
    }
    
    /**
     * 不変な設定レコード（withメソッドパターン）
     */
    public record Configuration(
        String host,
        int port,
        boolean useSsl,
        Duration timeout,
        Map<String, String> properties
    ) {
        public Configuration {
            Objects.requireNonNull(host, "host cannot be null");
            Objects.requireNonNull(timeout, "timeout cannot be null");
            if (port < 1 || port > 65535) {
                throw new IllegalArgumentException("Invalid port: " + port);
            }
            // propertiesを不変マップとして保存
            properties = properties == null ? Map.of() : Map.copyOf(properties);
        }
        
        // ビルダー風のwithメソッド
        public Configuration withHost(String newHost) {
            return new Configuration(newHost, port, useSsl, timeout, properties);
        }
        
        public Configuration withPort(int newPort) {
            return new Configuration(host, newPort, useSsl, timeout, properties);
        }
        
        public Configuration withSsl(boolean newUseSsl) {
            return new Configuration(host, port, newUseSsl, timeout, properties);
        }
        
        public Configuration withTimeout(Duration newTimeout) {
            return new Configuration(host, port, useSsl, newTimeout, properties);
        }
        
        public Configuration withProperty(String key, String value) {
            var newProperties = new HashMap<>(properties);
            newProperties.put(key, value);
            return new Configuration(host, port, useSsl, timeout, newProperties);
        }
        
        // デフォルト設定のファクトリメソッド
        public static Configuration createDefault() {
            return new Configuration(
                "localhost", 
                8080, 
                false, 
                Duration.ofSeconds(30),
                Map.of()
            );
        }
        
        public String getUrl() {
            String protocol = useSsl ? "https" : "http";
            return String.format("%s://%s:%d", protocol, host, port);
        }
    }
    
    // ========== パフォーマンステスト ==========
    
    /**
     * RecordとTraditionalクラスのパフォーマンス比較
     */
    public static class PerformanceComparison {
        // 従来のクラス
        public static class TraditionalPoint {
            private final int x;
            private final int y;
            
            public TraditionalPoint(int x, int y) {
                this.x = x;
                this.y = y;
            }
            
            public int getX() { return x; }
            public int getY() { return y; }
            
            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                TraditionalPoint that = (TraditionalPoint) o;
                return x == that.x && y == that.y;
            }
            
            @Override
            public int hashCode() {
                return Objects.hash(x, y);
            }
            
            @Override
            public String toString() {
                return "TraditionalPoint{x=" + x + ", y=" + y + '}';
            }
        }
        
        // レコード
        public record RecordPoint(int x, int y) {}
        
        public static void comparePerformance() {
            System.out.println("=== Record vs Traditional Class Performance ===");
            
            int iterations = 1_000_000;
            
            // 作成パフォーマンス
            long traditionalCreate = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    new TraditionalPoint(i, i);
                }
            });
            
            long recordCreate = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    new RecordPoint(i, i);
                }
            });
            
            System.out.printf("Creation - Traditional: %d ms, Record: %d ms%n", 
                            traditionalCreate, recordCreate);
            
            // equals パフォーマンス
            var tp1 = new TraditionalPoint(42, 100);
            var tp2 = new TraditionalPoint(42, 100);
            var rp1 = new RecordPoint(42, 100);
            var rp2 = new RecordPoint(42, 100);
            
            long traditionalEquals = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    tp1.equals(tp2);
                }
            });
            
            long recordEquals = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    rp1.equals(rp2);
                }
            });
            
            System.out.printf("Equals - Traditional: %d ms, Record: %d ms%n", 
                            traditionalEquals, recordEquals);
        }
        
        private static long measureTime(Runnable task) {
            long start = System.currentTimeMillis();
            task.run();
            return System.currentTimeMillis() - start;
        }
    }
    
    // ========== メインメソッド ==========
    
    public static void main(String[] args) {
        System.out.println("Records and Data-Oriented Programming Demo (Java 14 Compatible)");
        System.out.println("=============================================================");
        
        // 基本的なRecordのデモ
        demonstrateBasicRecords();
        
        // DOPでの図形処理
        demonstrateShapeProcessing();
        
        // ビジネスドメインモデル
        demonstrateECommerce();
        
        // カスタム検証付きレコード
        demonstrateValidatedRecords();
        
        // パフォーマンス比較
        PerformanceComparison.comparePerformance();
        
        System.out.println("\n🎯 Key Insights:");
        System.out.println("✓ Records eliminate boilerplate code (70-90% reduction)");
        System.out.println("✓ Data-Oriented Programming separates data from logic");
        System.out.println("✓ Records are immutable by default, promoting thread safety");
        System.out.println("✓ Compact constructors enable validation");
        System.out.println("✓ 'with' methods provide functional-style updates");
        
        System.out.println("\n⚡ Best Practices:");
        System.out.println("• Use compact constructors for validation");
        System.out.println("• Keep business logic in separate service classes");
        System.out.println("• Implement 'with' methods for immutable updates");
        System.out.println("• Use factory methods for complex creation logic");
        System.out.println("• Leverage record immutability for concurrent scenarios");
    }
    
    private static void demonstrateBasicRecords() {
        System.out.println("\n=== Basic Records Demo ===");
        
        var user = new User(
            UUID.randomUUID().toString(),
            "Alice Johnson",
            "alice@example.com",
            LocalDateTime.now()
        );
        
        System.out.println("User: " + user);
        System.out.println("Display name: " + user.displayName());
        System.out.println("Is new user: " + user.isNewUser());
        
        // アクセサメソッド
        System.out.println("ID: " + user.id());
        System.out.println("Name: " + user.name());
        
        // equals/hashCode は自動生成
        var sameUser = new User(user.id(), user.name(), user.email(), user.createdAt());
        System.out.println("Equals: " + user.equals(sameUser));
        System.out.println("HashCode same: " + (user.hashCode() == sameUser.hashCode()));
    }
    
    private static void demonstrateShapeProcessing() {
        System.out.println("\n=== Shape Processing (DOP) Demo ===");
        
        var shapes = List.of(
            new ShapeProcessing.Circle(5),
            new ShapeProcessing.Rectangle(4, 6),
            new ShapeProcessing.Triangle(3, 4, 5)
        );
        
        for (var shape : shapes) {
            System.out.println("\n" + ShapeProcessing.describe(shape));
            System.out.printf("Area: %.2f%n", ShapeProcessing.area(shape));
            System.out.printf("Perimeter: %.2f%n", ShapeProcessing.perimeter(shape));
        }
    }
    
    private static void demonstrateECommerce() {
        System.out.println("\n=== E-Commerce Domain Model Demo ===");
        
        // 商品在庫
        var inventory = Map.of(
            "P001", new ECommerceDomain.Product("P001", "Laptop", new BigDecimal("1500"), 10, Set.of("Electronics", "Computers")),
            "P002", new ECommerceDomain.Product("P002", "Mouse", new BigDecimal("25"), 50, Set.of("Electronics", "Accessories")),
            "P003", new ECommerceDomain.Product("P003", "Keyboard", new BigDecimal("75"), 0, Set.of("Electronics", "Accessories"))
        );
        
        // 顧客
        var customer = new ECommerceDomain.Customer(
            "C001", 
            "John Doe", 
            ECommerceDomain.CustomerType.PREMIUM,
            new BigDecimal("10000")
        );
        
        // 注文
        var order = new ECommerceDomain.Order(
            UUID.randomUUID().toString(),
            customer.id(),
            List.of(
                new ECommerceDomain.OrderItem("P001", 1, new BigDecimal("1500")),
                new ECommerceDomain.OrderItem("P002", 2, new BigDecimal("25"))
            ),
            ECommerceDomain.OrderStatus.PENDING,
            LocalDateTime.now()
        );
        
        System.out.println("Order: " + order.orderId());
        System.out.println("Customer: " + customer.name() + " (" + customer.type() + ")");
        
        // ビジネスロジックの実行
        var total = ECommerceDomain.OrderService.calculateTotal(order);
        System.out.println("Total: $" + total);
        
        var discountedTotal = ECommerceDomain.OrderService.applyDiscount(order, customer);
        System.out.println("After discount: $" + discountedTotal);
        
        // 注文検証
        var validation = ECommerceDomain.OrderService.validateOrder(order, inventory);
        System.out.println("Validation: " + (validation.valid() ? "PASS" : "FAIL"));
        if (!validation.valid()) {
            validation.errors().forEach(error -> System.out.println("  - " + error));
        }
        
        // ステータス更新
        var confirmedOrder = ECommerceDomain.OrderService.updateStatus(order, ECommerceDomain.OrderStatus.CONFIRMED);
        System.out.println("Updated status: " + confirmedOrder.status());
    }
    
    private static void demonstrateValidatedRecords() {
        System.out.println("\n=== Validated Records Demo ===");
        
        // ValidatedEmail
        try {
            var email = new ValidatedEmail("Alice.Smith@Example.COM");
            System.out.println("Email: " + email.value());
            System.out.println("Domain: " + email.domain());
            System.out.println("Local part: " + email.localPart());
            System.out.println("Is business email: " + email.isBusinessEmail());
            
            // 無効なメール
            new ValidatedEmail("invalid-email");
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        
        // Configuration
        var config = Configuration.createDefault()
            .withHost("api.example.com")
            .withPort(443)
            .withSsl(true)
            .withTimeout(Duration.ofSeconds(60))
            .withProperty("api.key", "secret123")
            .withProperty("api.version", "v2");
        
        System.out.println("\nConfiguration URL: " + config.getUrl());
        System.out.println("Properties: " + config.properties());
    }
}