# 第15章 マルチスレッドプログラミング - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題のREADMEの実行例は、この解答例を動かした出力です。
課題2の「同期なし」の値だけは、データ競合のため実行のたびに変わります。そのほかの出力は毎回同じです。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: スレッドを作って待つ | `BasicThreading.java` | `Thread`の作成と`start`、`join`で終わるのを待つ |
| 課題2: データ競合と同期 | `SynchronizationDemo.java` | 同期なしと`synchronized`の比較、`AtomicInteger`、`volatile` |
| 課題3: スレッドプールと非同期の処理 | `ExecutorDemo.java` | `ExecutorService`・`Future`、`CompletableFuture` |
| 課題4: 生産者と消費者 | `ProducerConsumer.java` | `BlockingQueue`で受け渡す、終わりの合図 |

## 実行方法

```bash
cd exercises/chapter15/solutions
javac *.java
java BasicThreading
java SynchronizationDemo
java ExecutorDemo
java ProducerConsumer
```

## 実装のポイント

- `run()`を直接呼ぶと、新しいスレッドではなく呼んだスレッドで実行される。`start()`で開始する
- `count++`は「読む・足す・書く」の3つの操作なので、複数のスレッドから同時に行うと結果が失われる（データ競合）。`synchronized`か`AtomicInteger`で防ぐ
- スレッドプールは`shutdown()`で受け付けを終え、`awaitTermination`で終わるのを待つ
- 結果を表示するときは、スレッドが終わる順序に頼らず、結果を集めてから決まった順で表示する。そうすると、出力が毎回同じになる
