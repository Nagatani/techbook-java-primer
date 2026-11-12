import java.util.Set;

/**
 * レシートを印刷するプリンター（動作確認用に、決めた回の印刷で紙詰まりを起こす）
 */
public class ReceiptPrinter implements AutoCloseable {
    private final Set<Integer> jamAt;  // 何回目の印刷で紙詰まりにするか
    private int count = 0;

    public ReceiptPrinter(Set<Integer> jamAt) {
        this.jamAt = jamAt;
        IO.println("[プリンター] に接続しました");
    }

    public void print(Order order) throws PrinterException {
        count++;
        if (jamAt.contains(count)) {
            throw new PrinterException("紙詰まりが起きました");
        }
        IO.println("  レシート: 注文" + order.orderId() + " " + order.itemCode()
                + " x " + order.quantity());
    }

    @Override
    public void close() {
        IO.println("[プリンター] との接続を切りました");
    }
}
