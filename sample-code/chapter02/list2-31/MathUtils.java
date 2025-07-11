/**
 * リスト2-31
 * MathUtilsクラス
 * 
 * 元ファイル: chapter02-getting-started.md (1071行目)
 */

public class MathUtils {
    // クラス変数
    public static final double PI = 3.14159265359;
    
    // クラスメソッド
    public static double circleArea(double radius) {
        return PI * radius * radius;
    }
}

// 使用例（インスタンス化不要）
double area = MathUtils.circleArea(5.0);