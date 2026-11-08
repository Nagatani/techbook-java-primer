// ✅ 適切な境界型パラメータ
public class NumberContainer<T extends Number> {
    private T value;
    
    public void multiplyBy(double factor) {
        // Number型の共通メソッドを使用
        double result = this.value.doubleValue() * factor;
        // 結果の設定は型に応じて処理
    }
}
