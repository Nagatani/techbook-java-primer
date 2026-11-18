import java.util.Arrays;

/**
 * レコードの暗黙のequalsが、コンポーネントをどう比べるかを観察する。
 *
 *   javac RecordEquality.java
 *   java RecordEquality
 */
public class RecordEquality {
    record Measure(double value) { }

    record Tags(String[] values) { }

    // アクセサーを明示的に宣言し、フィールドとは違う値を返す
    record Name(String value) {
        public String value() {
            return value.toUpperCase();
        }
    }

    public static void main(String[] args) {
        // 浮動小数点数: ==ではなく、Double.compareと同じ規則で比べる
        IO.println("0.0 == -0.0: " + (0.0 == -0.0));
        IO.println("Measure(0.0).equals(Measure(-0.0)): "
                + new Measure(0.0).equals(new Measure(-0.0)));
        IO.println("Double.compare(0.0, -0.0) == 0: " + (Double.compare(0.0, -0.0) == 0));
        IO.println("NaN == NaN: " + (Double.NaN == Double.NaN));
        IO.println("Measure(NaN).equals(Measure(NaN)): "
                + new Measure(Double.NaN).equals(new Measure(Double.NaN)));
        IO.println("Double.compare(NaN, NaN) == 0: " + (Double.compare(Double.NaN, Double.NaN) == 0));

        // 配列のコンポーネント: 中身ではなく参照を比べる（配列のequalsはObjectのまま）
        var a = new Tags(new String[] {"java", "record"});
        var b = new Tags(new String[] {"java", "record"});
        IO.println("配列の中身が同じ: " + Arrays.equals(a.values(), b.values()));
        IO.println("Tags.equals: " + a.equals(b));

        // 明示的に宣言したアクセサーは、equalsに使われない（フィールドの値を比べる）
        var lower = new Name("duke");
        var upper = new Name("DUKE");
        IO.println("アクセサーの値が同じ: " + lower.value().equals(upper.value()));
        IO.println("Name.equals: " + lower.equals(upper));
        IO.println("toString: " + lower);
    }
}
