/**
 * リストAC-11
 * コードスニペット
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (311行目)
 */

interface Shape {
    int getArea();
    Shape resize(double factor);
}

class Rectangle implements Shape {
    private final int width, height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
    
    public Shape resize(double factor) {
        return new Rectangle(
            (int)(width * factor), 
            (int)(height * factor)
        );
    }
}

class Square implements Shape {
    private final int side;
    
    public Square(int side) {
        this.side = side;
    }
    
    public int getArea() {
        return side * side;
    }
    
    public Shape resize(double factor) {
        return new Square((int)(side * factor));
    }
}