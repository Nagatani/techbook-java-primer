/**
 * リスト13-11
 * LambdaPerformanceクラス
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (423行目)
 */

public class LambdaPerformance {
    // ラムダ式のキャプチャによるメモリ影響
    public void demonstrateCapture() {
        // キャプチャなし - 静的にインスタンス化される
        Function<Integer, Integer> noCapture = x -> x * 2;
        
        // 実質的にfinal変数のキャプチャ
        int multiplier = 3;
        Function<Integer, Integer> withCapture = x -> x * multiplier;
        // 新しいインスタンスが生成される
        
        // パフォーマンステスト
        long start = System.nanoTime();
        IntStream.range(0, 1_000_000)
            .map(x -> x * 2)  // インライン化される
            .sum();
        long inlineTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        IntStream.range(0, 1_000_000)
            .map(noCapture::apply)  // メソッド参照
            .sum();
        long methodRefTime = System.nanoTime() - start;
        
        System.out.printf("Inline: %dms, Method ref: %dms%n",
            inlineTime / 1_000_000, 
            methodRefTime / 1_000_000);
    }
}