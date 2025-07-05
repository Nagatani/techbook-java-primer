package chapter08.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

/**
 * DataProcessorクラスのテストクラス
 */
class DataProcessorTest {
    
    private DataProcessor processor;
    private DataProcessor.DataEntry entry1;
    private DataProcessor.DataEntry entry2;
    private DataProcessor.DataEntry entry3;
    private DataProcessor.DataEntry entry4;
    
    @BeforeEach
    void setUp() {
        processor = new DataProcessor();
        
        entry1 = new DataProcessor.DataEntry("D001", "売上", 100.0);
        entry2 = new DataProcessor.DataEntry("D002", "売上", 150.0);
        entry3 = new DataProcessor.DataEntry("D003", "コスト", 80.0);
        entry4 = new DataProcessor.DataEntry("D004", "コスト", 120.0);
        
        processor.addData(entry1);
        processor.addData(entry2);
        processor.addData(entry3);
        processor.addData(entry4);
    }
    
    @Test
    void testDataEntryBasicProperties() {
        assertEquals("D001", entry1.getId());
        assertEquals("売上", entry1.getCategory());
        assertEquals(100.0, entry1.getValue());
        assertNotNull(entry1.getTimestamp());
        assertNotNull(entry1.getProperties());
    }
    
    @Test
    void testDataEntryProperties() {
        entry1.setProperty("region", "東京");
        entry1.setProperty("month", 1);
        
        assertEquals("東京", entry1.getProperty("region"));
        assertEquals(1, entry1.getProperty("month"));
        assertNull(entry1.getProperty("nonexistent"));
    }
    
    @Test
    void testAddData() {
        DataProcessor.DataEntry newEntry = new DataProcessor.DataEntry("D005", "利益", 200.0);
        assertTrue(processor.addData(newEntry));
        assertEquals(5, processor.getDataCount());
        
        // 重複IDは追加できない
        assertFalse(processor.addData(entry1));
        assertEquals(5, processor.getDataCount());
        
        // nullは追加できない
        assertFalse(processor.addData(null));
        assertEquals(5, processor.getDataCount());
    }
    
    @Test
    void testRemoveData() {
        assertTrue(processor.removeData("D001"));
        assertEquals(3, processor.getDataCount());
        assertNull(processor.findById("D001"));
        
        // 存在しないIDは削除できない
        assertFalse(processor.removeData("D999"));
        assertEquals(3, processor.getDataCount());
    }
    
    @Test
    void testFindById() {
        DataProcessor.DataEntry found = processor.findById("D001");
        assertNotNull(found);
        assertEquals("売上", found.getCategory());
        assertEquals(100.0, found.getValue());
        
        assertNull(processor.findById("D999"));
    }
    
    @Test
    void testFindByCategory() {
        List<DataProcessor.DataEntry> salesData = processor.findByCategory("売上");
        assertEquals(2, salesData.size());
        
        List<DataProcessor.DataEntry> costData = processor.findByCategory("コスト");
        assertEquals(2, costData.size());
        
        List<DataProcessor.DataEntry> noData = processor.findByCategory("存在しない");
        assertEquals(0, noData.size());
    }
    
    @Test
    void testFindByValueRange() {
        List<DataProcessor.DataEntry> range = processor.findByValueRange(100.0, 120.0);
        assertEquals(3, range.size()); // 100, 120のデータ
        
        range = processor.findByValueRange(200.0, 300.0);
        assertEquals(0, range.size());
    }
    
    @Test
    void testFindByTimeRange() {
        Date now = new Date();
        Date past = new Date(now.getTime() - 3600000); // 1時間前
        Date future = new Date(now.getTime() + 3600000); // 1時間後
        
        List<DataProcessor.DataEntry> range = processor.findByTimeRange(past, future);
        assertEquals(4, range.size()); // 全データが対象
    }
    
    @Test
    void testCalculateStatistics() {
        DataProcessor.ProcessingResult result = processor.calculateStatistics();
        
        assertFalse(result.hasErrors());
        assertEquals("統計計算", result.getOperation());
        
        Map<String, Double> results = result.getResults();
        assertEquals(4.0, results.get("count"));
        assertEquals(450.0, results.get("sum"));
        assertEquals(80.0, results.get("min"));
        assertEquals(150.0, results.get("max"));
        assertEquals(112.5, results.get("average"));
    }
    
    @Test
    void testCalculateCategoryStatistics() {
        DataProcessor.ProcessingResult result = processor.calculateCategoryStatistics();
        
        assertFalse(result.hasErrors());
        assertEquals("カテゴリ別統計計算", result.getOperation());
        
        Map<String, Double> results = result.getResults();
        assertEquals(2.0, results.get("売上_count"));
        assertEquals(125.0, results.get("売上_average"));
        assertEquals(2.0, results.get("コスト_count"));
        assertEquals(100.0, results.get("コスト_average"));
    }
    
    @Test
    void testGetTopN() {
        List<DataProcessor.DataEntry> top2 = processor.getTopN(2);
        assertEquals(2, top2.size());
        assertEquals(150.0, top2.get(0).getValue());
        assertEquals(120.0, top2.get(1).getValue());
    }
    
    @Test
    void testGetBottomN() {
        List<DataProcessor.DataEntry> bottom2 = processor.getBottomN(2);
        assertEquals(2, bottom2.size());
        assertEquals(80.0, bottom2.get(0).getValue());
        assertEquals(100.0, bottom2.get(1).getValue());
    }
    
    @Test
    void testSortByValue() {
        List<DataProcessor.DataEntry> ascending = processor.sortByValue(true);
        assertEquals(80.0, ascending.get(0).getValue());
        assertEquals(150.0, ascending.get(3).getValue());
        
        List<DataProcessor.DataEntry> descending = processor.sortByValue(false);
        assertEquals(150.0, descending.get(0).getValue());
        assertEquals(80.0, descending.get(3).getValue());
    }
    
    @Test
    void testSortByTimestamp() {
        List<DataProcessor.DataEntry> ascending = processor.sortByTimestamp(true);
        assertEquals(4, ascending.size());
        
        List<DataProcessor.DataEntry> descending = processor.sortByTimestamp(false);
        assertEquals(4, descending.size());
    }
    
    @Test
    void testFindDuplicates() {
        // 重複データを追加
        DataProcessor.DataEntry duplicate = new DataProcessor.DataEntry("D005", "売上", 100.0);
        processor.addData(duplicate);
        
        List<DataProcessor.DataEntry> duplicates = processor.findDuplicates();
        assertEquals(2, duplicates.size());
    }
    
    @Test
    void testProcessingHistory() {
        processor.calculateStatistics();
        processor.calculateCategoryStatistics();
        
        List<DataProcessor.ProcessingResult> history = processor.getProcessingHistory();
        assertEquals(2, history.size());
        
        DataProcessor.ProcessingResult latest = processor.getLatestResult();
        assertNotNull(latest);
        assertEquals("カテゴリ別統計計算", latest.getOperation());
    }
    
    @Test
    void testGetAllMethods() {
        List<DataProcessor.DataEntry> allData = processor.getAllData();
        assertEquals(4, allData.size());
        
        Set<String> categories = processor.getAllCategories();
        assertEquals(2, categories.size());
        assertTrue(categories.contains("売上"));
        assertTrue(categories.contains("コスト"));
    }
    
    @Test
    void testEmptyDataStatistics() {
        DataProcessor emptyProcessor = new DataProcessor();
        DataProcessor.ProcessingResult result = emptyProcessor.calculateStatistics();
        
        assertTrue(result.hasErrors());
        assertTrue(result.getErrors().get(0).contains("データが存在しません"));
    }
    
    @Test
    void testDataEntryEquality() {
        DataProcessor.DataEntry entry1Copy = new DataProcessor.DataEntry("D001", "売上", 100.0);
        DataProcessor.DataEntry differentEntry = new DataProcessor.DataEntry("D002", "売上", 100.0);
        
        assertEquals(entry1, entry1Copy);
        assertNotEquals(entry1, differentEntry);
        assertEquals(entry1.hashCode(), entry1Copy.hashCode());
    }
    
    @Test
    void testToStringMethods() {
        String entryStr = entry1.toString();
        assertTrue(entryStr.contains("D001"));
        assertTrue(entryStr.contains("売上"));
        assertTrue(entryStr.contains("100.0"));
        
        DataProcessor.ProcessingResult result = processor.calculateStatistics();
        String resultStr = result.toString();
        assertTrue(resultStr.contains("統計計算"));
    }
}