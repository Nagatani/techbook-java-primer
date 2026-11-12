/**
 * 第1章 課題4: 入力した数で計算する - 解答例
 *
 * 学習ポイント:
 * - IO.readlnの戻り値は文字列なので、Integer.parseIntで整数に変換してから計算する
 * - formattedと%dで書式を指定して表示する
 */
public class Exercise04_PriceCalculation {
    public static void main(String[] args) {
        String input = IO.readln("1個の値段を入力: ");
        int price = Integer.parseInt(input);

        IO.println("1個%d円の商品を3個買うと%d円です".formatted(price, price * 3));
        IO.println("1個%d円の商品を5個買うと%d円です".formatted(price, price * 5));
    }
}

/*
 * よくある間違い:
 *    IO.println(input * 3);  // 文字列に * は使えないため、コンパイルエラーになる
 *    IO.println(input + 3);  // 文字列の連結になり、「1203」と表示される
 */
