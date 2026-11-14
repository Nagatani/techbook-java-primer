package com.example.testing;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Property-based Testing（性質ベーステスト）のデモンストレーション
 * テストケースを手動で書く代わりに、満たすべき性質を定義してランダムデータで検証
 */
public class PropertyBasedTestDemo {
    
    /**
     * 簡易Property-based testingフレームワーク
     */
    public static class PropertyTester {
        // シードを固定すると、毎回同じ入力が生成され、失敗を再現できる。
        // -Dseed=数値 で別のシードを試せる
        private static final long SEED = Long.getLong("seed", 42L);
        private final Random random = new Random(SEED);
        private int iterations = 100;
        
        public PropertyTester withIterations(int iterations) {
            this.iterations = iterations;
            return this;
        }
        
        /**
         * 性質をテストする
         */
        public <T> void forAll(DataGenerator<T> generator, Property<T> property) {
            for (int i = 0; i < iterations; i++) {
                T data = generator.generate(random);
                try {
                    if (!property.test(data)) {
                        throw new PropertyViolationException(
                            "Property violated with input: " + data + " at iteration " + (i + 1)
                                + " (seed=" + SEED + ")"
                        );
                    }
                } catch (Exception e) {
                    throw new PropertyViolationException(
                        "Property test failed with input: " + data + " at iteration " + (i + 1)
                            + " (seed=" + SEED + ")", e
                    );
                }
            }
            IO.println("✓ Property verified over " + iterations + " test cases");
        }
    }
    
    @FunctionalInterface
    public interface DataGenerator<T> {
        T generate(Random random);
    }
    
    @FunctionalInterface
    public interface Property<T> {
        boolean test(T data);
    }
    
    public static class PropertyViolationException extends RuntimeException {
        public PropertyViolationException(String message) {
            super(message);
        }
        
        public PropertyViolationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * データジェネレータ
     */
    public static class Generators {
        public static DataGenerator<Integer> integers(int min, int max) {
            return random -> random.nextInt(max - min + 1) + min;
        }
        
        public static DataGenerator<String> strings(int minLength, int maxLength) {
            return random -> {
                int length = random.nextInt(maxLength - minLength + 1) + minLength;
                return random.ints(length, 'a', 'z' + 1)
                        .mapToObj(c -> String.valueOf((char) c))
                        .collect(Collectors.joining());
            };
        }
        
        public static <T> DataGenerator<List<T>> lists(DataGenerator<T> elementGenerator, int maxSize) {
            return random -> {
                int size = random.nextInt(maxSize + 1);
                return IntStream.range(0, size)
                        .mapToObj(i -> elementGenerator.generate(random))
                        .collect(Collectors.toList());
            };
        }
        
        public static DataGenerator<Double> doubles(double min, double max) {
            return random -> min + (max - min) * random.nextDouble();
        }
    }
    
    /**
     * ソートアルゴリズムのProperty-based testing
     */
    public static void testSortingProperties() {
        IO.println("=== Sorting Algorithm Property-based Testing ===");
        
        PropertyTester tester = new PropertyTester().withIterations(1000);
        
        // 性質1: ソート後の長さは変わらない
        IO.println("\nProperty 1: Sorting preserves length");
        tester.forAll(
            Generators.lists(Generators.integers(1, 1000), 100),
            list -> {
                List<Integer> sorted = new ArrayList<>(list);
                Collections.sort(sorted);
                return list.size() == sorted.size();
            }
        );
        
        // 性質2: ソートは冪等性を持つ（何度ソートしても結果は同じ）
        IO.println("\nProperty 2: Sorting is idempotent");
        tester.forAll(
            Generators.lists(Generators.integers(1, 1000), 50),
            list -> {
                List<Integer> sorted1 = new ArrayList<>(list);
                Collections.sort(sorted1);
                
                List<Integer> sorted2 = new ArrayList<>(sorted1);
                Collections.sort(sorted2);
                
                return sorted1.equals(sorted2);
            }
        );
        
        // 性質3: ソート結果は順序が正しい
        IO.println("\nProperty 3: Sorted list is in order");
        tester.forAll(
            Generators.lists(Generators.integers(1, 1000), 50),
            list -> {
                List<Integer> sorted = new ArrayList<>(list);
                Collections.sort(sorted);
                
                for (int i = 1; i < sorted.size(); i++) {
                    if (sorted.get(i - 1) > sorted.get(i)) {
                        return false;
                    }
                }
                return true;
            }
        );
        
        // 性質4: ソート前後で要素は保存される（並び替えのみ）
        IO.println("\nProperty 4: Sorting preserves elements");
        tester.forAll(
            Generators.lists(Generators.integers(1, 100), 30),
            list -> {
                List<Integer> sorted = new ArrayList<>(list);
                Collections.sort(sorted);
                
                Map<Integer, Long> originalCounts = list.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                Map<Integer, Long> sortedCounts = sorted.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
                
                return originalCounts.equals(sortedCounts);
            }
        );
    }
    
    /**
     * 文字列操作のProperty-based testing
     */
    public static void testStringProperties() {
        IO.println("\n=== String Operation Property-based Testing ===");
        
        PropertyTester tester = new PropertyTester().withIterations(500);
        
        // 性質1: reverse(reverse(s)) = s（逆転の逆転は元に戻る）
        IO.println("\nProperty 1: Reverse is involution");
        tester.forAll(
            Generators.strings(0, 50),
            str -> {
                String reversed = new StringBuilder(str).reverse().toString();
                String doubleReversed = new StringBuilder(reversed).reverse().toString();
                return str.equals(doubleReversed);
            }
        );
        
        // 性質2: concat(s1, s2).length = s1.length + s2.length
        IO.println("\nProperty 2: Concatenation preserves total length");
        tester.forAll(
            random -> Arrays.asList(
                Generators.strings(0, 25).generate(random),
                Generators.strings(0, 25).generate(random)
            ),
            strings -> {
                String s1 = strings.get(0);
                String s2 = strings.get(1);
                String concatenated = s1 + s2;
                return concatenated.length() == s1.length() + s2.length();
            }
        );
        
        // 性質3: substring操作の一貫性
        IO.println("\nProperty 3: Substring consistency");
        tester.forAll(
            random -> {
                String str = Generators.strings(1, 20).generate(random);
                int start = random.nextInt(str.length());
                int end = start + random.nextInt(str.length() - start + 1);
                return new SubstringTestData(str, start, end);
            },
            data -> {
                String substr = data.str.substring(data.start, data.end);
                return substr.length() == data.end - data.start;
            }
        );
    }
    
    static class SubstringTestData {
        final String str;
        final int start;
        final int end;
        
        SubstringTestData(String str, int start, int end) {
            this.str = str;
            this.start = start;
            this.end = end;
        }
        
        @Override
        public String toString() {
            return String.format("SubstringTestData{str='%s', start=%d, end=%d}", str, start, end);
        }
    }
    
    /**
     * データ構造の不変条件をテスト
     */
    public static void testDataStructureInvariants() {
        IO.println("\n=== Data Structure Invariant Testing ===");
        
        PropertyTester tester = new PropertyTester().withIterations(200);
        
        // 性質1: Stack操作の一貫性
        IO.println("\nProperty 1: Stack LIFO behavior");
        tester.forAll(
            Generators.lists(Generators.integers(1, 1000), 20),
            items -> {
                Stack<Integer> stack = new Stack<>();
                
                // プッシュ
                for (Integer item : items) {
                    stack.push(item);
                }
                
                // ポップして順序確認
                List<Integer> popped = new ArrayList<>();
                while (!stack.isEmpty()) {
                    popped.add(stack.pop());
                }
                
                // 逆順であることを確認
                Collections.reverse(popped);
                return items.equals(popped);
            }
        );
        
        // 性質2: Set操作の一意性
        IO.println("\nProperty 2: Set uniqueness invariant");
        tester.forAll(
            Generators.lists(Generators.integers(1, 100), 50),
            items -> {
                Set<Integer> set = new HashSet<>(items);
                // セットのサイズは重複を除いた数になる
                long uniqueCount = items.stream().distinct().count();
                return set.size() == uniqueCount;
            }
        );
        
        // 性質3: Map操作の一貫性
        IO.println("\nProperty 3: Map key-value consistency");
        tester.forAll(
            Generators.lists(
                random -> new AbstractMap.SimpleEntry<>(
                    Generators.strings(1, 10).generate(random),
                    Generators.integers(1, 1000).generate(random)
                ),
                30
            ),
            entries -> {
                Map<String, Integer> map = new HashMap<>();
                // 生成したキーは重複することがある。同じキーをputすると後の値で上書きされるので、
                // 期待する値は「そのキーで最後にputした値」になる
                Map<String, Integer> lastValues = new LinkedHashMap<>();
                for (Map.Entry<String, Integer> entry : entries) {
                    map.put(entry.getKey(), entry.getValue());
                    lastValues.put(entry.getKey(), entry.getValue());
                }
                
                // すべてのキーについて、最後にputした値が格納されていることを確認
                for (Map.Entry<String, Integer> expected : lastValues.entrySet()) {
                    if (!Objects.equals(map.get(expected.getKey()), expected.getValue())) {
                        return false;
                    }
                }
                // キーの数は、重複を除いた数と同じになる
                return map.size() == lastValues.size();
            }
        );
    }
    
    /**
     * 算術演算の性質をテスト
     */
    public static void testArithmeticProperties() {
        IO.println("\n=== Arithmetic Operation Property-based Testing ===");
        
        PropertyTester tester = new PropertyTester().withIterations(1000);
        
        // 性質1: 加法の交換法則
        IO.println("\nProperty 1: Addition is commutative");
        tester.forAll(
            random -> Arrays.asList(
                Generators.integers(-1000, 1000).generate(random),
                Generators.integers(-1000, 1000).generate(random)
            ),
            numbers -> {
                int a = numbers.get(0);
                int b = numbers.get(1);
                return a + b == b + a;
            }
        );
        
        // 性質2: 加法の結合法則
        IO.println("\nProperty 2: Addition is associative");
        tester.forAll(
            random -> Arrays.asList(
                Generators.integers(-300, 300).generate(random),
                Generators.integers(-300, 300).generate(random),
                Generators.integers(-300, 300).generate(random)
            ),
            numbers -> {
                int a = numbers.get(0);
                int b = numbers.get(1);
                int c = numbers.get(2);
                return (a + b) + c == a + (b + c);
            }
        );
        
        // 性質3: 乗法の分配法則
        IO.println("\nProperty 3: Multiplication distributes over addition");
        tester.forAll(
            random -> Arrays.asList(
                Generators.integers(-100, 100).generate(random),
                Generators.integers(-100, 100).generate(random),
                Generators.integers(-100, 100).generate(random)
            ),
            numbers -> {
                int a = numbers.get(0);
                int b = numbers.get(1);
                int c = numbers.get(2);
                return a * (b + c) == a * b + a * c;
            }
        );
        
        // 性質4: 除算と乗算の逆関係（ゼロ除算を除く）
        IO.println("\nProperty 4: Division is inverse of multiplication");
        tester.forAll(
            random -> {
                double a = Generators.doubles(-1000.0, 1000.0).generate(random);
                double b = Generators.doubles(-1000.0, 1000.0).generate(random);
                // ゼロに近い値は除外
                while (Math.abs(b) < 0.001) {
                    b = Generators.doubles(-1000.0, 1000.0).generate(random);
                }
                return Arrays.asList(a, b);
            },
            numbers -> {
                double a = numbers.get(0);
                double b = numbers.get(1);
                double result = (a * b) / b;
                // 浮動小数点誤差を考慮
                return Math.abs(result - a) < 0.0001;
            }
        );
    }
    
    /**
     * 状態ベースのProperty Testing
     */
    public static void testStatefulProperties() {
        IO.println("\n=== Stateful Property-based Testing ===");
        
        PropertyTester tester = new PropertyTester().withIterations(100);
        
        IO.println("\nProperty: Bank account balance consistency");
        tester.forAll(
            random -> {
                // ランダムな操作シーケンスを生成
                int operationCount = random.nextInt(20) + 1;
                List<BankOperation> operations = new ArrayList<>();
                
                for (int i = 0; i < operationCount; i++) {
                    if (random.nextBoolean()) {
                        // 預金操作
                        operations.add(new BankOperation(
                            BankOperation.Type.DEPOSIT,
                            random.nextInt(1000) + 1
                        ));
                    } else {
                        // 引き出し操作
                        operations.add(new BankOperation(
                            BankOperation.Type.WITHDRAW,
                            random.nextInt(500) + 1
                        ));
                    }
                }
                
                return operations;
            },
            operations -> {
                BankAccount account = new BankAccount(1000); // 初期残高1000
                int expectedBalance = 1000;
                
                for (BankOperation op : operations) {
                    try {
                        if (op.type == BankOperation.Type.DEPOSIT) {
                            account.deposit(op.amount);
                            expectedBalance += op.amount;
                        } else {
                            account.withdraw(op.amount);
                            expectedBalance -= op.amount;
                        }
                        
                        // 残高が一致することを確認
                        if (account.getBalance() != expectedBalance) {
                            return false;
                        }
                        
                        // 残高がマイナスになっていないことを確認
                        if (account.getBalance() < 0) {
                            return false;
                        }
                        
                    } catch (IllegalArgumentException e) {
                        // 引き出し不可の場合、残高は変わらないはず
                        if (account.getBalance() != expectedBalance) {
                            return false;
                        }
                    }
                }
                
                return true;
            }
        );
    }
    
    static class BankOperation {
        enum Type { DEPOSIT, WITHDRAW }
        final Type type;
        final int amount;
        
        BankOperation(Type type, int amount) {
            this.type = type;
            this.amount = amount;
        }
        
        @Override
        public String toString() {
            return type + "(" + amount + ")";
        }
    }
    
    static class BankAccount {
        private int balance;
        
        BankAccount(int initialBalance) {
            this.balance = initialBalance;
        }
        
        void deposit(int amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            balance += amount;
        }
        
        void withdraw(int amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            if (balance < amount) {
                throw new IllegalArgumentException("Insufficient balance");
            }
            balance -= amount;
        }
        
        int getBalance() {
            return balance;
        }
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        IO.println("Property-based Testing Demonstration");
        IO.println("====================================");
        IO.println("Seed: " + PropertyTester.SEED + " (change with -Dseed=...)");
        
        try {
            testSortingProperties();
            testStringProperties();
            testDataStructureInvariants();
            testArithmeticProperties();
            testStatefulProperties();
            
            IO.println("\n" + "=".repeat(50));
            IO.println("All property-based tests passed!");
            IO.println("\nProperty-based Testing Benefits:");
            IO.println("✓ Discovers edge cases that manual tests might miss");
            IO.println("✓ Tests fundamental properties rather than specific examples");
            IO.println("✓ Provides higher confidence through many test cases");
            IO.println("✓ Finds bugs that traditional unit tests overlook");
            IO.println("✓ Documents system behavior through properties");
            
        } catch (PropertyViolationException e) {
            System.err.println("× Property-based test failed: " + e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
        }
    }
}