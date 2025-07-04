/**
 * 第5章 演習問題2: Circleクラスの解答例（Shape継承）
 */
public class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
    
    @Override
    public void displayInfo() {
        System.out.println("円:");
        System.out.println("半径: " + radius);
        super.displayInfo();
    }
    
    public double getRadius() { return radius; }
}