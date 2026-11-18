/** sealedな型のswitchで、許可したサブクラスを1つでも扱い忘れるとコンパイルエラーになる */
public class NonExhaustiveSwitch {
    sealed interface Shape permits Circle, Square, Rect { }

    record Circle(double radius) implements Shape { }

    record Square(double side) implements Shape { }

    record Rect(double width, double height) implements Shape { }

    static double area(Shape shape) {
        return switch (shape) {
            case Circle(double r) -> Math.PI * r * r;
            case Square(double s) -> s * s;
        };
    }
}
