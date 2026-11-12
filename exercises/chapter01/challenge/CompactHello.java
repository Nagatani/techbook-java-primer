// 第1章 チャレンジ課題1: この課題の出発点になるプログラム（このファイルは書き換えない）
//
// Java 25のコンパクトソースファイルで書いたプログラムです。
// クラスの宣言がなく、mainメソッドもpublicやstatic、String[] argsを省いています。
// このプログラムと同じ動きをするプログラムを、本書の書き方（クラスを宣言し、
// public static void main(String[] args)から実行する形）でClassHello.javaに書いてください。

String greeting = "ようこそ、Javaの世界へ";

void main() {
    String name = IO.readln("お名前を入力: ");
    IO.println(greeting);
    IO.println(name + "さん、最初のプログラムを動かしてみましょう");
}
