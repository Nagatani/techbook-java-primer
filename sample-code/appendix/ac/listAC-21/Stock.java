/**
 * リストAC-21
 * Stockクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (569行目)
 */

// Subject = 観察される対象
public class Stock {
    private String symbol;
    private double price;
    private List<Observer> observers = new ArrayList<>();
    
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
}

// Observer = 観察者の抽象化
interface Observer {
    void update(Stock stock);
}

// ConcreteObserver = 具体的な観察者
class StockDisplay implements Observer {
    public void update(Stock stock) {
        System.out.println("Stock price updated: " + stock.getPrice());
    }
}