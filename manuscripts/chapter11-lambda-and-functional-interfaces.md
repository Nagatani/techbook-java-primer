# 第11章 ラムダ式と関数型インターフェイス

## 章末演習

本章で学んだラムダ式と関数型インターフェイスの概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
ラムダ式と関数型インターフェイスを使った実践的なプログラミング技術を習得します。

## 基礎レベル課題（必須）

### 課題1: 基本的なラムダ式の活用

基本的なラムダ式を活用した処理を実装し、関数型インターフェイスを理解してください。

**技術的背景：関数型プログラミングの台頭**

Java 8でラムダ式が導入された背景には、以下の課題がありました：

**従来の匿名クラスの問題：**
```java
// 冗長な匿名クラス
Collections.sort(list, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return s1.length() - s2.length();
    }
});

// ラムダ式による簡潔な記述
Collections.sort(list, (s1, s2) -> s1.length() - s2.length());
```

**標準関数型インターフェイスの実用例：**
- **Predicate<T>**：フィルタリング処理（ユーザー権限チェック、データ検証）
- **Function<T,R>**：データ変換（DTOからエンティティへの変換、文字列パース）
- **Consumer<T>**：副作用のある処理（ログ出力、データベース更新）
- **Supplier<T>**：遅延評価（重い処理の遅延実行、デフォルト値の提供）
- **Comparator<T>**：柔軟なソート（多重キーソート、動的ソート条件）

**関数型プログラミングの利点：**
- **簡潔性**：ボイラープレートコードの削減
- **可読性**：処理の意図が明確
- **並列処理**：Stream APIとの組み合わせで容易な並列化

この演習では、現代のJava開発で必須となる関数型プログラミングの基礎を習得します。

**要求仕様：**
- Predicate<T> を使った条件判定
- Function<T, R> を使ったデータ変換
- Consumer<T> を使った処理実行
- Supplier<T> を使った値生成
- Comparator<T> を使ったソート

**実行例：**
```
=== 基本的なラムダ式の活用 ===
数値リスト: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

Predicate（偶数判定）:
偶数: [2, 4, 6, 8, 10]

Function（２乗変換）:
元の値: [1, 2, 3, 4, 5]
２乗後: [1, 4, 9, 16, 25]

Consumer（出力処理）:
値の出力: 1, 2, 3, 4, 5

Supplier（ランダム値生成）:
ランダム値: 42, 17, 89, 33, 56

Comparator（ソート）:
文字列長さソート: ["a", "bb", "ccc", "dddd"]
逆順ソート: [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
```

### 課題2: カスタム関数型インターフェイス設計

独自の関数型インターフェイスを設計し、ラムダ式で実装してください。

**技術的背景：ドメイン特化の関数型インターフェイス**

標準の関数型インターフェイスで不十分な場合、独自のインターフェイスが必要になります：

**カスタムインターフェイスが必要な場面：**
- **複数引数の処理**：3つ以上の引数を取る関数（BiFunctionは2つまで）
- **チェック例外の扱い**：標準インターフェイスは非チェック例外のみ
- **ドメイン特化の名前**：ビジネスロジックを明確に表現
- **追加のデフォルトメソッド**：合成や変換の便利メソッド

**実際の使用例：**
```java
@FunctionalInterface
public interface ThrowingFunction<T, R> {
    R apply(T t) throws Exception;
    
    // 例外をRuntimeExceptionでラップするデフォルトメソッド
    default Function<T, R> unchecked() {
        return t -> {
            try {
                return apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
```

**設計の注意点：**
- **単一抽象メソッド**：@FunctionalInterfaceで保証
- **型パラメータの適切な使用**：汎用性と型安全性のバランス
- **意味のある名前**：applyよりcalculate、processなど

この演習では、実務で必要となるカスタム関数型インターフェイスの設計技術を学びます。

**要求仕様：**
- @FunctionalInterfaceアノテーション使用
- 数学演算用の関数型インターフェイス
- 文字列処理用の関数型インターフェイス
- デフォルトメソッドの活用
- 複数の実装パターン

**実行例：**
```
=== カスタム関数型インターフェイス設計 ===
数学演算テスト:
加算: 10 + 5 = 15
減算: 10 - 5 = 5
乗算: 10 * 5 = 50
除算: 10 / 5 = 2.0

複合演算:
(10 + 5) * 2 = 30
(10 - 5) / 2 = 2.5

文字列処理テスト:
大文字変換: "hello" → "HELLO"
小文字変換: "WORLD" → "world"
逆順変換: "Java" → "avaJ"

複合処理:
"hello world" → 大文字化 → 逆順 → "DLROW OLLEH"

条件付き処理:
長さ5以上の文字列のみ大文字化:
"hi" → "hi" (変換なし)
"hello" → "HELLO" (変換実行)
```

### 課題3: メソッド参照とコンストラクタ参照

メソッド参照とコンストラクタ参照を活用した処理を実装してください。

**技術的背景：メソッド参照によるさらなる簡潔化**

メソッド参照は、既存のメソッドをラムダ式として再利用する強力な機能です：

**4種類のメソッド参照：**
1. **静的メソッド参照**：`Integer::parseInt`
2. **特定オブジェクトのインスタンスメソッド参照**：`System.out::println`
3. **任意オブジェクトのインスタンスメソッド参照**：`String::length`
4. **コンストラクタ参照**：`ArrayList::new`

**実用例での比較：**
```java
// ラムダ式
list.stream().map(s -> Integer.parseInt(s))
// メソッド参照（より簡潔）
list.stream().map(Integer::parseInt)

// ラムダ式でのオブジェクト生成
Supplier<List<String>> supplier = () -> new ArrayList<>();
// コンストラクタ参照
Supplier<List<String>> supplier = ArrayList::new;
```

**メソッド参照の利点：**
- **可読性**：意図が明確で理解しやすい
- **再利用性**：既存メソッドの活用
- **パフォーマンス**：わずかながら効率的

**実際の活用場面：**
- **Stream API**：`map(String::toUpperCase)`
- **Optional**：`orElseThrow(IllegalArgumentException::new)`
- **Comparator**：`comparing(Person::getName)`

この演習では、コードをより簡潔で表現力豊かにする技術を学びます。

**要求仕様：**
- 静的メソッド参照（Class::staticMethod）
- インスタンスメソッド参照（instance::method）
- 任意オブジェクトのインスタンスメソッド参照（Class::instanceMethod）
- コンストラクタ参照（Class::new）
- メソッド参照からラムダ式への変換比較

**実行例：**
```
=== メソッド参照とコンストラクタ参照 ===
静的メソッド参照テスト:
数値リスト: [1, 4, 9, 16, 25]
平方根: [1.0, 2.0, 3.0, 4.0, 5.0]

インスタンスメソッド参照テスト:
文字列リスト: ["hello", "world", "java"]
大文字変換: ["HELLO", "WORLD", "JAVA"]

任意オブジェクトのメソッド参照:
文字列長さ: [5, 5, 4]

コンストラクタ参照テスト:
Person生成:
田中太郎（25歳）
佐藤花子（30歳）
鈴木一郎（35歳）

配列コンストラクタ参照:
整数配列: [0, 0, 0, 0, 0]
文字列配列: [null, null, null]

ラムダ式 vs メソッド参照比較:
ラムダ式: x -> Math.sqrt(x)
メソッド参照: Math::sqrt
結果は同じ: [1.0, 2.0, 3.0]
```

### 課題4: 高階関数とカリー化

高階関数とカリー化を実装し、関数型プログラミングの高度な概念を理解してください。

**技術的背景：関数型プログラミングの高度な技法**

高階関数とカリー化は、関数型プログラミングの強力な技法です：

**高階関数の実用例：**
- **戦略パターンの実装**：アルゴリズムを関数として渡す
- **デコレータパターン**：関数を拡張する関数
- **ファクトリパターン**：関数を生成する関数

```java
// 関数を返す高階関数の例
Function<Integer, Predicate<String>> lengthChecker = 
    maxLength -> str -> str.length() <= maxLength;

Predicate<String> isShort = lengthChecker.apply(5);
// isShort は文字列長が5以下かチェックする関数
```

**カリー化の利点：**
- **部分適用**：一部の引数を固定した特殊化関数の作成
- **関数の再利用**：汎用関数から特定用途の関数を派生
- **設定の分離**：設定とデータ処理の分離

**実際の応用例：**
- **バリデーション**：`validate(rules)(data)` - ルールとデータを分離
- **レート制限**：`rateLimit(maxCalls)(timeWindow)(function)` - 段階的な設定
- **ロギング**：`withLogging(logger)(level)(function)` - 設定可能なログ

**関数合成の重要性：**
- **パイプライン処理**：小さな関数を組み合わせて複雑な処理を構築
- **モジュラー設計**：再利用可能な小さな関数の組み合わせ

この演習では、関数型プログラミングの真髄である「関数を第一級オブジェクトとして扱う」技術を習得します。

**要求仕様：**
- 関数を引数として受け取る高階関数
- 関数を戻り値として返す高階関数
- カリー化（部分適用）の実装
- 関数の合成（compose、andThen）
- 遅延評価の実装

**実行例：**
```
=== 高階関数とカリー化 ===
高階関数テスト（関数を引数に取る）:
リスト: [1, 2, 3, 4, 5]
処理1（2倍）: [2, 4, 6, 8, 10]
処理2（2乗）: [1, 4, 9, 16, 25]

高階関数テスト（関数を返す）:
加算器生成:
add5 = createAdder(5)
add5(10) = 15
add5(20) = 25

カリー化テスト:
三項演算: f(x, y, z) = x + y * z
カリー化: f(2)(3)(4) = 2 + 3 * 4 = 14

部分適用:
multiply(2, 3) = 6
multiplyBy2 = multiply(2, _)
multiplyBy2(3) = 6
multiplyBy2(5) = 10

関数合成テスト:
f(x) = x * 2
g(x) = x + 3
compose: g(f(5)) = g(10) = 13
andThen: f(g(5)) = f(8) = 16

遅延評価テスト:
計算定義時: (処理なし)
実行時: 2 * 3 + 5 = 11
```



## 実装のヒント

### ラムダ式の基本原則
1. **簡潔な構文**: (parameters) -> expression
2. **型推論**: コンパイラが型を推論
3. **メソッド参照**: 既存メソッドの簡潔な表現
4. **関数合成**: andThen、composeで連鎖処理

### よくある落とし穴
- ラムダ式内でのfinalまたは実質的にfinalな変数のみ参照可能
- メソッド参照の種類の混同
- カリー化での型の複雑さ
- 関数型インターフェイスの選択

### 設計のベストプラクティス
- 可能な限りメソッド参照を使用
- 適切な関数型インターフェイスを選択
- 副作用の少ない関数を心がける
- 関数合成で柔軟な設計を実現



## 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter11/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── BasicLambda.java
│   ├── CustomFunctionalInterface.java
│   ├── MethodReference.java
│   └── HigherOrderFunction.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```



## 完了確認チェックリスト

### 基礎レベル
- [ ] 基本的なラムダ式と関数型インターフェイスが使えている
- [ ] カスタム関数型インターフェイスが設計できている
- [ ] メソッド参照の4パターンが理解できている
- [ ] 高階関数とカリー化が実装できている

### 技術要素
- [ ] ラムダ式の構文を正しく理解している
- [ ] 関数型プログラミングの基本概念を把握している
- [ ] 匿名クラスからラムダ式への移行ができている
- [ ] 関数合成で複雑な処理を表現できている

### 応用レベル
- [ ] モナドパターンの基本を理解している
- [ ] 並列処理での関数型アプローチを活用できている
- [ ] 宣言的なプログラミングスタイルを実践できている
- [ ] 関数型ライブラリやフレームワークが設計できている

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの重要な前提知識が必要です。まず必須の前提として、第9章までに学んだオブジェクト指向プログラミングとジェネリクスの概念を十分に習得していることが求められます。特に、インターフェイスの設計と実装、匿名クラスの作成と活用についての実践的な経験が不可欠です。これらの知識は、ラムダ式が匿名クラスの簡潔な表現であること、そして関数型インターフェイスとの関係性を理解する上で基礎となります。コレクションフレームワークの実践的な使用経験も重要で、List、Set、Mapの操作を通じてラムダ式の実用性を実感できます。

さらに、概念的な前提として、関数型プログラミングの基本概念への関心を持っていることが推奨されます。従来の命令型プログラミングとは異なるアプローチである関数型プログラミングでは、「どうやって」ではなく「何を」するかを表現する宣言的なスタイルが特徴です。コードの簡潔性と可読性への意識も重要で、ラムダ式や関数型インターフェイスを用いることで、より簡潔で表現力の高いコードを書けるようになるためです。

### 学習目標

本章では、Java 8で導入されたラムダ式と関数型インターフェイスの包括的な知識と技術を習得します。知識理解の面では、まず関数型プログラミングパラダイムの基本概念を深く理解します。関数型プログラミングは、計算を関数の組み合わせとして表現し、状態変更や副作用を避けるアプローチです。ラムダ式の文法と意味論を学び、匿名関数を簡潔に表現する方法を理解します。Javaの標準ライブラリで提供される関数型インターフェイス（Function、Predicate、Consumer、Supplier等）の特性と使い道を習得し、メソッド参照の概念と使用法を学びます。

技能習得の面では、ラムダ式を活用した簡潔で表現力の高いコード実装技術を身につけます。標準関数型インターフェイスを効果的に活用し、メソッド参照によってコードの可読性を向上させる方法を習得します。従来の匿名クラスからラムダ式への移行手法も重要なスキルで、既存コードの改善に活用できます。

プログラミングスタイルの観点からは、宣言的なプログラミングスタイルを習得します。これは、「どのように」実行するかよりも「何を」行うかを明確に表現する方法で、コードの意図をより直感的に伝えることができます。関数の合成を使った柔軟なプログラム設計手法を学び、再利用可能で保守性の高いコードを作成できます。副作用の少ない関数型スタイルを実践することで、バグの発生を減らし、テストしやすいコードを書けます。

最終的な到達レベルとしては、ラムダ式と関数型インターフェイスを状況に応じて適切に使い分けできます。複雑な処理をラムダ式の組み合わせで簡潔かつ理解しやすく表現できる能力を身につけ、現代的なJavaアプリケーション開発において、関数型プログラミングの利点を最大限に活用できることが、本章の最終目標です。
- 関数型スタイルで可読性の高いコードが書ける
- カスタム関数型インターフェイスが設計・実装できる



## 11.1 匿名クラスからラムダ式へ

Java 8でラムダ式が導入される前、その場限りのインターフェイス実装を提供するためには**匿名クラス（Anonymous Class）**が使われていました。これは名前を持たないクラスで、特にGUIのイベントリスナなどで多用されていました。

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

**関数型プログラミングの歴史的背景、数学的基盤、実務での活用例についての詳細は、付録B.3「プログラミングパラダイムの進化」を参照してください。**

### カリー化の実装例：通貨変換

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

```java
public class ResilientService {
    // 関数型リトライメカニズム
    public <T> Supplier<T> withRetry(Supplier<T> supplier, int maxAttempts) {
        return () -> {
            AtomicInteger attempts = new AtomicInteger(0);
            
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

```java
public class EventSourcedAccount {
    // イベントの定義
    sealed interface AccountEvent permits 
        AccountCreated, MoneyDeposited, MoneyWithdrawn {}
    
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

### 実践的なデザインパターン：関数型ビルダ

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

## 11.2 関数型インターフェイス

ラムダ式は、どのような場所でも書けるわけではありません。ラムダ式は、**関数型インターフェイス（Functional Interface）** として扱われます。

**関数型インターフェイス**とは、**実装すべき抽象メソッドが1つだけ**定義されているインターフェイスのことです。`@FunctionalInterface` アノテーションを付けると、コンパイラが抽象メソッドが1つだけかどうかをチェックしてくれるため、付けることが推奨されます。

`ActionListener`や前章の`Comparator`も、実装すべき抽象メソッドが実質的に1つだけですので、関数型インターフェイスです。そのため、ラムダ式で置き換えることができたのです。

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

## 11.3 メソッド参照

ラムダ式が既存のメソッドを呼びだすだけの場合、**メソッド参照（Method Reference）**という、さらに簡潔な記法が使えます。`クラス名::メソッド名`や`インスタンス変数::メソッド名`のように記述します。

| 種類 | 構文 | ラムダ式の例 |
| :--- | :--- | :--- |
| **静的メソッド参照** | `クラス名::静的メソッド名` | `s -> Integer.parseInt(s)` |
| **インスタンスメソッド参照**<br>(特定のインスタンス) | `インスタンス変数::メソッド名` | `s -> System.out.println(s)` |
| **インスタンスメソッド参照**<br>(不特定のインスタンス) | `クラス名::メソッド名` | `s -> s.toUpperCase()` |
| **コンストラクタ参照** | `クラス名::new` | `() -> new ArrayList<>()` |

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

        // ラムダ: s -> System.out.println(s)
        // メソッド参照: System.out::println
        words.forEach(System.out::println);

        // ラムダ: s -> s.toUpperCase()
        // メソッド参照: String::toUpperCase
        words.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);

        // ラムダ: () -> new ArrayList<>()
        // メソッド参照: ArrayList::new
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        System.out.println("新しいリスト: " + newList);
    }
}
```

## 11.4 ラムダ式の応用例

ラムダ式はコレクション操作だけでなく、Javaプログラムのさまざまな場面でコードを簡潔にします。

### スレッドの生成と実行

`Runnable`インターフェイス（`run`メソッドを持つ関数型インターフェイス）もラムダ式で簡単に実装できます。

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

## Deep Dive: 関数型プログラミングの数学的基礎

**関数型プログラミングの詳細な数学的基礎とモナド・ファンクタの理論については、付録B.03「プログラミングパラダイムの進化」を参照してください。**

以下は、本章の理解に必要な基本的な概念の概要です。

### 関数型プログラミングの理論的背景

関数型プログラミングは、数学の圏論（Category Theory）とラムダ計算（Lambda Calculus）にもとづいています。これらの概念を理解することで、より深いレベルでJavaの関数型機能を活用できます。

#### ラムダ計算の基礎

ラムダ計算は、1930年代にアロンゾ・チャーチによって考案された計算モデルです：

```
// ラムダ計算の基本形式
λx.x           // 恒等関数（Identity function）
λx.λy.x        // 定数関数（Constant function）
λf.λx.f(f(x))  // 関数の2回適用

// Javaでの対応
Function<Integer, Integer> identity = x -> x;
Function<Integer, Function<Integer, Integer>> constant = x -> y -> x;
Function<Function<Integer, Integer>, Function<Integer, Integer>> twice = 
    f -> x -> f.apply(f.apply(x));
```

#### モナドとその実践的価値

モナド（Monad）は、計算の文脈を扱うための数学的構造です。JavaのOptionalやStreamは、モナドの性質を持っています：

```java
// Optionalモナドの例
public class MonadExample {
    // map操作（ファンクタ）
    Optional<String> getName(int id) {
        return findUser(id).map(User::getName);
    }
    
    // flatMap操作（モナド）
    Optional<Address> getAddress(int id) {
        return findUser(id)
            .flatMap(User::getAddress)  
            // Optional<Optional<Address>>を平坦化
            .flatMap(Address::validate); // チェーン可能
    }
    
    // モナド則の検証
    // 左単位元則: Optional.of(x).flatMap(f) == f.apply(x)
    // 右単位元則: m.flatMap(Optional::of) == m
    // 結合則: m.flatMap(f).flatMap(g) == 
    //     m.flatMap(x -> f.apply(x).flatMap(g))
}
```

#### ファンクタとその応用

ファンクタ（Functor）は、構造を保ちながら値を変換する概念です：

```java
// カスタムファンクタの実装
public interface Functor<T> {
    <R> Functor<R> map(Function<T, R> mapper);
}

// 実装例：Result型（成功/失敗を表現）
public abstract class Result<T> implements Functor<T> {
    
    public static class Success<T> extends Result<T> {
        private final T value;
        
        public Success(T value) { this.value = value; }
        
        @Override
        public <R> Result<R> map(Function<T, R> mapper) {
            return new Success<>(mapper.apply(value));
        }
    }
    
    public static class Failure<T> extends Result<T> {
        private final String error;
        
        public Failure(String error) { this.error = error; }
        
        @Override
        public <R> Result<R> map(Function<T, R> mapper) {
            return new Failure<>(error);  // エラーを伝播
        }
    }
}
```

### 高階関数とカリー化

高階関数は、関数を引数として受け取るか、関数を返す関数です：

```java
// カリー化（Currying）の実装
public class Currying {
    // 通常の2引数関数
    BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
    
    // カリー化された関数
    Function<Integer, Function<Integer, Integer>> curriedAdd = 
        a -> b -> a + b;
    
    // 部分適用
    Function<Integer, Integer> add5 = curriedAdd.apply(5);
    // add5.apply(3) == 8
    
    // 汎用的なカリー化関数
    public static <A, B, C> Function<A, Function<B, C>> curry(
            BiFunction<A, B, C> biFunction) {
        return a -> b -> biFunction.apply(a, b);
    }
}
```

### 実務での関数型パターン

**1. レンズパターン（不変オブジェクトの更新）**

```java
public class Lens<T, F> {
    private final Function<T, F> getter;
    private final BiFunction<T, F, T> setter;
    
    public Lens(Function<T, F> getter, BiFunction<T, F, T> setter) {
        this.getter = getter;
        this.setter = setter;
    }
    
    public F get(T target) { return getter.apply(target); }
    public T set(T target, F value) { return setter.apply(target, value); }
    
    // 使用例
    Lens<Person, String> nameLens = new Lens<>(
        Person::getName,
        (person, name) -> new Person(name, person.getAge())
    );
}
```

**2. Free Monadパターン（DSLの構築）**

```java
// ドメイン固有言語（DSL）の例
public abstract class Free<F, A> {
    // 省略：実装の詳細は高度すぎるため
    
    // 使用例：データベース操作DSL
    Free<DbOp, User> program = 
        select("users", "id = 1")
        .flatMap(data -> insert("audit", data))
        .map(this::toUser);
}
```

### パフォーマンスへの影響

関数型プログラミングのオーバーヘッド：

```java
// ベンチマーク結果（JMH使用）
// 命令型ループ: 100ns
// Stream API: 150ns (50%のオーバーヘッド)
// 並列Stream: 30ns (3.3倍高速、8コアCPU)

// 最適化のポイント
Stream.of(1, 2, 3, 4, 5)
    .filter(x -> x > 2)      // 中間操作（遅延評価）
    .map(x -> x * 2)         // 中間操作（遅延評価）
    .collect(toList());      // 終端操作（ここで実行）
```

## まとめ

本章では、モダンJavaプログラミングの基礎となるラムダ式と関数型インターフェイスについて学びました。

-   **ラムダ式**は、匿名関数を簡潔に記述するための構文で、冗長な匿名クラスを置き換えます。
-   ラムダ式は、**抽象メソッドが1つだけの関数型インターフェイス**として扱われます。
-   `Predicate`, `Function`, `Consumer`, `Supplier`など、汎用的な関数型インターフェイスが標準で用意されています。
-   **メソッド参照**を使うと、既存のメソッドを呼びだすだけのラムダ式をさらに簡潔に書けます。

関数型プログラミングの理論的背景を理解することで、より洗練されたコードを書くことができます。しかし、実務では可読性とパフォーマンスのバランスを考慮し、適切に使い分けることが重要です。

これらの機能を使いこなすことで、コードの可読性が向上し、より宣言的で簡潔なプログラミングが可能になります。