package com.example.theory.complexity;

/**
 * 結合度（Coupling）の分類例
 * リストAC-12, AC-13, AC-14, AC-15を含む
 */
public class CouplingExamples {
    
    /**
     * 1. データ結合（Data Coupling）- 最も弱い結合
     * リストAC-12
     */
    public double calculateTax(double income, double rate) {
        return income * rate;
    }
    
    /**
     * 2. スタンプ結合（Stamp Coupling）
     * リストAC-13
     */
    public static class TaxInfo {
        private double income;
        private double rate;
        
        public TaxInfo(double income, double rate) {
            this.income = income;
            this.rate = rate;
        }
        
        public double getIncome() {
            return income;
        }
        
        public double getRate() {
            return rate;
        }
    }
    
    public double calculateTax(TaxInfo taxInfo) {
        return taxInfo.getIncome() * taxInfo.getRate();
    }
    
    /**
     * 3. 制御結合（Control Coupling）
     * リストAC-14
     */
    public static class Data {
        private String content;
        
        public Data(String content) {
            this.content = content;
        }
        
        public String getContent() {
            return content;
        }
    }
    
    // 悪い例：制御フラグを渡す
    public void processDataBad(Data data, int mode) {
        if (mode == 1) {
            // 処理A
            System.out.println("Processing mode 1: " + data.getContent());
        } else if (mode == 2) {
            // 処理B
            System.out.println("Processing mode 2: " + data.getContent());
        }
    }
    
    // 良い例：ポリモーフィズムを使用
    public interface DataProcessor {
        void process(Data data);
    }
    
    public static class ProcessorA implements DataProcessor {
        @Override
        public void process(Data data) {
            System.out.println("Processor A: " + data.getContent());
        }
    }
    
    public static class ProcessorB implements DataProcessor {
        @Override
        public void process(Data data) {
            System.out.println("Processor B: " + data.getContent());
        }
    }
    
    /**
     * 4. 共通結合（Common Coupling）
     * リストAC-15
     */
    // 悪い例：グローバル変数への依存
    public static class Order {
        private String orderId;
        
        public Order(String orderId) {
            this.orderId = orderId;
        }
        
        public String getOrderId() {
            return orderId;
        }
    }
    
    public interface Database {
        void save(Order order);
    }
    
    public static class GlobalConfig {
        private static GlobalConfig instance;
        private Database database;
        
        private GlobalConfig() {}
        
        public static GlobalConfig getInstance() {
            if (instance == null) {
                instance = new GlobalConfig();
            }
            return instance;
        }
        
        public Database getDatabase() {
            return database;
        }
        
        public void setDatabase(Database database) {
            this.database = database;
        }
    }
    
    // 悪い例
    public static class BadOrderProcessor {
        public void processOrder(Order order) {
            GlobalConfig.getInstance().getDatabase().save(order);
        }
    }
    
    // 良い例：依存性注入
    public static class GoodOrderProcessor {
        private final Database database;
        
        public GoodOrderProcessor(Database database) {
            this.database = database;
        }
        
        public void processOrder(Order order) {
            database.save(order);
        }
    }
}