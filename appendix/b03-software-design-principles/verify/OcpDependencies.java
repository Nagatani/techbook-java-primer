import java.util.List;

/**
 * 開放閉鎖の原則：計算するクラスが、どの型に依存しているかをバイトコードで見る。
 * 実行: javac OcpDependencies.java && java OcpDependencies
 * 観察: javap -c -p 'OcpDependencies$TypeCheckCalculator'
 *       javap -c -p 'OcpDependencies$ShapeCalculator'
 */
public class OcpDependencies {
    interface Shape {
        double area();
    }

    record Rectangle(double width, double height) implements Shape {
        public double area() {
            return width * height;
        }
    }

    record Circle(double radius) implements Shape {
        public double area() {
            return Math.PI * radius * radius;
        }
    }

    // あとから足した図形。ShapeCalculatorは変えていない
    record Triangle(double base, double height) implements Shape {
        public double area() {
            return base * height / 2;
        }
    }

    // 種類を調べて分岐する：図形を足すたびにこのクラスを書き換える
    static class TypeCheckCalculator {
        double area(Object shape) {
            if (shape instanceof Rectangle r) {
                return r.width() * r.height();
            } else if (shape instanceof Circle c) {
                return Math.PI * c.radius() * c.radius();
            }
            throw new IllegalArgumentException("知らない図形: " + shape);
        }
    }

    // インターフェイスだけを知っている：図形を足してもこのクラスは変わらない
    static class ShapeCalculator {
        double total(List<? extends Shape> shapes) {
            double sum = 0;
            for (Shape s : shapes) {
                sum += s.area();
            }
            return sum;
        }
    }

    public static void main(String[] args) {
        List<Shape> shapes = List.of(new Rectangle(5, 3), new Circle(1), new Triangle(6, 8));
        IO.println("ShapeCalculator: %.4f".formatted(new ShapeCalculator().total(shapes)));
        try {
            new TypeCheckCalculator().area(new Triangle(6, 8));
        } catch (IllegalArgumentException e) {
            IO.println("TypeCheckCalculator: " + e.getMessage());
        }
    }
}
