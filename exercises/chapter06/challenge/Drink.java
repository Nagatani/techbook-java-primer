import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * カフェのドリンク1杯。ビルダーで組み立てる不変クラス
 *
 * 必須の値（名前・基本の値段）はBuilderのコンストラクターで、
 * 省略できる値（サイズ・ショット・ミルク・トッピング・アイス）はメソッドの連結で指定する。
 */
public final class Drink {
    public static final String SIZE_S = "S";
    public static final String SIZE_M = "M";
    public static final String SIZE_L = "L";
    public static final String MILK_REGULAR = "普通";
    public static final String MILK_SOY = "豆乳";
    public static final String MILK_NONE = "なし";
    public static final int MAX_SHOTS = 2;
    public static final int MAX_TOPPINGS = 3;

    private static final int SHOT_PRICE = 60;
    private static final int SOY_PRICE = 50;
    private static final int TOPPING_PRICE = 50;

    private final String name;
    private final int basePrice;
    private final String size;
    private final int shots;
    private final String milk;
    private final List<String> toppings;
    private final boolean iced;

    // コンストラクターはprivate。Builderのbuild()からだけ呼ばれる
    private Drink(Builder builder) {
        this.name = builder.name;
        this.basePrice = builder.basePrice;
        this.size = builder.size;
        this.shots = builder.shots;
        this.milk = builder.milk;
        this.toppings = new ArrayList<>(builder.toppings);  // ビルダーのリストとは別のリストにする
        this.iced = builder.iced;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public int getShots() {
        return shots;
    }

    public String getMilk() {
        return milk;
    }

    public List<String> getToppings() {
        return new ArrayList<>(toppings);
    }

    public boolean isIced() {
        return iced;
    }

    // 値段 = 基本の値段 + サイズの差額 + 追加の料金
    public int price() {
        int price = basePrice + sizeDifference(size) + shots * SHOT_PRICE + toppings.size() * TOPPING_PRICE;
        if (MILK_SOY.equals(milk)) {
            price += SOY_PRICE;
        }
        return price;
    }

    // この1杯と同じ内容が入ったビルダーを返す。一部だけ変えた1杯を作るときに使う
    public Builder toBuilder() {
        Builder builder = new Builder(name, basePrice)
                .size(size)
                .shots(shots)
                .milk(milk)
                .iced(iced);
        for (String topping : toppings) {
            builder.addTopping(topping);
        }
        return builder;
    }

    private static int sizeDifference(String size) {
        if (SIZE_S.equals(size)) {
            return -50;
        }
        if (SIZE_L.equals(size)) {
            return 60;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Drink other = (Drink) obj;
        return basePrice == other.basePrice && shots == other.shots && iced == other.iced
                && name.equals(other.name) && size.equals(other.size)
                && milk.equals(other.milk) && toppings.equals(other.toppings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, basePrice, size, shots, milk, toppings, iced);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(iced ? "アイス" : "ホット").append(name).append("(").append(size).append(")");
        if (shots > 0) {
            sb.append(" ショット+").append(shots);
        }
        if (!MILK_REGULAR.equals(milk)) {
            sb.append(" ミルク:").append(milk);
        }
        if (!toppings.isEmpty()) {
            sb.append(" ").append(toppings);
        }
        return sb.append(" ").append(price()).append("円").toString();
    }

    /**
     * Drinkを組み立てるビルダー（静的ネストクラス。くわしくは第8章で学ぶ）
     */
    public static class Builder {
        private final String name;
        private final int basePrice;
        private String size = SIZE_M;
        private int shots = 0;
        private String milk = MILK_REGULAR;
        private final List<String> toppings = new ArrayList<>();
        private boolean iced = false;

        public Builder(String name, int basePrice) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("ドリンクの名前を指定してください");
            }
            if (basePrice <= 0) {
                throw new IllegalArgumentException("基本の値段は1円以上にしてください");
            }
            this.name = name;
            this.basePrice = basePrice;
        }

        public Builder size(String size) {
            if (!SIZE_S.equals(size) && !SIZE_M.equals(size) && !SIZE_L.equals(size)) {
                throw new IllegalArgumentException("サイズはS・M・Lのどれかにしてください");
            }
            this.size = size;
            return this;
        }

        public Builder shots(int shots) {
            if (shots < 0 || shots > MAX_SHOTS) {
                throw new IllegalArgumentException("追加のショットは0〜" + MAX_SHOTS + "にしてください");
            }
            this.shots = shots;
            return this;
        }

        public Builder milk(String milk) {
            if (!MILK_REGULAR.equals(milk) && !MILK_SOY.equals(milk) && !MILK_NONE.equals(milk)) {
                throw new IllegalArgumentException("ミルクは普通・豆乳・なしのどれかにしてください");
            }
            this.milk = milk;
            return this;
        }

        public Builder addTopping(String topping) {
            if (topping == null || topping.isBlank()) {
                throw new IllegalArgumentException("トッピングの名前を指定してください");
            }
            toppings.add(topping);
            return this;
        }

        public Builder iced(boolean iced) {
            this.iced = iced;
            return this;
        }

        // 値の組み合わせはここでまとめて確かめる
        public Drink build() {
            if (toppings.size() > MAX_TOPPINGS) {
                throw new IllegalStateException("トッピングは" + MAX_TOPPINGS + "つまでです");
            }
            if (MILK_NONE.equals(milk) && toppings.contains("ホイップ")) {
                throw new IllegalStateException("ミルクなしのドリンクにはホイップを載せられません");
            }
            return new Drink(this);
        }
    }
}
