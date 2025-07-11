/**
 * リスト3-6
 * Orderクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (449行目)
 */

public class Order {
    // フィールド（状態）
    private String orderId;
    private String customerName;
    private String status;  // "受付中", "処理中", "配送中", "完了"
    private double totalAmount;
    
    // コンストラクタ
    public Order(String orderId, String customerName, double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.status = "受付中";
    }
    
    // メソッド（振る舞い）
    public void processOrder() {
        if (status.equals("受付中")) {
            status = "処理中";
            System.out.println("注文 " + orderId + " の処理を開始しました");
        }
    }
    
    public void ship() {
        if (status.equals("処理中")) {
            status = "配送中";
            System.out.println("注文 " + orderId + " を発送しました");
        }
    }
    
    public void complete() {
        if (status.equals("配送中")) {
            status = "完了";
            System.out.println("注文 " + orderId + " が完了しました");
        }
    }
    
    public void displayInfo() {
        System.out.printf("注文ID: %s, 顧客: %s, 金額: %.2f円, 状態: %s%n",
                orderId, customerName, totalAmount, status);
    }
}