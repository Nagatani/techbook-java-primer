import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Order(
    String orderId,
    String customerId,
    LocalDateTime orderDate,
    OrderStatus status,
    BigDecimal totalAmount
) {
    public Order {
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("注文IDは必須です");
        }
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("合計金額は0以上でなければなりません");
        }
    }
    
    public Order updateStatus(OrderStatus newStatus) {
        if (this.status.isFinal()) {
            throw new IllegalStateException("最終状態の注文は更新できません");
        }
        return new Order(orderId, customerId, orderDate, newStatus, totalAmount);
    }
}
