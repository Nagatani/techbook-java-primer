/**
 * 第4章 演習問題2: SecureBankAccountクラスの解答例
 * 
 * 【学習ポイント】
 * - セキュリティを考慮したカプセル化
 * - 認証システムの実装
 * - 不正操作の防止
 * - 取引履歴の管理
 */
public class SecureBankAccount {
    private String accountNumber;
    private double balance;
    private String pin;
    private java.util.List<String> transactionHistory;
    private int failedAttempts;
    private boolean isLocked;
    private java.time.LocalDateTime lastTransaction;
    
    public SecureBankAccount(String accountNumber, String pin, double initialBalance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = Math.max(0, initialBalance);
        this.transactionHistory = new java.util.ArrayList<>();
        this.failedAttempts = 0;
        this.isLocked = false;
        this.lastTransaction = java.time.LocalDateTime.now();
        addTransaction("口座開設", initialBalance);
    }
    
    public boolean authenticate(String inputPin) {
        if (isLocked) {
            System.out.println("口座がロックされています。管理者にお問い合わせください。");
            return false;
        }
        
        if (pin.equals(inputPin)) {
            failedAttempts = 0;
            return true;
        } else {
            failedAttempts++;
            System.out.println("PIN認証に失敗しました。試行回数: " + failedAttempts + "/3");
            
            if (failedAttempts >= 3) {
                isLocked = true;
                System.out.println("口座がロックされました。");
                addTransaction("口座ロック", 0);
            }
            return false;
        }
    }
    
    public boolean deposit(String inputPin, double amount) {
        if (!authenticate(inputPin)) {
            return false;
        }
        
        if (amount <= 0) {
            System.out.println("入金額は正の値である必要があります。");
            return false;
        }
        
        balance += amount;
        addTransaction("入金", amount);
        lastTransaction = java.time.LocalDateTime.now();
        System.out.println(amount + "円を入金しました。残高: " + balance + "円");
        return true;
    }
    
    public boolean withdraw(String inputPin, double amount) {
        if (!authenticate(inputPin)) {
            return false;
        }
        
        if (amount <= 0) {
            System.out.println("出金額は正の値である必要があります。");
            return false;
        }
        
        if (balance < amount) {
            System.out.println("残高が不足しています。残高: " + balance + "円");
            addTransaction("出金失敗（残高不足）", amount);
            return false;
        }
        
        balance -= amount;
        addTransaction("出金", amount);
        lastTransaction = java.time.LocalDateTime.now();
        System.out.println(amount + "円を出金しました。残高: " + balance + "円");
        return true;
    }
    
    public double getBalance(String inputPin) {
        if (!authenticate(inputPin)) {
            return -1;
        }
        return balance;
    }
    
    public void showTransactionHistory(String inputPin) {
        if (!authenticate(inputPin)) {
            return;
        }
        
        System.out.println("=== 取引履歴 ===");
        for (String transaction : transactionHistory) {
            System.out.println(transaction);
        }
    }
    
    private void addTransaction(String type, double amount) {
        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
        String transaction = String.format("%s - %s: %.0f円 (残高: %.0f円)", 
                                         timestamp, type, amount, balance);
        transactionHistory.add(transaction);
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public boolean isLocked() {
        return isLocked;
    }
    
    // 管理者用のロック解除メソッド（実際の実装では管理者権限チェックが必要）
    public void unlockAccount(String adminCode) {
        if ("ADMIN123".equals(adminCode)) {  // 簡易的な管理者認証
            isLocked = false;
            failedAttempts = 0;
            addTransaction("口座ロック解除", 0);
            System.out.println("口座のロックが解除されました。");
        } else {
            System.out.println("管理者コードが正しくありません。");
        }
    }
}