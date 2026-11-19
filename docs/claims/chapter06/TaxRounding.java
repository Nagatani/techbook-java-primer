// 第6章コラム「お金の計算は「型」より「決まりごと」が難しい」
import java.math.BigDecimal;
import java.math.RoundingMode;

/** 消費税の端数を「どこで」「どう」丸めるかで、合計が変わる */
public class TaxRounding {
    public static void main(String[] args) {
        BigDecimal rate = new BigDecimal("0.10");
        BigDecimal[] prices = {new BigDecimal("98"), new BigDecimal("98"), new BigDecimal("98")};

        // 品目ごとに税を計算して切り捨て、足し合わせる
        BigDecimal perItem = BigDecimal.ZERO;
        for (BigDecimal p : prices) {
            perItem = perItem.add(p.multiply(rate).setScale(0, RoundingMode.DOWN));
        }
        // 合計してから税を計算し、1回だけ切り捨てる
        BigDecimal subtotal = BigDecimal.ZERO;
        for (BigDecimal p : prices) {
            subtotal = subtotal.add(p);
        }
        BigDecimal once = subtotal.multiply(rate).setScale(0, RoundingMode.DOWN);
        IO.println("品目ごとに切り捨て: " + perItem + "円");
        IO.println("合計してから切り捨て: " + once + "円");

        // 丸め方の違い（0.5ちょうどの値）
        for (String v : new String[] {"2.5", "3.5"}) {
            BigDecimal x = new BigDecimal(v);
            IO.println(v + " HALF_UP=" + x.setScale(0, RoundingMode.HALF_UP)
                    + " HALF_EVEN=" + x.setScale(0, RoundingMode.HALF_EVEN)
                    + " DOWN=" + x.setScale(0, RoundingMode.DOWN));
        }
    }
}
