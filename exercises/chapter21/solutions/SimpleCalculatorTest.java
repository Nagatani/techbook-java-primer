/**
 * 第21章 基本課題1の解答例: SimpleCalculatorのテスト
 *
 * テストを@Nestedで「四則演算」「判定」「最大値」の3つにまとめ、9件のテストを書いています。
 * 正常な値に加えて、境界の値（0、負の数）と異常な値（0で割る、空の配列、null）も確かめます。
 */

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("SimpleCalculatorのテスト")
class SimpleCalculatorTest {

    private SimpleCalculator calculator;

    // テストごとに新しい計算機を作るので、テストどうしが影響しあわない
    @BeforeEach
    void setUp() {
        calculator = new SimpleCalculator();
    }

    @Nested
    @DisplayName("四則演算")
    class Arithmetic {

        @Test
        @DisplayName("addは2つの数の和を返す（負の数と0も含む）")
        void add() {
            assertAll(
                () -> assertEquals(8, calculator.add(3, 5)),
                () -> assertEquals(-2, calculator.add(3, -5)),
                () -> assertEquals(7, calculator.add(7, 0))
            );
        }

        @Test
        @DisplayName("subtractは1つ目から2つ目を引いた差を返す")
        void subtract() {
            assertAll(
                () -> assertEquals(6, calculator.subtract(10, 4)),
                () -> assertEquals(-6, calculator.subtract(4, 10)),
                () -> assertEquals(0, calculator.subtract(-3, -3))
            );
        }

        @Test
        @DisplayName("multiplyは2つの数の積を返す（0を掛けると0）")
        void multiply() {
            assertAll(
                () -> assertEquals(12, calculator.multiply(3, 4)),
                () -> assertEquals(-12, calculator.multiply(-3, 4)),
                () -> assertEquals(0, calculator.multiply(5, 0))
            );
        }

        @Test
        @DisplayName("divideは小数点以下まで含めた商を返す")
        void divide() {
            // doubleは誤差を含むので、許す誤差を3つ目の引数に渡す
            assertAll(
                () -> assertEquals(2.5, calculator.divide(5, 2), 1e-9),
                () -> assertEquals(0.333333, calculator.divide(1, 3), 1e-6),
                () -> assertEquals(-4.0, calculator.divide(-8, 2), 1e-9)
            );
        }

        @Test
        @DisplayName("0で割るとIllegalArgumentExceptionになる")
        void divideByZero() {
            IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                    () -> calculator.divide(10, 0));
            assertEquals("除数は0にできません", e.getMessage());
        }
    }

    @Nested
    @DisplayName("判定")
    class Predicates {

        @Test
        @DisplayName("isEvenは偶数でtrue、奇数でfalseを返す（0と負の数も含む）")
        void isEven() {
            assertAll(
                () -> assertTrue(calculator.isEven(2)),
                () -> assertTrue(calculator.isEven(0)),
                () -> assertTrue(calculator.isEven(-4)),
                () -> assertFalse(calculator.isEven(1)),
                () -> assertFalse(calculator.isEven(-3))
            );
        }

        @Test
        @DisplayName("isPositiveは正の数だけtrueを返す（0はfalse）")
        void isPositive() {
            assertAll(
                () -> assertTrue(calculator.isPositive(1)),
                () -> assertFalse(calculator.isPositive(0)),
                () -> assertFalse(calculator.isPositive(-1))
            );
        }
    }

    @Nested
    @DisplayName("最大値")
    class Max {

        @Test
        @DisplayName("maxは配列の最大値を返す（負の数だけの配列と、要素が1つの配列も含む）")
        void max() {
            assertAll(
                () -> assertEquals(9, calculator.max(new int[] {3, 9, 4})),
                () -> assertEquals(-2, calculator.max(new int[] {-5, -2, -8})),
                () -> assertEquals(7, calculator.max(new int[] {7}))
            );
        }

        @Test
        @DisplayName("空の配列とnullはIllegalArgumentExceptionになる")
        void maxWithEmptyOrNull() {
            IllegalArgumentException empty = assertThrows(IllegalArgumentException.class,
                    () -> calculator.max(new int[0]));
            IllegalArgumentException nullArray = assertThrows(IllegalArgumentException.class,
                    () -> calculator.max(null));
            assertAll(
                () -> assertEquals("配列は空にできません", empty.getMessage()),
                () -> assertEquals("配列は空にできません", nullArray.getMessage())
            );
        }
    }
}
