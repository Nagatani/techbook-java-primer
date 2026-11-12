/**
 * 学食のメニュー。麺類はゆでるためにコンロを使う
 */
public enum Menu {
    CURRY("カレー", 450, false, 40),
    SET_MEAL("定食", 600, false, 80),
    RAMEN("ラーメン", 500, true, 100),
    UDON("うどん", 350, true, 70);

    private final String label;
    private final int price;
    private final boolean needsStove;
    private final int cookMillis;  // 調理にかかる時間（ミリ秒）

    Menu(String label, int price, boolean needsStove, int cookMillis) {
        this.label = label;
        this.price = price;
        this.needsStove = needsStove;
        this.cookMillis = cookMillis;
    }

    public String getLabel() {
        return label;
    }

    public int getPrice() {
        return price;
    }

    public boolean needsStove() {
        return needsStove;
    }

    public int getCookMillis() {
        return cookMillis;
    }
}
