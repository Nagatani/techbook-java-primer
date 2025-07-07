package com.example.nio2.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Advanced file watcher with debouncing, filtering, and custom processors.
 */
public class AdvancedFileWatcher extends FileWatcher {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedFileWatcher.class);
    
    private final ExecutorService executor;
    private final Map<String, FileProcessor> processors = new ConcurrentHashMap<>();
    private final Map<Path, ScheduledFuture<?>> pendingEvents = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    private Duration debounceDelay = Duration.ofMillis(500);
    
    public AdvancedFileWatcher() throws IOException {
        this(Runtime.getRuntime().availableProcessors());
    }
    
    public AdvancedFileWatcher(int threadPoolSize) throws IOException {
        super();
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        this.scheduler = Executors.newScheduledThreadPool(1);
        logger.info("Advanced FileWatcher initialized with {} worker threads", threadPoolSize);
    }
    
    /**
     * Set the debounce delay for file events.
     * @param delay the delay duration
     */
    public void setDebounceDelay(Duration delay) {
        this.debounceDelay = delay;
        logger.info("Debounce delay set to {}ms", delay.toMillis());
    }
    
    /**
     * Register a processor for files matching a pattern.
     * @param pattern file pattern (e.g., "*.log", "*.csv")
     * @param processor the processor to handle matching files
     */
    public void registerProcessor(String pattern, FileProcessor processor) {
        processors.put(pattern, processor);
        logger.info("Registered processor for pattern: {}", pattern);
    }
    
    /**
     * Register a simple consumer for files matching a pattern.
     * @param pattern file pattern
     * @param consumer the consumer to handle matching files
     */
    public void registerConsumer(String pattern, Consumer<Path> consumer) {
        registerProcessor(pattern, new FileProcessor() {
            @Override
            public void process(Path file, WatchEvent.Kind<?> eventType) {
                consumer.accept(file);
            }
        });
    }
    
    @Override
    protected void handleEvent(WatchEvent.Kind<?> kind, Path path) {
        // Cancel any pending event for this path
        ScheduledFuture<?> existing = pendingEvents.get(path);
        if (existing != null && !existing.isDone()) {
            existing.cancel(false);
            logger.debug("Cancelled pending event for: {}", path);
        }
        
        // Schedule new event with debounce
        ScheduledFuture<?> future = scheduler.schedule(() -> {
            processFileEvent(kind, path);
            pendingEvents.remove(path);
        }, debounceDelay.toMillis(), TimeUnit.MILLISECONDS);
        
        pendingEvents.put(path, future);
    }
    
    private void processFileEvent(WatchEvent.Kind<?> kind, Path path) {
        String filename = path.getFileName().toString();
        
        // Find matching processors
        processors.entrySet().stream()
            .filter(entry -> matchesPattern(filename, entry.getKey()))
            .forEach(entry -> {
                logger.debug("Processing {} with handler for pattern: {}", filename, entry.getKey());
                executor.submit(() -> {
                    try {
                        entry.getValue().process(path, kind);
                    } catch (Exception e) {
                        logger.error("Error processing file: " + path, e);
                    }
                });
            });
        
        // Log unhandled files
        if (processors.entrySet().stream().noneMatch(entry -> matchesPattern(filename, entry.getKey()))) {
            logger.info("No processor for {}: {}", kind.name(), path);
        }
    }
    
    private boolean matchesPattern(String filename, String pattern) {
        // Convert simple glob pattern to regex
        String regex = pattern.replace(".", "\\.")
                             .replace("*", ".*")
                             .replace("?", ".");
        return Pattern.matches(regex, filename);
    }
    
    @Override
    public void close() {
        super.close();
        
        // Cancel pending events
        pendingEvents.values().forEach(future -> future.cancel(false));
        pendingEvents.clear();
        
        // Shutdown executors
        scheduler.shutdown();
        executor.shutdown();
        
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        logger.info("Advanced FileWatcher closed");
    }
    
    /**
     * Interface for processing file events.
     */
    public interface FileProcessor {
        void process(Path file, WatchEvent.Kind<?> eventType);
    }
    
    /**
     * Built-in processor for log files.
     */
    public static class LogFileProcessor implements FileProcessor {
        private static final Logger logger = LoggerFactory.getLogger(LogFileProcessor.class);
        
        @Override
        public void process(Path file, WatchEvent.Kind<?> eventType) {
            if (eventType == StandardWatchEventKinds.ENTRY_MODIFY) {
                logger.info("Log file modified: {}", file);
                // TODO: Implement log parsing logic
            }
        }
    }
    
    /**
     * Built-in processor for CSV files.
     */
    public static class CsvFileProcessor implements FileProcessor {
        private static final Logger logger = LoggerFactory.getLogger(CsvFileProcessor.class);
        
        @Override
        public void process(Path file, WatchEvent.Kind<?> eventType) {
            if (eventType == StandardWatchEventKinds.ENTRY_CREATE) {
                logger.info("New CSV file detected: {}", file);
                // TODO: Implement CSV processing logic
            }
        }
    }
}