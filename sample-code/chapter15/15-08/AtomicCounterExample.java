import java.util.concurrent.atomic.AtomicInteger;

public class AtomicCounterExample {
    // スレッドセーフなカウンター
    private final AtomicInteger counter = new AtomicInteger(0);
    
    public void performCounting() throws InterruptedException {
        Thread[] threads = new Thread[10];
        
        // 10スレッドが同時に1000回ずつインクリメント
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    // アトミックなインクリメント操作
                    int newValue = counter.incrementAndGet();
                    if (newValue % 1000 == 0) {
                        IO.println("Counter reached: " + newValue);
                    }
                }
            });
            threads[i].start();
        }
        
        // すべてのスレッドが終わるのを待つ
        for (Thread thread : threads) {
            thread.join();
        }
        
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
