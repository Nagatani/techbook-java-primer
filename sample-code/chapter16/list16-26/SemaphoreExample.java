/**
 * リスト16-26
 * SemaphoreExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2247行目)
 */

import java.util.concurrent.Semaphore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SemaphoreExample {
    // 2. Semaphore - リソース数制限
    static class ResourcePool {
        private final Semaphore semaphore;
        private final boolean[] resources;
        
        public ResourcePool(int size) {
            this.semaphore = new Semaphore(size);
            this.resources = new boolean[size];
        }
        
        public int acquire() throws InterruptedException {
            semaphore.acquire();
            synchronized (resources) {
                for (int i = 0; i < resources.length; i++) {
                    if (!resources[i]) {
                        resources[i] = true;
                        System.out.println(Thread.currentThread().getName() + 
                            " がリソース " + i + " を取得");
                        return i;
                    }
                }
            }
            throw new IllegalStateException("リソースが見つかりません");
        }
        
        public void release(int resourceId) {
            synchronized (resources) {
                resources[resourceId] = false;
                System.out.println(Thread.currentThread().getName() + 
                    " がリソース " + resourceId + " を解放");
            }
            semaphore.release();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // Semaphoreのデモ
        System.out.println("=== Semaphore デモ ===");
        ResourcePool pool = new ResourcePool(3);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                try {
                    int resourceId = pool.acquire();
                    Thread.sleep(1000); // リソースを使用
                    pool.release(resourceId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}