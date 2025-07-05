package chapter11.solutions;

import java.util.function.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * イベント処理システムを実装するクラス
 * 
 * 学習内容：
 * - Consumer<T>の使用方法
 * - イベントリスナーパターン
 * - 関数型インターフェイスによるコールバック
 * - 非同期処理との組み合わせ
 */
public class EventProcessor {
    
    /**
     * イベントクラス
     */
    public static class Event {
        private final String type;
        private final String data;
        private final LocalDateTime timestamp;
        
        public Event(String type, String data) {
            this.type = type;
            this.data = data;
            this.timestamp = LocalDateTime.now();
        }
        
        public String getType() { return type; }
        public String getData() { return data; }
        public LocalDateTime getTimestamp() { return timestamp; }
        
        @Override
        public String toString() {
            return String.format("Event{type='%s', data='%s', timestamp=%s}", 
                type, data, timestamp);
        }
    }
    
    /**
     * リスナーを格納するリスト（スレッドセーフ）
     */
    private final List<Consumer<Event>> listeners = new CopyOnWriteArrayList<>();
    
    /**
     * イベントログを格納するリスト
     */
    private final List<Event> eventLog = new ArrayList<>();
    
    /**
     * 処理済みイベントの統計情報
     */
    private int processedCount = 0;
    private int errorCount = 0;
    
    /**
     * リスナーを追加
     */
    public void addListener(Consumer<Event> listener) {
        listeners.add(listener);
    }
    
    /**
     * リスナーを削除
     */
    public void removeListener(Consumer<Event> listener) {
        listeners.remove(listener);
    }
    
    /**
     * イベントを発行
     */
    public void publishEvent(Event event) {
        // イベントログに追加
        eventLog.add(event);
        
        // すべてのリスナーにイベントを通知
        listeners.forEach(listener -> {
            try {
                listener.accept(event);
                processedCount++;
            } catch (Exception e) {
                errorCount++;
                System.err.println("Error processing event: " + e.getMessage());
            }
        });
    }
    
    /**
     * 便利メソッド：イベントタイプとデータを指定してイベントを発行
     */
    public void publishEvent(String type, String data) {
        publishEvent(new Event(type, data));
    }
    
    /**
     * 特定のイベントタイプのみを処理するリスナーを作成
     */
    public Consumer<Event> createTypeSpecificListener(String eventType, Consumer<Event> handler) {
        return event -> {
            if (eventType.equals(event.getType())) {
                handler.accept(event);
            }
        };
    }
    
    /**
     * 条件を満たすイベントのみを処理するリスナーを作成
     */
    public Consumer<Event> createConditionalListener(Predicate<Event> condition, Consumer<Event> handler) {
        return event -> {
            if (condition.test(event)) {
                handler.accept(event);
            }
        };
    }
    
    /**
     * イベントを変換してから処理するリスナーを作成
     */
    public <T> Consumer<Event> createTransformingListener(Function<Event, T> transformer, Consumer<T> handler) {
        return event -> {
            T transformed = transformer.apply(event);
            handler.accept(transformed);
        };
    }
    
    /**
     * 複数のリスナーを組み合わせる
     */
    public Consumer<Event> combineListeners(Consumer<Event>... listeners) {
        return event -> {
            for (Consumer<Event> listener : listeners) {
                listener.accept(event);
            }
        };
    }
    
    /**
     * 統計情報を取得
     */
    public void printStatistics() {
        System.out.println("Event Statistics:");
        System.out.println("  Total events: " + eventLog.size());
        System.out.println("  Processed: " + processedCount);
        System.out.println("  Errors: " + errorCount);
        System.out.println("  Active listeners: " + listeners.size());
    }
    
    /**
     * 特定のタイプのイベントを取得
     */
    public List<Event> getEventsByType(String type) {
        return eventLog.stream()
            .filter(event -> type.equals(event.getType()))
            .collect(Collectors.toList());
    }
    
    /**
     * 最新のイベントを取得
     */
    public List<Event> getRecentEvents(int count) {
        return eventLog.stream()
            .skip(Math.max(0, eventLog.size() - count))
            .collect(Collectors.toList());
    }
    
    /**
     * イベントログをクリア
     */
    public void clearEventLog() {
        eventLog.clear();
        processedCount = 0;
        errorCount = 0;
    }
    
    /**
     * 遅延実行リスナーを作成
     */
    public Consumer<Event> createDelayedListener(Consumer<Event> handler, long delayMillis) {
        return event -> {
            Thread delayThread = new Thread(() -> {
                try {
                    Thread.sleep(delayMillis);
                    handler.accept(event);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            delayThread.start();
        };
    }
    
    /**
     * バッチ処理用のリスナーを作成
     */
    public static class BatchEventProcessor {
        private final List<Event> batch = new ArrayList<>();
        private final int batchSize;
        private final Consumer<List<Event>> batchHandler;
        
        public BatchEventProcessor(int batchSize, Consumer<List<Event>> batchHandler) {
            this.batchSize = batchSize;
            this.batchHandler = batchHandler;
        }
        
        public Consumer<Event> getListener() {
            return event -> {
                synchronized (batch) {
                    batch.add(event);
                    if (batch.size() >= batchSize) {
                        batchHandler.accept(new ArrayList<>(batch));
                        batch.clear();
                    }
                }
            };
        }
        
        public void flush() {
            synchronized (batch) {
                if (!batch.isEmpty()) {
                    batchHandler.accept(new ArrayList<>(batch));
                    batch.clear();
                }
            }
        }
    }
    
    /**
     * 頻度制限付きリスナーを作成
     */
    public static class RateLimitedListener {
        private final Consumer<Event> handler;
        private final long intervalMillis;
        private long lastExecutionTime = 0;
        
        public RateLimitedListener(Consumer<Event> handler, long intervalMillis) {
            this.handler = handler;
            this.intervalMillis = intervalMillis;
        }
        
        public Consumer<Event> getListener() {
            return event -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastExecutionTime >= intervalMillis) {
                    handler.accept(event);
                    lastExecutionTime = currentTime;
                }
            };
        }
    }
    
    /**
     * 事前定義されたリスナーファクトリ
     */
    public static class ListenerFactory {
        
        /**
         * コンソールにログを出力するリスナー
         */
        public static Consumer<Event> createConsoleLogger() {
            return event -> System.out.println("[LOG] " + event);
        }
        
        /**
         * エラーイベントのみを処理するリスナー
         */
        public static Consumer<Event> createErrorLogger() {
            return event -> {
                if ("ERROR".equals(event.getType())) {
                    System.err.println("[ERROR] " + event.getData());
                }
            };
        }
        
        /**
         * 統計情報を更新するリスナー
         */
        public static Consumer<Event> createStatisticsCollector() {
            return event -> {
                // 統計情報の更新ロジック
                System.out.println("[STATS] Event processed: " + event.getType());
            };
        }
        
        /**
         * デバッグ情報を出力するリスナー
         */
        public static Consumer<Event> createDebugLogger() {
            return event -> {
                if ("DEBUG".equals(event.getType())) {
                    System.out.println("[DEBUG] " + event.getData() + " at " + event.getTimestamp());
                }
            };
        }
    }
}