package com.example.records;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * データパイプラインとストリーム処理のデモンストレーション（Java 14互換）
 * Recordsを使用したイベントソーシングとデータ変換
 */
public class DataPipelineSimple {
    
    // ========== イベントソーシング ==========
    
    /**
     * イベントソーシングのドメインモデル
     */
    public static class EventSourcing {
        // ユーザー関連イベント
        public interface UserEvent {
            String userId();
            Instant timestamp();
        }
        
        public record UserCreated(
            String userId, 
            String username,
            String email,
            Map<String, Object> metadata,
            Instant timestamp
        ) implements UserEvent {}
        
        public record UserUpdated(
            String userId,
            Map<String, Object> changes,
            String updatedBy,
            Instant timestamp
        ) implements UserEvent {}
        
        public record UserDeleted(
            String userId,
            String reason,
            String deletedBy,
            Instant timestamp
        ) implements UserEvent {}
        
        public record UserActivated(
            String userId,
            String activatedBy,
            Instant timestamp
        ) implements UserEvent {}
        
        public record UserDeactivated(
            String userId,
            String reason,
            String deactivatedBy,
            Instant timestamp
        ) implements UserEvent {}
        
        // ユーザースナップショット
        public record UserSnapshot(
            String userId,
            String username,
            String email,
            boolean active,
            Map<String, Object> attributes,
            Instant createdAt,
            Instant lastModified,
            int version
        ) {
            public UserSnapshot {
                attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
            }
        }
        
        // イベントストアインターフェイス
        public interface EventStore {
            void append(UserEvent event);
            Stream<UserEvent> getEvents(String userId);
            Stream<UserEvent> getAllEvents();
            Stream<UserEvent> getEventsSince(Instant timestamp);
        }
        
        // インメモリイベントストア実装
        public static class InMemoryEventStore implements EventStore {
            private final List<UserEvent> events = new CopyOnWriteArrayList<>();
            
            @Override
            public void append(UserEvent event) {
                events.add(event);
            }
            
            @Override
            public Stream<UserEvent> getEvents(String userId) {
                return events.stream()
                    .filter(event -> event.userId().equals(userId));
            }
            
            @Override
            public Stream<UserEvent> getAllEvents() {
                return events.stream();
            }
            
            @Override
            public Stream<UserEvent> getEventsSince(Instant timestamp) {
                return events.stream()
                    .filter(event -> event.timestamp().isAfter(timestamp));
            }
        }
        
        // イベントプロセッサ
        public static class EventProcessor {
            /**
             * イベントストリームからユーザースナップショットを構築
             */
            public static Map<String, UserSnapshot> buildSnapshots(Stream<UserEvent> events) {
                Map<String, UserSnapshot> snapshots = new HashMap<>();
                
                events.forEach(event -> {
                    String userId = event.userId();
                    if (snapshots.containsKey(userId)) {
                        snapshots.put(userId, updateSnapshot(snapshots.get(userId), event));
                    } else {
                        snapshots.put(userId, createInitialSnapshot(event));
                    }
                });
                
                return snapshots;
            }
            
            private static UserSnapshot createInitialSnapshot(UserEvent event) {
                if (event instanceof UserCreated) {
                    UserCreated created = (UserCreated) event;
                    return new UserSnapshot(
                        created.userId(), 
                        created.username(), 
                        created.email(), 
                        true, 
                        created.metadata(), 
                        created.timestamp(), 
                        created.timestamp(), 
                        1
                    );
                }
                throw new IllegalStateException("First event must be UserCreated");
            }
            
            private static UserSnapshot updateSnapshot(UserSnapshot snapshot, UserEvent event) {
                if (event instanceof UserCreated) {
                    return createInitialSnapshot(event);
                } else if (event instanceof UserUpdated) {
                    UserUpdated updated = (UserUpdated) event;
                    var newAttributes = new HashMap<>(snapshot.attributes());
                    newAttributes.putAll(updated.changes());
                    
                    return new UserSnapshot(
                        updated.userId(),
                        (String) updated.changes().getOrDefault("username", snapshot.username()),
                        (String) updated.changes().getOrDefault("email", snapshot.email()),
                        snapshot.active(),
                        newAttributes,
                        snapshot.createdAt(),
                        updated.timestamp(),
                        snapshot.version() + 1
                    );
                } else if (event instanceof UserDeleted) {
                    UserDeleted deleted = (UserDeleted) event;
                    return new UserSnapshot(
                        deleted.userId(), 
                        snapshot.username(), 
                        snapshot.email(), 
                        false,
                        snapshot.attributes(), 
                        snapshot.createdAt(), 
                        deleted.timestamp(),
                        snapshot.version() + 1
                    );
                } else if (event instanceof UserActivated) {
                    UserActivated activated = (UserActivated) event;
                    return new UserSnapshot(
                        activated.userId(), 
                        snapshot.username(), 
                        snapshot.email(), 
                        true,
                        snapshot.attributes(), 
                        snapshot.createdAt(), 
                        activated.timestamp(),
                        snapshot.version() + 1
                    );
                } else if (event instanceof UserDeactivated) {
                    UserDeactivated deactivated = (UserDeactivated) event;
                    return new UserSnapshot(
                        deactivated.userId(), 
                        snapshot.username(), 
                        snapshot.email(), 
                        false,
                        snapshot.attributes(), 
                        snapshot.createdAt(), 
                        deactivated.timestamp(),
                        snapshot.version() + 1
                    );
                }
                return snapshot;
            }
            
            /**
             * 特定時点でのスナップショットを取得
             */
            public static Optional<UserSnapshot> getSnapshotAt(
                Stream<UserEvent> events, 
                String userId, 
                Instant pointInTime
            ) {
                var userEvents = events
                    .filter(e -> e.userId().equals(userId))
                    .filter(e -> !e.timestamp().isAfter(pointInTime))
                    .collect(Collectors.toList());
                
                if (userEvents.isEmpty()) {
                    return Optional.empty();
                }
                
                return Optional.of(userEvents.stream()
                    .reduce(
                        createInitialSnapshot(userEvents.get(0)),
                        EventProcessor::updateSnapshot,
                        (s1, s2) -> s2
                    ));
            }
        }
    }
    
    // ========== データ変換パイプライン ==========
    
    /**
     * 売上データの変換と集計
     */
    public static class SalesDataPipeline {
        // 生の売上データ
        public record RawSale(
            String transactionId,
            String productId,
            String customerId,
            BigDecimal amount,
            String currency,
            Instant timestamp,
            Map<String, String> metadata
        ) {}
        
        // 正規化された売上データ
        public record NormalizedSale(
            String transactionId,
            String productId,
            String customerId,
            BigDecimal amountUSD,  // USD換算済み
            Instant timestamp,
            String region,
            String category
        ) {}
        
        // 集計結果
        public record SalesAggregate(
            String dimension,      // 集計軸（product, customer, region等）
            String value,          // 具体的な値
            long count,
            BigDecimal totalAmount,
            BigDecimal avgAmount,
            Instant firstSale,
            Instant lastSale
        ) {}
        
        // 通貨換算サービス
        public static class CurrencyConverter {
            private static final Map<String, BigDecimal> EXCHANGE_RATES = Map.of(
                "USD", BigDecimal.ONE,
                "EUR", new BigDecimal("1.10"),
                "JPY", new BigDecimal("0.0067"),
                "GBP", new BigDecimal("1.27")
            );
            
            public static BigDecimal toUSD(BigDecimal amount, String currency) {
                var rate = EXCHANGE_RATES.getOrDefault(currency, BigDecimal.ONE);
                return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
            }
        }
        
        // データエンリッチメントサービス
        public static class DataEnricher {
            private static final Map<String, String> PRODUCT_CATEGORIES = Map.of(
                "P001", "Electronics",
                "P002", "Clothing",
                "P003", "Books",
                "P004", "Electronics",
                "P005", "Food"
            );
            
            private static final Map<String, String> CUSTOMER_REGIONS = Map.of(
                "C001", "North America",
                "C002", "Europe",
                "C003", "Asia",
                "C004", "North America",
                "C005", "Europe"
            );
            
            public static String getProductCategory(String productId) {
                return PRODUCT_CATEGORIES.getOrDefault(productId, "Unknown");
            }
            
            public static String getCustomerRegion(String customerId) {
                return CUSTOMER_REGIONS.getOrDefault(customerId, "Unknown");
            }
        }
        
        // パイプライン処理
        public static class Pipeline {
            /**
             * 生データを正規化
             */
            public static Stream<NormalizedSale> normalize(Stream<RawSale> rawSales) {
                return rawSales.map(raw -> new NormalizedSale(
                    raw.transactionId(),
                    raw.productId(),
                    raw.customerId(),
                    CurrencyConverter.toUSD(raw.amount(), raw.currency()),
                    raw.timestamp(),
                    DataEnricher.getCustomerRegion(raw.customerId()),
                    DataEnricher.getProductCategory(raw.productId())
                ));
            }
            
            /**
             * 売上データの集計
             */
            public static Stream<SalesAggregate> aggregateByDimension(
                Stream<NormalizedSale> sales,
                Function<NormalizedSale, String> dimensionExtractor,
                String dimensionName
            ) {
                var grouped = sales.collect(
                    Collectors.groupingBy(
                        dimensionExtractor,
                        Collectors.toList()
                    )
                );
                
                return grouped.entrySet().stream()
                    .map(entry -> {
                        var dimensionValue = entry.getKey();
                        var salesList = entry.getValue();
                        
                        long count = salesList.size();
                        BigDecimal total = salesList.stream()
                            .map(NormalizedSale::amountUSD)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                        BigDecimal avg = total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
                        
                        Instant firstSale = salesList.stream()
                            .map(NormalizedSale::timestamp)
                            .min(Instant::compareTo)
                            .orElse(Instant.now());
                        
                        Instant lastSale = salesList.stream()
                            .map(NormalizedSale::timestamp)
                            .max(Instant::compareTo)
                            .orElse(Instant.now());
                        
                        return new SalesAggregate(
                            dimensionName,
                            dimensionValue,
                            count,
                            total,
                            avg,
                            firstSale,
                            lastSale
                        );
                    });
            }
        }
    }
    
    // ========== APIレスポンス変換 ==========
    
    /**
     * API応答の変換とシリアライゼーション
     */
    public static class ApiTransformation {
        // APIレスポンスラッパー
        public record ApiResponse<T>(
            boolean success,
            T data,
            List<String> errors,
            Map<String, Object> metadata,
            Instant timestamp
        ) {
            public ApiResponse {
                errors = errors == null ? List.of() : List.copyOf(errors);
                metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
            }
            
            // ファクトリメソッド
            public static <T> ApiResponse<T> success(T data) {
                return new ApiResponse<>(
                    true, 
                    data, 
                    List.of(), 
                    Map.of("version", "1.0"),
                    Instant.now()
                );
            }
            
            public static <T> ApiResponse<T> success(T data, Map<String, Object> metadata) {
                var enrichedMetadata = new HashMap<>(metadata);
                enrichedMetadata.put("version", "1.0");
                return new ApiResponse<>(true, data, List.of(), enrichedMetadata, Instant.now());
            }
            
            public static <T> ApiResponse<T> failure(List<String> errors) {
                return new ApiResponse<>(
                    false, 
                    null, 
                    errors, 
                    Map.of("version", "1.0"),
                    Instant.now()
                );
            }
        }
        
        // ページング情報
        public record PageInfo(
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean hasNext,
            boolean hasPrevious
        ) {
            public static PageInfo of(int page, int size, long totalElements) {
                int totalPages = (int) Math.ceil((double) totalElements / size);
                return new PageInfo(
                    page,
                    size,
                    totalElements,
                    totalPages,
                    page < totalPages - 1,
                    page > 0
                );
            }
        }
        
        // ページングされたレスポンス
        public record PagedResponse<T>(
            List<T> content,
            PageInfo pageInfo
        ) {
            public PagedResponse {
                content = content == null ? List.of() : List.copyOf(content);
            }
        }
        
        // レスポンス変換器
        public static class ResponseTransformer {
            /**
             * リストをページングレスポンスに変換
             */
            public static <T> PagedResponse<T> paginate(List<T> items, int page, int size) {
                int start = page * size;
                int end = Math.min(start + size, items.size());
                
                var pageContent = start < items.size() 
                    ? items.subList(start, end)
                    : List.<T>of();
                
                return new PagedResponse<>(
                    pageContent,
                    PageInfo.of(page, size, items.size())
                );
            }
            
            /**
             * エラーレスポンスの構築
             */
            public static <T> ApiResponse<T> handleException(Exception e) {
                if (e instanceof IllegalArgumentException) {
                    return ApiResponse.failure(List.of("Invalid input: " + e.getMessage()));
                } else if (e instanceof NoSuchElementException) {
                    return ApiResponse.failure(List.of("Resource not found: " + e.getMessage()));
                } else if (e instanceof SecurityException) {
                    return ApiResponse.failure(List.of("Access denied: " + e.getMessage()));
                } else {
                    return ApiResponse.failure(List.of("Internal error: " + e.getClass().getSimpleName()));
                }
            }
        }
    }
    
    // ========== メインメソッド ==========
    
    public static void main(String[] args) {
        System.out.println("Data Pipeline and Stream Processing Demo (Java 14 Compatible)");
        System.out.println("===========================================================");
        
        // イベントソーシングデモ
        demonstrateEventSourcing();
        
        // 売上データパイプラインデモ
        demonstrateSalesPipeline();
        
        // API変換デモ
        demonstrateApiTransformation();
        
        System.out.println("\n🎯 Key Insights:");
        System.out.println("✓ Records enable clean event sourcing patterns");
        System.out.println("✓ Stream processing with records is concise and type-safe");
        System.out.println("✓ Event handling can be simplified with instanceof checks");
        System.out.println("✓ Immutable data structures ensure thread safety");
        System.out.println("✓ Functional transformations are easy with records");
    }
    
    private static void demonstrateEventSourcing() {
        System.out.println("\n=== Event Sourcing Demo ===");
        
        var eventStore = new EventSourcing.InMemoryEventStore();
        
        // ユーザー作成イベント
        var userId = UUID.randomUUID().toString();
        eventStore.append(new EventSourcing.UserCreated(
            userId,
            "alice",
            "alice@example.com",
            Map.of("role", "admin", "department", "IT"),
            Instant.now()
        ));
        
        sleep(100); // 時間経過をシミュレート
        
        // ユーザー更新イベント
        eventStore.append(new EventSourcing.UserUpdated(
            userId,
            Map.of("email", "alice.smith@example.com", "department", "Engineering"),
            "system",
            Instant.now()
        ));
        
        sleep(100);
        
        // ユーザー非活性化イベント
        eventStore.append(new EventSourcing.UserDeactivated(
            userId,
            "Temporary leave",
            "hr_admin",
            Instant.now()
        ));
        
        // 現在のスナップショット
        var snapshots = EventSourcing.EventProcessor.buildSnapshots(eventStore.getAllEvents());
        var userSnapshot = snapshots.get(userId);
        
        System.out.println("Current snapshot:");
        System.out.println("  User ID: " + userSnapshot.userId());
        System.out.println("  Username: " + userSnapshot.username());
        System.out.println("  Email: " + userSnapshot.email());
        System.out.println("  Active: " + userSnapshot.active());
        System.out.println("  Version: " + userSnapshot.version());
        System.out.println("  Attributes: " + userSnapshot.attributes());
        
        // 特定時点でのスナップショット
        var pointInTime = Instant.now().minusMillis(150);
        var historicalSnapshot = EventSourcing.EventProcessor.getSnapshotAt(
            eventStore.getAllEvents(),
            userId,
            pointInTime
        );
        
        historicalSnapshot.ifPresent(snapshot -> {
            System.out.println("\nHistorical snapshot (150ms ago):");
            System.out.println("  Email: " + snapshot.email());
            System.out.println("  Active: " + snapshot.active());
            System.out.println("  Version: " + snapshot.version());
        });
    }
    
    private static void demonstrateSalesPipeline() {
        System.out.println("\n=== Sales Data Pipeline Demo ===");
        
        // 生の売上データを生成
        var rawSales = Stream.of(
            new SalesDataPipeline.RawSale(
                "T001", "P001", "C001", new BigDecimal("150.00"), "USD",
                Instant.now().minus(5, ChronoUnit.DAYS), Map.of()
            ),
            new SalesDataPipeline.RawSale(
                "T002", "P002", "C002", new BigDecimal("200.00"), "EUR",
                Instant.now().minus(4, ChronoUnit.DAYS), Map.of()
            ),
            new SalesDataPipeline.RawSale(
                "T003", "P001", "C003", new BigDecimal("15000"), "JPY",
                Instant.now().minus(3, ChronoUnit.DAYS), Map.of()
            ),
            new SalesDataPipeline.RawSale(
                "T004", "P004", "C001", new BigDecimal("300.00"), "USD",
                Instant.now().minus(2, ChronoUnit.DAYS), Map.of()
            ),
            new SalesDataPipeline.RawSale(
                "T005", "P002", "C005", new BigDecimal("180.00"), "GBP",
                Instant.now().minus(1, ChronoUnit.DAYS), Map.of()
            )
        );
        
        // データ正規化
        var normalizedSales = SalesDataPipeline.Pipeline.normalize(rawSales).collect(Collectors.toList());
        
        System.out.println("Normalized sales:");
        normalizedSales.forEach(sale -> 
            System.out.printf("  %s: $%.2f USD (%s - %s)%n",
                sale.transactionId(),
                sale.amountUSD(),
                sale.category(),
                sale.region()
            )
        );
        
        // カテゴリ別集計
        var categoryAggregates = SalesDataPipeline.Pipeline.aggregateByDimension(
            normalizedSales.stream(),
            SalesDataPipeline.NormalizedSale::category,
            "Category"
        ).collect(Collectors.toList());
        
        System.out.println("\nSales by category:");
        categoryAggregates.forEach(agg ->
            System.out.printf("  %s: %d sales, Total: $%.2f, Avg: $%.2f%n",
                agg.value(),
                agg.count(),
                agg.totalAmount(),
                agg.avgAmount()
            )
        );
        
        // 地域別集計
        var regionAggregates = SalesDataPipeline.Pipeline.aggregateByDimension(
            normalizedSales.stream(),
            SalesDataPipeline.NormalizedSale::region,
            "Region"
        ).collect(Collectors.toList());
        
        System.out.println("\nSales by region:");
        regionAggregates.forEach(agg ->
            System.out.printf("  %s: %d sales, Total: $%.2f%n",
                agg.value(),
                agg.count(),
                agg.totalAmount()
            )
        );
    }
    
    private static void demonstrateApiTransformation() {
        System.out.println("\n=== API Transformation Demo ===");
        
        // 成功レスポンス
        var users = List.of(
            new User("1", "Alice", "alice@example.com"),
            new User("2", "Bob", "bob@example.com"),
            new User("3", "Charlie", "charlie@example.com"),
            new User("4", "David", "david@example.com"),
            new User("5", "Eve", "eve@example.com")
        );
        
        // ページングレスポンス
        var pagedUsers = ApiTransformation.ResponseTransformer.paginate(users, 1, 2);
        var successResponse = ApiTransformation.ApiResponse.success(
            pagedUsers,
            Map.of("endpoint", "/api/users", "method", "GET")
        );
        
        System.out.println("Success response:");
        System.out.println("  Success: " + successResponse.success());
        System.out.println("  Page: " + pagedUsers.pageInfo().page());
        System.out.println("  Total pages: " + pagedUsers.pageInfo().totalPages());
        System.out.println("  Content: " + pagedUsers.content().size() + " items");
        
        // エラーレスポンス
        var errorResponse = ApiTransformation.ResponseTransformer.handleException(
            new IllegalArgumentException("Invalid user ID format")
        );
        
        System.out.println("\nError response:");
        System.out.println("  Success: " + errorResponse.success());
        System.out.println("  Errors: " + errorResponse.errors());
    }
    
    // 簡単なUserレコード
    private record User(String id, String name, String email) {}
    
    // スレッドスリープのラッパー
    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}