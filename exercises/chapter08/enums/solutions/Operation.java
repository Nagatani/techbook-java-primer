/**
 * 四則演算（課題3の解答例）
 *
 * 計算の中身は列挙子ごとに違うので、抽象メソッドapplyを宣言し、列挙子ごとに{ }の中で実装する
 * （第8章「列挙子ごとのメソッド実装」）。
 */
public enum Operation {
    ADD("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    SUBTRACT("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    MULTIPLY("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }

        // 割る数が0なら計算できない
        @Override
        public boolean canApply(double x, double y) {
            return y != 0;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public abstract double apply(double x, double y);

    // 計算できるかどうか。ふつうはいつでも計算できるので、DIVIDEだけがオーバーライドする
    public boolean canApply(double x, double y) {
        return true;
    }

    // 記号から列挙子を探す。見つからなければnullを返す
    public static Operation fromSymbol(String symbol) {
        for (Operation op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        return null;
    }

    // 「10.0 + 5.0 = 15.0」の形の式。計算できないときはエラーの文にする
    public String format(double x, double y) {
        String expression = x + " " + symbol + " " + y + " = ";
        if (!canApply(x, y)) {
            return expression + "エラー: 0で割ることはできません";
        }
        return expression + apply(x, y);
    }
}
