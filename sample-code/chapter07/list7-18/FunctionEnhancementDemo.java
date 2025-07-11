/**
 * リスト7-18
 * FunctionEnhancementDemoクラス
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (737行目)
 */

@FunctionalInterface
interface EnhancedFunction<T, R> extends Function<T, R> {
    
    // Function<T, R>のapplyメソッドは抽象メソッド
    
    // 条件付き適用
    default Function<T, R> when(Predicate<T> condition, R defaultValue) {
        return t -> condition.test(t) ? this.apply(t) : defaultValue;
    }
    
    // リトライ機能
    default Function<T, R> withRetry(int maxAttempts) {
        return t -> {
            Exception lastException = null;
            for (int i = 0; i < maxAttempts; i++) {
                try {
                    return this.apply(t);
                } catch (Exception e) {
                    lastException = e;
                    try {
                        Thread.sleep(100 * (i + 1));  // 指数バックオフ
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            throw new RuntimeException("Failed after " + maxAttempts + " attempts", lastException);
        };
    }
    
    // メモ化（結果のキャッシュ）
    default Function<T, R> memoized() {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return t -> cache.computeIfAbsent(t, this::apply);
    }
    
    // 実行時間計測
    default Function<T, R> timed(Consumer<Long> timeConsumer) {
        return t -> {
            long start = System.nanoTime();
            try {
                return this.apply(t);
            } finally {
                timeConsumer.accept(System.nanoTime() - start);
            }
        };
    }
}

// 使用例
public class FunctionEnhancementDemo {
    public static void main(String[] args) {
        // 高価な計算を行う関数
        EnhancedFunction<Integer, Double> expensiveComputation = n -> {
            System.out.println("計算中: " + n);
            return Math.sqrt(n) * Math.PI;
        };
        
        // 機能を組み合わせて使用
        Function<Integer, Double> optimizedFunction = expensiveComputation
            .when(n -> n > 0, -1.0)  // 正の数のみ計算
            .memoized()              // 結果をキャッシュ
            .timed(time -> System.out.println("実行時間: " + time + "ns"));
        
        // 同じ値で複数回呼び出し
        System.out.println(optimizedFunction.apply(16));  // 計算実行
        System.out.println(optimizedFunction.apply(16));  // キャッシュから取得
        System.out.println(optimizedFunction.apply(-5));  // デフォルト値を返す
    }
}