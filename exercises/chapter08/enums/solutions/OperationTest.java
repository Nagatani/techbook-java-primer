/**
 * 四則演算の列挙型を試すプログラム（課題3の解答例）
 */
public class OperationTest {
    public static void main(String[] args) {
        IO.println("=== 計算機の演算子Enum ===");

        IO.println("基本演算:");
        for (Operation op : Operation.values()) {
            IO.println(op.format(10, 5));
        }
        IO.println();

        IO.println("連続計算（初期値 10）:");
        String[] symbols = {"+", "*", "-", "/"};
        double[] operands = {5, 2, 8, 2};
        double value = 10;
        for (int i = 0; i < symbols.length; i++) {
            Operation op = Operation.fromSymbol(symbols[i]);
            IO.println(op.format(value, operands[i]));
            value = op.apply(value, operands[i]);
        }
        IO.println("結果: " + value);
        IO.println();

        IO.println("特殊ケース:");
        IO.println(Operation.DIVIDE.format(10, 0));
        IO.println("記号 % の演算子: " + Operation.fromSymbol("%"));
    }
}
