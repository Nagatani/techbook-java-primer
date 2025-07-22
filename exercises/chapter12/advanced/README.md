# 第12章 応用課題

## 🎯 学習目標
- インターフェイスの高度な設計パターン
- 複雑な継承階層の管理
- ポリモーフィズムの実践活用
- アーキテクチャレベルの抽象化
- 拡張可能なシステム設計

## 📝 課題一覧

### 課題1: プラグイン型アーキテクチャシステム
**ファイル名**: `PluginArchitectureSystem.java`

動的プラグイン読み込みが可能な拡張可能システムを作成してください。

**要求仕様**:
- 動的クラスローディング
- プラグインライフサイクル管理
- 依存関係解決
- ホットプラグイン（実行時追加・削除）

**アーキテクチャ機能**:
- プラグインレジストリ
- イベントバス
- 設定管理
- セキュリティサンドボックス

**実行例**:
```
=== プラグイン型アーキテクチャシステム ===

🔌 PluginMaster Framework v3.0

=== システム初期化 ===
🚀 プラグインエコシステム起動:

コアシステム:
フレームワークバージョン: 3.0.1
Javaバージョン: 21
プラグインAPI: v2.5
セキュリティレベル: 高

プラグインディレクトリ:
- /plugins/core/ (コアプラグイン)
- /plugins/extensions/ (拡張プラグイン)  
- /plugins/third-party/ (サードパーティ)
- /plugins/development/ (開発用)

登録済みプラグイン:
総数: 47個
アクティブ: 32個
一時停止: 8個
エラー: 2個
未ロード: 5個

=== プラグイン管理 ===
⚙️ 動的プラグインシステム:

```java
public interface Plugin {
    // プラグイン基本情報
    PluginInfo getInfo();
    void initialize(PluginContext context) throws PluginException;
    void start() throws PluginException;
    void stop() throws PluginException;
    void destroy() throws PluginException;
    
    // プラグイン機能
    List<Service> getProvidedServices();
    List<Dependency> getRequiredDependencies();
    PluginConfiguration getConfiguration();
    
    // イベント処理
    void handleEvent(PluginEvent event);
    boolean canHandle(EventType eventType);
}

public class PluginManager {
    private final Map<String, LoadedPlugin> loadedPlugins = new ConcurrentHashMap<>();
    private final DependencyResolver dependencyResolver = new DependencyResolver();
    private final SecurityManager securityManager = new PluginSecurityManager();
    private final EventBus eventBus = new EventBus();
    
    public void loadPlugin(Path pluginPath) throws PluginLoadException {
        try {
            // プラグインJAR読み込み
            URLClassLoader pluginClassLoader = createIsolatedClassLoader(pluginPath);
            
            // プラグインマニフェスト解析
            PluginManifest manifest = parseManifest(pluginPath);
            
            // セキュリティ検証
            securityManager.validatePlugin(manifest, pluginPath);
            
            // プラグインクラス instantiation
            Class<?> pluginClass = pluginClassLoader.loadClass(manifest.getMainClass());
            Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            
            // 依存関係解決
            List<Dependency> dependencies = plugin.getRequiredDependencies();
            dependencyResolver.resolveDependencies(dependencies);
            
            // プラグイン初期化
            PluginContext context = createPluginContext(manifest);
            plugin.initialize(context);
            
            // プラグイン登録
            LoadedPlugin loadedPlugin = new LoadedPlugin(plugin, manifest, 
                pluginClassLoader, context);
            loadedPlugins.put(manifest.getId(), loadedPlugin);
            
            // イベント発火
            eventBus.post(new PluginLoadedEvent(manifest.getId()));
            
            logger.info("プラグイン読み込み成功: {} v{}", 
                manifest.getName(), manifest.getVersion());
            
        } catch (Exception e) {
            throw new PluginLoadException("プラグイン読み込み失敗: " + pluginPath, e);
        }
    }
    
    public void startPlugin(String pluginId) throws PluginException {
        LoadedPlugin loadedPlugin = loadedPlugins.get(pluginId);
        if (loadedPlugin == null) {
            throw new PluginNotFoundException("プラグイン未登録: " + pluginId);
        }
        
        try {
            // 依存プラグイン起動確認
            ensureDependenciesStarted(loadedPlugin);
            
            // プラグイン開始
            loadedPlugin.getPlugin().start();
            loadedPlugin.setState(PluginState.ACTIVE);
            
            // サービス登録
            registerProvidedServices(loadedPlugin);
            
            // イベント発火
            eventBus.post(new PluginStartedEvent(pluginId));
            
            logger.info("プラグイン開始: {}", pluginId);
            
        } catch (Exception e) {
            loadedPlugin.setState(PluginState.ERROR);
            throw new PluginException("プラグイン開始失敗: " + pluginId, e);
        }
    }
    
    public void unloadPlugin(String pluginId) throws PluginException {
        LoadedPlugin loadedPlugin = loadedPlugins.get(pluginId);
        if (loadedPlugin == null) {
            return; // 既に削除済み
        }
        
        try {
            // 依存しているプラグインを先に停止
            stopDependentPlugins(pluginId);
            
            // プラグイン停止
            if (loadedPlugin.getState() == PluginState.ACTIVE) {
                loadedPlugin.getPlugin().stop();
            }
            
            // プラグイン破棄
            loadedPlugin.getPlugin().destroy();
            
            // サービス登録解除
            unregisterProvidedServices(loadedPlugin);
            
            // クラスローダー解放
            loadedPlugin.getClassLoader().close();
            
            // プラグイン削除
            loadedPlugins.remove(pluginId);
            
            // イベント発火
            eventBus.post(new PluginUnloadedEvent(pluginId));
            
            logger.info("プラグイン削除: {}", pluginId);
            
        } catch (Exception e) {
            throw new PluginException("プラグイン削除失敗: " + pluginId, e);
        }
    }
}
```

プラグイン実行例:
```
=== データ処理プラグイン ===
プラグイン: DataProcessorPlugin v2.1
作者: DataCorp Inc.
説明: 高性能データ処理エンジン

機能:
- CSV/JSON/XML パーサー
- データ変換エンジン
- 統計計算機能
- 機械学習パイプライン

依存関係:
- CoreUtilities v1.5+
- MathLibrary v3.2+
- SecurityFramework v2.0+

提供サービス:
- DataParser Service
- DataTransformer Service  
- StatisticsCalculator Service
- MLPipeline Service

起動ログ:
[14:30:15] プラグイン初期化開始
[14:30:16] 依存関係解決完了
[14:30:17] セキュリティ検証通過
[14:30:18] サービス登録完了
[14:30:19] プラグイン起動完了

性能指標:
起動時間: 4.2秒
メモリ使用量: 245MB
CPU使用率: 12%
処理性能: 10,000 records/秒
```

=== ホットプラグイン ===
🔥 実行時プラグイン管理:

ホットプラグイン機能:
- ゼロダウンタイム追加
- 段階的ロードバランシング
- 設定の動的更新
- 自動ロールバック

実行時プラグイン追加例:
```
ホットプラグイン追加: SecurityEnhancer v1.3

追加プロセス:
14:45:00 - プラグイン配布開始
14:45:01 - セキュリティスキャン実行
14:45:02 - 依存関係検証
14:45:03 - 段階的ロード開始
14:45:04 - トラフィック分散調整 (10%)
14:45:05 - 動作確認完了
14:45:06 - トラフィック分散調整 (50%)
14:45:07 - 最終検証完了
14:45:08 - 全トラフィック移行 (100%)
14:45:09 - 旧プラグイン段階的停止
14:45:10 - ホットプラグイン完了

影響:
サービス停止時間: 0秒
レスポンス時間影響: +2ms (一時的)
エラー率: 0% (無影響)
ユーザー体験: 影響なし
```

動的設定更新:
```java
public class HotConfigurationManager {
    public void updatePluginConfiguration(String pluginId, 
            PluginConfiguration newConfig) throws ConfigurationException {
        try {
            LoadedPlugin plugin = pluginManager.getPlugin(pluginId);
            
            // 設定変更前の状態保存
            PluginConfiguration oldConfig = plugin.getConfiguration();
            ConfigurationSnapshot snapshot = createSnapshot(oldConfig);
            
            // 段階的設定適用
            ConfigurationApplier applier = new ConfigurationApplier();
            applier.applyGradually(plugin, newConfig, 
                Duration.ofSeconds(30)); // 30秒で段階適用
            
            // 動作監視
            HealthMonitor monitor = new HealthMonitor();
            HealthStatus status = monitor.monitorPlugin(plugin, 
                Duration.ofMinutes(2));
            
            if (status.isHealthy()) {
                // 設定変更成功 - スナップショット削除
                snapshot.delete();
                
                logger.info("設定更新成功: {} -> {}", 
                    pluginId, newConfig.getVersion());
            } else {
                // 設定変更失敗 - ロールバック
                rollbackConfiguration(plugin, snapshot);
                
                throw new ConfigurationException(
                    "設定更新失敗: ヘルスチェック不合格");
            }
            
        } catch (Exception e) {
            logger.error("動的設定更新エラー", e);
            throw new ConfigurationException("設定更新失敗", e);
        }
    }
}
```
```

### 課題2: 多層アーキテクチャ設計システム
**ファイル名**: `MultiLayerArchitectureSystem.java`

エンタープライズレベルの多層アーキテクチャシステムを作成してください。

**要求仕様**:
- レイヤー間の疎結合
- 横断的関心事の分離
- 依存性逆転の実装
- アスペクト指向プログラミング

**アーキテクチャレイヤー**:
- プレゼンテーション層
- アプリケーション層
- ドメイン層
- インフラストラクチャ層

**実行例**:
```
=== 多層アーキテクチャ設計システム ===

🏗️ Enterprise Architecture Framework v4.0

=== アーキテクチャ構成 ===
📐 レイヤー設計:

アーキテクチャ階層:
1. プレゼンテーション層 (UI)
   - Web UI (React)
   - REST API (Spring Boot)
   - GraphQL API
   - WebSocket

2. アプリケーション層 (Use Cases)
   - アプリケーションサービス
   - コマンドハンドラー
   - クエリハンドラー
   - ワークフロー管理

3. ドメイン層 (Business Logic)
   - エンティティ
   - 値オブジェクト
   - ドメインサービス
   - リポジトリインターフェイス

4. インフラストラクチャ層 (Technical)
   - データベースアクセス
   - 外部API連携
   - ファイルシステム
   - メッセージング

横断的関心事:
- ログ出力
- セキュリティ
- トランザクション管理
- キャッシュ管理
- メトリクス収集

=== ドメイン駆動設計 ===
🎯 ビジネスロジック中心設計:

```java
// ドメイン層 - エンティティ
public class Order {
    private final OrderId id;
    private final CustomerId customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private Money totalAmount;
    
    public Order(OrderId id, CustomerId customerId) {
        this.id = Objects.requireNonNull(id);
        this.customerId = Objects.requireNonNull(customerId);
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.totalAmount = Money.ZERO;
    }
    
    // ビジネスルール
    public void addItem(Product product, Quantity quantity) {
        if (status != OrderStatus.PENDING) {
            throw new IllegalStateException("確定済み注文は変更できません");
        }
        
        OrderItem item = new OrderItem(product, quantity);
        items.add(item);
        recalculateTotal();
        
        // ドメインイベント発行
        DomainEventPublisher.publish(new ItemAddedEvent(id, item));
    }
    
    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("空の注文は確定できません");
        }
        
        this.status = OrderStatus.CONFIRMED;
        
        // ドメインイベント発行
        DomainEventPublisher.publish(new OrderConfirmedEvent(id, totalAmount));
    }
    
    private void recalculateTotal() {
        this.totalAmount = items.stream()
            .map(item -> item.getProduct().getPrice().multiply(item.getQuantity()))
            .reduce(Money.ZERO, Money::add);
    }
}

// ドメイン層 - リポジトリインターフェイス
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
    List<Order> findByCustomerId(CustomerId customerId);
    List<Order> findByStatus(OrderStatus status);
}

// アプリケーション層 - アプリケーションサービス
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    
    public OrderApplicationService(OrderRepository orderRepository,
                                 ProductRepository productRepository,
                                 InventoryService inventoryService,
                                 PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }
    
    @Transactional
    public OrderId createOrder(CreateOrderCommand command) {
        try {
            // 注文作成
            OrderId orderId = OrderId.generate();
            Order order = new Order(orderId, command.getCustomerId());
            
            // 商品追加
            for (CreateOrderCommand.OrderItem item : command.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(item.getProductId()));
                
                // 在庫確認
                if (!inventoryService.isAvailable(item.getProductId(), item.getQuantity())) {
                    throw new InsufficientInventoryException(item.getProductId());
                }
                
                order.addItem(product, item.getQuantity());
            }
            
            // 注文保存
            orderRepository.save(order);
            
            return orderId;
            
        } catch (Exception e) {
            logger.error("注文作成エラー", e);
            throw new OrderCreationException("注文作成に失敗しました", e);
        }
    }
    
    @Transactional
    public void confirmOrder(ConfirmOrderCommand command) {
        try {
            // 注文取得
            Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(command.getOrderId()));
            
            // 在庫引当
            for (OrderItem item : order.getItems()) {
                inventoryService.reserve(item.getProduct().getId(), item.getQuantity());
            }
            
            // 支払い処理
            PaymentResult paymentResult = paymentService.processPayment(
                command.getPaymentInfo(), order.getTotalAmount());
            
            if (!paymentResult.isSuccessful()) {
                // 在庫引当解除
                releaseReservedInventory(order);
                throw new PaymentFailedException(paymentResult.getErrorMessage());
            }
            
            // 注文確定
            order.confirm();
            orderRepository.save(order);
            
            logger.info("注文確定完了: {}", command.getOrderId());
            
        } catch (Exception e) {
            logger.error("注文確定エラー", e);
            throw new OrderConfirmationException("注文確定に失敗しました", e);
        }
    }
}

// インフラストラクチャ層 - リポジトリ実装
@Repository
public class JpaOrderRepository implements OrderRepository {
    private final EntityManager entityManager;
    
    public JpaOrderRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @Override
    public void save(Order order) {
        OrderEntity entity = OrderMapper.toEntity(order);
        entityManager.merge(entity);
    }
    
    @Override
    public Optional<Order> findById(OrderId id) {
        OrderEntity entity = entityManager.find(OrderEntity.class, id.getValue());
        return Optional.ofNullable(entity)
            .map(OrderMapper::toDomain);
    }
    
    @Override
    public List<Order> findByCustomerId(CustomerId customerId) {
        TypedQuery<OrderEntity> query = entityManager.createQuery(
            "SELECT o FROM OrderEntity o WHERE o.customerId = :customerId", 
            OrderEntity.class);
        query.setParameter("customerId", customerId.getValue());
        
        return query.getResultList().stream()
            .map(OrderMapper::toDomain)
            .collect(Collectors.toList());
    }
}
```

実行結果:
```
=== 注文処理フロー ===

1. 注文作成リクエスト:
   顧客ID: CUST-001
   商品: [
     {商品ID: PROD-001, 数量: 2},
     {商品ID: PROD-005, 数量: 1}
   ]

2. ドメイン処理:
   - 注文エンティティ生成: ORDER-20240705-001
   - 商品情報取得: PROD-001 (¥1,200), PROD-005 (¥3,500)
   - 在庫確認: 在庫十分
   - 合計金額計算: ¥5,900

3. アプリケーション層処理:
   - トランザクション開始
   - ドメインルール検証
   - 在庫引当処理
   - データベース保存

4. 処理結果:
   注文ID: ORDER-20240705-001
   ステータス: PENDING
   処理時間: 127ms
   
=== 注文確定フロー ===

1. 確定リクエスト:
   注文ID: ORDER-20240705-001
   支払い情報: カード決済

2. ドメイン処理:
   - 注文状態検証: PENDING → CONFIRMED
   - ビジネスルール適用
   - ドメインイベント発行

3. 支払い処理:
   - 決済API呼び出し
   - 決済結果: 成功
   - 取引ID: TXN-789012

4. 在庫更新:
   - 仮引当 → 確定引当
   - 在庫数量更新

5. 処理結果:
   注文ステータス: CONFIRMED
   支払いステータス: COMPLETED
   処理時間: 1,847ms
```

=== アスペクト指向プログラミング ===
🔄 横断的関心事の分離:

AOP実装:
```java
@Aspect
@Component
public class CrossCuttingConcerns {
    
    @Around("@annotation(Auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        // 監査ログ開始
        AuditLogger.logMethodStart(methodName, args);
        
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 監査ログ成功
            AuditLogger.logMethodSuccess(methodName, result, executionTime);
            
            return result;
            
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 監査ログ失敗
            AuditLogger.logMethodFailure(methodName, e, executionTime);
            
            throw e;
        }
    }
    
    @Around("@annotation(Cacheable)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String cacheKey = generateCacheKey(joinPoint);
        
        // キャッシュ確認
        Object cachedResult = cacheManager.get(cacheKey);
        if (cachedResult != null) {
            logger.debug("キャッシュヒット: {}", cacheKey);
            return cachedResult;
        }
        
        // メソッド実行
        Object result = joinPoint.proceed();
        
        // キャッシュ保存
        cacheManager.put(cacheKey, result, Duration.ofMinutes(10));
        
        logger.debug("キャッシュ保存: {}", cacheKey);
        
        return result;
    }
    
    @Around("execution(* com.example.service.*.*(..))")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        
        Timer.Sample sample = Timer.start(meterRegistry);
        
        try {
            Object result = joinPoint.proceed();
            sample.stop(Timer.builder("method.execution")
                .tag("method", methodName)
                .tag("status", "success")
                .register(meterRegistry));
            
            return result;
            
        } catch (Exception e) {
            sample.stop(Timer.builder("method.execution")
                .tag("method", methodName)
                .tag("status", "error")
                .register(meterRegistry));
            
            throw e;
        }
    }
}
```

AOP適用例:
```
メソッド実行: OrderApplicationService.createOrder()

AOP処理チェーン:
1. パフォーマンス測定開始
2. 監査ログ記録開始
3. キャッシュ確認 (該当なし)
4. メソッド本体実行
5. 監査ログ記録完了
6. パフォーマンス測定完了

実行結果:
処理時間: 127ms
キャッシュ: 該当なし
監査ログ: 記録済み
メトリクス: 記録済み
```
```

### 課題3: マイクロサービス間通信フレームワーク
**ファイル名**: `MicroserviceCommunicationFramework.java`

マイクロサービス間の高性能通信フレームワークを作成してください。

**要求仕様**:
- 非同期メッセージング
- サーキットブレーカー
- ロードバランシング
- 分散トレーシング

**通信パターン**:
- 同期通信（REST、gRPC）
- 非同期通信（Message Queue）
- イベント駆動通信
- ストリーミング通信

**実行例**:
```
=== マイクロサービス間通信フレームワーク ===

🔗 ServiceMesh Connect v5.0

=== サービス構成 ===
🏢 マイクロサービスクラスター:

サービス一覧:
- user-service (3インスタンス)
- order-service (5インスタンス)
- payment-service (2インスタンス)
- inventory-service (4インスタンス)
- notification-service (2インスタンス)
- analytics-service (1インスタンス)

通信インフラ:
- Service Mesh: Istio
- Message Broker: Apache Kafka
- API Gateway: Kong
- Load Balancer: NGINX
- Service Discovery: Consul

ネットワーク構成:
内部通信: gRPC over HTTP/2
外部API: REST over HTTPS
非同期通信: Kafka (3 brokers)
監視: Prometheus + Grafana
トレーシング: Jaeger

=== 非同期メッセージング ===
📨 イベント駆動アーキテクチャ:

```java
// イベント発行者
@Service
public class OrderEventPublisher {
    private final EventBus eventBus;
    private final MessageProducer messageProducer;
    
    @EventHandler
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        try {
            // 在庫サービスへの減算イベント
            InventoryUpdateEvent inventoryEvent = new InventoryUpdateEvent(
                event.getOrderId(),
                event.getOrderItems(),
                InventoryOperation.DECREASE
            );
            
            // 支払いサービスへの請求イベント
            PaymentRequestEvent paymentEvent = new PaymentRequestEvent(
                event.getOrderId(),
                event.getCustomerId(),
                event.getTotalAmount()
            );
            
            // 通知サービスへの確認メールイベント
            NotificationEvent notificationEvent = new NotificationEvent(
                event.getCustomerId(),
                NotificationType.ORDER_CONFIRMATION,
                event.getOrderDetails()
            );
            
            // 非同期メッセージ送信
            CompletableFuture<Void> inventoryFuture = messageProducer.sendAsync(
                "inventory-updates", inventoryEvent);
            
            CompletableFuture<Void> paymentFuture = messageProducer.sendAsync(
                "payment-requests", paymentEvent);
            
            CompletableFuture<Void> notificationFuture = messageProducer.sendAsync(
                "notifications", notificationEvent);
            
            // 全送信完了待機
            CompletableFuture.allOf(inventoryFuture, paymentFuture, notificationFuture)
                .thenRun(() -> {
                    logger.info("全イベント送信完了: {}", event.getOrderId());
                })
                .exceptionally(throwable -> {
                    logger.error("イベント送信エラー", throwable);
                    return null;
                });
            
        } catch (Exception e) {
            logger.error("イベント発行エラー", e);
            
            // 補償トランザクション
            publishCompensationEvent(event);
        }
    }
}

// イベント購読者
@Service
public class InventoryEventSubscriber {
    
    @KafkaListener(topics = "inventory-updates")
    public void handleInventoryUpdate(InventoryUpdateEvent event) {
        try (MDCCloseable mdcCloseable = MDC.putCloseable("traceId", event.getTraceId())) {
            
            logger.info("在庫更新イベント受信: {}", event.getOrderId());
            
            // 在庫更新処理
            for (OrderItem item : event.getOrderItems()) {
                inventoryService.updateStock(
                    item.getProductId(), 
                    item.getQuantity(), 
                    event.getOperation());
            }
            
            // 成功イベント発行
            InventoryUpdatedEvent successEvent = new InventoryUpdatedEvent(
                event.getOrderId(), 
                InventoryUpdateStatus.SUCCESS);
            
            eventPublisher.publish(successEvent);
            
        } catch (InsufficientStockException e) {
            // 在庫不足の場合
            logger.warn("在庫不足: {}", e.getMessage());
            
            InventoryUpdatedEvent failureEvent = new InventoryUpdatedEvent(
                event.getOrderId(), 
                InventoryUpdateStatus.INSUFFICIENT_STOCK,
                e.getMessage());
            
            eventPublisher.publish(failureEvent);
            
        } catch (Exception e) {
            logger.error("在庫更新エラー", e);
            
            InventoryUpdatedEvent errorEvent = new InventoryUpdatedEvent(
                event.getOrderId(), 
                InventoryUpdateStatus.ERROR,
                e.getMessage());
            
            eventPublisher.publish(errorEvent);
        }
    }
}

// サーキットブレーカー実装
@Component
public class CircuitBreakerService {
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();
    
    public <T> T executeWithCircuitBreaker(String serviceName, 
                                         Supplier<T> operation, 
                                         Supplier<T> fallback) {
        CircuitBreaker circuitBreaker = getOrCreateCircuitBreaker(serviceName);
        
        return circuitBreaker.executeSupplier(() -> {
            try {
                return operation.get();
            } catch (Exception e) {
                logger.warn("サービス呼び出し失敗: {}", serviceName, e);
                throw e;
            }
        }).recover(throwable -> {
            logger.warn("サーキットブレーカー発動 - フォールバック実行: {}", serviceName);
            return fallback.get();
        });
    }
    
    private CircuitBreaker getOrCreateCircuitBreaker(String serviceName) {
        return circuitBreakers.computeIfAbsent(serviceName, name -> {
            CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50)  // 失敗率50%でOPEN
                .waitDurationInOpenState(Duration.ofSeconds(30))  // 30秒OPEN維持
                .slidingWindowSize(10)  // 直近10回の呼び出しを評価
                .minimumNumberOfCalls(5)  // 最低5回呼び出し後に評価開始
                .build();
            
            return CircuitBreaker.of(name, config);
        });
    }
}

// 分散トレーシング
@RestController
public class OrderController {
    
    @Autowired
    private Tracer tracer;
    
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        Span span = tracer.nextSpan()
            .name("create-order")
            .tag("service", "order-service")
            .tag("customer.id", request.getCustomerId())
            .start();
        
        try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
            
            // ユーザーサービス呼び出し
            Span userSpan = tracer.nextSpan()
                .name("get-customer")
                .tag("service", "user-service")
                .start();
            
            try (Tracer.SpanInScope userWs = tracer.withSpanInScope(userSpan)) {
                Customer customer = userServiceClient.getCustomer(request.getCustomerId());
                userSpan.tag("customer.found", "true");
            } catch (Exception e) {
                userSpan.tag("error", e.getMessage());
                throw e;
            } finally {
                userSpan.end();
            }
            
            // 在庫サービス呼び出し
            Span inventorySpan = tracer.nextSpan()
                .name("check-inventory")
                .tag("service", "inventory-service")
                .start();
            
            try (Tracer.SpanInScope inventoryWs = tracer.withSpanInScope(inventorySpan)) {
                boolean available = inventoryServiceClient.checkAvailability(request.getItems());
                inventorySpan.tag("inventory.available", String.valueOf(available));
                
                if (!available) {
                    throw new InsufficientInventoryException();
                }
            } finally {
                inventorySpan.end();
            }
            
            // 注文作成
            Order order = orderService.createOrder(request);
            span.tag("order.id", order.getId())
                .tag("order.amount", order.getTotalAmount().toString());
            
            return ResponseEntity.ok(new OrderResponse(order));
            
        } catch (Exception e) {
            span.tag("error", e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
```

通信パフォーマンス:
```
=== サービス間通信統計 ===

gRPC通信:
平均レイテンシ: 2.3ms
P95レイテンシ: 8.7ms
P99レイテンシ: 23.1ms
スループット: 15,000 RPS
エラー率: 0.05%

Kafka メッセージング:
平均配信時間: 1.2ms
メッセージ処理量: 50,000 msg/sec
パーティション数: 12
レプリケーション: 3

サーキットブレーカー統計:
CLOSED状態: 98.7%
OPEN状態: 1.1%  
HALF_OPEN状態: 0.2%
フォールバック実行: 1,247回

分散トレーシング:
トレース収集率: 95%
平均レスポンス時間: 127ms
最長トランザクション: 5.2秒
エラートレース: 0.8%
```

ロードバランシング:
```
=== 負荷分散状況 ===

order-service インスタンス:
- order-service-1: 21% (2,100 RPS)
- order-service-2: 19% (1,900 RPS)  
- order-service-3: 20% (2,000 RPS)
- order-service-4: 20% (2,000 RPS)
- order-service-5: 20% (2,000 RPS)

分散アルゴリズム: Weighted Round Robin
ヘルスチェック: 5秒間隔
故障検知時間: 15秒
自動復旧: 有効

サービスメッシュ統計:
総リクエスト: 50M/日
成功率: 99.95%
mTLS通信: 100%
ポリシー適用: 有効
```
```

## 🎯 習得すべき技術要素

### 設計原則の実践
- SOLID原則の適用
- DRY原則の実装
- インターフェイス分離
- 依存性逆転

### アーキテクチャパターン
- レイヤードアーキテクチャ
- ヘキサゴナルアーキテクチャ
- イベント駆動アーキテクチャ
- マイクロサービスアーキテクチャ

### 横断的関心事
- アスペクト指向プログラミング
- 依存性注入
- 設定管理
- ログ・監視

## 📚 参考リソース

- Clean Architecture (Robert C. Martin)
- Domain-Driven Design (Eric Evans)
- Building Microservices (Sam Newman)
- Enterprise Integration Patterns (Hohpe & Woolf)

## ⚠️ 重要な注意事項

エンタープライズシステムでは、パフォーマンス、スケーラビリティ、保守性のバランスを考慮した設計が重要です。実装前に要件を十分に分析してください。