package chapter07.solutions;

/**
 * 図形の抽象クラスを試すプログラム（課題1の解答例）
 */
public class ShapeDemo {
    public static void main(String[] args) {
        IO.println("=== 図形デモプログラム ===\n");

        Shape circle = new Circle(5.0);
        Shape rectangle = new Rectangle(4.0, 6.0);
        // Shape shape = new Shape("図形"); // 抽象クラスはnewできない（コンパイルエラー）

        circle.displayInfo();
        IO.println();
        rectangle.displayInfo();

        IO.println("\n=== ポリモーフィズムのデモ ===");
        Shape[] shapes = {circle, rectangle, new Circle(1.0), new Rectangle(2.0, 2.0)};
        for (Shape shape : shapes) {
            IO.println(shape.getName() + ": 面積 " + "%.2f".formatted(shape.calculateArea()));
        }

        IO.println("\n=== 面積の合計計算 ===");
        double totalArea = 0;
        for (Shape shape : shapes) {
            totalArea += shape.calculateArea();
        }
        IO.println("全図形の面積の合計: " + "%.2f".formatted(totalArea));
    }
}
