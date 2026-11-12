/**
 * 割引ルール。買い物かごを受け取り、割引額（円）を返す
 *
 * 抽象メソッドが1つだけなので、ラムダ式やメソッド参照で実装できる。
 */
@FunctionalInterface
public interface DiscountRule {
    int discount(Cart cart);

    // 2つのルールの割引額を足した、新しいルールを返す
    default DiscountRule plus(DiscountRule other) {
        return cart -> discount(cart) + other.discount(cart);
    }

    // 割引額に上限を付けた、新しいルールを返す
    default DiscountRule upTo(int limit) {
        return cart -> Math.min(discount(cart), limit);
    }
}
