/**
 * 第3章 基本課題3: BankAccountクラスの設計
 * 
 * 課題: 銀行口座を表すBankAccountクラスを作成し、コンストラクタとthisキーワードを活用してください。
 * 
 * 要求仕様:
 * - インスタンス変数: accountNumber（String）, balance（double）, ownerName（String）
 * - 複数のコンストラクタ（口座番号のみ、口座番号+名前、全項目）
 * - 入金・出金メソッドのオーバーロード
 * - thisキーワードを使った適切な実装
 * 
 * 評価ポイント:
 * - コンストラクタチェーンの実装
 * - thisキーワードの適切な使用
 * - 状態変更メソッドの設計
 */
public class BankAccount {
    // TODO: インスタンス変数を宣言してください
    // String accountNumber;
    // double balance;
    // String ownerName;
    
    // TODO: 口座番号のみを設定するコンストラクタを実装してください
    // public BankAccount(String accountNumber) {
    //     this(accountNumber, 0.0, "未設定");
    // }
    
    // TODO: 口座番号と名前を設定するコンストラクタを実装してください
    // public BankAccount(String accountNumber, String ownerName) {
    //     this(accountNumber, 0.0, ownerName);
    // }
    
    // TODO: 全ての項目を設定するコンストラクタを実装してください
    // public BankAccount(String accountNumber, double balance, String ownerName) {
    //     this.accountNumber = accountNumber;
    //     this.balance = balance;
    //     this.ownerName = ownerName;
    // }
    
    // TODO: 入金メソッドを実装してください
    // public void deposit(double amount) {
    //     if (amount > 0) {
    //         this.balance += amount;
    //         System.out.println(amount + "円を入金しました。");
    //     }
    // }
    
    // TODO: 出金メソッドを実装してください
    // public void withdraw(double amount) {
    //     if (amount > 0 && this.balance >= amount) {
    //         this.balance -= amount;
    //         System.out.println(amount + "円を出金しました。");
    //     } else {
    //         System.out.println("出金できません。");
    //     }
    // }
    
    // TODO: 手数料付き出金メソッドを実装してください（オーバーロード）
    // public void withdraw(double amount, double fee) {
    //     double total = amount + fee;
    //     if (total > 0 && this.balance >= total) {
    //         this.balance -= total;
    //         System.out.println(amount + "円を出金しました。（手数料: " + fee + "円）");
    //     } else {
    //         System.out.println("出金できません。");
    //     }
    // }
    
    // TODO: 口座情報表示メソッドを実装してください
    // public void showAccountInfo() {
    //     System.out.println("口座番号: " + accountNumber + ", 残高: " + balance + "円, 名義: " + ownerName);
    // }
}