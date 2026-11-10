# 第18章 応用課題

## 学習目標
- 高度なイベント処理アーキテクチャ
- 非同期・並行イベント処理
- カスタムイベントシステム設計
- リアルタイムイベント分析
- エンタープライズイベント駆動設計

## 課題一覧

### 課題1: 企業級イベント駆動アーキテクチャ
**ファイル名**: `EnterpriseEventDrivenArchitecture.java`

大規模システム向けの包括的なイベント駆動アーキテクチャを実装してください。

**要求仕様**:
- 非同期イベント処理
- イベントソーシング
- CQRS (Command Query Responsibility Segregation)
- 分散イベント処理

**システム構成**:
- イベントストア
- コマンドハンドラー
- イベントハンドラー
- プロジェクション管理

**実行例**:
```
=== 企業級イベント駆動アーキテクチャ ===

EventDriven Enterprise v3.0

=== イベントソーシングシステム ===
大容量イベント処理:

システム構成:
イベントストア: PostgreSQL + EventStore
メッセージブローカー: Apache Kafka
イベント処理: 4コア並行処理
スループット: 100,000 events/sec

イベント統計:
総イベント数: 12,847,562件
処理待ちイベント: 234件
エラーイベント: 12件 (0.0001%)
平均処理時間: 3.2ms

=== CQRS実装 ===
コマンド・クエリ分離:

```java
public class EnterpriseEventDrivenSystem {
    private final EventStore eventStore;
    private final CommandBus commandBus;
    private final EventBus eventBus;
    private final ProjectionManager projectionManager;
    private final EventProcessor eventProcessor;
    
    public EnterpriseEventDrivenSystem() {
        // イベントストア初期化
        this.eventStore = new PostgreSQLEventStore();
        
        // コマンド・イベントバス
        this.commandBus = new AsyncCommandBus();
        this.eventBus = new KafkaEventBus();
        
        // プロジェクション管理
        this.projectionManager = new ProjectionManager();
        
        // イベント処理エンジン
        this.eventProcessor = new ConcurrentEventProcessor(4);
        
        setupEventHandlers();
        startEventProcessing();
    }
    
    private void setupEventHandlers() {
        // コマンドハンドラー登録
        commandBus.registerHandler(CreateUserCommand.class, 
            new CreateUserCommandHandler(eventStore));
        commandBus.registerHandler(UpdateUserCommand.class,
            new UpdateUserCommandHandler(eventStore));
        commandBus.registerHandler(DeleteUserCommand.class,
            new DeleteUserCommandHandler(eventStore));
        
        // イベントハンドラー登録
        eventBus.subscribe(UserCreatedEvent.class,
            new UserCreatedEventHandler(projectionManager));
        eventBus.subscribe(UserUpdatedEvent.class,
            new UserUpdatedEventHandler(projectionManager));
        eventBus.subscribe(UserDeletedEvent.class,
            new UserDeletedEventHandler(projectionManager));
        
        // ドメインイベントハンドラー
        eventBus.subscribe(UserEmailChangedEvent.class,
            new SendEmailChangeNotificationHandler());
        eventBus.subscribe(UserPasswordChangedEvent.class,
            new SendSecurityNotificationHandler());
    }
    
    private void startEventProcessing() {
        // 非同期イベント処理開始
        eventProcessor.start();
        
        // イベントストリーム監視
        EventStreamMonitor monitor = new EventStreamMonitor(eventStore);
        monitor.addListener(this::handleNewEvents);
        monitor.start();
        
        // デッドレターキュー監視
        DeadLetterQueueMonitor dlqMonitor = new DeadLetterQueueMonitor();
        dlqMonitor.addListener(this::handleFailedEvents);
        dlqMonitor.start();
    }
    
    public CompletableFuture<CommandResult> executeCommand(Command command) {
        return commandBus.send(command)
            .thenApply(this::handleCommandResult)
            .exceptionally(this::handleCommandError);
    }
    
    private CommandResult handleCommandResult(Object result) {
        if (result instanceof DomainEvent[]) {
            DomainEvent[] events = (DomainEvent[]) result;
            
            // イベント永続化
            CompletableFuture<Void> storeEvents = 
                eventStore.saveEventsAsync(Arrays.asList(events));
            
            // イベント発行
            CompletableFuture<Void> publishEvents = storeEvents
                .thenCompose(v -> publishEventsAsync(events));
            
            return CommandResult.success(publishEvents);
        }
        return CommandResult.success();
    }
    
    private CompletableFuture<Void> publishEventsAsync(DomainEvent[] events) {
        List<CompletableFuture<Void>> publishTasks = Arrays.stream(events)
            .map(event -> eventBus.publishAsync(event))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(
            publishTasks.toArray(new CompletableFuture[0]));
    }
}

// 高性能イベントストア
public class PostgreSQLEventStore implements EventStore {
    private final DataSource dataSource;
    private final EventSerializer serializer;
    private final ExecutorService executorService;
    
    public PostgreSQLEventStore() {
        this.dataSource = createDataSource();
        this.serializer = new JsonEventSerializer();
        this.executorService = Executors.newFixedThreadPool(10);
    }
    
    @Override
    public CompletableFuture<Void> saveEventsAsync(List<DomainEvent> events) {
        return CompletableFuture.runAsync(() -> {
            try (Connection conn = dataSource.getConnection()) {
                conn.setAutoCommit(false);
                
                String sql = "INSERT INTO event_stream " +
                           "(aggregate_id, event_type, event_data, version, timestamp) " +
                           "VALUES (?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (DomainEvent event : events) {
                        stmt.setString(1, event.getAggregateId());
                        stmt.setString(2, event.getClass().getSimpleName());
                        stmt.setString(3, serializer.serialize(event));
                        stmt.setLong(4, event.getVersion());
                        stmt.setTimestamp(5, 
                            Timestamp.from(event.getTimestamp()));
                        stmt.addBatch();
                    }
                    
                    int[] results = stmt.executeBatch();
                    conn.commit();
                    
                    logger.info("Saved {} events", results.length);
                    
                } catch (SQLException e) {
                    conn.rollback();
                    throw new EventStoreException("Failed to save events", e);
                }
                
            } catch (SQLException e) {
                throw new EventStoreException("Database connection failed", e);
            }
        }, executorService);
    }
    
    @Override
    public CompletableFuture<List<DomainEvent>> getEventsAsync(
            String aggregateId, long fromVersion) {
        return CompletableFuture.supplyAsync(() -> {
            List<DomainEvent> events = new ArrayList<>();
            
            String sql = "SELECT event_type, event_data, version, timestamp " +
                        "FROM event_stream " +
                        "WHERE aggregate_id = ? AND version >= ? " +
                        "ORDER BY version";
            
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, aggregateId);
                stmt.setLong(2, fromVersion);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String eventType = rs.getString("event_type");
                        String eventData = rs.getString("event_data");
                        
                        DomainEvent event = serializer.deserialize(
                            eventData, eventType);
                        events.add(event);
                    }
                }
                
            } catch (SQLException e) {
                throw new EventStoreException("Failed to retrieve events", e);
            }
            
            return events;
        }, executorService);
    }
}

// 並行イベント処理エンジン
public class ConcurrentEventProcessor {
    private final int numberOfThreads;
    private final ExecutorService executorService;
    private final BlockingQueue<EventProcessingTask> taskQueue;
    private final EventProcessingMetrics metrics;
    
    public ConcurrentEventProcessor(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
        this.executorService = Executors.newFixedThreadPool(numberOfThreads);
        this.taskQueue = new LinkedBlockingQueue<>();
        this.metrics = new EventProcessingMetrics();
    }
    
    public void start() {
        // 処理スレッド開始
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(new EventProcessingWorker(i));
        }
        
        logger.info("Started {} event processing threads", numberOfThreads);
    }
    
    public CompletableFuture<Void> processEvent(DomainEvent event) {
        EventProcessingTask task = new EventProcessingTask(event);
        
        try {
            taskQueue.put(task);
            metrics.incrementQueuedEvents();
            return task.getCompletionFuture();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private class EventProcessingWorker implements Runnable {
        private final int workerId;
        
        public EventProcessingWorker(int workerId) {
            this.workerId = workerId;
        }
        
        @Override
        public void run() {
            Thread.currentThread().setName("EventProcessor-" + workerId);
            
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    EventProcessingTask task = taskQueue.take();
                    metrics.incrementProcessingEvents();
                    
                    long startTime = System.nanoTime();
                    
                    try {
                        processEventInternal(task.getEvent());
                        task.complete();
                        metrics.incrementSuccessfulEvents();
                        
                    } catch (Exception e) {
                        task.completeExceptionally(e);
                        metrics.incrementFailedEvents();
                        logger.error("Event processing failed", e);
                    }
                    
                    long duration = System.nanoTime() - startTime;
                    metrics.recordProcessingTime(duration);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        private void processEventInternal(DomainEvent event) throws Exception {
            // イベントハンドラー実行
            List<EventHandler> handlers = getEventHandlers(event.getClass());
            
            for (EventHandler handler : handlers) {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    logger.error("Handler {} failed for event {}", 
                        handler.getClass().getSimpleName(), 
                        event.getClass().getSimpleName(), e);
                    throw e;
                }
            }
        }
    }
}

// CQRS - コマンドハンドラー
@Component
public class CreateUserCommandHandler implements CommandHandler<CreateUserCommand> {
    private final UserRepository repository;
    private final EventStore eventStore;
    
    @Override
    public CompletableFuture<CommandResult> handle(CreateUserCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // ビジネスルール検証
                validateUserCreation(command);
                
                // ユーザーアグリゲート作成
                User user = User.create(
                    command.getUserId(),
                    command.getEmail(),
                    command.getName()
                );
                
                // ドメインイベント取得
                List<DomainEvent> events = user.getUncommittedEvents();
                
                // イベント永続化
                eventStore.saveEvents(command.getUserId(), events);
                
                // アグリゲート保存
                repository.save(user);
                
                return CommandResult.success(events);
                
            } catch (DomainException e) {
                return CommandResult.failure(e.getMessage());
            }
        });
    }
    
    private void validateUserCreation(CreateUserCommand command) {
        // メールアドレス重複チェック
        if (repository.existsByEmail(command.getEmail())) {
            throw new DomainException("Email already exists: " + command.getEmail());
        }
        
        // ビジネスルール検証
        UserBusinessRules.validateUserCreation(command);
    }
}

// プロジェクション管理
@Component
public class ProjectionManager {
    private final Map<Class<? extends DomainEvent>, List<ProjectionHandler>> handlers;
    private final ProjectionStore projectionStore;
    
    public ProjectionManager() {
        this.handlers = new ConcurrentHashMap<>();
        this.projectionStore = new ProjectionStore();
        setupProjectionHandlers();
    }
    
    private void setupProjectionHandlers() {
        // ユーザー一覧プロジェクション
        registerHandler(UserCreatedEvent.class, 
            new UserListProjectionHandler(projectionStore));
        registerHandler(UserUpdatedEvent.class,
            new UserListProjectionHandler(projectionStore));
        registerHandler(UserDeletedEvent.class,
            new UserListProjectionHandler(projectionStore));
        
        // ユーザー詳細プロジェクション
        registerHandler(UserCreatedEvent.class,
            new UserDetailProjectionHandler(projectionStore));
        registerHandler(UserProfileUpdatedEvent.class,
            new UserDetailProjectionHandler(projectionStore));
        
        // 統計プロジェクション
        registerHandler(UserCreatedEvent.class,
            new UserStatisticsProjectionHandler(projectionStore));
        registerHandler(UserDeletedEvent.class,
            new UserStatisticsProjectionHandler(projectionStore));
    }
    
    public CompletableFuture<Void> updateProjections(DomainEvent event) {
        List<ProjectionHandler> eventHandlers = handlers.get(event.getClass());
        
        if (eventHandlers == null || eventHandlers.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        
        List<CompletableFuture<Void>> updateTasks = eventHandlers.stream()
            .map(handler -> updateProjectionAsync(handler, event))
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(
            updateTasks.toArray(new CompletableFuture[0]));
    }
    
    private CompletableFuture<Void> updateProjectionAsync(
            ProjectionHandler handler, DomainEvent event) {
        return CompletableFuture.runAsync(() -> {
            try {
                handler.handle(event);
            } catch (Exception e) {
                logger.error("Projection update failed: {}", 
                    handler.getClass().getSimpleName(), e);
                throw new ProjectionException("Projection update failed", e);
            }
        });
    }
}
```

実行ログ:
```
=== イベント処理実行ログ ===

14:30:00.001 - システム初期化開始
14:30:00.015 - イベントストア接続確立
14:30:00.028 - コマンドバス初期化完了
14:30:00.041 - イベントバス初期化完了
14:30:00.055 - プロジェクション管理開始
14:30:00.068 - イベント処理エンジン開始
14:30:00.082 - システム起動完了

コマンド実行:
14:30:15.123 - CreateUserCommand受信 (userId: USR-001)
14:30:15.125 - ビジネスルール検証開始
14:30:15.128 - ユーザーアグリゲート作成
14:30:15.131 - UserCreatedEvent生成
14:30:15.134 - イベントストア保存開始
14:30:15.142 - イベントストア保存完了 (8ms)
14:30:15.144 - イベント発行開始
14:30:15.146 - イベント発行完了

イベント処理:
14:30:15.147 - UserCreatedEvent処理開始
14:30:15.148 - UserListProjection更新 (Thread-1)
14:30:15.149 - UserDetailProjection更新 (Thread-2)
14:30:15.150 - UserStatisticsProjection更新 (Thread-3)
14:30:15.152 - 通知メール送信 (Thread-4)
14:30:15.156 - 全プロジェクション更新完了

処理統計:
総処理時間: 33ms
イベント処理並行度: 4スレッド
プロジェクション更新: 3個
副次処理: 1個 (通知)
```
```

### 課題2: リアルタイムイベント分析システム
**ファイル名**: `RealTimeEventAnalyticsSystem.java`

大量のイベントをリアルタイムで分析・集計するシステムを作成してください。

**要求仕様**:
- ストリーミングイベント処理
- リアルタイム集計・統計
- 異常検知・アラート
- ダッシュボード表示

**分析機能**:
- 時系列分析
- パターン認識
- 予測分析
- 相関分析

**実行例**:
```
=== リアルタイムイベント分析システム ===

EventAnalytics Pro v2.0

=== ストリーミング分析エンジン ===
高速イベント処理:

処理統計:
入力レート: 50,000 events/sec
処理レート: 49,876 events/sec
処理率: 99.75%
遅延: 平均12ms, P99: 45ms

分析ウィンドウ:
- 1分間ウィンドウ: 3,000,000イベント
- 5分間ウィンドウ: 15,000,000イベント  
- 1時間ウィンドウ: 180,000,000イベント

=== 異常検知結果 ===
注意: 検知された異常:

14:30:15 - ログイン失敗率異常
通常: 2.3% → 現在: 15.7% (6.8倍)
影響範囲: AP-Tokyo-01サーバー
対応: セキュリティチーム通知済み

14:29:45 - API応答時間異常
通常: 120ms → 現在: 890ms (7.4倍)
影響範囲: payment-service
対応: オートスケーリング実行中

14:28:30 - トランザクション量急増
通常: 1,200件/min → 現在: 8,900件/min (7.4倍)
原因: キャンペーン開始
対応: 処理能力拡張完了

=== 予測分析 ===
次1時間の予測:

ユーザーアクセス: 
現在: 12,500人 → 予測: 18,700人 (+49.6%)
信頼度: 94.2%

トランザクション:
現在: 8,900件/h → 予測: 12,400件/h (+39.3%)
信頼度: 91.8%

システム負荷:
現在: CPU 67% → 予測: CPU 89% (警告レベル)
推奨: インスタンス追加
```

### 課題3: イベント駆動マイクロサービス統合
**ファイル名**: `EventDrivenMicroservicesIntegration.java`

複数のマイクロサービス間でのイベント駆動統合システムを作成してください。

**要求仕様**:
- サービス間イベント通信
- 分散トランザクション
- サーキットブレーカー
- サービスディスカバリー

**統合機能**:
- イベントルーティング
- 重複排除
- 順序保証
- 障害回復

**実行例**:
```
=== イベント駆動マイクロサービス統合 ===

MicroserviceHub v3.0

=== サービス統合マップ ===
分散システム構成:

接続サービス: 12個
- user-service (Port: 8081)
- order-service (Port: 8082)  
- payment-service (Port: 8083)
- inventory-service (Port: 8084)
- notification-service (Port: 8085)
- analytics-service (Port: 8086)
- recommendation-service (Port: 8087)
- review-service (Port: 8088)
- shipping-service (Port: 8089)
- fraud-detection-service (Port: 8090)
- loyalty-service (Port: 8091)
- support-service (Port: 8092)

イベントフロー:
Order Created → [Payment, Inventory, Notification]
Payment Completed → [Shipping, Loyalty, Analytics]
User Registered → [Notification, Recommendation, Analytics]
Fraud Detected → [Payment, Notification, Support]

メッセージング統計:
総メッセージ: 2,847,562件
成功: 2,844,891件 (99.91%)
失敗: 2,671件 (0.09%)
リトライ中: 1,234件
デッドレター: 156件

サーキットブレーカー状態:
- payment-service: CLOSED (正常)
- shipping-service: HALF_OPEN (回復中)
- fraud-detection-service: CLOSED (正常)
- recommendation-service: OPEN (障害中)
```

## 習得すべき技術要素

### イベント駆動アーキテクチャ
- イベントソーシング
- CQRS パターン  
- サーガパターン
- イベントストーミング

### 分散システム設計
- 非同期メッセージング
- 分散トランザクション
- 結果整合性
- 障害トレラント設計

### リアルタイム処理
- ストリーミング処理
- 複雑イベント処理 (CEP)
- 時系列データ分析
- 機械学習統合

## 参考リソース

- Building Event-Driven Microservices (Adam Bellemare)
- Microservices Patterns (Chris Richardson)  
- Streaming Systems (Tyler Akidau)
- Designing Event-Driven Systems (Ben Stopford)

## 重要な注意事項

イベント駆動システムは複雑性が高いため、適切な監視、ログ記録、テスト戦略が重要です。分散システムの特性を理解して実装してください。