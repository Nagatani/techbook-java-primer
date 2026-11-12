/**
 * 注文（課題2の解答例）
 *
 * 移った状態の履歴を配列に記録する。状態の数は6つなので、履歴は多くても6件になる。
 */
public class Order {
    private final String orderId;
    private OrderStatus status;
    private final OrderStatus[] history = new OrderStatus[OrderStatus.values().length];
    private int historyCount = 0;

    public Order(String orderId) {
        this.orderId = orderId;
        this.status = OrderStatus.PENDING;
        record(status);
    }

    private void record(OrderStatus s) {
        history[historyCount] = s;
        historyCount++;
    }

    /**
     * 状態を移す。移れないときは状態を変えずにfalseを返す
     */
    public boolean changeStatus(OrderStatus target) {
        if (!status.canTransitionTo(target)) {
            return false;
        }
        status = target;
        record(target);
        return true;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getHistory() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < historyCount; i++) {
            if (i > 0) {
                sb.append(" → ");
            }
            sb.append(history[i]);
        }
        return sb.toString();
    }
}
