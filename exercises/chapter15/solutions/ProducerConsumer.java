import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 生産者・消費者パターンの実装
 * 複数の実装方式とパフォーマンス比較
 */
public class ProducerConsumer {
    
    private static final Logger logger = Logger.getLogger(ProducerConsumer.class.getName());
    
    /**
     * タスクを表すクラス
     */
    public static class Task {
        private final String id;
        private final String data;
        private final long createdAt;
        private volatile long processedAt;
        
        public Task(String id, String data) {
            this.id = id;
            this.data = data;
            this.createdAt = System.currentTimeMillis();
        }
        
        public String getId() { return id; }
        public String getData() { return data; }
        public long getCreatedAt() { return createdAt; }
        public long getProcessedAt() { return processedAt; }
        
        public void markProcessed() {
            this.processedAt = System.currentTimeMillis();
        }
        
        public long getProcessingTime() {
            return processedAt > 0 ? processedAt - createdAt : -1;
        }
        
        @Override
        public String toString() {
            return String.format("Task[id=%s, data=%s, created=%s]", 
                id, data, LocalDateTime.ofEpochSecond(createdAt / 1000, 0, java.time.ZoneOffset.UTC)
                    .format(DateTimeFormatter.ISO_LOCAL_TIME));
        }
    }
    
    /**
     * 統計情報クラス
     */
    public static class Statistics {
        private final AtomicLong producedCount = new AtomicLong(0);
        private final AtomicLong consumedCount = new AtomicLong(0);
        private final AtomicLong totalProcessingTime = new AtomicLong(0);
        private final long startTime = System.currentTimeMillis();
        
        public void recordProduced() {
            producedCount.incrementAndGet();
        }
        
        public void recordConsumed(long processingTime) {
            consumedCount.incrementAndGet();
            if (processingTime > 0) {
                totalProcessingTime.addAndGet(processingTime);
            }
        }
        
        public long getProducedCount() { return producedCount.get(); }
        public long getConsumedCount() { return consumedCount.get(); }
        public long getQueueSize() { return producedCount.get() - consumedCount.get(); }
        public double getAverageProcessingTime() {
            long consumed = consumedCount.get();
            return consumed > 0 ? (double) totalProcessingTime.get() / consumed : 0.0;
        }
        
        public long getUptime() {
            return System.currentTimeMillis() - startTime;
        }
        
        public double getThroughput() {
            long uptime = getUptime();
            return uptime > 0 ? (double) consumedCount.get() / (uptime / 1000.0) : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format(
                "Statistics[produced=%d, consumed=%d, queue=%d, avgProcessTime=%.2fms, throughput=%.2f/sec]",
                getProducedCount(), getConsumedCount(), getQueueSize(), 
                getAverageProcessingTime(), getThroughput());
        }
    }
    
    /**
     * BlockingQueueを使用した実装
     */
    public static class BlockingQueueProducerConsumer {
        private final BlockingQueue<Task> queue;
        private final ExecutorService producerExecutor;
        private final ExecutorService consumerExecutor;
        private final AtomicBoolean running = new AtomicBoolean(false);
        private final Statistics statistics = new Statistics();
        
        public BlockingQueueProducerConsumer(int queueCapacity, int producerThreads, int consumerThreads) {
            this.queue = new ArrayBlockingQueue<>(queueCapacity);
            this.producerExecutor = Executors.newFixedThreadPool(producerThreads);
            this.consumerExecutor = Executors.newFixedThreadPool(consumerThreads);
        }
        
        public void start(Supplier<Task> taskProducer, Consumer<Task> taskConsumer, 
                         int productionRate, long durationMs) {
            if (running.getAndSet(true)) {
                throw new IllegalStateException("既に実行中です");
            }
            
            long endTime = System.currentTimeMillis() + durationMs;
            
            // プロデューサータスクを開始
            producerExecutor.submit(() -> {
                while (running.get() && System.currentTimeMillis() < endTime) {
                    try {
                        Task task = taskProducer.get();
                        if (task != null) {
                            queue.put(task); // ブロッキング
                            statistics.recordProduced();
                        }
                        
                        if (productionRate > 0) {
                            Thread.sleep(1000 / productionRate);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "プロデューサーエラー", e);
                    }
                }
            });
            
            // コンシューマータスクを開始
            for (int i = 0; i < ((ThreadPoolExecutor) consumerExecutor).getCorePoolSize(); i++) {
                consumerExecutor.submit(() -> {
                    while (running.get() || !queue.isEmpty()) {
                        try {
                            Task task = queue.poll(100, TimeUnit.MILLISECONDS);
                            if (task != null) {
                                taskConsumer.accept(task);
                                task.markProcessed();
                                statistics.recordConsumed(task.getProcessingTime());
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "コンシューマーエラー", e);
                        }
                    }
                });
            }
        }
        
        public void stop() {
            running.set(false);
            
            producerExecutor.shutdown();
            consumerExecutor.shutdown();
            
            try {
                if (!producerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    producerExecutor.shutdownNow();
                }
                if (!consumerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    consumerExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                producerExecutor.shutdownNow();
                consumerExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        public Statistics getStatistics() { return statistics; }
        public int getQueueSize() { return queue.size(); }
        public boolean isRunning() { return running.get(); }
    }
    
    /**
     * wait/notify を使用した実装
     */
    public static class WaitNotifyProducerConsumer {
        private final Object[] buffer;
        private final int capacity;
        private int count = 0;
        private int putIndex = 0;
        private int takeIndex = 0;
        private final Object lock = new Object();
        private final AtomicBoolean running = new AtomicBoolean(false);
        private final Statistics statistics = new Statistics();
        
        public WaitNotifyProducerConsumer(int capacity) {
            this.buffer = new Object[capacity];
            this.capacity = capacity;
        }
        
        public void put(Task task) throws InterruptedException {
            synchronized (lock) {
                while (count == capacity) {
                    lock.wait();
                }
                buffer[putIndex] = task;
                putIndex = (putIndex + 1) % capacity;
                count++;
                statistics.recordProduced();
                lock.notifyAll();
            }
        }
        
        @SuppressWarnings("unchecked")
        public Task take() throws InterruptedException {
            synchronized (lock) {
                while (count == 0) {
                    lock.wait();
                }
                Task task = (Task) buffer[takeIndex];
                buffer[takeIndex] = null;
                takeIndex = (takeIndex + 1) % capacity;
                count--;
                lock.notifyAll();
                return task;
            }
        }
        
        public void start(Supplier<Task> taskProducer, Consumer<Task> taskConsumer,
                         int producerThreads, int consumerThreads, long durationMs) {
            if (running.getAndSet(true)) {
                throw new IllegalStateException("既に実行中です");
            }
            
            ExecutorService executor = Executors.newFixedThreadPool(producerThreads + consumerThreads);
            long endTime = System.currentTimeMillis() + durationMs;
            
            // プロデューサー
            for (int i = 0; i < producerThreads; i++) {
                executor.submit(() -> {
                    while (running.get() && System.currentTimeMillis() < endTime) {
                        try {
                            Task task = taskProducer.get();
                            if (task != null) {
                                put(task);
                            }
                            Thread.sleep(10); // 調整用
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
            }
            
            // コンシューマー
            for (int i = 0; i < consumerThreads; i++) {
                executor.submit(() -> {
                    while (running.get() || getCount() > 0) {
                        try {
                            Task task = take();
                            taskConsumer.accept(task);
                            task.markProcessed();
                            statistics.recordConsumed(task.getProcessingTime());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
            }
            
            // 指定時間後に停止
            executor.schedule(() -> stop(), durationMs, TimeUnit.MILLISECONDS);
        }
        
        public void stop() {
            running.set(false);
            synchronized (lock) {
                lock.notifyAll();
            }
        }
        
        public int getCount() {
            synchronized (lock) {
                return count;
            }
        }
        
        public Statistics getStatistics() { return statistics; }
        public boolean isRunning() { return running.get(); }
    }
    
    /**
     * Semaphore を使用した実装
     */
    public static class SemaphoreProducerConsumer {
        private final Task[] buffer;
        private final int capacity;
        private final Semaphore emptySlots;
        private final Semaphore fullSlots;
        private final Semaphore mutex = new Semaphore(1);
        private int putIndex = 0;
        private int takeIndex = 0;
        private final AtomicBoolean running = new AtomicBoolean(false);
        private final Statistics statistics = new Statistics();
        
        public SemaphoreProducerConsumer(int capacity) {
            this.buffer = new Task[capacity];
            this.capacity = capacity;
            this.emptySlots = new Semaphore(capacity);
            this.fullSlots = new Semaphore(0);
        }
        
        public void put(Task task) throws InterruptedException {
            emptySlots.acquire();
            mutex.acquire();
            try {
                buffer[putIndex] = task;
                putIndex = (putIndex + 1) % capacity;
                statistics.recordProduced();
            } finally {
                mutex.release();
                fullSlots.release();
            }
        }
        
        public Task take() throws InterruptedException {
            fullSlots.acquire();
            mutex.acquire();
            try {
                Task task = buffer[takeIndex];
                buffer[takeIndex] = null;
                takeIndex = (takeIndex + 1) % capacity;
                return task;
            } finally {
                mutex.release();
                emptySlots.release();
            }
        }
        
        public void start(Supplier<Task> taskProducer, Consumer<Task> taskConsumer,
                         int producerThreads, int consumerThreads, long durationMs) {
            if (running.getAndSet(true)) {
                throw new IllegalStateException("既に実行中です");
            }
            
            ExecutorService executor = Executors.newFixedThreadPool(producerThreads + consumerThreads);
            long endTime = System.currentTimeMillis() + durationMs;
            
            // プロデューサー
            for (int i = 0; i < producerThreads; i++) {
                executor.submit(() -> {
                    while (running.get() && System.currentTimeMillis() < endTime) {
                        try {
                            Task task = taskProducer.get();
                            if (task != null) {
                                put(task);
                            }
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
            }
            
            // コンシューマー
            for (int i = 0; i < consumerThreads; i++) {
                executor.submit(() -> {
                    while (running.get() || getAvailableItems() > 0) {
                        try {
                            Task task = take();
                            taskConsumer.accept(task);
                            task.markProcessed();
                            statistics.recordConsumed(task.getProcessingTime());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                });
            }
            
            // 指定時間後に停止
            executor.schedule(() -> stop(), durationMs, TimeUnit.MILLISECONDS);
        }
        
        public void stop() {
            running.set(false);
        }
        
        public int getAvailableItems() {
            return fullSlots.availablePermits();
        }
        
        public Statistics getStatistics() { return statistics; }
        public boolean isRunning() { return running.get(); }
    }
    
    /**
     * パフォーマンス比較用のベンチマーククラス
     */
    public static class Benchmark {
        
        public static void compareImplementations() {
            System.out.println("=== 生産者・消費者パターン パフォーマンス比較 ===");
            
            Supplier<Task> taskProducer = () -> 
                new Task("task-" + System.nanoTime(), "データ: " + Math.random());
            
            Consumer<Task> taskConsumer = task -> {
                try {
                    Thread.sleep(1); // 処理時間をシミュレート
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            
            long duration = 5000; // 5秒
            
            // BlockingQueue実装
            System.out.println("--- BlockingQueue実装 ---");
            runBlockingQueueBenchmark(taskProducer, taskConsumer, duration);
            
            // wait/notify実装
            System.out.println("\n--- wait/notify実装 ---");
            runWaitNotifyBenchmark(taskProducer, taskConsumer, duration);
            
            // Semaphore実装
            System.out.println("\n--- Semaphore実装 ---");
            runSemaphoreBenchmark(taskProducer, taskConsumer, duration);
        }
        
        private static void runBlockingQueueBenchmark(Supplier<Task> producer, 
                                                    Consumer<Task> consumer, long duration) {
            BlockingQueueProducerConsumer pc = new BlockingQueueProducerConsumer(100, 2, 3);
            
            pc.start(producer, consumer, 100, duration);
            
            try {
                Thread.sleep(duration + 1000); // 少し余分に待つ
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            pc.stop();
            System.out.println("結果: " + pc.getStatistics());
        }
        
        private static void runWaitNotifyBenchmark(Supplier<Task> producer, 
                                                 Consumer<Task> consumer, long duration) {
            WaitNotifyProducerConsumer pc = new WaitNotifyProducerConsumer(100);
            
            pc.start(producer, consumer, 2, 3, duration);
            
            try {
                Thread.sleep(duration + 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            pc.stop();
            System.out.println("結果: " + pc.getStatistics());
        }
        
        private static void runSemaphoreBenchmark(Supplier<Task> producer, 
                                                Consumer<Task> consumer, long duration) {
            SemaphoreProducerConsumer pc = new SemaphoreProducerConsumer(100);
            
            pc.start(producer, consumer, 2, 3, duration);
            
            try {
                Thread.sleep(duration + 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            pc.stop();
            System.out.println("結果: " + pc.getStatistics());
        }
    }
    
    /**
     * デモ用メイン関数
     */
    public static void main(String[] args) {
        // 基本的な動作確認
        System.out.println("=== 基本動作確認 ===");
        
        Supplier<Task> simpleProducer = () -> 
            new Task("demo-" + System.currentTimeMillis(), "デモデータ");
        
        Consumer<Task> simpleConsumer = task -> {
            System.out.println("処理中: " + task);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        BlockingQueueProducerConsumer demo = new BlockingQueueProducerConsumer(10, 1, 2);
        demo.start(simpleProducer, simpleConsumer, 10, 3000);
        
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        demo.stop();
        System.out.println("デモ結果: " + demo.getStatistics());
        System.out.println();
        
        // パフォーマンス比較
        Benchmark.compareImplementations();
    }
}