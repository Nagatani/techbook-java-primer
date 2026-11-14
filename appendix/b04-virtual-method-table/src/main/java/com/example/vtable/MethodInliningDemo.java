package com.example.vtable;

import java.util.*;

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
        
        // ループを含むメソッド（小さければループがあってもインライン化される）
        public int sumToN(int n) {
            int sum = 0;
            for (int i = 1; i <= n; i++) {
                sum += i;
            }
            return sum;
        }
    }
    
    /**
     * インライン化の判断に影響する要因の例
     * （どれも「絶対にインライン化されない」わけではない。
     *  -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining で実際の判断を確かめられる）
     */
    public static class InliningInhibitors {
        
        // 大きなメソッド（バイトコードは114バイトで、MaxInlineSize=35を超える。
        // よく呼ばれる呼び出し箇所ではFreqInlineSize=325以下ならインライン化される）
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
        
        // 同期メソッド（小さければインライン化される。ただしロックの取得と解放のコストは残る）
        public synchronized int synchronizedMethod(int x) {
            return x * 2;
        }
        
        // 例外処理を含むメソッド（小さければインライン化される）
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
        
        // 再帰メソッド（自分自身の呼び出しは既定で1段まで：MaxRecursiveInlineLevel=1）
        public int recursiveMethod(int n) {
            if (n <= 1) {
                return 1;
            }
            return n * recursiveMethod(n - 1);
        }
    }
    
    /**
     * 時間の比を文字列にする（0で割らないようにする）
     */
    static String ratio(long a, long b) {
        if (a <= 0 || b <= 0) {
            return "n/a (too fast to measure)";
        }
        return "%.2fx".formatted((double) a / b);
    }
    
    /**
     * ホットスポット検出のデモンストレーション
     * よく呼ばれるメソッドは、インタープリター実行のあとC1、C2の順にJITコンパイルされる。
     * どのラウンドでコンパイルされたかは -XX:+PrintCompilation で確かめる
     */
    public static class HotSpotDemo {
        private static final int CALLS_PER_ROUND = 10000;
        
        private int counter = 0;
        
        // ホットメソッド（頻繁に呼ばれる）
        public int hotMethod(int x) {
            counter++;
            return x * x + x + 1;
        }
        
        public static void demonstrateHotSpotDetection() {
            IO.println("=== HotSpot Detection and Compilation Demo ===");
            IO.println("Run with -XX:+PrintCompilation to see when hotMethod is compiled");
            
            HotSpotDemo demo = new HotSpotDemo();
            int rounds = 5;
            long[] executionTimes = new long[rounds];
            
            for (int round = 0; round < rounds; round++) {
                long startTime = System.nanoTime();
                
                int sum = 0;
                for (int i = 0; i < CALLS_PER_ROUND; i++) {
                    sum += demo.hotMethod(i);
                }
                
                executionTimes[round] = System.nanoTime() - startTime;
                
                IO.println("Round %d: %d μs (sum: %d)".formatted(
                                round + 1, 
                                executionTimes[round] / 1000,
                                sum));
            }
            
            IO.println("\nRound 1 / round %d: %s".formatted(
                            rounds, ratio(executionTimes[0], executionTimes[rounds - 1])));
            IO.println("Total calls: " + demo.counter);
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
            IO.println("=== Inlining Optimization Example ===");
            
            int iterations = 10_000;
            
            // 最適化されやすい設計
            OptimizedCalculation[] optimized = new OptimizedCalculation[1000];
            Random random = new Random(42); // 毎回同じ値になるよう種を固定する
            for (int i = 0; i < optimized.length; i++) {
                optimized[i] = new OptimizedCalculation(
                    random.nextDouble(-10, 10),
                    random.nextDouble(-10, 10)
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
            
            IO.println("Optimized design:   " + optimizedTime / 1000 + " μs");
            
            // UnoptimizedCalculationは呼び出しのたびにコンソールへ出力するため、時間の比較はしない
            IO.println("(sum: %.2f)".formatted(sum1));
            IO.println("Note: UnoptimizedCalculation prints on every call, so it is not timed");
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
        
        private static double withChaining(int iterations) {
            double sum = 0;
            for (int i = 0; i < iterations; i++) {
                sum += new FluentCalculator(i)
                    .add(10)
                    .multiply(2)
                    .subtract(5)
                    .divide(3)
                    .getValue();
            }
            return sum;
        }
        
        private static double direct(int iterations) {
            double sum = 0;
            for (int i = 0; i < iterations; i++) {
                sum += ((i + 10.0) * 2 - 5) / 3;   // メソッドチェーンと同じdoubleの計算
            }
            return sum;
        }
        
        public static void demonstrateMethodChaining() {
            IO.println("=== Method Chaining and Inlining ===");
            
            int iterations = 1_000_000;
            
            // ウォームアップ
            double warm = 0;
            for (int round = 0; round < 10; round++) {
                warm += withChaining(iterations) + direct(iterations);
            }
            
            long startTime = System.nanoTime();
            double sum = withChaining(iterations);
            long chainTime = System.nanoTime() - startTime;
            
            startTime = System.nanoTime();
            double sum2 = direct(iterations);
            long directTime = System.nanoTime() - startTime;
            
            IO.println("Method chaining:    " + chainTime / 1000 + " μs (sum: " + sum + ")");
            IO.println("Direct calculation: " + directTime / 1000 + " μs (sum: " + sum2 + ")");
            IO.println("Chaining / direct: " + ratio(chainTime, directTime));
            IO.println("(After inlining and escape analysis, the two are often close)");
            if (warm < 0) IO.println("Unexpected result");
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
            IO.println("JVM Flags for Inlining Control (JDK 25 defaults; check with -XX:+PrintFlagsFinal):");
            IO.println("  -XX:MaxInlineSize=35        # Bytecode size limit for normal call sites");
            IO.println("  -XX:FreqInlineSize=325      # Bytecode size limit for hot call sites");
            IO.println("  -XX:InlineSmallCode=2500    # Size limit for already compiled methods");
            IO.println("  -XX:MaxInlineLevel=15       # Maximum inlining depth");
            IO.println("  -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining  # Print inlining decisions");
            IO.println();
            
            // 異なるサイズのメソッドによるインライン化可能性
            int[] methodSizes = {10, 35, 100, 325, 1000};
            String[] descriptions = {
                "Very small - inlined at normal call sites",
                "MaxInlineSize - limit for normal call sites",
                "Medium size - inlined only at hot call sites", 
                "FreqInlineSize - limit for hot call sites",
                "Larger than FreqInlineSize - not inlined"
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
        IO.println("  • Keep methods small (bytecode size within MaxInlineSize/FreqInlineSize)");
        IO.println("  • Keep call sites monomorphic in hot paths when it is natural to do so");
        IO.println("  • Minimize method call overhead in hot paths");
        IO.println("  • Prefer composition over deep inheritance");
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
        IO.println("  • Use -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining to see inlining decisions");
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
        IO.println("✓ Method chaining is often close to direct calculation after inlining");
        IO.println("✓ Inlining depth is limited to prevent code explosion");
        IO.println("✓ Profile before optimizing - JVM is very sophisticated");
        
        IO.println("\nPerformance Tips:");
        IO.println("• Trust the JVM - it's often smarter than manual optimization");
        IO.println("• Measure with realistic workloads and data");
        IO.println("• Focus on algorithm improvements over micro-optimizations");
        IO.println("• Consider readability and maintainability first");
    }
}