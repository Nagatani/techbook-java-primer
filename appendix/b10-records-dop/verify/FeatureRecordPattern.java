/**
 * switchのパターンマッチングとレコードパターンが使える版を確かめる（javac --release 20 と 21 で比べる）。
 */
public class FeatureRecordPattern {
    sealed interface Shape permits Circle, Square { }

    record Circle(double radius) implements Shape { }

    record Square(double side) implements Shape { }

    // switchのパターンマッチング（型パターン）
    static String name(Shape shape) {
        return switch (shape) {
            case Circle c -> "circle";
            case Square s -> "square";
        };
    }

    // レコードパターン
    static double radius(Object o) {
        if (o instanceof Circle(double r)) {
            return r;
        }
        return 0;
    }
}
