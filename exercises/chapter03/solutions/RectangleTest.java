/**
 * 第3章 演習問題2: RectangleTestクラスの解答例
 * 
 * 【テストの目的】
 * - メソッドオーバーロードの動作確認
 * - 計算メソッドの正確性確認
 * - エラーケース（負の値など）の処理確認
 * - 実践的な使用例の提示
 * 
 * 【デバッグのコツ】
 * 1. 浮動小数点計算の精度に注意
 * 2. 正方形と長方形の区別を明確にする
 * 3. エラーケースでの挙動を確認
 * 4. オーバーロードメソッドの呼び出しを確認
 */
public class RectangleTest {
    public static void main(String[] args) {
        System.out.println("=== 第3章 演習問題2: Rectangle クラスのテスト ===");
        
        // 【基本テスト】コンストラクタのテスト
        testConstructors();
        
        // 【基本テスト】オーバーロードメソッドのテスト
        testOverloadedMethods();
        
        // 【応用テスト】計算メソッドのテスト
        testCalculationMethods();
        
        // 【発展テスト】比較・判定メソッドのテスト
        testComparisonMethods();
        
        // 【実践テスト】実際の使用例
        testPracticalUsage();
        
        // 【エラーテスト】エラーケースの処理
        testErrorCases();
        
        System.out.println("\n=== テスト完了 ===");
    }
    
    /**
     * コンストラクタのテスト
     */
    private static void testConstructors() {
        System.out.println("\n--- コンストラクタのテスト ---");
        
        // 正方形用コンストラクタ
        Rectangle square = new Rectangle(5.0);
        System.out.println("正方形（5.0）: " + square.toString());
        
        // 長方形用コンストラクタ
        Rectangle rectangle = new Rectangle(8.0, 3.0);
        System.out.println("長方形（8.0, 3.0）: " + rectangle.toString());
        
        // デフォルトコンストラクタ
        Rectangle defaultRect = new Rectangle();
        System.out.println("デフォルト: " + defaultRect.toString());
        
        // コピーコンストラクタ
        Rectangle copyRect = new Rectangle(square);
        System.out.println("コピー: " + copyRect.toString());
        
        // 正方形が正しく作成されているか確認
        System.out.println("正方形判定: " + square.isSquare());
        System.out.println("長方形判定: " + rectangle.isSquare());
    }
    
    /**
     * オーバーロードメソッドのテスト
     */
    private static void testOverloadedMethods() {
        System.out.println("\n--- オーバーロードメソッドのテスト ---");
        
        Rectangle rect = new Rectangle(10.0, 5.0);
        System.out.println("初期状態: " + rect.toString());
        
        // setSize(double) - 正方形に変更
        rect.setSize(7.0);
        System.out.println("setSize(7.0)後: " + rect.toString());
        
        // setSize(double, double) - 長方形に変更
        rect.setSize(12.0, 4.0);
        System.out.println("setSize(12.0, 4.0)後: " + rect.toString());
        
        // setSize(Rectangle) - 別のオブジェクトからコピー
        Rectangle source = new Rectangle(6.0, 9.0);
        rect.setSize(source);
        System.out.println("setSize(Rectangle)後: " + rect.toString());
        
        // 個別の設定メソッド
        rect.setWidth(15.0);
        rect.setHeight(8.0);
        System.out.println("個別設定後: " + rect.toString());
    }
    
    /**
     * 計算メソッドのテスト
     */
    private static void testCalculationMethods() {
        System.out.println("\n--- 計算メソッドのテスト ---");
        
        Rectangle rect = new Rectangle(6.0, 8.0);
        
        System.out.println("長方形: " + rect.toString());
        System.out.printf("面積: %.2f%n", rect.getArea());
        System.out.printf("周囲: %.2f%n", rect.getPerimeter());
        System.out.printf("対角線: %.2f%n", rect.getDiagonal());
        System.out.printf("アスペクト比: %.2f%n", rect.getAspectRatio());
        
        // 計算の正確性確認
        double expectedArea = 6.0 * 8.0;
        double expectedPerimeter = 2 * (6.0 + 8.0);
        double expectedDiagonal = Math.sqrt(6.0 * 6.0 + 8.0 * 8.0);
        
        System.out.println("\n計算結果の検証:");
        System.out.println("面積正確性: " + (Math.abs(rect.getArea() - expectedArea) < 1e-10));
        System.out.println("周囲正確性: " + (Math.abs(rect.getPerimeter() - expectedPerimeter) < 1e-10));
        System.out.println("対角線正確性: " + (Math.abs(rect.getDiagonal() - expectedDiagonal) < 1e-10));
    }
    
    /**
     * 比較・判定メソッドのテスト
     */
    private static void testComparisonMethods() {
        System.out.println("\n--- 比較・判定メソッドのテスト ---");
        
        Rectangle square = new Rectangle(5.0);          // 正方形
        Rectangle portrait = new Rectangle(4.0, 6.0);   // 縦長
        Rectangle landscape = new Rectangle(8.0, 3.0);  // 横長
        
        System.out.println("正方形: " + square.toString());
        System.out.println("  正方形判定: " + square.isSquare());
        System.out.println("  縦長判定: " + square.isPortrait());
        System.out.println("  横長判定: " + square.isLandscape());
        
        System.out.println("\n縦長長方形: " + portrait.toString());
        System.out.println("  正方形判定: " + portrait.isSquare());
        System.out.println("  縦長判定: " + portrait.isPortrait());
        System.out.println("  横長判定: " + portrait.isLandscape());
        
        System.out.println("\n横長長方形: " + landscape.toString());
        System.out.println("  正方形判定: " + landscape.isSquare());
        System.out.println("  縦長判定: " + landscape.isPortrait());
        System.out.println("  横長判定: " + landscape.isLandscape());
        
        // 比較メソッドのテスト
        System.out.println("\n長方形の比較:");
        System.out.println("正方形 vs 縦長 (同じサイズ): " + square.isSameSize(portrait));
        System.out.println("正方形 vs 縦長 (同じ面積): " + square.isSameArea(portrait));
        System.out.println("正方形 > 横長 (面積): " + square.isLargerThan(landscape));
    }
    
    /**
     * 実践的な使用例のテスト
     */
    private static void testPracticalUsage() {
        System.out.println("\n--- 実践的な使用例のテスト ---");
        
        System.out.println("=== 長方形計算プログラム ===");
        
        // 複数の長方形を作成
        Rectangle[] rectangles = {
            new Rectangle(5.0),         // 正方形
            new Rectangle(8.0, 3.0),    // 長方形
            new Rectangle(6.0, 6.0),    // 正方形
            new Rectangle(10.0, 2.0)    // 長方形
        };
        
        double totalArea = 0;
        for (int i = 0; i < rectangles.length; i++) {
            Rectangle rect = rectangles[i];
            System.out.println("\n図形" + (i + 1) + ":");
            rect.displayDetailedInfo();
            totalArea += rect.getArea();
        }
        
        System.out.printf("%n全図形の合計面積: %.2f%n", totalArea);
        
        // 最大の長方形を見つける
        Rectangle largest = rectangles[0];
        for (Rectangle rect : rectangles) {
            if (rect.isLargerThan(largest)) {
                largest = rect;
            }
        }
        System.out.println("最大の長方形: " + largest.toString());
        
        // 変形操作のデモ
        System.out.println("\n=== 変形操作のデモ ===");
        Rectangle demo = new Rectangle(4.0, 3.0);
        System.out.println("初期状態: " + demo.toString());
        
        demo.scale(2.0);
        System.out.println("2倍拡大後: " + demo.toString());
        
        demo.rotate();
        System.out.println("90度回転後: " + demo.toString());
    }
    
    /**
     * エラーケースのテスト
     */
    private static void testErrorCases() {
        System.out.println("\n--- エラーケースのテスト ---");
        
        // 負の値の処理
        Rectangle negativeRect = new Rectangle(-5.0, -3.0);
        System.out.println("負の値で作成: " + negativeRect.toString());
        
        // ゼロ値の処理
        Rectangle zeroRect = new Rectangle(0.0, 5.0);
        System.out.println("ゼロ値で作成: " + zeroRect.toString());
        System.out.println("面積: " + zeroRect.getArea());
        System.out.println("周囲: " + zeroRect.getPerimeter());
        
        // 極端に大きな値
        Rectangle hugeRect = new Rectangle(Double.MAX_VALUE / 2, 1.0);
        System.out.println("巨大な値で作成: 幅=" + hugeRect.getWidth());
        
        // 極端に小さな値
        Rectangle tinyRect = new Rectangle(Double.MIN_VALUE, Double.MIN_VALUE);
        System.out.println("極小値で作成: " + tinyRect.toString());
        
        // 無効な拡大率
        Rectangle testRect = new Rectangle(5.0, 5.0);
        System.out.println("拡大前: " + testRect.toString());
        testRect.scale(-1.0);  // 負の拡大率
        System.out.println("負の拡大率適用後: " + testRect.toString());
        testRect.scale(0.0);   // ゼロ拡大率
        System.out.println("ゼロ拡大率適用後: " + testRect.toString());
    }
    
    /**
     * 静的メソッドのテスト
     */
    private static void testStaticMethods() {
        System.out.println("\n--- 静的メソッドのテスト ---");
        
        Rectangle rect1 = new Rectangle(5.0, 4.0);
        Rectangle rect2 = new Rectangle(3.0, 6.0);
        
        System.out.println("長方形1: " + rect1.toString());
        System.out.println("長方形2: " + rect2.toString());
        
        double totalArea = Rectangle.getTotalArea(rect1, rect2);
        System.out.println("合計面積: " + totalArea);
        
        Rectangle larger = Rectangle.getLarger(rect1, rect2);
        System.out.println("より大きな長方形: " + larger.toString());
    }
}