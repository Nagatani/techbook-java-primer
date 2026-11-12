/**
 * 円（課題2の解答例）
 */
public class Circle extends Shape {
    private double radius;

    public Circle(String color, double radius) {
        super("円", color);
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}
