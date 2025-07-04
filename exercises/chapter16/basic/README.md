# 第16章 基本課題

## 🎯 学習目標
- マルチスレッドプログラミングの基本概念
- ThreadクラスとRunnableインターフェイスの使い分け
- 同期処理（synchronized、Lock）の理解と実装
- java.util.concurrentパッケージの活用
- スレッドプールとExecutorServiceの使用
- 非同期プログラミング（CompletableFuture）による効率的な並行処理

## 📝 課題一覧

### 課題1: 基本的なスレッド操作
**ファイル名**: `BasicThreading.java`, `BasicThreadingTest.java`

基本的なスレッド生成と操作を実装し、マルチスレッドの動作を理解してください。

**要求仕様**:
- Threadクラスの継承によるスレッド作成
- Runnableインターフェイスの実装によるスレッド作成
- スレッドの開始、停止、待機
- スレッド間の実行順序の確認
- スレッドの優先度設定

**実行例**:
```
=== 基本的なスレッド操作 ===
メインスレッド開始: main

Thread継承方式:
WorkerThread-1 開始
WorkerThread-1: タスク 1 実行中...
WorkerThread-1: タスク 2 実行中...
WorkerThread-1: タスク 3 実行中...
WorkerThread-1 完了

Runnable実装方式:
Thread-pool-1-thread-1: カウンタ 1
Thread-pool-1-thread-2: カウンタ 1
Thread-pool-1-thread-3: カウンタ 1
Thread-pool-1-thread-1: カウンタ 2
Thread-pool-1-thread-2: カウンタ 2
Thread-pool-1-thread-3: カウンタ 2

複数スレッド並行実行:
スレッドA: 処理 1/5
スレッドB: 処理 1/5
スレッドC: 処理 1/5
スレッドA: 処理 2/5
スレッドB: 処理 2/5
スレッドC: 処理 2/5
...

スレッド情報:
スレッドA: ID=12, 優先度=5, 状態=RUNNABLE
スレッドB: ID=13, 優先度=7, 状態=RUNNABLE
スレッドC: ID=14, 優先度=3, 状態=RUNNABLE

実行時間測定:
シングルスレッド: 5,000ms
マルチスレッド（3スレッド）: 1,750ms
性能向上: 2.86倍

全スレッド完了
メインスレッド終了
```

**評価ポイント**:
- スレッド作成方法の理解
- 並行実行の概念理解
- スレッドライフサイクルの理解

---

### 課題2: 同期処理とデータ競合対策
**ファイル名**: `SynchronizedCounter.java`, `BankAccount.java`, `SynchronizationTest.java`

同期処理を実装し、データ競合を防ぐ方法を理解してください。

**要求仕様**:
- synchronized キーワードによる排他制御
- Lock インターフェイスを使った明示的なロック
- volatile キーワードの使用
- スレッドセーフなデータ構造の実装
- デッドロック回避策

**実行例**:
```
=== 同期処理とデータ競合対策 ===
データ競合テスト:
非同期カウンタ:
期待値: 10,000
実際の値: 8,743
データ競合が発生しました！

同期カウンタ:
期待値: 10,000
実際の値: 10,000
データ整合性が保たれました

銀行口座の並行操作:
初期残高: 100,000円

並行取引（10スレッド×100回取引）:
スレッド1: 取引開始
スレッド2: 取引開始
...
スレッド1: 1,000円入金 → 残高: 101,000円
スレッド2: 500円出金 → 残高: 100,500円
スレッド3: 2,000円入金 → 残高: 102,500円
...

最終残高: 150,000円
取引回数: 1,000回
成功取引: 950回
失敗取引: 50回（残高不足）

Lockを使った高度な同期:
ReadWriteLock テスト:
読み取りスレッド×5: 並行実行可能
書き込みスレッド×2: 排他実行
読み取り時間: 100ms
書き込み時間: 500ms

デッドロック回避テスト:
Account A → Account B 振込: 成功
Account B → Account A 振込: 成功
ロック順序制御により デッドロック回避

volatile変数テスト:
フラグ変更前: false
フラグ変更後: true（全スレッドで即座に反映）
```

**評価ポイント**:
- 排他制御の適切な実装
- パフォーマンスを考慮した同期設計
- デッドロック回避策の理解

---

### 課題3: 並行コレクションとExecutor
**ファイル名**: `ConcurrentCollections.java`, `TaskExecutor.java`, `ConcurrentTest.java`

並行処理専用のコレクションとExecutorServiceを活用してください。

**要求仕様**:
- ConcurrentHashMap、CopyOnWriteArrayList等の使用
- ExecutorServiceによるスレッドプール管理
- Future と Callable による結果取得
- CompletableFuture による非同期処理
- 並行処理パターンの実装

**実行例**:
```
=== 並行コレクションとExecutor ===
ConcurrentHashMap テスト:
初期データ: {user1=100, user2=200, user3=300}

並行更新（10スレッド）:
スレッド1: user1 に 10 追加
スレッド2: user2 に 20 追加
スレッド3: user3 に 30 追加
...

最終結果: {user1=150, user2=250, user3=350}
データ整合性: 保証

CopyOnWriteArrayList テスト:
読み取り専用操作（高速）
書き込み時のコピー作成
並行読み取り: 10スレッド同時実行
読み取り時間: 1ms（ロックなし）

ExecutorService テスト:
スレッドプール作成: 5スレッド
タスク投入: 20個

タスク実行中:
Thread-1: タスク1 実行（1秒）
Thread-2: タスク2 実行（2秒）
Thread-3: タスク3 実行（1秒）
Thread-4: タスク4 実行（3秒）
Thread-5: タスク5 実行（1秒）

進行状況: 5/20 完了
進行状況: 10/20 完了
進行状況: 15/20 完了
進行状況: 20/20 完了

実行結果:
全タスク完了時間: 8秒
平均タスク時間: 1.6秒

Future による結果取得:
非同期計算開始: 複雑な数学計算
他の処理継続中...
計算結果取得: 42（計算時間: 3秒）

CompletableFuture チェーン:
step1: データ取得 → [1, 2, 3, 4, 5]
step2: 変換処理 → [2, 4, 6, 8, 10]
step3: 集計処理 → 30
最終結果: 30

スレッドプール統計:
アクティブスレッド: 0
完了タスク数: 20
キューサイズ: 0
プール状態: 正常終了
```

**評価ポイント**:
- 並行コレクションの適切な選択
- ExecutorService の効果的な活用
- 非同期処理パターンの実装

---

### 課題4: 生産者・消費者パターン
**ファイル名**: `ProducerConsumer.java`, `BlockingQueueExample.java`, `ProducerConsumerTest.java`

生産者・消費者パターンを実装し、スレッド間通信を理解してください。

**要求仕様**:
- BlockingQueue を使った生産者・消費者パターン
- wait/notify を使った低レベル同期
- セマフォによるリソース制限
- スレッド間のデータ受け渡し
- 処理能力のバランス調整

**実行例**:
```
=== 生産者・消費者パターン ===
BlockingQueue による実装:
キューサイズ: 10
生産者: 2スレッド
消費者: 3スレッド

生産開始:
Producer-1: アイテム1 生成 → キュー[1]
Producer-2: アイテム2 生成 → キュー[1,2]
Producer-1: アイテム3 生成 → キュー[1,2,3]
...

消費開始:
Consumer-1: アイテム1 消費 ← キュー[2,3,4]
Consumer-2: アイテム2 消費 ← キュー[3,4,5]
Consumer-3: アイテム3 消費 ← キュー[4,5,6]
...

統計情報:
生産アイテム数: 1,000個
消費アイテム数: 1,000個
平均キューサイズ: 5.2個
生産速度: 100個/秒
消費速度: 120個/秒

wait/notify による実装:
共有バッファ（サイズ: 5）
Producer: データ生成中...
Buffer: [data1] - notify消費者
Consumer: data1 消費 - notify生産者
Buffer: [data2] - notify消費者
Consumer: data2 消費 - notify生産者

セマフォによるリソース制限:
利用可能リソース: 3個
Thread-1: リソース取得（残り2個）
Thread-2: リソース取得（残り1個）
Thread-3: リソース取得（残り0個）
Thread-4: 待機中...
Thread-1: リソース解放（残り1個）
Thread-4: リソース取得（残り0個）

スループット測定:
生産者1：消費者1 → 50個/秒
生産者1：消費者2 → 75個/秒
生産者2：消費者2 → 90個/秒
生産者2：消費者3 → 95個/秒
最適構成: 生産者2：消費者3

パフォーマンス比較:
ArrayBlockingQueue: 95個/秒
LinkedBlockingQueue: 87個/秒
SynchronousQueue: 120個/秒（直接受け渡し）
```

**評価ポイント**:
- 生産者・消費者パターンの理解
- 適切な同期プリミティブの選択
- スループット最適化の考慮

## 💡 ヒント

### 課題1のヒント
- Thread.start() でスレッド開始（run() 直接呼び出しは×）
- Thread.join() で他スレッドの完了待機
- Thread.sleep() で一時停止

### 課題2のヒント
- synchronized メソッド: public synchronized void method()
- synchronized ブロック: synchronized(object) { ... }
- ReentrantLock で明示的ロック制御

### 課題3のヒント
- Executors.newFixedThreadPool(n) でスレッドプール
- Future<T> で非同期結果取得
- CompletableFuture.supplyAsync() で非同期実行

### 課題4のヒント
- ArrayBlockingQueue で固定サイズキュー
- put() でブロッキング挿入、take() でブロッキング取得
- Semaphore でリソース数制限

## 🔍 マルチスレッドのポイント

1. **スレッド作成**: Thread継承 vs Runnable実装
2. **同期制御**: synchronized vs Lock インターフェイス
3. **並行コレクション**: スレッドセーフなデータ構造
4. **ExecutorService**: スレッドプールによる効率的管理
5. **スレッド間通信**: BlockingQueue、wait/notify
6. **パフォーマンス**: 適切な並行度とオーバーヘッド考慮

## ✅ 完了チェックリスト

- [ ] 課題1: 基本的なスレッド操作ができている
- [ ] 課題2: 同期処理とデータ競合対策が実装できている
- [ ] 課題3: 並行コレクションとExecutorが活用できている
- [ ] 課題4: 生産者・消費者パターンが実装できている
- [ ] スレッドセーフなプログラムが書けている
- [ ] 適切な並行処理設計ができている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度な並行プログラミングに挑戦しましょう！