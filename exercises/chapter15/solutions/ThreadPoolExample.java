import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * スレッドプール活用クラス
 * 各種Executorの特性と使い分けを学習
 */
public class ThreadPoolExample {
    
    private static final Logger logger = Logger.getLogger(ThreadPoolExample.class.getName());
    
    /**
     * タスクの実行結果を表すクラス
     */
    public static class TaskResult {
        private final String taskId;
        private final long startTime;
        private final long endTime;
        private final String threadName;
        private final boolean success;
        private final String result;
        private final String error;
        
        public TaskResult(String taskId, long startTime, long endTime, 
                         String threadName, boolean success, String result, String error) {
            this.taskId = taskId;
            this.startTime = startTime;
            this.endTime = endTime;
            this.threadName = threadName;
            this.success = success;
            this.result = result;
            this.error = error;
        }
        
        public String getTaskId() { return taskId; }
        public long getStartTime() { return startTime; }
        public long getEndTime() { return endTime; }
        public long getDuration() { return endTime - startTime; }
        public String getThreadName() { return threadName; }
        public boolean isSuccess() { return success; }
        public String getResult() { return result; }
        public String getError() { return error; }
        
        @Override
        public String toString() {
            return String.format("TaskResult[id=%s, thread=%s, duration=%dms, success=%s]", 
                taskId, threadName, getDuration(), success);
        }
    }
    
    /**
     * スレッドプールの統計情報
     */
    public static class ThreadPoolStatistics {
        private final String poolType;
        private final int corePoolSize;
        private final int maximumPoolSize;
        private final long completedTaskCount;
        private final int activeCount;
        private final int queueSize;
        private final long totalExecutionTime;
        private final int taskCount;
        
        public ThreadPoolStatistics(String poolType, ThreadPoolExecutor executor, 
                                   long totalExecutionTime, int taskCount) {
            this.poolType = poolType;
            this.corePoolSize = executor.getCorePoolSize();
            this.maximumPoolSize = executor.getMaximumPoolSize();
            this.completedTaskCount = executor.getCompletedTaskCount();
            this.activeCount = executor.getActiveCount();
            this.queueSize = executor.getQueue().size();
            this.totalExecutionTime = totalExecutionTime;
            this.taskCount = taskCount;
        }
        
        public String getPoolType() { return poolType; }
        public int getCorePoolSize() { return corePoolSize; }
        public int getMaximumPoolSize() { return maximumPoolSize; }
        public long getCompletedTaskCount() { return completedTaskCount; }
        public int getActiveCount() { return activeCount; }
        public int getQueueSize() { return queueSize; }
        public double getAverageExecutionTime() { 
            return taskCount > 0 ? (double) totalExecutionTime / taskCount : 0.0; 
        }
        
        @Override
        public String toString() {
            return String.format(
                "%s[core=%d, max=%d, completed=%d, active=%d, queue=%d, avgTime=%.2fms]",
                poolType, corePoolSize, maximumPoolSize, completedTaskCount, 
                activeCount, queueSize, getAverageExecutionTime());
        }
    }
    
    /**
     * 基本的なタスククラス
     */
    public static class ComputationTask implements Callable<TaskResult> {
        private final String taskId;
        private final int workload;
        private final boolean shouldFail;
        
        public ComputationTask(String taskId, int workload, boolean shouldFail) {
            this.taskId = taskId;
            this.workload = workload;
            this.shouldFail = shouldFail;
        }
        
        @Override
        public TaskResult call() throws Exception {
            long startTime = System.currentTimeMillis();
            String threadName = Thread.currentThread().getName();
            
            try {
                if (shouldFail) {
                    throw new RuntimeException("意図的な失敗: " + taskId);
                }
                
                // CPU集約的な処理をシミュレート
                long result = 0;
                for (int i = 0; i < workload * 1000; i++) {
                    result += Math.sqrt(i);
                }
                
                Thread.sleep(10 + new Random().nextInt(50)); // 追加の遅延
                
                long endTime = System.currentTimeMillis();
                return new TaskResult(taskId, startTime, endTime, threadName, 
                                    true, "計算結果: " + result, null);
                
            } catch (Exception e) {
                long endTime = System.currentTimeMillis();
                return new TaskResult(taskId, startTime, endTime, threadName, 
                                    false, null, e.getMessage());
            }
        }
    }
    
    /**
     * I/O集約的なタスククラス
     */
    public static class IOTask implements Callable<TaskResult> {
        private final String taskId;
        private final int ioDelayMs;
        
        public IOTask(String taskId, int ioDelayMs) {
            this.taskId = taskId;
            this.ioDelayMs = ioDelayMs;
        }
        
        @Override
        public TaskResult call() throws Exception {
            long startTime = System.currentTimeMillis();
            String threadName = Thread.currentThread().getName();
            
            try {
                // I/O操作をシミュレート
                Thread.sleep(ioDelayMs);
                
                long endTime = System.currentTimeMillis();
                return new TaskResult(taskId, startTime, endTime, threadName, 
                                    true, "I/O完了: " + taskId, null);
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                long endTime = System.currentTimeMillis();
                return new TaskResult(taskId, startTime, endTime, threadName, 
                                    false, null, "中断されました");
            }
        }
    }
    
    /**
     * スレッドプールの比較テストクラス
     */
    public static class ThreadPoolComparison {
        
        /**
         * 固定サイズスレッドプールのテスト
         */
        public static ThreadPoolStatistics testFixedThreadPool(int poolSize, int taskCount) {
            ExecutorService executor = Executors.newFixedThreadPool(poolSize);
            return executeTasksAndGatherStatistics("FixedThreadPool", executor, taskCount);
        }
        
        /**
         * キャッシュ型スレッドプールのテスト
         */
        public static ThreadPoolStatistics testCachedThreadPool(int taskCount) {
            ExecutorService executor = Executors.newCachedThreadPool();
            return executeTasksAndGatherStatistics("CachedThreadPool", executor, taskCount);
        }
        
        /**
         * 単一スレッドプールのテスト
         */
        public static ThreadPoolStatistics testSingleThreadExecutor(int taskCount) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            return executeTasksAndGatherStatistics("SingleThreadExecutor", executor, taskCount);
        }
        
        /**
         * ワークスティーリングプールのテスト
         */
        public static ThreadPoolStatistics testWorkStealingPool(int parallelism, int taskCount) {
            ForkJoinPool executor = new ForkJoinPool(parallelism);
            try {
                long startTime = System.currentTimeMillis();
                
                List<Future<TaskResult>> futures = new ArrayList<>();
                for (int i = 0; i < taskCount; i++) {
                    ComputationTask task = new ComputationTask("task-" + i, 100, i % 20 == 0);
                    futures.add(executor.submit(task));
                }
                
                long totalExecutionTime = 0;
                for (Future<TaskResult> future : futures) {
                    try {
                        TaskResult result = future.get();
                        totalExecutionTime += result.getDuration();
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "タスク実行エラー", e);
                    }
                }
                
                long endTime = System.currentTimeMillis();
                
                return new ThreadPoolStatistics("WorkStealingPool", 
                    new ThreadPoolExecutor(parallelism, parallelism, 0L, TimeUnit.MILLISECONDS, 
                                         new LinkedBlockingQueue<>()) {{
                        setCompletedTaskCount(taskCount);
                    }}, totalExecutionTime, taskCount);
                
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        /**
         * カスタムスレッドプールのテスト
         */
        public static ThreadPoolStatistics testCustomThreadPool(int coreSize, int maxSize, 
                                                               int queueCapacity, int taskCount) {
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize, maxSize, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "CustomPool-" + threadNumber.getAndIncrement());
                        t.setDaemon(false);
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // 拒否ポリシー
            );
            
            return executeTasksAndGatherStatistics("CustomThreadPool", executor, taskCount);
        }
        
        private static ThreadPoolStatistics executeTasksAndGatherStatistics(
                String poolType, ExecutorService executor, int taskCount) {
            try {
                long startTime = System.currentTimeMillis();
                
                List<Future<TaskResult>> futures = new ArrayList<>();
                for (int i = 0; i < taskCount; i++) {
                    ComputationTask task = new ComputationTask("task-" + i, 100, i % 20 == 0);
                    futures.add(executor.submit(task));
                }
                
                long totalExecutionTime = 0;
                for (Future<TaskResult> future : futures) {
                    try {
                        TaskResult result = future.get();
                        totalExecutionTime += result.getDuration();
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "タスク実行エラー", e);
                    }
                }
                
                long endTime = System.currentTimeMillis();
                
                ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;
                return new ThreadPoolStatistics(poolType, tpe, totalExecutionTime, taskCount);
                
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    /**
     * スケジュールされたタスクの例
     */
    public static class ScheduledTaskExample {
        private final ScheduledExecutorService scheduler;
        private final AtomicInteger taskCounter = new AtomicInteger(0);
        private final AtomicLong totalExecutions = new AtomicLong(0);
        
        public ScheduledTaskExample(int poolSize) {
            this.scheduler = Executors.newScheduledThreadPool(poolSize);
        }
        
        /**
         * 一回だけ実行されるスケジュールタスク
         */
        public ScheduledFuture<TaskResult> scheduleOnce(int delaySeconds, String taskId) {
            return scheduler.schedule(() -> {
                int taskNum = taskCounter.incrementAndGet();
                long executions = totalExecutions.incrementAndGet();
                
                return new TaskResult(
                    taskId + "-" + taskNum,
                    System.currentTimeMillis(),
                    System.currentTimeMillis() + 100,
                    Thread.currentThread().getName(),
                    true,
                    "実行回数: " + executions,
                    null
                );
            }, delaySeconds, TimeUnit.SECONDS);
        }
        
        /**
         * 固定レートで実行されるスケジュールタスク
         */
        public ScheduledFuture<?> scheduleAtFixedRate(int initialDelaySeconds, 
                                                     int periodSeconds, String taskId) {
            return scheduler.scheduleAtFixedRate(() -> {
                int taskNum = taskCounter.incrementAndGet();
                long executions = totalExecutions.incrementAndGet();
                
                logger.info(String.format("固定レートタスク実行: %s-%d (実行回数: %d)", 
                    taskId, taskNum, executions));
                
                try {
                    Thread.sleep(50); // 短い処理時間
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, initialDelaySeconds, periodSeconds, TimeUnit.SECONDS);
        }
        
        /**
         * 固定遅延で実行されるスケジュールタスク
         */
        public ScheduledFuture<?> scheduleWithFixedDelay(int initialDelaySeconds, 
                                                        int delaySeconds, String taskId) {
            return scheduler.scheduleWithFixedDelay(() -> {
                int taskNum = taskCounter.incrementAndGet();
                long executions = totalExecutions.incrementAndGet();
                
                logger.info(String.format("固定遅延タスク実行: %s-%d (実行回数: %d)", 
                    taskId, taskNum, executions));
                
                try {
                    Thread.sleep(100 + new Random().nextInt(100)); // 可変の処理時間
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, initialDelaySeconds, delaySeconds, TimeUnit.SECONDS);
        }
        
        public long getTotalExecutions() {
            return totalExecutions.get();
        }
        
        public void shutdown() {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * パフォーマンステストクラス
     */
    public static class PerformanceTest {
        
        public static void compareThreadPools() {
            System.out.println("=== スレッドプール性能比較 ===");
            
            int taskCount = 100;
            
            System.out.println("タスク数: " + taskCount);
            System.out.println();
            
            // 各種スレッドプールの性能測定
            ThreadPoolStatistics fixed = ThreadPoolComparison.testFixedThreadPool(5, taskCount);
            System.out.println("固定サイズプール(5): " + fixed);
            
            ThreadPoolStatistics cached = ThreadPoolComparison.testCachedThreadPool(taskCount);
            System.out.println("キャッシュプール: " + cached);
            
            ThreadPoolStatistics single = ThreadPoolComparison.testSingleThreadExecutor(taskCount);
            System.out.println("単一スレッド: " + single);
            
            ThreadPoolStatistics workStealing = ThreadPoolComparison.testWorkStealingPool(
                Runtime.getRuntime().availableProcessors(), taskCount);
            System.out.println("ワークスティーリング: " + workStealing);
            
            ThreadPoolStatistics custom = ThreadPoolComparison.testCustomThreadPool(
                3, 10, 50, taskCount);
            System.out.println("カスタムプール: " + custom);
        }
        
        public static void testIOvsComputeTasks() {
            System.out.println("\n=== I/O vs CPU集約タスクの比較 ===");
            
            // I/O集約タスク用（多くのスレッド）
            ExecutorService ioPool = Executors.newFixedThreadPool(20);
            
            // CPU集約タスク用（CPUコア数程度）
            ExecutorService cpuPool = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors());
            
            try {
                // I/Oタスクのテスト
                long ioStart = System.currentTimeMillis();
                List<Future<TaskResult>> ioFutures = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    ioFutures.add(ioPool.submit(new IOTask("io-" + i, 100)));
                }
                
                for (Future<TaskResult> future : ioFutures) {
                    future.get();
                }
                long ioTime = System.currentTimeMillis() - ioStart;
                
                // CPUタスクのテスト
                long cpuStart = System.currentTimeMillis();
                List<Future<TaskResult>> cpuFutures = new ArrayList<>();
                for (int i = 0; i < 50; i++) {
                    cpuFutures.add(cpuPool.submit(new ComputationTask("cpu-" + i, 200, false)));
                }
                
                for (Future<TaskResult> future : cpuFutures) {
                    future.get();
                }
                long cpuTime = System.currentTimeMillis() - cpuStart;
                
                System.out.println("I/Oタスク実行時間: " + ioTime + "ms (20スレッド)");
                System.out.println("CPUタスク実行時間: " + cpuTime + "ms (" + 
                    Runtime.getRuntime().availableProcessors() + "スレッド)");
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "性能テストエラー", e);
            } finally {
                ioPool.shutdown();
                cpuPool.shutdown();
            }
        }
    }
    
    /**
     * デモ用メイン関数
     */
    public static void main(String[] args) {
        // 基本的なスレッドプール使用例
        System.out.println("=== 基本的なスレッドプール使用例 ===");
        
        ExecutorService executor = Executors.newFixedThreadPool(3);
        
        try {
            List<Future<TaskResult>> futures = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                ComputationTask task = new ComputationTask("demo-" + i, 50, i == 5);
                futures.add(executor.submit(task));
            }
            
            for (Future<TaskResult> future : futures) {
                try {
                    TaskResult result = future.get();
                    System.out.println(result);
                } catch (Exception e) {
                    System.out.println("タスク実行エラー: " + e.getMessage());
                }
            }
            
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println();
        
        // スケジュールタスクの例
        System.out.println("=== スケジュールタスクの例 ===");
        ScheduledTaskExample scheduledExample = new ScheduledTaskExample(2);
        
        try {
            // 2秒後に実行
            ScheduledFuture<TaskResult> onceFuture = scheduledExample.scheduleOnce(2, "once");
            
            // 1秒後から2秒間隔で実行
            ScheduledFuture<?> fixedRateFuture = scheduledExample.scheduleAtFixedRate(1, 2, "fixed-rate");
            
            // 1秒後から前回完了の1秒後に実行
            ScheduledFuture<?> fixedDelayFuture = scheduledExample.scheduleWithFixedDelay(1, 1, "fixed-delay");
            
            // 10秒間実行
            Thread.sleep(10000);
            
            // キャンセル
            fixedRateFuture.cancel(false);
            fixedDelayFuture.cancel(false);
            
            System.out.println("総実行回数: " + scheduledExample.getTotalExecutions());
            
            // 一回実行タスクの結果を取得
            try {
                TaskResult onceResult = onceFuture.get();
                System.out.println("一回実行タスク結果: " + onceResult);
            } catch (Exception e) {
                System.out.println("一回実行タスクエラー: " + e.getMessage());
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            scheduledExample.shutdown();
        }
        
        System.out.println();
        
        // パフォーマンス比較
        PerformanceTest.compareThreadPools();
        PerformanceTest.testIOvsComputeTasks();
    }
}