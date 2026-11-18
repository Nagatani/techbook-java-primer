import com.sun.management.UnixOperatingSystemMXBean;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileSystemLoopException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Files.listとFiles.walkのストリームは、閉じるまでディレクトリを開いたままにすることを観察する。
 * 開いているファイル記述子の数は、UnixのOS（macOS・Linux）でだけ数えられる。
 */
public class StreamsMustClose {
    public static void main(String[] args) throws IOException {
        Path dir = Files.createTempDirectory("streams");
        for (String name : List.of("b.txt", "a.txt", "c.txt")) {
            Files.writeString(dir.resolve(name), name);
        }

        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        if (os instanceof UnixOperatingSystemMXBean unix) {
            long before = unix.getOpenFileDescriptorCount();
            List<Stream<Path>> leaked = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                Stream<Path> s = Files.list(dir);
                s.count(); // 終端操作を呼んでも閉じられない
                leaked.add(s);
            }
            long afterOpen = unix.getOpenFileDescriptorCount();
            leaked.forEach(Stream::close);
            long afterClose = unix.getOpenFileDescriptorCount();
            IO.println("閉じずに100回Files.listしたあとの増分: " + (afterOpen - before));
            IO.println("closeしたあとの増分: " + (afterClose - before));
        } else {
            IO.println("このOSではファイル記述子の数を数えられない");
        }

        // 順序はAPIでは決まっていない。決まった順序が必要ならsortedで並べる
        try (Stream<Path> s = Files.list(dir)) {
            IO.println("名前順に並べた一覧: "
                    + s.map(p -> p.getFileName().toString()).sorted().toList());
        }

        // FOLLOW_LINKSで循環するシンボリックリンクをたどると例外になる
        Path sub = Files.createDirectory(dir.resolve("sub"));
        Path loop = sub.resolve("loop");
        Files.createSymbolicLink(loop, dir);
        try (Stream<Path> s = Files.walk(dir, FileVisitOption.FOLLOW_LINKS)) {
            s.count();
            IO.println("循環をたどった");
        } catch (UncheckedIOException e) {
            IO.println("FOLLOW_LINKSで循環: " + e.getClass().getSimpleName()
                    + "（原因 " + e.getCause().getClass().getSimpleName() + "）");
            IO.println("原因はFileSystemLoopException: "
                    + (e.getCause() instanceof FileSystemLoopException));
        }
        try (Stream<Path> s = Files.walk(dir)) {
            IO.println("FOLLOW_LINKSなしの要素数: " + s.count());
        }

        // 後片付け（深い順に消す）
        try (Stream<Path> s = Files.walk(dir)) {
            for (Path p : s.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(p);
            }
        }
    }
}
