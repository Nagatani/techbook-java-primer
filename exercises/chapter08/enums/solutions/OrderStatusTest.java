/**
 * 注文の状態を試すプログラム（課題2の解答例）
 */
public class OrderStatusTest {
    public static void main(String[] args) {
        IO.println("=== 注文状態管理システム ===");

        Order order = new Order("ORD-001");
        IO.println("注文ID: " + order.getOrderId());
        IO.println("初期状態: " + order.getStatus() + "（" + order.getStatus().getLabel() + "）");
        IO.println();

        IO.println("状態遷移:");
        while (order.getStatus().nextStatus() != null) {
            OrderStatus before = order.getStatus();
            OrderStatus after = before.nextStatus();
            order.changeStatus(after);
            IO.println(before + " → " + after + "（" + after.getLabel() + "）");
        }
        IO.println("履歴: " + order.getHistory());
        IO.println();

        IO.println("状態遷移の検査:");
        showTransition(OrderStatus.PENDING, OrderStatus.SHIPPED);
        showTransition(OrderStatus.DELIVERED, OrderStatus.PENDING);
        showTransition(OrderStatus.SHIPPED, OrderStatus.CANCELLED);
        showTransition(OrderStatus.CONFIRMED, OrderStatus.CANCELLED);
        IO.println();

        Order cancelled = new Order("ORD-002");
        cancelled.changeStatus(OrderStatus.CONFIRMED);
        cancelled.changeStatus(OrderStatus.CANCELLED);
        boolean resumed = cancelled.changeStatus(OrderStatus.PREPARING);
        IO.println("キャンセルした注文の履歴: " + cancelled.getHistory());
        IO.println("キャンセル後に準備へ進めるか: " + resumed);
        IO.println();

        IO.print("キャンセルできる状態:");
        for (OrderStatus s : OrderStatus.values()) {
            if (s.isCancellable()) {
                IO.print(" " + s);
            }
        }
        IO.println();
    }

    private static void showTransition(OrderStatus from, OrderStatus to) {
        String mark = from.canTransitionTo(to) ? "✓ 移れます" : "✗ 無効な遷移です";
        IO.println(from + " → " + to + ": " + mark);
    }
}
