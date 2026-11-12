/** 在庫が足りないときの例外 */
public class OutOfStockException extends OrderException {
    private final String itemCode;
    private final int requested;
    private final int stock;

    public OutOfStockException(String itemCode, int requested, int stock, int lineNumber) {
        super("在庫が足りません（" + itemCode + "、注文 " + requested + "個、在庫 " + stock + "個）",
                lineNumber);
        this.itemCode = itemCode;
        this.requested = requested;
        this.stock = stock;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getShortage() {
        return requested - stock;
    }
}
