// 第15章：BlockingQueueのput・takeが待つことと、実装クラスごとの容量
//   java Queues blocking    満杯のキューへのputと、空のキューからのtakeで、スレッドが待つ
//   java Queues kinds       実装クラスごとの残りの容量と、PriorityBlockingQueueの取り出す順
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Queues {
    static Thread.State waitFor(Thread t, Thread.State want) throws InterruptedException {
        for (int i = 0; i < 200 && t.getState() != want; i++) {
            Thread.sleep(10);
        }
        return t.getState();
    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0 && args[0].equals("kinds")) {
            kinds();
            return;
        }
        BlockingQueue<Integer> full = new ArrayBlockingQueue<>(1);
        full.put(1);
        Thread producer = new Thread(() -> {
            try {
                full.put(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();
        IO.println("満杯のキューにputしたスレッド: " + waitFor(producer, Thread.State.WAITING));
        full.take();
        producer.join(2000);
        IO.println("1つ取り出した後: " + producer.getState() + "（キューの要素: " + full + "）");

        BlockingQueue<Integer> empty = new LinkedBlockingQueue<>();
        Integer[] got = new Integer[1];
        Thread consumer = new Thread(() -> {
            try {
                got[0] = empty.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();
        IO.println("空のキューからtakeしたスレッド: " + waitFor(consumer, Thread.State.WAITING));
        empty.put(42);
        consumer.join(2000);
        IO.println("1つ入れた後: " + consumer.getState() + "（受け取った値: " + got[0] + "）");

        SynchronousQueue<Integer> sync = new SynchronousQueue<>();
        Thread giver = new Thread(() -> {
            try {
                sync.put(7);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        giver.start();
        IO.println("SynchronousQueueにputしたスレッド（相手のtakeが来るまで）: " + waitFor(giver, Thread.State.WAITING));
        IO.println("SynchronousQueueのtakeで受け取った値: " + sync.take());
        giver.join(2000);
    }

    static void kinds() throws InterruptedException {
        IO.println("ArrayBlockingQueue(5)の残りの容量: " + new ArrayBlockingQueue<Integer>(5).remainingCapacity());
        IO.println("LinkedBlockingQueue()の残りの容量: " + cap(new LinkedBlockingQueue<Integer>()));
        IO.println("LinkedBlockingQueue(5)の残りの容量: " + new LinkedBlockingQueue<Integer>(5).remainingCapacity());
        IO.println("PriorityBlockingQueue()の残りの容量: " + cap(new PriorityBlockingQueue<Integer>()));
        IO.println("SynchronousQueueの残りの容量: " + new SynchronousQueue<Integer>().remainingCapacity()
                + "、offerの結果（相手がいないとき）: " + new SynchronousQueue<Integer>().offer(1));
        PriorityBlockingQueue<Integer> pq = new PriorityBlockingQueue<>();
        pq.put(30);
        pq.put(10);
        pq.put(20);
        IO.println("PriorityBlockingQueueに30・10・20を入れて取り出す順: " + pq.take() + "・" + pq.take() + "・" + pq.take());
    }

    static String cap(BlockingQueue<Integer> q) {
        int c = q.remainingCapacity();
        return c + (c == Integer.MAX_VALUE ? "（Integer.MAX_VALUE）" : "");
    }
}
