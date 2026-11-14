package com.example.jvm;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

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
        
        // オブジェクト操作（new, dup, invokespecial, invokeinterface）
        public List<String> objectOperations() {
            List<String> list = new ArrayList<>(); // new, dup, invokespecial
            list.add("Hello");                     // invokeinterface（List型で受けているため）
            list.add("World");                     // invokeinterface
            
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
     * 測定値は環境やJVMの状態で大きく変わる。正確に測るにはJMHを使う
     */
    public static class JITCompilationDemo {
        private static final int WARMUP_ITERATIONS = 20000;
        private static final int MEASUREMENT_ITERATIONS = 100000;
        
        // 測定結果を捨てずに使うための変数（JITが計算ごと消さないようにする）
        private static long blackhole = 0;
        
        /**
         * JITコンパイレーションの効果を測定
         */
        public static void demonstrateJITEffect() {
            IO.println("=== JIT Compilation Effect Demonstration ===");
            IO.println("Note: Run with -XX:+PrintCompilation to see JIT compilation events");
            
            SimpleCalculation calc = new SimpleCalculation();
            
            // 1回目: まだJITコンパイルされていない状態（主にインタープリター実行）
            long coldTime = measurePerformance(() -> {
                int sum = 0;
                for (int i = 0; i < 1000; i++) {
                    sum += calc.complexCalculation(i);
                }
                return sum;
            });
            
            // ウォームアップ: 繰り返し呼んでJITコンパイルを促す
            IO.println("Warming up... (triggering JIT compilation)");
            for (int i = 0; i < WARMUP_ITERATIONS; i++) {
                blackhole += calc.complexCalculation(i % 100);
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
            IO.println("Speedup: " + ratio(coldTime, hotTime));
            IO.println();
        }
        
        private static long measurePerformance(Supplier<Integer> task) {
            long start = System.nanoTime();
            
            // 複数回実行して平均を取る
            for (int i = 0; i < 10; i++) {
                blackhole += task.get();
            }
            
            long end = System.nanoTime();
            return (end - start) / 10; // 平均時間
        }
        
        /**
         * 2つの時間の比を文字列にする（0で割らないようにする）
         */
        static String ratio(long slower, long faster) {
            if (slower <= 0 || faster <= 0) {
                return "n/a (too fast to measure)";
            }
            return "%.2fx".formatted((double) slower / faster);
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
                
                // ループと算術演算（ループ最適化の対象）
                for (int i = 0; i < 10; i++) {
                    result = (result * 17 + 23) % 97;
                    result = result > 50 ? result - 25 : result + 25;
                }
                
                return result;
            }
            
            // 条件分岐を含むメソッド
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
         * 分岐の予測しやすさによる違いのデモ
         * 予測しにくい分岐では、CPUの分岐予測が外れて処理が遅くなることがある
         * （JITは分岐の偏りのプロファイルも使ってコードを並べる）
         */
        public static void demonstrateBranchPrediction() {
            IO.println("=== Branch Prediction and Profile-Guided Optimization ===");
            
            SimpleCalculation calc = new SimpleCalculation();
            
            // 入力は先に作っておき、乱数生成の時間を測定に含めない
            int[] predictableInputs = new int[MEASUREMENT_ITERATIONS];
            int[] randomInputs = new int[MEASUREMENT_ITERATIONS];
            Random random = new Random(42);
            for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
                predictableInputs[i] = (i % 10 == 0) ? 1 : 2; // 90%が偶数
                randomInputs[i] = random.nextInt(100);
            }
            
            // 両方の入力でウォームアップする
            for (int round = 0; round < 5; round++) {
                runBranches(calc, predictableInputs);
                runBranches(calc, randomInputs);
            }
            
            IO.println("Testing predictable branching (90% even numbers)...");
            long predictableTime = measureBranches(calc, predictableInputs);
            
            IO.println("Testing unpredictable branching (random)...");
            long unpredictableTime = measureBranches(calc, randomInputs);
            
            IO.println("Predictable branching time:   %d μs".formatted(predictableTime / 1000));
            IO.println("Unpredictable branching time: %d μs".formatted(unpredictableTime / 1000));
            IO.println("Unpredictable / predictable: " + ratio(unpredictableTime, predictableTime));
            IO.println();
        }
        
        private static long runBranches(SimpleCalculation calc, int[] inputs) {
            long sum = 0;
            for (int input : inputs) {
                sum += calc.branchingCalculation(input);
            }
            blackhole += sum;
            return sum;
        }
        
        private static long measureBranches(SimpleCalculation calc, int[] inputs) {
            long start = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                runBranches(calc, inputs);
            }
            return (System.nanoTime() - start) / 10;
        }
    }
    
    /**
     * メソッドインライン化の分析
     * 小さなメソッドの呼び出しは、JITのインライン化によって
     * 同じ計算を直接書いた場合とほぼ同じ速さになる
     */
    public static class InliningAnalysisDemo {
        private static final int ITERATIONS = 1_000_000;
        
        /**
         * インライン化されやすい小さなメソッドを持つクラス
         */
        static class InlineCandidate {
            private int value;
            
            public int getValue() {
                return value;
            }
            
            public void setValue(int value) {
                this.value = value;
            }
            
            public int add(int x) {
                return value + x;
            }
        }
        
        public static void demonstrateInlining() {
            IO.println("=== Method Inlining Analysis ===");
            
            InlineCandidate candidate = new InlineCandidate();
            
            // 両方の書き方でウォームアップする
            for (int round = 0; round < 10; round++) {
                viaMethods(candidate);
                direct();
            }
            
            long methodTime = measure(() -> viaMethods(candidate));
            long directTime = measure(InliningAnalysisDemo::direct);
            
            IO.println("Via small methods:  %d μs".formatted(methodTime / 1000));
            IO.println("Direct calculation: %d μs".formatted(directTime / 1000));
            IO.println("Methods / direct: " + JITCompilationDemo.ratio(methodTime, directTime));
            IO.println("(After inlining, the two are usually close)");
            
            IO.println("\nTo see inlining decisions, run with:");
            IO.println("-XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining");
            IO.println("To compare without inlining, run with:");
            IO.println("-XX:CompileCommand=dontinline,*InlineCandidate.*");
            IO.println();
        }
        
        // 小さなメソッドを呼んで計算する
        private static long viaMethods(InlineCandidate candidate) {
            long sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                candidate.setValue(i);
                sum += candidate.getValue();
                sum += candidate.add(i);
            }
            return sum;
        }
        
        // 同じ計算を直接書く
        private static long direct() {
            long sum = 0;
            for (int i = 0; i < ITERATIONS; i++) {
                int value = i;
                sum += value;
                sum += value + i;
            }
            return sum;
        }
        
        private static long measure(java.util.function.LongSupplier task) {
            long start = System.nanoTime();
            for (int i = 0; i < 10; i++) {
                JITCompilationDemo.blackhole += task.getAsLong();
            }
            return (System.nanoTime() - start) / 10;
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
        IO.println("✓ Unpredictable branches can cause CPU branch mispredictions");
        IO.println("✓ Method inlining eliminates call overhead");
        IO.println("✓ Understanding bytecode helps write efficient code");
        
        IO.println("\nOptimization Tips:");
        IO.println("• Keep frequently called methods small for inlining");
        IO.println("• Avoid unpredictable branching in hot paths");
        IO.println("• Profile your application to identify hot spots");
        IO.println("• Trust JIT compiler - it's very sophisticated!");
        
        IO.println("\nUseful JVM Flags for Analysis:");
        IO.println("• -XX:+PrintCompilation : Show JIT compilation events");
        IO.println("• -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining : Show inlining decisions");
        IO.println("• -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly : Show generated assembly code (requires hsdis)");
        IO.println("• -XX:+PrintFlagsFinal -version : Show the default values of JIT thresholds");
    }
}