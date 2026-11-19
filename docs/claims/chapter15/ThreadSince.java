// 第15章：Thread・synchronized・wait/notifyは最初の版（JDK 1.0）からある（--release 8でコンパイルできることを確かめる）
public class ThreadSince {
    private final Object lock = new Object();
    private boolean ready;

    void await() throws InterruptedException {
        synchronized (lock) {
            while (!ready) {
                lock.wait();
            }
        }
    }

    void signal() {
        synchronized (lock) {
            ready = true;
            lock.notifyAll();
        }
    }

    static Thread make(Runnable r) {
        return new Thread(r);
    }
}
