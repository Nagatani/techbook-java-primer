package com.example.interfaces;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 関数型インターフェイスの拡張とdefaultメソッドの活用
 * 関数合成とユーティリティ機能を提供
 */
public class EnhancedFunctionalInterface {
    
    /**
     * 拡張されたFunction型インターフェイス
     */
    @FunctionalInterface
    interface EnhancedFunction<T, R> extends Function<T, R> {
        
        // Function<T, R>のapplyメソッドは抽象メソッド
        
        /**
         * 条件付き適用
         */
        default Function<T, R> when(Predicate<T> condition, R defaultValue) {
            return t -> condition.test(t) ? this.apply(t) : defaultValue;
        }
        
        /**
         * リトライ機能付き実行
         */
        default Function<T, R> withRetry(int maxAttempts) {
            return t -> {
                Exception lastException = null;
                for (int i = 0; i < maxAttempts; i++) {
                    try {
                        return this.apply(t);
                    } catch (Exception e) {
                        lastException = e;
                        System.out.println("Attempt " + (i + 1) + " failed: " + e.getMessage());
                    }
                }
                throw new RuntimeException("Failed after " + maxAttempts + " attempts", lastException);
            };
        }
        
        /**
         * メモ化（計算結果のキャッシュ）
         */
        default Function<T, R> memoized() {
            Map<T, R> cache = new ConcurrentHashMap<>();
            return t -> cache.computeIfAbsent(t, this::apply);
        }
        
        /**
         * 実行時間測定
         */
        default Function<T, R> timed() {
            return t -> {
                long startTime = System.nanoTime();
                try {
                    R result = this.apply(t);
                    long endTime = System.nanoTime();
                    System.out.println("Execution time: " + (endTime - startTime) / 1_000_000.0 + "ms");
                    return result;
                } catch (Exception e) {
                    long endTime = System.nanoTime();
                    System.out.println("Execution time (failed): " + (endTime - startTime) / 1_000_000.0 + "ms");
                    throw e;
                }
            };
        }
        
        /**
         * nullセーフな実行
         */
        default Function<T, R> nullSafe(R defaultValue) {
            return t -> {
                if (t == null) {
                    return defaultValue;
                }
                try {
                    return this.apply(t);
                } catch (Exception e) {
                    return defaultValue;
                }
            };
        }
        
        /**
         * 関数の合成（チェーン）
         */
        default <V> EnhancedFunction<T, V> thenApply(Function<R, V> after) {
            return t -> after.apply(this.apply(t));
        }
        
        /**
         * 関数の前処理
         */
        default <V> EnhancedFunction<V, R> preProcess(Function<V, T> before) {
            return v -> this.apply(before.apply(v));
        }
        
        /**
         * 静的ファクトリメソッド
         */
        static <T, R> EnhancedFunction<T, R> of(Function<T, R> function) {
            return function::apply;
        }
        
        /**
         * 恒等関数
         */
        static <T> EnhancedFunction<T, T> identity() {
            return t -> t;
        }
        
        /**
         * 定数関数
         */
        static <T, R> EnhancedFunction<T, R> constant(R value) {
            return t -> value;
        }
    }
    
    /**
     * 拡張されたPredicate型インターフェイス
     */
    @FunctionalInterface
    interface EnhancedPredicate<T> extends Predicate<T> {
        
        /**
         * 条件の説明を取得
         */
        default String getDescription() {
            return "Custom predicate";
        }
        
        /**
         * 条件の反転（説明付き）
         */
        default EnhancedPredicate<T> notWithDescription(String description) {
            return new EnhancedPredicate<T>() {
                @Override
                public boolean test(T t) {
                    return !EnhancedPredicate.this.test(t);
                }
                
                @Override
                public String getDescription() {
                    return description;
                }
            };
        }
        
        /**
         * 複数条件の組み合わせ（説明付き）
         */
        default EnhancedPredicate<T> andWithDescription(EnhancedPredicate<T> other, String description) {
            return new EnhancedPredicate<T>() {
                @Override
                public boolean test(T t) {
                    return EnhancedPredicate.this.test(t) && other.test(t);
                }
                
                @Override
                public String getDescription() {
                    return description;
                }
            };
        }
        
        /**
         * 静的ファクトリメソッド
         */
        static <T> EnhancedPredicate<T> of(Predicate<T> predicate, String description) {
            return new EnhancedPredicate<T>() {
                @Override
                public boolean test(T t) {
                    return predicate.test(t);
                }
                
                @Override
                public String getDescription() {
                    return description;
                }
            };
        }
        
        /**
         * 範囲チェック
         */
        static <T extends Comparable<T>> EnhancedPredicate<T> between(T min, T max) {
            return new EnhancedPredicate<T>() {
                @Override
                public boolean test(T t) {
                    return t.compareTo(min) >= 0 && t.compareTo(max) <= 0;
                }
                
                @Override
                public String getDescription() {
                    return "between " + min + " and " + max;
                }
            };
        }
    }
    
    /**
     * 実演用のサンプルクラス
     */
    static class Person {
        private final String name;
        private final int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        public String getName() { return name; }
        public int getAge() { return age; }
        
        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + '}';
        }
    }
    
    /**
     * 数値計算のサンプル関数
     */
    static class MathOperations {
        
        // 重い計算をシミュレート
        static EnhancedFunction<Integer, Integer> expensiveCalculation = 
            EnhancedFunction.of(n -> {
                try {
                    Thread.sleep(100); // 100ms待機
                    return n * n * n; // 3乗計算
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return 0;
                }
            });
        
        // 失敗する可能性のある計算
        static EnhancedFunction<Integer, Integer> unreliableCalculation = 
            EnhancedFunction.of(n -> {
                if (Math.random() < 0.5) {
                    throw new RuntimeException("Random failure");
                }
                return n * 2;
            });
        
        // 文字列パーサー
        static EnhancedFunction<String, Integer> stringParser = 
            EnhancedFunction.of(Integer::parseInt);
    }
    
    /**
     * デモンストレーション
     */
    public static void demonstrateEnhancedFunctions() {
        System.out.println("=== Enhanced Function Interface Demo ===");
        
        // 基本的な関数の使用
        System.out.println("\n--- Basic Function Usage ---");
        EnhancedFunction<String, Integer> lengthFunction = EnhancedFunction.of(String::length);
        System.out.println("Length of 'Hello': " + lengthFunction.apply("Hello"));
        
        // 条件付き適用
        System.out.println("\n--- Conditional Application ---");
        Function<String, Integer> conditionalLength = lengthFunction
            .when(s -> s != null && !s.isEmpty(), -1);
        System.out.println("Length of 'Hello': " + conditionalLength.apply("Hello"));
        System.out.println("Length of '': " + conditionalLength.apply(""));
        System.out.println("Length of null: " + conditionalLength.apply(null));
        
        // メモ化のデモ
        System.out.println("\n--- Memoization Demo ---");
        Function<Integer, Integer> memoizedExpensive = MathOperations.expensiveCalculation.memoized();
        
        System.out.println("First call (should be slow):");
        System.out.println("Result: " + memoizedExpensive.apply(5));
        
        System.out.println("Second call (should be fast):");
        System.out.println("Result: " + memoizedExpensive.apply(5));
        
        // 実行時間測定
        System.out.println("\n--- Execution Time Measurement ---");
        Function<Integer, Integer> timedExpensive = MathOperations.expensiveCalculation.timed();
        System.out.println("Timed result: " + timedExpensive.apply(3));
        
        // リトライ機能
        System.out.println("\n--- Retry Mechanism ---");
        Function<Integer, Integer> reliableCalculation = MathOperations.unreliableCalculation.withRetry(3);
        try {
            System.out.println("Reliable result: " + reliableCalculation.apply(10));
        } catch (Exception e) {
            System.out.println("Failed after retries: " + e.getMessage());
        }
        
        // nullセーフな実行
        System.out.println("\n--- Null Safe Execution ---");
        Function<String, Integer> safeParser = MathOperations.stringParser.nullSafe(-1);
        System.out.println("Parse '123': " + safeParser.apply("123"));
        System.out.println("Parse null: " + safeParser.apply(null));
        System.out.println("Parse 'abc': " + safeParser.apply("abc"));
        
        // 関数の合成
        System.out.println("\n--- Function Composition ---");
        EnhancedFunction<Person, String> nameExtractor = EnhancedFunction.of(Person::getName);
        EnhancedFunction<Person, Integer> nameLength = nameExtractor.thenApply(String::length);
        
        Person person = new Person("Alice", 30);
        System.out.println("Person: " + person);
        System.out.println("Name length: " + nameLength.apply(person));
        
        // 拡張Predicateのデモ
        System.out.println("\n--- Enhanced Predicate Demo ---");
        EnhancedPredicate<Person> isAdult = EnhancedPredicate.of(p -> p.getAge() >= 18, "is adult");
        EnhancedPredicate<Person> hasLongName = EnhancedPredicate.of(p -> p.getName().length() > 5, "has long name");
        
        EnhancedPredicate<Person> isAdultWithLongName = isAdult
            .andWithDescription(hasLongName, "is adult with long name");
        
        Person[] people = {
            new Person("Alice", 30),
            new Person("Bob", 16),
            new Person("Alexander", 25)
        };
        
        for (Person p : people) {
            System.out.println(p + " -> " + isAdultWithLongName.getDescription() + ": " + 
                             isAdultWithLongName.test(p));
        }
        
        // 範囲チェック
        System.out.println("\n--- Range Check ---");
        EnhancedPredicate<Integer> inRange = EnhancedPredicate.between(10, 20);
        System.out.println("15 " + inRange.getDescription() + ": " + inRange.test(15));
        System.out.println("25 " + inRange.getDescription() + ": " + inRange.test(25));
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateEnhancedFunctions();
    }
}