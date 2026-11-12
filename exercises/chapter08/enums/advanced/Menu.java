/**
 * 飲み物のメニュー。名前と基本の価格（Sサイズ）を持つ
 */
public enum Menu {
    COFFEE("ドリップコーヒー", 300),
    LATTE("カフェラテ", 400),
    MATCHA_LATTE("抹茶ラテ", 450),
    ICED_TEA("アイスティー", 330);

    private final String displayName;
    private final int basePrice;

    Menu(String displayName, int basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    // サイズを考えた価格
    public int priceFor(Size size) {
        return basePrice + size.getExtraCharge();
    }
}
