package chapter07.solutions;

/**
 * 円（課題1の解答例）
 */
public class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        super("円");
        this.radius = radius;
    }

    @Override
    public double calculateArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }

    public double getRadius() {
        return radius;
    }
}
