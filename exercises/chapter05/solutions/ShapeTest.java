/**
 * 図形のクラスを試すプログラム（課題2の解答例）
 */
public class ShapeTest {
    public static void main(String[] args) {
        IO.println("=== 図形計算システム ===");

        // Shape型の配列に、RectangleとCircleを一緒に入れられる
        Shape[] shapes = {
            new Rectangle("赤", 6.0, 4.0),
            new Circle("青", 5.0)
        };

        double total = 0.0;
        for (int i = 0; i < shapes.length; i++) {
            IO.print("図形" + (i + 1) + ": ");
            shapes[i].showInfo();
            IO.println();
            total += shapes[i].getArea();
        }
        IO.println("全図形の合計面積: " + "%.2f".formatted(total));
    }
}
