/**
 * 第3章 基本課題3: BankAccountTest クラス
 * 
 * BankAccountクラスをテストするためのクラスです。
 * 複数のコンストラクタと入金・出金メソッドを使って口座を管理してください。
 * 
 * 実行例:
 * === 銀行口座管理システム ===
 * 口座1: 口座番号: 12345, 残高: 0.0円, 名義: 未設定
 * 口座2: 口座番号: 67890, 残高: 0.0円, 名義: 田中太郎
 * 口座3: 口座番号: 11111, 残高: 50000.0円, 名義: 佐藤花子
 * 
 * 入金後:
 * 口座1: 口座番号: 12345, 残高: 10000.0円, 名義: 未設定
 * 
 * 出金後:
 * 口座3: 口座番号: 11111, 残高: 30000.0円, 名義: 佐藤花子
 */
public class BankAccountTest {
    public static void main(String[] args) {
        System.out.println("=== 銀行口座管理システム ===");
        
        // TODO: 口座番号のみを設定して口座を作成してください
        // BankAccount account1 = new BankAccount("12345");
        // account1.showAccountInfo();
        // System.out.println();
        
        // TODO: 口座番号と名前を設定して口座を作成してください
        // BankAccount account2 = new BankAccount("67890", "田中太郎");
        // account2.showAccountInfo();
        // System.out.println();
        
        // TODO: 全ての項目を設定して口座を作成してください
        // BankAccount account3 = new BankAccount("11111", 50000.0, "佐藤花子");
        // account3.showAccountInfo();
        // System.out.println();
        
        // TODO: 入金を行ってください
        // account1.deposit(10000.0);
        // System.out.println("入金後:");
        // account1.showAccountInfo();
        // System.out.println();
        
        // TODO: 手数料付き出金を行ってください
        // account3.withdraw(20000.0, 100.0);
        // System.out.println("出金後:");
        // account3.showAccountInfo();
    }
}