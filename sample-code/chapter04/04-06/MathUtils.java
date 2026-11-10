public class MathUtils {
    public static final double PI = 3.14159;  // 公開定数
    
    public static int add(int a, int b) {     // 公開ユーティリティメソッド
        return a + b;
    }
    
    public boolean isPositive(int number) {   // 公開インスタンスメソッド
        return number > 0;
    }
}

class MathUtilsTest {
    public static void main(String[] args) {
        // publicな定数へのアクセス
        IO.println("PI = " + MathUtils.PI);
        
        // publicなstaticメソッドの呼び出し
        int sum = MathUtils.add(10, 20);
        IO.println("10 + 20 = " + sum);
        
        // publicなインスタンスメソッドの呼び出し
        MathUtils utils = new MathUtils();
        IO.println("5は正の数？ " + utils.isPositive(5));
        IO.println("-3は正の数？ " + utils.isPositive(-3));
    }
}
