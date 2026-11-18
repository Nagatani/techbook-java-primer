/** switchのパターンとレコードパターン（--release 21 からコンパイルできる） */
public class SinceRecordPattern {
    sealed interface Shape permits Circle, Square {}
    record Circle(double r) implements Shape {}
    record Square(double side) implements Shape {}

    static double area(Shape s) {
        return switch (s) {
            case Circle(double r) -> Math.PI * r * r;
            case Square(double side) -> side * side;
        };
    }
}
