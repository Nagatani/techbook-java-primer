/**
 * 第3章 演習問題3: Circleクラスのテスト - 解答例
 *
 * 学習ポイント:
 * - 同じクラスから、フィールドの値が違う複数のオブジェクトを作る
 * - オブジェクトごとにメソッドの結果が変わることを確かめる
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

        double difference = circle1.calculateArea() - circle2.calculateArea();
        IO.println("面積の差: " + "%.2f".formatted(difference) + "cm²");
    }
}
