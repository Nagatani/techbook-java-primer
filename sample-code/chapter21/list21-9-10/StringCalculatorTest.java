/**
 * リスト21-9
 * StringCalculatorTestクラス
 * 
 * 元ファイル: chapter21-unit-testing.md (310行目)
 */

public class StringCalculatorTest {
    @Test
    public void testAdd_EmptyString_ReturnsZero() {
        // まだ存在しないクラスとメソッドのテストを書く
        StringCalculator calc = new StringCalculator();
        assertEquals(0, calc.add(""));
    }
}