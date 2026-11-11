import java.util.concurrent.*;
import java.util.Arrays;
import java.util.Random;

public class ParallelPerformanceExample {
    
    // 大規模配列の並列ソート
    public static void parallelSortBenchmark() {
        int size = 10_000_000;
        int[] array1 = new int[size];
        int[] array2 = new int[size];
        Random random = new Random();
        
        // ランダムデータの生成
        for (int i = 0; i < size; i++) {
            int value = random.nextInt();
            array1[i] = value;
            array2[i] = value;
        }
        
        // シングルスレッドソート
        long startTime = System.currentTimeMillis();
        Arrays.sort(array1);
        long singleThreadTime = System.currentTimeMillis() - startTime;
        
        // 並列ソート
        startTime = System.currentTimeMillis();
        Arrays.parallelSort(array2);
        long parallelTime = System.currentTimeMillis() - startTime;
        
        IO.println("=== ソートベンチマーク ===");
        IO.println("配列サイズ: " + size);
        IO.println("シングルスレッド: " + singleThreadTime + "ms");
        IO.println("並列処理: " + parallelTime + "ms");
        IO.println("性能向上: " + 
            String.format("%.2fx", (double)singleThreadTime / parallelTime));
    }
    
    // ForkJoinPoolを使った再帰的並列処理
    static class RecursiveSum extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;
        private final long[] array;
        private final int start, end;
        
        public RecursiveSum(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                // 小さなタスクは直接計算
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            } else {
                // 大きなタスクは分割
                int mid = (start + end) / 2;
                RecursiveSum left = new RecursiveSum(array, start, mid);
                RecursiveSum right = new RecursiveSum(array, mid, end);
                
                left.fork(); // 左側を非同期実行
                long rightResult = right.compute(); // 右側を現在のスレッドで実行
                long leftResult = left.join(); // 左側の結果を待つ
                
                return leftResult + rightResult;
            }
        }
    }
    
    public static void forkJoinExample() {
        int size = 100_000_000;
        long[] array = new long[size];
        Arrays.fill(array, 1);
        
        ForkJoinPool pool = new ForkJoinPool();
        
        long startTime = System.currentTimeMillis();
        Long result = pool.invoke(new RecursiveSum(array, 0, size));
        long time = System.currentTimeMillis() - startTime;
        
        IO.println("\n=== ForkJoinPool ベンチマーク ===");
        IO.println("要素数: " + size);
        IO.println("計算結果: " + result);
        IO.println("処理時間: " + time + "ms");
        IO.println("使用スレッド数: " + pool.getParallelism());
    }
    
    public static void main(String[] args) {
        // CPUコア数の確認
        int processors = Runtime.getRuntime().availableProcessors();
        IO.println("利用可能なCPUコア数: " + processors);
        
        parallelSortBenchmark();
        forkJoinExample();
    }
}
