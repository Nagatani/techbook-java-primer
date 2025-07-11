package com.example.interfaces;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

/**
 * 決済処理インターフェイスの進化を示すサンプル
 * API拡張における後方互換性の維持方法を学習
 */
public class PaymentProcessorEvolution {
    
    /**
     * Java 7以前の従来のインターフェイス
     */
    public interface PaymentProcessorV1 {
        void processPayment(BigDecimal amount);
        PaymentResult getResult();
    }
    
    /**
     * Java 8以降：defaultメソッドによる拡張
     */
    public interface PaymentProcessorV2 extends PaymentProcessorV1 {
        
        // 新機能をdefaultメソッドで追加（後方互換性を維持）
        default void processRefund(BigDecimal amount) {
            System.out.println("Processing refund: " + amount);
            // デフォルト実装では基本的な処理のみ
        }
        
        // 共通のバリデーション処理
        default boolean validateAmount(BigDecimal amount) {
            return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
        }
        
        // 共通のロギング処理
        default void logTransaction(BigDecimal amount, String type) {
            System.out.println(LocalDateTime.now() + " - " + type + ": " + amount);
        }
        
        // テンプレートメソッドパターンの実装
        default void processPaymentWithLogging(BigDecimal amount) {
            logTransaction(amount, "PAYMENT_START");
            if (validateAmount(amount)) {
                processPayment(amount);
                logTransaction(amount, "PAYMENT_SUCCESS");
            } else {
                logTransaction(amount, "PAYMENT_FAILED");
                throw new IllegalArgumentException("Invalid amount: " + amount);
            }
        }
    }
    
    /**
     * さらなる拡張：非同期処理の追加
     */
    public interface PaymentProcessorV3 extends PaymentProcessorV2 {
        
        // 非同期処理の追加
        default CompletableFuture<PaymentResult> processPaymentAsync(BigDecimal amount) {
            return CompletableFuture.supplyAsync(() -> {
                processPayment(amount);
                return getResult();
            });
        }
        
        // バッチ処理機能
        default void processBatchPayments(BigDecimal[] amounts) {
            for (BigDecimal amount : amounts) {
                processPaymentWithLogging(amount);
            }
        }
    }
    
    /**
     * 決済結果を表すクラス
     */
    public static class PaymentResult {
        private final boolean success;
        private final String message;
        private final String transactionId;
        
        public PaymentResult(boolean success, String message, String transactionId) {
            this.success = success;
            this.message = message;
            this.transactionId = transactionId;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getTransactionId() { return transactionId; }
        
        @Override
        public String toString() {
            return "PaymentResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", transactionId='" + transactionId + '\'' +
                    '}';
        }
    }
    
    /**
     * クレジットカード決済の実装
     */
    public static class CreditCardProcessor implements PaymentProcessorV3 {
        private PaymentResult lastResult;
        
        @Override
        public void processPayment(BigDecimal amount) {
            // クレジットカード固有の処理
            System.out.println("Processing credit card payment: " + amount);
            // 実際の処理をシミュレート
            boolean success = amount.compareTo(new BigDecimal("10000")) <= 0;
            String message = success ? "Payment successful" : "Payment failed - amount too large";
            String transactionId = "CC-" + System.currentTimeMillis();
            
            lastResult = new PaymentResult(success, message, transactionId);
        }
        
        @Override
        public PaymentResult getResult() {
            return lastResult;
        }
        
        // 必要に応じてdefaultメソッドをオーバーライド
        @Override
        public void processRefund(BigDecimal amount) {
            System.out.println("Processing credit card refund: " + amount);
            // クレジットカード固有のリファンド処理
        }
    }
    
    /**
     * 銀行振込決済の実装
     */
    public static class BankTransferProcessor implements PaymentProcessorV3 {
        private PaymentResult lastResult;
        
        @Override
        public void processPayment(BigDecimal amount) {
            // 銀行振込固有の処理
            System.out.println("Processing bank transfer payment: " + amount);
            // 実際の処理をシミュレート
            boolean success = amount.compareTo(new BigDecimal("1000000")) <= 0;
            String message = success ? "Transfer successful" : "Transfer failed - daily limit exceeded";
            String transactionId = "BT-" + System.currentTimeMillis();
            
            lastResult = new PaymentResult(success, message, transactionId);
        }
        
        @Override
        public PaymentResult getResult() {
            return lastResult;
        }
        
        // 銀行振込では追加のバリデーション
        @Override
        public boolean validateAmount(BigDecimal amount) {
            return PaymentProcessorV3.super.validateAmount(amount) &&
                   amount.compareTo(new BigDecimal("1")) >= 0; // 最低1円以上
        }
    }
    
    /**
     * デモンストレーション用のメインメソッド
     */
    public static void main(String[] args) {
        System.out.println("=== Payment Processor Evolution Demo ===");
        
        // 異なる実装を統一的に扱う
        PaymentProcessorV3[] processors = {
            new CreditCardProcessor(),
            new BankTransferProcessor()
        };
        
        BigDecimal[] amounts = {
            new BigDecimal("100"),
            new BigDecimal("5000"),
            new BigDecimal("50000")
        };
        
        for (PaymentProcessorV3 processor : processors) {
            System.out.println("\n--- " + processor.getClass().getSimpleName() + " ---");
            
            for (BigDecimal amount : amounts) {
                try {
                    // defaultメソッドを活用した統一的な処理
                    processor.processPaymentWithLogging(amount);
                    System.out.println("Result: " + processor.getResult());
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            
            // 非同期処理のデモ
            System.out.println("\nAsync processing demo:");
            CompletableFuture<PaymentResult> future = processor.processPaymentAsync(new BigDecimal("1000"));
            try {
                PaymentResult result = future.get();
                System.out.println("Async result: " + result);
            } catch (Exception e) {
                System.out.println("Async error: " + e.getMessage());
            }
        }
    }
}