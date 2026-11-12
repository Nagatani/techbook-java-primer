/**
 * 第1章 チャレンジ課題2: エラーを読んでプログラムを直す - 解答例
 *
 * READMEに載せたプログラムの誤りと直し方:
 * 1. ファイル名はFixErrors.javaなのに、クラス名がFixErrorだった → public class FixErrors にする
 * 2. mainがmianになっていた → main にする（コンパイルは通るが、実行時に「mainメソッドが見つからない」と言われる）
 * 3. io.println と書いていた → IO.println にする（大文字・小文字を区別する）
 * 4. 1行目の出力の最後にセミコロンがなかった → ; を付ける
 * 5. 文字列を単一引用符で囲んでいた → 二重引用符で囲む
 */
public class FixErrors {
    public static void main(String[] args) {
        IO.println("エラーを直せました");
        IO.println("コンパイルと実行の流れを確かめましょう");
    }
}
