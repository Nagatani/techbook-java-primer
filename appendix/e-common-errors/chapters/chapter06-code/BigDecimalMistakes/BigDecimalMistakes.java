import java.math.BigDecimal;

public class BigDecimalMistakes {
    public static void main(String[] args) {
        // 誤り1: addの戻り値を受け取っていない
        BigDecimal total = BigDecimal.ZERO;
        total.add(new BigDecimal("100"));
        IO.println("合計: " + total);

        // 誤り2: doubleの値から作っている
        BigDecimal price = new BigDecimal(0.1);
        IO.println("価格: " + price);

        // 誤り3: equalsで値を比べている
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");
        IO.println("等しいか: " + a.equals(b));

        // 誤り4: 割り切れない割り算で桁数を指定していない
        BigDecimal perPerson = new BigDecimal("10000")
                .divide(new BigDecimal("3"));
        IO.println("1人あたり: " + perPerson);
    }
}
