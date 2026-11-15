package com.example.theory.specifications;

import java.util.ArrayList;
import java.util.List;

/**
 * クラス不変条件（Invariant）の設計
 * assert文による検査は、java -ea で実行したときだけ有効になる
 * 金額をdoubleで表しているのは例を短くするため。
 * doubleは0.1のような10進の小数を正確に表せないので、実務ではBigDecimalか、
 * 最小単位（円・セント）の整数で金額を表す
 */
public class BankAccountWithInvariants {
    private double balance;
    private List<Transaction> transactions;
    
    /**
     * クラス不変条件:
     *   1. balance >= 0
     *   2. balance == transactions.stream().mapToDouble(Transaction::getAmount).sum()
     * 不変条件は、コンストラクターの終わりと、各publicメソッドの始めと終わりで成り立つ。
     * メソッドの途中（transactionsに追加した直後でbalanceを更新する前）では一時的に崩れてよい
     */
    
    public BankAccountWithInvariants(double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
        if (initialBalance > 0) {
            transactions.add(new Transaction("INITIAL", initialBalance));
        }
        // 不変条件の確立
        assert checkInvariant() : "Invariant violated in constructor";
    }
    
    public void deposit(double amount) {
        // 事前条件
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        
        double oldBalance = balance; // 事後条件で「呼び出し前の値」を参照するために覚えておく
        
        // 操作
        transactions.add(new Transaction("DEPOSIT", amount));
        balance += amount;
        
        // 事後条件: 残高がamountだけ増えている
        assert Math.abs(balance - (oldBalance + amount)) < 0.001 : "Postcondition violated after deposit";
        // 不変条件の維持
        assert checkInvariant() : "Invariant violated after deposit";
    }
    
    public void withdraw(double amount) {
        // 事前条件
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance < amount) {
            throw new InsufficientFundsException("Insufficient balance");
        }
        
        double oldBalance = balance;
        
        // 操作
        transactions.add(new Transaction("WITHDRAW", -amount));
        balance -= amount;
        
        // 事後条件: 残高がamountだけ減っている
        assert Math.abs(balance - (oldBalance - amount)) < 0.001 : "Postcondition violated after withdrawal";
        // 不変条件の維持
        assert checkInvariant() : "Invariant violated after withdrawal";
    }
    
    public double getBalance() {
        return balance;
    }
    
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);
    }
    
    private boolean checkInvariant() {
        if (balance < 0) {
            return false;
        }
        double calculatedBalance = transactions.stream()
            .mapToDouble(Transaction::getAmount)
            .sum();
        return Math.abs(balance - calculatedBalance) < 0.001; // 浮動小数点誤差を考慮
    }
    
    /**
     * トランザクションクラス
     */
    public static class Transaction {
        private final String type;
        private final double amount;
        
        public Transaction(String type, double amount) {
            this.type = type;
            this.amount = amount;
        }
        
        public String getType() {
            return type;
        }
        
        public double getAmount() {
            return amount;
        }
        
        @Override
        public String toString() {
            return String.format("%s: %.2f", type, amount);
        }
    }
    
    /**
     * カスタム例外クラス
     */
    public static class InsufficientFundsException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        
        public InsufficientFundsException(String message) {
            super(message);
        }
    }
    
    /**
     * 不変条件のデモンストレーション
     */
    public static void demonstrateInvariants() {
        IO.println("=== Demonstrating Class Invariants ===");
        
        try {
            // 正常なケース
            BankAccountWithInvariants account = new BankAccountWithInvariants(1000.0);
            IO.println("Initial balance: $" + account.getBalance());
            
            account.deposit(500.0);
            IO.println("After deposit $500: $" + account.getBalance());
            
            account.withdraw(200.0);
            IO.println("After withdrawal $200: $" + account.getBalance());
            
            // トランザクション履歴の確認
            IO.println("\nTransaction history:");
            account.getTransactionHistory().forEach(IO::println);
            
            // 事前条件違反のテスト
            IO.println("\n=== Testing precondition violations ===");
            try {
                account.deposit(-100);
            } catch (IllegalArgumentException e) {
                IO.println("Caught expected exception: " + e.getMessage());
            }
            
            try {
                account.withdraw(2000);
            } catch (InsufficientFundsException e) {
                IO.println("Caught expected exception: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}