// 第15章：Semaphoreの許可証の数と、許可証がないときのacquire()
import java.util.concurrent.Semaphore;

public class SemaphoreProbe {
    public static void main(String[] args) throws InterruptedException {
        Semaphore s = new Semaphore(3);
        IO.println("new Semaphore(3)の許可証: " + s.availablePermits());
        s.acquire();
        IO.println("acquire()を1回呼んだ後: " + s.availablePermits());
        s.acquire(2);
        IO.println("すべて受け取った後: " + s.availablePermits());
        Thread t = new Thread(() -> {
            try {
                s.acquire();
                s.release();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        t.start();
        for (int i = 0; i < 200 && t.getState() != Thread.State.WAITING; i++) {
            Thread.sleep(10);
        }
        IO.println("許可証がないときにacquire()したスレッド: " + t.getState());
        s.release();
        t.join(2000);
        IO.println("release()で1枚返した後: " + t.getState() + "、許可証: " + s.availablePermits());
    }
}
