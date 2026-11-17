import java.util.stream.IntStream;

public class ParallelStreamExample {
    public static void main(String[] args) {
        int size = 100_000_000;

        // シーケンシャル処理の計測（経過時間はナノ秒で測る）
        long start = System.nanoTime();
        double sumSeq = IntStream.range(0, size)
            .mapToDouble(Math::sqrt)
            .sum();
        long seqTime = (System.nanoTime() - start) / 1_000_000;

        // 並列処理の計測
        start = System.nanoTime();
        double sumPar = IntStream.range(0, size)
            .parallel()
            .mapToDouble(Math::sqrt)
            .sum();
        long parTime = (System.nanoTime() - start) / 1_000_000;

        IO.println("シーケンシャル: " + seqTime + "ms");
        IO.println("並列: " + parTime + "ms");
        IO.println("高速化率: %.1f倍".formatted(
            (double) seqTime / parTime));
    }
}
