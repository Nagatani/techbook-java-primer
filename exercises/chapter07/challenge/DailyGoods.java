/**
 * 日用品（標準税率10%）
 */
public class DailyGoods extends Item {
    public DailyGoods(String code, String name, int price) {
        super(code, name, price);
    }

    @Override
    public int taxRate() {
        return 10;
    }

    @Override
    public String category() {
        return "日用品";
    }
}
