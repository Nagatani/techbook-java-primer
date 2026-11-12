/**
 * 商品の税込価格を計算するライブラリです。
 *
 * <h2>変更履歴</h2>
 * <ul>
 *   <li>1.1: 軽減税率に対応した{@link price.TaxRate}と
 *       {@link price.PriceCalculator#withTax(int, TaxRate)}を追加。
 *       {@link price.PriceCalculator#withTax(int)}を非推奨にした</li>
 *   <li>1.0: 最初の版</li>
 * </ul>
 *
 * @since 1.0
 */
package price;
