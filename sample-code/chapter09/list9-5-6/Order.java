/**
 * リスト9-6
 * OrderProcessorクラス
 * 
 * 元ファイル: chapter09-records.md (251行目)
 */

// データの定義（Records）
public record Order(String orderId, Customer customer, List<OrderItem> items, LocalDateTime orderDate) {}
public record Customer(String id, String name, String email) {}
public record OrderItem(String productName, int quantity, BigDecimal price) {}

// ロジックの分離（純粋関数）
public class OrderProcessor {
    public static BigDecimal calculateTotal(Order order) {
        return order.items().stream()
            .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public static PaymentResult processPayment(Order order, PaymentMethod method) {
        // 支払い処理ロジック
    }
    
    public static void updateInventory(Order order) {
        // 在庫更新ロジック
    }
}