# 付録B.19: Recordsとモダンなデータ指向プログラミング

## 概要

本付録では、Java 14で正式導入されたRecordsと、それを活用したデータ指向プログラミング（Data-Oriented Programming, DOP）について詳細に解説します。Recordsがもたらす新しいプログラミングパラダイムと、パターンマッチングとの統合による表現力の向上について学びます。

**対象読者**: Recordsの基本を理解し、モダンなJavaプログラミングに興味がある開発者  
**前提知識**: 第12章「Records」の内容、基本的なオブジェクト指向プログラミング  
**関連章**: 第12章、第6章（不変性）、第11章（関数型プログラミング）

---

## データ指向プログラミング（DOP）の概念

### DOPの基本原則

```java
// 従来のOOP（オブジェクト指向）アプローチ
public class OOPApproach {
    static abstract class Shape {
        abstract double area();
        abstract double perimeter();
    }
    
    static class Circle extends Shape {
        private final double radius;
        
        Circle(double radius) {
            this.radius = radius;
        }
        
        @Override
        double area() {
            return Math.PI * radius * radius;
        }
        
        @Override
        double perimeter() {
            return 2 * Math.PI * radius;
        }
    }
    
    static class Rectangle extends Shape {
        private final double width, height;
        
        Rectangle(double width, double height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        double area() {
            return width * height;
        }
        
        @Override
        double perimeter() {
            return 2 * (width + height);
        }
    }
}

// DOP（データ指向）アプローチ
public class DOPApproach {
    // データは単純なレコード
    sealed interface Shape permits Circle, Rectangle, Triangle {}
    
    record Circle(double radius) implements Shape {}
    record Rectangle(double width, double height) implements Shape {}
    record Triangle(double a, double b, double c) implements Shape {}
    
    // 操作は独立した関数
    static double area(Shape shape) {
        return switch (shape) {
            case Circle(var r) -> Math.PI * r * r;
            case Rectangle(var w, var h) -> w * h;
            case Triangle(var a, var b, var c) -> {
                double s = (a + b + c) / 2;
                yield Math.sqrt(s * (s - a) * (s - b) * (s - c));
            }
        };
    }
    
    static double perimeter(Shape shape) {
        return switch (shape) {
            case Circle(var r) -> 2 * Math.PI * r;
            case Rectangle(var w, var h) -> 2 * (w + h);
            case Triangle(var a, var b, var c) -> a + b + c;
        };
    }
}
```

### データとロジックの分離

```java
// データモデル（純粋なデータ）
public class DataModel {
    // ドメインモデル
    record Customer(String id, String name, CustomerType type) {}
    record Order(String orderId, String customerId, List<OrderItem> items, OrderStatus status) {}
    record OrderItem(String productId, int quantity, BigDecimal price) {}
    record Product(String id, String name, BigDecimal price, int stock) {}
    
    enum CustomerType { REGULAR, PREMIUM, VIP }
    enum OrderStatus { PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED }
}

// ビジネスロジック（純粋関数）
public class BusinessLogic {
    static BigDecimal calculateTotal(Order order) {
        return order.items().stream()
            .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    static BigDecimal applyDiscount(Order order, Customer customer) {
        var total = calculateTotal(order);
        var discountRate = switch (customer.type()) {
            case REGULAR -> BigDecimal.ZERO;
            case PREMIUM -> new BigDecimal("0.10");
            case VIP -> new BigDecimal("0.20");
        };
        return total.multiply(BigDecimal.ONE.subtract(discountRate));
    }
    
    static Order updateStatus(Order order, OrderStatus newStatus) {
        return new Order(order.orderId(), order.customerId(), order.items(), newStatus);
    }
    
    static Result<Order> validateOrder(Order order, Map<String, Product> inventory) {
        var errors = new ArrayList<String>();
        
        for (var item : order.items()) {
            var product = inventory.get(item.productId());
            if (product == null) {
                errors.add("Product not found: " + item.productId());
            } else if (product.stock() < item.quantity()) {
                errors.add("Insufficient stock for: " + product.name());
            }
        }
        
        return errors.isEmpty() 
            ? Result.success(order)
            : Result.failure(errors);
    }
}
```

---

## 高度なパターンマッチング

### ネストしたパターンマッチング

```java
// 複雑なデータ構造のパターンマッチング
public class AdvancedPatternMatching {
    // ネストしたレコード構造
    record Address(String street, String city, String country) {}
    record Person(String name, int age, Address address) {}
    record Company(String name, Person ceo, List<Person> employees) {}
    
    // 深いパターンマッチング
    static String getLocation(Object obj) {
        return switch (obj) {
            // ネストしたレコードの分解
            case Person(var name, var age, Address(var street, var city, "Japan")) ->
                name + " lives in " + city + ", Japan";
            
            case Person(var name, var age, Address(var street, var city, var country)) ->
                name + " lives in " + country;
            
            // リストのパターンマッチング（Java 19+）
            case Company(var name, var ceo, List<Person> employees) when employees.size() > 100 ->
                name + " is a large company";
            
            case Company(var name, Person(var ceoName, _, _), var employees) ->
                name + " is led by " + ceoName;
            
            default -> "Unknown";
        };
    }
    
    // ガード条件付きパターンマッチング
    static String categorize(Person person) {
        return switch (person) {
            case Person(var name, var age, _) when age < 18 -> name + " is a minor";
            case Person(var name, var age, _) when age >= 65 -> name + " is a senior";
            case Person(var name, var age, Address(_, _, "Japan")) when age >= 20 ->
                name + " can drink in Japan";
            case Person(var name, _, _) -> name + " is an adult";
        };
    }
}
```

### レコードパターンとsealed階層

```java
// 代数的データ型の実装
public class AlgebraicDataTypes {
    // 式の表現
    sealed interface Expr permits Const, Add, Mul, Var {}
    
    record Const(double value) implements Expr {}
    record Add(Expr left, Expr right) implements Expr {}
    record Mul(Expr left, Expr right) implements Expr {}
    record Var(String name) implements Expr {}
    
    // 式の評価
    static double eval(Expr expr, Map<String, Double> env) {
        return switch (expr) {
            case Const(var value) -> value;
            case Add(var left, var right) -> eval(left, env) + eval(right, env);
            case Mul(var left, var right) -> eval(left, env) * eval(right, env);
            case Var(var name) -> env.getOrDefault(name, 0.0);
        };
    }
    
    // 式の簡約
    static Expr simplify(Expr expr) {
        return switch (expr) {
            case Add(Const(var a), Const(var b)) -> new Const(a + b);
            case Add(Const(0), var e) -> simplify(e);
            case Add(var e, Const(0)) -> simplify(e);
            case Add(var left, var right) -> new Add(simplify(left), simplify(right));
            
            case Mul(Const(var a), Const(var b)) -> new Const(a * b);
            case Mul(Const(0), _) -> new Const(0);
            case Mul(_, Const(0)) -> new Const(0);
            case Mul(Const(1), var e) -> simplify(e);
            case Mul(var e, Const(1)) -> simplify(e);
            case Mul(var left, var right) -> new Mul(simplify(left), simplify(right));
            
            default -> expr;
        };
    }
    
    // 式の文字列表現
    static String toString(Expr expr) {
        return switch (expr) {
            case Const(var value) -> String.valueOf(value);
            case Add(var left, var right) -> "(" + toString(left) + " + " + toString(right) + ")";
            case Mul(var left, var right) -> "(" + toString(left) + " * " + toString(right) + ")";
            case Var(var name) -> name;
        };
    }
}
```

---

## Recordsの内部実装

### コンパイラが生成するコード

```java
// ソースコード
public record Point(int x, int y) {}

// コンパイラが生成する実際のコード（概念的）
public final class Point extends Record {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int x() { return x; }
    public int y() { return y; }
    
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Point other &&
               x == other.x && y == other.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "Point[x=" + x + ", y=" + y + "]";
    }
    
    // レコード特有のメソッド
    @Override
    public final Object[] componentArray() {
        return new Object[] { x, y };
    }
}
```

### カスタマイズと検証

```java
public record ValidatedEmail(String value) {
    // コンパクトコンストラクタで検証
    public ValidatedEmail {
        if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
        value = value.toLowerCase();  // 正規化
    }
    
    // 追加のメソッド
    public String domain() {
        return value.substring(value.indexOf('@') + 1);
    }
    
    public String localPart() {
        return value.substring(0, value.indexOf('@'));
    }
}

// 不変性を保ちながらの更新
public record Configuration(
    String host,
    int port,
    boolean useSsl,
    Duration timeout
) {
    // ビルダーパターン風のwithメソッド
    public Configuration withHost(String newHost) {
        return new Configuration(newHost, port, useSsl, timeout);
    }
    
    public Configuration withPort(int newPort) {
        return new Configuration(host, newPort, useSsl, timeout);
    }
    
    public Configuration withSsl(boolean newUseSsl) {
        return new Configuration(host, port, newUseSsl, timeout);
    }
    
    public Configuration withTimeout(Duration newTimeout) {
        return new Configuration(host, port, useSsl, newTimeout);
    }
    
    // デフォルト値を持つファクトリメソッド
    public static Configuration createDefault() {
        return new Configuration("localhost", 8080, false, Duration.ofSeconds(30));
    }
}
```

---

## 実践的なデータパイプライン

### ストリーム処理との統合

```java
public class DataPipeline {
    // イベントソーシング風のデータモデル
    sealed interface Event permits UserCreated, UserUpdated, UserDeleted {}
    
    record UserCreated(String userId, String name, Instant timestamp) implements Event {}
    record UserUpdated(String userId, Map<String, Object> changes, Instant timestamp) implements Event {}
    record UserDeleted(String userId, Instant timestamp) implements Event {}
    
    record UserSnapshot(String userId, String name, boolean active, Instant lastModified) {}
    
    // イベントストリームの処理
    static Map<String, UserSnapshot> processEvents(Stream<Event> events) {
        return events.reduce(
            new HashMap<String, UserSnapshot>(),
            (snapshots, event) -> {
                var updated = new HashMap<>(snapshots);
                switch (event) {
                    case UserCreated(var id, var name, var time) ->
                        updated.put(id, new UserSnapshot(id, name, true, time));
                    
                    case UserUpdated(var id, var changes, var time) -> {
                        var current = updated.get(id);
                        if (current != null) {
                            var newName = (String) changes.getOrDefault("name", current.name());
                            updated.put(id, new UserSnapshot(id, newName, current.active(), time));
                        }
                    }
                    
                    case UserDeleted(var id, var time) -> {
                        var current = updated.get(id);
                        if (current != null) {
                            updated.put(id, new UserSnapshot(id, current.name(), false, time));
                        }
                    }
                }
                return updated;
            },
            (m1, m2) -> { m1.putAll(m2); return m1; }
        );
    }
}
```

### JSONシリアライゼーション

```java
// Jackson統合
public class JsonSerialization {
    record ApiResponse<T>(
        boolean success,
        T data,
        List<String> errors,
        Instant timestamp
    ) {
        // 成功レスポンスのファクトリ
        static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(true, data, List.of(), Instant.now());
        }
        
        // エラーレスポンスのファクトリ
        static <T> ApiResponse<T> error(List<String> errors) {
            return new ApiResponse<>(false, null, errors, Instant.now());
        }
    }
    
    // カスタムシリアライザ
    @JsonSerialize(using = MoneySerializer.class)
    @JsonDeserialize(using = MoneyDeserializer.class)
    record Money(BigDecimal amount, Currency currency) {
        public Money {
            if (amount.scale() > currency.getDefaultFractionDigits()) {
                amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
            }
        }
        
        public Money add(Money other) {
            if (!currency.equals(other.currency)) {
                throw new IllegalArgumentException("Currency mismatch");
            }
            return new Money(amount.add(other.amount), currency);
        }
    }
    
    static class MoneySerializer extends JsonSerializer<Money> {
        @Override
        public void serialize(Money value, JsonGenerator gen, SerializerProvider serializers) 
            throws IOException {
            gen.writeStartObject();
            gen.writeNumberField("amount", value.amount());
            gen.writeStringField("currency", value.currency().getCurrencyCode());
            gen.writeEndObject();
        }
    }
    
    static class MoneyDeserializer extends JsonDeserializer<Money> {
        @Override
        public Money deserialize(JsonParser p, DeserializationContext ctxt) 
            throws IOException {
            JsonNode node = p.getCodec().readTree(p);
            BigDecimal amount = node.get("amount").decimalValue();
            Currency currency = Currency.getInstance(node.get("currency").asText());
            return new Money(amount, currency);
        }
    }
}
```

---

## パフォーマンスとメモリ効率

### レコードの最適化

```java
public class RecordOptimization {
    // インライン候補のレコード
    @jdk.internal.ValueBased  // 将来のValue Typeの候補
    record Point2D(double x, double y) {
        // 小さく、不変で、equalsとhashCodeが値ベース
        
        public double distanceFrom(Point2D other) {
            double dx = x - other.x;
            double dy = y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }
    
    // メモリレイアウトの最適化
    record OptimizedData(
        // プリミティブ型を優先
        long id,
        int count,
        boolean active,
        // 参照型は後に
        String name,
        List<String> tags
    ) {}
    
    // オブジェクトプーリング
    static class RecordPool<T extends Record> {
        private final Queue<T> pool = new ConcurrentLinkedQueue<>();
        private final Function<T, T> recycler;
        
        RecordPool(Function<T, T> recycler) {
            this.recycler = recycler;
        }
        
        public T acquire(T template) {
            T instance = pool.poll();
            return instance != null ? recycler.apply(instance) : template;
        }
        
        public void release(T instance) {
            pool.offer(instance);
        }
    }
}
```

### ベンチマーク

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class RecordBenchmark {
    
    // 従来のクラス
    static class TraditionalPoint {
        private final int x, y;
        
        TraditionalPoint(int x, int y) {
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
    }
    
    // レコード
    record RecordPoint(int x, int y) {}
    
    @Benchmark
    public TraditionalPoint createTraditional() {
        return new TraditionalPoint(42, 100);
    }
    
    @Benchmark
    public RecordPoint createRecord() {
        return new RecordPoint(42, 100);
    }
    
    @Benchmark
    public boolean equalsTraditional() {
        var p1 = new TraditionalPoint(42, 100);
        var p2 = new TraditionalPoint(42, 100);
        return p1.equals(p2);
    }
    
    @Benchmark
    public boolean equalsRecord() {
        var p1 = new RecordPoint(42, 100);
        var p2 = new RecordPoint(42, 100);
        return p1.equals(p2);
    }
}
```

---

## 将来の発展

### Project Valhallaとの統合

```java
// 将来のValue Records（概念的）
public value record ComplexNumber(double real, double imaginary) {
    // Value Typeとして最適化される可能性
    
    public ComplexNumber add(ComplexNumber other) {
        return new ComplexNumber(
            real + other.real,
            imaginary + other.imaginary
        );
    }
    
    public ComplexNumber multiply(ComplexNumber other) {
        return new ComplexNumber(
            real * other.real - imaginary * other.imaginary,
            real * other.imaginary + imaginary * other.real
        );
    }
    
    public double magnitude() {
        return Math.sqrt(real * real + imaginary * imaginary);
    }
}

// Inline Types（将来の機能）
public inline record Vec3(float x, float y, float z) {
    // スタック割り当て可能
    // 配列がフラットになる
}
```

---

## まとめ

Recordsとデータ指向プログラミングにより：

1. **簡潔性**: ボイラープレートコードの大幅な削減
2. **表現力**: パターンマッチングとの組み合わせによる強力な表現
3. **不変性**: デフォルトで不変なデータ構造
4. **パフォーマンス**: 将来のValue Type最適化への道
5. **保守性**: データとロジックの明確な分離

これらの技術は、特に関数型プログラミングスタイルや、データ変換を多用するアプリケーションにおいて威力を発揮します。Recordsは単なる構文糖ではなく、Javaプログラミングの新しいパラダイムを可能にする重要な機能です。