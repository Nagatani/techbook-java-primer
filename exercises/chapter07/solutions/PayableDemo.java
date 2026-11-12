package chapter07.solutions;

/**
 * 支払いのインターフェイスを試すプログラム（課題2の解答例）
 */
public class PayableDemo {
    public static void main(String[] args) {
        IO.println("=== 支払いシステムデモ ===\n");

        Payable employee1 = new Employee("山田太郎", "E001", 300000, 0.2);
        Payable invoice1 = new Invoice("INV-001", "ノートパソコン", 5, 80000);

        employee1.displayPaymentInfo();
        invoice1.displayPaymentInfo();

        IO.println("\n=== ポリモーフィズムを使った処理 ===");
        Payable[] payables = {
            employee1,
            invoice1,
            new Employee("佐藤花子", "E002", 250000, 0.1),
            new Invoice("INV-002", "マウス", -3, 2000)
        };
        double totalPayment = 0;
        for (Payable payable : payables) {
            totalPayment += payable.calculatePayment();
        }
        IO.println("件数: " + payables.length + "件");
        IO.println("\n総支払い金額: " + "%,.0f".formatted(totalPayment) + "円");

        IO.println("\n=== タイプ別処理 ===");
        for (Payable payable : payables) {
            // 第5章のinstanceofのパターンマッチング: 型を確かめて、その型の変数に入れる
            if (payable instanceof Employee employee) {
                employee.displayEmployeeInfo();
            } else if (payable instanceof Invoice invoice) {
                invoice.displayInvoiceInfo();
            }
        }
    }
}
