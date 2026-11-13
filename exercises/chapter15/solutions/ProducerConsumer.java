import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生産者と消費者（課題4の解答例）
 */
public class ProducerConsumer {
    private static final int END = -1; // 終わりの合図

    public static void main(String[] args) throws InterruptedException {
        IO.println("--- BlockingQueue ---");
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        int[] consumed = new int[2];
        int[] sums = new int[2];

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 100; i++) {
                    queue.put(i); // 満杯なら空くまで待つ
                }
                for (int i = 0; i < consumed.length; i++) {
                    queue.put(END); // 消費者の数だけ終わりの合図を送る
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        Thread[] consumers = new Thread[2];
        for (int c = 0; c < consumers.length; c++) {
            int id = c;
            consumers[c] = new Thread(() -> {
                try {
                    while (true) {
                        int item = queue.take(); // 空なら届くまで待つ
                        if (item == END) {
                            break;
                        }
                        consumed[id]++;
                        sums[id] += item;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            consumers[c].start();
        }
        producer.start();
        producer.join();
        for (Thread t : consumers) {
            t.join();
        }
        IO.println("消費した数の合計: " + (consumed[0] + consumed[1]));
        IO.println("値の合計: " + (sums[0] + sums[1]) + "（1〜100の和は5050）");
        IO.println("キューに残った数: " + queue.size());

        IO.println("\n--- Semaphoreで同時に使える数を制限 ---");
        Semaphore printers = new Semaphore(3);
        AtomicInteger using = new AtomicInteger();
        AtomicInteger maxUsing = new AtomicInteger();
        Thread[] users = new Thread[8];
        for (int i = 0; i < users.length; i++) {
            users[i] = new Thread(() -> {
                try {
                    printers.acquire();
                    try {
                        int now = using.incrementAndGet();
                        maxUsing.accumulateAndGet(now, Math::max);
                        Thread.sleep(50);
                        using.decrementAndGet();
                    } finally {
                        printers.release();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            users[i].start();
        }
        for (Thread t : users) {
            t.join();
        }
        IO.println("利用者: " + users.length + "人、プリンター: 3台");
        IO.println("同時に使った数は3以下: " + (maxUsing.get() <= 3));
        IO.println("使い終わったあとの空き: " + printers.availablePermits() + "台");
    }
}
