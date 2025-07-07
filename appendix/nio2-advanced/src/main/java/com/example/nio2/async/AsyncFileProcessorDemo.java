package com.example.nio2.async;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Demonstration of parallel file processing capabilities.
 */
public class AsyncFileProcessorDemo {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java AsyncFileProcessorDemo <file1> [file2] [file3] ...");
            System.err.println("   or: java AsyncFileProcessorDemo --directory <dir> <pattern>");
            System.exit(1);
        }
        
        try {
            if ("--directory".equals(args[0])) {
                if (args.length < 3) {
                    System.err.println("Usage: java AsyncFileProcessorDemo --directory <dir> <pattern>");
                    System.exit(1);
                }
                processDirectory(Paths.get(args[1]), args[2]);
            } else {
                List<Path> files = Arrays.stream(args)
                    .map(Paths::get)
                    .collect(Collectors.toList());
                processFiles(files);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void processFiles(List<Path> files) throws Exception {
        System.out.println("=== Async File Processing Demo ===");
        System.out.println("Processing " + files.size() + " files...\n");
        
        ParallelFileProcessor processor = new ParallelFileProcessor();
        
        try {
            // Example 1: Read all files in parallel
            System.out.println("1. Reading files in parallel:");
            long startTime = System.currentTimeMillis();
            
            CompletableFuture<Map<Path, String>> readFuture = processor.processFiles(files, file -> {
                try {
                    String content = Files.readString(file);
                    System.out.printf("  Read %s (%d bytes)%n", file.getFileName(), content.length());
                    return content;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            
            Map<Path, String> contents = readFuture.get();
            long readTime = System.currentTimeMillis() - startTime;
            System.out.printf("Read %d files in %dms%n%n", contents.size(), readTime);
            
            // Example 2: Calculate statistics
            System.out.println("2. Calculating file statistics:");
            CompletableFuture<ParallelFileProcessor.FileStatistics> statsFuture = 
                processor.calculateStatistics(files);
            
            ParallelFileProcessor.FileStatistics stats = statsFuture.get();
            System.out.println("Statistics: " + stats);
            System.out.println();
            
            // Example 3: Process with timeout
            System.out.println("3. Processing with timeout (5 seconds):");
            CompletableFuture<Map<Path, Integer>> timeoutFuture = 
                processor.processFilesWithTimeout(files, file -> {
                    try {
                        // Simulate slow processing
                        Thread.sleep(1000);
                        return Files.readAllLines(file).size();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, Duration.ofSeconds(5));
            
            Map<Path, Integer> lineCounts = timeoutFuture.get();
            lineCounts.forEach((file, count) -> 
                System.out.printf("  %s: %d lines%n", file.getFileName(), count)
            );
            
        } finally {
            processor.shutdown();
        }
    }
    
    private static void processDirectory(Path directory, String pattern) throws Exception {
        System.out.println("=== Directory Processing Demo ===");
        System.out.printf("Directory: %s%n", directory);
        System.out.printf("Pattern: %s%n%n", pattern);
        
        ParallelFileProcessor processor = new ParallelFileProcessor();
        
        try {
            // Find and process matching files
            CompletableFuture<Map<Path, String>> future = 
                processor.findAndProcessFiles(directory, pattern, file -> {
                    try {
                        BasicFileAttributes attrs = Files.readAttributes(file, BasicFileAttributes.class);
                        return String.format("Size: %d bytes, Modified: %s", 
                            attrs.size(), 
                            attrs.lastModifiedTime());
                    } catch (IOException e) {
                        return "Error: " + e.getMessage();
                    }
                });
            
            Map<Path, String> results = future.get();
            
            System.out.printf("Found %d files:%n", results.size());
            results.forEach((file, info) -> 
                System.out.printf("  %s - %s%n", file.getFileName(), info)
            );
            
            // Aggregate contents
            if (!results.isEmpty()) {
                System.out.println("\nAggregating file contents...");
                List<Path> matchingFiles = new ArrayList<>(results.keySet());
                
                CompletableFuture<String> aggregateFuture = 
                    processor.aggregateFileContents(
                        matchingFiles.subList(0, Math.min(5, matchingFiles.size())), 
                        "\n--- NEXT FILE ---\n"
                    );
                
                String aggregated = aggregateFuture.get();
                System.out.printf("Aggregated content length: %d characters%n", aggregated.length());
            }
            
        } finally {
            processor.shutdown();
        }
    }
}