# 第5章 チャレンジ課題：エンタープライズシステムの継承設計

## 概要

本課題では、実際のエンタープライズシステムで求められるレベルの継承設計に挑戦します。大規模なドメインモデリング、複雑なビジネスルールの実装、パフォーマンスと保守性のバランスなど、プロフェッショナルな開発で直面する課題を扱います。

## 学習目標

- 大規模システムにおける継承階層の設計
- ドメイン駆動設計（DDD）の実践
- 継承とコンポジションの適切な使い分け
- パフォーマンスを考慮した多態性の実装
- エンタープライズパターンの適用

## チャレンジ課題：総合ECプラットフォーム

### システム概要

大規模ECプラットフォームのコアドメインモデルを設計・実装してください。このシステムは、B2C、B2B、C2C（マーケットプレイス）のすべてのビジネスモデルをサポートし、グローバル展開を前提とした設計が必要です。

### ドメインモデルの全体像

```
Entity（DDD基底クラス）
├── User（抽象）
│   ├── Customer
│   │   ├── IndividualCustomer
│   │   └── CorporateCustomer
│   ├── Seller
│   │   ├── IndividualSeller
│   │   ├── CorporateSeller
│   │   └── PlatformSeller
│   └── SystemUser
│       ├── Administrator
│       ├── CustomerSupport
│       └── WarehouseStaff
│
├── Product（抽象）
│   ├── PhysicalProduct
│   │   ├── StandardProduct
│   │   ├── PerishableProduct
│   │   └── FragileProduct
│   ├── DigitalProduct
│   │   ├── Software
│   │   ├── DigitalContent
│   │   └── Subscription
│   └── Service
│       ├── OneTimeService
│       └── RecurringService
│
├── Order（抽象）
│   ├── StandardOrder
│   ├── SubscriptionOrder
│   ├── PreOrder
│   └── GroupBuyingOrder
│
└── Payment（抽象）
    ├── CreditCardPayment
    ├── BankTransfer
    ├── DigitalWallet
    └── CryptoCurrency
```

### コアドメインエンティティの実装

#### 1. DDD基底クラス

```java
public abstract class Entity {
    protected final String id;
    protected final LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected long version; // 楽観的ロック用
    
    protected Entity(String id) {
        this.id = Objects.requireNonNull(id);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.version = 1L;
    }
    
    // ドメインイベントの管理
    private final transient List<DomainEvent> domainEvents = new ArrayList<>();
    
    protected void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }
    
    public List<DomainEvent> getUncommittedEvents() {
        return new ArrayList<>(domainEvents);
    }
    
    public void markEventsAsCommitted() {
        domainEvents.clear();
    }
    
    // エンティティの等価性はIDで判断
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Entity)) return false;
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

// 集約ルート
public abstract class AggregateRoot extends Entity {
    protected AggregateRoot(String id) {
        super(id);
    }
    
    // 不変条件の検証
    protected abstract void validateInvariants();
    
    // 状態変更の前に必ず不変条件を検証
    protected void ensureInvariants() {
        validateInvariants();
    }
}
```

#### 2. ユーザー階層の実装

```java
public abstract class User extends AggregateRoot {
    protected String email;
    protected String hashedPassword;
    protected UserStatus status;
    protected final Set<Role> roles = new HashSet<>();
    protected final List<Address> addresses = new ArrayList<>();
    protected final Map<String, Preference> preferences = new HashMap<>();
    
    // 認証トークンの管理（セキュリティ）
    private final Map<String, AuthToken> activeTokens = new ConcurrentHashMap<>();
    
    // テンプレートメソッド：ユーザー登録プロセス
    public final void register(RegistrationRequest request) {
        validateRegistration(request);
        
        this.email = request.getEmail();
        this.hashedPassword = hashPassword(request.getPassword());
        this.status = UserStatus.PENDING_VERIFICATION;
        
        performTypeSpecificRegistration(request);
        
        // ドメインイベントの発行
        registerEvent(new UserRegisteredEvent(this.id, this.email, getClass().getSimpleName()));
        
        sendVerificationEmail();
    }
    
    protected abstract void validateRegistration(RegistrationRequest request);
    protected abstract void performTypeSpecificRegistration(RegistrationRequest request);
    
    // ポリモーフィックな振る舞い
    public abstract boolean canPurchase(Product product);
    public abstract double getApplicableDiscountRate(Order order);
    public abstract int getReturnPeriodDays();
}

// B2B顧客の実装
public class CorporateCustomer extends User {
    private String companyName;
    private String taxId;
    private CreditLimit creditLimit;
    private PaymentTerms paymentTerms;
    private final List<SubAccount> subAccounts = new ArrayList<>();
    
    @Override
    protected void validateRegistration(RegistrationRequest request) {
        CorporateRegistrationRequest corpRequest = (CorporateRegistrationRequest) request;
        
        // 法人番号の検証
        if (!TaxIdValidator.isValid(corpRequest.getTaxId())) {
            throw new InvalidTaxIdException(corpRequest.getTaxId());
        }
        
        // 信用調査
        CreditCheckResult creditCheck = creditCheckService.check(corpRequest.getCompanyName(), corpRequest.getTaxId());
        if (creditCheck.getRiskLevel() > RiskLevel.MEDIUM) {
            throw new CreditCheckFailedException("信用リスクが高すぎます");
        }
    }
    
    @Override
    public boolean canPurchase(Product product) {
        // 与信限度額のチェック
        double currentOutstanding = calculateOutstandingAmount();
        double productPrice = product.getPrice(this);
        
        return currentOutstanding + productPrice <= creditLimit.getAmount() &&
               product.isAvailableForB2B() &&
               status == UserStatus.ACTIVE;
    }
    
    @Override
    public double getApplicableDiscountRate(Order order) {
        // ボリュームディスカウントの計算
        double baseDiscount = 0.05; // 基本5%割引
        
        // 年間購入額に応じた追加割引
        double annualPurchaseAmount = getAnnualPurchaseAmount();
        if (annualPurchaseAmount > 10_000_000) {
            baseDiscount += 0.10;
        } else if (annualPurchaseAmount > 5_000_000) {
            baseDiscount += 0.05;
        }
        
        // 支払い条件による割引
        if (paymentTerms == PaymentTerms.PREPAID) {
            baseDiscount += 0.02;
        }
        
        return Math.min(baseDiscount, 0.25); // 最大25%割引
    }
}

// セラー階層
public abstract class Seller extends User {
    protected SellerProfile profile;
    protected final List<Product> products = new ArrayList<>();
    protected final SellerMetrics metrics = new SellerMetrics();
    protected CommissionStructure commissionStructure;
    
    // 商品登録（テンプレートメソッド）
    public final Product registerProduct(ProductRegistrationRequest request) {
        validateProductRegistration(request);
        
        Product product = createProduct(request);
        
        // 手数料の計算
        double listingFee = calculateListingFee(product);
        chargeListingFee(listingFee);
        
        products.add(product);
        
        // 商品の審査（非同期）
        submitForReview(product);
        
        registerEvent(new ProductRegisteredEvent(this.id, product.getId()));
        
        return product;
    }
    
    protected abstract void validateProductRegistration(ProductRegistrationRequest request);
    protected abstract Product createProduct(ProductRegistrationRequest request);
    protected abstract double calculateListingFee(Product product);
    
    // セラーのパフォーマンス評価
    public SellerRating calculateRating() {
        double productQualityScore = metrics.getAverageProductRating();
        double deliveryScore = metrics.getOnTimeDeliveryRate();
        double customerServiceScore = metrics.getCustomerSatisfactionScore();
        double returnRate = metrics.getReturnRate();
        
        // 重み付け平均
        double overallScore = productQualityScore * 0.3 +
                            deliveryScore * 0.3 +
                            customerServiceScore * 0.3 +
                            (1 - returnRate) * 0.1;
        
        return SellerRating.fromScore(overallScore);
    }
}
```

#### 3. 商品階層の実装（高度なポリモーフィズム）

```java
public abstract class Product extends AggregateRoot {
    protected String name;
    protected String description;
    protected final List<ProductImage> images = new ArrayList<>();
    protected final Map<String, ProductAttribute> attributes = new HashMap<>();
    protected ProductStatus status;
    protected final PriceStrategy priceStrategy;
    protected final InventoryStrategy inventoryStrategy;
    
    // Visitorパターンによる処理の外部化
    public abstract <T> T accept(ProductVisitor<T> visitor);
    
    // 価格計算（ストラテジーパターン + ポリモーフィズム）
    public final Money calculatePrice(User user, int quantity) {
        Money basePrice = getBasePrice();
        
        // ユーザータイプ別の価格調整
        basePrice = adjustPriceForUser(basePrice, user);
        
        // 数量割引
        basePrice = priceStrategy.applyQuantityDiscount(basePrice, quantity);
        
        // 商品タイプ固有の価格調整
        basePrice = applyTypeSpecificPricing(basePrice, user, quantity);
        
        return basePrice;
    }
    
    protected abstract Money getBasePrice();
    protected abstract Money adjustPriceForUser(Money price, User user);
    protected abstract Money applyTypeSpecificPricing(Money price, User user, int quantity);
    
    // 在庫管理（ポリモーフィック）
    public abstract boolean checkAvailability(int quantity);
    public abstract void reserve(String orderId, int quantity);
    public abstract void release(String orderId, int quantity);
    
    // 配送計算（商品タイプによって異なる）
    public abstract ShippingInfo calculateShipping(Address destination, ShippingMethod method);
}

// 生鮮食品（複雑なビジネスルール）
public class PerishableProduct extends PhysicalProduct {
    private LocalDate expirationDate;
    private TemperatureRequirement temperatureRequirement;
    private final PerishabilityCalculator perishabilityCalculator;
    
    @Override
    public Money applyTypeSpecificPricing(Money price, User user, int quantity) {
        // 賞味期限による動的価格設定
        long daysUntilExpiration = ChronoUnit.DAYS.between(LocalDate.now(), expirationDate);
        
        if (daysUntilExpiration <= 1) {
            return price.multiply(0.3); // 70%オフ
        } else if (daysUntilExpiration <= 3) {
            return price.multiply(0.5); // 50%オフ
        } else if (daysUntilExpiration <= 7) {
            return price.multiply(0.8); // 20%オフ
        }
        
        return price;
    }
    
    @Override
    public boolean checkAvailability(int quantity) {
        if (!super.checkAvailability(quantity)) {
            return false;
        }
        
        // 賞味期限チェック
        if (LocalDate.now().isAfter(expirationDate)) {
            return false;
        }
        
        // 在庫の鮮度チェック
        double freshnessScore = perishabilityCalculator.calculateFreshness(this);
        return freshnessScore >= 0.6; // 60%以上の鮮度が必要
    }
    
    @Override
    public ShippingInfo calculateShipping(Address destination, ShippingMethod method) {
        // 冷蔵・冷凍配送の必要性チェック
        if (temperatureRequirement.requiresColdChain()) {
            if (!method.supportsColdChain()) {
                throw new IncompatibleShippingMethodException(
                    "この商品は冷蔵/冷凍配送が必要です");
            }
        }
        
        // 配送可能距離の計算
        double maxDistance = perishabilityCalculator.calculateMaxShippingDistance(
            this, method.getEstimatedDeliveryHours());
        
        if (destination.getDistanceFrom(getWarehouseLocation()) > maxDistance) {
            throw new ShippingDistanceExceededException(
                "この商品は配送距離が長すぎるため配送できません");
        }
        
        return new ShippingInfo(
            method.getBaseRate().multiply(temperatureRequirement.getCostMultiplier()),
            method.getEstimatedDeliveryHours(),
            temperatureRequirement.getSpecialHandlingInstructions()
        );
    }
}

// デジタル商品（サブスクリプション）
public class Subscription extends DigitalProduct {
    private final BillingCycle billingCycle;
    private final List<Feature> features;
    private final Map<String, UsageLimit> usageLimits;
    
    @Override
    public <T> T accept(ProductVisitor<T> visitor) {
        return visitor.visitSubscription(this);
    }
    
    // サブスクリプション固有の価格計算
    @Override
    protected Money applyTypeSpecificPricing(Money price, User user, int quantity) {
        // 年間契約割引
        if (billingCycle == BillingCycle.ANNUAL) {
            price = price.multiply(0.8); // 20%割引
        }
        
        // 複数ライセンス割引
        if (quantity > 10) {
            price = price.multiply(0.9); // 追加10%割引
        }
        
        // 既存顧客割引
        if (user instanceof Customer && hasActiveSubscription((Customer) user)) {
            price = price.multiply(0.95); // 5%割引
        }
        
        return price;
    }
}
```

#### 4. 注文処理システム（高度なポリモーフィズムとパターン）

```java
public abstract class Order extends AggregateRoot {
    protected final Customer customer;
    protected final List<OrderLine> orderLines = new ArrayList<>();
    protected OrderStatus status;
    protected final PaymentInfo paymentInfo;
    protected final ShippingInfo shippingInfo;
    
    // State パターンによる状態管理
    protected OrderState currentState;
    
    // Chain of Responsibility パターンによる検証
    private final OrderValidator validatorChain;
    
    protected Order(String id, Customer customer) {
        super(id);
        this.customer = customer;
        this.currentState = new DraftState();
        this.validatorChain = buildValidatorChain();
    }
    
    // テンプレートメソッド：注文処理
    public final void process() {
        validateOrder();
        
        reserveInventory();
        
        calculateTotals();
        
        ProcessResult result = processPayment();
        if (!result.isSuccessful()) {
            rollbackInventory();
            throw new PaymentFailedException(result.getErrorMessage());
        }
        
        scheduleShipment();
        
        currentState = currentState.transitionTo(OrderStatus.PROCESSING);
        
        registerEvent(new OrderProcessedEvent(this.id, customer.getId(), calculateTotalAmount()));
        
        notifyCustomer();
    }
    
    protected abstract void reserveInventory();
    protected abstract ProcessResult processPayment();
    protected abstract void scheduleShipment();
    
    // ポリモーフィックな合計計算
    public Money calculateTotalAmount() {
        Money subtotal = orderLines.stream()
            .map(line -> line.getProduct().calculatePrice(customer, line.getQuantity()))
            .reduce(Money.ZERO, Money::add);
        
        Money discount = calculateDiscount(subtotal);
        Money tax = calculateTax(subtotal.subtract(discount));
        Money shipping = calculateShipping();
        
        return subtotal.subtract(discount).add(tax).add(shipping);
    }
    
    protected abstract Money calculateDiscount(Money subtotal);
    protected abstract Money calculateTax(Money amount);
    protected abstract Money calculateShipping();
}

// グループ購入注文（複雑なビジネスロジック）
public class GroupBuyingOrder extends Order {
    private final int minimumParticipants;
    private final int maximumParticipants;
    private final LocalDateTime deadline;
    private final Map<String, Participant> participants = new ConcurrentHashMap<>();
    private final PriceThreshold[] priceThresholds;
    
    @Override
    protected void reserveInventory() {
        // グループ購入では締切まで在庫予約しない
        if (LocalDateTime.now().isBefore(deadline)) {
            return;
        }
        
        // 最小参加人数チェック
        if (participants.size() < minimumParticipants) {
            throw new InsufficientParticipantsException(
                String.format("最小参加人数（%d人）に達していません", minimumParticipants));
        }
        
        // 一括在庫予約
        int totalQuantity = participants.values().stream()
            .mapToInt(Participant::getQuantity)
            .sum();
        
        for (OrderLine line : orderLines) {
            line.getProduct().reserve(this.id, totalQuantity);
        }
    }
    
    @Override
    protected Money calculateDiscount(Money subtotal) {
        // 参加人数に応じた段階的割引
        int participantCount = participants.size();
        
        for (PriceThreshold threshold : priceThresholds) {
            if (participantCount >= threshold.getMinParticipants()) {
                return subtotal.multiply(threshold.getDiscountRate());
            }
        }
        
        return Money.ZERO;
    }
    
    // 参加者の追加（並行処理対応）
    public synchronized boolean addParticipant(Customer customer, int quantity) {
        if (LocalDateTime.now().isAfter(deadline)) {
            return false;
        }
        
        if (participants.size() >= maximumParticipants) {
            return false;
        }
        
        Participant participant = new Participant(customer, quantity);
        participants.put(customer.getId(), participant);
        
        // 価格再計算の通知
        notifyPriceUpdate();
        
        return true;
    }
}
```

### パフォーマンス最適化とデザインパターン

```java
// Flyweightパターンによるメモリ最適化
public class ProductAttributeFactory {
    private static final Map<String, ProductAttribute> cache = new ConcurrentHashMap<>();
    
    public static ProductAttribute getInstance(String name, String value) {
        String key = name + ":" + value;
        return cache.computeIfAbsent(key, k -> new ProductAttribute(name, value));
    }
}

// Proxyパターンによる遅延ロード
public class LazyLoadingProduct extends Product {
    private final String productId;
    private Product realProduct;
    private final ProductRepository repository;
    
    @Override
    public Money getBasePrice() {
        ensureLoaded();
        return realProduct.getBasePrice();
    }
    
    private synchronized void ensureLoaded() {
        if (realProduct == null) {
            realProduct = repository.load(productId);
        }
    }
}

// メソッドディスパッチの最適化
public class OptimizedOrderProcessor {
    // タイプ別の処理をマップで管理（vtableの手動実装）
    private final Map<Class<? extends Order>, OrderHandler> handlers = new HashMap<>();
    
    public OptimizedOrderProcessor() {
        handlers.put(StandardOrder.class, new StandardOrderHandler());
        handlers.put(SubscriptionOrder.class, new SubscriptionOrderHandler());
        handlers.put(PreOrder.class, new PreOrderHandler());
        handlers.put(GroupBuyingOrder.class, new GroupBuyingOrderHandler());
    }
    
    public void process(Order order) {
        OrderHandler handler = handlers.get(order.getClass());
        if (handler != null) {
            handler.handle(order);
        } else {
            // フォールバック処理
            order.process();
        }
    }
}
```

## 評価ポイント

1. **ドメインモデリングの品質**（35点）
   - ビジネスルールの正確な表現
   - 適切な抽象化レベル
   - ドメイン知識の反映

2. **継承設計の適切性**（25点）
   - リスコフの置換原則の遵守
   - 継承の深さと幅のバランス
   - is-a関係の正しい適用

3. **パフォーマンスとスケーラビリティ**（20点）
   - 効率的なポリモーフィズムの実装
   - メモリ使用量の最適化
   - 並行処理への対応

4. **保守性と拡張性**（20点）
   - SOLID原則の実践
   - デザインパターンの適切な適用
   - テスタビリティの確保

## 発展的な拡張

1. **イベントソーシング**：
   - すべての状態変更をイベントとして記録
   - イベントからの状態再構築
   - 監査ログの自動生成

2. **CQRS（コマンドクエリ責任分離）**：
   - 読み取りモデルと書き込みモデルの分離
   - パフォーマンスの最適化
   - 結果整合性の実装

3. **マルチテナンシー**：
   - テナント別のカスタマイズ
   - データ分離戦略
   - 動的な機能フラグ

このチャレンジ課題を通じて、エンタープライズレベルの継承設計とポリモーフィズムの実践的な活用方法を習得してください。