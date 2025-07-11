package com.example.nio2.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstration of advanced file watching with debouncing and processors.
 */
public class AdvancedWatcherDemo {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedWatcherDemo.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java AdvancedWatcherDemo <directory-to-watch>");
            System.exit(1);
        }
        
        Path watchDir = Paths.get(args[0]);
        
        if (!Files.exists(watchDir) || !Files.isDirectory(watchDir)) {
            System.err.println("Directory does not exist: " + watchDir);
            System.exit(1);
        }
        
        try {
            demonstrateAdvancedWatcher(watchDir);
        } catch (Exception e) {
            logger.error("Error in demo", e);
        }
    }
    
    private static void demonstrateAdvancedWatcher(Path directory) throws IOException, InterruptedException {
        System.out.println("=== Advanced File Watcher Demo ===");
        System.out.println("Watching directory: " + directory.toAbsolutePath());
        System.out.println("Features: Debouncing, File type processors, Statistics");
        System.out.println("Press Ctrl+C to stop...\n");
        
        // Statistics tracking
        AtomicInteger totalEvents = new AtomicInteger();
        AtomicInteger logEvents = new AtomicInteger();
        AtomicInteger csvEvents = new AtomicInteger();
        AtomicInteger jsonEvents = new AtomicInteger();
        
        try (AdvancedFileWatcher watcher = new AdvancedFileWatcher()) {
            // Configure debouncing to prevent rapid-fire events
            watcher.setDebounceDelay(Duration.ofSeconds(1));
            
            // Register processors for different file types
            watcher.registerProcessor("*.log", new AdvancedFileWatcher.FileProcessor() {
                @Override
                public void process(Path file, WatchEvent.Kind<?> eventType) {
                    logEvents.incrementAndGet();
                    totalEvents.incrementAndGet();
                    System.out.printf("[%s] LOG FILE %s: %s (size: %s)%n",
                        getCurrentTime(),
                        getEventName(eventType),
                        file.getFileName(),
                        getFileSize(file)
                    );
                    
                    // Simulate log parsing
                    if (eventType == StandardWatchEventKinds.ENTRY_MODIFY) {
                        simulateLogParsing(file);
                    }
                }
            });
            
            watcher.registerProcessor("*.csv", new AdvancedFileWatcher.FileProcessor() {
                @Override
                public void process(Path file, WatchEvent.Kind<?> eventType) {
                    csvEvents.incrementAndGet();
                    totalEvents.incrementAndGet();
                    System.out.printf("[%s] CSV FILE %s: %s (rows: ~%d)%n",
                        getCurrentTime(),
                        getEventName(eventType),
                        file.getFileName(),
                        estimateCsvRows(file)
                    );
                }
            });
            
            watcher.registerProcessor("*.json", new AdvancedFileWatcher.FileProcessor() {
                @Override
                public void process(Path file, WatchEvent.Kind<?> eventType) {
                    jsonEvents.incrementAndGet();
                    totalEvents.incrementAndGet();
                    System.out.printf("[%s] JSON FILE %s: %s%n",
                        getCurrentTime(),
                        getEventName(eventType),
                        file.getFileName()
                    );
                }
            });
            
            // Generic processor for other files
            watcher.registerConsumer("*", path -> {
                String filename = path.getFileName().toString();
                if (!filename.endsWith(".log") && !filename.endsWith(".csv") && !filename.endsWith(".json")) {
                    totalEvents.incrementAndGet();
                    System.out.printf("[%s] OTHER FILE: %s%n", getCurrentTime(), filename);
                }
            });
            
            // Start watching
            watcher.watchDirectory(directory, true);
            watcher.startWatching();
            
            // Print statistics periodically
            Thread statsThread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(10000); // Every 10 seconds
                        printStatistics(totalEvents, logEvents, csvEvents, jsonEvents);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            statsThread.setDaemon(true);
            statsThread.start();
            
            // Keep the main thread alive
            Thread.currentThread().join();
        }
    }
    
    private static void simulateLogParsing(Path logFile) {
        try {
            // Simulate reading last few lines
            Thread.sleep(100);
            System.out.printf("  -> Parsed log file: Found 3 ERROR, 5 WARN, 12 INFO messages%n");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private static long estimateCsvRows(Path csvFile) {
        try {
            long size = Files.size(csvFile);
            return size / 50; // Rough estimate: 50 bytes per row
        } catch (IOException e) {
            return 0;
        }
    }
    
    private static String getFileSize(Path file) {
        try {
            long bytes = Files.size(file);
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
            return (bytes / (1024 * 1024)) + " MB";
        } catch (IOException e) {
            return "unknown";
        }
    }
    
    private static String getCurrentTime() {
        return LocalDateTime.now().format(TIME_FORMATTER);
    }
    
    private static String getEventName(WatchEvent.Kind<?> kind) {
        if (kind == StandardWatchEventKinds.ENTRY_CREATE) return "CREATED";
        if (kind == StandardWatchEventKinds.ENTRY_DELETE) return "DELETED";
        if (kind == StandardWatchEventKinds.ENTRY_MODIFY) return "MODIFIED";
        return "UNKNOWN";
    }
    
    private static void printStatistics(AtomicInteger total, AtomicInteger logs, 
                                       AtomicInteger csvs, AtomicInteger jsons) {
        System.out.println("\n--- Statistics ---");
        System.out.printf("Total events: %d%n", total.get());
        System.out.printf("Log files: %d%n", logs.get());
        System.out.printf("CSV files: %d%n", csvs.get());
        System.out.printf("JSON files: %d%n", jsons.get());
        System.out.println("------------------\n");
    }
}