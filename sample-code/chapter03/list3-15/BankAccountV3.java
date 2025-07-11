/**
 * リスト3-15
 * BankAccountV3クラス
 * 
 * 元ファイル: chapter03-oop-basics.md (1015行目)
 */

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BankAccountV3 {
    private final String accountNumber;  // finalで不変性を保証
    private double balance;
    private final List<Transaction> transactions;
    private boolean isActive;
    
    public BankAccountV3(String accountNumber, double initialBalance) {
        // コンストラクタでも検証
        if (!isValidAccountNumber(accountNumber)) {
            throw new IllegalArgumentException("無効な口座番号です");
        }
        if (initialBalance < 0) {
            throw new IllegalArgumentException("初期残高は0以上である必要があります");
        }
        
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        this.isActive = true;
        
        // 初期取引を記録
        transactions.add(new Transaction(
            TransactionType.INITIAL_DEPOSIT, 
            initialBalance, 
            LocalDateTime.now()
        ));
    }
    
    public synchronized void deposit(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("入金額は正の値である必要があります");
        }
        
        balance += amount;
        transactions.add(new Transaction(
            TransactionType.DEPOSIT, 
            amount, 
            LocalDateTime.now()
        ));
    }
    
    public synchronized boolean withdraw(double amount) {
        validateAccountActive();
        if (amount <= 0) {
            throw new IllegalArgumentException("出金額は正の値である必要があります");
        }
        
        if (balance < amount) {
            return false;  // 残高不足
        }
        
        balance -= amount;
        transactions.add(new Transaction(
            TransactionType.WITHDRAWAL, 
            amount, 
            LocalDateTime.now()
        ));
        return true;
    }
    
    // イミュータブルな取引履歴を返す
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);  // 防御的コピー
    }
    
    // アカウントの凍結
    public void freeze() {
        this.isActive = false;
    }
    
    private void validateAccountActive() {
        if (!isActive) {
            throw new IllegalStateException("この口座は凍結されています");
        }
    }
    
    private static boolean isValidAccountNumber(String accountNumber) {
        return accountNumber != null && accountNumber.matches("\\d{10}");
    }
    
    // 内部クラスで取引を表現
    public static class Transaction {
        private final TransactionType type;
        private final double amount;
        private final LocalDateTime timestamp;
        
        private Transaction(TransactionType type, double amount, LocalDateTime timestamp) {
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
        }
        
        // getterのみ提供（イミュータブル）
        public TransactionType getType() { return type; }
        public double getAmount() { return amount; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    public enum TransactionType {
        INITIAL_DEPOSIT, DEPOSIT, WITHDRAWAL
    }
}