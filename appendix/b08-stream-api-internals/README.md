# 付録B.08 Stream APIの内部実装

Java 8で導入されたStream APIの内部実装の詳細と、パフォーマンスを最大化するための最適化技術について学習できるプロジェクトです。

## 概要

Stream APIは単なる便利なAPIではなく、高度に最適化された並列処理フレームワークです。その内部メカニズムを理解することで、より効率的なコードを書くことができます。

## なぜStream APIの内部実装を理解する必要があるのか

### よくある性能の問題

- **大量データ処理でのパフォーマンス不足**: 分割しにくいデータ構造や、ボクシングの多い処理
- **メモリ不足によるOutOfMemoryError**: 全データをメモリに展開してから処理する
- **不適切な並列化によるパフォーマンス劣化**: 小さなデータセットでの並列ストリーム使用

## サンプルコード構成

### 1. Spliterator（分割可能イテレータ）の実装
- `CustomSpliterator.java`: カスタムSpliteratorの実装例
  - ArrayListSpliterator: 配列リストの効率的な分割
  - RangeSpliterator: 数値範囲の並列処理（`StreamSupport.intStream`でIntStreamにする）
  - LineSpliterator: ファイルから読み込んだ行のリスト（`List<String>`）を大きめの単位で分割する

### 2. カスタムコレクターの実装
- `CustomCollectors.java`: 効率的なデータ収集の実装
  - EfficientStringJoiner: StringBuilderを使った文字列結合（`Collectors.joining`と同じ働きを自作して、コレクターの4つの関数の役割を学ぶ）
  - ConcurrentHistogramCollector: 並列処理対応のヒストグラム
  - StatisticsCollector: 一度のパスで複数の統計を計算
  - TopKCollector: 大量データから上位K件を効率的に抽出

### 3. 並列ストリームの最適化
- `ParallelStreamOptimization.java`: 並列処理のベストプラクティス
  - データサイズ別の並列化効果測定
  - カスタムForkJoinPoolの使用（独自のプールの中でparallelStreamを動かす方法は、仕様で保証された動作ではない）
  - 並列化に適さないケースの例
  - Spliterator特性による性能への影響

## 実行方法

### 全体のデモンストレーション
```bash
javac -d . src/main/java/com/example/streams/*.java

# Spliteratorのデモ
java com.example.streams.CustomSpliterator

# カスタムコレクターのデモ
java com.example.streams.CustomCollectors

# 並列ストリーム最適化のデモ
java com.example.streams.ParallelStreamOptimization
```

## 学習ポイント

### 1. Spliteratorの仕組み
- **分割戦略**: データを効率的に分割する方法
- **特性フラグ**: ORDERED、SIZED、CONCURRENTなどの最適化ヒント
- **推定サイズ**: 分割判断と並列度の決定

### 2. カスタムコレクターの設計
- **Supplier**: アキュムレータの初期化
- **Accumulator**: 要素の蓄積処理
- **Combiner**: 並列結果の結合
- **Finisher**: 最終結果の変換
- **Characteristics**: 並列化の最適化ヒント

### 3. 並列ストリームの最適化
- **適切なデータサイズ**: 並列化のオーバーヘッドを考慮
- **CPU集約 vs I/O集約**: タスクの性質による最適化
- **分割特性**: ArrayList vs LinkedList vs HashSet
- **副作用の回避**: スレッドセーフな設計

### 4. パフォーマンス測定
- **ベンチマーキング**: 正確な性能測定の方法
- **JMH使用**: マイクロベンチマークのベストプラクティス
- **ウォームアップ**: JVMの最適化を考慮した測定

## 実践的な最適化技術

### Fork/Joinフレームワークの活用
- **Work Stealing**: 負荷バランシングの仕組み
- **共通プール**: 並列ストリームが使うForkJoinPool.commonPool()の既定の並列度は、通常「CPUコア数 - 1」（呼び出したスレッドも処理に加わる）
- **カスタムプール**: スレッド数の調整
- **CPU使用率の最大化**: マルチコア活用

### メモリ効率の向上
- **遅延評価**: 必要な時点での計算実行
- **中間操作の融合**: 複数操作の統合最適化
- **ガベージコレクション**: 短命オブジェクトの削減

### データ構造の選択
- **分割効率**: ArrayList > HashSet > LinkedList
- **キャッシュ局所性**: 連続メモリアクセスの重要性
- **NUMA考慮**: 大規模システムでの最適化

## 関連技術

- **Fork/Joinフレームワーク**: 並列処理の基盤
- **JMH (Java Microbenchmark Harness)**: 性能測定ツール
- **並行コレクション**: ConcurrentHashMap、CopyOnWriteArrayList
- **リアクティブストリーム**: Reactor、RxJava

## 実行結果について

`ParallelStreamOptimization`は、データサイズごとに逐次と並列の時間を次の形式で表示します（数値の部分は環境で変わります）。

```
=== Parallel Stream Effectiveness by Data Size ===
Size:       100 | Sequential:    (時間)ms | Parallel:    (時間)ms | Speedup: (倍率)x
...
Size:   1000000 | Sequential:    (時間)ms | Parallel:    (時間)ms | Speedup: (倍率)x
```

時間は数回の空回し（ウォームアップ）のあとに`System.nanoTime()`で測っていますが、結果はCPUのコア数・JVM・負荷などの環境で変わります。小さなデータでは並列のほうが遅く、大きなデータでは並列のほうが速くなる傾向を読み取ってください。正確に比べるにはJMHで測ります。

このプロジェクトを通じて、Stream APIの内部動作を深く理解し、実際のアプリケーションで高性能なデータ処理を実現する技術を身につけることができます。