package com.example.theory.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * カプセル化の理論的意義の例
 */
public class EncapsulationExamples {

    /**
     * 悪い例：実装詳細の露出
     * 配列と件数が公開されているので、どのクラスからでも不変条件
     * （transactionCountは0以上で、記録した件数と等しい）を壊せる
     */
    public static class BadBankAccount {
        public double[] transactionHistory = new double[3]; // 実装詳細の露出
        public int transactionCount;                        // 不変条件の破綻可能性

        public void addTransaction(double amount) {
            // 配列サイズチェックなし - 4件目でArrayIndexOutOfBoundsException
            transactionHistory[transactionCount++] = amount;
        }
    }

    /**
     * 良い例：データの整合性を保証するカプセル化
     * 不変条件: balance は transactions の金額の合計に等しく、0以上
     */
    public static class GoodBankAccount {
        private final List<Transaction> transactions; // 実装詳細を隠蔽
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
        }

        public double getBalance() {
            return balance; // 基本型の値なので、呼び出し側が変えてもフィールドは変わらない
        }

        public List<Transaction> getTransactionHistory() {
            return List.copyOf(transactions); // 変更できないコピーを返す（元のリストを変えても影響しない）
        }
    }

    /**
     * Transactionクラス（不変）
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

    public static void demonstrateEncapsulation() {
        IO.println("--- BadBankAccount ---");
        BadBankAccount bad = new BadBankAccount();
        bad.addTransaction(100);
        bad.transactionCount = -1; // 外から不変条件を壊せる
        try {
            bad.addTransaction(50);
        } catch (ArrayIndexOutOfBoundsException e) {
            IO.println("Broken invariant: " + e.getMessage());
        }

        IO.println("--- GoodBankAccount ---");
        GoodBankAccount good = new GoodBankAccount();
        good.deposit(100);
        good.deposit(50);
        try {
            good.deposit(-30);
        } catch (IllegalArgumentException e) {
            IO.println("Rejected: " + e.getMessage());
        }
        List<Transaction> history = good.getTransactionHistory();
        try {
            history.add(new Transaction("DEPOSIT", 1_000_000));
        } catch (UnsupportedOperationException e) {
            IO.println("History is read-only");
        }
        IO.println("Balance: " + good.getBalance()
                + ", transactions: " + good.getTransactionHistory().size());
    }

    public static void main(String[] args) {
        demonstrateEncapsulation();
    }
}
