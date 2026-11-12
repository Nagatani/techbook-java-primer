/**
 * 第1章 課題1: Hello World の拡張 - 解答例
 *
 * 自分の名前と現在の年を表示するプログラムを作成してください。
 *
 * 学習ポイント:
 * - IO.println()の使い方
 * - 文字列リテラルの記述方法
 * - 複数行の出力
 */
public class Exercise01_HelloWorld {
    public static void main(String[] args) {
        // 解答例 1: 直接文字列を出力（もっともシンプル）
        IO.println("こんにちは！私の名前は田中太郎です。");
        IO.println("今年は2026年です。");
        IO.println("Java学習を開始します！");

        IO.println(); // 空行を挿入

        // 解答例 2: formattedで書式を指定する（第1章「書式を指定した出力」）
        IO.println("こんにちは！私の名前は%sです。".formatted("田中太郎"));
        IO.println("今年は%d年です。".formatted(2026));
        IO.println("Java学習を開始します！");
    }
}

/*
 * よくある間違いとその対策:
 *
 * 1. セミコロン忘れ
 *    × IO.println("Hello")  // セミコロンがない
 *    ○ IO.println("Hello"); // セミコロンが必要
 *
 * 2. 引用符の間違い
 *    × IO.println('Hello'); // 単一引用符は1文字用
 *    ○ IO.println("Hello"); // 文字列は二重引用符
 *
 * 3. 大文字小文字の間違い
 *    × io.println("Hello"); // ioは見つからない
 *    ○ IO.println("Hello"); // クラス名IOはすべて大文字
 *
 * 4. クラス名とファイル名の不一致
 *    - publicクラスの名前とファイル名は必ず一致させる
 *    - Exercise01_HelloWorld.java → public class Exercise01_HelloWorld
 *
 * コンパイルと実行:
 *    javac Exercise01_HelloWorld.java   （Exercise01_HelloWorld.classができる）
 *    java Exercise01_HelloWorld         （.classは付けない）
 */
