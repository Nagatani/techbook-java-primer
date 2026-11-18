import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * 関数型インターフェイスの「抽象メソッドが1つ」の数え方を、標準ライブラリのインターフェイスで観察する。
 */
public class FunctionalInterfaces {

    /** インターフェイスが宣言している抽象メソッドの名前を、名前の順に並べる */
    static List<String> abstractMethods(Class<?> type) {
        return Arrays.stream(type.getDeclaredMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers()))
                .map(Method::getName)
                .sorted()
                .toList();
    }

    /** Objectのpublicメソッドと同じシグネチャか */
    static boolean isObjectMethod(Method m) {
        try {
            Object.class.getMethod(m.getName(), m.getParameterTypes());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    static void report(Class<?> type) {
        long counted = Arrays.stream(type.getDeclaredMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers()))
                .filter(m -> !isObjectMethod(m))
                .count();
        long defaults = Arrays.stream(type.getDeclaredMethods()).filter(Method::isDefault).count();
        IO.println(type.getSimpleName() + ": 抽象メソッド " + abstractMethods(type)
                + "、Objectのメソッドを除くと " + counted + " 個、defaultメソッド " + defaults + " 個");
    }

    public static void main(String[] args) {
        report(Comparator.class);
        report(Function.class);
        report(Runnable.class);

        // Comparatorは抽象メソッドequalsを宣言していても、ラムダ式で作れる
        Comparator<String> byLength = (a, b) -> Integer.compare(a.length(), b.length());
        IO.println(List.of("ccc", "a", "bb").stream().sorted(byLength).toList());
    }
}
