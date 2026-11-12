package demo;

import price.PriceCalculator;

/**
 * 1.0のAPIを使っていたプログラム。1.1でコンパイルすると警告が出る例
 */
public class OldClient {
    private OldClient() {
    }

    /**
     * 非推奨のメソッドを呼ぶ。
     *
     * @param args 使わない
     */
    @SuppressWarnings("removal") // 移行するまでの間だけ警告を抑える
    public static void main(String[] args) {
        IO.println("緑茶 128円 → " + PriceCalculator.withTax(128) + "円");
        IO.println("ノート 220円 → " + PriceCalculator.withTax(220) + "円");
    }
}
