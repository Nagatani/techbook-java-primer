// 第15章：1つのロックを奪い合うと、スレッドを増やしても処理量は比例して伸びない
// 1スレッドで100万回と、4スレッドで100万回ずつ（合計400万回）のsynchronizedなcount++の時間を比べる
// 比例して伸びるなら、4スレッドの時間は1スレッドとほぼ同じになる
public class LockScaling {
    static int count;
    static final Object LOCK = new Object();

    static long run(int threads, int perThread) throws InterruptedException {
        Thread[] ts = new Thread[threads];
        long t0 = System.nanoTime();
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) {
                    synchronized (LOCK) {
                        count++;
                    }
                }
            });
            ts[i].start();
        }
        for (Thread t : ts) {
            t.join();
        }
        return System.nanoTime() - t0;
    }

    public static void main(String[] args) throws InterruptedException {
        run(1, 1_000_000);  // ウォームアップ
        run(4, 1_000_000);
        long one = run(1, 1_000_000);
        long four = run(4, 1_000_000);
        double ratio = (double) four / one;
        IO.println("4スレッド（4倍の仕事）÷1スレッドの時間: %.1f倍".formatted(ratio));
        IO.println(ratio >= 1.5 ? "比例して伸びない（4スレッドでも時間が縮まない）" : "ほぼ比例して伸びた");
    }
}
