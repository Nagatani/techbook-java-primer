/**
 * 食品（軽減税率8%）。消費期限の近い弁当などに値引きシールを貼れる
 */
public class Food extends Item implements Discountable {
    private final int discountPercent;

    public Food(String code, String name, int price, int discountPercent) {
        super(code, name, price);
        // 範囲外の値引き率は値引きなしとして扱う
        this.discountPercent = Discountable.isValidPercent(discountPercent) ? discountPercent : 0;
    }

    @Override
    public int taxRate() {
        return 8;
    }

    @Override
    public String category() {
        return "食品";
    }

    @Override
    public int discountPercent() {
        return discountPercent;
    }

    // 売る価格は、Discountableのdefaultメソッドで値引きした金額
    @Override
    public int salePrice() {
        return applyDiscount(getPrice());
    }

    @Override
    public String receiptLine() {
        if (!isDiscounted()) {
            return super.receiptLine();
        }
        return super.receiptLine() + "（" + discountPercent + "%引き）";
    }
}
