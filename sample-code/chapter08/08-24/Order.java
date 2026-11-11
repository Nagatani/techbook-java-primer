import java.time.LocalDateTime;
import java.util.List;

// データの定義（Records）
public record Order(String orderId, Customer customer, List<OrderItem> items, LocalDateTime orderDate) {}
