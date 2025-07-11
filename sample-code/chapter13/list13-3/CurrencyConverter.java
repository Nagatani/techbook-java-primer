/**
 * リスト13-3
 * CurrencyConverterクラス
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (118行目)
 */

public class CurrencyConverter {
    // 通常の2引数から3引数の変換で、通貨レートを適用
    public Function<String, Function<String, Function<Double, Double>>> 
        curriedConvert = from -> to -> amount -> {
            double rate = getExchangeRate(from, to);
            return amount * rate;
        };
    
    // 使用例
    public void demonstrateCurrying() {
        // USDからJPYへの変換関数を作成
        Function<Double, Double> usdToJpy = curriedConvert("USD", "JPY");
        
        // 同じ変換を何度も使える
        System.out.println(usdToJpy.apply(100.0));  // 15000.0
        System.out.println(usdToJpy.apply(250.0));  // 37500.0
        
        // 複数の変換関数をマップで管理
        Map<String, Function<Double, Double>> converters = Map.of(
            "USD_TO_JPY", curriedConvert("USD", "JPY"),
            "EUR_TO_JPY", curriedConvert("EUR", "JPY"),
            "GBP_TO_JPY", curriedConvert("GBP", "JPY")
        );
    }
}