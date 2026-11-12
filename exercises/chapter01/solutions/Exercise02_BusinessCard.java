/**
 * 第1章 課題2: IO.printとIO.printlnで名刺を表示する - 解答例
 *
 * 学習ポイント:
 * - IO.printlnは最後に改行し、IO.printは改行しない
 * - 引数なしのIO.println()は改行だけを出力する
 * - 文字列の中の\nは改行になる
 */
public class Exercise02_BusinessCard {
    public static void main(String[] args) {
        IO.println("==============================");
        IO.println("情報学部 1年");
        IO.print("氏名: ");
        IO.println("田中太郎");
        IO.println();
        IO.println("好きなこと: プログラミング");
        IO.print("==============================\n");
    }
}
