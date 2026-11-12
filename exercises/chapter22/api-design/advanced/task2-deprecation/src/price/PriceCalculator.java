package price;

/**
 * 税込価格を計算するメソッドを集めたクラスです。
 *
 * <p>金額はすべて円単位の整数で扱います。
 * 税込価格の1円未満は切り捨てます。</p>
 *
 * <pre>{@code
 * int food = PriceCalculator.withTax(398, TaxRate.REDUCED);    // 429
 * int book = PriceCalculator.withTax(1500, TaxRate.STANDARD);  // 1650
 * }</pre>
 *
 * @since 1.0
 */
public final class PriceCalculator {

    private PriceCalculator() {
        // インスタンスを作らせない
    }

    /**
     * 標準税率（10%）で税込価格を計算します。
     *
     * <p>1.0では、このメソッドだけを提供していました。
     * 軽減税率を選べないことと、戻り値が{@code double}で1円未満の端数が残ることから、
     * 1.1で非推奨にしました。</p>
     *
     * @param price 税抜価格（0以上）
     * @return 税込価格（端数を含む）
     * @throws IllegalArgumentException 価格が負のとき
     * @deprecated 1.1から非推奨です。2.0で削除する予定です。
     *             代わりに{@link #withTax(int, TaxRate)}に{@link TaxRate#STANDARD}を渡してください。
     *             戻り値は1円未満を切り捨てた{@code int}になります。
     */
    @Deprecated(since = "1.1", forRemoval = true)
    public static double withTax(int price) {
        checkPrice(price);
        return price * 1.1;
    }

    /**
     * 指定した税率で税込価格を計算します。1円未満は切り捨てます。
     *
     * @param price 税抜価格（0以上）
     * @param rate  税率
     * @return 税込価格
     * @throws IllegalArgumentException 価格が負のとき
     * @throws NullPointerException     税率が{@code null}のとき
     * @since 1.1
     */
    public static int withTax(int price, TaxRate rate) {
        checkPrice(price);
        if (rate == null) {
            throw new NullPointerException("税率がnullです");
        }
        return price * (100 + rate.percent()) / 100;
    }

    /**
     * 税込価格に含まれる消費税の額を計算します。
     *
     * @param price 税抜価格（0以上）
     * @param rate  税率
     * @return 消費税の額（{@code withTax(price, rate) - price}）
     * @throws IllegalArgumentException 価格が負のとき
     * @throws NullPointerException     税率が{@code null}のとき
     * @see #withTax(int, TaxRate)
     * @since 1.1
     */
    public static int taxOf(int price, TaxRate rate) {
        return withTax(price, rate) - price;
    }

    private static void checkPrice(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("価格が負です: " + price);
        }
    }
}
