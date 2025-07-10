## 第16章 マルチスレッドプログラミング - Part C: 並行コレクションと同期プリミティブ

### 並行コレクションの活用

通常のコレクション（ArrayList、HashMap等）は、複数のスレッドから同時にアクセスされることを想定していません。Javaは、並行処理に対応した特別なコレクションを提供しています。

#### ConcurrentHashMap

```java
import java.util.concurrent.*;
import java.util.Map;

public class ConcurrentCollectionsExample {
    
    public static void main(String[] args) throws InterruptedException {
        // 1. ConcurrentHashMap - スレッドセーフなマップ
        ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<>();
        String[] words = {"apple", "banana", "apple", "cherry", "banana", "apple"};
        
        // 複数スレッドで単語をカウント
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (String word : words) {
            executor.execute(() -> {
                wordCount.merge(word, 1, Integer::sum);
                System.out.println(Thread.currentThread().getName() + 
                    " counted " + word + ": " + wordCount.get(word));
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("\n単語カウント結果: " + wordCount);
    }
}
```

#### CopyOnWriteArrayList

```java
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class CopyOnWriteExample {
    public static void main(String[] args) throws InterruptedException {
        // 2. CopyOnWriteArrayList - 読み取り最適化リスト
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.addAll(List.of("初期値1", "初期値2", "初期値3"));
        
        // 読み取りスレッド（高速・ロックなし）
        Runnable reader = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("読み取り: " + list);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        // 書き込みスレッド（コピーが発生）
        Runnable writer = () -> {
            for (int i = 0; i < 3; i++) {
                list.add("新規追加" + i);
                System.out.println("書き込み: 新規追加" + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        Thread readerThread = new Thread(reader);
        Thread writerThread = new Thread(writer);
        readerThread.start();
        writerThread.start();
        readerThread.join();
        writerThread.join();
    }
}
```

#### BlockingQueue と プロデューサー・コンシューマーパターン

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerExample {
    // 共有キュー
    private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
    private static final AtomicInteger producedCount = new AtomicInteger(0);
    private static final AtomicInteger consumedCount = new AtomicInteger(0);
    
    // プロデューサー（生産者）
    static class Producer implements Runnable {
        private final String name;
        private final int itemsToProduce;
        
        public Producer(String name, int itemsToProduce) {
            this.name = name;
            this.itemsToProduce = itemsToProduce;
        }
        
        @Override
        public void run() {
            try {
                for (int i = 0; i < itemsToProduce; i++) {
                    int item = producedCount.incrementAndGet();
                    queue.put(item); // キューがいっぱいの場合はブロック
                    System.out.printf("%s produced: %d (queue size: %d)%n", 
                        name, item, queue.size());
                    Thread.sleep(100); // 生産に時間がかかることをシミュレート
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // コンシューマー（消費者）
    static class Consumer implements Runnable {
        private final String name;
        private final int itemsToConsume;
        
        public Consumer(String name, int itemsToConsume) {
            this.name = name;
            this.itemsToConsume = itemsToConsume;
        }
        
        @Override
        public void run() {
            try {
                for (int i = 0; i < itemsToConsume; i++) {
                    Integer item = queue.take(); // キューが空の場合はブロック
                    consumedCount.incrementAndGet();
                    System.out.printf("%s consumed: %d (queue size: %d)%n", 
                        name, item, queue.size());
                    Thread.sleep(200); // 消費に時間がかかることをシミュレート
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 2つのプロデューサーと3つのコンシューマーを作成
        executor.execute(new Producer("Producer-1", 10));
        executor.execute(new Producer("Producer-2", 10));
        executor.execute(new Consumer("Consumer-1", 7));
        executor.execute(new Consumer("Consumer-2", 7));
        executor.execute(new Consumer("Consumer-3", 6));
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        System.out.println("\n=== 最終結果 ===");
        System.out.println("生産された総数: " + producedCount.get());
        System.out.println("消費された総数: " + consumedCount.get());
        System.out.println("キューの残り: " + queue.size());
    }
}
```

### 高度な同期プリミティブ

#### ReadWriteLock - 読み取り/書き込みロック

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class AdvancedSynchronizationExample {
    
    // 1. ReadWriteLock - 読み取り/書き込みロック
    static class SharedResource {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock readLock = lock.readLock();
        private final Lock writeLock = lock.writeLock();
        private String data = "初期データ";
        
        public String read() {
            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 読み取り開始");
                Thread.sleep(100); // 読み取り処理をシミュレート
                return data;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } finally {
                System.out.println(Thread.currentThread().getName() + " 読み取り完了");
                readLock.unlock();
            }
        }
        
        public void write(String newData) {
            writeLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 書き込み開始");
                Thread.sleep(500); // 書き込み処理をシミュレート
                data = newData;
                System.out.println(Thread.currentThread().getName() + " 書き込み完了: " + newData);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writeLock.unlock();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        // ReadWriteLockのデモ
        System.out.println("=== ReadWriteLock デモ ===");
        SharedResource resource = new SharedResource();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 複数の読み取りスレッド（並行実行可能）
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                String data = resource.read();
                System.out.println("読み取りデータ: " + data);
            });
        }
        
        // 書き込みスレッド（排他実行）
        executor.execute(() -> resource.write("更新データ"));
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
```

#### Semaphore - リソース数制限

```java
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
```

#### CountDownLatch - カウントダウン同期

```java
import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
    // 3. CountDownLatch - カウントダウン同期
    static class RaceExample {
        public static void demonstrate() throws InterruptedException {
            int runnerCount = 5;
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch finishSignal = new CountDownLatch(runnerCount);
            
            for (int i = 0; i < runnerCount; i++) {
                int runnerId = i + 1;
                new Thread(() -> {
                    try {
                        System.out.println("ランナー " + runnerId + " 準備完了");
                        startSignal.await(); // スタート信号を待つ
                        
                        System.out.println("ランナー " + runnerId + " スタート！");
                        Thread.sleep((long)(Math.random() * 3000)); // 走行時間
                        System.out.println("ランナー " + runnerId + " ゴール！");
                        
                        finishSignal.countDown(); // ゴールしたことを通知
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            Thread.sleep(1000);
            System.out.println("位置について... よーい...");
            Thread.sleep(1000);
            System.out.println("ドン！");
            startSignal.countDown(); // 全ランナーをスタートさせる
            
            finishSignal.await(); // 全ランナーのゴールを待つ
            System.out.println("レース終了！");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // CountDownLatchのデモ
        System.out.println("=== CountDownLatch デモ ===");
        RaceExample.demonstrate();
    }
}
```

#### CyclicBarrier - 循環バリア

```java
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class CyclicBarrierExample {
    
    public static void main(String[] args) {
        int workerCount = 3;
        
        // バリアアクション：全員が到達したときに実行される
        Runnable barrierAction = () -> 
            System.out.println("=== 全員が到達しました！次のフェーズへ ===");
        
        CyclicBarrier barrier = new CyclicBarrier(workerCount, barrierAction);
        
        // ワーカースレッド
        for (int i = 0; i < workerCount; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    for (int phase = 1; phase <= 3; phase++) {
                        // 各フェーズの作業
                        System.out.printf("Worker %d: フェーズ %d 開始%n", 
                            workerId, phase);
                        Thread.sleep((long)(Math.random() * 2000));
                        System.out.printf("Worker %d: フェーズ %d 完了%n", 
                            workerId, phase);
                        
                        // バリアで待機
                        barrier.await();
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
```

#### Exchanger - データ交換

```java
import java.util.concurrent.Exchanger;

public class ExchangerExample {
    
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();
        
        // プロデューサースレッド
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String data = "データ" + i;
                    System.out.println("Producer: " + data + " を送信");
                    String response = exchanger.exchange(data);
                    System.out.println("Producer: " + response + " を受信");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // コンシューマースレッド
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String received = exchanger.exchange("ACK" + i);
                    System.out.println("Consumer: " + received + " を受信");
                    System.out.println("Consumer: ACK" + i + " を送信");
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
    }
}
```

#### wait/notify を使った低レベル同期

```java
public class WaitNotifyExample {
    private static final Object lock = new Object();
    private static boolean dataReady = false;
    private static String sharedData = null;
    
    public static void main(String[] args) {
        // プロデューサー
        Thread producer = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Producer: データ生成中...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                sharedData = "重要なデータ";
                dataReady = true;
                System.out.println("Producer: データ生成完了、通知送信");
                lock.notify(); // 待機中のスレッドに通知
            }
        });
        
        // コンシューマー
        Thread consumer = new Thread(() -> {
            synchronized (lock) {
                while (!dataReady) {
                    System.out.println("Consumer: データ待機中...");
                    try {
                        lock.wait(); // データが準備されるまで待機
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("Consumer: データ受信: " + sharedData);
            }
        });
        
        consumer.start();
        producer.start();
    }
}
```

#### 並行コレクションの性能比較

```java
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
```

### まとめ

このパートでは、並行コレクションと高度な同期プリミティブについて学習しました：

- **並行コレクション**：ConcurrentHashMap、CopyOnWriteArrayList、BlockingQueue
- **プロデューサー・コンシューマーパターン**：BlockingQueueを使った実装
- **高度な同期プリミティブ**：
  - ReadWriteLock：読み取り/書き込みの最適化
  - Semaphore：リソース数の制限
  - CountDownLatch：複数スレッドの同期
  - CyclicBarrier：循環的な同期ポイント
  - Exchanger：スレッド間のデータ交換
- **低レベル同期**：wait/notifyメカニズム

これらのツールを適切に使い分けることで、効率的で安全な並行処理プログラムを作成できます。次のパートでは、実践的な並行処理のパターンと落とし穴について学習します。

---

次のパート：[Part D - 実践的な並行処理](chapter16d-practical-concurrency.md)
