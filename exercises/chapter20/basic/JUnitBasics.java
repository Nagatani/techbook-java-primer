/**
 * 第20章 基本課題1: JUnit基礎テスト
 * 
 * JUnit 5の基本的な機能を使用してテストクラスを作成してください。
 * 
 * 要件:
 * 1. SimpleCalculatorクラスのテストを作成
 * 2. 基本的なアサーションを使用（assertEquals, assertTrue, assertFalse等）
 * 3. @BeforeEach, @AfterEach, @Test アノテーションを使用
 * 4. 例外テスト（assertThrows）を含める
 * 5. テストメソッドに適切な名前と@DisplayNameを付ける
 * 
 * 学習ポイント:
 * - JUnit 5の基本的なアノテーション
 * - 各種アサーションメソッドの使用法
 * - テストメソッドの命名規則
 * - テストの前処理と後処理
 */

/**
 * テスト対象となる簡単な計算機クラス
 */
class SimpleCalculator {
    
    /**
     * 加算を行います
     * @param a 値1
     * @param b 値2
     * @return a + b
     */
    public int add(int a, int b) {
        return a + b;
    }
    
    /**
     * 減算を行います
     * @param a 値1
     * @param b 値2
     * @return a - b
     */
    public int subtract(int a, int b) {
        return a - b;
    }
    
    /**
     * 乗算を行います
     * @param a 値1
     * @param b 値2
     * @return a * b
     */
    public int multiply(int a, int b) {
        return a * b;
    }
    
    /**
     * 除算を行います
     * @param a 値1（被除数）
     * @param b 値2（除数）
     * @return a / b
     * @throws IllegalArgumentException bが0の場合
     */
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("除数は0にできません");
        }
        return (double) a / b;
    }
    
    /**
     * 数値が偶数かどうかを判定します
     * @param number 判定する数値
     * @return 偶数の場合true、奇数の場合false
     */
    public boolean isEven(int number) {
        return number % 2 == 0;
    }
    
    /**
     * 数値が正の値かどうかを判定します
     * @param number 判定する数値
     * @return 正の値の場合true、0または負の値の場合false
     */
    public boolean isPositive(int number) {
        return number > 0;
    }
    
    /**
     * 配列の最大値を取得します
     * @param numbers 数値の配列
     * @return 最大値
     * @throws IllegalArgumentException 配列がnullまたは空の場合
     */
    public int max(int[] numbers) {
        if (numbers == null || numbers.length == 0) {
            throw new IllegalArgumentException("配列は空にできません");
        }
        
        int max = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] > max) {
                max = numbers[i];
            }
        }
        return max;
    }
}

// TODO: 以下にSimpleCalculatorTestクラスを作成してください

/*
 * テストクラス実装のヒント:
 * 
 * 1. 基本的なテストクラス構造
 * 
 * import org.junit.jupiter.api.*;
 * import static org.junit.jupiter.api.Assertions.*;
 * 
 * @DisplayName("簡単な計算機のテスト")
 * class SimpleCalculatorTest {
 *     
 *     private SimpleCalculator calculator;
 *     
 *     @BeforeEach
 *     void setUp() {
 *         calculator = new SimpleCalculator();
 *     }
 *     
 *     @AfterEach
 *     void tearDown() {
 *         calculator = null;
 *     }
 * }
 * 
 * 2. 基本的なテストメソッドの例
 * 
 * @Test
 * @DisplayName("正の数の加算が正しく動作する")
 * void testAddPositiveNumbers() {
 *     // Given（準備）
 *     int a = 3;
 *     int b = 5;
 *     
 *     // When（実行）
 *     int result = calculator.add(a, b);
 *     
 *     // Then（検証）
 *     assertEquals(8, result);
 * }
 * 
 * 3. 例外テストの例
 * 
 * @Test
 * @DisplayName("0で除算した場合に例外が発生する")
 * void testDivideByZero() {
 *     IllegalArgumentException exception = assertThrows(
 *         IllegalArgumentException.class,
 *         () -> calculator.divide(10, 0)
 *     );
 *     assertEquals("除数は0にできません", exception.getMessage());
 * }
 * 
 * 4. 複数条件のテスト
 * 
 * @Test
 * @DisplayName("偶数判定が正しく動作する")
 * void testIsEven() {
 *     assertAll(
 *         () -> assertTrue(calculator.isEven(2)),
 *         () -> assertTrue(calculator.isEven(0)),
 *         () -> assertFalse(calculator.isEven(1)),
 *         () -> assertFalse(calculator.isEven(-1))
 *     );
 * }
 * 
 * 実装すべきテストメソッド:
 * - testAdd() - 加算のテスト
 * - testSubtract() - 減算のテスト
 * - testMultiply() - 乗算のテスト
 * - testDivide() - 除算のテスト
 * - testDivideByZero() - ゼロ除算の例外テスト
 * - testIsEven() - 偶数判定のテスト
 * - testIsPositive() - 正の値判定のテスト
 * - testMax() - 最大値取得のテスト
 * - testMaxWithEmptyArray() - 空配列での例外テスト
 * - testMaxWithNullArray() - null配列での例外テスト
 * 
 * 境界値テストの追加も検討してください:
 * - 最大値・最小値での計算
 * - 0との計算
 * - 負の値での計算
 */