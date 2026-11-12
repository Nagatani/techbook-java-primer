package price;

/**
 * 消費税の税率です。
 *
 * @since 1.1
 */
public enum TaxRate {
    /** 標準税率（10%） */
    STANDARD(10),
    /** 軽減税率（8%）。飲食料品などに使う */
    REDUCED(8);

    private final int percent;

    TaxRate(int percent) {
        this.percent = percent;
    }

    /**
     * 税率をパーセントで返します。
     *
     * @return 税率（たとえば標準税率なら10）
     */
    public int percent() {
        return percent;
    }
}
