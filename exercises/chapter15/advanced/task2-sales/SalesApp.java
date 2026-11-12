import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SalesApp {
    public static void main(String[] args) throws InterruptedException {
        // 店舗名とファイル。表示の順序はこの順に固定する
        Map<String, Path> stores = new LinkedHashMap<>();
        stores.put("本店", Path.of("data", "honten.csv"));
        stores.put("駅前店", Path.of("data", "ekimae.csv"));
        stores.put("大学店", Path.of("data", "daigaku.csv"));

        ExecutorService executor = Executors.newFixedThreadPool(3);
        try {
            summarizeWithFuture(stores, executor);
            IO.println("");
            stores.put("公園店", Path.of("data", "kouen.csv"));  // ファイルがない店舗
            summarizeWithCompletableFuture(stores, executor);
        } finally {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    /** ExecutorServiceにCallableを渡し、Futureで結果を受け取る */
    private static void summarizeWithFuture(Map<String, Path> stores, ExecutorService executor)
            throws InterruptedException {
        IO.println("=== Futureによる店舗ごとの集計 ===");
        ConcurrentHashMap<String, Integer> itemCounts = new ConcurrentHashMap<>();

        // すべての店舗のタスクを先に投入してから、結果を順に受け取る
        List<Future<StoreResult>> futures = new ArrayList<>();
        for (Map.Entry<String, Path> e : stores.entrySet()) {
            futures.add(executor.submit(new SalesTask(e.getKey(), e.getValue(), itemCounts)));
        }

        int grandTotal = 0;
        for (Future<StoreResult> future : futures) {
            try {
                StoreResult result = future.get();  // そのタスクが終わるまで待つ
                grandTotal += result.total();
                String note = result.skippedLines() > 0
                        ? "（読めない行 " + result.skippedLines() + "行）" : "";
                IO.println("  %s: %,d円%s".formatted(result.store(), result.total(), note));
            } catch (ExecutionException e) {
                IO.println("  集計に失敗しました: " + e.getCause());
            }
        }
        IO.println("  合計: %,d円".formatted(grandTotal));

        IO.println("");
        IO.println("=== 商品ごとの販売数（多い順） ===");
        itemCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .forEach(e -> IO.println("  %s %d個".formatted(e.getKey(), e.getValue())));
    }

    /** CompletableFutureで集計し、失敗した店舗は0円として扱う */
    private static void summarizeWithCompletableFuture(Map<String, Path> stores, ExecutorService executor) {
        IO.println("=== CompletableFutureによる集計 ===");
        ConcurrentHashMap<String, Integer> itemCounts = new ConcurrentHashMap<>();

        List<CompletableFuture<StoreResult>> futures = new ArrayList<>();
        for (Map.Entry<String, Path> e : stores.entrySet()) {
            String store = e.getKey();
            SalesTask task = new SalesTask(store, e.getValue(), itemCounts);
            CompletableFuture<StoreResult> future = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return task.call();
                        } catch (Exception ex) {
                            throw new CompletionException(ex);
                        }
                    }, executor)
                    .exceptionally(ex -> {
                        IO.println("  " + store + ": ファイルを読めませんでした（0円として扱います）");
                        return new StoreResult(store, 0, 0);
                    });
            futures.add(future);
        }

        // すべての店舗が終わってから合計を求める
        CompletableFuture<Integer> total = CompletableFuture
                .allOf(futures.toArray(new CompletableFuture<?>[0]))
                .thenApply(v -> futures.stream().mapToInt(f -> f.join().total()).sum());

        StoreResult best = futures.stream()
                .map(CompletableFuture::join)
                .max(Comparator.comparingInt(StoreResult::total))
                .orElseThrow();
        IO.println("  集計した店舗: " + futures.size() + "店");
        IO.println("  合計: %,d円".formatted(total.join()));
        IO.println("  売上がいちばん多い店舗: %s（%,d円）".formatted(best.store(), best.total()));
    }
}
