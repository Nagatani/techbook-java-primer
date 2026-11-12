import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 1店舗の売上ファイルを読んで集計するタスク。別のスレッドで実行される
 */
public class SalesTask implements Callable<StoreResult> {
    private final String store;
    private final Path file;
    private final ConcurrentHashMap<String, Integer> itemCounts;  // 全店舗で共有する商品ごとの個数

    public SalesTask(String store, Path file, ConcurrentHashMap<String, Integer> itemCounts) {
        this.store = store;
        this.file = file;
        this.itemCounts = itemCounts;
    }

    @Override
    public StoreResult call() throws IOException {
        List<String> lines = Files.readAllLines(file);
        int total = 0;
        int skipped = 0;
        for (String line : lines.subList(1, lines.size())) {  // 1行目は見出し
            String[] fields = line.split(",");
            try {
                String item = fields[0];
                int price = Integer.parseInt(fields[1]);
                int quantity = Integer.parseInt(fields[2]);
                total += price * quantity;
                // 複数のスレッドから同じキーを更新しても、mergeは1回の操作として行われる
                itemCounts.merge(item, quantity, Integer::sum);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                skipped++;
            }
        }
        return new StoreResult(store, total, skipped);
    }
}
