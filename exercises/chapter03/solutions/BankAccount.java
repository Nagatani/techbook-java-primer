/**
 * 銀行口座を表すクラス（課題6の解答例）
 */
public class BankAccount {
    // インスタンス変数を宣言する
    String accountNumber;
    double balance;
    String ownerName;
    
    // 口座番号のみを設定するコンストラクタ
    public BankAccount(String accountNumber) {
        this(accountNumber, 0.0, "未設定");
    }
    
    // 口座番号と名前を設定するコンストラクタ
    public BankAccount(String accountNumber, String ownerName) {
        this(accountNumber, 0.0, ownerName);
    }
    
    // 全ての項目を設定するコンストラクタ
    public BankAccount(String accountNumber, double balance, String ownerName) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.ownerName = ownerName;
    }
    
    // 入金メソッド
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            IO.println(amount + "円を入金しました。");
        }
    }
    
    // 出金メソッド
    public void withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            IO.println(amount + "円を出金しました。");
        } else {
            IO.println("出金できません。");
        }
    }
    
    // 手数料付き出金メソッド（オーバーロード）
    public void withdraw(double amount, double fee) {
        double total = amount + fee;
        if (total > 0 && this.balance >= total) {
            this.balance -= total;
            IO.println(amount + "円を出金しました。（手数料: " + fee + "円）");
        } else {
            IO.println("出金できません。");
        }
    }
    
    // 口座情報表示メソッド
    public void showAccountInfo() {
        IO.println("口座番号: " + accountNumber + ", 残高: " + balance + "円, 名義: " + ownerName);
    }
}