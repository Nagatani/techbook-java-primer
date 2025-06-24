# 第14章 マルチスレッドプログラミング

## 始めに：並行計算の進化とマルチコア時代のプログラミング

前章までJavaの主要な技術について学習してきました。本章では、現代のコンピューティングにおいて不可欠な「マルチスレッドプログラミング」について詳細に学習していきます。

マルチスレッドプログラミングは、単なる性能向上の手法ではありません。これは、コンピュータアーキテクチャの根本的な変化に対応し、現代の複雑なアプリケーション要件を満たすための、必須の技術パラダイムです。

### コンピュータアーキテクチャの歴史的変遷

コンピュータの処理能力向上の歴史は、並行処理技術の発展と密接に関連しています。この歴史を理解することは、現代のマルチスレッドプログラミングの意義を深く理解するために重要です。

**単一プロセッサ時代（1940年代〜2000年代初期）**：初期のコンピュータから2000年代初期まで、性能向上の主な手段はプロセッサのクロック周波数向上でした。ムーアの法則に従い、トランジスタ密度の向上により処理速度が飛躍的に向上していました。

**物理的限界の到来（2000年代中期）**：クロック周波数の向上は、消費電力と熱の問題により物理的限界に達しました。単一コアの性能向上だけでは、さらなる計算能力の向上が困難になりました。

**マルチコア革命（2005年〜）**：Intel Core 2 Duo（2006年）、AMD Athlon 64 X2（2005年）の登場により、消費者向けマルチコアプロセッサが普及しました。現在では、スマートフォンでも8コア以上のプロセッサが一般的です。

**並列計算の民主化**：従来は大学や研究機関のスーパーコンピュータでのみ利用されていた並列計算技術が、一般的なデスクトップやモバイルデバイスでも利用できるようになりました。

### 並行プログラミングの理論的基盤

並行プログラミングの概念は、コンピュータサイエンスの理論的研究から生まれました：

**プロセス理論（1960年代〜）**：Tony HoareのCommunicating Sequential Processes（CSP）やRobin Milnerのπ-calculusなど、並行システムの数学的モデルが確立されました。

**同期プリミティブの開発（1960年代〜1970年代）**：Edsger Dijkstraのセマフォ、Per Brinch HansenとTony Hoareのモニターなど、並行プログラムの同期制御メカニズムが開発されました。

**デッドロック理論**：複数のプロセスが互いに待ち状態になるデッドロック問題の理論的解析と回避手法が研究されました。

**並行性の分類**：データ並列性、タスク並列性、パイプライン並列性など、並行処理のさまざまな形態が分類され、それぞれに適した実装手法が開発されました。

### プログラミング言語における並行処理サポートの発展

プログラミング言語は、並行処理サポートにおいて段階的に進化してきました：

**低レベル並行処理（1970年代〜1980年代）**：C言語のPOSIX threadsのように、OSレベルのプリミティブを直接使用する手法が主流でした。高い性能を実現できる一方で、プログラミングは極めて困難でした。

```c
#include <pthread.h>

pthread_t thread;
pthread_mutex_t mutex;

void* thread_function(void* arg) {
    pthread_mutex_lock(&mutex);
    // クリティカルセクション
    pthread_mutex_unlock(&mutex);
    return NULL;
}
```

**オブジェクト指向並行処理（1990年代〜）**：Java、C#などの言語により、オブジェクト指向の概念と統合された、より高レベルな並行処理支援が提供されました。

**関数型並行処理（2000年代〜）**：Erlang、Haskellなどの関数型言語により、不変性と純粋関数を活用した、副作用のない並行処理モデルが確立されました。

**アクターモデル（2000年代〜）**：Erlangの影響を受けて、ScalaのAkka、JavaScriptのNode.jsなどでアクターモデルによる並行処理が普及しました。

### Javaにおける並行プログラミングの発展

Javaは、並行プログラミングサポートにおいて段階的に機能を拡張してきました：

**Java 1.0（1995年）- 基本的なスレッドサポート**：Threadクラス、Runnableインターフェイス、synchronizedキーワードなど、基本的な並行処理機能が提供されました。

**Java 1.1（1997年）- 同期化の改善**：wait()、notify()、notifyAll()メソッドによる、より柔軟な同期制御が可能になりました。

**Java 1.4（2002年）- NIO**：非ブロッキングI/Oにより、大量の接続を効率的に処理できるようになりました。

**Java 5（2004年）- java.util.concurrent**：Doug Leaによって開発された高レベル並行処理ライブラリが標準に組み込まれ、実用的な並行プログラミングが大幅に容易になりました。

**Java 7（2011年）- Fork/Join Framework**：分割統治法による並列処理を効率的に実装できるフレームワークが追加されました。

**Java 8（2014年）- Parallel Streams**：Stream APIと組み合わせることで、データ並列処理を宣言的に記述できるようになりました。

### 現代アプリケーションにおける並行処理の必要性

現代のソフトウェア開発では、並行処理は以下の分野で不可欠となっています：

**Webアプリケーション**：数千から数万の同時接続を処理するため、効率的な並行処理が必要です。サーブレット、Spring WebFlux、非同期処理などが重要な技術となっています。

**ビッグデータ処理**：Apache Spark、Apache Hadoopなどのフレームワークは、大量のデータを並列処理することで、実用的な時間での分析を実現しています。

**リアルタイムシステム**：ゲーム、金融取引、IoTシステムなど、リアルタイム性が要求されるシステムでは、効率的な並行処理が性能の鍵となります。

**機械学習**：深層学習の訓練や推論処理では、GPUを活用した大規模並列処理が標準的な手法となっています。

**マイクロサービス**：分散システムアーキテクチャでは、サービス間の非同期通信と並行処理が、システム全体の性能と可用性を決定します。

### 並行プログラミングの課題と対策

並行プログラミングは高い性能を実現する一方で、特有の課題があります：

**競合状態（Race Condition）**：複数のスレッドが同じリソースに同時にアクセスすることで、予期しない結果が生じる問題です。適切な同期化により防止できます。

**デッドロック**：複数のスレッドが互いを待ち続ける状態です。リソースの取得順序の統一、タイムアウトの設定などで回避できます。

**性能のボトルネック**：過度の同期化により、並行処理の利点が失われる場合があります。適切な粒度での同期化設計が重要です。

**デバッグの困難さ**：並行プログラムのバグは再現が困難で、デバッグに高度な技術が必要です。ユニットテスト、ログ記録、専用デバッガの活用が重要です。

### 現代的な並行プログラミングパラダイム

現代の並行プログラミングでは、従来の低レベルなスレッド操作から、より高レベルなパラダイムへの移行が進んでいます：

**非同期プログラミング**：CompleテーブルFuture、Reactive Streamsにより、非ブロッキングな処理フローを構築できます。

**イミューダブルオブジェクト**：不変オブジェクトを使用することで、スレッド安全性を簡単に確保できます。

**関数型並行処理**：Stream APIのparallel() メソッドにより、関数型の操作を自動的に並列化できます。

**アクターモデル**：Akkaライブラリにより、メッセージパッシングベースの並行処理を実装できます。

### 本章で学習する内容の意義

本章では、これらの歴史的背景と現代的な課題を踏まえて、Javaにおけるマルチスレッドプログラミングを体系的に学習していきます。単にスレッドの作り方を覚えるのではなく、以下の点を重視して学習を進めます：

**並行処理の設計思想**：なぜ並行処理が必要なのか、どのような場面で効果的なのかを理解し、適切な並行処理設計ができる能力を身につけます。

**スレッド安全性の理解**：競合状態やデッドロックなどの問題を理解し、安全な並行プログラムを作成する技術を習得します。

**高レベル API の活用**：java.util.concurrentパッケージの活用により、実用的で保守性の高い並行プログラムを作成できます。

**性能とのバランス**：並行処理による性能向上と、複雑性の増大のバランスを適切に判断する能力を養います。

**現代技術との統合**：Reactive Programming、非同期処理、マイクロサービスなど、現代的な技術との関連性を理解します。

マルチスレッドプログラミングを深く理解することは、現代のマルチコア時代において高性能なアプリケーションを開発する能力を身につけることにつながります。本章を通じて、単なる「複数のスレッドを動かす技術」を超えて、「並行システムの設計と実装の総合的な能力」を習得していきましょう。

現代のマルチコアプロセッサを効率的に活用するために、マルチスレッドプログラミングは必須の技術です。本章では、Javaにおけるマルチスレッドの基礎から実践的な応用まで学習します。

## 14.1 マルチスレッドプログラミングの基礎

### スレッドとは

スレッドとは、プログラム内での処理の流れを表す実行単位です。通常、1つのプログラムは1つのスレッド（メインスレッド）で実行されますが、マルチスレッドとは、1つのプログラム内で複数のスレッドを同時に実行し、処理を並行して進める技術です。

### マルチスレッドのメリット

現代のコンピュータは、複数のコアを持つCPU（マルチコアプロセッサ）を搭載するのが一般的です。マルチスレッドプログラミングは、この複数のコアを効率的に活用し、アプリケーションのパフォーマンスを向上させるための重要な技術です。

主なメリットは以下のとおりです：

* **パフォーマンスの向上**: 時間のかかる処理を複数のスレッドに分割し、同時に実行することで、全体の処理時間を短縮できます
* **応答性の維持**: GUIアプリケーションなどで、時間のかかる処理をバックグラウンドのスレッドに任せることで、ユーザーインターフェイスの応答性を維持できます
* **リソースの有効活用**: サーバアプリケーションなどでは、複数のクライアントからのリクエストを同時に処理することで、CPUなどの計算リソースを有効に活用できます

## 14.2 スレッドの作成と実行

Javaにおけるマルチスレッド用プログラムの作成方法には、代表的な2つの方法があります。

### Runnableインターフェイスの実装（推奨）

`Runnable`は、スレッドが実行するタスク（処理内容）を定義するための関数型インターフェイスです。`run()`メソッドを1つだけ持ちます。

#### 基本的なRunnableの実装

```java
// Runnableを実装したクラスを定義
public class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnableを実装したクラスによるタスク実行: " + 
                          Thread.currentThread().getName());
    }

    public static void main(String[] args) {
        // Runnable実装クラスのインスタンスを作成
        MyTask task = new MyTask();
        // Threadクラスのコンストラクタに渡し、スレッドを作成
        Thread thread = new Thread(task);
        // start()メソッドを呼び出してスレッドを実行開始
        thread.start();
    }
}
```

#### 実用的なマルチスレッド画像処理システム

以下は、複数の画像ファイルを並行処理で変換するシステムの例です。これは実際の業務でよく見られる並行処理のパターンを示しています：

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 画像処理システム - マルチスレッドによるバッチ処理の実践例
 * 
 * このシステムは以下の特徴を持ちます：
 * - 複数の画像ファイルを並行処理で変換
 * - プログレス表示とエラーハンドリング
 * - リソース制御とパフォーマンス最適化
 * - 実際の業務で使用される設計パターン
 */
public class ImageProcessingSystem {
    
    /**
     * 画像処理タスクのレコード
     * @param inputPath 入力ファイルパス
     * @param outputPath 出力ファイルパス
     * @param operation 処理種別
     */
    public record ImageTask(Path inputPath, Path outputPath, String operation) {}
    
    /**
     * 画像処理結果のレコード
     */
    public record ProcessingResult(ImageTask task, boolean success, 
                                  long processingTimeMs, String errorMessage) {}
    
    /**
     * 単一の画像処理を実行するRunnable実装
     * 
     * このクラスは、責任の分離の原則に従い、1つの画像ファイルの処理のみに専念します。
     * 例外安全性を確保し、処理時間の計測も行います。
     */
    private static class ImageProcessor implements Runnable {
        private final ImageTask task;
        private final BlockingQueue<ProcessingResult> resultQueue;
        private final AtomicInteger completedCount;
        private final AtomicInteger totalCount;
        
        public ImageProcessor(ImageTask task, 
                            BlockingQueue<ProcessingResult> resultQueue,
                            AtomicInteger completedCount, 
                            AtomicInteger totalCount) {
            this.task = task;
            this.resultQueue = resultQueue;
            this.completedCount = completedCount;
            this.totalCount = totalCount;
        }
        
        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            boolean success = false;
            String errorMessage = null;
            
            try {
                System.out.println(Thread.currentThread().getName() + 
                    " が " + task.inputPath().getFileName() + " の処理を開始");
                
                // 実際の画像処理をシミュレート（実装では画像変換ライブラリを使用）
                processImage(task);
                
                // 処理完了の表示
                int completed = completedCount.incrementAndGet();
                System.out.printf("進捗: %d/%d 完了 - %s%n", 
                    completed, totalCount.get(), task.inputPath().getFileName());
                
                success = true;
                
            } catch (Exception e) {
                errorMessage = e.getMessage();
                System.err.println("エラー: " + task.inputPath().getFileName() + 
                    " の処理中にエラーが発生: " + errorMessage);
            } finally {
                long processingTime = System.currentTimeMillis() - startTime;
                ProcessingResult result = new ProcessingResult(
                    task, success, processingTime, errorMessage);
                
                try {
                    resultQueue.put(result);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        /**
         * 画像処理の実行（シミュレーション）
         * 実際の実装では、BufferedImage、ImageIO、Graphics2Dなどを使用
         */
        private void processImage(ImageTask task) throws IOException, InterruptedException {
            // ファイルサイズに応じた処理時間をシミュレート
            long fileSize = Files.size(task.inputPath());
            long processingTime = Math.min(fileSize / 1000, 3000); // 最大3秒
            
            Thread.sleep(processingTime);
            
            // 実際の処理では以下のような画像変換を行う：
            // BufferedImage image = ImageIO.read(task.inputPath().toFile());
            // BufferedImage resized = resizeImage(image, task.operation());
            // ImageIO.write(resized, "jpg", task.outputPath().toFile());
            
            // 出力ファイルの作成をシミュレート
            Files.createDirectories(task.outputPath().getParent());
            Files.copy(task.inputPath(), task.outputPath(), 
                StandardCopyOption.REPLACE_EXISTING);
        }
    }
    
    /**
     * バッチ処理の実行
     */
    public void processBatch(List<ImageTask> tasks) throws InterruptedException {
        if (tasks.isEmpty()) {
            System.out.println("処理するタスクがありません。");
            return;
        }
        
        System.out.println("=== 画像バッチ処理システム開始 ===");
        System.out.println("処理予定ファイル数: " + tasks.size());
        
        // CPUコア数に基づいたスレッドプール作成
        int threadCount = Math.min(tasks.size(), 
            Runtime.getRuntime().availableProcessors());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // 結果収集用のキューと進捗カウンタ
        BlockingQueue<ProcessingResult> resultQueue = new LinkedBlockingQueue<>();
        AtomicInteger completedCount = new AtomicInteger(0);
        AtomicInteger totalCount = new AtomicInteger(tasks.size());
        
        long batchStartTime = System.currentTimeMillis();
        
        try {
            // すべてのタスクをスレッドプールに投入
            for (ImageTask task : tasks) {
                ImageProcessor processor = new ImageProcessor(
                    task, resultQueue, completedCount, totalCount);
                executor.execute(processor);
            }
            
            // 処理開始の確認
            System.out.println("スレッドプール (" + threadCount + " スレッド) で処理開始");
            
            // 適切なシャットダウン処理
            executor.shutdown();
            
            // すべてのタスクの完了を待機（最大10分）
            boolean completed = executor.awaitTermination(10, TimeUnit.MINUTES);
            
            if (!completed) {
                System.err.println("タイムアウト: 強制終了します");
                executor.shutdownNow();
                return;
            }
            
            // 結果の集計と表示
            List<ProcessingResult> results = new ArrayList<>();
            while (results.size() < tasks.size()) {
                ProcessingResult result = resultQueue.poll(1, TimeUnit.SECONDS);
                if (result != null) {
                    results.add(result);
                }
            }
            
            displayResults(results, batchStartTime);
            
        } finally {
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        }
    }
    
    /**
     * 処理結果の表示とパフォーマンス分析
     */
    private void displayResults(List<ProcessingResult> results, long batchStartTime) {
        long totalBatchTime = System.currentTimeMillis() - batchStartTime;
        
        System.out.println("\n=== 処理結果サマリー ===");
        
        long successCount = results.stream()
            .mapToLong(r -> r.success() ? 1 : 0)
            .sum();
        
        double averageProcessingTime = results.stream()
            .mapToLong(ProcessingResult::processingTimeMs)
            .average()
            .orElse(0.0);
        
        System.out.println("成功: " + successCount + " / " + results.size());
        System.out.println("総処理時間: " + totalBatchTime + " ms");
        System.out.println("平均処理時間: " + String.format("%.1f ms", averageProcessingTime));
        
        // エラーがあった場合の詳細表示
        List<ProcessingResult> errors = results.stream()
            .filter(r -> !r.success())
            .toList();
        
        if (!errors.isEmpty()) {
            System.out.println("\n=== エラー詳細 ===");
            for (ProcessingResult error : errors) {
                System.out.println("ファイル: " + 
                    error.task().inputPath().getFileName() + 
                    " - " + error.errorMessage());
            }
        }
        
        // パフォーマンス効果の表示
        long sequentialEstimate = results.stream()
            .mapToLong(ProcessingResult::processingTimeMs)
            .sum();
        
        double speedup = (double) sequentialEstimate / totalBatchTime;
        System.out.println("\n並列処理効果:");
        System.out.printf("推定順次処理時間: %d ms%n", sequentialEstimate);
        System.out.printf("実際の並列処理時間: %d ms%n", totalBatchTime);
        System.out.printf("速度向上率: %.2fx%n", speedup);
    }
    
    /**
     * デモ実行
     */
    public static void main(String[] args) {
        try {
            ImageProcessingSystem system = new ImageProcessingSystem();
            
            // テスト用のタスクを作成
            List<ImageTask> tasks = Arrays.asList(
                new ImageTask(Paths.get("input/photo1.jpg"), 
                            Paths.get("output/photo1_resized.jpg"), "resize"),
                new ImageTask(Paths.get("input/photo2.jpg"), 
                            Paths.get("output/photo2_resized.jpg"), "resize"),
                new ImageTask(Paths.get("input/photo3.jpg"), 
                            Paths.get("output/photo3_resized.jpg"), "resize"),
                new ImageTask(Paths.get("input/photo4.jpg"), 
                            Paths.get("output/photo4_resized.jpg"), "resize"),
                new ImageTask(Paths.get("input/photo5.jpg"), 
                            Paths.get("output/photo5_resized.jpg"), "resize")
            );
            
            // ダミーファイルの作成（実際の実装では既存の画像ファイルを使用）
            createDummyFiles(tasks);
            
            // バッチ処理の実行
            system.processBatch(tasks);
            
        } catch (Exception e) {
            System.err.println("システムエラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * テスト用のダミーファイル作成
     */
    private static void createDummyFiles(List<ImageTask> tasks) {
        try {
            Files.createDirectories(Paths.get("input"));
            Files.createDirectories(Paths.get("output"));
            
            for (ImageTask task : tasks) {
                if (!Files.exists(task.inputPath())) {
                    // ランダムサイズのダミーファイルを作成
                    byte[] data = new byte[1000 + (int)(Math.random() * 5000)];
                    Arrays.fill(data, (byte) 0xFF);
                    Files.write(task.inputPath(), data);
                }
            }
        } catch (IOException e) {
            System.err.println("ダミーファイル作成エラー: " + e.getMessage());
        }
    }
}
```

**このシステムの特徴と学習ポイント:**

1. **責任の分離**: `ImageProcessor`クラスは単一の画像処理のみに専念し、結果の集約は別のクラスが担当します。

2. **例外安全性**: 各画像の処理でエラーが発生しても、ほかの画像の処理に影響を与えません。

3. **プログレス管理**: `AtomicInteger`を使用してスレッドセーフな進捗カウンタを実装しています。

4. **リソース管理**: CPUコア数にもとづいてスレッド数を決定し、システムリソースの効率的な活用を図っています。

5. **パフォーマンス測定**: 並列処理の効果を定量的に測定し、表示します。

6. **現実的な設計**: 実際の業務システムで使用される設計パターンとエラーハンドリングを実装しています。

### ラムダ式による簡潔な記述

`Runnable`は関数型インターフェイスであるため、Java 8以降ではラムダ式を使って簡潔に記述できます。

```java
public class Main {
    public static void main(String[] args) {
        // ラムダを使ってRunnableを直接定義
        Runnable task = () -> {
            System.out.println("ラムダ式によるタスク実行: " + 
                              Thread.currentThread().getName());
        };

        Thread thread = new Thread(task);
        thread.start();

        // さらに簡潔に書くことも可能
        new Thread(() -> {
            System.out.println("さらに簡潔なラムダ式によるタスク実行: " + 
                              Thread.currentThread().getName());
        }).start();
    }
}
```

### Threadクラスの継承（参考）

参考として、`Thread`クラスを継承する方法も示します。

```java
// Threadクラスを継承したクラスを定義
public class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Threadクラスを継承したスレッド実行: " + this.getName());
    }

    public static void main(String[] args) {
        MyThread thread = new MyThread();
        thread.start();
    }
}
```

`Runnable`を利用する方法は、Javaがクラスの多重継承をサポートしないという制約を受けません。また、「タスク（何をするか）」と「スレッド（どう実行するか）」を分離できるため、より柔軟で疎結合な設計が可能になります。そのため、`Thread`クラスを直接継承する方法よりも推奨されます。

## 14.3 スレッドの基本的な制御

### Thread.sleep()

現在実行中のスレッドを、指定されたミリ秒の間、一時的に休止させます。これは、CPUリソースの過度な消費を防いだり、処理のタイミングを調整したりする目的で利用されます。

```java
// 1秒ごとにメッセージを出力するタスク
Runnable task = () -> {
    for (int i = 0; i < 5; i++) {
        System.out.println("スレッド実行中... " + i);
        try {
            Thread.sleep(1000); // 1000ミリ秒間スリープ
        } catch (InterruptedException e) {
            System.err.println("スレッドが中断されました。");
            Thread.currentThread().interrupt();
            break;
        }
    }
};

new Thread(task).start();
```

### thread.join()

特定のスレッドの処理が完了するまで、現在のスレッドが待機します。これにより、複数のスレッドの処理結果を待ってから、次の処理に進むことができます。

```java
public class JoinExample {
    public static void main(String[] args) throws InterruptedException {
        Runnable timeConsumingTask = () -> {
            System.out.println("バックグラウンド処理を開始します。");
            try {
                Thread.sleep(3000); // 3秒かかる処理を模倣
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("バックグラウンド処理が完了しました。");
        };

        Thread backgroundThread = new Thread(timeConsumingTask);
        backgroundThread.start();

        System.out.println("メインスレッドはバックグラウンド処理の完了を待ちます。");
        backgroundThread.join(); // backgroundThreadが終了するまで待機
        System.out.println("すべての処理が完了しました。");
    }
}
```

## 14.4 共有リソースと同期制御

複数のスレッドが、同じデータ（オブジェクトや変数）に同時にアクセスすると、予期せぬ問題が発生することがあります。これを**競合状態 (Race Condition)** と呼びます。

### 競合状態の例

以下の`Counter`クラスは、複数のスレッドから`increment()`メソッドが呼び出されると、期待通りに動作しない可能性があります。

```java
public class RaceConditionExample {
    
    private static class Counter {
        private int count = 0;

        // このメソッドはスレッドセーフではありません
        public void increment() {
            // 1. 現在のcount値を読み込み
            // 2. 読み込んだ値に1を加算
            // 3. 計算結果をcountに書き戻し
            // この3つのステップがアトミック(不可分)ではないため、競合状態が発生
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        // 2つのスレッドを作成し、それぞれ100,000回インクリメント
        Runnable task = () -> {
            for (int i = 0; i < 100000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        // 両方のスレッドが終了するのを待つ
        t1.join();
        t2.join();

        // 期待値は200,000ですが、それより少ない値になることが多い
        System.out.println("最終カウント: " + counter.getCount());
    }
}
```

#### 実践的な同期制御 - 銀行口座システム

現実的な同期制御の例として、銀行口座の残高管理システムを実装してみましょう。このシステムでは複数のスレッドが同時に入金・出金操作を行います：

```java
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.text.NumberFormat;
import java.util.*;

/**
 * 銀行口座システム - 同期制御の実践例
 * 
 * このシステムは以下の同期制御技術を示します：
 * - synchronizedメソッドによる基本的な排他制御
 * - ReentrantLockによる高度な制御
 * - 複数リソースでのデッドロック回避策
 * - アトミック操作の活用
 */
public class BankAccountSystem {
    
    /**
     * 基本的な銀行口座（synchronized使用）
     */
    public static class BasicBankAccount {
        private double balance;
        private final String accountNumber;
        private final NumberFormat formatter = NumberFormat.getCurrencyInstance();
        
        public BasicBankAccount(String accountNumber, double initialBalance) {
            this.accountNumber = accountNumber;
            this.balance = initialBalance;
        }
        
        /**
         * 入金処理（スレッドセーフ）
         */
        public synchronized void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("入金額は正の値である必要があります");
            }
            
            double oldBalance = balance;
            balance += amount;
            
            System.out.printf("口座 %s: 入金 %s (残高 %s → %s) [%s]%n",
                accountNumber, formatter.format(amount),
                formatter.format(oldBalance), formatter.format(balance),
                Thread.currentThread().getName());
        }
        
        /**
         * 出金処理（スレッドセーフ）
         */
        public synchronized boolean withdraw(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("出金額は正の値である必要があります");
            }
            
            if (balance >= amount) {
                double oldBalance = balance;
                balance -= amount;
                
                System.out.printf("口座 %s: 出金 %s (残高 %s → %s) [%s]%n",
                    accountNumber, formatter.format(amount),
                    formatter.format(oldBalance), formatter.format(balance),
                    Thread.currentThread().getName());
                return true;
            } else {
                System.out.printf("口座 %s: 出金失敗 %s (残高不足: %s) [%s]%n",
                    accountNumber, formatter.format(amount),
                    formatter.format(balance), Thread.currentThread().getName());
                return false;
            }
        }
        
        public synchronized double getBalance() {
            return balance;
        }
        
        public String getAccountNumber() {
            return accountNumber;
        }
    }
    
    /**
     * 高度な銀行口座（ReentrantLock使用）
     */
    public static class AdvancedBankAccount {
        private double balance;
        private final String accountNumber;
        private final ReentrantLock lock = new ReentrantLock();
        private final NumberFormat formatter = NumberFormat.getCurrencyInstance();
        
        public AdvancedBankAccount(String accountNumber, double initialBalance) {
            this.accountNumber = accountNumber;
            this.balance = initialBalance;
        }
        
        /**
         * タイムアウト付き入金処理
         */
        public boolean depositWithTimeout(double amount, long timeoutMs) {
            if (amount <= 0) {
                throw new IllegalArgumentException("入金額は正の値である必要があります");
            }
            
            try {
                if (lock.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                    try {
                        double oldBalance = balance;
                        balance += amount;
                        
                        System.out.printf("口座 %s: 入金 %s (残高 %s → %s) [%s]%n",
                            accountNumber, formatter.format(amount),
                            formatter.format(oldBalance), formatter.format(balance),
                            Thread.currentThread().getName());
                        return true;
                    } finally {
                        lock.unlock();
                    }
                } else {
                    System.out.printf("口座 %s: 入金タイムアウト %s [%s]%n",
                        accountNumber, formatter.format(amount),
                        Thread.currentThread().getName());
                    return false;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        public double getBalance() {
            lock.lock();
            try {
                return balance;
            } finally {
                lock.unlock();
            }
        }
        
        public String getAccountNumber() {
            return accountNumber;
        }
        
        /**
         * デッドロック回避を考慮した送金処理
         */
        public static boolean transfer(AdvancedBankAccount from, AdvancedBankAccount to, 
                                     double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("送金額は正の値である必要があります");
            }
            
            // デッドロック回避: 口座番号の順序でロック取得順序を決定
            AdvancedBankAccount firstLock, secondLock;
            if (from.accountNumber.compareTo(to.accountNumber) < 0) {
                firstLock = from;
                secondLock = to;
            } else {
                firstLock = to;
                secondLock = from;
            }
            
            firstLock.lock.lock();
            try {
                secondLock.lock.lock();
                try {
                    if (from.balance >= amount) {
                        from.balance -= amount;
                        to.balance += amount;
                        
                        System.out.printf("送金: %s → %s, 金額 %s [%s]%n",
                            from.accountNumber, to.accountNumber,
                            NumberFormat.getCurrencyInstance().format(amount),
                            Thread.currentThread().getName());
                        return true;
                    } else {
                        System.out.printf("送金失敗: %s → %s, 残高不足 (残高: %s, 送金額: %s) [%s]%n",
                            from.accountNumber, to.accountNumber,
                            NumberFormat.getCurrencyInstance().format(from.balance),
                            NumberFormat.getCurrencyInstance().format(amount),
                            Thread.currentThread().getName());
                        return false;
                    }
                } finally {
                    secondLock.lock.unlock();
                }
            } finally {
                firstLock.lock.unlock();
            }
        }
    }
    
    /**
     * 銀行業務シミュレーション
     */
    public static void demonstrateBankOperations() throws InterruptedException {
        System.out.println("=== 銀行口座システム デモンストレーション ===\n");
        
        // 基本的な口座での同時アクセステスト
        System.out.println("1. 基本的な同期制御テスト:");
        BasicBankAccount basicAccount = new BasicBankAccount("0001", 10000.0);
        
        ExecutorService basicExecutor = Executors.newFixedThreadPool(3);
        
        // 複数のスレッドで同時に入金・出金を実行
        for (int i = 0; i < 5; i++) {
            final int operationId = i;
            basicExecutor.submit(() -> {
                basicAccount.deposit(100.0 * operationId);
                basicAccount.withdraw(50.0 * operationId);
            });
        }
        
        basicExecutor.shutdown();
        basicExecutor.awaitTermination(5, TimeUnit.SECONDS);
        
        System.out.printf("基本口座最終残高: %s%n%n", 
            NumberFormat.getCurrencyInstance().format(basicAccount.getBalance()));
        
        // 高度な口座での送金テスト
        System.out.println("2. 高度な同期制御と送金テスト:");
        AdvancedBankAccount account1 = new AdvancedBankAccount("ADV-001", 5000.0);
        AdvancedBankAccount account2 = new AdvancedBankAccount("ADV-002", 3000.0);
        AdvancedBankAccount account3 = new AdvancedBankAccount("ADV-003", 7000.0);
        
        List<AdvancedBankAccount> accounts = Arrays.asList(account1, account2, account3);
        ExecutorService advancedExecutor = Executors.newFixedThreadPool(5);
        
        // ランダムな送金処理を複数のスレッドで実行
        for (int i = 0; i < 10; i++) {
            advancedExecutor.submit(() -> {
                Collections.shuffle(accounts);
                AdvancedBankAccount from = accounts.get(0);
                AdvancedBankAccount to = accounts.get(1);
                double amount = 100.0 + Math.random() * 500.0;
                
                AdvancedBankAccount.transfer(from, to, amount);
            });
        }
        
        // タイムアウト付き入金のテスト
        for (int i = 0; i < 3; i++) {
            final int depositAmount = (i + 1) * 200;
            advancedExecutor.submit(() -> {
                Collections.shuffle(accounts);
                accounts.get(0).depositWithTimeout(depositAmount, 1000);
            });
        }
        
        advancedExecutor.shutdown();
        advancedExecutor.awaitTermination(10, TimeUnit.SECONDS);
        
        System.out.println("\n最終残高:");
        double totalBalance = 0;
        for (AdvancedBankAccount account : accounts) {
            double balance = account.getBalance();
            totalBalance += balance;
            System.out.printf("口座 %s: %s%n", account.getAccountNumber(),
                NumberFormat.getCurrencyInstance().format(balance));
        }
        System.out.printf("総残高: %s%n", 
            NumberFormat.getCurrencyInstance().format(totalBalance));
    }
    
    public static void main(String[] args) {
        try {
            demonstrateBankOperations();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("処理が中断されました");
        } catch (Exception e) {
            System.err.println("エラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

**このシステムで学習できる同期制御の重要ポイント:**

1. **synchronizedメソッド**: `BasicBankAccount`では、メソッド全体を同期化する最もシンプルな手法を使用しています。

2. **ReentrantLock**: `AdvancedBankAccount`では、タイムアウト機能付きのより高度なロック制御を実装しています。

3. **デッドロック回避**: 送金処理では、常に口座番号の順序でロックを取得することで、デッドロックを防いでいます。

4. **例外安全性**: finally節でのlock.unlock()により、例外が発生してもロックが確実に解放されます。

5. **パフォーマンス考慮**: 必要最小限の範囲でのみロックを保持し、パフォーマンスへの影響を最小化しています。

6. **実際的な業務ロジック**: 銀行口座という現実的なシナリオで、同期制御の必要性を理解できます。

### synchronizedによる排他制御

この問題を解決するために、Javaは`synchronized`キーワードによる**排他制御 (Mutual Exclusion)** のしくみを提供します。`synchronized`で保護されたコードブロックは、一度に1つのスレッドしか実行できないことが保証されます。

#### synchronizedメソッド

メソッド全体を同期化するには、メソッド宣言に`synchronized`を付与します。

```java
class SynchronizedCounter {
    private int count = 0;

    // このメソッドはsynchronizedによりスレッドセーフになります
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
```

#### synchronizedブロック

メソッド全体ではなく、特定のコードブロックのみを同期化したい場合は、`synchronized`ブロックを使用します。これにより、ロックの範囲を最小限に抑え、パフォーマンスへの影響を軽減できます。

```java
public class SynchronizedBlockExample {
    private final Object lock = new Object(); // ロック専用のオブジェクト
    private int count = 0;

    public void performTask() {
        System.out.println(Thread.currentThread().getName() + 
                          "がロックを取得しようとしています。");

        // synchronizedブロック: 専用のlockオブジェクトで同期
        synchronized (lock) {
            // このブロック内は一度に1つのスレッドしか実行できません
            count++;
            System.out.println(Thread.currentThread().getName() + 
                              "がロック内で処理を実行中。Count: " + count);
        }
    }
}
```

### デッドロック

**デッドロック**は、2つ以上のスレッドが互いに相手のロックを待ってしまい、処理が永遠に進まなくなる状態のことです。

```java
public class DeadlockExample {
    private static final Object resourceA = new Object();
    private static final Object resourceB = new Object();

    public static void main(String[] args) {
        // スレッド1: AをロックしてからBをロックしようとする
        Thread t1 = new Thread(() -> {
            synchronized (resourceA) {
                System.out.println("Thread 1: Locked resource A");
                try { Thread.sleep(100); } catch (InterruptedException e) {}

                System.out.println("Thread 1: Waiting for resource B...");
                synchronized (resourceB) {
                    System.out.println("Thread 1: Locked resource B");
                }
            }
        });

        // スレッド2: BをロックしてからAをロックしようとする
        Thread t2 = new Thread(() -> {
            synchronized (resourceB) {
                System.out.println("Thread 2: Locked resource B");
                try { Thread.sleep(100); } catch (InterruptedException e) {}

                System.out.println("Thread 2: Waiting for resource A...");
                synchronized (resourceA) {
                    System.out.println("Thread 2: Locked resource A");
                }
            }
        });

        t1.start();
        t2.start();
    }
}
```

最も一般的な回避策は、**すべてのスレッドでロックを取得する順序を統一する**ことです。たとえば、常に`resourceA` → `resourceB`の順でロックを取得するようにすれば、デッドロックは発生しません。

## 14.5 Executorフレームワークによる高度なスレッド管理

`Thread`クラスを直接インスタンス化して使用する方法は、小規模なプログラムでは問題ありません。しかし、多くのタスクを処理する必要がある場合、スレッドの生成と破棄にかかるオーバーヘッドが大きくなり、無制限にスレッドが生成されてリソースを枯渇させる危険性があります。

この問題を解決するのが **Executorフレームワーク (`java.util.concurrent`)** です。これは、スレッドの生成・管理を抽象化し、**スレッドプール**を利用して効率的にタスクを実行するためのしくみを提供します。

#### 実践的なWebクローラシステム

以下は、ExecutorServiceを活用した実用的なWebクローラシステムです。このシステムは複数のWebページを並行してダウンロードし、結果を効率的に処理します：

```java
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Webクローラーシステム - ExecutorServiceの実践例
 * 
 * このシステムは以下の技術を実装しています：
 * - 複数タイプのExecutorServiceの使い分け
 * - CompletableFutureによる非同期処理
 * - 結果の集約と統計分析
 * - エラーハンドリングとリトライ機能
 */
public class WebCrawlerSystem {
    
    /**
     * Webページの情報を表すレコード
     */
    public record WebPageInfo(String url, int statusCode, long contentLength, 
                             Duration downloadTime, String errorMessage) {
        public boolean isSuccess() {
            return statusCode == 200 && errorMessage == null;
        }
    }
    
    /**
     * 単一ページのダウンロードタスク
     */
    private static class PageDownloader implements Callable<WebPageInfo> {
        private final String url;
        private final int timeoutSeconds;
        private final AtomicInteger completedCount;
        private final int totalCount;
        
        public PageDownloader(String url, int timeoutSeconds, 
                            AtomicInteger completedCount, int totalCount) {
            this.url = url;
            this.timeoutSeconds = timeoutSeconds;
            this.completedCount = completedCount;
            this.totalCount = totalCount;
        }
        
        @Override
        public WebPageInfo call() throws Exception {
            Instant startTime = Instant.now();
            
            try {
                System.out.printf("[%s] %s のダウンロード開始%n", 
                    Thread.currentThread().getName(), url);
                
                // HTTP接続の設定
                URL urlObj = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(timeoutSeconds * 1000);
                connection.setReadTimeout(timeoutSeconds * 1000);
                connection.setRequestProperty("User-Agent", 
                    "WebCrawler-Demo/1.0 (Educational Purpose)");
                
                int statusCode = connection.getResponseCode();
                long contentLength = 0;
                
                if (statusCode == 200) {
                    // コンテンツのダウンロード（実際の実装では内容を保存）
                    try (InputStream inputStream = connection.getInputStream();
                         ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                        
                        byte[] buffer = new byte[8192];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        contentLength = outputStream.size();
                    }
                }
                
                Duration downloadTime = Duration.between(startTime, Instant.now());
                int completed = completedCount.incrementAndGet();
                
                System.out.printf("[%s] %s 完了 (%d/%d) - %d bytes, %d ms%n",
                    Thread.currentThread().getName(), url, completed, totalCount,
                    contentLength, downloadTime.toMillis());
                
                return new WebPageInfo(url, statusCode, contentLength, downloadTime, null);
                
            } catch (Exception e) {
                Duration downloadTime = Duration.between(startTime, Instant.now());
                System.err.printf("[%s] %s エラー: %s%n",
                    Thread.currentThread().getName(), url, e.getMessage());
                
                return new WebPageInfo(url, -1, 0, downloadTime, e.getMessage());
            }
        }
    }
    
    /**
     * 同期的なバッチダウンロード（固定スレッドプール使用）
     */
    public List<WebPageInfo> downloadBatchSync(List<String> urls, 
                                               int threadPoolSize, 
                                               int timeoutSeconds) 
                                               throws InterruptedException {
        System.out.println("=== 同期的バッチダウンロード開始 ===");
        System.out.printf("URL数: %d, スレッドプール: %d%n", urls.size(), threadPoolSize);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);
        AtomicInteger completedCount = new AtomicInteger(0);
        
        try {
            // 全タスクを投入
            List<Future<WebPageInfo>> futures = new ArrayList<>();
            for (String url : urls) {
                PageDownloader downloader = new PageDownloader(
                    url, timeoutSeconds, completedCount, urls.size());
                Future<WebPageInfo> future = executor.submit(downloader);
                futures.add(future);
            }
            
            // 結果の収集
            List<WebPageInfo> results = new ArrayList<>();
            for (Future<WebPageInfo> future : futures) {
                try {
                    WebPageInfo result = future.get(); // ブロッキング
                    results.add(result);
                } catch (ExecutionException e) {
                    System.err.println("タスク実行エラー: " + e.getCause().getMessage());
                }
            }
            
            return results;
            
        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }
    
    /**
     * 非同期バッチダウンロード（CompletableFuture使用）
     */
    public CompletableFuture<List<WebPageInfo>> downloadBatchAsync(List<String> urls, 
                                                                  int timeoutSeconds) {
        System.out.println("=== 非同期バッチダウンロード開始 ===");
        System.out.printf("URL数: %d%n", urls.size());
        
        // 専用のExecutorServiceを作成
        ExecutorService executor = Executors.newWorkStealingPool();
        AtomicInteger completedCount = new AtomicInteger(0);
        
        // 各URLに対してCompletableFutureを作成
        List<CompletableFuture<WebPageInfo>> futures = urls.stream()
            .map(url -> CompletableFuture.supplyAsync(() -> {
                try {
                    PageDownloader downloader = new PageDownloader(
                        url, timeoutSeconds, completedCount, urls.size());
                    return downloader.call();
                } catch (Exception e) {
                    return new WebPageInfo(url, -1, 0, Duration.ZERO, e.getMessage());
                }
            }, executor))
            .collect(Collectors.toList());
        
        // 全ての非同期タスクが完了したら結果をまとめる
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()))
            .whenComplete((result, throwable) -> {
                executor.shutdown();
            });
    }
    
    /**
     * ダウンロード結果の分析と表示
     */
    public void analyzeResults(List<WebPageInfo> results) {
        System.out.println("\n=== ダウンロード結果分析 ===");
        
        if (results.isEmpty()) {
            System.out.println("結果がありません。");
            return;
        }
        
        // 基本統計
        long successCount = results.stream().mapToLong(r -> r.isSuccess() ? 1 : 0).sum();
        long totalBytes = results.stream()
            .filter(WebPageInfo::isSuccess)
            .mapToLong(WebPageInfo::contentLength)
            .sum();
        
        OptionalDouble avgDownloadTime = results.stream()
            .filter(WebPageInfo::isSuccess)
            .mapToDouble(r -> r.downloadTime().toMillis())
            .average();
        
        System.out.printf("成功率: %d/%d (%.1f%%)%n", 
            successCount, results.size(), 
            (double) successCount / results.size() * 100);
        
        System.out.printf("総ダウンロード容量: %,d bytes (%.2f MB)%n", 
            totalBytes, totalBytes / 1024.0 / 1024.0);
        
        if (avgDownloadTime.isPresent()) {
            System.out.printf("平均ダウンロード時間: %.1f ms%n", avgDownloadTime.getAsDouble());
        }
        
        // エラー分析
        Map<String, Long> errorCounts = results.stream()
            .filter(r -> !r.isSuccess())
            .collect(Collectors.groupingBy(
                r -> r.errorMessage() != null ? r.errorMessage() : "HTTP " + r.statusCode(),
                Collectors.counting()));
        
        if (!errorCounts.isEmpty()) {
            System.out.println("\nエラー内訳:");
            errorCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> 
                    System.out.printf("  %s: %d件%n", entry.getKey(), entry.getValue()));
        }
        
        // 速度ランキング（成功したもののみ）
        System.out.println("\n速度ランキング (上位5件):");
        results.stream()
            .filter(WebPageInfo::isSuccess)
            .sorted(Comparator.comparing(WebPageInfo::downloadTime))
            .limit(5)
            .forEach(info -> 
                System.out.printf("  %s: %d ms (%,d bytes)%n",
                    info.url(), info.downloadTime().toMillis(), info.contentLength()));
    }
    
    /**
     * デモンストレーション
     */
    public static void demonstrateWebCrawling() {
        WebCrawlerSystem crawler = new WebCrawlerSystem();
        
        // テスト用のURL（実際のWebサイトではサーバーへの負荷を考慮すること）
        List<String> testUrls = Arrays.asList(
            "https://httpbin.org/delay/1",
            "https://httpbin.org/status/200",
            "https://httpbin.org/status/404", 
            "https://httpbin.org/bytes/1024",
            "https://httpbin.org/html",
            "https://httpbin.org/json",
            "https://httpbin.org/xml"
        );
        
        try {
            // 1. 同期的なバッチ処理
            long startTime = System.currentTimeMillis();
            List<WebPageInfo> syncResults = crawler.downloadBatchSync(testUrls, 3, 10);
            long syncTime = System.currentTimeMillis() - startTime;
            
            System.out.printf("\n同期処理時間: %d ms%n", syncTime);
            crawler.analyzeResults(syncResults);
            
            // 2. 非同期バッチ処理
            System.out.println("\n" + "=".repeat(50));
            startTime = System.currentTimeMillis();
            
            CompletableFuture<List<WebPageInfo>> asyncFuture = 
                crawler.downloadBatchAsync(testUrls, 10);
            
            // 非同期処理の完了を待機
            List<WebPageInfo> asyncResults = asyncFuture.get(30, TimeUnit.SECONDS);
            long asyncTime = System.currentTimeMillis() - startTime;
            
            System.out.printf("\n非同期処理時間: %d ms%n", asyncTime);
            crawler.analyzeResults(asyncResults);
            
            // パフォーマンス比較
            System.out.println("\n=== パフォーマンス比較 ===");
            System.out.printf("同期処理: %d ms%n", syncTime);
            System.out.printf("非同期処理: %d ms%n", asyncTime);
            System.out.printf("速度向上: %.2fx%n", (double) syncTime / asyncTime);
            
        } catch (Exception e) {
            System.err.println("デモ実行エラー: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        demonstrateWebCrawling();
    }
}
```

**このWebクローラシステムの特徴:**

1. **ExecutorService の使い分け**:
   - `newFixedThreadPool()`: 同期的なバッチ処理用
   - `newWorkStealingPool()`: 非同期処理での負荷分散

2. **Callable と Future**:
   - 戻り値のあるタスクの実装
   - `Future.get()` による結果の取得

3. **CompleテーブルFuture**:
   - 現代的な非同期プログラミング
   - 複数の非同期タスクの組み合わせ

4. **エラーハンドリング**:
   - ネットワークエラーの適切な処理
   - タイムアウトとリソース管理

5. **パフォーマンス分析**:
   - 実行時間の測定と比較
   - 統計情報の算出と表示

6. **実用的な設計**:
   - リソースリークの防止
   - 適切なシャットダウン処理

### ExecutorServiceとスレッドプール

`ExecutorService`は、タスクの投入とスレッドプールの管理を行うためのインターフェイスです。スレッドプールは、あらかじめ作成された再利用可能なスレッドの集まりです。

#### ExecutorServiceの利点

* **パフォーマンス向上**: スレッドの生成・破棄コストを削減できます
* **リソース管理**: 作成されるスレッド数を制限し、システムの安定性を高めます
* **コードの簡略化**: タスクの実行と管理が容易になります

#### ExecutorServiceの作成

`Executors`クラスが提供するファクトリメソッドを利用して、一般的な`ExecutorService`を簡単に作成できます。

* `Executors.newFixedThreadPool(int nThreads)`: 固定数のスレッドを持つプール
* `Executors.newCachedThreadPool()`: 必要に応じてスレッドを新規作成・再利用するプール
* `Executors.newSingleThreadExecutor()`: 1つのスレッドのみでタスクを順次実行するプール

### タスクの投入と非同期処理の結果取得

#### execute(Runnable): 戻り値のないタスク

戻り値が不要なタスクは`execute()`メソッドで投入します。

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorExample {
    public static void main(String[] args) {
        // 3つのスレッドを持つスレッドプールを作成
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // 10個のタスクを投入
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            Runnable task = () -> {
                System.out.println("Task " + taskId + " is being executed by " + 
                                  Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            };
            executor.execute(task);
        }

        executor.shutdown();
    }
}
```

#### submit(Callable)とFuture: 戻り値のあるタスク

計算結果などの戻り値が必要なタスクや、例外をスローする可能性があるタスクには、`Callable<V>`インターフェイスを使用します。`submit()`メソッドで`Callable`を投入すると、非同期処理の結果を表す`Future<V>`オブジェクトが即座に返されます。

```java
import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // 戻り値のあるCallableタスクを定義（1から100までの合計を計算）
        Callable<Integer> task = () -> {
            System.out.println("Calculating sum...");
            Thread.sleep(2000); // 時間のかかる計算を模倣
            int sum = 0;
            for (int i = 1; i <= 100; i++) {
                sum += i;
            }
            return sum;
        };

        // Callableタスクを投入し、Futureオブジェクトを受け取る
        Future<Integer> future = executor.submit(task);

        System.out.println("他の処理を実行中です...");

        // Future.get()を呼び出すと、タスクが完了するまでブロック(待機)し、結果を返す
        Integer result = future.get();
        System.out.println("計算結果: " + result);

        executor.shutdown();
    }
}
```

### ExecutorServiceのシャットダウン

`ExecutorService`を使用し終えたら、必ずシャットダウン処理を行う必要があります。そうしないと、JVMが終了しません。

```java
ExecutorService executor = Executors.newFixedThreadPool(2);
// ...タスクの投入...

try {
    System.out.println("シャットダウンを開始します。");
    executor.shutdown(); // 新規タスクの受付を停止
    
    // 1分以内にすべてのタスクが完了するのを待つ
    if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        System.err.println("タスクがタイムアウトしました。強制終了します。");
        executor.shutdownNow(); // 強制的にシャットダウンを試行
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}
System.out.println("シャットダウンが完了しました。");
```

## 14.6 実践例：マルチスレッド徒競走シミュレーション

実際のマルチスレッドプログラムの例として、徒競走をシミュレーションするプログラムを作成してみましょう。

### 選手レコード

```java
/**
 * アスリートレコード
 * @param name 選手名
 * @param speed 速度[m/s]
 */
public record Athlete(String name, double speed) {}
```

### 走行レーンクラス（Runnableの実装）

```java
import java.text.DecimalFormat;

/**
 * 走行レーン（一人のAthleteを保持するトラックの1レーンをイメージ）
 */
public class Lane implements Runnable {
    private final Athlete athlete;
    private final int runningDistance;
    private double time;

    public Lane(Athlete athlete, int runningDistance) {
        this.athlete = athlete;
        this.runningDistance = runningDistance;
    }

    public Athlete getAthlete() {
        return athlete;
    }

    public double getTime() {
        return time;
    }

    @Override
    public void run() {
        DecimalFormat df = new DecimalFormat("0.00");

        System.out.println(athlete.name() + ":スタート(速度: " + 
                          df.format(athlete.speed()) + "[m/s])");

        // 走る時間を計算
        double t = runningDistance / athlete.speed();
        long mills = Math.round(t * 1000);

        try {
            // スレッドを止めて走っている演出
            Thread.sleep(mills);
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // タイムを設定
        this.time = t;

        System.out.println(athlete.name() + ":ゴール タイム:" + 
                          df.format(t) + "[s]");
    }
}
```

### 徒競走クラス

```java
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 徒競走クラス
 */
public class Footrace {
    public static final int RUNNING_DISTANCE = 30;

    private final List<Athlete> athletes = new ArrayList<>();

    public List<Athlete> getAthletes() {
        return athletes;
    }

    public void start() {
        System.out.println(RUNNING_DISTANCE + "メートル走");

        // 走行レーンの設定
        List<Lane> lanes = new ArrayList<>();
        for (Athlete ath : athletes) {
            lanes.add(new Lane(ath, RUNNING_DISTANCE));
        }

        // Runnableインターフェイスを実装したクラスは、Threadクラスにラッピングする必要がある
        List<Thread> threads = new ArrayList<>();
        lanes.forEach(l -> threads.add(new Thread(l)));

        // 選手一人ずつ走る（マルチスレッドで同時スタート）
        for (Thread t : threads) {
            t.start();
        }

        // 各選手が走り終えるのを待つ
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // タイムでソート
        Collections.sort(lanes, (Lane a, Lane b) -> 
            Double.compare(a.getTime(), b.getTime()));

        System.out.println("=== 結果発表 ===");
        int rank = 1;
        for (Lane l : lanes) {
            System.out.printf("第%d位\t%s\t%.2f[s]%s", 
                             rank++, l.getAthlete().name(), l.getTime(), 
                             System.lineSeparator());
        }
    }

    public static void main(String[] args) {
        Footrace race = new Footrace();
        // 選手を何人か作成（速度は2.0〜6.0[m/s]でランダム）
        race.getAthletes().add(new Athlete("たろう", 
            ThreadLocalRandom.current().nextDouble(2, 6)));
        race.getAthletes().add(new Athlete("じろう", 
            ThreadLocalRandom.current().nextDouble(2, 6)));
        race.getAthletes().add(new Athlete("さぶろう", 
            ThreadLocalRandom.current().nextDouble(2, 6)));

        race.start();
    }
}
```

実行すると、以下のように3人の選手がほぼ同時にスタートし、それぞれの速度に応じてゴールするマルチスレッドプログラムとなります：

```
30メートル走
たろう:スタート(速度: 4.39[m/s])
さぶろう:スタート(速度: 3.02[m/s])
じろう:スタート(速度: 3.85[m/s])
たろう:ゴール タイム:6.83[s]
じろう:ゴール タイム:7.78[s]
さぶろう:ゴール タイム:9.93[s]
=== 結果発表 ===
第1位	たろう	6.83[s]
第2位	じろう	7.78[s]
第3位	さぶろう	9.93[s]
```

## まとめ

本章では、Javaにおけるマルチスレッドプログラミングの基礎から実践的な応用まで学習しました。スレッドの作成方法、同期制御、Executorフレームワークの使用方法を理解することで、効率的で安全な並行プログラムを作成できるようになりました。

マルチスレッドプログラミングは強力な技術ですが、競合状態やデッドロックなどの問題を引き起こす可能性があります。適切な設計と同期制御を心がけ、十分なテストを行うことが重要です。