/**
 * 第5章 演習問題2: Rectangleクラスの解答例（Shape継承）
 */
public class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
    
    @Override
    public double getPerimeter() {
        return 2 * (width + height);
    }
    
    @Override
    public void displayInfo() {
        System.out.println("長方形:");
        System.out.println("幅: " + width + ", 高さ: " + height);
        super.displayInfo();
    }
    
    public double getWidth() { return width; }
    public double getHeight() { return height; }
}