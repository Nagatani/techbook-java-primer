/**
 * 販売できる商品が持つ能力（can-do）を表すインターフェイス
 */
public interface Sellable {
    // 消費税率（%）
    int TAX_RATE = 10;

    String getTitle();

    // 税抜きの価格。実装クラスで決める
    int price();

    // 税込みの価格。計算はstaticメソッドに任せる
    default int priceWithTax() {
        return withTax(price());
    }

    default String describe() {
        return "販売 " + priceWithTax() + "円（税込）";
    }

    // 税抜きの金額を税込みにする（1円未満は切り捨て）。オブジェクトがなくても使える
    static int withTax(int price) {
        return price * (100 + TAX_RATE) / 100;
    }
}
