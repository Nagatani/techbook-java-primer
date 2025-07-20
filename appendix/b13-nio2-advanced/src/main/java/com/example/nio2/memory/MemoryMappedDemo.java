package com.example.nio2.memory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Random;

/**
 * Demonstration of memory-mapped file operations.
 */
public class MemoryMappedDemo {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java MemoryMappedDemo <file-path> [operation]");
            System.err.println("Operations: read, write, search, count");
            System.exit(1);
        }
        
        Path filePath = Paths.get(args[0]);
        String operation = args.length > 1 ? args[1] : "demo";
        
        try {
            switch (operation.toLowerCase()) {
                case "read":
                    demonstrateRead(filePath);
                    break;
                case "write":
                    demonstrateWrite(filePath);
                    break;
                case "search":
                    demonstrateSearch(filePath);
                    break;
                case "count":
                    demonstrateLineCount(filePath);
                    break;
                default:
                    demonstrateAll(filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void demonstrateAll(Path filePath) throws IOException {
        System.out.println("=== Memory-Mapped File Demo ===");
        System.out.println("File: " + filePath.toAbsolutePath());
        
        // Create a test file if it doesn't exist
        if (!Files.exists(filePath)) {
            createTestFile(filePath);
        }
        
        long fileSize = Files.size(filePath);
        System.out.printf("File size: %s%n%n", formatSize(fileSize));
        
        // Demonstrate various operations
        demonstrateRead(filePath);
        demonstrateSearch(filePath);
        demonstrateLineCount(filePath);
        demonstrateModification(filePath);
    }
    
    private static void demonstrateRead(Path filePath) throws IOException {
        System.out.println("--- Reading with Memory-Mapped File ---");
        
        try (MemoryMappedFile mmf = new MemoryMappedFile(filePath, FileChannel.MapMode.READ_ONLY)) {
            MappedByteBuffer buffer = mmf.mapEntireFile();
            
            // Read first 100 bytes
            int readLength = Math.min(100, buffer.remaining());
            byte[] bytes = new byte[readLength];
            buffer.get(bytes);
            
            System.out.println("First " + readLength + " bytes:");
            System.out.println(new String(bytes, StandardCharsets.UTF_8));
            System.out.println("...\n");
            
            // Random access demonstration
            if (buffer.capacity() > 1000) {
                buffer.position(1000);
                byte[] middleBytes = new byte[Math.min(50, buffer.remaining())];
                buffer.get(middleBytes);
                System.out.println("Bytes from position 1000:");
                System.out.println(new String(middleBytes, StandardCharsets.UTF_8));
                System.out.println("...\n");
            }
        }
    }
    
    private static void demonstrateWrite(Path filePath) throws IOException {
        System.out.println("--- Writing with Memory-Mapped File ---");
        
        // Create a copy for writing
        Path writePath = filePath.getParent().resolve(filePath.getFileName() + ".write");
        Files.copy(filePath, writePath, StandardCopyOption.REPLACE_EXISTING);
        
        try (MemoryMappedFile mmf = new MemoryMappedFile(writePath, FileChannel.MapMode.READ_WRITE)) {
            MappedByteBuffer buffer = mmf.mapEntireFile();
            
            // Modify content at specific position
            String newContent = "[MODIFIED BY MEMORY MAPPING]";
            byte[] bytes = newContent.getBytes(StandardCharsets.UTF_8);
            
            buffer.position(0);
            buffer.put(bytes);
            
            // Force changes to disk
            mmf.force();
            
            System.out.println("Modified file created: " + writePath);
            System.out.println("Wrote " + bytes.length + " bytes at position 0\n");
        }
    }
    
    private static void demonstrateSearch(Path filePath) throws IOException {
        System.out.println("--- Pattern Search with Memory-Mapped File ---");
        
        String searchPattern = "the";
        byte[] pattern = searchPattern.getBytes(StandardCharsets.UTF_8);
        
        long startTime = System.currentTimeMillis();
        long position = MemoryMappedFile.findPattern(filePath, pattern);
        long duration = System.currentTimeMillis() - startTime;
        
        if (position >= 0) {
            System.out.printf("Pattern '%s' found at position %d%n", searchPattern, position);
            
            // Show context
            try (MemoryMappedFile mmf = new MemoryMappedFile(filePath, FileChannel.MapMode.READ_ONLY)) {
                long contextStart = Math.max(0, position - 20);
                long contextSize = Math.min(60, Files.size(filePath) - contextStart);
                MappedByteBuffer buffer = mmf.mapRegion(contextStart, contextSize);
                
                byte[] context = new byte[(int) contextSize];
                buffer.get(context);
                System.out.println("Context: ..." + new String(context, StandardCharsets.UTF_8) + "...");
            }
        } else {
            System.out.printf("Pattern '%s' not found%n", searchPattern);
        }
        
        System.out.printf("Search completed in %dms%n%n", duration);
    }
    
    private static void demonstrateLineCount(Path filePath) throws IOException {
        System.out.println("--- Line Counting with Memory-Mapped File ---");
        
        long startTime = System.currentTimeMillis();
        long lineCount = MemoryMappedFile.countLines(filePath);
        long duration = System.currentTimeMillis() - startTime;
        
        System.out.printf("Total lines: %,d%n", lineCount);
        System.out.printf("Counting completed in %dms%n", duration);
        
        long fileSize = Files.size(filePath);
        double throughput = (double) fileSize / duration * 1000 / (1024 * 1024);
        System.out.printf("Throughput: %.2f MB/s%n%n", throughput);
    }
    
    private static void demonstrateModification(Path filePath) throws IOException {
        System.out.println("--- In-Place Modification Demo ---");
        
        // Create a copy for modification
        Path modPath = filePath.getParent().resolve(filePath.getFileName() + ".mod");
        Files.copy(filePath, modPath, StandardCopyOption.REPLACE_EXISTING);
        
        try (MemoryMappedFile mmf = new MemoryMappedFile(modPath, FileChannel.MapMode.READ_WRITE)) {
            MappedByteBuffer buffer = mmf.mapEntireFile();
            
            // Example: Convert all lowercase 'a' to uppercase 'A'
            int modifications = 0;
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                if (b == 'a') {
                    buffer.put(buffer.position() - 1, (byte) 'A');
                    modifications++;
                }
            }
            
            mmf.force();
            System.out.printf("Modified %d characters in file: %s%n%n", modifications, modPath);
        }
    }
    
    private static void createTestFile(Path filePath) throws IOException {
        System.out.println("Creating test file...");
        
        StringBuilder content = new StringBuilder();
        content.append("This is a test file for demonstrating memory-mapped file operations.\n");
        content.append("Memory-mapped files provide efficient access to large files.\n");
        content.append("They map file contents directly into memory.\n\n");
        
        // Add some lorem ipsum text
        String lorem = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ";
        Random random = new Random();
        
        for (int i = 0; i < 1000; i++) {
            content.append("Line ").append(i + 1).append(": ");
            int words = 5 + random.nextInt(10);
            for (int j = 0; j < words; j++) {
                content.append(lorem);
            }
            content.append("\n");
        }
        
        Files.writeString(filePath, content.toString());
        System.out.println("Test file created: " + filePath);
        System.out.println("Size: " + formatSize(Files.size(filePath)) + "\n");
    }
    
    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}