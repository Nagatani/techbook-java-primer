# 第14章 マルチスレッドプログラミング

現代のマルチコアプロセッサを効率的に活用するために、マルチスレッドプログラミングは必須の技術です。この章では、Javaにおけるマルチスレッドの基礎から実践的な応用まで学習します。

## 14.1 マルチスレッドプログラミングの基礎

### スレッドとは

スレッドとは、プログラム内での処理の流れを表す実行単位です。通常、1つのプログラムは1つのスレッド（メインスレッド）で実行されますが、マルチスレッドとは、1つのプログラム内で複数のスレッドを同時に実行し、処理を並行して進める技術です。

### マルチスレッドのメリット

現代のコンピュータは、複数のコアを持つCPU（マルチコアプロセッサ）を搭載するのが一般的です。マルチスレッドプログラミングは、この複数のコアを効率的に活用し、アプリケーションのパフォーマンスを向上させるための重要な技術です。

主なメリットは以下の通りです：

* **パフォーマンスの向上**: 時間のかかる処理を複数のスレッドに分割し、同時に実行することで、全体の処理時間を短縮できます
* **応答性の維持**: GUIアプリケーションなどで、時間のかかる処理をバックグラウンドのスレッドに任せることで、ユーザーインターフェイスの応答性を維持できます
* **リソースの有効活用**: サーバーアプリケーションなどでは、複数のクライアントからのリクエストを同時に処理することで、CPUなどの計算リソースを有効に活用できます

## 14.2 スレッドの作成と実行

Javaにおけるマルチスレッド用プログラムの作成方法には、代表的な2つの方法があります。

### Runnableインターフェイスの実装（推奨）

`Runnable`は、スレッドが実行するタスク（処理内容）を定義するための関数型インターフェイスです。`run()`メソッドを1つだけ持ちます。

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

### synchronizedによる排他制御

この問題を解決するために、Javaは`synchronized`キーワードによる**排他制御 (Mutual Exclusion)** の仕組みを提供します。`synchronized`で保護されたコードブロックは、一度に1つのスレッドしか実行できないことが保証されます。

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

この問題を解決するのが **Executorフレームワーク (`java.util.concurrent`)** です。これは、スレッドの生成・管理を抽象化し、**スレッドプール**を利用して効率的にタスクを実行するための仕組みを提供します。

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

この章では、Javaにおけるマルチスレッドプログラミングの基礎から実践的な応用まで学習しました。スレッドの作成方法、同期制御、Executorフレームワークの使用方法を理解することで、効率的で安全な並行プログラムを作成できるようになりました。

マルチスレッドプログラミングは強力な技術ですが、競合状態やデッドロックなどの問題を引き起こす可能性があります。適切な設計と同期制御を心がけ、十分なテストを行うことが重要です。