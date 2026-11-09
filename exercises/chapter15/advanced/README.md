# 第15章 応用課題

## 🎯 学習目標
- 高度なJavaプログラミング技術統合
- 企業レベルのアプリケーション開発
- 複合的な技術要素の組み合わせ
- 実務レベルの問題解決
- アーキテクチャ設計の実践

## 📝 課題一覧

### 課題1: 統合ECプラットフォーム
**ファイル名**: `IntegratedECommercePlatform.java`

複数の技術要素を統合したエンタープライズ級ECプラットフォームを作成してください。

**要求仕様**:
- マイクロサービスアーキテクチャ
- イベント駆動設計
- CQRS + Event Sourcing
- 分散トランザクション処理

**システム構成**:
- ユーザー管理サービス
- 商品カタログサービス
- 注文処理サービス
- 在庫管理サービス
- 決済サービス
- 通知サービス

**実行例**:
```
=== 統合ECプラットフォーム ===

🛒 EnterpriseCommerce v3.0

=== システムアーキテクチャ ===
🏗️ マイクロサービス構成:

サービス一覧:
1. user-service (ユーザー管理)
   - Port: 8081
   - Database: PostgreSQL
   - Cache: Redis
   
2. catalog-service (商品カタログ)
   - Port: 8082
   - Database: MongoDB
   - Search: Elasticsearch
   
3. order-service (注文処理)
   - Port: 8083
   - Database: PostgreSQL
   - Event Store: EventStore DB
   
4. inventory-service (在庫管理)
   - Port: 8084
   - Database: PostgreSQL
   - Cache: Redis
   
5. payment-service (決済処理)
   - Port: 8085
   - Database: PostgreSQL
   - External: Stripe API
   
6. notification-service (通知)
   - Port: 8086
   - Queue: RabbitMQ
   - Email: SendGrid

インフラストラクチャ:
- API Gateway: Kong
- Service Mesh: Istio
- Message Broker: Apache Kafka
- Config Server: Spring Cloud Config
- Service Discovery: Consul
- Monitoring: Prometheus + Grafana
- Tracing: Jaeger

=== イベント駆動アーキテクチャ ===
📨 非同期イベント処理:

```java
// ドメインイベント定義
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = UserRegisteredEvent.class, name = "USER_REGISTERED"),
    @JsonSubTypes.Type(value = OrderPlacedEvent.class, name = "ORDER_PLACED"),
    @JsonSubTypes.Type(value = PaymentProcessedEvent.class, name = "PAYMENT_PROCESSED"),
    @JsonSubTypes.Type(value = InventoryUpdatedEvent.class, name = "INVENTORY_UPDATED")
})
public abstract class DomainEvent {
    private final String eventId;
    private final Instant timestamp;
    private final String aggregateId;
    private final Long version;
    
    protected DomainEvent(String aggregateId, Long version) {
        this.eventId = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
        this.aggregateId = aggregateId;
        this.version = version;
    }
}

// 注文処理サービス
@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final EventStore eventStore;
    private final EventPublisher eventPublisher;
    private final SagaOrchestrator sagaOrchestrator;
    
    public OrderResult placeOrder(PlaceOrderCommand command) {
        try {
            // 1. コマンド検証
            CommandValidation validation = validateCommand(command);
            if (!validation.isValid()) {
                return OrderResult.failure(validation.getErrors());
            }
            
            // 2. 注文アグリゲート作成
            Order order = Order.create(
                command.getCustomerId(),
                command.getOrderItems(),
                command.getShippingAddress()
            );
            
            // 3. ビジネスルール適用
            BusinessRuleEngine ruleEngine = new BusinessRuleEngine();
            RuleValidationResult ruleResult = ruleEngine.validate(order);
            
            if (!ruleResult.isValid()) {
                return OrderResult.failure(ruleResult.getViolations());
            }
            
            // 4. イベント生成
            List<DomainEvent> events = order.getUncommittedEvents();
            
            // 5. Event Sourcing - イベント永続化
            eventStore.saveEvents(order.getId(), events, order.getVersion());
            
            // 6. 読み取りモデル更新
            updateReadModel(order);
            
            // 7. イベント発行
            events.forEach(eventPublisher::publish);
            
            // 8. Saga開始 (分散トランザクション)
            SagaDefinition orderSaga = createOrderProcessingSaga(order);
            sagaOrchestrator.startSaga(orderSaga);
            
            logger.info("注文作成完了: orderId={}, customerId={}", 
                order.getId(), command.getCustomerId());
            
            return OrderResult.success(order.getId());
            
        } catch (Exception e) {
            logger.error("注文作成エラー", e);
            return OrderResult.failure("注文処理中にエラーが発生しました: " + e.getMessage());
        }
    }
    
    private SagaDefinition createOrderProcessingSaga(Order order) {
        return SagaDefinition.builder()
            .sagaId(UUID.randomUUID().toString())
            .sagaType("OrderProcessingSaga")
            .step("ReserveInventory")
                .invokeParticipant("inventory-service")
                .withCommand(new ReserveInventoryCommand(order.getOrderItems()))
                .withCompensation(new ReleaseInventoryCommand(order.getOrderItems()))
            .step("ProcessPayment")
                .invokeParticipant("payment-service")
                .withCommand(new ProcessPaymentCommand(
                    order.getCustomerId(), 
                    order.getTotalAmount(),
                    order.getPaymentMethod()))
                .withCompensation(new RefundPaymentCommand(order.getId()))
            .step("UpdateInventory")
                .invokeParticipant("inventory-service")
                .withCommand(new UpdateInventoryCommand(order.getOrderItems()))
                .withCompensation(new RestoreInventoryCommand(order.getOrderItems()))
            .step("SendNotification")
                .invokeParticipant("notification-service")
                .withCommand(new SendOrderConfirmationCommand(
                    order.getCustomerId(), 
                    order.getId()))
            .build();
    }
}

// CQRS実装 - コマンドハンドラー
@Component
public class OrderCommandHandler {
    
    @EventSourcingHandler
    public void handle(PlaceOrderCommand command) {
        Order order = Order.create(
            command.getCustomerId(),
            command.getOrderItems(),
            command.getShippingAddress()
        );
        
        repository.save(order);
    }
    
    @EventSourcingHandler
    public void handle(UpdateOrderStatusCommand command) {
        Order order = repository.load(command.getOrderId());
        order.updateStatus(command.getNewStatus());
        repository.save(order);
    }
    
    @EventSourcingHandler
    public void handle(CancelOrderCommand command) {
        Order order = repository.load(command.getOrderId());
        order.cancel(command.getReason());
        repository.save(order);
    }
}

// CQRS実装 - クエリハンドラー
@Component
public class OrderQueryHandler {
    private final OrderProjectionRepository projectionRepository;
    
    public OrderDetails getOrderDetails(String orderId) {
        OrderProjection projection = projectionRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        return OrderDetails.builder()
            .orderId(projection.getOrderId())
            .customerId(projection.getCustomerId())
            .status(projection.getStatus())
            .orderItems(projection.getOrderItems())
            .totalAmount(projection.getTotalAmount())
            .createdAt(projection.getCreatedAt())
            .build();
    }
    
    public PagedResult<OrderSummary> getCustomerOrders(String customerId, 
                                                      Pageable pageable) {
        Page<OrderProjection> projections = projectionRepository
            .findByCustomerId(customerId, pageable);
        
        List<OrderSummary> summaries = projections.getContent().stream()
            .map(this::toOrderSummary)
            .collect(Collectors.toList());
        
        return new PagedResult<>(summaries, projections.getTotalElements());
    }
}

// Event Sourcing - イベントストア
@Repository
public class EventStore {
    private final JdbcTemplate jdbcTemplate;
    private final EventSerializer eventSerializer;
    
    public void saveEvents(String aggregateId, List<DomainEvent> events, Long expectedVersion) {
        try {
            // 楽観的排他制御
            Long currentVersion = getCurrentVersion(aggregateId);
            if (!Objects.equals(currentVersion, expectedVersion)) {
                throw new ConcurrencyException(
                    "Expected version " + expectedVersion + 
                    " but was " + currentVersion);
            }
            
            // イベント保存
            String sql = "INSERT INTO event_store (aggregate_id, event_type, event_data, version, timestamp) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            List<Object[]> batchArgs = events.stream()
                .map(event -> new Object[]{
                    aggregateId,
                    event.getClass().getSimpleName(),
                    eventSerializer.serialize(event),
                    event.getVersion(),
                    event.getTimestamp()
                })
                .collect(Collectors.toList());
            
            jdbcTemplate.batchUpdate(sql, batchArgs);
            
            logger.debug("Saved {} events for aggregate {}", events.size(), aggregateId);
            
        } catch (DataAccessException e) {
            throw new EventStoreException("Failed to save events", e);
        }
    }
    
    public List<DomainEvent> getEvents(String aggregateId) {
        String sql = "SELECT event_type, event_data, version, timestamp " +
                    "FROM event_store WHERE aggregate_id = ? ORDER BY version";
        
        return jdbcTemplate.query(sql, new Object[]{aggregateId}, (rs, rowNum) -> {
            String eventType = rs.getString("event_type");
            String eventData = rs.getString("event_data");
            return eventSerializer.deserialize(eventData, eventType);
        });
    }
}

// Saga Orchestrator
@Service
public class SagaOrchestrator {
    private final SagaRepository sagaRepository;
    private final MessageSender messageSender;
    
    public void startSaga(SagaDefinition sagaDefinition) {
        try {
            // Saga状態初期化
            SagaInstance saga = new SagaInstance(
                sagaDefinition.getSagaId(),
                sagaDefinition.getSagaType(),
                SagaStatus.STARTED
            );
            
            sagaRepository.save(saga);
            
            // 最初のステップ実行
            SagaStep firstStep = sagaDefinition.getSteps().get(0);
            executeStep(saga, firstStep);
            
            logger.info("Saga started: sagaId={}, type={}", 
                saga.getSagaId(), saga.getSagaType());
            
        } catch (Exception e) {
            logger.error("Failed to start saga", e);
            throw new SagaException("Saga start failed", e);
        }
    }
    
    private void executeStep(SagaInstance saga, SagaStep step) {
        try {
            // ステップ状態更新
            saga.setCurrentStep(step.getName());
            saga.setStatus(SagaStatus.STEP_EXECUTING);
            sagaRepository.save(saga);
            
            // コマンド送信
            Command command = step.getCommand();
            String participant = step.getParticipant();
            
            messageSender.sendCommand(participant, command, 
                new SagaReplyHandler(saga.getSagaId(), step.getName()));
            
            logger.debug("Executing saga step: sagaId={}, step={}", 
                saga.getSagaId(), step.getName());
            
        } catch (Exception e) {
            // ステップ失敗 - 補償処理開始
            startCompensation(saga, step);
        }
    }
    
    private void startCompensation(SagaInstance saga, SagaStep failedStep) {
        logger.warn("Starting compensation for saga: sagaId={}, failedStep={}", 
            saga.getSagaId(), failedStep.getName());
        
        saga.setStatus(SagaStatus.COMPENSATING);
        sagaRepository.save(saga);
        
        // 実行済みステップの逆順で補償処理実行
        List<SagaStep> executedSteps = getExecutedSteps(saga);
        Collections.reverse(executedSteps);
        
        for (SagaStep step : executedSteps) {
            executeCompensation(saga, step);
        }
        
        saga.setStatus(SagaStatus.COMPENSATED);
        sagaRepository.save(saga);
    }
}
```

実行例:
```
=== 注文処理フロー実行ログ ===

注文作成要求:
顧客ID: CUST-12345
商品: [
  {id: "PROD-001", quantity: 2, price: 1200},
  {id: "PROD-005", quantity: 1, price: 3500}
]
配送先: 東京都渋谷区...
支払方法: クレジットカード

14:30:00.001 - PlaceOrderCommand受信
14:30:00.003 - コマンド検証開始
14:30:00.005 - ビジネスルール検証
14:30:00.007 - 注文アグリゲート作成
14:30:00.010 - OrderPlacedEvent生成
14:30:00.012 - Event Store保存
14:30:00.015 - 読み取りモデル更新
14:30:00.018 - イベント発行
14:30:00.020 - Saga開始

Saga実行ログ:
SagaId: SAGA-20240705-001
Type: OrderProcessingSaga

Step 1: ReserveInventory
14:30:00.025 - inventory-service呼び出し
14:30:00.067 - 在庫引当成功 (42ms)

Step 2: ProcessPayment  
14:30:00.070 - payment-service呼び出し
14:30:01.234 - 決済処理成功 (1.164s)

Step 3: UpdateInventory
14:30:01.237 - inventory-service呼び出し
14:30:01.289 - 在庫更新成功 (52ms)

Step 4: SendNotification
14:30:01.292 - notification-service呼び出し
14:30:01.456 - 通知送信成功 (164ms)

Saga完了:
14:30:01.460 - OrderProcessingSaga完了
総実行時間: 1.459秒
ステップ成功率: 100%
```

=== 分散トレーシング ===
```
Trace ID: 64b2c8f4a1e3d2b9
Span情報:

order-service [1.459s]
├── validate-command [2ms]
├── create-aggregate [3ms]
├── save-events [5ms]
├── publish-events [3ms]
└── start-saga [1.446s]
    ├── inventory-service::reserve [42ms]
    ├── payment-service::process [1.164s]
    ├── inventory-service::update [52ms]
    └── notification-service::send [164ms]

分散トレース統計:
総リクエスト: 1
成功: 1 (100%)
平均レスポンス時間: 1.459s
サービス間呼び出し: 4回
エラー率: 0%
```
```

### 課題2: リアルタイム分析プラットフォーム
**ファイル名**: `RealTimeAnalyticsPlatform.java`

大量データのリアルタイム分析を行うプラットフォームを作成してください。

**要求仕様**:
- ストリーミングデータ処理
- 複雑イベント処理 (CEP)
- 機械学習パイプライン
- リアルタイムダッシュボード

**システム機能**:
- データ収集・前処理
- ストリーム分析
- 異常検知
- 予測分析

**実行例**:
```
=== リアルタイム分析プラットフォーム ===

📊 AnalyticsEngine Pro v4.0

=== ストリーミングアーキテクチャ ===
🌊 大容量データ処理:

データソース:
- Webアクセスログ: 50,000 events/sec
- IoTセンサー: 100,000 events/sec  
- トランザクション: 5,000 events/sec
- ソーシャルメディア: 20,000 events/sec
総入力: 175,000 events/sec

処理パイプライン:
1. Data Ingestion (Apache Kafka)
2. Stream Processing (Apache Flink)
3. Complex Event Processing (Esper)
4. Machine Learning (Apache Spark ML)
5. Storage (ClickHouse + ElasticSearch)
6. Visualization (Grafana + Custom Dashboard)

=== 複雑イベント処理 ===
⚡ CEP実装:

```java
@Component
public class ComplexEventProcessor {
    private final EPServiceProvider epService;
    private final AnomalyDetector anomalyDetector;
    private final PatternMatcher patternMatcher;
    
    @PostConstruct
    public void initializeEPL() {
        // 異常検知パターン
        String anomalyQuery = 
            "SELECT * FROM UserEvent.win:time(5 min) " +
            "GROUP BY userId " +
            "HAVING COUNT(*) > 1000"; // 5分間で1000回以上のアクセス
        
        epService.getEPAdministrator()
            .createEPL(anomalyQuery)
            .addListener(this::handleAnomalousActivity);
        
        // セキュリティ脅威検知
        String securityQuery = 
            "SELECT a.userId, a.ipAddress, COUNT(*) as attemptCount " +
            "FROM LoginAttempt.win:time(1 min) a " +
            "WHERE a.success = false " +
            "GROUP BY a.userId, a.ipAddress " +
            "HAVING COUNT(*) >= 5"; // 1分間で5回以上の失敗
        
        epService.getEPAdministrator()
            .createEPL(securityQuery)
            .addListener(this::handleSecurityThreat);
        
        // ビジネス機会検知
        String opportunityQuery =
            "SELECT p.productId, SUM(p.viewCount) as totalViews, " +
            "       AVG(p.conversionRate) as avgConversion " +
            "FROM ProductEvent.win:time(1 hour) p " +
            "GROUP BY p.productId " +
            "HAVING SUM(p.viewCount) > 10000 AND AVG(p.conversionRate) < 0.01"; // 高閲覧・低転換
        
        epService.getEPAdministrator()
            .createEPL(opportunityQuery)
            .addListener(this::handleBusinessOpportunity);
        
        // システムパフォーマンス監視
        String performanceQuery =
            "SELECT s.serviceId, AVG(s.responseTime) as avgResponse, " +
            "       COUNT(*) as requestCount " +
            "FROM ServiceCall.win:time(30 sec) s " +
            "GROUP BY s.serviceId " +
            "HAVING AVG(s.responseTime) > 1000"; // 30秒間の平均が1秒超
        
        epService.getEPAdministrator()
            .createEPL(performanceQuery)
            .addListener(this::handlePerformanceIssue);
    }
    
    public void handleAnomalousActivity(EventBean[] newEvents, EventBean[] oldEvents) {
        for (EventBean event : newEvents) {
            String userId = (String) event.get("userId");
            Long eventCount = (Long) event.get("count(*)");
            
            AnomalyAlert alert = AnomalyAlert.builder()
                .alertId(UUID.randomUUID().toString())
                .userId(userId)
                .anomalyType(AnomalyType.HIGH_FREQUENCY_ACCESS)
                .severity(Severity.HIGH)
                .eventCount(eventCount)
                .detectedAt(Instant.now())
                .message(String.format("ユーザー %s が5分間で %d 回のアクセス", userId, eventCount))
                .build();
            
            // 異常検知アルゴリズム実行
            AnomalyScore score = anomalyDetector.calculateAnomalyScore(alert);
            alert.setAnomalyScore(score);
            
            // アラート送信
            alertService.sendAlert(alert);
            
            // 自動対応
            if (score.getScore() > 0.8) {
                // 高スコア異常 - 自動ブロック
                securityService.temporaryBlock(userId, Duration.ofMinutes(30));
            }
            
            logger.warn("異常活動検知: userId={}, count={}, score={}", 
                userId, eventCount, score.getScore());
        }
    }
    
    public void handleSecurityThreat(EventBean[] newEvents, EventBean[] oldEvents) {
        for (EventBean event : newEvents) {
            String userId = (String) event.get("userId");
            String ipAddress = (String) event.get("ipAddress");
            Long attemptCount = (Long) event.get("attemptCount");
            
            SecurityThreat threat = SecurityThreat.builder()
                .threatId(UUID.randomUUID().toString())
                .userId(userId)
                .ipAddress(ipAddress)
                .threatType(ThreatType.BRUTE_FORCE_ATTACK)
                .severity(Severity.CRITICAL)
                .attemptCount(attemptCount)
                .detectedAt(Instant.now())
                .build();
            
            // 脅威インテリジェンス確認
            ThreatIntelligence intel = threatIntelligenceService.analyze(threat);
            threat.setThreatIntelligence(intel);
            
            // 即座にブロック
            securityService.immediateBlock(ipAddress);
            
            // セキュリティチームに緊急通知
            securityAlertService.sendCriticalAlert(threat);
            
            logger.error("セキュリティ脅威検知: userId={}, ip={}, attempts={}", 
                userId, ipAddress, attemptCount);
        }
    }
}

// 機械学習パイプライン
@Component
public class MLPipeline {
    private final SparkSession spark;
    private final ModelRegistry modelRegistry;
    
    @Scheduled(fixedRate = 300000) // 5分毎
    public void executeMLPipeline() {
        try {
            // データ読み込み
            Dataset<Row> rawData = loadStreamingData();
            
            // 特徴量エンジニアリング
            Dataset<Row> features = performFeatureEngineering(rawData);
            
            // 予測実行
            List<MLPrediction> predictions = executePredictions(features);
            
            // 結果配信
            distributeResults(predictions);
            
        } catch (Exception e) {
            logger.error("ML pipeline execution failed", e);
        }
    }
    
    private List<MLPrediction> executePredictions(Dataset<Row> features) {
        List<MLPrediction> predictions = new ArrayList<>();
        
        // ユーザー行動予測
        Model userBehaviorModel = modelRegistry.getModel("user-behavior-v2.1");
        Dataset<Row> behaviorPredictions = userBehaviorModel.transform(features);
        predictions.addAll(extractPredictions(behaviorPredictions, "user_behavior"));
        
        // 売上予測
        Model salesModel = modelRegistry.getModel("sales-forecast-v1.8");
        Dataset<Row> salesPredictions = salesModel.transform(features);
        predictions.addAll(extractPredictions(salesPredictions, "sales_forecast"));
        
        // 異常検知
        Model anomalyModel = modelRegistry.getModel("anomaly-detection-v3.2");
        Dataset<Row> anomalyPredictions = anomalyModel.transform(features);
        predictions.addAll(extractPredictions(anomalyPredictions, "anomaly_detection"));
        
        // チャーン予測
        Model churnModel = modelRegistry.getModel("churn-prediction-v2.0");
        Dataset<Row> churnPredictions = churnModel.transform(features);
        predictions.addAll(extractPredictions(churnPredictions, "churn_prediction"));
        
        return predictions;
    }
}
```

実行例:
```
=== リアルタイム分析実行ログ ===

データ処理統計:
入力イベント数: 175,000 events/sec
処理済みイベント数: 174,837 events/sec
処理率: 99.91%
遅延: 平均23ms, P99: 89ms
スループット: 2.1GB/sec

CEP検知結果:
異常活動検知: 47件
- 高頻度アクセス: 23件
- 異常パターン: 12件  
- 疑わしい行動: 12件

セキュリティ脅威: 8件
- ブルートフォース攻撃: 5件
- DDoS攻撃の兆候: 2件
- 不正アクセス試行: 1件

ビジネス機会: 156件
- 高関心商品: 89件
- 価格最適化候補: 34件
- クロスセル機会: 33件

ML予測結果:
ユーザー行動予測: 精度94.7%
- 購入確率予測: 12,847件
- 離脱リスク予測: 3,421件

売上予測: 精度91.2%
- 時間別売上: ¥12,450,000 (予測)
- 商品別需要: 234商品分析

異常検知: 精度97.8%
- システム異常: 2件
- データ品質問題: 1件

処理パフォーマンス:
Kafka消費: 175,000 msg/sec
Flink処理: 平均23ms
Spark ML: バッチあたり2.3秒
ClickHouse書き込み: 89,000 rows/sec
```
```

### 課題3: IoTデバイス管理プラットフォーム
**ファイル名**: `IoTDeviceManagementPlatform.java`

大規模IoTデバイスを管理するプラットフォームを作成してください。

**要求仕様**:
- デバイス登録・管理
- リアルタイムデータ収集
- デバイス制御・設定更新
- 異常検知・アラート

**実行例**:
```
=== IoTデバイス管理プラットフォーム ===

🌐 IoTManager Enterprise v5.0

=== デバイス群管理 ===
📱 大規模IoT環境:

管理対象デバイス:
総デバイス数: 2,500,000台
オンライン: 2,487,352台 (99.49%)
オフライン: 12,648台 (0.51%)

デバイス種別:
- 温度センサー: 890,000台
- 湿度センサー: 654,000台  
- 照度センサー: 432,000台
- モーションセンサー: 298,000台
- スマートメーター: 156,000台
- カメラ: 70,000台

地理的分布:
- 日本: 1,200,000台
- アジア太平洋: 800,000台
- 北米: 300,000台
- ヨーロッパ: 200,000台

データ処理:
受信データ: 125,000,000 points/hour
処理データ: 124,876,234 points/hour
処理率: 99.90%
平均遅延: 45ms
```

## 🎯 習得すべき技術要素

### アーキテクチャパターン
- マイクロサービスアーキテクチャ
- イベント駆動アーキテクチャ
- CQRS + Event Sourcing
- Saga パターン

### 分散システム技術
- 分散トランザクション
- 分散合意アルゴリズム
- 分散キャッシュ
- 分散監視・トレーシング

### ビッグデータ・ストリーミング
- Apache Kafka
- Apache Flink
- Apache Spark
- Complex Event Processing

## 📚 参考リソース

- Microservices Patterns (Chris Richardson)
- Building Event-Driven Microservices (Adam Bellemare)
- Streaming Systems (Tyler Akidau)
- Designing Data-Intensive Applications (Martin Kleppmann)

## ⚠️ 重要な注意事項

これらの課題は企業レベルの複雑なシステムを扱います。実装時はセキュリティ、パフォーマンス、運用性を十分に考慮し、適切な監視とログ記録を実装してください。