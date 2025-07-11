/**
 * リスト3-7
 * OrderDemoクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (497行目)
 */

public class OrderDemo {
    public static void main(String[] args) {
        // オブジェクトの生成
        Order order1 = new Order("ORD-001", "田中太郎", 15800);
        
        // 注文のライフサイクルをシミュレート
        order1.displayInfo();
        
        order1.processOrder();
        order1.displayInfo();
        
        order1.ship();
        order1.displayInfo();
        
        order1.complete();
        order1.displayInfo();
    }
}