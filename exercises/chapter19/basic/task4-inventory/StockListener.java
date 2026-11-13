import java.util.EventListener;

/**
 * 在庫の変化を受け取るリスナー
 */
public interface StockListener extends EventListener {
    void stockChanged(StockEvent e);
}
