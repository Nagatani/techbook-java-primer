// DecimalBasics.java - doubleの誤差と、BigDecimalによる10進数の正確な計算
import java.math.BigDecimal;

public class DecimalBasics {
    public static void main(String[] args) {
        // doubleで0.1を10回足す
        double sum = 0.0;
        for (int i = 0; i < 10; i++) {
            sum += 0.1;
        }
        IO.println("double: 0.1を10回足す → " + sum);
        IO.println("double: 1.0と等しいか → " + (sum == 1.0));
        IO.println("double: 1.10 - 1.00 → " + (1.10 - 1.00));

        // BigDecimalは文字列から作る
        BigDecimal tenth = new BigDecimal("0.1");
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < 10; i++) {
            // addは新しいオブジェクトを返すので、戻り値を受け取る
            total = total.add(tenth);
        }
        IO.println("BigDecimal: 0.1を10回足す → " + total);
        BigDecimal price = new BigDecimal("1.10");
        IO.println("BigDecimal: 1.10 - 1.00 → "
                + price.subtract(new BigDecimal("1.00")));

        // doubleの値から作ると、doubleの誤差まで受け継ぐ
        IO.println("new BigDecimal(0.1) → " + new BigDecimal(0.1));
        IO.println("BigDecimal.valueOf(0.1) → "
                + BigDecimal.valueOf(0.1));
    }
}
