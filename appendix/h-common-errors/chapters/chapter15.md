# 第15章 マルチスレッドプログラミング：よくあるエラーと対処法

書籍『Java入門』第15章「マルチスレッドプログラミング」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

本章では、マルチスレッドプログラミングに特有のエラーを扱います。

## 基本的な競合状態

複数のスレッドが同じデータに同時にアクセスし、値が壊れます。

### エラー例

コンパイルできる全文：[chapter15-code/Counter/](chapter15-code/Counter/)

```java
// エラー例
public class Counter {
    private int count = 0;
    
    public void increment() {
        count++; // 非原子的操作
    }
}
```

### 対処法

```java
// synchronized使用
public synchronized void increment() {
    count++;
}

// Atomicクラス使用
private final AtomicInteger count = new AtomicInteger(0);
count.incrementAndGet();
```

### 重要なポイント

- 共有データへのアクセスは同期化が必要
- 単純な演算でもAtomicクラスを検討
- volatileは可視性のみ保証

## デッドロックの基礎

複数のスレッドが互いのロックを待ち、停止します。

### エラー例

```java
// エラー例：ロック順序の不一致
public void method1() {
    synchronized (lock1) {
        synchronized (lock2) {  // method2がlock2を持っていると、ここで待ち続ける
            // 処理
        }
    }
}

public void method2() {
    synchronized (lock2) {  // method1と逆の順序
        synchronized (lock1) {  // method1がlock1を持っていると、ここで待ち続ける
            // 処理
        }
    }
}
```

method1を実行するスレッドがlock1を、method2を実行するスレッドがlock2を取ったところで、互いに相手のロックを待ち続けます。

### 対処法

```java
// 常に同じ順序でロック取得
public void method1() {
    synchronized (lock1) {
        synchronized (lock2) {
            // 処理
        }
    }
}

public void method2() {
    synchronized (lock1) {  // 同じ順序
        synchronized (lock2) {
            // 処理
        }
    }
}
```

### 重要なポイント

- ロック順序を統一する
- 必要最小限のロック範囲
- tryLockでタイムアウト設定

## スレッドセーフでないコレクション

通常のコレクションをマルチスレッドで使っています。

### エラー例

```java
// エラー例
List<String> list = new ArrayList<>();
// 複数スレッドから同時にadd → 要素が失われる、nullが入る、
// 内部の配列を広げる途中でArrayIndexOutOfBoundsExceptionが発生するなど、結果が実行のたびに変わる
// あるスレッドが拡張for文で反復している最中に別のスレッドが変更すると、ConcurrentModificationExceptionになる
```

### 対処法

```java
// 並行コレクション使用
List<String> list = new CopyOnWriteArrayList<>();
Map<String, String> map = new ConcurrentHashMap<>();
Queue<String> queue = new ConcurrentLinkedQueue<>();
```

### 重要なポイント

- java.util.concurrentパッケージを活用
- 読み込み頻度が高い場合はCopyOnWriteを検討
- Collections.synchronizedXXX()は性能に注意

## ExecutorServiceのリソース管理

スレッドプールが正しくシャットダウンされません。

### エラー例

```java
// エラー例
ExecutorService executor = Executors.newFixedThreadPool(5);
executor.submit(task);
// shutdownを呼ばない → リソースリーク
```

### 対処法

```java
ExecutorService executor = Executors.newFixedThreadPool(5);
try {
    executor.submit(task);
} finally {
    executor.shutdown();
    try {
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    } catch (InterruptedException e) {
        executor.shutdownNow();
        Thread.currentThread().interrupt();  // 割り込まれた状態を戻す
    }
}
```

### 重要なポイント

- 必ずfinallyでshutdown
- awaitTerminationでタスク完了を待つ
- タイムアウト時はshutdownNow

## 関連する共通エラー

以下のエラーも本章の内容に関連します。

- **メモリ可視性の問題**（→ 付録B.14）
  - volatileキーワードの正しい使用法
- **InterruptedException**（→ 第15章）
  - スレッドの割り込み処理
- **ConcurrentModificationException**（→ [付録H ConcurrentModificationException](../runtime-exceptions/ConcurrentModificationException.md)）
  - 反復処理中のコレクション変更

## デバッグのヒント

1. デッドロック検出

   ```java
   ThreadMXBean bean = ManagementFactory.getThreadMXBean();
   long[] deadlocked = bean.findDeadlockedThreads();
   if (deadlocked != null) {
       // デッドロック発生
   }
   ```

2. スレッドダンプ
   - jstackコマンドでスレッド状態確認
   - IDEのデバッガーでブレークポイント設定

3. 並行処理のテスト
   - CountDownLatchで同期的にテスト
   - 複数回実行して再現性確認

## さらに学ぶには

- [付録H: ConcurrentModificationException](../runtime-exceptions/ConcurrentModificationException.md)
- 付録B.14: Javaメモリモデル（高度な内容）
