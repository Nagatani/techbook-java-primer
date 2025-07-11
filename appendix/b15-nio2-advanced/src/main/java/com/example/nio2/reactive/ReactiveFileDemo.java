package com.example.nio2.reactive;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Demonstration of reactive file processing with backpressure.
 */
public class ReactiveFileDemo {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java ReactiveFileDemo <file-path> [buffer-size]");
            System.exit(1);
        }
        
        Path filePath = Paths.get(args[0]);
        int bufferSize = args.length > 1 ? Integer.parseInt(args[1]) : 4096;
        
        if (!Files.exists(filePath)) {
            System.err.println("File not found: " + filePath);
            System.exit(1);
        }
        
        try {
            System.out.println("=== Reactive File Processing Demo ===");
            System.out.printf("File: %s%n", filePath.toAbsolutePath());
            System.out.printf("Size: %s%n", formatSize(Files.size(filePath)));
            System.out.printf("Buffer size: %d bytes%n%n", bufferSize);
            
            // Demo 1: Simple reactive reading
            demonstrateSimpleReading(filePath, bufferSize);
            
            // Demo 2: Backpressure handling
            demonstrateBackpressure(filePath, bufferSize);
            
            // Demo 3: Line processing
            demonstrateLineProcessing(filePath);
            
            // Demo 4: Transform and aggregate
            demonstrateTransformAndAggregate(filePath);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void demonstrateSimpleReading(Path filePath, int bufferSize) 
            throws InterruptedException {
        System.out.println("--- Demo 1: Simple Reactive Reading ---");
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicLong totalBytes = new AtomicLong(0);
        AtomicInteger chunkCount = new AtomicInteger(0);
        
        ReactiveFileReader reader = new ReactiveFileReader(filePath, bufferSize);
        
        reader.subscribe(new Flow.Subscriber<String>() {
            private Flow.Subscription subscription;
            
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                System.out.println("Subscribed to file reader");
                subscription.request(Long.MAX_VALUE); // Request all data
            }
            
            @Override
            public void onNext(String chunk) {
                int bytes = chunk.getBytes().length;
                totalBytes.addAndGet(bytes);
                int chunks = chunkCount.incrementAndGet();
                
                if (chunks % 100 == 0) {
                    System.out.printf("Progress: %d chunks, %s total%n", 
                        chunks, formatSize(totalBytes.get()));
                }
            }
            
            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error: " + throwable.getMessage());
                latch.countDown();
            }
            
            @Override
            public void onComplete() {
                System.out.printf("Complete: %d chunks, %s total%n%n", 
                    chunkCount.get(), formatSize(totalBytes.get()));
                latch.countDown();
            }
        });
        
        latch.await(10, TimeUnit.SECONDS);
    }
    
    private static void demonstrateBackpressure(Path filePath, int bufferSize) 
            throws InterruptedException {
        System.out.println("--- Demo 2: Backpressure Handling ---");
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger processedChunks = new AtomicInteger(0);
        
        ReactiveFileReader reader = new ReactiveFileReader(filePath, bufferSize);
        
        reader.subscribe(new Flow.Subscriber<String>() {
            private Flow.Subscription subscription;
            private final int maxBuffer = 5;
            private final AtomicInteger inProgress = new AtomicInteger(0);
            
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                System.out.println("Subscribed with backpressure control");
                subscription.request(maxBuffer); // Request limited amount
            }
            
            @Override
            public void onNext(String chunk) {
                inProgress.incrementAndGet();
                
                // Simulate slow processing
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(50); // Simulate work
                        int processed = processedChunks.incrementAndGet();
                        
                        if (processed % 10 == 0) {
                            System.out.printf("Processed %d chunks (buffer: %d/%d)%n", 
                                processed, inProgress.get(), maxBuffer);
                        }
                        
                        // Request more when buffer has space
                        if (inProgress.decrementAndGet() < maxBuffer / 2) {
                            subscription.request(1);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            
            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error: " + throwable.getMessage());
                latch.countDown();
            }
            
            @Override
            public void onComplete() {
                // Wait for processing to complete
                while (inProgress.get() > 0) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                
                System.out.printf("Complete: Processed %d chunks%n%n", processedChunks.get());
                latch.countDown();
            }
        });
        
        latch.await(30, TimeUnit.SECONDS);
    }
    
    private static void demonstrateLineProcessing(Path filePath) 
            throws InterruptedException {
        System.out.println("--- Demo 3: Line-by-Line Processing ---");
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger lineCount = new AtomicInteger(0);
        AtomicLong totalLength = new AtomicLong(0);
        
        // Create a line processor
        ReactiveFileProcessor.TransformProcessor<String, String> lineProcessor = 
            new ReactiveFileProcessor.TransformProcessor<>(line -> {
                lineCount.incrementAndGet();
                totalLength.addAndGet(line.length());
                return line.toUpperCase(); // Transform to uppercase
            });
        
        // Create final subscriber
        lineProcessor.subscribe(new Flow.Subscriber<String>() {
            private Flow.Subscription subscription;
            private int processedLines = 0;
            
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                subscription.request(10); // Process 10 lines at a time
            }
            
            @Override
            public void onNext(String line) {
                processedLines++;
                
                // Show first few lines
                if (processedLines <= 5) {
                    System.out.println("Line " + processedLines + ": " + 
                        line.substring(0, Math.min(50, line.length())) + "...");
                }
                
                // Request more
                if (processedLines % 10 == 0) {
                    subscription.request(10);
                }
            }
            
            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error: " + throwable.getMessage());
                latch.countDown();
            }
            
            @Override
            public void onComplete() {
                System.out.printf("Processed %d lines, average length: %.1f%n%n", 
                    lineCount.get(), 
                    lineCount.get() > 0 ? (double) totalLength.get() / lineCount.get() : 0);
                latch.countDown();
            }
        });
        
        // Start processing
        ReactiveFileReader reader = new ReactiveFileReader(filePath);
        Flow.Subscriber<String> lineBuffering = 
            ReactiveFileProcessor.createLineSubscriber(lineProcessor);
        reader.subscribe(lineBuffering);
        
        latch.await(30, TimeUnit.SECONDS);
    }
    
    private static void demonstrateTransformAndAggregate(Path filePath) 
            throws InterruptedException, IOException {
        System.out.println("--- Demo 4: Transform and Aggregate ---");
        
        // Create a test file if needed
        if (Files.size(filePath) > 1_000_000) {
            System.out.println("File too large for aggregation demo, skipping...\n");
            return;
        }
        
        CountDownLatch latch = new CountDownLatch(1);
        StringBuilder aggregated = new StringBuilder();
        
        // Word count processor
        ReactiveFileProcessor.TransformProcessor<String, Integer> wordCounter = 
            new ReactiveFileProcessor.TransformProcessor<>(chunk -> {
                return chunk.split("\\s+").length;
            });
        
        wordCounter.subscribe(new Flow.Subscriber<Integer>() {
            private Flow.Subscription subscription;
            private int totalWords = 0;
            private int chunkCount = 0;
            
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                System.out.println("Starting word count...");
                subscription.request(Long.MAX_VALUE);
            }
            
            @Override
            public void onNext(Integer wordCount) {
                totalWords += wordCount;
                chunkCount++;
                
                if (chunkCount % 10 == 0) {
                    System.out.printf("Chunks processed: %d, words so far: %d%n", 
                        chunkCount, totalWords);
                }
            }
            
            @Override
            public void onError(Throwable throwable) {
                System.err.println("Error: " + throwable.getMessage());
                latch.countDown();
            }
            
            @Override
            public void onComplete() {
                System.out.printf("Total word count: %d (from %d chunks)%n%n", 
                    totalWords, chunkCount);
                latch.countDown();
            }
        });
        
        // Start processing
        ReactiveFileReader reader = new ReactiveFileReader(filePath, 1024);
        reader.subscribe(wordCounter);
        
        latch.await(30, TimeUnit.SECONDS);
    }
    
    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}