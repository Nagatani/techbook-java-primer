// 市場データ（価格と変動率）
class MarketData {
    private final double price;
    private final double volatility;

    MarketData(double price, double volatility) {
        this.price = price;
        this.volatility = volatility;
    }

    double getPrice() { return price; }
    double getVolatility() { return volatility; }
}

enum TradeType { BUY, SELL, HOLD }

// 取引の判断
class TradingDecision {
    static final TradingDecision HOLD = new TradingDecision(TradeType.HOLD);

    private final TradeType type;

    TradingDecision(TradeType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TradingDecision{" + type + "}";
    }
}
