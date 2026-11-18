import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * FileLockはプロセス（JVM）の単位で働くことを観察する。
 * 同じJVMの中で重ねてロックすると例外になり、別のJVMからはtryLockがnullを返す。
 * 引数にファイル名を渡すと、子のプロセスとしてtryLockを試す。
 */
public class FileLocks {
    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            child(Path.of(args[0]));
            return;
        }
        Path dir = Files.createTempDirectory("locks");
        Path file = dir.resolve("lock.dat");
        Files.write(file, new byte[100]);
        try (FileChannel ch = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE);
             FileChannel other = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            FileLock lock = ch.lock(0, 50, false);
            IO.println("排他ロック: isShared=" + lock.isShared() + " isValid=" + lock.isValid());

            // 1. 同じJVMで重なる範囲をロックすると、待たずに例外になる（別のチャネルでも同じ）
            try {
                other.tryLock(10, 10, false);
            } catch (OverlappingFileLockException e) {
                IO.println("同じJVMの別のチャネルで重なる範囲: " + e.getClass().getSimpleName());
            }
            // 2. 重ならない範囲ならロックできる
            try (FileLock second = other.tryLock(50, 50, false)) {
                IO.println("重ならない範囲: " + (second != null ? "ロックできた" : "null"));
            }

            // 3. 別のJVM（子のプロセス）からtryLockするとnullが返る
            IO.println("子のプロセス（ロック中）: " + runChild(file));
            // 4. ロックを取らずに書き込む処理は止められない（ロックが強制かどうかはOSによる）
            try {
                int n = other.write(ByteBuffer.wrap(new byte[] {1, 2, 3}), 0);
                IO.println("ロック中の範囲へ、ロックを取らずに書き込み: " + n + "バイト書けた");
            } catch (IOException e) {
                IO.println("ロック中の範囲へ、ロックを取らずに書き込み: " + e.getClass().getSimpleName());
            }
            lock.release();
            IO.println("解放後: isValid=" + lock.isValid());
            IO.println("子のプロセス（解放後）: " + runChild(file));
        }

        // 5. 共有ロックには読み取りのできるチャネルが要る
        try (FileChannel writeOnly = FileChannel.open(file, StandardOpenOption.WRITE)) {
            writeOnly.lock(0, 10, true);
        } catch (java.nio.channels.NonReadableChannelException e) {
            IO.println("書き込み専用のチャネルで共有ロック: " + e.getClass().getSimpleName());
        }
        Files.delete(file);
        Files.delete(dir);
    }

    private static String runChild(Path file) throws IOException, InterruptedException {
        String java = ProcessHandle.current().info().command().orElse("java");
        Process p = new ProcessBuilder(java, "-cp", System.getProperty("java.class.path"),
                "FileLocks", file.toString()).redirectErrorStream(true).start();
        String out = new String(p.getInputStream().readAllBytes()).strip();
        p.waitFor();
        return out;
    }

    private static void child(Path file) throws IOException {
        try (FileChannel ch = FileChannel.open(file, StandardOpenOption.READ, StandardOpenOption.WRITE);
             FileLock lock = ch.tryLock(0, 50, false)) {
            IO.println(lock == null ? "tryLockはnull" : "ロックできた");
        }
    }
}
