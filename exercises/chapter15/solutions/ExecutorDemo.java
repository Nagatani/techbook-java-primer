import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * スレッドプールと非同期の処理（課題3の解答例）
 */
public class ExecutorDemo {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        IO.println("--- ExecutorServiceとCallable ---");
        ExecutorService pool = Executors.newFixedThreadPool(3);
        List<Future<Integer>> futures = new ArrayList<>();
        int[] limits = {10, 100, 1000, 10000};
        for (int limit : limits) {
            // 1からlimitまでの素数の個数を数える
            futures.add(pool.submit(() -> countPrimes(limit)));
        }
        // 投入した順にgetするので、表示の順序は決まる
        for (int i = 0; i < limits.length; i++) {
            IO.println(limits[i] + "以下の素数: " + futures.get(i).get() + "個");
        }

        IO.println("\n--- ConcurrentHashMapで集計 ---");
        String[][] baskets = {
            {"りんご", "みかん", "りんご"},
            {"ぶどう", "りんご"},
            {"みかん", "みかん", "ぶどう"},
        };
        ConcurrentHashMap<String, Integer> counts = new ConcurrentHashMap<>();
        List<Future<?>> jobs = new ArrayList<>();
        for (String[] basket : baskets) {
            jobs.add(pool.submit(() -> {
                for (String fruit : basket) {
                    counts.merge(fruit, 1, Integer::sum);
                }
            }));
        }
        for (Future<?> job : jobs) {
            job.get();
        }
        IO.println("集計（名前順）: " + new TreeMap<>(counts));

        pool.shutdown();
        IO.println("プールの終了を待つ: " + pool.awaitTermination(5, TimeUnit.SECONDS));

        IO.println("\n--- CompletableFutureで処理をつなぐ ---");
        int total = CompletableFuture
                .supplyAsync(() -> List.of(1, 2, 3, 4, 5))
                .thenApply(list -> list.stream().map(n -> n * 2).toList())
                .thenApply(list -> {
                    IO.println("2倍にした値: " + list);
                    return list.stream().mapToInt(Integer::intValue).sum();
                })
                .join();
        IO.println("合計: " + total);

        CompletableFuture<String> price = CompletableFuture.supplyAsync(() -> "1200円");
        CompletableFuture<String> stock = CompletableFuture.supplyAsync(() -> "在庫あり");
        IO.println("2つの結果を合わせる: " + price.thenCombine(stock, (p, s) -> p + "・" + s).join());
    }

    static int countPrimes(int limit) {
        int count = 0;
        for (int n = 2; n <= limit; n++) {
            boolean prime = true;
            for (int d = 2; d * d <= n; d++) {
                if (n % d == 0) {
                    prime = false;
                    break;
                }
            }
            if (prime) {
                count++;
            }
        }
        return count;
    }
}
