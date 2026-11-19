// 第15章：synchronizedメソッドが排他するのは、同じオブジェクトのロックを取るスレッドどうし
// 2つのスレッドが同時にincrement()の中にいたことがあるかを、同じインスタンスと別のインスタンスで比べる
import java.util.concurrent.atomic.AtomicInteger;

public class SyncScope {
    static final AtomicInteger inside = new AtomicInteger();
    static volatile int maxInside;

    static class Counter {
        private int count;

        public synchronized void increment() {
            int now = inside.incrementAndGet();
            if (now > maxInside) {
                maxInside = now;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            count++;
            inside.decrementAndGet();
        }
    }

    static int run(Counter a, Counter b) throws InterruptedException {
        maxInside = 0;
        Thread t1 = new Thread(() -> { for (int i = 0; i < 10; i++) a.increment(); });
        Thread t2 = new Thread(() -> { for (int i = 0; i < 10; i++) b.increment(); });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        return maxInside;
    }

    public static void main(String[] args) throws InterruptedException {
        Counter shared = new Counter();
        IO.println("同じインスタンス: 同時に中にいたスレッドは最大" + run(shared, shared) + "個");
        IO.println("別のインスタンス: 同時に中にいたスレッドは最大" + run(new Counter(), new Counter()) + "個");
    }
}
