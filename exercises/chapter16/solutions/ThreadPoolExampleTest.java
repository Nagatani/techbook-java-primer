import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.util.concurrent.*;

/**
 * ThreadPoolExampleクラスのテストクラス
 */
public class ThreadPoolExampleTest {
    
    @Test
    public void testTaskResultCreation() {
        ThreadPoolExample.TaskResult result = new ThreadPoolExample.TaskResult(
            "test-1", 1000, 1100, "thread-1", true, "成功", null);
        
        assertEquals("test-1", result.getTaskId());
        assertEquals(1000, result.getStartTime());
        assertEquals(1100, result.getEndTime());
        assertEquals(100, result.getDuration());
        assertEquals("thread-1", result.getThreadName());
        assertTrue(result.isSuccess());
        assertEquals("成功", result.getResult());
        assertNull(result.getError());
        
        String resultString = result.toString();
        assertTrue(resultString.contains("test-1"));
        assertTrue(resultString.contains("thread-1"));
        assertTrue(resultString.contains("100ms"));
        assertTrue(resultString.contains("true"));
    }
    
    @Test
    public void testComputationTaskSuccess() throws Exception {
        ThreadPoolExample.ComputationTask task = new ThreadPoolExample.ComputationTask("comp-1", 50, false);
        
        ThreadPoolExample.TaskResult result = task.call();
        
        assertNotNull(result);
        assertEquals("comp-1", result.getTaskId());
        assertTrue(result.isSuccess());
        assertNotNull(result.getResult());
        assertTrue(result.getResult().contains("計算結果"));
        assertNull(result.getError());
        assertTrue(result.getDuration() > 0);
    }
    
    @Test
    public void testComputationTaskFailure() throws Exception {
        ThreadPoolExample.ComputationTask task = new ThreadPoolExample.ComputationTask("comp-fail", 50, true);
        
        ThreadPoolExample.TaskResult result = task.call();
        
        assertNotNull(result);
        assertEquals("comp-fail", result.getTaskId());
        assertFalse(result.isSuccess());
        assertNull(result.getResult());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("意図的な失敗"));
    }
    
    @Test
    public void testIOTask() throws Exception {
        ThreadPoolExample.IOTask task = new ThreadPoolExample.IOTask("io-1", 50);
        
        long startTime = System.currentTimeMillis();
        ThreadPoolExample.TaskResult result = task.call();
        long endTime = System.currentTimeMillis();
        
        assertNotNull(result);
        assertEquals("io-1", result.getTaskId());
        assertTrue(result.isSuccess());
        assertNotNull(result.getResult());
        assertTrue(result.getResult().contains("I/O完了"));
        assertNull(result.getError());
        assertTrue(endTime - startTime >= 50); // 最低でも50ms
    }
    
    @Test
    public void testThreadPoolStatistics() {
        // カスタムThreadPoolExecutorでテスト
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        
        // いくつかのタスクを実行
        for (int i = 0; i < 5; i++) {
            executor.submit(() -> {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // 少し待ってから統計を取得
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        ThreadPoolExample.ThreadPoolStatistics stats = 
            new ThreadPoolExample.ThreadPoolStatistics("TestPool", executor, 500, 5);
        
        assertEquals("TestPool", stats.getPoolType());
        assertEquals(2, stats.getCorePoolSize());
        assertEquals(4, stats.getMaximumPoolSize());
        assertEquals(100.0, stats.getAverageExecutionTime(), 0.01);
        
        String statsString = stats.toString();
        assertTrue(statsString.contains("TestPool"));
        assertTrue(statsString.contains("core=2"));
        assertTrue(statsString.contains("max=4"));
        
        executor.shutdown();
    }
    
    @Test
    public void testFixedThreadPool() {
        ThreadPoolExample.ThreadPoolStatistics stats = 
            ThreadPoolExample.ThreadPoolComparison.testFixedThreadPool(3, 10);
        
        assertNotNull(stats);
        assertEquals("FixedThreadPool", stats.getPoolType());
        assertEquals(3, stats.getCorePoolSize());
        assertEquals(3, stats.getMaximumPoolSize());
        assertTrue(stats.getCompletedTaskCount() > 0);
    }
    
    @Test
    public void testSingleThreadExecutor() {
        ThreadPoolExample.ThreadPoolStatistics stats = 
            ThreadPoolExample.ThreadPoolComparison.testSingleThreadExecutor(5);
        
        assertNotNull(stats);
        assertEquals("SingleThreadExecutor", stats.getPoolType());
        assertEquals(1, stats.getCorePoolSize());
        assertEquals(1, stats.getMaximumPoolSize());
        assertTrue(stats.getCompletedTaskCount() > 0);
    }
    
    @Test
    public void testCustomThreadPool() {
        ThreadPoolExample.ThreadPoolStatistics stats = 
            ThreadPoolExample.ThreadPoolComparison.testCustomThreadPool(2, 5, 10, 8);
        
        assertNotNull(stats);
        assertEquals("CustomThreadPool", stats.getPoolType());
        assertEquals(2, stats.getCorePoolSize());
        assertEquals(5, stats.getMaximumPoolSize());
        assertTrue(stats.getCompletedTaskCount() > 0);
    }
    
    @Test
    public void testScheduledTaskExample() throws Exception {
        ThreadPoolExample.ScheduledTaskExample scheduledExample = 
            new ThreadPoolExample.ScheduledTaskExample(2);
        
        try {
            // 1秒後に実行されるタスク
            ScheduledFuture<ThreadPoolExample.TaskResult> future = 
                scheduledExample.scheduleOnce(1, "once-task");
            
            ThreadPoolExample.TaskResult result = future.get(2, TimeUnit.SECONDS);
            
            assertNotNull(result);
            assertTrue(result.getTaskId().contains("once-task"));
            assertTrue(result.isSuccess());
            assertTrue(result.getResult().contains("実行回数"));
            
        } finally {
            scheduledExample.shutdown();
        }
    }
    
    @Test
    public void testScheduledTaskFixedRate() throws Exception {
        ThreadPoolExample.ScheduledTaskExample scheduledExample = 
            new ThreadPoolExample.ScheduledTaskExample(2);
        
        try {
            // 即座に開始、1秒間隔で実行
            ScheduledFuture<?> future = scheduledExample.scheduleAtFixedRate(0, 1, "rate-task");
            
            Thread.sleep(3500); // 3.5秒待機（約3-4回実行される）
            
            future.cancel(false);
            
            assertTrue(scheduledExample.getTotalExecutions() >= 3);
            
        } finally {
            scheduledExample.shutdown();
        }
    }
    
    @Test
    public void testScheduledTaskFixedDelay() throws Exception {
        ThreadPoolExample.ScheduledTaskExample scheduledExample = 
            new ThreadPoolExample.ScheduledTaskExample(2);
        
        try {
            // 即座に開始、前回完了の1秒後に実行
            ScheduledFuture<?> future = scheduledExample.scheduleWithFixedDelay(0, 1, "delay-task");
            
            Thread.sleep(3000); // 3秒待機
            
            future.cancel(false);
            
            assertTrue(scheduledExample.getTotalExecutions() >= 2);
            
        } finally {
            scheduledExample.shutdown();
        }
    }
    
    @Test
    public void testTaskResultError() {
        ThreadPoolExample.TaskResult errorResult = new ThreadPoolExample.TaskResult(
            "error-task", 1000, 1050, "thread-error", false, null, "エラーメッセージ");
        
        assertEquals("error-task", errorResult.getTaskId());
        assertFalse(errorResult.isSuccess());
        assertNull(errorResult.getResult());
        assertEquals("エラーメッセージ", errorResult.getError());
        assertEquals(50, errorResult.getDuration());
    }
    
    @Test
    public void testThreadPoolExecutorBasics() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 2, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1));
        
        try {
            // 基本的な設定の確認
            assertEquals(1, executor.getCorePoolSize());
            assertEquals(2, executor.getMaximumPoolSize());
            assertEquals(0, executor.getActiveCount());
            
            // タスクを実行
            Future<?> future = executor.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            // アクティブなタスクが増えることを確認
            Thread.sleep(10);
            assertTrue(executor.getActiveCount() > 0);
            
            future.get();
            
        } finally {
            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
    
    @Test
    public void testRejectionPolicy() throws Exception {
        // 容量の小さいキューで拒否ポリシーをテスト
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1, 1, 1L, TimeUnit.SECONDS, 
            new LinkedBlockingQueue<>(1),
            new ThreadPoolExecutor.AbortPolicy());
        
        try {
            // 最初のタスクは実行される
            executor.submit(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            // 2番目のタスクはキューに入る
            executor.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            // 3番目のタスクは拒否される（AbortPolicyによりRejectedExecutionException）
            assertThrows(RejectedExecutionException.class, () -> {
                executor.submit(() -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            });
            
        } finally {
            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
    
    @Test
    public void testCachedThreadPool() throws Exception {
        ExecutorService executor = Executors.newCachedThreadPool();
        
        try {
            // 複数のタスクを同時に実行
            Future<?>[] futures = new Future<?>[5];
            for (int i = 0; i < 5; i++) {
                final int taskId = i;
                futures[i] = executor.submit(() -> {
                    try {
                        Thread.sleep(100);
                        return "Task " + taskId + " completed";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return "Task " + taskId + " interrupted";
                    }
                });
            }
            
            // すべてのタスクの完了を待つ
            for (Future<?> future : futures) {
                assertNotNull(future.get(2, TimeUnit.SECONDS));
            }
            
        } finally {
            executor.shutdown();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }
}