/**
 * BankAccountを試すプログラム（課題6の解答例）
 */
public class BankAccountTest {
    public static void main(String[] args) {
        IO.println("=== 銀行口座管理システム ===");
        
        // 口座番号のみを設定して口座を作成する
        BankAccount account1 = new BankAccount("12345");
        IO.print("口座1: ");
        account1.showAccountInfo();
        IO.println();
        
        // 口座番号と名前を設定して口座を作成する
        BankAccount account2 = new BankAccount("67890", "田中太郎");
        IO.print("口座2: ");
        account2.showAccountInfo();
        IO.println();
        
        // 全ての項目を設定して口座を作成する
        BankAccount account3 = new BankAccount("11111", 50000.0, "佐藤花子");
        IO.print("口座3: ");
        account3.showAccountInfo();
        IO.println();
        
        // 入金を行ってください
        account1.deposit(10000.0);
        IO.println("入金後:");
        IO.print("口座1: ");
        account1.showAccountInfo();
        IO.println();
        
        // 手数料付き出金を行ってください
        account3.withdraw(20000.0, 100.0);
        IO.println("出金後:");
        IO.print("口座3: ");
        account3.showAccountInfo();
    }
}