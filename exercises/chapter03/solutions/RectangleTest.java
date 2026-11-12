/**
 * Rectangleを試すプログラム（課題5の解答例）
 */
public class RectangleTest {
    public static void main(String[] args) {
        IO.println("=== 長方形計算プログラム ===");
        
        // 正方形のRectangleオブジェクトを作成する
        Rectangle square = new Rectangle(5.0);
        IO.print("正方形: ");
        square.showInfo();
        IO.println();
        
        // 長方形のRectangleオブジェクトを作成する
        Rectangle rectangle = new Rectangle(8.0, 3.0);
        IO.print("長方形: ");
        rectangle.showInfo();
        IO.println();
        
        // setSizeメソッドを使ってサイズを変更する
        rectangle.setSize(10.0);
        IO.println("サイズ変更後:");
        IO.print("長方形: ");
        rectangle.showInfo();
    }
}