import java.util.ArrayList;
import java.util.List;

public class OrderApp {
    public static void main(String[] args) {
        List<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine("ノート", 150, 3));
        lines.add(new OrderLine("ボールペン", 120, 2));

        Order order = new Order("A-001", "山田", lines);
        IO.println("=== 最初の注文 ===");
        order.print();

        IO.println();
        IO.println("=== 渡したリストに行を足す ===");
        lines.add(new OrderLine("消しゴム", 80, 1));
        IO.println("渡したリスト: " + lines.size() + "行、注文: " + order.getLines().size() + "行");

        IO.println();
        IO.println("=== 受け取ったリストから行を消す ===");
        List<OrderLine> received = order.getLines();
        received.clear();
        IO.println("受け取ったリスト: " + received.size() + "行、注文: " + order.getLines().size() + "行");

        IO.println();
        IO.println("=== 注文を変える（新しい注文ができる） ===");
        Order changed = order
                .withLine(new OrderLine("ノート", 150, 2))
                .withLine(new OrderLine("付箋", 200, 1))
                .withQuantity("ボールペン", 0);
        IO.println("変更後:");
        changed.print();
        IO.println("変更前（そのまま残る）:");
        order.print();
        IO.println("差額: " + (changed.total() - order.total()) + "円");

        IO.println();
        IO.println("=== ない商品の個数を変える ===");
        IO.println("同じオブジェクトが返る: " + (changed.withQuantity("のり", 5) == changed));
    }
}
