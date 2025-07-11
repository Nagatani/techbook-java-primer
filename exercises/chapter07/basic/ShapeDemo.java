package chapter07.basic;

/**
 * Shapeクラスとその実装クラスのデモプログラム
 * 
 * このクラスを実行して、抽象クラスとポリモーフィズムの動作を確認してください。
 */
public class ShapeDemo {
    public static void main(String[] args) {
        System.out.println("=== 図形デモプログラム ===\n");
        
        // TODO: Circle（半径5.0）とRectangle（幅4.0、高さ6.0）のインスタンスを作成してください
        // Shape circle = new Circle(...);
        // Shape rectangle = new Rectangle(...);
        
        // TODO: 各図形の情報を表示してください
        // ヒント: displayInfo()メソッドを使用
        
        System.out.println("\n=== ポリモーフィズムのデモ ===");
        
        // TODO: Shape型の配列を作成し、複数の図形を格納してください
        // Shape[] shapes = { ... };
        
        // TODO: 拡張for文を使って全ての図形の情報を表示してください
        
        System.out.println("\n=== 面積の合計計算 ===");
        
        // TODO: 全ての図形の面積の合計を計算して表示してください
        double totalArea = 0;
        // 計算処理を実装
        
        System.out.println("全図形の面積の合計: " + totalArea);
    }
}