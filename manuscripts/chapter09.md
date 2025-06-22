# 第9章 ラムダ式と関数型インタフェース

## はじめに：プログラミングパラダイムの収束と関数型プログラミングの復活

前章までで、Javaにおけるオブジェクト指向プログラミングの核心技術について学習してきました。この章では、Java 8（2014年）で導入された革命的な機能である「ラムダ式（Lambda expressions）」と「関数型インターフェイス（Functional interfaces）」について詳細に学習していきます。

これらの機能は、単なる新しい構文の追加ではありません。これは、プログラミング言語の設計思想における重要な転換点を示しており、オブジェクト指向と関数型プログラミングの融合による、より表現力豊かで効率的なプログラミングスタイルの実現を目指したものです。

### プログラミングパラダイムの歴史的発展

プログラミング言語の歴史を振り返ると、複数のプログラミングパラダイム（programming paradigm）が並行して発展してきました。それぞれのパラダイムは、異なる問題領域や思考モデルに適した解決手法を提供してきました。

**手続き型プログラミング（1950年代〜）**：プログラムを一連の手順（procedure）として記述する手法で、FORTRAN、COBOL、Cなどの言語で実装されました。計算処理を段階的に記述することで、複雑な問題を解決可能な形に分解します。

**オブジェクト指向プログラミング（1960年代〜）**：データとそれを操作する手続きを一体化したオブジェクトを中心とする手法で、Simula、Smalltalk、C++、Javaなどで発展しました。現実世界のモデリングと大規模システムの構築に適しています。

**関数型プログラミング（1950年代〜）**：数学的な関数概念を基盤とし、計算を関数の組み合わせとして表現する手法で、LISP、ML、Haskell、Erlangなどで実装されました。宣言的なプログラミングスタイルと、副作用を排除した純粋性を重視します。

**論理型プログラミング（1970年代〜）**：論理的推論に基づいてプログラムを記述する手法で、Prologなどで実装されました。人工知能や知識表現の分野で活用されています。

### 関数型プログラミングの核心思想

関数型プログラミングは、数学における関数概念をプログラミングの基礎とする思想です。その核心的な原則は以下の通りです：

**第一級関数（First-class functions）**：関数を値として扱い、変数に代入したり、他の関数の引数として渡したり、戻り値として返したりできる仕組みです。これにより、高度な抽象化と柔軟なプログラム構造が実現できます。

**不変性（Immutability）**：一度作成されたデータは変更されることがなく、新しいデータは既存のデータから変換によって生成されます。これにより、プログラムの予測可能性と安全性が向上します。

**純粋関数（Pure functions）**：同じ入力に対して常に同じ出力を返し、副作用（外部状態の変更）を持たない関数です。テストが容易で、並行処理における安全性が保証されます。

**高階関数（Higher-order functions）**：関数を引数として受け取ったり、関数を戻り値として返したりする関数です。map、filter、reduceなどの操作により、データ変換を宣言的に記述できます。

### Java言語における関数型機能の必要性

Javaは、長期間にわたってオブジェクト指向パラダイムを中心として発展してきましたが、2010年代に入り、以下のような課題が顕在化しました：

**並行処理の複雑性**：マルチコアプロセッサの普及により、並行処理の重要性が高まりましたが、従来のオブジェクト指向的なアプローチでは、状態の共有と変更に伴う競合状態やデッドロックなどの問題が深刻化しました。

**ビッグデータ処理の需要**：大量のデータを効率的に処理するため、宣言的で高レベルな操作が求められるようになりました。従来のfor文による命令的な処理では、意図が不明確で、最適化も困難でした。

**API設計の制約**：インターフェイスに単一のメソッドしか定義できない場合でも、匿名クラスによる冗長な記述が必要で、コードの可読性と保守性が低下していました。

**関数型言語との競争**：Scala、Clojure、Kotlinなど、JVM上で動作する関数型の特徴を持つ言語が登場し、Javaの表現力不足が指摘されるようになりました。

### Java 8における関数型機能の導入

これらの課題に対応するため、Java 8では大規模な言語拡張が行われました。その中核となるのが、ラムダ式と関数型インターフェイスです：

**ラムダ式の導入**：匿名関数を簡潔に記述できる構文が追加され、関数をファーストクラスの値として扱えるようになりました。これにより、高階関数の使用が自然で読みやすい形で実現されました。

**関数型インターフェイスの標準化**：Function、Predicate、Consumer、Supplierなど、よく使用される関数型のパターンが標準ライブラリとして提供され、一貫した関数型プログラミングスタイルが可能になりました。

**Stream APIの導入**：コレクションに対する関数型操作を支援する強力なAPIが追加され、map、filter、reduceなどの関数型的な操作を効率的に実行できるようになりました。

**メソッド参照の導入**：既存のメソッドを関数として参照する簡潔な記法が追加され、コードの重複を避けながら可読性を向上させることができるようになりました。

### 関数型プログラミングとオブジェクト指向の融合

Java 8の革新性は、関数型プログラミングとオブジェクト指向プログラミングを対立するものとして扱うのではなく、相互補完的な関係として統合したことにあります：

**適材適所の活用**：データの構造化と状態管理にはオブジェクト指向を使用し、データの変換と操作には関数型を使用するという、ハイブリッドなアプローチが可能になりました。

**既存コードとの互換性**：ラムダ式は既存の関数型インターフェイスとシームレスに統合され、既存のAPIやライブラリを変更することなく、新しい機能を活用できます。

**段階的な学習曲線**：オブジェクト指向に慣れ親しんだ開発者が、段階的に関数型の概念を学習し、適用できる環境が整備されました。

### 現代的なソフトウェア開発における関数型の重要性

現代のソフトウェア開発において、関数型プログラミングの重要性はますます高まっています：

**リアクティブプログラミング**：非同期イベントストリームの処理において、関数型の操作が中核的な役割を果たしています。RxJava、Project Reactorなどのライブラリは、ラムダ式を前提とした設計になっています。

**クラウドネイティブ開発**：サーバーレスアーキテクチャやマイクロサービスでは、状態を持たない純粋関数の概念が、スケーラビリティと保守性の向上に貢献しています。

**データ分析と機械学習**：Apache Spark、Apache Beamなどのビッグデータ処理フレームワークでは、関数型の操作が標準的なプログラミングモデルとなっています。

**テスト駆動開発**：純粋関数はテストが容易で、モックやスタブの必要性が減少し、より信頼性の高いテストコードが作成できます。

### この章で学習する内容の意義

この章では、これらの歴史的背景と現代的な課題を踏まえて、Javaにおける関数型プログラミング機能を体系的に学習していきます。単にラムダ式の書き方を覚えるのではなく、以下の点を重視して学習を進めます：

**関数型思考の習得**：問題を関数の組み合わせとして捉え、宣言的にプログラムを記述する思考力を身につけます。

**適切な使い分け**：オブジェクト指向と関数型のそれぞれの長所を理解し、状況に応じて適切なパラダイムを選択する判断力を養います。

**Stream APIの実践的活用**：大量のデータを効率的に処理するためのStream操作を習得し、現代的なデータ処理技術の基礎を身につけます。

**非同期プログラミングの基礎**：CompletableFutureやリアクティブプログラミングライブラリとの連携により、非同期処理の基本概念を理解します。

**関数型設計パターン**：従来のGoFデザインパターンを関数型の観点から再解釈し、より簡潔で表現力豊かな設計手法を習得します。

関数型プログラミングを深く理解することは、Javaプログラマーとしての表現力を大幅に向上させ、現代的なソフトウェア開発手法への適応力を身につけることにつながります。この章を通じて、単なる「新しい構文」を超えて、「思考の新しい次元」を開拓していきましょう。

この章では、Java 8で導入されたラムダ式と関数型プログラミングについて学習します。C言語の関数ポインタとの比較も含めて、モダンなJavaプログラミング手法を習得しましょう。

## 9.1 ラムダ式とは

### C言語の関数ポインタとの比較

```c
// C言語の関数ポインタ
int add(int a, int b) {
    return a + b;
}

int (*operation)(int, int) = add;
int result = operation(5, 3);
```

```java
// Javaのラムダ式
BinaryOperator<Integer> operation = (a, b) -> a + b;
int result = operation.apply(5, 3);
```

### 従来の匿名クラス vs ラムダ式：実践的なイベント処理システム

以下の包括的な例では、イベント処理システムを通じて、従来の匿名クラスからラムダ式への進化と、その実用的な利点を学習します：

```java
import java.util.*;
import java.util.function.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * イベント処理システムにおけるラムダ式活用例
 * 従来の匿名クラスと比較した、関数型プログラミングの利点を実証
 */

// イベントデータクラス
class Event {
    private String eventId;
    private String eventType;
    private String message;
    private LocalDateTime timestamp;
    private int severity; // 1-5 (1:情報, 5:緊急)
    
    public Event(String eventId, String eventType, String message, int severity) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.message = message;
        this.severity = severity;
        this.timestamp = LocalDateTime.now();
    }
    
    // ゲッターメソッド
    public String getEventId() { return eventId; }
    public String getEventType() { return eventType; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getSeverity() { return severity; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s: %s (重要度:%d) - %s", 
            eventType, eventId, message, severity,
            timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }
}

// イベントハンドラーインターフェイス
@FunctionalInterface
interface EventHandler {
    void handle(Event event);
}

// イベント処理システム
public class EventProcessingSystem {
    private List<Event> events = new ArrayList<>();
    private List<EventHandler> handlers = new ArrayList<>();
    
    public void addEvent(Event event) {
        events.add(event);
        System.out.println("イベント追加: " + event);
        notifyHandlers(event);
    }
    
    public void addHandler(EventHandler handler) {
        handlers.add(handler);
    }
    
    private void notifyHandlers(Event event) {
        handlers.forEach(handler -> handler.handle(event));
    }
    
    // 従来の匿名クラスを使った処理例
    public void demonstrateAnonymousClasses() {
        System.out.println("\n=== 従来の匿名クラス使用例 ===");
        
        // 重要度フィルタ（匿名クラス）
        List<Event> criticalEvents = events.stream()
            .filter(new Predicate<Event>() {
                @Override
                public boolean test(Event event) {
                    return event.getSeverity() >= 4;
                }
            })
            .collect(Collectors.toList());
        
        // イベント変換（匿名クラス）
        List<String> eventSummaries = events.stream()
            .map(new Function<Event, String>() {
                @Override
                public String apply(Event event) {
                    return String.format("%s: %s", event.getEventType(), event.getMessage());
                }
            })
            .collect(Collectors.toList());
        
        // ソート（匿名クラス）
        List<Event> sortedEvents = new ArrayList<>(events);
        sortedEvents.sort(new Comparator<Event>() {
            @Override
            public int compare(Event e1, Event e2) {
                return Integer.compare(e2.getSeverity(), e1.getSeverity());
            }
        });
        
        System.out.println("重要イベント数（匿名クラス）: " + criticalEvents.size());
        System.out.println("サマリー数（匿名クラス）: " + eventSummaries.size());
        System.out.println("ソート済みイベント数（匿名クラス）: " + sortedEvents.size());
    }
    
    // ラムダ式を使った処理例（同じ処理をより簡潔に）
    public void demonstrateLambdaExpressions() {
        System.out.println("\n=== ラムダ式使用例 ===");
        
        // 重要度フィルタ（ラムダ式）
        List<Event> criticalEvents = events.stream()
            .filter(event -> event.getSeverity() >= 4)
            .collect(Collectors.toList());
        
        // イベント変換（ラムダ式）
        List<String> eventSummaries = events.stream()
            .map(event -> String.format("%s: %s", event.getEventType(), event.getMessage()))
            .collect(Collectors.toList());
        
        // ソート（ラムダ式）
        List<Event> sortedEvents = events.stream()
            .sorted((e1, e2) -> Integer.compare(e2.getSeverity(), e1.getSeverity()))
            .collect(Collectors.toList());
        
        System.out.println("重要イベント数（ラムダ式）: " + criticalEvents.size());
        System.out.println("サマリー数（ラムダ式）: " + eventSummaries.size());
        System.out.println("ソート済みイベント数（ラムダ式）: " + sortedEvents.size());
    }
    
    // メソッド参照を使った処理例（さらに簡潔）
    public void demonstrateMethodReferences() {
        System.out.println("\n=== メソッド参照使用例 ===");
        
        // イベントタイプ別グループ化
        Map<String, List<Event>> eventsByType = events.stream()
            .collect(Collectors.groupingBy(Event::getEventType));
        
        // 重要度別カウント
        Map<Integer, Long> severityCounts = events.stream()
            .collect(Collectors.groupingBy(Event::getSeverity, Collectors.counting()));
        
        // 全イベントの表示
        System.out.println("全イベント一覧:");
        events.forEach(System.out::println);
        
        System.out.println("\nイベントタイプ別グループ:");
        eventsByType.forEach((type, eventList) -> 
            System.out.println(type + ": " + eventList.size() + "件"));
        
        System.out.println("\n重要度別カウント:");
        severityCounts.forEach((severity, count) -> 
            System.out.println("重要度" + severity + ": " + count + "件"));
    }
    
    // 関数型プログラミングの合成例
    public void demonstrateFunctionalComposition() {
        System.out.println("\n=== 関数型プログラミング合成例 ===");
        
        // 複数の述語の組み合わせ
        Predicate<Event> isHighSeverity = event -> event.getSeverity() >= 4;
        Predicate<Event> isSystemEvent = event -> event.getEventType().equals("SYSTEM");
        Predicate<Event> isRecentEvent = event -> 
            event.getTimestamp().isAfter(LocalDateTime.now().minusMinutes(5));
        
        // 述語の合成
        Predicate<Event> criticalSystemEvent = isHighSeverity.and(isSystemEvent);
        Predicate<Event> importantRecentEvent = isHighSeverity.and(isRecentEvent);
        
        // 関数の合成
        Function<Event, String> eventToType = Event::getEventType;
        Function<String, String> typeToCategory = type -> 
            type.startsWith("SYS") ? "システム" : "アプリケーション";
        Function<Event, String> eventToCategory = eventToType.andThen(typeToCategory);
        
        // 合成された述語と関数の使用
        long criticalSystemCount = events.stream()
            .filter(criticalSystemEvent)
            .count();
        
        List<String> eventCategories = events.stream()
            .map(eventToCategory)
            .distinct()
            .collect(Collectors.toList());
        
        System.out.println("重要なシステムイベント数: " + criticalSystemCount);
        System.out.println("イベントカテゴリ: " + eventCategories);
    }
    
    public static void main(String[] args) {
        EventProcessingSystem system = new EventProcessingSystem();
        
        // イベントハンドラーの登録（ラムダ式使用）
        system.addHandler(event -> {
            if (event.getSeverity() >= 4) {
                System.out.println("🚨 重要アラート: " + event.getMessage());
            }
        });
        
        system.addHandler(event -> {
            if (event.getEventType().equals("SECURITY")) {
                System.out.println("🔒 セキュリティ監査ログに記録: " + event.getEventId());
            }
        });
        
        // サンプルイベントの追加
        system.addEvent(new Event("EVT001", "SYSTEM", "サーバー起動完了", 2));
        system.addEvent(new Event("EVT002", "SECURITY", "不正アクセス試行検出", 5));
        system.addEvent(new Event("EVT003", "APPLICATION", "ユーザーログイン", 1));
        system.addEvent(new Event("EVT004", "SYSTEM", "メモリ使用量警告", 4));
        system.addEvent(new Event("EVT005", "SECURITY", "権限昇格試行", 5));
        system.addEvent(new Event("EVT006", "APPLICATION", "データ処理完了", 2));
        
        // 各種処理方法のデモンストレーション
        system.demonstrateAnonymousClasses();
        system.demonstrateLambdaExpressions();
        system.demonstrateMethodReferences();
        system.demonstrateFunctionalComposition();
    }
}
```

**このプログラムから学ぶ重要なラムダ式の概念：**

1. **簡潔性の向上**：匿名クラスと比較して、ラムダ式は大幅にコード量を削減し、可読性を向上させます。

2. **関数型思考**：データの変換と処理を関数の組み合わせとして表現できます。

3. **合成可能性**：述語（Predicate）や関数（Function）を組み合わせて、より複雑な処理を構築できます。

4. **宣言的プログラミング**：「何をするか」に集中でき、「どのようにするか」の詳細は言語に任せられます。

5. **関数をファーストクラスとして扱える**：関数を変数に代入し、引数として渡し、戻り値として返すことができます。

## 9.2 関数型インタフェース：データ処理パイプラインの構築

### 標準の関数型インタフェースの実践的活用：オンライン注文処理システム

以下の包括的な例では、オンライン注文処理システムを通じて、各種関数型インターフェイスの実用的な活用方法と、それらを組み合わせた強力なデータ処理パイプラインを学習します：

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * オンライン注文処理システムにおける関数型インターフェイス活用例
 * 標準関数型インターフェイスの実践的使用と組み合わせパターンを実証
 */

// 商品データクラス
class Product {
    private String productId;
    private String name;
    private BigDecimal price;
    private String category;
    private int stockQuantity;
    
    public Product(String productId, String name, BigDecimal price, String category, int stockQuantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.stockQuantity = stockQuantity;
    }
    
    // ゲッターメソッド
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public BigDecimal getPrice() { return price; }
    public String getCategory() { return category; }
    public int getStockQuantity() { return stockQuantity; }
    
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    @Override
    public String toString() {
        return String.format("%s: %s (¥%s)", productId, name, price);
    }
}

// 注文アイテムクラス
class OrderItem {
    private Product product;
    private int quantity;
    
    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    
    public BigDecimal getTotalPrice() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
    
    @Override
    public String toString() {
        return String.format("%s x%d = ¥%s", product.getName(), quantity, getTotalPrice());
    }
}

// 注文クラス
class Order {
    private String orderId;
    private String customerId;
    private List<OrderItem> items;
    private LocalDateTime orderTime;
    private String status;
    
    public Order(String orderId, String customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = new ArrayList<>();
        this.orderTime = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public void addItem(OrderItem item) { items.add(item); }
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public List<OrderItem> getItems() { return new ArrayList<>(items); }
    public LocalDateTime getOrderTime() { return orderTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public BigDecimal getTotalAmount() {
        return items.stream()
                   .map(OrderItem::getTotalPrice)
                   .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public String toString() {
        return String.format("Order %s (%s): ¥%s - %s", orderId, customerId, getTotalAmount(), status);
    }
}

public class OrderProcessingSystem {
    private List<Product> products = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    
    public OrderProcessingSystem() {
        initializeProducts();
    }
    
    private void initializeProducts() {
        products.add(new Product("P001", "ノートパソコン", new BigDecimal("89800"), "電子機器", 10));
        products.add(new Product("P002", "マウス", new BigDecimal("2800"), "電子機器", 50));
        products.add(new Product("P003", "キーボード", new BigDecimal("8500"), "電子機器", 30));
        products.add(new Product("P004", "モニター", new BigDecimal("35200"), "電子機器", 15));
        products.add(new Product("P005", "本：Java入門", new BigDecimal("3200"), "書籍", 25));
        products.add(new Product("P006", "本：データ構造", new BigDecimal("4800"), "書籍", 20));
    }
    
    // Predicate<T>の活用例：フィルタリング処理
    public void demonstratePredicates() {
        System.out.println("=== Predicate<T>の活用例 ===");
        
        // 基本的な述語
        Predicate<Product> isExpensive = product -> product.getPrice().compareTo(new BigDecimal("10000")) > 0;
        Predicate<Product> isElectronics = product -> "電子機器".equals(product.getCategory());
        Predicate<Product> isInStock = product -> product.getStockQuantity() > 0;
        Predicate<Product> isLowStock = product -> product.getStockQuantity() < 20;
        
        // 述語の合成
        Predicate<Product> expensiveElectronics = isExpensive.and(isElectronics);
        Predicate<Product> availableOrBooks = isInStock.or(product -> "書籍".equals(product.getCategory()));
        Predicate<Product> notExpensive = isExpensive.negate();
        
        // フィルタリングの実行
        List<Product> expensiveElectronicProducts = products.stream()
            .filter(expensiveElectronics)
            .collect(Collectors.toList());
        
        List<Product> lowStockProducts = products.stream()
            .filter(isLowStock)
            .collect(Collectors.toList());
        
        System.out.println("高価な電子機器: " + expensiveElectronicProducts.size() + "件");
        System.out.println("在庫少製品: " + lowStockProducts.size() + "件");
        
        // 動的な述語生成
        Predicate<Product> createPriceFilter(BigDecimal minPrice, BigDecimal maxPrice) {
            return product -> product.getPrice().compareTo(minPrice) >= 0 
                           && product.getPrice().compareTo(maxPrice) <= 0;
        }
        
        Predicate<Product> midRangeProducts = createPriceFilter(new BigDecimal("5000"), new BigDecimal("50000"));
        long midRangeCount = products.stream().filter(midRangeProducts).count();
        System.out.println("中価格帯製品: " + midRangeCount + "件");
    }
    
    // Function<T, R>の活用例：データ変換処理
    public void demonstrateFunctions() {
        System.out.println("\n=== Function<T, R>の活用例 ===");
        
        // 基本的な関数
        Function<Product, String> productToName = Product::getName;
        Function<Product, BigDecimal> productToPrice = Product::getPrice;
        Function<Product, String> productToDescription = product -> 
            String.format("%s (¥%s)", product.getName(), product.getPrice());
        
        // 関数の合成
        Function<Product, String> productToPriceString = productToPrice.andThen(price -> "¥" + price);
        Function<String, String> nameToUpperCase = String::toUpperCase;
        Function<Product, String> productToUpperName = productToName.andThen(nameToUpperCase);
        
        // データ変換の実行
        List<String> productNames = products.stream()
            .map(productToName)
            .collect(Collectors.toList());
        
        List<String> productDescriptions = products.stream()
            .map(productToDescription)
            .collect(Collectors.toList());
        
        Map<String, List<String>> productsByCategory = products.stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,
                Collectors.mapping(productToName, Collectors.toList())
            ));
        
        System.out.println("製品名一覧: " + productNames.size() + "件");
        System.out.println("カテゴリ別製品:");
        productsByCategory.forEach((category, names) -> 
            System.out.println("  " + category + ": " + names));
        
        // 複雑な変換チェーン
        Function<List<Product>, Map<String, BigDecimal>> calculateCategoryAverages = 
            productList -> productList.stream()
                .collect(Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.averagingDouble(p -> p.getPrice().doubleValue())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> BigDecimal.valueOf(entry.getValue()).setScale(2, RoundingMode.HALF_UP)
                ));
        
        Map<String, BigDecimal> categoryAverages = calculateCategoryAverages.apply(products);
        System.out.println("カテゴリ別平均価格: " + categoryAverages);
    }
    
    // Consumer<T>の活用例：副作用のある処理
    public void demonstrateConsumers() {
        System.out.println("\n=== Consumer<T>の活用例 ===");
        
        // 基本的なコンシューマー
        Consumer<Product> printProduct = product -> System.out.println("製品: " + product);
        Consumer<Product> updateStock = product -> {
            if (product.getStockQuantity() < 10) {
                product.setStockQuantity(product.getStockQuantity() + 50);
                System.out.println("在庫補充: " + product.getName() + " -> " + product.getStockQuantity());
            }
        };
        Consumer<Order> processOrder = order -> {
            order.setStatus("PROCESSING");
            System.out.println("注文処理開始: " + order.getOrderId());
        };
        
        // コンシューマーの合成
        Consumer<Product> printAndUpdate = printProduct.andThen(updateStock);
        
        // 処理の実行
        System.out.println("低在庫製品の補充:");
        products.stream()
            .filter(product -> product.getStockQuantity() < 20)
            .forEach(printAndUpdate);
        
        // バッチ処理用のコンシューマー
        Consumer<List<Order>> batchProcessOrders = orderList -> {
            System.out.println("バッチ処理開始: " + orderList.size() + "件の注文");
            orderList.forEach(processOrder);
            System.out.println("バッチ処理完了");
        };
        
        batchProcessOrders.accept(orders);
    }
    
    // Supplier<T>の活用例：遅延評価とファクトリーパターン
    public void demonstrateSuppliers() {
        System.out.println("\n=== Supplier<T>の活用例 ===");
        
        // 基本的なサプライヤー
        Supplier<String> orderIdGenerator = () -> "ORD" + System.currentTimeMillis();
        Supplier<LocalDateTime> currentTime = LocalDateTime::now;
        Supplier<BigDecimal> randomDiscount = () -> 
            BigDecimal.valueOf(Math.random() * 0.2).setScale(2, RoundingMode.HALF_UP);
        
        // 設定可能なサプライヤー
        Supplier<Product> createRandomProduct = () -> {
            String[] categories = {"電子機器", "書籍", "衣料品"};
            String category = categories[(int)(Math.random() * categories.length)];
            return new Product(
                "P" + System.currentTimeMillis(),
                "ランダム製品" + (int)(Math.random() * 1000),
                BigDecimal.valueOf(1000 + Math.random() * 49000).setScale(0, RoundingMode.HALF_UP),
                category,
                (int)(Math.random() * 100)
            );
        };
        
        // 遅延評価の活用
        Supplier<List<Product>> expensiveProductsSupplier = () -> 
            products.stream()
                .filter(p -> p.getPrice().compareTo(new BigDecimal("10000")) > 0)
                .collect(Collectors.toList());
        
        // 実行
        System.out.println("新しい注文ID: " + orderIdGenerator.get());
        System.out.println("現在時刻: " + currentTime.get());
        System.out.println("ランダム割引: " + randomDiscount.get() + "%");
        
        Product randomProduct = createRandomProduct.get();
        System.out.println("ランダム製品: " + randomProduct);
        
        // 必要な時まで計算を遅延
        System.out.println("高価格製品の遅延評価:");
        if (Math.random() > 0.5) { // 条件によって実行
            List<Product> expensiveProducts = expensiveProductsSupplier.get();
            System.out.println("高価格製品数: " + expensiveProducts.size());
        }
    }
    
    // BinaryOperator<T>とUnaryOperator<T>の活用例
    public void demonstrateOperators() {
        System.out.println("\n=== Operator系の活用例 ===");
        
        // BinaryOperator<T>の例
        BinaryOperator<BigDecimal> addPrices = BigDecimal::add;
        BinaryOperator<BigDecimal> multiplyPrices = BigDecimal::multiply;
        BinaryOperator<String> combineNames = (s1, s2) -> s1 + " & " + s2;
        BinaryOperator<Product> selectCheaper = (p1, p2) -> 
            p1.getPrice().compareTo(p2.getPrice()) <= 0 ? p1 : p2;
        
        // UnaryOperator<T>の例
        UnaryOperator<BigDecimal> applyTax = price -> price.multiply(new BigDecimal("1.10"));
        UnaryOperator<BigDecimal> applyDiscount = price -> price.multiply(new BigDecimal("0.9"));
        UnaryOperator<String> formatProductName = name -> "[商品] " + name;
        
        // オペレーターの合成
        UnaryOperator<BigDecimal> applyTaxAndDiscount = applyTax.andThen(applyDiscount);
        
        // 実行例
        BigDecimal price1 = new BigDecimal("10000");
        BigDecimal price2 = new BigDecimal("15000");
        
        System.out.println("価格の合計: " + addPrices.apply(price1, price2));
        System.out.println("税込価格: " + applyTax.apply(price1));
        System.out.println("税込割引価格: " + applyTaxAndDiscount.apply(price1));
        
        // 注文の総額計算
        if (!orders.isEmpty()) {
            Optional<BigDecimal> totalOrderValue = orders.stream()
                .map(Order::getTotalAmount)
                .reduce(addPrices);
            totalOrderValue.ifPresent(total -> 
                System.out.println("全注文の総額: " + total));
        }
        
        // 製品名の整形
        List<String> formattedNames = products.stream()
            .map(Product::getName)
            .map(formatProductName)
            .collect(Collectors.toList());
        System.out.println("整形済み製品名: " + formattedNames.size() + "件");
    }
    
    // 複合的な処理パイプライン例
    public void demonstrateComplexPipeline() {
        System.out.println("\n=== 複合的な処理パイプライン ===");
        
        // サンプル注文の作成
        Order order1 = new Order("ORD001", "CUST001");
        order1.addItem(new OrderItem(products.get(0), 1)); // ノートパソコン
        order1.addItem(new OrderItem(products.get(1), 2)); // マウス x2
        
        Order order2 = new Order("ORD002", "CUST002");
        order2.addItem(new OrderItem(products.get(4), 3)); // 本 x3
        
        orders.add(order1);
        orders.add(order2);
        
        // 複合パイプライン：高額注文の特別処理
        Predicate<Order> isHighValueOrder = order -> order.getTotalAmount().compareTo(new BigDecimal("50000")) > 0;
        Function<Order, String> createVipMessage = order -> 
            String.format("VIPお客様 %s への特別サービス適用 (注文額: %s)", 
                         order.getCustomerId(), order.getTotalAmount());
        Consumer<String> sendNotification = message -> System.out.println("📧 " + message);
        
        // パイプラインの実行
        orders.stream()
            .filter(isHighValueOrder)
            .map(createVipMessage)
            .forEach(sendNotification);
        
        // 複合統計処理
        Map<String, Object> orderStatistics = orders.stream()
            .collect(Collectors.teeing(
                Collectors.summingDouble(order -> order.getTotalAmount().doubleValue()),
                Collectors.averagingDouble(order -> order.getTotalAmount().doubleValue()),
                (sum, avg) -> Map.of(
                    "totalValue", BigDecimal.valueOf(sum),
                    "averageValue", BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP),
                    "orderCount", orders.size()
                )
            ));
        
        System.out.println("注文統計: " + orderStatistics);
    }
    
    public static void main(String[] args) {
        OrderProcessingSystem system = new OrderProcessingSystem();
        
        system.demonstratePredicates();
        system.demonstrateFunctions();
        system.demonstrateConsumers();
        system.demonstrateSuppliers();
        system.demonstrateOperators();
        system.demonstrateComplexPipeline();
    }
}
```

**このプログラムから学ぶ重要な関数型インターフェイスの概念：**

1. **Predicate<T>**: 条件判定とフィルタリングの強力なツール。`and()`, `or()`, `negate()`による論理演算で複雑な条件を構築できます。

2. **Function<T, R>**: データ変換の中核。`andThen()`, `compose()`によるチェーン処理で複雑な変換パイプラインを構築できます。

3. **Consumer<T>**: 副作用のある処理（出力、状態変更）に特化。`andThen()`による処理の連鎖が可能です。

4. **Supplier<T>**: 遅延評価とファクトリーパターンに最適。必要な時まで計算を遅延できます。

5. **Operator系**: 同一型での演算に特化した特殊なFunction。数値計算や文字列処理で威力を発揮します。

### カスタム関数型インタフェース

```java
@FunctionalInterface
public interface MathOperation {
    double calculate(double a, double b);
}

@FunctionalInterface
public interface StringProcessor {
    String process(String input);
}

public class CustomFunctionalInterface {
    public static void main(String[] args) {
        // カスタム関数型インターフェースの使用
        MathOperation addition = (a, b) -> a + b;
        MathOperation multiplication = (a, b) -> a * b;
        MathOperation power = (a, b) -> Math.pow(a, b);
        
        System.out.println(addition.calculate(5, 3));        // 8.0
        System.out.println(multiplication.calculate(4, 7));  // 28.0
        System.out.println(power.calculate(2, 3));           // 8.0
        
        StringProcessor upperCase = s -> s.toUpperCase();
        StringProcessor reverse = s -> new StringBuilder(s).reverse().toString();
        StringProcessor addPrefix = s -> "【重要】" + s;
        
        System.out.println(upperCase.process("hello"));      // "HELLO"
        System.out.println(reverse.process("Java"));         // "avaJ"
        System.out.println(addPrefix.process("お知らせ"));    // "【重要】お知らせ"
    }
}
```

## 9.3 メソッド参照

### 各種メソッド参照

```java
import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // 静的メソッド参照
        words.forEach(System.out::println);
        
        // インスタンスメソッド参照（特定のオブジェクト）
        String prefix = "Fruit: ";
        Function<String, String> addPrefix = prefix::concat;
        
        // インスタンスメソッド参照（任意のオブジェクト）
        Function<String, String> toUpper = String::toUpperCase;
        Function<String, Integer> getLength = String::length;
        
        words.stream()
             .map(String::toUpperCase)  // メソッド参照
             .forEach(System.out::println);
        
        // コンストラクタ参照
        Supplier<List<String>> listSupplier = ArrayList::new;
        Function<String, StringBuilder> sbBuilder = StringBuilder::new;
        
        List<String> newList = listSupplier.get();
        StringBuilder sb = sbBuilder.apply("Hello");
    }
    
    // 静的メソッドの例
    public static void printWithPrefix(String s) {
        System.out.println("Value: " + s);
    }
}
```

## 9.4 高階関数

### 関数を引数として受け取る

```java
import java.util.*;
import java.util.function.*;

public class HigherOrderFunction {
    
    // 高階関数：関数を引数として受け取る
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(mapper.apply(item));
        }
        return result;
    }
    
    public static <T> T reduce(List<T> list, T identity, BinaryOperator<T> accumulator) {
        T result = identity;
        for (T item : list) {
            result = accumulator.apply(result, item);
        }
        return result;
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 偶数のフィルタリング
        List<Integer> evenNumbers = filter(numbers, n -> n % 2 == 0);
        System.out.println("偶数: " + evenNumbers);
        
        // 2倍にマッピング
        List<Integer> doubled = map(numbers, n -> n * 2);
        System.out.println("2倍: " + doubled);
        
        // 合計の計算
        Integer sum = reduce(numbers, 0, (a, b) -> a + b);
        System.out.println("合計: " + sum);
        
        // チェイニング
        List<String> result = map(
            filter(numbers, n -> n % 2 == 0),
            n -> "Number: " + n
        );
        System.out.println("結果: " + result);
    }
}
```

### 関数を返す関数

```java
import java.util.function.*;

public class FunctionFactory {
    
    // 関数を返す関数
    public static Function<Integer, Integer> multiplier(int factor) {
        return x -> x * factor;
    }
    
    public static Predicate<String> lengthChecker(int minLength) {
        return s -> s.length() >= minLength;
    }
    
    public static Function<String, String> decorator(String prefix, String suffix) {
        return s -> prefix + s + suffix;
    }
    
    public static void main(String[] args) {
        // 乗算器の作成
        Function<Integer, Integer> doubler = multiplier(2);
        Function<Integer, Integer> tripler = multiplier(3);
        
        System.out.println(doubler.apply(5));  // 10
        System.out.println(tripler.apply(4));  // 12
        
        // 長さチェッカーの作成
        Predicate<String> longEnough = lengthChecker(5);
        
        System.out.println(longEnough.test("Hello"));     // true
        System.out.println(longEnough.test("Hi"));        // false
        
        // デコレーターの作成
        Function<String, String> htmlTag = decorator("<h1>", "</h1>");
        Function<String, String> bracket = decorator("[", "]");
        
        System.out.println(htmlTag.apply("タイトル"));      // "<h1>タイトル</h1>"
        System.out.println(bracket.apply("重要"));         // "[重要]"
    }
}
```

## 9.5 関数の合成

```java
import java.util.function.*;

public class FunctionComposition {
    public static void main(String[] args) {
        Function<String, String> addHello = s -> "Hello " + s;
        Function<String, String> addExclamation = s -> s + "!";
        Function<String, String> toUpper = String::toUpperCase;
        
        // andThen: f.andThen(g) = g(f(x))
        Function<String, String> greetingComposed = addHello
            .andThen(addExclamation)
            .andThen(toUpper);
        
        System.out.println(greetingComposed.apply("World"));  // "HELLO WORLD!"
        
        // compose: f.compose(g) = f(g(x))
        Function<String, Integer> getLength = String::length;
        Function<Integer, Integer> doubleValue = x -> x * 2;
        
        Function<String, Integer> lengthDoubler = doubleValue.compose(getLength);
        System.out.println(lengthDoubler.apply("Hello"));  // 10
        
        // Predicateの合成
        Predicate<String> isLong = s -> s.length() > 5;
        Predicate<String> startsWithA = s -> s.startsWith("A");
        
        Predicate<String> longAndStartsWithA = isLong.and(startsWithA);
        Predicate<String> shortOrStartsWithA = isLong.negate().or(startsWithA);
        
        System.out.println(longAndStartsWithA.test("Application"));  // true
        System.out.println(shortOrStartsWithA.test("App"));          // true
    }
}
```

## 9.6 実践的な例

### イベント処理システム

```java
import java.util.*;
import java.util.function.*;

class Event {
    private String type;
    private String message;
    private long timestamp;
    
    public Event(String type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getType() { return type; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s (%d)", type, message, timestamp);
    }
}

public class EventProcessor {
    private List<Consumer<Event>> handlers = new ArrayList<>();
    
    public void addHandler(Consumer<Event> handler) {
        handlers.add(handler);
    }
    
    public void addConditionalHandler(Predicate<Event> condition, Consumer<Event> handler) {
        handlers.add(event -> {
            if (condition.test(event)) {
                handler.accept(event);
            }
        });
    }
    
    public void processEvent(Event event) {
        handlers.forEach(handler -> handler.accept(event));
    }
    
    public static void main(String[] args) {
        EventProcessor processor = new EventProcessor();
        
        // 全イベントをログに記録
        processor.addHandler(event -> 
            System.out.println("LOG: " + event));
        
        // エラーイベントのみ特別処理
        processor.addConditionalHandler(
            event -> "ERROR".equals(event.getType()),
            event -> System.err.println("ERROR ALERT: " + event.getMessage())
        );
        
        // 情報イベントをファイルに保存（模擬）
        processor.addConditionalHandler(
            event -> "INFO".equals(event.getType()),
            event -> System.out.println("SAVED TO FILE: " + event.getMessage())
        );
        
        // テストイベント
        processor.processEvent(new Event("INFO", "システム開始"));
        processor.processEvent(new Event("ERROR", "データベース接続エラー"));
        processor.processEvent(new Event("DEBUG", "デバッグ情報"));
    }
}
```

### 計算パイプライン

```java
import java.util.function.*;

public class CalculationPipeline {
    
    public static class Pipeline<T> {
        private Function<T, T> function;
        
        public Pipeline(Function<T, T> function) {
            this.function = function;
        }
        
        public Pipeline<T> then(Function<T, T> next) {
            return new Pipeline<>(function.andThen(next));
        }
        
        public T execute(T input) {
            return function.apply(input);
        }
    }
    
    public static void main(String[] args) {
        // 数値処理パイプライン
        Pipeline<Integer> numberPipeline = new Pipeline<Integer>(x -> x)
            .then(x -> x + 10)      // 10を加算
            .then(x -> x * 2)       // 2倍
            .then(x -> x - 5);      // 5を減算
        
        System.out.println(numberPipeline.execute(5));  // ((5+10)*2)-5 = 25
        
        // 文字列処理パイプライン
        Pipeline<String> stringPipeline = new Pipeline<String>(s -> s)
            .then(String::trim)
            .then(String::toLowerCase)
            .then(s -> s.replaceAll("\\s+", "_"))
            .then(s -> "processed_" + s);
        
        String result = stringPipeline.execute("  Hello World  ");
        System.out.println(result);  // "processed_hello_world"
    }
}
```

## 9.7 練習問題

1. 整数のリストを受け取り、偶数のみを抽出して2倍にし、降順でソートする関数を作成してください。

2. 文字列の検証を行う関数を組み合わせて、複合的な検証ルールを作成してください。

3. 簡単な電卓アプリケーションを作成し、演算を関数として渡せるようにしてください。

## 9.8 Recordの活用（Java 16以降）

Java 16から正式に導入されたRecordは、データを格納するためのシンプルなクラスを簡単に定義できる機能です。ラムダ式と組み合わせることで、よりモダンで簡潔なJavaコードを書くことができます。

### Recordの基本

```java
// 従来のクラスでのデータ保持
class Employee {
    private final String name;
    private final int age;
    private final String department;
    
    public Employee(String name, int age, String department) {
        this.name = name;
        this.age = age;
        this.department = department;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDepartment() { return department; }
    
    @Override
    public boolean equals(Object obj) {
        // 省略...
    }
    
    @Override
    public int hashCode() {
        // 省略...
    }
    
    @Override
    public String toString() {
        return "Employee{name='" + name + "', age=" + age + ", department='" + department + "'}";
    }
}

// Recordでのデータ保持
public record Employee(String name, int age, String department) {
    // コンストラクタ、アクセサメソッド、equals、hashCode、toStringが自動生成される
}

public class RecordBasicExample {
    public static void main(String[] args) {
        Employee emp = new Employee("Alice", 25, "IT");
        
        // アクセサメソッドはフィールド名と同じ
        System.out.println("名前: " + emp.name());
        System.out.println("年齢: " + emp.age());
        System.out.println("部署: " + emp.department());
        
        // toStringが自動生成される
        System.out.println(emp); // Employee[name=Alice, age=25, department=IT]
        
        // 不変オブジェクトなので、新しいインスタンスを作成
        Employee emp2 = new Employee("Bob", 30, "Sales");
        System.out.println(emp2);
    }
}
```

### Recordの特徴とメリット

1. **ボイラープレートコードの削減**: コンストラクタ、getter、equals、hashCode、toStringが自動生成
2. **不変性**: 一度作成されたオブジェクトの値は変更不可
3. **スレッドセーフ**: 不変オブジェクトなのでマルチスレッド環境でも安全

### Recordとラムダ式の組み合わせ

```java
import java.util.*;
import java.util.stream.*;

public record Person(String name, int age, String city) {}

public class RecordWithLambdaExample {
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", 25, "Tokyo"),
            new Person("Bob", 30, "Osaka"),
            new Person("Charlie", 35, "Tokyo"),
            new Person("Dave", 40, "Nagoya"),
            new Person("Eve", 45, "Tokyo")
        );
        
        // 30歳以上の人を抽出
        List<Person> adults = people.stream()
            .filter(person -> person.age() >= 30)
            .collect(Collectors.toList());
        
        System.out.println("30歳以上:");
        adults.forEach(System.out::println);
        
        // 都市別にグループ化
        Map<String, List<Person>> byCity = people.stream()
            .collect(Collectors.groupingBy(Person::city));
        
        System.out.println("\n都市別グループ:");
        byCity.forEach((city, persons) -> {
            System.out.println(city + ": " + persons.size() + "人");
            persons.forEach(p -> System.out.println("  " + p.name() + "(" + p.age() + ")"));
        });
        
        // 平均年齢を計算
        double averageAge = people.stream()
            .mapToInt(Person::age)
            .average()
            .orElse(0.0);
        
        System.out.println("\n平均年齢: " + averageAge);
        
        // 名前をアルファベット順にソート
        List<String> sortedNames = people.stream()
            .map(Person::name)
            .sorted()
            .collect(Collectors.toList());
        
        System.out.println("\nソートされた名前: " + sortedNames);
    }
}
```

### RecordでのCSVデータ処理

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public record CsvPerson(String name, int age, String city) {
    // バリデーションを追加することも可能
    public CsvPerson {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        if (age < 0) {
            throw new IllegalArgumentException("年齢は0以上である必要があります");
        }
    }
    
    // カスタムメソッドを追加できる
    public boolean isAdult() {
        return age >= 20;
    }
    
    public String getDisplayName() {
        return name + "(" + age + "歳)";
    }
}

public class CsvRecordProcessor {
    
    public static List<CsvPerson> readCsvFile(String filename) {
        try {
            return Files.lines(Paths.get(filename))
                .map(line -> line.split(","))
                .filter(fields -> fields.length == 3)
                .map(fields -> new CsvPerson(
                    fields[0].trim(),
                    Integer.parseInt(fields[1].trim()),
                    fields[2].trim()
                ))
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("ファイル読み込みエラー: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public static void main(String[] args) {
        // CSVデータの例: "Alice,25,Tokyo"
        // 実際にはファイルから読み込み
        List<CsvPerson> people = Arrays.asList(
            new CsvPerson("Alice", 25, "Tokyo"),
            new CsvPerson("Bob", 17, "Osaka"),
            new CsvPerson("Charlie", 35, "Tokyo"),
            new CsvPerson("Dave", 16, "Nagoya")
        );
        
        System.out.println("全データ:");
        people.forEach(System.out::println);
        
        // 成人のみを抽出
        System.out.println("\n成人のみ:");
        people.stream()
              .filter(CsvPerson::isAdult)
              .forEach(person -> System.out.println(person.getDisplayName()));
        
        // 都市別統計
        System.out.println("\n都市別人数:");
        people.stream()
              .collect(Collectors.groupingBy(CsvPerson::city, Collectors.counting()))
              .forEach((city, count) -> System.out.println(city + ": " + count + "人"));
    }
}
```

### Recordと関数型インターフェイスの活用

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record Product(String name, double price, String category) {
    
    public boolean isExpensive() {
        return price > 1000.0;
    }
    
    public Product withDiscount(double percentage) {
        double discountedPrice = price * (1.0 - percentage / 100.0);
        return new Product(name, discountedPrice, category);
    }
}

public class ProductProcessor {
    
    // 商品をフィルタリングする関数
    public static List<Product> filterProducts(List<Product> products, Predicate<Product> condition) {
        return products.stream()
                      .filter(condition)
                      .collect(Collectors.toList());
    }
    
    // 商品を変換する関数
    public static List<Product> transformProducts(List<Product> products, Function<Product, Product> transformer) {
        return products.stream()
                      .map(transformer)
                      .collect(Collectors.toList());
    }
    
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
            new Product("ノートPC", 80000.0, "電子機器"),
            new Product("マウス", 2000.0, "電子機器"),
            new Product("本", 1500.0, "書籍"),
            new Product("コーヒー", 500.0, "飲料"),
            new Product("スマートフォン", 120000.0, "電子機器")
        );
        
        System.out.println("全商品:");
        products.forEach(System.out::println);
        
        // 高額商品の抽出
        List<Product> expensiveProducts = filterProducts(products, Product::isExpensive);
        System.out.println("\n高額商品 (1000円以上):");
        expensiveProducts.forEach(System.out::println);
        
        // 電子機器の抽出
        List<Product> electronics = filterProducts(products, 
            product -> "電子機器".equals(product.category()));
        System.out.println("\n電子機器:");
        electronics.forEach(System.out::println);
        
        // 20%オフのセールを適用
        List<Product> saleProducts = transformProducts(products, 
            product -> product.withDiscount(20.0));
        System.out.println("\n20%オフセール価格:");
        saleProducts.forEach(System.out::println);
        
        // カテゴリ別平均価格
        Map<String, Double> averagePriceByCategory = products.stream()
            .collect(Collectors.groupingBy(
                Product::category,
                Collectors.averagingDouble(Product::price)
            ));
        
        System.out.println("\nカテゴリ別平均価格:");
        averagePriceByCategory.forEach((category, avgPrice) -> 
            System.out.println(category + ": " + String.format("%.0f円", avgPrice)));
        
        // 最も高い商品
        products.stream()
                .max(Comparator.comparingDouble(Product::price))
                .ifPresent(product -> 
                    System.out.println("\n最高額商品: " + product));
    }
}
```

### Recordの制約と注意点

1. **継承不可**: Recordは他のクラスを継承できない
2. **変更不可**: フィールドの値は変更できない
3. **フィールドの依存**: フィールドが可変オブジェクトの場合、Record自体は不変でも内部データが変更される可能性がある

```java
import java.util.*;

// 注意: Listは可変オブジェクト
public record TeamRecord(String name, List<String> members) {
    // コピーを作成して不変性を保証
    public TeamRecord(String name, List<String> members) {
        this.name = name;
        this.members = List.copyOf(members); // 不変リストとしてコピー
    }
    
    public List<String> members() {
        return members; // これは既に不変リストなので安全
    }
}

public class RecordImmutabilityExample {
    public static void main(String[] args) {
        List<String> memberList = new ArrayList<>();
        memberList.add("Alice");
        memberList.add("Bob");
        
        TeamRecord team = new TeamRecord("開発チーム", memberList);
        
        // 元のリストを変更してもRecord内のデータは影響を受けない
        memberList.add("Charlie");
        
        System.out.println("チームメンバー: " + team.members()); // [Alice, Bob]
        
        // Recordから取得したリストは不変
        // team.members().add("Dave"); // UnsupportedOperationException
    }
}
```

## まとめ

この章では、ラムダ式と関数型プログラミングの基礎、そしてモダンJavaのRecord機能を学習しました。関数型インタフェース、メソッド参照、高階関数、関数の合成などの概念と、Recordによる簡潔なデータクラスの作成を組み合わせることで、より簡潔で読みやすく、保守性の高いモダンJavaコードを書けるようになりました。

Recordは特にStream APIやラムダ式と組み合わせることで、データ処理や集計操作を非常に簡潔に記述でき、ボイラープレートコードを大幅に削減しながら型安全性と不変性を保証できます。