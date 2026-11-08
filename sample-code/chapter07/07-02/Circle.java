// Circle.java
public class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        super("円"); // 親クラスのコンストラクタを呼び出す
        this.radius = radius;
    }

    // 抽象メソッドの実装
    @Override
    public double getArea() {
        return this.radius * this.radius * Math.PI;
    }
}
