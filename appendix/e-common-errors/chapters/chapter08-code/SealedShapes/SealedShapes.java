public class SealedShapes {
    // 実装を増やすときは、permitsにも書き足す
    sealed interface Shape permits Circle, Square, Triangle {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    // 実装を増やしたら、defaultのないswitchにもcaseを書き足す
    static double area(Shape shape) {
        return switch (shape) {
            case Circle c -> Math.PI * c.radius() * c.radius();
            case Square s -> s.side() * s.side();
            case Triangle t -> t.base() * t.height() / 2;
        };
    }

    public static void main(String[] args) {
        IO.println(area(new Square(3)));
        IO.println(area(new Triangle(4, 5)));
    }
}
