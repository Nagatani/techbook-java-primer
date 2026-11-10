public class DebugExample {
    public static void main(String[] args) {
        int a = 10;
        int b = 20;
        
        IO.println("a = " + a);  // デバッグ出力
        IO.println("b = " + b);  // デバッグ出力
        
        int sum = a + b;
        IO.println("sum = " + sum);  // 計算結果を確認
    }
}
