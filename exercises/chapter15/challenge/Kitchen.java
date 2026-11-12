import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 厨房。窓口から受けた注文を待ち行列に入れ、複数のコックが取り出して調理する
 */
public class Kitchen {
    /** 待ち行列に入れる1件の仕事。調理が終わったらdoneを完了させる */
    private record Job(Order order, CompletableFuture<Order> done) {
    }

    /** コックに仕事の終わりを知らせる目印（ポイズンピル） */
    private static final Job END = new Job(null, null);

    private final int cookCount;
    private final BlockingQueue<Job> queue;
    private final ExecutorService cooks;
    private final Semaphore stoves;
    private final AtomicInteger stovesInUse = new AtomicInteger();
    private final AtomicInteger maxStovesInUse = new AtomicInteger();
    private final AtomicInteger accepted = new AtomicInteger();
    private final AtomicInteger finished = new AtomicInteger();
    private final ConcurrentHashMap<Menu, Integer> sales = new ConcurrentHashMap<>();
    private boolean open = true;  // synchronizedのメソッドの中だけで読み書きする

    public Kitchen(int cookCount, int stoveCount, int queueCapacity) {
        this.cookCount = cookCount;
        this.queue = new ArrayBlockingQueue<>(queueCapacity);
        this.cooks = Executors.newFixedThreadPool(cookCount);
        this.stoves = new Semaphore(stoveCount);
        for (int i = 0; i < cookCount; i++) {
            cooks.submit(this::cookLoop);
        }
    }

    /**
     * 注文を受ける。待ち行列がいっぱいなら空くまで待つ。
     * 戻り値のCompletableFutureは、調理が終わると完了する
     *
     * @throws IllegalStateException 受付を終えたあとに呼ばれたとき
     */
    public synchronized CompletableFuture<Order> placeOrder(Order order) throws InterruptedException {
        if (!open) {
            throw new IllegalStateException("受付は終了しました");
        }
        CompletableFuture<Order> done = new CompletableFuture<>();
        queue.put(new Job(order, done));
        accepted.incrementAndGet();
        return done;
    }

    /** 受付を終え、コックが残りの注文を作り終えるのを待つ */
    public void close() throws InterruptedException {
        synchronized (this) {
            if (!open) {
                return;
            }
            open = false;
        }
        for (int i = 0; i < cookCount; i++) {
            queue.put(END);  // コックの人数分の目印を最後に入れる
        }
        cooks.shutdown();
        if (!cooks.awaitTermination(10, TimeUnit.SECONDS)) {
            cooks.shutdownNow();
        }
    }

    /** 1人のコックの仕事。目印を受け取るまで注文を取り出して調理する */
    private void cookLoop() {
        try {
            while (true) {
                Job job = queue.take();
                if (job == END) {
                    return;
                }
                cook(job.order().menu());
                sales.merge(job.order().menu(), 1, Integer::sum);
                finished.incrementAndGet();
                job.done().complete(job.order());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void cook(Menu menu) throws InterruptedException {
        if (!menu.needsStove()) {
            Thread.sleep(menu.getCookMillis());
            return;
        }
        stoves.acquire();  // コンロが空くまで待つ
        try {
            int now = stovesInUse.incrementAndGet();
            maxStovesInUse.accumulateAndGet(now, Math::max);
            Thread.sleep(menu.getCookMillis());
        } finally {
            stovesInUse.decrementAndGet();
            stoves.release();
        }
    }

    public int getAccepted() {
        return accepted.get();
    }

    public int getFinished() {
        return finished.get();
    }

    public int getMaxStovesInUse() {
        return maxStovesInUse.get();
    }

    /** メニューごとの販売数。売れていないメニューは0 */
    public int getSales(Menu menu) {
        return sales.getOrDefault(menu, 0);
    }
}
