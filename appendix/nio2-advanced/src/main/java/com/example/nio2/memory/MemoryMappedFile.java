package com.example.nio2.memory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for working with memory-mapped files.
 */
public class MemoryMappedFile implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(MemoryMappedFile.class);
    
    private final Path path;
    private final RandomAccessFile file;
    private final FileChannel channel;
    private final FileChannel.MapMode mode;
    private MappedByteBuffer buffer;
    
    public MemoryMappedFile(Path path, FileChannel.MapMode mode) throws IOException {
        this.path = path;
        this.mode = mode;
        
        String fileMode = (mode == FileChannel.MapMode.READ_ONLY) ? "r" : "rw";
        this.file = new RandomAccessFile(path.toFile(), fileMode);
        this.channel = file.getChannel();
        
        logger.info("Opened memory-mapped file: {} (mode: {})", path, mode);
    }
    
    /**
     * Map the entire file into memory.
     */
    public MappedByteBuffer mapEntireFile() throws IOException {
        long size = channel.size();
        this.buffer = channel.map(mode, 0, size);
        logger.info("Mapped entire file: {} bytes", size);
        return buffer;
    }
    
    /**
     * Map a portion of the file into memory.
     */
    public MappedByteBuffer mapRegion(long position, long size) throws IOException {
        this.buffer = channel.map(mode, position, size);
        logger.info("Mapped region: position={}, size={}", position, size);
        return buffer;
    }
    
    /**
     * Read a string from the mapped buffer at the specified position.
     */
    public String readString(int position, int length) {
        if (buffer == null) {
            throw new IllegalStateException("File not mapped");
        }
        
        byte[] bytes = new byte[length];
        buffer.position(position);
        buffer.get(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Write a string to the mapped buffer at the specified position.
     */
    public void writeString(int position, String data) {
        if (buffer == null) {
            throw new IllegalStateException("File not mapped");
        }
        
        if (mode == FileChannel.MapMode.READ_ONLY) {
            throw new IllegalStateException("Cannot write to read-only mapping");
        }
        
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        buffer.position(position);
        buffer.put(bytes);
    }
    
    /**
     * Force changes to be written to disk.
     */
    public void force() {
        if (buffer != null && mode != FileChannel.MapMode.READ_ONLY) {
            buffer.force();
            logger.debug("Forced buffer to disk");
        }
    }
    
    /**
     * Get the current buffer for direct manipulation.
     */
    public MappedByteBuffer getBuffer() {
        return buffer;
    }
    
    /**
     * Clean up the mapped buffer to release memory immediately.
     */
    public void cleanBuffer() {
        if (buffer != null) {
            try {
                // Use reflection to access the cleaner (not ideal but effective)
                Method cleanerMethod = buffer.getClass().getMethod("cleaner");
                cleanerMethod.setAccessible(true);
                Object cleaner = cleanerMethod.invoke(buffer);
                if (cleaner != null) {
                    Method cleanMethod = cleaner.getClass().getMethod("clean");
                    cleanMethod.setAccessible(true);
                    cleanMethod.invoke(cleaner);
                    logger.debug("Buffer cleaned");
                }
            } catch (Exception e) {
                logger.warn("Failed to clean buffer immediately, relying on GC", e);
            }
            buffer = null;
        }
    }
    
    @Override
    public void close() throws IOException {
        cleanBuffer();
        
        if (channel != null) {
            channel.close();
        }
        
        if (file != null) {
            file.close();
        }
        
        logger.info("Closed memory-mapped file: {}", path);
    }
    
    /**
     * Utility method to count lines in a large file efficiently.
     */
    public static long countLines(Path path) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            long fileSize = channel.size();
            long position = 0;
            long lineCount = 0;
            int chunkSize = 1024 * 1024 * 100; // 100MB chunks
            
            while (position < fileSize) {
                long remainingSize = fileSize - position;
                long mappingSize = Math.min(chunkSize, remainingSize);
                
                MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    position,
                    mappingSize
                );
                
                while (buffer.hasRemaining()) {
                    if (buffer.get() == '\n') {
                        lineCount++;
                    }
                }
                
                position += mappingSize;
                
                // Clean up the buffer
                cleanMappedByteBuffer(buffer);
            }
            
            return lineCount;
        }
    }
    
    /**
     * Search for a pattern in a large file using memory mapping.
     */
    public static long findPattern(Path path, byte[] pattern) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            long fileSize = channel.size();
            long position = 0;
            int chunkSize = 1024 * 1024 * 100; // 100MB chunks
            int overlap = pattern.length - 1; // Overlap to handle patterns across chunks
            
            while (position < fileSize) {
                long mappingStart = Math.max(0, position - overlap);
                long remainingSize = fileSize - mappingStart;
                long mappingSize = Math.min(chunkSize + overlap, remainingSize);
                
                MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    mappingStart,
                    mappingSize
                );
                
                // Skip the overlap region if not at start
                if (position > 0) {
                    buffer.position(overlap);
                }
                
                long foundAt = searchInBuffer(buffer, pattern, mappingStart);
                if (foundAt >= 0) {
                    cleanMappedByteBuffer(buffer);
                    return foundAt;
                }
                
                position += chunkSize;
                cleanMappedByteBuffer(buffer);
            }
            
            return -1; // Pattern not found
        }
    }
    
    private static long searchInBuffer(ByteBuffer buffer, byte[] pattern, long bufferOffset) {
        int patternLength = pattern.length;
        int limit = buffer.limit() - patternLength + 1;
        
        for (int i = buffer.position(); i < limit; i++) {
            boolean found = true;
            for (int j = 0; j < patternLength; j++) {
                if (buffer.get(i + j) != pattern[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return bufferOffset + i;
            }
        }
        
        return -1;
    }
    
    private static void cleanMappedByteBuffer(MappedByteBuffer buffer) {
        try {
            Method cleanerMethod = buffer.getClass().getMethod("cleaner");
            cleanerMethod.setAccessible(true);
            Object cleaner = cleanerMethod.invoke(buffer);
            if (cleaner != null) {
                Method cleanMethod = cleaner.getClass().getMethod("clean");
                cleanMethod.setAccessible(true);
                cleanMethod.invoke(cleaner);
            }
        } catch (Exception e) {
            // Fallback to garbage collection
        }
    }
}