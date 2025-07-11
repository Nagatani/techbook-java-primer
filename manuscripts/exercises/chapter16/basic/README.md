# 第16章 マルチスレッドプログラミング - 基礎レベル課題

## 概要
本ディレクトリでは、マルチスレッドプログラミングの基礎的な概念を実践的に学習します。スレッドの作成、同期処理、データ競合の理解と対策を通じて、並行処理の基本を身につけます。

## 課題一覧

### 課題1: スレッドの基本操作
**BasicThreadDemo.java**

Threadクラスの継承とRunnableインターフェイスの実装によるスレッド作成を実践します。

**要求仕様：**
- Threadクラスを継承したカスタムスレッドクラスの作成
- Runnableインターフェイスを実装したタスククラスの作成
- 複数スレッドの並行実行と制御
- スレッドの状態遷移の観察

**実装のポイント：**
```java
// Threadクラスの継承
class MyThread extends Thread {
    @Override
    public void run() {
        // スレッドで実行する処理
    }
}

// Runnableインターフェイスの実装
class MyTask implements Runnable {
    @Override
    public void run() {
        // スレッドで実行する処理
    }
}
```

### 課題2: 共有リソースとsynchronized
**SynchronizedCounter.java**

複数スレッドからアクセスされる共有リソースの同期化を実装します。

**要求仕様：**
- 非同期カウンタによるデータ競合の確認
- synchronizedメソッドによる排他制御の実装
- synchronizedブロックによる細粒度の同期
- データ整合性の検証

**実装のポイント：**
```java
// メソッド全体の同期化
public synchronized void increment() {
    count++;
}

// ブロック単位の同期化
public void increment() {
    synchronized(this) {
        count++;
    }
}
```

### 課題3: スレッド間通信
**ProducerConsumer.java**

wait/notifyを使った基本的なプロデューサー・コンシューマーパターンを実装します。

**要求仕様：**
- 共有バッファを介したスレッド間のデータ受け渡し
- wait/notifyによる協調動作
- バッファのオーバーフロー/アンダーフロー防止
- 正しい同期化による一貫性の保証

**実装のポイント：**
```java
// プロデューサー側
synchronized(buffer) {
    while (buffer.isFull()) {
        buffer.wait();
    }
    buffer.add(item);
    buffer.notifyAll();
}

// コンシューマー側
synchronized(buffer) {
    while (buffer.isEmpty()) {
        buffer.wait();
    }
    Item item = buffer.take();
    buffer.notifyAll();
}
```

### 課題4: volatileキーワードの使用
**VolatileFlag.java**

volatileキーワードを使った軽量な同期を実装します。

**要求仕様：**
- volatileフラグによるスレッド間の状態共有
- メモリ可視性の確認
- 原子性が必要ない場合の適切な使用
- volatileの制限事項の理解

**実装のポイント：**
```java
private volatile boolean running = true;

// 別スレッドから停止を指示
public void stop() {
    running = false;
}

// メインループ
while (running) {
    // 処理を継続
}
```

## 学習のポイント

1. **スレッドのライフサイクル**
   - NEW → RUNNABLE → BLOCKED/WAITING → TERMINATED
   - start()とrun()の違いを理解する

2. **同期化の必要性**
   - データ競合（レースコンディション）の発生条件
   - 同期化のオーバーヘッドとのトレードオフ

3. **デッドロックの回避**
   - ロックの順序を統一する
   - タイムアウトの活用

4. **パフォーマンスの考慮**
   - 同期化の粒度（細かすぎても粗すぎても問題）
   - 必要最小限の同期化

## 提出物

1. 各課題のソースコード（*.java）
2. 実行結果のログまたはスクリーンショット
3. 各課題で学んだことのまとめ（README_submission.md）

## 評価基準

- **正確性（40%）**: プログラムが仕様通りに動作するか
- **同期化の適切性（30%）**: データ競合やデッドロックが発生しないか
- **コードの品質（20%）**: 可読性、適切な命名、コメント
- **理解度（10%）**: 学習内容のまとめが適切か

## 参考リソース

- [Java並行処理プログラミング](https://www.oracle.com/java/technologies/javase/concurrency.html)
- [Java Concurrency in Practice](http://jcip.net/)
- 本書第16章 Part A: マルチスレッドの基礎