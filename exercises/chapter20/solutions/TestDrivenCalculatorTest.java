/**
 * 第20章 課題1: テスト駆動開発による計算機のテスト
 * 
 * JUnit 5を使用したテストクラス
 * TDDのテストファーストアプローチを実践
 * 
 * 学習ポイント:
 * - JUnit 5のアノテーションとアサーション
 * - 境界値分析によるテストケース設計
 * - エラー推測によるテスト設計
 * - パラメータ化テストの活用
 */

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;

/**
 * TestDrivenCalculator のテストクラス
 * 
 * このテストクラスはTDDサイクルに従って作成されています：
 * 1. 失敗するテストを書く（Red）
 * 2. テストを通す最小実装（Green）  
 * 3. リファクタリング（Refactor）
 */
@DisplayName("テスト駆動計算機のテスト")
class TestDrivenCalculatorTest {
    
    private TestDrivenCalculator calculator;
    
    @BeforeEach
    void setUp() {
        calculator = new TestDrivenCalculator();
    }
    
    @AfterEach
    void tearDown() {
        calculator = null;
    }
    
    // 基本機能のテスト
    
    @Test
    @DisplayName("初期値は0である")
    void testInitialValue() {
        assertEquals(0.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    @Test
    @DisplayName("値の設定ができる")
    void testSetValue() {
        calculator.setValue(10.0);
        assertEquals(10.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    // 四則演算のテスト
    
    @Test
    @DisplayName("加算が正しく動作する")
    void testAddition() {
        calculator.setValue(5.0);
        calculator.add(3.0);
        assertEquals(8.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    @Test
    @DisplayName("減算が正しく動作する")
    void testSubtraction() {
        calculator.setValue(10.0);
        calculator.subtract(4.0);
        assertEquals(6.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    @Test
    @DisplayName("乗算が正しく動作する")
    void testMultiplication() {
        calculator.setValue(6.0);
        calculator.multiply(7.0);
        assertEquals(42.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    @Test
    @DisplayName("除算が正しく動作する")
    void testDivision() {
        calculator.setValue(15.0);
        calculator.divide(3.0);
        assertEquals(5.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    // エラーケースのテスト（エラー推測）
    
    @Test
    @DisplayName("ゼロ除算で例外が発生する")
    void testDivisionByZero() {
        calculator.setValue(10.0);
        
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> {
            calculator.divide(0.0);
        });
        
        assertEquals("Division by zero", exception.getMessage());
        assertTrue(calculator.hasError());
    }
    
    @Test
    @DisplayName("非常に小さな値での除算もゼロ除算として扱われる")
    void testDivisionByVerySmallNumber() {
        calculator.setValue(10.0);
        
        assertThrows(ArithmeticException.class, () -> {
            calculator.divide(1e-15);
        });
        
        assertTrue(calculator.hasError());
    }
    
    @Test
    @DisplayName("負の数の平方根で例外が発生する")
    void testSquareRootOfNegativeNumber() {
        calculator.setValue(-4.0);
        
        ArithmeticException exception = assertThrows(ArithmeticException.class, () -> {
            calculator.sqrt();
        });
        
        assertEquals("Square root of negative number", exception.getMessage());
        assertTrue(calculator.hasError());
    }
    
    @Test
    @DisplayName("NaNの設定でエラー状態になる")
    void testSetNaN() {
        calculator.setValue(Double.NaN);
        assertTrue(calculator.hasError());
    }
    
    @Test
    @DisplayName("無限大の設定でエラー状態になる")
    void testSetInfinity() {
        calculator.setValue(Double.POSITIVE_INFINITY);
        assertTrue(calculator.hasError());
        
        calculator.clear();
        calculator.setValue(Double.NEGATIVE_INFINITY);
        assertTrue(calculator.hasError());
    }
    
    // 境界値分析のテスト
    
    @ParameterizedTest
    @ValueSource(doubles = {0.0, 1.0, -1.0, Double.MAX_VALUE, Double.MIN_VALUE})
    @DisplayName("境界値での加算テスト")
    void testAdditionWithBoundaryValues(double value) {
        calculator.setValue(0.0);
        calculator.add(value);
        assertEquals(value, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    @ParameterizedTest
    @CsvSource({
        "5.0, 3.0, 8.0",
        "-5.0, 3.0, -2.0", 
        "5.0, -3.0, 2.0",
        "-5.0, -3.0, -8.0",
        "0.0, 0.0, 0.0"
    })
    @DisplayName("様々な値での加算テスト")
    void testAdditionWithVariousValues(double initial, double addend, double expected) {
        calculator.setValue(initial);
        calculator.add(addend);
        assertEquals(expected, calculator.getCurrentValue(), 1e-10);
        assertFalse(calculator.hasError());
    }
    
    // チェーン計算のテスト
    
    @Test
    @DisplayName("チェーン計算が正しく動作する")
    void testChainCalculation() {
        double result = calculator.setValue(10.0)
                                 .add(5.0)     // 15
                                 .multiply(2.0) // 30
                                 .subtract(10.0) // 20
                                 .divide(4.0)   // 5
                                 .getCurrentValue();
        
        assertEquals(5.0, result);
        assertFalse(calculator.hasError());
    }
    
    @Test
    @DisplayName("複雑なチェーン計算")
    void testComplexChainCalculation() {
        calculator.setValue(2.0)
                  .power(3.0)      // 8
                  .sqrt()          // 2.828...
                  .multiply(3.0)   // 8.485...
                  .add(1.515);     // 10.0
        
        assertEquals(10.0, calculator.getCurrentValue(), 1e-10);
        assertFalse(calculator.hasError());
    }
    
    // 高度な機能のテスト
    
    @Test
    @DisplayName("平方根の計算")
    void testSquareRoot() {
        calculator.setValue(9.0);
        calculator.sqrt();
        assertEquals(3.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
        
        calculator.setValue(2.0);
        calculator.sqrt();
        assertEquals(Math.sqrt(2.0), calculator.getCurrentValue(), 1e-10);
    }
    
    @Test
    @DisplayName("累乗の計算")
    void testPower() {
        calculator.setValue(2.0);
        calculator.power(3.0);
        assertEquals(8.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
        
        calculator.setValue(5.0);
        calculator.power(0.0);
        assertEquals(1.0, calculator.getCurrentValue());
    }
    
    @Test
    @DisplayName("パーセンテージの計算")
    void testPercent() {
        calculator.setValue(50.0);
        calculator.percent();
        assertEquals(0.5, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    // クリア機能のテスト
    
    @Test
    @DisplayName("クリアで初期状態に戻る")
    void testClear() {
        calculator.setValue(100.0);
        calculator.add(50.0);
        calculator.clear();
        
        assertEquals(0.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
        assertTrue(calculator.getHistory().size() > 0); // "クリア" エントリが追加される
    }
    
    @Test
    @DisplayName("エラー状態からのクリア")
    void testClearFromErrorState() {
        calculator.setValue(10.0);
        assertThrows(ArithmeticException.class, () -> calculator.divide(0.0));
        assertTrue(calculator.hasError());
        
        calculator.clear();
        assertEquals(0.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    // 履歴機能のテスト
    
    @Test
    @DisplayName("計算履歴が正しく記録される")
    void testCalculationHistory() {
        calculator.setValue(10.0);
        calculator.add(5.0);
        calculator.multiply(2.0);
        
        List<String> history = calculator.getHistory();
        assertTrue(history.size() >= 3);
        assertTrue(history.get(0).contains("設定"));
        assertTrue(history.get(1).contains("+"));
        assertTrue(history.get(2).contains("×"));
    }
    
    @Test
    @DisplayName("履歴をクリアできる")
    void testClearHistory() {
        calculator.setValue(10.0);
        calculator.add(5.0);
        calculator.clearHistory();
        
        assertTrue(calculator.getHistory().isEmpty());
    }
    
    @Test
    @DisplayName("最後の履歴エントリを取得できる")
    void testGetLastHistoryEntry() {
        assertNull(calculator.getLastHistoryEntry());
        
        calculator.setValue(10.0);
        String lastEntry = calculator.getLastHistoryEntry();
        assertNotNull(lastEntry);
        assertTrue(lastEntry.contains("設定"));
    }
    
    // 状態管理のテスト
    
    @Test
    @DisplayName("計算機の状態を保存・復元できる")
    void testSaveAndRestoreState() {
        calculator.setValue(42.0);
        calculator.add(8.0);
        
        Map<String, Object> state = calculator.saveState();
        
        calculator.clear();
        assertEquals(0.0, calculator.getCurrentValue());
        
        calculator.restoreState(state);
        assertEquals(50.0, calculator.getCurrentValue());
        assertFalse(calculator.hasError());
    }
    
    @Test
    @DisplayName("統計情報を取得できる")
    void testGetStatistics() {
        calculator.setValue(5.0);
        calculator.add(3.0);
        
        Map<String, Object> stats = calculator.getStatistics();
        assertEquals(8.0, (Double) stats.get("currentValue"));
        assertFalse((Boolean) stats.get("hasError"));
        assertTrue((Boolean) stats.get("isPositive"));
        assertFalse((Boolean) stats.get("isNegative"));
        assertFalse((Boolean) stats.get("isZero"));
    }
    
    // toString メソッドのテスト
    
    @Test
    @DisplayName("文字列表現が正しい")
    void testToString() {
        calculator.setValue(42.0);
        assertEquals("42", calculator.toString());
        
        calculator.setValue(3.14159);
        assertTrue(calculator.toString().startsWith("3.14159"));
        
        calculator.setValue(0.0);
        assertEquals("0", calculator.toString());
    }
    
    @Test
    @DisplayName("エラー状態での文字列表現")
    void testToStringWithError() {
        calculator.setValue(10.0);
        assertThrows(ArithmeticException.class, () -> calculator.divide(0.0));
        assertEquals("ERROR", calculator.toString());
    }
    
    // equals と hashCode のテスト
    
    @Test
    @DisplayName("等価性の判定")
    void testEquals() {
        TestDrivenCalculator calc1 = new TestDrivenCalculator();
        TestDrivenCalculator calc2 = new TestDrivenCalculator();
        
        calc1.setValue(10.0);
        calc2.setValue(10.0);
        
        assertEquals(calc1, calc2);
        assertEquals(calc1.hashCode(), calc2.hashCode());
    }
    
    @Test
    @DisplayName("エラー状態での等価性")
    void testEqualsWithError() {
        TestDrivenCalculator calc1 = new TestDrivenCalculator();
        TestDrivenCalculator calc2 = new TestDrivenCalculator();
        
        calc1.setValue(10.0);
        assertThrows(ArithmeticException.class, () -> calc1.divide(0.0));
        
        calc2.setValue(5.0);
        assertThrows(ArithmeticException.class, () -> calc2.divide(0.0));
        
        assertEquals(calc1, calc2); // 両方ともエラー状態
    }
    
    // パフォーマンステスト
    
    @Test
    @DisplayName("大量の計算のパフォーマンステスト")
    @Timeout(1) // 1秒以内に完了すること
    void testPerformance() {
        for (int i = 0; i < 10000; i++) {
            calculator.setValue(i);
            calculator.add(1.0);
            calculator.multiply(2.0);
            calculator.divide(3.0);
        }
        
        // 計算が完了することを確認
        assertFalse(calculator.hasError());
    }
    
    // 履歴サイズ制限のテスト
    
    @Test
    @DisplayName("履歴サイズが制限される")
    void testHistorySizeLimit() {
        // 101回の操作を実行（履歴制限は100）
        for (int i = 0; i <= 101; i++) {
            calculator.setValue(i);
        }
        
        List<String> history = calculator.getHistory();
        assertTrue(history.size() <= 100);
    }
}

/*
 * テスト設計の特徴:
 * 
 * 1. 包括的なテストカバレッジ
 *    - 正常ケース
 *    - 異常ケース（例外）
 *    - 境界値
 *    - エラー状態
 * 
 * 2. JUnit 5の活用
 *    - @DisplayName でテストの意図を明確化
 *    - @ParameterizedTest でテストケースの効率化
 *    - @Timeout でパフォーマンステスト
 *    - @BeforeEach/@AfterEach でセットアップ/クリーンアップ
 * 
 * 3. 境界値分析
 *    - ゼロ、最大値、最小値
 *    - 正の数、負の数
 *    - NaN、無限大
 * 
 * 4. エラー推測
 *    - ゼロ除算
 *    - 負の数の平方根
 *    - オーバーフロー状況
 * 
 * 5. 状態テスト
 *    - エラー状態の確認
 *    - 履歴の管理
 *    - オブジェクトの等価性
 * 
 * TDDのメリット:
 * - テストが設計書として機能
 * - リファクタリングの安全性
 * - バグの早期発見
 * - コードの品質向上
 */