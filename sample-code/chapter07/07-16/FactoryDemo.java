// 使用例
public class FactoryDemo {
    public static void main(String[] args) {
        // ファクトリーを使って図形を生成
        Figure circle = FigureFactory.createFigure("circle", 5.0);
        Figure rectangle = FigureFactory.createFigure("rectangle", 4.0, 6.0);
        Figure triangle = FigureFactory.createFigure("triangle", 3.0, 4.0);
        
        // 具体的なクラスを知らなくても使える
        Figure[] figures = { circle, rectangle, triangle };
        
        for (Figure figure : figures) {
            figure.draw();
            IO.println("面積: " + figure.getArea());
            IO.println();
        }
    }
}
