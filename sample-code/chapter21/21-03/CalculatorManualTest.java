// CalculatorManualTest.java
public class CalculatorManualTest {
    public static void main(String[] args) {
        // Arrange（準備）
        Calculator calculator = new Calculator();
        int inputA = 2;
        int inputB = 3;
        int expected = 5;

        // Act（実行）
        int actual = calculator.add(inputA, inputB);

        // Assert（検証）
        if (actual == expected) {
            IO.println("○ テスト成功！");
        } else {
            System.err.println("× テスト失敗！");
        }
    }
}
