# 効果的なデバッグ手法

## 概要

デバッグは、プログラムの問題を特定し、修正するための体系的なプロセスです。効率的なデバッグ手法を身につけることで、開発生産性が大幅に向上します。

## 基本的なデバッグ手法

### 1. printデバッグ

最も基本的で、すぐに使える手法です。

```java
public class Calculator {
    public int divide(int a, int b) {
        System.out.println("divide開始: a=" + a + ", b=" + b);
        
        if (b == 0) {
            System.out.println("エラー: ゼロ除算");
            throw new ArithmeticException("ゼロで除算はできません");
        }
        
        int result = a / b;
        System.out.println("計算結果: " + result);
        
        return result;
    }
}
```

#### 効果的なprintデバッグのコツ

```java
public class DebugHelper {
    private static final boolean DEBUG = true;
    
    public static void debug(String message) {
        if (DEBUG) {
            System.out.println("[DEBUG] " + 
                Thread.currentThread().getName() + " - " + 
                new Date() + ": " + message);
        }
    }
    
    public static void debug(String format, Object... args) {
        if (DEBUG) {
            debug(String.format(format, args));
        }
    }
}

// 使用例
DebugHelper.debug("処理開始: ユーザーID=%d", userId);
DebugHelper.debug("検索結果: %d件", results.size());
```

### 2. ログを使ったデバッグ

プロダクション環境でも使える、より洗練された方法です。

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    public Order processOrder(Long orderId) {
        logger.debug("注文処理開始: orderId={}", orderId);
        
        try {
            Order order = findOrder(orderId);
            logger.trace("注文情報取得: {}", order);
            
            validateOrder(order);
            logger.debug("注文検証完了");
            
            Order processed = executeOrder(order);
            logger.info("注文処理成功: orderId={}, total={}", 
                orderId, processed.getTotal());
            
            return processed;
            
        } catch (Exception e) {
            logger.error("注文処理エラー: orderId={}", orderId, e);
            throw new OrderProcessingException("注文処理に失敗しました", e);
        }
    }
}
```

### 3. アサーションの活用

前提条件や不変条件をチェックします。

```java
public class BankAccount {
    private double balance;
    
    public void withdraw(double amount) {
        // 前提条件
        assert amount > 0 : "引き出し額は正の値である必要があります: " + amount;
        assert amount <= balance : "残高不足: 残高=" + balance + ", 引出額=" + amount;
        
        double oldBalance = balance;
        balance -= amount;
        
        // 事後条件
        assert balance >= 0 : "残高が負になりました: " + balance;
        assert Math.abs((oldBalance - amount) - balance) < 0.001 : "計算エラー";
    }
    
    // JVMオプション: -ea または -enableassertions でアサーションを有効化
}
```

## IDEデバッガの活用

### 1. ブレークポイントの種類

```java
public class DebugExample {
    private List<String> items = new ArrayList<>();
    
    public void processItems() {
        // 1. 行ブレークポイント
        for (String item : items) {  // ここで停止
            
            // 2. 条件付きブレークポイント
            if (item.length() > 10) {  // item.length() > 10 の時のみ停止
                process(item);
            }
        }
    }
    
    // 3. メソッドブレークポイント
    public void process(String item) {  // メソッド開始時に停止
        // 処理
    }
    
    // 4. 例外ブレークポイント
    public void riskyOperation() {
        try {
            // NullPointerExceptionが発生したら自動停止
            String result = null;
            result.length();
        } catch (Exception e) {
            // 処理
        }
    }
}
```

### 2. ウォッチ式と評価

```java
public class ComplexObject {
    private Map<String, List<Integer>> data = new HashMap<>();
    
    public void analyze() {
        // デバッガで以下の式を評価
        // data.size()
        // data.keySet()
        // data.values().stream().mapToInt(List::size).sum()
        
        for (Map.Entry<String, List<Integer>> entry : data.entrySet()) {
            // ウォッチ式の例:
            // entry.getValue().stream().filter(n -> n > 100).count()
            processEntry(entry);
        }
    }
}
```

### 3. ステップ実行

```java
public class StepDebugExample {
    public void mainProcess() {
        String input = getUserInput();      // Step Over (F8)
        String processed = process(input);  // Step Into (F7)
        saveResult(processed);              // Step Out (Shift+F8)
    }
    
    private String process(String input) {
        validate(input);        // Step Into でこのメソッド内部へ
        return transform(input);
    }
}
```

## 高度なデバッグ技法

### 1. バイナリサーチデバッグ

問題のある範囲を半分ずつ絞り込んでいく手法です。

```java
public class BinarySearchDebug {
    public void complexProcess(List<Data> dataList) {
        // 問題: どこかでデータが破損する
        
        int size = dataList.size();
        debugCheckpoint("開始", dataList);
        
        // 前半処理
        for (int i = 0; i < size / 2; i++) {
            processData(dataList.get(i));
        }
        debugCheckpoint("中間点", dataList);
        
        // 後半処理
        for (int i = size / 2; i < size; i++) {
            processData(dataList.get(i));
        }
        debugCheckpoint("終了", dataList);
    }
    
    private void debugCheckpoint(String label, List<Data> data) {
        System.out.println(label + ": " + validateData(data));
    }
}
```

### 2. デバッグプロキシパターン

```java
public interface Service {
    String process(String input);
}

public class DebugProxy implements Service {
    private final Service target;
    private final Logger logger = LoggerFactory.getLogger(DebugProxy.class);
    
    public DebugProxy(Service target) {
        this.target = target;
    }
    
    @Override
    public String process(String input) {
        long startTime = System.currentTimeMillis();
        logger.debug("メソッド呼び出し: process({})", input);
        
        try {
            String result = target.process(input);
            long duration = System.currentTimeMillis() - startTime;
            logger.debug("正常終了: 結果={}, 実行時間={}ms", result, duration);
            return result;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("異常終了: 実行時間={}ms", duration, e);
            throw e;
        }
    }
}
```

### 3. メモリダンプ解析

```java
public class MemoryDebug {
    public static void analyzeMemory() {
        // ヒープダンプをプログラムから生成
        try {
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            HotSpotDiagnosticMXBean mxBean = ManagementFactory.newPlatformMXBeanProxy(
                server, 
                "com.sun.management:type=HotSpotDiagnostic", 
                HotSpotDiagnosticMXBean.class
            );
            mxBean.dumpHeap("heap_dump_" + System.currentTimeMillis() + ".hprof", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // JVMオプション: -XX:+HeapDumpOnOutOfMemoryError
    // ツール: jmap, jhat, Eclipse MAT, VisualVM
}
```

## 並行処理のデバッグ

### 1. スレッドダンプの活用

```java
public class ThreadDebug {
    public static void printThreadDump() {
        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        
        for (Map.Entry<Thread, StackTraceElement[]> entry : stackTraces.entrySet()) {
            Thread thread = entry.getKey();
            StackTraceElement[] stack = entry.getValue();
            
            System.out.println("\nThread: " + thread.getName() + 
                " [" + thread.getState() + "]");
            for (StackTraceElement element : stack) {
                System.out.println("\t" + element);
            }
        }
    }
    
    // JVMからの取得: kill -3 <pid> (Unix) または Ctrl+Break (Windows)
}
```

### 2. 同期問題のデバッグ

```java
public class ConcurrencyDebug {
    private final Object lock = new Object();
    private int counter = 0;
    
    public void increment() {
        // デバッグ情報付き同期
        long threadId = Thread.currentThread().getId();
        System.out.println("Thread " + threadId + " waiting for lock");
        
        synchronized (lock) {
            System.out.println("Thread " + threadId + " acquired lock");
            int oldValue = counter;
            counter++;
            System.out.println("Thread " + threadId + 
                ": " + oldValue + " -> " + counter);
        }
        
        System.out.println("Thread " + threadId + " released lock");
    }
}
```

## パフォーマンスデバッグ

### 1. 実行時間の計測

```java
public class PerformanceDebug {
    private static class TimingInfo {
        private final long startTime = System.nanoTime();
        private long lastCheckpoint = startTime;
        
        public void checkpoint(String label) {
            long current = System.nanoTime();
            long fromStart = (current - startTime) / 1_000_000;
            long fromLast = (current - lastCheckpoint) / 1_000_000;
            
            System.out.printf("%s: %dms (累計: %dms)%n", 
                label, fromLast, fromStart);
            lastCheckpoint = current;
        }
    }
    
    public void complexOperation() {
        TimingInfo timing = new TimingInfo();
        
        loadData();
        timing.checkpoint("データ読み込み");
        
        processData();
        timing.checkpoint("データ処理");
        
        saveResults();
        timing.checkpoint("結果保存");
    }
}
```

### 2. メモリ使用量の監視

```java
public class MemoryMonitor {
    private static final Runtime runtime = Runtime.getRuntime();
    
    public static void printMemoryUsage(String label) {
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        System.out.printf("%s - メモリ使用量: %.2f MB / %.2f MB%n",
            label,
            usedMemory / 1024.0 / 1024.0,
            totalMemory / 1024.0 / 1024.0
        );
    }
    
    public void memoryIntensiveOperation() {
        printMemoryUsage("処理開始前");
        
        List<byte[]> arrays = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            arrays.add(new byte[1024 * 1024]); // 1MB
            if (i % 10 == 0) {
                printMemoryUsage("配列 " + i + " 個作成後");
            }
        }
        
        printMemoryUsage("処理完了後");
    }
}
```

## デバッグのベストプラクティス

### 1. 再現可能な環境の構築

```java
@Test
public void debuggableTest() {
    // 固定シードで乱数を初期化
    Random random = new Random(12345);
    
    // 固定時刻でテスト
    Clock fixedClock = Clock.fixed(
        Instant.parse("2024-01-01T00:00:00Z"), 
        ZoneOffset.UTC
    );
    
    // テスト実行
    MyService service = new MyService(random, fixedClock);
    Result result = service.process();
    
    // 期待値と比較
    assertEquals(expectedResult, result);
}
```

### 2. 問題の最小化

```java
public class MinimalReproduction {
    // 元の複雑なコード
    public void complexBuggyMethod(ComplexInput input) {
        // 100行のコード...
    }
    
    // 問題を再現する最小限のコード
    @Test
    public void reproduceIssue() {
        // 問題を引き起こす最小限の入力
        String problematicData = "特定の文字列";
        
        // 問題の核心部分だけを抽出
        int result = processCore(problematicData);
        
        // 問題の確認
        assertNotEquals(-1, result, "予期しない結果");
    }
}
```

### 3. デバッグ情報の整理

```java
public class DebugReport {
    private final StringBuilder report = new StringBuilder();
    
    public void addSection(String title, Object... details) {
        report.append("\n=== ").append(title).append(" ===\n");
        for (int i = 0; i < details.length; i += 2) {
            report.append(details[i]).append(": ").append(details[i + 1]).append("\n");
        }
    }
    
    public void generateReport(Exception e) {
        addSection("エラー情報",
            "種類", e.getClass().getName(),
            "メッセージ", e.getMessage(),
            "発生時刻", new Date()
        );
        
        addSection("環境情報",
            "Javaバージョン", System.getProperty("java.version"),
            "OS", System.getProperty("os.name"),
            "メモリ", Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB"
        );
        
        System.out.println(report.toString());
    }
}
```

## 関連項目

- [stack-trace-reading.md](stack-trace-reading.md)
- プロファイリングツールガイド
- ログ設計のベストプラクティス
- テスト駆動デバッグ