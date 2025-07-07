package com.example.nio2.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * Basic file system watcher using WatchService.
 * Monitors directories for file creation, modification, and deletion events.
 */
public class FileWatcher implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(FileWatcher.class);
    
    private final WatchService watchService;
    private final Map<WatchKey, Path> keyMap = new HashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread watchThread;
    
    public FileWatcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        logger.info("FileWatcher initialized");
    }
    
    /**
     * Register a directory for watching.
     * @param directory the directory to watch
     * @throws IOException if registration fails
     */
    public void watchDirectory(Path directory) throws IOException {
        watchDirectory(directory, false);
    }
    
    /**
     * Register a directory for watching with optional recursive monitoring.
     * @param directory the directory to watch
     * @param recursive whether to watch subdirectories
     * @throws IOException if registration fails
     */
    public void watchDirectory(Path directory, boolean recursive) throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("Path must be a directory: " + directory);
        }
        
        if (recursive) {
            // Register all subdirectories
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    registerDirectory(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } else {
            registerDirectory(directory);
        }
    }
    
    private void registerDirectory(Path directory) throws IOException {
        WatchKey key = directory.register(
            watchService,
            ENTRY_CREATE,
            ENTRY_DELETE,
            ENTRY_MODIFY
        );
        
        Path previous = keyMap.put(key, directory);
        if (previous == null) {
            logger.info("Watching directory: {}", directory);
        } else {
            logger.info("Updated watch for directory: {}", directory);
        }
    }
    
    /**
     * Start watching in a separate thread.
     */
    public void startWatching() {
        if (running.compareAndSet(false, true)) {
            watchThread = new Thread(this::processEvents, "FileWatcher-Thread");
            watchThread.setDaemon(true);
            watchThread.start();
            logger.info("Started watching");
        }
    }
    
    /**
     * Stop watching.
     */
    public void stopWatching() {
        if (running.compareAndSet(true, false)) {
            if (watchThread != null) {
                watchThread.interrupt();
                try {
                    watchThread.join(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            logger.info("Stopped watching");
        }
    }
    
    private void processEvents() {
        while (running.get()) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                if (!running.get()) {
                    return;
                }
                Thread.currentThread().interrupt();
                return;
            }
            
            Path dir = keyMap.get(key);
            if (dir == null) {
                logger.error("WatchKey not recognized!");
                continue;
            }
            
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                
                if (kind == OVERFLOW) {
                    logger.warn("Events may have been lost or discarded");
                    continue;
                }
                
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                Path child = dir.resolve(filename);
                
                handleEvent(kind, child);
                
                // If a new directory is created, and we're in recursive mode, register it
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            registerDirectory(child);
                        }
                    } catch (IOException e) {
                        logger.error("Failed to register new directory: " + child, e);
                    }
                }
            }
            
            boolean valid = key.reset();
            if (!valid) {
                keyMap.remove(key);
                logger.warn("Directory no longer accessible: {}", dir);
                
                if (keyMap.isEmpty()) {
                    break;
                }
            }
        }
    }
    
    /**
     * Handle file system events. Override this method to customize behavior.
     * @param kind the type of event
     * @param path the path affected by the event
     */
    protected void handleEvent(WatchEvent.Kind<?> kind, Path path) {
        logger.info("{}: {}", kind.name(), path);
    }
    
    @Override
    public void close() {
        stopWatching();
        try {
            watchService.close();
        } catch (IOException e) {
            logger.error("Error closing WatchService", e);
        }
    }
}