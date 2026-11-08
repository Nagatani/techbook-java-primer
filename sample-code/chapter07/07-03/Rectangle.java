// Rectangle.java
public class Rectangle extends Shape {
    private double width;
    private double height;

    public Rectangle(double width, double height) {
        super("長方形");
        this.width = width;
        this.height = height;
    }

    // 抽象メソッドの実装
    @Override
    public double getArea() {
        return this.width * this.height;
    }
}
