/**
 * 円を表すクラス（課題3の解答例）
 */
public class Circle {
    double radius;

    // 面積 = π × 半径 × 半径
    double calculateArea() {
        return Math.PI * radius * radius;
    }

    // 円周 = 2 × π × 半径
    double calculateCircumference() {
        return 2 * Math.PI * radius;
    }

    void displayInfo() {
        IO.println("円の情報:");
        IO.println("半径: " + radius + "cm");
        IO.println("面積: " + "%.2f".formatted(calculateArea()) + "cm²");
        IO.println("円周: " + "%.2f".formatted(calculateCircumference()) + "cm");
    }
}
