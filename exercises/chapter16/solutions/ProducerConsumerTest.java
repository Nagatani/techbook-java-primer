import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * ProducerConsumerクラスのテストクラス
 */
public class ProducerConsumerTest {
    
    @Test
    public void testTaskBasicFunctionality() {
        ProducerConsumer.Task task = new ProducerConsumer.Task("test-1", "test data");
        
        assertEquals("test-1", task.getId());
        assertEquals("test data", task.getData());
        assertTrue(task.getCreatedAt() > 0);
        assertEquals(0, task.getProcessedAt());
        assertEquals(-1, task.getProcessingTime());
        
        task.markProcessed();
        assertTrue(task.getProcessedAt() > 0);
        assertTrue(task.getProcessingTime() >= 0);
        
        String taskString = task.toString();
        assertTrue(taskString.contains("test-1"));
        assertTrue(taskString.contains("test data"));
    }
    
    @Test
    public void testStatistics() {
        ProducerConsumer.Statistics stats = new ProducerConsumer.Statistics();
        
        assertEquals(0, stats.getProducedCount());
        assertEquals(0, stats.getConsumedCount());
        assertEquals(0, stats.getQueueSize());
        assertEquals(0.0, stats.getAverageProcessingTime());
        assertTrue(stats.getUptime() >= 0);
        assertEquals(0.0, stats.getThroughput());
        
        stats.recordProduced();
        stats.recordProduced();
        assertEquals(2, stats.getProducedCount());
        assertEquals(2, stats.getQueueSize());
        
        stats.recordConsumed(100);
        stats.recordConsumed(200);
        assertEquals(2, stats.getConsumedCount());
        assertEquals(0, stats.getQueueSize());
        assertEquals(150.0, stats.getAverageProcessingTime());
        
        String statsString = stats.toString();
        assertTrue(statsString.contains("produced=2"));
        assertTrue(statsString.contains("consumed=2"));
    }
    
    @Test
    public void testBlockingQueueProducerConsumer() throws InterruptedException {
        ProducerConsumer.BlockingQueueProducerConsumer pc = 
            new ProducerConsumer.BlockingQueueProducerConsumer(10, 1, 1);
        
        AtomicInteger taskCounter = new AtomicInteger(0);
        AtomicInteger processedCounter = new AtomicInteger(0);
        
        Supplier<ProducerConsumer.Task> producer = () -> {
            int id = taskCounter.incrementAndGet();
            return new ProducerConsumer.Task("task-" + id, "data-" + id);
        };
        
        Consumer<ProducerConsumer.Task> consumer = task -> {
            processedCounter.incrementAndGet();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        assertFalse(pc.isRunning());
        
        pc.start(producer, consumer, 50, 1000);
        assertTrue(pc.isRunning());
        
        Thread.sleep(1500);
        
        pc.stop();
        assertFalse(pc.isRunning());
        
        ProducerConsumer.Statistics stats = pc.getStatistics();
        assertTrue(stats.getProducedCount() > 0);
        assertTrue(stats.getConsumedCount() > 0);
        assertTrue(processedCounter.get() > 0);
    }
    
    @Test
    public void testWaitNotifyProducerConsumer() throws InterruptedException {
        ProducerConsumer.WaitNotifyProducerConsumer pc = 
            new ProducerConsumer.WaitNotifyProducerConsumer(5);
        
        AtomicInteger taskCounter = new AtomicInteger(0);
        AtomicInteger processedCounter = new AtomicInteger(0);
        
        Supplier<ProducerConsumer.Task> producer = () -> {
            int id = taskCounter.incrementAndGet();
            return new ProducerConsumer.Task("task-" + id, "data-" + id);
        };
        
        Consumer<ProducerConsumer.Task> consumer = task -> {
            processedCounter.incrementAndGet();
        };
        
        assertFalse(pc.isRunning());
        
        pc.start(producer, consumer, 1, 1, 1000);
        assertTrue(pc.isRunning());
        
        Thread.sleep(1500);
        
        pc.stop();
        assertFalse(pc.isRunning());
        
        ProducerConsumer.Statistics stats = pc.getStatistics();
        assertTrue(stats.getProducedCount() > 0);
        assertTrue(stats.getConsumedCount() > 0);
    }
    
    @Test
    public void testSemaphoreProducerConsumer() throws InterruptedException {
        ProducerConsumer.SemaphoreProducerConsumer pc = 
            new ProducerConsumer.SemaphoreProducerConsumer(5);
        
        AtomicInteger taskCounter = new AtomicInteger(0);
        AtomicInteger processedCounter = new AtomicInteger(0);
        
        Supplier<ProducerConsumer.Task> producer = () -> {
            int id = taskCounter.incrementAndGet();
            return new ProducerConsumer.Task("task-" + id, "data-" + id);
        };
        
        Consumer<ProducerConsumer.Task> consumer = task -> {
            processedCounter.incrementAndGet();
        };
        
        assertFalse(pc.isRunning());
        
        pc.start(producer, consumer, 1, 1, 1000);
        assertTrue(pc.isRunning());
        
        Thread.sleep(1500);
        
        pc.stop();
        assertFalse(pc.isRunning());
        
        ProducerConsumer.Statistics stats = pc.getStatistics();
        assertTrue(stats.getProducedCount() > 0);
        assertTrue(stats.getConsumedCount() > 0);
    }
    
    @Test
    public void testWaitNotifyPutTake() throws InterruptedException {
        ProducerConsumer.WaitNotifyProducerConsumer pc = 
            new ProducerConsumer.WaitNotifyProducerConsumer(2);
        
        ProducerConsumer.Task task1 = new ProducerConsumer.Task("1", "data1");
        ProducerConsumer.Task task2 = new ProducerConsumer.Task("2", "data2");
        
        assertEquals(0, pc.getCount());
        
        pc.put(task1);
        assertEquals(1, pc.getCount());
        
        pc.put(task2);
        assertEquals(2, pc.getCount());
        
        ProducerConsumer.Task taken1 = pc.take();
        assertEquals("1", taken1.getId());
        assertEquals(1, pc.getCount());
        
        ProducerConsumer.Task taken2 = pc.take();
        assertEquals("2", taken2.getId());
        assertEquals(0, pc.getCount());
    }
    
    @Test
    public void testSemaphorePutTake() throws InterruptedException {
        ProducerConsumer.SemaphoreProducerConsumer pc = 
            new ProducerConsumer.SemaphoreProducerConsumer(2);
        
        ProducerConsumer.Task task1 = new ProducerConsumer.Task("1", "data1");
        ProducerConsumer.Task task2 = new ProducerConsumer.Task("2", "data2");
        
        assertEquals(0, pc.getAvailableItems());
        
        pc.put(task1);
        assertEquals(1, pc.getAvailableItems());
        
        pc.put(task2);
        assertEquals(2, pc.getAvailableItems());
        
        ProducerConsumer.Task taken1 = pc.take();
        assertEquals("1", taken1.getId());
        assertEquals(1, pc.getAvailableItems());
        
        ProducerConsumer.Task taken2 = pc.take();
        assertEquals("2", taken2.getId());
        assertEquals(0, pc.getAvailableItems());
    }
    
    @Test
    public void testBlockingQueueCapacity() throws InterruptedException {
        ProducerConsumer.BlockingQueueProducerConsumer pc = 
            new ProducerConsumer.BlockingQueueProducerConsumer(2, 1, 1);
        
        // 容量を超えるタスクを生産するテスト
        AtomicInteger produced = new AtomicInteger(0);
        Supplier<ProducerConsumer.Task> producer = () -> {
            int id = produced.incrementAndGet();
            return new ProducerConsumer.Task("task-" + id, "data");
        };
        
        Consumer<ProducerConsumer.Task> slowConsumer = task -> {
            try {
                Thread.sleep(100); // 遅い消費者
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        pc.start(producer, slowConsumer, 100, 500);
        
        Thread.sleep(600);
        pc.stop();
        
        // キューが満杯になることでプロデューサーがブロックされることを確認
        assertTrue(pc.getQueueSize() <= 2);
    }
    
    @Test
    public void testMultipleProducersConsumers() throws InterruptedException {
        ProducerConsumer.BlockingQueueProducerConsumer pc = 
            new ProducerConsumer.BlockingQueueProducerConsumer(20, 3, 2);
        
        AtomicInteger taskIdCounter = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);
        
        Supplier<ProducerConsumer.Task> producer = () -> {
            int id = taskIdCounter.incrementAndGet();
            return new ProducerConsumer.Task("task-" + id, "data-" + id);
        };
        
        Consumer<ProducerConsumer.Task> consumer = task -> {
            processedCount.incrementAndGet();
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
        
        pc.start(producer, consumer, 200, 2000);
        
        Thread.sleep(2500);
        pc.stop();
        
        ProducerConsumer.Statistics stats = pc.getStatistics();
        assertTrue(stats.getProducedCount() > 100); // 複数プロデューサーによる生産
        assertTrue(stats.getConsumedCount() > 50);  // 複数コンシューマーによる消費
        assertTrue(stats.getThroughput() > 0);
    }
    
    @Test
    public void testExceptionHandling() throws InterruptedException {
        ProducerConsumer.BlockingQueueProducerConsumer pc = 
            new ProducerConsumer.BlockingQueueProducerConsumer(10, 1, 1);
        
        // 例外を投げるプロデューサー
        Supplier<ProducerConsumer.Task> faultyProducer = () -> {
            throw new RuntimeException("Producer error");
        };
        
        Consumer<ProducerConsumer.Task> consumer = task -> {
            // 正常なコンシューマー
        };
        
        // 例外が発生してもシステムが停止しないことを確認
        pc.start(faultyProducer, consumer, 10, 500);
        
        Thread.sleep(600);
        pc.stop();
        
        // 統計は正常に取得できるはず
        assertNotNull(pc.getStatistics());
    }
    
    @Test
    public void testDoubleStart() {
        ProducerConsumer.BlockingQueueProducerConsumer pc = 
            new ProducerConsumer.BlockingQueueProducerConsumer(10, 1, 1);
        
        Supplier<ProducerConsumer.Task> producer = () -> 
            new ProducerConsumer.Task("1", "data");
        Consumer<ProducerConsumer.Task> consumer = task -> {};
        
        pc.start(producer, consumer, 10, 1000);
        
        // 二重起動は例外を投げるはず
        assertThrows(IllegalStateException.class, () -> {
            pc.start(producer, consumer, 10, 1000);
        });
        
        pc.stop();
    }
    
    @Test
    public void testStatisticsThroughput() throws InterruptedException {
        ProducerConsumer.Statistics stats = new ProducerConsumer.Statistics();
        
        // 複数のタスクを高速で記録
        for (int i = 0; i < 100; i++) {
            stats.recordProduced();
            stats.recordConsumed(10);
        }
        
        Thread.sleep(100); // 少し時間を経過させる
        
        assertTrue(stats.getThroughput() > 0);
        assertEquals(100, stats.getProducedCount());
        assertEquals(100, stats.getConsumedCount());
        assertEquals(10.0, stats.getAverageProcessingTime());
    }
    
    @Test
    public void testTaskProcessingTime() throws InterruptedException {
        ProducerConsumer.Task task = new ProducerConsumer.Task("timing-test", "data");
        
        long createdTime = task.getCreatedAt();
        assertTrue(createdTime > 0);
        assertEquals(-1, task.getProcessingTime());
        
        Thread.sleep(10);
        task.markProcessed();
        
        long processedTime = task.getProcessedAt();
        assertTrue(processedTime > createdTime);
        assertTrue(task.getProcessingTime() >= 10);
    }
}