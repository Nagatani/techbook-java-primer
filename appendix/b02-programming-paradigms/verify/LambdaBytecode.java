import java.util.function.BinaryOperator;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;

/** ラムダ式・メソッド参照・匿名クラスが、何にコンパイルされ、実行時に何になるかを見る */
public class LambdaBytecode {

    static IntUnaryOperator noCapture() {
        return x -> x * 2;
    }

    static IntUnaryOperator capture(int factor) {
        return x -> x * factor;
    }

    static BinaryOperator<Integer> methodRef() {
        return Integer::sum;
    }

    static Supplier<String> anonymous() {
        return new Supplier<String>() {
            @Override
            public String get() {
                return "anonymous";
            }
        };
    }

    static void describe(String label, Object o) {
        Class<?> c = o.getClass();
        IO.println(label + ": hidden=" + c.isHidden()
            + " synthetic=" + c.isSynthetic()
            + " anonymous=" + c.isAnonymousClass()
            + " name=" + c.getName().replaceAll("/0x[0-9a-f]+$", "/0x…"));
    }

    public static void main(String[] args) {
        describe("ラムダ式", noCapture());
        describe("メソッド参照", methodRef());
        describe("匿名クラス", anonymous());
        IO.println("capture(3).applyAsInt(5) = " + capture(3).applyAsInt(5));
    }
}
