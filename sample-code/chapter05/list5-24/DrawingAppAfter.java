/**
 * リスト5-24
 * DrawingAppAfterクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (1204行目)
 */

// 抽象基底クラス
abstract class Shape {
    abstract double calculateArea();
    abstract void draw();
}

// 円クラス
class Circle extends Shape {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    double calculateArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    void draw() {
        System.out.println("円を描画：半径 = " + radius);
    }
}

// 長方形クラス
class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    double calculateArea() {
        return width * height;
    }
    
    @Override
    void draw() {
        System.out.println("長方形を描画：幅 = " + width + ", 高さ = " + height);
    }
}

// 三角形クラス
class Triangle extends Shape {
    private double base;
    private double height;
    
    public Triangle(double base, double height) {
        this.base = base;
        this.height = height;
    }
    
    @Override
    double calculateArea() {
        return 0.5 * base * height;
    }
    
    @Override
    void draw() {
        System.out.println("三角形を描画：底辺 = " + base + ", 高さ = " + height);
    }
}

// 使用例
public class DrawingAppAfter {
    public static void main(String[] args) {
        Shape[] shapes = {
            new Circle(5.0),
            new Rectangle(10.0, 20.0),
            new Triangle(15.0, 8.0)
        };
        
        // すべての図形を描画（型を意識しない）
        for (Shape shape : shapes) {
            shape.draw();
            System.out.println("面積: " + shape.calculateArea());
        }
        
        // 新しい図形（五角形）を追加しても、このコードは変更不要
    }
}

// 新しい図形の追加が容易
class Pentagon extends Shape {
    private double side;
    
    public Pentagon(double side) {
        this.side = side;
    }
    
    @Override
    double calculateArea() {
        return 0.25 * Math.sqrt(5 * (5 + 2 * Math.sqrt(5))) * side * side;
    }
    
    @Override
    void draw() {
        System.out.println("五角形を描画：一辺 = " + side);
    }
}