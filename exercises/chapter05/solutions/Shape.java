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
        System.out.println("色: " + color);
        System.out.println("面積: " + String.format("%.2f", getArea()));
        System.out.println("周囲: " + String.format("%.2f", getPerimeter()));
    }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}