package chapter11.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * EventProcessorクラスのテストクラス
 */
public class EventProcessorTest {
    
    private EventProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new EventProcessor();
    }
    
    @Test
    void testEventCreation() {
        EventProcessor.Event event = new EventProcessor.Event("TEST", "test data");
        
        assertEquals("TEST", event.getType());
        assertEquals("test data", event.getData());
        assertNotNull(event.getTimestamp());
    }
    
    @Test
    void testAddAndRemoveListener() {
        AtomicInteger counter = new AtomicInteger(0);
        Consumer<EventProcessor.Event> listener = event -> counter.incrementAndGet();
        
        processor.addListener(listener);
        processor.publishEvent("TEST", "data");
        
        assertEquals(1, counter.get());
        
        processor.removeListener(listener);
        processor.publishEvent("TEST", "data");
        
        // カウンターは増加しない
        assertEquals(1, counter.get());
    }
    
    @Test
    void testPublishEvent() {
        List<EventProcessor.Event> receivedEvents = new ArrayList<>();
        Consumer<EventProcessor.Event> listener = receivedEvents::add;
        
        processor.addListener(listener);
        processor.publishEvent("TEST", "test data");
        
        assertEquals(1, receivedEvents.size());
        assertEquals("TEST", receivedEvents.get(0).getType());
        assertEquals("test data", receivedEvents.get(0).getData());
    }
    
    @Test
    void testMultipleListeners() {
        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);
        
        processor.addListener(event -> counter1.incrementAndGet());
        processor.addListener(event -> counter2.incrementAndGet());
        
        processor.publishEvent("TEST", "data");
        
        assertEquals(1, counter1.get());
        assertEquals(1, counter2.get());
    }
    
    @Test
    void testCreateTypeSpecificListener() {
        AtomicInteger testCounter = new AtomicInteger(0);
        AtomicInteger errorCounter = new AtomicInteger(0);
        
        Consumer<EventProcessor.Event> testListener = processor.createTypeSpecificListener("TEST", 
            event -> testCounter.incrementAndGet());
        
        Consumer<EventProcessor.Event> errorListener = processor.createTypeSpecificListener("ERROR", 
            event -> errorCounter.incrementAndGet());
        
        processor.addListener(testListener);
        processor.addListener(errorListener);
        
        processor.publishEvent("TEST", "test data");
        processor.publishEvent("ERROR", "error data");
        processor.publishEvent("INFO", "info data");
        
        assertEquals(1, testCounter.get());
        assertEquals(1, errorCounter.get());
    }
    
    @Test
    void testCreateConditionalListener() {
        AtomicInteger counter = new AtomicInteger(0);
        
        Consumer<EventProcessor.Event> listener = processor.createConditionalListener(
            event -> event.getData().contains("important"),
            event -> counter.incrementAndGet()
        );
        
        processor.addListener(listener);
        
        processor.publishEvent("TEST", "normal data");
        processor.publishEvent("TEST", "important data");
        processor.publishEvent("TEST", "very important information");
        
        assertEquals(2, counter.get());
    }
    
    @Test
    void testCreateTransformingListener() {
        List<String> transformedData = new ArrayList<>();
        
        Consumer<EventProcessor.Event> listener = processor.createTransformingListener(
            event -> event.getType() + ":" + event.getData(),
            transformedData::add
        );
        
        processor.addListener(listener);
        processor.publishEvent("TEST", "data");
        
        assertEquals(1, transformedData.size());
        assertEquals("TEST:data", transformedData.get(0));
    }
    
    @Test
    void testCombineListeners() {
        AtomicInteger counter1 = new AtomicInteger(0);
        AtomicInteger counter2 = new AtomicInteger(0);
        
        Consumer<EventProcessor.Event> listener1 = event -> counter1.incrementAndGet();
        Consumer<EventProcessor.Event> listener2 = event -> counter2.incrementAndGet();
        
        Consumer<EventProcessor.Event> combined = processor.combineListeners(listener1, listener2);
        
        processor.addListener(combined);
        processor.publishEvent("TEST", "data");
        
        assertEquals(1, counter1.get());
        assertEquals(1, counter2.get());
    }
    
    @Test
    void testGetEventsByType() {
        processor.publishEvent("TEST", "test1");
        processor.publishEvent("ERROR", "error1");
        processor.publishEvent("TEST", "test2");
        processor.publishEvent("INFO", "info1");
        
        List<EventProcessor.Event> testEvents = processor.getEventsByType("TEST");
        
        assertEquals(2, testEvents.size());
        assertEquals("test1", testEvents.get(0).getData());
        assertEquals("test2", testEvents.get(1).getData());
    }
    
    @Test
    void testGetRecentEvents() {
        processor.publishEvent("TEST", "event1");
        processor.publishEvent("TEST", "event2");
        processor.publishEvent("TEST", "event3");
        processor.publishEvent("TEST", "event4");
        
        List<EventProcessor.Event> recentEvents = processor.getRecentEvents(2);
        
        assertEquals(2, recentEvents.size());
        assertEquals("event3", recentEvents.get(0).getData());
        assertEquals("event4", recentEvents.get(1).getData());
    }
    
    @Test
    void testClearEventLog() {
        processor.publishEvent("TEST", "event1");
        processor.publishEvent("TEST", "event2");
        
        assertEquals(2, processor.getEventsByType("TEST").size());
        
        processor.clearEventLog();
        
        assertEquals(0, processor.getEventsByType("TEST").size());
    }
    
    @Test
    void testBatchEventProcessor() {
        List<List<EventProcessor.Event>> batches = new ArrayList<>();
        
        EventProcessor.BatchEventProcessor batchProcessor = 
            new EventProcessor.BatchEventProcessor(3, batches::add);
        
        processor.addListener(batchProcessor.getListener());
        
        processor.publishEvent("TEST", "event1");
        processor.publishEvent("TEST", "event2");
        processor.publishEvent("TEST", "event3");
        
        assertEquals(1, batches.size());
        assertEquals(3, batches.get(0).size());
        
        processor.publishEvent("TEST", "event4");
        processor.publishEvent("TEST", "event5");
        
        batchProcessor.flush();
        
        assertEquals(2, batches.size());
        assertEquals(2, batches.get(1).size());
    }
    
    @Test
    void testRateLimitedListener() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger(0);
        
        EventProcessor.RateLimitedListener rateLimitedListener = 
            new EventProcessor.RateLimitedListener(event -> counter.incrementAndGet(), 100);
        
        processor.addListener(rateLimitedListener.getListener());
        
        // 短時間で複数のイベントを発行
        processor.publishEvent("TEST", "event1");
        processor.publishEvent("TEST", "event2");
        processor.publishEvent("TEST", "event3");
        
        // 最初の1つだけが処理される
        assertEquals(1, counter.get());
        
        // 十分な時間を待つ
        Thread.sleep(150);
        
        processor.publishEvent("TEST", "event4");
        
        // 制限時間が過ぎたので、次のイベントが処理される
        assertEquals(2, counter.get());
    }
    
    @Test
    void testListenerFactory() {
        // ConsoleLoggerとErrorLoggerをテスト
        Consumer<EventProcessor.Event> consoleLogger = EventProcessor.ListenerFactory.createConsoleLogger();
        Consumer<EventProcessor.Event> errorLogger = EventProcessor.ListenerFactory.createErrorLogger();
        
        processor.addListener(consoleLogger);
        processor.addListener(errorLogger);
        
        // 実際の出力は検証が困難なので、例外が発生しないことを確認
        assertDoesNotThrow(() -> {
            processor.publishEvent("TEST", "test message");
            processor.publishEvent("ERROR", "error message");
        });
    }
    
    @Test
    void testListenerException() {
        Consumer<EventProcessor.Event> faultyListener = event -> {
            throw new RuntimeException("Test exception");
        };
        
        AtomicInteger successCounter = new AtomicInteger(0);
        Consumer<EventProcessor.Event> successListener = event -> successCounter.incrementAndGet();
        
        processor.addListener(faultyListener);
        processor.addListener(successListener);
        
        // 例外が発生してもプログラムは継続する
        assertDoesNotThrow(() -> processor.publishEvent("TEST", "data"));
        
        // 正常なリスナーは実行される
        assertEquals(1, successCounter.get());
    }
}