public class PrintHelper {
    // 文字列を出力
    public void print(String message) {
        IO.println(message);
    }
    
    // 整数を出力
    public void print(int number) {
        IO.println("数値: " + number);
    }
    
    // 2つの値を出力
    public void print(String label, int value) {
        IO.println(label + ": " + value);
    }
}
