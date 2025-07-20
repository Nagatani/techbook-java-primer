package com.example.nio2.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demonstration of FileWatcher capabilities.
 */
public class FileWatcherDemo {
    private static final Logger logger = LoggerFactory.getLogger(FileWatcherDemo.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java FileWatcherDemo <directory-to-watch> [recursive]");
            System.exit(1);
        }
        
        Path watchDir = Paths.get(args[0]);
        boolean recursive = args.length > 1 && "recursive".equalsIgnoreCase(args[1]);
        
        if (!Files.exists(watchDir) || !Files.isDirectory(watchDir)) {
            System.err.println("Directory does not exist: " + watchDir);
            System.exit(1);
        }
        
        try {
            demonstrateBasicWatcher(watchDir, recursive);
        } catch (Exception e) {
            logger.error("Error in demo", e);
        }
    }
    
    private static void demonstrateBasicWatcher(Path directory, boolean recursive) throws IOException, InterruptedException {
        System.out.println("=== Basic File Watcher Demo ===");
        System.out.println("Watching directory: " + directory.toAbsolutePath());
        System.out.println("Recursive: " + recursive);
        System.out.println("Press Ctrl+C to stop...\n");
        
        try (CustomFileWatcher watcher = new CustomFileWatcher()) {
            watcher.watchDirectory(directory, recursive);
            watcher.startWatching();
            
            // Keep the main thread alive
            Thread.currentThread().join();
        }
    }
    
    /**
     * Custom file watcher that formats output nicely.
     */
    static class CustomFileWatcher extends FileWatcher {
        CustomFileWatcher() throws IOException {
            super();
        }
        
        @Override
        protected void handleEvent(WatchEvent.Kind<?> kind, Path path) {
            String time = LocalDateTime.now().format(TIME_FORMATTER);
            String eventType = formatEventType(kind);
            String fileType = getFileType(path);
            
            System.out.printf("[%s] %s %s: %s%n", 
                time, 
                eventType, 
                fileType,
                path.toAbsolutePath()
            );
        }
        
        private String formatEventType(WatchEvent.Kind<?> kind) {
            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                return "CREATED ";
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                return "DELETED ";
            } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                return "MODIFIED";
            } else {
                return "UNKNOWN ";
            }
        }
        
        private String getFileType(Path path) {
            try {
                if (Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
                    return "[DIR] ";
                } else {
                    return "[FILE]";
                }
            } catch (Exception e) {
                return "[????]";
            }
        }
    }
}