/**
 * PINで守る銀行口座（課題2の解答例）
 */
public class SecureBankAccount {
    // privateフィールドを宣言する
    private String accountNumber;
    private double balance;
    private String pin;
    
    // コンストラクタ
    public SecureBankAccount(String accountNumber, double initialBalance, String pin) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.pin = pin;
        IO.println("口座作成: 口座番号 " + accountNumber);
        IO.println("残高: " + "%,.0f".formatted(balance) + "円");
    }
    
    // PIN認証のprivateメソッド
    private boolean authenticate(String inputPin) {
        boolean isValid = this.pin.equals(inputPin);
        if (isValid) {
            IO.println("PIN認証成功");
        } else {
            IO.println("PIN認証失敗");
            IO.println("取引が拒否されました。");
        }
        return isValid;
    }
    
    // 残高照会メソッド
    public double getBalance(String pin) {
        if (authenticate(pin)) {
            return balance;
        }
        return -1; // 認証失敗時は-1を返す
    }
    
    // 入金メソッド
    public void deposit(double amount, String pin) {
        if (!authenticate(pin)) {
            return;
        }
        
        if (amount > 0) {
            balance += amount;
            IO.println("%,.0f".formatted(amount) + "円を入金しました。");
            IO.println("現在の残高: " + "%,.0f".formatted(balance) + "円");
        } else {
            IO.println("エラー: 入金額は0円より大きい必要があります。");
        }
    }
    
    // 出金メソッド
    public void withdraw(double amount, String pin) {
        if (!authenticate(pin)) {
            return;
        }
        
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            IO.println("%,.0f".formatted(amount) + "円を出金しました。");
            IO.println("現在の残高: " + "%,.0f".formatted(balance) + "円");
        } else if (amount > balance) {
            IO.println("エラー: 残高不足です。");
        } else {
            IO.println("エラー: 出金額は0円より大きい必要があります。");
        }
    }
    
    // 口座番号のgetterメソッド（PINは不要）
    public String getAccountNumber() {
        return accountNumber;
    }
    
    // 口座情報表示メソッド
    public void showAccountInfo(String pin) {
        if (authenticate(pin)) {
            IO.println("口座番号: " + accountNumber);
            IO.println("残高: " + "%,.0f".formatted(balance) + "円");
        }
    }
}