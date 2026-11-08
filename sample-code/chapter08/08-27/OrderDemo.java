import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderDemo {
    public static void main(String[] args) {
        Order order = new Order("A001", "C001", LocalDateTime.of(2024, 4, 1, 10, 0),
                OrderStatus.PENDING, new BigDecimal("1980"));
        Order shipped = order.updateStatus(OrderStatus.PROCESSING).updateStatus(OrderStatus.SHIPPED);
        Order delivered = shipped.updateStatus(OrderStatus.DELIVERED);
        System.out.println(delivered.status().getDescription());
        try {
            delivered.updateStatus(OrderStatus.CANCELLED);
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }
}
