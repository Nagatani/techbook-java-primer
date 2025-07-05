/**
 * 抽象メソッドを持つEnum
 * 計算機の四則演算を表現
 */
public enum OperationEnum {
    /** 加算 */
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    /** 減算 */
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    /** 乗算 */
    TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    /** 除算 */
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            if (y == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return x / y;
        }
    },
    /** 剰余 */
    MODULO("%") {
        @Override
        public double apply(double x, double y) {
            if (y == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return x % y;
        }
    },
    /** 冪乗 */
    POWER("^") {
        @Override
        public double apply(double x, double y) {
            return Math.pow(x, y);
        }
    };
    
    private final String symbol;
    
    OperationEnum(String symbol) {
        this.symbol = symbol;
    }
    
    /**
     * 演算を実行する抽象メソッド
     * @param x 第1オペランド
     * @param y 第2オペランド
     * @return 演算結果
     */
    public abstract double apply(double x, double y);
    
    /**
     * 演算子記号を取得
     * @return 演算子記号
     */
    public String getSymbol() {
        return symbol;
    }
    
    /**
     * 記号から対応する演算を取得
     * @param symbol 演算子記号
     * @return 対応する演算
     * @throws IllegalArgumentException 対応する演算がない場合
     */
    public static OperationEnum fromSymbol(String symbol) {
        for (OperationEnum op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operation: " + symbol);
    }
    
    /**
     * 演算の優先順位を取得
     * @return 優先順位（高いほど優先）
     */
    public int getPriority() {
        return switch (this) {
            case PLUS, MINUS -> 1;
            case TIMES, DIVIDE, MODULO -> 2;
            case POWER -> 3;
        };
    }
    
    /**
     * 結合順序を取得
     * @return 右結合の場合true
     */
    public boolean isRightAssociative() {
        return this == POWER;
    }
    
    /**
     * 演算の日本語名を取得
     * @return 日本語名
     */
    public String getJapaneseName() {
        return switch (this) {
            case PLUS -> "加算";
            case MINUS -> "減算";
            case TIMES -> "乗算";
            case DIVIDE -> "除算";
            case MODULO -> "剰余";
            case POWER -> "冪乗";
        };
    }
    
    @Override
    public String toString() {
        return symbol;
    }
}