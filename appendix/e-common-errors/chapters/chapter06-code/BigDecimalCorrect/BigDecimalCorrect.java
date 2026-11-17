import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalCorrect {
    public static void main(String[] args) {
        // 修正1: 計算の結果は新しいオブジェクトなので、戻り値を受け取る
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(new BigDecimal("100"));
        IO.println("合計: " + total);

        // 修正2: 文字列から作る（doubleの値からはBigDecimal.valueOf）
        BigDecimal price = new BigDecimal("0.1");
        IO.println("価格: " + price + "、" + BigDecimal.valueOf(0.1));

        // 修正3: 値が等しいかはcompareToで調べる
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");
        IO.println("等しいか: " + (a.compareTo(b) == 0));

        // 修正4: 割り算には桁数と丸め方を指定する
        BigDecimal perPerson = new BigDecimal("10000")
                .divide(new BigDecimal("3"), 2, RoundingMode.HALF_UP);
        IO.println("1人あたり: " + perPerson);
    }
}
