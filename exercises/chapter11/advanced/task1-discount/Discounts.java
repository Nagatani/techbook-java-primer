/**
 * 店の決まった割引。どれもDiscountRuleのdiscountと同じ形（Cartを受け取りintを返す）の静的メソッド
 */
public class Discounts {
    // パンを3個以上買うと100円引き
    public static int breadBundle(Cart cart) {
        return cart.countOf("パン") >= 3 ? 100 : 0;
    }

    // 弁当と飲み物を一緒に買うと、組の数 × 50円引き
    public static int lunchSet(Cart cart) {
        int sets = Math.min(cart.countOf("弁当"), cart.countOf("飲み物"));
        return sets * 50;
    }
}
