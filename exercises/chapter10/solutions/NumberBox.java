package chapter10.solutions;

/**
 * 数値だけを入れられる箱（課題2の解答例）
 *
 * Tの境界をNumberにしたので、Tの値に対してNumberのメソッド（doubleValue・intValue）を呼べます。
 *
 * @param <T> Numberまたはそのサブクラス
 */
public class NumberBox<T extends Number> {
    private T number;

    public NumberBox(T number) {
        this.number = number;
    }

    public T getNumber() {
        return number;
    }

    public void setNumber(T number) {
        this.number = number;
    }

    public double getAsDouble() {
        return number.doubleValue();
    }

    public int getAsInt() {
        return number.intValue();
    }

    public boolean isPositive() {
        return number.doubleValue() > 0;
    }

    /**
     * 型の違うNumberBoxとも比べられるよう、引数をNumberBox<?>にする。
     * どの型でもNumberなので、doubleに直して比べられる
     */
    public int compareTo(NumberBox<?> other) {
        return Double.compare(getAsDouble(), other.getAsDouble());
    }

    public void displayInfo() {
        IO.println("NumberBox情報:");
        IO.println("  型: " + number.getClass().getSimpleName());
        IO.println("  値: " + number);
        IO.println("  double値: " + getAsDouble());
        IO.println("  int値: " + getAsInt());
    }

    @Override
    public String toString() {
        return "NumberBox{" + number + "}";
    }
}
