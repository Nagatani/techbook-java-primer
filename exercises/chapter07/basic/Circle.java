package chapter07.basic;

/**
 * 円を表すクラス
 * 
 * Shapeクラスを継承して、円に特有の実装を提供します。
 */
public class Circle extends Shape {
    // 円の半径
    private double radius;
    
    /**
     * コンストラクタ
     * @param radius 円の半径
     */
    public Circle(double radius) {
        super("円");
        // TODO: radiusフィールドを初期化してください
        
    }
    
    /**
     * 円の面積を計算する
     * TODO: 円の面積の公式（π × 半径 × 半径）を使って実装してください
     * ヒント: Math.PIを使用できます
     * @return 円の面積
     */
    @Override
    public double calculateArea() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * 円の周囲の長さ（円周）を計算する
     * TODO: 円周の公式（2 × π × 半径）を使って実装してください
     * @return 円の周囲の長さ
     */
    @Override
    public double calculatePerimeter() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * 半径を取得する
     * @return 円の半径
     */
    public double getRadius() {
        return radius;
    }
}