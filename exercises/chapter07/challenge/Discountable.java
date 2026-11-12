/**
 * 値引きできる商品が持つ能力を表すインターフェイス
 */
public interface Discountable {
    // 値引き率（%）。0なら値引きしない
    int discountPercent();

    // 値引き後の金額（1円未満は切り捨て）
    default int applyDiscount(int price) {
        return price * (100 - discountPercent()) / 100;
    }

    default boolean isDiscounted() {
        return discountPercent() > 0;
    }

    // 値引き率として使える値か（0〜50%）。オブジェクトを作る前に確かめるのでstaticにする
    static boolean isValidPercent(int percent) {
        return percent >= 0 && percent <= 50;
    }
}
