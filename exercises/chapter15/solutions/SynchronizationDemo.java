import java.util.concurrent.atomic.AtomicInteger;

/**
 * データ競合と同期（課題2の解答例）
 *
 * 「同期なし」の結果だけは、実行のたびに変わる（データ競合が起きるため）。
 */
public class SynchronizationDemo {
    static class UnsafeCounter {
        private int count;
        void increment() { count++; }
        int get() { return count; }
    }

    static class SafeCounter {
        private int count;
        synchronized void increment() { count++; }
        synchronized int get() { return count; }
    }

    static class BankAccount {
        private int balance;
        BankAccount(int balance) { this.balance = balance; }
        synchronized void deposit(int amount) { balance += amount; }
        synchronized boolean withdraw(int amount) {
            if (amount > balance) {
                return false;
            }
            balance -= amount;
            return true;
        }
        synchronized int getBalance() { return balance; }
    }

    static volatile boolean running = true;

    // 10個のスレッドで、それぞれ1000回taskを実行する
    static void runInThreads(Runnable task) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    task.run();
                }
            });
            threads[i].start();
        }
        for (Thread t : threads) {
            t.join();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        IO.println("--- カウンター（10スレッド×1000回） ---");
        UnsafeCounter unsafe = new UnsafeCounter();
        runInThreads(unsafe::increment);
        IO.println("同期なし: " + unsafe.get());
        SafeCounter safe = new SafeCounter();
        runInThreads(safe::increment);
        IO.println("synchronized: " + safe.get());
        AtomicInteger atomic = new AtomicInteger();
        runInThreads(atomic::incrementAndGet);
        IO.println("AtomicInteger: " + atomic.get());

        IO.println("\n--- 口座（10スレッドが1000回ずつ入金と出金） ---");
        BankAccount account = new BankAccount(10_000);
        runInThreads(() -> {
            account.deposit(300);
            account.withdraw(200);
        });
        IO.println("最終残高: " + account.getBalance() + "円（10000 + 100 × 10000回）");

        IO.println("\n--- volatileの停止フラグ ---");
        Thread worker = new Thread(() -> {
            while (running) {
                Thread.onSpinWait();
            }
            IO.println("作業スレッド: 停止の合図を受け取りました");
        });
        worker.start();
        Thread.sleep(100);
        running = false;
        worker.join();
        IO.println("メインスレッド: 作業スレッドが終わりました");
    }
}
