package com.example.theory.complexity;

import java.time.LocalDate;

/**
 * 凝集度（Cohesion）の分類例
 * 強い順に、機能的凝集、逐次的凝集、通信的凝集、手続き的凝集、時間的凝集、論理的凝集、偶発的凝集となる
 * このファイルでは、機能的凝集・逐次的凝集・論理的凝集・偶発的凝集を示す
 */
public class CohesionExamples {
    
    /**
     * 1. 機能的凝集（Functional Cohesion）- 最も強い凝集
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
     * 3. 論理的凝集（Logical Cohesion）
     * 「出力する」という種類が同じだけの処理を1つのメソッドにまとめ、引数で選ばせる
     * 呼び出し側は制御フラグを渡すことになり、制御結合も生む
     */
    public static class ReportOutput {
        public static final int CONSOLE = 1;
        public static final int CSV = 2;
        public static final int HTML = 3;

        public String output(String title, int kind) {
            if (kind == CONSOLE) {
                return "[" + title + "]";
            } else if (kind == CSV) {
                return "\"" + title + "\"";
            } else if (kind == HTML) {
                return "<h1>" + title + "</h1>";
            }
            throw new IllegalArgumentException("Unknown kind: " + kind);
        }
    }

    /**
     * 4. 偶発的凝集（Coincidental Cohesion）- 避けるべき
     */
    public static class Utilities {
        // 悪い例：関連のない機能の寄せ集め
        public String formatDate(LocalDate date) {
            // 日付フォーマット処理
            return date.toString();
        }
        
        public double calculateTax(double income) {
            // 税金計算処理
            return income * 0.2;
        }
        
        public void sendEmail(String to, String subject) {
            // メール送信処理
            IO.println("Sending email to " + to + " with subject: " + subject);
        }
        
        public int[] sortArray(int[] array) {
            // 配列ソート処理
            int[] sorted = array.clone();
            java.util.Arrays.sort(sorted);
            return sorted;
        }
    }
    /**
     * 凝集度の例を動かすデモ
     */
    public static void demonstrateCohesion() {
        IO.println("--- Functional cohesion ---");
        PrimeChecker checker = new PrimeChecker();
        IO.println("isPrime(17) = " + checker.isPrime(17) + ", isPrime(20) = " + checker.isPrime(20));

        IO.println("--- Sequential cohesion ---");
        DataProcessor processor = new DataProcessor();
        DataProcessor.ProcessedData result =
            processor.process(new DataProcessor.RawData("  Hello Cohesion  "));
        IO.println("process(\"  Hello Cohesion  \") = " + result.getContent());

        IO.println("--- Logical cohesion ---");
        ReportOutput out = new ReportOutput();
        IO.println(out.output("Sales", ReportOutput.CONSOLE));
        IO.println(out.output("Sales", ReportOutput.HTML));
    }

    public static void main(String[] args) {
        demonstrateCohesion();
    }
}
