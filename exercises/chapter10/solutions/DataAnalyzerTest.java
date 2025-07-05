package chapter10.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class DataAnalyzerTest {
    
    private DataAnalyzer analyzer;
    
    @BeforeEach
    void setUp() {
        List<DataAnalyzer.DataPoint> data = Arrays.asList(
            new DataAnalyzer.DataPoint("売上", 100.0, "Q1"),
            new DataAnalyzer.DataPoint("売上", 150.0, "Q2"),
            new DataAnalyzer.DataPoint("売上", 120.0, "Q3"),
            new DataAnalyzer.DataPoint("コスト", 80.0, "Q1"),
            new DataAnalyzer.DataPoint("コスト", 90.0, "Q2"),
            new DataAnalyzer.DataPoint("コスト", 85.0, "Q3"),
            new DataAnalyzer.DataPoint("利益", 300.0, "異常値")
        );
        analyzer = new DataAnalyzer(data);
    }
    
    @Test
    void testBasicStatistics() {
        Map<String, Double> stats = analyzer.calculateBasicStatistics();
        
        assertEquals(7.0, stats.get("count"));
        assertEquals(925.0, stats.get("sum"));
        assertEquals(80.0, stats.get("min"));
        assertEquals(300.0, stats.get("max"));
        assertTrue(stats.containsKey("average"));
        assertTrue(stats.containsKey("median"));
        assertTrue(stats.containsKey("stdDev"));
    }
    
    @Test
    void testCategoryStatistics() {
        Map<String, Map<String, Double>> categoryStats = analyzer.calculateCategoryStatistics();
        
        assertTrue(categoryStats.containsKey("売上"));
        assertTrue(categoryStats.containsKey("コスト"));
        assertTrue(categoryStats.containsKey("利益"));
        
        Map<String, Double> salesStats = categoryStats.get("売上");
        assertEquals(3.0, salesStats.get("count"));
        assertEquals(370.0, salesStats.get("sum"));
    }
    
    @Test
    void testOutlierDetection() {
        List<DataAnalyzer.DataPoint> outliers = analyzer.findOutliers();
        assertEquals(1, outliers.size());
        assertEquals(300.0, outliers.get(0).getValue());
    }
    
    @Test
    void testTopN() {
        List<DataAnalyzer.DataPoint> top3 = analyzer.getTopN(3);
        assertEquals(3, top3.size());
        assertEquals(300.0, top3.get(0).getValue());
        assertEquals(150.0, top3.get(1).getValue());
    }
    
    @Test
    void testFiltering() {
        List<DataAnalyzer.DataPoint> filtered = analyzer.filter(
            point -> point.getValue() > 100.0
        );
        assertEquals(3, filtered.size());
    }
    
    @Test
    void testTransformation() {
        List<String> labels = analyzer.transform(DataAnalyzer.DataPoint::getLabel);
        assertEquals(7, labels.size());
        assertTrue(labels.contains("Q1"));
    }
    
    @Test
    void testGrouping() {
        Map<String, List<DataAnalyzer.DataPoint>> grouped = analyzer.groupByCategory();
        assertEquals(3, grouped.size());
        assertEquals(3, grouped.get("売上").size());
        assertEquals(3, grouped.get("コスト").size());
        assertEquals(1, grouped.get("利益").size());
    }
    
    @Test
    void testFactoryMethods() {
        DataAnalyzer analyzer1 = DataAnalyzer.fromValues("テスト", 1.0, 2.0, 3.0);
        Map<String, Double> stats = analyzer1.calculateBasicStatistics();
        assertEquals(3.0, stats.get("count"));
        assertEquals(2.0, stats.get("average"));
    }
}