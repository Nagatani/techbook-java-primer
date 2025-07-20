package com.example.theory.complexity;

import java.util.Date;

/**
 * 凝集度（Cohesion）の分類例
 * リストAC-16, AC-17, AC-18を含む
 */
public class CohesionExamples {
    
    /**
     * 1. 機能的凝集（Functional Cohesion）- 最も強い凝集
     * リストAC-16
     */
    public static class PrimeChecker {
        public boolean isPrime(int number) {
            if (number < 2) return false;
            for (int i = 2; i <= Math.sqrt(number); i++) {
                if (number % i == 0) return false;
            }
            return true;
        }
    }
    
    /**
     * 2. 逐次的凝集（Sequential Cohesion）
     * リストAC-17
     */
    public static class DataProcessor {
        public static class RawData {
            private String content;
            
            public RawData(String content) {
                this.content = content;
            }
            
            public String getContent() {
                return content;
            }
        }
        
        public static class CleanedData {
            private String content;
            
            public CleanedData(String content) {
                this.content = content;
            }
            
            public String getContent() {
                return content;
            }
        }
        
        public static class ValidatedData {
            private String content;
            
            public ValidatedData(String content) {
                this.content = content;
            }
            
            public String getContent() {
                return content;
            }
        }
        
        public static class ProcessedData {
            private String content;
            
            public ProcessedData(String content) {
                this.content = content;
            }
            
            public String getContent() {
                return content;
            }
        }
        
        public ProcessedData process(RawData raw) {
            CleanedData cleaned = clean(raw);
            ValidatedData validated = validate(cleaned);
            return transform(validated);
        }
        
        private CleanedData clean(RawData raw) {
            // クリーニング処理
            String cleaned = raw.getContent().trim().toLowerCase();
            return new CleanedData(cleaned);
        }
        
        private ValidatedData validate(CleanedData cleaned) {
            // バリデーション処理
            if (cleaned.getContent().isEmpty()) {
                throw new IllegalArgumentException("Data cannot be empty");
            }
            return new ValidatedData(cleaned.getContent());
        }
        
        private ProcessedData transform(ValidatedData validated) {
            // 変換処理
            String transformed = validated.getContent().toUpperCase();
            return new ProcessedData(transformed);
        }
    }
    
    /**
     * 3. 偶発的凝集（Coincidental Cohesion）- 避けるべき
     * リストAC-18
     */
    public static class Utilities {
        // 悪い例：関連のない機能の寄せ集め
        public String formatDate(Date date) {
            // 日付フォーマット処理
            return date.toString();
        }
        
        public double calculateTax(double income) {
            // 税金計算処理
            return income * 0.2;
        }
        
        public void sendEmail(String to, String subject) {
            // メール送信処理
            System.out.println("Sending email to " + to + " with subject: " + subject);
        }
        
        public int[] sortArray(int[] array) {
            // 配列ソート処理
            int[] sorted = array.clone();
            java.util.Arrays.sort(sorted);
            return sorted;
        }
    }
}