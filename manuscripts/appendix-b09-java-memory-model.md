# 付録B.09: Java Memory ModelとHappens-Before関係

## 概要

本付録では、Javaの並行プログラミングにおける最も重要かつ難解な概念の一つである、Java Memory Model（JMM）とHappens-Before関係について詳細に解説します。これらの概念を理解することで、マルチスレッドプログラムの正確性を保証し、微妙な並行性バグを回避できるようになります。

**対象読者**: マルチスレッドプログラミングの基礎を理解し、より深い理解を求める開発者  
**前提知識**: 第16章「マルチスレッドプログラミング」の内容、基本的な並行処理の概念  
**関連章**: 第16章、第6章（不変性とfinal）

## なぜJava Memory Modelの理解が重要なのか

### 実際の並行性バグ事例

**事例1: 可視性問題による無限ループ**
```java
public class VisibilityBug {
    private boolean stopRequested = false;
    
    public void backgroundWork() {
        while (!stopRequested) {
            // 何らかの処理
            doWork();
        }
        System.out.println("停止しました"); // 実行されない場合がある
    }
    
    public void requestStop() {
        stopRequested = true; // 他のスレッドから見えない可能性
    }
}
```
**問題**: `stopRequested`の変更が他のスレッドから見えず、無限ループが発生
**実際の障害**: 某ECサイトでバッチ処理が停止せず、サービス影響

**事例2: 初期化の見切り発車問題**
```java
public class LazyInitialization {
    private ExpensiveObject instance;
    
    public ExpensiveObject getInstance() {
        if (instance == null) {
            instance = new ExpensiveObject(); // 危険：部分的に構築されたオブジェクト
        }
        return instance;
    }
}
```
**問題**: オブジェクト構築の途中で他のスレッドから参照される可能性
**影響**: NullPointerExceptionや不正な状態のオブジェクト使用

**事例3: Double-Checked Lockingの失敗**
```java
public class BrokenDoubleCheckedLocking {
    private volatile ExpensiveObject instance; // volatileがないと危険
    
    public ExpensiveObject getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = new ExpensiveObject(); // リオーダリングの危険
                }
            }
        }
        return instance;
    }
}
```

### ビジネスへの深刻な影響

**実際のシステム障害:**
- **金融取引システム**: データ競合により残高計算に誤差が発生
- **在庫管理システム**: 可視性問題で在庫数が正しく更新されず
- **リアルタイムゲーム**: メモリオーダリング問題でゲーム状態の不整合

**損失の例:**
- **Knight Capital**: アルゴリズムの並行性バグで45分間で4億4000万ドルの損失
- **某証券会社**: 並行処理バグによる誤発注で数十億円の損失

**デバッグの困難性:**
- **再現困難**: 特定の条件でのみ発生
- **タイミング依存**: デバッガーを使うと問題が隠れる
- **プラットフォーム依存**: CPU架構により動作が変わる

---

## メモリモデルの基礎

### なぜメモリモデルが必要なのか - パフォーマンスと正確性のトレードオフ

現代のコンピュータシステムは、パフォーマンスを最大化するために様々な最適化を行います：

```java
// プログラマの意図したコード
class Example {
    int x = 0;
    boolean flag = false;
    
    void writer() {
        x = 42;        // 1
        flag = true;   // 2
    }
    
    void reader() {
        if (flag) {    // 3
            int r = x; // 4
            // rは42であることが期待される...本当に？
        }
    }
}
```

### ハードウェアレベルの最適化

現代のCPUとメモリシステムは以下の最適化を行います：

1. **命令の並べ替え（リオーダリング）**
   ```
   元の順序: store x, 42 → store flag, true
   最適化後: store flag, true → store x, 42
   ```

2. **CPUキャッシュ**
   ```
   CPU1 Cache: x=42, flag=true
   CPU2 Cache: x=0, flag=false （古い値）
   Main Memory: 更新が遅延
   ```

3. **ストアバッファ**
   ```
   CPU → Store Buffer → L1 Cache → L2 Cache → Main Memory
         （書き込み遅延）
   ```

### Java Memory Modelの役割

JMMは、これらのハードウェア最適化の存在下で、プログラムの振る舞いを定義します：

```java
// JMMが保証する最小限の順序関係
public class MemoryModelExample {
    // volatileはメモリ可視性を保証
    volatile boolean ready = false;
    int data = 0;
    
    void producer() {
        data = 42;          // 1: 通常の書き込み
        ready = true;       // 2: volatile書き込み
    }
    
    void consumer() {
        if (ready) {        // 3: volatile読み込み
            int r = data;   // 4: 通常の読み込み
            // JMMはr == 42を保証する
        }
    }
}
```

---

## Happens-Before関係の詳細

### Happens-Beforeの定義

Happens-Before（HB）関係は、メモリ操作の順序を定義する部分順序関係です：

```
A happens-before B の場合：
- Aの効果はBから観測可能
- AとBの間に因果関係が成立
```

### 基本的なHappens-Before規則

```java
// 1. プログラム順序規則
class ProgramOrder {
    void example() {
        int a = 1;      // 1
        int b = 2;      // 2
        int c = a + b;  // 3
        // 同一スレッド内: 1 HB 2, 2 HB 3, 1 HB 3
    }
}

// 2. モニターロック規則
class MonitorLock {
    Object lock = new Object();
    int shared = 0;
    
    void writer() {
        synchronized(lock) {
            shared = 42;  // unlock HB 次のlock
        }
    }
    
    void reader() {
        synchronized(lock) {
            int r = shared;  // 前のunlock HB このlock
        }
    }
}

// 3. volatile変数規則
class VolatileRule {
    volatile long timestamp = 0;
    String message = "";
    
    void publish() {
        message = "Hello";        // 1
        timestamp = System.nanoTime(); // 2: volatile write
        // 1 HB 2
    }
    
    void consume() {
        long t = timestamp;       // 3: volatile read
        String m = message;       // 4
        // 2 HB 3, therefore 1 HB 4
    }
}

// 4. スレッド開始規則
class ThreadStart {
    int data = 0;
    
    void example() {
        data = 42;                // 1
        Thread t = new Thread(() -> {
            int r = data;         // 3
            // r == 42 が保証される
        });
        t.start();                // 2
        // 1 HB 2 HB 3
    }
}

// 5. スレッド終了規則
class ThreadJoin {
    class Worker extends Thread {
        int result = 0;
        
        public void run() {
            result = compute();   // 1
        }
    }
    
    void example() throws InterruptedException {
        Worker w = new Worker();
        w.start();
        w.join();                 // 2
        int r = w.result;         // 3
        // 1 HB 2 HB 3
    }
}
```

### Happens-Beforeの推移性

```java
// HB関係は推移的
class Transitivity {
    volatile int x = 0;
    int y = 0;
    
    void writer() {
        y = 1;           // A
        x = 1;           // B (volatile write)
    }
    
    void reader() {
        int r1 = x;      // C (volatile read)
        int r2 = y;      // D
        // A HB B (program order)
        // B HB C (volatile)
        // C HB D (program order)
        // Therefore: A HB D (transitivity)
    }
}
```

---

## メモリバリアと実装詳細

### メモリバリアの種類

```java
// JVMが使用する主なメモリバリア
public class MemoryBarriers {
    // 1. LoadLoad Barrier
    // Load1; LoadLoad; Load2
    // Load1の完了後にLoad2を実行
    
    // 2. StoreStore Barrier
    // Store1; StoreStore; Store2
    // Store1の完了後にStore2を実行
    
    // 3. LoadStore Barrier
    // Load1; LoadStore; Store2
    // Load1の完了後にStore2を実行
    
    // 4. StoreLoad Barrier (最も強力)
    // Store1; StoreLoad; Load2
    // Store1の完了後にLoad2を実行
}

// volatileアクセスのバリア配置
class VolatileBarriers {
    volatile int v;
    
    void volatileWrite(int value) {
        // StoreStore Barrier
        v = value;
        // StoreLoad Barrier
    }
    
    int volatileRead() {
        // LoadLoad Barrier
        int result = v;
        // LoadStore Barrier
        return result;
    }
}
```

### x86アーキテクチャでの実装

```java
// x86は比較的強いメモリモデル
class X86Implementation {
    // x86での実装例（疑似コード）
    void volatileWrite(int value) {
        // mov [address], value
        // mfence  // StoreLoadバリアのみ必要
    }
    
    int volatileRead() {
        // mov eax, [address]
        // x86では追加バリア不要（TSO保証）
        return value;
    }
}

// ARMなど弱いメモリモデルでの実装
class ARMImplementation {
    void volatileWrite(int value) {
        // dmb st   // StoreStoreバリア
        // str value, [address]
        // dmb sy   // StoreLoadバリア
    }
    
    int volatileRead() {
        // ldr r0, [address]
        // dmb sy   // LoadLoad/LoadStoreバリア
        return value;
    }
}
```

---

## ロックフリーアルゴリズムの実装

### Compare-And-Swap (CAS) 操作

```java
import java.util.concurrent.atomic.AtomicReference;

// ロックフリースタックの実装
public class LockFreeStack<T> {
    private static class Node<T> {
        final T item;
        final Node<T> next;
        
        Node(T item, Node<T> next) {
            this.item = item;
            this.next = next;
        }
    }
    
    private final AtomicReference<Node<T>> top = 
        new AtomicReference<>();
    
    public void push(T item) {
        Node<T> newHead = new Node<>(item, null);
        while (true) {
            Node<T> currentHead = top.get();
            newHead = new Node<>(item, currentHead);
            if (top.compareAndSet(currentHead, newHead)) {
                return;
            }
            // CAS失敗、リトライ
        }
    }
    
    public T pop() {
        while (true) {
            Node<T> currentHead = top.get();
            if (currentHead == null) {
                return null;
            }
            Node<T> newHead = currentHead.next;
            if (top.compareAndSet(currentHead, newHead)) {
                return currentHead.item;
            }
            // CAS失敗、リトライ
        }
    }
}
```

### ABA問題とその解決

```java
import java.util.concurrent.atomic.AtomicStampedReference;

// ABA問題を解決するスタンプ付き参照
public class ABASafeStack<T> {
    private static class Node<T> {
        final T item;
        final Node<T> next;
        
        Node(T item, Node<T> next) {
            this.item = item;
            this.next = next;
        }
    }
    
    private final AtomicStampedReference<Node<T>> top = 
        new AtomicStampedReference<>(null, 0);
    
    public void push(T item) {
        while (true) {
            int[] stampHolder = new int[1];
            Node<T> currentHead = top.get(stampHolder);
            int currentStamp = stampHolder[0];
            
            Node<T> newHead = new Node<>(item, currentHead);
            if (top.compareAndSet(currentHead, newHead, 
                                 currentStamp, currentStamp + 1)) {
                return;
            }
        }
    }
    
    public T pop() {
        while (true) {
            int[] stampHolder = new int[1];
            Node<T> currentHead = top.get(stampHolder);
            int currentStamp = stampHolder[0];
            
            if (currentHead == null) {
                return null;
            }
            
            Node<T> newHead = currentHead.next;
            if (top.compareAndSet(currentHead, newHead,
                                 currentStamp, currentStamp + 1)) {
                return currentHead.item;
            }
        }
    }
}
```

---

## False Sharingとパフォーマンス最適化

### False Sharingの問題

```java
// False Sharingが発生するコード
class FalseSharingExample {
    static class Counter {
        volatile long value = 0;
    }
    
    // 同じキャッシュラインに配置される可能性
    static Counter[] counters = new Counter[8];
    static {
        for (int i = 0; i < counters.length; i++) {
            counters[i] = new Counter();
        }
    }
}

// パディングによる解決
class PaddedCounter {
    // CPUキャッシュライン（通常64バイト）を考慮
    volatile long p1, p2, p3, p4, p5, p6, p7;  // パディング
    volatile long value = 0;
    volatile long p8, p9, p10, p11, p12, p13, p14;  // パディング
}

// @Contendedアノテーションの使用（Java 8以降）
@sun.misc.Contended
class ContendedCounter {
    volatile long value = 0;
}
```

### 高性能カウンターの実装

```java
import java.util.concurrent.atomic.LongAdder;

// スケーラブルなカウンター実装
public class HighPerformanceCounter {
    // LongAdderは内部的にセル配列を使用
    private final LongAdder counter = new LongAdder();
    
    public void increment() {
        counter.increment();  // 競合を自動的に分散
    }
    
    public long get() {
        return counter.sum();  // すべてのセルを集計
    }
}

// カスタム実装例
class StripedCounter {
    private static final int STRIPES = 
        Runtime.getRuntime().availableProcessors() * 4;
    
    @sun.misc.Contended
    static class Cell {
        volatile long value;
    }
    
    private final Cell[] cells = new Cell[STRIPES];
    
    public StripedCounter() {
        for (int i = 0; i < cells.length; i++) {
            cells[i] = new Cell();
        }
    }
    
    public void increment() {
        int index = (int) (Thread.currentThread().getId() % STRIPES);
        cells[index].value++;
    }
    
    public long get() {
        long sum = 0;
        for (Cell cell : cells) {
            sum += cell.value;
        }
        return sum;
    }
}
```

---

## 実践的なガイドライン

### メモリモデルを意識した設計

1. **可能な限り不変性を活用**
   ```java
   // 不変オブジェクトは同期不要
   public final class ImmutablePoint {
       private final int x, y;
       
       public ImmutablePoint(int x, int y) {
           this.x = x;
           this.y = y;
       }
       // getterのみ、setterなし
   }
   ```

2. **適切な同期プリミティブの選択**
   ```java
   // 用途に応じた選択
   // 単純な状態フラグ: volatile
   // 複雑な状態遷移: synchronized/Lock
   // 高頻度更新: AtomicXXX/LongAdder
   // 読み込み多数: ReadWriteLock
   ```

3. **パフォーマンステスト**
   ```java
   // JMHを使用した正確な測定
   @Benchmark
   public void testVolatile() {
       volatileCounter++;
   }
   
   @Benchmark
   public void testAtomic() {
       atomicCounter.incrementAndGet();
   }
   ```

---

## まとめ

Java Memory Modelとその実装を理解することで：

1. **正確性の保証**: 並行プログラムの正しい動作を保証
2. **パフォーマンス最適化**: 適切な同期機構の選択
3. **デバッグ能力**: 並行性バグの原因特定と修正
4. **高度な実装**: ロックフリーアルゴリズムの開発

これらの知識は、高性能な並行アプリケーションの開発に不可欠です。ただし、過度な最適化は避け、まずは正確性を優先し、実測に基づいて最適化を行うことが重要です。