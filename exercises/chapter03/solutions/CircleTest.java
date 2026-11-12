/**
 * Circleを試すプログラム（課題3の解答例）
 */
public class CircleTest {
    public static void main(String[] args) {
        IO.println("=== 円の計算プログラム ===");

        Circle circle1 = new Circle();
        circle1.radius = 5.0;
        Circle circle2 = new Circle();
        circle2.radius = 3.0;

        IO.println("[円1]");
        circle1.displayInfo();
        IO.println();
        IO.println("[円2]");
        circle2.displayInfo();
        IO.println();

        double diff = circle1.calculateArea() - circle2.calculateArea();
        IO.println("面積の差: " + "%.2f".formatted(diff) + "cm²");
    }
}
