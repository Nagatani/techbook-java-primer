/**
 * リスト3-50
 * MathUtilsクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (2268行目)
 */

public class MathUtils {
    // privateコンストラクタでインスタンス化を防ぐ
    private MathUtils() {}

    public static double calculateCircleArea(double radius) {
        return Math.PI * radius * radius;
    }

    public static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}

// 使用例
double area = MathUtils.calculateCircleArea(5.0);