package demo;

import price.PriceCalculator;
import price.TaxRate;

/**
 * 1.1の新しいAPIでレシートを作るプログラム
 */
public class ReceiptDemo {
    private ReceiptDemo() {
    }

    /**
     * 商品ごとに税率を変えて合計を表示する。
     *
     * @param args 使わない
     */
    public static void main(String[] args) {
        String[] names = {"おにぎり", "緑茶", "ノート", "ボールペン"};
        int[] prices = {150, 128, 220, 110};
        TaxRate[] rates = {TaxRate.REDUCED, TaxRate.REDUCED, TaxRate.STANDARD, TaxRate.STANDARD};

        int total = 0;
        for (int i = 0; i < names.length; i++) {
            int withTax = PriceCalculator.withTax(prices[i], rates[i]);
            IO.println("%s %d円 → %d円（税率%d%%）".formatted(
                names[i], prices[i], withTax, rates[i].percent()));
            total += withTax;
        }
        IO.println("合計 " + total + "円");
        IO.println("緑茶の消費税: " + PriceCalculator.taxOf(128, TaxRate.REDUCED) + "円");
    }
}
