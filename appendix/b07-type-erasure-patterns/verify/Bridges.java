import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;

/**
 * コンパイラーが作るブリッジメソッドを観察する。
 *
 *   javac Bridges.java && java Bridges
 *   javap -p -v 'Bridges$Version'     ブリッジメソッドのフラグ（ACC_BRIDGE・ACC_SYNTHETIC）
 *   javap -p -c 'Bridges$Version'     ブリッジメソッドの中身
 */
public class Bridges {
    // 1. ジェネリックなインターフェイスを、型引数を決めて実装する
    static final class Version implements Comparable<Version> {
        final int number;
        Version(int number) { this.number = number; }

        @Override
        public int compareTo(Version other) {
            return Integer.compare(number, other.number);
        }
    }

    // 2. 型パラメーターの戻り値を、具体的な型でオーバーライドする
    abstract static class Processor<T> {
        abstract T process(String input);
    }

    static class UpperCase extends Processor<String> {
        @Override
        String process(String input) { return input.toUpperCase(); }
    }

    // 3. ジェネリクスと関係のない、共変戻り値型のオーバーライド
    static class Animal {
        Animal self() { return this; }
    }

    static class Dog extends Animal {
        @Override
        Dog self() { return this; }
    }

    static void printMethods(Class<?> c) {
        Method[] methods = c.getDeclaredMethods();
        // getDeclaredMethods()の順序は決まっていないので並べ替える
        Arrays.sort(methods, Comparator.comparing(Method::toString));
        IO.println(c.getSimpleName() + "のメソッド:");
        for (Method m : methods) {
            IO.println("  " + m.getReturnType().getSimpleName() + " " + m.getName()
                    + Arrays.toString(m.getParameterTypes()).replace("class ", "")
                    + " bridge=" + m.isBridge() + " synthetic=" + m.isSynthetic());
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {
        printMethods(Version.class);
        printMethods(UpperCase.class);
        printMethods(Dog.class);

        // raw型を通すと、Version以外の値をcompareToに渡せてしまう。
        // 呼ばれるのはブリッジメソッド compareTo(Object) で、その中のキャストが失敗する
        Comparable raw = new Version(1);
        try {
            raw.compareTo("2");
        } catch (ClassCastException e) {
            StackTraceElement top = e.getStackTrace()[0];
            IO.println("ClassCastException: " + top.getClassName() + "." + top.getMethodName());
        }
    }
}
