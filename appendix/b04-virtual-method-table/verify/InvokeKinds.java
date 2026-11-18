import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * メソッドの種類ごとに、呼び出しがどのバイトコード命令になるかを観察する。
 *
 *   javac InvokeKinds.java
 *   javap -c -p InvokeKinds
 */
public class InvokeKinds {
    interface Drawable { void draw(); }

    static class Base {
        void greet() { }
    }

    static class Child extends Base implements Drawable {
        static void staticMethod() { }
        private void privateMethod() { }
        final void finalMethod() { }
        void virtualMethod() { }
        public void draw() { }

        @Override
        void greet() {
            super.greet();          // スーパークラスのメソッドの呼び出し
        }

        void callAll(Drawable d) {
            staticMethod();
            privateMethod();
            finalMethod();
            virtualMethod();
            d.draw();
        }
    }

    public static void main(String[] args) {
        Child c = new Child();      // コンストラクターの呼び出し
        c.callAll(c);
        // Objectのメソッドのうち、オーバーライドできるもの（final・static・privateでないもの）
        String overridable = Arrays.stream(Object.class.getDeclaredMethods())
                .filter(m -> !Modifier.isFinal(m.getModifiers()))
                .filter(m -> !Modifier.isStatic(m.getModifiers()))
                .filter(m -> !Modifier.isPrivate(m.getModifiers()))
                .map(Method::getName)
                .sorted()
                .collect(Collectors.joining(" "));
        IO.println("Objectのオーバーライドできるメソッド: " + overridable);
    }
}
