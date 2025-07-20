package com.example.theory.patterns;

import java.util.ArrayList;
import java.util.List;

/**
 * Observerパターンとイベント代数
 * リストAC-21
 */
public class ObserverPattern {
    
    /**
     * Subject = 観察される対象
     */
    public static class Stock {
        private String symbol;
        private double price;
        private List<Observer> observers = new ArrayList<>();
        
        public Stock(String symbol, double price) {
            this.symbol = symbol;
            this.price = price;
        }
        
        public void addObserver(Observer observer) {
            observers.add(observer);
        }
        
        public void removeObserver(Observer observer) {
            observers.remove(observer);
        }
        
        public void setPrice(double price) {
            this.price = price;
            notifyObservers(); // イベント発火
        }
        
        private void notifyObservers() {
            for (Observer observer : observers) {
                observer.update(this);
            }
        }
        
        public String getSymbol() {
            return symbol;
        }
        
        public double getPrice() {
            return price;
        }
    }
    
    /**
     * Observer = 観察者の抽象化
     */
    public interface Observer {
        void update(Stock stock);
    }
    
    /**
     * ConcreteObserver = 具体的な観察者（株価表示）
     */
    public static class StockDisplay implements Observer {
        private String name;
        
        public StockDisplay(String name) {
            this.name = name;
        }
        
        @Override
        public void update(Stock stock) {
            System.out.println(name + " - Stock price updated: " + 
                             stock.getSymbol() + " = $" + stock.getPrice());
        }
    }
    
    /**
     * ConcreteObserver = 具体的な観察者（アラート）
     */
    public static class StockAlert implements Observer {
        private String name;
        private double threshold;
        
        public StockAlert(String name, double threshold) {
            this.name = name;
            this.threshold = threshold;
        }
        
        @Override
        public void update(Stock stock) {
            if (stock.getPrice() > threshold) {
                System.out.println(name + " - ALERT: " + stock.getSymbol() + 
                                 " exceeded threshold of $" + threshold + 
                                 " (current: $" + stock.getPrice() + ")");
            }
        }
    }
    
    /**
     * ConcreteObserver = 具体的な観察者（ログ記録）
     */
    public static class StockLogger implements Observer {
        private List<String> log = new ArrayList<>();
        
        @Override
        public void update(Stock stock) {
            String logEntry = "Log: " + stock.getSymbol() + " = $" + stock.getPrice();
            log.add(logEntry);
            System.out.println(logEntry);
        }
        
        public List<String> getLog() {
            return new ArrayList<>(log);
        }
    }
    
    /**
     * Observerパターンのデモンストレーション
     */
    public static void demonstrateObserverPattern() {
        // Subjectの作成
        Stock appleStock = new Stock("AAPL", 150.0);
        
        // Observerの作成と登録
        StockDisplay display1 = new StockDisplay("Display 1");
        StockDisplay display2 = new StockDisplay("Display 2");
        StockAlert alert = new StockAlert("Price Alert", 155.0);
        StockLogger logger = new StockLogger();
        
        appleStock.addObserver(display1);
        appleStock.addObserver(display2);
        appleStock.addObserver(alert);
        appleStock.addObserver(logger);
        
        // 価格変更とイベント通知
        System.out.println("=== Price Update 1 ===");
        appleStock.setPrice(152.0);
        
        System.out.println("\n=== Price Update 2 ===");
        appleStock.setPrice(156.0);
        
        // Observerの削除
        System.out.println("\n=== After removing Display 1 ===");
        appleStock.removeObserver(display1);
        appleStock.setPrice(154.0);
        
        // ログの確認
        System.out.println("\n=== Log History ===");
        logger.getLog().forEach(System.out::println);
    }
}