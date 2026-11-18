/**
 * クラスの読み込み（ロード）と初期化（static初期化子の実行）が起きる時期を観察する。
 *
 *   javac InitOrder.java
 *   java InitOrder                         （初期化の時期）
 *   java -Xlog:class+load InitOrder        （読み込みの時期も表示する）
 */
public class InitOrder {
    static class Parent {
        static int parentValue = 10;
        static { IO.println("  Parentを初期化した"); }
    }

    static class Child extends Parent {
        static final int CONSTANT = 42;           // コンパイル時定数（定数変数）
        static int counter = 0;
        static { IO.println("  Childを初期化した"); }
    }

    interface Shape {
        Object ORIGIN = log("  Shapeを初期化した");
        default void draw() { }                   // デフォルトメソッドを持つ
    }

    interface Named {
        Object PREFIX = log("  Namedを初期化した");
        String name();                            // デフォルトメソッドを持たない
    }

    static class Circle implements Shape, Named {
        static { IO.println("  Circleを初期化した"); }
        public String name() { return "circle"; }
    }

    static Object log(String message) {
        IO.println(message);
        return message;
    }

    public static void main(String[] args) throws Exception {
        IO.println("1. Child.CONSTANTを読む");
        IO.println("  値: " + Child.CONSTANT);
        IO.println("2. Child.classを使う");
        IO.println("  " + Child.class.getSimpleName());
        IO.println("3. Childの配列を作る");
        Child[] array = new Child[3];
        IO.println("  長さ: " + array.length);
        IO.println("4. Class.forName(名前, false, ローダー)で読み込む");
        Class.forName("InitOrder$Child", false, InitOrder.class.getClassLoader());
        IO.println("5. Child.parentValueを読む（Parentで宣言したフィールド）");
        IO.println("  値: " + Child.parentValue);
        IO.println("6. Child.counterを読む");
        IO.println("  値: " + Child.counter);
        IO.println("7. new Circle()");
        new Circle();
        IO.println("8. Named.PREFIXを読む");
        IO.println("  値: " + Named.PREFIX.toString().trim());
    }
}
