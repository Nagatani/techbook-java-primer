// ○ 適切な境界型パラメーター
public class NumberContainer<T extends Number> {
    private final T value;
    
    public NumberContainer(T value) {
        this.value = value;
    }
    
    public double multipliedBy(double factor) {
        // Number型の共通メソッドでdoubleに変換してから計算し、結果を返す
        return this.value.doubleValue() * factor;
    }
}
