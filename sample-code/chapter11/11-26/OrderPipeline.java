import java.util.function.Function;

public class OrderPipeline {
    // 注文：品名、単価、数量、金額
    record Order(String item, int unitPrice, int quantity, int total) {}

    // 不正な注文なら例外をスローする
    static Order validate(Order order) {
        if (order.quantity() <= 0) {
            throw new IllegalArgumentException("数量が不正です: " + order.quantity());
        }
        return order;
    }

    // 単価×数量で金額を求める
    static Order calculateSubtotal(Order order) {
        return new Order(order.item(), order.unitPrice(), order.quantity(),
            order.unitPrice() * order.quantity());
    }

    // 10000円以上なら1割引きにする
    static Order applyDiscount(Order order) {
        if (order.total() < 10000) {
            return order;
        }
        return new Order(order.item(), order.unitPrice(), order.quantity(),
            order.total() - order.total() / 10);
    }

    // 消費税（10%）を加える
    static Order addTax(Order order) {
        return new Order(order.item(), order.unitPrice(), order.quantity(),
            order.total() + order.total() / 10);
    }

    static String toReceipt(Order order) {
        return order.item() + " × " + order.quantity() + " = " + order.total() + "円";
    }

    public static void main(String[] args) {
        // 各段階の関数をandThenで1つの関数にまとめる
        Function<Order, Order> validate = OrderPipeline::validate;
        Function<Order, String> processOrder = validate
            .andThen(OrderPipeline::calculateSubtotal)
            .andThen(OrderPipeline::applyDiscount)
            .andThen(OrderPipeline::addTax)
            .andThen(OrderPipeline::toReceipt);

        IO.println(processOrder.apply(new Order("ノート", 200, 5, 0)));
        IO.println(processOrder.apply(new Order("電卓", 4000, 3, 0)));
        try {
            processOrder.apply(new Order("ペン", 100, 0, 0));
        } catch (IllegalArgumentException e) {
            IO.println("エラー: " + e.getMessage());
        }
    }
}
