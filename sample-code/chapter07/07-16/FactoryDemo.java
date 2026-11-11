// 使用例
public class FactoryDemo {
    public static void main(String[] args) {
        // ファクトリーを使って図形を生成
        Shape circle = ShapeFactory.createShape("circle", 5.0);
        Shape rectangle = ShapeFactory.createShape("rectangle", 4.0, 6.0);
        Shape triangle = ShapeFactory.createShape("triangle", 3.0, 4.0);
        
        // 具体的なクラスを知らなくても使える
        Shape[] shapes = { circle, rectangle, triangle };
        
        for (Shape shape : shapes) {
            shape.draw();
            IO.println("面積: " + shape.getArea());
            IO.println();
        }
    }
}
