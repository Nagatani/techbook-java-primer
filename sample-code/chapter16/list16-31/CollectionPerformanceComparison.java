/**
 * リスト16-31
 * CollectionPerformanceComparisonクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2503行目)
 */

import java.util.*;
import java.util.concurrent.*;

public class CollectionPerformanceComparison {
    
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 10000;
        
        // 1. 同期化されたHashMap
        Map<Integer, String> syncMap = Collections.synchronizedMap(new HashMap<>());
        long syncTime = measurePerformance(syncMap, threadCount, operationsPerThread);
        
        // 2. ConcurrentHashMap
        Map<Integer, String> concurrentMap = new ConcurrentHashMap<>();
        long concurrentTime = measurePerformance(concurrentMap, threadCount, 
            operationsPerThread);
        
        System.out.println("=== パフォーマンス比較 ===");
        System.out.println("SynchronizedMap: " + syncTime + "ms");
        System.out.println("ConcurrentHashMap: " + concurrentTime + "ms");
        System.out.println("性能向上: " + 
            String.format("%.2fx", (double)syncTime / concurrentTime));
    }
    
    private static long measurePerformance(Map<Integer, String> map, 
                                          int threadCount, 
                                          int operationsPerThread) 
                                          throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.execute(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    int key = threadId * operationsPerThread + j;
                    map.put(key, "value" + key);
                    map.get(key);
                }
                latch.countDown();
            });
        }
        
        latch.await();
        long endTime = System.currentTimeMillis();
        executor.shutdown();
        
        return endTime - startTime;
    }
}