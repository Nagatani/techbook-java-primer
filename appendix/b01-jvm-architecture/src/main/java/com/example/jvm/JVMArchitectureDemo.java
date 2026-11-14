package com.example.jvm;

import java.lang.management.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * JVMアーキテクチャとランタイムデータエリアのデモンストレーション
 * JVMの内部構造、メモリ管理、クラスローディングの仕組みを学習
 */
public class JVMArchitectureDemo {
    
    /**
     * クラスローダー階層の調査
     */
    public static class ClassLoaderHierarchyDemo {
        
        public static void demonstrateClassLoaderHierarchy() {
            IO.println("=== ClassLoader Hierarchy Investigation ===");
            
            // システムクラスローダー（アプリケーションクラスローダー）
            ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
            IO.println("System ClassLoader: " + systemLoader);
            IO.println("  Class: " + systemLoader.getClass().getName());
            
            // 拡張クラスローダー（プラットフォームクラスローダー）
            ClassLoader platformLoader = systemLoader.getParent();
            IO.println("\nPlatform ClassLoader: " + platformLoader);
            if (platformLoader != null) {
                IO.println("  Class: " + platformLoader.getClass().getName());
            }
            
            // ブートストラップクラスローダー（nullで表される）
            ClassLoader bootstrapLoader = platformLoader != null ? platformLoader.getParent() : null;
            IO.println("\nBootstrap ClassLoader: " + bootstrapLoader);
            IO.println("  (null means Bootstrap ClassLoader - implemented in native code)");
            
            IO.println("\n--- Class Loader Investigation for Different Classes ---");
            
            // 各種クラスのクラスローダーを確認
            investigateClassLoader("java.lang.String", String.class);
            investigateClassLoader("java.util.HashMap", HashMap.class);
            investigateClassLoader("java.util.concurrent.ConcurrentHashMap", 
                                 java.util.concurrent.ConcurrentHashMap.class);
            investigateClassLoader("com.example.jvm.JVMArchitectureDemo", 
                                 JVMArchitectureDemo.class);
            
            IO.println();
        }
        
        private static void investigateClassLoader(String className, Class<?> clazz) {
            ClassLoader loader = clazz.getClassLoader();
            IO.println("%-45s -> %s".formatted(
                            className, 
                            loader == null ? "Bootstrap ClassLoader" : loader.getClass().getSimpleName()));
        }
        
        /**
         * カスタムクラスローダーのデモンストレーション
         */
        public static class CustomClassLoaderDemo extends ClassLoader {
            private final Map<String, Class<?>> loadedClasses = new HashMap<>();
            private int classLoadCount = 0;
            
            public CustomClassLoaderDemo(ClassLoader parent) {
                super(parent);
            }
            
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                IO.println("Custom ClassLoader: Attempting to load " + name);
                classLoadCount++;
                
                // 実際のクラスロードはスキップし、統計のみ収集
                throw new ClassNotFoundException("Demo ClassLoader - class not found: " + name);
            }
            
            @Override
            protected Class<?> loadClass(String name, boolean resolve) 
                    throws ClassNotFoundException {
                
                IO.println("Custom ClassLoader: loadClass called for " + name);
                
                // 親委譲モデルに従う
                return super.loadClass(name, resolve);
            }
            
            public void printStatistics() {
                IO.println("Custom ClassLoader Statistics:");
                IO.println("  Classes attempted to load: " + classLoadCount);
                IO.println("  Actually loaded classes: " + loadedClasses.size());
            }
            
            public static void demonstrateCustomClassLoader() {
                IO.println("\n--- Custom ClassLoader Demo ---");
                
                CustomClassLoaderDemo customLoader = new CustomClassLoaderDemo(
                    ClassLoader.getSystemClassLoader());
                
                try {
                    // 既存クラスのロード（親に委譲される）
                    Class<?> stringClass = customLoader.loadClass("java.lang.String");
                    IO.println("Successfully loaded: " + stringClass.getName());
                    
                    // 存在しないクラスのロード
                    customLoader.loadClass("com.nonexistent.TestClass");
                    
                } catch (ClassNotFoundException e) {
                    IO.println("Expected exception: " + e.getMessage());
                }
                
                customLoader.printStatistics();
            }
        }
    }
    
    /**
     * ランタイムデータエリアの調査
     */
    public static class RuntimeDataAreasDemo {
        
        /**
         * ヒープメモリの分析
         */
        public static void analyzeHeapMemory() {
            IO.println("=== Heap Memory Analysis ===");
            
            // メモリ管理Beanの取得
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
            
            // ヒープメモリの全体使用状況
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            printMemoryUsage("Heap Memory", heapUsage);
            
            // 非ヒープメモリ（メタスペースなど）の使用状況
            MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
            printMemoryUsage("Non-Heap Memory", nonHeapUsage);
            
            IO.println("\n--- Memory Pool Details ---");
            for (MemoryPoolMXBean pool : memoryPools) {
                MemoryUsage usage = pool.getUsage();
                if (usage != null) {
                    IO.println("%-25s: %s".formatted(
                                    pool.getName(), 
                                    formatMemorySize(usage.getUsed()) + " / " + 
                                    formatMemorySize(usage.getMax())));
                }
            }
            
            IO.println();
        }
        
        private static void printMemoryUsage(String name, MemoryUsage usage) {
            IO.println(name + ":");
            IO.println("  Init:      " + formatMemorySize(usage.getInit()));
            IO.println("  Used:      " + formatMemorySize(usage.getUsed()));
            IO.println("  Committed: " + formatMemorySize(usage.getCommitted()));
            IO.println("  Max:       " + formatMemorySize(usage.getMax()));
        }
        
        private static String formatMemorySize(long bytes) {
            if (bytes < 0) return "undefined";
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
        
        /**
         * スタックフレームの調査
         */
        public static void analyzeStackFrames() {
            IO.println("=== Stack Frame Analysis ===");
            
            // メソッド呼び出しチェーンの作成
            recursiveMethod(5);
        }
        
        private static int recursiveMethod(int depth) {
            if (depth <= 0) {
                printCurrentStackTrace();
                return 1;
            }
            
            int localVariable = depth * 2;
            return localVariable + recursiveMethod(depth - 1);
        }
        
        private static void printCurrentStackTrace() {
            IO.println("Current Stack Trace:");
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            
            for (int i = 0; i < Math.min(stackTrace.length, 8); i++) {
                StackTraceElement element = stackTrace[i];
                IO.println("  [%d] %s.%s(%s:%d)".formatted(
                                i,
                                element.getClassName(),
                                element.getMethodName(),
                                element.getFileName(),
                                element.getLineNumber()));
            }
            IO.println();
        }
        
        /**
         * ガベージコレクション情報の表示
         */
        public static void analyzeGarbageCollection() {
            IO.println("=== Garbage Collection Analysis ===");
            
            List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
            
            for (GarbageCollectorMXBean gcBean : gcBeans) {
                IO.println("GC: " + gcBean.getName());
                IO.println("  Collection Count: " + gcBean.getCollectionCount());
                IO.println("  Collection Time:  " + gcBean.getCollectionTime() + " ms");
                
                String[] memoryPoolNames = gcBean.getMemoryPoolNames();
                IO.println("  Memory Pools: " + Arrays.toString(memoryPoolNames));
                IO.println();
            }
        }
    }
    
    /**
     * メモリ世代とオブジェクトライフサイクルのデモンストレーション
     */
    public static class MemoryGenerationDemo {
        private static final List<Object> longLivedObjects = new ArrayList<>();
        
        public static void demonstrateObjectLifecycle() {
            IO.println("=== Object Lifecycle and Memory Generations ===");
            
            // ガベージコレクション前の状態
            RuntimeDataAreasDemo.analyzeHeapMemory();
            
            IO.println("Creating short-lived objects (Eden space)...");
            createShortLivedObjects();
            
            IO.println("\nForcing garbage collection...");
            System.gc();
            
            try {
                Thread.sleep(100); // GCの完了を待つ
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            IO.println("Creating long-lived objects (promoted to Old generation after surviving several GCs)...");
            createLongLivedObjects();
            
            IO.println("\nFinal memory state:");
            RuntimeDataAreasDemo.analyzeHeapMemory();
            RuntimeDataAreasDemo.analyzeGarbageCollection();
        }
        
        private static void createShortLivedObjects() {
            // 大量の短命オブジェクトを作成（Eden領域に配置）
            for (int i = 0; i < 10000; i++) {
                String temp = "Temporary string " + i;
                StringBuilder sb = new StringBuilder(temp);
                sb.append(" - processed");
                // これらのオブジェクトはすぐにガベージ対象となる
            }
        }
        
        private static void createLongLivedObjects() {
            // 長寿命オブジェクトを作成（参照を持ち続けるので、何回かGCを生き延びるとOld領域へ移る）
            for (int i = 0; i < 1000; i++) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", i);
                data.put("timestamp", System.currentTimeMillis());
                data.put("data", new byte[1024]); // 1KB
                
                longLivedObjects.add(data);
            }
        }
        
        /**
         * メモリリークのシミュレーション
         */
        public static class MemoryLeakDemo {
            private static final Map<Integer, byte[]> memoryHog = new HashMap<>();
            
            public static void simulateMemoryLeak() {
                IO.println("\n--- Memory Leak Simulation ---");
                IO.println("WARNING: This will consume memory - use small heap for demo");
                
                try {
                    for (int i = 0; i < 1000; i++) {
                        // 大きなオブジェクトを作成してキャッシュに保存
                        memoryHog.put(i, new byte[1024 * 1024]); // 1MB
                        
                        if (i % 100 == 0) {
                            MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                            double usedPercent = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
                            IO.println("Iteration %d: Heap usage %.1f%%".formatted(i, usedPercent));
                            
                            if (usedPercent > 80) {
                                IO.println("Heap usage high - stopping simulation");
                                break;
                            }
                        }
                    }
                } catch (OutOfMemoryError e) {
                    IO.println("OutOfMemoryError caught: " + e.getMessage());
                } finally {
                    // メモリを解放
                    memoryHog.clear();
                    System.gc();
                }
            }
        }
    }
    
    /**
     * スレッドとスタック領域のデモンストレーション
     */
    public static class ThreadStackDemo {
        
        public static void demonstrateThreadStacks() {
            IO.println("=== Thread Stack Demonstration ===");
            
            // メインスレッドのスタック情報
            Thread mainThread = Thread.currentThread();
            IO.println("Main Thread: " + mainThread.getName());
            IO.println("Stack depth (frames): " + getStackDepth());
            
            // 複数スレッドでのスタック使用
            List<Thread> threads = new ArrayList<>();
            
            for (int i = 0; i < 3; i++) {
                final int threadId = i;
                Thread thread = new Thread(() -> {
                    recursiveStackConsumer(threadId, 100);
                }, "StackDemo-" + i);
                
                threads.add(thread);
                thread.start();
            }
            
            // スレッドの完了を待つ
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            IO.println("All threads completed\n");
        }
        
        private static void recursiveStackConsumer(int threadId, int depth) {
            if (depth <= 0) {
                IO.println("Thread %d reached max depth, stack depth (frames): %d".formatted(
                                threadId, getStackDepth()));
                return;
            }
            
            // ローカル変数はスタックフレームに置かれる（配列の本体はヒープに置かれ、フレームには参照だけが入る）
            byte[] localArray = new byte[1024]; // 1KB
            long timestamp = System.nanoTime();
            String threadName = Thread.currentThread().getName();
            
            // ローカル変数を使用（コンパイラ最適化を防ぐ）
            if (localArray.length > 0 && timestamp > 0 && threadName.length() > 0) {
                recursiveStackConsumer(threadId, depth - 1);
            }
        }
        
        // スタックに積まれているフレームの数（バイト数ではない）
        private static int getStackDepth() {
            return Thread.currentThread().getStackTrace().length;
        }
        
        private static int maxRecursionDepth = 0;
        
        /**
         * スタックオーバーフローのシミュレーション
         */
        public static void simulateStackOverflow() {
            IO.println("--- Stack Overflow Simulation ---");
            IO.println("WARNING: This will cause StackOverflowError");
            
            maxRecursionDepth = 0;
            try {
                infiniteRecursion(0);
            } catch (StackOverflowError e) {
                IO.println("StackOverflowError caught at depth: " + maxRecursionDepth);
            }
        }
        
        private static void infiniteRecursion(int depth) {
            maxRecursionDepth = depth;
            // 配列の本体はヒープに置かれる。フレームが増える原因は再帰呼び出しそのもの
            byte[] frameData = new byte[100];
            Arrays.fill(frameData, (byte) depth);
            
            // 一定間隔で深度を報告
            if (depth % 1000 == 0) {
                IO.println("Recursion depth: " + depth);
            }
            
            infiniteRecursion(depth + 1);
        }
    }
    
    /**
     * JVMランタイム情報の表示
     */
    public static class JVMRuntimeInfo {
        
        public static void displayJVMInformation() {
            IO.println("=== JVM Runtime Information ===");
            
            // 基本的なJVM情報
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            IO.println("JVM Information:");
            IO.println("  Name:    " + runtimeBean.getVmName());
            IO.println("  Version: " + runtimeBean.getVmVersion());
            IO.println("  Vendor:  " + runtimeBean.getVmVendor());
            IO.println("  Uptime:  " + runtimeBean.getUptime() + " ms");
            
            // システム情報
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            IO.println("\nOperating System:");
            IO.println("  Name:         " + osBean.getName());
            IO.println("  Version:      " + osBean.getVersion());
            IO.println("  Architecture: " + osBean.getArch());
            IO.println("  Processors:   " + osBean.getAvailableProcessors());
            
            // JVM引数
            List<String> arguments = runtimeBean.getInputArguments();
            IO.println("\nJVM Arguments:");
            if (arguments.isEmpty()) {
                IO.println("  (No custom arguments)");
            } else {
                arguments.forEach(arg -> IO.println("  " + arg));
            }
            
            // クラスパス情報
            IO.println("\nClasspath Information:");
            IO.println("  Classpath: " + runtimeBean.getClassPath());
            IO.println("  Library Path: " + runtimeBean.getLibraryPath());
            
            // 重要なシステムプロパティ
            IO.println("\nKey System Properties:");
            String[] importantProps = {
                "java.version", "java.vendor", "java.home",
                "user.dir", "java.io.tmpdir", "file.separator",
                "line.separator", "path.separator"
            };
            
            for (String prop : importantProps) {
                String value = System.getProperty(prop);
                IO.println("  %-20s: %s".formatted(prop, value));
            }
            
            IO.println();
        }
        
        public static void displayThreadInformation() {
            IO.println("=== Thread Information ===");
            
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            IO.println("Thread Statistics:");
            IO.println("  Live Threads:   " + threadBean.getThreadCount());
            IO.println("  Daemon Threads: " + threadBean.getDaemonThreadCount());
            IO.println("  Peak Threads:   " + threadBean.getPeakThreadCount());
            IO.println("  Total Started:  " + threadBean.getTotalStartedThreadCount());
            
            // スレッド詳細情報
            long[] threadIds = threadBean.getAllThreadIds();
            ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadIds);
            
            IO.println("\nThread Details:");
            for (ThreadInfo info : threadInfos) {
                if (info != null) {
                    IO.println("  [%d] %s (%s)".formatted(
                                    info.getThreadId(),
                                    info.getThreadName(),
                                    info.getThreadState()));
                }
            }
            
            IO.println();
        }
    }
    
    /**
     * メモリ使用パターンの分析
     */
    public static void analyzeMemoryPatterns() {
        IO.println("=== Memory Usage Pattern Analysis ===");
        
        // 初期状態の記録
        MemoryUsage initialHeap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        IO.println("Initial heap usage: " + 
                         RuntimeDataAreasDemo.formatMemorySize(initialHeap.getUsed()));
        
        // 異なるオブジェクト作成パターンのテスト
        testArrayCreation();
        testObjectCreation();
        testStringCreation();
        
        // 最終状態の記録
        System.gc(); // 明示的なGC実行
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        MemoryUsage finalHeap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        IO.println("Final heap usage: " + 
                         RuntimeDataAreasDemo.formatMemorySize(finalHeap.getUsed()));
        
        long memoryDelta = finalHeap.getUsed() - initialHeap.getUsed();
        IO.println("Memory delta: " + 
                         RuntimeDataAreasDemo.formatMemorySize(Math.abs(memoryDelta)) +
                         (memoryDelta >= 0 ? " (increased)" : " (decreased)"));
        
        IO.println();
    }
    
    private static void testArrayCreation() {
        IO.println("Testing array creation...");
        long start = System.nanoTime();
        
        int[][] arrays = new int[1000][];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = new int[100];
            Arrays.fill(arrays[i], i);
        }
        
        long end = System.nanoTime();
        IO.println("  Array creation: %.2f ms".formatted((end - start) / 1_000_000.0));
    }
    
    private static void testObjectCreation() {
        IO.println("Testing object creation...");
        long start = System.nanoTime();
        
        List<Map<String, Integer>> objects = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Map<String, Integer> map = new HashMap<>();
            map.put("id", i);
            map.put("value", i * 2);
            objects.add(map);
        }
        
        long end = System.nanoTime();
        IO.println("  Object creation: %.2f ms".formatted((end - start) / 1_000_000.0));
    }
    
    private static void testStringCreation() {
        IO.println("Testing string creation...");
        long start = System.nanoTime();
        
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("String number ").append(i).append(" with random: ").append(ThreadLocalRandom.current().nextInt());
            strings.add(sb.toString());
        }
        
        long end = System.nanoTime();
        IO.println("  String creation: %.2f ms".formatted((end - start) / 1_000_000.0));
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        IO.println("JVM Architecture and Runtime Data Areas Demonstration");
        IO.println("====================================================");
        
        JVMRuntimeInfo.displayJVMInformation();
        JVMRuntimeInfo.displayThreadInformation();
        
        ClassLoaderHierarchyDemo.demonstrateClassLoaderHierarchy();
        ClassLoaderHierarchyDemo.CustomClassLoaderDemo.demonstrateCustomClassLoader();
        
        RuntimeDataAreasDemo.analyzeHeapMemory();
        RuntimeDataAreasDemo.analyzeStackFrames();
        RuntimeDataAreasDemo.analyzeGarbageCollection();
        
        ThreadStackDemo.demonstrateThreadStacks();
        
        MemoryGenerationDemo.demonstrateObjectLifecycle();
        
        analyzeMemoryPatterns();
        
        // 危険な操作はコメントアウト（必要に応じて有効化）
        // MemoryGenerationDemo.MemoryLeakDemo.simulateMemoryLeak();
        // ThreadStackDemo.simulateStackOverflow();
        
        IO.println("Key Insights:");
        IO.println("✓ JVM manages multiple memory areas with different purposes");
        IO.println("✓ ClassLoader hierarchy provides security and modularity");
        IO.println("✓ Stack memory is thread-local, heap memory is shared");
        IO.println("✓ Garbage collection automatically manages heap memory");
        IO.println("✓ Understanding memory layout helps optimize applications");
        
        IO.println("\nPerformance Tips:");
        IO.println("• Monitor heap usage to prevent OutOfMemoryError");
        IO.println("• Avoid creating unnecessary objects in loops");
        IO.println("• Use appropriate data structures for your use case");
        IO.println("• Consider object pooling for expensive objects");
        IO.println("• Profile memory usage in production-like environments");
    }
}