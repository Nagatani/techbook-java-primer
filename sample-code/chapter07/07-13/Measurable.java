// 大きさを比べられるオブジェクトのトレイト（状態を持たず、振る舞いだけを提供する）
interface Measurable {
    // 比べる基準になる値（実装クラスが決める）
    double measure();

    default boolean isLessThan(Measurable other) {
        return measure() < other.measure();
    }

    default boolean isGreaterThan(Measurable other) {
        return measure() > other.measure();
    }

    default boolean isEqualTo(Measurable other) {
        return measure() == other.measure();
    }

    default boolean isBetween(Measurable lower, Measurable upper) {
        return isGreaterThan(lower) && isLessThan(upper);
    }
}

// 使用例
class Temperature implements Measurable {
    private final double celsius;

    public Temperature(double celsius) {
        this.celsius = celsius;
    }

    @Override
    public double measure() {
        return celsius;
    }

    public void checkRange() {
        Temperature freezing = new Temperature(0);
        Temperature boiling = new Temperature(100);

        if (this.isBetween(freezing, boiling)) {
            IO.println("水は液体状態です");
        }
    }
}
