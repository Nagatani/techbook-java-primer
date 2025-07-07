package com.example.testing;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

/**
 * ミューテーションテストのデモンストレーション
 * テストの品質を評価し、見逃されるバグを発見するための手法
 */
public class MutationTestDemo {
    
    /**
     * テスト対象のビジネスロジッククラス群
     */
    
    /**
     * 温度変換ユーティリティ（ミューテーション対象）
     */
    public static class TemperatureConverter {
        
        /**
         * 摂氏から華氏への変換
         */
        public static double celsiusToFahrenheit(double celsius) {
            // ミューテーション候補: 9を8に、5を4に、32を31に変更
            return (celsius * 9.0 / 5.0) + 32.0;
        }
        
        /**
         * 華氏から摂氏への変換
         */
        public static double fahrenheitToCelsius(double fahrenheit) {
            // ミューテーション候補: 32を31に、5を4に、9を8に変更
            return (fahrenheit - 32.0) * 5.0 / 9.0;
        }
        
        /**
         * 温度が氷点下かどうかの判定
         */
        public static boolean isFreezing(double celsius) {
            // ミューテーション候補: <= を < に、>= に、!= に変更
            return celsius <= 0.0;
        }
        
        /**
         * 温度カテゴリの判定
         */
        public static String getTemperatureCategory(double celsius) {
            // ミューテーション候補: 境界値の変更、条件の否定
            if (celsius < 0) {
                return "Freezing";
            } else if (celsius < 10) {
                return "Cold";
            } else if (celsius < 25) {
                return "Mild";
            } else if (celsius < 35) {
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
            // ミューテーション候補: + を - に変更
            return a + b;
        }
        
        public int subtract(int a, int b) {
            // ミューテーション候補: - を + に変更
            return a - b;
        }
        
        public int multiply(int a, int b) {
            // ミューテーション候補: * を / に、+ に変更
            return a * b;
        }
        
        public double divide(int a, int b) {
            // ミューテーション候補: == を != に、0を1に変更
            if (b == 0) {
                throw new ArithmeticException("Division by zero");
            }
            // ミューテーション候補: / を * に変更
            return (double) a / b;
        }
        
        public int max(int a, int b) {
            // ミューテーション候補: > を < に、>= に、== に変更
            return (a > b) ? a : b;
        }
        
        public boolean isEven(int number) {
            // ミューテーション候補: % を / に、== を != に、0を1に変更
            return number % 2 == 0;
        }
        
        public long factorial(int n) {
            // ミューテーション候補: < を <= に、<= を < に変更
            if (n < 0) {
                throw new IllegalArgumentException("Negative number");
            }
            // ミューテーション候補: <= を < に変更
            if (n <= 1) {
                return 1;
            }
            
            long result = 1;
            // ミューテーション候補: <= を < に、i++ を i-- に変更
            for (int i = 2; i <= n; i++) {
                // ミューテーション候補: * を + に変更
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
            // ミューテーション候補: == を != に変更
            if (email == null) {
                return false;
            }
            
            // ミューテーション候補: && を || に変更
            return email.contains("@") && email.contains(".");
        }
        
        public boolean isValidPassword(String password) {
            // ミューテーション候補: == を != に変更
            if (password == null) {
                return false;
            }
            
            // ミューテーション候補: >= を > に、8を7に変更
            if (password.length() >= 8) {
                // ミューテーション候補: && を || に変更
                return containsDigit(password) && containsLetter(password);
            }
            
            return false;
        }
        
        private boolean containsDigit(String str) {
            // ミューテーション候補: anyMatch を noneMatch に変更
            return str.chars().anyMatch(Character::isDigit);
        }
        
        private boolean containsLetter(String str) {
            // ミューテーション候補: anyMatch を noneMatch に変更
            return str.chars().anyMatch(Character::isLetter);
        }
        
        public boolean isValidAge(int age) {
            // ミューテーション候補: >= を > に、&& を || に、<= を < に変更
            return age >= 0 && age <= 150;
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
            private final int survivedMutations;
            private final List<String> survivedMutationDetails;
            
            public MutationResult(String testClass, int totalMutations, int killedMutations) {
                this.testClass = testClass;
                this.totalMutations = totalMutations;
                this.killedMutations = killedMutations;
                this.survivedMutations = totalMutations - killedMutations;
                this.survivedMutationDetails = new ArrayList<>();
            }
            
            public double getMutationScore() {
                return totalMutations > 0 ? (double) killedMutations / totalMutations : 0.0;
            }
            
            public void addSurvivedMutation(String detail) {
                survivedMutationDetails.add(detail);
            }
            
            @Override
            public String toString() {
                return String.format(
                    "MutationResult{class='%s', total=%d, killed=%d, survived=%d, score=%.1f%%}",
                    testClass, totalMutations, killedMutations, survivedMutations, 
                    getMutationScore() * 100
                );
            }
        }
        
        /**
         * 模擬的なミューテーションテスト実行
         */
        public static MutationResult simulateMutationTest(String className, Function<String, Boolean> testRunner) {
            System.out.println("Running mutation test for: " + className);
            
            // 実際のミューテーションテストツールでは、バイトコードレベルで変更を加える
            // ここでは教育目的で結果をシミュレート
            
            List<String> potentialMutations = Arrays.asList(
                "Changed conditional boundary (< to <=)",
                "Negated conditional (== to !=)",
                "Changed arithmetic operator (+ to -)",
                "Modified constant value (0 to 1)",
                "Replaced return value (true to false)",
                "Removed method call",
                "Changed loop boundary (< to <=)",
                "Modified array access",
                "Changed logical operator (&& to ||)",
                "Altered comparison operator (> to >=)"
            );
            
            int totalMutations = potentialMutations.size();
            int killedMutations = 0;
            MutationResult result = new MutationResult(className, totalMutations, 0);
            
            Random random = ThreadLocalRandom.current();
            
            for (String mutation : potentialMutations) {
                System.out.println("  Testing mutation: " + mutation);
                
                // テストがミューテーションを検出できるかシミュレート
                // 実際のツールではミューテーションを適用したコードでテストを実行
                boolean testPassed = testRunner.apply(mutation);
                
                if (!testPassed) {
                    // テストが失敗 = ミューテーションが検出された（killed）
                    killedMutations++;
                    System.out.println("    ✓ Mutation killed by tests");
                } else {
                    // テストが成功 = ミューテーションが検出されなかった（survived）
                    System.out.println("    ✗ Mutation survived - test gap detected!");
                    result.addSurvivedMutation(mutation);
                }
            }
            
            return new MutationResult(className, totalMutations, killedMutations);
        }
    }
    
    /**
     * 温度変換器のテストスイート
     */
    public static class TemperatureConverterTests {
        
        /**
         * 基本的なテスト（ミューテーションスコアが低い例）
         */
        public static class WeakTests {
            public static boolean runTests(String mutation) {
                try {
                    // 基本的なテストケースのみ
                    assert TemperatureConverter.celsiusToFahrenheit(0) == 32.0;
                    assert TemperatureConverter.fahrenheitToCelsius(32) == 0.0;
                    assert TemperatureConverter.isFreezing(-1);
                    
                    return true; // 多くのミューテーションを見逃す
                } catch (AssertionError e) {
                    return false;
                }
            }
        }
        
        /**
         * 改善されたテスト（ミューテーションスコアが高い例）
         */
        public static class StrongTests {
            public static boolean runTests(String mutation) {
                try {
                    // 境界値テスト
                    assert TemperatureConverter.celsiusToFahrenheit(0) == 32.0;
                    assert TemperatureConverter.celsiusToFahrenheit(100) == 212.0;
                    assert TemperatureConverter.celsiusToFahrenheit(-40) == -40.0;
                    
                    assert TemperatureConverter.fahrenheitToCelsius(32) == 0.0;
                    assert TemperatureConverter.fahrenheitToCelsius(212) == 100.0;
                    assert TemperatureConverter.fahrenheitToCelsius(-40) == -40.0;
                    
                    // 境界値での氷点下判定
                    assert TemperatureConverter.isFreezing(0); // 境界値
                    assert TemperatureConverter.isFreezing(-0.1);
                    assert !TemperatureConverter.isFreezing(0.1);
                    
                    // カテゴリ判定の境界値テスト
                    assert "Freezing".equals(TemperatureConverter.getTemperatureCategory(-1));
                    assert "Cold".equals(TemperatureConverter.getTemperatureCategory(0));
                    assert "Cold".equals(TemperatureConverter.getTemperatureCategory(9));
                    assert "Mild".equals(TemperatureConverter.getTemperatureCategory(10));
                    assert "Mild".equals(TemperatureConverter.getTemperatureCategory(24));
                    assert "Warm".equals(TemperatureConverter.getTemperatureCategory(25));
                    assert "Warm".equals(TemperatureConverter.getTemperatureCategory(34));
                    assert "Hot".equals(TemperatureConverter.getTemperatureCategory(35));
                    
                    // ミューテーションによっては特定のケースで失敗する
                    if (mutation.contains("conditional boundary") && 
                        mutation.contains("<=")) {
                        // 0度境界での判定をより厳密にテスト
                        assert TemperatureConverter.isFreezing(0.0);
                    }
                    
                    return true;
                } catch (AssertionError e) {
                    return false; // ミューテーションを検出
                }
            }
        }
    }
    
    /**
     * 計算機のテストスイート
     */
    public static class CalculatorTests {
        
        public static boolean runTests(String mutation) {
            try {
                Calculator calc = new Calculator();
                
                // 基本演算テスト
                assert calc.add(2, 3) == 5;
                assert calc.subtract(5, 3) == 2;
                assert calc.multiply(4, 3) == 12;
                assert calc.divide(10, 2) == 5.0;
                
                // ゼロ除算テスト
                try {
                    calc.divide(10, 0);
                    assert false; // 例外が発生するべき
                } catch (ArithmeticException e) {
                    // 正常
                }
                
                // max関数テスト
                assert calc.max(5, 3) == 5;
                assert calc.max(3, 5) == 5;
                assert calc.max(5, 5) == 5;
                
                // 偶数判定テスト
                assert calc.isEven(2);
                assert calc.isEven(0);
                assert !calc.isEven(1);
                assert !calc.isEven(-1);
                assert calc.isEven(-2);
                
                // 階乗テスト
                assert calc.factorial(0) == 1;
                assert calc.factorial(1) == 1;
                assert calc.factorial(5) == 120;
                
                try {
                    calc.factorial(-1);
                    assert false; // 例外が発生するべき
                } catch (IllegalArgumentException e) {
                    // 正常
                }
                
                // ミューテーション特化テスト
                if (mutation.contains("arithmetic operator")) {
                    // 算術演算子の変更を検出するための追加テスト
                    assert calc.add(0, 0) == 0;
                    assert calc.multiply(1, 100) == 100;
                }
                
                return true;
            } catch (AssertionError e) {
                return false;
            }
        }
    }
    
    /**
     * バリデータのテストスイート
     */
    public static class UserValidatorTests {
        
        public static boolean runTests(String mutation) {
            try {
                UserValidator validator = new UserValidator();
                
                // メールバリデーションテスト
                assert validator.isValidEmail("user@example.com");
                assert validator.isValidEmail("test.email@domain.co.jp");
                assert !validator.isValidEmail(null);
                assert !validator.isValidEmail("");
                assert !validator.isValidEmail("invalid-email");
                assert !validator.isValidEmail("@domain.com");
                assert !validator.isValidEmail("user@");
                
                // パスワードバリデーションテスト
                assert validator.isValidPassword("password123");
                assert validator.isValidPassword("mypass1");
                assert !validator.isValidPassword(null);
                assert !validator.isValidPassword("short");
                assert !validator.isValidPassword("nouppercase");
                assert !validator.isValidPassword("12345678"); // 数字のみ
                assert !validator.isValidPassword("abcdefgh"); // 文字のみ
                
                // 年齢バリデーションテスト
                assert validator.isValidAge(0);
                assert validator.isValidAge(25);
                assert validator.isValidAge(150);
                assert !validator.isValidAge(-1);
                assert !validator.isValidAge(151);
                
                // ミューテーション特化テスト
                if (mutation.contains("conditional boundary")) {
                    // 境界値での詳細テスト
                    assert validator.isValidPassword("abcdefg1"); // 8文字ちょうど
                    assert validator.isValidAge(0); // 下限境界
                    assert validator.isValidAge(150); // 上限境界
                }
                
                return true;
            } catch (AssertionError e) {
                return false;
            }
        }
    }
    
    /**
     * ミューテーションテストの実行とレポート
     */
    public static void runMutationTestDemo() {
        System.out.println("=== Mutation Testing Demonstration ===");
        
        // 1. 弱いテストスイートの例
        System.out.println("\n--- Weak Test Suite Example ---");
        SimpleMutationTester.MutationResult weakResult = 
            SimpleMutationTester.simulateMutationTest(
                "TemperatureConverter (Weak Tests)", 
                TemperatureConverterTests.WeakTests::runTests
            );
        
        System.out.println("Result: " + weakResult);
        System.out.println("Mutation Score: " + String.format("%.1f%%", weakResult.getMutationScore() * 100));
        
        if (!weakResult.survivedMutationDetails.isEmpty()) {
            System.out.println("Survived mutations indicate test gaps:");
            for (String detail : weakResult.survivedMutationDetails) {
                System.out.println("  - " + detail);
            }
        }
        
        // 2. 強いテストスイートの例
        System.out.println("\n--- Strong Test Suite Example ---");
        SimpleMutationTester.MutationResult strongResult = 
            SimpleMutationTester.simulateMutationTest(
                "TemperatureConverter (Strong Tests)", 
                TemperatureConverterTests.StrongTests::runTests
            );
        
        System.out.println("Result: " + strongResult);
        System.out.println("Mutation Score: " + String.format("%.1f%%", strongResult.getMutationScore() * 100));
        
        // 3. その他のクラスのテスト
        System.out.println("\n--- Calculator Tests ---");
        SimpleMutationTester.MutationResult calcResult = 
            SimpleMutationTester.simulateMutationTest(
                "Calculator", 
                CalculatorTests::runTests
            );
        System.out.println("Result: " + calcResult);
        
        System.out.println("\n--- UserValidator Tests ---");
        SimpleMutationTester.MutationResult validatorResult = 
            SimpleMutationTester.simulateMutationTest(
                "UserValidator", 
                UserValidatorTests::runTests
            );
        System.out.println("Result: " + validatorResult);
        
        // 4. 結果の分析
        System.out.println("\n--- Mutation Testing Analysis ---");
        System.out.println("Test Suite Quality Comparison:");
        System.out.println("  Weak Tests:   " + String.format("%.1f%%", weakResult.getMutationScore() * 100) + " mutation score");
        System.out.println("  Strong Tests: " + String.format("%.1f%%", strongResult.getMutationScore() * 100) + " mutation score");
        System.out.println("  Improvement:  " + String.format("%.1f", 
            (strongResult.getMutationScore() - weakResult.getMutationScore()) * 100) + " percentage points");
    }
    
    /**
     * ミューテーションテストのベストプラクティス解説
     */
    public static void explainMutationTestingBestPractices() {
        System.out.println("\n=== Mutation Testing Best Practices ===");
        
        System.out.println("\n1. ミューテーションテストの目的:");
        System.out.println("   - テストスイートの品質評価");
        System.out.println("   - テストカバレッジの盲点発見");
        System.out.println("   - より効果的なテストケースの作成指針");
        
        System.out.println("\n2. 高いミューテーションスコアを得るためのポイント:");
        System.out.println("   - 境界値を徹底的にテスト");
        System.out.println("   - 条件分岐のすべてのパスをカバー");
        System.out.println("   - エラーケースも含めて網羅");
        System.out.println("   - 戻り値の検証を厳密に行う");
        
        System.out.println("\n3. よくあるミューテーション例:");
        for (SimpleMutationTester.MutationType type : SimpleMutationTester.MutationType.values()) {
            System.out.println("   - " + type);
        }
        
        System.out.println("\n4. ミューテーションスコアの目安:");
        System.out.println("   - 80%以上: 非常に良いテストスイート");
        System.out.println("   - 60-79%: 改善の余地あり");
        System.out.println("   - 60%未満: テストスイートの大幅改善が必要");
        
        System.out.println("\n5. 実践での活用:");
        System.out.println("   - CI/CDパイプラインに組み込み");
        System.out.println("   - コードレビューでの品質指標として使用");
        System.out.println("   - リファクタリング時の安全性確認");
        System.out.println("   - 新人教育でのテスト作成指導");
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        System.out.println("Mutation Testing Demonstration");
        System.out.println("==============================");
        
        runMutationTestDemo();
        explainMutationTestingBestPractices();
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🎯 Mutation Testing Insights:");
        System.out.println("✓ Tests the tests - validates test suite quality");
        System.out.println("✓ Reveals hidden test gaps that coverage metrics miss");
        System.out.println("✓ Drives creation of more robust test cases");
        System.out.println("✓ Higher mutation scores = better bug detection");
        System.out.println("✓ Essential for critical software where bugs are costly");
        
        System.out.println("\n🛠️  Recommended Tools:");
        System.out.println("- PIT (Java): Most popular Java mutation testing tool");
        System.out.println("- Stryker (JavaScript/TypeScript): Modern mutation testing");
        System.out.println("- mutmut (Python): Simple Python mutation testing");
        System.out.println("- Cosmic Ray (Python): Advanced mutation testing features");
    }
}