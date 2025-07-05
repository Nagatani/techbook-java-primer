import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * CSV読み書きクラス
 * 高性能で柔軟なCSV処理機能を提供
 */
public class CSVHandler {
    
    private static final Logger logger = Logger.getLogger(CSVHandler.class.getName());
    
    /**
     * CSV設定クラス
     */
    public static class CSVConfig {
        private final char delimiter;
        private final char quote;
        private final char escape;
        private final String lineSeparator;
        private final boolean hasHeader;
        private final String encoding;
        
        public CSVConfig(char delimiter, char quote, char escape, String lineSeparator, 
                        boolean hasHeader, String encoding) {
            this.delimiter = delimiter;
            this.quote = quote;
            this.escape = escape;
            this.lineSeparator = lineSeparator;
            this.hasHeader = hasHeader;
            this.encoding = encoding;
        }
        
        public static CSVConfig defaultConfig() {
            return new CSVConfig(',', '"', '\\', System.lineSeparator(), true, "UTF-8");
        }
        
        public static CSVConfig tsvConfig() {
            return new CSVConfig('\\t', '"', '\\', System.lineSeparator(), true, "UTF-8");
        }
        
        public char getDelimiter() { return delimiter; }
        public char getQuote() { return quote; }
        public char getEscape() { return escape; }
        public String getLineSeparator() { return lineSeparator; }
        public boolean hasHeader() { return hasHeader; }
        public String getEncoding() { return encoding; }
    }
    
    /**
     * CSVレコードクラス
     */
    public static class CSVRecord {
        private final List<String> fields;
        private final Map<String, String> namedFields;
        private final int lineNumber;
        
        public CSVRecord(List<String> fields, List<String> headers, int lineNumber) {
            this.fields = new ArrayList<>(fields);
            this.lineNumber = lineNumber;
            
            this.namedFields = new HashMap<>();
            if (headers != null) {
                for (int i = 0; i < Math.min(fields.size(), headers.size()); i++) {
                    namedFields.put(headers.get(i), fields.get(i));
                }
            }
        }
        
        public List<String> getFields() {
            return new ArrayList<>(fields);
        }
        
        public String getField(int index) {
            return index < fields.size() ? fields.get(index) : null;
        }
        
        public String getField(String fieldName) {
            return namedFields.get(fieldName);
        }
        
        public int getFieldCount() {
            return fields.size();
        }
        
        public int getLineNumber() {
            return lineNumber;
        }
        
        public boolean hasField(String fieldName) {
            return namedFields.containsKey(fieldName);
        }
        
        public Set<String> getFieldNames() {
            return new HashSet<>(namedFields.keySet());
        }
        
        @Override
        public String toString() {
            return String.format("CSVRecord[line=%d, fields=%s]", lineNumber, fields);
        }
    }
    
    /**
     * CSV統計情報クラス
     */
    public static class CSVStatistics {
        private final int totalRecords;
        private final int totalFields;
        private final int emptyRecords;
        private final Map<String, Integer> fieldDistribution;
        private final long processingTimeMs;
        
        public CSVStatistics(int totalRecords, int totalFields, int emptyRecords,
                           Map<String, Integer> fieldDistribution, long processingTimeMs) {
            this.totalRecords = totalRecords;
            this.totalFields = totalFields;
            this.emptyRecords = emptyRecords;
            this.fieldDistribution = new HashMap<>(fieldDistribution);
            this.processingTimeMs = processingTimeMs;
        }
        
        public int getTotalRecords() { return totalRecords; }
        public int getTotalFields() { return totalFields; }
        public int getEmptyRecords() { return emptyRecords; }
        public Map<String, Integer> getFieldDistribution() { return new HashMap<>(fieldDistribution); }
        public long getProcessingTimeMs() { return processingTimeMs; }
        
        @Override
        public String toString() {
            return String.format("CSV統計: レコード数=%d, フィールド数=%d, 空レコード数=%d, 処理時間=%dms",
                totalRecords, totalFields, emptyRecords, processingTimeMs);
        }
    }
    
    /**
     * CSVファイルを読み込み
     * @param filePath ファイルパス
     * @param config CSV設定
     * @return CSVレコードのリスト
     * @throws IOException ファイル読み込みエラー
     */
    public static List<CSVRecord> readCSV(Path filePath, CSVConfig config) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("CSVファイルが見つかりません: " + filePath);
        }
        
        long startTime = System.currentTimeMillis();
        List<CSVRecord> records = new ArrayList<>();
        List<String> headers = null;
        
        try (BufferedReader reader = Files.newBufferedReader(filePath, 
                java.nio.charset.Charset.forName(config.getEncoding()))) {
            
            String line;
            int lineNumber = 1;
            
            while ((line = reader.readLine()) != null) {
                List<String> fields = parseLine(line, config);
                
                if (lineNumber == 1 && config.hasHeader()) {
                    headers = fields;
                } else {
                    records.add(new CSVRecord(fields, headers, lineNumber));
                }
                
                lineNumber++;
            }
        }
        
        long processingTime = System.currentTimeMillis() - startTime;
        logger.info(String.format("CSV読み込み完了: %d レコード, %dms", records.size(), processingTime));
        
        return records;
    }
    
    /**
     * CSVファイルに書き込み
     * @param filePath ファイルパス
     * @param records CSVレコードのリスト
     * @param headers ヘッダー行（nullの場合はヘッダーなし）
     * @param config CSV設定
     * @throws IOException ファイル書き込みエラー
     */
    public static void writeCSV(Path filePath, List<CSVRecord> records, 
                               List<String> headers, CSVConfig config) throws IOException {
        long startTime = System.currentTimeMillis();
        
        // 親ディレクトリを作成
        Path parentDir = filePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                java.nio.charset.Charset.forName(config.getEncoding()))) {
            
            // ヘッダー行を書き込み
            if (headers != null && !headers.isEmpty()) {
                writer.write(formatLine(headers, config));
                writer.write(config.getLineSeparator());
            }
            
            // データ行を書き込み
            for (CSVRecord record : records) {
                writer.write(formatLine(record.getFields(), config));
                writer.write(config.getLineSeparator());
            }
        }
        
        long processingTime = System.currentTimeMillis() - startTime;
        logger.info(String.format("CSV書き込み完了: %d レコード, %dms", records.size(), processingTime));
    }
    
    /**
     * CSVデータから統計情報を生成
     * @param records CSVレコードのリスト
     * @return 統計情報
     */
    public static CSVStatistics generateStatistics(List<CSVRecord> records) {
        long startTime = System.currentTimeMillis();
        
        int totalRecords = records.size();
        int totalFields = records.stream().mapToInt(CSVRecord::getFieldCount).sum();
        int emptyRecords = (int) records.stream()
                .filter(record -> record.getFields().stream().allMatch(String::isEmpty))
                .count();
        
        Map<String, Integer> fieldDistribution = new HashMap<>();
        for (CSVRecord record : records) {
            for (String fieldName : record.getFieldNames()) {
                fieldDistribution.merge(fieldName, 1, Integer::sum);
            }
        }
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        return new CSVStatistics(totalRecords, totalFields, emptyRecords, 
                               fieldDistribution, processingTime);
    }
    
    /**
     * CSVレコードをフィルタリング
     * @param records 元のレコードリスト
     * @param predicate フィルタ条件
     * @return フィルタ済みのレコードリスト
     */
    public static List<CSVRecord> filterRecords(List<CSVRecord> records, 
                                              java.util.function.Predicate<CSVRecord> predicate) {
        return records.stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }
    
    /**
     * CSVレコードを変換
     * @param records 元のレコードリスト
     * @param mapper 変換関数
     * @return 変換済みのレコードリスト
     */
    public static <T> List<T> mapRecords(List<CSVRecord> records, Function<CSVRecord, T> mapper) {
        return records.stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
    
    /**
     * CSVレコードをグループ化
     * @param records CSVレコードリスト
     * @param keyExtractor グループ化キー抽出関数
     * @return グループ化されたマップ
     */
    public static Map<String, List<CSVRecord>> groupRecords(List<CSVRecord> records, 
                                                          Function<CSVRecord, String> keyExtractor) {
        return records.stream()
                .collect(Collectors.groupingBy(keyExtractor));
    }
    
    /**
     * 複数のCSVファイルをマージ
     * @param filePaths マージするCSVファイルのパスリスト
     * @param config CSV設定
     * @return マージされたレコードリスト
     * @throws IOException ファイル読み込みエラー
     */
    public static List<CSVRecord> mergeCSVFiles(List<Path> filePaths, CSVConfig config) throws IOException {
        List<CSVRecord> mergedRecords = new ArrayList<>();
        
        for (Path filePath : filePaths) {
            List<CSVRecord> records = readCSV(filePath, config);
            mergedRecords.addAll(records);
        }
        
        logger.info(String.format("CSV マージ完了: %d ファイル, %d レコード", 
                   filePaths.size(), mergedRecords.size()));
        
        return mergedRecords;
    }
    
    /**
     * CSVを別の形式に変換
     * @param inputPath 入力ファイルパス
     * @param outputPath 出力ファイルパス
     * @param inputConfig 入力CSV設定
     * @param outputConfig 出力CSV設定
     * @throws IOException ファイル操作エラー
     */
    public static void convertCSVFormat(Path inputPath, Path outputPath, 
                                      CSVConfig inputConfig, CSVConfig outputConfig) throws IOException {
        List<CSVRecord> records = readCSV(inputPath, inputConfig);
        
        // ヘッダーの取得（最初のレコードから）
        List<String> headers = null;
        if (!records.isEmpty() && inputConfig.hasHeader()) {
            headers = new ArrayList<>(records.get(0).getFieldNames());
        }
        
        writeCSV(outputPath, records, headers, outputConfig);
        
        logger.info(String.format("CSV変換完了: %s -> %s", inputPath, outputPath));
    }
    
    /**
     * 行をパース（引用符とエスケープを考慮）
     * @param line パースする行
     * @param config CSV設定
     * @return フィールドのリスト
     */
    private static List<String> parseLine(String line, CSVConfig config) {
        List<String> fields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean inQuotes = false;
        boolean escapeNext = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (escapeNext) {
                currentField.append(c);
                escapeNext = false;
            } else if (c == config.getEscape()) {
                escapeNext = true;
            } else if (c == config.getQuote()) {
                inQuotes = !inQuotes;
            } else if (c == config.getDelimiter() && !inQuotes) {
                fields.add(currentField.toString());
                currentField.setLength(0);
            } else {
                currentField.append(c);
            }
        }
        
        fields.add(currentField.toString());
        return fields;
    }
    
    /**
     * フィールドリストを1行の文字列にフォーマット
     * @param fields フィールドリスト
     * @param config CSV設定
     * @return フォーマット済み文字列
     */
    private static String formatLine(List<String> fields, CSVConfig config) {
        return fields.stream()
                .map(field -> formatField(field, config))
                .collect(Collectors.joining(String.valueOf(config.getDelimiter())));
    }
    
    /**
     * フィールドを適切にエスケープ・引用符で囲む
     * @param field フィールド値
     * @param config CSV設定
     * @return フォーマット済みフィールド
     */
    private static String formatField(String field, CSVConfig config) {
        if (field == null) {
            return "";
        }
        
        boolean needsQuoting = field.contains(String.valueOf(config.getDelimiter())) ||
                              field.contains(String.valueOf(config.getQuote())) ||
                              field.contains(config.getLineSeparator());
        
        if (needsQuoting) {
            String escaped = field.replace(String.valueOf(config.getQuote()), 
                                         config.getEscape() + String.valueOf(config.getQuote()));
            return config.getQuote() + escaped + config.getQuote();
        }
        
        return field;
    }
    
    /**
     * CSVファイルの検証
     * @param filePath ファイルパス
     * @param config CSV設定
     * @return 検証結果のリスト
     * @throws IOException ファイル読み込みエラー
     */
    public static List<String> validateCSV(Path filePath, CSVConfig config) throws IOException {
        List<String> validationErrors = new ArrayList<>();
        
        try (BufferedReader reader = Files.newBufferedReader(filePath, 
                java.nio.charset.Charset.forName(config.getEncoding()))) {
            
            String line;
            int lineNumber = 1;
            int expectedFieldCount = -1;
            
            while ((line = reader.readLine()) != null) {
                List<String> fields = parseLine(line, config);
                
                if (expectedFieldCount == -1) {
                    expectedFieldCount = fields.size();
                } else if (fields.size() != expectedFieldCount) {
                    validationErrors.add(String.format(
                        "行 %d: フィールド数が不正です（期待値: %d, 実際: %d）",
                        lineNumber, expectedFieldCount, fields.size()));
                }
                
                lineNumber++;
            }
        }
        
        return validationErrors;
    }
    
    /**
     * CSVファイルのプレビューを生成
     * @param filePath ファイルパス
     * @param config CSV設定
     * @param maxRows 最大表示行数
     * @return プレビュー文字列
     * @throws IOException ファイル読み込みエラー
     */
    public static String generatePreview(Path filePath, CSVConfig config, int maxRows) throws IOException {
        List<CSVRecord> records = readCSV(filePath, config);
        StringBuilder preview = new StringBuilder();
        
        preview.append("=== CSV プレビュー ===\n");
        preview.append(String.format("ファイル: %s\n", filePath.getFileName()));
        preview.append(String.format("総レコード数: %d\n", records.size()));
        preview.append("---\n");
        
        int displayRows = Math.min(maxRows, records.size());
        for (int i = 0; i < displayRows; i++) {
            CSVRecord record = records.get(i);
            preview.append(String.format("行 %d: %s\n", record.getLineNumber(), record.getFields()));
        }
        
        if (records.size() > maxRows) {
            preview.append(String.format("... 他 %d 行\n", records.size() - maxRows));
        }
        
        return preview.toString();
    }
}