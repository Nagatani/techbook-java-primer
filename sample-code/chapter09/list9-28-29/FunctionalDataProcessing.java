/**
 * リスト9-29
 * FunctionalDataProcessingクラス
 * 
 * 元ファイル: chapter09-records.md (1321行目)
 */

// 関数型スタイルでのデータ処理
public class FunctionalDataProcessing {
    
    // 注文データの関数型処理
    public static Optional<Order> findHighestValueOrder(Stream<Order> orders) {
        return orders
            .filter(order -> !(order.status() instanceof Cancelled))
            .max(Comparator.comparing(Order::totalAmount, 
                Comparator.comparing(Money::amount)));
    }
    
    // 顧客別注文サマリーの生成
    public static Map<Customer, CustomerSummary> generateCustomerSummaries(Stream<Order> orders) {
        return orders
            .filter(order -> !(order.status() instanceof Cancelled))
            .collect(Collectors.groupingBy(
                Order::customer,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    FunctionalDataProcessing::createCustomerSummary
                )
            ));
    }
    
    private static CustomerSummary createCustomerSummary(List<Order> orders) {
        int orderCount = orders.size();
        Money totalSpent = orders.stream()
            .map(Order::totalAmount)
            .reduce(new Money(BigDecimal.ZERO, Currency.getInstance("JPY")), Money::add);
        
        LocalDateTime lastOrderDate = orders.stream()
            .map(Order::createdAt)
            .max(LocalDateTime::compareTo)
            .orElse(LocalDateTime.MIN);
        
        Money averageOrderValue = orderCount > 0 
            ? new Money(totalSpent.amount().divide(BigDecimal.valueOf(orderCount), 2, RoundingMode.HALF_UP), totalSpent.currency())
            : new Money(BigDecimal.ZERO, Currency.getInstance("JPY"));
        
        return new CustomerSummary(orderCount, totalSpent, lastOrderDate, averageOrderValue);
    }
    
    // 商品別売上分析
    public static List<ProductSales> analyzeProductSales(Stream<Order> orders) {
        return orders
            .filter(order -> !(order.status() instanceof Cancelled))
            .flatMap(order -> order.items().stream())
            .collect(Collectors.groupingBy(
                OrderItem::product,
                Collectors.summingInt(OrderItem::quantity)
            ))
            .entrySet()
            .stream()
            .map(entry -> new ProductSales(entry.getKey(), entry.getValue()))
            .sorted(Comparator.comparing(ProductSales::quantitySold).reversed())
            .collect(Collectors.toList());
    }
}

public record CustomerSummary(
    int orderCount,
    Money totalSpent,
    LocalDateTime lastOrderDate,
    Money averageOrderValue
) {}

public record ProductSales(Product product, int quantitySold) {}