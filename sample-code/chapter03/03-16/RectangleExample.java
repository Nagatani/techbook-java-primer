// 使用例
public class RectangleExample {
    public static void main(String[] args) {
        // オブジェクトの作成
        Rectangle rect = new Rectangle(10.0, 5.0);
        
        // メソッドの呼び出し
        rect.displayInfo();
        
        // メソッドの戻り値を使用
        double area = rect.calculateArea();
        IO.println("面積は" + area + "です");
        
        // 引数付きメソッドの呼び出し
        rect.resize(2.0);
        rect.displayInfo();
    }
}
