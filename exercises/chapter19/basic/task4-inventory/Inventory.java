import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 在庫を管理し、変化をリスナーに知らせるクラス（画面に依存しないロジック）
 *
 * 画面の部品のことは知らず、登録されたリスナーに通知するだけです。
 */
public class Inventory {
    private final Map<String, Integer> stock = new LinkedHashMap<>();
    private final List<StockListener> listeners = new ArrayList<>();

    public void addStockListener(StockListener listener) {
        listeners.add(listener);
    }

    public void removeStockListener(StockListener listener) {
        listeners.remove(listener);
    }

    /** 商品を登録する。通知はしない */
    public void register(String item, int quantity) {
        stock.put(item, quantity);
    }

    /** 入荷。数を増やして通知する */
    public void receive(String item, int quantity) {
        if (quantity <= 0 || !stock.containsKey(item)) {
            return;
        }
        int old = stock.get(item);
        stock.put(item, old + quantity);
        fireStockChanged(item, old, old + quantity);
    }

    /** 出荷。在庫が足りなければ何もせずfalseを返す */
    public boolean ship(String item, int quantity) {
        if (quantity <= 0 || !stock.containsKey(item) || stock.get(item) < quantity) {
            return false;
        }
        int old = stock.get(item);
        stock.put(item, old - quantity);
        fireStockChanged(item, old, old - quantity);
        return true;
    }

    public int getQuantity(String item) {
        return stock.getOrDefault(item, 0);
    }

    public List<String> getItems() {
        return List.copyOf(stock.keySet());
    }

    // 登録されたリスナーすべてに、同じイベントを渡す
    private void fireStockChanged(String item, int oldQuantity, int newQuantity) {
        StockEvent event = new StockEvent(this, item, oldQuantity, newQuantity);
        for (StockListener listener : listeners) {
            listener.stockChanged(event);
        }
    }
}
