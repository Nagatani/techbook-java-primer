/**
 * 第11章 発展課題1: レジの割引ルール（解答例）
 *
 * 割引ルールを独自の関数型インターフェイスDiscountRuleで表し、
 * ラムダ式・静的メソッド参照・特定のインスタンスのメソッド参照で登録します。
 */
public class DiscountApp {
    public static void main(String[] args) {
        MemberCard card = new MemberCard("田中", 5);

        Register register = new Register();
        register.addRule("パン3個割", Discounts::breadBundle);          // 静的メソッド参照
        register.addRule("弁当セット割", Discounts::lunchSet);
        register.addRule("1000円以上で100円引き",
                cart -> cart.total() >= 1000 ? 100 : 0);                // ラムダ式
        register.addRule(card.getOwner() + "さんの会員割引(5%)", card::discount);  // 特定のインスタンスのメソッド参照

        IO.println("=== 1人目 ===");
        Cart cart1 = new Cart();
        cart1.add(new Item("あんパン", "パン", 150));
        cart1.add(new Item("食パン", "パン", 230));
        cart1.add(new Item("メロンパン", "パン", 160));
        cart1.add(new Item("牛乳", "飲み物", 180));
        register.checkout(cart1);

        IO.println();
        IO.println("=== 2人目 ===");
        Cart cart2 = new Cart();
        cart2.add(new Item("のり弁当", "弁当", 480));
        cart2.add(new Item("唐揚げ弁当", "弁当", 550));
        cart2.add(new Item("お茶", "飲み物", 130));
        register.checkout(cart2);

        IO.println();
        IO.println("=== ルールを組み合わせる ===");
        // 2つのセット割を合わせ、上限を120円にしたルール
        DiscountRule bread = Discounts::breadBundle;
        DiscountRule setDiscounts = bread.plus(Discounts::lunchSet).upTo(120);
        Register sale = new Register();
        sale.addRule("セット割(上限120円)", setDiscounts);
        Cart cart3 = new Cart();
        cart3.add(new Item("あんパン", "パン", 150));
        cart3.add(new Item("クリームパン", "パン", 150));
        cart3.add(new Item("カレーパン", "パン", 180));
        cart3.add(new Item("幕の内弁当", "弁当", 600));
        cart3.add(new Item("お茶", "飲み物", 130));
        sale.checkout(cart3);
    }
}
