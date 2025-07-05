package chapter12.solutions;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 不変データ構造を実装するRecordクラス集
 * 
 * 学習内容：
 * - Recordによる不変性の実現
 * - 複雑なデータ構造でのイミュータビリティ
 * - ファクトリメソッドパターン
 * - Builder パターンとの組み合わせ
 */
public class ImmutableData {
    
    /**
     * 座標を表すRecord
     */
    public record Point(double x, double y) {
        
        public Point {
            // バリデーション
            if (Double.isNaN(x) || Double.isNaN(y)) {
                throw new IllegalArgumentException("Coordinates cannot be NaN");
            }
            if (Double.isInfinite(x) || Double.isInfinite(y)) {
                throw new IllegalArgumentException("Coordinates cannot be infinite");
            }
        }
        
        /**
         * 原点からの距離を計算
         */
        public double distanceFromOrigin() {
            return Math.sqrt(x * x + y * y);
        }
        
        /**
         * 他の点からの距離を計算
         */
        public double distanceFrom(Point other) {
            Objects.requireNonNull(other, "Other point cannot be null");
            double dx = x - other.x();
            double dy = y - other.y();
            return Math.sqrt(dx * dx + dy * dy);
        }
        
        /**
         * 点を移動した新しい点を作成
         */
        public Point translate(double dx, double dy) {
            return new Point(x + dx, y + dy);
        }
        
        /**
         * 点を拡大/縮小した新しい点を作成
         */
        public Point scale(double factor) {
            return new Point(x * factor, y * factor);
        }
        
        /**
         * 点を回転した新しい点を作成（角度はラジアン）
         */
        public Point rotate(double angle) {
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            return new Point(x * cos - y * sin, x * sin + y * cos);
        }
        
        /**
         * 原点の定数
         */
        public static final Point ORIGIN = new Point(0, 0);
        
        /**
         * 単位ベクトル
         */
        public static final Point UNIT_X = new Point(1, 0);
        public static final Point UNIT_Y = new Point(0, 1);
    }
    
    /**
     * 矩形を表すRecord
     */
    public record Rectangle(Point topLeft, Point bottomRight) {
        
        public Rectangle {
            Objects.requireNonNull(topLeft, "Top-left point cannot be null");
            Objects.requireNonNull(bottomRight, "Bottom-right point cannot be null");
            
            if (topLeft.x() >= bottomRight.x() || topLeft.y() >= bottomRight.y()) {
                throw new IllegalArgumentException("Invalid rectangle coordinates");
            }
        }
        
        /**
         * 便利なコンストラクタ
         */
        public Rectangle(double x1, double y1, double x2, double y2) {
            this(new Point(x1, y1), new Point(x2, y2));
        }
        
        /**
         * 幅を取得
         */
        public double width() {
            return bottomRight.x() - topLeft.x();
        }
        
        /**
         * 高さを取得
         */
        public double height() {
            return bottomRight.y() - topLeft.y();
        }
        
        /**
         * 面積を計算
         */
        public double area() {
            return width() * height();
        }
        
        /**
         * 周囲を計算
         */
        public double perimeter() {
            return 2 * (width() + height());
        }
        
        /**
         * 中心点を取得
         */
        public Point center() {
            return new Point(
                (topLeft.x() + bottomRight.x()) / 2,
                (topLeft.y() + bottomRight.y()) / 2
            );
        }
        
        /**
         * 点が矩形内にあるかチェック
         */
        public boolean contains(Point point) {
            return point.x() >= topLeft.x() && point.x() <= bottomRight.x() &&
                   point.y() >= topLeft.y() && point.y() <= bottomRight.y();
        }
        
        /**
         * 矩形を移動
         */
        public Rectangle translate(double dx, double dy) {
            return new Rectangle(
                topLeft.translate(dx, dy),
                bottomRight.translate(dx, dy)
            );
        }
        
        /**
         * 矩形を拡大/縮小
         */
        public Rectangle scale(double factor) {
            Point center = center();
            Point newTopLeft = center.translate(
                (topLeft.x() - center.x()) * factor,
                (topLeft.y() - center.y()) * factor
            );
            Point newBottomRight = center.translate(
                (bottomRight.x() - center.x()) * factor,
                (bottomRight.y() - center.y()) * factor
            );
            return new Rectangle(newTopLeft, newBottomRight);
        }
    }
    
    /**
     * 商品を表すRecord
     */
    public record Product(
        String id,
        String name,
        String category,
        double price,
        Set<String> tags,
        Map<String, String> attributes
    ) {
        
        public Product {
            Objects.requireNonNull(id, "Product ID cannot be null");
            Objects.requireNonNull(name, "Product name cannot be null");
            Objects.requireNonNull(category, "Product category cannot be null");
            Objects.requireNonNull(tags, "Product tags cannot be null");
            Objects.requireNonNull(attributes, "Product attributes cannot be null");
            
            if (id.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be empty");
            }
            if (name.trim().isEmpty()) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
            if (price < 0) {
                throw new IllegalArgumentException("Product price cannot be negative");
            }
            
            // 不変性を保つためにコピーを作成
            tags = Set.copyOf(tags);
            attributes = Map.copyOf(attributes);
        }
        
        /**
         * 便利なコンストラクタ
         */
        public Product(String id, String name, String category, double price) {
            this(id, name, category, price, Set.of(), Map.of());
        }
        
        /**
         * タグを追加した新しい商品を作成
         */
        public Product addTag(String tag) {
            Set<String> newTags = new java.util.HashSet<>(tags);
            newTags.add(tag);
            return new Product(id, name, category, price, newTags, attributes);
        }
        
        /**
         * タグを削除した新しい商品を作成
         */
        public Product removeTag(String tag) {
            Set<String> newTags = tags.stream()
                .filter(t -> !t.equals(tag))
                .collect(Collectors.toSet());
            return new Product(id, name, category, price, newTags, attributes);
        }
        
        /**
         * 属性を追加した新しい商品を作成
         */
        public Product addAttribute(String key, String value) {
            Map<String, String> newAttributes = new java.util.HashMap<>(attributes);
            newAttributes.put(key, value);
            return new Product(id, name, category, price, tags, newAttributes);
        }
        
        /**
         * 属性を削除した新しい商品を作成
         */
        public Product removeAttribute(String key) {
            Map<String, String> newAttributes = attributes.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(key))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return new Product(id, name, category, price, tags, newAttributes);
        }
        
        /**
         * 価格を更新した新しい商品を作成
         */
        public Product withPrice(double newPrice) {
            return new Product(id, name, category, newPrice, tags, attributes);
        }
        
        /**
         * 割引価格を計算
         */
        public double getDiscountedPrice(double discountRate) {
            if (discountRate < 0 || discountRate > 1) {
                throw new IllegalArgumentException("Discount rate must be between 0 and 1");
            }
            return price * (1 - discountRate);
        }
        
        /**
         * 特定のタグを持つかチェック
         */
        public boolean hasTag(String tag) {
            return tags.contains(tag);
        }
        
        /**
         * 特定の属性を持つかチェック
         */
        public boolean hasAttribute(String key) {
            return attributes.containsKey(key);
        }
        
        /**
         * 属性値を取得
         */
        public String getAttribute(String key) {
            return attributes.get(key);
        }
        
        /**
         * 特定のカテゴリかチェック
         */
        public boolean isCategory(String category) {
            return this.category.equals(category);
        }
        
        /**
         * 価格範囲内かチェック
         */
        public boolean isPriceInRange(double minPrice, double maxPrice) {
            return price >= minPrice && price <= maxPrice;
        }
    }
    
    /**
     * 注文項目を表すRecord
     */
    public record OrderItem(Product product, int quantity, double unitPrice) {
        
        public OrderItem {
            Objects.requireNonNull(product, "Product cannot be null");
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            if (unitPrice < 0) {
                throw new IllegalArgumentException("Unit price cannot be negative");
            }
        }
        
        /**
         * 便利なコンストラクタ（商品価格を使用）
         */
        public OrderItem(Product product, int quantity) {
            this(product, quantity, product.price());
        }
        
        /**
         * 総額を計算
         */
        public double totalPrice() {
            return quantity * unitPrice;
        }
        
        /**
         * 数量を変更した新しい注文項目を作成
         */
        public OrderItem withQuantity(int newQuantity) {
            return new OrderItem(product, newQuantity, unitPrice);
        }
        
        /**
         * 単価を変更した新しい注文項目を作成
         */
        public OrderItem withUnitPrice(double newUnitPrice) {
            return new OrderItem(product, quantity, newUnitPrice);
        }
        
        /**
         * 割引を適用した新しい注文項目を作成
         */
        public OrderItem applyDiscount(double discountRate) {
            if (discountRate < 0 || discountRate > 1) {
                throw new IllegalArgumentException("Discount rate must be between 0 and 1");
            }
            return new OrderItem(product, quantity, unitPrice * (1 - discountRate));
        }
    }
    
    /**
     * 注文を表すRecord
     */
    public record Order(
        String orderId,
        String customerId,
        LocalDateTime orderDate,
        List<OrderItem> items,
        String status
    ) {
        
        public Order {
            Objects.requireNonNull(orderId, "Order ID cannot be null");
            Objects.requireNonNull(customerId, "Customer ID cannot be null");
            Objects.requireNonNull(orderDate, "Order date cannot be null");
            Objects.requireNonNull(items, "Order items cannot be null");
            Objects.requireNonNull(status, "Order status cannot be null");
            
            if (orderId.trim().isEmpty()) {
                throw new IllegalArgumentException("Order ID cannot be empty");
            }
            if (customerId.trim().isEmpty()) {
                throw new IllegalArgumentException("Customer ID cannot be empty");
            }
            if (items.isEmpty()) {
                throw new IllegalArgumentException("Order must have at least one item");
            }
            
            // 不変性を保つためにコピーを作成
            items = List.copyOf(items);
        }
        
        /**
         * 便利なコンストラクタ
         */
        public Order(String orderId, String customerId, List<OrderItem> items) {
            this(orderId, customerId, LocalDateTime.now(), items, "PENDING");
        }
        
        /**
         * 注文総額を計算
         */
        public double totalAmount() {
            return items.stream()
                .mapToDouble(OrderItem::totalPrice)
                .sum();
        }
        
        /**
         * 注文項目数を取得
         */
        public int itemCount() {
            return items.size();
        }
        
        /**
         * 商品の総数量を取得
         */
        public int totalQuantity() {
            return items.stream()
                .mapToInt(OrderItem::quantity)
                .sum();
        }
        
        /**
         * 項目を追加した新しい注文を作成
         */
        public Order addItem(OrderItem item) {
            List<OrderItem> newItems = new java.util.ArrayList<>(items);
            newItems.add(item);
            return new Order(orderId, customerId, orderDate, newItems, status);
        }
        
        /**
         * 項目を削除した新しい注文を作成
         */
        public Order removeItem(OrderItem item) {
            List<OrderItem> newItems = items.stream()
                .filter(i -> !i.equals(item))
                .collect(Collectors.toList());
            return new Order(orderId, customerId, orderDate, newItems, status);
        }
        
        /**
         * ステータスを変更した新しい注文を作成
         */
        public Order withStatus(String newStatus) {
            return new Order(orderId, customerId, orderDate, items, newStatus);
        }
        
        /**
         * 特定の商品を含むかチェック
         */
        public boolean containsProduct(Product product) {
            return items.stream()
                .anyMatch(item -> item.product().equals(product));
        }
        
        /**
         * 特定のカテゴリの商品を含むかチェック
         */
        public boolean containsCategory(String category) {
            return items.stream()
                .anyMatch(item -> item.product().isCategory(category));
        }
        
        /**
         * 注文の合計重量を計算（重量属性がある場合）
         */
        public double getTotalWeight() {
            return items.stream()
                .mapToDouble(item -> {
                    String weightStr = item.product().getAttribute("weight");
                    if (weightStr != null) {
                        try {
                            return Double.parseDouble(weightStr) * item.quantity();
                        } catch (NumberFormatException e) {
                            return 0.0;
                        }
                    }
                    return 0.0;
                })
                .sum();
        }
        
        /**
         * 注文が完了しているかチェック
         */
        public boolean isCompleted() {
            return "COMPLETED".equals(status);
        }
        
        /**
         * 注文がキャンセルされているかチェック
         */
        public boolean isCancelled() {
            return "CANCELLED".equals(status);
        }
        
        /**
         * 有効な注文かチェック
         */
        public boolean isValid() {
            return !items.isEmpty() && totalAmount() > 0;
        }
        
        /**
         * 注文をキャンセルした新しい注文を作成
         */
        public Order cancel() {
            return withStatus("CANCELLED");
        }
        
        /**
         * 注文を完了した新しい注文を作成
         */
        public Order complete() {
            return withStatus("COMPLETED");
        }
        
        /**
         * 注文を確定した新しい注文を作成
         */
        public Order confirm() {
            return withStatus("CONFIRMED");
        }
        
        /**
         * 注文を発送した新しい注文を作成
         */
        public Order ship() {
            return withStatus("SHIPPED");
        }
    }
    
    /**
     * 注文のビルダークラス
     */
    public static class OrderBuilder {
        private String orderId;
        private String customerId;
        private LocalDateTime orderDate = LocalDateTime.now();
        private List<OrderItem> items = new java.util.ArrayList<>();
        private String status = "PENDING";
        
        public OrderBuilder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }
        
        public OrderBuilder customerId(String customerId) {
            this.customerId = customerId;
            return this;
        }
        
        public OrderBuilder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }
        
        public OrderBuilder addItem(OrderItem item) {
            this.items.add(item);
            return this;
        }
        
        public OrderBuilder addItem(Product product, int quantity) {
            return addItem(new OrderItem(product, quantity));
        }
        
        public OrderBuilder addItem(Product product, int quantity, double unitPrice) {
            return addItem(new OrderItem(product, quantity, unitPrice));
        }
        
        public OrderBuilder status(String status) {
            this.status = status;
            return this;
        }
        
        public Order build() {
            return new Order(orderId, customerId, orderDate, items, status);
        }
    }
    
    /**
     * 注文ビルダーを作成
     */
    public static OrderBuilder orderBuilder() {
        return new OrderBuilder();
    }
}