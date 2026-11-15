package com.example.theory.complexity;

/**
 * 結合度（Coupling）の分類例
 * 弱い順に、データ結合、スタンプ結合、制御結合、外部結合、共通結合、内容結合となる
 * このファイルでは、データ結合・スタンプ結合・制御結合・共通結合を示す
 */
public class CouplingExamples {
    
    /**
     * 1. データ結合（Data Coupling）- 最も弱い結合
     */
    public double calculateTax(double income, double rate) {
        return income * rate;
    }
    
    /**
     * 2. スタンプ結合（Stamp Coupling）
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
            IO.println("Processing mode 1: " + data.getContent());
        } else if (mode == 2) {
            // 処理B
            IO.println("Processing mode 2: " + data.getContent());
        }
    }
    
    // 良い例：ポリモーフィズムを使用
    public interface DataProcessor {
        void process(Data data);
    }
    
    public static class ProcessorA implements DataProcessor {
        @Override
        public void process(Data data) {
            IO.println("Processor A: " + data.getContent());
        }
    }
    
    public static class ProcessorB implements DataProcessor {
        @Override
        public void process(Data data) {
            IO.println("Processor B: " + data.getContent());
        }
    }
    
    /**
     * 4. 共通結合（Common Coupling）
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
    /**
     * 結合度の違いを確かめるデモ
     */
    public static void demonstrateCoupling() {
        CouplingExamples c = new CouplingExamples();

        IO.println("--- Data coupling / Stamp coupling ---");
        IO.println("calculateTax(50000, 0.2) = " + c.calculateTax(50000, 0.2));
        IO.println("calculateTax(TaxInfo) = " + c.calculateTax(new TaxInfo(50000, 0.2)));

        IO.println("--- Control coupling ---");
        Data data = new Data("report");
        c.processDataBad(data, 1);
        c.processDataBad(data, 3); // 想定外のモードは何もしない（誤りに気づけない）
        DataProcessor[] processors = { new ProcessorA(), new ProcessorB() };
        for (DataProcessor p : processors) {
            p.process(data);
        }

        IO.println("--- Common coupling ---");
        try {
            new BadOrderProcessor().processOrder(new Order("A-001"));
        } catch (NullPointerException e) {
            IO.println("BadOrderProcessor failed: GlobalConfig has no database");
        }
        // 依存を引数で受け取るので、テスト用の偽物を渡せる
        GoodOrderProcessor good = new GoodOrderProcessor(
            order -> IO.println("Saved by test double: " + order.getOrderId()));
        good.processOrder(new Order("A-002"));
    }

    public static void main(String[] args) {
        demonstrateCoupling();
    }
}
