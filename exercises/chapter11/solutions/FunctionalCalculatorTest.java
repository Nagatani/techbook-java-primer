package exercises.chapter11.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Arrays;
import java.util.function.UnaryOperator;

/**
 * FunctionalCalculatorのテストクラス
 * 
 * 【テスト項目】
 * 1. 基本演算のテスト（正常系・異常系）
 * 2. 関数合成のテスト
 * 3. 条件付き演算のテスト
 * 4. パイプライン処理のテスト
 * 5. 並列処理のテスト
 * 6. 履歴機能のテスト
 */
class FunctionalCalculatorTest {
    
    private FunctionalCalculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new FunctionalCalculator();
    }
    
    @Test
    @DisplayName("基本演算：加算のテスト")
    void testBasicAddition() {
        double result = calculator.calculate(5.0, "+", 3.0);
        assertEquals(8.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("基本演算：減算のテスト")
    void testBasicSubtraction() {
        double result = calculator.calculate(10.0, "-", 4.0);
        assertEquals(6.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("基本演算：乗算のテスト")
    void testBasicMultiplication() {
        double result = calculator.calculate(6.0, "*", 7.0);
        assertEquals(42.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("基本演算：除算のテスト")
    void testBasicDivision() {
        double result = calculator.calculate(15.0, "/", 3.0);
        assertEquals(5.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("異常系：ゼロ除算のテスト")
    void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculator.calculate(10.0, "/", 0.0);
        });
    }
    
    @Test
    @DisplayName("異常系：不正な演算子のテスト")
    void testInvalidOperator() {
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculate(5.0, "invalid", 3.0);
        });
    }
    
    @Test
    @DisplayName("関数合成：複数の演算を組み合わせ")
    void testFunctionComposition() {
        UnaryOperator<Double> addTwo = x -> x + 2;
        UnaryOperator<Double> multiplyThree = x -> x * 3;
        UnaryOperator<Double> square = x -> x * x;
        
        // (x + 2) * 3 の平方：(5 + 2) * 3 = 21, 21^2 = 441
        double result = calculator.compose(5.0, addTwo, multiplyThree, square);
        assertEquals(441.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("条件付き演算：条件を満たす場合")
    void testConditionalCalculateWhenTrue() {
        double result = calculator.conditionalCalculate(
            10.0, 
            x -> x > 5, 
            x -> x * 2
        );
        assertEquals(20.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("条件付き演算：条件を満たさない場合")
    void testConditionalCalculateWhenFalse() {
        double result = calculator.conditionalCalculate(
            3.0, 
            x -> x > 5, 
            x -> x * 2
        );
        assertEquals(3.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("パイプライン処理：連続演算")
    void testPipelineProcessing() {
        double result = calculator.pipeline(10.0)
                                 .add(5.0)      // 15.0
                                 .multiply(2.0) // 30.0
                                 .subtract(10.0)// 20.0
                                 .divide(4.0)   // 5.0
                                 .result();
        assertEquals(5.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("パイプライン処理：条件付き演算")
    void testPipelineWithConditional() {
        double result = calculator.pipeline(10.0)
                                 .applyIf(x -> x > 5, x -> x * 2)  // 20.0
                                 .applyIf(x -> x < 15, x -> x + 10) // 条件不満足、20.0のまま
                                 .result();
        assertEquals(20.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("共通演算：平方根")
    void testCommonOperationsSqrt() {
        double result = calculator.pipeline(16.0)
                                 .apply(FunctionalCalculator.CommonOperations.SQRT)
                                 .result();
        assertEquals(4.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("共通演算：平方")
    void testCommonOperationsSquare() {
        double result = calculator.pipeline(5.0)
                                 .apply(FunctionalCalculator.CommonOperations.SQUARE)
                                 .result();
        assertEquals(25.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("共通演算：絶対値")
    void testCommonOperationsAbs() {
        double result = calculator.pipeline(-10.0)
                                 .apply(FunctionalCalculator.CommonOperations.ABS)
                                 .result();
        assertEquals(10.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("共通演算：逆数")
    void testCommonOperationsReciprocal() {
        double result = calculator.pipeline(0.25)
                                 .apply(FunctionalCalculator.CommonOperations.RECIPROCAL)
                                 .result();
        assertEquals(4.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("共通演算：逆数のゼロ除算")
    void testCommonOperationsReciprocalZero() {
        assertThrows(ArithmeticException.class, () -> {
            calculator.pipeline(0.0)
                     .apply(FunctionalCalculator.CommonOperations.RECIPROCAL)
                     .result();
        });
    }
    
    @Test
    @DisplayName("集約演算：合計")
    void testAggregateSum() {
        List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        double result = calculator.aggregate(numbers, Double::sum);
        assertEquals(15.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("集約演算：最大値")
    void testAggregateMax() {
        List<Double> numbers = Arrays.asList(1.0, 5.0, 3.0, 2.0, 4.0);
        double result = calculator.aggregate(numbers, Double::max);
        assertEquals(5.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("集約演算：最小値")
    void testAggregateMin() {
        List<Double> numbers = Arrays.asList(5.0, 1.0, 3.0, 2.0, 4.0);
        double result = calculator.aggregate(numbers, Double::min);
        assertEquals(1.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("並列集約演算：合計")
    void testParallelAggregateSum() {
        List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
        double result = calculator.parallelAggregate(numbers, Double::sum);
        assertEquals(15.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("カスタム演算子の登録と使用")
    void testCustomOperator() {
        // 平均演算子を登録
        calculator.registerOperator("avg", (a, b) -> (a + b) / 2);
        
        double result = calculator.calculate(10.0, "avg", 20.0);
        assertEquals(15.0, result, 0.0001);
    }
    
    @Test
    @DisplayName("演算履歴の確認")
    void testOperationHistory() {
        calculator.calculate(5.0, "+", 3.0);
        calculator.calculate(10.0, "*", 2.0);
        
        List<FunctionalCalculator.Operation> history = calculator.getHistory();
        assertEquals(2, history.size());
        
        FunctionalCalculator.Operation first = history.get(0);
        assertEquals("+", first.operator());
        assertEquals(5.0, first.operand1());
        assertEquals(3.0, first.operand2());
        assertEquals(8.0, first.result());
        
        FunctionalCalculator.Operation second = history.get(1);
        assertEquals("*", second.operator());
        assertEquals(10.0, second.operand1());
        assertEquals(2.0, second.operand2());
        assertEquals(20.0, second.result());
    }
    
    @Test
    @DisplayName("演算履歴のクリア")
    void testClearHistory() {
        calculator.calculate(5.0, "+", 3.0);
        calculator.calculate(10.0, "*", 2.0);
        
        assertEquals(2, calculator.getHistory().size());
        
        calculator.clearHistory();
        assertEquals(0, calculator.getHistory().size());
    }
    
    @Test
    @DisplayName("境界値テスト：非常に大きな数値")
    void testLargeNumbers() {
        double result = calculator.calculate(Double.MAX_VALUE / 2, "+", Double.MAX_VALUE / 2);
        assertEquals(Double.MAX_VALUE, result, 0.0001);
    }
    
    @Test
    @DisplayName("境界値テスト：非常に小さな数値")
    void testSmallNumbers() {
        double result = calculator.calculate(Double.MIN_VALUE, "+", Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE * 2, result, 0.0001);
    }
    
    @Test
    @DisplayName("境界値テスト：無限大")
    void testInfinity() {
        double result = calculator.calculate(Double.MAX_VALUE, "*", 2.0);
        assertEquals(Double.POSITIVE_INFINITY, result);
    }
    
    @Test
    @DisplayName("共通演算：条件判定のテスト")
    void testCommonPredicates() {
        assertTrue(FunctionalCalculator.CommonOperations.IS_POSITIVE.test(5.0));
        assertFalse(FunctionalCalculator.CommonOperations.IS_POSITIVE.test(-5.0));
        assertFalse(FunctionalCalculator.CommonOperations.IS_POSITIVE.test(0.0));
        
        assertTrue(FunctionalCalculator.CommonOperations.IS_EVEN.test(4.0));
        assertFalse(FunctionalCalculator.CommonOperations.IS_EVEN.test(5.0));
        
        assertTrue(FunctionalCalculator.CommonOperations.inRange(1.0, 10.0).test(5.0));
        assertFalse(FunctionalCalculator.CommonOperations.inRange(1.0, 10.0).test(15.0));
    }
    
    @Test
    @DisplayName("複雑な演算チェーンのテスト")
    void testComplexOperationChain() {
        // (((10 + 5) * 2) - 3) / 3 = 9
        double result = calculator.pipeline(10.0)
                                 .add(5.0)
                                 .multiply(2.0)
                                 .subtract(3.0)
                                 .divide(3.0)
                                 .result();
        assertEquals(9.0, result, 0.0001);
    }
}