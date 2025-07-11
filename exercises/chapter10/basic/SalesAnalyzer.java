import java.util.*;
import java.util.stream.*;

/**
 * 第10章 Stream API - 基本演習
 * 売上分析クラス
 */
public class SalesAnalyzer {
    private List<Product> products;
    
    public SalesAnalyzer(List<Product> products) {
        this.products = products;
    }
    
    /**
     * 1. カテゴリ別の売上合計を計算
     */
    public Map<String, Double> calculateSalesByCategory() {
        // TODO: Streamを使用して実装
        // ヒント: Collectors.groupingBy()とCollectors.summingDouble()を使用
        return new HashMap<>();
    }
    
    /**
     * 2. 価格範囲内の商品を取得
     */
    public List<Product> filterByPriceRange(double minPrice, double maxPrice) {
        // TODO: Streamを使用して実装
        // ヒント: filter()メソッドを使用
        return new ArrayList<>();
    }
    
    /**
     * 3. 在庫切れの商品（quantity = 0）を取得
     */
    public List<Product> getOutOfStockProducts() {
        // TODO: Streamを使用して実装
        // ヒント: filter()メソッドを使用
        return new ArrayList<>();
    }
    
    /**
     * 4. 売上金額のTOP N商品を取得
     */
    public List<Product> getTopSellingProducts(int n) {
        // TODO: Streamを使用して実装
        // ヒント: sorted()とlimit()を使用
        return new ArrayList<>();
    }
    
    /**
     * 5. 商品名とカテゴリを結合した文字列のリストを取得
     */
    public List<String> getProductDescriptions() {
        // TODO: Streamを使用して実装
        // ヒント: map()メソッドを使用
        // 例: "Laptop (Electronics)"
        return new ArrayList<>();
    }
}