/**
 * 第3章 演習問題5: RectangleTestクラスの解答例
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
        IO.println("=== 第3章 演習問題5: Rectangle クラスのテスト ===");
        
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
        
        IO.println("\n=== テスト完了 ===");
    }
    
    /**
     * コンストラクタのテスト
     */
    private static void testConstructors() {
        IO.println("\n--- コンストラクタのテスト ---");
        
        // 正方形用コンストラクタ
        Rectangle square = new Rectangle(5.0);
        IO.println("正方形（5.0）: " + square.toString());
        
        // 長方形用コンストラクタ
        Rectangle rectangle = new Rectangle(8.0, 3.0);
        IO.println("長方形（8.0, 3.0）: " + rectangle.toString());
        
        // デフォルトコンストラクタ
        Rectangle defaultRect = new Rectangle();
        IO.println("デフォルト: " + defaultRect.toString());
        
        // コピーコンストラクタ
        Rectangle copyRect = new Rectangle(square);
        IO.println("コピー: " + copyRect.toString());
        
        // 正方形が正しく作成されているか確認
        IO.println("正方形判定: " + square.isSquare());
        IO.println("長方形判定: " + rectangle.isSquare());
    }
    
    /**
     * オーバーロードメソッドのテスト
     */
    private static void testOverloadedMethods() {
        IO.println("\n--- オーバーロードメソッドのテスト ---");
        
        Rectangle rect = new Rectangle(10.0, 5.0);
        IO.println("初期状態: " + rect.toString());
        
        // setSize(double) - 正方形に変更
        rect.setSize(7.0);
        IO.println("setSize(7.0)後: " + rect.toString());
        
        // setSize(double, double) - 長方形に変更
        rect.setSize(12.0, 4.0);
        IO.println("setSize(12.0, 4.0)後: " + rect.toString());
        
        // setSize(Rectangle) - 別のオブジェクトからコピー
        Rectangle source = new Rectangle(6.0, 9.0);
        rect.setSize(source);
        IO.println("setSize(Rectangle)後: " + rect.toString());
        
        // 個別の設定メソッド
        rect.setWidth(15.0);
        rect.setHeight(8.0);
        IO.println("個別設定後: " + rect.toString());
    }
    
    /**
     * 計算メソッドのテスト
     */
    private static void testCalculationMethods() {
        IO.println("\n--- 計算メソッドのテスト ---");
        
        Rectangle rect = new Rectangle(6.0, 8.0);
        
        IO.println("長方形: " + rect.toString());
        IO.println("面積: %.2f".formatted(rect.getArea()));
        IO.println("周囲: %.2f".formatted(rect.getPerimeter()));
        IO.println("対角線: %.2f".formatted(rect.getDiagonal()));
        IO.println("アスペクト比: %.2f".formatted(rect.getAspectRatio()));
        
        // 計算の正確性確認
        double expectedArea = 6.0 * 8.0;
        double expectedPerimeter = 2 * (6.0 + 8.0);
        double expectedDiagonal = Math.sqrt(6.0 * 6.0 + 8.0 * 8.0);
        
        IO.println("\n計算結果の検証:");
        IO.println("面積正確性: " + (Math.abs(rect.getArea() - expectedArea) < 1e-10));
        IO.println("周囲正確性: " + (Math.abs(rect.getPerimeter() - expectedPerimeter) < 1e-10));
        IO.println("対角線正確性: " + (Math.abs(rect.getDiagonal() - expectedDiagonal) < 1e-10));
    }
    
    /**
     * 比較・判定メソッドのテスト
     */
    private static void testComparisonMethods() {
        IO.println("\n--- 比較・判定メソッドのテスト ---");
        
        Rectangle square = new Rectangle(5.0);          // 正方形
        Rectangle portrait = new Rectangle(4.0, 6.0);   // 縦長
        Rectangle landscape = new Rectangle(8.0, 3.0);  // 横長
        
        IO.println("正方形: " + square.toString());
        IO.println("  正方形判定: " + square.isSquare());
        IO.println("  縦長判定: " + square.isPortrait());
        IO.println("  横長判定: " + square.isLandscape());
        
        IO.println("\n縦長長方形: " + portrait.toString());
        IO.println("  正方形判定: " + portrait.isSquare());
        IO.println("  縦長判定: " + portrait.isPortrait());
        IO.println("  横長判定: " + portrait.isLandscape());
        
        IO.println("\n横長長方形: " + landscape.toString());
        IO.println("  正方形判定: " + landscape.isSquare());
        IO.println("  縦長判定: " + landscape.isPortrait());
        IO.println("  横長判定: " + landscape.isLandscape());
        
        // 比較メソッドのテスト
        IO.println("\n長方形の比較:");
        IO.println("正方形 vs 縦長 (同じサイズ): " + square.isSameSize(portrait));
        IO.println("正方形 vs 縦長 (同じ面積): " + square.isSameArea(portrait));
        IO.println("正方形 > 横長 (面積): " + square.isLargerThan(landscape));
    }
    
    /**
     * 実践的な使用例のテスト
     */
    private static void testPracticalUsage() {
        IO.println("\n--- 実践的な使用例のテスト ---");
        
        IO.println("=== 長方形計算プログラム ===");
        
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
            IO.println("\n図形" + (i + 1) + ":");
            rect.displayDetailedInfo();
            totalArea += rect.getArea();
        }
        
        IO.println("%n全図形の合計面積: %.2f".formatted(totalArea));
        
        // 最大の長方形を見つける
        Rectangle largest = rectangles[0];
        for (Rectangle rect : rectangles) {
            if (rect.isLargerThan(largest)) {
                largest = rect;
            }
        }
        IO.println("最大の長方形: " + largest.toString());
        
        // 変形操作のデモ
        IO.println("\n=== 変形操作のデモ ===");
        Rectangle demo = new Rectangle(4.0, 3.0);
        IO.println("初期状態: " + demo.toString());
        
        demo.scale(2.0);
        IO.println("2倍拡大後: " + demo.toString());
        
        demo.rotate();
        IO.println("90度回転後: " + demo.toString());
    }
    
    /**
     * エラーケースのテスト
     */
    private static void testErrorCases() {
        IO.println("\n--- エラーケースのテスト ---");
        
        // 負の値の処理
        Rectangle negativeRect = new Rectangle(-5.0, -3.0);
        IO.println("負の値で作成: " + negativeRect.toString());
        
        // ゼロ値の処理
        Rectangle zeroRect = new Rectangle(0.0, 5.0);
        IO.println("ゼロ値で作成: " + zeroRect.toString());
        IO.println("面積: " + zeroRect.getArea());
        IO.println("周囲: " + zeroRect.getPerimeter());
        
        // 極端に大きな値
        Rectangle hugeRect = new Rectangle(Double.MAX_VALUE / 2, 1.0);
        IO.println("巨大な値で作成: 幅=" + hugeRect.getWidth());
        
        // 極端に小さな値
        Rectangle tinyRect = new Rectangle(Double.MIN_VALUE, Double.MIN_VALUE);
        IO.println("極小値で作成: " + tinyRect.toString());
        
        // 無効な拡大率
        Rectangle testRect = new Rectangle(5.0, 5.0);
        IO.println("拡大前: " + testRect.toString());
        testRect.scale(-1.0);  // 負の拡大率
        IO.println("負の拡大率適用後: " + testRect.toString());
        testRect.scale(0.0);   // ゼロ拡大率
        IO.println("ゼロ拡大率適用後: " + testRect.toString());
    }
    
    /**
     * 静的メソッドのテスト
     */
    private static void testStaticMethods() {
        IO.println("\n--- 静的メソッドのテスト ---");
        
        Rectangle rect1 = new Rectangle(5.0, 4.0);
        Rectangle rect2 = new Rectangle(3.0, 6.0);
        
        IO.println("長方形1: " + rect1.toString());
        IO.println("長方形2: " + rect2.toString());
        
        double totalArea = Rectangle.getTotalArea(rect1, rect2);
        IO.println("合計面積: " + totalArea);
        
        Rectangle larger = Rectangle.getLarger(rect1, rect2);
        IO.println("より大きな長方形: " + larger.toString());
    }
}