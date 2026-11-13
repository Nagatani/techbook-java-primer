import java.util.stream.IntStream;
import java.time.Duration;
import java.time.Instant;

public class ParallelStreamExample {
    public static void main(String[] args) {
        int size = 100_000_000;
        
        // シーケンシャル処理の計測
        Instant start = Instant.now();
        double sumSeq = IntStream.range(0, size)
            .mapToDouble(Math::sqrt)
            .sum();
        Duration seqTime = Duration.between(start, Instant.now());
        
        // 並列処理の計測
        start = Instant.now();
        double sumPar = IntStream.range(0, size)
            .parallel()
            .mapToDouble(Math::sqrt)
            .sum();
        Duration parTime = Duration.between(start, Instant.now());
        
        IO.println("シーケンシャル: " + seqTime.toMillis() + "ms");
        IO.println("並列: " + parTime.toMillis() + "ms");
        IO.println("高速化率: %.1f倍".formatted(
            (double) seqTime.toMillis() / parTime.toMillis()));
    }
}
