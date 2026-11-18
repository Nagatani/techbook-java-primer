import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 不変なオブジェクトを指す「今の値」の参照を、複数のスレッドが差し替える。
 * volatileの読み取りと書き込みに分けると更新が失われ、
 * AtomicReference.updateAndGetなら失われないことを観察する。
 *   javac LostUpdate.java && java LostUpdate
 */
public class LostUpdate {
    record Counter(int value) {
        Counter next() {
            return new Counter(value + 1);
        }
    }

    static volatile Counter shared = new Counter(0);
    static final AtomicReference<Counter> atomic = new AtomicReference<>(new Counter(0));

    static final int THREADS = 4;
    static final int PER_THREAD = 100_000;

    public static void main(String[] args) throws InterruptedException {
        run(() -> {
            for (int i = 0; i < PER_THREAD; i++) {
                shared = shared.next();         // 読み取り→作成→書き込みの3段階
            }
        });
        run(() -> {
            for (int i = 0; i < PER_THREAD; i++) {
                atomic.updateAndGet(Counter::next);
            }
        });
        int expected = THREADS * PER_THREAD;
        int lost = shared.value();
        IO.println("期待する値: " + expected);
        IO.println("volatileで差し替え: " + (lost == expected ? "失われなかった" : "更新が失われた")
                + "（" + lost + "）");
        IO.println("AtomicReference.updateAndGet: " + atomic.get().value());
    }

    static void run(Runnable task) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int t = 0; t < THREADS; t++) {
            Thread th = new Thread(task);
            threads.add(th);
            th.start();
        }
        for (Thread th : threads) {
            th.join();
        }
    }
}
