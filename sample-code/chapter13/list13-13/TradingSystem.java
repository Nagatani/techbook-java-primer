/**
 * リスト13-13
 * TradingSystemクラス
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (517行目)
 */

public class TradingSystem {
    // マーケットデータのストリーム処理
    public class MarketDataProcessor {
        // 価格変動の分析
        public Flux<TradingSignal> analyzeMarketData(Flux<MarketTick> ticks) {
            return ticks
                .window(Duration.ofSeconds(1))
                .flatMap(window -> window
                    .collect(Collectors.toList())
                    .map(this::calculateVolatility)
                )
                .filter(volatility -> volatility > THRESHOLD)
                .map(this::generateTradingSignal)
                .onBackpressureBuffer(1000)
                .publishOn(Schedulers.parallel());
        }
        
        // 複雑な取引戦略の組み合わせ
        public Function<MarketData, TradingDecision> combineStrategies(
            List<TradingStrategy> strategies) {
            
            return marketData -> strategies.stream()
                .map(strategy -> strategy.evaluate(marketData))
                .reduce(TradingDecision.NEUTRAL, 
                    TradingDecision::combine);
        }
    }
}