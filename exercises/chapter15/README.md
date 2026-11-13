# 第15章 マルチスレッドプログラミング - 演習課題

書籍『Java入門』第15章「マルチスレッドプログラミング」の演習課題です。

## 基礎課題（[basic/](basic/)）

- スレッドを作って待つ（Threadの継承、Runnable、start・join）
- データ競合と同期（synchronized、AtomicInteger、volatile）
- スレッドプールと非同期の処理（ExecutorService・Callable・Future、ConcurrentHashMap、CompletableFuture）
- 生産者と消費者（BlockingQueue、Semaphore）

## 発展課題（[advanced/](advanced/)）

- 映画館の座席予約（synchronizedによる競合状態の防止、AtomicInteger、ExecutorService）
- 店舗の売上の並行集計（Callable・Future、ConcurrentHashMapのmerge、CompletableFutureのexceptionally・allOf）
- このフォルダーのJavaファイルは解答例です

## チャレンジ課題（[challenge/](challenge/)）

- 学食の注文処理（BlockingQueueによるプロデューサー・コンシューマー、Semaphore、ポイズンピルによる終了、CompletableFuture）
- このフォルダーのJavaファイルは解答例です

## 解答例（[solutions/](solutions/)）

- 基礎課題の課題1〜4の解答例（実行例はこの解答例の出力）
