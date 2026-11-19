// 第15章：AtomicInteger.incrementAndGetとsynchronizedのcount++の時間を、スレッドが1つのときと8つのときで比べる
// 1スレッドあたり200万回。それぞれ3回測り、いちばん短い時間を使う（環境で変わる。大小の関係だけを見る）
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicVsSync {
    static final AtomicInteger atomic = new AtomicInteger();
    static int count;
    static final Object LOCK = new Object();

    static long time(int threads, Runnable inc) throws InterruptedException {
        long best = Long.MAX_VALUE;
        for (int round = 0; round < 3; round++) {
            Thread[] ts = new Thread[threads];
            long t0 = System.nanoTime();
            for (int i = 0; i < threads; i++) {
                ts[i] = new Thread(() -> {
                    for (int j = 0; j < 2_000_000; j++) {
                        inc.run();
                    }
                });
                ts[i].start();
            }
            for (Thread t : ts) {
                t.join();
            }
            best = Math.min(best, System.nanoTime() - t0);
        }
        return best;
    }

    public static void main(String[] args) throws InterruptedException {
        Runnable a = atomic::incrementAndGet;
        Runnable s = () -> {
            synchronized (LOCK) {
                count++;
            }
        };
        time(1, a);
        time(1, s);
        long[] ta = new long[2];
        int k = 0;
        for (int n : new int[] {1, 8}) {
            ta[k] = time(n, a);
            long tsync = time(n, s);
            IO.println("%dスレッド: synchronizedの時間はAtomicIntegerの%.1f倍".formatted(n, (double) tsync / ta[k]));
            k++;
        }
        // 8スレッドは8倍の仕事をする。比例して伸びるなら、時間は1スレッドとほぼ同じになる
        double ratio = (double) ta[1] / ta[0];
        IO.println("AtomicInteger: 8スレッド（8倍の仕事）÷1スレッドの時間 %.1f倍".formatted(ratio));
        IO.println(ratio >= 1.5 ? "AtomicIntegerも、同じ変数を奪い合うと比例して伸びない" : "AtomicIntegerはほぼ比例して伸びた");
    }
}
