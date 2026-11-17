// DecimalComparison.java - BigDecimalの比較と、桁数の上限がない整数BigInteger
import java.math.BigDecimal;
import java.math.BigInteger;

public class DecimalComparison {
    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");

        // equalsは値と小数点以下の桁数（スケール）の両方を比べる
        IO.println("2.0.equals(2.00) → " + a.equals(b));
        // compareToは値だけを比べ、小さければ負、等しければ0、大きければ正
        IO.println("2.0.compareTo(2.00) → " + a.compareTo(b));
        boolean isLess = a.compareTo(new BigDecimal("2.01")) < 0;
        IO.println("2.0は2.01より小さいか → " + isLess);

        // longの最大値に1を足すと、longはあふれて負の値になる
        long max = Long.MAX_VALUE;
        IO.println("long: " + max + " + 1 → " + (max + 1));
        // BigIntegerはあふれない
        BigInteger big = BigInteger.valueOf(max).add(BigInteger.ONE);
        IO.println("BigInteger: " + max + " + 1 → " + big);

        // 30の階乗はlongに収まらない
        BigInteger factorial = BigInteger.ONE;
        for (int i = 2; i <= 30; i++) {
            factorial = factorial.multiply(BigInteger.valueOf(i));
        }
        IO.println("30の階乗 → " + factorial);
    }
}
