# 第15章 マルチスレッドプログラミング - 演習課題

書籍『Java入門』第15章「マルチスレッドプログラミング」の演習課題です。

## 基礎課題（[basic/](basic/)）

- 基本的なスレッド操作
- 同期処理とデータ競合対策
- 並行コレクションとExecutor
- プロデューサー・コンシューマーパターン

## 発展課題（[advanced/](advanced/)）

- 映画館の座席予約（synchronizedによる競合状態の防止、AtomicInteger、ExecutorService）
- 店舗の売上の並行集計（Callable・Future、ConcurrentHashMapのmerge、CompletableFutureのexceptionally・allOf）
- このフォルダーのJavaファイルは解答例です

## チャレンジ課題（[challenge/](challenge/)）

- 学食の注文処理（BlockingQueueによるプロデューサー・コンシューマー、Semaphore、ポイズンピルによる終了、CompletableFuture）
- このフォルダーのJavaファイルは解答例です

## 解答例（[solutions/](solutions/)）

- `CompletableFutureExample.java`
- `CompletableFutureExampleTest.java`
- `ProducerConsumer.java`
- `ProducerConsumerTest.java`
- `ThreadPoolExample.java`
- `ThreadPoolExampleTest.java`
- `ThreadSafeCounter.java`
- `ThreadSafeCounterTest.java`
