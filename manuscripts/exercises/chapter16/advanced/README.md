# 第16章 マルチスレッドプログラミング - 応用レベル課題

## 概要
応用レベル課題では、Executorフレームワークや並行コレクションを活用した、より実践的な並行処理プログラムを実装します。現実のアプリケーション開発で必要となる高度な技術を習得します。

## 課題一覧

### 課題1: ExecutorServiceによるタスク管理
**TaskExecutorDemo.java**

ExecutorServiceを使った効率的なタスク管理システムを実装します。

**要求仕様：**
- 固定サイズスレッドプールの作成と活用
- CallableとFutureによる結果の取得
- タスクのキャンセルとタイムアウト処理
- 複数タスクの並列実行と結果集約

**実装のポイント：**
```java
ExecutorService executor = Executors.newFixedThreadPool(4);

// Callableタスクの送信
Future<Integer> future = executor.submit(() -> {
    // 計算処理
    return result;
});

// 結果の取得（タイムアウト付き）
try {
    Integer result = future.get(5, TimeUnit.SECONDS);
} catch (TimeoutException e) {
    future.cancel(true);
}
```

**実装要件：**
- 並列計算タスクの実装（素数計算、行列演算など）
- プログレス表示機能
- エラーハンドリングと再試行メカニズム
- リソースの適切なクリーンアップ

### 課題2: 並行コレクションの活用
**ConcurrentDataProcessor.java**

ConcurrentHashMapとBlockingQueueを使った高性能データ処理システムを構築します。

**要求仕様：**
- ConcurrentHashMapによる高速キャッシュの実装
- BlockingQueueを使ったプロデューサー・コンシューマー
- 複数スレッドによるデータ処理パイプライン
- スループットの測定と最適化

**実装のポイント：**
```java
// 並行マップによるキャッシュ
ConcurrentHashMap<String, Data> cache = new ConcurrentHashMap<>();
cache.computeIfAbsent(key, k -> loadData(k));

// ブロッキングキューによるパイプライン
BlockingQueue<Task> queue = new LinkedBlockingQueue<>(100);
// プロデューサー
queue.offer(task, 1, TimeUnit.SECONDS);
// コンシューマー
Task task = queue.poll(1, TimeUnit.SECONDS);
```

**実装要件：**
- マルチステージ処理パイプライン
- バックプレッシャー制御
- 統計情報の収集と表示
- 優雅な停止処理

### 課題3: CompletableFutureによる非同期処理チェーン
**AsyncProcessingChain.java**

CompletableFutureを使った複雑な非同期処理フローを実装します。

**要求仕様：**
- 非同期APIの呼び出しチェーン
- 並列処理の組み合わせと結果の集約
- 例外処理とフォールバック
- 非同期処理の合成とタイムアウト

**実装のポイント：**
```java
CompletableFuture<String> future = 
    CompletableFuture.supplyAsync(() -> fetchData())
        .thenApplyAsync(data -> process(data))
        .thenCombine(
            CompletableFuture.supplyAsync(() -> fetchAdditionalData()),
            (result1, result2) -> combine(result1, result2)
        )
        .exceptionally(ex -> handleError(ex));
```

**実装要件：**
- REST APIの並列呼び出しと結果統合
- リトライロジックの実装
- プログレス通知機能
- キャンセル可能な処理チェーン

## 学習のポイント

1. **Executorフレームワークの活用**
   - スレッドプールのサイズ設計
   - タスクの粒度とスケジューリング
   - リソース管理とシャットダウン処理

2. **並行コレクションの選択**
   - ConcurrentHashMap vs Collections.synchronizedMap
   - BlockingQueue の種類と特性
   - 並行度とメモリ使用量のトレードオフ

3. **非同期プログラミングパターン**
   - コールバック地獄の回避
   - エラー伝播と処理
   - 非同期処理のテスト手法

## 提出物

1. 各課題のソースコード（*.java）
2. パフォーマンス測定結果とその分析
3. 設計判断の根拠を含むドキュメント
4. ユニットテストコード

## 評価基準

- **機能の完成度（30%）**: 要求仕様を満たしているか
- **パフォーマンス（25%）**: 効率的な並行処理が実現できているか
- **設計品質（25%）**: 適切なパターンと技術の選択
- **エラー処理（10%）**: 異常系の適切な処理
- **コード品質（10%）**: 保守性と可読性

## 発展課題

さらに理解を深めたい場合：
- ForkJoinPoolを使った再帰的並列処理
- Reactive Streamsパターンの実装
- ノンブロッキングアルゴリズムの実装

## 参考リソース

- [Java Executor Framework](https://docs.oracle.com/javase/tutorial/essential/concurrency/executors.html)
- [Java Concurrent Collections](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html)
- [CompletableFuture Guide](https://www.baeldung.com/java-completablefuture)