/**
 * 第2章 基本課題7: 文字列の操作
 *
 * 課題: キーボードから入力した英文を、Stringのメソッドを使って調べてください。
 *
 * 要求仕様:
 * - IO.readlnで1行の英文を読み、前後の空白をtrimで取り除く
 * - 文字数、大文字にした文字列、最初の単語（最初の空白より前）を表示する
 * - 「Java」という語を含むかどうかを表示する
 * - 入力が「exit」かどうかを、大文字・小文字を区別せずに調べて表示する
 * - StringBuilderを使って、文字の並びを逆にした文字列を作って表示する
 *
 * 実行例（「 I like Java programming 」と入力した場合）:
 * 英文を入力: I like Java programming
 * 入力: I like Java programming
 * 文字数: 23
 * 大文字: I LIKE JAVA PROGRAMMING
 * 最初の単語: I
 * Javaを含むか: true
 * exitか: false
 * 逆順: gnimmargorp avaJ ekil I
 *
 * 評価ポイント:
 * - 文字列の比較に==ではなくequals・equalsIgnoreCaseを使っている
 * - 空白を含まない入力でも最初の単語を表示できる
 * - ループの中の連結にStringBuilderを使っている
 */
public class Exercise07_Strings {
    public static void main(String[] args) {
        String line = IO.readln("英文を入力: ");
        if (line == null) {
            IO.println("入力がありません");
            return;
        }
        // TODO: trimで前後の空白を取り除いてください

        // TODO: 実行例のように表示してください
        // ヒント: 最初の単語は indexOf(" ") と substring で取り出せます
        //         空白がないとき indexOf は -1 を返します
    }
}
