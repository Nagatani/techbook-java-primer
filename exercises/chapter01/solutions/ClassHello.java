/**
 * 第1章 チャレンジ課題1: コンパクトソースファイルをクラスを宣言する形に書き直す - 解答例
 *
 * CompactHello.javaとの違い:
 * - クラスの宣言（public class ClassHello）を書いた
 * - mainメソッドを public static void main(String[] args) にした
 * - フィールドgreetingは、mainメソッドの中の変数にした
 *   （staticメソッドからstaticでないフィールドを直接使うことはできない。くわしくは第3章で学ぶ）
 */
public class ClassHello {
    public static void main(String[] args) {
        String greeting = "ようこそ、Javaの世界へ";
        String name = IO.readln("お名前を入力: ");
        IO.println(greeting);
        IO.println(name + "さん、最初のプログラムを動かしてみましょう");
    }
}
