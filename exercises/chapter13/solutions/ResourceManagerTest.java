import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.List;
import java.util.ArrayList;

/**
 * ResourceManagerクラスのテストクラス
 */
public class ResourceManagerTest {
    
    @TempDir
    Path tempDir;
    
    private TestLogHandler logHandler;
    private Logger logger;
    
    @BeforeEach
    public void setUp() {
        logHandler = new TestLogHandler();
        logger = Logger.getLogger(ResourceManager.class.getName());
        logger.addHandler(logHandler);
        logger.setLevel(Level.ALL);
    }
    
    @AfterEach
    public void tearDown() {
        logger.removeHandler(logHandler);
    }
    
    /**
     * テスト用のログハンドラー
     */
    private static class TestLogHandler extends Handler {
        private final List<LogRecord> records = new ArrayList<>();
        
        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }
        
        @Override
        public void flush() {}
        
        @Override
        public void close() throws SecurityException {}
        
        public List<LogRecord> getRecords() {
            return records;
        }
        
        public boolean hasMessage(String message) {
            return records.stream()
                    .anyMatch(record -> record.getMessage().contains(message));
        }
    }
    
    @Test
    public void testCustomResourceBasicUsage() throws Exception {
        ResourceManager.CustomResource resource = new ResourceManager.CustomResource("test-resource");
        assertFalse(resource.isClosed());
        
        resource.doWork();
        resource.close();
        
        assertTrue(resource.isClosed());
        assertEquals("test-resource", resource.getName());
    }
    
    @Test
    public void testCustomResourceAutoClose() {
        String result = ResourceManager.useMultipleResources("resource1", "resource2");
        
        assertTrue(result.contains("両方のリソースでの作業が完了しました"));
        assertTrue(logHandler.hasMessage("リソース 'resource1' を開きました"));
        assertTrue(logHandler.hasMessage("リソース 'resource2' を開きました"));
        assertTrue(logHandler.hasMessage("リソース 'resource1' を閉じました"));
        assertTrue(logHandler.hasMessage("リソース 'resource2' を閉じました"));
    }
    
    @Test
    public void testProblematicResourceWorkFailure() {
        String result = ResourceManager.handleProblematicResource("prob-resource", true, false);
        
        assertTrue(result.contains("メイン処理でエラー"));
        assertTrue(result.contains("作業中にエラーが発生しました"));
        assertTrue(logHandler.hasMessage("問題のあるリソース 'prob-resource' を開きました"));
        assertTrue(logHandler.hasMessage("問題のあるリソース 'prob-resource' を閉じています"));
    }
    
    @Test
    public void testProblematicResourceCloseFailure() {
        String result = ResourceManager.handleProblematicResource("prob-resource", false, true);
        
        assertTrue(result.contains("作業を正常に完了しました"));
        // close()での例外は抑制された例外として扱われる
        assertTrue(logHandler.hasMessage("問題のあるリソース 'prob-resource' を開きました"));
        assertTrue(logHandler.hasMessage("問題のあるリソース 'prob-resource' を閉じています"));
    }
    
    @Test
    public void testProblematicResourceBothFailure() {
        String result = ResourceManager.handleProblematicResource("prob-resource", true, true);
        
        assertTrue(result.contains("メイン処理でエラー"));
        assertTrue(result.contains("作業中にエラーが発生しました"));
        assertTrue(result.contains("抑制された例外"));
        assertTrue(result.contains("クローズ中にエラーが発生しました"));
    }
    
    @Test
    public void testFileOperations() throws IOException {
        // テストファイルの作成
        Path sourceFile = tempDir.resolve("source.txt");
        Path targetFile = tempDir.resolve("target.txt");
        String content = "Hello, World!\nThis is a test file.";
        
        Files.writeString(sourceFile, content);
        
        // ファイルコピーテスト
        ResourceManager.copyFile(sourceFile.toString(), targetFile.toString());
        
        assertTrue(Files.exists(targetFile));
        String copiedContent = Files.readString(targetFile);
        assertEquals(content, copiedContent);
    }
    
    @Test
    public void testTextFileOperations() throws IOException {
        Path testFile = tempDir.resolve("test.txt");
        String content = "Line 1\nLine 2\nLine 3";
        
        // 書き込みテスト
        ResourceManager.writeTextFile(testFile.toString(), content);
        assertTrue(Files.exists(testFile));
        
        // 読み取りテスト
        String readContent = ResourceManager.readTextFile(testFile.toString());
        assertTrue(readContent.contains("Line 1"));
        assertTrue(readContent.contains("Line 2"));
        assertTrue(readContent.contains("Line 3"));
    }
    
    @Test
    public void testNestedTryWithResources() {
        String result = ResourceManager.nestedTryWithResources("outer", "inner");
        
        assertTrue(result.contains("ネストしたリソースの処理が完了しました"));
        assertTrue(logHandler.hasMessage("リソース 'outer' を開きました"));
        assertTrue(logHandler.hasMessage("リソース 'inner' を開きました"));
        assertTrue(logHandler.hasMessage("リソース 'inner' を閉じました"));
        assertTrue(logHandler.hasMessage("リソース 'outer' を閉じました"));
    }
    
    @Test
    public void testResourceInitializationFailure() {
        String result = ResourceManager.handleResourceInitializationFailure(true);
        
        assertTrue(result.contains("リソース初期化または処理でエラー"));
        assertTrue(result.contains("リソース初期化に失敗しました"));
    }
    
    @Test
    public void testResourceInitializationSuccess() {
        String result = ResourceManager.handleResourceInitializationFailure(false);
        
        assertTrue(result.contains("リソース処理が完了しました"));
        assertTrue(logHandler.hasMessage("リソース 'normal-resource' を開きました"));
        assertTrue(logHandler.hasMessage("リソース 'normal-resource' を閉じました"));
    }
    
    @Test
    public void testExceptionChain() {
        String result = ResourceManager.demonstrateExceptionChain(3);
        
        assertTrue(result.contains("例外をキャッチしました"));
        assertTrue(result.contains("原因: Exception"));
        assertTrue(result.contains("原因: IllegalArgumentException"));
        assertTrue(result.contains("根本原因: 無効な引数です"));
    }
    
    @Test
    public void testExceptionChainDepthZero() {
        String result = ResourceManager.demonstrateExceptionChain(0);
        
        assertTrue(result.contains("例外をキャッチしました"));
        assertTrue(result.contains("根本原因: 無効な引数です"));
    }
    
    @Test
    public void testCustomResourceClosedStateException() {
        assertThrows(Exception.class, () -> {
            ResourceManager.CustomResource resource = new ResourceManager.CustomResource("test");
            resource.close();
            resource.doWork(); // 閉じられた後に作業を試行
        });
    }
    
    @Test
    public void testFileOperationsWithNonExistentFile() {
        assertThrows(IOException.class, () -> {
            ResourceManager.readTextFile("non-existent-file.txt");
        });
    }
    
    @Test
    public void testCopyFileWithNonExistentSource() {
        assertThrows(IOException.class, () -> {
            ResourceManager.copyFile("non-existent-source.txt", "target.txt");
        });
    }
    
    @Test
    public void testUseMultipleResourcesWithException() {
        // リソース名が不足している場合
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            ResourceManager.useMultipleResources("resource1");
        });
    }
    
    @Test
    public void testLoggerMessages() {
        ResourceManager.useMultipleResources("test1", "test2");
        
        List<LogRecord> records = logHandler.getRecords();
        assertTrue(records.size() > 0);
        
        boolean foundOpenMessage = false;
        boolean foundCloseMessage = false;
        
        for (LogRecord record : records) {
            if (record.getMessage().contains("を開きました")) {
                foundOpenMessage = true;
            }
            if (record.getMessage().contains("を閉じました")) {
                foundCloseMessage = true;
            }
        }
        
        assertTrue(foundOpenMessage);
        assertTrue(foundCloseMessage);
    }
}