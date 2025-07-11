package com.example.nio2.async;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Demonstration of asynchronous file copying with progress tracking.
 */
public class AsyncFileCopyDemo {
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java AsyncFileCopyDemo <source-file> <destination-file>");
            System.exit(1);
        }
        
        Path source = Paths.get(args[0]);
        Path destination = Paths.get(args[1]);
        
        if (!Files.exists(source)) {
            System.err.println("Source file does not exist: " + source);
            System.exit(1);
        }
        
        try {
            demonstrateAsyncCopy(source, destination);
            demonstrateAsyncReadWrite();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void demonstrateAsyncCopy(Path source, Path destination) throws Exception {
        System.out.println("=== Async File Copy Demo ===");
        System.out.printf("Source: %s%n", source.toAbsolutePath());
        System.out.printf("Destination: %s%n", destination.toAbsolutePath());
        
        long fileSize = Files.size(source);
        System.out.printf("File size: %s%n%n", formatSize(fileSize));
        
        AsyncFileIO asyncIO = new AsyncFileIO();
        
        // Progress tracking
        AtomicLong lastUpdate = new AtomicLong(System.currentTimeMillis());
        AsyncFileIO.ProgressListener progressListener = new AsyncFileIO.ProgressListener() {
            @Override
            public void onProgress(long bytesTransferred, long totalBytes, double percentage) {
                long now = System.currentTimeMillis();
                if (now - lastUpdate.get() > 100) { // Update every 100ms
                    lastUpdate.set(now);
                    printProgress(bytesTransferred, totalBytes, percentage);
                }
            }
            
            @Override
            public void onComplete(long totalBytes) {
                printProgress(totalBytes, totalBytes, 1.0);
                System.out.println("\nCopy completed successfully!");
            }
        };
        
        long startTime = System.currentTimeMillis();
        
        CompletableFuture<Long> copyFuture = asyncIO.copyFileAsync(source, destination, progressListener);
        
        // Wait for completion
        Long bytesCopied = copyFuture.get();
        long duration = System.currentTimeMillis() - startTime;
        
        double throughput = (double) bytesCopied / duration * 1000 / (1024 * 1024); // MB/s
        System.out.printf("Copied %s in %dms (%.2f MB/s)%n", 
            formatSize(bytesCopied), duration, throughput);
        
        // Verify copy
        if (Files.exists(destination)) {
            long destSize = Files.size(destination);
            if (destSize == fileSize) {
                System.out.println("Verification: SUCCESS - File sizes match");
            } else {
                System.err.printf("Verification: FAILED - Size mismatch (expected %d, got %d)%n", 
                    fileSize, destSize);
            }
        }
    }
    
    private static void demonstrateAsyncReadWrite() throws Exception {
        System.out.println("\n=== Async Read/Write Demo ===");
        
        AsyncFileIO asyncIO = new AsyncFileIO();
        Path tempFile = Files.createTempFile("async-demo", ".txt");
        
        try {
            // Write content asynchronously
            String content = "This is a test of asynchronous file I/O.\n" +
                           "Multiple lines of text are being written.\n" +
                           "This demonstrates non-blocking file operations.";
            
            System.out.println("Writing content asynchronously...");
            CompletableFuture<Void> writeFuture = asyncIO.writeFileAsync(tempFile, content);
            writeFuture.get(); // Wait for write to complete
            System.out.println("Write completed");
            
            // Read content asynchronously
            System.out.println("Reading content asynchronously...");
            CompletableFuture<String> readFuture = asyncIO.readFileAsync(tempFile);
            String readContent = readFuture.get();
            System.out.println("Read completed");
            
            // Verify content
            if (content.equals(readContent)) {
                System.out.println("Verification: SUCCESS - Content matches");
            } else {
                System.err.println("Verification: FAILED - Content mismatch");
            }
            
            // Append content asynchronously
            String appendContent = "\nThis line was appended asynchronously.";
            System.out.println("Appending content asynchronously...");
            CompletableFuture<Void> appendFuture = asyncIO.appendFileAsync(tempFile, appendContent);
            appendFuture.get();
            System.out.println("Append completed");
            
            // Read again to verify append
            String finalContent = asyncIO.readFileAsync(tempFile).get();
            if (finalContent.equals(content + appendContent)) {
                System.out.println("Append verification: SUCCESS");
            } else {
                System.err.println("Append verification: FAILED");
            }
            
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
    
    private static void printProgress(long current, long total, double percentage) {
        int barLength = 50;
        int filled = (int) (barLength * percentage);
        
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                bar.append("=");
            } else if (i == filled) {
                bar.append(">");
            } else {
                bar.append(" ");
            }
        }
        bar.append("]");
        
        System.out.printf("\r%s %3d%% (%s / %s)", 
            bar.toString(), 
            (int) (percentage * 100),
            formatSize(current),
            formatSize(total)
        );
    }
    
    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}