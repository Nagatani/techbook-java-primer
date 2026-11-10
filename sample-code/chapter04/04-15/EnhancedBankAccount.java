import java.time.LocalDateTime;

// BankAccountV3をさらに発展させた設計例
public class EnhancedBankAccount extends BankAccountV3 {
    // 追加のフィールド：口座の状態管理
    private String status;
    private LocalDateTime lastActivityDate;
    private int failedTransactionCount;
    
    // 口座状態を表す定数
    public static final String STATUS_ACTIVE = "ACTIVE";      // アクティブ
    public static final String STATUS_SUSPENDED = "SUSPENDED"; // 一時停止
    public static final String STATUS_CLOSED = "CLOSED";       // 閉鎖
    
    // 拡張されたコンストラクタ
    public EnhancedBankAccount(String accountNumber, String accountHolder, double initialBalance) {
        super(accountNumber, initialBalance);  // 親クラスのコンストラクタを呼び出し
        this.status = STATUS_ACTIVE;
        this.lastActivityDate = LocalDateTime.now();
        this.failedTransactionCount = 0;
    }
    
    // オーバーライドされた入金メソッド：状態チェックを追加
    @Override
    public void deposit(double amount) {
        validateAccountStatus();
        super.deposit(amount);
        updateLastActivity();
    }
    
    // オーバーライドされた出金メソッド：失敗回数の追跡
    @Override
    public boolean withdraw(double amount) {
        validateAccountStatus();
        boolean success = super.withdraw(amount);
        if (!success) {
            failedTransactionCount++;
            if (failedTransactionCount >= 3) {
                status = STATUS_SUSPENDED;
                IO.println("連続した取引失敗により、口座が一時停止されました。");
            }
        } else {
            failedTransactionCount = 0;  // 成功したらリセット
        }
        updateLastActivity();
        return success;
    }
    
    // プライベートメソッド：内部ロジック
    private void validateAccountStatus() {
        if (!STATUS_ACTIVE.equals(status)) {
            throw new IllegalStateException("口座がアクティブではありません: " + status);
        }
    }
    
    private void updateLastActivity() {
        this.lastActivityDate = LocalDateTime.now();
    }
    
    // 口座の再アクティブ化
    public void reactivateAccount() {
        if (STATUS_SUSPENDED.equals(status)) {
            status = STATUS_ACTIVE;
            failedTransactionCount = 0;
            IO.println("口座が再アクティブ化されました。");
        }
    }
}
