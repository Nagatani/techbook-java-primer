import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/** 注文データを1行ずつ処理する。失敗した注文は記録して次の行へ進む */
public class OrderProcessor {
    private static final Logger logger = Logger.getLogger(OrderProcessor.class.getName());
    private static final int MAX_ATTEMPTS = 3;

    private final Inventory inventory;
    private final List<OrderException> failures = new ArrayList<>();
    private int successCount = 0;

    public OrderProcessor(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * 読み取り口のすべての行を処理する
     * 読み取りそのものの失敗（IOException）は、続けられないので呼び出し側に伝える
     */
    public void processAll(OrderSource source, ReceiptPrinter printer) throws IOException {
        String line;
        while ((line = source.readLine()) != null) {
            int lineNumber = source.getLineNumber();
            try {
                Order order = Order.parse(line, lineNumber);
                inventory.reserve(order);
                printWithRetry(order, printer);
                successCount++;
            } catch (OutOfStockException e) {
                // 業務上起こりうる失敗なので警告にする
                logger.warning(lineNumber + "行目: " + e.getMessage() + " 不足 " + e.getShortage() + "個");
                failures.add(e);
            } catch (OrderException e) {
                logger.log(Level.WARNING, lineNumber + "行目: " + e.getMessage(), e);
                failures.add(e);
            }
        }
    }

    /** 印刷を最大3回まで試す。すべて失敗したら取り置きを戻し、注文の例外に包んで投げる */
    private void printWithRetry(Order order, ReceiptPrinter printer) throws OrderException {
        PrinterException last = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                printer.print(order);
                return;
            } catch (PrinterException e) {
                last = e;
                String next = attempt < MAX_ATTEMPTS ? "やり直します" : "あきらめます";
                logger.info("注文" + order.orderId() + ": " + e.getMessage()
                        + "（" + attempt + "回目） → " + next);
            }
        }
        inventory.cancel(order);  // 取り置きを戻してから失敗を伝える
        throw new OrderException("レシートを印刷できないため注文" + order.orderId() + "を取り消しました",
                order.lineNumber(), last);
    }

    public int getSuccessCount() {
        return successCount;
    }

    public List<OrderException> getFailures() {
        return failures;
    }
}
