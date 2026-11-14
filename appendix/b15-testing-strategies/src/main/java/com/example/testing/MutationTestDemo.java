package com.example.testing;

import java.util.ArrayList;
import java.util.List;

/**
 * ミューテーションテストのデモンストレーション
 * テストの品質を評価し、見逃されるバグを発見するための手法
 *
 * <p>実際のミューテーションテストツール（PITなど）は、バイトコードを書き換えて
 * ミュータント（わざと小さなバグを入れたコード）を自動で作る。
 * このデモでは、仕組みが分かるように、ミュータントをサブクラスとして手で用意し、
 * 同じテストを元のコードと各ミュータントに対して実行する。
 * テストが失敗すればミュータントを「殺した（killed）」、成功すれば「生き残った（survived）」と数える。
 */
public class MutationTestDemo {
    
    // テスト対象のビジネスロジッククラス群
    // ミュータントをサブクラスで作れるよう、インスタンスメソッドにしている
    
    /**
     * 温度変換ユーティリティ（ミューテーション対象）
     */
    public static class TemperatureConverter {
        
        /**
         * 摂氏から華氏への変換
         */
        public double celsiusToFahrenheit(double celsius) {
            return (celsius * 9.0 / 5.0) + 32.0;
        }
        
        /**
         * 華氏から摂氏への変換
         */
        public double fahrenheitToCelsius(double fahrenheit) {
            return (fahrenheit - 32.0) * 5.0 / 9.0;
        }
        
        /**
         * 温度が氷点下かどうかの判定
         */
        public boolean isFreezing(double celsius) {
            return celsius <= 0.0;
        }
        
        /**
         * 温度カテゴリの判定
         */
        public String getTemperatureCategory(double celsius) {
            return category(celsius, 0, 10, 25, 35, false);
        }
        
        /**
         * カテゴリの判定の本体。境界値と、境界を含めるか（inclusive）を引数で受け取り、
         * ミュータントが境界の条件だけを変えられるようにしている
         */
        protected String category(double celsius, double freezing, double cold,
                                  double mild, double warm, boolean inclusive) {
            if (inclusive ? celsius <= freezing : celsius < freezing) {
                return "Freezing";
            } else if (celsius < cold) {
                return "Cold";
            } else if (celsius < mild) {
                return "Mild";
            } else if (celsius < warm) {
                return "Warm";
            } else {
                return "Hot";
            }
        }
    }
    
    /**
     * 計算機クラス（ミューテーション対象）
     */
    public static class Calculator {
        
        public int add(int a, int b) {
            return a + b;
        }
        
        public int subtract(int a, int b) {
            return a - b;
        }
        
        public int multiply(int a, int b) {
            return a * b;
        }
        
        public double divide(int a, int b) {
            if (b == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return (double) a / b;
        }
        
        public int max(int a, int b) {
            return (a > b) ? a : b;
        }
        
        public boolean isEven(int number) {
            return number % 2 == 0;
        }
        
        public long factorial(int n) {
            if (n < 0) {
                throw new IllegalArgumentException("Negative number");
            }
            long result = 1;
            for (int i = 2; i <= n; i++) {
                result *= i;
            }
            return result;
        }
    }
    
    /**
     * バリデータクラス（ミューテーション対象）
     */
    public static class UserValidator {
        
        public boolean isValidEmail(String email) {
            if (email == null) {
                return false;
            }
            return email.contains("@") && email.contains(".");
        }
        
        /**
         * 8文字以上で、数字と英字の両方を含むパスワードを有効とする
         */
        public boolean isValidPassword(String password) {
            if (password == null) {
                return false;
            }
            if (password.length() >= 8) {
                return containsDigit(password) && containsLetter(password);
            }
            return false;
        }
        
        protected boolean containsDigit(String str) {
            return str.chars().anyMatch(Character::isDigit);
        }
        
        protected boolean containsLetter(String str) {
            return str.chars().anyMatch(Character::isLetter);
        }
        
        public boolean isValidAge(int age) {
            return age >= 0 && age <= 150;
        }
    }
    
    /**
     * ミュータント（説明と、バグを入れた実装の組）
     */
    record Mutant<T>(String description, T implementation) {
    }
    
    /**
     * テストスイート（実装を受け取り、期待と違えばAssertionErrorを投げる）
     */
    @FunctionalInterface
    interface TestSuite<T> {
        void run(T target);
    }
    
    /**
     * 条件が偽ならAssertionErrorを投げる。
     * Javaのassert文は、java -eaを付けないと評価されないため使わない
     */
    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
    
    /**
     * 簡易ミューテーションテストエンジン
     */
    public static class SimpleMutationTester {
        
        /**
         * ミューテーションの種類
         */
        public enum MutationType {
            CONDITIONAL_BOUNDARY("< to <=, > to >=, etc."),
            NEGATE_CONDITIONALS("== to !=, < to >=, etc."),
            ARITHMETIC_OPERATOR("+ to -, * to /, etc."),
            RETURN_VALUE("return true to false, return x to 0, etc."),
            REMOVE_CALL("method() to /* method() */");
            
            private final String description;
            
            MutationType(String description) {
                this.description = description;
            }
            
            @Override
            public String toString() {
                return name() + ": " + description;
            }
        }
        
        /**
         * ミューテーションテスト結果
         */
        public static class MutationResult {
            private final String testClass;
            private final int totalMutations;
            private final int killedMutations;
            private final List<String> survivedMutationDetails;
            
            public MutationResult(String testClass, int totalMutations, int killedMutations,
                                  List<String> survivedMutationDetails) {
                this.testClass = testClass;
                this.totalMutations = totalMutations;
                this.killedMutations = killedMutations;
                this.survivedMutationDetails = List.copyOf(survivedMutationDetails);
            }
            
            public double getMutationScore() {
                return totalMutations > 0 ? (double) killedMutations / totalMutations : 0.0;
            }
            
            public List<String> getSurvivedMutationDetails() {
                return survivedMutationDetails;
            }
            
            @Override
            public String toString() {
                return "MutationResult{class='%s', total=%d, killed=%d, survived=%d, score=%.1f%%}".formatted(
                    testClass, totalMutations, killedMutations,
                    totalMutations - killedMutations, getMutationScore() * 100);
            }
        }
        
        /**
         * テストが成功すればtrue、失敗（AssertionErrorや予期しない例外）すればfalseを返す
         */
        static <T> boolean passes(TestSuite<T> tests, T target) {
            try {
                tests.run(target);
                return true;
            } catch (AssertionError | RuntimeException e) {
                return false;
            }
        }
        
        /**
         * 元のコードでテストが通ることを確かめてから、各ミュータントに対してテストを実行する
         */
        public static <T> MutationResult run(String name, T original,
                                             List<Mutant<T>> mutants, TestSuite<T> tests) {
            IO.println("Running mutation test for: " + name);
            if (!passes(tests, original)) {
                throw new IllegalStateException("Tests must pass on the original code: " + name);
            }
            
            int killed = 0;
            List<String> survived = new ArrayList<>();
            for (Mutant<T> mutant : mutants) {
                IO.println("  Testing mutation: " + mutant.description());
                if (passes(tests, mutant.implementation())) {
                    // テストが成功 = ミューテーションを検出できなかった（survived）
                    IO.println("    ✗ Mutation survived - test gap or equivalent mutant");
                    survived.add(mutant.description());
                } else {
                    // テストが失敗 = ミューテーションを検出した（killed）
                    killed++;
                    IO.println("    ✓ Mutation killed by tests");
                }
            }
            return new MutationResult(name, mutants.size(), killed, survived);
        }
    }
    
    /**
     * 温度変換器のミュータント
     */
    static List<Mutant<TemperatureConverter>> temperatureMutants() {
        return List.of(
            new Mutant<>("celsiusToFahrenheit: + 32.0 to - 32.0 (arithmetic operator)",
                new TemperatureConverter() {
                    @Override
                    public double celsiusToFahrenheit(double celsius) {
                        return (celsius * 9.0 / 5.0) - 32.0;
                    }
                }),
            new Mutant<>("celsiusToFahrenheit: 32.0 to 31.0 (constant)",
                new TemperatureConverter() {
                    @Override
                    public double celsiusToFahrenheit(double celsius) {
                        return (celsius * 9.0 / 5.0) + 31.0;
                    }
                }),
            new Mutant<>("fahrenheitToCelsius: - 32.0 to + 32.0 (arithmetic operator)",
                new TemperatureConverter() {
                    @Override
                    public double fahrenheitToCelsius(double fahrenheit) {
                        return (fahrenheit + 32.0) * 5.0 / 9.0;
                    }
                }),
            new Mutant<>("isFreezing: <= to < (conditional boundary)",
                new TemperatureConverter() {
                    @Override
                    public boolean isFreezing(double celsius) {
                        return celsius < 0.0;
                    }
                }),
            new Mutant<>("isFreezing: <= to > (negated conditional)",
                new TemperatureConverter() {
                    @Override
                    public boolean isFreezing(double celsius) {
                        return celsius > 0.0;
                    }
                }),
            new Mutant<>("isFreezing: return false (return value)",
                new TemperatureConverter() {
                    @Override
                    public boolean isFreezing(double celsius) {
                        return false;
                    }
                }),
            new Mutant<>("isFreezing: 0.0 to -0.0 (equivalent: 0.0 == -0.0)",
                new TemperatureConverter() {
                    @Override
                    public boolean isFreezing(double celsius) {
                        return celsius <= -0.0;
                    }
                }),
            new Mutant<>("getTemperatureCategory: < 0 to <= 0 (conditional boundary)",
                new TemperatureConverter() {
                    @Override
                    public String getTemperatureCategory(double celsius) {
                        return category(celsius, 0, 10, 25, 35, true);
                    }
                }),
            new Mutant<>("getTemperatureCategory: < 10 to < 11 (constant)",
                new TemperatureConverter() {
                    @Override
                    public String getTemperatureCategory(double celsius) {
                        return category(celsius, 0, 11, 25, 35, false);
                    }
                }),
            new Mutant<>("getTemperatureCategory: < 35 to < 36 (constant)",
                new TemperatureConverter() {
                    @Override
                    public String getTemperatureCategory(double celsius) {
                        return category(celsius, 0, 10, 25, 36, false);
                    }
                })
        );
    }
    
    /**
     * 温度変換器のテストスイート
     */
    public static class TemperatureConverterTests {
        
        /**
         * 基本的なテスト（ミューテーションスコアが低い例）
         */
        public static void weakTests(TemperatureConverter converter) {
            // 基本的なテストケースのみ
            check(converter.celsiusToFahrenheit(0) == 32.0, "0C = 32F");
            check(converter.fahrenheitToCelsius(32) == 0.0, "32F = 0C");
            check(converter.isFreezing(-1), "-1C is freezing");
        }
        
        /**
         * 改善されたテスト（ミューテーションスコアが高い例）
         */
        public static void strongTests(TemperatureConverter converter) {
            check(converter.celsiusToFahrenheit(0) == 32.0, "0C = 32F");
            check(converter.celsiusToFahrenheit(100) == 212.0, "100C = 212F");
            check(converter.celsiusToFahrenheit(-40) == -40.0, "-40C = -40F");
            
            check(converter.fahrenheitToCelsius(32) == 0.0, "32F = 0C");
            check(converter.fahrenheitToCelsius(212) == 100.0, "212F = 100C");
            check(converter.fahrenheitToCelsius(-40) == -40.0, "-40F = -40C");
            
            // 境界値での氷点下判定
            check(converter.isFreezing(0), "0C is freezing (boundary)");
            check(converter.isFreezing(-0.1), "-0.1C is freezing");
            check(!converter.isFreezing(0.1), "0.1C is not freezing");
            
            // カテゴリ判定の境界値テスト
            check("Freezing".equals(converter.getTemperatureCategory(-1)), "-1C");
            check("Cold".equals(converter.getTemperatureCategory(0)), "0C");
            check("Cold".equals(converter.getTemperatureCategory(9)), "9C");
            check("Mild".equals(converter.getTemperatureCategory(10)), "10C");
            check("Mild".equals(converter.getTemperatureCategory(24)), "24C");
            check("Warm".equals(converter.getTemperatureCategory(25)), "25C");
            check("Warm".equals(converter.getTemperatureCategory(34)), "34C");
            check("Hot".equals(converter.getTemperatureCategory(35)), "35C");
        }
    }
    
    /**
     * 計算機のミュータント
     */
    static List<Mutant<Calculator>> calculatorMutants() {
        return List.of(
            new Mutant<>("add: + to - (arithmetic operator)", new Calculator() {
                @Override
                public int add(int a, int b) {
                    return a - b;
                }
            }),
            new Mutant<>("subtract: - to + (arithmetic operator)", new Calculator() {
                @Override
                public int subtract(int a, int b) {
                    return a + b;
                }
            }),
            new Mutant<>("multiply: * to + (arithmetic operator)", new Calculator() {
                @Override
                public int multiply(int a, int b) {
                    return a + b;
                }
            }),
            new Mutant<>("divide: b == 0 to b == 1 (constant)", new Calculator() {
                @Override
                public double divide(int a, int b) {
                    if (b == 1) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return (double) a / b;
                }
            }),
            new Mutant<>("divide: / to * (arithmetic operator)", new Calculator() {
                @Override
                public double divide(int a, int b) {
                    if (b == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return (double) a * b;
                }
            }),
            new Mutant<>("max: > to < (negated conditional)", new Calculator() {
                @Override
                public int max(int a, int b) {
                    return (a < b) ? a : b;
                }
            }),
            new Mutant<>("max: > to >= (equivalent: same result when a == b)", new Calculator() {
                @Override
                public int max(int a, int b) {
                    return (a >= b) ? a : b;
                }
            }),
            new Mutant<>("isEven: == 0 to != 0 (negated conditional)", new Calculator() {
                @Override
                public boolean isEven(int number) {
                    return number % 2 != 0;
                }
            }),
            new Mutant<>("factorial: i <= n to i < n (conditional boundary)", new Calculator() {
                @Override
                public long factorial(int n) {
                    if (n < 0) {
                        throw new IllegalArgumentException("Negative number");
                    }
                    long result = 1;
                    for (int i = 2; i < n; i++) {
                        result *= i;
                    }
                    return result;
                }
            }),
            new Mutant<>("factorial: n < 0 to n <= 0 (conditional boundary)", new Calculator() {
                @Override
                public long factorial(int n) {
                    if (n <= 0) {
                        throw new IllegalArgumentException("Negative number");
                    }
                    long result = 1;
                    for (int i = 2; i <= n; i++) {
                        result *= i;
                    }
                    return result;
                }
            })
        );
    }
    
    /**
     * 計算機のテストスイート
     */
    public static class CalculatorTests {
        
        public static void tests(Calculator calc) {
            // 基本演算テスト
            check(calc.add(2, 3) == 5, "add");
            check(calc.subtract(5, 3) == 2, "subtract");
            check(calc.multiply(4, 3) == 12, "multiply");
            check(calc.divide(10, 2) == 5.0, "divide");
            check(calc.divide(7, 1) == 7.0, "divide by 1");
            
            // ゼロ除算テスト
            boolean thrown = false;
            try {
                calc.divide(10, 0);
            } catch (ArithmeticException e) {
                thrown = true;
            }
            check(thrown, "divide by zero must throw");
            
            // max関数テスト
            check(calc.max(5, 3) == 5, "max(5, 3)");
            check(calc.max(3, 5) == 5, "max(3, 5)");
            check(calc.max(5, 5) == 5, "max(5, 5)");
            
            // 偶数判定テスト
            check(calc.isEven(2), "2 is even");
            check(calc.isEven(0), "0 is even");
            check(!calc.isEven(1), "1 is odd");
            check(!calc.isEven(-1), "-1 is odd");
            check(calc.isEven(-2), "-2 is even");
            
            // 階乗テスト
            check(calc.factorial(0) == 1, "0!");
            check(calc.factorial(1) == 1, "1!");
            check(calc.factorial(5) == 120, "5!");
            
            thrown = false;
            try {
                calc.factorial(-1);
            } catch (IllegalArgumentException e) {
                thrown = true;
            }
            check(thrown, "factorial(-1) must throw");
        }
    }
    
    /**
     * バリデータのミュータント
     */
    static List<Mutant<UserValidator>> validatorMutants() {
        return List.of(
            new Mutant<>("isValidEmail: && to || (logical operator)", new UserValidator() {
                @Override
                public boolean isValidEmail(String email) {
                    if (email == null) {
                        return false;
                    }
                    return email.contains("@") || email.contains(".");
                }
            }),
            new Mutant<>("isValidEmail: null check removed (remove call)", new UserValidator() {
                @Override
                public boolean isValidEmail(String email) {
                    return email.contains("@") && email.contains(".");
                }
            }),
            new Mutant<>("isValidPassword: >= 8 to > 8 (conditional boundary)", new UserValidator() {
                @Override
                public boolean isValidPassword(String password) {
                    if (password == null) {
                        return false;
                    }
                    if (password.length() > 8) {
                        return containsDigit(password) && containsLetter(password);
                    }
                    return false;
                }
            }),
            new Mutant<>("isValidPassword: && to || (logical operator)", new UserValidator() {
                @Override
                public boolean isValidPassword(String password) {
                    if (password == null) {
                        return false;
                    }
                    if (password.length() >= 8) {
                        return containsDigit(password) || containsLetter(password);
                    }
                    return false;
                }
            }),
            new Mutant<>("isValidAge: >= 0 to > 0 (conditional boundary)", new UserValidator() {
                @Override
                public boolean isValidAge(int age) {
                    return age > 0 && age <= 150;
                }
            }),
            new Mutant<>("isValidAge: <= 150 to < 150 (conditional boundary)", new UserValidator() {
                @Override
                public boolean isValidAge(int age) {
                    return age >= 0 && age < 150;
                }
            }),
            new Mutant<>("isValidAge: && to || (logical operator)", new UserValidator() {
                @Override
                public boolean isValidAge(int age) {
                    return age >= 0 || age <= 150;
                }
            })
        );
    }
    
    /**
     * バリデータのテストスイート
     */
    public static class UserValidatorTests {
        
        public static void tests(UserValidator validator) {
            // メールバリデーションテスト
            check(validator.isValidEmail("user@example.com"), "valid email");
            check(validator.isValidEmail("test.email@domain.co.jp"), "valid email with dot");
            check(!validator.isValidEmail(null), "null email");
            check(!validator.isValidEmail(""), "empty email");
            check(!validator.isValidEmail("invalid-email"), "no @");
            check(!validator.isValidEmail("user@localhost"), "no dot");
            
            // パスワードバリデーションテスト
            check(validator.isValidPassword("password123"), "valid password");
            check(validator.isValidPassword("abcdefg1"), "exactly 8 characters (boundary)");
            check(!validator.isValidPassword("abcdef1"), "7 characters (boundary)");
            check(!validator.isValidPassword(null), "null password");
            check(!validator.isValidPassword("12345678"), "digits only");
            check(!validator.isValidPassword("abcdefgh"), "letters only");
            
            // 年齢バリデーションテスト
            check(validator.isValidAge(0), "age 0 (boundary)");
            check(validator.isValidAge(25), "age 25");
            check(validator.isValidAge(150), "age 150 (boundary)");
            check(!validator.isValidAge(-1), "age -1");
            check(!validator.isValidAge(151), "age 151");
        }
    }
    
    /**
     * ミューテーションテストの実行とレポート
     */
    public static void runMutationTestDemo() {
        IO.println("=== Mutation Testing Demonstration ===");
        
        // 1. 弱いテストスイートの例
        IO.println("\n--- Weak Test Suite Example ---");
        SimpleMutationTester.MutationResult weakResult = SimpleMutationTester.run(
            "TemperatureConverter (Weak Tests)", new TemperatureConverter(),
            temperatureMutants(), TemperatureConverterTests::weakTests);
        printResult(weakResult);
        
        // 2. 強いテストスイートの例
        IO.println("\n--- Strong Test Suite Example ---");
        SimpleMutationTester.MutationResult strongResult = SimpleMutationTester.run(
            "TemperatureConverter (Strong Tests)", new TemperatureConverter(),
            temperatureMutants(), TemperatureConverterTests::strongTests);
        printResult(strongResult);
        
        // 3. その他のクラスのテスト
        IO.println("\n--- Calculator Tests ---");
        printResult(SimpleMutationTester.run(
            "Calculator", new Calculator(), calculatorMutants(), CalculatorTests::tests));
        
        IO.println("\n--- UserValidator Tests ---");
        printResult(SimpleMutationTester.run(
            "UserValidator", new UserValidator(), validatorMutants(), UserValidatorTests::tests));
        
        // 4. 結果の分析
        IO.println("\n--- Mutation Testing Analysis ---");
        IO.println("Test Suite Quality Comparison:");
        IO.println("  Weak Tests:   " + "%.1f%%".formatted(weakResult.getMutationScore() * 100) + " mutation score");
        IO.println("  Strong Tests: " + "%.1f%%".formatted(strongResult.getMutationScore() * 100) + " mutation score");
        IO.println("  Improvement:  " + "%.1f".formatted(
            (strongResult.getMutationScore() - weakResult.getMutationScore()) * 100) + " percentage points");
        IO.println("Equivalent mutants (same behavior as the original) can never be killed,");
        IO.println("so a score below 100% does not always mean a missing test.");
    }
    
    private static void printResult(SimpleMutationTester.MutationResult result) {
        IO.println("Result: " + result);
        if (!result.getSurvivedMutationDetails().isEmpty()) {
            IO.println("Survived mutations:");
            for (String detail : result.getSurvivedMutationDetails()) {
                IO.println("  - " + detail);
            }
        }
    }
    
    /**
     * ミューテーションテストのベストプラクティス解説
     */
    public static void explainMutationTestingBestPractices() {
        IO.println("\n=== Mutation Testing Best Practices ===");
        
        IO.println("\n1. ミューテーションテストの目的:");
        IO.println("   - テストスイートの品質評価");
        IO.println("   - テストカバレッジの盲点発見");
        IO.println("   - より効果的なテストケースの作成指針");
        
        IO.println("\n2. 高いミューテーションスコアを得るためのポイント:");
        IO.println("   - 境界値を徹底的にテスト");
        IO.println("   - 条件分岐のすべてのパスをカバー");
        IO.println("   - エラーケースも含めて網羅");
        IO.println("   - 戻り値の検証を厳密に行う");
        
        IO.println("\n3. よくあるミューテーション例:");
        for (SimpleMutationTester.MutationType type : SimpleMutationTester.MutationType.values()) {
            IO.println("   - " + type);
        }
        
        IO.println("\n4. ミューテーションスコアの目安:");
        IO.println("   - 80%以上: 非常に良いテストスイート");
        IO.println("   - 60-79%: 改善の余地あり");
        IO.println("   - 60%未満: テストスイートの大幅改善が必要");
        
        IO.println("\n5. 実践での活用:");
        IO.println("   - CI/CDパイプラインに組み込み");
        IO.println("   - コードレビューでの品質指標として使用");
        IO.println("   - リファクタリング時の安全性確認");
        IO.println("   - 新人教育でのテスト作成指導");
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        IO.println("Mutation Testing Demonstration");
        IO.println("==============================");
        
        runMutationTestDemo();
        explainMutationTestingBestPractices();
        
        IO.println("\n" + "=".repeat(50));
        IO.println("Mutation Testing Insights:");
        IO.println("✓ Tests the tests - validates test suite quality");
        IO.println("✓ Reveals hidden test gaps that coverage metrics miss");
        IO.println("✓ Drives creation of more robust test cases");
        IO.println("✓ Higher mutation scores = better bug detection");
        IO.println("✓ Essential for critical software where bugs are costly");
        
        IO.println("\nRecommended Tools:");
        IO.println("- PIT (Java): Most popular Java mutation testing tool");
        IO.println("- Stryker (JavaScript/TypeScript): Modern mutation testing");
        IO.println("- mutmut (Python): Simple Python mutation testing");
        IO.println("- Cosmic Ray (Python): Advanced mutation testing features");
    }
}