/**
 * リスト9-28
 * OrderServiceクラス
 * 
 * 元ファイル: chapter09-records.md (1193行目)
 */

// 注文処理のドメインモデル
public record Customer(String id, String name, String email) {}

public record Product(String id, String name, Money price) {}

public record OrderItem(Product product, int quantity) {
    public Money totalPrice() {
        return product.price().multiply(BigDecimal.valueOf(quantity));
    }
}

public record Order(
    String orderId,
    Customer customer,
    List<OrderItem> items,
    OrderStatus status,
    LocalDateTime createdAt
) {
    public Money totalAmount() {
        return items.stream()
            .map(OrderItem::totalPrice)
            .reduce(new Money(BigDecimal.ZERO, Currency.getInstance("JPY")), Money::add);
    }
    
    public int totalItemCount() {
        return items.stream().mapToInt(OrderItem::quantity).sum();
    }
    
    public boolean hasProduct(String productId) {
        return items.stream()
            .anyMatch(item -> item.product().id().equals(productId));
    }
}

// 注文状態の管理
public sealed interface OrderStatus permits Pending, Confirmed, Shipped, Delivered, Cancelled {}
public record Pending() implements OrderStatus {}
public record Confirmed(LocalDateTime confirmedAt, String paymentId) implements OrderStatus {}
public record Shipped(LocalDateTime shippedAt, String trackingNumber) implements OrderStatus {}
public record Delivered(LocalDateTime deliveredAt, String signature) implements OrderStatus {}
public record Cancelled(LocalDateTime cancelledAt, String reason) implements OrderStatus {}

// ビジネスロジックの実装
public class OrderService {
    
    public static String getOrderStatusMessage(Order order) {
        return switch (order.status()) {
            case Pending() -> "ご注文を受け付けました";
            case Confirmed(var confirmedAt, var paymentId) -> 
                "お支払いが確認できました（決済ID: " + paymentId + "）";
            case Shipped(var shippedAt, var trackingNumber) -> 
                "商品を発送しました（追跡番号: " + trackingNumber + "）";
            case Delivered(var deliveredAt, var signature) -> 
                "配達が完了しました（受領者: " + signature + "）";
            case Cancelled(var cancelledAt, var reason) -> 
                "注文がキャンセルされました（理由: " + reason + "）";
        };
    }
    
    public static boolean canBeCancelled(Order order) {
        return switch (order.status()) {
            case Pending(), Confirmed(_, _) -> true;
            case Shipped(_, _), Delivered(_, _), Cancelled(_, _) -> false;
        };
    }
    
    public static Order confirmOrder(Order order, String paymentId) {
        if (!(order.status() instanceof Pending)) {
            throw new IllegalStateException("Only pending orders can be confirmed");
        }
        
        return new Order(
            order.orderId(),
            order.customer(),
            order.items(),
            new Confirmed(LocalDateTime.now(), paymentId),
            order.createdAt()
        );
    }
    
    public static Order shipOrder(Order order, String trackingNumber) {
        if (!(order.status() instanceof Confirmed)) {
            throw new IllegalStateException("Only confirmed orders can be shipped");
        }
        
        return new Order(
            order.orderId(),
            order.customer(),
            order.items(),
            new Shipped(LocalDateTime.now(), trackingNumber),
            order.createdAt()
        );
    }
    
    // 注文レポートの生成
    public static OrderReport generateReport(List<Order> orders, LocalDate date) {
        var ordersOnDate = orders.stream()
            .filter(order -> order.createdAt().toLocalDate().equals(date))
            .collect(Collectors.toList());
        
        int totalOrders = ordersOnDate.size();
        Money totalRevenue = ordersOnDate.stream()
            .filter(order -> !(order.status() instanceof Cancelled))
            .map(Order::totalAmount)
            .reduce(new Money(BigDecimal.ZERO, Currency.getInstance("JPY")), Money::add);
        
        Map<String, Long> statusCounts = ordersOnDate.stream()
            .collect(Collectors.groupingBy(
                order -> order.status().getClass().getSimpleName(),
                Collectors.counting()
            ));
        
        return new OrderReport(date, totalOrders, totalRevenue, statusCounts);
    }
}

public record OrderReport(
    LocalDate date,
    int totalOrders,
    Money totalRevenue,
    Map<String, Long> statusCounts
) {}