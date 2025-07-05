import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WatchServiceExampleクラスのテストクラス
 */
public class WatchServiceExampleTest {
    
    @TempDir
    Path tempDir;
    
    private WatchServiceExample.FileWatcher watcher;
    
    @AfterEach
    public void tearDown() {
        if (watcher != null) {
            watcher.stop();
        }
    }
    
    @Test
    public void testFileWatcherBasicFunctionality() throws IOException, InterruptedException {
        WatchServiceExample.WatchConfig config = WatchServiceExample.WatchConfig.defaultConfig();
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        AtomicInteger eventCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);
        
        watcher.addEventHandler(event -> {
            eventCount.incrementAndGet();
            latch.countDown();
        });
        
        assertFalse(watcher.isRunning());
        watcher.start();
        assertTrue(watcher.isRunning());
        
        // ファイルを作成してイベントを発生させる
        Files.write(tempDir.resolve("test.txt"), "Test content".getBytes());
        
        // イベントが発生するまで待機
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertTrue(eventCount.get() > 0);
        
        watcher.stop();
        assertFalse(watcher.isRunning());
    }
    
    @Test
    public void testFileEventTypes() throws IOException, InterruptedException {
        WatchServiceExample.WatchConfig config = WatchServiceExample.WatchConfig.defaultConfig();
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        Set<WatchServiceExample.FileEventType> observedEvents = new HashSet<>();
        CountDownLatch latch = new CountDownLatch(3); // CREATE, MODIFY, DELETE
        
        watcher.addEventHandler(event -> {
            observedEvents.add(event.getType());
            latch.countDown();
        });
        
        watcher.start();
        
        Path testFile = tempDir.resolve("test.txt");
        
        // ファイル作成
        Files.write(testFile, "Initial content".getBytes());
        
        // ファイル変更
        Files.write(testFile, "Modified content".getBytes());
        
        // ファイル削除
        Files.delete(testFile);
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        
        assertTrue(observedEvents.contains(WatchServiceExample.FileEventType.CREATED));
        assertTrue(observedEvents.contains(WatchServiceExample.FileEventType.MODIFIED));
        assertTrue(observedEvents.contains(WatchServiceExample.FileEventType.DELETED));
    }
    
    @Test
    public void testRecursiveWatching() throws IOException, InterruptedException {
        WatchServiceExample.WatchConfig config = new WatchServiceExample.WatchConfig(
            true, Set.of("*"), Set.of(), 
            EnumSet.of(WatchServiceExample.FileEventType.CREATED), true);
        
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        AtomicInteger eventCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);
        
        watcher.addEventHandler(event -> {
            eventCount.incrementAndGet();
            latch.countDown();
        });
        
        watcher.start();
        
        // サブディレクトリを作成
        Path subDir = tempDir.resolve("subdir");
        Files.createDirectories(subDir);
        
        // サブディレクトリ内にファイルを作成
        Files.write(subDir.resolve("nested.txt"), "Nested content".getBytes());
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertTrue(eventCount.get() >= 2); // ディレクトリ作成 + ファイル作成
    }
    
    @Test
    public void testExcludePatterns() throws IOException, InterruptedException {
        WatchServiceExample.WatchConfig config = new WatchServiceExample.WatchConfig(
            false, Set.of("*"), Set.of("*.tmp", "*.log"), 
            EnumSet.of(WatchServiceExample.FileEventType.CREATED), true);
        
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        AtomicInteger eventCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);
        
        watcher.addEventHandler(event -> {
            eventCount.incrementAndGet();
            latch.countDown();
        });
        
        watcher.start();
        
        // 除外対象ファイルを作成（イベントが発生しないはず）
        Files.write(tempDir.resolve("temp.tmp"), "Temp content".getBytes());
        Files.write(tempDir.resolve("debug.log"), "Log content".getBytes());
        
        // 通常ファイルを作成（イベントが発生するはず）
        Files.write(tempDir.resolve("normal.txt"), "Normal content".getBytes());
        
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(1, eventCount.get()); // normal.txt のみ
    }
    
    @Test
    public void testIncludePatterns() throws IOException, InterruptedException {
        WatchServiceExample.WatchConfig config = new WatchServiceExample.WatchConfig(
            false, Set.of("*.txt"), Set.of(), 
            EnumSet.of(WatchServiceExample.FileEventType.CREATED), true);
        
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        AtomicInteger eventCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);
        
        watcher.addEventHandler(event -> {
            eventCount.incrementAndGet();
            latch.countDown();
        });
        
        watcher.start();
        
        // 包含対象外ファイルを作成
        Files.write(tempDir.resolve("test.java"), "Java content".getBytes());
        Files.write(tempDir.resolve("readme.md"), "Markdown content".getBytes());
        
        // 包含対象ファイルを作成
        Files.write(tempDir.resolve("test.txt"), "Text content".getBytes());
        
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(1, eventCount.get()); // test.txt のみ
    }
    
    @Test
    public void testEventMetadata() throws IOException, InterruptedException {
        WatchServiceExample.WatchConfig config = new WatchServiceExample.WatchConfig(
            false, Set.of("*"), Set.of(), 
            EnumSet.of(WatchServiceExample.FileEventType.CREATED), true);
        
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        CountDownLatch latch = new CountDownLatch(1);
        final WatchServiceExample.FileEvent[] capturedEvent = new WatchServiceExample.FileEvent[1];
        
        watcher.addEventHandler(event -> {
            capturedEvent[0] = event;
            latch.countDown();
        });
        
        watcher.start();
        
        String content = "Test content for metadata";
        Files.write(tempDir.resolve("test.txt"), content.getBytes());
        
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        
        WatchServiceExample.FileEvent event = capturedEvent[0];
        assertNotNull(event);
        assertEquals(WatchServiceExample.FileEventType.CREATED, event.getType());
        
        Map<String, Object> metadata = event.getMetadata();
        assertNotNull(metadata);
        assertTrue(metadata.containsKey("size"));
        assertTrue(metadata.containsKey("lastModified"));
        assertTrue(metadata.containsKey("isDirectory"));
        assertTrue(metadata.containsKey("isRegularFile"));
        
        assertEquals((long) content.getBytes().length, metadata.get("size"));
        assertEquals(false, metadata.get("isDirectory"));
        assertEquals(true, metadata.get("isRegularFile"));
    }
    
    @Test
    public void testWatchStatistics() throws IOException, InterruptedException {
        WatchServiceExample.WatchConfig config = WatchServiceExample.WatchConfig.defaultConfig();
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        CountDownLatch latch = new CountDownLatch(3);
        
        watcher.addEventHandler(event -> latch.countDown());
        watcher.start();
        
        WatchServiceExample.WatchStatistics initialStats = watcher.getStatistics();
        assertEquals(0, initialStats.getTotalEvents());
        assertTrue(initialStats.getUptime() >= 0);
        
        // 複数のイベントを発生させる
        Path testFile = tempDir.resolve("stats_test.txt");
        Files.write(testFile, "Content 1".getBytes());
        Files.write(testFile, "Content 2".getBytes());
        Files.delete(testFile);
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        
        WatchServiceExample.WatchStatistics finalStats = watcher.getStatistics();
        assertTrue(finalStats.getTotalEvents() >= 3);
        assertTrue(finalStats.getUptime() > 0);
        assertTrue(finalStats.getLastEventTime() > initialStats.getStartTime());
        
        Map<WatchServiceExample.FileEventType, Long> eventCounts = finalStats.getEventCounts();
        assertTrue(eventCounts.containsKey(WatchServiceExample.FileEventType.CREATED));
        assertTrue(eventCounts.containsKey(WatchServiceExample.FileEventType.MODIFIED));
        assertTrue(eventCounts.containsKey(WatchServiceExample.FileEventType.DELETED));
    }
    
    @Test
    public void testFileEventClass() {
        Path testPath = Paths.get("test.txt");
        WatchServiceExample.FileEvent event = new WatchServiceExample.FileEvent(
            WatchServiceExample.FileEventType.CREATED, testPath);
        
        assertEquals(WatchServiceExample.FileEventType.CREATED, event.getType());
        assertEquals(testPath, event.getPath());
        assertTrue(event.getTimestamp() > 0);
        assertTrue(event.getMetadata().isEmpty());
        
        event.addMetadata("size", 1024L);
        assertEquals(1024L, event.getMetadata().get("size"));
        
        String eventString = event.toString();
        assertTrue(eventString.contains("CREATED"));
        assertTrue(eventString.contains("test.txt"));
    }
    
    @Test
    public void testWatchConfigMethods() {
        Set<String> includePatterns = Set.of("*.txt", "*.java");
        Set<String> excludePatterns = Set.of("*.tmp");
        Set<WatchServiceExample.FileEventType> eventTypes = 
            EnumSet.of(WatchServiceExample.FileEventType.CREATED, WatchServiceExample.FileEventType.MODIFIED);
        
        WatchServiceExample.WatchConfig config = new WatchServiceExample.WatchConfig(
            true, includePatterns, excludePatterns, eventTypes, false);
        
        assertTrue(config.isRecursive());
        assertEquals(includePatterns, config.getIncludePatterns());
        assertEquals(excludePatterns, config.getExcludePatterns());
        assertEquals(eventTypes, config.getEventTypes());
        assertFalse(config.isMetadataEnabled());
        
        WatchServiceExample.WatchConfig defaultConfig = WatchServiceExample.WatchConfig.defaultConfig();
        assertTrue(defaultConfig.isRecursive());
        assertTrue(defaultConfig.isMetadataEnabled());
        assertTrue(defaultConfig.getIncludePatterns().contains("*"));
        assertTrue(defaultConfig.getExcludePatterns().contains("*.tmp"));
    }
    
    @Test
    public void testMultipleEventHandlers() throws IOException, InterruptedException {
        WatchServiceExample.WatchConfig config = WatchServiceExample.WatchConfig.defaultConfig();
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        AtomicInteger handler1Count = new AtomicInteger(0);
        AtomicInteger handler2Count = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);
        
        watcher.addEventHandler(event -> {
            handler1Count.incrementAndGet();
            latch.countDown();
        });
        
        watcher.addEventHandler(event -> {
            handler2Count.incrementAndGet();
            latch.countDown();
        });
        
        watcher.start();
        
        Files.write(tempDir.resolve("multi_handler_test.txt"), "Test".getBytes());
        
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(1, handler1Count.get());
        assertEquals(1, handler2Count.get());
    }
    
    @Test
    public void testWatchNonExistentDirectory() {
        Path nonExistent = tempDir.resolve("nonexistent");
        WatchServiceExample.WatchConfig config = WatchServiceExample.WatchConfig.defaultConfig();
        
        assertThrows(IllegalArgumentException.class, () -> {
            new WatchServiceExample.FileWatcher(nonExistent, config).start();
        });
    }
    
    @Test
    public void testDoubleStart() throws IOException {
        WatchServiceExample.WatchConfig config = WatchServiceExample.WatchConfig.defaultConfig();
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        watcher.start();
        
        assertThrows(IllegalStateException.class, () -> {
            watcher.start();
        });
    }
    
    @Test
    public void testStopWithoutStart() {
        WatchServiceExample.WatchConfig config = WatchServiceExample.WatchConfig.defaultConfig();
        watcher = new WatchServiceExample.FileWatcher(tempDir, config);
        
        // 例外が発生しないことを確認
        assertDoesNotThrow(() -> watcher.stop());
    }
    
    @Test
    public void testWatchStatisticsToString() {
        WatchServiceExample.WatchStatistics stats = new WatchServiceExample.WatchStatistics();
        stats.recordEvent(WatchServiceExample.FileEventType.CREATED);
        stats.recordEvent(WatchServiceExample.FileEventType.MODIFIED);
        
        String statsString = stats.toString();
        assertNotNull(statsString);
        assertTrue(statsString.contains("監視統計"));
        assertTrue(statsString.contains("総イベント数=2"));
        assertTrue(statsString.contains("CREATED"));
        assertTrue(statsString.contains("MODIFIED"));
    }
}