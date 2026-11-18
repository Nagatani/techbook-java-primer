import java.util.concurrent.atomic.AtomicInteger;

/**
 * 仮想スレッドが、ブロックしている間に土台のプラットフォームスレッド（キャリア）を
 * 手放すかを観察する。キャリアを1つに制限し、4つの仮想スレッドが同時に眠っていられるかを数える。
 * 手放せば4つ同時に眠れる。手放せない（ピン留めされる）なら、同時に眠れるのは1つだけである。
 *
 *   java VirtualPinning synchronized   synchronizedの中でsleepする（JDK 24以降は手放す）
 *   java VirtualPinning clinit         クラスの初期化の中でsleepする（JDK 25でもピン留めされる）
 */
public class VirtualPinning {

    static final int THREADS = 4;
    static final AtomicInteger sleeping = new AtomicInteger();
    static final AtomicInteger maxSleeping = new AtomicInteger();

    /** 眠っている仮想スレッドの数を数えながら、200ミリ秒眠る */
    static void sleepAndCount() {
        maxSleeping.accumulateAndGet(sleeping.incrementAndGet(), Math::max);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        sleeping.decrementAndGet();
    }

    // クラスの初期化（static初期化子）の中で眠る4つのクラス
    static final class Init0 { static { sleepAndCount(); } static void touch() { } }
    static final class Init1 { static { sleepAndCount(); } static void touch() { } }
    static final class Init2 { static { sleepAndCount(); } static void touch() { } }
    static final class Init3 { static { sleepAndCount(); } static void touch() { } }

    public static void main(String[] args) throws InterruptedException {
        // 仮想スレッドを作る前に、キャリアを1つに制限する
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");
        String mode = args.length > 0 ? args[0] : "synchronized";

        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            int n = i;
            Object lock = new Object();
            Runnable task = switch (mode) {
                case "clinit" -> () -> {
                    switch (n) {
                        case 0 -> Init0.touch();
                        case 1 -> Init1.touch();
                        case 2 -> Init2.touch();
                        default -> Init3.touch();
                    }
                };
                default -> () -> {
                    synchronized (lock) {
                        sleepAndCount();
                    }
                };
            };
            threads[i] = Thread.ofVirtual().start(task);
        }
        for (Thread t : threads) {
            t.join();
        }
        IO.println(mode + "の中で同時に眠れた仮想スレッドの数（キャリアは1つ）: " + maxSleeping.get());
    }
}
