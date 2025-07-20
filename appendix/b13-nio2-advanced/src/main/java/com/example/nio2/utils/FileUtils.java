package com.example.nio2.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods for advanced file operations using NIO.2.
 */
public class FileUtils {
    
    /**
     * Copy directory recursively using NIO.2.
     */
    public static void copyDirectoryRecursively(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) 
                    throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
                    throws IOException {
                Path targetFile = target.resolve(source.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * Delete directory recursively.
     */
    public static void deleteDirectoryRecursively(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) 
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) 
                    throws IOException {
                if (exc != null) {
                    throw exc;
                }
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    
    /**
     * Find files matching multiple patterns.
     */
    public static List<Path> findFiles(Path directory, String... patterns) throws IOException {
        List<PathMatcher> matchers = Arrays.stream(patterns)
            .map(pattern -> FileSystems.getDefault().getPathMatcher("glob:" + pattern))
            .collect(Collectors.toList());
        
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream
                .filter(Files::isRegularFile)
                .filter(path -> matchers.stream().anyMatch(matcher -> matcher.matches(path.getFileName())))
                .collect(Collectors.toList());
        }
    }
    
    /**
     * Calculate directory size recursively.
     */
    public static long calculateDirectorySize(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory)) {
            return stream
                .filter(Files::isRegularFile)
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .sum();
        }
    }
    
    /**
     * Compare two files efficiently using memory-mapped files.
     */
    public static boolean compareFiles(Path file1, Path file2) throws IOException {
        if (Files.size(file1) != Files.size(file2)) {
            return false;
        }
        
        try (RandomAccessFile raf1 = new RandomAccessFile(file1.toFile(), "r");
             RandomAccessFile raf2 = new RandomAccessFile(file2.toFile(), "r");
             FileChannel channel1 = raf1.getChannel();
             FileChannel channel2 = raf2.getChannel()) {
            
            long size = channel1.size();
            long position = 0;
            long chunkSize = 1024 * 1024; // 1MB chunks
            
            while (position < size) {
                long remaining = size - position;
                long mapSize = Math.min(chunkSize, remaining);
                
                MappedByteBuffer buffer1 = channel1.map(
                    FileChannel.MapMode.READ_ONLY, position, mapSize);
                MappedByteBuffer buffer2 = channel2.map(
                    FileChannel.MapMode.READ_ONLY, position, mapSize);
                
                if (!buffer1.equals(buffer2)) {
                    return false;
                }
                
                position += mapSize;
            }
            
            return true;
        }
    }
    
    /**
     * Split a large file into smaller chunks.
     */
    public static List<Path> splitFile(Path sourceFile, long chunkSize) throws IOException {
        List<Path> chunks = new ArrayList<>();
        long fileSize = Files.size(sourceFile);
        
        try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
            long position = 0;
            int chunkNumber = 0;
            
            while (position < fileSize) {
                long remaining = fileSize - position;
                long currentChunkSize = Math.min(chunkSize, remaining);
                
                Path chunkFile = sourceFile.getParent().resolve(
                    sourceFile.getFileName() + ".chunk" + chunkNumber);
                
                try (FileChannel chunkChannel = FileChannel.open(chunkFile,
                        StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    
                    long transferred = sourceChannel.transferTo(
                        position, currentChunkSize, chunkChannel);
                    
                    position += transferred;
                    chunkNumber++;
                    chunks.add(chunkFile);
                }
            }
        }
        
        return chunks;
    }
    
    /**
     * Merge multiple files into one.
     */
    public static void mergeFiles(List<Path> sourceFiles, Path targetFile) throws IOException {
        try (FileChannel targetChannel = FileChannel.open(targetFile,
                StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            
            long position = 0;
            
            for (Path sourceFile : sourceFiles) {
                try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
                    long transferred = sourceChannel.transferTo(0, sourceChannel.size(), targetChannel);
                    position += transferred;
                }
            }
        }
    }
    
    /**
     * Create a sparse file (file with holes).
     */
    public static void createSparseFile(Path file, long size, Map<Long, byte[]> dataChunks) 
            throws IOException {
        
        try (RandomAccessFile raf = new RandomAccessFile(file.toFile(), "rw");
             FileChannel channel = raf.getChannel()) {
            
            // Set file size
            raf.setLength(size);
            
            // Write data chunks at specific positions
            for (Map.Entry<Long, byte[]> entry : dataChunks.entrySet()) {
                ByteBuffer buffer = ByteBuffer.wrap(entry.getValue());
                channel.write(buffer, entry.getKey());
            }
        }
    }
    
    /**
     * Watch for file modifications with debouncing.
     */
    public static void watchWithDebounce(Path directory, Duration delay, 
                                        Consumer<Path> handler) throws IOException {
        
        Map<Path, ScheduledFuture<?>> pendingEvents = new ConcurrentHashMap<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            
            while (true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException e) {
                    break;
                }
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.kind() == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }
                    
                    Path changed = directory.resolve((Path) event.context());
                    
                    // Cancel existing scheduled event
                    ScheduledFuture<?> existing = pendingEvents.get(changed);
                    if (existing != null) {
                        existing.cancel(false);
                    }
                    
                    // Schedule new event
                    ScheduledFuture<?> future = scheduler.schedule(() -> {
                        handler.accept(changed);
                        pendingEvents.remove(changed);
                    }, delay.toMillis(), TimeUnit.MILLISECONDS);
                    
                    pendingEvents.put(changed, future);
                }
                
                if (!key.reset()) {
                    break;
                }
            }
        } finally {
            scheduler.shutdown();
        }
    }
    
    /**
     * Get file attributes as a map.
     */
    public static Map<String, Object> getFileAttributes(Path file) throws IOException {
        Map<String, Object> attributes = new HashMap<>();
        
        // Basic attributes
        BasicFileAttributes basic = Files.readAttributes(file, BasicFileAttributes.class);
        attributes.put("size", basic.size());
        attributes.put("creationTime", basic.creationTime());
        attributes.put("lastModifiedTime", basic.lastModifiedTime());
        attributes.put("lastAccessTime", basic.lastAccessTime());
        attributes.put("isDirectory", basic.isDirectory());
        attributes.put("isRegularFile", basic.isRegularFile());
        attributes.put("isSymbolicLink", basic.isSymbolicLink());
        
        // Posix attributes (if supported)
        if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
            PosixFileAttributes posix = Files.readAttributes(file, PosixFileAttributes.class);
            attributes.put("owner", posix.owner().getName());
            attributes.put("group", posix.group().getName());
            attributes.put("permissions", PosixFilePermissions.toString(posix.permissions()));
        }
        
        return attributes;
    }
}