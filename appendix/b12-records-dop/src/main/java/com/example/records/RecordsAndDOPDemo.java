package com.example.records;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * RecordsとData-Oriented Programming (DOP)のデモンストレーション
 * ボイラープレートコードの削減とデータ中心設計の実装
 */
public class RecordsAndDOPDemo {
    
    // ========== 基本的なRecordの定義 ==========
    
    /**
     * シンプルなUserレコード
     * 従来なら100行必要なコードが1行で実装可能
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
     * DOPアプローチでの図形処理
     * データとロジックを分離
     */
    public static class ShapeProcessing {
        // データは単純なレコード（sealed階層）
        public sealed interface Shape permits Circle, Rectangle, Triangle {}
        
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
        
        // 操作は独立した関数（パターンマッチング使用）
        public static double area(Shape shape) {
            return switch (shape) {
                case Circle(var r) -> Math.PI * r * r;
                case Rectangle(var w, var h) -> w * h;
                case Triangle(var a, var b, var c) -> {
                    double s = (a + b + c) / 2;
                    yield Math.sqrt(s * (s - a) * (s - b) * (s - c));
                }
            };
        }
        
        public static double perimeter(Shape shape) {
            return switch (shape) {
                case Circle(var r) -> 2 * Math.PI * r;
                case Rectangle(var w, var h) -> 2 * (w + h);
                case Triangle(var a, var b, var c) -> a + b + c;
            };
        }
        
        public static String describe(Shape shape) {
            return switch (shape) {
                case Circle(var r) -> String.format("Circle with radius %.2f", r);
                case Rectangle(var w, var h) -> String.format("Rectangle %.2f x %.2f", w, h);
                case Triangle(var a, var b, var c) -> String.format("Triangle with sides %.2f, %.2f, %.2f", a, b, c);
            };
        }
        
        public static Shape scale(Shape shape, double factor) {
            if (factor <= 0) throw new IllegalArgumentException("Scale factor must be positive");
            
            return switch (shape) {
                case Circle(var r) -> new Circle(r * factor);
                case Rectangle(var w, var h) -> new Rectangle(w * factor, h * factor);
                case Triangle(var a, var b, var c) -> new Triangle(a * factor, b * factor, c * factor);
            };
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
                var discountRate = switch (customer.type()) {
                    case REGULAR -> BigDecimal.ZERO;
                    case PREMIUM -> new BigDecimal("0.10");
                    case VIP -> new BigDecimal("0.20");
                };
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
            
            public static Order mergeOrders(Order order1, Order order2) {
                if (!order1.customerId().equals(order2.customerId())) {
                    throw new IllegalArgumentException("Cannot merge orders from different customers");
                }
                
                var mergedItems = new ArrayList<>(order1.items());
                mergedItems.addAll(order2.items());
                
                return new Order(
                    UUID.randomUUID().toString(),
                    order1.customerId(),
                    mergedItems,
                    OrderStatus.PENDING,
                    LocalDateTime.now()
                );
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
    
    // ========== 高度なパターンマッチング ==========
    
    /**
     * ネストしたレコードとパターンマッチング
     */
    public static class AdvancedPatternMatching {
        public record Address(String street, String city, String country, String postalCode) {}
        
        public record Person(String name, int age, Address address, List<String> hobbies) {}
        
        public record Company(String name, Person ceo, List<Person> employees, Address headquarters) {}
        
        // 深いパターンマッチング
        public static String getLocationInfo(Object entity) {
            return switch (entity) {
                // ネストしたレコードの分解
                case Person(var name, var age, Address(var street, var city, "Japan", _), _) ->
                    name + " lives in " + city + ", Japan";
                
                case Person(var name, var age, Address(_, _, var country, _), _) ->
                    name + " lives in " + country;
                
                // ガード条件付きパターンマッチング
                case Company(var name, Person(var ceoName, var ceoAge, _, _), var employees, _) 
                    when employees.size() > 100 ->
                    name + " is a large company led by " + ceoName;
                
                case Company(var name, Person(var ceoName, _, _, _), var employees, _) ->
                    name + " is led by " + ceoName + " with " + employees.size() + " employees";
                
                default -> "Unknown entity";
            };
        }
        
        // 年齢カテゴリの判定
        public static String categorizeByAge(Person person) {
            return switch (person) {
                case Person(var name, var age, _, _) when age < 18 -> 
                    name + " is a minor";
                case Person(var name, var age, _, _) when age >= 65 -> 
                    name + " is a senior citizen";
                case Person(var name, var age, Address(_, _, "Japan", _), _) when age >= 20 ->
                    name + " is an adult in Japan (can drink)";
                case Person(var name, var age, Address(_, _, "USA", _), _) when age >= 21 ->
                    name + " is an adult in USA (can drink)";
                case Person(var name, _, _, _) -> 
                    name + " is an adult";
            };
        }
    }
    
    // ========== 代数的データ型 ==========
    
    /**
     * 式の表現（代数的データ型）
     */
    public static class AlgebraicExpressions {
        // 式の定義
        public sealed interface Expr permits Const, Var, Add, Mul, Neg {}
        
        public record Const(double value) implements Expr {}
        public record Var(String name) implements Expr {}
        public record Add(Expr left, Expr right) implements Expr {}
        public record Mul(Expr left, Expr right) implements Expr {}
        public record Neg(Expr expr) implements Expr {}
        
        // 式の評価
        public static double eval(Expr expr, Map<String, Double> env) {
            return switch (expr) {
                case Const(var value) -> value;
                case Var(var name) -> env.getOrDefault(name, 0.0);
                case Add(var left, var right) -> eval(left, env) + eval(right, env);
                case Mul(var left, var right) -> eval(left, env) * eval(right, env);
                case Neg(var e) -> -eval(e, env);
            };
        }
        
        // 式の簡約
        public static Expr simplify(Expr expr) {
            return switch (expr) {
                // 定数の折りたたみ
                case Add(Const(var a), Const(var b)) -> new Const(a + b);
                case Mul(Const(var a), Const(var b)) -> new Const(a * b);
                case Neg(Const(var a)) -> new Const(-a);
                
                // 恒等式
                case Add(Const(0), var e) -> simplify(e);
                case Add(var e, Const(0)) -> simplify(e);
                case Mul(Const(0), _) -> new Const(0);
                case Mul(_, Const(0)) -> new Const(0);
                case Mul(Const(1), var e) -> simplify(e);
                case Mul(var e, Const(1)) -> simplify(e);
                case Neg(Neg(var e)) -> simplify(e);
                
                // 再帰的な簡約
                case Add(var left, var right) -> {
                    var simplLeft = simplify(left);
                    var simplRight = simplify(right);
                    yield (simplLeft.equals(left) && simplRight.equals(right)) 
                        ? expr 
                        : simplify(new Add(simplLeft, simplRight));
                }
                
                case Mul(var left, var right) -> {
                    var simplLeft = simplify(left);
                    var simplRight = simplify(right);
                    yield (simplLeft.equals(left) && simplRight.equals(right)) 
                        ? expr 
                        : simplify(new Mul(simplLeft, simplRight));
                }
                
                case Neg(var e) -> {
                    var simplified = simplify(e);
                    yield simplified.equals(e) ? expr : simplify(new Neg(simplified));
                }
                
                default -> expr;
            };
        }
        
        // 式の文字列表現
        public static String format(Expr expr) {
            return switch (expr) {
                case Const(var value) -> String.format("%.2f", value);
                case Var(var name) -> name;
                case Add(var left, var right) -> "(" + format(left) + " + " + format(right) + ")";
                case Mul(var left, var right) -> "(" + format(left) + " * " + format(right) + ")";
                case Neg(var e) -> "-" + format(e);
            };
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
            
            // toString パフォーマンス
            long traditionalToString = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    tp1.toString();
                }
            });
            
            long recordToString = measureTime(() -> {
                for (int i = 0; i < iterations; i++) {
                    rp1.toString();
                }
            });
            
            System.out.printf("ToString - Traditional: %d ms, Record: %d ms%n", 
                            traditionalToString, recordToString);
        }
        
        private static long measureTime(Runnable task) {
            long start = System.currentTimeMillis();
            task.run();
            return System.currentTimeMillis() - start;
        }
    }
    
    // ========== メインメソッド ==========
    
    public static void main(String[] args) {
        System.out.println("Records and Data-Oriented Programming Demo");
        System.out.println("==========================================");
        
        // 基本的なRecordのデモ
        demonstrateBasicRecords();
        
        // DOPでの図形処理
        demonstrateShapeProcessing();
        
        // ビジネスドメインモデル
        demonstrateECommerce();
        
        // 高度なパターンマッチング
        demonstrateAdvancedPatternMatching();
        
        // 代数的データ型
        demonstrateAlgebraicExpressions();
        
        // カスタム検証付きレコード
        demonstrateValidatedRecords();
        
        // パフォーマンス比較
        PerformanceComparison.comparePerformance();
        
        System.out.println("\n🎯 Key Insights:");
        System.out.println("✓ Records eliminate boilerplate code (70-90% reduction)");
        System.out.println("✓ Data-Oriented Programming separates data from logic");
        System.out.println("✓ Pattern matching enables powerful data transformations");
        System.out.println("✓ Sealed hierarchies provide exhaustive type checking");
        System.out.println("✓ Records are immutable by default, promoting thread safety");
        
        System.out.println("\n⚡ Best Practices:");
        System.out.println("• Use compact constructors for validation");
        System.out.println("• Leverage pattern matching for data processing");
        System.out.println("• Keep business logic in separate service classes");
        System.out.println("• Use sealed hierarchies for algebraic data types");
        System.out.println("• Implement 'with' methods for immutable updates");
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
            
            var scaled = ShapeProcessing.scale(shape, 2.0);
            System.out.println("Scaled 2x: " + ShapeProcessing.describe(scaled));
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
    
    private static void demonstrateAdvancedPatternMatching() {
        System.out.println("\n=== Advanced Pattern Matching Demo ===");
        
        var tokyoAddress = new AdvancedPatternMatching.Address("1-1 Shibuya", "Tokyo", "Japan", "150-0002");
        var nyAddress = new AdvancedPatternMatching.Address("5th Avenue", "New York", "USA", "10001");
        
        var alice = new AdvancedPatternMatching.Person("Alice", 25, tokyoAddress, List.of("reading", "gaming"));
        var bob = new AdvancedPatternMatching.Person("Bob", 70, nyAddress, List.of("gardening"));
        var charlie = new AdvancedPatternMatching.Person("Charlie", 16, tokyoAddress, List.of("sports"));
        
        var techCorp = new AdvancedPatternMatching.Company(
            "TechCorp",
            alice,
            IntStream.range(0, 150)
                .mapToObj(i -> new AdvancedPatternMatching.Person("Employee" + i, 30, tokyoAddress, List.of()))
                .collect(Collectors.toList()),
            tokyoAddress
        );
        
        // 深いパターンマッチング
        System.out.println(AdvancedPatternMatching.getLocationInfo(alice));
        System.out.println(AdvancedPatternMatching.getLocationInfo(bob));
        System.out.println(AdvancedPatternMatching.getLocationInfo(techCorp));
        
        // 年齢カテゴリ判定
        System.out.println(AdvancedPatternMatching.categorizeByAge(alice));
        System.out.println(AdvancedPatternMatching.categorizeByAge(bob));
        System.out.println(AdvancedPatternMatching.categorizeByAge(charlie));
    }
    
    private static void demonstrateAlgebraicExpressions() {
        System.out.println("\n=== Algebraic Expressions Demo ===");
        
        // 式の構築: (x * 2) + (y + 3)
        var expr = new AlgebraicExpressions.Add(
            new AlgebraicExpressions.Mul(
                new AlgebraicExpressions.Var("x"),
                new AlgebraicExpressions.Const(2)
            ),
            new AlgebraicExpressions.Add(
                new AlgebraicExpressions.Var("y"),
                new AlgebraicExpressions.Const(3)
            )
        );
        
        System.out.println("Expression: " + AlgebraicExpressions.format(expr));
        
        // 評価
        var env = Map.of("x", 5.0, "y", 7.0);
        var result = AlgebraicExpressions.eval(expr, env);
        System.out.println("Evaluation with x=5, y=7: " + result);
        
        // 簡約
        var expr2 = new AlgebraicExpressions.Add(
            new AlgebraicExpressions.Mul(
                new AlgebraicExpressions.Const(3),
                new AlgebraicExpressions.Const(4)
            ),
            new AlgebraicExpressions.Mul(
                new AlgebraicExpressions.Var("x"),
                new AlgebraicExpressions.Const(0)
            )
        );
        
        System.out.println("\nOriginal: " + AlgebraicExpressions.format(expr2));
        var simplified = AlgebraicExpressions.simplify(expr2);
        System.out.println("Simplified: " + AlgebraicExpressions.format(simplified));
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