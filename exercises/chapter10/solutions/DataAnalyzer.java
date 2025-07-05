package chapter10.solutions;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Stream APIを活用したデータ分析クラス
 */
public class DataAnalyzer {
    
    public static class DataPoint {
        private final String category;
        private final double value;
        private final String label;
        
        public DataPoint(String category, double value, String label) {
            this.category = category;
            this.value = value;
            this.label = label;
        }
        
        public String getCategory() { return category; }
        public double getValue() { return value; }
        public String getLabel() { return label; }
        
        @Override
        public String toString() {
            return String.format("DataPoint{category='%s', value=%.2f, label='%s'}",
                               category, value, label);
        }
    }
    
    public static class AnalysisResult {
        private final Map<String, Double> statistics;
        private final List<DataPoint> outliers;
        private final Map<String, List<DataPoint>> groupedData;
        
        public AnalysisResult(Map<String, Double> statistics, 
                            List<DataPoint> outliers,
                            Map<String, List<DataPoint>> groupedData) {
            this.statistics = new HashMap<>(statistics);
            this.outliers = new ArrayList<>(outliers);
            this.groupedData = new HashMap<>(groupedData);
        }
        
        public Map<String, Double> getStatistics() { return new HashMap<>(statistics); }
        public List<DataPoint> getOutliers() { return new ArrayList<>(outliers); }
        public Map<String, List<DataPoint>> getGroupedData() { return new HashMap<>(groupedData); }
    }
    
    private final List<DataPoint> data;
    
    public DataAnalyzer(List<DataPoint> data) {
        this.data = new ArrayList<>(data);
    }
    
    /**
     * 基本統計を計算する
     */
    public Map<String, Double> calculateBasicStatistics() {
        DoubleSummaryStatistics stats = data.stream()
            .mapToDouble(DataPoint::getValue)
            .summaryStatistics();
        
        Map<String, Double> result = new HashMap<>();
        result.put("count", (double) stats.getCount());
        result.put("sum", stats.getSum());
        result.put("min", stats.getMin());
        result.put("max", stats.getMax());
        result.put("average", stats.getAverage());
        
        // 中央値と標準偏差を追加
        List<Double> sortedValues = data.stream()
            .mapToDouble(DataPoint::getValue)
            .sorted()
            .boxed()
            .collect(Collectors.toList());
        
        result.put("median", calculateMedian(sortedValues));
        result.put("stdDev", calculateStandardDeviation(stats.getAverage()));
        
        return result;
    }
    
    /**
     * カテゴリ別統計を計算する
     */
    public Map<String, Map<String, Double>> calculateCategoryStatistics() {
        return data.stream()
            .collect(Collectors.groupingBy(
                DataPoint::getCategory,
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    points -> {
                        DoubleSummaryStatistics stats = points.stream()
                            .mapToDouble(DataPoint::getValue)
                            .summaryStatistics();
                        
                        Map<String, Double> categoryStats = new HashMap<>();
                        categoryStats.put("count", (double) stats.getCount());
                        categoryStats.put("sum", stats.getSum());
                        categoryStats.put("average", stats.getAverage());
                        categoryStats.put("min", stats.getMin());
                        categoryStats.put("max", stats.getMax());
                        
                        return categoryStats;
                    }
                )
            ));
    }
    
    /**
     * 外れ値を検出する
     */
    public List<DataPoint> findOutliers() {
        double average = data.stream()
            .mapToDouble(DataPoint::getValue)
            .average()
            .orElse(0.0);
        
        double stdDev = calculateStandardDeviation(average);
        double threshold = 2.0 * stdDev;
        
        return data.stream()
            .filter(point -> Math.abs(point.getValue() - average) > threshold)
            .collect(Collectors.toList());
    }
    
    /**
     * 上位N件を取得する
     */
    public List<DataPoint> getTopN(int n) {
        return data.stream()
            .sorted(Comparator.comparingDouble(DataPoint::getValue).reversed())
            .limit(n)
            .collect(Collectors.toList());
    }
    
    /**
     * 条件でフィルタリング
     */
    public List<DataPoint> filter(Predicate<DataPoint> predicate) {
        return data.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    /**
     * データを変換する
     */
    public <R> List<R> transform(Function<DataPoint, R> transformer) {
        return data.stream()
            .map(transformer)
            .collect(Collectors.toList());
    }
    
    /**
     * カテゴリ別にグループ化
     */
    public Map<String, List<DataPoint>> groupByCategory() {
        return data.stream()
            .collect(Collectors.groupingBy(DataPoint::getCategory));
    }
    
    /**
     * 範囲別にグループ化
     */
    public Map<String, List<DataPoint>> groupByValueRange(double... ranges) {
        return data.stream()
            .collect(Collectors.groupingBy(point -> {
                double value = point.getValue();
                for (int i = 0; i < ranges.length - 1; i++) {
                    if (value >= ranges[i] && value < ranges[i + 1]) {
                        return String.format("%.1f-%.1f", ranges[i], ranges[i + 1]);
                    }
                }
                return "範囲外";
            }));
    }
    
    /**
     * 包括的な分析を実行
     */
    public AnalysisResult analyze() {
        Map<String, Double> statistics = calculateBasicStatistics();
        List<DataPoint> outliers = findOutliers();
        Map<String, List<DataPoint>> groupedData = groupByCategory();
        
        return new AnalysisResult(statistics, outliers, groupedData);
    }
    
    /**
     * カスタム集約を実行
     */
    public <T> T collectWith(Collector<DataPoint, ?, T> collector) {
        return data.stream().collect(collector);
    }
    
    /**
     * 並列処理で統計を計算
     */
    public Map<String, Double> calculateStatisticsParallel() {
        DoubleSummaryStatistics stats = data.parallelStream()
            .mapToDouble(DataPoint::getValue)
            .summaryStatistics();
        
        Map<String, Double> result = new HashMap<>();
        result.put("count", (double) stats.getCount());
        result.put("average", stats.getAverage());
        result.put("min", stats.getMin());
        result.put("max", stats.getMax());
        
        return result;
    }
    
    // ヘルパーメソッド
    private double calculateMedian(List<Double> sortedValues) {
        int size = sortedValues.size();
        if (size == 0) return 0.0;
        
        if (size % 2 == 0) {
            return (sortedValues.get(size / 2 - 1) + sortedValues.get(size / 2)) / 2.0;
        } else {
            return sortedValues.get(size / 2);
        }
    }
    
    private double calculateStandardDeviation(double average) {
        double variance = data.stream()
            .mapToDouble(point -> Math.pow(point.getValue() - average, 2))
            .average()
            .orElse(0.0);
        
        return Math.sqrt(variance);
    }
    
    // ファクトリーメソッド
    public static DataAnalyzer of(DataPoint... points) {
        return new DataAnalyzer(Arrays.asList(points));
    }
    
    public static DataAnalyzer fromValues(String category, double... values) {
        List<DataPoint> points = IntStream.range(0, values.length)
            .mapToObj(i -> new DataPoint(category, values[i], "Point" + i))
            .collect(Collectors.toList());
        return new DataAnalyzer(points);
    }
}