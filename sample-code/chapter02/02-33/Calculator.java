public class Calculator {
    // メソッドの定義
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static double divide(double a, double b) {
        if (b == 0) {
            IO.println("ゼロ除算エラー");
            return 0;
        }
        return a / b;
    }
    
    public static void main(String[] args) {
        int result = add(10, 5);
        IO.println("結果: " + result);
        
        double divResult = divide(10.0, 3.0);
        IO.println("除算結果: " + divResult);
    }
}
