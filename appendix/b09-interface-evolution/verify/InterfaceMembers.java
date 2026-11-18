import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;

/**
 * インターフェイスに書けるメンバーの種類と、それぞれに付く修飾子を観察する。
 * javap -v -p 'InterfaceMembers$Greeter' でフラグを、javap -c -p でメソッドの呼び出し命令を見る。
 */
public class InterfaceMembers {

    interface Greeter {
        // 修飾子を書かなくても public static final になる
        String PREFIX = "Hello, ";

        // 修飾子を書かなくても public abstract になる
        String name();

        // defaultメソッド（Java 8）
        default String greet() {
            return decorate(PREFIX + name());
        }

        // staticメソッド（Java 8）
        static Greeter of(String name) {
            return () -> name;
        }

        // privateメソッド（Java 9）
        private String decorate(String text) {
            return "[" + text + "]";
        }
    }

    public static void main(String[] args) {
        Greeter g = Greeter.of("Java");
        IO.println(g.greet());

        for (Field f : Greeter.class.getDeclaredFields()) {
            IO.println("field  " + f.getName() + ": " + Modifier.toString(f.getModifiers()));
        }
        Method[] methods = Greeter.class.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        for (Method m : methods) {
            if (m.isSynthetic()) {
                continue;  // ラムダ式の本体としてコンパイラーが作ったメソッドは除く
            }
            IO.println("method " + m.getName() + ": " + Modifier.toString(m.getModifiers())
                    + (m.isDefault() ? " (default)" : ""));
        }
    }
}
