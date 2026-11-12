/**
 * 長さの単位。1単位が何メートルかを持つ。
 */
public enum LengthUnit {
    MILLIMETER("ミリメートル", "mm", 0.001),
    CENTIMETER("センチメートル", "cm", 0.01),
    METER("メートル", "m", 1.0),
    KILOMETER("キロメートル", "km", 1000.0),
    INCH("インチ", "in", 0.0254),
    FOOT("フィート", "ft", 0.3048),
    MILE("マイル", "mi", 1609.344);

    private final String japaneseName;
    private final String symbol;
    private final double meters;

    LengthUnit(String japaneseName, String symbol, double meters) {
        this.japaneseName = japaneseName;
        this.symbol = symbol;
        this.meters = meters;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getMeters() {
        return meters;
    }

    /** コンボボックスに表示される文字列 */
    @Override
    public String toString() {
        return japaneseName + " (" + symbol + ")";
    }
}
