// 第15章：競合状態を観察する
//   java Race atm      残高を読んでから書き戻すまでに、もう1つのスレッドが割り込む（割り込む位置をsleepで固定する）
//   java Race counter  10個のスレッドが1,000回ずつcount++する（同期なし）。何度か繰り返し、10,000にならない回があるかを見る
import java.util.concurrent.CountDownLatch;

public class Race {
    static int balance = 10_000;
    static int withdrawn = 0;

    static void withdraw(int amount) {
        int current = balance;           // 残高を読む
        sleep(100);                      // 読んでから書き戻すまでのあいだに、ほかのスレッドが動く
        if (current >= amount) {
            balance = current - amount;  // 計算した残高を書き戻す
            synchronized (Race.class) {
                withdrawn += amount;
            }
        }
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static int count;

    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0 && args[0].equals("counter")) {
            counter();
            return;
        }
        Thread atm1 = new Thread(() -> withdraw(8_000));
        Thread atm2 = new Thread(() -> withdraw(8_000));
        atm1.start();
        atm2.start();
        atm1.join();
        atm2.join();
        IO.println("引き出した合計: " + withdrawn + "円、残高: " + balance + "円");
    }

    static void counter() throws InterruptedException {
        int rounds = 200;
        int lost = 0;
        int min = Integer.MAX_VALUE;
        for (int r = 0; r < rounds; r++) {
            count = 0;
            CountDownLatch go = new CountDownLatch(1);
            Thread[] ts = new Thread[10];
            for (int i = 0; i < ts.length; i++) {
                ts[i] = new Thread(() -> {
                    try {
                        go.await();
                    } catch (InterruptedException e) {
                        return;
                    }
                    for (int j = 0; j < 1000; j++) {
                        count++;
                    }
                });
                ts[i].start();
            }
            go.countDown();
            for (Thread t : ts) {
                t.join();
            }
            if (count < 10_000) {
                lost++;
                min = Math.min(min, count);
            }
        }
        IO.println(lost > 0
                ? rounds + "回のうち、10,000より小さくなった回がある"
                : rounds + "回とも10,000だった");
    }
}
