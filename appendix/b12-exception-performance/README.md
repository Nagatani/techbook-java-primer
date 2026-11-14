# 付録B.12 例外処理のパフォーマンス詳解

例外処理のパフォーマンス特性、最適化技術、代替エラーハンドリングパターンを実装したJavaプロジェクトです。

## 概要

このプロジェクトでは、次の内容を実装とベンチマークを通じて確かめます。

- Javaにおける例外処理のコスト
- スタックトレース生成のオーバーヘッド
- 例外プーリングと、スタックトレースを作らない例外
- 代替エラーハンドリングパターン（Result/Either、Null Object）
- パフォーマンスモニタリングとメトリクス収集

## 例外のコストについて分かること

ベンチマークを実行すると、次のような傾向が分かります。
具体的な数値はCPU、OS、JDKの版、JITの状態で大きく変わるため、自分の環境で`benchmarks.jar`を実行して確かめてください。

- スタックトレースを作る例外は、通常の戻り値による制御より桁違いに遅い
- コストの大部分はスタックトレースの生成（`fillInStackTrace()`）が占める。スタックトレースを作らない例外は、作る例外よりずっと速い
- スタックトレースを作るコストは、呼び出しの深さにほぼ比例して増える
- 例外が発生しなければ、try-catchブロック自体のオーバーヘッドはほとんどない
- 同じ例外オブジェクトを投げてすぐ同じメソッドで受け取る処理は、JITが単なる分岐に置き換えることがある。このため`cachedExceptionFlow`は、通常の処理とほとんど同じ時間になることがある

### 例外パフォーマンスが重要になる場合

1. 高頻度の操作
2. タイトループとパフォーマンスクリティカルなパス
3. APIでの入力検証
4. 制御フローの決定（アンチパターン）

### 例外パフォーマンスが問題にならない場合

1. 真に例外的な状況（まれなエラー）
2. アプリケーションの起動・終了
3. 設定エラー
4. ネットワーク・I/Oの障害（もともと時間がかかる）

## プロジェクト構成

```
src/main/java/com/example/exception/
├── performance/          # JMHベンチマーク
│   ├── BasicExceptionBenchmark.java
│   ├── StackTraceDepthBenchmark.java
│   └── ExceptionTypeBenchmark.java
├── optimization/         # 最適化技術
│   ├── ExceptionPool.java
│   ├── PoolableException.java
│   ├── ValidationException.java
│   └── ConditionalStackTraceException.java
├── patterns/            # 代替パターン
│   ├── Result.java
│   └── NullObject.java
├── monitoring/          # メトリクスとモニタリング
│   ├── ExceptionMetrics.java
│   └── ExceptionHandler.java
└── Demo.java           # デモプログラム
```

## プロジェクトの実行

### 前提条件

- Java 25以上
- Maven 3.9以上

### ビルドと実行

```bash
# プロジェクトのビルド（テストも実行される）
mvn clean package

# デモの実行
mvn exec:java -Dexec.mainClass="com.example.exception.Demo"

# ベンチマークの一覧
java -jar target/benchmarks.jar -l

# 特定のベンチマークを実行
java -jar target/benchmarks.jar BasicExceptionBenchmark

# すべてのベンチマークを実行
java -jar target/benchmarks.jar

# 特定のパラメーターで実行
java -jar target/benchmarks.jar StackTraceDepthBenchmark -p stackDepth=10,50,100
```

JDK 23以降のjavacは、指定しないと注釈処理を行いません。
JMHはベンチマークの一覧（`META-INF/BenchmarkList`）を注釈処理で作るため、`pom.xml`では`maven-compiler-plugin`の`annotationProcessorPaths`に`jmh-generator-annprocess`を指定しています。
これがないと、`benchmarks.jar`を実行してもベンチマークが見つかりません。

### テストの実行

```bash
mvn test
```

## ベンチマーク

### BasicExceptionBenchmark

| ベンチマーク | 測る内容 |
|---|---|
| `normalFlow` | 例外を使わない通常の処理（基準） |
| `tryCatchOverheadNoException` | 例外が発生しないtry-catch |
| `exceptionFlow` | スタックトレースを作る例外 |
| `exceptionFlowNoStackTrace` | `fillInStackTrace()`をオーバーライドしてスタックトレースを作らない例外 |
| `cachedExceptionFlow` | あらかじめ作っておいた例外オブジェクトを投げる |

### StackTraceDepthBenchmark

`stackDepth`（既定は1、10、50、100、200）の深さまで再帰呼び出しをしてから例外を投げます。

| ベンチマーク | 測る内容 |
|---|---|
| `exceptionAtDepth` | スタックトレースを作る例外 |
| `exceptionAtDepthNoStackTrace` | スタックトレースを作らない例外 |
| `normalReturnAtDepth` | 例外を使わない戻り値（基準） |

### ExceptionTypeBenchmark

検査例外、実行時例外、`Error`、軽量な例外、抑制された例外（suppressed）や原因（cause）を持つ例外を比べます。

## 最適化技術

### 1. 例外プーリング

スタックトレースが不要な高頻度の例外で、例外オブジェクトを使い回す方法です。

```java
ExceptionPool<ValidationException> pool = new ExceptionPool<>(
    ValidationException::create, 100
);

// プールされた例外を使用
ValidationException e = pool.acquire("検証に失敗しました");
try {
    throw e;
} catch (ValidationException ve) {
    // 処理
} finally {
    pool.release(e);
}
```

`Demo`の第2節では、スタックトレースを作る例外を毎回生成する場合、スタックトレースを作らない例外を毎回生成する場合、プールの例外を使い回す場合を比べます。
速くなる理由の大部分は、使い回しではなくスタックトレースを作らないことにあります。

例外オブジェクトの使い回しは、スレッド間でメッセージや状態が混ざる危険があり、通常は勧めません。
スタックトレースを作らない例外（`RuntimeException`のコンストラクターで`writableStackTrace=false`を指定したもの）で足りることが多いです。

### 2. 条件付きスタックトレース

必要なときだけスタックトレースを生成します。
`ConditionalStackTraceException`は、条件を評価した結果を`writableStackTrace`としてスーパークラスのコンストラクターに渡します。

```java
public ConditionalStackTraceException(String message, BooleanSupplier stackTraceCondition) {
    super(message, null, true, stackTraceCondition.getAsBoolean());
}
```

`fillInStackTrace()`をオーバーライドして、フィールドに入れた条件を見る書き方は動きません。
`Throwable`のコンストラクターは、サブクラスのフィールドに値が入る前に`fillInStackTrace()`を呼ぶため、条件がまだ`null`で`NullPointerException`になります。

用意しているサブクラスは次のとおりです。

```java
// 環境ベース（-Dapp.environment=development のときだけ作る）
throw new ConditionalStackTraceException.EnvironmentAwareException("Config error");

// レート制限（1分に1回）
throw new ConditionalStackTraceException.RateLimitedException("Too many requests");

// サンプリング（100回に1回）
throw new ConditionalStackTraceException.SamplingException("Common error");

// デバッグモードのときだけ作る
throw new ConditionalStackTraceException.DebugModeException("Debug error");
```

### 3. Resultパターン

予期されるエラーでは、例外の代わりに結果の値を返します。

```java
Result<Integer, String> result = parseNumber(input)
    .flatMap(n -> divide(100, n))
    .map(n -> n * 2)
    .filter(n -> n > 0, "Must be positive");

result.ifSuccess(IO::println)
      .ifFailure(error -> System.err.println("Error: " + error));
```

例外を投げないため、スタックトレースを作るコストがかかりません。

### 4. Null Objectパターン

`null`の代わりに「何もしないオブジェクト」を返し、`null`チェックと`NullPointerException`をなくします。

```java
User user = userService.findById(userId); // nullを返さない
if (user.isValid()) {
    // 有効なユーザーを処理
} else {
    // 無効または存在しないユーザーを処理
}
```

## 実世界のシナリオ

### 入力検証

例外を使う例です。最初に見つかったエラーしか報告できません。

```java
public void validateInput(String input) throws ValidationException {
    if (input == null) throw new ValidationException("Null input");
    if (input.isEmpty()) throw new ValidationException("Empty input");
    // その他の検証...
}
```

結果の値を返す例です。複数のエラーをまとめて返せます。

```java
public Result<ValidInput, List<String>> validateInput(String input) {
    List<String> errors = new ArrayList<>();
    if (input == null) {
        errors.add("Null input");
    } else if (input.isEmpty()) {
        errors.add("Empty input");
    }

    return errors.isEmpty()
        ? Result.success(new ValidInput(input))
        : Result.failure(errors);
}
```

### リソース読み込み

リソースがないことがふつうに起こるなら、例外より`Optional`が向いています。

```java
public Optional<Resource> loadResource(String path) {
    // リソースがない場合にOptional.empty()を返す
}
```

### ビジネスロジック

在庫切れや残高不足のように予期される結果は、例外ではなく結果の種類として表せます。

```java
OrderResult result = processOrder(order);
switch (result.getStatus()) {
    case SUCCESS -> shipOrder();
    case INSUFFICIENT_FUNDS -> notifyCustomer();
    case OUT_OF_STOCK -> backorder();
}
```

## モニタリングとメトリクス

例外の発生回数や処理時間を集計できます。

```java
ExceptionMetrics metrics = new ExceptionMetrics(true);
ExceptionHandler handler = new ExceptionHandler(metrics);

// 操作をモニタリング
handler.monitor(() -> riskyOperation());

// 詳細レポートを取得
IO.println(metrics.generateReport());
```

出力の例です（回数・時刻・時間は実行ごとに変わります）。

```
=== Exception Metrics Report ===
Uptime: PT0.013795S

IllegalArgumentException:
  Occurrences: 247
  First: 2026-11-14T06:16:50.230036Z
  Last: 2026-11-14T06:16:50.232378Z
  Processing time (ns):
    Average: 909.92
    Min: 541
    Max: 7041
  Stack trace size:
    Average: 4.00
    Max: 4
```

## ベストプラクティス

### 例外パフォーマンスを最適化すべき場合

1. まずプロファイリングして、実際の影響を測定する
2. 高頻度のパス（ホットスポット）に焦点を当てる
3. ビジネスロジックでは、例外ではなくドメインモデルで結果を表す
4. 検証では、例外を投げずにエラーを返す
5. 予期される条件には`Optional`や`Result`を使う

### 最適化すべきでない場合

1. まれで予期しないエラー
2. フレームワークの境界（フレームワークの規約に従う）
3. デバッグが必要な場面（スタックトレースは貴重）
4. 測定していない段階（早すぎる最適化より明瞭さを優先する）

## JVMの最適化

JVMは例外にも最適化を行います。

- 例外を投げる場所と受け取る場所が、インライン化によって同じコンパイル単位に入ると、JITは例外の送出を単なる分岐に置き換えることがある
- エスケープ解析によって、外へ出ない例外オブジェクトの割り当てが消えることがある
- `NullPointerException`や`ArithmeticException`のようにJVMが暗黙に投げる例外は、同じ場所で何度も発生すると、スタックトレースのない使い回しの例外に置き換えられることがある（`-XX:+OmitStackTraceInFastThrow`、既定で有効）

ただし、スタックトレースを作るコスト自体はなくせません。
ベンチマークの結果を読むときは、JITの最適化で測りたい処理が消えていないかに注意してください。
`BasicExceptionBenchmark`では、定数の引数をJITが畳み込まないように、引数を`@State`のフィールドから読んでいます。

## まとめ

覚えておくべきことは次のとおりです。

- まず測定する（パフォーマンスの問題を仮定しない）
- 例外は例外的なケースに使う
- 最適化は、測定で確かめた改善に絞る
- 可読性を犠牲にしない

ほとんどのアプリケーションでは、マイクロ最適化よりも分かりやすい例外処理のほうが重要です。
これらの技術は、パフォーマンスが本当に重要な場面で判断するための知識として役立ちます。
