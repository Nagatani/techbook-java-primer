/**
 * リスト4-5
 * MathUtilsクラス
 * 
 * 元ファイル: chapter04-classes-and-instances.md (237行目)
 */

public class MathUtils {
    public static final double PI = 3.14159;  // 公開定数
    
    public static int add(int a, int b) {     // 公開ユーティリティメソッド
        return a + b;
    }
    
    public boolean isPositive(int number) {   // 公開インスタンスメソッド
        return number > 0;
    }
}