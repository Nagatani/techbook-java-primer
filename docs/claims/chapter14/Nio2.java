// 第14章：java.nio.file（Java 7、NIO.2）とjava.nio（Java 1.4）。--release 8で通ることだけを確かめる
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Nio2 {
    static boolean exists() {
        Path p = Paths.get("data.txt");
        ByteBuffer b = ByteBuffer.allocate(16);
        return Files.exists(p) && b.capacity() > 0;
    }
}
