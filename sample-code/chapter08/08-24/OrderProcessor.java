import java.math.BigDecimal;

// ロジックの分離（純粋関数）
public class OrderProcessor {
    public static BigDecimal calculateTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.items()) {
            total = total.add(item.price().multiply(BigDecimal.valueOf(item.quantity())));
        }
        return total;
    }
    
    public static boolean hasProduct(Order order, String productName) {
        for (OrderItem item : order.items()) {
            if (item.productName().equals(productName)) {
                return true;
            }
        }
        return false;
    }
}
