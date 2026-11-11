import java.math.BigDecimal;

public record OrderItem(String productName, int quantity, BigDecimal price) {}
