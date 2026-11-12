package chapter12.solutions;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 商品の売上の分析（課題2の解答例）
 */
public class SalesAnalyzer {
    private List<Product> products;

    public SalesAnalyzer(List<Product> products) {
        this.products = products;
    }

    /**
     * 1. カテゴリごとの売上の合計。groupingByの3つ目の引数に、グループごとの集め方（summingDouble）を渡す
     */
    public Map<String, Double> calculateSalesByCategory() {
        return products.stream()
                .collect(Collectors.groupingBy(Product::getCategory, TreeMap::new,
                        Collectors.summingDouble(Product::getTotalSales)));
    }

    /**
     * 2. 価格がminPrice以上maxPrice以下の商品
     */
    public List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        return products.stream()
                .filter(p -> p.getPrice() >= minPrice && p.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    /**
     * 3. 在庫切れ（数量が0）の商品
     */
    public List<Product> getOutOfStockProducts() {
        return products.stream()
                .filter(p -> p.getQuantity() == 0)
                .collect(Collectors.toList());
    }

    /**
     * 4. 売上金額の多い順に上位n件
     */
    public List<Product> getTopSellingProducts(int n) {
        return products.stream()
                .sorted(Comparator.comparingDouble(Product::getTotalSales).reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * 5. 「商品名 (カテゴリ)」の形の文字列
     */
    public List<String> getProductDescriptions() {
        return products.stream()
                .map(p -> p.getName() + " (" + p.getCategory() + ")")
                .collect(Collectors.toList());
    }
}
