package com.example.nio2.memory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Demonstration of shared memory for inter-process communication.
 */
public class SharedMemoryDemo {
    
    public static void main(String[] args) {
        if (args.length < 1) {
            IO.println("Usage: java SharedMemoryDemo <mode>");
            IO.println("Modes:");
            IO.println("  producer - Write messages to shared memory");
            IO.println("  consumer - Read messages from shared memory");
            IO.println("  demo     - Run both producer and consumer");
            System.exit(1);
        }
        
        String mode = args[0].toLowerCase();
        
        try {
            switch (mode) {
                case "producer":
                    runProducer();
                    break;
                case "consumer":
                    runConsumer();
                    break;
                case "demo":
                    runDemo();
                    break;
                default:
                    System.err.println("Unknown mode: " + mode);
                    System.exit(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void runDemo() throws Exception {
        IO.println("=== Shared Memory Demo ===");
        IO.println("Running producer and consumer in same process\n");
        
        Path sharedFile = Files.createTempFile("shared-memory", ".dat");
        IO.println("Shared memory file: " + sharedFile);
        
        // Create shared memory queue
        int messageSize = 256;
        int queueCapacity = 10;
        
        AtomicBoolean running = new AtomicBoolean(true);
        CountDownLatch startLatch = new CountDownLatch(2);
        
        // Producer thread
        Thread producer = new Thread(() -> {
            try (SharedMemoryFile.SharedMemoryQueue queue = 
                    new SharedMemoryFile.SharedMemoryQueue(sharedFile, messageSize, queueCapacity)) {
                
                startLatch.countDown();
                startLatch.await(); // Wait for consumer to start
                
                IO.println("\n[Producer] Started");
                
                for (int i = 1; running.get() && i <= 20; i++) {
                    String message = String.format("Message #%d from producer at %tT", 
                        i, System.currentTimeMillis());
                    
                    byte[] data = message.getBytes(StandardCharsets.UTF_8);
                    
                    while (!queue.enqueue(data)) {
                        IO.println("[Producer] Queue full, waiting...");
                        Thread.sleep(100);
                    }
                    
                    IO.println("[Producer] Sent: " + message);
                    Thread.sleep(500); // Simulate work
                }
                
                IO.println("[Producer] Finished");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Consumer thread
        Thread consumer = new Thread(() -> {
            try (SharedMemoryFile.SharedMemoryQueue queue = 
                    new SharedMemoryFile.SharedMemoryQueue(sharedFile, messageSize, queueCapacity)) {
                
                startLatch.countDown();
                startLatch.await(); // Wait for producer to start
                
                IO.println("[Consumer] Started");
                
                int emptyCount = 0;
                while (running.get() || emptyCount < 10) {
                    byte[] data = queue.dequeue();
                    
                    if (data != null) {
                        String message = new String(data, StandardCharsets.UTF_8);
                        IO.println("[Consumer] Received: " + message);
                        emptyCount = 0;
                        Thread.sleep(750); // Simulate slow processing
                    } else {
                        emptyCount++;
                        Thread.sleep(100);
                    }
                }
                
                IO.println("[Consumer] Finished");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        // Start threads
        producer.start();
        consumer.start();
        
        // Wait for producer to finish
        producer.join();
        running.set(false);
        
        // Wait for consumer to finish
        consumer.join();
        
        // Cleanup
        Files.deleteIfExists(sharedFile);
        IO.println("\nDemo completed");
    }
    
    private static void runProducer() throws Exception {
        IO.println("=== Shared Memory Producer ===");
        
        Path sharedFile = Paths.get("shared-queue.dat");
        int messageSize = 256;
        int queueCapacity = 100;
        
        try (SharedMemoryFile.SharedMemoryQueue queue = 
                new SharedMemoryFile.SharedMemoryQueue(sharedFile, messageSize, queueCapacity)) {
            
            IO.println("Producer started. Press Ctrl+C to stop.");
            IO.println("Shared file: " + sharedFile.toAbsolutePath());
            
            int messageCount = 0;
            while (true) {
                String message = String.format("Message #%d from PID %s at %tT", 
                    ++messageCount, 
                    ProcessHandle.current().pid(),
                    System.currentTimeMillis());
                
                byte[] data = message.getBytes(StandardCharsets.UTF_8);
                
                if (queue.enqueue(data)) {
                    IO.println("Sent: " + message);
                } else {
                    IO.println("Queue full, waiting...");
                    Thread.sleep(1000);
                }
                
                Thread.sleep(2000); // Send message every 2 seconds
            }
        }
    }
    
    private static void runConsumer() throws Exception {
        IO.println("=== Shared Memory Consumer ===");
        
        Path sharedFile = Paths.get("shared-queue.dat");
        
        if (!Files.exists(sharedFile)) {
            System.err.println("Shared file not found: " + sharedFile.toAbsolutePath());
            System.err.println("Please run the producer first.");
            return;
        }
        
        int messageSize = 256;
        int queueCapacity = 100;
        
        try (SharedMemoryFile.SharedMemoryQueue queue = 
                new SharedMemoryFile.SharedMemoryQueue(sharedFile, messageSize, queueCapacity)) {
            
            IO.println("Consumer started. Press Ctrl+C to stop.");
            IO.println("Shared file: " + sharedFile.toAbsolutePath());
            
            while (true) {
                byte[] data = queue.dequeue();
                
                if (data != null) {
                    String message = new String(data, StandardCharsets.UTF_8);
                    IO.println("Received: " + message);
                } else {
                    // No messages available
                    Thread.sleep(100);
                }
            }
        }
    }
}