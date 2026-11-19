// 第15章：2つのスレッドがロックを逆の順に取るとデッドロックになり、同じ順に取ると起こらない
// 最初のロックを取ったあと100ミリ秒待ち、2つのスレッドがどちらも最初のロックを取ろうとする状況を作る
// デッドロックはThreadMXBean.findDeadlockedThreadsで検出する（スレッドはデーモンにして、止まってもプログラムが終われるようにする）
import java.lang.management.ManagementFactory;

public class Deadlock {
    static Thread worker(Object first, Object second) {
        Thread t = new Thread(() -> {
            synchronized (first) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
                synchronized (second) {
                    // 2つめのロックも取れた
                }
            }
        });
        t.setDaemon(true);
        return t;
    }

    static String trial(boolean sameOrder) throws InterruptedException {
        Object lock1 = new Object();
        Object lock2 = new Object();
        Thread a = worker(lock1, lock2);
        Thread b = sameOrder ? worker(lock1, lock2) : worker(lock2, lock1);
        a.start();
        b.start();
        a.join(2000);
        b.join(2000);
        long[] ids = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
        int n = ids == null ? 0 : ids.length;
        return (a.isAlive() || b.isAlive() ? "終わらない" : "2つとも終わった")
                + "（デッドロックになったスレッド: " + n + "個）";
    }

    public static void main(String[] args) throws InterruptedException {
        IO.println("同じ順（lock1→lock2）: " + trial(true));
        IO.println("逆の順: " + trial(false));
    }
}
