// DecimalRounding.java - 掛け算の桁数、丸め、割り切れない割り算
import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalRounding {
    public static void main(String[] args) {
        BigDecimal price = new BigDecimal("1234");
        BigDecimal taxRate = new BigDecimal("0.10");

        // 掛け算の結果は、小数点以下の桁数が2つの数の桁数の和になる
        BigDecimal tax = price.multiply(taxRate);
        IO.println("消費税（計算どおり）: " + tax);
        // setScaleで小数点以下の桁数をそろえる（ここでは円未満を切り捨て）
        BigDecimal roundedTax = tax.setScale(0, RoundingMode.DOWN);
        IO.println("消費税（円未満切り捨て）: " + roundedTax);
        IO.println("税込み価格: " + price.add(roundedTax));

        // 割り切れない割り算は、桁数と丸め方を指定する
        BigDecimal bill = new BigDecimal("10000");
        BigDecimal people = new BigDecimal("3");
        IO.println("1人あたり（小数点以下2桁）: "
                + bill.divide(people, 2, RoundingMode.HALF_UP));
        IO.println("1人あたり（円未満切り上げ）: "
                + bill.divide(people, 0, RoundingMode.UP));

        // 桁数を指定しないと、ArithmeticException（第13章で学ぶ例外）になる
        try {
            IO.println(bill.divide(people));
        } catch (ArithmeticException e) {
            IO.println("桁数を指定しない割り算 → " + e.getMessage());
        }
    }
}
