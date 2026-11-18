import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * FileChannel.mapが返すMappedByteBufferは2GB（Integer.MAX_VALUEバイト）までであること、
 * FFM APIのMemorySegmentならそれより大きな領域をマップでき、Arenaを閉じると使えなくなることを観察する。
 * 3GBのファイルはsetLengthで作る（多くのファイルシステムでは中身のない「疎なファイル」になり、ディスクはほとんど使わない）。
 */
public class MappedLimits {
    private static final long THREE_GB = 3L * 1024 * 1024 * 1024;

    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("mapped");
        Path big = dir.resolve("big.dat");
        try {
            try (FileChannel ch = FileChannel.open(big, StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.READ, StandardOpenOption.WRITE)) {
                // 最後の1バイトだけを書き、3GBのファイルにする
                ch.write(java.nio.ByteBuffer.wrap(new byte[] {42}), THREE_GB - 1);
                IO.println("ファイルの大きさ: " + ch.size() + "バイト");

                // 1. MappedByteBufferは2GBまで
                try {
                    ch.map(FileChannel.MapMode.READ_ONLY, 0, (long) Integer.MAX_VALUE + 1);
                } catch (IllegalArgumentException e) {
                    IO.println("map(Integer.MAX_VALUE + 1): "
                            + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
                MappedByteBuffer mbb = ch.map(FileChannel.MapMode.READ_ONLY, 0, Integer.MAX_VALUE);
                IO.println("map(Integer.MAX_VALUE)のcapacity: " + mbb.capacity());

                // 2. MemorySegmentなら3GBをまとめてマップできる
                MemorySegment seg;
                try (Arena arena = Arena.ofConfined()) {
                    seg = ch.map(FileChannel.MapMode.READ_ONLY, 0, ch.size(), arena);
                    IO.println("MemorySegmentの大きさ: " + seg.byteSize() + "バイト");
                    IO.println("最後のバイト: " + seg.get(ValueLayout.JAVA_BYTE, THREE_GB - 1));
                    IO.println("途中のバイト（書いていない部分）: " + seg.get(ValueLayout.JAVA_BYTE, THREE_GB / 2));
                    try {
                        seg.asByteBuffer();
                    } catch (RuntimeException e) {
                        IO.println("3GBのsegment.asByteBuffer(): "
                                + e.getClass().getSimpleName() + ": " + e.getMessage());
                    }
                }
                // 3. Arenaを閉じると、そのセグメントにはもうアクセスできない
                try {
                    seg.get(ValueLayout.JAVA_BYTE, 0);
                } catch (IllegalStateException e) {
                    IO.println("Arenaを閉じたあとのアクセス: "
                            + e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }

            // 4. Files.readAllBytesは2GBを超えるファイルを読めない
            try {
                Files.readAllBytes(big);
            } catch (OutOfMemoryError e) {
                IO.println("Files.readAllBytes(3GB): " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        } finally {
            Files.deleteIfExists(big);
            Files.delete(dir);
        }
    }
}
