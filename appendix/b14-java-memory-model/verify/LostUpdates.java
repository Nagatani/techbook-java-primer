import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

/**
 * 複数のスレッドが同じカウンターを増やしたとき、更新が失われるかを観察する。
 * 4つのスレッドがそれぞれ100万回ずつ増やすので、正しければ400万になる。
 *
 *   java LostUpdates
 */
public class LostUpdates {

    static final int THREADS = 4;
    static final int PER_THREAD = 1_000_000;
    static final int EXPECTED = THREADS * PER_THREAD;

    static int plainCount;
    static volatile int volatileCount;
    static int syncCount;
    static final Object LOCK = new Object();
    static final AtomicInteger ATOMIC = new AtomicInteger();
    static final LongAdder ADDER = new LongAdder();

    public static void main(String[] args) throws InterruptedException {
        report("int（plain）のcount++", () -> plainCount++, () -> plainCount);
        report("volatile intのcount++", () -> volatileCount++, () -> volatileCount);
        report("synchronizedの中のcount++", () -> {
            synchronized (LOCK) {
                syncCount++;
            }
        }, () -> syncCount);
        report("AtomicInteger.incrementAndGet", ATOMIC::incrementAndGet, ATOMIC::get);
        report("LongAdder.increment", ADDER::increment, () -> (int) ADDER.sum());
    }

    interface Total {
        int get();
    }

    static void report(String name, Runnable increment, Total total) throws InterruptedException {
        CountDownLatch start = new CountDownLatch(1);
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            threads[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    return;
                }
                for (int j = 0; j < PER_THREAD; j++) {
                    increment.run();
                }
            });
            threads[i].start();
        }
        // すべてのスレッドを同時に走らせる
        start.countDown();
        for (Thread t : threads) {
            t.join();
        }
        int result = total.get();
        String verdict = result == EXPECTED ? "400万（正しい）" : "400万より少ない（更新が失われた）";
        IO.println(name + ": " + verdict);
    }
}
