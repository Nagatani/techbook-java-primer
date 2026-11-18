import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * FileChannel.mapの3つのモード（READ_ONLY・READ_WRITE・PRIVATE）の違いを観察する。
 */
public class MapModes {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("mapmodes");
        Path file = dir.resolve("data.bin");
        Files.write(file, new byte[0]);
        try {
            try (FileChannel ch = FileChannel.open(file, StandardOpenOption.READ,
                    StandardOpenOption.WRITE)) {
                // 1. READ_WRITEでファイルより大きくマップすると、ファイルが伸びる
                IO.println("マップ前の大きさ: " + ch.size());
                MappedByteBuffer rw = ch.map(FileChannel.MapMode.READ_WRITE, 0, 16);
                IO.println("16バイトをREAD_WRITEでマップしたあとの大きさ: " + ch.size());

                // 2. READ_WRITEで書いた内容はファイルに反映される
                rw.put(0, (byte) 'A');
                rw.force();
                IO.println("READ_WRITEで書いたあとのファイルの先頭: " + (char) Files.readAllBytes(file)[0]);

                // 3. PRIVATEはコピーオンライト。書いてもファイルは変わらない
                MappedByteBuffer priv = ch.map(FileChannel.MapMode.PRIVATE, 0, 16);
                priv.put(0, (byte) 'B');
                IO.println("PRIVATEのバッファーの先頭: " + (char) priv.get(0)
                        + " / ファイルの先頭: " + (char) Files.readAllBytes(file)[0]);

                // 4. READ_ONLYのバッファーに書くと例外
                MappedByteBuffer ro = ch.map(FileChannel.MapMode.READ_ONLY, 0, 16);
                IO.println("READ_ONLYのisReadOnly: " + ro.isReadOnly());
            }
            // 5. 読み取り専用で開いたチャネルはREAD_WRITEでマップできない
            try (FileChannel ch = FileChannel.open(file, StandardOpenOption.READ)) {
                ch.map(FileChannel.MapMode.READ_WRITE, 0, 16);
            } catch (NonWritableChannelException e) {
                IO.println("READで開いてREAD_WRITEでマップ: " + e.getClass().getSimpleName());
            }
        } finally {
            Files.deleteIfExists(file);
            Files.delete(dir);
        }
    }
}
