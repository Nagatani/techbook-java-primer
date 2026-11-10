// 使用例でリスコフ置換原則違反を確認
public class RectangleSquareTest {
    // 長方形を扱う一般的なメソッド
    public static void testRectangle(Rectangle rect) {
        rect.setWidth(5);
        rect.setHeight(10);
        
        // 期待される面積: 5 × 10 = 50
        int expectedArea = 50;
        int actualArea = rect.getArea();
        
        IO.println("幅を5、高さを10に設定");
        IO.println("期待される面積: " + expectedArea);
        IO.println("実際の面積: " + actualArea);
        
        if (expectedArea == actualArea) {
            IO.println("✓ テスト成功");
        } else {
            IO.println("✗ テスト失敗！");
        }
    }
    
    public static void main(String[] args) {
        IO.println("=== 長方形のテスト ===");
        Rectangle rectangle = new Rectangle(3, 4);
        testRectangle(rectangle);
        
        IO.println("\n=== 正方形のテスト ===");
        Square square = new Square(5);
        testRectangle(square);
    }
}
