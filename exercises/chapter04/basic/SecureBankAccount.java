/**
 * 第4章 基本課題2: SecureBankAccountクラスのセキュリティ強化
 * 
 * 課題: 銀行口座を表すクラスを作成し、セキュリティを考慮したカプセル化を実装してください。
 * 
 * 要求仕様:
 * - privateフィールド: accountNumber（String）, balance（double）, pin（String）
 * - PINコードによる認証システム
 * - 残高の不正操作防止
 * - 取引履歴の管理
 * 
 * 評価ポイント:
 * - セキュリティを意識したカプセル化
 * - 認証システムの実装
 * - 不正操作の防止
 */
public class SecureBankAccount {
    // TODO: privateフィールドを宣言してください
    // private String accountNumber;
    // private double balance;
    // private String pin;
    
    // TODO: コンストラクターを実装してください
    // public SecureBankAccount(String accountNumber, double initialBalance, String pin) {
    //     this.accountNumber = accountNumber;
    //     this.balance = initialBalance;
    //     this.pin = pin;
    //     IO.println("口座作成: 口座番号 " + accountNumber);
    //     IO.println("残高: " + "%,.0f".formatted(balance) + "円");
    // }
    
    // TODO: PIN認証のprivateメソッドを実装してください
    // private boolean authenticate(String inputPin) {
    //     boolean isValid = this.pin.equals(inputPin);
    //     if (isValid) {
    //         IO.println("PIN認証成功");
    //     } else {
    //         IO.println("PIN認証失敗");
    //         IO.println("取引が拒否されました。");
    //     }
    //     return isValid;
    // }
    
    // TODO: 残高照会メソッドを実装してください
    // public double getBalance(String pin) {
    //     if (authenticate(pin)) {
    //         return balance;
    //     }
    //     return -1; // 認証失敗時は-1を返す
    // }
    
    // TODO: 入金メソッドを実装してください
    // public void deposit(double amount, String pin) {
    //     if (!authenticate(pin)) {
    //         return;
    //     }
    //     
    //     if (amount > 0) {
    //         balance += amount;
    //         IO.println("%,.0f".formatted(amount) + "円を入金しました。");
    //         IO.println("現在の残高: " + "%,.0f".formatted(balance) + "円");
    //     } else {
    //         IO.println("エラー: 入金額は0円より大きい必要があります。");
    //     }
    // }
    
    // TODO: 出金メソッドを実装してください
    // public void withdraw(double amount, String pin) {
    //     if (!authenticate(pin)) {
    //         return;
    //     }
    //     
    //     if (amount > 0 && amount <= balance) {
    //         balance -= amount;
    //         IO.println("%,.0f".formatted(amount) + "円を出金しました。");
    //         IO.println("現在の残高: " + "%,.0f".formatted(balance) + "円");
    //     } else if (amount > balance) {
    //         IO.println("エラー: 残高不足です。");
    //     } else {
    //         IO.println("エラー: 出金額は0円より大きい必要があります。");
    //     }
    // }
    
    // TODO: 口座番号のgetterメソッドを実装してください（PINは不要）
    // public String getAccountNumber() {
    //     return accountNumber;
    // }
    
    // TODO: 口座情報表示メソッドを実装してください
    // public void showAccountInfo(String pin) {
    //     if (authenticate(pin)) {
    //         IO.println("口座番号: " + accountNumber);
    //         IO.println("残高: " + "%,.0f".formatted(balance) + "円");
    //     }
    // }
}