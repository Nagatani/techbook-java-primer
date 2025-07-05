# 第11章 ラムダ式と関数型インターフェイス - 解答例

この章では、Java 8で導入されたラムダ式と関数型インターフェイスについて学習します。

## 学習内容

### 1. 関数型インターフェイスの基本
- `Function<T, R>` - 入力を受け取り、出力を返す
- `Consumer<T>` - 入力を受け取り、何も返さない
- `Predicate<T>` - 入力を受け取り、真偽値を返す
- `Supplier<T>` - 入力なしで出力を返す

### 2. ラムダ式の構文
- `(parameter) -> expression`
- `(parameter) -> { statements; }`
- メソッド参照の使用

### 3. 関数合成とパイプライン処理
- `compose()` - 右から左への関数合成
- `andThen()` - 左から右への関数合成
- 複数の関数を組み合わせた処理

## 実装例

### FunctionProcessor.java
関数型インターフェイスを活用した処理クラス：
- 基本的な関数の使用方法
- 関数合成（compose, andThen）
- カスタム関数の作成
- 部分適用のシミュレーション

### EventProcessor.java
イベント処理システムの実装：
- Consumer<T>を使用したイベントリスナーパターン
- 条件付きイベント処理
- バッチ処理とレート制限
- 非同期イベント処理

### ValidationSystem.java
関数型インターフェイスを活用したバリデーションシステム：
- Predicate<T>を使用した検証ロジック
- 複数の検証条件の組み合わせ
- カスタムバリデーションルール
- 複合バリデーション

### PipelineProcessor.java
パイプライン処理の実装：
- 段階的なデータ変換
- 条件付きパイプライン
- 並行処理パイプライン
- エラーハンドリングを含む処理

## 重要ポイント

### 1. 関数合成
```java
Function<Integer, Integer> doubleValue = x -> x * 2;
Function<Integer, Integer> addTen = x -> x + 10;

// compose: 右から左（addTen → doubleValue）
Function<Integer, Integer> composed = doubleValue.compose(addTen);
// 5 → 15 → 30

// andThen: 左から右（doubleValue → addTen）
Function<Integer, Integer> chained = doubleValue.andThen(addTen);
// 5 → 10 → 20
```

### 2. イベントリスナーパターン
```java
// 特定のイベントタイプのみ処理
Consumer<Event> listener = processor.createTypeSpecificListener("ERROR", 
    event -> System.err.println("Error: " + event.getData()));

// 条件付き処理
Consumer<Event> conditionalListener = processor.createConditionalListener(
    event -> event.getData().contains("important"),
    event -> handleImportantEvent(event));
```

### 3. バリデーションの組み合わせ
```java
// 複数条件のAND結合
Predicate<String> validation = StringValidators.notEmpty()
    .and(StringValidators.minLength(5))
    .and(StringValidators.maxLength(10));

// 複数条件のOR結合
Predicate<String> anyCondition = condition1.or(condition2).or(condition3);
```

### 4. パイプライン処理
```java
ProcessingResult<String> result = Pipeline.<String>start()
    .then("trim", String::trim)
    .then("toUpperCase", String::toUpperCase)
    .then("addPrefix", s -> "PROCESSED_" + s)
    .process("  hello world  ");
```

## 実行方法

各テストクラスを実行して、実装の動作を確認してください：

```bash
# 関数処理のテスト
java -cp . chapter11.solutions.FunctionProcessorTest

# イベント処理のテスト  
java -cp . chapter11.solutions.EventProcessorTest

# バリデーションシステムのテスト
java -cp . chapter11.solutions.ValidationSystemTest

# パイプライン処理のテスト
java -cp . chapter11.solutions.PipelineProcessorTest
```

## 実用的な応用例

### 1. データ変換パイプライン
```java
// CSVデータの処理
ProcessingResult<List<String>> result = 
    DataTransformationPipeline.processCsvData("name,age,city");
```

### 2. ユーザー登録のバリデーション
```java
ValidationResult result = UserValidator.validateUser(
    "John Doe", "john@example.com", 25, "Password123!");
```

### 3. 設定可能なイベント処理
```java
// バッチ処理
BatchEventProcessor batchProcessor = 
    new BatchEventProcessor(10, batch -> processBatch(batch));

// レート制限
RateLimitedListener rateLimited = 
    new RateLimitedListener(handler, 1000); // 1秒間隔
```

これらの実装例は、関数型プログラミングの概念をJavaで実践的に活用する方法を示しています。ラムダ式と関数型インターフェイスを使用することで、より読みやすく、再利用可能で、テストしやすいコードを作成することができます。