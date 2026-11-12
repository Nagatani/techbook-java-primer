/**
 * 第1章 課題3: キーボードから名前を読んであいさつする - 解答例
 *
 * 学習ポイント:
 * - IO.readlnはメッセージを表示してから、Enterキーまでの1行を文字列として返す
 * - 文字列は + でつなげる
 */
public class Exercise03_Greeting {
    public static void main(String[] args) {
        String name = IO.readln("お名前を入力: ");
        String hometown = IO.readln("出身地を入力: ");

        IO.println("こんにちは、" + name + "さん。");
        IO.println(hometown + "の出身なのですね。Javaの学習を一緒に始めましょう。");
    }
}
