package com.example.nio2.reactive;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.Flow.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reactive file reader implementing the Flow API Publisher interface.
 */
public class ReactiveFileReader implements Publisher<String> {
    private static final Logger logger = LoggerFactory.getLogger(ReactiveFileReader.class);
    
    private final Path path;
    private final int bufferSize;
    
    public ReactiveFileReader(Path path) {
        this(path, 8192); // 8KB default buffer
    }
    
    public ReactiveFileReader(Path path, int bufferSize) {
        this.path = path;
        this.bufferSize = bufferSize;
        logger.info("Created reactive reader for: {} (buffer: {} bytes)", path, bufferSize);
    }
    
    @Override
    public void subscribe(Subscriber<? super String> subscriber) {
        if (subscriber == null) {
            throw new NullPointerException("Subscriber cannot be null");
        }
        
        try {
            FileSubscription subscription = new FileSubscription(subscriber, path, bufferSize);
            subscriber.onSubscribe(subscription);
        } catch (IOException e) {
            subscriber.onError(e);
        }
    }
    
    /**
     * File subscription handling backpressure and async reading.
     */
    private static class FileSubscription implements Subscription {
        private final Subscriber<? super String> subscriber;
        private final AsynchronousFileChannel channel;
        private final int bufferSize;
        private final AtomicLong requested = new AtomicLong(0);
        private final AtomicBoolean cancelled = new AtomicBoolean(false);
        private final AtomicLong position = new AtomicLong(0);
        private final AtomicBoolean reading = new AtomicBoolean(false);
        
        FileSubscription(Subscriber<? super String> subscriber, Path path, int bufferSize) 
                throws IOException {
            this.subscriber = subscriber;
            this.bufferSize = bufferSize;
            this.channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            logger.debug("Subscription created for: {}", path);
        }
        
        @Override
        public void request(long n) {
            if (n <= 0) {
                subscriber.onError(new IllegalArgumentException(
                    "Requested amount must be positive: " + n));
                return;
            }
            
            long previousRequested = requested.getAndAdd(n);
            logger.debug("Request: {} (total: {})", n, previousRequested + n);
            
            if (previousRequested == 0) {
                // Start reading if this is the first request
                readNext();
            }
        }
        
        @Override
        public void cancel() {
            if (cancelled.compareAndSet(false, true)) {
                logger.debug("Subscription cancelled");
                closeChannel();
            }
        }
        
        private void readNext() {
            if (cancelled.get() || requested.get() <= 0) {
                return;
            }
            
            if (!reading.compareAndSet(false, true)) {
                // Already reading
                return;
            }
            
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            long currentPosition = position.get();
            
            channel.read(buffer, currentPosition, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer bytesRead, ByteBuffer attachment) {
                    try {
                        if (bytesRead == -1) {
                            // End of file
                            subscriber.onComplete();
                            closeChannel();
                            return;
                        }
                        
                        // Update position
                        position.addAndGet(bytesRead);
                        
                        // Convert to string and emit
                        attachment.flip();
                        String chunk = StandardCharsets.UTF_8.decode(attachment).toString();
                        
                        if (!cancelled.get()) {
                            subscriber.onNext(chunk);
                            
                            // Decrement requested count
                            long remaining = requested.decrementAndGet();
                            
                            // Continue reading if more requested
                            if (remaining > 0 && !cancelled.get()) {
                                reading.set(false);
                                readNext();
                            } else {
                                reading.set(false);
                            }
                        }
                    } catch (Exception e) {
                        subscriber.onError(e);
                        closeChannel();
                    }
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    subscriber.onError(exc);
                    closeChannel();
                }
            });
        }
        
        private void closeChannel() {
            try {
                if (channel != null && channel.isOpen()) {
                    channel.close();
                    logger.debug("Channel closed");
                }
            } catch (IOException e) {
                logger.error("Error closing channel", e);
            }
        }
    }
}