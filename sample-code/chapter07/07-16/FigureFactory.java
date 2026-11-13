import java.util.*;

// 共通のインターフェイス
interface Figure {
    void draw();
    double getArea();
}

// 具体的な実装クラス
class CircleFigure implements Figure {
    private double radius;
    
    public CircleFigure(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        IO.println("○ 半径 " + radius + " の円を描画");
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
}

class RectangleFigure implements Figure {
    private double width;
    private double height;
    
    public RectangleFigure(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void draw() {
        IO.println("□ " + width + " x " + height + " の長方形を描画");
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
}

class TriangleFigure implements Figure {
    private double base;
    private double height;
    
    public TriangleFigure(double base, double height) {
        this.base = base;
        this.height = height;
    }
    
    @Override
    public void draw() {
        IO.println("△ 底辺 " + base + ", 高さ " + height + " の三角形を描画");
    }
    
    @Override
    public double getArea() {
        return base * height / 2;
    }
}

// ファクトリクラス
public class FigureFactory {
    public static Figure createFigure(String type, double... params) {
        switch (type.toLowerCase()) {
            case "circle":
                if (params.length != 1) {
                    throw new IllegalArgumentException("円には半径が必要です");
                }
                return new CircleFigure(params[0]);
                
            case "rectangle":
                if (params.length != 2) {
                    throw new IllegalArgumentException("長方形には幅と高さが必要です");
                }
                return new RectangleFigure(params[0], params[1]);
                
            case "triangle":
                if (params.length != 2) {
                    throw new IllegalArgumentException("三角形には底辺と高さが必要です");
                }
                return new TriangleFigure(params[0], params[1]);
                
            default:
                throw new IllegalArgumentException("不明な図形タイプ: " + type);
        }
    }
}
