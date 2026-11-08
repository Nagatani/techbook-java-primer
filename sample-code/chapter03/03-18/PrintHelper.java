public class PrintHelper {
    // 文字列を出力
    public void print(String message) {
        System.out.println(message);
    }
    
    // 整数を出力
    public void print(int number) {
        System.out.println("数値: " + number);
    }
    
    // 2つの値を出力
    public void print(String label, int value) {
        System.out.println(label + ": " + value);
    }
}
