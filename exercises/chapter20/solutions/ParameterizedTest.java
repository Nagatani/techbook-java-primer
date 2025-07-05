/**
 * 第20章 課題3: パラメータ化テスト - 解答例
 * 
 * JUnit 5のパラメータ化テスト機能を使用した効率的なテスト実装
 * 
 * 学習ポイント:
 * - @ParameterizedTest の基本的な使用法
 * - 様々なデータソースの活用
 * - テストケースの効率化
 * - データ駆動テストの実践
 * - 境界値分析とテストパターン
 */

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.ArgumentConverter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * パラメータ化テストのデモンストレーション用クラス群
 */
public class ParameterizedTest {
    
    /**
     * 文字列操作ユーティリティクラス
     */
    public static class StringUtils {
        
        /**
         * 文字列が回文かどうかを判定する
         * 
         * @param str 判定する文字列
         * @return 回文の場合true
         */
        public static boolean isPalindrome(String str) {
            if (str == null) return false;
            
            String cleaned = str.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
            return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
        }
        
        /**
         * 文字列の強度を評価する
         * 
         * @param password パスワード文字列
         * @return 強度レベル (0-4)
         */
        public static int getPasswordStrength(String password) {
            if (password == null || password.isEmpty()) return 0;
            
            int strength = 0;
            
            // 長さチェック
            if (password.length() >= 8) strength++;
            if (password.length() >= 12) strength++;
            
            // 文字種チェック
            if (password.matches(".*[a-z].*")) strength++;  // 小文字
            if (password.matches(".*[A-Z].*")) strength++;  // 大文字
            if (password.matches(".*[0-9].*")) strength++;  // 数字
            if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++; // 記号
            
            return Math.min(strength, 4);
        }
        
        /**
         * 文字列をケバブケースに変換する
         * 
         * @param input 入力文字列
         * @return ケバブケース形式の文字列
         */
        public static String toKebabCase(String input) {
            if (input == null || input.isEmpty()) return input;
            
            return input
                .replaceAll("([a-z])([A-Z])", "$1-$2")  // camelCase対応
                .replaceAll("\\s+", "-")                 // スペースをハイフンに
                .replaceAll("_", "-")                    // アンダースコアをハイフンに
                .toLowerCase();
        }
    }
    
    /**
     * 数学計算ユーティリティクラス
     */
    public static class MathUtils {
        
        /**
         * 最大公約数を計算する
         * 
         * @param a 数値1
         * @param b 数値2
         * @return 最大公約数
         */
        public static int gcd(int a, int b) {
            if (b == 0) return Math.abs(a);
            return gcd(b, a % b);
        }
        
        /**
         * 素数判定を行う
         * 
         * @param n 判定する数値
         * @return 素数の場合true
         */
        public static boolean isPrime(int n) {
            if (n <= 1) return false;
            if (n <= 3) return true;
            if (n % 2 == 0 || n % 3 == 0) return false;
            
            for (int i = 5; i * i <= n; i += 6) {
                if (n % i == 0 || n % (i + 2) == 0) return false;
            }
            return true;
        }
        
        /**
         * フィボナッチ数列のn番目の値を計算する
         * 
         * @param n インデックス (0以上)
         * @return フィボナッチ数
         */
        public static long fibonacci(int n) {
            if (n < 0) throw new IllegalArgumentException("nは0以上である必要があります");
            if (n <= 1) return n;
            
            long a = 0, b = 1;
            for (int i = 2; i <= n; i++) {
                long temp = a + b;
                a = b;
                b = temp;
            }
            return b;
        }
        
        /**
         * 階乗を計算する
         * 
         * @param n 計算する数値 (0以上)
         * @return n!
         */
        public static long factorial(int n) {
            if (n < 0) throw new IllegalArgumentException("nは0以上である必要があります");
            if (n <= 1) return 1;
            
            long result = 1;
            for (int i = 2; i <= n; i++) {
                result *= i;
            }
            return result;
        }
    }
    
    /**
     * 日付操作ユーティリティクラス
     */
    public static class DateUtils {
        
        /**
         * 年が閏年かどうかを判定する
         * 
         * @param year 年
         * @return 閏年の場合true
         */
        public static boolean isLeapYear(int year) {
            return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        }
        
        /**
         * 指定した月の日数を取得する
         * 
         * @param year 年
         * @param month 月 (1-12)
         * @return その月の日数
         */
        public static int getDaysInMonth(int year, int month) {
            if (month < 1 || month > 12) {
                throw new IllegalArgumentException("月は1-12の範囲で指定してください");
            }
            
            int[] daysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            
            if (month == 2 && isLeapYear(year)) {
                return 29;
            }
            
            return daysInMonth[month - 1];
        }
        
        /**
         * 日付文字列の妥当性を検証する
         * 
         * @param dateStr 日付文字列 (YYYY-MM-DD形式)
         * @return 妥当な日付の場合true
         */
        public static boolean isValidDate(String dateStr) {
            try {
                LocalDate.parse(dateStr);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }
    }
    
    /**
     * カスタム引数コンバーター
     */
    public static class StringToArrayConverter implements ArgumentConverter {
        @Override
        public Object convert(Object source, org.junit.jupiter.params.converter.ConversionContext context) {
            if (source instanceof String) {
                return ((String) source).split(",");
            }
            throw new IllegalArgumentException("文字列のみ変換可能です");
        }
    }
}

/**
 * パラメータ化テストクラス
 */
@DisplayName("パラメータ化テストの実践")
class ParameterizedTestExample {
    
    // === @ValueSource を使用した基本的なパラメータ化テスト ===
    
    @ParameterizedTest
    @ValueSource(strings = {"racecar", "A man a plan a canal Panama", "race a car", "hello"})
    @DisplayName("回文判定テスト - 文字列ソース")
    void testIsPalindrome_WithValueSource(String input) {
        // 期待値を動的に計算
        boolean expected = input.toLowerCase().replaceAll("[^a-zA-Z0-9]", "")
                .equals(new StringBuilder(input.toLowerCase().replaceAll("[^a-zA-Z0-9]", "")).reverse().toString());
        
        assertEquals(expected, ParameterizedTest.StringUtils.isPalindrome(input),
                "回文判定が正しくありません: " + input);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29})
    @DisplayName("素数判定テスト - 既知の素数")
    void testIsPrime_KnownPrimes(int number) {
        assertTrue(ParameterizedTest.MathUtils.isPrime(number),
                number + " は素数であるべきです");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {4, 6, 8, 9, 10, 12, 14, 15, 16, 18})
    @DisplayName("素数判定テスト - 合成数")
    void testIsPrime_CompositeNumbers(int number) {
        assertFalse(ParameterizedTest.MathUtils.isPrime(number),
                number + " は合成数であるべきです");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {2000, 2004, 2008, 2012, 2016, 2020, 2024})
    @DisplayName("閏年判定テスト - 閏年")
    void testIsLeapYear_LeapYears(int year) {
        assertTrue(ParameterizedTest.DateUtils.isLeapYear(year),
                year + " は閏年であるべきです");
    }
    
    // === @CsvSource を使用した複数引数テスト ===
    
    @ParameterizedTest
    @CsvSource({
        "48, 18, 6",
        "54, 24, 6", 
        "100, 25, 25",
        "17, 13, 1",
        "0, 5, 5",
        "7, 0, 7"
    })
    @DisplayName("最大公約数計算テスト")
    void testGcd(int a, int b, int expected) {
        assertEquals(expected, ParameterizedTest.MathUtils.gcd(a, b),
                String.format("gcd(%d, %d) = %d であるべきです", a, b, expected));
    }
    
    @ParameterizedTest
    @CsvSource({
        "password, 0",
        "Password, 1", 
        "Password1, 2",
        "Password1!, 3",
        "MySecurePass123!, 4",
        "short, 0",
        "verylongpassword, 1"
    })
    @DisplayName("パスワード強度テスト")
    void testPasswordStrength(String password, int expectedStrength) {
        assertEquals(expectedStrength, ParameterizedTest.StringUtils.getPasswordStrength(password),
                String.format("パスワード '%s' の強度は %d であるべきです", password, expectedStrength));
    }
    
    @ParameterizedTest
    @CsvSource({
        "2020, 2, 29",
        "2021, 2, 28",
        "2000, 2, 29",
        "1900, 2, 28",
        "2023, 1, 31",
        "2023, 4, 30",
        "2023, 12, 31"
    })
    @DisplayName("月の日数計算テスト")
    void testGetDaysInMonth(int year, int month, int expectedDays) {
        assertEquals(expectedDays, ParameterizedTest.DateUtils.getDaysInMonth(year, month),
                String.format("%d年%d月の日数は%d日であるべきです", year, month, expectedDays));
    }
    
    // === @CsvFileSource を使用したファイルベーステスト ===
    
    @ParameterizedTest
    @CsvFileSource(resources = "/test-data.csv", numLinesToSkip = 1)
    @DisplayName("CSVファイルからのテストデータ読み込み")
    void testWithCsvFile(String input, String expected) {
        // 注意: このテストを実行するには resources/test-data.csv が必要
        // 実際の実装では適切なテストデータファイルを用意する
        assertEquals(expected, ParameterizedTest.StringUtils.toKebabCase(input));
    }
    
    // === @MethodSource を使用した複雑なテストデータ ===
    
    @ParameterizedTest
    @MethodSource("fibonacciTestData")
    @DisplayName("フィボナッチ数列テスト")
    void testFibonacci(int n, long expected) {
        assertEquals(expected, ParameterizedTest.MathUtils.fibonacci(n),
                String.format("fibonacci(%d) = %d であるべきです", n, expected));
    }
    
    static Stream<org.junit.jupiter.params.provider.Arguments> fibonacciTestData() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(0, 0L),
            org.junit.jupiter.params.provider.Arguments.of(1, 1L),
            org.junit.jupiter.params.provider.Arguments.of(2, 1L),
            org.junit.jupiter.params.provider.Arguments.of(3, 2L),
            org.junit.jupiter.params.provider.Arguments.of(4, 3L),
            org.junit.jupiter.params.provider.Arguments.of(5, 5L),
            org.junit.jupiter.params.provider.Arguments.of(10, 55L),
            org.junit.jupiter.params.provider.Arguments.of(20, 6765L)
        );
    }
    
    @ParameterizedTest
    @MethodSource("factorialTestData")
    @DisplayName("階乗計算テスト")
    void testFactorial(int n, long expected) {
        assertEquals(expected, ParameterizedTest.MathUtils.factorial(n),
                String.format("%d! = %d であるべきです", n, expected));
    }
    
    static Stream<org.junit.jupiter.params.provider.Arguments> factorialTestData() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of(0, 1L),
            org.junit.jupiter.params.provider.Arguments.of(1, 1L),
            org.junit.jupiter.params.provider.Arguments.of(2, 2L),
            org.junit.jupiter.params.provider.Arguments.of(3, 6L),
            org.junit.jupiter.params.provider.Arguments.of(4, 24L),
            org.junit.jupiter.params.provider.Arguments.of(5, 120L),
            org.junit.jupiter.params.provider.Arguments.of(10, 3628800L)
        );
    }
    
    @ParameterizedTest
    @MethodSource("stringConversionTestData")
    @DisplayName("文字列変換テスト")
    void testToKebabCase(String input, String expected) {
        assertEquals(expected, ParameterizedTest.StringUtils.toKebabCase(input),
                String.format("'%s' のケバブケース変換結果は '%s' であるべきです", input, expected));
    }
    
    static Stream<org.junit.jupiter.params.provider.Arguments> stringConversionTestData() {
        return Stream.of(
            org.junit.jupiter.params.provider.Arguments.of("HelloWorld", "hello-world"),
            org.junit.jupiter.params.provider.Arguments.of("hello world", "hello-world"),
            org.junit.jupiter.params.provider.Arguments.of("hello_world", "hello-world"),
            org.junit.jupiter.params.provider.Arguments.of("HELLO WORLD", "hello-world"),
            org.junit.jupiter.params.provider.Arguments.of("camelCaseString", "camel-case-string"),
            org.junit.jupiter.params.provider.Arguments.of("", ""),
            org.junit.jupiter.params.provider.Arguments.of("single", "single")
        );
    }
    
    // === @EnumSource を使用した列挙型テスト ===
    
    public enum TestMonth {
        JANUARY(1, 31),
        FEBRUARY(2, 28), // 平年の場合
        MARCH(3, 31),
        APRIL(4, 30),
        MAY(5, 31),
        JUNE(6, 30),
        JULY(7, 31),
        AUGUST(8, 31),
        SEPTEMBER(9, 30),
        OCTOBER(10, 31),
        NOVEMBER(11, 30),
        DECEMBER(12, 31);
        
        private final int number;
        private final int days;
        
        TestMonth(int number, int days) {
            this.number = number;
            this.days = days;
        }
        
        public int getNumber() { return number; }
        public int getDays() { return days; }
    }
    
    @ParameterizedTest
    @EnumSource(TestMonth.class)
    @DisplayName("月の日数テスト - 平年")
    void testMonthDays_NonLeapYear(TestMonth month) {
        int expectedDays = month.getDays();
        int actualDays = ParameterizedTest.DateUtils.getDaysInMonth(2023, month.getNumber());
        
        assertEquals(expectedDays, actualDays,
                String.format("%s の日数は %d 日であるべきです", month.name(), expectedDays));
    }
    
    @ParameterizedTest
    @EnumSource(value = TestMonth.class, names = {"FEBRUARY"})
    @DisplayName("2月の日数テスト - 閏年")
    void testFebruaryDays_LeapYear(TestMonth month) {
        assertEquals(29, ParameterizedTest.DateUtils.getDaysInMonth(2020, month.getNumber()),
                "閏年の2月は29日であるべきです");
    }
    
    // === @ArgumentsSource を使用したカスタムデータソース ===
    
    public static class InvalidDateProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends org.junit.jupiter.params.provider.Arguments> provideArguments(
                org.junit.jupiter.params.extension.ExtensionContext context) {
            return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of("2023-13-01"), // 無効な月
                org.junit.jupiter.params.provider.Arguments.of("2023-02-30"), // 無効な日
                org.junit.jupiter.params.provider.Arguments.of("2023-04-31"), // 4月に31日
                org.junit.jupiter.params.provider.Arguments.of("invalid-date"), // 無効なフォーマット
                org.junit.jupiter.params.provider.Arguments.of("2023/02/15"), // 無効なセパレータ
                org.junit.jupiter.params.provider.Arguments.of("23-02-15")    // 無効な年フォーマット
            );
        }
    }
    
    @ParameterizedTest
    @ArgumentsSource(InvalidDateProvider.class)
    @DisplayName("無効な日付文字列のテスト")
    void testInvalidDates(String invalidDate) {
        assertFalse(ParameterizedTest.DateUtils.isValidDate(invalidDate),
                String.format("'%s' は無効な日付として判定されるべきです", invalidDate));
    }
    
    // === カスタムコンバーターを使用したテスト ===
    
    @ParameterizedTest
    @ValueSource(strings = {"apple,banana,cherry", "red,green,blue", "one,two,three"})
    @DisplayName("カスタムコンバーターテスト")
    void testWithCustomConverter(@ConvertWith(ParameterizedTest.StringToArrayConverter.class) String[] array) {
        assertNotNull(array);
        assertTrue(array.length > 0);
        
        // 各要素が空でないことを確認
        for (String element : array) {
            assertNotNull(element);
            assertFalse(element.trim().isEmpty());
        }
    }
    
    // === 動的テスト名の設定 ===
    
    @ParameterizedTest(name = "{index} => isPrime({0}) = {1}")
    @CsvSource({
        "1, false",
        "2, true",
        "3, true", 
        "4, false",
        "17, true",
        "25, false"
    })
    @DisplayName("素数判定テスト - カスタム名前")
    void testIsPrimeWithCustomName(int number, boolean expected) {
        assertEquals(expected, ParameterizedTest.MathUtils.isPrime(number));
    }
    
    @ParameterizedTest(name = "年 {0} は閏年: {1}")
    @CsvSource({
        "2000, true",
        "1900, false",
        "2020, true",
        "2021, false",
        "2024, true"
    })
    @DisplayName("閏年判定テスト - 日本語名前")
    void testLeapYearWithJapaneseName(int year, boolean expected) {
        assertEquals(expected, ParameterizedTest.DateUtils.isLeapYear(year));
    }
    
    // === 境界値とエラーケースのテスト ===
    
    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -10})
    @DisplayName("フィボナッチ数列 - 負の値での例外テスト")
    void testFibonacci_NegativeValues(int n) {
        assertThrows(IllegalArgumentException.class, 
                () -> ParameterizedTest.MathUtils.fibonacci(n),
                "負の値では IllegalArgumentException が発生するべきです");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -10})
    @DisplayName("階乗計算 - 負の値での例外テスト")
    void testFactorial_NegativeValues(int n) {
        assertThrows(IllegalArgumentException.class,
                () -> ParameterizedTest.MathUtils.factorial(n),
                "負の値では IllegalArgumentException が発生するべきです");
    }
    
    @ParameterizedTest
    @ValueSource(ints = {0, 13, -1, 15})
    @DisplayName("月の日数計算 - 無効な月での例外テスト")
    void testGetDaysInMonth_InvalidMonth(int month) {
        if (month < 1 || month > 12) {
            assertThrows(IllegalArgumentException.class,
                    () -> ParameterizedTest.DateUtils.getDaysInMonth(2023, month),
                    "無効な月では IllegalArgumentException が発生するべきです");
        }
    }
    
    // === パフォーマンステスト ===
    
    @ParameterizedTest
    @ValueSource(ints = {1000, 5000, 10000, 50000})
    @DisplayName("素数判定 - パフォーマンステスト")
    void testIsPrime_Performance(int number) {
        long startTime = System.nanoTime();
        ParameterizedTest.MathUtils.isPrime(number);
        long endTime = System.nanoTime();
        
        long executionTime = endTime - startTime;
        assertTrue(executionTime < 1_000_000, // 1ms以内
                String.format("素数判定が遅すぎます: %d ナノ秒", executionTime));
    }
}

/*
 * パラメータ化テストの実装ポイント:
 * 
 * 1. データソースの種類と使い分け
 *    - @ValueSource: 単一値の配列
 *    - @CsvSource: 複数の値をCSV形式で指定
 *    - @CsvFileSource: 外部CSVファイルから読み込み
 *    - @MethodSource: 複雑なテストデータの動的生成
 *    - @EnumSource: 列挙型の全要素でテスト
 *    - @ArgumentsSource: カスタムデータプロバイダー
 * 
 * 2. テスト名のカスタマイズ
 *    - name属性による動的なテスト名設定
 *    - {index}, {0}, {1}などのプレースホルダー活用
 *    - 日本語でのわかりやすいテスト名
 * 
 * 3. 引数の変換と処理
 *    - @ConvertWith による型変換
 *    - ArgumentConverter の実装
 *    - 複雑なオブジェクトの生成
 * 
 * 4. 効率的なテスト設計
 *    - 境界値分析による網羅的テスト
 *    - 正常系と異常系の組み合わせ
 *    - パフォーマンス要件の検証
 * 
 * 5. 実践的な活用パターン
 *    - データ駆動テストの実装
 *    - 回帰テストの自動化
 *    - 複数環境での動作確認
 * 
 * 利点:
 * - テストコードの重複削減
 * - 多様なテストケースの効率的な実行
 * - テストデータの外部化
 * - 可読性の向上
 * 
 * 注意点:
 * - テストの独立性を保つ
 * - 適切なアサーションメッセージ
 * - テストデータの管理
 * - 実行時間の考慮
 */