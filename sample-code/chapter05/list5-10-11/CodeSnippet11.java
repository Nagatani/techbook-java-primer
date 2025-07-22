/**
 * リスト5-11
 * Rectangleクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (647行目)
 */

// 改善されたコード：継承ではなくインターフェイスを使用
public interface Shape {
    int getArea();
}

public class Rectangle implements Shape {
    private final int width;
    private final int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getArea() {
        return width * height;
    }
}

public class Square implements Shape {
    private final int size;
    
    public Square(int size) {
        this.size = size;
    }
    
    @Override
    public int getArea() {
        return size * size;
    }
}