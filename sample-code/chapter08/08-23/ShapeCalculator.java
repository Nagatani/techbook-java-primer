// パターンマッチングによる処理
public class ShapeCalculator {
    public static double calculateArea(Shape shape) {
        return switch (shape) {
            case Circle(var radius) -> Math.PI * radius * radius;
            case Rectangle(var width, var height) -> width * height;
            case Triangle(var base, var height) -> 0.5 * base * height;
        };
    }
}
