# 第13章 ラムダ式と関数型インターフェイス<small>関数型プログラミングへの扱い</small>

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの大切な前提知識がポイントです。まずポイントとなる前提として、第11章までに学んだオブジェクト指向プログラミングとジェネリクスの概念を十分に習得していることが求められます。特に、インターフェイスの設計と実装、匿名クラスの作成と活用についての実践的な経験が大切です。これらの知識は、ラムダ式が匿名クラスの簡潔な表現であること、そして関数型インターフェイスとの関係性を理解する上で基礎となります。コレクションフレームワークの実践的な使用経験も大切で、List、Set、Mapの操作を通じてラムダ式の実用性を実感できます。

さらに、概念的な前提として、関数型プログラミングの基本概念への関心を持っていることが推奨されます。従来の命令型プログラミングとは異なるアプローチである関数型プログラミングでは、「どうやって」ではなく「何を」するかを表現する宣言的なスタイルが特徴です。コードの簡潔性と可読性への意識も重要で、ラムダ式や関数型インターフェイスを用いることで、より簡潔で表現力の高いコードを書けるようになるためです。

### 学習目標

本章では、Java 8で導入されたラムダ式と関数型インターフェイスの包括的な知識と技術を習得します。知識理解の面では、まず関数型プログラミングパラダイムの基本概念を深く理解します。関数型プログラミングは、計算を関数の組み合わせとして表現し、状態変更や副作用を避けるアプローチです。ラムダ式の文法と意味論を学び、匿名関数を簡潔に表現する方法を理解します。Javaの標準ライブラリで提供される関数型インターフェイス（Function、Predicate、Consumer、Supplier等）の特性と使い道を習得し、メソッド参照の概念と使用法を学びます。

技能習得の面では、ラムダ式を活用した簡潔で表現力の高いコード実装技術を身につけます。標準関数型インターフェイスを効果的に活用し、メソッド参照によってコードの可読性を向上させる方法を習得します。従来の匿名クラスからラムダ式への移行手法も大切なスキルで、既存コードの改善に活用できます。

プログラミングスタイルの観点からは、宣言的なプログラミングスタイルを習得します。これは、「どのように」実行するかよりも「何を」行うかを明確に表現する方法で、コードの意図をより直感的に伝えることができます。関数の合成を使った柔軟なプログラム設計手法を学び、再利用可能で保守性の高いコードを作成できます。副作用の少ない関数型スタイルを実践することで、バグの発生を減らし、テストしやすいコードを書けます。

最終的な到達レベルとしては、ラムダ式と関数型インターフェイスを状況に応じて適切に使い分けできます。複雑な処理をラムダ式の組み合わせで簡潔かつ理解しやすく表現できる能力を身につけ、現代的なJavaアプリケーション開発において、関数型プログラミングの利点を最大限に活用できることが、本章の最終目標です。
- 関数型スタイルで可読性の高いコードが書ける
- カスタム関数型インターフェイスが設計・実装できる



## 13.1 匿名クラスからラムダ式へ

Java 8でラムダ式が導入される前、その場限りのインターフェイス実装を提供するためには**匿名クラス（Anonymous Class）**が使われていました。これは名前を持たないクラスで、特にGUIのイベントリスナなどで多用されていました。

### ラムダ式への第一歩：簡単な例から始める

まず、最も簡単な例でラムダ式の基本を理解しましょう。Runnableインターフェイスを使った例を見てみます：

<span class="listing-number">**サンプルコード13-1**</span>

```java
// 従来の匿名クラスを使った方法
Runnable task1 = new Runnable() {
    @Override
    public void run() {
        System.out.println("Hello from anonymous class!");
    }
};

// ラムダ式を使った方法（同じ動作）
Runnable task2 = () -> System.out.println("Hello from lambda!");

// 実行（どちらも同じ結果）
task1.run();  // Hello from anonymous class!
task2.run();  // Hello from lambda!
```

ラムダ式は、匿名クラスの冗長な記述を大幅に簡略化します。`() ->` が「引数なしで何かを実行する」という意味になります。

<span class="listing-number">**サンプルコード13-2**</span>

```java
// 匿名クラスを使ったボタンのクリック処理
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ボタンがクリックされました！");
    }
});
```
このコードは、`actionPerformed`という1つのメソッドを実装するためだけに、`new ActionListener() { ... }`という定型的な記述が多く、冗長でした。

**ラムダ式**は、この匿名クラスの記述を、本質的な処理だけを抜き出して劇的に簡潔にするために導入されました。

## 関数型プログラミングパラダイムの歴史

関数型プログラミングの理論的基盤は、1930年代にアロンゾ・チャーチが開発したラムダ計算（Lambda Calculus）にあります。このシンプルな記法が、現代のラムダ式の源流となっています。

1958年にJohn McCarthyが開発したLispは最初の関数型言語であり、その後、メーリングリスト（1973年）、Haskell（1990年）などの純粋関数型言語が登場しました。

2010年代に入ると、並行処理やビッグデータ処理の重要性が高まり、Java 8（2014年）やJavaScript ES6（2015年）など、オブジェクト指向言語にも関数型の機能が取り入れられました。


**高階関数による処理の抽象化**

高階関数（Higher-Order Function）は、関数を引数として受け取ったり、関数を戻り値として返したりする関数です。これにより、共通的な処理パターンを抽象化し、様々な具体的な処理を統一的に扱うことができます。以下の例では、取引戦略の評価とリスク管理を高階関数として実装しています。

<span class="listing-number">**サンプルコード13-20**</span>

```java
public class HigherOrderFunctions {
    // 高階関数によるリスク管理の抽象化
    public <T, R> Function<T, R> withRiskManagement(
        Function<T, R> strategy,
        Predicate<T> riskCheck,
        R defaultValue) {
        
        return input -> {
            if (riskCheck.test(input)) {
                return strategy.apply(input);
            }
            return defaultValue;
        };
    }
    
    // 使用例：取引戦略にリスク管理を適用
    public void demonstrateHigherOrderFunction() {
        Function<MarketData, TradingDecision> baseStrategy = 
            data -> new TradingDecision(data.getPrice() > 100 ? BUY : SELL);
            
        Function<MarketData, TradingDecision> safeStrategy = 
            withRiskManagement(
                baseStrategy,
                data -> data.getVolatility() < 0.5,  // リスクチェック
                TradingDecision.HOLD  // デフォルト値
            );
            
        // 実際の取引実行
        TradingDecision decision = safeStrategy.apply(currentMarketData);
    }
}
```

### カリー化の実装例：通貨変換

<span class="listing-number">**サンプルコード13-3**</span>

```java
public class CurrencyConverter {
    // 通常の2引数から3引数の変換で、通貨レートを適用
    public Function<String, Function<String, Function<Double, Double>>> 
        curriedConvert = from -> to -> amount -> {
            double rate = getExchangeRate(from, to);
            return amount * rate;
        };
    
    // 使用例
    public void demonstrateCurrying() {
        // USDからJPYへの変換関数を作成
        Function<Double, Double> usdToJpy = curriedConvert("USD", "JPY");
        
        // 同じ変換を何度も使える
        System.out.println(usdToJpy.apply(100.0));  // 15000.0
        System.out.println(usdToJpy.apply(250.0));  // 37500.0
        
        // 複数の変換関数をマップで管理
        Map<String, Function<Double, Double>> converters = Map.of(
            "USD_TO_JPY", curriedConvert("USD", "JPY"),
            "EUR_TO_JPY", curriedConvert("EUR", "JPY"),
            "GBP_TO_JPY", curriedConvert("GBP", "JPY")
        );
    }
}
```

**モナドの実践例：Optionalを使ったエラーハンドリング**

<span class="listing-number">**サンプルコード13-4**</span>

```java
public class UserService {
    // モナドを使わない場合
    public String getUserEmailTraditional(String userId) {
        User user = userRepository.findById(userId);
        if (user != null) {
            Profile profile = user.getProfile();
            if (profile != null) {
                Email email = profile.getEmail();
                if (email != null && email.isVerified()) {
                    return email.getAddress();
                }
            }
        }
        return "noemail@example.com";
    }
    
    // Optionalモナドを使った場合
    public String getUserEmail(String userId) {
        return userRepository.findById(userId)
            .map(User::getProfile)
            .map(Profile::getEmail)
            .filter(Email::isVerified)
            .map(Email::getAddress)
            .orElse("noemail@example.com");
    }
    
    // さらに高度なモナドの合成
    public CompletableFuture<String> sendNotification(String userId) {
        return userRepository.findByIdAsync(userId)
            .thenCompose(userOpt -> userOpt
                .map(user -> notificationService.send(user))
                .orElse(CompletableFuture.
                    completedFuture("User not found"))
            );
    }
}
```

### 関数型プログラミングのアンチパターンと回避方法

**アンチパターン1: 過度なラムダネスト**

<span class="listing-number">**サンプルコード13-5**</span>

```java
// 悪い例：読みづらいネストされたラムダ
result = list.stream()
    .map(x -> {
        return transform(x, y -> {
            return process(y, z -> {
                return calculate(z);
            });
        });
    })
    .collect(Collectors.toList());

// 良い例：メソッド参照とヘルパーメソッドを使用
result = list.stream()
    .map(this::transformItem)
    .collect(Collectors.toList());

private Item transformItem(Item x) {
    return transform(x, this::processItem);
}
```

**アンチパターン2: 副作用の濫用**

<span class="listing-number">**サンプルコード13-6**</span>

```java
// 悪い例：ラムダ内で外部状態を変更
List<String> results = new ArrayList<>();
items.forEach(item -> {
    results.add(processItem(item));  // 副作用！
});

// 良い例：純粋な関数型アプローチ
List<String> results = items.stream()
    .map(this::processItem)
    .collect(Collectors.toList());
```

### 実世界での応用例：リアクティブストリーム処理

**Netflix のマイクロサービスアーキテクチャ**

Netflixは1日に数十億のAPIリクエストを処理するために、関数型プログラミングとリアクティブストリームを活用：

<span class="listing-number">**サンプルコード13-7**</span>

```java
// 従来の同期的アプローチ（スケールしない）
public class VideoRecommendationService {
    public List<Video> getRecommendations(String userId) {
        User user = userService.getUser(userId);  // ブロッキング
        List<Video> watched = historyService.getWatchHistory(userId);
                                                        // ブロッキング
        List<Video> trending = trendingService.getTrending();  // ブロッキング
        
        return recommendationEngine.calculate(user, watched, trending);
    }
}

// リアクティブ・関数型アプローチ（高スケーラビリティ）
public class ReactiveVideoRecommendationService {
    public Mono<List<Video>> getRecommendations(String userId) {
        return Mono.zip(
            userService.getUserAsync(userId),
            historyService.getWatchHistoryAsync(userId),
            trendingService.getTrendingAsync()
        )
        .map(tuple -> {
            User user = tuple.getT1();
            List<Video> watched = tuple.getT2();
            List<Video> trending = tuple.getT3();
            
            return recommendationEngine.calculate(user, watched, trending);
        })
        .timeout(Duration.ofMillis(100))
        .onErrorReturn(Collections.emptyList());
    }
}
```

**パフォーマンス比較**
- レイテンシ： 300ms → 50ms（83%削減）
- スループット： 1000 req/s → 10000 req/s（10倍向上）
- リソース使用率： 80% → 30%（62%削減）

### 関数型プログラミングの実装パターン集

**1. リトライとサーキットブレーカー**

<span class="listing-number">**サンプルコード13-8**</span>

```java
public class ResilientService {
    // 関数型リトライメカニズム
    public <T> Supplier<T> withRetry(Supplier<T> supplier, int maxAttempts) {
        return () -> {
            AtomicInteger attempts = new AtomicInteger(0);
            
**イベント処理システムでの関数型アプローチ**

イベント駆動アーキテクチャにおいて、関数型プログラミングは特に有効です。イベントハンドラをラムダ式として定義し、関数の組み合わせでイベント処理パイプラインを構築することで、保守性と拡張性の高いシステムを実現できます。

<span class="listing-number">**サンプルコード13-25**</span>

```java
public class EventProcessor {
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new ConcurrentHashMap<>();
    
    // イベントハンドラの登録
    public <T> void on(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add((Consumer<Object>) handler);
    }
    
    // 関数型フィルタとトランスフォーマー
    public <T> void onFiltered(Class<T> eventType, 
                              Predicate<T> filter,
                              Consumer<T> handler) {
        on(eventType, event -> {
            if (filter.test(event)) {
                handler.accept(event);
            }
        });
    }
    
    // 使用例
    public void setupEventHandlers() {
        // 単純なハンドラ
        on(OrderCreated.class, order -> logger.info("Order created: {}", order.getId()));
        
        // フィルタ付きハンドラ
        onFiltered(OrderCreated.class,
            order -> order.getAmount() > 1000,
            order -> notificationService.sendHighValueOrderAlert(order));
    }
}
```

            return Stream.generate(() -> {
                try {
                    return Optional.of(supplier.get());
                } catch (Exception e) {
                    if (attempts.incrementAndGet() >= maxAttempts) {
                        throw new RuntimeException("Max attempts reached", e);
                    }
                    return Optional.<T>empty();
                }
            })
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow();
        };
    }
    
    // 使用例
    public String fetchDataWithRetry() {
        Supplier<String> unreliableService = () -> {
            if (Math.random() < 0.7) throw new RuntimeException("Service unavailable");
            return "Success!";
        };
        
        Supplier<String> resilientService = withRetry(unreliableService, 3);
        return resilientService.get();
    }
}
```

**2. 関数合成によるミドルウェアパターン**

<span class="listing-number">**サンプルコード13-9**</span>

```java
public class MiddlewareChain {
    // HTTPリクエスト処理のミドルウェア
    @FunctionalInterface
    interface Middleware extends Function<Request, Response> {
        default Middleware andThen(Middleware next) {
            return request -> {
                Response response = this.apply(request);
                if (response.isSuccess()) {
                    return next.apply(request);
                }
                return response;
            };
        }
    }
    
**テンプレートメソッドパターンの関数型実装**

テンプレートメソッドパターンでは、アルゴリズムの骨格を定義し、具体的な処理ステップをサブクラスに委ねます。関数型アプローチでは、これを継承ではなく関数の組み合わせとして実現でき、より柔軟で再利用しやすい設計が可能になります。

<span class="listing-number">**サンプルコード13-23**</span>

```java
// データ処理の基本的なテンプレート
public class DataProcessingTemplate {
    public <T, R> R processData(
        T input,
        Function<T, T> preprocessor,
        Function<T, R> processor,
        Function<R, R> postprocessor) {
        
        T preprocessed = preprocessor.apply(input);
        R processed = processor.apply(preprocessed);
        return postprocessor.apply(processed);
    }
    
    // 使用例：CSVデータの処理
    public List<Customer> processCsvData(String csvData) {
        return processData(
            csvData,
            data -> data.trim().toLowerCase(),  // 前処理
            this::parseCustomers,              // メイン処理
            customers -> customers.stream()     // 後処理
                .filter(c -> c.isValid())
                .collect(Collectors.toList())
        );
    }
}
```

    // 認証ミドルウェア
    Middleware authenticate = request -> {
        String token = request.getHeader("Authorization");
        if (tokenService.isValid(token)) {
            request.setAttribute("user", tokenService.getUser(token));
            return Response.success();
        }
        return Response.unauthorized();
    };
    
    // ロギングミドルウェア
    Middleware logging = request -> {
        logger.info("Request: {} {}", request.getMethod(), request.getPath());
        long start = System.currentTimeMillis();
        
        return Try.of(() -> Response.success())
            .andFinally(() -> {
                long duration = System.currentTimeMillis() - start;
                logger.info("Response time: {}ms", duration);
            })
            .get();
    };
    
    // レート制限ミドルウェア
    Middleware rateLimiting = request -> {
        String clientId = request.getClientId();
        if (rateLimiter.tryAcquire(clientId)) {
            return Response.success();
        }
        return Response.tooManyRequests();
    };
    
    // ミドルウェアチェーンの構築
    Middleware pipeline = logging
        .andThen(rateLimiting)
        .andThen(authenticate)
        .andThen(request -> businessLogic.handle(request));
}
```

**3. イベントソーシングとCQRS**

<span class="listing-number">**サンプルコード13-10**</span>

```java
public class EventSourcedAccount {
    // イベントの定義
    sealed interface AccountEvent permits 
        AccountCreated, MoneyDeposited, MoneyWithdrawn {}
    
**ファクトリーパターンの関数型実装**

従来のファクトリーパターンでは、オブジェクトの生成ロジックを専用のクラスにカプセル化していましたが、関数型アプローチでは、Supplier関数やFunction関数を使ってより柔軟なファクトリを実装できます。これにより、実行時の条件に応じて異なる生成戦略を動的に選択することが可能になります。

<span class="listing-number">**サンプルコード13-24**</span>

```java
// 関数型ファクトリパターン
public class ProcessorFactory {
    private final Map<String, Supplier<DataProcessor>> processors = Map.of(
        "csv", CsvProcessor::new,
        "xml", XmlProcessor::new,
        "json", JsonProcessor::new
    );
    
    public Optional<DataProcessor> createProcessor(String type) {
        return Optional.ofNullable(processors.get(type))
                      .map(Supplier::get);
    }
    
    // さらに高度な例：パラメータ化されたファクトリ
    public Function<ProcessorConfig, DataProcessor> createConfigurableFactory(
        String type) {
        return config -> processors.get(type)
                                  .get()
                                  .configure(config);
    }
}
```

    record AccountCreated(String accountId, String owner) implements AccountEvent {}
    record MoneyDeposited(String accountId, Money amount) implements AccountEvent {}
    record MoneyWithdrawn(String accountId, Money amount) implements AccountEvent {}
    
    // イベントストリーム処理
    public class AccountProjection {
        public AccountState project(List<AccountEvent> events) {
            return events.stream()
                .reduce(
                    AccountState.empty(),
                    this::applyEvent,
                    (s1, s2) -> s2  // 並列処理では使用しない
                );
        }
        
        private AccountState applyEvent(AccountState state, AccountEvent event) {
            return switch (event) {
                case AccountCreated(var id, var owner) -> 
                    new AccountState(id, owner, Money.ZERO);
                    
                case MoneyDeposited(var id, var amount) -> 
                    state.withBalance(state.balance().add(amount));
                    
                case MoneyWithdrawn(var id, var amount) -> 
                    state.withBalance(state.balance().subtract(amount));
            };
        }
    }
}
```

### メモリ効率とパフォーマンス最適化

**ラムダ式の内部実装とメモリ使用**

<span class="listing-number">**サンプルコード13-11**</span>

```java
public class LambdaPerformance {
    // ラムダ式のキャプチャによるメモリ影響
    public void demonstrateCapture() {
        // キャプチャなし - 静的にインスタンス化される
        Function<Integer, Integer> noCapture = x -> x * 2;
        
        // 実質的にfinal変数のキャプチャ
        int multiplier = 3;
        Function<Integer, Integer> withCapture = x -> x * multiplier;
        // 新しいインスタンスが生成される
        
        // パフォーマンステスト
        long start = System.nanoTime();
        IntStream.range(0, 1_000_000)
            .map(x -> x * 2)  // インライン化される
            .sum();
        long inlineTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        IntStream.range(0, 1_000_000)
            .map(noCapture::apply)  // メソッド参照
            .sum();
        long methodRefTime = System.nanoTime() - start;
        
        System.out.printf("Inline: %dms, Method ref: %dms%n",
            inlineTime / 1_000_000, 
            methodRefTime / 1_000_000);
    }
}
```

**デザインパターンでの関数型アプローチ活用**

関数型プログラミングは、従来のデザインパターンをより簡潔かつ柔軟に実装する手段を提供します。特に、戦略パターン、コマンドパターン、テンプレートメソッドパターンなどでは、ラムダ式を使うことで実装を大幅に簡素化できます。以下では、実際のビジネスロジックでよく使われるパターンを関数型アプローチで実装する方法を紹介します。

### 実践的なデザインパターン：関数型ビルダ

<span class="listing-number">**サンプルコード13-12**</span>

```java
public class FunctionalBuilder {
    // 従来のビルダーパターン
    public static class TraditionalBuilder {
        private String name;
        private int age;
        
        public TraditionalBuilder withName(String name) {
            this.name = name;
            return this;
        }
        
        public TraditionalBuilder withAge(int age) {
            this.age = age;
            return this;
        }
        
        public Person build() {
            return new Person(name, age);
        }
    }
    
    // 関数型ビルダーパターン
    public static class FunctionalPersonBuilder {
        private final List<Consumer<Person>> operations = new ArrayList<>();
        
        public FunctionalPersonBuilder with(Consumer<Person> operation) {
            operations.add(operation);
            return this;
        }
        
        public Person build() {
            Person person = new Person();
            operations.forEach(op -> op.accept(person));
            return person;
        }
    }
    
    // 使用例
    public void demonstrateBuilders() {
**設定ビルダーパターンの関数型実装**

設定やコンフィギュレーションを構築する際、関数型ビルダーパターンは特に威力を発揮します。条件分岐を含む複雑な設定ロジックを、ラムダ式を使って直感的に表現できます。

<span class="listing-number">**サンプルコード13-27**</span>

```java
public class ConfigurationBuilder {
    public static class DatabaseConfig {
        private String host;
        private int port;
        private String database;
        private boolean sslEnabled;
        
        // 設定メソッド（省略）
    }
    
    public DatabaseConfig buildDatabaseConfig(String environment) {
        return new FunctionalConfigBuilder<DatabaseConfig>()
            .with(config -> config.setHost("localhost"))  // デフォルト設定
            .with(config -> config.setPort(5432))
            .whenCondition("production".equals(environment),
                config -> {
                    config.setHost("prod-db.company.com");
                    config.setSslEnabled(true);
                })
            .whenCondition("test".equals(environment),
                config -> config.setDatabase("test_db"))
            .build(DatabaseConfig::new);
    }
}
```

        // 関数型ビルダーの利点：動的な構築ロジック
        Person person = new FunctionalPersonBuilder()
            .with(p -> p.setName("Alice"))
            .with(p -> p.setAge(30))
            .with(p -> {
                if (p.getAge() >= 18) {
                    p.grantAdultPrivileges();
                }
            })
            .build();
    }
}
```

### 産業界での採用事例：金融取引システム

**高頻度取引（HFT）システムでの関数型アプローチ**

<span class="listing-number">**サンプルコード13-13**</span>

```java
public class TradingSystem {
    // マーケットデータのストリーム処理
    public class MarketDataProcessor {
        // 価格変動の分析
        public Flux<TradingSignal> analyzeMarketData(Flux<MarketTick> ticks) {
            return ticks
                .window(Duration.ofSeconds(1))
                .flatMap(window -> window
                    .collect(Collectors.toList())
**関数型アプローチによるビジネスロジック設計**

複雑なビジネスロジックを関数型で設計することで、テストしやすく、再利用可能で、理解しやすいコードを作成できます。各機能を純粋関数として実装し、関数の組み合わせで複雑な処理を表現します。

<span class="listing-number">**サンプルコード13-26**</span>

```java
public class BusinessLogicProcessor {
    // 純粋関数による価格計算
    public Function<Order, BigDecimal> calculatePrice = order -> {
        BigDecimal basePrice = order.getBasePrice();
        BigDecimal discount = applyDiscount(order);
        BigDecimal tax = calculateTax(basePrice.subtract(discount));
        return basePrice.subtract(discount).add(tax);
    };
    
    // 関数合成による複雑な処理
    public Function<Order, ProcessedOrder> processOrder = 
        order -> Optional.of(order)
            .map(this::validateOrder)
            .map(this::applyBusinessRules)
            .map(this::calculateFinalPrice)
            .map(this::generateProcessedOrder)
            .orElseThrow(() -> new OrderProcessingException("Invalid order"));
}
```

                    .map(this::calculateVolatility)
                )
                .filter(volatility -> volatility > THRESHOLD)
                .map(this::generateTradingSignal)
                .onBackpressureBuffer(1000)
                .publishOn(Schedulers.parallel());
        }
        
        // 複雑な取引戦略の組み合わせ
        public Function<MarketData, TradingDecision> combineStrategies(
            List<TradingStrategy> strategies) {
            
            return marketData -> strategies.stream()
                .map(strategy -> strategy.evaluate(marketData))
                .reduce(TradingDecision.NEUTRAL, 
                    TradingDecision::combine);
        }
    }
}
```

### 参考文献・関連資料
- "Structure and Interpretation of Computer Programs" - Abelson & Sussman
- "Functional Programming in Java" - Venkat Subramaniam
- "Java 8 in Action" - Raoul-Gabriel Urma
- "Effective Java (3rd Edition)" - Joshua Bloch
- "Modern Java in Action" - Raoul-Gabriel Urma, Mario Fusco, Alan Mycroft
- "Reactive Programming with RxJava" - Tomasz Nurkiewicz
- "Functional and Reactive Domain Modeling" - Debasish Ghosh

```java
// ラムダ式を使った場合
button.addActionListener(e -> System.out.println("ボタンがクリックされました！"));
```

## 13.2 関数型インターフェイス

ラムダ式は、どのような場所でも書けるわけではありません。ラムダ式は、**関数型インターフェイス（Functional Interface）** として扱われます。

**関数型インターフェイス**とは、**実装すべき抽象メソッドが1つだけ**定義されているインターフェイスのことです。`@FunctionalInterface` アノテーションを付けると、コンパイラが抽象メソッドが1つだけかどうかをチェックしてくれるため、付けることがあるとよいでしょう。

**Comparatorを使ったデータ並び替えの最適化**

データ並び替え処理において、Comparatorインターフェイスは可読性と性能の両方を向上させる大切な機能です。従来の冗長なComparable実装に比べて、ラムダ式を使ったComparatorは処理の意図を明確にし、複雑な並び替え条件も直感的に表現できます。特に、コレクションのソート処理でその威力を発揮し、ビジネスロジックに集中できる簡潔なコードを実現します。

<span class="listing-number">**サンプルコード13-18**</span>

```java
// 従来の方法：冗長で理解しにくい
Collections.sort(students, new Comparator<Student>() {
    @Override
    public int compare(Student s1, Student s2) {
        return s1.getName().compareTo(s2.getName());
    }
});

// ラムダ式：簡潔で意図が明確
students.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));

// メソッド参照：さらに簡潔
students.sort(Comparator.comparing(Student::getName));
```

`ActionListener`や`Comparator`も、実装すべき抽象メソッドが実質的に1つだけですので、関数型インターフェイスです。そのため、ラムダ式で置き換えることができたのです。

<span class="listing-number">**サンプルコード13-14**</span>

```java
@FunctionalInterface
interface MyFunction {
    int calculate(int x, int y);
}

public class Main {
    public static void main(String[] args) {
        // ラムダ式を関数型インターフェイス型の変数に代入
        MyFunction addition = (a, b) -> a + b;
        MyFunction subtraction = (a, b) -> a - b;

        System.out.println("足し算: " + addition.calculate(10, 5)); // 15
        System.out.println("引き算: " + subtraction.calculate(10, 5)); // 5
    }
}
```

### ラムダ式の構文バリエーション

ラムダ式は、状況に応じて記述をさらに簡略化できます。

-   **引数の型の省略**: `(int a, int b) -> ...` は `(a, b) -> ...` と書けます。
-   **引数が1つの場合、括弧の省略**: `(a) -> ...` は `a -> ...` と書けます。
-   **処理が1行の場合、中括弧の省略**: `a -> { return a * 2; }` は `a -> a * 2` と書けます。
-   **引数がない場合**: `() -> System.out.println("Hello");` のように括弧だけを書きます。

**複数条件でのComparator組み合わせ**

実際の業務では、単一の条件だけでなく、複数の条件を組み合わせた並び替えが必要になることが多くあります。Comparatorインターフェイスでは、`thenComparing`メソッドを使って複数の条件を論理的に組み合わせることができ、これにより複雑な並び替えロジックも直感的に表現できます。

<span class="listing-number">**サンプルコード13-19**</span>

```java
// 学年で並び替え、同じ学年の場合は名前で並び替え、
// さらに同名の場合は学生番号で並び替え
students.sort(
    Comparator.comparing(Student::getGrade)
        .thenComparing(Student::getName)
        .thenComparing(Student::getStudentId)
);

// 降順ソートの組み合わせ
students.sort(
    Comparator.comparing(Student::getGrade).reversed()
        .thenComparing(Student::getName)
);

// 複雑な条件：優先度、作成日時、タイトルの順
tasks.sort(
    Comparator.comparing(Task::getPriority).reversed()
        .thenComparing(Task::getCreatedAt)
        .thenComparing(Task::getTitle)
);
```

### `java.util.function` パッケージ

Javaには、`java.util.function`パッケージに、よく使われる汎用的な関数型インターフェイスが多数用意されています。これらを活用することで、自分でインターフェイスを定義する手間を省けます。

| インターフェイス | 抽象メソッド | 説明 |
| :--- | :--- | :--- |
| `Predicate<T>` | `boolean test(T t)` | T型を受け取り、`boolean`値を返す（判定） |
| `Function<T, R>` | `R apply(T t)` | T型を受け取り、R型を返す（変換） |
| `Consumer<T>` | `void accept(T t)` | T型を受け取り、何も返さない（消費） |
| `Supplier<T>` | `T get()` | 何も受け取らず、T型を返す（供給） |
| `UnaryOperator<T>` | `T apply(T t)` | T型を受け取り、同じT型を返す（単項演算） |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | 同じT型を2つ受け取り、同じT型を返す（二項演算） |

<span class="listing-number">**サンプルコード13-15**</span>

```java
import java.util.function.*;

public class StandardFunctionalInterfaces {
    public static void main(String[] args) {
        // Predicate: 文字列が空かどうかを判定
        Predicate<String> isEmpty = s -> s.isEmpty();
        System.out.println("''は空？: " + isEmpty.test("")); // true

        // Function: 文字列を長さに変換
        Function<String, Integer> getLength = s -> s.length();
        System.out.println("'Java'の長さ: " + getLength.apply("Java")); // 4

        // Consumer: 文字列を大文字で出力
        Consumer<String> printUpper = s -> System.out.println(s.toUpperCase());
        printUpper.accept("hello"); // HELLO

        // Supplier: 現在時刻を供給
        Supplier<Long> currentTime = () -> System.currentTimeMillis();
        System.out.println("現在時刻: " + currentTime.get());
    }
}
```

## 13.3 メソッド参照

ラムダ式が既存のメソッドを呼びだすだけの場合、**メソッド参照（Method Reference）**という、さらに簡潔な記法が使えます。`クラス名::メソッド名`や`インスタンス変数::メソッド名`のように記述します。

| 種類 | 構文 | ラムダ式の例 |
| :--- | :--- | :--- |
| **静的メソッド参照** | `クラス名::静的メソッド名` | `s -> Integer.parseInt(s)` |
| **インスタンスメソッド参照**<br>(特定のインスタンス) | `インスタンス変数::メソッド名` | `s -> System.out.println(s)` |
| **インスタンスメソッド参照**<br>(不特定のインスタンス) | `クラス名::メソッド名` | `s -> s.toUpperCase()` |
| **コンストラクタ参照** | `クラス名::new` | `() -> new ArrayList<>()` |

<span class="listing-number">**サンプルコード13-16**</span>

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");

**実践的なラムダ式の活用パターン**

並行処理において、ラムダ式は特に威力を発揮します。従来の匿名クラスによる冗長な記述を避け、処理の本質的な内容に集中できるようになります。特に、ExecutorServiceと組み合わせることで、スレッドプールを効率的に活用した並行処理を簡潔に記述できます。

<span class="listing-number">**サンプルコード13-21**</span>

```java
// ExecutorServiceとラムダ式の組み合わせ
ExecutorService executor = Executors.newFixedThreadPool(4);

// 複数のタスクを並行実行
List<Callable<String>> tasks = Arrays.asList(
    () -> "Task 1 completed",
    () -> "Task 2 completed", 
    () -> "Task 3 completed"
);

try {
    List<Future<String>> results = executor.invokeAll(tasks);
    results.forEach(future -> {
        try {
            System.out.println(future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
} finally {
    executor.shutdown();
}
```

        // ラムダ: s -> System.out.println(s)
        // メソッド参照: System.out::println
        words.forEach(System.out::println);

        // ラムダ: s -> s.toUpperCase()
        // メソッド参照: String::toUpperCase
        words.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);

**メソッド参照による可読性の向上**

メソッド参照は、既存のメソッドを呼び出すだけのラムダ式をより簡潔に表現する方法です。特に、Streamのmap操作やcollect操作でよく使われ、コードの意図をより明確に示すことができます。

<span class="listing-number">**サンプルコード13-22**</span>

```java
// ラムダ式 vs メソッド参照の比較
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// ラムダ式：やや冗長
names.stream()
     .map(name -> name.toUpperCase())
     .forEach(name -> System.out.println(name));

// メソッド参照：簡潔で意図が明確
names.stream()
     .map(String::toUpperCase)
     .forEach(System.out::println);

// コンストラクタ参照の活用
List<Person> people = names.stream()
    .map(Person::new)  // name -> new Person(name) と同じ
    .collect(Collectors.toList());
```

        // ラムダ: () -> new ArrayList<>()
        // メソッド参照: ArrayList::new
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        System.out.println("新しいリスト: " + newList);
    }
}
```

## 13.4 ラムダ式の応用例

ラムダ式はコレクション操作だけでなく、Javaプログラムのさまざまな場面でコードを簡潔にします。

### スレッドの生成と実行

`Runnable`インターフェイス（`run`メソッドを持つ関数型インターフェイス）もラムダ式で簡単に実装できます。

<span class="listing-number">**サンプルコード13-17**</span>

```java
public class ThreadLambdaExample {
    public static void main(String[] args) {
        // 匿名クラスでのRunnable
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println("Thread t1 (Anonymous) is running...");
            }
        });
        t1.start();

        // ラムダ式でのRunnable
        Thread t2 = new Thread(() -> System.out.println("Thread t2 (Lambda) is running..."));
        t2.start();
    }
}
```

## さらに深い理解のために


## まとめ

本章では、モダンJavaプログラミングの基礎となるラムダ式と関数型インターフェイスについて学びました。

-   **ラムダ式**は、匿名関数を簡潔に記述するための構文で、冗長な匿名クラスを置き換えます。
-   ラムダ式は、**抽象メソッドが1つだけの関数型インターフェイス**として扱われます。
-   `Predicate`, `Function`, `Consumer`, `Supplier`など、汎用的な関数型インターフェイスが標準で用意されています。
-   **メソッド参照**を使うと、既存のメソッドを呼びだすだけのラムダ式をさらに簡潔に書けます。

関数型プログラミングの理論的背景を理解することで、より洗練されたコードを書くことができます。しかし、実務では可読性とパフォーマンスのバランスを考慮し、適切に使い分けることが大切です。

これらの機能を使いこなすことで、コードの可読性が向上し、より宣言的で簡潔なプログラミングが可能になります。

## 章末演習

本章で学んだラムダ式と関数型インターフェイスを実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`

### 第13章の課題構成

```
exercises/chapter13/
├── basic/              # 基礎課題（ポイント）
│   ├── README.md       # 詳細な課題説明
│   └── BasicLambda.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- ラムダ式と関数型インターフェイスの実践的な活用
- メソッド参照による簡潔なコード記述
- 高階関数とカリー化の理解と実装

### 課題の概要

1. **基礎課題**: 標準関数型インターフェイスの活用とラムダ式の基本構文
2. **発展課題**: カスタム関数型インターフェイスの設計とメソッド参照
3. **チャレンジ課題**: 高階関数とカリー化による関数型プログラミング

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第14章「例外処理」に進みましょう。

**データバリデーション処理の関数型実装**

複雑なバリデーションロジックは、関数型アプローチを使うことで、再利用可能で組み合わせ可能な小さな検証関数として実装できます。これにより、ビジネスルールの変更に柔軟に対応できるバリデーションシステムを構築できます。

<span class="listing-number">**サンプルコード13-28**</span>

```java
public class ValidationFramework {
    // 基本的なバリデーション関数
    public static Predicate<String> notEmpty = s -> s != null && !s.trim().isEmpty();
    public static Predicate<String> validEmail = s -> s.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    public static Function<Integer, Predicate<String>> minLength = min -> s -> s.length() >= min;
    public static Function<Integer, Predicate<String>> maxLength = max -> s -> s.length() <= max;
    
    // 複合バリデーション
    public static Predicate<User> validUser = user ->
        notEmpty.test(user.getName()) &&
        validEmail.test(user.getEmail()) &&
        minLength.apply(8).test(user.getPassword());
    
    // エラーメッセージ付きバリデーション
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        
        // コンストラクタとメソッド（省略）
    }
    
    public static Function<User, ValidationResult> validateUserWithErrors = user -> {
        List<String> errors = new ArrayList<>();
        
        if (!notEmpty.test(user.getName())) {
            errors.add("名前はポイントです");
        }
        if (!validEmail.test(user.getEmail())) {
            errors.add("有効なメールアドレスを入力してください");
        }
        if (!minLength.apply(8).test(user.getPassword())) {
            errors.add("パスワードは8文字以上である必要があります");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    };
}
```

## より深い理解のために

本章で学んだ関数型プログラミングについて、さらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

**付録リソース**: `/manuscripts/appendix-b03-programming-paradigms.md`

この付録では以下の高度なトピックを扱います：

- **関数型プログラミングの歴史的背景**: LispからHaskell、そしてモダン言語への発展
- **数学的基礎**: ラムダ計算、チャーチ数、Yコンビネータ
- **圏論とモナド**: ファンクタ、アプリカティブ、モナドのコンセプトとJavaにおける実装
- **実務での活用例**: ドメイン固有言語（DSL）、状態管理、エラーハンドリング