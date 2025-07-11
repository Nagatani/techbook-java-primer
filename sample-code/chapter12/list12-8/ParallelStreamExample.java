/**
 * リスト12-8
 * ParallelStreamExampleクラス
 * 
 * 元ファイル: chapter12-advanced-collections.md (301行目)
 */

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
        
        System.out.println("シーケンシャル: " + seqTime.toMillis() + "ms");
        System.out.println("並列: " + parTime.toMillis() + "ms");
        System.out.println("高速化率: " + 
            (double)seqTime.toMillis() / parTime.toMillis() + "倍");
    }
}