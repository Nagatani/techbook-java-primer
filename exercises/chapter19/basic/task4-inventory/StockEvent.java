import java.util.EventObject;

/**
 * 在庫の数が変わったことを知らせるイベント
 *
 * EventObjectを継承し、どこで起きたか（source）に加えて、商品名と変化の前後の数を持ちます。
 */
public class StockEvent extends EventObject {
    private final String item;
    private final int oldQuantity;
    private final int newQuantity;

    public StockEvent(Object source, String item, int oldQuantity, int newQuantity) {
        super(source);
        this.item = item;
        this.oldQuantity = oldQuantity;
        this.newQuantity = newQuantity;
    }

    public String getItem() {
        return item;
    }

    public int getOldQuantity() {
        return oldQuantity;
    }

    public int getNewQuantity() {
        return newQuantity;
    }

    /** 増えた数（減ったときは負の数） */
    public int getChange() {
        return newQuantity - oldQuantity;
    }
}
