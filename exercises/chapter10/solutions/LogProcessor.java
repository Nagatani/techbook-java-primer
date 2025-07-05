package chapter10.solutions;

import java.time.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.regex.*;

/**
 * ログ解析をStream APIで実装
 */
public class LogProcessor {
    
    public static class LogEntry {
        private final LocalDateTime timestamp;
        private final String level;
        private final String message;
        private final String source;
        
        public LogEntry(LocalDateTime timestamp, String level, String message, String source) {
            this.timestamp = timestamp;
            this.level = level;
            this.message = message;
            this.source = source;
        }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public String getLevel() { return level; }
        public String getMessage() { return message; }
        public String getSource() { return source; }
        
        @Override
        public String toString() {
            return String.format("%s [%s] %s: %s", timestamp, level, source, message);
        }
    }
    
    private final List<LogEntry> logs;
    
    public LogProcessor(List<LogEntry> logs) {
        this.logs = new ArrayList<>(logs);
    }
    
    /**
     * レベルでフィルタリング
     */
    public List<LogEntry> filterByLevel(String level) {
        return logs.stream()
            .filter(log -> log.getLevel().equals(level))
            .collect(Collectors.toList());
    }
    
    /**
     * 時間範囲でフィルタリング
     */
    public List<LogEntry> filterByTimeRange(LocalDateTime start, LocalDateTime end) {
        return logs.stream()
            .filter(log -> !log.getTimestamp().isBefore(start) && !log.getTimestamp().isAfter(end))
            .collect(Collectors.toList());
    }
    
    /**
     * メッセージで検索
     */
    public List<LogEntry> searchByMessage(String keyword) {
        return logs.stream()
            .filter(log -> log.getMessage().contains(keyword))
            .collect(Collectors.toList());
    }
    
    /**
     * 正規表現で検索
     */
    public List<LogEntry> searchByPattern(String pattern) {
        Pattern compiled = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        return logs.stream()
            .filter(log -> compiled.matcher(log.getMessage()).find())
            .collect(Collectors.toList());
    }
    
    /**
     * レベル別のカウント
     */
    public Map<String, Long> countByLevel() {
        return logs.stream()
            .collect(Collectors.groupingBy(
                LogEntry::getLevel,
                Collectors.counting()
            ));
    }
    
    /**
     * 時間帯別のカウント
     */
    public Map<Integer, Long> countByHour() {
        return logs.stream()
            .collect(Collectors.groupingBy(
                log -> log.getTimestamp().getHour(),
                Collectors.counting()
            ));
    }
    
    /**
     * ソース別のカウント
     */
    public Map<String, Long> countBySource() {
        return logs.stream()
            .collect(Collectors.groupingBy(
                LogEntry::getSource,
                Collectors.counting()
            ));
    }
    
    /**
     * エラーログの抽出
     */
    public List<LogEntry> getErrorLogs() {
        return filterByLevel("ERROR");
    }
    
    /**
     * 最新のN件を取得
     */
    public List<LogEntry> getLatest(int n) {
        return logs.stream()
            .sorted(Comparator.comparing(LogEntry::getTimestamp).reversed())
            .limit(n)
            .collect(Collectors.toList());
    }
    
    /**
     * 異常なアクティビティを検出
     */
    public List<LogEntry> findAnomalies() {
        Map<String, Long> sourceCounts = countBySource();
        double averageCount = sourceCounts.values().stream()
            .mapToLong(Long::longValue)
            .average()
            .orElse(0.0);
        
        Set<String> anomalousS sources = sourceCounts.entrySet().stream()
            .filter(entry -> entry.getValue() > averageCount * 2)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
        
        return logs.stream()
            .filter(log -> anomalousSources.contains(log.getSource()))
            .collect(Collectors.toList());
    }
    
    /**
     * 日別のサマリを作成
     */
    public Map<LocalDate, Map<String, Long>> getDailySummary() {
        return logs.stream()
            .collect(Collectors.groupingBy(
                log -> log.getTimestamp().toLocalDate(),
                Collectors.groupingBy(
                    LogEntry::getLevel,
                    Collectors.counting()
                )
            ));
    }
    
    /**
     * 最も頻繁なメッセージを取得
     */
    public Optional<Map.Entry<String, Long>> getMostFrequentMessage() {
        return logs.stream()
            .collect(Collectors.groupingBy(
                LogEntry::getMessage,
                Collectors.counting()
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue());
    }
    
    /**
     * カスタム集約で処理
     */
    public <T> T processLogs(Collector<LogEntry, ?, T> collector) {
        return logs.stream().collect(collector);
    }
    
    // ファクトリーメソッド
    public static LogProcessor of(LogEntry... logs) {
        return new LogProcessor(Arrays.asList(logs));
    }
    
    public static LogEntry createEntry(String level, String message, String source) {
        return new LogEntry(LocalDateTime.now(), level, message, source);
    }
}