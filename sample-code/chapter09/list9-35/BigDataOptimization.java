/**
 * リスト9-35
 * BigDataOptimizationクラス
 * 
 * 元ファイル: chapter09-records.md (1671行目)
 */

public class BigDataOptimization {
    
    // 大量のRecordを効率的に処理
    public record LogEntry(
        Instant timestamp,
        String level,
        String message,
        String source
    ) {}
    
    // メモリ効率を重視したストリーム処理
    public static Map<String, Long> analyzeLogsByLevel(Stream<String> logLines) {
        return logLines
            .parallel() // 並列処理
            .map(BigDataOptimization::parseLogEntry)
            .filter(Objects::nonNull)
            .collect(Collectors.groupingByConcurrent(
                LogEntry::level,
                Collectors.counting()
            ));
    }
    
    private static LogEntry parseLogEntry(String line) {
        try {
            String[] parts = line.split("\\|", 4);
            if (parts.length != 4) return null;
            
            return new LogEntry(
                Instant.parse(parts[0]),
                parts[1].intern(), // 文字列インターン化でメモリ削減
                parts[2],
                parts[3].intern()
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    // バッチ処理でのメモリ効率
    public static void processBatchData(List<LogEntry> entries) {
        // チャンクサイズを調整してメモリ使用量制御
        int chunkSize = 10_000;
        
        for (int i = 0; i < entries.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, entries.size());
            List<LogEntry> chunk = entries.subList(i, end);
            
            // チャンクごとに処理
            processChunk(chunk);
            
            // ガベージコレクションのヒント
            if (i % (chunkSize * 10) == 0) {
                System.gc();
            }
        }
    }
    
    private static void processChunk(List<LogEntry> chunk) {
        // 実際の処理...
    }
}