# 第4章 チャレンジ課題：実務レベルのシステム設計

## 概要

本課題では、実際のソフトウェア開発で求められるレベルのシステム設計に挑戦します。複雑なビジネスルール、例外処理、パフォーマンスの考慮、拡張性の確保など、プロフェッショナルな開発で必要とされる技術を実践します。

## 学習目標

- 実務レベルの複雑なシステム設計
- SOLID原則に基づいた設計
- エラーハンドリングとログ出力
- パフォーマンスを考慮した実装
- テスタビリティの高い設計

## チャレンジ課題：ホテル予約管理システム

### システム概要

高級ホテルチェーンの予約管理システムを設計・実装してください。このシステムは、複数のホテル、様々な部屋タイプ、料金体系、予約管理、顧客管理などを統合的に扱います。

### 主要クラスの設計

#### 1. Hotelクラス

```java
package com.hotelchain.core;

public class Hotel {
    private final String hotelId;
    private final String name;
    private final Address address;
    private final Map<RoomType, List<Room>> rooms;
    private final PricingStrategy pricingStrategy;
    private final List<Amenity> amenities;
    
    // ホテル固有のビジネスルール
    private final int maxAdvanceBookingDays = 365;
    private final double overbookingRate = 1.05; // 5%のオーバーブッキングを許可
    
    public List<Room> findAvailableRooms(LocalDate checkIn, LocalDate checkOut, 
                                        RoomType type, int numberOfGuests) {
        // 複雑な空室検索ロジック
        // - 日付の妥当性チェック
        // - 部屋タイプと収容人数のマッチング
        // - メンテナンス中の部屋の除外
        // - オーバーブッキングの考慮
    }
    
    public Money calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut,
                                Guest guest, List<AddOnService> services) {
        // 動的な価格計算
        // - 基本料金
        // - 季節料金
        // - 会員割引
        // - 長期滞在割引
        // - 追加サービス料金
    }
}
```

#### 2. Reservationクラス

```java
public class Reservation {
    private final String reservationId;
    private final Guest guest;
    private final Room room;
    private final DateRange stayPeriod;
    private final Money totalAmount;
    private final List<AddOnService> services;
    private ReservationStatus status;
    private final List<ReservationEvent> eventHistory;
    
    // 予約の状態遷移を管理
    public void confirm(PaymentInfo payment) {
        validateStateTransition(ReservationStatus.CONFIRMED);
        processPayment(payment);
        recordEvent(new ReservationConfirmedEvent(this, LocalDateTime.now()));
        notifyGuest();
    }
    
    public void cancel(CancellationReason reason) {
        validateCancellation();
        Money cancellationFee = calculateCancellationFee();
        processCancellation(cancellationFee);
        room.release(stayPeriod);
        recordEvent(new ReservationCancelledEvent(this, reason, LocalDateTime.now()));
    }
    
    private Money calculateCancellationFee() {
        // キャンセルポリシーに基づく料金計算
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), stayPeriod.getStart());
        if (daysUntilCheckIn >= 7) return Money.ZERO;
        if (daysUntilCheckIn >= 3) return totalAmount.multiply(0.5);
        return totalAmount; // 3日前以降は全額
    }
}
```

#### 3. Roomクラス（高度なカプセル化）

```java
public class Room {
    private final String roomNumber;
    private final RoomType type;
    private final int floor;
    private final Set<RoomFeature> features;
    private final int maxOccupancy;
    
    // 予約スケジュールの内部管理
    private final TreeMap<LocalDate, RoomStatus> statusCalendar;
    private final List<MaintenanceSchedule> maintenanceSchedules;
    
    // スレッドセーフな予約処理
    public synchronized boolean reserve(DateRange period, String reservationId) {
        if (!isAvailable(period)) {
            return false;
        }
        
        for (LocalDate date = period.getStart(); 
             !date.isAfter(period.getEnd()); 
             date = date.plusDays(1)) {
            statusCalendar.put(date, new RoomStatus.Reserved(reservationId));
        }
        
        return true;
    }
    
    // 複雑な利用可能性チェック
    public boolean isAvailable(DateRange period) {
        // メンテナンススケジュールのチェック
        for (MaintenanceSchedule maintenance : maintenanceSchedules) {
            if (maintenance.overlaps(period)) {
                return false;
            }
        }
        
        // 予約状況のチェック
        for (LocalDate date = period.getStart(); 
             !date.isAfter(period.getEnd()); 
             date = date.plusDays(1)) {
            RoomStatus status = statusCalendar.get(date);
            if (status != null && !status.isAvailable()) {
                return false;
            }
        }
        
        return true;
    }
}
```

#### 4. PricingStrategyインターフェース（戦略パターン）

```java
public interface PricingStrategy {
    Money calculateBasePrice(Room room, DateRange period);
    Money applySeasonalAdjustment(Money basePrice, DateRange period);
    Money applyMemberDiscount(Money price, Guest guest);
    Money applyPromotions(Money price, List<Promotion> activePromotions);
}

public class DynamicPricingStrategy implements PricingStrategy {
    private final OccupancyRateService occupancyService;
    private final MarketDataService marketDataService;
    
    @Override
    public Money calculateBasePrice(Room room, DateRange period) {
        // AIを使用した動的価格設定
        double occupancyRate = occupancyService.getPredictedOccupancy(period);
        double marketDemand = marketDataService.getDemandIndex(period);
        
        Money basePrice = room.getType().getBasePrice();
        double multiplier = calculateMultiplier(occupancyRate, marketDemand);
        
        return basePrice.multiply(multiplier);
    }
}
```

#### 5. Guestクラスとロイヤリティプログラム

```java
public class Guest {
    private final String guestId;
    private final PersonalInfo personalInfo;
    private final LoyaltyMembership loyalty;
    private final List<Preference> preferences;
    private final PaymentMethod defaultPayment;
    
    // プライバシーを考慮した設計
    public class PersonalInfo {
        private final EncryptedString name;
        private final EncryptedString email;
        private final EncryptedString phone;
        
        public String getName() {
            // 復号化は権限チェック後
            SecurityContext.checkPermission("READ_GUEST_INFO");
            return name.decrypt();
        }
    }
    
    // ロイヤリティプログラムの実装
    public class LoyaltyMembership {
        private final MembershipTier tier;
        private int points;
        private final List<Benefit> earnedBenefits;
        
        public void earnPoints(Reservation reservation) {
            int earnedPoints = calculatePoints(reservation);
            points += earnedPoints;
            checkTierUpgrade();
            notifyPointsEarned(earnedPoints);
        }
        
        public List<Benefit> getApplicableBenefits(Reservation reservation) {
            return earnedBenefits.stream()
                .filter(benefit -> benefit.isApplicable(reservation))
                .collect(Collectors.toList());
        }
    }
}
```

### 高度な実装要件

#### 1. トランザクション管理

```java
public class ReservationService {
    private final HotelRepository hotelRepo;
    private final ReservationRepository reservationRepo;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    
    @Transactional
    public ReservationResult createReservation(ReservationRequest request) {
        try {
            // 1. 部屋の確保（楽観的ロック）
            Room room = hotelRepo.findRoomWithLock(request.getRoomId());
            if (!room.reserve(request.getDateRange(), request.getReservationId())) {
                throw new RoomNotAvailableException();
            }
            
            // 2. 予約の作成
            Reservation reservation = new Reservation.Builder()
                .withGuest(request.getGuest())
                .withRoom(room)
                .withDateRange(request.getDateRange())
                .withServices(request.getServices())
                .build();
            
            // 3. 支払い処理
            PaymentResult payment = paymentService.processPayment(
                reservation.getTotalAmount(), 
                request.getPaymentInfo()
            );
            
            if (!payment.isSuccessful()) {
                throw new PaymentFailedException(payment.getErrorMessage());
            }
            
            // 4. 予約の保存
            reservationRepo.save(reservation);
            
            // 5. 通知（非同期）
            notificationService.sendConfirmationAsync(reservation);
            
            return ReservationResult.success(reservation);
            
        } catch (Exception e) {
            // ロールバック処理はフレームワークが実行
            logger.error("Reservation creation failed", e);
            return ReservationResult.failure(e.getMessage());
        }
    }
}
```

#### 2. イベントソーシングの実装

```java
public abstract class DomainEvent {
    private final String aggregateId;
    private final LocalDateTime occurredAt;
    private final String userId;
    
    public abstract void apply(AggregateRoot aggregate);
}

public class RoomBookedEvent extends DomainEvent {
    private final String roomId;
    private final DateRange period;
    private final String guestId;
    
    @Override
    public void apply(AggregateRoot aggregate) {
        if (aggregate instanceof Room) {
            Room room = (Room) aggregate;
            room.applyBooking(this);
        }
    }
}

// イベントストア
public class EventStore {
    public void save(DomainEvent event) {
        // イベントの永続化
    }
    
    public List<DomainEvent> getEvents(String aggregateId, long fromVersion) {
        // イベントの復元
    }
}
```

#### 3. キャッシュとパフォーマンス最適化

```java
public class CachedRoomAvailabilityService {
    private final LoadingCache<AvailabilityQuery, List<Room>> cache;
    
    public CachedRoomAvailabilityService() {
        this.cache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .recordStats()
            .build(this::loadAvailableRooms);
    }
    
    public List<Room> findAvailableRooms(Hotel hotel, DateRange period, RoomType type) {
        AvailabilityQuery query = new AvailabilityQuery(hotel, period, type);
        return cache.get(query);
    }
    
    private List<Room> loadAvailableRooms(AvailabilityQuery query) {
        // 実際の空室検索（重い処理）
        return query.getHotel().findAvailableRooms(
            query.getPeriod().getStart(),
            query.getPeriod().getEnd(),
            query.getRoomType(),
            query.getNumberOfGuests()
        );
    }
    
    public void invalidateCache(Hotel hotel, DateRange period) {
        // 予約や変更があった場合のキャッシュ無効化
        cache.asMap().keySet().stream()
            .filter(query -> query.getHotel().equals(hotel) && 
                           query.getPeriod().overlaps(period))
            .forEach(cache::invalidate);
    }
}
```

### 実装上の注意点

1. **エラーハンドリング**：
   - カスタム例外の階層を設計
   - 適切なログ出力
   - ユーザーフレンドリーなエラーメッセージ

2. **セキュリティ**：
   - 個人情報の暗号化
   - SQLインジェクション対策
   - 認証・認可の実装

3. **テスト容易性**：
   - 依存性注入の活用
   - モックオブジェクトの使用
   - 統合テストの実装

4. **監査とコンプライアンス**：
   - すべての重要な操作のログ記録
   - GDPR等の規制への対応
   - データ保持ポリシーの実装

## 評価ポイント

1. **設計の品質**（40点）
   - SOLID原則の遵守
   - 適切な抽象化
   - 拡張性と保守性

2. **実装の完成度**（30点）
   - ビジネスルールの正確な実装
   - エラー処理の適切さ
   - パフォーマンスの考慮

3. **カプセル化の実践**（20点）
   - 内部状態の適切な隠蔽
   - 不変性の活用
   - 防御的プログラミング

4. **プロフェッショナリズム**（10点）
   - コードの可読性
   - 適切なコメントとドキュメント
   - ベストプラクティスの適用

## 発展的な拡張

1. **マイクロサービス化**：
   - 予約サービス、支払いサービス、通知サービスの分離
   - REST APIの設計
   - サービス間通信の実装

2. **リアクティブプログラミング**：
   - 非同期処理の実装
   - バックプレッシャーの管理
   - イベント駆動アーキテクチャ

3. **機械学習の統合**：
   - 需要予測
   - 動的価格設定
   - パーソナライゼーション

このチャレンジ課題を通じて、実務で求められる高度な設計・実装スキルを身につけてください。