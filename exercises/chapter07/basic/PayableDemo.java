package chapter07.basic;

/**
 * Payableインターフェイスとその実装クラスのデモプログラム
 * 
 * このクラスを実行して、インターフェイスとポリモーフィズムの動作を確認してください。
 */
public class PayableDemo {
    public static void main(String[] args) {
        System.out.println("=== 支払いシステムデモ ===\n");
        
        // TODO: Employee（名前、ID、基本給、ボーナス率）のインスタンスを作成してください
        // Payable employee1 = new Employee("山田太郎", "E001", 300000, 0.2);
        
        // TODO: Invoice（請求書番号、商品説明、数量、単価）のインスタンスを作成してください
        // Payable invoice1 = new Invoice("INV-001", "ノートパソコン", 5, 80000);
        
        // TODO: 各オブジェクトの支払い情報を表示してください
        
        System.out.println("\n=== ポリモーフィズムを使った処理 ===");
        
        // TODO: Payable型の配列を作成し、複数の支払い可能オブジェクトを格納してください
        // Payable[] payables = { ... };
        
        // TODO: 全ての支払い金額の合計を計算してください
        double totalPayment = 0;
        // 計算処理を実装
        
        System.out.println("\n総支払い金額: " + totalPayment + "円");
        
        // TODO: 従業員と請求書を分けて処理する例を実装してください
        System.out.println("\n=== タイプ別処理 ===");
        // instanceof演算子を使って、EmployeeとInvoiceを区別して処理
    }
}