## 第16章 マルチスレッドプログラミング - Part A: マルチスレッドの基礎

### 20.1 マルチスレッドプログラミングの基礎

スレッドとは、プログラム内での処理の流れを表す実行単位です。通常、1つのプログラムは1つのスレッド（メインスレッド）で実行されます。

**マルチスレッド**とは、1つのプログラム内で複数のスレッドを同時に実行し、処理を並行して進める技術です。

#### なぜマルチスレッドが必要か

現代のコンピュータは、複数のコアを持つCPU（マルチコアプロセッサ）を搭載するのが一般的です。マルチスレッドプログラミングは、この複数のコアを効率的に活用し、アプリケーションのパフォーマンスを向上させるための重要な技術です。

-   **パフォーマンスの向上**: 時間のかかる処理を複数のスレッドに分割し、同時に実行することで、全体の処理時間を短縮できます。
-   **応答性の維持**: GUIアプリケーションなどで、時間のかかる処理をバックグラウンドのスレッドに任せることで、ユーザーインターフェイスが固まるのを防ぎます。
-   **リソースの有効活用**: サーバアプリケーションなどでは、複数のクライアントからのリクエストを同時に処理することで、計算リソースを有効に活用できます。

### 20.2 スレッドの作成と実行

Javaでスレッドを作成するには、主に`Runnable`インターフェイスを実装する方法が推奨されます。

#### `Runnable`インターフェイスの実装（推奨）

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
            System.out.println("ラムダ式でのタスク実行: " + 
                Thread.currentThread().getName());
        }).start();
    }
}
```

`Runnable`を利用する方法は、Javaがクラスの多重継承をサポートしないという制約を受けず、また「タスク（何をするか）」と「スレッド（どう実行するか）」を分離できるため、より柔軟な設計が可能です。

### 20.3 共有リソースと同期制御

複数のスレッドが、同じデータ（オブジェクトや変数）に同時にアクセスすると、予期せぬ問題が発生することがあります。これを**競合状態 (Race Condition)** と呼びます。

#### `synchronized`による排他制御

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

#### 基本的なスレッド操作の例

```java
public class BasicThreadExample {
    public static void main(String[] args) throws InterruptedException {
        // スレッドの作成と開始
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread-1: " + i);
                try {
                    Thread.sleep(1000); // 1秒間スリープ
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Thread-2: " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        
        // スレッドの開始
        thread1.start();
        thread2.start();
        
        // スレッドの完了を待つ
        thread1.join();
        thread2.join();
        
        System.out.println("すべてのスレッドが完了しました");
    }
}
```

#### データ競合の例と対策

```java
// データ競合が発生する例
class UnsafeCounter {
    private int count = 0;
    
    public void increment() {
        count++; // これはアトミックな操作ではない
    }
    
    public int getCount() {
        return count;
    }
}

// スレッドセーフな例
class SafeCounter {
    private int count = 0;
    
    public synchronized void increment() {
        count++; // synchronizedにより同時に1つのスレッドのみアクセス可能
    }
    
    public synchronized int getCount() {
        return count;
    }
}

public class DataRaceExample {
    public static void main(String[] args) throws InterruptedException {
        // 非安全なカウンタのテスト
        UnsafeCounter unsafeCounter = new UnsafeCounter();
        Thread[] threads = new Thread[10];
        
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    unsafeCounter.increment();
                }
            });
            threads[i].start();
        }
        
        // すべてのスレッドの完了を待つ
        for (Thread t : threads) {
            t.join();
        }
        
        System.out.println("非安全なカウンタ: " + unsafeCounter.getCount());
        // 期待値は10,000だが、データ競合により異なる値になる可能性がある
        
        // 安全なカウンタのテスト
        SafeCounter safeCounter = new SafeCounter();
        Thread[] safeThreads = new Thread[10];
        
        for (int i = 0; i < 10; i++) {
            safeThreads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    safeCounter.increment();
                }
            });
            safeThreads[i].start();
        }
        
        for (Thread t : safeThreads) {
            t.join();
        }
        
        System.out.println("安全なカウンタ: " + safeCounter.getCount());
        // 必ず10,000になる
    }
}
```

#### synchronizedブロックの使用

```java
class BankAccount {
    private double balance;
    private final Object lock = new Object();
    
    public BankAccount(double initialBalance) {
        this.balance = initialBalance;
    }
    
    public void deposit(double amount) {
        synchronized (lock) {
            if (amount > 0) {
                balance += amount;
                System.out.println(Thread.currentThread().getName() + 
                    " が " + amount + " 円入金しました。残高: " + balance);
            }
        }
    }
    
    public boolean withdraw(double amount) {
        synchronized (lock) {
            if (amount > 0 && balance >= amount) {
                balance -= amount;
                System.out.println(Thread.currentThread().getName() + 
                    " が " + amount + " 円出金しました。残高: " + balance);
                return true;
            }
            return false;
        }
    }
    
    public double getBalance() {
        synchronized (lock) {
            return balance;
        }
    }
}

public class BankAccountExample {
    public static void main(String[] args) throws InterruptedException {
        BankAccount account = new BankAccount(1000);
        
        // 複数のスレッドが同時に口座にアクセス
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.deposit(100);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "入金スレッド1");
        
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.withdraw(50);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "出金スレッド1");
        
        Thread t3 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                account.deposit(200);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "入金スレッド2");
        
        t1.start();
        t2.start();
        t3.start();
        
        t1.join();
        t2.join();
        t3.join();
        
        System.out.println("最終残高: " + account.getBalance());
    }
}
```

### まとめ

このパートでは、マルチスレッドプログラミングの基礎を学習しました：

- **スレッドの概念**：プログラム内の並行実行単位
- **スレッドの作成**：Runnableインターフェイスを使った実装
- **競合状態**：複数スレッドが共有データに同時アクセスする問題
- **同期制御**：synchronizedキーワードによる排他制御

これらの基礎知識は、より高度な並行処理プログラミングを学ぶための土台となります。次のパートでは、Executorフレームワークを使った、より実践的なスレッド管理について学習します。



次のパート：[Part B - Executorフレームワーク](chapter16b-executor-framework.md)
