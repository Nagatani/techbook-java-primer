// 第15章：10,000個の仮想スレッドがそれぞれ1秒sleepしても、全体は数秒で終わる
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualMany {
    public static void main(String[] args) {
        AtomicInteger done = new AtomicInteger();
        AtomicBoolean allVirtual = new AtomicBoolean(true);
        long t0 = System.nanoTime();
        try (ExecutorService es = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                es.submit(() -> {
                    if (!Thread.currentThread().isVirtual()) {
                        allVirtual.set(false);
                    }
                    Thread.sleep(Duration.ofSeconds(1));
                    done.incrementAndGet();
                    return null;
                });
            }
        }
        double sec = (System.nanoTime() - t0) / 1e9;
        IO.println("終わったタスク: " + done.get() + "、すべて仮想スレッド: " + allVirtual.get());
        IO.println(sec < 5 ? "5秒未満で終わった" : "5秒以上かかった");
        Thread v = Thread.ofVirtual().unstarted(() -> { });
        IO.println("Thread.ofVirtual()で作ったスレッドのisVirtual: " + v.isVirtual());
    }
}
