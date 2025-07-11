# 第10章 発展課題：Stream APIの高度な活用

## 課題概要

Stream APIの高度な機能を活用し、複雑なデータ処理を効率的に実装します。並列処理、カスタムCollector、パフォーマンス最適化などの実践的なスキルを習得します。

## 課題1：リアルタイムデータ処理システム

### 目的
- 無限ストリームの処理
- 並列ストリームの高度な活用
- バックプレッシャーの実装

### 要求仕様

1. **センサーデータの処理**
   ```java
   public class SensorData {
       private String sensorId;
       private double value;
       private Instant timestamp;
       private SensorType type;
   }
   
   public class StreamProcessor {
       // 無限ストリームの生成
       public Stream<SensorData> generateSensorStream() {
           return Stream.generate(() -> {
               // ランダムなセンサーデータを生成
               return new SensorData(...);
           });
       }
       
       // ウィンドウ処理（5秒間のデータを集約）
       public Stream<WindowedData> processWindowed(
               Stream<SensorData> stream, 
               Duration windowSize) {
           // タイムウィンドウごとにデータを集約
       }
       
       // 異常検知
       public Stream<Alert> detectAnomalies(Stream<SensorData> stream) {
           // 移動平均からの乖離を検出
       }
   }
   ```

2. **複雑な集約処理**
   ```java
   public class AggregationCollector {
       // カスタムCollectorで統計情報を計算
       public static Collector<SensorData, ?, Statistics> 
               toStatistics() {
           return Collector.of(
               Statistics::new,
               (stats, data) -> stats.accept(data),
               (stats1, stats2) -> stats1.combine(stats2),
               stats -> stats.finish()
           );
       }
   }
   
   public class Statistics {
       private double sum = 0;
       private double sumOfSquares = 0;
       private long count = 0;
       private double min = Double.MAX_VALUE;
       private double max = Double.MIN_VALUE;
       
       // 平均、分散、標準偏差を計算
       public void accept(SensorData data) { }
       public Statistics combine(Statistics other) { }
       public Statistics finish() { }
   }
   ```

3. **並列処理の最適化**
   ```java
   public class ParallelOptimizer {
       // ForkJoinPoolのカスタマイズ
       private final ForkJoinPool customThreadPool = 
           new ForkJoinPool(Runtime.getRuntime().availableProcessors());
       
       public CompletableFuture<List<ProcessedData>> 
               processInCustomPool(List<RawData> data) {
           return CompletableFuture.supplyAsync(() ->
               data.parallelStream()
                   .filter(this::isValid)
                   .map(this::transform)
                   .collect(Collectors.toList()),
               customThreadPool
           );
       }
       
       // データサイズに応じた並列化の判断
       public Stream<ProcessedData> adaptiveProcess(
               List<RawData> data) {
           if (data.size() > PARALLEL_THRESHOLD) {
               return data.parallelStream().map(this::process);
           } else {
               return data.stream().map(this::process);
           }
       }
   }
   ```

### 実装のヒント
```java
// スライディングウィンドウの実装
public Stream<List<T>> sliding(Stream<T> stream, int size) {
    return StreamSupport.stream(
        new AbstractSpliterator<List<T>>(
            Long.MAX_VALUE, 
            Spliterator.ORDERED) {
            
            private final Deque<T> buffer = new ArrayDeque<>(size);
            private final Spliterator<T> source = stream.spliterator();
            
            @Override
            public boolean tryAdvance(Consumer<? super List<T>> action) {
                // 実装
            }
        }, false);
}
```

### 評価ポイント
- 無限ストリームの適切な処理
- メモリ効率的な実装
- 並列処理の最適化
- カスタムSpliteratorの実装

## 課題2：高度なデータ変換フレームワーク

### 目的
- 複雑な型変換の実装
- エラーハンドリングの統合
- 関数合成とパイプライン

### 要求仕様

1. **変換パイプライン**
   ```java
   public class TransformationPipeline<T, R> {
       private final List<Function<?, ?>> transformations;
       
       public static <T> TransformationPipeline<T, T> create() {
           return new TransformationPipeline<>();
       }
       
       public <U> TransformationPipeline<T, U> map(
               Function<? super R, ? extends U> mapper) {
           // 型安全な変換の追加
       }
       
       public <U> TransformationPipeline<T, U> flatMap(
               Function<? super R, Stream<? extends U>> mapper) {
           // 実装
       }
       
       public TransformationPipeline<T, R> filter(
               Predicate<? super R> predicate) {
           // 実装
       }
       
       public TransformationPipeline<T, R> recover(
               Function<Exception, ? extends R> recovery) {
           // エラーハンドリング
       }
       
       public Stream<R> apply(Stream<T> input) {
           // パイプラインの実行
       }
   }
   ```

2. **条件付き変換**
   ```java
   public class ConditionalTransformer {
       public static <T, R> Function<T, Stream<R>> 
               conditionalFlatMap(
                   Predicate<T> condition,
                   Function<T, Stream<R>> ifTrue,
                   Function<T, Stream<R>> ifFalse) {
           return t -> condition.test(t) ? 
               ifTrue.apply(t) : ifFalse.apply(t);
       }
       
       // パターンマッチング風の変換
       public static <T> PatternMatcher<T> match(T value) {
           return new PatternMatcher<>(value);
       }
   }
   
   public class PatternMatcher<T> {
       private final T value;
       private boolean matched = false;
       
       public <R> PatternMatcher<T> when(
               Predicate<T> condition, 
               Consumer<T> action) {
           if (!matched && condition.test(value)) {
               action.accept(value);
               matched = true;
           }
           return this;
       }
       
       public void otherwise(Consumer<T> action) {
           if (!matched) {
               action.accept(value);
           }
       }
   }
   ```

3. **バッチ処理最適化**
   ```java
   public class BatchProcessor {
       // バッチサイズごとに処理
       public <T> Stream<List<T>> batch(
               Stream<T> stream, int batchSize) {
           return StreamUtils.batch(stream, batchSize);
       }
       
       // 並列バッチ処理
       public <T, R> Stream<R> processBatches(
               Stream<T> stream,
               int batchSize,
               Function<List<T>, List<R>> batchProcessor) {
           return batch(stream, batchSize)
               .parallel()
               .flatMap(batch -> 
                   batchProcessor.apply(batch).stream());
       }
   }
   ```

### 評価ポイント
- 型安全な変換パイプライン
- エラーハンドリングの統合
- パフォーマンスの最適化
- 使いやすいAPI設計

## 課題3：ストリーム分析エンジン

### 目的
- 複雑な統計処理
- リアルタイム分析
- カスタムCollectorの高度な実装

### 要求仕様

1. **統計的分析**
   ```java
   public class StatisticalAnalyzer {
       // 移動平均の計算
       public static Collector<Double, ?, List<Double>> 
               movingAverage(int windowSize) {
           return Collector.of(
               () -> new MovingAverageAccumulator(windowSize),
               MovingAverageAccumulator::add,
               MovingAverageAccumulator::combine,
               MovingAverageAccumulator::finish
           );
       }
       
       // パーセンタイル計算
       public static Collector<Double, ?, Map<Integer, Double>> 
               percentiles(int... percentiles) {
           // 実装
       }
       
       // 相関係数の計算
       public static <T> Collector<T, ?, Double> 
               correlation(
                   Function<T, Double> xExtractor,
                   Function<T, Double> yExtractor) {
           // ピアソンの相関係数
       }
   }
   ```

2. **時系列分析**
   ```java
   public class TimeSeriesAnalyzer {
       // 時系列データの補間
       public Stream<TimePoint> interpolate(
               Stream<TimePoint> sparse,
               Duration interval,
               InterpolationMethod method) {
           // 線形補間、スプライン補間など
       }
       
       // トレンド検出
       public TrendAnalysis analyzeTrend(
               Stream<TimePoint> data,
               Duration window) {
           // 上昇、下降、横ばいの判定
       }
       
       // 季節性の検出
       public SeasonalityAnalysis detectSeasonality(
               Stream<TimePoint> data,
               List<Duration> candidatePeriods) {
           // フーリエ変換ベースの分析
       }
   }
   ```

3. **分散集計**
   ```java
   public class DistributedAggregator {
       // マップリデュース風の集計
       public <T, K, V, R> R mapReduce(
               Stream<T> data,
               Function<T, Stream<Pair<K, V>>> mapper,
               BinaryOperator<V> reducer,
               Function<Map<K, V>, R> finalizer) {
           return data.parallel()
               .flatMap(mapper)
               .collect(Collectors.toMap(
                   Pair::getKey,
                   Pair::getValue,
                   reducer
               ))
               .entrySet().stream()
               .collect(Collectors.collectingAndThen(
                   Collectors.toMap(
                       Map.Entry::getKey,
                       Map.Entry::getValue
                   ),
                   finalizer
               ));
       }
   }
   ```

### 評価ポイント
- 複雑な統計処理の実装
- メモリ効率的なアルゴリズム
- 並列処理での正確性
- 実用的な分析機能

## 提出方法

1. 各課題の完全な実装とベンチマーク
2. パフォーマンステストの結果と分析
3. 設計判断の説明ドキュメント
4. 実データでのデモンストレーション

## 発展学習の提案

- **リアクティブストリーム**：Project ReactorやRxJavaの学習
- **Akka Streams**：より高度なストリーム処理
- **Apache Flink/Spark**：分散ストリーム処理
- **JMH**：マイクロベンチマークによる最適化

## 参考リソース

- Java Performance Companion（Charlie Hunt他）
- Reactive Programming with RxJava（Tomasz Nurkiewicz他）
- Stream Processing with Apache Flink（Fabian Hueske他）
- Java Concurrency in Practice（Brian Goetz他）