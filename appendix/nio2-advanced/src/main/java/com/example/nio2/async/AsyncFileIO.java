package com.example.nio2.async;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Asynchronous file I/O operations using AsynchronousFileChannel.
 */
public class AsyncFileIO {
    private static final Logger logger = LoggerFactory.getLogger(AsyncFileIO.class);
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    
    /**
     * Read a file asynchronously and return its contents as a CompletableFuture.
     */
    public CompletableFuture<String> readFileAsync(Path path) {
        return readFileAsync(path, DEFAULT_BUFFER_SIZE);
    }
    
    /**
     * Read a file asynchronously with specified buffer size.
     */
    public CompletableFuture<String> readFileAsync(Path path, int bufferSize) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                path, StandardOpenOption.READ
            );
            
            long fileSize = Files.size(path);
            ByteBuffer buffer = ByteBuffer.allocate((int) Math.min(fileSize, Integer.MAX_VALUE));
            
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                private long position = 0;
                
                @Override
                public void completed(Integer bytesRead, ByteBuffer attachment) {
                    if (bytesRead == -1) {
                        // End of file
                        attachment.flip();
                        String content = StandardCharsets.UTF_8.decode(attachment).toString();
                        future.complete(content);
                        closeQuietly(channel);
                        return;
                    }
                    
                    position += bytesRead;
                    
                    if (attachment.hasRemaining() && position < fileSize) {
                        // Continue reading
                        channel.read(attachment, position, attachment, this);
                    } else {
                        // All data read
                        attachment.flip();
                        String content = StandardCharsets.UTF_8.decode(attachment).toString();
                        future.complete(content);
                        closeQuietly(channel);
                    }
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                    closeQuietly(channel);
                }
            });
            
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Write content to a file asynchronously.
     */
    public CompletableFuture<Void> writeFileAsync(Path path, String content) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                path,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
            
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(content);
            AtomicLong position = new AtomicLong(0);
            
            channel.write(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer bytesWritten, ByteBuffer attachment) {
                    position.addAndGet(bytesWritten);
                    
                    if (attachment.hasRemaining()) {
                        // Write remaining data
                        channel.write(attachment, position.get(), attachment, this);
                    } else {
                        // All data written
                        future.complete(null);
                        closeQuietly(channel);
                    }
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                    closeQuietly(channel);
                }
            });
            
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Copy a file asynchronously with progress tracking.
     */
    public CompletableFuture<Long> copyFileAsync(Path source, Path target, 
                                                  ProgressListener progressListener) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        
        try {
            AsynchronousFileChannel sourceChannel = AsynchronousFileChannel.open(
                source, StandardOpenOption.READ
            );
            
            AsynchronousFileChannel targetChannel = AsynchronousFileChannel.open(
                target,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
            
            long fileSize = Files.size(source);
            int bufferSize = (int) Math.min(fileSize, 1024 * 1024); // Max 1MB buffer
            ByteBuffer buffer = ByteBuffer.allocateDirect(bufferSize);
            
            AtomicLong totalBytesTransferred = new AtomicLong(0);
            AtomicLong readPosition = new AtomicLong(0);
            AtomicLong writePosition = new AtomicLong(0);
            
            // Start the copy process
            CompletionHandler<Integer, Void> handler = new CompletionHandler<Integer, Void>() {
                @Override
                public void completed(Integer bytesRead, Void attachment) {
                    if (bytesRead == -1) {
                        // Copy complete
                        future.complete(totalBytesTransferred.get());
                        closeQuietly(sourceChannel);
                        closeQuietly(targetChannel);
                        if (progressListener != null) {
                            progressListener.onComplete(totalBytesTransferred.get());
                        }
                        return;
                    }
                    
                    // Prepare buffer for writing
                    buffer.flip();
                    
                    // Write to target
                    targetChannel.write(buffer, writePosition.get(), null,
                        new CompletionHandler<Integer, Void>() {
                            @Override
                            public void completed(Integer bytesWritten, Void attachment) {
                                writePosition.addAndGet(bytesWritten);
                                totalBytesTransferred.addAndGet(bytesWritten);
                                
                                if (progressListener != null) {
                                    double progress = (double) totalBytesTransferred.get() / fileSize;
                                    progressListener.onProgress(totalBytesTransferred.get(), fileSize, progress);
                                }
                                
                                if (buffer.hasRemaining()) {
                                    // Write remaining data
                                    targetChannel.write(buffer, writePosition.get(), null, this);
                                } else {
                                    // Prepare for next read
                                    buffer.clear();
                                    readPosition.addAndGet(bytesRead);
                                    sourceChannel.read(buffer, readPosition.get(), null, handler);
                                }
                            }
                            
                            @Override
                            public void failed(Throwable exc, Void attachment) {
                                future.completeExceptionally(exc);
                                closeQuietly(sourceChannel);
                                closeQuietly(targetChannel);
                            }
                        });
                }
                
                @Override
                public void failed(Throwable exc, Void attachment) {
                    future.completeExceptionally(exc);
                    closeQuietly(sourceChannel);
                    closeQuietly(targetChannel);
                }
            };
            
            // Start reading
            sourceChannel.read(buffer, 0, null, handler);
            
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Append content to a file asynchronously.
     */
    public CompletableFuture<Void> appendFileAsync(Path path, String content) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                path,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
            
            long position = Files.size(path);
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(content);
            
            channel.write(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()) {
                        channel.write(attachment, position + result, attachment, this);
                    } else {
                        future.complete(null);
                        closeQuietly(channel);
                    }
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                    closeQuietly(channel);
                }
            });
            
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    private void closeQuietly(AsynchronousFileChannel channel) {
        try {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
        } catch (IOException e) {
            logger.warn("Error closing channel", e);
        }
    }
    
    /**
     * Interface for progress tracking.
     */
    public interface ProgressListener {
        void onProgress(long bytesTransferred, long totalBytes, double percentage);
        void onComplete(long totalBytes);
    }
}