import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AtomicCounterExample {
    // スレッドセーフなカウンター
    private final AtomicInteger counter = new AtomicInteger(0);
    
    public void performCounting() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // 10スレッドが同時に1000回ずつインクリメント
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                for (int j = 0; j < 1000; j++) {
                    // アトミックなインクリメント操作
                    int newValue = counter.incrementAndGet();
                    if (newValue % 1000 == 0) {
                        IO.println("Counter reached: " + newValue);
                    }
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        IO.println("Final counter value: " + counter.get());
        // 常に正確に10,000になる
    }
    
    // その他の便利なアトミック操作
    public void demonstrateAtomicOperations() {
        AtomicInteger value = new AtomicInteger(10);
        
        // 現在値を取得して、新しい値を設定
        int oldValue = value.getAndSet(20);
        IO.println("Old: " + oldValue + ", New: " + value.get());
        
        // 条件付き更新（CAS操作）
        boolean updated = value.compareAndSet(20, 30);
        IO.println("Updated: " + updated + ", Value: " + value.get());
        
        // 加算操作
        int result = value.addAndGet(5);
        IO.println("After adding 5: " + result);
    }
}
