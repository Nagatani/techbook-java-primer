// 第12章：要素ごとの処理が軽いとき、要素数によって並列ストリームが速くなるか
import java.util.stream.LongStream;

public class ParallelSmall {
    static long best(Runnable r) {
        long best = Long.MAX_VALUE;
        for (int i = 0; i < 30; i++) {
            long t = System.nanoTime();
            r.run();
            best = Math.min(best, System.nanoTime() - t);
        }
        return best;
    }

    static long sink;

    public static void main(String[] args) {
        for (int n : new int[] {5_000, 50_000, 5_000_000}) {
            long seq = best(() -> sink += LongStream.range(0, n).map(x -> x * x).sum());
            long par = best(() -> sink += LongStream.range(0, n).parallel().map(x -> x * x).sum());
            System.out.println(n + " elements: " + (par < seq ? "parallel faster" : "sequential faster")
                    + " (seq " + seq / 1000 + "us, par " + par / 1000 + "us)");
        }
    }
}
