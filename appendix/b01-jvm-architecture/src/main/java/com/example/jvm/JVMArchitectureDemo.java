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
            System.out.println("=== ClassLoader Hierarchy Investigation ===");
            
            // システムクラスローダー（アプリケーションクラスローダー）
            ClassLoader systemLoader = ClassLoader.getSystemClassLoader();
            System.out.println("System ClassLoader: " + systemLoader);
            System.out.println("  Class: " + systemLoader.getClass().getName());
            
            // 拡張クラスローダー（プラットフォームクラスローダー）
            ClassLoader platformLoader = systemLoader.getParent();
            System.out.println("\nPlatform ClassLoader: " + platformLoader);
            if (platformLoader != null) {
                System.out.println("  Class: " + platformLoader.getClass().getName());
            }
            
            // ブートストラップクラスローダー（nullで表される）
            ClassLoader bootstrapLoader = platformLoader != null ? platformLoader.getParent() : null;
            System.out.println("\nBootstrap ClassLoader: " + bootstrapLoader);
            System.out.println("  (null means Bootstrap ClassLoader - implemented in native code)");
            
            System.out.println("\n--- Class Loader Investigation for Different Classes ---");
            
            // 各種クラスのクラスローダーを確認
            investigateClassLoader("java.lang.String", String.class);
            investigateClassLoader("java.util.HashMap", HashMap.class);
            investigateClassLoader("java.util.concurrent.ConcurrentHashMap", 
                                 java.util.concurrent.ConcurrentHashMap.class);
            investigateClassLoader("com.example.jvm.JVMArchitectureDemo", 
                                 JVMArchitectureDemo.class);
            
            System.out.println();
        }
        
        private static void investigateClassLoader(String className, Class<?> clazz) {
            ClassLoader loader = clazz.getClassLoader();
            System.out.printf("%-45s -> %s%n", 
                            className, 
                            loader == null ? "Bootstrap ClassLoader" : loader.getClass().getSimpleName());
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
                System.out.println("Custom ClassLoader: Attempting to load " + name);
                classLoadCount++;
                
                // 実際のクラスロードはスキップし、統計のみ収集
                throw new ClassNotFoundException("Demo ClassLoader - class not found: " + name);
            }
            
            @Override
            protected Class<?> loadClass(String name, boolean resolve) 
                    throws ClassNotFoundException {
                
                System.out.println("Custom ClassLoader: loadClass called for " + name);
                
                // 親委譲モデルに従う
                return super.loadClass(name, resolve);
            }
            
            public void printStatistics() {
                System.out.println("Custom ClassLoader Statistics:");
                System.out.println("  Classes attempted to load: " + classLoadCount);
                System.out.println("  Actually loaded classes: " + loadedClasses.size());
            }
            
            public static void demonstrateCustomClassLoader() {
                System.out.println("\n--- Custom ClassLoader Demo ---");
                
                CustomClassLoaderDemo customLoader = new CustomClassLoaderDemo(
                    ClassLoader.getSystemClassLoader());
                
                try {
                    // 既存クラスのロード（親に委譲される）
                    Class<?> stringClass = customLoader.loadClass("java.lang.String");
                    System.out.println("Successfully loaded: " + stringClass.getName());
                    
                    // 存在しないクラスのロード
                    customLoader.loadClass("com.nonexistent.TestClass");
                    
                } catch (ClassNotFoundException e) {
                    System.out.println("Expected exception: " + e.getMessage());
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
            System.out.println("=== Heap Memory Analysis ===");
            
            // メモリ管理Beanの取得
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            List<MemoryPoolMXBean> memoryPools = ManagementFactory.getMemoryPoolMXBeans();
            
            // ヒープメモリの全体使用状況
            MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
            printMemoryUsage("Heap Memory", heapUsage);
            
            // 非ヒープメモリ（メタスペースなど）の使用状況
            MemoryUsage nonHeapUsage = memoryBean.getNonHeapMemoryUsage();
            printMemoryUsage("Non-Heap Memory", nonHeapUsage);
            
            System.out.println("\n--- Memory Pool Details ---");
            for (MemoryPoolMXBean pool : memoryPools) {
                MemoryUsage usage = pool.getUsage();
                if (usage != null) {
                    System.out.printf("%-25s: %s%n", 
                                    pool.getName(), 
                                    formatMemorySize(usage.getUsed()) + " / " + 
                                    formatMemorySize(usage.getMax()));
                }
            }
            
            System.out.println();
        }
        
        private static void printMemoryUsage(String name, MemoryUsage usage) {
            System.out.println(name + ":");
            System.out.println("  Init:      " + formatMemorySize(usage.getInit()));
            System.out.println("  Used:      " + formatMemorySize(usage.getUsed()));
            System.out.println("  Committed: " + formatMemorySize(usage.getCommitted()));
            System.out.println("  Max:       " + formatMemorySize(usage.getMax()));
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
            System.out.println("=== Stack Frame Analysis ===");
            
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
            System.out.println("Current Stack Trace:");
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            
            for (int i = 0; i < Math.min(stackTrace.length, 8); i++) {
                StackTraceElement element = stackTrace[i];
                System.out.printf("  [%d] %s.%s(%s:%d)%n",
                                i,
                                element.getClassName(),
                                element.getMethodName(),
                                element.getFileName(),
                                element.getLineNumber());
            }
            System.out.println();
        }
        
        /**
         * ガベージコレクション情報の表示
         */
        public static void analyzeGarbageCollection() {
            System.out.println("=== Garbage Collection Analysis ===");
            
            List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
            
            for (GarbageCollectorMXBean gcBean : gcBeans) {
                System.out.println("GC: " + gcBean.getName());
                System.out.println("  Collection Count: " + gcBean.getCollectionCount());
                System.out.println("  Collection Time:  " + gcBean.getCollectionTime() + " ms");
                
                String[] memoryPoolNames = gcBean.getMemoryPoolNames();
                System.out.println("  Memory Pools: " + Arrays.toString(memoryPoolNames));
                System.out.println();
            }
        }
    }
    
    /**
     * メモリ世代とオブジェクトライフサイクルのデモンストレーション
     */
    public static class MemoryGenerationDemo {
        private static final List<Object> longLivedObjects = new ArrayList<>();
        
        public static void demonstrateObjectLifecycle() {
            System.out.println("=== Object Lifecycle and Memory Generations ===");
            
            // ガベージコレクション前の状態
            RuntimeDataAreasDemo.analyzeHeapMemory();
            
            System.out.println("Creating short-lived objects (Eden space)...");
            createShortLivedObjects();
            
            System.out.println("\nForcing garbage collection...");
            System.gc();
            
            try {
                Thread.sleep(100); // GCの完了を待つ
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("Creating long-lived objects (will promote to Old generation)...");
            createLongLivedObjects();
            
            System.out.println("\nFinal memory state:");
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
            // 長寿命オブジェクトを作成（Old領域に移動する）
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
                System.out.println("\n--- Memory Leak Simulation ---");
                System.out.println("WARNING: This will consume memory - use small heap for demo");
                
                try {
                    for (int i = 0; i < 1000; i++) {
                        // 大きなオブジェクトを作成してキャッシュに保存
                        memoryHog.put(i, new byte[1024 * 1024]); // 1MB
                        
                        if (i % 100 == 0) {
                            MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                            double usedPercent = (double) heapUsage.getUsed() / heapUsage.getMax() * 100;
                            System.out.printf("Iteration %d: Heap usage %.1f%%\n", i, usedPercent);
                            
                            if (usedPercent > 80) {
                                System.out.println("Heap usage high - stopping simulation");
                                break;
                            }
                        }
                    }
                } catch (OutOfMemoryError e) {
                    System.out.println("OutOfMemoryError caught: " + e.getMessage());
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
            System.out.println("=== Thread Stack Demonstration ===");
            
            // メインスレッドのスタック情報
            Thread mainThread = Thread.currentThread();
            System.out.println("Main Thread: " + mainThread.getName());
            System.out.println("Stack Size: " + getApproximateStackSize());
            
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
            
            System.out.println("All threads completed\n");
        }
        
        private static void recursiveStackConsumer(int threadId, int depth) {
            if (depth <= 0) {
                System.out.printf("Thread %d reached max depth, stack size: %d\n", 
                                threadId, getApproximateStackSize());
                return;
            }
            
            // スタックフレームにローカル変数を配置
            byte[] localArray = new byte[1024]; // 1KB
            long timestamp = System.nanoTime();
            String threadName = Thread.currentThread().getName();
            
            // ローカル変数を使用（コンパイラ最適化を防ぐ）
            if (localArray.length > 0 && timestamp > 0 && threadName.length() > 0) {
                recursiveStackConsumer(threadId, depth - 1);
            }
        }
        
        private static long getApproximateStackSize() {
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            long threadId = Thread.currentThread().getId();
            ThreadInfo threadInfo = threadBean.getThreadInfo(threadId);
            
            if (threadInfo != null) {
                return threadInfo.getStackTrace().length;
            }
            return 0;
        }
        
        /**
         * スタックオーバーフローのシミュレーション
         */
        public static void simulateStackOverflow() {
            System.out.println("--- Stack Overflow Simulation ---");
            System.out.println("WARNING: This will cause StackOverflowError");
            
            try {
                infiniteRecursion(0);
            } catch (StackOverflowError e) {
                System.out.println("StackOverflowError caught at depth: " + e.getMessage());
            }
        }
        
        private static void infiniteRecursion(int depth) {
            // スタックフレームにデータを追加
            byte[] frameData = new byte[100];
            Arrays.fill(frameData, (byte) depth);
            
            // 一定間隔で深度を報告
            if (depth % 1000 == 0) {
                System.out.println("Recursion depth: " + depth);
            }
            
            infiniteRecursion(depth + 1);
        }
    }
    
    /**
     * JVMランタイム情報の表示
     */
    public static class JVMRuntimeInfo {
        
        public static void displayJVMInformation() {
            System.out.println("=== JVM Runtime Information ===");
            
            // 基本的なJVM情報
            RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
            System.out.println("JVM Information:");
            System.out.println("  Name:    " + runtimeBean.getVmName());
            System.out.println("  Version: " + runtimeBean.getVmVersion());
            System.out.println("  Vendor:  " + runtimeBean.getVmVendor());
            System.out.println("  Uptime:  " + runtimeBean.getUptime() + " ms");
            
            // システム情報
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            System.out.println("\nOperating System:");
            System.out.println("  Name:         " + osBean.getName());
            System.out.println("  Version:      " + osBean.getVersion());
            System.out.println("  Architecture: " + osBean.getArch());
            System.out.println("  Processors:   " + osBean.getAvailableProcessors());
            
            // JVM引数
            List<String> arguments = runtimeBean.getInputArguments();
            System.out.println("\nJVM Arguments:");
            if (arguments.isEmpty()) {
                System.out.println("  (No custom arguments)");
            } else {
                arguments.forEach(arg -> System.out.println("  " + arg));
            }
            
            // クラスパス情報
            System.out.println("\nClasspath Information:");
            System.out.println("  Classpath: " + runtimeBean.getClassPath());
            System.out.println("  Library Path: " + runtimeBean.getLibraryPath());
            
            // 重要なシステムプロパティ
            System.out.println("\nKey System Properties:");
            String[] importantProps = {
                "java.version", "java.vendor", "java.home",
                "user.dir", "java.io.tmpdir", "file.separator",
                "line.separator", "path.separator"
            };
            
            for (String prop : importantProps) {
                String value = System.getProperty(prop);
                System.out.printf("  %-20s: %s%n", prop, value);
            }
            
            System.out.println();
        }
        
        public static void displayThreadInformation() {
            System.out.println("=== Thread Information ===");
            
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            System.out.println("Thread Statistics:");
            System.out.println("  Live Threads:   " + threadBean.getThreadCount());
            System.out.println("  Daemon Threads: " + threadBean.getDaemonThreadCount());
            System.out.println("  Peak Threads:   " + threadBean.getPeakThreadCount());
            System.out.println("  Total Started:  " + threadBean.getTotalStartedThreadCount());
            
            // スレッド詳細情報
            long[] threadIds = threadBean.getAllThreadIds();
            ThreadInfo[] threadInfos = threadBean.getThreadInfo(threadIds);
            
            System.out.println("\nThread Details:");
            for (ThreadInfo info : threadInfos) {
                if (info != null) {
                    System.out.printf("  [%d] %s (%s)%n", 
                                    info.getThreadId(),
                                    info.getThreadName(),
                                    info.getThreadState());
                }
            }
            
            System.out.println();
        }
    }
    
    /**
     * メモリ使用パターンの分析
     */
    public static void analyzeMemoryPatterns() {
        System.out.println("=== Memory Usage Pattern Analysis ===");
        
        // 初期状態の記録
        MemoryUsage initialHeap = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        System.out.println("Initial heap usage: " + 
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
        System.out.println("Final heap usage: " + 
                         RuntimeDataAreasDemo.formatMemorySize(finalHeap.getUsed()));
        
        long memoryDelta = finalHeap.getUsed() - initialHeap.getUsed();
        System.out.println("Memory delta: " + 
                         RuntimeDataAreasDemo.formatMemorySize(Math.abs(memoryDelta)) +
                         (memoryDelta >= 0 ? " (increased)" : " (decreased)"));
        
        System.out.println();
    }
    
    private static void testArrayCreation() {
        System.out.println("Testing array creation...");
        long start = System.nanoTime();
        
        int[][] arrays = new int[1000][];
        for (int i = 0; i < arrays.length; i++) {
            arrays[i] = new int[100];
            Arrays.fill(arrays[i], i);
        }
        
        long end = System.nanoTime();
        System.out.printf("  Array creation: %.2f ms\n", (end - start) / 1_000_000.0);
    }
    
    private static void testObjectCreation() {
        System.out.println("Testing object creation...");
        long start = System.nanoTime();
        
        List<Map<String, Integer>> objects = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Map<String, Integer> map = new HashMap<>();
            map.put("id", i);
            map.put("value", i * 2);
            objects.add(map);
        }
        
        long end = System.nanoTime();
        System.out.printf("  Object creation: %.2f ms\n", (end - start) / 1_000_000.0);
    }
    
    private static void testStringCreation() {
        System.out.println("Testing string creation...");
        long start = System.nanoTime();
        
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("String number ").append(i).append(" with random: ").append(ThreadLocalRandom.current().nextInt());
            strings.add(sb.toString());
        }
        
        long end = System.nanoTime();
        System.out.printf("  String creation: %.2f ms\n", (end - start) / 1_000_000.0);
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        System.out.println("JVM Architecture and Runtime Data Areas Demonstration");
        System.out.println("====================================================");
        
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
        
        System.out.println("🎯 Key Insights:");
        System.out.println("✓ JVM manages multiple memory areas with different purposes");
        System.out.println("✓ ClassLoader hierarchy provides security and modularity");
        System.out.println("✓ Stack memory is thread-local, heap memory is shared");
        System.out.println("✓ Garbage collection automatically manages heap memory");
        System.out.println("✓ Understanding memory layout helps optimize applications");
        
        System.out.println("\n⚡ Performance Tips:");
        System.out.println("• Monitor heap usage to prevent OutOfMemoryError");
        System.out.println("• Avoid creating unnecessary objects in loops");
        System.out.println("• Use appropriate data structures for your use case");
        System.out.println("• Consider object pooling for expensive objects");
        System.out.println("• Profile memory usage in production-like environments");
    }
}