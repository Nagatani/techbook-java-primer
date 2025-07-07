package com.example.nio2.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * JMH benchmarks comparing different I/O approaches.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class IOBenchmark {
    
    @Param({"1048576", "10485760", "104857600"}) // 1MB, 10MB, 100MB
    private int fileSize;
    
    private Path tempFile;
    private byte[] data;
    
    @Setup
    public void setup() throws IOException {
        tempFile = Files.createTempFile("benchmark", ".dat");
        data = new byte[fileSize];
        new Random().nextBytes(data);
        Files.write(tempFile, data);
    }
    
    @TearDown
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }
    
    @Benchmark
    public byte[] traditionalIO() throws IOException {
        try (FileInputStream fis = new FileInputStream(tempFile.toFile())) {
            byte[] buffer = new byte[fileSize];
            int totalRead = 0;
            while (totalRead < fileSize) {
                int read = fis.read(buffer, totalRead, fileSize - totalRead);
                if (read == -1) break;
                totalRead += read;
            }
            return buffer;
        }
    }
    
    @Benchmark
    public byte[] bufferedIO() throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(tempFile.toFile()), 65536)) {
            byte[] buffer = new byte[fileSize];
            int totalRead = 0;
            while (totalRead < fileSize) {
                int read = bis.read(buffer, totalRead, fileSize - totalRead);
                if (read == -1) break;
                totalRead += read;
            }
            return buffer;
        }
    }
    
    @Benchmark
    public byte[] nioChannelRead() throws IOException {
        try (FileChannel channel = FileChannel.open(tempFile, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(fileSize);
            while (buffer.hasRemaining()) {
                channel.read(buffer);
            }
            buffer.flip();
            byte[] result = new byte[fileSize];
            buffer.get(result);
            return result;
        }
    }
    
    @Benchmark
    public byte[] nioDirectBufferRead() throws IOException {
        try (FileChannel channel = FileChannel.open(tempFile, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(fileSize);
            while (buffer.hasRemaining()) {
                channel.read(buffer);
            }
            buffer.flip();
            byte[] result = new byte[fileSize];
            buffer.get(result);
            return result;
        }
    }
    
    @Benchmark
    public byte[] memoryMappedRead() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(tempFile.toFile(), "r");
             FileChannel channel = file.getChannel()) {
            
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                fileSize
            );
            
            byte[] result = new byte[fileSize];
            buffer.get(result);
            return result;
        }
    }
    
    @Benchmark
    public byte[] filesReadAllBytes() throws IOException {
        return Files.readAllBytes(tempFile);
    }
    
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(IOBenchmark.class.getSimpleName())
            .build();
        
        new Runner(opt).run();
    }
}