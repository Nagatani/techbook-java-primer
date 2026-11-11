# <b>15章</b> <span>マルチスレッドプログラミング</span> <small>並行処理と同期制御の実践</small>

## 本章の学習目標

### この章で学ぶこと

1. マルチスレッドの基本
    - ThreadクラスとRunnableインターフェイスの使い分け
    - スレッドのライフサイクルと状態遷移
    - 競合状態とデッドロックの理解と回避
2. 同期制御の技術
    - synchronizedキーワードによる排他制御
    - LockインターフェイスとReentrantLockの活用
    - Atomic変数によるロックフリーな同期化
3. ExecutorServiceとスレッドプール
    - スレッドプールの種類と選択基準
    - タスクのスケジューリングと管理
    - FutureとCompletableFutureによる非同期処理
4. 並行コレクション
    - ConcurrentHashMap、CopyOnWriteArrayListなどの活用
    - BlockingQueueによるプロデューサー・コンシューマーパターン
    - パフォーマンスとスケーラビリティの最適化

### この章を始める前に

第13章までのJavaアプリケーション開発能力、とくに例外処理とコレクションフレームワークを理解していれば準備完了です。

最終的な到達レベルとして、基本的な並行処理プログラムを安全に実装できることから始まり、段階的にスキルを向上させます。
ExecutorServiceを使った実用的な非同期処理の実装、デッドロック回避やライブロック対策、マルチコア環境での2倍以上の性能向上を実現するプログラムの作成まで習得します。
これにより、高性能で応答性の高いアプリケーションを構築する能力を身につけます。

## 並行処理の世界への招待

### なぜマルチスレッドが必要なのか

これまでの章で学習してきたプログラムは、すべて「1つずつ順番に」処理を実行してきました。
しかし、現実のアプリケーションでは、複数の作業を同時に行いたい場面がよくあります。

たとえば、Webブラウザーを考えてみましょう。
- 画像をダウンロードしている
- HTMLを解析している
- JavaScriptを実行している
- ユーザーのクリックに反応している

これらすべてが「同時に」動いているように見えます。
もし1つずつ順番に処理していたら、画像のダウンロードが終わるまでページが表示されず、
とても使いづらいブラウザーになってしまいます。

### 身近な例で理解する並行処理

レストランの厨房を想像してください。

#### シングルスレッド（一人のシェフ）の場合

```
1. お客様Aの前菜を作る（5分）
2. お客様Aのメイン料理を作る（15分）
3. お客様Aのデザートを作る（5分）
4. お客様Bの前菜を作る（5分）... 
```
お客様Aの料理がすべて完成するまで25分、お客様Bは最低でも25分待つことになります。

#### マルチスレッド（複数のシェフ）の場合

前菜・メイン・デザートを別々のシェフが受け持ち、並行して作ります。複数のシェフが並行して働くことで、短時間で多くの料理を提供できます。次の図は、お客様AとBの料理を作り終えるまでの時間を比べたものです。

<div class="figure">

![シェフが1人の場合と3人の場合の時間の比較](images/diagrams/chapter15-chef-timeline.svg)

</div>

### Javaでの簡単な例

まず、シングルスレッドの例を見てみましょう。

以下のコードは、朝食の準備を順番に行うシングルスレッドのプログラムです。コーヒー（3秒）、トースト（2秒）、卵（5秒）を順番に処理するため、合計10秒かかります。各処理が完了するまで次の処理を開始できないため、待ち時間が無駄になっています。

<span class="listing-number">**サンプルコード15-1**　SingleThreadExample.java（1〜24、28、35〜36、42〜43行目）</span>

```java
public class SingleThreadExample {
    public static void main(String[] args) {
        IO.println("朝食の準備を開始");
        
        // コーヒーを淹れる（3秒かかる）
        makeCoffee();
        
        // トーストを焼く（2秒かかる）
        makeToast();
        
        // 卵を茹でる（5秒かかる）
        boilEgg();
        
        IO.println("朝食の準備完了！");
        // 合計: 10秒かかる
    }
    
    private static void makeCoffee() {
        IO.println("コーヒーを淹れています...");
        sleep(3000);
        IO.println("コーヒーができました");
    }
    
    private static void makeToast() {
    // ...
    }
    // ...
    
    private static void sleep(int milliseconds) {
    // ...
    }
}
```

実行結果：

```
朝食の準備を開始
コーヒーを淹れています...
コーヒーができました
トーストを焼いています...
トーストができました
卵を茹でています...
ゆで卵ができました
朝食の準備完了！
```

すべての処理が順番に実行されるため、完了までに約10秒かかります。

これをマルチスレッドで書き換えると、次のようになります。

<span class="listing-number">**サンプルコード15-2**</span>

```java
public class MultiThreadExample {
    public static void main(String[] args) throws InterruptedException {
        IO.println("朝食の準備を開始");
        
        // それぞれ別々のスレッドで同時に実行
        Thread coffeeThread = new Thread(() -> makeCoffee());
        Thread toastThread = new Thread(() -> makeToast());
        Thread eggThread = new Thread(() -> boilEgg());
        
        // スレッドを開始
        coffeeThread.start();
        toastThread.start();
        eggThread.start();
        
        // すべてのスレッドが終了するのを待つ
        coffeeThread.join();
        toastThread.join();
        eggThread.join();
        
        IO.println("朝食の準備完了！");
        // 合計: 約5秒（もっとも時間のかかる卵の時間）で完了
    }
    
    // makeCoffee、makeToast、boilEgg、sleepメソッドは同じ
}
```

実行結果：

```
朝食の準備を開始
コーヒーを淹れています...
卵を茹でています...
トーストを焼いています...
トーストができました
コーヒーができました
ゆで卵ができました
朝食の準備完了！
```

3つのスレッドは同時に動くため、「〜しています」と「〜ができました」の順序は実行のたびに変わることがあります。
このように、マルチスレッドを使うことで、10秒かかっていた処理を約5秒に短縮できました。

## 基礎レベル課題（ポイント）

### 課題1: 基本的なスレッド操作

基本的なスレッド生成と操作を実装してください。



<span class="listing-number">**サンプルコード15-3**</span>

```java
// ① Thread継承方式（非推奨）
class MyThread extends Thread {
    @Override
    public void run() {
        IO.println("Thread継承方式で実行");
    }
}

// ② Runnable実装方式（推奨）  
class MyTask implements Runnable {
    @Override
    public void run() {
        IO.println("Runnable実装方式で実行");
    }
}

// ③ ラムダ式方式（Java 8以降推奨）
// ラムダ式を使用することで、簡潔でわかりやすいスレッド作成が可能になります。
// この方式は一度だけ実行する短時間のタスクに最適で、無名クラスの冗長さを排除できます。
// 関数型プログラミングのパラダイムに従い、実行される処理（タスク）を明確に表現できます。
new Thread(() -> {
    IO.println("ラムダ式方式で実行");
}).start();
```

この演習では、マルチスレッドプログラミングの基礎を実践的に学びます。

#### 要求仕様
- Threadクラスの継承によるスレッド作成
- Runnableインターフェイスの実装によるスレッド作成
- スレッドの開始、停止、待機
- スレッド間の実行順序の確認
- スレッドの優先度設定

実行例。

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

評価ポイント。
- スレッド作成方法の理解
- 並行実行の概念理解
- スレッドライフサイクルの理解

実装ヒント。
- Thread.start（）でスレッド開始（run() 直接呼び出しは×）
- Thread.join() で他スレッドの完了待機
- Thread.sleep() で一時停止

### 課題2: 同期処理とデータ競合対策

同期処理を実装し、データ競合を防ぐ方法を理解してください。

マルチスレッド環境で複数のスレッドが共有リソースにアクセスすると、さまざまな問題が起こります。
その代表がデータ競合（Race Condition）です。

以下の例は、マルチスレッド環境でデータ競合（レースコンディション）が発生する典型的なパターンです。一見単純に見える`count++`操作が、実際にはCPUレベルで3つの独立した処理（①現在値の読み込み、②値のインクリメント、③結果の書き込み）に分解されます。複数のスレッドが同時に実行すると、期待と異なる結果になります。

<span class="listing-number">**サンプルコード15-4**　UnsafeCounter.java</span>

```java
class UnsafeCounter {
    private int count = 0;
    
    public void increment() {
        count++;  // ①
    }
}
```

①の処理内容`count++`は以下の3つの原子的でない操作で構成されています。
- CPU命令1: メモリからcountの現在値を読み込み  
- CPU命令2: レジスター内で値を1増加
- CPU命令3: 増加後の値をメモリに書き戻し

実際の実行結果では、10個のスレッドがそれぞれ1000回incrementを実行した場合、理論的には10,000になるはずですが、実際には8,000～9,800程度の値になってしまいます。これは、複数のスレッドが同じタイミングで同じ値を読み込み、同じ結果を書き込むことで、一部のインクリメント操作が失われるためです。

同期化メカニズムの比較。
- synchronized
    + シンプルだが粒度が粗い
- ReentrantLock
    + 柔軟だが複雑（tryLock、公平性制御）
- ReadWriteLock
    + 読み込み多数の場合に高性能
- Atomic変数
    + CAS（Compare-And-Swap）による高速化

<div class="figure">

![synchronizedによる排他制御メカニズム](images/diagrams/chapter15-synchronization.svg)

</div>

実世界での同期化の例。
- 銀行システム
    + 口座残高の一貫性保証
- 在庫管理
    + 同時注文による在庫のマイナス防止
- チケット予約
    + 座席の二重予約防止
- キャッシュ管理
    + データの整合性維持

デッドロックは、次の4つの条件（Coffman条件）がすべてそろったときに起こります。
1. 相互排他
    + 1つのリソースを同時に使えるのは1つのスレッドだけである
2. 保持と待機
    + リソースを保持したまま、ほかのリソースを待つ
3. 横取り不可
    + ほかのスレッドが保持するリソースを強制的に解放させられない
4. 循環待機
    + スレッドどうしの待ちが輪になる

同期化はデータを守る一方で、性能にも影響します。
いちばん大きな要因はロックの競合で、ロックを待つスレッドが増えるほどスループットが下がります。
待たされたスレッドを切り替えるコンテキストスイッチにも、1回あたり1〜10マイクロ秒ほどのオーバーヘッドがかかります。
さらに、ロックの取得と解放ではメモリバリアが働き、CPUキャッシュの内容を同期するコストが生じます。
こうした理由から、コア数を増やしても性能が比例して伸びるわけではなく、スケーラビリティには限界があります。

この演習では、さまざまな同期化技術を実践し、パフォーマンス要件に応じた選択ができる能力を養います。

#### 要求仕様
- synchronizedキーワードによる排他制御
- Lockインターフェイスを使った明示的なロック
- volatileキーワードの使用
- スレッドセーフなデータ構造の実装
- デッドロック回避策

実行例。

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

### 課題3: 並行コレクションとExecutor

並行処理専用のコレクションとExecutorServiceを活用してください。

Executorフレームワークは、Doug Leaが設計した並行処理のフレームワークで、スレッドプールとタスクの管理を受け持ちます。
まず、これを使わずにスレッドを生成するとどのような問題が起こるかを見ます。

以下は、マルチスレッドプログラミングでよく見られる危険なアンチパターンです。大量のタスクに対して無制限にスレッドを生成することで、深刻なパフォーマンス問題とシステムクラッシュを引き起こす可能性があります。

<span class="listing-number">**サンプルコード15-5**</span>

```java
// アンチパターン：スレッドの無制限生成
for (int i = 0; i < 10000; i++) {
    new Thread(() -> processTask()).start();  // ①
}
```

#### ①のコードが引き起こす問題

①のコードは、タスクの数だけ、つまり10,000個のスレッドを作ります。
各スレッドは、スタック用のメモリ領域（64ビット環境の既定では1MB程度）を確保します。
10,000スレッドでは約10GBの領域を確保することになり、OSの上限に達すると、スレッドを作れずに`OutOfMemoryError`が発生します。
メモリが足りたとしても、OSはこれだけのスレッドを切り替えなければなりません。
コンテキストスイッチは1回あたり約1〜10マイクロ秒かかるので、スレッドの数だけこのオーバーヘッドが積み重なります。
その結果、CPUは実際の作業よりもスレッドの管理に時間を使うようになり、最後にはOSのスケジューラーが正常に動作できなくなって、システム全体が不安定になります。

#### Executorの利点

Executorを使うと、これらの問題を避けられます。
Executorはスレッドプールのスレッドを再利用するので、スレッドを作っては捨てるオーバーヘッドがかかりません。
処理しきれないタスクはタスクキューで待たせるため、タスクが一度に押し寄せてもスレッドが増え続けることはありません（バックプレッシャーの制御）。
また、実行状況の統計情報を取れるので、監視やチューニングに役立ちます。
終了するときは`shutdown()`を呼べば、投入済みのタスクを終えてから安全に停止します。

#### 並行コレクションの必要性とスレッドセーフティ

通常のコレクションクラスは、マルチスレッド環境では致命的な問題を引き起こす可能性があります。以下の例では、スレッドセーフでないHashMapと、並行アクセスに対応したConcurrentHashMapの違いを示しています。

<span class="listing-number">**サンプルコード15-6**</span>

```java
// 危険：通常のHashMapの並行アクセス
Map<String, Integer> unsafeMap = new HashMap<>();  // ①

// 安全：ConcurrentHashMapの使用
Map<String, Integer> safeMap = new ConcurrentHashMap<>();  // ②
```

①の`HashMap`に複数のスレッドが同時に書き込むと、内部のデータ構造が同時に変更されて不正な状態になり、データが壊れます。
Java 7以前の`HashMap`では、内部の配列を広げる処理が同時に走ると内部のリンクリストが循環構造になり、`get`操作が永久に終わらない無限ループに陥ることもありました。
また、実行時に`ConcurrentModificationException`が発生することもあります。

②の`ConcurrentHashMap`は、マップ全体を1つのロックで守るのではなく、書き込む位置（バケット）ごとに排他制御をします。
そのため、異なる位置への書き込みは並行して進みます。
読み取り操作はロックを取得しないので、読み取りが書き込みを待たされることもありません。
さらに、`putIfAbsent`、`replace`、`computeIfAbsent`のように、確認と更新を組み合わせた操作を1つのアトミックな操作として安全に実行できます。

##### 実際のシステムでの使用例
- Webサーバー
    + リクエスト処理用スレッドプール
- データベース接続プール
    + コネクション管理
- メッセージキュー
    + 非同期処理の実装
- バッチ処理
    + 並列データ処理

##### ExecutorServiceの種類と用途
- FixedThreadPool
    + CPU集約的タスク（スレッド数はCPUのコア数程度）
- CachedThreadPool
    + I/O集約的タスク（動的拡張）
- ScheduledThreadPool
    + 定期実行タスク
- ForkJoinPool
    + 分割統治アルゴリズム

##### パフォーマンスの考慮

スレッドプールを設計するときは、まずスレッド数を決めます。
スレッド数の目安は、リトルの法則（Little's Law）の式N = λ × W（λは単位時間あたりに届くタスクの数、Wは1つのタスクにかかる時間）で見積もれます。
次にキューの大きさを決めます。
キューを大きくすると多くのタスクを待たせられますが、その分メモリを使い、待ち時間（レイテンシ）も長くなります。
最後に、キューがあふれたときの扱いを拒否ポリシー（`CallerRunsPolicy`、`AbortPolicy`など）で決めます。

この演習では、プロダクションレベルの並行処理設計を学びます。

#### 要求仕様
- ConcurrentHashMap、CopyOnWriteArrayList等の使用
- ExecutorServiceによるスレッドプール管理
- FutureとCallableによる結果取得
- CompletableFutureによる非同期処理
- 並行処理パターンの実装

##### 実行例

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

### 課題4: プロデューサー・コンシューマーパターン

プロデューサー・コンシューマーパターンを実装し、スレッド間通信を理解してください。

#### 技術的背景

プロデューサー・コンシューマーパターンは、スレッドどうしが協調して働くための、並行処理の古典的かつもっとも重要なデザインパターンの1つです。

データを作る側（生産者）と使う側（消費者）の速度は、たいてい一致しません。
このパターンでは、両者の間にバッファーを置いて速度の差を吸収します。
バッファーがあるので、一時的にデータが急増しても受け止められます。
また、生産者と消費者はバッファーを介してやり取りするだけなので、互いに独立して作れます（デカップリング）。
そのため、負荷に応じて生産者や消費者の数を増減でき、スケーラビリティも高くなります。

##### 実世界での適用例

プロデューサー・コンシューマーパターンは、多くの実際のシステムで活用されています。以下に代表的な適用例を示します。

多数のアプリケーションが生成するログメッセージを遅延なく処理するログ処理システムでは、プロデューサー・コンシューマーパターンが有効です。
①アプリケーション（生産者）がログメッセージを生成し、
②BlockingQueue（バッファー）がメッセージを一時的に蓄積します。
③ログライター（消費者）がファイルやデータベースに書き込みます。
これにより、アプリケーションの処理速度とディスクI/Oの速度差を吸収できます。

動画エンコーディングシステムでは、大容量の動画ファイルを短時間で処理するために、次の構成を使います。
①動画読み込み部（生産者）がフレームを抽出し、②フレームキュー（バッファー）がメモリ使用量を制御しながらフレームを蓄積します。
③エンコーダー（消費者）が圧縮処理を実行します。
これにより、I/O処理とCPU集約的な処理を分離し、マルチコアCPUの性能を最大限に活用できます。

<span class="listing-number">**サンプルコード15-7**</span>

```java
// システム構成の概念図
// Producer → [BlockingQueue] → Consumer
//    ↓            ↓              ↓
// データ生成 → バッファリング → データ処理
```

##### BlockingQueueの種類と特性
- ArrayBlockingQueue
    + 固定サイズ、公平性オプション
- LinkedBlockingQueue
    + オプショナル容量、高スループット
- PriorityBlockingQueue
    + 優先度付きタスク処理
- SynchronousQueue
    + 容量0、直接受け渡し

##### 古典的wait/notifyの問題点と正しい実装パターン

wait/notifyは、Javaの初期から存在するスレッド間通信のメカニズムです。1つのスレッドが特定の条件を待ち（wait）、別のスレッドがその条件を満たしたら通知（notify）するという仕組みです。しかし、このメカニズムは多くの微妙な問題を含んでおり、正しく使用するには注意が重要です。

##### wait/notifyの基本的な仕組み
- `wait()`: 現在のスレッドを待機状態にし、ロックを解放する
- `notify()`: 待機中のスレッドを1つ起こす
- `notifyAll()`: 待機中のすべてのスレッドを起こす

以下の例では、よくある間違った実装と正しい実装パターンを比較します。

<span class="listing-number">**サンプルコード15-8**</span>

```java
// 誤った実装例（危険）
synchronized(lock) {
    if (!condition) {  // ①
        lock.wait();
    }
}

// 正しい実装例（推奨）
synchronized(lock) {
    while (!condition) {  // ②
        lock.wait();
    }
}
```

①のように`if`文で条件を調べると、`wait()`から復帰したあとに条件をもう一度確かめません。
ところが、スレッドは通知を受けていないのに`wait()`から復帰することがあります。
これを偽の起床（Spurious Wakeup）と呼び、OSのレベルで起こります。
また、通知を受けて復帰した時点で、ほかのスレッドがすでに条件を変えている場合もあります。
どちらの場合も、①では条件が満たされていないまま処理を続けてしまいます。

②のように`while`文を使うと、`wait()`から復帰するたびに条件を評価し直します。
偽の起床が起きても、ほかのスレッドが先に条件を変えていても、条件が満たされるまで再び待機します。
そのため、複数のスレッドが同時に動く環境でも安全に動作します。

##### パフォーマンスチューニング

このパターンの性能は、キューの大きさと、生産者と消費者の数の比率で決まります。
キューの大きさは、課題3で見たように、メモリの使用量とレイテンシのバランスを見て決めます。
生産者と消費者の比率は、リトルの法則を使って見積もります。
スループットを上げたいときは、データを1件ずつではなくまとめて処理するバッチ処理が使えます。
また、消費が追いつかない過負荷のときに生産を抑えるバックプレッシャーのしくみも用意しておきます。

##### 実際のシステムでの問題

実際のシステムでは、容量に上限のないキューを使うと、消費が追いつかない間にデータがたまり続け、メモリが枯渇する危険があります。
消費者の処理能力が足りないと、キューにたまったデータがいつまでも処理されない飢餓状態になります。
そこまでいかなくても、キューで待つ時間が積み重なって、データが処理されるまでのレイテンシが長くなります。

この演習では、実践的なスレッド間協調メカニズムを習得します。

#### 要求仕様
- BlockingQueueを使ったプロデューサー・コンシューマーパターン
- wait/notifyを使った低レベル同期
- セマフォによるリソース制限
- スレッド間のデータ受け渡し
- 処理能力のバランス調整

##### 実行例

```
=== プロデューサー・コンシューマーパターン ===
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

評価ポイント。
- プロデューサー・コンシューマーパターンの理解
- 用途に応じた同期プリミティブの選択
- スループット最適化の考慮

実装ヒント。
- ArrayBlockingQueueで固定サイズキュー
- put（）でブロッキング挿入、take() でブロッキング取得
- Semaphoreでリソース数制限

## 実装のヒント

### マルチスレッドのポイント

1. スレッド作成
    + Thread継承vs Runnable実装
2. 同期制御
    + synchronized vs Lockインターフェイス
3. 並行コレクション
    + スレッドセーフなデータ構造
4. ExecutorService
    + スレッドプールによるリソースの共有
5. スレッド間通信
    + BlockingQueue、wait/notify
6. パフォーマンス
    + 最適な並行度とオーバーヘッド考慮

### よくある落とし穴
- run（）メソッドの直接呼び出し（start()を使う）
- 同期処理の範囲が広すぎる・狭すぎる
- デッドロックの発生（ロック順序に注意）
- リソースリーク（ExecutorServiceのshutdown忘れ）

### 設計のベストプラクティス
- 可能な限りイミュータブルオブジェクトを使用
- 共有状態を最小限に抑制
- 並行コレクションの積極的活用
- ブロック単位やメソッド単位での必要最小限の同期処理設計

## 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください。

```
exercises/chapter15/
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

## 完了確認チェックリスト

### 基礎レベル
- 基本的なスレッド操作ができている
- 同期処理とデータ競合対策が実装できている
- 並行コレクションとExecutorが活用できている
- プロデューサー・コンシューマーパターンが実装できている

### 技術要素
- スレッドセーフなプログラムが書けている
- 効果的な並行処理設計ができている
- パフォーマンスを考慮した実装ができている
- デッドロック等の問題を回避できている

### 応用レベル
- 高度な並行処理パターンを実装できている
- パフォーマンス測定と最適化ができている
- 実用的な並行アプリケーションが構築できている
- 並行処理のトラブルシューティングができている

次のステップ： 基本課題が完了したら、advanced/の発展課題でより高度な並行プログラミングに挑戦しましょう。

## マルチスレッドプログラミングの基礎

スレッドとは、プログラム内での処理の流れを表す実行単位です。
通常、1つのプログラムは1つのスレッド（メインスレッド）で実行されます。

マルチスレッドとは、1つのプログラム内で複数のスレッドを同時に実行し、処理を並行して進める技術です。

### なぜマルチスレッドが必要か

「並行処理の世界への招待」では、朝食の準備を3つのスレッドに分けて、全体の時間を短くしました。
ここでは、マルチスレッドが役立つ場面を整理します。

現代のコンピューターは、複数のコアを持つCPU（マルチコアプロセッサー）を搭載するのが一般的です。
時間のかかる処理を複数のスレッドに分割すれば、複数のコアで同時に実行して、全体の処理時間を短縮できます。
GUIアプリケーションでは、時間のかかる処理をバックグラウンドのスレッドに任せることで、ユーザーインターフェイスが固まるのを防げます。
サーバーアプリケーションでは、複数のクライアントからのリクエストを同時に処理することで、計算リソースを無駄なく使えます。

### スレッドの作成と実行

Javaでスレッドを作成するには、主に`Runnable`インターフェイスを実装する方法が推奨されます。

スレッドは生成されてから終了するまでに、次の図のような状態を遷移します。

<div class="figure">

![スレッドのライフサイクルと状態遷移](images/diagrams/chapter15-thread-lifecycle.svg)

</div>

### `Runnable`インターフェイスの実装（推奨）

`Runnable`は、スレッドが実行するタスク（処理内容）を定義するための関数型インターフェイスです。`run()`メソッドを1つだけ持ちます。

<span class="listing-number">**サンプルコード15-9**　Main.java</span>

```java
// Runnableを実装したクラス
class MyTask implements Runnable {
    @Override
    public void run() {
        IO.println("タスク実行中: " + Thread.currentThread().getName());
    }
}

public class Main {
    public static void main(String[] args) {
        MyTask task = new MyTask();
        Thread thread = new Thread(task);
        thread.start(); // スレッドを開始

        // ラムダ式を使えばさらに簡潔
        new Thread(() -> {
            IO.println("ラムダ式でのタスク実行: " + Thread.currentThread().getName());
        }).start();
    }
}
```

`Runnable`を利用する方法は、Javaがクラスの多重継承をサポートしないという制約を受けず、また「タスク（何をするか）」と「スレッド（どう実行するか）」を分離できるため、より柔軟な設計が可能です。

### 共有リソースと同期制御

複数のスレッドが、同じデータ（オブジェクトや変数）に同時にアクセスすると、予期せぬ問題が発生することがあります。これを競合状態 (Race Condition) と呼びます。

たとえば、銀行口座の残高を更新する処理を考えてみましょう。残高10,000円の口座から「2つのATMで同時に」8,000円を引き出そうとした場合を想定します。
同期制御がないと両方の引き出しが成功してしまい、引き出した金額と残高が合わなくなる可能性があります。これは、以下のような処理の流れで発生します。

1. ATM1が残高10,000円を読み取る
2. ATM2も残高10,000円を読み取る（まだATM1の処理は完了していない）
3. ATM1が「10,000円 - 8,000円 = 2,000円」と計算して更新
4. ATM2も「10,000円 - 8,000円 = 2,000円」と計算して更新

その結果、残高10,000円の口座から合計16,000円が引き出されてしまいます。

<div class="figure">

![同期制御がないときの競合状態](images/diagrams/chapter15-race-condition.svg)

</div>

### `synchronized`による排他制御

この問題を解決するために、Javaは`synchronized`キーワードによる排他制御のしくみを提供します。`synchronized`で保護されたコードブロックは、一度に1つのスレッドしか実行できないことが保証されます。

`synchronized`キーワードをメソッドに付けることで、そのメソッド全体が排他制御の対象となります。これにより、同時に1つのスレッドしかそのメソッドを実行できなくなり、データの整合性が保たれます。

<span class="listing-number">**サンプルコード15-10**　SynchronizedCounter.java</span>

```java
class SynchronizedCounter {
    private int count = 0;

    // synchronizedキーワードにより、このメソッドは同時に1つのスレッドしか実行できない
    // これにより、count++という複合操作（読み取り→インクリメント→書き込み）が
    // 原子的（アトミック）に実行されることが保証される
    public synchronized void increment() {
        count++;
    }

    // 読み取りメソッドも一貫性のため、synchronizedにすることが推奨される
    public synchronized int getCount() {
        return count;
    }
}
```

`synchronized`は非常に強力ですが、使用には注意が必要です。
ロックの範囲が広すぎるとパフォーマンスの低下を招きます。
また、複数のロックが絡み合うとデッドロック（スレッドがお互いを待ち続けて処理が進まなくなる状態）を引き起こす危険性もあります。

<div class="figure">

![デッドロック：2つのスレッドが互いのロックを待ち続ける](images/diagrams/chapter15-deadlock.svg)

</div>

デッドロックは、どのスレッドもロックを同じ順序で取得するように決めておけば防げます。
図の例なら、2つのスレッドがどちらもlock1を取ってからlock2を取るようにします。

メソッド全体ではなく、特定のコードブロックだけを同期化したい場合は、`synchronized`ブロックを使用します。これにより、必要最小限の範囲だけをロックすることができ、パフォーマンスの向上につながります。

<span class="listing-number">**サンプルコード15-11**　BankAccount.java</span>

```java
class BankAccount {
    private double balance;
    private final Object lock = new Object(); // ロックオブジェクト
    
    public void deposit(double amount) {
        // 検証処理は同期化の外で実行（パフォーマンス向上）
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の数である必要があります");
        }
        
        // 残高の更新部分だけを同期化
        synchronized (lock) {
            IO.println("入金前残高: " + balance);
            balance += amount;
            IO.println("入金後残高: " + balance);
        }
    }
    
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        
        synchronized (lock) {
            if (balance >= amount) {
                IO.println("引き出し前残高: " + balance);
                balance -= amount;
                IO.println("引き出し後残高: " + balance);
                return true;
            }
            return false;
        }
    }
}
```

### volatileキーワードによるメモリ可視性の保証

`volatile`キーワードは、あるスレッドが変数に書き込んだ値が、他のスレッドから即座に見えることを保証します（メモリの可視性）。
主な用途は、スレッド間でのフラグの共有です。
`volatile`は`synchronized`より軽量ですが、できることも限られます。
アトミックになるのは単純な読み書きだけで、読み取り→計算→書き込みのような複合操作は保証しません。

<span class="listing-number">**サンプルコード15-12**　VolatileDemo.java</span>

```java
class ThreadStopExample {
    // volatileフラグによる安全なスレッド停止
    private volatile boolean stopRequested = false;
    
    public void startWorker() {
        Thread worker = new Thread(() -> {
            int count = 0;
            // volatileフラグを定期的にチェック
            while (!stopRequested) {
                count++;
                if (count % 1000000 == 0) {
                    IO.println("Working... count: " + count);
                }
            }
            IO.println("Worker stopped at count: " + count);
        });
        worker.start();
    }
    
    public void stopWorker() {
        // volatileにより、この変更は即座に他のスレッドから見える
        stopRequested = true;
        IO.println("Stop requested");
    }
}

// 使用例
public class VolatileDemo {
    public static void main(String[] args) throws InterruptedException {
        ThreadStopExample example = new ThreadStopExample();
        example.startWorker();
        
        // 3秒間動作させる
        Thread.sleep(3000);
        
        // ワーカーを停止
        example.stopWorker();
    }
}
```

カウンターのインクリメント（`count++`）のような複合操作には`volatile`を使えません。そのような場合は、次に説明する`AtomicInteger`などのアトミッククラスを使用してください。

### Atomicクラスによるロックフリーなアトミック操作

`java.util.concurrent.atomic`パッケージは、ロックを使わずにアトミックな操作を実現するクラス群を提供します。これらは内部的にCAS（Compare-And-Swap）操作を使用し、高性能な並行処理を実現します。

主なAtomicクラス。
- `AtomicInteger`: int値のアトミック操作
- `AtomicLong`: long値のアトミック操作
- `AtomicBoolean`: boolean値のアトミック操作
- `AtomicReference<T>`: 参照型のアトミック操作

<span class="listing-number">**サンプルコード15-13**　AtomicCounterExample.java（6〜31、49行目）</span>

```java
public class AtomicCounterExample {
    // スレッドセーフなカウンター
    private final AtomicInteger counter = new AtomicInteger(0);
    
    public void performCounting() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        // 10スレッドが同時に1000回ずつインクリメント
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                for (int j = 0; j < 1000; j++) {
                    // アトミックなインクリメント操作
                    int newValue = counter.incrementAndGet();
                    if (newValue % 1000 == 0) {
                        IO.println("Counter reached: " + newValue);
                    }
                }
            });
        }
        
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        
        IO.println("Final counter value: " + counter.get());
        // 常に正確に10,000になる
    }
    // ...
}
```

Atomicクラスはsynchronizedよりもはるかに高速で、とくに読み取りが多い場合や競合が少ない場合に優れたパフォーマンスを発揮します。

### Executorフレームワークによる高度なスレッド管理

スレッドを直接生成・管理するのは複雑でエラーも発生しやすいため、現代のJavaプログラミングではExecutorフレームワークを使用するのが一般的です。これはスレッドの生成・管理を抽象化し、スレッドプールを利用してタスクを順次実行するためのしくみです。

#### ExecutorServiceとスレッドプール

`ExecutorService`は、タスクの投入とスレッドプールの管理を行うためのインターフェイスです。スレッドプールは、あらかじめ作成された再利用可能なスレッドの集まりです。

スレッドプールはスレッドを使い回すので、タスクごとにスレッドを生成・破棄するコストがかかりません。
作るスレッドの数に上限を設けられるため、スレッドが増えすぎてシステムが不安定になることも防げます。
スレッドが空いていないときに届いたタスクは、キューで実行を待ちます。
また、タスクの実行時に例外が発生しても、スレッドプールはそのまま動作を続けます。

<div class="figure">

![ExecutorServiceのタスクのキューとスレッドプール](images/diagrams/chapter15-executor.svg)

</div>

主なExecutorServiceの種類。
- `newFixedThreadPool(int)`: 固定数のスレッドを持つプール
- `newCachedThreadPool()`: I/O待機が多いタスク向けにスレッドを作成・再利用するプール
- `newSingleThreadExecutor()`: 単一スレッドで順次実行するプール
- `newScheduledThreadPool(int)`: スケジュール実行が可能なプール

<span class="listing-number">**サンプルコード15-14**　ExecutorExample.java</span>

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
                IO.println("Task " + taskId + " is being executed by " + 
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

#### プロデューサー・コンシューマーパターンの実装

プロデューサー・コンシューマーパターンは、並行処理における古典的かつ重要なデザインパターンです。
プロデューサー（生産者）がデータを生成し、コンシューマー（消費者）がそれを処理するという構造で、
両者の処理速度の違いをバッファー（キュー）で吸収します。
このパターンは、ログ処理、メッセージキュー、パイプライン処理など、多くの実用的なシステムで使用されています。

<span class="listing-number">**サンプルコード15-15**　ProducerConsumerExample.java（8〜18、27〜44、53〜80、87行目）</span>

```java
public class ProducerConsumerExample {
    // 共有キュー
    // BlockingQueueは、スレッド間でデータを安全に受け渡すための
    // 特殊なキューです。キューが満杯の時は生産者が自動的にブロックされ、
    // キューが空の時は消費者が自動的にブロックされます。
    private static final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);
    private static final AtomicInteger producedCount = new AtomicInteger(0);
    private static final AtomicInteger consumedCount = new AtomicInteger(0);
    
    // プロデューサー（生産者）
    static class Producer implements Runnable {
        // ...
        @Override
        public void run() {
            try {
                for (int i = 0; i < itemsToProduce; i++) {
                    int item = producedCount.incrementAndGet();
                    queue.put(item); // キューがいっぱいの場合はブロック
                    IO.println("%s produced: %d (queue size: %d)".formatted(
                        name, item, queue.size()));
                    Thread.sleep(100); // 生産に時間がかかることをシミュレート
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    // コンシューマー（消費者）
    static class Consumer implements Runnable {
        // ...
        @Override
        public void run() {
            try {
                for (int i = 0; i < itemsToConsume; i++) {
                    Integer item = queue.take(); // キューが空の場合はブロック
                    consumedCount.incrementAndGet();
                    IO.println("%s consumed: %d (queue size: %d)".formatted(
                        name, item, queue.size()));
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
        // ...
}
```

#### Future と Callable を使った非同期処理

`Callable`は`Runnable`と同じくタスクを定義するインターフェイスですが、戻り値のない`Runnable`と違って値を返せます。
また、`Callable`は検査例外をスローできます。
`Callable`のタスクを`ExecutorService`に投入すると、`Future`が返ります。
`Future`インターフェイスは非同期処理の結果を表すオブジェクトで、結果を保持し、処理の完了を待てます。
実行中のタスクをキャンセルすることもできます。

<span class="listing-number">**サンプルコード15-16**　FutureCallableExample.java（5、7〜8、15〜23、29〜60、61行目）</span>

```java
public class FutureCallableExample {
    // ...
    // 計算に時間がかかるタスク
    static class CalculationTask implements Callable<Long> {
        // ...
        @Override
        public Long call() throws Exception {
            IO.println("計算開始: フィボナッチ数列の第" + n + "項");
            long start = System.currentTimeMillis();
            long result = fibonacci(n);
            long time = System.currentTimeMillis() - start;
            IO.println("計算完了: F(%d) = %d (所要時間: %dms)".formatted(n, result, time));
            return result;
        }
        // ...
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
        IO.println("計算中に他の処理を実行...");
        Thread.sleep(100);
        
        // 結果を取得（必要になったタイミングで）
        IO.println("\n=== 計算結果 ===");
        for (int i = 0; i < futures.size(); i++) {
            try {
                // get()は結果が得られるまでブロック
                Long result = futures.get(i).get(10, TimeUnit.SECONDS);
                IO.println("F(" + numbers[i] + ") = " + result);
            } catch (TimeoutException e) {
                IO.println("F(" + numbers[i] + ") = タイムアウト");
                futures.get(i).cancel(true); // タスクをキャンセル
            }
        }
        
        executor.shutdown();
    }
}
```

#### CompletableFuture による高度な非同期処理

`CompletableFuture`は、Java 8で導入された強力な非同期処理フレームワークです。従来の`Future`の制限を克服し、関数型プログラミングスタイルで非同期処理を記述できます。

CompletableFutureの主な特徴。
- 非同期処理の連鎖
    + `thenApply`、`thenCompose`などで処理を連鎖
- 例外処理
    + `exceptionally`、`handle`で例外を優雅に処理
- 複数の非同期処理の組み合わせ
    + `allOf`、`anyOf`で複数の処理を統合
- タイムアウト処理
    + Java 9以降では`orTimeout`メソッドも利用可能

<span class="listing-number">**サンプルコード15-17**　CompletableFutureExample.java（6、9、12〜24、38、40、52〜75、91〜92行目）</span>

```java
public class CompletableFutureExample {
    // ...
    static class WeatherService {
        // ...
        public static CompletableFuture<String> getTemperature(String city) {
            return CompletableFuture.supplyAsync(() -> {
                IO.println("気温取得中: " + city);
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
        // ...
    }
    // ...
    public static void main(String[] args) throws Exception {
        // ...
        // 2. チェーン処理
        CompletableFuture<String> chainedFuture = future1
            .thenApply(result -> result + " -> 変換処理")
            .thenApply(result -> result + " -> 最終処理");
        
        IO.println("チェーン結果: " + chainedFuture.get());
        
        // 3. 複数の非同期処理を組み合わせる
        String[] cities = {"東京", "大阪", "名古屋"};
        
        // 各都市の気温と湿度を並行して取得
        CompletableFuture<?>[] allWeatherData = Arrays.stream(cities)
        .map(city -> {
            CompletableFuture<String> temp = WeatherService.getTemperature(city);
            CompletableFuture<String> humidity = WeatherService.getHumidity(city);
            
            // 気温と湿度を組み合わせる
            return temp.thenCombine(humidity, (t, h) -> t + ", " + h);
        })
        .map(future -> future.thenAccept(IO::println))
        .toArray(CompletableFuture[]::new);
        
        // すべての処理が完了するまで待つ
        CompletableFuture.allOf(allWeatherData).get();
        // ...
    }
}
```

#### 非同期処理の連鎖と合成

`CompletableFuture`の真の力は、複数の非同期処理を優雅に連鎖・合成できる点にあります。これにより、複雑な非同期ワークフローを宣言的に記述できます。

主な連鎖メソッド。
- `thenApply()`
    + 結果を変換（同期的）
- `thenApplyAsync()`
    + 結果を変換（非同期的）
- `thenCompose()`
    + 別のCompletableFutureを返す関数を適用
- `thenCombine()`
    + 2つのCompletableFutureの結果を結合
- `thenAccept()`
    + 結果を消費（戻り値なし）

<span class="listing-number">**サンプルコード15-18**　AsyncChainingExample.java（4〜14、35〜48、56行目）</span>

```java
public class AsyncChainingExample {
    
    // ユーザー情報を取得
    static CompletableFuture<User> fetchUser(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            IO.println("Fetching user: " + userId);
            // データベースアクセスをシミュレート
            sleep(1000);
            return new User(userId, "User-" + userId);
        });
    }
    // ...
    
    public static void main(String[] args) throws Exception {
        // 非同期処理を連鎖させる
        CompletableFuture<Integer> totalFuture = fetchUser("USER001")
            .thenCompose(user -> fetchOrders(user))  // 別の非同期処理に連鎖
            .thenApply(orders -> calculateTotal(orders))  // 同期的な変換
            .exceptionally(ex -> {
                System.err.println("Error occurred: " + ex.getMessage());
                return 0;  // デフォルト値を返す
            });
        
        IO.println("Total amount: " + totalFuture.get());
    }
    
    // ...
}
```

この例では、ユーザー情報の取得→注文履歴の取得→合計金額の計算という一連の処理を、美しく連鎖させています。各ステップは非同期に実行され、エラーハンドリングも含まれています。

## 実践的なパフォーマンス測定とベンチマーク

複数のスレッドで処理を分けても、データ量や処理の内容によっては速くならないことがあります。
効果は実際に時間を測って確かめます。
サンプルコード15-19は、配列のソートを`Arrays.sort`と`Arrays.parallelSort`で比べ、`ForkJoinPool`で大きな配列の合計を求めます。

`RecursiveTask`を継承したクラスの`compute`メソッドは、範囲が小さければ直接計算し、大きければ2つに分けます。
分けた片方を`fork`で別のスレッドに任せ、もう片方を自分で計算してから、`join`で結果を待って足し合わせます。
第12章「Stream APIと高度なコレクション操作」で学んだ並列ストリームも、内部ではこのFork/Joinのしくみで処理を分けています。
並列ストリームの使い方と、逐次処理との時間の比べ方は第12章の「並列ストリームによるパフォーマンス向上」を参照してください。

<span class="listing-number">**サンプルコード15-19**　ParallelPerformanceExample.java（5、20〜30、38〜44、51〜73、75〜84、101行目）</span>

```java
public class ParallelPerformanceExample {
        // ...
        
        // シングルスレッドソート
        long startTime = System.currentTimeMillis();
        Arrays.sort(array1);
        long singleThreadTime = System.currentTimeMillis() - startTime;
        
        // 並列ソート
        startTime = System.currentTimeMillis();
        Arrays.parallelSort(array2);
        long parallelTime = System.currentTimeMillis() - startTime;
        
        // ...
    
    // ForkJoinPoolを使った再帰的並列処理
    static class RecursiveSum extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;
        private final long[] array;
        private final int start, end;
        
        // ...
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
    // ...
    public static void forkJoinExample() {
        int size = 100_000_000;
        long[] array = new long[size];
        Arrays.fill(array, 1);
        
        ForkJoinPool pool = new ForkJoinPool();
        
        long startTime = System.currentTimeMillis();
        Long result = pool.invoke(new RecursiveSum(array, 0, size));
        long time = System.currentTimeMillis() - startTime;
        // ...
}
```

## スレッドセーフなシングルトンパターン

<span class="listing-number">**サンプルコード15-20**　ThreadSafeSingletonExamples.java（3、18〜57、79行目）</span>

```java
public class ThreadSafeSingletonExamples {
    // ...
    
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
    
    // 4. Enumシングルトン（もっとも安全）
    enum EnumSingleton {
        INSTANCE;
        
        public void doSomething() {
            IO.println("Enum singleton method");
        }
    }
    // ...
}
```

## 実践的な並行処理パターン

### 並列データ処理パイプライン

<span class="listing-number">**サンプルコード15-21**　ParallelDataPipeline.java（5〜15、21〜37、41、43、70〜79、82〜83行目）</span>

```java
public class ParallelDataPipeline {
    
    // データ処理のステージ
    interface Stage<I, O> {
        O process(I input);
    }
    
    // 並列パイプライン
    static class Pipeline<T> {
        private final ExecutorService executor;
        private final List<Stage<?, ?>> stages;
        // ...
        
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
        
        // ...
    }
    // ...
    public static void main(String[] args) throws Exception {
        // ...
        // パイプライン構築
        CompletableFuture<String> future = pipeline
            .addStage(fetchStage)
            .addStage(transformStage)
            .addStage(saveStage)
            .process("http://example.com");
        
        // 結果取得
        String result = future.get();
        IO.println("Pipeline result: " + result);
        // ...
    }
}
```

### リトライとサーキットブレーカーパターン

<span class="listing-number">**サンプルコード15-22**　ResiliencePatterns.java（4〜7、20〜43、58〜89、127行目）</span>

```java
public class ResiliencePatterns {
    
    // リトライパターン
    static class RetryExecutor {
        // ...
        private <T> CompletableFuture<T> executeWithRetry(Callable<T> task, int attempt) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return task.call();
                } catch (Exception e) {
                    if (attempt < maxRetries) {
                        IO.println("Retry attempt " + (attempt + 1) + 
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
        // ...
        public <T> T execute(Callable<T> task) throws Exception {
            State currentState = state.get();
            
            if (currentState == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime.get() > timeout) {
                    state.set(State.HALF_OPEN);
                    IO.println("Circuit breaker: HALF_OPEN");
                } else {
                    throw new RuntimeException("Circuit breaker is OPEN");
                }
            }
            
            try {
                T result = task.call();
                if (currentState == State.HALF_OPEN) {
                    state.set(State.CLOSED);
                    failureCount.set(0);
                    IO.println("Circuit breaker: CLOSED");
                }
                return result;
            } catch (Exception e) {
                failureCount.incrementAndGet();
                lastFailureTime.set(System.currentTimeMillis());
                
                if (failureCount.get() >= failureThreshold) {
                    state.set(State.OPEN);
                    IO.println("Circuit breaker: OPEN");
                }
                throw e;
            }
        }
    }
    // ...
}
```

## よくあるエラーと対処法

マルチスレッドのエラーは、実行するたびに結果が変わったり、まれにしか発生しなかったりするため、再現と原因の特定が難しいのが特徴です。本章の内容に関連して出会いやすいエラーは次のとおりです。

- 競合状態（レースコンディション）
    + 複数のスレッドが同じデータを同時に更新すると、値が失われる
- デッドロック
    + 複数のスレッドが互いのロックの解放を待ち、処理が止まる。ロックを取得する順序をそろえて防ぐ
- スレッドセーフでないコレクションの使用
    + `ArrayList`や`HashMap`を複数のスレッドから更新すると、内容が壊れたり例外が発生したりする。`ConcurrentHashMap`などの並行処理用のコレクションを使う
- `ExecutorService`のシャットダウン忘れ
    + `shutdown()`を呼ばないと、スレッドプールが残ってプログラムが終了しない

エラーの例と修正したコード（コンパイルできる全文を含む）、デッドロックの検出などのデバッグのヒントは、GitHubリポジトリにまとめています。<br>
`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/h-common-errors/chapters/chapter15.md`

## 本章のまとめ

本章では、マルチスレッドプログラミングの基礎から実践的な並行処理までを学習しました。

- スレッドの基本
    + `Runnable`でタスクを定義し、`Thread`で実行する
    + `join`で他のスレッドの終了を待つ
- 同期制御
    + 共有データへの同時アクセスは競合状態を引き起こす
    + `synchronized`で排他制御し、`volatile`でメモリ可視性を保証する
    + 単純なカウンターなどはAtomicクラスでロックを使わずに更新できる
- Executorフレームワーク
    + スレッドの生成と管理をスレッドプールに任せる
    + `Future`と`Callable`で結果を受け取り、`CompletableFuture`で非同期処理を連鎖させる
    + `BlockingQueue`でプロデューサー・コンシューマーパターンを実装できる
- 実践的な並行処理
    + 並列ソートやForkJoinPoolの効果は、時間を測って確かめる（並列ストリームは第12章）
    + シングルトンはHolderクラスやenumでスレッドセーフに実装できる
    + デッドロックはロックを取得する順序をそろえて防ぐ。`volatile`は複合操作をアトミックにしない
    + パイプライン、リトライ、サーキットブレーカーなどのパターンを組み合わせて、障害に強い処理を作る

並行処理のバグは再現しにくいため、設計の段階で共有データとロックの範囲を明確にすることが大切です。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter15/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

1. 基礎課題
    + 基本的なスレッド操作と同期処理の実装
2. 発展課題
    + Executorフレームワークと並行コレクションの活用
3. チャレンジ課題
    + プロデューサー・コンシューマーパターンの実装

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダー内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第16章「ネットワークプログラミング」に進みましょう。

※ 本章の高度な内容については、付録B.14「Javaメモリモデル」（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b14-java-memory-model/`）を参照してください。
