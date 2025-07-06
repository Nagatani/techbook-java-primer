# 第16章 マルチスレッドプログラミング

## 本章の学習目標

### 前提知識

本章を学習するためには、第13章までに習得した実用的なJavaアプリケーション開発能力が必須となります。特に、オブジェクト指向設計の実践的な理解、インターフェイスと実装の分離、例外処理やコレクションフレームワークの適切な使用など、堅牢なプログラムを作成する基本的な能力が求められます。また、同期処理と非同期処理の概念的な理解も重要です。単一のスレッドで順番に処理が実行される同期処理と、複数の処理が並行して実行される非同期処理の違いを理解し、なぜ非同期処理が必要なのかを認識していることが必要です。さらに、現代のソフトウェア開発においてパフォーマンスとスケーラビリティがなぜ重要なのかという問題意識を持っていることで、本章の内容をより深く理解できます。

システム理解の前提として、オペレーティングシステムにおけるプロセスとスレッドの基本概念を理解していることが望ましいです。プロセスが独立したメモリ空間を持つのに対し、スレッドは同一プロセス内でメモリを共有するという違いを理解することで、マルチスレッドプログラミングの利点と課題がより明確になります。また、CPUのコア数、キャッシュメモリ、メモリの可視性といったハードウェアアーキテクチャの基本的な理解があることで、並行処理で発生する問題の本質的な原因を理解しやすくなります。

### 学習目標

本章では、現代のJavaアプリケーション開発に不可欠なマルチスレッドプログラミングを体系的に学習します。知識理解の面では、まずマルチスレッドプログラミングがなぜ必要なのか、その本質的な価値を理解します。マルチコアCPUが標準となった現代において、単一スレッドでは活用できない計算資源を最大限に活用し、応答性の高いアプリケーションを構築するためには、並行処理が不可欠です。同時に、並行処理がもたらす新たな課題、特に競合状態（レースコンディション）やデッドロックといった問題の本質を理解し、これらを回避・解決するための理論的基盤を学びます。

Java 5で導入されたjava.util.concurrentパッケージの設計思想を理解することも重要な学習目標です。このパッケージは、Doug Leaらによって設計された、高性能かつ安全な並行処理のための包括的なフレームワークです。スレッドプールとExecutorServiceの概念を理解することで、スレッドの生成と破棄のオーバーヘッドを削減し、効率的なタスク管理を実現する方法を学びます。

技能習得の観点では、基本的なスレッドの生成と管理から始め、段階的に高度な並行処理技術を習得します。synchronizedキーワードによる排他制御の基本から、より柔軟なLockインターフェイス、さらに高性能なAtomic変数の使用まで、さまざまな同期化メカニズムを状況に応じて使い分けられるようになります。ExecutorServiceを使った効率的なタスク管理では、固定サイズのスレッドプール、キャッシュ型スレッドプール、スケジュール実行など、要件に応じた適切な実装を選択し、活用する能力を身につけます。

並行プログラミング能力の面では、単に動作するマルチスレッドプログラムを書くだけでなく、安全で効率的な並行処理プログラムを設計・実装する能力を養います。これには、不変オブジェクトの活用、スレッドセーフなデータ構造の選択、適切な粒度での同期化など、実践的な設計パターンの理解が含まれます。また、性能向上を目的とした並列化では、並列化のオーバーヘッドと性能向上のバランスを考慮し、実際に測定可能な改善を実現する実装技術を習得します。

最終的な到達レベルとして、基本的な並行処理プログラムを安全に実装できることから始まり、ExecutorServiceを使った実用的な非同期処理の実装、並行処理特有の問題の理解と適切な対策の実施、そしてマルチコア環境での性能向上を実現するプログラムの作成まで、段階的にスキルを向上させます。これにより、現代のソフトウェア開発で求められる高性能で応答性の高いアプリケーションを構築する能力を身につけることができます。

---

## 章の構成

本章は、マルチスレッドプログラミングを体系的に学習できるよう、以下のパートで構成されています：

### [Part A: マルチスレッドの基礎](chapter16a-thread-basics.md)
- マルチスレッドプログラミングの必要性
- スレッドの作成と実行
- 共有リソースと同期制御
- synchronizedキーワードによる排他制御

### [Part B: Executorフレームワーク](chapter16b-executor-framework.md)
- ExecutorServiceとスレッドプール
- FutureとCallableによる非同期処理
- CompleテーブルFutureによる高度な非同期処理

### [Part C: 並行コレクションと同期プリミティブ](chapter16c-concurrent-utilities.md)
- 並行コレクション（ConcurrentHashMap、BlockingQueue等）
- 高度な同期プリミティブ（Lock、Semaphore、CountDownLatch等）
- プロデューサー・コンシューマーパターン

### [Part D: 実践的な並行処理](chapter16d-practical-concurrency.md)
- パフォーマンス測定とベンチマーク
- スレッドセーフなパターン
- 並行処理の落とし穴と解決策
- ForkJoinPoolと並列ストリーム

### [Part E: 章末演習](chapter16e-exercises.md)
- 基礎レベル課題：スレッド操作と同期処理
- 実践レベル課題：並行コレクションとExecutor
- 応用レベル課題：高度な並行処理パターン

## 学習の進め方

1. Part Aでスレッドの基本概念と同期制御を理解
2. Part Bで実用的なExecutorフレームワークを習得
3. Part Cで並行処理専用のユーティリティを学習
4. Part Dで実践的な並行処理の設計と実装を習得
5. Part Eの演習課題で実践的なスキルを身につける

各パートは独立して読むことも可能ですが、順番に学習することで、基礎から実践まで体系的にマルチスレッドプログラミングを習得できるよう設計されています。

本章で学んだマルチスレッドプログラミングの概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
マルチスレッドプログラミングの基礎から高度な並行処理までを習得します。


---

## 基礎レベル課題（必須）

### 課題1: 基本的なスレッド操作

基本的なスレッド生成と操作を実装してください。

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

---

### 課題2: 同期処理とデータ競合対策

同期処理を実装し、データ競合を防ぐ方法を理解してください。

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


---

### 課題3: 並行コレクションとExecutor

並行処理専用のコレクションとExecutorServiceを活用してください。

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


---

### 課題4: 生産者・消費者パターン

生産者・消費者パターンを実装し、スレッド間通信を理解してください。

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

---

## 実装のヒント

### マルチスレッドのポイント

1. **スレッド作成**: Thread継承vs Runnable実装
2. **同期制御**: synchronized vs Lockインターフェイス
3. **並行コレクション**: スレッドセーフなデータ構造
4. **ExecutorService**: スレッドプールによる効率的管理
5. **スレッド間通信**: BlockingQueue、wait/notify
6. **パフォーマンス**: 適切な並行度とオーバーヘッド考慮

### よくある落とし穴
- run（）メソッドの直接呼び出し（start()を使う）
- 同期処理の範囲が広すぎる・狭すぎる
- デッドロックの発生（ロック順序に注意）
- リソースリーク（ExecutorServiceのshutdown忘れ）

### 設計のベストプラクティス
- 可能な限りimmuテーブルオブジェクトを使用
- 共有状態を最小限に抑制
- 並行コレクションの積極的活用
- 適切な粒度での同期処理設計

---

## 実装環境

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

---

## 完了確認チェックリスト

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

## 20.1 マルチスレッドプログラミングの基礎

スレッドとは、プログラム内での処理の流れを表す実行単位です。通常、1つのプログラムは1つのスレッド（メインスレッド）で実行されます。

**マルチスレッド**とは、1つのプログラム内で複数のスレッドを同時に実行し、処理を並行して進める技術です。

### なぜマルチスレッドが必要か

現代のコンピュータは、複数のコアを持つCPU（マルチコアプロセッサ）を搭載するのが一般的です。マルチスレッドプログラミングは、この複数のコアを効率的に活用し、アプリケーションのパフォーマンスを向上させるための重要な技術です。

-   **パフォーマンスの向上**: 時間のかかる処理を複数のスレッドに分割し、同時に実行することで、全体の処理時間を短縮できます。
-   **応答性の維持**: GUIアプリケーションなどで、時間のかかる処理をバックグラウンドのスレッドに任せることで、ユーザーインターフェイスが固まるのを防ぎます。
-   **リソースの有効活用**: サーバアプリケーションなどでは、複数のクライアントからのリクエストを同時に処理することで、計算リソースを有効に活用できます。

## 20.2 スレッドの作成と実行

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

## 20.3 共有リソースと同期制御

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

## 20.4 Executorフレームワークによる高度なスレッド管理

スレッドを直接生成・管理するのは複雑で、エラーも発生しやすいため、現代のJavaプログラミングでは**Executorフレームワーク**を使用するのが一般的です。これは、スレッドの生成・管理を抽象化し、**スレッドプール**を利用して効率的にタスクを実行するためのしくみです。

### `ExecutorService`とスレッドプール

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

### `Future`による非同期処理の結果取得

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

### 一般的な並行処理の落とし穴と解決策

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

## まとめ

-   **マルチスレッド**は、複数の処理を並行して実行し、アプリケーションのパフォーマンスや応答性を向上させる技術です。
-   タスクの定義には、柔軟性の高い`Runnable`インターフェイス（特にラムダ式）の使用が推奨されます。
-   複数のスレッドが共有リソースにアクセスする際は、`synchronized`などによる**同期制御**が必要です。
-   **Executorフレームワーク**とスレッドプールを利用することで、スレッド管理を安全かつ効率的に行うことができます。

マルチスレッドプログラミングは、現代のアプリケーション開発において不可欠なスキルの1つです。