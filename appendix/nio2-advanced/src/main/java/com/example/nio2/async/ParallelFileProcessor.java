package com.example.nio2.async;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parallel file processor for handling multiple files concurrently.
 */
public class ParallelFileProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ParallelFileProcessor.class);
    
    private final ExecutorService executor;
    private final int parallelism;
    
    public ParallelFileProcessor() {
        this(Runtime.getRuntime().availableProcessors());
    }
    
    public ParallelFileProcessor(int parallelism) {
        this.parallelism = parallelism;
        this.executor = new ForkJoinPool(parallelism);
        logger.info("Initialized with parallelism: {}", parallelism);
    }
    
    /**
     * Process multiple files in parallel.
     */
    public <T> CompletableFuture<Map<Path, T>> processFiles(
            List<Path> files, 
            Function<Path, T> processor) {
        
        logger.info("Processing {} files with parallelism {}", files.size(), parallelism);
        long startTime = System.currentTimeMillis();
        
        List<CompletableFuture<FileResult<T>>> futures = files.stream()
            .map(file -> CompletableFuture
                .supplyAsync(() -> {
                    try {
                        T result = processor.apply(file);
                        return new FileResult<>(file, result, null);
                    } catch (Exception e) {
                        logger.error("Error processing file: " + file, e);
                        return new FileResult<T>(file, null, e);
                    }
                }, executor)
            )
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> {
                long duration = System.currentTimeMillis() - startTime;
                Map<Path, T> results = futures.stream()
                    .map(CompletableFuture::join)
                    .filter(r -> r.error == null)
                    .collect(Collectors.toMap(
                        r -> r.file,
                        r -> r.result
                    ));
                
                logger.info("Processed {} files in {}ms", results.size(), duration);
                return results;
            });
    }
    
    /**
     * Process files in batches to limit concurrent operations.
     */
    public <T> CompletableFuture<Map<Path, T>> processFilesInBatches(
            List<Path> files,
            Function<Path, T> processor,
            int batchSize) {
        
        logger.info("Processing {} files in batches of {}", files.size(), batchSize);
        
        List<List<Path>> batches = new ArrayList<>();
        for (int i = 0; i < files.size(); i += batchSize) {
            batches.add(files.subList(i, Math.min(i + batchSize, files.size())));
        }
        
        CompletableFuture<Map<Path, T>> resultFuture = CompletableFuture.completedFuture(new HashMap<>());
        
        for (List<Path> batch : batches) {
            resultFuture = resultFuture.thenCompose(accumulatedResults ->
                processFiles(batch, processor)
                    .thenApply(batchResults -> {
                        accumulatedResults.putAll(batchResults);
                        return accumulatedResults;
                    })
            );
        }
        
        return resultFuture;
    }
    
    /**
     * Find and process files matching a pattern.
     */
    public CompletableFuture<Map<Path, String>> findAndProcessFiles(
            Path directory,
            String glob,
            Function<Path, String> processor) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
                List<Path> matchingFiles = Files.walk(directory)
                    .filter(Files::isRegularFile)
                    .filter(matcher::matches)
                    .collect(Collectors.toList());
                
                logger.info("Found {} files matching pattern: {}", matchingFiles.size(), glob);
                return matchingFiles;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }, executor).thenCompose(files -> processFiles(files, processor));
    }
    
    /**
     * Process files with timeout.
     */
    public <T> CompletableFuture<Map<Path, T>> processFilesWithTimeout(
            List<Path> files,
            Function<Path, T> processor,
            Duration timeout) {
        
        CompletableFuture<Map<Path, T>> future = processFiles(files, processor);
        
        return future.orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
            .exceptionally(throwable -> {
                if (throwable instanceof TimeoutException) {
                    logger.error("Processing timed out after {}", timeout);
                    return Collections.emptyMap();
                }
                throw new CompletionException(throwable);
            });
    }
    
    /**
     * Aggregate results from multiple files.
     */
    public CompletableFuture<String> aggregateFileContents(
            List<Path> files,
            String delimiter) {
        
        return processFiles(files, file -> {
            try {
                return Files.readString(file);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).thenApply(results -> 
            results.values().stream()
                .collect(Collectors.joining(delimiter))
        );
    }
    
    /**
     * Calculate statistics for multiple files.
     */
    public CompletableFuture<FileStatistics> calculateStatistics(List<Path> files) {
        return processFiles(files, file -> {
            try {
                BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                return new FileInfo(file, attrs.size(), attrs.lastModifiedTime().toMillis());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).thenApply(results -> {
            long totalSize = results.values().stream()
                .mapToLong(info -> info.size)
                .sum();
            
            long avgSize = results.isEmpty() ? 0 : totalSize / results.size();
            
            Optional<FileInfo> largest = results.values().stream()
                .max(Comparator.comparingLong(info -> info.size));
            
            Optional<FileInfo> newest = results.values().stream()
                .max(Comparator.comparingLong(info -> info.lastModified));
            
            return new FileStatistics(
                results.size(),
                totalSize,
                avgSize,
                largest.map(info -> info.path).orElse(null),
                newest.map(info -> info.path).orElse(null)
            );
        });
    }
    
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    private static class FileResult<T> {
        final Path file;
        final T result;
        final Exception error;
        
        FileResult(Path file, T result, Exception error) {
            this.file = file;
            this.result = result;
            this.error = error;
        }
    }
    
    private static class FileInfo {
        final Path path;
        final long size;
        final long lastModified;
        
        FileInfo(Path path, long size, long lastModified) {
            this.path = path;
            this.size = size;
            this.lastModified = lastModified;
        }
    }
    
    public static class FileStatistics {
        public final int fileCount;
        public final long totalSize;
        public final long averageSize;
        public final Path largestFile;
        public final Path newestFile;
        
        FileStatistics(int fileCount, long totalSize, long averageSize,
                      Path largestFile, Path newestFile) {
            this.fileCount = fileCount;
            this.totalSize = totalSize;
            this.averageSize = averageSize;
            this.largestFile = largestFile;
            this.newestFile = newestFile;
        }
        
        @Override
        public String toString() {
            return String.format(
                "FileStatistics{count=%d, totalSize=%d, avgSize=%d, largest=%s, newest=%s}",
                fileCount, totalSize, averageSize, largestFile, newestFile
            );
        }
    }
}