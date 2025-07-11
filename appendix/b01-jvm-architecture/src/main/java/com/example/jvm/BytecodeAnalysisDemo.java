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
            
            System.out.println("Conversions: " + longValue + ", " + floatValue + ", " + doubleValue);
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
            
            System.out.println("Method invocations: " + length + ", " + list.size());
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
            System.out.println("=== Bytecode Analysis for " + clazz.getSimpleName() + "." + methodName + " ===");
            
            try {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (method.getName().equals(methodName)) {
                        System.out.println("Method: " + method);
                        System.out.println("Return Type: " + method.getReturnType());
                        System.out.println("Parameter Types: " + Arrays.toString(method.getParameterTypes()));
                        System.out.println("Modifiers: " + Modifier.toString(method.getModifiers()));
                        
                        // 実際のバイトコード表示は javap -c コマンドを推奨
                        System.out.println("\nTo see actual bytecode, run:");
                        System.out.println("javap -c " + clazz.getName());
                        System.out.println();
                        return;
                    }
                }
                System.out.println("Method not found: " + methodName);
            } catch (Exception e) {
                System.err.println("Error analyzing method: " + e.getMessage());
            }
        }
        
        /**
         * クラスファイルの基本情報を表示
         */
        public static void analyzeClassFile(Class<?> clazz) {
            System.out.println("=== Class File Analysis for " + clazz.getName() + " ===");
            
            // クラス基本情報
            System.out.println("Class Name: " + clazz.getName());
            System.out.println("Simple Name: " + clazz.getSimpleName());
            System.out.println("Package: " + clazz.getPackage());
            System.out.println("Modifiers: " + Modifier.toString(clazz.getModifiers()));
            System.out.println("Superclass: " + clazz.getSuperclass());
            System.out.println("Interfaces: " + Arrays.toString(clazz.getInterfaces()));
            
            // フィールド情報
            Field[] fields = clazz.getDeclaredFields();
            System.out.println("\nFields (" + fields.length + "):");
            for (Field field : fields) {
                System.out.printf("  %s %s %s%n", 
                                Modifier.toString(field.getModifiers()),
                                field.getType().getSimpleName(),
                                field.getName());
            }
            
            // メソッド情報
            Method[] methods = clazz.getDeclaredMethods();
            System.out.println("\nMethods (" + methods.length + "):");
            for (Method method : methods) {
                System.out.printf("  %s %s %s(%s)%n",
                                Modifier.toString(method.getModifiers()),
                                method.getReturnType().getSimpleName(),
                                method.getName(),
                                formatParameterTypes(method.getParameterTypes()));
            }
            
            System.out.println();
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
            System.out.println("=== JIT Compilation Effect Demonstration ===");
            System.out.println("Note: Run with -XX:+PrintCompilation to see JIT compilation events");
            
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
            System.out.println("Warming up... (triggering JIT compilation)");
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
            
            System.out.printf("Cold execution time: %d μs%n", coldTime / 1000);
            System.out.printf("Hot execution time:  %d μs%n", hotTime / 1000);
            System.out.printf("Speedup: %.2fx%n", (double) coldTime / hotTime);
            System.out.println();
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
            System.out.println("=== Branch Prediction and Profile-Guided Optimization ===");
            
            SimpleCalculation calc = new SimpleCalculation();
            
            // 予測可能な分岐パターン（偶数が90%）
            System.out.println("Testing predictable branching (90% even numbers)...");
            long predictableTime = measureBranchPerformance(calc, true);
            
            // 予測不可能な分岐パターン（ランダム）
            System.out.println("Testing unpredictable branching (random)...");
            long unpredictableTime = measureBranchPerformance(calc, false);
            
            System.out.printf("Predictable branching time:   %d μs%n", predictableTime / 1000);
            System.out.printf("Unpredictable branching time: %d μs%n", unpredictableTime / 1000);
            System.out.printf("Predictable is %.2fx faster%n", 
                            (double) unpredictableTime / predictableTime);
            System.out.println();
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
            System.out.println("=== Method Inlining Analysis ===");
            
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
            
            System.out.printf("Inlinable methods time:     %d μs%n", inlineTime / 1000);
            System.out.printf("Non-inlinable methods time: %d μs%n", nonInlineTime / 1000);
            System.out.printf("Inlinable is %.2fx faster%n", 
                            (double) nonInlineTime / inlineTime);
            
            System.out.println("\nTo see inlining decisions, run with:");
            System.out.println("-XX:+PrintCompilation -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining");
            System.out.println();
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
            if (sum < 0) System.out.println("Unexpected result");
            
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
            if (sum < 0) System.out.println("Unexpected result");
            
            return (end - start) * 10; // 正規化
        }
    }
    
    /**
     * バイトコード命令セットのサンプル実装
     */
    public static class BytecodeInstructionDemo {
        
        public static void demonstrateInstructionTypes() {
            System.out.println("=== Bytecode Instruction Types Demo ===");
            
            BasicBytecodeDemo demo = new BasicBytecodeDemo();
            
            // 各種バイトコード命令のデモ実行
            System.out.println("1. Arithmetic Operations:");
            int arithmetic = demo.arithmeticOperations(10, 3);
            System.out.println("   Result: " + arithmetic);
            
            System.out.println("\n2. Load/Store Operations:");
            String loadStore = demo.loadStoreOperations("Hello Bytecode");
            System.out.println("   Result: " + loadStore);
            
            System.out.println("\n3. Control Flow:");
            System.out.println("   controlFlow(5): " + demo.controlFlow(5));
            System.out.println("   controlFlow(-3): " + demo.controlFlow(-3));
            System.out.println("   controlFlow(0): " + demo.controlFlow(0));
            
            System.out.println("\n4. Loop Construct:");
            int loopResult = demo.loopConstruct(10);
            System.out.println("   Sum 0 to 9: " + loopResult);
            
            System.out.println("\n5. Object Operations:");
            List<String> objects = demo.objectOperations();
            System.out.println("   Created list: " + objects);
            
            System.out.println("\n6. Array Operations:");
            int[] array = demo.arrayOperations(5);
            System.out.println("   Created array: " + Arrays.toString(array));
            
            System.out.println("\n7. Type Conversions:");
            demo.typeConversions(42);
            
            System.out.println("\n8. Method Invocations:");
            demo.methodInvocations();
            
            System.out.println();
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        System.out.println("Bytecode Analysis and JIT Compilation Demonstration");
        System.out.println("===================================================");
        
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
        
        System.out.println("🎯 Key Insights:");
        System.out.println("✓ Java source code compiles to platform-independent bytecode");
        System.out.println("✓ JIT compiler optimizes hot methods at runtime");
        System.out.println("✓ Branch prediction significantly affects performance");
        System.out.println("✓ Method inlining eliminates call overhead");
        System.out.println("✓ Understanding bytecode helps write efficient code");
        
        System.out.println("\n⚡ Optimization Tips:");
        System.out.println("• Keep frequently called methods small for inlining");
        System.out.println("• Avoid unpredictable branching in hot paths");
        System.out.println("• Use final classes/methods when inheritance isn't needed");
        System.out.println("• Profile your application to identify hot spots");
        System.out.println("• Trust JIT compiler - it's very sophisticated!");
        
        System.out.println("\n🔧 Useful JVM Flags for Analysis:");
        System.out.println("• -XX:+PrintCompilation : Show JIT compilation events");
        System.out.println("• -XX:+PrintInlining : Show inlining decisions");
        System.out.println("• -XX:+PrintAssembly : Show generated assembly code (requires hsdis)");
        System.out.println("• -XX:CompileThreshold=1000 : Lower compilation threshold");
    }
}