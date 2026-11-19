// 第15章：仮想スレッド（Java 21で正式。Java 20まではプレビュー）
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreads {
    static void make() {
        Thread t = Thread.ofVirtual().unstarted(() -> { });
        ExecutorService es = Executors.newVirtualThreadPerTaskExecutor();
    }
}
