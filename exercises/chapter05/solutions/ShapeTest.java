/**
 * 第5章 演習問題2: ShapeTestクラスの解答例
 */
public class ShapeTest {
    public static void main(String[] args) {
        System.out.println("=== Shape クラスのテスト ===");
        
        Shape[] shapes = {
            new Rectangle("赤", 6.0, 4.0),
            new Circle("青", 5.0),
            new Rectangle("緑", 3.0, 8.0)
        };
        
        double totalArea = 0;
        for (int i = 0; i < shapes.length; i++) {
            System.out.println("図形" + (i + 1) + ":");
            shapes[i].displayInfo();
            totalArea += shapes[i].getArea();
            System.out.println();
        }
        
        System.out.println("全図形の合計面積: " + String.format("%.2f", totalArea));
    }
}