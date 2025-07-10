# 付録B.4: 並行処理と分散システム

この付録では、Javaにおける並行処理の設計とアーキテクチャパターンについて詳細に解説します。

## B.4.1 並行オブジェクト指向とアクターモデル

> **対象読者**: 並行処理の設計に興味がある方  
> **前提知識**: スレッドとプロセスの基本概念  
> **関連章**: 第3章、第16章

## なぜ並行処理設計が重要なのか

### 従来の並行処理による実際の問題

**問題1: デッドロックによるシステム全停止**
```java
// 実際に発生したデッドロックパターン
public class BankingSystem {
    public void transfer(Account from, Account to, double amount) {
        synchronized(from) {  // ロック1取得
            synchronized(to) {  // ロック2取得 → デッドロック発生点
                from.withdraw(amount);
                to.deposit(amount);
            }
        }
    }
}
// Thread1: transfer(accountA, accountB, 100)
// Thread2: transfer(accountB, accountA, 200) 
// → デッドロック発生、全取引停止
```
**実際の影響**: 某銀行システムで30分間全取引停止、顧客からの苦情殺到

**問題2: 競合状態による不整合**
```java
// 在庫管理での競合状態
public class Inventory {
    private int stock = 100;
    
    public boolean purchase(int quantity) {
        if (stock >= quantity) {  // Thread1,2が同時にチェック
            // 他スレッドがstock変更可能
            stock -= quantity;    // 不正な減算が発生
            return true;
        }
        return false;
    }
}
// 結果：在庫100個なのに150個売れてしまう
```
**影響**: ECサイトで在庫オーバー、顧客への謝罪と補償で数百万円の損失

### ビジネスへの深刻な影響

**実際の障害事例**

並行処理の設計不備は、さまざまな業界で深刻な問題を引き起こしています。証券取引システムでは、競合状態による取引価格計算エラーが発生し、誤った取引約定に至りました。予約システムでは、同時予約処理により座席のダブルブッキングが発生し、深刻な顧客トラブルとなりました。ゲームサーバでは、レースコンディションによりプレイヤーデータが破損し、ユーザーの信頼を大きく損ないました。

**並行処理問題がもたらすコスト**

これらの並行処理に関する問題は、組織に重大なコストをもたらします。デバッグの観点では、再現が困難なバグのため調査時間が通常の10-50倍に膨らみます。サービス運用では、デッドロックにより数時間のシステム停止が発生することがあります。特に金融・決済システムでは、データ不整合により重大インシデントとなり、社会的な信用問題に発展する可能性もあります。

**アクターモデルがもたらす効果**

アクターモデルは、これらの問題を根本的に解決します。メッセージパッシングによる通信により、デッドロックを根本的に回避できます。各アクター間で状態を共有しないため、競合状態を完全に排除できます。また、1つのアクターに障害が発生しても、ほかのアクターに波及しない障害分離が実現されます。さらに、分散環境での自然な拡張により、優れたスケーラビリティを提供します。

**実際の成功事例**

アクターモデルの導入により、多くの組織が顕著な成果を上げています。某ゲーム会社では、Akkaフレームワークの導入により同時接続数を10倍に増加させましたが、必要なサーバ台数は2倍のみという効率的な拡張を実現しました。メッセージングアプリケーションでは、アクターモデルにより99.99%という高い可用性を達成しました。IoTプラットフォームでは、数百万台のデバイスからの同時処理を安定して運用できるようになりました。



### アクターモデルの概要

**並行処理アーキテクチャの革新的アプローチ**

アクターモデルは、従来のスレッドベース並行処理の問題を根本的に解決する設計パラダイムです：

**従来のスレッドベース並行処理の課題**：
- 共有状態によるデッドロック発生
- 複雑なロック機構による保守困難性
- 競合状態（Race Condition）による予期しない動作
- デバッグとテストの極端な困難性

**アクターモデルの技術的特徴**：
- **メッセージパッシング**: 共有状態を排除した通信方式
- **カプセル化**: 各アクターが独立した状態と処理を保持
- **非同期処理**: ノンブロッキングなメッセージ交換
- **障害分離**: 1つのアクター障害がほかに波及しない設計

**実装における利点**：
- **デッドロック回避**: メッセージベース通信によるロック不要
- **スケーラビリティ**: 分散環境での自然な拡張
- **保守性**: 各アクターの独立性による変更容易性
- **テスト性**: メッセージ入出力による明確なテスト境界
                sender().tell(new DepositResult(balance), self());
            })
            .match(Transfer.class, msg -> {
                if (balance >= msg.amount) {
                    balance -= msg.amount;
                    msg.target.tell(new Deposit(msg.amount), self());
                    sender().tell(TransferResult.success(), self());
                } else {
                    sender().tell(TransferResult.insufficientFunds(), self());
                }
            })
            .build();
    }
}
```

アクターモデルでは、各アクターは独立したプロセスとして動作し、メッセージパッシングによって通信します。これにより、共有状態の問題を回避し、より安全な並行処理を実現できます。

### Javaにおける並行処理の進化

#### 従来のThread API

```java
// 従来のThread実装（推奨されない）
public class LegacyThreadExample {
    public void processData() {
        Thread worker = new Thread(new Runnable() {
            @Override
            public void run() {
                // 処理内容
                performHeavyComputation();
            }
        });
        worker.start();
        
        try {
            worker.join();  // 完了待ち
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

#### ExecutorServiceの導入

```java
// 現代的なExecutorService実装
public class ModernConcurrencyExample {
    private final ExecutorService executor = 
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    
    public CompletableFuture<String> processDataAsync(String data) {
        return CompletableFuture.supplyAsync(() -> {
            // 非同期処理
            return performHeavyComputation(data);
        }, executor);
    }
    
    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
```

### リアクティブプログラミング

#### RxJavaによる非同期処理

```java
// RxJavaを使った非同期データ処理
public class ReactiveExample {
    public Observable<ProcessedData> processStream(Observable<RawData> input) {
        return input
            .filter(data -> data.isValid())
            .map(this::transform)
            .flatMap(this::enrichWithExternalData)
            .retry(3)
            .timeout(5, TimeUnit.SECONDS)
            .observeOn(Schedulers.computation());
    }
    
    private Observable<ProcessedData> enrichWithExternalData(TransformedData data) {
        return Observable.fromCallable(() -> {
            // 外部APIからデータを取得
            ExternalData external = externalService.fetch(data.getId());
            return new ProcessedData(data, external);
        }).subscribeOn(Schedulers.io());
    }
}
```

### プロジェクトLoom: Virtual Threads

```java
// Virtual Threads（Java 19+のプレビュー機能）
public class VirtualThreadExample {
    public void processMany() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<String>> futures = new ArrayList<>();
            
            // 大量のタスクを軽量スレッドで実行
            for (int i = 0; i < 100_000; i++) {
                final int taskId = i;
                Future<String> future = executor.submit(() -> {
                    // I/Oバウンドなタスク
                    return performNetworkCall(taskId);
                });
                futures.add(future);
            }
            
            // 結果を収集
            for (Future<String> future : futures) {
                try {
                    String result = future.get();
                    processResult(result);
                } catch (ExecutionException | InterruptedException e) {
                    handleError(e);
                }
            }
        }
    }
}
```

### 分散システムでのJavaアプリケーション設計

#### マイクロサービスアーキテクチャ

```java
// Spring Boot マイクロサービス例
@RestController
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping("/orders")
    public CompletableFuture<ResponseEntity<OrderResponse>> createOrder(
            @RequestBody OrderRequest request) {
        
        return orderService.processOrderAsync(request)
            .thenApply(order -> ResponseEntity.ok(new OrderResponse(order)))
            .exceptionally(error -> {
                log.error("Order processing failed", error);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            });
    }
}

@Service
public class OrderService {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private InventoryService inventoryService;
    
    @Async
    public CompletableFuture<Order> processOrderAsync(OrderRequest request) {
        return CompletableFuture
            .supplyAsync(() -> validateOrder(request))
            .thenCompose(order -> inventoryService.reserveItems(order.getItems())
                .thenApply(reservation -> order.withReservation(reservation)))
            .thenCompose(order -> paymentService.processPayment(order.getPayment())
                .thenApply(payment -> order.withPayment(payment)))
            .thenApply(this::finalizeOrder);
    }
}
```

#### イベント駆動アーキテクチャ

```java
// Apache Kafkaを使ったイベント駆動システム
@Component
public class OrderEventProducer {
    
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    public void publishOrderCreated(Order order) {
        OrderEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getItems(),
            Instant.now()
        );
        
        kafkaTemplate.send("order-events", event.getOrderId(), event);
    }
}

@KafkaListener(topics = "order-events")
@Component
public class InventoryEventConsumer {
    
    @Autowired
    private InventoryService inventoryService;
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            inventoryService.updateInventory(event.getItems());
            log.info("Inventory updated for order: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to update inventory for order: {}", 
                     event.getOrderId(), e);
            // デッドレターキューへ送信
            publishToDeadLetterQueue(event, e);
        }
    }
}
```

### サーキットブレーカーパターン

```java
// Resilience4jを使ったサーキットブレーカー
@Component
public class ExternalApiClient {
    
    private final CircuitBreaker circuitBreaker;
    private final WebClient webClient;
    
    public ExternalApiClient() {
        this.circuitBreaker = CircuitBreaker.ofDefaults("externalApi");
        this.webClient = WebClient.builder()
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    }
    
    public Mono<ApiResponse> callExternalApi(String data) {
        Supplier<Mono<ApiResponse>> decoratedSupplier = 
            CircuitBreaker.decorateSupplier(circuitBreaker, () -> {
                return webClient.post()
                    .uri("https://external-api.example.com/process")
                    .body(BodyInserters.fromValue(data))
                    .retrieve()
                    .bodyToMono(ApiResponse.class)
                    .timeout(Duration.ofSeconds(5));
            });
        
        return decoratedSupplier.get()
            .doOnError(error -> log.error("External API call failed", error))
            .onErrorReturn(new ApiResponse("fallback"));
    }
}
```

### 並行処理でのパフォーマンス最適化

#### Lock-Free データ構造

```java
// ConcurrentHashMapを使った効率的な並行処理
public class ConcurrentCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final Function<K, V> valueLoader;
    
    public ConcurrentCache(Function<K, V> valueLoader) {
        this.valueLoader = valueLoader;
    }
    
    public V get(K key) {
        return cache.computeIfAbsent(key, valueLoader);
    }
    
    public void invalidate(K key) {
        cache.remove(key);
    }
    
    public void invalidateAll() {
        cache.clear();
    }
}

// AtomicReferenceを使った待ちなし更新
public class AtomicCounter {
    private final AtomicLong counter = new AtomicLong(0);
    
    public long incrementAndGet() {
        return counter.incrementAndGet();
    }
    
    public long addAndGet(long delta) {
        return counter.addAndGet(delta);
    }
    
    public boolean compareAndSet(long expect, long update) {
        return counter.compareAndSet(expect, update);
    }
}
```

#### CompleテーブルFutureの高度な活用

```java
// 複数の非同期操作の組み合わせ
public class AsyncOperationComposer {
    
    public CompletableFuture<CombinedResult> processDataParallel(String data) {
        CompletableFuture<ProcessedData> dataFuture = 
            CompletableFuture.supplyAsync(() -> processData(data));
        
        CompletableFuture<ValidationResult> validationFuture = 
            CompletableFuture.supplyAsync(() -> validateData(data));
        
        CompletableFuture<EnrichmentData> enrichmentFuture = 
            CompletableFuture.supplyAsync(() -> enrichData(data));
        
        return CompletableFuture.allOf(dataFuture, validationFuture, enrichmentFuture)
            .thenApply(v -> new CombinedResult(
                dataFuture.join(),
                validationFuture.join(),
                enrichmentFuture.join()
            ));
    }
    
    public CompletableFuture<String> processWithFallback(String data) {
        return CompletableFuture
            .supplyAsync(() -> primaryProcessor.process(data))
            .orTimeout(2, TimeUnit.SECONDS)
            .handle((result, throwable) -> {
                if (throwable != null) {
                    log.warn("Primary processing failed, using fallback", throwable);
                    return fallbackProcessor.process(data);
                }
                return result;
            });
    }
}
```

### パフォーマンス監視とメトリクス

```java
// Micrometerを使ったメトリクス収集
@Component
public class MetricsCollectingService {
    
    private final MeterRegistry meterRegistry;
    private final Timer requestTimer;
    private final Counter errorCounter;
    
    public MetricsCollectingService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.requestTimer = Timer.builder("request.duration")
            .description("Request processing time")
            .register(meterRegistry);
        this.errorCounter = Counter.builder("request.errors")
            .description("Request error count")
            .register(meterRegistry);
    }
    
    public CompletableFuture<String> processWithMetrics(String data) {
        Timer.Sample sample = Timer.start(meterRegistry);
        
        return CompletableFuture
            .supplyAsync(() -> performProcessing(data))
            .whenComplete((result, throwable) -> {
                sample.stop(requestTimer);
                if (throwable != null) {
                    errorCounter.increment();
                }
            });
    }
}
```

### 参考文献・関連資料
- "Java Concurrency in Practice" - Brian Goetz
- "Reactive Programming with RxJava" - Tomasz Nurkiewicz
- "Building Microservices" - Sam Newman
- "Designing Data-Intensive Applications" - Martin Kleppmann