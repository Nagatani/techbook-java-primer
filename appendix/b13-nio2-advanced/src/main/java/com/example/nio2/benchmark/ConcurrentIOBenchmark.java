package com.example.nio2.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Benchmarks comparing concurrent file processing approaches.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class ConcurrentIOBenchmark {
    
    @Param({"10", "100", "1000"})
    private int fileCount;
    
    @Param({"1024", "10240", "102400"}) // 1KB, 10KB, 100KB
    private int fileSize;
    
    private List<Path> tempFiles;
    private ExecutorService executor;
    
    @Setup
    public void setup() throws IOException {
        // Create temporary files
        tempFiles = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < fileCount; i++) {
            Path tempFile = Files.createTempFile("concurrent", ".dat");
            byte[] data = new byte[fileSize];
            random.nextBytes(data);
            Files.write(tempFile, data);
            tempFiles.add(tempFile);
        }
        
        // Create executor
        executor = ForkJoinPool.commonPool();
    }
    
    @TearDown
    public void tearDown() throws IOException {
        // Delete temporary files
        for (Path file : tempFiles) {
            Files.deleteIfExists(file);
        }
        
        // Shutdown executor
        if (executor != null) {
            executor.shutdown();
        }
    }
    
    @Benchmark
    public long sequentialProcessing() throws IOException {
        long totalBytes = 0;
        
        for (Path file : tempFiles) {
            byte[] content = Files.readAllBytes(file);
            totalBytes += processContent(content);
        }
        
        return totalBytes;
    }
    
    @Benchmark
    public long parallelStreamProcessing() {
        return tempFiles.parallelStream()
            .mapToLong(file -> {
                try {
                    byte[] content = Files.readAllBytes(file);
                    return processContent(content);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .sum();
    }
    
    @Benchmark
    public long completableFutureProcessing() throws Exception {
        List<CompletableFuture<Long>> futures = tempFiles.stream()
            .map(file -> CompletableFuture.supplyAsync(() -> {
                try {
                    byte[] content = Files.readAllBytes(file);
                    return processContent(content);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }, executor))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .mapToLong(CompletableFuture::join)
                .sum())
            .get();
    }
    
    @Benchmark
    public long asyncFileChannelProcessing() throws Exception {
        List<CompletableFuture<Long>> futures = new ArrayList<>();
        
        for (Path file : tempFiles) {
            CompletableFuture<Long> future = new CompletableFuture<>();
            
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                file, StandardOpenOption.READ, executor
            );
            
            ByteBuffer buffer = ByteBuffer.allocate(fileSize);
            
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    attachment.flip();
                    byte[] content = new byte[attachment.remaining()];
                    attachment.get(content);
                    future.complete(processContent(content));
                    closeQuietly(channel);
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                    closeQuietly(channel);
                }
            });
            
            futures.add(future);
        }
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .mapToLong(CompletableFuture::join)
                .sum())
            .get();
    }
    
    private long processContent(byte[] content) {
        // Simulate some processing
        long checksum = 0;
        for (byte b : content) {
            checksum += b & 0xFF;
        }
        return checksum;
    }
    
    private void closeQuietly(AsynchronousFileChannel channel) {
        try {
            channel.close();
        } catch (IOException ignored) {
        }
    }
    
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ConcurrentIOBenchmark.class.getSimpleName())
            .build();
        
        new Runner(opt).run();
    }
}