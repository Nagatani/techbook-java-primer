/**
 * 第5章 演習問題2: Shapeクラスの解答例（抽象クラス）
 */
public abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    public abstract double getArea();
    public abstract double getPerimeter();
    
    public void displayInfo() {
        IO.println("色: " + color);
        IO.println("面積: " + String.format("%.2f", getArea()));
        IO.println("周囲: " + String.format("%.2f", getPerimeter()));
    }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}