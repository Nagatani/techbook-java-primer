package com.example.nio2.reactive;

import java.nio.file.Path;
import java.util.concurrent.Flow.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reactive file processor with transformation and backpressure support.
 */
public class ReactiveFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ReactiveFileProcessor.class);
    
    /**
     * Process a file reactively with the given processor function.
     */
    public static void processFile(Path path, Function<String, String> processor, 
                                  Subscriber<String> subscriber) {
        ReactiveFileReader reader = new ReactiveFileReader(path);
        TransformProcessor<String, String> transformer = new TransformProcessor<>(processor);
        
        // Connect reader -> transformer -> subscriber
        reader.subscribe(transformer);
        transformer.subscribe(subscriber);
    }
    
    /**
     * Create a line-based subscriber that processes individual lines.
     */
    public static Subscriber<String> createLineSubscriber(Processor<String, String> lineProcessor) {
        return new LineBufferingSubscriber(lineProcessor);
    }
    
    /**
     * Transformation processor implementing backpressure.
     */
    public static class TransformProcessor<T, R> implements Processor<T, R> {
        private final Function<T, R> transformer;
        private Subscription upstream;
        private Subscriber<? super R> downstream;
        private final AtomicLong requested = new AtomicLong(0);
        private final AtomicInteger inFlight = new AtomicInteger(0);
        
        public TransformProcessor(Function<T, R> transformer) {
            this.transformer = transformer;
        }
        
        @Override
        public void subscribe(Subscriber<? super R> subscriber) {
            this.downstream = subscriber;
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    requested.addAndGet(n);
                    if (upstream != null) {
                        upstream.request(n);
                    }
                }
                
                @Override
                public void cancel() {
                    if (upstream != null) {
                        upstream.cancel();
                    }
                }
            });
        }
        
        @Override
        public void onSubscribe(Subscription subscription) {
            this.upstream = subscription;
            long initialRequest = requested.get();
            if (initialRequest > 0) {
                subscription.request(initialRequest);
            }
        }
        
        @Override
        public void onNext(T item) {
            if (downstream != null && requested.get() > 0) {
                inFlight.incrementAndGet();
                try {
                    R transformed = transformer.apply(item);
                    downstream.onNext(transformed);
                    requested.decrementAndGet();
                } catch (Exception e) {
                    onError(e);
                } finally {
                    inFlight.decrementAndGet();
                }
            }
        }
        
        @Override
        public void onError(Throwable throwable) {
            if (downstream != null) {
                downstream.onError(throwable);
            }
        }
        
        @Override
        public void onComplete() {
            // Wait for in-flight items to complete
            while (inFlight.get() > 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            if (downstream != null) {
                downstream.onComplete();
            }
        }
    }
    
    /**
     * Subscriber that buffers chunks and emits complete lines.
     */
    private static class LineBufferingSubscriber implements Subscriber<String> {
        private final Processor<String, String> lineProcessor;
        private final StringBuilder buffer = new StringBuilder();
        private Subscription subscription;
        
        LineBufferingSubscriber(Processor<String, String> lineProcessor) {
            this.lineProcessor = lineProcessor;
        }
        
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            subscription.request(1); // Start with one chunk
        }
        
        @Override
        public void onNext(String chunk) {
            buffer.append(chunk);
            
            // Extract complete lines
            String content = buffer.toString();
            int lastNewline = content.lastIndexOf('\n');
            
            if (lastNewline >= 0) {
                String completeLines = content.substring(0, lastNewline);
                buffer.setLength(0);
                buffer.append(content.substring(lastNewline + 1));
                
                // Process each line
                for (String line : completeLines.split("\n")) {
                    lineProcessor.onNext(line);
                }
            }
            
            // Request next chunk
            subscription.request(1);
        }
        
        @Override
        public void onError(Throwable throwable) {
            lineProcessor.onError(throwable);
        }
        
        @Override
        public void onComplete() {
            // Process any remaining content
            if (buffer.length() > 0) {
                lineProcessor.onNext(buffer.toString());
            }
            lineProcessor.onComplete();
        }
    }
    
    /**
     * Simple logging subscriber for demonstration.
     */
    public static class LoggingSubscriber<T> implements Subscriber<T> {
        private final String name;
        private Subscription subscription;
        private final AtomicLong itemCount = new AtomicLong(0);
        private final int requestSize;
        
        public LoggingSubscriber(String name) {
            this(name, 1);
        }
        
        public LoggingSubscriber(String name, int requestSize) {
            this.name = name;
            this.requestSize = requestSize;
        }
        
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            logger.info("[{}] Subscribed", name);
            subscription.request(requestSize);
        }
        
        @Override
        public void onNext(T item) {
            long count = itemCount.incrementAndGet();
            logger.info("[{}] Item {}: {}", name, count, 
                item.toString().substring(0, Math.min(50, item.toString().length())));
            
            // Request more items
            subscription.request(requestSize);
        }
        
        @Override
        public void onError(Throwable throwable) {
            logger.error("[{}] Error", name, throwable);
        }
        
        @Override
        public void onComplete() {
            logger.info("[{}] Completed. Total items: {}", name, itemCount.get());
        }
    }
}