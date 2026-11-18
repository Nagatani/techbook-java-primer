import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * WatchServiceの実装がOSによって違うこと、イベントが届くまでの時間、
 * イベントがたまりすぎたときのOVERFLOWを観察する。
 */
public class WatchServiceImpl {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("watch");
        try (WatchService ws = FileSystems.getDefault().newWatchService()) {
            IO.println("WatchServiceの実装: " + ws.getClass().getName());
            WatchKey key = dir.register(ws, StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE);

            // 1. ファイルを作ってからイベントが届くまでの時間
            long start = System.nanoTime();
            Files.writeString(dir.resolve("first.txt"), "1");
            WatchKey signalled = ws.poll(10, TimeUnit.SECONDS);
            long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            IO.println("最初のイベント: " + kinds(signalled) + "（" + bucket(ms) + "）");
            signalled.reset();

            // 2. 作ってすぐ消したファイル
            Path flash = dir.resolve("flash.txt");
            Files.writeString(flash, "x");
            Files.delete(flash);
            IO.println("作ってすぐ消したファイルのイベント: " + drain(ws));

            // 3. イベントを取り出さないうちに多数のファイルを作る
            int n = args.length > 0 ? Integer.parseInt(args[0]) : 1000;
            for (int i = 0; i < n; i++) {
                Files.writeString(dir.resolve("f" + i + ".txt"), "");
            }
            int events = 0;
            boolean overflow = false;
            long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
            WatchKey k3;
            while ((k3 = ws.poll(3, TimeUnit.SECONDS)) != null && System.nanoTime() < deadline) {
                for (WatchEvent<?> e : k3.pollEvents()) {
                    if (e.kind() == StandardWatchEventKinds.OVERFLOW) {
                        overflow = true;
                    } else {
                        events++;
                    }
                }
                k3.reset();
            }
            IO.println(n + "個作ったときのイベント: " + events + "件、OVERFLOW=" + overflow);
            IO.println("WatchKeyは同じもの: " + (signalled == key));
        } finally {
            try (Stream<Path> s = Files.list(dir)) {
                for (Path p : s.toList()) {
                    Files.delete(p);
                }
            }
            Files.delete(dir);
        }
    }

    /** 3秒間イベントが来なくなるまで、届いたイベントをすべて集める */
    private static String drain(WatchService ws) throws InterruptedException {
        List<String> all = new ArrayList<>();
        WatchKey k;
        while ((k = ws.poll(3, TimeUnit.SECONDS)) != null) {
            all.addAll(kinds(k));
            k.reset();
        }
        return all.isEmpty() ? "なし" : all.toString();
    }

    private static List<String> kinds(WatchKey key) {
        List<String> list = new ArrayList<>();
        for (WatchEvent<?> e : key.pollEvents()) {
            list.add(e.kind().name() + " " + e.context());
        }
        return list;
    }

    private static String bucket(long ms) {
        if (ms < 100) {
            return "100ミリ秒未満";
        }
        return "100ミリ秒以上、約" + Math.round(ms / 1000.0) + "秒";
    }
}
