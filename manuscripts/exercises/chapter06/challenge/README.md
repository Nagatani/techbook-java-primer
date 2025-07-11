# 第6章 チャレンジ課題：エンタープライズレベルの不変性設計

## 概要

本課題では、大規模エンタープライズシステムにおける不変性設計の実践に挑戦します。パフォーマンス、スケーラビリティ、保守性を考慮した実装を通じて、プロダクションレベルで要求される高度な技術を習得します。

## 学習目標

- 大規模システムにおける不変性のトレードオフ理解
- メモリ効率と不変性のバランス
- 分散環境での不変性設計
- リアクティブシステムでの活用
- 監査とコンプライアンスへの対応

## チャレンジ課題：金融取引システムの不変性設計

### システム概要

高頻度取引（HFT）にも対応可能な金融取引システムのコアコンポーネントを、完全な不変性を保ちながら実装してください。このシステムは、規制要件により全ての状態変更を監査可能にし、かつミリ秒単位のレイテンシ要件を満たす必要があります。

### コアドメインモデル

#### 1. 取引注文の不変モデル

```java
public final class Order {
    private final OrderId orderId;
    private final Instant timestamp;
    private final TraderId traderId;
    private final Symbol symbol;
    private final OrderType type;
    private final Side side;
    private final BigDecimal price;
    private final long quantity;
    private final OrderStatus status;
    private final ImmutableList<OrderEvent> events;
    private final long version;
    
    // メモリ効率のための内部表現
    private static final class CompactOrder {
        final long orderId;  // 8 bytes
        final long timestamp; // 8 bytes (epoch millis)
        final int traderId;   // 4 bytes
        final short symbolId; // 2 bytes
        final byte typeAndSide; // 1 byte (4 bits each)
        final long priceScaled; // 8 bytes (scaled decimal)
        final int quantity;   // 4 bytes
        final byte status;    // 1 byte
        // Total: 36 bytes vs hundreds for normal object
    }
    
    // ゼロコピー更新
    public Order updateStatus(OrderStatus newStatus) {
        if (this.status == newStatus) {
            return this; // 同一インスタンスを返す
        }
        
        // 状態遷移の検証
        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalStateTransitionException(
                String.format("Cannot transition from %s to %s", status, newStatus));
        }
        
        // イベントの追加
        OrderEvent event = new OrderStatusChangedEvent(
            orderId, Instant.now(), status, newStatus, version + 1);
        
        // 構造共有による効率的な更新
        return new Order(
            orderId, timestamp, traderId, symbol, type, side,
            price, quantity, newStatus, events.add(event), version + 1
        );
    }
    
    // 部分約定の処理
    public PartialFillResult partialFill(long filledQuantity, BigDecimal executionPrice) {
        if (filledQuantity <= 0 || filledQuantity > getRemainingQuantity()) {
            throw new IllegalArgumentException("Invalid fill quantity");
        }
        
        Fill fill = new Fill(
            new FillId(), orderId, Instant.now(), 
            filledQuantity, executionPrice, version + 1
        );
        
        OrderEvent event = new OrderFilledEvent(
            orderId, Instant.now(), fill, version + 1);
        
        Order updatedOrder = new Order(
            orderId, timestamp, traderId, symbol, type, side,
            price, quantity, 
            filledQuantity == getRemainingQuantity() ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED,
            events.add(event), version + 1
        );
        
        return new PartialFillResult(updatedOrder, fill);
    }
    
    // 高速な集計計算
    public OrderStatistics calculateStatistics() {
        // イベントストリームの並列処理
        return events.parallelStream()
            .collect(OrderStatistics.collector());
    }
}

// 注文イベントの階層
public abstract class OrderEvent {
    private final OrderId orderId;
    private final Instant timestamp;
    private final long version;
    
    // JSONシリアライゼーション用のタイプ情報
    public abstract String getEventType();
    public abstract JsonNode toJson();
}

// イベントの具体実装
public final class OrderFilledEvent extends OrderEvent {
    private final Fill fill;
    
    @Override
    public String getEventType() {
        return "ORDER_FILLED";
    }
    
    @Override
    public JsonNode toJson() {
        // 高速なJSONシリアライゼーション
        return JsonNodeFactory.instance.objectNode()
            .put("orderId", getOrderId().toString())
            .put("timestamp", getTimestamp().toEpochMilli())
            .put("version", getVersion())
            .put("fillId", fill.getFillId().toString())
            .put("quantity", fill.getQuantity())
            .put("price", fill.getPrice().toPlainString());
    }
}
```

#### 2. 注文書（Order Book）の不変実装

```java
public final class ImmutableOrderBook {
    private final Symbol symbol;
    private final ImmutableSortedMap<BigDecimal, PriceLevel> bids; // 買い注文
    private final ImmutableSortedMap<BigDecimal, PriceLevel> asks; // 売り注文
    private final long sequenceNumber;
    private final Instant lastUpdateTime;
    
    // 価格レベル（同一価格の注文群）
    public static final class PriceLevel {
        private final BigDecimal price;
        private final ImmutableList<Order> orders;
        private final long totalQuantity;
        
        // 注文の追加（FIFOを保証）
        public PriceLevel addOrder(Order order) {
            return new PriceLevel(
                price,
                orders.add(order),
                totalQuantity + order.getQuantity()
            );
        }
        
        // 注文の削除（O(n)だが実用上問題ない）
        public PriceLevel removeOrder(OrderId orderId) {
            int index = -1;
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getOrderId().equals(orderId)) {
                    index = i;
                    break;
                }
            }
            
            if (index == -1) {
                return this; // 変更なし
            }
            
            Order removed = orders.get(index);
            return new PriceLevel(
                price,
                orders.remove(index),
                totalQuantity - removed.getQuantity()
            );
        }
    }
    
    // 注文の追加（高速化のための最適化付き）
    public OrderBookUpdate addOrder(Order order) {
        ImmutableSortedMap<BigDecimal, PriceLevel> targetSide = 
            order.getSide() == Side.BUY ? bids : asks;
        
        PriceLevel level = targetSide.get(order.getPrice());
        PriceLevel newLevel = (level == null) 
            ? new PriceLevel(order.getPrice(), ImmutableList.of(order), order.getQuantity())
            : level.addOrder(order);
        
        ImmutableSortedMap<BigDecimal, PriceLevel> newSide = 
            targetSide.put(order.getPrice(), newLevel);
        
        ImmutableOrderBook newBook = new ImmutableOrderBook(
            symbol,
            order.getSide() == Side.BUY ? newSide : bids,
            order.getSide() == Side.BUY ? asks : newSide,
            sequenceNumber + 1,
            Instant.now()
        );
        
        // 変更内容を含む更新オブジェクトを返す
        return new OrderBookUpdate(
            this, newBook, 
            OrderBookUpdateType.ORDER_ADDED,
            order
        );
    }
    
    // マッチングエンジン（取引の約定）
    public MatchingResult match() {
        if (bids.isEmpty() || asks.isEmpty()) {
            return MatchingResult.noMatch();
        }
        
        BigDecimal bestBid = bids.firstKey();
        BigDecimal bestAsk = asks.firstKey();
        
        if (bestBid.compareTo(bestAsk) < 0) {
            return MatchingResult.noMatch(); // スプレッドがある
        }
        
        // 約定処理
        PriceLevel bidLevel = bids.get(bestBid);
        PriceLevel askLevel = asks.get(bestAsk);
        
        Order buyOrder = bidLevel.orders.get(0);
        Order sellOrder = askLevel.orders.get(0);
        
        long matchQuantity = Math.min(
            buyOrder.getRemainingQuantity(),
            sellOrder.getRemainingQuantity()
        );
        
        BigDecimal executionPrice = sellOrder.getTimestamp().isBefore(buyOrder.getTimestamp())
            ? sellOrder.getPrice() : buyOrder.getPrice();
        
        // 約定による新しい状態の生成
        PartialFillResult buyFill = buyOrder.partialFill(matchQuantity, executionPrice);
        PartialFillResult sellFill = sellOrder.partialFill(matchQuantity, executionPrice);
        
        // Order Bookの更新
        ImmutableOrderBook updatedBook = this
            .updateOrder(buyOrder, buyFill.getOrder())
            .updateOrder(sellOrder, sellFill.getOrder());
        
        Trade trade = new Trade(
            new TradeId(),
            buyFill.getFill(),
            sellFill.getFill(),
            executionPrice,
            matchQuantity,
            Instant.now()
        );
        
        return new MatchingResult(updatedBook, trade, true);
    }
    
    // スナップショット生成（監査用）
    public OrderBookSnapshot createSnapshot() {
        return new OrderBookSnapshot(
            symbol,
            sequenceNumber,
            lastUpdateTime,
            bids.values().stream()
                .flatMap(level -> level.orders.stream())
                .collect(ImmutableList.toImmutableList()),
            asks.values().stream()
                .flatMap(level -> level.orders.stream())
                .collect(ImmutableList.toImmutableList())
        );
    }
}
```

#### 3. 高性能な不変価格フィード

```java
public final class PriceFeed {
    private final Symbol symbol;
    private final ImmutableRingBuffer<Tick> ticks;
    private final TickAggregator aggregator;
    private final long windowSizeMillis;
    
    // 効率的なリングバッファ実装
    public static final class ImmutableRingBuffer<E> {
        private final Object[] elements;
        private final int capacity;
        private final int head;
        private final int size;
        
        public ImmutableRingBuffer<E> add(E element) {
            if (size < capacity) {
                // バッファに空きがある
                Object[] newElements = elements.clone();
                newElements[(head + size) % capacity] = element;
                return new ImmutableRingBuffer<>(
                    newElements, capacity, head, size + 1
                );
            } else {
                // バッファがフル（最古の要素を上書き）
                Object[] newElements = elements.clone();
                int newHead = (head + 1) % capacity;
                newElements[head] = element;
                return new ImmutableRingBuffer<>(
                    newElements, capacity, newHead, size
                );
            }
        }
        
        // O(1)でのストリーム生成
        public Stream<E> stream() {
            return IntStream.range(0, size)
                .mapToObj(i -> (E) elements[(head + i) % capacity]);
        }
    }
    
    // ティックデータ
    public static final class Tick {
        private final BigDecimal price;
        private final long volume;
        private final Instant timestamp;
        private final TickType type;
        
        // コンパクトな内部表現
        private final long compactData; // price and volume packed
    }
    
    // 価格フィードの更新
    public PriceFeed addTick(Tick tick) {
        ImmutableRingBuffer<Tick> newTicks = ticks.add(tick);
        
        // ウィンドウ内のティックのみを集計
        Instant cutoff = tick.getTimestamp().minusMillis(windowSizeMillis);
        
        TickStatistics stats = newTicks.stream()
            .filter(t -> t.getTimestamp().isAfter(cutoff))
            .collect(TickStatistics.collector());
        
        return new PriceFeed(
            symbol, newTicks, 
            aggregator.update(stats),
            windowSizeMillis
        );
    }
    
    // リアルタイム統計
    public MarketStatistics getStatistics() {
        return new MarketStatistics(
            aggregator.getVWAP(),
            aggregator.getVolume(),
            aggregator.getHigh(),
            aggregator.getLow(),
            aggregator.getVolatility()
        );
    }
}
```

#### 4. 分散環境での不変性設計

```java
// CRDTベースの分散Order Book
public final class DistributedOrderBook {
    private final NodeId nodeId;
    private final VectorClock vectorClock;
    private final ImmutableMap<NodeId, NodeOrderBook> nodeBooks;
    private final ConflictResolver resolver;
    
    // ノード固有のOrder Book
    private static final class NodeOrderBook {
        private final NodeId nodeId;
        private final ImmutableOrderBook localBook;
        private final VectorClock lastSeen;
        private final ImmutableList<OrderEvent> pendingEvents;
    }
    
    // 分散更新の適用
    public DistributedOrderBook applyRemoteUpdate(
            NodeId remoteNode, 
            OrderBookUpdate update, 
            VectorClock remoteClock) {
        
        // 因果関係の確認
        if (!vectorClock.canAccept(remoteClock)) {
            // 順序が保証されない更新は保留
            return withPendingUpdate(remoteNode, update, remoteClock);
        }
        
        // 競合の検出と解決
        if (hasConflict(update)) {
            update = resolver.resolve(this, update);
        }
        
        // 更新の適用
        NodeOrderBook nodeBook = nodeBooks.get(remoteNode);
        NodeOrderBook updatedNodeBook = nodeBook.applyUpdate(update, remoteClock);
        
        return new DistributedOrderBook(
            nodeId,
            vectorClock.merge(remoteClock),
            nodeBooks.put(remoteNode, updatedNodeBook),
            resolver
        );
    }
    
    // 最終的な一貫性のための収束
    public ImmutableOrderBook converge() {
        // 全ノードのOrder Bookをマージ
        return nodeBooks.values().stream()
            .map(NodeOrderBook::getLocalBook)
            .reduce(ImmutableOrderBook.empty(symbol), 
                    (book1, book2) -> merger.merge(book1, book2));
    }
}

// イベントの永続化とリプレイ
public final class EventJournal {
    private final Path journalPath;
    private final Compression compression;
    private final Encryption encryption;
    
    // 非同期書き込みのためのバッファ
    private final ImmutableList<OrderEvent> buffer;
    private final int bufferSize;
    
    public CompletableFuture<EventJournal> append(OrderEvent event) {
        ImmutableList<OrderEvent> newBuffer = buffer.add(event);
        
        if (newBuffer.size() >= bufferSize) {
            // バッファがフルになったら永続化
            return persistBatch(newBuffer)
                .thenApply(v -> new EventJournal(
                    journalPath, compression, encryption,
                    ImmutableList.empty(), bufferSize
                ));
        } else {
            return CompletableFuture.completedFuture(
                new EventJournal(
                    journalPath, compression, encryption,
                    newBuffer, bufferSize
                )
            );
        }
    }
    
    private CompletableFuture<Void> persistBatch(ImmutableList<OrderEvent> events) {
        return CompletableFuture.runAsync(() -> {
            try {
                // バイナリシリアライゼーション
                ByteBuffer buffer = serialize(events);
                
                // 圧縮
                if (compression != Compression.NONE) {
                    buffer = compression.compress(buffer);
                }
                
                // 暗号化
                if (encryption != Encryption.NONE) {
                    buffer = encryption.encrypt(buffer);
                }
                
                // アトミックな書き込み
                Files.write(
                    journalPath.resolve(generateFileName()),
                    buffer.array(),
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.SYNC
                );
                
            } catch (IOException e) {
                throw new JournalException("Failed to persist events", e);
            }
        });
    }
}
```

### パフォーマンス最適化テクニック

```java
// 1. オブジェクトプーリング（不変オブジェクトの再利用）
public final class OrderPool {
    private static final ConcurrentHashMap<OrderKey, Order> pool = 
        new ConcurrentHashMap<>();
    
    public static Order intern(Order order) {
        OrderKey key = OrderKey.of(order);
        Order existing = pool.get(key);
        
        if (existing != null && existing.equals(order)) {
            return existing; // 既存のインスタンスを再利用
        }
        
        pool.put(key, order);
        return order;
    }
}

// 2. 遅延評価による最適化
public final class LazyOrderStatistics {
    private final ImmutableList<OrderEvent> events;
    private volatile OrderStatistics cached;
    
    public OrderStatistics get() {
        OrderStatistics result = cached;
        if (result == null) {
            synchronized (this) {
                result = cached;
                if (result == null) {
                    cached = result = calculate();
                }
            }
        }
        return result;
    }
}

// 3. 構造共有の最大化
public final class StructuralSharingOptimizer {
    public static <K, V> ImmutableMap<K, V> optimizedPut(
            ImmutableMap<K, V> map, K key, V value) {
        
        V existing = map.get(key);
        if (value.equals(existing)) {
            return map; // 同じ値なら更新しない
        }
        
        // Persistent Hash Mapのノード共有を最大化
        return map.put(key, value);
    }
}
```

## 評価ポイント

1. **エンタープライズ要件の実装**（35点）
   - 監査証跡の完全性
   - 規制要件への準拠
   - 高可用性の実現

2. **パフォーマンスとスケーラビリティ**（30点）
   - レイテンシ要件の達成
   - メモリ効率の最適化
   - 並行処理性能

3. **分散システムでの不変性**（20点）
   - 最終的一貫性の実現
   - 競合解決メカニズム
   - ネットワーク分断への対応

4. **運用性と保守性**（15点）
   - デバッグの容易性
   - パフォーマンス監視
   - バージョン管理

## 発展的な拡張

1. **ブロックチェーンとの統合**：
   - 不変台帳の実装
   - スマートコントラクト
   - 分散合意形成

2. **機械学習との組み合わせ**：
   - 不変特徴量の管理
   - モデルバージョニング
   - 実験の再現性

3. **クラウドネイティブ対応**：
   - イミュータブルインフラ
   - GitOpsワークフロー
   - 宣言的設定管理

このチャレンジ課題を通じて、エンタープライズシステムにおける不変性設計の実践的なスキルと、パフォーマンスとのバランスを取る能力を習得してください。