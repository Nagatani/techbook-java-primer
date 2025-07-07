# 付録B.5: ソフトウェア設計原則

この付録では、ソフトウェア設計の基本原則とアーキテクチャパターンについて詳細に解説します。

## B.5.1 SOLID原則とカプセル化

> **対象読者**: ソフトウェア設計の原則を深く理解したい方  
> **前提知識**: オブジェクト指向の基本概念  
> **関連章**: 第4章

## なぜSOLID原則が重要なのか

### 悪い設計による実際の問題

**問題1: 単一責任原則違反による保守地獄**
```java
// 悪い例：全てを一つのクラスで処理
public class UserManager {
    public void saveUser(User user) { /* DB処理 */ }
    public String generateReport(User user) { /* レポート生成 */ }
    public void sendEmail(User user) { /* メール送信 */ }
    public boolean validateUser(User user) { /* バリデーション */ }
}
// 問題：どれか一つを変更すると全体に影響、テストも困難
```
**実際の影響**: レポート機能変更で全ユーザー管理機能が停止、復旧に2日間

**問題2: 密結合による変更の困難性**
```java
// 悪い例：具象クラスに直接依存
public class OrderService {
    private MySQLUserRepository userRepo = new MySQLUserRepository();
    private SMTPEmailService emailService = new SMTPEmailService();
    
    public void processOrder(Order order) {
        User user = userRepo.findById(order.getUserId());  // MySQL依存
        emailService.send(user.getEmail(), "Order confirmed");  // SMTP依存
    }
}
// 問題：DBやメールサービス変更で大量修正、テストでモック困難
```
**影響**: クラウド移行時にDB/メール変更で3ヶ月のリファクタリング作業

### ビジネスへの深刻な影響

**設計原則違反による実際のコスト**

SOLID原則に反した設計は、組織に深刻なコストをもたらします。開発効率の面では、密結合により新機能追加時間が3-5倍に増加します。品質に関しては、変更時の影響範囲を予測することが困難となり、バグ混入率が倍増します。技術債務として、悪い設計が蓄積することで開発速度が年々低下していきます。人材面では、複雑なコードにより新人教育コストが増加し、結果として離職率の上昇を招きます。

**SOLID原則がもたらす効果**

適切にSOLID原則を適用することで、これらの問題を解決できます。変更容易性については、単一責任原則により変更の影響を局所化でき、修正時間を50-70%短縮できます。テストの観点では、依存性注入によりユニットテストの作成が容易になり、品質が向上します。再利用性では、インターフェイス分離により別プロジェクトでも再利用可能なコンポーネントを作成できます。拡張性については、開放閉鎖原則により既存コードを変更することなく新機能を追加できます。

**実際の成功事例**

SOLID原則の導入により、多くの組織が顕著な成果を上げています。某ECサイトでは、SOLID原則の導入により新機能リリース頻度を3倍向上させました。金融システムでは、依存性注入によりテストカバレッジ95%を達成し、障害率を90%削減しました。SaaSプラットフォームでは、インターフェイス分離により顧客カスタマイズ工数を80%削減することができました。

**設計投資がもたらす効果**

適切な設計への投資は、長期的に大きなリターンをもたらします。初期コストとしては設計検討時間が20%増加しますが、継続的な効果として年間開発コストを30-50%削減し、品質を大幅に向上させることができます。また、技術的負債を予防することで、将来のリファクタリングコストを削減し、長期的な競争力を確保できます。

---

### SOLID原則との関連

オブジェクト指向設計では、保守性の高いコードを書くためのSOLID原則があります。カプセル化は特に以下の原則と密接に関連しています：

#### 単一責任原則 (Single Responsibility Principle)

**責任の分離による保守性向上**

単一責任原則は、クラスが変更される理由を一つに限定することで、システムの安定性と保守性を向上させます：

**原則違反の問題点**：
- 一つの変更が複数の機能に影響
- テストケースの作成が複雑化
- 修正時のリグレッション発生リスク
- 異なる変更理由による競合の発生

**適切な責任分離の効果**：
- **変更の局所化**: 給与計算変更時にDB操作に影響しない
- **テスト容易性**: 各責任を独立してテスト可能
- **再利用性**: SalaryCalculatorを他の文脈でも利用可能
- **並行開発**: 異なるチームが独立して開発可能

**実装戦略**：
- Employeeクラス：データ保持のみに責任を限定
- SalaryCalculatorクラス：計算ロジックに特化
- EmployeeRepositoryクラス：永続化処理に特化
```

#### 開放閉鎖原則 (Open/Closed Principle)
カプセル化により内部実装を隠蔽し、インターフェイスを安定させることで、拡張に開かれ、修まさに閉じた設計が可能になります。

#### インターフェイス分離原則 (Interface Segregation Principle)
クライアントが使用しないメソッドへの依存を強制すべきではありません。大きなインターフェイスを小さな専用インターフェイスに分割します。

```java
// 悪い例：大きすぎるインターフェイス
interface Worker {
    void work();
    void eat();
    void sleep();
}

// ロボットクラスはeat()とsleep()を必要としない
class Robot implements Worker {
    public void work() { /* 実装 */ }
    public void eat() { /* 不要だが実装を強制される */ }
    public void sleep() { /* 不要だが実装を強制される */ }
}

// 良い例：インターフェイスを分離
interface Workable {
    void work();
}

interface Feedable {
    void eat();
}

interface Sleepable {
    void sleep();
}

class Human implements Workable, Feedable, Sleepable {
    public void work() { /* 実装 */ }
    public void eat() { /* 実装 */ }
    public void sleep() { /* 実装 */ }
}

class Robot implements Workable {
    public void work() { /* 実装 */ }
    // 不要なメソッドの実装は強制されない
}
```

#### 依存関係逆転原則 (Dependency Inversion Principle)
高レベルモジュールは低レベルモジュールに依存すべきではありません。両方とも抽象に依存すべきです。

```java
// 悪い例：具象クラスに直接依存
class EmailService {
    public void sendEmail(String message) { /* 実装 */ }
}

class NotificationManager {
    private EmailService emailService = new EmailService(); // 具象依存
    
    public void sendNotification(String message) {
        emailService.sendEmail(message);
    }
}

// 良い例：抽象に依存
interface NotificationService {
    void send(String message);
}

class EmailService implements NotificationService {
    public void send(String message) { /* Email実装 */ }
}

class SMSService implements NotificationService {
    public void send(String message) { /* SMS実装 */ }
}

class NotificationManager {
    private NotificationService service; // 抽象に依存
    
    public NotificationManager(NotificationService service) {
        this.service = service; // 依存性注入
    }
    
    public void sendNotification(String message) {
        service.send(message);
    }
}
```

### 情報隠蔽の深い意味

カプセル化は単なる「データを隠す」技術ではありません。David Parnasが提唱した情報隠蔽の概念は、以下の利点をもたらします：

1. **変更の局所化**: 実装変更の影響を最小限に抑える
2. **再利用性の向上**: インターフェイスが安定することで再利用しやすくなる
3. **テスト容易性**: 依存関係が明確になりテストが書きやすくなる
4. **並行開発**: 異なる開発者が独立してクラスを開発できる

### 抽象化レベルの考え方

優れたクラス設計では、適切な抽象化レベルを維持することが重要です：

```java
// 低レベルな実装詳細を隠蔽する例
public class FileProcessor {
    private List<String> processedLines;
    private BufferedReader reader;
    
    // 高レベルなインターフェイス
    public List<String> processFile(String filename) {
        // 内部で複雑な処理を隠蔽
        openFile(filename);
        readLines();
        processLines();
        closeFile();
        return processedLines;
    }
    
    // 低レベルな詳細は private で隠蔽
    private void openFile(String filename) { /* ... */ }
    private void readLines() { /* ... */ }
    private void processLines() { /* ... */ }
    private void closeFile() { /* ... */ }
}
```

### 契約による設計 (Design by Contract)

カプセル化は、クラスが外部に提供する「契約」を明確にします：

- **事前条件 (Precondition)**: メソッド呼び出し時に満たすべき条件
- **事後条件 (Postcondition)**: メソッド実行後に保証される条件  
- **不変条件 (Invariant)**: オブジェクトが常に満たすべき条件

```java
public class Rectangle {
    private double width, height;
    
    public void setWidth(double width) {
        // 事前条件: 幅は正の値
        if (width <= 0) {
            throw new IllegalArgumentException("幅は正の値である必要があります");
        }
        this.width = width;
        // 事後条件: 不変条件（面積 > 0）が維持される
        assert calculateArea() > 0;
    }
    
    // 不変条件: 長方形の面積は常に正の値
    private boolean invariant() {
        return width > 0 && height > 0;
    }
}
```

## B.5.2 デザインパターンとアーキテクチャ

### 生成パターン

#### シングルトンパターン

```java
// スレッドセーフなシングルトン実装
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private static final Object lock = new Object();
    
    private DatabaseConnection() {
        // 初期化処理
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
}

// より良い実装：Enum Singleton
public enum DatabaseConnectionEnum {
    INSTANCE;
    
    private Connection connection;
    
    DatabaseConnectionEnum() {
        // 初期化処理
        initializeConnection();
    }
    
    public Connection getConnection() {
        return connection;
    }
}
```

#### ファクトリパターン

```java
// 抽象ファクトリーパターン
interface UIFactory {
    Button createButton();
    TextField createTextField();
}

class WindowsUIFactory implements UIFactory {
    public Button createButton() {
        return new WindowsButton();
    }
    
    public TextField createTextField() {
        return new WindowsTextField();
    }
}

class MacUIFactory implements UIFactory {
    public Button createButton() {
        return new MacButton();
    }
    
    public TextField createTextField() {
        return new MacTextField();
    }
}

// 使用例
public class Application {
    private UIFactory factory;
    
    public Application(String osType) {
        if ("Windows".equals(osType)) {
            factory = new WindowsUIFactory();
        } else if ("Mac".equals(osType)) {
            factory = new MacUIFactory();
        }
    }
    
    public void createUI() {
        Button button = factory.createButton();
        TextField textField = factory.createTextField();
        // UI構築
    }
}
```

### 構造パターン

#### アダプタパターン

```java
// 既存のクラス（変更不可）
class LegacyPrinter {
    public void printOldFormat(String text) {
        System.out.println("Legacy: " + text);
    }
}

// 新しいインターフェイス
interface ModernPrinter {
    void print(Document document);
}

// アダプター
class PrinterAdapter implements ModernPrinter {
    private LegacyPrinter legacyPrinter;
    
    public PrinterAdapter(LegacyPrinter legacyPrinter) {
        this.legacyPrinter = legacyPrinter;
    }
    
    @Override
    public void print(Document document) {
        // ドキュメントを古い形式に変換
        String text = document.getText();
        legacyPrinter.printOldFormat(text);
    }
}
```

#### デコレーターパターン

```java
// 基本インターフェイス
interface Coffee {
    double cost();
    String description();
}

// 基本実装
class SimpleCoffee implements Coffee {
    @Override
    public double cost() {
        return 2.0;
    }
    
    @Override
    public String description() {
        return "Simple Coffee";
    }
}

// デコレーター基底クラス
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

// 具体的なデコレーター
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double cost() {
        return coffee.cost() + 0.5;
    }
    
    @Override
    public String description() {
        return coffee.description() + " + Milk";
    }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    @Override
    public double cost() {
        return coffee.cost() + 0.2;
    }
    
    @Override
    public String description() {
        return coffee.description() + " + Sugar";
    }
}

// 使用例
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
coffee = new SugarDecorator(coffee);
System.out.println(coffee.description() + " costs " + coffee.cost());
```

### 振る舞いパターン

#### オブザーバパターン

```java
// Subject（観察対象）
interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}

// Observer（観察者）
interface Observer {
    void update(String message);
}

// 具体的なSubject
class NewsAgency implements Observable {
    private List<Observer> observers = new ArrayList<>();
    private String news;
    
    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }
    
    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
    
    public void setNews(String news) {
        this.news = news;
        notifyObservers();
    }
}

// 具体的なObserver
class NewsChannel implements Observer {
    private String name;
    
    public NewsChannel(String name) {
        this.name = name;
    }
    
    @Override
    public void update(String news) {
        System.out.println(name + " received news: " + news);
    }
}
```

#### ストラテジーパターン

```java
// 戦略インターフェイス
interface PaymentStrategy {
    void pay(double amount);
}

// 具体的な戦略
class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;
    
    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using Credit Card: " + cardNumber);
    }
}

class PayPalPayment implements PaymentStrategy {
    private String email;
    
    public PayPalPayment(String email) {
        this.email = email;
    }
    
    @Override
    public void pay(double amount) {
        System.out.println("Paid " + amount + " using PayPal: " + email);
    }
}

// コンテキスト
class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
    
    public void checkout(double amount) {
        paymentStrategy.pay(amount);
    }
}
```

## B.5.3 アーキテクチャパターン

### レイヤドアーキテクチャ

```java
// プレゼンテーション層
@RestController
public class UserController {
    @Autowired
    private UserService userService;
    
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        UserDTO dto = UserDTO.from(user);
        return ResponseEntity.ok(dto);
    }
}

// ビジネス層
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    public User createUser(CreateUserRequest request) {
        // ビジネスロジック
        validateUser(request);
        User user = new User(request.getName(), request.getEmail());
        return userRepository.save(user);
    }
}

// データアクセス層
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

### ヘキサゴナルアーキテクチャ（ポート&アダプタ）

```java
// ドメイン層（中心）
public class Order {
    private OrderId id;
    private List<OrderItem> items;
    private OrderStatus status;
    
    public void addItem(Product product, int quantity) {
        // ビジネスルール
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        items.add(new OrderItem(product, quantity));
    }
    
    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot confirm empty order");
        }
        this.status = OrderStatus.CONFIRMED;
    }
}

// ポート（インターフェイス）
public interface OrderRepository {
    void save(Order order);
    Optional<Order> findById(OrderId id);
}

public interface PaymentService {
    PaymentResult processPayment(Order order);
}

// アプリケーションサービス
@Service
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    
    public OrderApplicationService(OrderRepository orderRepository, 
                                 PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }
    
    @Transactional
    public void processOrder(OrderId orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        order.confirm();
        PaymentResult result = paymentService.processPayment(order);
        
        if (result.isSuccessful()) {
            orderRepository.save(order);
        } else {
            throw new PaymentFailedException(result.getErrorMessage());
        }
    }
}

// アダプター（実装）
@Repository
public class JpaOrderRepository implements OrderRepository {
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public void save(Order order) {
        entityManager.persist(order);
    }
    
    @Override
    public Optional<Order> findById(OrderId id) {
        OrderEntity entity = entityManager.find(OrderEntity.class, id.getValue());
        return entity != null ? Optional.of(entity.toDomain()) : Optional.empty();
    }
}
```

### Clean Architecture

```java
// エンティティ（最内層）
public class User {
    private final UserId id;
    private final String name;
    private final Email email;
    
    public User(UserId id, String name, Email email) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
    }
    
    public boolean canSendMessage() {
        return email.isVerified();
    }
}

// ユースケース（アプリケーションビジネスルール）
public class SendMessageUseCase {
    private final UserRepository userRepository;
    private final MessageService messageService;
    
    public SendMessageUseCase(UserRepository userRepository, 
                            MessageService messageService) {
        this.userRepository = userRepository;
        this.messageService = messageService;
    }
    
    public void execute(SendMessageRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new UserNotFoundException(request.getUserId()));
        
        if (!user.canSendMessage()) {
            throw new UnverifiedEmailException();
        }
        
        Message message = new Message(
            user.getId(),
            request.getRecipient(),
            request.getContent()
        );
        
        messageService.send(message);
    }
}

// インターフェイスアダプター
@RestController
public class MessageController {
    private final SendMessageUseCase sendMessageUseCase;
    
    public MessageController(SendMessageUseCase sendMessageUseCase) {
        this.sendMessageUseCase = sendMessageUseCase;
    }
    
    @PostMapping("/messages")
    public ResponseEntity<Void> sendMessage(@RequestBody SendMessageDTO dto) {
        SendMessageRequest request = dto.toRequest();
        sendMessageUseCase.execute(request);
        return ResponseEntity.ok().build();
    }
}
```

### 参考文献・関連資料
- "Clean Code" - Robert C. Martin
- "Effective Java" - Joshua Bloch
- "Object-Oriented Software Construction" - Bertrand Meyer
- "Design Patterns" - Gang of Four
- "Clean Architecture" - Robert C. Martin
- "Domain-Driven Design" - Eric Evans