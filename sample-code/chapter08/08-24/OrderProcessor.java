import java.math.BigDecimal;

// ロジックの分離（純粋関数）
public class OrderProcessor {
    public static BigDecimal calculateTotal(Order order) {
        return order.items().stream()
            .map(item -> item.price().multiply(BigDecimal.valueOf(item.quantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public static boolean hasProduct(Order order, String productName) {
        return order.items().stream()
            .anyMatch(item -> item.productName().equals(productName));
    }
}
