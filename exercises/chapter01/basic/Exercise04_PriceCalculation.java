/**
 * 第1章 基本課題4: 入力した数で計算する
 *
 * 課題: 商品1個の値段を整数で入力してもらい、3個分と5個分の金額を表示するプログラムを作成してください。
 *
 * 要求仕様:
 * - IO.readlnで値段を文字列として読み、Integer.parseIntで整数に変換する
 * - 3個分と5個分の金額を、*（掛け算）で計算する
 * - 表示には文字列のformattedメソッドと%dを使う
 *
 * 実行例（「120」と入力した場合）:
 * 1個の値段を入力: 120
 * 1個120円の商品を3個買うと360円です
 * 1個120円の商品を5個買うと600円です
 *
 * 評価ポイント:
 * - IO.readlnの戻り値が文字列であり、計算の前に整数へ変換する必要があることを理解している
 * - formattedと%dで、C言語のprintfのように書式を指定して表示できる
 *
 * 補足:
 * - 数字以外を入力するとNumberFormatExceptionで止まります。この課題では気にしなくてかまいません
 *   （第1章のコラム「Integer.parseIntのエラーから学ぶ」を参照）
 */
public class Exercise04_PriceCalculation {
    public static void main(String[] args) {
        // TODO: 値段を読み、整数に変換してください
        // String input = IO.readln("1個の値段を入力: ");
        // int price = Integer.parseInt(input);

        // TODO: 3個分と5個分の金額を表示してください
        // 例: IO.println("1個%d円の商品を3個買うと%d円です".formatted(price, price * 3));
    }
}
