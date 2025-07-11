/**
 * リストAC-27
 * BankAccountクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (849行目)
 */

public class BankAccount {
    private double balance;
    private List<Transaction> transactions;
    
    // クラス不変条件:
    // balance == transactions.stream().mapToDouble(Transaction::getAmount).sum()
    
    public BankAccount(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        if (initialBalance > 0) {
            transactions.add(new Transaction("INITIAL", initialBalance));
        }
        // 不変条件の確立
        assert checkInvariant();
    }
    
    public void deposit(double amount) {
        // 事前条件
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        // 操作
        transactions.add(new Transaction("DEPOSIT", amount));
        balance += amount;
        
        // 事後条件と不変条件の維持
        assert checkInvariant();
    }
    
    public void withdraw(double amount) {
        // 事前条件
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance < amount) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        
        // 操作
        transactions.add(new Transaction("WITHDRAW", -amount));
        balance -= amount;
        
        // 事後条件と不変条件の維持
        assert checkInvariant();
    }
    
    private boolean checkInvariant() {
        double calculatedBalance = transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .sum();
        return Math.abs(balance - calculatedBalance) < 0.001; // 浮動小数点誤差を考慮
    }
}