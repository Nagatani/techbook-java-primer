import java.math.BigDecimal;

// ロジックの分離（引数だけで結果が決まり、何も書き換えない）
public class OrderProcessor {
    public static BigDecimal calculateTotal(Order order) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.items()) {
            total = total.add(item.price()
                    .multiply(BigDecimal.valueOf(item.quantity())));
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
