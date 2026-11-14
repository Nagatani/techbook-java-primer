package com.example.theory.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * カプセル化の理論的意義の例
 */
public class EncapsulationExamples {
    
    /**
     * 悪い例：実装詳細の露出
     */
    public static class BadBankAccount {
        public double[] transactionHistory; // 実装詳細の露出
        public int transactionCount;        // 不変条件の破綻可能性
        
        public void addTransaction(double amount) {
            // 配列サイズチェックなし - バグの温床
            transactionHistory[transactionCount++] = amount;
        }
    }
    
    /**
     * 良い例：データの整合性を保証するカプセル化
     */
    public static class GoodBankAccount {
        private List<Transaction> transactions; // 実装詳細を隠蔽
        private double balance;
        
        public GoodBankAccount() {
            this.transactions = new ArrayList<>();
            this.balance = 0.0;
        }
        
        public void deposit(double amount) {
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            transactions.add(new Transaction("DEPOSIT", amount));
            balance += amount;
            // 不変条件: balance = sum of all transactions
        }
        
        public double getBalance() {
            return balance; // 基本型の値なので、呼び出し側が変えてもフィールドは変わらない
        }
        
        public List<Transaction> getTransactionHistory() {
            return List.copyOf(transactions); // 変更できないコピーを返す（元のリストを変えても影響しない）
        }
    }
    
    /**
     * Transactionクラス
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
    }
}