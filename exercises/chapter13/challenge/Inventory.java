import java.util.Map;
import java.util.TreeMap;

/** 商品の在庫 */
public class Inventory {
    private final Map<String, Integer> stock = new TreeMap<>();

    public void add(String itemCode, int quantity) {
        stock.merge(itemCode, quantity, Integer::sum);
    }

    /** 在庫から取り置く。商品がない・在庫が足りないときは例外にし、在庫は変えない */
    public void reserve(Order order) throws OrderException {
        Integer current = stock.get(order.itemCode());
        if (current == null) {
            throw new InvalidOrderException("商品コード " + order.itemCode() + " は扱っていません",
                    order.lineNumber());
        }
        if (current < order.quantity()) {
            throw new OutOfStockException(order.itemCode(), order.quantity(), current,
                    order.lineNumber());
        }
        stock.put(order.itemCode(), current - order.quantity());
    }

    /** 取り置きを取り消して在庫に戻す */
    public void cancel(Order order) {
        stock.merge(order.itemCode(), order.quantity(), Integer::sum);
    }

    @Override
    public String toString() {
        return stock.toString();
    }
}
