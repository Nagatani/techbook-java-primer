/**
 * Moneyを試すプログラム（課題4の解答例）
 */
public class MoneyTest {
    public static void main(String[] args) {
        IO.println("=== 金額計算クラステスト ===");

        Money money1 = new Money(1000);
        Money money2 = new Money(500);
        IO.println("金額1: " + money1);
        IO.println("金額2: " + money2);
        IO.println();

        IO.println("加算: " + money1 + " + " + money2 + " = " + money1.plus(money2));
        IO.println("減算: " + money1 + " - " + money2 + " = " + money1.minus(money2));
        IO.println("10%増し: " + money1 + " → " + money1.increaseByPercent(10));
        IO.println("比較: " + money1 + " > " + money2 + ": " + money1.isGreaterThan(money2));
        IO.println("計算のあとの金額1: " + money1);
        IO.println("1000円どうしは等しい？: " + money1.equals(new Money(1000)));
        IO.println();

        IO.println("負になる計算:");
        // 例外の受け取り方（try-catch）は第13章で学ぶ
        try {
            money2.minus(money1);
        } catch (IllegalArgumentException e) {
            IO.println("計算できませんでした: " + e.getMessage());
        }
    }
}
