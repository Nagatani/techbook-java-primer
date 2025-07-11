# 第5章 応用課題：継承階層の設計と高度なポリモーフィズム

## 概要

本課題では、より複雑な継承階層の設計と、実践的なポリモーフィズムの活用方法を学習します。多段階継承、抽象クラスの活用、インターフェースとの組み合わせなど、実務で必要とされる高度な技術を習得します。

## 学習目標

- 多段階継承の適切な設計
- 抽象クラスを使った共通処理の実装
- インターフェースと継承の組み合わせ
- テンプレートメソッドパターンの理解
- 継承よりコンポジションの原則の理解

## 課題1：金融商品管理システム

### 要求仕様

銀行で扱う様々な金融商品を管理するシステムを設計してください。

#### 階層構造

```
FinancialProduct（抽象）
├── Account（抽象）
│   ├── SavingsAccount（普通預金）
│   ├── CheckingAccount（当座預金）
│   └── TimeDeposit（定期預金）
└── Investment（抽象）
    ├── Stock（株式）
    ├── Bond（債券）
    └── MutualFund（投資信託）
```

#### 基底抽象クラス：FinancialProduct

```java
public abstract class FinancialProduct {
    protected final String productId;
    protected final String customerId;
    protected String productName;
    protected double balance;
    protected LocalDate openDate;
    protected ProductStatus status;
    
    // テンプレートメソッドパターンの実装
    public final double calculateTotalValue() {
        double baseValue = getBalance();
        double interest = calculateInterest();
        double fees = calculateFees();
        double taxes = calculateTaxes(baseValue + interest);
        
        return baseValue + interest - fees - taxes;
    }
    
    // 子クラスで実装すべき抽象メソッド
    protected abstract double calculateInterest();
    protected abstract double calculateFees();
    protected abstract double calculateTaxes(double grossValue);
    
    // フックメソッド（子クラスでオーバーライド可能）
    protected void beforeClose() {
        // デフォルトでは何もしない
    }
    
    protected void afterClose() {
        // デフォルトでは何もしない
    }
    
    // 商品のクローズ処理（テンプレートメソッド）
    public final void close() {
        if (status != ProductStatus.ACTIVE) {
            throw new IllegalStateException("商品はアクティブではありません");
        }
        
        beforeClose();
        
        double finalValue = calculateTotalValue();
        processClosing(finalValue);
        status = ProductStatus.CLOSED;
        
        afterClose();
    }
    
    protected abstract void processClosing(double finalValue);
}
```

#### 中間抽象クラス：Account

```java
public abstract class Account extends FinancialProduct {
    protected double minimumBalance;
    protected int monthlyTransactionLimit;
    protected int currentMonthTransactions;
    
    // 口座共通の機能
    public void deposit(double amount) {
        validateAmount(amount);
        balance += amount;
        currentMonthTransactions++;
        recordTransaction(TransactionType.DEPOSIT, amount);
    }
    
    public boolean withdraw(double amount) {
        validateAmount(amount);
        
        if (!canWithdraw(amount)) {
            return false;
        }
        
        balance -= amount;
        currentMonthTransactions++;
        recordTransaction(TransactionType.WITHDRAWAL, amount);
        
        return true;
    }
    
    // 子クラスでカスタマイズ可能
    protected boolean canWithdraw(double amount) {
        return balance - amount >= minimumBalance && 
               currentMonthTransactions < monthlyTransactionLimit;
    }
    
    // 月次処理（抽象メソッド）
    public abstract void performMonthlyMaintenance();
}
```

#### 具体クラス：SavingsAccount

```java
public class SavingsAccount extends Account {
    private static final double ANNUAL_INTEREST_RATE = 0.001; // 0.1%
    private static final double MONTHLY_FEE = 0;
    private static final double TAX_RATE = 0.2; // 利息に対する税率20%
    
    public SavingsAccount(String customerId, double initialDeposit) {
        super(generateProductId(), customerId);
        this.productName = "普通預金";
        this.balance = initialDeposit;
        this.minimumBalance = 0;
        this.monthlyTransactionLimit = Integer.MAX_VALUE;
    }
    
    @Override
    protected double calculateInterest() {
        // 日割り計算
        long daysSinceOpen = ChronoUnit.DAYS.between(openDate, LocalDate.now());
        return balance * ANNUAL_INTEREST_RATE * daysSinceOpen / 365;
    }
    
    @Override
    protected double calculateFees() {
        return MONTHLY_FEE * getMonthsSinceOpen();
    }
    
    @Override
    protected double calculateTaxes(double grossValue) {
        double interest = calculateInterest();
        return interest * TAX_RATE;
    }
    
    @Override
    public void performMonthlyMaintenance() {
        // 利息の付与
        double monthlyInterest = balance * ANNUAL_INTEREST_RATE / 12;
        balance += monthlyInterest;
        
        // 取引回数のリセット
        currentMonthTransactions = 0;
    }
    
    @Override
    protected void processClosing(double finalValue) {
        // 残高を顧客の決済口座に移動
        transferToSettlementAccount(customerId, finalValue);
    }
}
```

#### 具体クラス：TimeDeposit（定期預金）

```java
public class TimeDeposit extends Account {
    private final LocalDate maturityDate;
    private final double contractedRate;
    private boolean isMatured;
    
    public TimeDeposit(String customerId, double principal, 
                      int termMonths, double annualRate) {
        super(generateProductId(), customerId);
        this.productName = "定期預金";
        this.balance = principal;
        this.maturityDate = openDate.plusMonths(termMonths);
        this.contractedRate = annualRate;
        this.minimumBalance = principal; // 元本割れ不可
        this.monthlyTransactionLimit = 0; // 満期まで引き出し不可
    }
    
    @Override
    protected boolean canWithdraw(double amount) {
        // 満期前は原則引き出し不可（中途解約は別メソッド）
        return isMatured && super.canWithdraw(amount);
    }
    
    @Override
    protected double calculateInterest() {
        if (LocalDate.now().isBefore(maturityDate)) {
            // 中途解約の場合はペナルティレート
            return balance * 0.001 * getMonthsSinceOpen() / 12;
        } else {
            // 満期の場合は約定金利
            long termMonths = ChronoUnit.MONTHS.between(openDate, maturityDate);
            return balance * contractedRate * termMonths / 12;
        }
    }
    
    @Override
    public void performMonthlyMaintenance() {
        if (!isMatured && !LocalDate.now().isBefore(maturityDate)) {
            isMatured = true;
            // 満期時の処理
            double interest = calculateInterest();
            balance += interest;
            monthlyTransactionLimit = Integer.MAX_VALUE; // 引き出し可能に
        }
    }
    
    // 定期預金特有のメソッド
    public double breakDeposit() {
        if (isMatured) {
            throw new IllegalStateException("既に満期です");
        }
        
        beforeClose();
        
        double penaltyInterest = balance * 0.001 * getMonthsSinceOpen() / 12;
        double finalAmount = balance + penaltyInterest;
        
        processClosing(finalAmount);
        status = ProductStatus.CLOSED;
        
        return finalAmount;
    }
}
```

### インターフェースとの組み合わせ

```java
// リスク評価可能な商品
public interface RiskAssessable {
    RiskLevel assessRisk();
    double calculateVaR(double confidenceLevel); // Value at Risk
}

// 取引可能な商品
public interface Tradeable {
    boolean buy(double quantity, double price);
    boolean sell(double quantity, double price);
    double getCurrentMarketPrice();
}

// 株式クラス
public class Stock extends Investment implements RiskAssessable, Tradeable {
    private String tickerSymbol;
    private double shares;
    private double averageCost;
    private StockExchange exchange;
    
    @Override
    public RiskLevel assessRisk() {
        double volatility = exchange.getHistoricalVolatility(tickerSymbol);
        if (volatility > 0.3) return RiskLevel.HIGH;
        if (volatility > 0.15) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
    
    @Override
    public boolean buy(double quantity, double price) {
        double cost = quantity * price;
        // 購入処理の実装
        shares += quantity;
        averageCost = ((averageCost * (shares - quantity)) + cost) / shares;
        return true;
    }
    
    @Override
    public double getCurrentMarketPrice() {
        return exchange.getQuote(tickerSymbol).getLastPrice();
    }
}
```

## 課題2：メディアコンテンツ管理システム

### 要求仕様

図書館やビデオレンタル店で使用するメディア管理システムを設計してください。

#### 階層構造とインターフェース

```
MediaItem（抽象）
├── PhysicalMedia（抽象）
│   ├── Book
│   ├── DVD
│   └── AudioCD
└── DigitalMedia（抽象）
    ├── EBook
    ├── StreamingVideo
    └── AudioBook

インターフェース：
- Borrowable（貸出可能）
- Downloadable（ダウンロード可能）
- Streamable（ストリーミング可能）
```

### 複雑な実装例

```java
public abstract class MediaItem {
    protected String itemId;
    protected String title;
    protected String creator;
    protected int releaseYear;
    protected Genre genre;
    protected double rating;
    protected final List<Review> reviews = new ArrayList<>();
    
    // 評価システム（テンプレートメソッド）
    public final void addReview(Review review) {
        validateReview(review);
        reviews.add(review);
        updateRating();
        afterReviewAdded(review);
    }
    
    protected abstract void validateReview(Review review);
    protected abstract void afterReviewAdded(Review review);
    
    private void updateRating() {
        if (reviews.isEmpty()) {
            rating = 0;
        } else {
            rating = reviews.stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0);
        }
    }
}

// 物理メディアの貸出管理
public abstract class PhysicalMedia extends MediaItem implements Borrowable {
    protected Location currentLocation;
    protected Member borrower;
    protected LocalDate dueDate;
    protected int totalCopies;
    protected int availableCopies;
    
    @Override
    public boolean borrow(Member member, int days) {
        if (!isAvailable()) {
            return false;
        }
        
        borrower = member;
        dueDate = LocalDate.now().plusDays(days);
        availableCopies--;
        
        recordBorrowing(member);
        return true;
    }
    
    @Override
    public void returnItem() {
        if (borrower == null) {
            throw new IllegalStateException("この商品は貸出中ではありません");
        }
        
        LocalDate returnDate = LocalDate.now();
        if (returnDate.isAfter(dueDate)) {
            int overdueDays = (int) ChronoUnit.DAYS.between(dueDate, returnDate);
            double fine = calculateFine(overdueDays);
            borrower.addFine(fine);
        }
        
        borrower = null;
        dueDate = null;
        availableCopies++;
    }
    
    protected abstract double calculateFine(int overdueDays);
}

// 複数のインターフェースを実装
public class StreamingVideo extends DigitalMedia 
        implements Streamable, Downloadable {
    
    private String streamingUrl;
    private VideoQuality maxQuality;
    private long fileSize;
    private DRMProtection drmProtection;
    
    @Override
    public StreamingSession startStreaming(Member member, VideoQuality quality) {
        if (!member.hasActiveSubscription()) {
            throw new UnauthorizedException("アクティブなサブスクリプションが必要です");
        }
        
        if (quality.compareTo(maxQuality) > 0) {
            quality = maxQuality; // 最大画質に制限
        }
        
        StreamingSession session = new StreamingSession(this, member, quality);
        session.start();
        
        return session;
    }
    
    @Override
    public DownloadTask download(Member member, VideoQuality quality) {
        if (!member.canDownload()) {
            throw new UnauthorizedException("ダウンロード権限がありません");
        }
        
        DownloadTask task = new DownloadTask(this, quality);
        task.setProgressListener(progress -> 
            member.updateDownloadProgress(itemId, progress));
        
        return task;
    }
}
```

## 課題3：交通機関シミュレーションシステム

### 要求仕様

様々な交通機関をシミュレートするシステムを設計してください。

#### 複雑な継承階層とインターフェース

```java
// 移動能力を表すインターフェース
public interface Movable {
    void move(double distance);
    double getCurrentSpeed();
    Position getCurrentPosition();
}

// 乗客輸送能力
public interface PassengerCarrier {
    boolean boardPassenger(Passenger passenger);
    boolean alightPassenger(String passengerId);
    int getCurrentPassengerCount();
    int getCapacity();
}

// 貨物輸送能力
public interface CargoCarrier {
    boolean loadCargo(Cargo cargo);
    boolean unloadCargo(String cargoId);
    double getCurrentCargoWeight();
    double getMaxCargoWeight();
}

// 基底抽象クラス
public abstract class Vehicle implements Movable {
    protected String vehicleId;
    protected double maxSpeed;
    protected double currentSpeed;
    protected Position position;
    protected double fuelLevel;
    protected VehicleStatus status;
    
    // テンプレートメソッド：車両の起動
    public final void start() {
        if (status != VehicleStatus.STOPPED) {
            throw new IllegalStateException("車両は既に動作中です");
        }
        
        performPreStartChecks();
        
        if (!hasEnoughFuel()) {
            throw new InsufficientFuelException();
        }
        
        startEngine();
        status = VehicleStatus.RUNNING;
        
        afterStart();
    }
    
    protected abstract void performPreStartChecks();
    protected abstract void startEngine();
    protected abstract boolean hasEnoughFuel();
    
    protected void afterStart() {
        // デフォルトでは何もしない（フックメソッド）
    }
}

// 陸上車両
public abstract class LandVehicle extends Vehicle {
    protected int numberOfWheels;
    protected double tirePressure;
    
    @Override
    protected void performPreStartChecks() {
        checkTirePressure();
        checkBrakes();
    }
    
    protected abstract void checkBrakes();
    
    private void checkTirePressure() {
        if (tirePressure < getMinimumTirePressure()) {
            throw new SafetyException("タイヤの空気圧が不足しています");
        }
    }
    
    protected abstract double getMinimumTirePressure();
}

// バス（陸上車両 + 乗客輸送）
public class Bus extends LandVehicle implements PassengerCarrier {
    private final int capacity;
    private final Map<String, Passenger> passengers = new HashMap<>();
    private final Queue<BusStop> route = new LinkedList<>();
    
    public Bus(String vehicleId, int capacity) {
        this.vehicleId = vehicleId;
        this.capacity = capacity;
        this.maxSpeed = 80.0;
        this.numberOfWheels = 6;
    }
    
    @Override
    public boolean boardPassenger(Passenger passenger) {
        if (passengers.size() >= capacity) {
            return false;
        }
        
        if (status != VehicleStatus.STOPPED) {
            throw new SafetyException("車両の停止中のみ乗車可能です");
        }
        
        passengers.put(passenger.getId(), passenger);
        passenger.setCurrentVehicle(this);
        
        return true;
    }
    
    @Override
    public void move(double distance) {
        if (status != VehicleStatus.RUNNING) {
            throw new IllegalStateException("車両が起動していません");
        }
        
        double fuelConsumption = calculateFuelConsumption(distance);
        if (fuelLevel < fuelConsumption) {
            throw new InsufficientFuelException();
        }
        
        // 移動処理
        position = position.moveTowards(getNextStop().getPosition(), distance);
        fuelLevel -= fuelConsumption;
        
        // 停留所に到着したかチェック
        if (position.equals(getNextStop().getPosition())) {
            arriveAtStop(route.poll());
        }
    }
    
    private void arriveAtStop(BusStop stop) {
        status = VehicleStatus.STOPPED;
        
        // 降車処理
        List<String> alightingPassengers = passengers.values().stream()
            .filter(p -> p.getDestination().equals(stop))
            .map(Passenger::getId)
            .collect(Collectors.toList());
        
        for (String passengerId : alightingPassengers) {
            alightPassenger(passengerId);
        }
        
        // 新規乗客の乗車
        for (Passenger waiting : stop.getWaitingPassengers()) {
            if (!boardPassenger(waiting)) {
                break; // 満員
            }
        }
    }
}

// 航空機（複雑な多重インターフェース実装）
public class Airplane extends Vehicle 
        implements PassengerCarrier, CargoCarrier {
    
    private final int passengerCapacity;
    private final double maxCargoWeight;
    private double altitude;
    private FlightPlan flightPlan;
    private final List<CrewMember> crew = new ArrayList<>();
    
    // 離陸処理（複雑なテンプレートメソッド）
    public void takeOff() {
        // 事前チェック
        performPreFlightChecks();
        
        // 滑走路での加速
        while (currentSpeed < getTakeOffSpeed()) {
            accelerate();
        }
        
        // 離陸
        status = VehicleStatus.AIRBORNE;
        climb(getCruiseAltitude());
    }
    
    private void performPreFlightChecks() {
        checkFuelLevel();
        checkCargoBalance();
        checkPassengerSeatBelts();
        checkCrewReadiness();
        checkWeatherConditions();
        checkRunwayAvailability();
    }
}
```

## 評価ポイント

1. **継承階層の設計**（30点）
   - 適切な抽象化レベル
   - 共通機能の親クラスへの集約
   - 過度な継承の回避

2. **ポリモーフィズムの活用**（25点）
   - 統一的なインターフェースの提供
   - 型に依存しない処理の実装
   - 拡張性の確保

3. **インターフェースとの組み合わせ**（25点）
   - 能力ベースの設計
   - 多重実装による柔軟性
   - 契約による設計

4. **設計パターンの適用**（20点）
   - テンプレートメソッドパターン
   - ストラテジパターン
   - ファクトリパターン

## 発展学習の提案

1. **SOLID原則の実践**：
   - 単一責任の原則
   - リスコフの置換原則
   - 依存性逆転の原則

2. **継承 vs コンポジション**：
   - 継承の限界を理解
   - コンポジションによる柔軟な設計
   - ミックスインパターン

3. **ジェネリクスとの組み合わせ**：
   - 型安全な継承階層
   - 境界型パラメータ
   - 共変と反変

これらの応用課題を通じて、実務で求められる高度な継承設計とポリモーフィズムの活用技術を身につけてください。