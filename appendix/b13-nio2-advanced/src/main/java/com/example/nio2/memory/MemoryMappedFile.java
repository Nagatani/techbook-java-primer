package com.example.nio2.memory;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for working with memory-mapped files.
 *
 * <p>FileChannel.map(mode, offset, size)が返すMappedByteBufferは、標準のAPIでは
 * 解放（アンマップ）できず、GCに回収されるまでマッピングが残る。
 * このクラスはFFM API（Java 22で正式）のArenaを使い、
 * FileChannel.map(mode, offset, size, arena)でマップしたMemorySegmentを
 * close()でArenaごと確実に解放する。
 */
public class MemoryMappedFile implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(MemoryMappedFile.class);

    private final Path path;
    private final FileChannel channel;
    private final FileChannel.MapMode mode;
    // このファイルからマップしたセグメントをまとめて管理する。closeで一度に解放される
    private final Arena arena = Arena.ofShared();
    private MemorySegment segment;
    private ByteBuffer buffer;

    public MemoryMappedFile(Path path, FileChannel.MapMode mode) throws IOException {
        this.path = path;
        this.mode = mode;

        if (mode == FileChannel.MapMode.READ_ONLY) {
            this.channel = FileChannel.open(path, StandardOpenOption.READ);
        } else {
            this.channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
        }

        logger.info("Opened memory-mapped file: {} (mode: {})", path, mode);
    }

    /**
     * Map the entire file into memory.
     */
    public ByteBuffer mapEntireFile() throws IOException {
        long size = channel.size();
        map(0, size);
        logger.info("Mapped entire file: {} bytes", size);
        return buffer;
    }

    /**
     * Map a portion of the file into memory.
     */
    public ByteBuffer mapRegion(long position, long size) throws IOException {
        map(position, size);
        logger.info("Mapped region: position={}, size={}", position, size);
        return buffer;
    }

    private void map(long position, long size) throws IOException {
        this.segment = channel.map(mode, position, size, arena);
        // ByteBufferとして扱えるようにする（2GBを超える領域はMemorySegmentのまま扱う）
        this.buffer = segment.asByteBuffer();
    }

    /**
     * Read a string from the mapped buffer at the specified position.
     */
    public String readString(int position, int length) {
        if (buffer == null) {
            throw new IllegalStateException("File not mapped");
        }

        byte[] bytes = new byte[length];
        buffer.get(position, bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Write a string to the mapped buffer at the specified position.
     */
    public void writeString(int position, String data) {
        if (buffer == null) {
            throw new IllegalStateException("File not mapped");
        }

        if (mode == FileChannel.MapMode.READ_ONLY) {
            throw new IllegalStateException("Cannot write to read-only mapping");
        }

        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        buffer.put(position, bytes);
    }

    /**
     * Force changes to be written to disk.
     */
    public void force() {
        if (segment != null && mode != FileChannel.MapMode.READ_ONLY) {
            segment.force();
            logger.debug("Forced segment to disk");
        }
    }

    /**
     * Get the current buffer for direct manipulation.
     */
    public ByteBuffer getBuffer() {
        return buffer;
    }

    @Override
    public void close() throws IOException {
        buffer = null;
        segment = null;
        // Arenaを閉じると、このArenaでマップしたすべての領域がすぐにアンマップされる
        arena.close();
        channel.close();

        logger.info("Closed memory-mapped file: {}", path);
    }

    /**
     * Utility method to count lines in a large file efficiently.
     */
    public static long countLines(Path path) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            long fileSize = channel.size();
            long position = 0;
            long lineCount = 0;
            int chunkSize = 1024 * 1024 * 100; // 100MB chunks

            while (position < fileSize) {
                long mappingSize = Math.min(chunkSize, fileSize - position);

                // チャンクごとにArenaを作り、読み終えたらすぐに解放する
                try (Arena chunkArena = Arena.ofConfined()) {
                    ByteBuffer buffer = channel.map(
                        FileChannel.MapMode.READ_ONLY,
                        position,
                        mappingSize,
                        chunkArena
                    ).asByteBuffer();

                    while (buffer.hasRemaining()) {
                        if (buffer.get() == '\n') {
                            lineCount++;
                        }
                    }
                }

                position += mappingSize;
            }

            return lineCount;
        }
    }

    /**
     * Search for a pattern in a large file using memory mapping.
     */
    public static long findPattern(Path path, byte[] pattern) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            long fileSize = channel.size();
            long position = 0;
            int chunkSize = 1024 * 1024 * 100; // 100MB chunks
            int overlap = pattern.length - 1; // チャンクの境界をまたぐパターンを見つけるための重なり

            while (position < fileSize) {
                // [position, position + chunkSize + overlap) をマップし、
                // 開始位置が [position, position + chunkSize) にある一致を探す。
                // 末尾の重なりがあるので、境界をまたぐ一致も見つかる
                long mappingSize = Math.min((long) chunkSize + overlap, fileSize - position);

                try (Arena chunkArena = Arena.ofConfined()) {
                    ByteBuffer buffer = channel.map(
                        FileChannel.MapMode.READ_ONLY,
                        position,
                        mappingSize,
                        chunkArena
                    ).asByteBuffer();

                    long foundAt = searchInBuffer(buffer, pattern, position);
                    if (foundAt >= 0) {
                        return foundAt;
                    }
                }

                position += chunkSize;
            }

            return -1; // Pattern not found
        }
    }

    private static long searchInBuffer(ByteBuffer buffer, byte[] pattern, long bufferOffset) {
        int patternLength = pattern.length;
        int limit = buffer.limit() - patternLength + 1;

        for (int i = buffer.position(); i < limit; i++) {
            boolean found = true;
            for (int j = 0; j < patternLength; j++) {
                if (buffer.get(i + j) != pattern[j]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                return bufferOffset + i;
            }
        }

        return -1;
    }
}
