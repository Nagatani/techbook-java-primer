# 第16章 マルチスレッドプログラミング

## 📋 本章の学習目標

### 前提知識
**必須前提**：
- 第13章までの実用的なJavaアプリケーション開発能力
- 同期処理と非同期処理の概念的理解
- パフォーマンスとスケーラビリティへの関心

**システム理解前提**：
- OSのプロセスとスレッドの基本概念
- CPUとメモリアーキテクチャの基本理解

### 学習目標
**知識理解目標**：
- マルチスレッドプログラミングの必要性と利点
- 並行処理の課題（競合状態、デッドロック）と対策
- java.util.concurrentパッケージの設計思想
- スレッドプールとExecutorServiceの概念

**技能習得目標**：
- スレッドの生成と管理
- 同期化メカニズム（synchronized、Lock、Atomic変数）の使用
- ExecutorServiceを使った効率的なタスク管理
- 並行コレクションの活用

**並行プログラミング能力目標**：
- 安全で効率的な並行処理プログラムの設計・実装
- 性能向上を目的とした並列化の実装
- デッ���ロック等の問題の回避と解決

**到達レベルの指標**：
- 基本的な並行処理プログラムが安全に実装できる
- ExecutorServiceを使った実用的な非同期処理が実装できる
- 並行処理特有の問題を理解し、適切な対策が取れる
- マルチコア環境での性能向上を実現するプログラムが作成できる

---

## 📝 章末演習

本章で学んだマルチスレッドプログラミングの概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- マルチスレッドプログラミングの基本概念
- ThreadクラスとRunnableインターフェイスの使い分け
- 同期処理（synchronized、Lock）の理解と実装
- java.util.concurrentパッケージの活用
- スレッドプールとExecutorServiceの使用
- 非同期プログラミング（CompletableFuture）による効率的な並行処理

### 📁 課題の場所
演習課題は `exercises/chapter16/` ディレクトリに用意されています：

```
exercises/chapter16/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── BasicThreading.java
│   ├── SynchronizedCounter.java
│   ├── ConcurrentCollections.java
│   └── ProducerConsumer.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 🚀 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. TODOコメントを参考に実装
4. スレッドセーフティを常に意識し、データ競合を防ぐ
5. ExecutorServiceでスレッドプールを活用し、効率的な並行処理を実現する

基本課題が完了したら、`advanced/`の発展課題でより高度な並行プログラミングに挑戦してみましょう！

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

public class ExecutorExample {
    public static void main(String[] args) {
        // 3つのスレッドを持つスレッドプールを作成
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " is being executed by " + Thread.currentThread().getName());
            });
        }

        // ExecutorServiceをシャットダウン
        executor.shutdown();
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

## まとめ

-   **マルチスレッド**は、複数の処理を並行して実行し、アプリケーションのパフォーマンスや応答性を向上させる技術です。
-   タスクの定義には、柔軟性の高い`Runnable`インターフェイス（特にラムダ式）の使用が推奨されます。
-   複数のスレッドが共有リソースにアクセスする際は、`synchronized`などによる**同期制御**が必要です。
-   **Executorフレームワーク**とスレッドプールを利用することで、スレッド管理を安全かつ効率的に行うことができます。

マルチスレッドプログラミングは、現代のアプリケーション開発において不可欠なスキルの1つです。