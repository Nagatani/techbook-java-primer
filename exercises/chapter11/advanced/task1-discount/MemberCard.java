/**
 * 会員カード。会員ごとに割引率（%）が違う
 */
public class MemberCard {
    private final String owner;
    private final int rate;

    public MemberCard(String owner, int rate) {
        this.owner = owner;
        this.rate = rate;
    }

    public String getOwner() {
        return owner;
    }

    // 合計金額の rate % を割り引く（1円未満は切り捨て）
    public int discount(Cart cart) {
        return cart.total() * rate / 100;
    }
}
