# 第20章 マルチスレッドプログラミング

## 🎯総合演習プロジェクトへのステップ

本章で学ぶマルチスレッド技術は、**総合演習プロジェクト「ToDoリストアプリケーション」** の応答性を向上させ、より快適なユーザー体験を実現するために応用できます。

- **UIの応答性維持**: タスクリストのファイル保存や読み込みは、データ量が多くなると時間がかかり、その間GUIが固まってしまう（フリーズする）可能性があります。これらの重いI/O処理をバックグラウンドのスレッドで実行することで、処理中もユーザーはGUIを操作し続けることができます。
- **安全なUI更新**: バックグラウンドスレッドでの処理が完了した後、その結果（読み込んだタスクリストなど）をGUIに反映させる必要があります。Swingのコンポーネントは、イベントディスパッチスレッド（EDT）からしか安全に操作できません。`SwingUtilities.invokeLater`を使い、UIの更新処理をEDTに依頼することで、スレッドセーフなGUI更新を実現します��
  ```java
  // バックグラウンドスレッドでの処理
  new Thread(() -> {
      List<Task> loadedTasks = loadTasksFromFile(); // 時間のかかる処理
      
      // GUIの更新はEDTで行う
      SwingUtilities.invokeLater(() -> {
          updateTaskListUI(loadedTasks);
      });
  }).start();
  ```

## 20.1 マルチスレッドプログ��ミングの基礎

スレッドとは、プログラム内での処理の流れを表す実行単位です。通常、1つのプログラムは1つのスレッド（メインスレッド）で実行されます。

**マルチスレッド**とは、1つのプログラム内で複数のスレッドを同時に実行し、処理を並行して進める技術です。

### なぜマルチスレッドが必要か

現代のコンピュータは、複数のコアを持つCPU（マルチコアプロセッサ）を搭載するのが一般的です。マルチスレッドプログラミングは、この複数のコアを効率的に活用し、アプリケーションのパフォーマンスを向上させるための重要な技術です。

-   **パフォーマンスの向上**: 時間のかかる処理を複数のスレッドに分割し、同時に実行することで、全体の処理時間を短縮できます。
-   **応答性の維持**: GUIアプリケーションなどで、時間のかかる処理をバックグラウンドのスレッドに任せることで、ユーザーインターフェイスが固まるのを防ぎます。
-   **リソースの有効活用**: サーバアプリケーションなどでは、複数のクライアントからのリクエストを同時に処理することで、計算リソースを有効に活���できます。

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
