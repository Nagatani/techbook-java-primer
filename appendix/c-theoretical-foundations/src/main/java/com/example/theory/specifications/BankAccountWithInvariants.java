package com.example.theory.specifications;

import java.util.ArrayList;
import java.util.List;

/**
 * クラス不変条件（Invariant）の設計
 * リストAC-27
 */
public class BankAccountWithInvariants {
    private double balance;
    private List<Transaction> transactions;
    
    /**
     * クラス不変条件:
     * balance == transactions.stream().mapToDouble(Transaction::getAmount).sum()
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
        
        // 操作
        transactions.add(new Transaction("DEPOSIT", amount));
        balance += amount;
        
        // 事後条件と不変条件の維持
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
        
        // 操作
        transactions.add(new Transaction("WITHDRAW", -amount));
        balance -= amount;
        
        // 事後条件と不変条件の維持
        assert checkInvariant() : "Invariant violated after withdrawal";
    }
    
    public double getBalance() {
        return balance;
    }
    
    public List<Transaction> getTransactionHistory() {
        return new ArrayList<>(transactions);
    }
    
    private boolean checkInvariant() {
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
        public InsufficientFundsException(String message) {
            super(message);
        }
    }
    
    /**
     * 不変条件のデモンストレーション
     */
    public static void demonstrateInvariants() {
        System.out.println("=== Demonstrating Class Invariants ===");
        
        try {
            // 正常なケース
            BankAccountWithInvariants account = new BankAccountWithInvariants(1000.0);
            System.out.println("Initial balance: $" + account.getBalance());
            
            account.deposit(500.0);
            System.out.println("After deposit $500: $" + account.getBalance());
            
            account.withdraw(200.0);
            System.out.println("After withdrawal $200: $" + account.getBalance());
            
            // トランザクション履歴の確認
            System.out.println("\nTransaction history:");
            account.getTransactionHistory().forEach(System.out::println);
            
            // 事前条件違反のテスト
            System.out.println("\n=== Testing precondition violations ===");
            try {
                account.deposit(-100);
            } catch (IllegalArgumentException e) {
                System.out.println("Caught expected exception: " + e.getMessage());
            }
            
            try {
                account.withdraw(2000);
            } catch (InsufficientFundsException e) {
                System.out.println("Caught expected exception: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }
}