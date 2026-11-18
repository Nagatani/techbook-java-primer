import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * @Timeoutで決めた時間を超えたテストが失敗になり、途中で割り込まれることを観察する。
 */
public class TimeoutProbe {

    static class Cases {
        @Test
        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void tooSlow() throws InterruptedException {
            Thread.sleep(10_000); // 10秒待つ
        }

        @Test
        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void fastEnough() throws InterruptedException {
            Thread.sleep(10);
        }
    }

    /** 割り込みを確かめない計算のループ（1秒間回り続ける） */
    static class Busy {
        @Test
        @Timeout(value = 200, unit = TimeUnit.MILLISECONDS)
        void busyLoop() {
            long end = System.nanoTime() + TimeUnit.SECONDS.toNanos(1);
            long n = 0;
            while (System.nanoTime() < end) {
                n++;
            }
        }
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        ProbeRunner.run(Cases.class);
        long ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        IO.println(ms < 5_000 ? "finished in less than 5 seconds (interrupted)"
                : "took " + ms + " ms");

        start = System.nanoTime();
        ProbeRunner.run(Busy.class);
        ms = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        IO.println(ms >= 1_000 ? "busy loop ran to the end (at least 1 second)"
                : "busy loop stopped after " + ms + " ms");
    }
}
