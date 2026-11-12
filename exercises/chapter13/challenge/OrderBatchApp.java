import java.io.IOException;
import java.util.Set;

/** 注文データの一括処理を実演するmainメソッド */
public class OrderBatchApp {
    public static void main(String[] args) {
        LogSetup.configure();

        Inventory inventory = new Inventory();
        inventory.add("A100", 10);  // ノート
        inventory.add("B200", 3);   // ボールペン
        inventory.add("C300", 5);   // 消しゴム

        String[] orders = {
            "1001,A100,2",
            "1002,B200,1",
            "1003,B200,5",
            "1004,X999,1",
            "1005,C300,二",
            "1006,A100,3",
            "1007,C300",
            "1008,C300,4",
            "1009,A100,0",
        };

        IO.println("=== 1回目: 注文データの処理 ===");
        OrderProcessor processor = new OrderProcessor(inventory);
        // 2回目の印刷で1回、4〜6回目の印刷で3回続けて紙詰まりを起こす
        try (OrderSource source = new OrderSource("orders-1.csv", orders);
             ReceiptPrinter printer = new ReceiptPrinter(Set.of(2, 4, 5, 6))) {
            processor.processAll(source, printer);
        } catch (IOException e) {
            IO.println("処理を中断しました: " + e.getMessage());
        }
        printSummary(processor, inventory);

        IO.println();
        IO.println("=== 2回目: 読み取りの途中で失敗する ===");
        String[] moreOrders = {"2001,A100,1", "2002,C300,1", "2003,B200,1"};
        OrderProcessor second = new OrderProcessor(inventory);
        try (OrderSource source = new OrderSource("orders-2.csv", moreOrders, 2, true);
             ReceiptPrinter printer = new ReceiptPrinter(Set.of())) {
            second.processAll(source, printer);
        } catch (IOException e) {
            IO.println("処理を中断しました: " + e.getMessage());
            // 閉じる処理で起きた例外は、抑制された例外として本来の例外に付いている
            for (Throwable suppressed : e.getSuppressed()) {
                IO.println("  抑制された例外: " + suppressed.getMessage());
            }
        } finally {
            IO.println("処理済み " + second.getSuccessCount() + "件（finallyで必ず表示）");
        }
        IO.println("在庫: " + inventory);
    }

    private static void printSummary(OrderProcessor processor, Inventory inventory) {
        IO.println("--- 結果 ---");
        IO.println("成功 " + processor.getSuccessCount() + "件、失敗 " + processor.getFailures().size() + "件");
        for (OrderException e : processor.getFailures()) {
            IO.println("  " + e.getLineNumber() + "行目 " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        IO.println("在庫: " + inventory);
    }
}
