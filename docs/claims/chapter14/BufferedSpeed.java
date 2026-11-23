// 第14章：1バイトずつ書くとき、バッファーを使うとOSへの要求が減って速くなる
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class BufferedSpeed {
    static long write(OutputStream out) throws IOException {
        long t = System.nanoTime();
        try (out) {
            for (int i = 0; i < 200_000; i++) out.write(i);
        }
        return System.nanoTime() - t;
    }

    public static void main(String[] args) throws IOException {
        Path f = Files.createTempFile("ch14", ".bin");
        long plain = write(new FileOutputStream(f.toFile()));
        long buffered = write(new BufferedOutputStream(new FileOutputStream(f.toFile())));
        System.out.println("buffered is more than 10x faster: " + (plain > buffered * 10));
        Files.delete(f);
    }
}
