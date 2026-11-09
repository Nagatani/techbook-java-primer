import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;

/**
 * CSVHandlerクラスのテストクラス
 */
public class CSVHandlerTest {
    
    @TempDir
    Path tempDir;
    
    private Path csvFile;
    private CSVHandler.CSVConfig defaultConfig;
    
    @BeforeEach
    public void setUp() throws IOException {
        csvFile = tempDir.resolve("test.csv");
        defaultConfig = CSVHandler.CSVConfig.defaultConfig();
        
        // テスト用CSVファイルを作成
        String csvContent = "Name,Age,City\n" +
                           "Alice,25,Tokyo\n" +
                           "Bob,30,Osaka\n" +
                           "Charlie,35,Kyoto\n";
        
        Files.write(csvFile, csvContent.getBytes());
    }
    
    @Test
    public void testReadCSV() throws IOException {
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, defaultConfig);
        
        assertEquals(3, records.size());
        
        CSVHandler.CSVRecord firstRecord = records.get(0);
        assertEquals("Alice", firstRecord.getField("Name"));
        assertEquals("25", firstRecord.getField("Age"));
        assertEquals("Tokyo", firstRecord.getField("City"));
    }
    
    @Test
    public void testReadCSVWithoutHeader() throws IOException {
        CSVHandler.CSVConfig configNoHeader = new CSVHandler.CSVConfig(
            ',', '"', '\\', System.lineSeparator(), false, "UTF-8");
        
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, configNoHeader);
        
        assertEquals(4, records.size()); // ヘッダー行も含む
        
        CSVHandler.CSVRecord firstRecord = records.get(0);
        assertEquals("Name", firstRecord.getField(0));
        assertEquals("Age", firstRecord.getField(1));
        assertEquals("City", firstRecord.getField(2));
    }
    
    @Test
    public void testWriteCSV() throws IOException {
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, defaultConfig);
        Path outputFile = tempDir.resolve("output.csv");
        
        List<String> headers = Arrays.asList("Name", "Age", "City");
        CSVHandler.writeCSV(outputFile, records, headers, defaultConfig);
        
        assertTrue(Files.exists(outputFile));
        
        // 書き込まれた内容を確認
        List<CSVHandler.CSVRecord> writtenRecords = CSVHandler.readCSV(outputFile, defaultConfig);
        assertEquals(records.size(), writtenRecords.size());
        
        for (int i = 0; i < records.size(); i++) {
            assertEquals(records.get(i).getField("Name"), writtenRecords.get(i).getField("Name"));
            assertEquals(records.get(i).getField("Age"), writtenRecords.get(i).getField("Age"));
            assertEquals(records.get(i).getField("City"), writtenRecords.get(i).getField("City"));
        }
    }
    
    @Test
    public void testCSVRecordMethods() throws IOException {
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, defaultConfig);
        CSVHandler.CSVRecord record = records.get(0);
        
        assertEquals(3, record.getFieldCount());
        assertEquals(2, record.getLineNumber()); // ヘッダーの次の行
        assertTrue(record.hasField("Name"));
        assertFalse(record.hasField("NonExistent"));
        
        Set<String> fieldNames = record.getFieldNames();
        assertTrue(fieldNames.contains("Name"));
        assertTrue(fieldNames.contains("Age"));
        assertTrue(fieldNames.contains("City"));
        
        List<String> fields = record.getFields();
        assertEquals("Alice", fields.get(0));
        assertEquals("25", fields.get(1));
        assertEquals("Tokyo", fields.get(2));
    }
    
    @Test
    public void testGenerateStatistics() throws IOException {
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, defaultConfig);
        CSVHandler.CSVStatistics stats = CSVHandler.generateStatistics(records);
        
        assertEquals(3, stats.getTotalRecords());
        assertEquals(9, stats.getTotalFields()); // 3レコード × 3フィールド
        assertEquals(0, stats.getEmptyRecords());
        assertTrue(stats.getProcessingTimeMs() >= 0);
        
        Map<String, Integer> distribution = stats.getFieldDistribution();
        assertEquals(3, distribution.get("Name").intValue());
        assertEquals(3, distribution.get("Age").intValue());
        assertEquals(3, distribution.get("City").intValue());
    }
    
    @Test
    public void testFilterRecords() throws IOException {
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, defaultConfig);
        
        Predicate<CSVHandler.CSVRecord> ageFilter = record -> {
            String age = record.getField("Age");
            return age != null && Integer.parseInt(age) >= 30;
        };
        
        List<CSVHandler.CSVRecord> filteredRecords = CSVHandler.filterRecords(records, ageFilter);
        
        assertEquals(2, filteredRecords.size());
        assertEquals("Bob", filteredRecords.get(0).getField("Name"));
        assertEquals("Charlie", filteredRecords.get(1).getField("Name"));
    }
    
    @Test
    public void testMapRecords() throws IOException {
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, defaultConfig);
        
        List<String> names = CSVHandler.mapRecords(records, record -> record.getField("Name"));
        
        assertEquals(3, names.size());
        assertEquals("Alice", names.get(0));
        assertEquals("Bob", names.get(1));
        assertEquals("Charlie", names.get(2));
    }
    
    @Test
    public void testGroupRecords() throws IOException {
        // 都市別グループ化のためのテストデータ
        String csvContent = "Name,Age,City\n" +
                           "Alice,25,Tokyo\n" +
                           "Bob,30,Osaka\n" +
                           "Charlie,35,Tokyo\n" +
                           "David,28,Osaka\n";
        
        Path groupTestFile = tempDir.resolve("group_test.csv");
        Files.write(groupTestFile, csvContent.getBytes());
        
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(groupTestFile, defaultConfig);
        Map<String, List<CSVHandler.CSVRecord>> grouped = 
            CSVHandler.groupRecords(records, record -> record.getField("City"));
        
        assertEquals(2, grouped.size());
        assertEquals(2, grouped.get("Tokyo").size());
        assertEquals(2, grouped.get("Osaka").size());
    }
    
    @Test
    public void testMergeCSVFiles() throws IOException {
        // 追加のCSVファイルを作成
        Path csvFile2 = tempDir.resolve("test2.csv");
        String csvContent2 = "Name,Age,City\n" +
                            "David,28,Fukuoka\n" +
                            "Eve,22,Sendai\n";
        Files.write(csvFile2, csvContent2.getBytes());
        
        List<Path> filePaths = Arrays.asList(csvFile, csvFile2);
        List<CSVHandler.CSVRecord> mergedRecords = CSVHandler.mergeCSVFiles(filePaths, defaultConfig);
        
        assertEquals(5, mergedRecords.size()); // 3 + 2 レコード
    }
    
    @Test
    public void testConvertCSVFormat() throws IOException {
        Path tsvFile = tempDir.resolve("output.tsv");
        CSVHandler.CSVConfig tsvConfig = CSVHandler.CSVConfig.tsvConfig();
        
        CSVHandler.convertCSVFormat(csvFile, tsvFile, defaultConfig, tsvConfig);
        
        assertTrue(Files.exists(tsvFile));
        
        // TSVファイルの内容を確認
        List<CSVHandler.CSVRecord> tsvRecords = CSVHandler.readCSV(tsvFile, tsvConfig);
        assertEquals(3, tsvRecords.size());
    }
    
    @Test
    public void testValidateCSV() throws IOException {
        // 無効なCSVファイルを作成（フィールド数が不一致）
        Path invalidCsvFile = tempDir.resolve("invalid.csv");
        String invalidContent = "Name,Age,City\n" +
                               "Alice,25,Tokyo\n" +
                               "Bob,30\n" + // フィールドが足りない
                               "Charlie,35,Kyoto,Extra\n"; // フィールドが多い
        Files.write(invalidCsvFile, invalidContent.getBytes());
        
        List<String> validationErrors = CSVHandler.validateCSV(invalidCsvFile, defaultConfig);
        
        assertEquals(2, validationErrors.size());
        assertTrue(validationErrors.get(0).contains("行 3"));
        assertTrue(validationErrors.get(1).contains("行 4"));
    }
    
    @Test
    public void testGeneratePreview() throws IOException {
        String preview = CSVHandler.generatePreview(csvFile, defaultConfig, 2);
        
        assertNotNull(preview);
        assertTrue(preview.contains("CSV プレビュー"));
        assertTrue(preview.contains("test.csv"));
        assertTrue(preview.contains("総レコード数: 3"));
        assertTrue(preview.contains("Alice"));
        assertTrue(preview.contains("Bob"));
        assertTrue(preview.contains("... 他 1 行")); // maxRows=2なので1行が省略される
    }
    
    @Test
    public void testParseLineWithQuotes() throws IOException {
        // 引用符を含むCSVファイル
        Path quotedCsvFile = tempDir.resolve("quoted.csv");
        String quotedContent = "Name,Description,Price\n" +
                              "\"Product A\",\"A great, useful product\",\"$10.00\"\n" +
                              "Product B,Simple product,5.50\n";
        Files.write(quotedCsvFile, quotedContent.getBytes());
        
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(quotedCsvFile, defaultConfig);
        
        assertEquals(2, records.size());
        assertEquals("Product A", records.get(0).getField("Name"));
        assertEquals("A great, useful product", records.get(0).getField("Description"));
        assertEquals("$10.00", records.get(0).getField("Price"));
    }
    
    @Test
    public void testCSVConfigMethods() {
        CSVHandler.CSVConfig config = CSVHandler.CSVConfig.defaultConfig();
        
        assertEquals(',', config.getDelimiter());
        assertEquals('"', config.getQuote());
        assertEquals('\\', config.getEscape());
        assertTrue(config.hasHeader());
        assertEquals("UTF-8", config.getEncoding());
        
        CSVHandler.CSVConfig tsvConfig = CSVHandler.CSVConfig.tsvConfig();
        assertEquals('\t', tsvConfig.getDelimiter());
    }
    
    @Test
    public void testCSVRecordWithNullField() throws IOException {
        List<String> fields = Arrays.asList("Alice", null, "Tokyo");
        List<String> headers = Arrays.asList("Name", "Age", "City");
        CSVHandler.CSVRecord record = new CSVHandler.CSVRecord(fields, headers, 1);
        
        assertEquals("Alice", record.getField("Name"));
        assertNull(record.getField("Age"));
        assertEquals("Tokyo", record.getField("City"));
        assertNull(record.getField(10)); // 範囲外インデックス
    }
    
    @Test
    public void testReadNonExistentFile() {
        Path nonExistent = tempDir.resolve("nonexistent.csv");
        
        assertThrows(IOException.class, () -> {
            CSVHandler.readCSV(nonExistent, defaultConfig);
        });
    }
    
    @Test
    public void testEmptyCSVFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.csv");
        Files.write(emptyFile, new byte[0]);
        
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(emptyFile, defaultConfig);
        
        assertTrue(records.isEmpty());
    }
    
    @Test
    public void testCSVStatisticsToString() throws IOException {
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, defaultConfig);
        CSVHandler.CSVStatistics stats = CSVHandler.generateStatistics(records);
        
        String statsString = stats.toString();
        assertNotNull(statsString);
        assertTrue(statsString.contains("CSV統計"));
        assertTrue(statsString.contains("レコード数=3"));
    }
    
    @Test
    public void testCSVRecordToString() throws IOException {
        List<CSVHandler.CSVRecord> records = CSVHandler.readCSV(csvFile, defaultConfig);
        CSVHandler.CSVRecord record = records.get(0);
        
        String recordString = record.toString();
        assertNotNull(recordString);
        assertTrue(recordString.contains("CSVRecord"));
        assertTrue(recordString.contains("line=2"));
        assertTrue(recordString.contains("Alice"));
    }
}