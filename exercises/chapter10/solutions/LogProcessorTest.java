package chapter10.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.util.*;

class LogProcessorTest {
    
    private LogProcessor processor;
    
    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        List<LogProcessor.LogEntry> logs = Arrays.asList(
            new LogProcessor.LogEntry(now.minusHours(2), "INFO", "アプリケーション開始", "App"),
            new LogProcessor.LogEntry(now.minusHours(1), "ERROR", "データベース接続エラー", "DB"),
            new LogProcessor.LogEntry(now.minusMinutes(30), "WARN", "メモリ使用量が高い", "System"),
            new LogProcessor.LogEntry(now.minusMinutes(10), "INFO", "ユーザーログイン", "Auth"),
            new LogProcessor.LogEntry(now, "ERROR", "ファイル不明", "File")
        );
        processor = new LogProcessor(logs);
    }
    
    @Test
    void testFilterByLevel() {
        List<LogProcessor.LogEntry> errors = processor.filterByLevel("ERROR");
        assertEquals(2, errors.size());
        
        List<LogProcessor.LogEntry> info = processor.filterByLevel("INFO");
        assertEquals(2, info.size());
    }
    
    @Test
    void testSearchByMessage() {
        List<LogProcessor.LogEntry> results = processor.searchByMessage("エラー");
        assertEquals(1, results.size());
        assertTrue(results.get(0).getMessage().contains("エラー"));
    }
    
    @Test
    void testCountByLevel() {
        Map<String, Long> counts = processor.countByLevel();
        assertEquals(2, counts.get("ERROR").longValue());
        assertEquals(2, counts.get("INFO").longValue());
        assertEquals(1, counts.get("WARN").longValue());
    }
    
    @Test
    void testCountBySource() {
        Map<String, Long> counts = processor.countBySource();
        assertEquals(1, counts.get("App").longValue());
        assertEquals(1, counts.get("DB").longValue());
    }
    
    @Test
    void testGetErrorLogs() {
        List<LogProcessor.LogEntry> errors = processor.getErrorLogs();
        assertEquals(2, errors.size());
        assertTrue(errors.stream().allMatch(log -> "ERROR".equals(log.getLevel())));
    }
    
    @Test
    void testGetLatest() {
        List<LogProcessor.LogEntry> latest = processor.getLatest(3);
        assertEquals(3, latest.size());
        // 最新のログが最初に来ることを確認
        assertTrue(latest.get(0).getTimestamp().isAfter(latest.get(1).getTimestamp()));
    }
    
    @Test
    void testFactoryMethods() {
        LogProcessor.LogEntry entry = LogProcessor.createEntry("INFO", "テスト", "Test");
        assertEquals("INFO", entry.getLevel());
        assertEquals("テスト", entry.getMessage());
        assertEquals("Test", entry.getSource());
    }
}