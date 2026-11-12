/**
 * 第1章 基本課題2: IO.printとIO.printlnで名刺を表示する
 *
 * 課題: IO.print、IO.println、引数なしのIO.println()を使い分けて、名刺のような表示を作成してください。
 *
 * 要求仕様:
 * - 枠の線（=====）を上下に表示する
 * - 「氏名:」と名前は、IO.printとIO.printlnを組み合わせて1行に表示する
 * - 枠の中に空行を1行入れる（引数なしのIO.println()）
 * - 最後の行は、IO.printと文字列の中の\nで改行する
 *
 * 実行例:
 * ==============================
 * 情報学部 1年
 * 氏名: 田中太郎
 *
 * 好きなこと: プログラミング
 * ==============================
 *
 * 評価ポイント:
 * - IO.printとIO.printlnの違い（最後に改行するかどうか）を理解している
 * - 引数なしのIO.println()で空行を出力できる
 * - \nで改行を出力できる
 */
public class Exercise02_BusinessCard {
    public static void main(String[] args) {
        IO.println("==============================");
        // TODO: 所属を表示してください

        // TODO: IO.print("氏名: "); と IO.println("名前"); を組み合わせて1行に表示してください

        // TODO: 空行を表示してください

        // TODO: 好きなことを表示してください

        // TODO: IO.print と \n を使って、下の枠の線を表示してください
    }
}
