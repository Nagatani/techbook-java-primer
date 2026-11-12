/**
 * 割引の種類。割引の計算方法が列挙子ごとに違うので、抽象メソッドを列挙子ごとに実装する
 */
public enum Discount {
    NONE("割引なし") {
        @Override
        public int apply(int price) {
            return price;
        }
    },
    STUDENT("学割10%") {
        @Override
        public int apply(int price) {
            return price * 90 / 100;
        }
    },
    MY_BOTTLE("マイボトル30円引き") {
        @Override
        public int apply(int price) {
            return price - 30;
        }
    },
    SECOND_CUP("2杯目半額") {
        @Override
        public int apply(int price) {
            return price / 2;
        }
    };

    private final String description;

    Discount(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 割引後の価格。列挙子ごとに実装する
    public abstract int apply(int price);
}
