/**
 * sealedなインターフェイスとレコードの組み合わせ、switchのパターンマッチングを観察する。
 *
 *   javac SealedShapes.java
 *   javap -v 'SealedShapes$Shape'          （PermittedSubclasses属性）
 *   javap -c -p SealedShapes                （typeSwitchのinvokedynamicとMatchException）
 *   java SealedShapes
 */
public class SealedShapes {
    sealed interface Shape permits Circle, Square, Rect { }

    record Circle(double radius) implements Shape {
        // レコードパターンがアクセサーを呼ぶことを確かめるため、呼ばれたら表示する
        public double radius() {
            IO.println("  radius()が呼ばれた");
            return radius;
        }
    }

    record Square(double side) implements Shape { }

    record Rect(double width, double height) implements Shape { }

    // 3つの型をすべて扱うので、defaultがなくても網羅的
    static double area(Shape shape) {
        return switch (shape) {
            case Circle(double r) -> Math.PI * r * r;
            case Square(double s) -> s * s;
            case Rect(double w, double h) -> w * h;
        };
    }

    public static void main(String[] args) {
        IO.println("permitted: " + java.util.Arrays.stream(Shape.class.getPermittedSubclasses())
                .map(Class::getSimpleName).toList());
        IO.println("Circle: " + area(new Circle(1)));
        IO.println("Square: " + area(new Square(2)));
        IO.println("Rect: " + area(new Rect(2, 3)));
        try {
            area(null);
        } catch (NullPointerException e) {
            IO.println("null: NullPointerException");
        }
    }
}
