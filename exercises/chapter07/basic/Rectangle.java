package chapter07.basic;

/**
 * 長方形を表すクラス
 * 
 * Shapeクラスを継承して、長方形に特有の実装を提供します。
 */
public class Rectangle extends Shape {
    // 長方形の幅
    private double width;
    // 長方形の高さ
    private double height;
    
    /**
     * コンストラクタ
     * @param width 長方形の幅
     * @param height 長方形の高さ
     */
    public Rectangle(double width, double height) {
        super("長方形");
        // TODO: widthとheightフィールドを初期化してください
        
    }
    
    /**
     * 長方形の面積を計算する
     * TODO: 長方形の面積の公式（幅 × 高さ）を使って実装してください
     * @return 長方形の面積
     */
    @Override
    public double calculateArea() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * 長方形の周囲の長さを計算する
     * TODO: 長方形の周囲の長さの公式（2 × (幅 + 高さ)）を使って実装してください
     * @return 長方形の周囲の長さ
     */
    @Override
    public double calculatePerimeter() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * 幅を取得する
     * @return 長方形の幅
     */
    public double getWidth() {
        return width;
    }
    
    /**
     * 高さを取得する
     * @return 長方形の高さ
     */
    public double getHeight() {
        return height;
    }
}