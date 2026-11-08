// 使用例
public class OverloadExample {
    public static void main(String[] args) {
        PrintHelper helper = new PrintHelper();
        
        // 同じprintという名前だが、引数によって異なるメソッドが呼ばれる
        helper.print("こんにちは");          // String版
        helper.print(42);                   // int版
        helper.print("年齢", 20);           // String, int版
    }
}
