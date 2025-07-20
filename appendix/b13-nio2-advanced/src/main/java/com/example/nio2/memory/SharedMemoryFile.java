package com.example.nio2.memory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared memory implementation using memory-mapped files for inter-process communication.
 */
public class SharedMemoryFile implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(SharedMemoryFile.class);
    
    private final Path filePath;
    private final int size;
    private final RandomAccessFile file;
    private final FileChannel channel;
    private final MappedByteBuffer buffer;
    
    // Header structure (first 16 bytes)
    private static final int HEADER_SIZE = 16;
    private static final int VERSION_OFFSET = 0;
    private static final int WRITE_COUNT_OFFSET = 4;
    private static final int LAST_WRITE_OFFSET = 8;
    
    public SharedMemoryFile(Path filePath, int size) throws IOException {
        this.filePath = filePath;
        this.size = size + HEADER_SIZE;
        
        // Create or open the file
        boolean exists = Files.exists(filePath);
        this.file = new RandomAccessFile(filePath.toFile(), "rw");
        this.channel = file.getChannel();
        
        // Set file size if new
        if (!exists || file.length() < this.size) {
            file.setLength(this.size);
            logger.info("Created shared memory file: {} (size: {} bytes)", filePath, this.size);
        }
        
        // Map the file
        this.buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, this.size);
        
        // Initialize header if new file
        if (!exists) {
            initializeHeader();
        }
        
        logger.info("Opened shared memory: {} (version: {})", filePath, getVersion());
    }
    
    private void initializeHeader() {
        buffer.putInt(VERSION_OFFSET, 1); // Version
        buffer.putInt(WRITE_COUNT_OFFSET, 0); // Write count
        buffer.putLong(LAST_WRITE_OFFSET, System.currentTimeMillis()); // Last write time
        buffer.force();
    }
    
    public int getVersion() {
        return buffer.getInt(VERSION_OFFSET);
    }
    
    public int getWriteCount() {
        return buffer.getInt(WRITE_COUNT_OFFSET);
    }
    
    public long getLastWriteTime() {
        return buffer.getLong(LAST_WRITE_OFFSET);
    }
    
    /**
     * Write data to shared memory at specified offset.
     */
    public void writeData(int offset, byte[] data) throws IOException {
        if (offset < 0 || offset + data.length > size - HEADER_SIZE) {
            throw new IllegalArgumentException("Invalid offset or data too large");
        }
        
        try (FileLock lock = channel.lock(0, HEADER_SIZE, false)) {
            // Update header
            int writeCount = buffer.getInt(WRITE_COUNT_OFFSET);
            buffer.putInt(WRITE_COUNT_OFFSET, writeCount + 1);
            buffer.putLong(LAST_WRITE_OFFSET, System.currentTimeMillis());
            
            // Write data
            buffer.position(HEADER_SIZE + offset);
            buffer.put(data);
            
            // Force to disk
            buffer.force();
            
            logger.debug("Wrote {} bytes at offset {}", data.length, offset);
        }
    }
    
    /**
     * Read data from shared memory at specified offset.
     */
    public byte[] readData(int offset, int length) throws IOException {
        if (offset < 0 || offset + length > size - HEADER_SIZE) {
            throw new IllegalArgumentException("Invalid offset or length");
        }
        
        try (FileLock lock = channel.lock(0, size, true)) {
            byte[] data = new byte[length];
            buffer.position(HEADER_SIZE + offset);
            buffer.get(data);
            
            logger.debug("Read {} bytes from offset {}", length, offset);
            return data;
        }
    }
    
    /**
     * Atomic compare-and-swap operation.
     */
    public boolean compareAndSwapInt(int offset, int expected, int update) throws IOException {
        if (offset < 0 || offset + 4 > size - HEADER_SIZE) {
            throw new IllegalArgumentException("Invalid offset");
        }
        
        try (FileLock lock = channel.lock(HEADER_SIZE + offset, 4, false)) {
            int current = buffer.getInt(HEADER_SIZE + offset);
            if (current == expected) {
                buffer.putInt(HEADER_SIZE + offset, update);
                buffer.force();
                return true;
            }
            return false;
        }
    }
    
    /**
     * Wait for data to change at specified offset.
     */
    public void waitForChange(int offset, byte[] currentValue, long timeoutMs) 
            throws IOException, InterruptedException {
        long startTime = System.currentTimeMillis();
        byte[] readValue = new byte[currentValue.length];
        
        while (System.currentTimeMillis() - startTime < timeoutMs) {
            readValue = readData(offset, currentValue.length);
            
            boolean changed = false;
            for (int i = 0; i < currentValue.length; i++) {
                if (readValue[i] != currentValue[i]) {
                    changed = true;
                    break;
                }
            }
            
            if (changed) {
                return;
            }
            
            Thread.sleep(10); // Small delay to prevent busy waiting
        }
        
        throw new InterruptedException("Timeout waiting for change");
    }
    
    @Override
    public void close() throws IOException {
        // Force any pending changes
        if (buffer != null) {
            buffer.force();
        }
        
        if (channel != null) {
            channel.close();
        }
        
        if (file != null) {
            file.close();
        }
        
        logger.info("Closed shared memory file: {}", filePath);
    }
    
    /**
     * Simple message queue implementation using shared memory.
     */
    public static class SharedMemoryQueue {
        private final SharedMemoryFile sharedMemory;
        private final int messageSize;
        private final int queueCapacity;
        private static final int METADATA_SIZE = 12; // head, tail, count
        
        public SharedMemoryQueue(Path path, int messageSize, int queueCapacity) 
                throws IOException {
            this.messageSize = messageSize;
            this.queueCapacity = queueCapacity;
            
            int totalSize = METADATA_SIZE + (messageSize * queueCapacity);
            this.sharedMemory = new SharedMemoryFile(path, totalSize);
            
            // Initialize if new
            if (sharedMemory.getWriteCount() == 0) {
                initializeQueue();
            }
        }
        
        private void initializeQueue() throws IOException {
            byte[] metadata = new byte[METADATA_SIZE];
            ByteBuffer metaBuffer = ByteBuffer.wrap(metadata);
            metaBuffer.putInt(0); // head
            metaBuffer.putInt(0); // tail
            metaBuffer.putInt(0); // count
            sharedMemory.writeData(0, metadata);
        }
        
        public boolean enqueue(byte[] message) throws IOException {
            if (message.length > messageSize) {
                throw new IllegalArgumentException("Message too large");
            }
            
            byte[] metadata = sharedMemory.readData(0, METADATA_SIZE);
            ByteBuffer metaBuffer = ByteBuffer.wrap(metadata);
            int head = metaBuffer.getInt();
            int tail = metaBuffer.getInt();
            int count = metaBuffer.getInt();
            
            if (count >= queueCapacity) {
                return false; // Queue full
            }
            
            // Write message
            int offset = METADATA_SIZE + (tail * messageSize);
            byte[] paddedMessage = new byte[messageSize];
            System.arraycopy(message, 0, paddedMessage, 0, message.length);
            sharedMemory.writeData(offset, paddedMessage);
            
            // Update metadata
            tail = (tail + 1) % queueCapacity;
            count++;
            metaBuffer.rewind();
            metaBuffer.putInt(head);
            metaBuffer.putInt(tail);
            metaBuffer.putInt(count);
            sharedMemory.writeData(0, metadata);
            
            return true;
        }
        
        public byte[] dequeue() throws IOException {
            byte[] metadata = sharedMemory.readData(0, METADATA_SIZE);
            ByteBuffer metaBuffer = ByteBuffer.wrap(metadata);
            int head = metaBuffer.getInt();
            int tail = metaBuffer.getInt();
            int count = metaBuffer.getInt();
            
            if (count == 0) {
                return null; // Queue empty
            }
            
            // Read message
            int offset = METADATA_SIZE + (head * messageSize);
            byte[] message = sharedMemory.readData(offset, messageSize);
            
            // Update metadata
            head = (head + 1) % queueCapacity;
            count--;
            metaBuffer.rewind();
            metaBuffer.putInt(head);
            metaBuffer.putInt(tail);
            metaBuffer.putInt(count);
            sharedMemory.writeData(0, metadata);
            
            // Trim padding
            int actualLength = 0;
            for (int i = message.length - 1; i >= 0; i--) {
                if (message[i] != 0) {
                    actualLength = i + 1;
                    break;
                }
            }
            
            byte[] trimmed = new byte[actualLength];
            System.arraycopy(message, 0, trimmed, 0, actualLength);
            return trimmed;
        }
        
        public void close() throws IOException {
            sharedMemory.close();
        }
    }
}