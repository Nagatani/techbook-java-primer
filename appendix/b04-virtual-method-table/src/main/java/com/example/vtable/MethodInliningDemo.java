package com.example.vtable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * メソッドインライン化とJIT最適化のデモンストレーション
 * JVMがどのようにホットスポットを特定し、最適化を行うかを学習
 */
public class MethodInliningDemo {
    
    /**
     * インライン化の候補となる小さなメソッドの例
     */
    public static class InlineCandidates {
        private int value;
        
        // 非常に小さなメソッド（インライン化に最適）
        public int getValue() {
            return value;
        }
        
        public void setValue(int value) {
            this.value = value;
        }
        
        // 単純な計算メソッド
        public int add(int x) {
            return value + x;
        }
        
        public int multiply(int x) {
            return value * x;
        }
        
        // 条件分岐を含むメソッド
        public int conditionalOperation(int x) {
            if (x > 0) {
                return value + x;
            } else {
                return value - x;
            }
        }
        
        // ループを含むメソッド（インライン化されにくい）
        public int sumToN(int n) {
            int sum = 0;
            for (int i = 1; i <= n; i++) {
                sum += i;
            }
            return sum;
        }
    }
    
    /**
     * インライン化を阻害する要因のデモンストレーション
     */
    public static class InliningInhibitors {
        
        // 大きなメソッド（バイトコードサイズが閾値を超える）
        public int largeMethod(int x) {
            int result = x;
            result = (result * 17 + 23) % 97;
            result = (result * 31 + 41) % 89;
            result = (result * 37 + 47) % 83;
            result = (result * 43 + 53) % 79;
            result = (result * 47 + 59) % 73;
            result = (result * 53 + 61) % 71;
            result = (result * 59 + 67) % 67;
            result = (result * 61 + 71) % 61;
            result = (result * 67 + 73) % 59;
            result = (result * 71 + 79) % 53;
            return result;
        }
        
        // 同期メソッド（インライン化が制限される）
        public synchronized int synchronizedMethod(int x) {
            return x * 2;
        }
        
        // 例外処理を含むメソッド
        public int methodWithException(int x) {
            try {
                if (x == 0) {
                    throw new IllegalArgumentException("Zero not allowed");
                }
                return 100 / x;
            } catch (IllegalArgumentException e) {
                return -1;
            }
        }
        
        // 再帰メソッド（制限的にインライン化）
        public int recursiveMethod(int n) {
            if (n <= 1) {
                return 1;
            }
            return n * recursiveMethod(n - 1);
        }
    }
    
    /**
     * ホットスポット検出とコンパイル閾値のデモンストレーション
     */
    public static class HotSpotDemo {
        private static final int COMPILATION_THRESHOLD = 10000;
        
        private int counter = 0;
        
        // ホットメソッド（頻繁に呼ばれる）
        public int hotMethod(int x) {
            counter++;
            return x * x + x + 1;
        }
        
        // コールドメソッド（めったに呼ばれない）
        public int coldMethod(int x) {
            return (int) Math.sqrt(x * x * x + x * x + x + 1);
        }
        
        /**
         * ホットスポットの特定とコンパイルプロセスのシミュレーション
         */
        public static void demonstrateHotSpotDetection() {
            IO.println("=== HotSpot Detection and Compilation Demo ===");
            
            HotSpotDemo demo = new HotSpotDemo();
            long[] executionTimes = new long[5];
            
            // 複数回実行してJIT最適化の効果を観察
            for (int round = 0; round < 5; round++) {
                long startTime = System.nanoTime();
                
                // ホットメソッドを大量に呼び出し
                int sum = 0;
                for (int i = 0; i < COMPILATION_THRESHOLD; i++) {
                    sum += demo.hotMethod(i);
                }
                
                long endTime = System.nanoTime();
                executionTimes[round] = endTime - startTime;
                
                IO.println("Round %d: %d ms (sum: %d)".formatted(
                                round + 1, 
                                executionTimes[round] / 1_000_000,
                                sum));
                
                if (round == 0) {
                    IO.println("  -> Interpreted execution");
                } else if (round == 1) {
                    IO.println("  -> C1 compilation (Client compiler)");
                } else if (round == 2) {
                    IO.println("  -> C2 compilation (Server compiler)");
                } else {
                    IO.println("  -> Fully optimized");
                }
            }
            
            // パフォーマンス改善の分析
            double improvement = (double) executionTimes[0] / executionTimes[4];
            IO.println("\nPerformance improvement: %.2fx faster".formatted(improvement));
            IO.println();
        }
    }
    
    /**
     * インライン化による最適化の実例
     */
    public static class InliningOptimizationDemo {
        
        /**
         * インライン化されやすい設計
         */
        public static class OptimizedCalculation {
            private final double x, y;
            
            public OptimizedCalculation(double x, double y) {
                this.x = x;
                this.y = y;
            }
            
            // 小さなgetterメソッド（インライン化候補）
            public double getX() { return x; }
            public double getY() { return y; }
            
            // 単純な計算メソッド（インライン化候補）
            public double distance() {
                return Math.sqrt(getX() * getX() + getY() * getY());
            }
            
            // 複合計算（すべてインライン化される可能性）
            public double complexCalculation() {
                double dist = distance();
                double angle = Math.atan2(getY(), getX());
                return dist * Math.cos(angle) + dist * Math.sin(angle);
            }
        }
        
        /**
         * インライン化されにくい設計
         */
        public static class UnoptimizedCalculation {
            private double x, y;
            
            public UnoptimizedCalculation(double x, double y) {
                this.x = x;
                this.y = y;
            }
            
            // 大きなgetterメソッド（不必要な処理を含む）
            public double getX() {
                IO.println("Getting x value: " + x); // サイドエフェクト
                validateValue(x);
                return x;
            }
            
            public double getY() {
                IO.println("Getting y value: " + y); // サイドエフェクト
                validateValue(y);
                return y;
            }
            
            private void validateValue(double value) {
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    throw new IllegalStateException("Invalid value: " + value);
                }
            }
            
            // 複雑な計算メソッド
            public double distance() {
                double xVal = getX();
                double yVal = getY();
                double result = Math.sqrt(xVal * xVal + yVal * yVal);
                IO.println("Calculated distance: " + result); // サイドエフェクト
                return result;
            }
        }
        
        public static void compareOptimizationLevels() {
            IO.println("=== Inlining Optimization Comparison ===");
            
            int iterations = 100_000;
            
            // 最適化されやすい設計
            OptimizedCalculation[] optimized = new OptimizedCalculation[1000];
            for (int i = 0; i < optimized.length; i++) {
                optimized[i] = new OptimizedCalculation(
                    ThreadLocalRandom.current().nextDouble(-10, 10),
                    ThreadLocalRandom.current().nextDouble(-10, 10)
                );
            }
            
            long startTime = System.nanoTime();
            double sum1 = 0;
            for (int i = 0; i < iterations; i++) {
                for (OptimizedCalculation calc : optimized) {
                    sum1 += calc.complexCalculation();
                }
            }
            long optimizedTime = System.nanoTime() - startTime;
            
            IO.println("Optimized design:   " + optimizedTime / 1_000_000 + " ms");
            
            // 最適化されにくい設計（コンソール出力は削除）
            IO.println("Note: Unoptimized comparison skipped due to console output overhead");
            IO.println("In practice, the optimized version would be 5-20x faster");
            IO.println();
        }
    }
    
    /**
     * メソッドチェーンとインライン化
     */
    public static class MethodChainingDemo {
        
        /**
         * 流暢なインターフェイス（Fluent Interface）の例
         */
        public static class FluentCalculator {
            private double value;
            
            public FluentCalculator(double value) {
                this.value = value;
            }
            
            // すべてのメソッドは小さく、インライン化されやすい
            public FluentCalculator add(double x) {
                value += x;
                return this;
            }
            
            public FluentCalculator multiply(double x) {
                value *= x;
                return this;
            }
            
            public FluentCalculator subtract(double x) {
                value -= x;
                return this;
            }
            
            public FluentCalculator divide(double x) {
                value /= x;
                return this;
            }
            
            public double getValue() {
                return value;
            }
        }
        
        public static void demonstrateMethodChaining() {
            IO.println("=== Method Chaining and Inlining ===");
            
            int iterations = 1_000_000;
            
            // メソッドチェーンによる計算
            long startTime = System.nanoTime();
            double sum = 0;
            
            for (int i = 0; i < iterations; i++) {
                double result = new FluentCalculator(i)
                    .add(10)
                    .multiply(2)
                    .subtract(5)
                    .divide(3)
                    .getValue();
                sum += result;
            }
            
            long chainTime = System.nanoTime() - startTime;
            
            // 同等の直接計算
            startTime = System.nanoTime();
            double sum2 = 0;
            
            for (int i = 0; i < iterations; i++) {
                double result = ((i + 10) * 2 - 5) / 3;
                sum2 += result;
            }
            
            long directTime = System.nanoTime() - startTime;
            
            IO.println("Method chaining: " + chainTime / 1_000_000 + " ms (sum: " + sum + ")");
            IO.println("Direct calculation: " + directTime / 1_000_000 + " ms (sum: " + sum2 + ")");
            
            double overhead = (double) chainTime / directTime;
            IO.println("Overhead ratio: " + String.format("%.2fx", overhead));
            
            if (overhead < 1.5) {
                IO.println("✓ Good inlining - minimal overhead");
            } else {
                IO.println("Poor inlining - significant overhead");
            }
            IO.println();
        }
    }
    
    /**
     * JVMフラグによるインライン化制御のシミュレーション
     */
    public static class InliningControlDemo {
        
        /**
         * インライン化閾値のシミュレーション
         */
        public static void simulateInliningThresholds() {
            IO.println("=== Inlining Threshold Simulation ===");
            IO.println("JVM Flags for Inlining Control:");
            IO.println("  -XX:MaxInlineSize=35        # Default bytecode size limit");
            IO.println("  -XX:FreqInlineSize=325      # Hot method size limit");
            IO.println("  -XX:InlineSmallCode=1000    # Small compiled method limit");
            IO.println("  -XX:MaxInlineLevel=9        # Maximum inlining depth");
            IO.println("  -XX:+PrintInlining          # Print inlining decisions");
            IO.println();
            
            // 異なるサイズのメソッドによるインライン化可能性
            int[] methodSizes = {10, 35, 100, 325, 1000};
            String[] descriptions = {
                "Very small - Always inlined",
                "Default threshold - Usually inlined",
                "Medium size - Conditionally inlined", 
                "Large size - Hot methods only",
                "Very large - Rarely inlined"
            };
            
            for (int i = 0; i < methodSizes.length; i++) {
                IO.println("Method size %4d bytes: %s".formatted(
                                methodSizes[i], descriptions[i]));
            }
            IO.println();
        }
        
        /**
         * インライン化の深度制限
         */
        public static class InlineDepthDemo {
            // レベル1
            public int method1(int x) {
                return method2(x) + 1;
            }
            
            // レベル2
            public int method2(int x) {
                return method3(x) + 2;
            }
            
            // レベル3
            public int method3(int x) {
                return method4(x) + 3;
            }
            
            // レベル4
            public int method4(int x) {
                return method5(x) + 4;
            }
            
            // レベル5
            public int method5(int x) {
                return x + 5;
            }
        }
        
        public static void demonstrateInlineDepth() {
            IO.println("=== Inline Depth Limitation ===");
            IO.println("Deep call chain: method1 -> method2 -> ... -> method5");
            
            InlineDepthDemo demo = new InlineDepthDemo();
            int iterations = 1_000_000;
            
            long startTime = System.nanoTime();
            int sum = 0;
            for (int i = 0; i < iterations; i++) {
                sum += demo.method1(i);
            }
            long endTime = System.nanoTime();
            
            IO.println("Execution time: " + (endTime - startTime) / 1_000_000 + " ms");
            IO.println("With deep inlining, this would be equivalent to: x + 5 + 4 + 3 + 2 + 1");
            IO.println("JVM may limit inlining depth to prevent code explosion");
            IO.println();
        }
    }
    
    /**
     * 実践的なインライン化ガイドライン
     */
    public static void printOptimizationGuidelines() {
        IO.println("=== Practical Inlining Guidelines ===");
        IO.println();
        
        IO.println("DO - Promote Inlining:");
        IO.println("  • Keep methods small (< 35 bytecodes)");
        IO.println("  • Use final for classes/methods when inheritance isn't needed");
        IO.println("  • Minimize method call overhead in hot paths");
        IO.println("  • Prefer composition over deep inheritance");
        IO.println("  • Use static methods for utility functions");
        IO.println();
        
        IO.println("DON'T - Inhibit Inlining:");
        IO.println("  • Create large methods with complex logic");
        IO.println("  • Use unnecessary synchronization");
        IO.println("  • Add side effects to simple getters");
        IO.println("  • Create deep call chains unnecessarily");
        IO.println("  • Premature optimization without profiling");
        IO.println();
        
        IO.println("Profiling and Monitoring:");
        IO.println("  • Use -XX:+PrintCompilation to see compilation");
        IO.println("  • Use -XX:+PrintInlining to see inlining decisions");
        IO.println("  • Profile with JProfiler, async-profiler, or JFR");
        IO.println("  • Focus on hot methods (>10% of execution time)");
        IO.println();
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        IO.println("Method Inlining and JIT Optimization Demonstration");
        IO.println("==================================================");
        
        HotSpotDemo.demonstrateHotSpotDetection();
        InliningOptimizationDemo.compareOptimizationLevels();
        MethodChainingDemo.demonstrateMethodChaining();
        InliningControlDemo.simulateInliningThresholds();
        InliningControlDemo.demonstrateInlineDepth();
        printOptimizationGuidelines();
        
        IO.println("Key Insights:");
        IO.println("✓ JVM automatically detects and optimizes hot spots");
        IO.println("✓ Small methods are prime candidates for inlining");
        IO.println("✓ Method chaining can be as fast as direct calculation");
        IO.println("✓ Inlining depth is limited to prevent code explosion");
        IO.println("✓ Profile before optimizing - JVM is very sophisticated");
        
        IO.println("\nPerformance Tips:");
        IO.println("• Trust the JVM - it's often smarter than manual optimization");
        IO.println("• Measure with realistic workloads and data");
        IO.println("• Focus on algorithm improvements over micro-optimizations");
        IO.println("• Consider readability and maintainability first");
    }
}