import java.util.function.Supplier;

/**
 * invokedynamic命令が使われる2つの場面（ラムダ式と文字列の連結）を、javapで観察する。
 *
 *   javac Dynamic.java
 *   javap -c -p Dynamic
 */
public class Dynamic {
    static String greet(String name, int count) {
        return "Hello, " + name + " x" + count;      // 文字列の連結
    }

    static Supplier<String> lazy() {
        return () -> "lambda";                         // ラムダ式
    }

    public static void main(String[] args) {
        IO.println(greet("JVM", 3));
        IO.println(lazy().get());
    }
}
