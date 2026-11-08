// 初期バージョン：カプセル化なし（問題のある設計）
public class BankAccountV0 {
    public String accountNumber;
    public double balance;
    
    public BankAccountV0(String accountNumber, double initialBalance) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
}
