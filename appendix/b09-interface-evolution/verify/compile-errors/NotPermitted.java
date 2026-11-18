/** sealedインターフェイスは、permitsに挙げていないクラスからは実装できない */
public class NotPermitted {
    sealed interface Shape permits Circle {
    }

    record Circle(double r) implements Shape {
    }

    record Square(double side) implements Shape {
    }
}
