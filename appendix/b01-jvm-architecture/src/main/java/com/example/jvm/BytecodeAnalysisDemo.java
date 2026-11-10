package com.example.jvm;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * バイトコード分析とJITコンパイレーションのデモンストレーション
 * Javaソースコードがどのようにバイトコードにコンパイルされ、最適化されるかを学習
 */
public class BytecodeAnalysisDemo {
    
    /**
     * 基本的なバイトコード命令のデモンストレーション
     */
    public static class BasicBytecodeDemo {
        
        // 算術演算（iadd, isub, imul, idiv）
        public int arithmeticOperations(int a, int b) {
            int sum = a + b;      // iadd
            int diff = a - b;     // isub
            int product = a * b;  // imul
            int quotient = a / b; // idiv
            
            return sum + diff + product + quotient;
        }
        
        // ロード/ストア命令（iload, istore, aload, astore）
        public String loadStoreOperations(String input) {
            String local = input;        // astore
            String result = local;       // aload
            int length = result.length(); // invokevirtual
            
            return result + " (length: " + length + ")";
        }
        
        // 制御フロー命令（if_icmpge, goto, ifeq）
        public String controlFlow(int value) {
            if (value > 0) {        // ifle (条件が反転される)
                return "positive";
            } else if (value < 0) { // ifge 
                return "negative";
            } else {                // else部分
                return "zero";
            }
        }
        
        // ループ構造（goto, if_icmpge）
        public int loopConstruct(int n) {
            int sum = 0;           // iconst_0, istore
            for (int i = 0; i < n; i++) { // ループ制御のバイトコード
                sum += i;          // iadd
            }
            return sum;
        }
        
        // オブジェクト操作（new, dup, invokespecial, invokevirtual）
        public List<String> objectOperations() {
            List<String> list = new ArrayList<>(); // new, dup, invokespecial
            list.add("Hello");                     // invokevirtual
            list.add("World");                     // invokevirtual
            
            return list;
        }
        
        // 配列操作（newarray, iaload, iastore）
        public int[] arrayOperations(int size) {
            int[] array = new int[size];  // newarray
            
            for (int i = 0; i < size; i++) {
                array[i] = i * 2;         // iastore
            }
            
            return array;
        }
        
        // 型変換（i2l, i2f, i2d）
        public void typeConversions(int value) {
            long longValue = value;     // i2l
            float floatValue = value;   // i2f
            double doubleValue = value; // i2d
            
            IO.println("Conversions: " + longValue + ", " + floatValue + ", " + doubleValue);
        }
        
        // メソッド呼び出し（invokestatic, invokevirtual, invokeinterface）
        public void methodInvocations() {
            // static method call
            String result = String.valueOf(42); // invokestatic
            
            // virtual method call
            int length = result.length();       // invokevirtual
            
            // interface method call
            List<String> list = new ArrayList<>();
            list.add(result);                   // invokeinterface
            
            IO.println("Method invocations: " + length + ", " + list.size());
        }
    }
    
    /**
     * バイトコード分析ツール
     */
    public static class BytecodeAnalyzer {
        
        /**
         * メソッドのバイトコード情報を表示（簡略版）
         */
        public static void analyzeMethod(Class<?> clazz, String methodName) {
            IO.println("=== Bytecode Analysis for " + clazz.getSimpleName() + "." + methodName + " ===");
            
            try {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals(methodName)) {
                        IO.println("Method: " + method);
                        IO.println("Return Type: " + method.getReturnType());
                        IO.println("Parameter Types: " + Arrays.toString(method.getParameterTypes()));
                        IO.println("Modifiers: " + Modifier.toString(method.getModifiers()));
                        
                        // 実際のバイトコード表示は javap -c コマンドを推奨
                        IO.println("\nTo see actual bytecode, run:");
                        IO.println("javap -c " + clazz.getName());
                        IO.println();
                        return;
                    }
                }
                IO.println("Method not found: " + methodName);
            } catch (Exception e) {
                System.err.println("Error analyzing method: " + e.getMessage());
            }
        }
        
        /**
         * クラスファイルの基本情報を表示
         */
        public static void analyzeClassFile(Class<?> clazz) {
            IO.println("=== Class File Analysis for " + clazz.getName() + " ===");
            
            // クラス基本情報
            IO.println("Class Name: " + clazz.getName());
            IO.println("Simple Name: " + clazz.getSimpleName());
            IO.println("Package: " + clazz.getPackage());
            IO.println("Modifiers: " + Modifier.toString(clazz.getModifiers()));
            IO.println("Superclass: " + clazz.getSuperclass());
            IO.println("Interfaces: " + Arrays.toString(clazz.getInterfaces()));
            
            // フィールド情報
            Field[] fields = clazz.getDeclaredFields();
            IO.println("\nFields (" + fields.length + "):");
            for (Field field : fields) {
                IO.println("  %s %s %s".formatted(
                                Modifier.toString(field.getModifiers()),
                                field.getType().getSimpleName(),
                                field.getName()));
            }
            
            // メソッド情報
            Method[] methods = clazz.getDeclaredMethods();
            IO.println("\nMethods (" + methods.length + "):");
            for (Method method : methods) {
                IO.println("  %s %s %s(%s)".formatted(
                                Modifier.toString(method.getModifiers()),
                                method.getReturnType().getSimpleName(),
                                method.getName(),
                                formatParameterTypes(method.getParameterTypes())));
            }
            
            IO.println();
        }
        
        private static String formatParameterTypes(Class<?>[] paramTypes) {
            if (paramTypes.length == 0) return "";
            
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) sb.append(", ");
                sb.append(paramTypes[i].getSimpleName());
            }
            return sb.toString();
        }
    }
    
    /**
     * JITコンパイレーションのデモンストレーション
     */
    public static class JITCompilationDemo {
        private static final int WARMUP_ITERATIONS = 20000;
        private static final int MEASUREMENT_ITERATIONS = 100000;
        
        /**
         * JITコンパイレーションの効果を測定
         */
        public static void demonstrateJITEffect() {
            IO.println("=== JIT Compilation Effect Demonstration ===");
            IO.println("Note: Run with -XX:+PrintCompilation to see JIT compilation events");
            
            // ホットメソッドの準備
            SimpleCalculation calc = new SimpleCalculation();
            
            // 1回目: インタープリター実行
            long coldTime = measurePerformance(() -> {
                int sum = 0;
                for (int i = 0; i < 1000; i++) {
                    sum += calc.complexCalculation(i);
                }
                return sum;
            });
            
            // ウォームアップ: JITコンパイルを促進
            IO.println("Warming up... (triggering JIT compilation)");
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                calc.complexCalculation(i % 100);
            }
            
            // 2回目: JITコンパイル後の実行
            long hotTime = measurePerformance(() -> {
                int sum = 0;
                for (int i = 0; i < 1000; i++) {
                    sum += calc.complexCalculation(i);
                }
                return sum;
            });
            
            IO.println("Cold execution time: %d μs".formatted(coldTime / 1000));
            IO.println("Hot execution time:  %d μs".formatted(hotTime / 1000));
            IO.println("Speedup: %.2fx".formatted((double) coldTime / hotTime));
            IO.println();
        }
        
        private static long measurePerformance(Supplier<Integer> task) {
            long start = System.nanoTime();
            
            // 複数回実行して平均を取る
            for (int i = 0; i < 10; i++) {
                task.get();
            }
            
            long end = System.nanoTime();
            return (end - start) / 10; // 平均時間
        }
        
        @FunctionalInterface
        interface Supplier<T> {
            T get();
        }
        
        /**
         * JITコンパイルの対象となる計算メソッド
         */
        static class SimpleCalculation {
            public int complexCalculation(int n) {
                int result = n;
                
                // 複数の計算を含む（インライン化やループ最適化の対象）
                for (int i = 0; i < 10; i++) {
                    result = (result * 17 + 23) % 97;
                    result = result > 50 ? result - 25 : result + 25;
                }
                
                return result;
            }
            
            // 条件分岐を含むメソッド（分岐予測の対象）
            public int branchingCalculation(int n) {
                if (n % 2 == 0) {
                    return n * n;
                } else if (n % 3 == 0) {
                    return n * n * n;
                } else if (n % 5 == 0) {
                    return n * 2;
                } else {
                    return n + 1;
                }
            }
        }
        
        /**
         * 分岐予測とプロファイルガイド最適化のデモ
         */
        public static void demonstrateBranchPrediction() {
            IO.println("=== Branch Prediction and Profile-Guided Optimization ===");
            
            SimpleCalculation calc = new SimpleCalculation();
            
            // 予測可能な分岐パターン（偶数が90%）
            IO.println("Testing predictable branching (90% even numbers)...");
            long predictableTime = measureBranchPerformance(calc, true);
            
            // 予測不可能な分岐パターン（ランダム）
            IO.println("Testing unpredictable branching (random)...");
            long unpredictableTime = measureBranchPerformance(calc, false);
            
            IO.println("Predictable branching time:   %d μs".formatted(predictableTime / 1000));
            IO.println("Unpredictable branching time: %d μs".formatted(unpredictableTime / 1000));
            IO.println("Predictable is %.2fx faster".formatted(
                            (double) unpredictableTime / predictableTime));
            IO.println();
        }
        
        private static long measureBranchPerformance(SimpleCalculation calc, boolean predictable) {
            // ウォームアップ
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                int input = predictable ? 
                           (i % 10 == 0 ? 1 : 2) :  // 90% even
                           ThreadLocalRandom.current().nextInt(100);
                calc.branchingCalculation(input);
            }
            
            // 測定
            long start = System.nanoTime();
            
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
                int input = predictable ? 
                           (i % 10 == 0 ? 1 : 2) :  // 90% even
                           ThreadLocalRandom.current().nextInt(100);
                calc.branchingCalculation(input);
            }
            
            long end = System.nanoTime();
            return end - start;
        }
    }
    
    /**
     * メソッドインライン化の分析
     */
    public static class InliningAnalysisDemo {
        private static final int WARMUP_ITERATIONS = 20000;
        private static final int MEASUREMENT_ITERATIONS = 100000;
        
        /**
         * インライン化されやすいメソッドの例
         */
        static class InlineCandidate {
            private int value;
            
            // 小さなメソッド（インライン化候補）
            public int getValue() {
                return value;
            }
            
            public void setValue(int value) {
                this.value = value;
            }
            
            // 単純な計算（インライン化候補）
            public int add(int x) {
                return value + x;
            }
            
            // やや複雑な計算（条件付きインライン化）
            public int conditionalAdd(int x) {
                if (x > 0) {
                    return value + x;
                } else {
                    return value;
                }
            }
        }
        
        /**
         * インライン化されにくいメソッドの例
         */
        static class NonInlineCandidate {
            private int value;
            
            // 大きなメソッド（インライン化されにくい）
            public int largeMethod(int x) {
                int result = value;
                
                // 多くの計算処理
                for (int i = 0; i < 10; i++) {
                    result = (result * 17 + 23) % 97;
                    result = (result * 31 + 41) % 89;
                    result = (result * 37 + 47) % 83;
                    
                    if (result > 50) {
                        result -= 25;
                    } else {
                        result += 25;
                    }
                }
                
                return result + x;
            }
            
            // 例外処理を含むメソッド（インライン化困難）
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
            
            // 同期メソッド（インライン化制限）
            public synchronized int synchronizedMethod(int x) {
                return value + x;
            }
        }
        
        public static void demonstrateInlining() {
            IO.println("=== Method Inlining Analysis ===");
            
            InlineCandidate inlinable = new InlineCandidate();
            NonInlineCandidate nonInlinable = new NonInlineCandidate();
            
            // ウォームアップとインライン化の促進
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                inlinable.setValue(i);
                int sum = inlinable.getValue() + inlinable.add(i) + inlinable.conditionalAdd(i);
                
                sum += nonInlinable.largeMethod(i % 100);
                sum += nonInlinable.methodWithException(i % 10 + 1);
                sum += nonInlinable.synchronizedMethod(i);
            }
            
            // インライン化されたメソッドのパフォーマンス測定
            long inlineTime = measureInlinablePerformance(inlinable);
            
            // インライン化されにくいメソッドのパフォーマンス測定
            long nonInlineTime = measureNonInlinablePerformance(nonInlinable);
            
            IO.println("Inlinable methods time:     %d μs".formatted(inlineTime / 1000));
            IO.println("Non-inlinable methods time: %d μs".formatted(nonInlineTime / 1000));
            IO.println("Inlinable is %.2fx faster".formatted(
                            (double) nonInlineTime / inlineTime));
            
            IO.println("\nTo see inlining decisions, run with:");
            IO.println("-XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining");
            IO.println();
        }
        
        private static long measureInlinablePerformance(InlineCandidate candidate) {
            long start = System.nanoTime();
            
            int sum = 0;
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
                candidate.setValue(i);
                sum += candidate.getValue();
                sum += candidate.add(i);
                sum += candidate.conditionalAdd(i % 2);
            }
            
            long end = System.nanoTime();
            
            // 結果を使用（デッドコード除去を防ぐ）
            if (sum < 0) IO.println("Unexpected result");
            
            return end - start;
        }
        
        private static long measureNonInlinablePerformance(NonInlineCandidate candidate) {
            long start = System.nanoTime();
            
            int sum = 0;
            for (int i = 0; i < MEASUREMENT_ITERATIONS / 10; i++) { // 回数を減らす
                sum += candidate.largeMethod(i);
                sum += candidate.methodWithException(i % 10 + 1);
                sum += candidate.synchronizedMethod(i);
            }
            
            long end = System.nanoTime();
            
            // 結果を使用（デッドコード除去を防ぐ）
            if (sum < 0) IO.println("Unexpected result");
            
            return (end - start) * 10; // 正規化
        }
    }
    
    /**
     * バイトコード命令セットのサンプル実装
     */
    public static class BytecodeInstructionDemo {
        
        public static void demonstrateInstructionTypes() {
            IO.println("=== Bytecode Instruction Types Demo ===");
            
            BasicBytecodeDemo demo = new BasicBytecodeDemo();
            
            // 各種バイトコード命令のデモ実行
            IO.println("1. Arithmetic Operations:");
            int arithmetic = demo.arithmeticOperations(10, 3);
            IO.println("   Result: " + arithmetic);
            
            IO.println("\n2. Load/Store Operations:");
            String loadStore = demo.loadStoreOperations("Hello Bytecode");
            IO.println("   Result: " + loadStore);
            
            IO.println("\n3. Control Flow:");
            IO.println("   controlFlow(5): " + demo.controlFlow(5));
            IO.println("   controlFlow(-3): " + demo.controlFlow(-3));
            IO.println("   controlFlow(0): " + demo.controlFlow(0));
            
            IO.println("\n4. Loop Construct:");
            int loopResult = demo.loopConstruct(10);
            IO.println("   Sum 0 to 9: " + loopResult);
            
            IO.println("\n5. Object Operations:");
            List<String> objects = demo.objectOperations();
            IO.println("   Created list: " + objects);
            
            IO.println("\n6. Array Operations:");
            int[] array = demo.arrayOperations(5);
            IO.println("   Created array: " + Arrays.toString(array));
            
            IO.println("\n7. Type Conversions:");
            demo.typeConversions(42);
            
            IO.println("\n8. Method Invocations:");
            demo.methodInvocations();
            
            IO.println();
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        IO.println("Bytecode Analysis and JIT Compilation Demonstration");
        IO.println("===================================================");
        
        // バイトコード命令のデモ
        BytecodeInstructionDemo.demonstrateInstructionTypes();
        
        // クラスファイル分析
        BytecodeAnalyzer.analyzeClassFile(BasicBytecodeDemo.class);
        
        // 特定メソッドの分析
        BytecodeAnalyzer.analyzeMethod(BasicBytecodeDemo.class, "arithmeticOperations");
        
        // JITコンパイレーション効果
        JITCompilationDemo.demonstrateJITEffect();
        
        // 分岐予測
        JITCompilationDemo.demonstrateBranchPrediction();
        
        // インライン化分析
        InliningAnalysisDemo.demonstrateInlining();
        
        IO.println("Key Insights:");
        IO.println("✓ Java source code compiles to platform-independent bytecode");
        IO.println("✓ JIT compiler optimizes hot methods at runtime");
        IO.println("✓ Branch prediction significantly affects performance");
        IO.println("✓ Method inlining eliminates call overhead");
        IO.println("✓ Understanding bytecode helps write efficient code");
        
        IO.println("\nOptimization Tips:");
        IO.println("• Keep frequently called methods small for inlining");
        IO.println("• Avoid unpredictable branching in hot paths");
        IO.println("• Use final classes/methods when inheritance isn't needed");
        IO.println("• Profile your application to identify hot spots");
        IO.println("• Trust JIT compiler - it's very sophisticated!");
        
        IO.println("\nUseful JVM Flags for Analysis:");
        IO.println("• -XX:+PrintCompilation : Show JIT compilation events");
        IO.println("• -XX:+PrintInlining : Show inlining decisions");
        IO.println("• -XX:+PrintAssembly : Show generated assembly code (requires hsdis)");
        IO.println("• -XX:CompileThreshold=1000 : Lower compilation threshold");
    }
}