/**
 * 長方形を表すクラス（課題5の解答例）
 */
public class Rectangle {
    // インスタンス変数を宣言する
    double width;
    double height;
    
    // 正方形用のコンストラクター（一辺の長さのみ）
    public Rectangle(double size) {
        this(size, size);
    }
    
    // 長方形用のコンストラクター（幅と高さ）
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    // 正方形用のsetSizeメソッド（一辺の長さのみ）
    public void setSize(double size) {
        this.width = size;
        this.height = size;
    }
    
    // 長方形用のsetSizeメソッド（幅と高さ）
    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    // 面積を計算するメソッド
    public double getArea() {
        return width * height;
    }
    
    // 周囲を計算するメソッド
    public double getPerimeter() {
        return 2 * (width + height);
    }
    
    // 情報表示メソッド
    public void showInfo() {
        IO.println("幅: " + width + ", 高さ: " + height);
        IO.println("面積: " + getArea() + ", 周囲: " + getPerimeter());
    }
}