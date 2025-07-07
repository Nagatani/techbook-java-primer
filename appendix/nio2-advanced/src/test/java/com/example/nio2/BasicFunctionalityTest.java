package com.example.nio2;

import com.example.nio2.async.AsyncFileIO;
import com.example.nio2.memory.MemoryMappedFile;
import com.example.nio2.watcher.FileWatcher;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

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
        Files.walk(tempDir)
            .sorted((a, b) -> -a.compareTo(b))
            .forEach(path -> {
                try {
                    Files.delete(path);
                } catch (IOException e) {
                    // Ignore
                }
            });
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
}