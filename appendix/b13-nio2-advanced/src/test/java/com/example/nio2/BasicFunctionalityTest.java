package com.example.nio2;

import com.example.nio2.async.AsyncFileIO;
import com.example.nio2.memory.MemoryMappedFile;
import com.example.nio2.reactive.ReactiveFileReader;
import com.example.nio2.utils.FileUtils;
import com.example.nio2.watcher.AdvancedFileWatcher;
import com.example.nio2.watcher.FileWatcher;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.concurrent.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Basic functionality tests for NIO.2 advanced features.
 */
public class BasicFunctionalityTest {
    
    private Path tempDir;
    
    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("nio2-test");
    }
    
    @AfterEach
    void tearDown() throws IOException {
        // Files.walkのストリームは開いたディレクトリを持つので、try-with-resourcesで閉じる
        try (Stream<Path> paths = Files.walk(tempDir)) {
            paths.sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        // Ignore
                    }
                });
        }
    }
    
    @Test
    void testFileWatcher() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Path testFile = tempDir.resolve("test.txt");
        
        try (FileWatcher watcher = new FileWatcher() {
            @Override
            protected void handleEvent(WatchEvent.Kind<?> kind, Path path) {
                if (kind == StandardWatchEventKinds.ENTRY_CREATE && 
                    path.getFileName().toString().equals("test.txt")) {
                    latch.countDown();
                }
            }
        }) {
            watcher.watchDirectory(tempDir);
            watcher.startWatching();
            
            // Create file
            Files.writeString(testFile, "Hello NIO.2");
            
            // Wait for event
            assertTrue(latch.await(5, TimeUnit.SECONDS), "File creation event not detected");
        }
    }
    
    @Test
    void testAsyncFileIO() throws Exception {
        Path testFile = tempDir.resolve("async.txt");
        String content = "This is async content";
        
        AsyncFileIO asyncIO = new AsyncFileIO();
        
        // Test write
        CompletableFuture<Void> writeFuture = asyncIO.writeFileAsync(testFile, content);
        writeFuture.get(5, TimeUnit.SECONDS);
        
        assertTrue(Files.exists(testFile), "File not created");
        
        // Test read
        CompletableFuture<String> readFuture = asyncIO.readFileAsync(testFile);
        String readContent = readFuture.get(5, TimeUnit.SECONDS);
        
        assertEquals(content, readContent, "Content mismatch");
    }
    
    @Test
    void testMemoryMappedFile() throws IOException {
        Path testFile = tempDir.resolve("mapped.dat");
        Files.writeString(testFile, "Memory mapped content");
        
        try (MemoryMappedFile mmf = new MemoryMappedFile(testFile, FileChannel.MapMode.READ_ONLY)) {
            mmf.mapEntireFile();
            
            String read = mmf.readString(0, 6);
            assertEquals("Memory", read, "Read content mismatch");
        }
    }
    
    @Test
    void testAsyncFileCopy() throws Exception {
        Path source = tempDir.resolve("source.dat");
        Path target = tempDir.resolve("target.dat");
        
        // Create source file
        byte[] data = new byte[1024 * 1024]; // 1MB
        Files.write(source, data);
        
        AsyncFileIO asyncIO = new AsyncFileIO();
        AtomicBoolean progressCalled = new AtomicBoolean(false);
        
        CompletableFuture<Long> copyFuture = asyncIO.copyFileAsync(
            source, target, 
            new AsyncFileIO.ProgressListener() {
                @Override
                public void onProgress(long bytesTransferred, long totalBytes, double percentage) {
                    progressCalled.set(true);
                }
                
                @Override
                public void onComplete(long totalBytes) {
                    assertEquals(data.length, totalBytes, "Copy size mismatch");
                }
            }
        );
        
        Long bytesCopied = copyFuture.get(10, TimeUnit.SECONDS);
        
        assertEquals(data.length, bytesCopied, "Bytes copied mismatch");
        assertTrue(Files.exists(target), "Target file not created");
        assertEquals(Files.size(source), Files.size(target), "File sizes don't match");
        assertTrue(progressCalled.get(), "Progress listener not called");
    }

    @Test
    void testDebounceKeepsCreateEvent() throws Exception {
        // 作ってすぐ書き込んだファイルは、デバウンスのあとも ENTRY_CREATE として処理される
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<WatchEvent.Kind<?>> seen = new AtomicReference<>();
        try (AdvancedFileWatcher watcher = new AdvancedFileWatcher(1)) {
            watcher.setDebounceDelay(Duration.ofMillis(300));
            watcher.registerProcessor("*.csv", (file, kind) -> {
                seen.set(kind);
                latch.countDown();
            });
            watcher.watchDirectory(tempDir);
            watcher.startWatching();

            Path csv = tempDir.resolve("data.csv");
            Files.writeString(csv, "a,b\n");
            Files.writeString(csv, "1,2\n", StandardOpenOption.APPEND);

            assertTrue(latch.await(10, TimeUnit.SECONDS), "CSV event not processed");
            assertEquals(StandardWatchEventKinds.ENTRY_CREATE, seen.get());
        }
    }

    @Test
    void testMergeFilesTruncatesTarget() throws IOException {
        // 結合先に長いファイルが残っていても、古い内容が後ろに残らない
        Path a = Files.writeString(tempDir.resolve("a.part"), "abc");
        Path b = Files.writeString(tempDir.resolve("b.part"), "def");
        Path target = Files.writeString(tempDir.resolve("merged.txt"), "0123456789ABCDEF");

        FileUtils.mergeFiles(List.of(a, b), target);

        assertEquals("abcdef", Files.readString(target));
        assertTrue(FileUtils.compareFiles(target, Files.writeString(tempDir.resolve("expected.txt"), "abcdef")));
    }

    @Test
    void testReactiveReaderKeepsMultibyteCharacters() throws Exception {
        // 5バイトずつ読むと、3バイトの文字（「あ」など）が読み込みの境目で切れる
        String text = "あいうえおかきくけこ";
        Path file = Files.writeString(tempDir.resolve("utf8.txt"), text);
        StringBuilder received = new StringBuilder();
        CompletableFuture<String> done = new CompletableFuture<>();

        new ReactiveFileReader(file, 5).subscribe(new Flow.Subscriber<String>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription s) {
                subscription = s;
                s.request(1);
            }

            @Override
            public void onNext(String item) {
                received.append(item);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                done.completeExceptionally(t);
            }

            @Override
            public void onComplete() {
                done.complete(received.toString());
            }
        });

        assertEquals(text, done.get(5, TimeUnit.SECONDS));
    }
}
