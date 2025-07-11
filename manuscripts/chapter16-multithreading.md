# 第16章 マルチスレッドプログラミング

## 16.1 本章の学習目標

### 16.1.1 前提知識

本章を学習するためには、第13章までに習得した実用的なJavaアプリケーション開発能力が必須となります。特に、オブジェクト指向設計の実践的な理解、インターフェイスと実装の分離、例外処理やコレクションフレームワークの適切な使用など、堅牢なプログラムを作成する基本的な能力が求められます。また、同期処理と非同期処理の概念的な理解も重要です。単一のスレッドで順番に処理が実行される同期処理と、複数の処理が並行して実行される非同期処理の違いを理解し、なぜ非同期処理が必要なのかを認識していることが必要です。さらに、現代のソフトウェア開発においてパフォーマンスとスケーラビリティがなぜ重要なのかという問題意識を持っていることで、本章の内容をより深く理解できます。

システム理解の前提として、OSにおけるプロセスとスレッドの基本概念を理解していることが望ましいです。プロセスが独立したメモリ空間を持つのに対し、スレッドは同一プロセス内でメモリを共有するという違いを理解することで、マルチスレッドプログラミングの利点と課題がより明確になります。また、CPUのコア数、キャッシュメモリ、メモリの可視性といったハードウェアアーキテクチャの基本的な理解があることで、並行処理で発生する問題の本質的な原因を理解しやすくなります。

### 16.1.2 学習目標

本章では、現代のJavaアプリケーション開発に不可欠なマルチスレッドプログラミングを体系的に学習します。知識理解の面では、まずマルチスレッドプログラミングがなぜ必要なのか、その本質的な価値を理解します。マルチコアCPUが標準となった現代において、単一スレッドでは活用できない計算資源を最大限に活用し、応答性の高いアプリケーションを構築するためには、並行処理が不可欠です。同時に、並行処理がもたらす新たな課題、特に競合状態（レースコンディション）やデッドロックといった問題の本質を理解し、これらを回避・解決するための理論的基盤を学びます。

Java 5で導入されたjava.util.concurrentパッケージの設計思想を理解することも重要な学習目標です。このパッケージは、Doug Leaらによって設計された、高性能かつ安全な並行処理のための包括的なフレームワークです。スレッドプールとExecutorServiceの概念を理解することで、スレッドの生成と破棄のオーバーヘッドを削減し、効率的なタスク管理を実現する方法を学びます。

技能習得の観点では、基本的なスレッドの生成と管理から始め、段階的に高度な並行処理技術を習得します。synchronizedキーワードによる排他制御の基本から、より柔軟なLockインターフェイス、さらに高性能なAtomic変数の使用まで、さまざまな同期化メカニズムを状況に応じて使い分けられます。ExecutorServiceを使った効率的なタスク管理では、固定サイズのスレッドプール、キャッシュ型スレッドプール、スケジュール実行など、要件に応じた適切な実装を選択し、活用する能力を身につけます。

並行プログラミング能力の面では、単に動作するマルチスレッドプログラムを書くだけでなく、安全で効率的な並行処理プログラムを設計・実装する能力を養います。これには、不変オブジェクトの活用、スレッドセーフなデータ構造の選択、適切な粒度での同期化など、実践的な設計パターンの理解が含まれます。また、性能向上を目的とした並列化では、並列化のオーバーヘッドと性能向上のバランスを考慮し、実際に測定可能な改善を実現する実装技術を習得します。

最終的な到達レベルとして、基本的な並行処理プログラムを安全に実装できることから始まり、ExecutorServiceを使った実用的な非同期処理の実装、並行処理特有の問題の理解と適切な対策の実施、そしてマルチコア環境での性能向上を実現するプログラムの作成まで、段階的にスキルを向上させます。これにより、現代のソフトウェア開発で求められる高性能で応答性の高いアプリケーションを構築する能力を身につけることができます。



## 16.2 章の構成

本章は、マルチスレッドプログラミングを体系的に学習できるよう、以下のパートで構成されています：

### 16.2.1 [Part A: マルチスレッドの基礎](chapter16a-thread-basics.md)
- マルチスレッドプログラミングの必要性
- スレッドの作成と実行
- 共有リソースと同期制御
- synchronizedキーワードによる排他制御

### 16.2.2 [Part B: Executorフレームワーク](chapter16b-executor-framework.md)
- ExecutorServiceとスレッドプール
- FutureとCallableによる非同期処理
- CompleテーブルFutureによる高度な非同期処理

### 16.2.3 [Part C: 並行コレクションと同期プリミティブ](chapter16c-concurrent-utilities.md)
- 並行コレクション（ConcurrentHashMap、BlockingQueue等）
- 高度な同期プリミティブ（Lock、Semaphore、CountDownLatch等）
- プロデューサー・コンシューマーパターン

### 16.2.4 [Part D: 実践的な並行処理](chapter16d-practical-concurrency.md)
- パフォーマンス測定とベンチマーク
- スレッドセーフなパターン
- 並行処理の落とし穴と解決策
- ForkJoinPoolと並列ストリーム

### 16.2.5 [Part E: 章末演習](chapter16e-exercises.md)
- 基礎レベル課題：スレッド操作と同期処理
- 実践レベル課題：並行コレクションとExecutor
- 応用レベル課題：高度な並行処理パターン

## 16.3 学習の進め方

1. Part Aでスレッドの基本概念と同期制御を理解
2. Part Bで実用的なExecutorフレームワークを習得
3. Part Cで並行処理専用のユーティリティを学習
4. Part Dで実践的な並行処理の設計と実装を習得
5. Part Eの演習課題で実践的なスキルを身につける

各パートは独立して読むことも可能ですが、順番に学習することで、基礎から実践まで体系的にマルチスレッドプログラミングを習得できるよう設計されています。

本章で学んだマルチスレッドプログラミングの概念を活用して、実践的な練習課題に取り組みましょう。

### 16.3.1 演習の目標
マルチスレッドプログラミングの基礎から高度な並行処理までを習得します。




## 16.4 基礎レベル課題（必須）

### 16.4.1 課題1: 基本的なスレッド操作

基本的なスレッド生成と操作を実装してください。

**技術的背景：マルチスレッドの必要性と現代のコンピューティング**

現代のCPUアーキテクチャとマルチスレッドの重要性：

**ムーアの法則の終焉と並列化の時代：**
- **単一コア性能の限界**：2005年ごろから周波数向上が頭打ち
- **マルチコア化**：Intel Core i9は最大24コア、AMD Threadripperは64コア
- **並列化の必須化**：性能向上は並列化でのみ実現可能

**スレッド生成方法の比較：**
```java
// 1. Thread継承（非推奨）
class MyThread extends Thread {
    // 継承の制限：他のクラスを継承できない
}

// 2. Runnable実装（推奨）
class MyTask implements Runnable {
    // インターフェースなので柔軟な設計が可能
}

// 3. ラムダ式（Java 8以降の推奨）
new Thread(() -> {
    // 最も簡潔で読みやすい
}).start();
```

**実際のシステムでの活用例：**
- **Webサーバ**：リクエストごとにスレッド割り当て（Tomcat等）
- **ゲームエンジン**：物理演算、AI、レンダリングの並列処理
- **データベース**：クエリの並列実行、インデックス構築
- **動画エンコーディング**：フレームの並列処理

**スレッドのライフサイクル：**
```
NEW → RUNNABLE → BLOCKED/WAITING/TIMED_WAITING → TERMINATED
```

**パフォーマンスの考慮事項：**
- **コンテキストスイッチ**：約1-10マイクロ秒のオーバーヘッド
- **スレッド生成コスト**：約100マイクロ秒（プールの必要性）
- **最適なスレッド数**：CPU数 × （1 + 待機時間/実行時間）

この演習では、マルチスレッドプログラミングの基礎を実践的に学びます。

**要求仕様：**
- Threadクラスの継承によるスレッド作成
- Runnableインターフェイスの実装によるスレッド作成
- スレッドの開始、停止、待機
- スレッド間の実行順序の確認
- スレッドの優先度設定

**実行例：**
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

**評価ポイント：**
- スレッド作成方法の理解
- 並行実行の概念理解
- スレッドライフサイクルの理解

**実装ヒント：**
- Thread.start（）でスレッド開始（run() 直接呼び出しは×）
- Thread.join() で他スレッドの完了待機
- Thread.sleep() で一時停止



### 16.4.2 課題2: 同期処理とデータ競合対策

同期処理を実装し、データ競合を防ぐ方法を理解してください。

**技術的背景：並行処理の問題と同期化メカニズム**

マルチスレッド環境での共有リソースアクセスは、さまざまな問題を引き起こします：

**データ競合（Race Condition）の危険性：**
```java
// 危険な例：非同期カウンタ
class UnsafeCounter {
    private int count = 0;
    
    public void increment() {
        count++;  // 実際は3つの操作：読み込み→計算→書き込み
    }
}
// 10スレッドで1000回実行すると、期待値10000に対して8000-9800程度になる
```

**同期化メカニズムの比較：**
- **synchronized**：シンプルだが粒度が粗い
- **ReentrantLock**：柔軟だが複雑（tryLock、公平性制御）
- **ReadWriteLock**：読み込み多数の場合に高性能
- **Atomic変数**：CAS（Compare-And-Swap）による高速化

**実世界での同期化の例：**
- **銀行システム**：口座残高の一貫性保証
- **在庫管理**：同時注文による在庫のマイナス防止
- **チケット予約**：座席の二重予約防止
- **キャッシュ管理**：データの整合性維持

**デッドロックの4条件（Coffman条件）：**
1. **相互排他**：リソースの排他的使用
2. **保持と待機**：リソースを保持しながらほかを待つ
3. **横取り不可**：強制的なリソース解放不可
4. **循環待機**：待機の循環グラフ形成

**パフォーマンスへの影響：**
- **ロック競合**：スループット低下の主要因
- **コンテキストスイッチ**：1-10マイクロ秒のオーバーヘッド
- **メモリバリア**：CPUキャッシュの同期コスト
- **スケーラビリティ**：コア数増加による性能向上の限界

この演習では、さまざまな同期化技術を実践し、適切な選択ができる能力を養います。

**要求仕様：**
- synchronizedキーワードによる排他制御
- Lockインターフェイスを使った明示的なロック
- volatileキーワードの使用
- スレッドセーフなデータ構造の実装
- デッドロック回避策

**実行例：**
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




### 16.4.3 課題3: 並行コレクションとExecutor

並行処理専用のコレクションとExecutorServiceを活用してください。

**技術的背景：スレッドプールとタスク管理の重要性**

Executorフレームワークは、Doug Leaによって設計された革新的な並行処理フレームワークです：

**スレッド生成の問題点：**
```java
// アンチパターン：スレッドの野放図な生成
for (int i = 0; i < 10000; i++) {
    new Thread(() -> processTask()).start();  // 危険！
}
// 問題：OutOfMemoryError、コンテキストスイッチの嵐
```

**Executorの利点：**
- **スレッドプール**：再利用によるオーバーヘッド削減
- **タスクキュー**：バックプレッシャー制御
- **統計情報**：監視とチューニング
- **優雅な終了**：shutdown()による安全な停止

**並行コレクションの必要性：**
```java
// 危険：通常のHashMapの並行アクセス
Map<String, Integer> map = new HashMap<>();  // スレッドセーフでない

// 安全：ConcurrentHashMap
Map<String, Integer> map = new ConcurrentHashMap<>();
// セグメントロックによる高い並行性（16セグメント）
```

**実際のシステムでの使用例：**
- **Webサーバ**：リクエスト処理用スレッドプール
- **データベース接続プール**：コネクション管理
- **メッセージキュー**：非同期処理の実装
- **バッチ処理**：並列データ処理

**ExecutorServiceの種類と用途：**
- **FixedThreadPool**：CPU集約的タスク（コア数×2）
- **CachedThreadPool**：I/O集約的タスク（動的拡張）
- **ScheduledThreadPool**：定期実行タスク
- **ForkJoinPool**：分割統治アルゴリズム

**パフォーマンスの考慮：**
- **最適スレッド数**：Little's Law → N = λ × W
- **キューサイズ**：メモリとレイテンシのトレードオフ
- **拒否ポリシー**：CallerRunsPolicy、AbortPolicy等

この演習では、プロダクションレベルの並行処理設計を学びます。

**要求仕様：**
- ConcurrentHashMap、CopyOnWriteArrayList等の使用
- ExecutorServiceによるスレッドプール管理
- FutureとCallableによる結果取得
- CompleテーブルFutureによる非同期処理
- 並行処理パターンの実装

**実行例：**
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




### 16.4.4 課題4: 生産者・消費者パターン

生産者・消費者パターンを実装し、スレッド間通信を理解してください。

**技術的背景：生産者・消費者パターンとスレッド間協調**

生産者・消費者パターンは、並行処理の古典的かつ最も重要なデザインパターンの1つです：

**パターンの必要性：**
- **速度差の吸収**：生産速度と消費速度の不一致
- **バッファリング**：一時的なスパイクへの対応
- **デカップリング**：生産者と消費者の独立性
- **スケーラビリティ**：動的な生産者/消費者の増減

**実世界での適用例：**
```java
// 例：ログ処理システム
Producer: アプリケーション → ログメッセージ生成
Buffer: BlockingQueue → メッセージバッファリング
Consumer: ログライター → ファイル書き込み

// 例：動画エンコーディング
Producer: 動画読み込み → フレーム抽出
Buffer: フレームキュー → メモリ管理
Consumer: エンコーダー → 圧縮処理
```

**BlockingQueueの種類と特性：**
- **ArrayBlockingQueue**：固定サイズ、公平性オプション
- **LinkedBlockingQueue**：オプショナル容量、高スループット
- **PriorityBlockingQueue**：優先度付きタスク処理
- **SynchronousQueue**：容量0、直接受け渡し

**古典的wait/notifyの問題点：**
```java
// 誤った実装例
synchronized(lock) {
    if (!condition) {  // 問題：spurious wakeup
        lock.wait();
    }
}
// 正しい実装：whileループ必須
synchronized(lock) {
    while (!condition) {
        lock.wait();
    }
}
```

**パフォーマンスチューニング：**
- **キューサイズ**：メモリとレイテンシのバランス
- **生産者/消費者比率**：リトルの法則による最適化
- **バッチ処理**：スループット向上技術
- **バックプレッシャー**：過負荷時の制御戦略

**実際のシステムでの問題：**
- **メモリ枯渇**：無制限キューの危険性
- **飢餓状態**：消費者の処理能力不足
- **レイテンシ**：キューイング遅延の蓄積

この演習では、実践的なスレッド間協調メカニズムを習得します。

**要求仕様：**
- BlockingQueueを使った生産者・消費者パターン
- wait/notifyを使った低レベル同期
- セマフォによるリソース制限
- スレッド間のデータ受け渡し
- 処理能力のバランス調整

**実行例：**
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

**評価ポイント：**
- 生産者・消費者パターンの理解
- 適切な同期プリミティブの選択
- スループット最適化の考慮

**実装ヒント：**
- ArrayBlockingQueueで固定サイズキュー
- put（）でブロッキング挿入、take() でブロッキング取得
- Semaphoreでリソース数制限



## 16.5 実装のヒント

### 16.5.1 マルチスレッドのポイント

1. **スレッド作成**: Thread継承vs Runnable実装
2. **同期制御**: synchronized vs Lockインターフェイス
3. **並行コレクション**: スレッドセーフなデータ構造
4. **ExecutorService**: スレッドプールによる効率的管理
5. **スレッド間通信**: BlockingQueue、wait/notify
6. **パフォーマンス**: 適切な並行度とオーバーヘッド考慮

### 16.5.2 よくある落とし穴
- run（）メソッドの直接呼び出し（start()を使う）
- 同期処理の範囲が広すぎる・狭すぎる
- デッドロックの発生（ロック順序に注意）
- リソースリーク（ExecutorServiceのshutdown忘れ）

### 16.5.3 設計のベストプラクティス
- 可能な限りimmuテーブルオブジェクトを使用
- 共有状態を最小限に抑制
- 並行コレクションの積極的活用
- 適切な粒度での同期処理設計



## 16.6 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter16/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── BasicThreading.java
│   ├── SynchronizedCounter.java
│   ├── ConcurrentCollections.java
│   └── ProducerConsumer.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```



## 16.7 完了確認チェックリスト

### 基礎レベル
- [ ] 基本的なスレッド操作ができている
- [ ] 同期処理とデータ競合対策が実装できている
- [ ] 並行コレクションとExecutorが活用できている
- [ ] 生産者・消費者パターンが実装できている

### 技術要素
- [ ] スレッドセーフなプログラムが書けている
- [ ] 適切な並行処理設計ができている
- [ ] パフォーマンスを考慮した実装ができている
- [ ] デッドロック等の問題を回避できている

### 応用レベル
- [ ] 高度な並行処理パターンを実装できている
- [ ] パフォーマンス測定と最適化ができている
- [ ] 実用的な並行アプリケーションが構築できている
- [ ] 並行処理のトラブルシューティングができている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度な並行プログラミングに挑戦しましょう！

## 16.8 マルチスレッドプログラミングの基礎

スレッドとは、プログラム内での処理の流れを表す実行単位です。通常、1つのプログラムは1つのスレッド（メインスレッド）で実行されます。

**マルチスレッド**とは、1つのプログラム内で複数のスレッドを同時に実行し、処理を並行して進める技術です。

### なぜマルチスレッドが必要か

現代のコンピュータは、複数のコアを持つCPU（マルチコアプロセッサ）を搭載するのが一般的です。マルチスレッドプログラミングは、この複数のコアを効率的に活用し、アプリケーションのパフォーマンスを向上させるための重要な技術です。

-   **パフォーマンスの向上**: 時間のかかる処理を複数のスレッドに分割し、同時に実行することで、全体の処理時間を短縮できます。
-   **応答性の維持**: GUIアプリケーションなどで、時間のかかる処理をバックグラウンドのスレッドに任せることで、ユーザーインターフェイスが固まるのを防ぎます。
-   **リソースの有効活用**: サーバアプリケーションなどでは、複数のクライアントからのリクエストを同時に処理することで、計算リソースを有効に活用できます。

### 16.8.1 スレッドの作成と実行

Javaでスレッドを作成するには、主に`Runnable`インターフェイスを実装する方法が推奨されます。

### `Runnable`インターフェイスの実装（推奨）

`Runnable`は、スレッドが実行するタスク（処理内容）を定義するための関数型インターフェイスです。`run()`メソッドを1つだけ持ちます。

```java
// Runnableを実装したクラス
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("タスク実行中: " + Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        MyTask task = new MyTask();
        Thread thread = new Thread(task);
        thread.start(); // スレッドを開始

        // ラムダ式を使えばさらに簡潔
        new Thread(() -> {
            System.out.println("ラムダ式でのタスク実行: " + Thread.currentThread().getName());
        }).start();
    }
}
```

`Runnable`を利用する方法は、Javaがクラスの多重継承をサポートしないという制約を受けず、また「タスク（何をするか）」と「スレッド（どう実行するか）」を分離できるため、より柔軟な設計が可能です。

### 16.8.2 共有リソースと同期制御

複数のスレッドが、同じデータ（オブジェクトや変数）に同時にアクセスすると、予期せぬ問題が発生することがあります。これを**競合状態 (Race Condition)** と呼びます。

### `synchronized`による排他制御

この問題を解決するために、Javaは`synchronized`キーワードによる**排他制御**のしくみを提供します。`synchronized`で保護されたコードブロックは、一度に1つのスレッドしか実行できないことが保証されます。

```java
class SynchronizedCounter {
    private int count = 0;

    // このメソッドはsynchronizedによりスレッドセーフになる
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
```

`synchronized`は非常に強力ですが、ロックの範囲が広すぎるとパフォーマンスの低下を招いたり、複数のロックが絡み合うと**デッドロック**（スレッドがお互いを待ち続けて処理が進まなくなる状態）を引き起こしたりする危険性もあります。

### 16.8.3 Executorフレームワークによる高度なスレッド管理

スレッドを直接生成・管理するのは複雑で、エラーも発生しやすいため、現代のJavaプログラミングでは**Executorフレームワーク**を使用するのが一般的です。これは、スレッドの生成・管理を抽象化し、**スレッドプール**を利用して効率的にタスクを実行するためのしくみです。

#### 16.9.1 ExecutorServiceとスレッドプール

`ExecutorService`は、タスクの投入とスレッドプールの管理を行うためのインターフェイスです。スレッドプールは、あらかじめ作成された再利用可能なスレッドの集まりです。

-   **パフォーマンス向上**: スレッドの生成・破棄コストを削減できます。
-   **リソース管理**: 作成されるスレッド数を制限し、システムの安定性を高めます。

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorExample {
    public static void main(String[] args) {
        // 3つのスレッドを持つスレッドプールを作成
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " is being executed by " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // 1秒の処理をシミュレート
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // ExecutorServiceをシャットダウン
        executor.shutdown();
        try {
            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
```

### 実践的なマルチスレッドの例

#### 1. プロデューサー・コンシューマーパターンの実装

```java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumerExample {
    // 共有キュー
    private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
    private static final AtomicInteger producedCount = new AtomicInteger(0);
    private static final AtomicInteger consumedCount = new AtomicInteger(0);
    
    // プロデューサー（生産者）
    static class Producer implements Runnable {
        private final String name;
        private final int itemsToProduce;
        
        public Producer(String name, int itemsToProduce) {
            this.name = name;
            this.itemsToProduce = itemsToProduce;
        }
        
        @Override
        public void run() {
            try {
                for (int i = 0; i < itemsToProduce; i++) {
                    int item = producedCount.incrementAndGet();
                    queue.put(item); // キューがいっぱいの場合はブロック
                    System.out.printf("%s produced: %d (queue size: %d)%n", 
                        name, item, queue.size());
                    Thread.sleep(100); // 生産に時間がかかることをシミュレート
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // コンシューマー（消費者）
    static class Consumer implements Runnable {
        private final String name;
        private final int itemsToConsume;
        
        public Consumer(String name, int itemsToConsume) {
            this.name = name;
            this.itemsToConsume = itemsToConsume;
        }
        
        @Override
        public void run() {
            try {
                for (int i = 0; i < itemsToConsume; i++) {
                    Integer item = queue.take(); // キューが空の場合はブロック
                    consumedCount.incrementAndGet();
                    System.out.printf("%s consumed: %d (queue size: %d)%n", 
                        name, item, queue.size());
                    Thread.sleep(200); // 消費に時間がかかることをシミュレート
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 2つのプロデューサーと3つのコンシューマーを作成
        executor.execute(new Producer("Producer-1", 10));
        executor.execute(new Producer("Producer-2", 10));
        executor.execute(new Consumer("Consumer-1", 7));
        executor.execute(new Consumer("Consumer-2", 7));
        executor.execute(new Consumer("Consumer-3", 6));
        
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        System.out.println("\n=== 最終結果 ===");
        System.out.println("生産された総数: " + producedCount.get());
        System.out.println("消費された総数: " + consumedCount.get());
        System.out.println("キューの残り: " + queue.size());
    }
}
```

#### 2. Future と Callable を使った非同期処理

```java
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class FutureCallableExample {
    
    // 計算に時間がかかるタスク
    static class CalculationTask implements Callable<Long> {
        private final int n;
        
        public CalculationTask(int n) {
            this.n = n;
        }
        
        @Override
        public Long call() throws Exception {
            System.out.println("計算開始: フィボナッチ数列の第" + n + "項");
            long start = System.currentTimeMillis();
            long result = fibonacci(n);
            long time = System.currentTimeMillis() - start;
            System.out.printf("計算完了: F(%d) = %d (所要時間: %dms)%n", n, result, time);
            return result;
        }
        
        private long fibonacci(int n) {
            if (n <= 1) return n;
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }
    
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<Long>> futures = new ArrayList<>();
        
        // 複数の計算タスクを非同期で実行
        int[] numbers = {35, 38, 40, 42};
        for (int n : numbers) {
            Future<Long> future = executor.submit(new CalculationTask(n));
            futures.add(future);
        }
        
        // 他の処理を実行できる
        System.out.println("計算中に他の処理を実行...");
        Thread.sleep(100);
        
        // 結果を取得（必要になったタイミングで）
        System.out.println("\n=== 計算結果 ===");
        for (int i = 0; i < futures.size(); i++) {
            try {
                // get()は結果が得られるまでブロック
                Long result = futures.get(i).get(10, TimeUnit.SECONDS);
                System.out.println("F(" + numbers[i] + ") = " + result);
            } catch (TimeoutException e) {
                System.out.println("F(" + numbers[i] + ") = タイムアウト");
                futures.get(i).cancel(true); // タスクをキャンセル
            }
        }
        
        executor.shutdown();
    }
}
```

#### 3. CompleテーブルFuture による高度な非同期処理

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class CompletableFutureExample {
    
    // 外部APIを呼び出すシミュレーション
    static class WeatherService {
        private static final Random random = new Random();
        
        public static CompletableFuture<String> getTemperature(String city) {
            return CompletableFuture.supplyAsync(() -> {
                System.out.println("気温取得中: " + city);
                // APIコールをシミュレート
                try {
                    Thread.sleep(1000 + random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                int temp = 20 + random.nextInt(15);
                return city + ": " + temp + "°C";
            });
        }
        
        public static CompletableFuture<String> getHumidity(String city) {
            return CompletableFuture.supplyAsync(() -> {
                System.out.println("湿度取得中: " + city);
                try {
                    Thread.sleep(1000 + random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                int humidity = 40 + random.nextInt(40);
                return city + ": " + humidity + "%";
            });
        }
    }
    
    public static void main(String[] args) throws Exception {
        // 1. 単純な非同期処理
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("非同期タスク開始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "処理完了";
        });
        
        // 2. チェーン処理
        CompletableFuture<String> chainedFuture = future1
            .thenApply(result -> result + " -> 変換処理")
            .thenApply(result -> result + " -> 最終処理");
        
        System.out.println("チェーン結果: " + chainedFuture.get());
        
        // 3. 複数の非同期処理を組み合わせる
        String[] cities = {"東京", "大阪", "名古屋"};
        
        // 各都市の気温と湿度を並行して取得
        CompletableFuture<Void> allWeatherData = CompletableFuture.allOf(
            cities[0], cities[1], cities[2]
        ).stream()
        .map(city -> {
            CompletableFuture<String> temp = WeatherService.getTemperature(city);
            CompletableFuture<String> humidity = WeatherService.getHumidity(city);
            
            // 気温と湿度を組み合わせる
            return temp.thenCombine(humidity, (t, h) -> t + ", " + h);
        })
        .map(future -> future.thenAccept(System.out::println))
        .toArray(CompletableFuture[]::new);
        
        // すべての処理が完了するまで待つ
        CompletableFuture.allOf(allWeatherData).get();
        
        // 4. タイムアウト処理
        CompletableFuture<String> timeoutFuture = CompletableFuture
            .supplyAsync(() -> {
                try {
                    Thread.sleep(5000); // 長時間かかる処理
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "遅い処理の結果";
            })
            .orTimeout(2, TimeUnit.SECONDS)
            .exceptionally(e -> "タイムアウトしました");
        
        System.out.println("\nタイムアウト処理: " + timeoutFuture.get());
    }
}
```

#### 4. 並行コレクションの活用

```java
import java.util.concurrent.*;
import java.util.Map;
import java.util.List;
import java.util.Random;

public class ConcurrentCollectionsExample {
    
    public static void main(String[] args) throws InterruptedException {
        // 1. ConcurrentHashMap - スレッドセーフなマップ
        ConcurrentHashMap<String, Integer> wordCount = new ConcurrentHashMap<>();
        String[] words = {"apple", "banana", "apple", "cherry", "banana", "apple"};
        
        // 複数スレッドで単語をカウント
        ExecutorService executor = Executors.newFixedThreadPool(3);
        for (String word : words) {
            executor.execute(() -> {
                wordCount.merge(word, 1, Integer::sum);
                System.out.println(Thread.currentThread().getName() + 
                    " counted " + word + ": " + wordCount.get(word));
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.println("\n単語カウント結果: " + wordCount);
        
        // 2. CopyOnWriteArrayList - 読み取り最適化リスト
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.addAll(List.of("初期値1", "初期値2", "初期値3"));
        
        // 読み取りスレッド（高速・ロックなし）
        Runnable reader = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("読み取り: " + list);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        // 書き込みスレッド（コピーが発生）
        Runnable writer = () -> {
            for (int i = 0; i < 3; i++) {
                list.add("新規追加" + i);
                System.out.println("書き込み: 新規追加" + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        Thread readerThread = new Thread(reader);
        Thread writerThread = new Thread(writer);
        readerThread.start();
        writerThread.start();
        readerThread.join();
        writerThread.join();
        
        // 3. ConcurrentLinkedQueue - ロックフリーキュー
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        Random random = new Random();
        
        // 生産者
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                int value = random.nextInt(100);
                queue.offer(value);
                System.out.println("生産: " + value);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // 消費者
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                Integer value;
                while ((value = queue.poll()) == null) {
                    // キューが空の場合は待機
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("消費: " + value);
            }
        });
        
        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}
```

#### 5. 高度な同期プリミティブ

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class AdvancedSynchronizationExample {
    
    // 1. ReadWriteLock - 読み取り/書き込みロック
    static class SharedResource {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock readLock = lock.readLock();
        private final Lock writeLock = lock.writeLock();
        private String data = "初期データ";
        
        public String read() {
            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 読み取り開始");
                Thread.sleep(100); // 読み取り処理をシミュレート
                return data;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } finally {
                System.out.println(Thread.currentThread().getName() + " 読み取り完了");
                readLock.unlock();
            }
        }
        
        public void write(String newData) {
            writeLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 書き込み開始");
                Thread.sleep(500); // 書き込み処理をシミュレート
                data = newData;
                System.out.println(Thread.currentThread().getName() + " 書き込み完了: " + newData);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writeLock.unlock();
            }
        }
    }
    
    // 2. Semaphore - リソース数制限
    static class ResourcePool {
        private final Semaphore semaphore;
        private final boolean[] resources;
        
        public ResourcePool(int size) {
            this.semaphore = new Semaphore(size);
            this.resources = new boolean[size];
        }
        
        public int acquire() throws InterruptedException {
            semaphore.acquire();
            synchronized (resources) {
                for (int i = 0; i < resources.length; i++) {
                    if (!resources[i]) {
                        resources[i] = true;
                        System.out.println(Thread.currentThread().getName() + 
                            " がリソース " + i + " を取得");
                        return i;
                    }
                }
            }
            throw new IllegalStateException("リソースが見つかりません");
        }
        
        public void release(int resourceId) {
            synchronized (resources) {
                resources[resourceId] = false;
                System.out.println(Thread.currentThread().getName() + 
                    " がリソース " + resourceId + " を解放");
            }
            semaphore.release();
        }
    }
    
    // 3. CountDownLatch - カウントダウン同期
    static class RaceExample {
        public static void demonstrate() throws InterruptedException {
            int runnerCount = 5;
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch finishSignal = new CountDownLatch(runnerCount);
            
            for (int i = 0; i < runnerCount; i++) {
                int runnerId = i + 1;
                new Thread(() -> {
                    try {
                        System.out.println("ランナー " + runnerId + " 準備完了");
                        startSignal.await(); // スタート信号を待つ
                        
                        System.out.println("ランナー " + runnerId + " スタート！");
                        Thread.sleep((long)(Math.random() * 3000)); // 走行時間
                        System.out.println("ランナー " + runnerId + " ゴール！");
                        
                        finishSignal.countDown(); // ゴールしたことを通知
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            Thread.sleep(1000);
            System.out.println("位置について... よーい...");
            Thread.sleep(1000);
            System.out.println("ドン！");
            startSignal.countDown(); // 全ランナーをスタートさせる
            
            finishSignal.await(); // 全ランナーのゴールを待つ
            System.out.println("レース終了！");
        }
    }
    
    public static void main(String[] args) throws Exception {
        // ReadWriteLockのデモ
        System.out.println("=== ReadWriteLock デモ ===");
        SharedResource resource = new SharedResource();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 複数の読み取りスレッド（並行実行可能）
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                String data = resource.read();
                System.out.println("読み取りデータ: " + data);
            });
        }
        
        // 書き込みスレッド（排他実行）
        executor.execute(() -> resource.write("更新データ"));
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
        
        // Semaphoreのデモ
        System.out.println("\n=== Semaphore デモ ===");
        ResourcePool pool = new ResourcePool(3);
        ExecutorService poolExecutor = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < 5; i++) {
            poolExecutor.execute(() -> {
                try {
                    int resourceId = pool.acquire();
                    Thread.sleep(1000); // リソースを使用
                    pool.release(resourceId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        poolExecutor.shutdown();
        poolExecutor.awaitTermination(10, TimeUnit.SECONDS);
        
        // CountDownLatchのデモ
        System.out.println("\n=== CountDownLatch デモ ===");
        RaceExample.demonstrate();
    }
}
```

#### 16.9.2 Futureによる非同期処理の結果取得

計算結果などの戻り値が必要なタスクには、`Callable<V>`インターフェイスを使います。`submit()`メソッドで`Callable`を投入すると、非同期処理の結果を表す`Future<V>`オブジェクトが返されます。

```java
import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> {
            System.out.println("Calculating sum...");
            Thread.sleep(2000); // 時間のかかる計算を模倣
            int sum = 0;
            for (int i = 1; i <= 100; i++) {
                sum += i;
            }
            return sum;
        };

        Future<Integer> future = executor.submit(task);
        System.out.println("他の処理を実行中です...");

        // future.get()でタスクの完了を待ち、結果を取得
        Integer result = future.get();
        System.out.println("計算結果: " + result);

        executor.shutdown();
    }
}
```

### 実践的なパフォーマンス測定とベンチマーク

```java
import java.util.concurrent.*;
import java.util.Arrays;
import java.util.Random;

public class ParallelPerformanceExample {
    
    // 大規模配列の並列ソート
    public static void parallelSortBenchmark() {
        int size = 10_000_000;
        int[] array1 = new int[size];
        int[] array2 = new int[size];
        Random random = new Random();
        
        // ランダムデータの生成
        for (int i = 0; i < size; i++) {
            int value = random.nextInt();
            array1[i] = value;
            array2[i] = value;
        }
        
        // シングルスレッドソート
        long startTime = System.currentTimeMillis();
        Arrays.sort(array1);
        long singleThreadTime = System.currentTimeMillis() - startTime;
        
        // 並列ソート
        startTime = System.currentTimeMillis();
        Arrays.parallelSort(array2);
        long parallelTime = System.currentTimeMillis() - startTime;
        
        System.out.println("=== ソートベンチマーク ===");
        System.out.println("配列サイズ: " + size);
        System.out.println("シングルスレッド: " + singleThreadTime + "ms");
        System.out.println("並列処理: " + parallelTime + "ms");
        System.out.println("性能向上: " + 
            String.format("%.2fx", (double)singleThreadTime / parallelTime));
    }
    
    // 並列ストリーム処理
    public static void parallelStreamExample() {
        int size = 50_000_000;
        long[] numbers = new long[size];
        Arrays.fill(numbers, 1);
        
        // シーケンシャル処理
        long startTime = System.currentTimeMillis();
        long sum1 = Arrays.stream(numbers).sum();
        long sequentialTime = System.currentTimeMillis() - startTime;
        
        // 並列処理
        startTime = System.currentTimeMillis();
        long sum2 = Arrays.stream(numbers).parallel().sum();
        long parallelTime = System.currentTimeMillis() - startTime;
        
        System.out.println("\n=== ストリーム処理ベンチマーク ===");
        System.out.println("要素数: " + size);
        System.out.println("シーケンシャル: " + sequentialTime + "ms");
        System.out.println("並列: " + parallelTime + "ms");
        System.out.println("性能向上: " + 
            String.format("%.2fx", (double)sequentialTime / parallelTime));
    }
    
    // ForkJoinPoolを使った再帰的並列処理
    static class RecursiveSum extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;
        private final long[] array;
        private final int start, end;
        
        public RecursiveSum(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                // 小さなタスクは直接計算
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            } else {
                // 大きなタスクは分割
                int mid = (start + end) / 2;
                RecursiveSum left = new RecursiveSum(array, start, mid);
                RecursiveSum right = new RecursiveSum(array, mid, end);
                
                left.fork(); // 左側を非同期実行
                long rightResult = right.compute(); // 右側を現在のスレッドで実行
                long leftResult = left.join(); // 左側の結果を待つ
                
                return leftResult + rightResult;
            }
        }
    }
    
    public static void forkJoinExample() {
        int size = 100_000_000;
        long[] array = new long[size];
        Arrays.fill(array, 1);
        
        ForkJoinPool pool = new ForkJoinPool();
        
        long startTime = System.currentTimeMillis();
        Long result = pool.invoke(new RecursiveSum(array, 0, size));
        long time = System.currentTimeMillis() - startTime;
        
        System.out.println("\n=== ForkJoinPool ベンチマーク ===");
        System.out.println("要素数: " + size);
        System.out.println("計算結果: " + result);
        System.out.println("処理時間: " + time + "ms");
        System.out.println("使用スレッド数: " + pool.getParallelism());
    }
    
    public static void main(String[] args) {
        // CPUコア数の確認
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("利用可能なCPUコア数: " + processors);
        
        parallelSortBenchmark();
        parallelStreamExample();
        forkJoinExample();
    }
}
```

### スレッドセーフなシングルトンパターン

```java
public class ThreadSafeSingletonExamples {
    
    // 1. 同期化メソッドを使った実装（シンプルだが遅い）
    static class SynchronizedSingleton {
        private static SynchronizedSingleton instance;
        
        private SynchronizedSingleton() {}
        
        public static synchronized SynchronizedSingleton getInstance() {
            if (instance == null) {
                instance = new SynchronizedSingleton();
            }
            return instance;
        }
    }
    
    // 2. ダブルチェックロッキング（高速だが複雑）
    static class DoubleCheckedSingleton {
        private static volatile DoubleCheckedSingleton instance;
        
        private DoubleCheckedSingleton() {}
        
        public static DoubleCheckedSingleton getInstance() {
            if (instance == null) {
                synchronized (DoubleCheckedSingleton.class) {
                    if (instance == null) {
                        instance = new DoubleCheckedSingleton();
                    }
                }
            }
            return instance;
        }
    }
    
    // 3. 静的内部クラスホルダー（推奨・遅延初期化）
    static class HolderSingleton {
        private HolderSingleton() {}
        
        private static class Holder {
            private static final HolderSingleton INSTANCE = new HolderSingleton();
        }
        
        public static HolderSingleton getInstance() {
            return Holder.INSTANCE;
        }
    }
    
    // 4. Enumシングルトン（最も安全）
    enum EnumSingleton {
        INSTANCE;
        
        public void doSomething() {
            System.out.println("Enum singleton method");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // マルチスレッドでのシングルトンアクセステスト
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);
        
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                // 各実装のインスタンスを取得
                HolderSingleton singleton = HolderSingleton.getInstance();
                System.out.println(Thread.currentThread().getName() + 
                    ": " + singleton.hashCode());
                latch.countDown();
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // すべて同じハッシュコードが出力されることを確認
    }
}
```

#### 16.15 一般的な並行処理の落とし穴と解決策

```java
import java.util.*;
import java.util.concurrent.*;

public class ConcurrencyPitfallsAndSolutions {
    
    // 落とし穴1: 不適切な同期によるデッドロック
    static class DeadlockExample {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();
        
        // デッドロックが発生する可能性のあるコード
        public void method1() {
            synchronized (lock1) {
                System.out.println("Method1: lock1を取得");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock2) {
                    System.out.println("Method1: lock2を取得");
                }
            }
        }
        
        public void method2() {
            synchronized (lock2) {
                System.out.println("Method2: lock2を取得");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock1) {
                    System.out.println("Method2: lock1を取得");
                }
            }
        }
        
        // 解決策: ロック順序の統一
        public void safeMethod1() {
            synchronized (lock1) {
                synchronized (lock2) {
                    System.out.println("Safe method1 実行");
                }
            }
        }
        
        public void safeMethod2() {
            synchronized (lock1) {  // 同じ順序でロック
                synchronized (lock2) {
                    System.out.println("Safe method2 実行");
                }
            }
        }
    }
    
    // 落とし穴2: 不適切なvolatileの使用
    static class VolatileMisuse {
        // 誤り: volatileは複合操作をアトミックにしない
        private volatile int counter = 0;
        
        public void incorrectIncrement() {
            counter++; // これはアトミックではない！
        }
        
        // 解決策: AtomicIntegerを使用
        private final AtomicInteger atomicCounter = new AtomicInteger(0);
        
        public void correctIncrement() {
            atomicCounter.incrementAndGet();
        }
    }
    
    // 落とし穴3: スレッドセーフでないコレクションの誤用
    static class CollectionMisuse {
        // 誤り: 通常のHashMapは並行アクセスに対して安全でない
        private final Map<String, Integer> unsafeMap = new HashMap<>();
        
        public void unsafeOperation() {
            // 複数スレッドから同時に呼ばれると問題発生
            unsafeMap.put("key", unsafeMap.getOrDefault("key", 0) + 1);
        }
        
        // 解決策1: ConcurrentHashMapを使用
        private final ConcurrentHashMap<String, Integer> safeMap = 
            new ConcurrentHashMap<>();
        
        public void safeOperation1() {
            safeMap.merge("key", 1, Integer::sum);
        }
        
        // 解決策2: 同期化
        private final Map<String, Integer> syncMap = new HashMap<>();
        
        public synchronized void safeOperation2() {
            syncMap.put("key", syncMap.getOrDefault("key", 0) + 1);
        }
    }
    
    // 落とし穴4: スレッドリーク
    static class ThreadLeakExample {
        // 誤り: ExecutorServiceを適切にシャットダウンしない
        public void causeThreadLeak() {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            executor.execute(() -> System.out.println("Task"));
            // executorをシャットダウンしないとスレッドがリーク！
        }
        
        // 解決策: try-finallyまたはtry-with-resourcesパターン
        public void preventThreadLeak() {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            try {
                executor.execute(() -> System.out.println("Task"));
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("並行処理の一般的な落とし穴の例");
        
        // デッドロックの回避例
        DeadlockExample example = new DeadlockExample();
        Thread t1 = new Thread(example::safeMethod1);
        Thread t2 = new Thread(example::safeMethod2);
        t1.start();
        t2.start();
        
        // アトミック操作の例
        VolatileMisuse volatileExample = new VolatileMisuse();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < 1000; i++) {
            executor.execute(volatileExample::correctIncrement);
        }
        
        executor.shutdown();
    }
}
```

### 16.8.4 より深い理解のために

本章で学んだマルチスレッドプログラミングの基礎をさらに深く理解したい方は、付録B.09「Java Memory ModelとHappens-Before関係」を参照してください。この付録では以下の高度なトピックを扱います：

- **Java Memory Model（JMM）**: メモリの可視性とプログラムの実行順序を定義するしくみ
- **Happens-Before関係**: 並行プログラムの正確性を保証する順序関係
- **メモリバリアとvolatileの実装**: ハードウェアレベルでの同期メカニズム
- **ロックフリーアルゴリズム**: 高性能な並行データ構造の実装
- **False Sharingとパフォーマンス最適化**: CPUキャッシュを考慮した最適化技術

これらの知識は、高性能な並行アプリケーションの開発や、微妙な並行性バグのデバッグに役立ちます。

### 16.8.5 まとめ

-   **マルチスレッド**は、複数の処理を並行して実行し、アプリケーションのパフォーマンスや応答性を向上させる技術です。
-   タスクの定義には、柔軟性の高い`Runnable`インターフェイス（特にラムダ式）の使用が推奨されます。
-   複数のスレッドが共有リソースにアクセスする際は、`synchronized`などによる**同期制御**が必要です。
-   **Executorフレームワーク**とスレッドプールを利用することで、スレッド管理を安全かつ効率的に行うことができます。

マルチスレッドプログラミングは、現代のアプリケーション開発において不可欠なスキルの1つです。



<!-- Merged from chapter16a-thread-basics.md -->






<!-- Merged from chapter16b-executor-framework.md -->

## 16.9 Executorフレームワーク

スレッドを直接生成・管理するのは複雑で、エラーも発生しやすいため、現代のJavaプログラミングでは**Executorフレームワーク**を使用するのが一般的です。これは、スレッドの生成・管理を抽象化し、**スレッドプール**を利用して効率的にタスクを実行するためのしくみです。

### 16.9.1 ExecutorServiceとスレッドプール

`ExecutorService`は、タスクの投入とスレッドプールの管理を行うためのインターフェイスです。スレッドプールは、あらかじめ作成された再利用可能なスレッドの集まりです。

-   **パフォーマンス向上**: スレッドの生成・破棄コストを削減できます。
-   **リソース管理**: 作成されるスレッド数を制限し、システムの安定性を高めます。

### 16.9.2 Futureによる非同期処理の結果取得

計算結果などの戻り値が必要なタスクには、`Callable<V>`インターフェイスを使います。`submit()`メソッドで`Callable`を投入すると、非同期処理の結果を表す`Future<V>`オブジェクトが返されます。

```java
import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> {
            System.out.println("Calculating sum...");
            Thread.sleep(2000); // 時間のかかる計算を模倣
            int sum = 0;
            for (int i = 1; i <= 100; i++) {
                sum += i;
            }
            return sum;
        };

        Future<Integer> future = executor.submit(task);
        System.out.println("他の処理を実行中です...");

        // future.get()でタスクの完了を待ち、結果を取得
        Integer result = future.get();
        System.out.println("計算結果: " + result);

        executor.shutdown();
    }
}
```

### 16.9.4 さまざまなExecutorの種類

```java
import java.util.concurrent.*;

public class ExecutorTypesExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. 固定サイズのスレッドプール
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        System.out.println("=== Fixed Thread Pool ===");
        for (int i = 0; i < 6; i++) {
            final int taskId = i;
            fixedPool.execute(() -> {
                System.out.println("Task " + taskId + " executed by " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        fixedPool.shutdown();
        fixedPool.awaitTermination(10, TimeUnit.SECONDS);
        
        // 2. キャッシュ型スレッドプール（必要に応じてスレッドを作成）
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        System.out.println("\n=== Cached Thread Pool ===");
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            cachedPool.execute(() -> {
                System.out.println("Task " + taskId + " executed by " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        cachedPool.shutdown();
        cachedPool.awaitTermination(5, TimeUnit.SECONDS);
        
        // 3. 単一スレッドのExecutor
        ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
        System.out.println("\n=== Single Thread Executor ===");
        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            singleExecutor.execute(() -> {
                System.out.println("Task " + taskId + " executed sequentially");
            });
        }
        singleExecutor.shutdown();
        singleExecutor.awaitTermination(2, TimeUnit.SECONDS);
        
        // 4. スケジュール機能付きExecutor
        ScheduledExecutorService scheduledExecutor = 
            Executors.newScheduledThreadPool(2);
        System.out.println("\n=== Scheduled Executor ===");
        
        // 一定時間後に実行
        scheduledExecutor.schedule(() -> 
            System.out.println("Delayed task executed after 2 seconds"), 
            2, TimeUnit.SECONDS);
        
        // 定期的に実行
        ScheduledFuture<?> periodicTask = scheduledExecutor.scheduleAtFixedRate(() -> 
            System.out.println("Periodic task executed every 1 second"), 
            0, 1, TimeUnit.SECONDS);
        
        // 5秒後に定期タスクをキャンセル
        Thread.sleep(5000);
        periodicTask.cancel(false);
        
        scheduledExecutor.shutdown();
        scheduledExecutor.awaitTermination(2, TimeUnit.SECONDS);
    }
}
```

### 16.9.6 Executorのベストプラクティス

```java
import java.util.concurrent.*;

public class ExecutorBestPractices {
    
    // カスタムThreadFactoryの例
    static class NamedThreadFactory implements ThreadFactory {
        private int counter = 0;
        private final String prefix;
        
        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }
        
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(prefix + "-" + counter++);
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
    
    // RejectedExecutionHandlerの例
    static class CustomRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("Task rejected: " + r.toString());
            // 代替処理を実行するか、ログに記録する
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // カスタムThreadPoolExecutorの作成
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,                      // コアプールサイズ
            4,                      // 最大プールサイズ
            60L,                    // キープアライブ時間
            TimeUnit.SECONDS,       // 時間単位
            new LinkedBlockingQueue<>(10),  // タスクキュー
            new NamedThreadFactory("CustomWorker"),  // ThreadFactory
            new CustomRejectionHandler()  // RejectedExecutionHandler
        );
        
        // ExecutorServiceの監視
        System.out.println("=== Executor監視 ===");
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            try {
                executor.execute(() -> {
                    System.out.println("Task " + taskId + " executed by " + 
                        Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                
                // Executorの状態を出力
                System.out.printf("Active: %d, Completed: %d, Queue: %d%n",
                    executor.getActiveCount(),
                    executor.getCompletedTaskCount(),
                    executor.getQueue().size());
                
            } catch (RejectedExecutionException e) {
                System.err.println("Task " + taskId + " was rejected");
            }
            
            Thread.sleep(100);
        }
        
        // 適切なシャットダウン処理
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            System.err.println("Executor did not terminate in time");
            executor.shutdownNow();
        }
        
        System.out.println("\n最終統計:");
        System.out.println("完了タスク数: " + executor.getCompletedTaskCount());
    }
}
```

### 16.9.7 まとめ

このセクションでは、Executorフレームワークを使った高度なスレッド管理について学習しました：

- **ExecutorService**：スレッドプールによる効率的なタスク管理
- **Future と Callable**：非同期処理の結果取得
- **CompleテーブルFuture**：チェイン処理や組み合わせ処理が可能な高度な非同期処理
- **さまざまなExecutor**：用途に応じた適切なExecutorの選択
- **ベストプラクティス**：カスタム設定と適切なリソース管理

Executorフレームワークを使うことで、スレッドの直接管理から解放され、より安全で効率的な並行処理プログラムを作成できます。次のパートでは、並行コレクションと高度な同期プリミティブについて学習します。



次のパート：[Part C - 並行コレクションと同期プリミティブ](chapter16c-concurrent-utilities.md)



<!-- Merged from chapter16c-concurrent-utilities.md -->


## 16.10 並行コレクションの活用

通常のコレクション（ArrayList、HashMap等）は、複数のスレッドから同時にアクセスされることを想定していません。Javaは、並行処理に対応した特別なコレクションを提供しています。

### 16.10.1 ConcurrentHashMap

### 16.10.2 CopyOnWriteArrayList

```java
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

public class CopyOnWriteExample {
    public static void main(String[] args) throws InterruptedException {
        // 2. CopyOnWriteArrayList - 読み取り最適化リスト
        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.addAll(List.of("初期値1", "初期値2", "初期値3"));
        
        // 読み取りスレッド（高速・ロックなし）
        Runnable reader = () -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("読み取り: " + list);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        // 書き込みスレッド（コピーが発生）
        Runnable writer = () -> {
            for (int i = 0; i < 3; i++) {
                list.add("新規追加" + i);
                System.out.println("書き込み: 新規追加" + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        
        Thread readerThread = new Thread(reader);
        Thread writerThread = new Thread(writer);
        readerThread.start();
        writerThread.start();
        readerThread.join();
        writerThread.join();
    }
}
```

### 16.10.3 BlockingQueue と プロデューサー・コンシューマーパターン

## 16.11 高度な同期プリミティブ

### 16.11.1 ReadWriteLock - 読み取り/書き込みロック

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class AdvancedSynchronizationExample {
    
    // 1. ReadWriteLock - 読み取り/書き込みロック
    static class SharedResource {
        private final ReadWriteLock lock = new ReentrantReadWriteLock();
        private final Lock readLock = lock.readLock();
        private final Lock writeLock = lock.writeLock();
        private String data = "初期データ";
        
        public String read() {
            readLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 読み取り開始");
                Thread.sleep(100); // 読み取り処理をシミュレート
                return data;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } finally {
                System.out.println(Thread.currentThread().getName() + " 読み取り完了");
                readLock.unlock();
            }
        }
        
        public void write(String newData) {
            writeLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " 書き込み開始");
                Thread.sleep(500); // 書き込み処理をシミュレート
                data = newData;
                System.out.println(Thread.currentThread().getName() + " 書き込み完了: " + newData);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writeLock.unlock();
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        // ReadWriteLockのデモ
        System.out.println("=== ReadWriteLock デモ ===");
        SharedResource resource = new SharedResource();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        // 複数の読み取りスレッド（並行実行可能）
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                String data = resource.read();
                System.out.println("読み取りデータ: " + data);
            });
        }
        
        // 書き込みスレッド（排他実行）
        executor.execute(() -> resource.write("更新データ"));
        
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }
}
```

### 16.11.2 Semaphore - リソース数制限

```java
import java.util.concurrent.Semaphore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SemaphoreExample {
    // 2. Semaphore - リソース数制限
    static class ResourcePool {
        private final Semaphore semaphore;
        private final boolean[] resources;
        
        public ResourcePool(int size) {
            this.semaphore = new Semaphore(size);
            this.resources = new boolean[size];
        }
        
        public int acquire() throws InterruptedException {
            semaphore.acquire();
            synchronized (resources) {
                for (int i = 0; i < resources.length; i++) {
                    if (!resources[i]) {
                        resources[i] = true;
                        System.out.println(Thread.currentThread().getName() + 
                            " がリソース " + i + " を取得");
                        return i;
                    }
                }
            }
            throw new IllegalStateException("リソースが見つかりません");
        }
        
        public void release(int resourceId) {
            synchronized (resources) {
                resources[resourceId] = false;
                System.out.println(Thread.currentThread().getName() + 
                    " がリソース " + resourceId + " を解放");
            }
            semaphore.release();
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // Semaphoreのデモ
        System.out.println("=== Semaphore デモ ===");
        ResourcePool pool = new ResourcePool(3);
        ExecutorService executor = Executors.newFixedThreadPool(5);
        
        for (int i = 0; i < 5; i++) {
            executor.execute(() -> {
                try {
                    int resourceId = pool.acquire();
                    Thread.sleep(1000); // リソースを使用
                    pool.release(resourceId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
    }
}
```

### 16.11.3 CountDownLatch - カウントダウン同期

```java
import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
    // 3. CountDownLatch - カウントダウン同期
    static class RaceExample {
        public static void demonstrate() throws InterruptedException {
            int runnerCount = 5;
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch finishSignal = new CountDownLatch(runnerCount);
            
            for (int i = 0; i < runnerCount; i++) {
                int runnerId = i + 1;
                new Thread(() -> {
                    try {
                        System.out.println("ランナー " + runnerId + " 準備完了");
                        startSignal.await(); // スタート信号を待つ
                        
                        System.out.println("ランナー " + runnerId + " スタート！");
                        Thread.sleep((long)(Math.random() * 3000)); // 走行時間
                        System.out.println("ランナー " + runnerId + " ゴール！");
                        
                        finishSignal.countDown(); // ゴールしたことを通知
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            Thread.sleep(1000);
            System.out.println("位置について... よーい...");
            Thread.sleep(1000);
            System.out.println("ドン！");
            startSignal.countDown(); // 全ランナーをスタートさせる
            
            finishSignal.await(); // 全ランナーのゴールを待つ
            System.out.println("レース終了！");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // CountDownLatchのデモ
        System.out.println("=== CountDownLatch デモ ===");
        RaceExample.demonstrate();
    }
}
```

### 16.11.4 CyclicBarrier - 循環バリア

```java
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class CyclicBarrierExample {
    
    public static void main(String[] args) {
        int workerCount = 3;
        
        // バリアアクション：全員が到達したときに実行される
        Runnable barrierAction = () -> 
            System.out.println("=== 全員が到達しました！次のフェーズへ ===");
        
        CyclicBarrier barrier = new CyclicBarrier(workerCount, barrierAction);
        
        // ワーカースレッド
        for (int i = 0; i < workerCount; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    for (int phase = 1; phase <= 3; phase++) {
                        // 各フェーズの作業
                        System.out.printf("Worker %d: フェーズ %d 開始%n", 
                            workerId, phase);
                        Thread.sleep((long)(Math.random() * 2000));
                        System.out.printf("Worker %d: フェーズ %d 完了%n", 
                            workerId, phase);
                        
                        // バリアで待機
                        barrier.await();
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
```

### 16.11.5 Exchanger - データ交換

```java
import java.util.concurrent.Exchanger;

public class ExchangerExample {
    
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();
        
        // プロデューサースレッド
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String data = "データ" + i;
                    System.out.println("Producer: " + data + " を送信");
                    String response = exchanger.exchange(data);
                    System.out.println("Producer: " + response + " を受信");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // コンシューマースレッド
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String received = exchanger.exchange("ACK" + i);
                    System.out.println("Consumer: " + received + " を受信");
                    System.out.println("Consumer: ACK" + i + " を送信");
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
    }
}
```

### 16.11.6 wait/notify を使った低レベル同期

```java
public class WaitNotifyExample {
    private static final Object lock = new Object();
    private static boolean dataReady = false;
    private static String sharedData = null;
    
    public static void main(String[] args) {
        // プロデューサー
        Thread producer = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Producer: データ生成中...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                sharedData = "重要なデータ";
                dataReady = true;
                System.out.println("Producer: データ生成完了、通知送信");
                lock.notify(); // 待機中のスレッドに通知
            }
        });
        
        // コンシューマー
        Thread consumer = new Thread(() -> {
            synchronized (lock) {
                while (!dataReady) {
                    System.out.println("Consumer: データ待機中...");
                    try {
                        lock.wait(); // データが準備されるまで待機
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("Consumer: データ受信: " + sharedData);
            }
        });
        
        consumer.start();
        producer.start();
    }
}
```

### 16.11.7 並行コレクションの性能比較

```java
import java.util.*;
import java.util.concurrent.*;

public class CollectionPerformanceComparison {
    
    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10;
        int operationsPerThread = 10000;
        
        // 1. 同期化されたHashMap
        Map<Integer, String> syncMap = Collections.synchronizedMap(new HashMap<>());
        long syncTime = measurePerformance(syncMap, threadCount, operationsPerThread);
        
        // 2. ConcurrentHashMap
        Map<Integer, String> concurrentMap = new ConcurrentHashMap<>();
        long concurrentTime = measurePerformance(concurrentMap, threadCount, 
            operationsPerThread);
        
        System.out.println("=== パフォーマンス比較 ===");
        System.out.println("SynchronizedMap: " + syncTime + "ms");
        System.out.println("ConcurrentHashMap: " + concurrentTime + "ms");
        System.out.println("性能向上: " + 
            String.format("%.2fx", (double)syncTime / concurrentTime));
    }
    
    private static long measurePerformance(Map<Integer, String> map, 
                                          int threadCount, 
                                          int operationsPerThread) 
                                          throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.execute(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    int key = threadId * operationsPerThread + j;
                    map.put(key, "value" + key);
                    map.get(key);
                }
                latch.countDown();
            });
        }
        
        latch.await();
        long endTime = System.currentTimeMillis();
        executor.shutdown();
        
        return endTime - startTime;
    }
}
```

## 16.12 まとめ

このセクションでは、並行コレクションと高度な同期プリミティブについて学習しました：

- **並行コレクション**：ConcurrentHashMap、CopyOnWriteArrayList、BlockingQueue
- **プロデューサー・コンシューマーパターン**：BlockingQueueを使った実装
- **高度な同期プリミティブ**：
  - ReadWriteLock：読み取り/書き込みの最適化
  - Semaphore：リソース数の制限
  - CountDownLatch：複数スレッドの同期
  - CyclicBarrier：循環的な同期ポイント
  - Exchanger：スレッド間のデータ交換
- **低レベル同期**：wait/notifyメカニズム

これらのツールを適切に使い分けることで、効率的で安全な並行処理プログラムを作成できます。次のパートでは、実践的な並行処理のパターンと落とし穴について学習します。



次のパート：[Part D - 実践的な並行処理](chapter16d-practical-concurrency.md)




<!-- Merged from chapter16d-practical-concurrency.md -->

## 16.13 実践的なパフォーマンス測定とベンチマーク

```java
import java.util.concurrent.*;
import java.util.Arrays;
import java.util.Random;

public class ParallelPerformanceExample {
    
    // 大規模配列の並列ソート
    public static void parallelSortBenchmark() {
        int size = 10_000_000;
        int[] array1 = new int[size];
        int[] array2 = new int[size];
        Random random = new Random();
        
        // ランダムデータの生成
        for (int i = 0; i < size; i++) {
            int value = random.nextInt();
            array1[i] = value;
            array2[i] = value;
        }
        
        // シングルスレッドソート
        long startTime = System.currentTimeMillis();
        Arrays.sort(array1);
        long singleThreadTime = System.currentTimeMillis() - startTime;
        
        // 並列ソート
        startTime = System.currentTimeMillis();
        Arrays.parallelSort(array2);
        long parallelTime = System.currentTimeMillis() - startTime;
        
        System.out.println("=== ソートベンチマーク ===");
        System.out.println("配列サイズ: " + size);
        System.out.println("シングルスレッド: " + singleThreadTime + "ms");
        System.out.println("並列処理: " + parallelTime + "ms");
        System.out.println("性能向上: " + 
            String.format("%.2fx", (double)singleThreadTime / parallelTime));
    }
    
    // 並列ストリーム処理
    public static void parallelStreamExample() {
        int size = 50_000_000;
        long[] numbers = new long[size];
        Arrays.fill(numbers, 1);
        
        // シーケンシャル処理
        long startTime = System.currentTimeMillis();
        long sum1 = Arrays.stream(numbers).sum();
        long sequentialTime = System.currentTimeMillis() - startTime;
        
        // 並列処理
        startTime = System.currentTimeMillis();
        long sum2 = Arrays.stream(numbers).parallel().sum();
        long parallelTime = System.currentTimeMillis() - startTime;
        
        System.out.println("\n=== ストリーム処理ベンチマーク ===");
        System.out.println("要素数: " + size);
        System.out.println("シーケンシャル: " + sequentialTime + "ms");
        System.out.println("並列: " + parallelTime + "ms");
        System.out.println("性能向上: " + 
            String.format("%.2fx", (double)sequentialTime / parallelTime));
    }
    
    // ForkJoinPoolを使った再帰的並列処理
    static class RecursiveSum extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;
        private final long[] array;
        private final int start, end;
        
        public RecursiveSum(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                // 小さなタスクは直接計算
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            } else {
                // 大きなタスクは分割
                int mid = (start + end) / 2;
                RecursiveSum left = new RecursiveSum(array, start, mid);
                RecursiveSum right = new RecursiveSum(array, mid, end);
                
                left.fork(); // 左側を非同期実行
                long rightResult = right.compute(); // 右側を現在のスレッドで実行
                long leftResult = left.join(); // 左側の結果を待つ
                
                return leftResult + rightResult;
            }
        }
    }
    
    public static void forkJoinExample() {
        int size = 100_000_000;
        long[] array = new long[size];
        Arrays.fill(array, 1);
        
        ForkJoinPool pool = new ForkJoinPool();
        
        long startTime = System.currentTimeMillis();
        Long result = pool.invoke(new RecursiveSum(array, 0, size));
        long time = System.currentTimeMillis() - startTime;
        
        System.out.println("\n=== ForkJoinPool ベンチマーク ===");
        System.out.println("要素数: " + size);
        System.out.println("計算結果: " + result);
        System.out.println("処理時間: " + time + "ms");
        System.out.println("使用スレッド数: " + pool.getParallelism());
    }
    
    public static void main(String[] args) {
        // CPUコア数の確認
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("利用可能なCPUコア数: " + processors);
        
        parallelSortBenchmark();
        parallelStreamExample();
        forkJoinExample();
    }
}
```

## 16.14 スレッドセーフなシングルトンパターン

```java
public class ThreadSafeSingletonExamples {
    
    // 1. 同期化メソッドを使った実装（シンプルだが遅い）
    static class SynchronizedSingleton {
        private static SynchronizedSingleton instance;
        
        private SynchronizedSingleton() {}
        
        public static synchronized SynchronizedSingleton getInstance() {
            if (instance == null) {
                instance = new SynchronizedSingleton();
            }
            return instance;
        }
    }
    
    // 2. ダブルチェックロッキング（高速だが複雑）
    static class DoubleCheckedSingleton {
        private static volatile DoubleCheckedSingleton instance;
        
        private DoubleCheckedSingleton() {}
        
        public static DoubleCheckedSingleton getInstance() {
            if (instance == null) {
                synchronized (DoubleCheckedSingleton.class) {
                    if (instance == null) {
                        instance = new DoubleCheckedSingleton();
                    }
                }
            }
            return instance;
        }
    }
    
    // 3. 静的内部クラスホルダー（推奨・遅延初期化）
    static class HolderSingleton {
        private HolderSingleton() {}
        
        private static class Holder {
            private static final HolderSingleton INSTANCE = new HolderSingleton();
        }
        
        public static HolderSingleton getInstance() {
            return Holder.INSTANCE;
        }
    }
    
    // 4. Enumシングルトン（最も安全）
    enum EnumSingleton {
        INSTANCE;
        
        public void doSomething() {
            System.out.println("Enum singleton method");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // マルチスレッドでのシングルトンアクセステスト
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);
        
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                // 各実装のインスタンスを取得
                HolderSingleton singleton = HolderSingleton.getInstance();
                System.out.println(Thread.currentThread().getName() + 
                    ": " + singleton.hashCode());
                latch.countDown();
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // すべて同じハッシュコードが出力されることを確認
    }
}
```

## 16.15 一般的な並行処理の落とし穴と解決策

```java
import java.util.*;
import java.util.concurrent.*;

public class ConcurrencyPitfallsAndSolutions {
    
    // 落とし穴1: 不適切な同期によるデッドロック
    static class DeadlockExample {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();
        
        // デッドロックが発生する可能性のあるコード
        public void method1() {
            synchronized (lock1) {
                System.out.println("Method1: lock1を取得");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock2) {
                    System.out.println("Method1: lock2を取得");
                }
            }
        }
        
        public void method2() {
            synchronized (lock2) {
                System.out.println("Method2: lock2を取得");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock1) {
                    System.out.println("Method2: lock1を取得");
                }
            }
        }
        
        // 解決策: ロック順序の統一
        public void safeMethod1() {
            synchronized (lock1) {
                synchronized (lock2) {
                    System.out.println("Safe method1 実行");
                }
            }
        }
        
        public void safeMethod2() {
            synchronized (lock1) {  // 同じ順序でロック
                synchronized (lock2) {
                    System.out.println("Safe method2 実行");
                }
            }
        }
    }
    
    // 落とし穴2: 不適切なvolatileの使用
    static class VolatileMisuse {
        // 誤り: volatileは複合操作をアトミックにしない
        private volatile int counter = 0;
        
        public void incorrectIncrement() {
            counter++; // これはアトミックではない！
        }
        
        // 解決策: AtomicIntegerを使用
        private final AtomicInteger atomicCounter = new AtomicInteger(0);
        
        public void correctIncrement() {
            atomicCounter.incrementAndGet();
        }
    }
    
    // 落とし穴3: スレッドセーフでないコレクションの誤用
    static class CollectionMisuse {
        // 誤り: 通常のHashMapは並行アクセスに対して安全でない
        private final Map<String, Integer> unsafeMap = new HashMap<>();
        
        public void unsafeOperation() {
            // 複数スレッドから同時に呼ばれると問題発生
            unsafeMap.put("key", unsafeMap.getOrDefault("key", 0) + 1);
        }
        
        // 解決策1: ConcurrentHashMapを使用
        private final ConcurrentHashMap<String, Integer> safeMap = 
            new ConcurrentHashMap<>();
        
        public void safeOperation1() {
            safeMap.merge("key", 1, Integer::sum);
        }
        
        // 解決策2: 同期化
        private final Map<String, Integer> syncMap = new HashMap<>();
        
        public synchronized void safeOperation2() {
            syncMap.put("key", syncMap.getOrDefault("key", 0) + 1);
        }
    }
    
    // 落とし穴4: スレッドリーク
    static class ThreadLeakExample {
        // 誤り: ExecutorServiceを適切にシャットダウンしない
        public void causeThreadLeak() {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            executor.execute(() -> System.out.println("Task"));
            // executorをシャットダウンしないとスレッドがリーク！
        }
        
        // 解決策: try-finallyまたはtry-with-resourcesパターン
        public void preventThreadLeak() {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            try {
                executor.execute(() -> System.out.println("Task"));
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("並行処理の一般的な落とし穴の例");
        
        // デッドロックの回避例
        DeadlockExample example = new DeadlockExample();
        Thread t1 = new Thread(example::safeMethod1);
        Thread t2 = new Thread(example::safeMethod2);
        t1.start();
        t2.start();
        
        // アトミック操作の例
        VolatileMisuse volatileExample = new VolatileMisuse();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < 1000; i++) {
            executor.execute(volatileExample::correctIncrement);
        }
        
        executor.shutdown();
    }
}
```

## 16.16 実践的な並行処理パターン

### 16.16.1 並列データ処理パイプライン

```java
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class ParallelDataPipeline {
    
    // データ処理のステージ
    interface Stage<I, O> {
        O process(I input);
    }
    
    // 並列パイプライン
    static class Pipeline<T> {
        private final ExecutorService executor;
        private final List<Stage<?, ?>> stages;
        
        public Pipeline(int threadPoolSize) {
            this.executor = Executors.newFixedThreadPool(threadPoolSize);
            this.stages = new ArrayList<>();
        }
        
        public <O> Pipeline<O> addStage(Stage<T, O> stage) {
            stages.add(stage);
            return (Pipeline<O>) this;
        }
        
        public CompletableFuture<T> process(T input) {
            CompletableFuture<Object> result = CompletableFuture.completedFuture(input);
            
            for (Stage<?, ?> stage : stages) {
                result = result.thenApplyAsync(data -> 
                    ((Stage<Object, Object>) stage).process(data), executor);
            }
            
            return (CompletableFuture<T>) result;
        }
        
        public void shutdown() {
            executor.shutdown();
        }
    }
    
    public static void main(String[] args) throws Exception {
        // データ処理パイプラインの例
        Pipeline<String> pipeline = new Pipeline<>(4);
        
        // ステージ1: データ取得
        Stage<String, String> fetchStage = url -> {
            System.out.println("Fetching data from: " + url);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Raw data from " + url;
        };
        
        // ステージ2: データ変換
        Stage<String, String> transformStage = data -> {
            System.out.println("Transforming: " + data);
            return data.toUpperCase();
        };
        
        // ステージ3: データ保存
        Stage<String, String> saveStage = data -> {
            System.out.println("Saving: " + data);
            return "Saved: " + data;
        };
        
        // パイプライン構築
        CompletableFuture<String> future = pipeline
            .addStage(fetchStage)
            .addStage(transformStage)
            .addStage(saveStage)
            .process("http://example.com");
        
        // 結果取得
        String result = future.get();
        System.out.println("Pipeline result: " + result);
        
        pipeline.shutdown();
    }
}
```

### 16.16.2 リトライとサーキットブレーカーパターン

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ResiliencePatterns {
    
    // リトライパターン
    static class RetryExecutor {
        private final int maxRetries;
        private final long retryDelay;
        
        public RetryExecutor(int maxRetries, long retryDelay) {
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
        }
        
        public <T> CompletableFuture<T> executeWithRetry(Callable<T> task) {
            return executeWithRetry(task, 0);
        }
        
        private <T> CompletableFuture<T> executeWithRetry(Callable<T> task, int attempt) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return task.call();
                } catch (Exception e) {
                    if (attempt < maxRetries) {
                        System.out.println("Retry attempt " + (attempt + 1) + 
                            " after " + retryDelay + "ms");
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        return executeWithRetry(task, attempt + 1).join();
                    }
                    throw new CompletionException(e);
                }
            });
        }
    }
    
    // サーキットブレーカーパターン
    static class CircuitBreaker {
        enum State { CLOSED, OPEN, HALF_OPEN }
        
        private final AtomicReference<State> state = 
            new AtomicReference<>(State.CLOSED);
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicLong lastFailureTime = new AtomicLong(0);
        
        private final int failureThreshold;
        private final long timeout;
        
        public CircuitBreaker(int failureThreshold, long timeout) {
            this.failureThreshold = failureThreshold;
            this.timeout = timeout;
        }
        
        public <T> T execute(Callable<T> task) throws Exception {
            State currentState = state.get();
            
            if (currentState == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime.get() > timeout) {
                    state.set(State.HALF_OPEN);
                    System.out.println("Circuit breaker: HALF_OPEN");
                } else {
                    throw new RuntimeException("Circuit breaker is OPEN");
                }
            }
            
            try {
                T result = task.call();
                if (currentState == State.HALF_OPEN) {
                    state.set(State.CLOSED);
                    failureCount.set(0);
                    System.out.println("Circuit breaker: CLOSED");
                }
                return result;
            } catch (Exception e) {
                failureCount.incrementAndGet();
                lastFailureTime.set(System.currentTimeMillis());
                
                if (failureCount.get() >= failureThreshold) {
                    state.set(State.OPEN);
                    System.out.println("Circuit breaker: OPEN");
                }
                throw e;
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        // リトライパターンの例
        RetryExecutor retryExecutor = new RetryExecutor(3, 1000);
        
        CompletableFuture<String> retryFuture = retryExecutor.executeWithRetry(() -> {
            if (Math.random() < 0.7) {
                throw new RuntimeException("Random failure");
            }
            return "Success!";
        });
        
        try {
            System.out.println("Retry result: " + retryFuture.get());
        } catch (Exception e) {
            System.out.println("Failed after retries: " + e.getMessage());
        }
        
        // サーキットブレーカーの例
        CircuitBreaker circuitBreaker = new CircuitBreaker(3, 5000);
        
        for (int i = 0; i < 10; i++) {
            try {
                String result = circuitBreaker.execute(() -> {
                    if (Math.random() < 0.6) {
                        throw new RuntimeException("Service failure");
                    }
                    return "Service response";
                });
                System.out.println("Request " + i + ": " + result);
            } catch (Exception e) {
                System.out.println("Request " + i + " failed: " + e.getMessage());
            }
            
            Thread.sleep(500);
        }
    }
}
```

## 16.17 本章のまとめ

本章では、マルチスレッドプログラミングの基礎から実践的な並行処理まで体系的に学習しました：

- **パフォーマンス測定**：並列処理による性能向上の測定方法
- **ForkJoinPool**：分割統治法による並列処理
- **並列ストリーム**：Java 8以降の並列処理API
- **スレッドセーフなパターン**：シングルトンパターンの実装方法
- **一般的な落とし穴**：
  - デッドロック
  - 不適切なvolatileの使用
  - スレッドセーフでないコレクション
  - スレッドリーク
- **実践的なパターン**：
  - 並列データ処理パイプライン
  - リトライパターン
  - サーキットブレーカーパターン

これらの知識を活用することで、安全で効率的な並行処理プログラムを設計・実装できます。



次のパート：[Part E - 章末演習](chapter16e-exercises.md)



<!-- Merged from chapter16e-exercises.md -->

## 16.18 章末演習

本章で学んだマルチスレッドプログラミングの概念を活用して、実践的な練習課題に取り組みましょう。

### 16.18.1 演習の目標
マルチスレッドプログラミングの基礎から高度な並行処理までを習得します。



## 16.19 基礎レベル課題（必須）

### 16.19.1 課題1: 基本的なスレッド操作

基本的なスレッド生成と操作を実装してください。

**技術的背景：**
マルチスレッドプログラミングは、現代のソフトウェア開発において重要な技術です。CPUのマルチコア化に伴い、並列処理を効果的に活用することで、プログラムのパフォーマンスを大幅に向上させることができます。Javaでは、ThreadクラスとRunnableインターフェイスを使用して、スレッドを生成・管理します。これらの基本的な仕組みを理解することで、より高度な並行処理フレームワーク（ExecutorやForkJoinPool）の理解にもつながります。

**要求仕様：**
- Threadクラスの継承によるスレッド作成
- Runnableインターフェイスの実装によるスレッド作成
- スレッドの開始、停止、待機
- スレッド間の実行順序の確認
- スレッドの優先度設定

**実行例：**
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

**評価ポイント：**
- スレッド作成方法の理解
- 並行実行の概念理解
- スレッドライフサイクルの理解

**実装ヒント：**
- Thread.start（）でスレッド開始（run() 直接呼び出しは×）
- Thread.join() で他スレッドの完了待機
- Thread.sleep() で一時停止



### 16.19.2 課題2: 同期処理とデータ競合対策

同期処理を実装し、データ競合を防ぐ方法を理解してください。

**技術的背景：**
複数のスレッドが同じデータにアクセスする場合、適切な同期処理を行わないとデータ競合（race condition）が発生します。これはプログラムの動作を予測不可能にし、重大なバグの原因となります。Javaでは、synchronizedキーワード、Lockインターフェイス、volatileキーワードなど、様々な同期メカニズムを提供しています。これらを適切に使い分けることで、スレッドセーフなプログラムを実装できます。

**要求仕様：**
- synchronizedキーワードによる排他制御
- Lockインターフェイスを使った明示的なロック
- volatileキーワードの使用
- スレッドセーフなデータ構造の実装
- デッドロック回避策

**実行例：**
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



### 16.19.3 課題3: 並行コレクションとExecutor

並行処理専用のコレクションとExecutorServiceを活用してください。

**技術的背景：**
Javaの並行コレクション（java.util.concurrent パッケージ）は、マルチスレッド環境での安全な操作を保証します。ConcurrentHashMapはロックストライピング技術により高い並行性を実現し、CopyOnWriteArrayListは読み取り頻度が高い場合に最適です。ExecutorServiceフレームワークは、スレッドの生成・管理の複雑さを隠蔽し、効率的なスレッドプール管理を提供します。これらの技術により、スケーラブルで高性能な並行処理アプリケーションの開発が可能になります。

**要求仕様：**
- ConcurrentHashMap、CopyOnWriteArrayList等の使用
- ExecutorServiceによるスレッドプール管理
- FutureとCallableによる結果取得
- CompletableFutureによる非同期処理
- 並行処理パターンの実装

**実行例：**
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



### 16.19.4 課題4: 生産者・消費者パターン

生産者・消費者パターンを実装し、スレッド間通信を理解してください。

**技術的背景：**
生産者・消費者パターンは、並行プログラミングの基本的な設計パターンの一つです。このパターンは、データを生成するスレッド（生産者）とデータを処理するスレッド（消費者）を分離し、バッファを介して通信させます。これにより、生産と消費の速度差を吸収し、システム全体のスループットを向上させることができます。実際のシステムでは、ログ処理、メッセージキュー、イベント駆動アーキテクチャなどで広く使用されています。

**要求仕様：**
- BlockingQueueを使った生産者・消費者パターン
- wait/notifyを使った低レベル同期
- セマフォによるリソース制限
- スレッド間のデータ受け渡し
- 処理能力のバランス調整

**実行例：**
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

**評価ポイント：**
- 生産者・消費者パターンの理解
- 適切な同期プリミティブの選択
- スループット最適化の考慮

**実装ヒント：**
- ArrayBlockingQueueで固定サイズキュー
- put（）でブロッキング挿入、take() でブロッキング取得
- Semaphoreでリソース数制限



## 16.20 実装のヒント

### 16.20.1 マルチスレッドのポイント

1. **スレッド作成**: Thread継承vs Runnable実装
2. **同期制御**: synchronized vs Lockインターフェイス
3. **並行コレクション**: スレッドセーフなデータ構造
4. **ExecutorService**: スレッドプールによる効率的管理
5. **スレッド間通信**: BlockingQueue、wait/notify
6. **パフォーマンス**: 適切な並行度とオーバーヘッド考慮

### 16.20.2 よくある落とし穴
- run（）メソッドの直接呼び出し（start()を使う）
- 同期処理の範囲が広すぎる・狭すぎる
- デッドロックの発生（ロック順序に注意）
- リソースリーク（ExecutorServiceのshutdown忘れ）

### 16.20.3 設計のベストプラクティス
- 可能な限りimmuテーブルオブジェクトを使用
- 共有状態を最小限に抑制
- 並行コレクションの積極的活用
- 適切な粒度での同期処理設計



## 16.21 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter16/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── BasicThreading.java
│   ├── SynchronizedCounter.java
│   ├── ConcurrentCollections.java
│   └── ProducerConsumer.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```



## 16.22 完了確認チェックリスト

### 16.22.1 基礎レベル
- [ ] 基本的なスレッド操作ができている
- [ ] 同期処理とデータ競合対策が実装できている
- [ ] 並行コレクションとExecutorが活用できている
- [ ] 生産者・消費者パターンが実装できている

### 16.22.2 技術要素
- [ ] スレッドセーフなプログラムが書けている
- [ ] 適切な並行処理設計ができている
- [ ] パフォーマンスを考慮した実装ができている
- [ ] デッドロック等の問題を回避できている

### 16.22.3 応用レベル
- [ ] 高度な並行処理パターンを実装できている
- [ ] パフォーマンス測定と最適化ができている
- [ ] 実用的な並行アプリケーションが構築できている
- [ ] 並行処理のトラブルシューティングができている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度な並行プログラミングに挑戦しましょう！


