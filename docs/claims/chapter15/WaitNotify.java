// 第15章：wait()・notify()・notifyAll()のふるまい
//   java WaitNotify
import java.util.concurrent.atomic.AtomicInteger;

public class WaitNotify {
    static final Object lock = new Object();
    static final AtomicInteger woke = new AtomicInteger();

    static Thread waiter() {
        Thread t = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                    woke.incrementAndGet();
                } catch (InterruptedException e) {
                    // 割り込まれたら終わる
                }
            }
        });
        t.setDaemon(true);
        t.start();
        return t;
    }

    static void untilWaiting(Thread... ts) throws InterruptedException {
        for (Thread t : ts) {
            while (t.getState() != Thread.State.WAITING) {
                Thread.sleep(10);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread a = waiter();
        untilWaiting(a);
        // aはsynchronized (lock)の中でwait()している。ロックを解放していれば、mainもロックを取れる
        synchronized (lock) {
            IO.println("wait()中のスレッドがいても、ほかのスレッドがロックを取れた");
        }
        Thread b = waiter();
        untilWaiting(b);
        synchronized (lock) {
            lock.notify();
        }
        Thread.sleep(300);
        IO.println("notify(): 2つ待っているうち、起きたのは" + woke.get() + "つ");
        synchronized (lock) {
            lock.notifyAll();
        }
        Thread.sleep(300);
        IO.println("notifyAll()の後: 起きたのは合わせて" + woke.get() + "つ");

        try {
            lock.wait(10);
        } catch (IllegalMonitorStateException e) {
            IO.println("ロックを持たずにwait(): " + e.getClass().getSimpleName());
        }
        try {
            lock.notify();
        } catch (IllegalMonitorStateException e) {
            IO.println("ロックを持たずにnotify(): " + e.getClass().getSimpleName());
        }
    }
}
