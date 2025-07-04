/**
 * 第1章 課題1: Hello World の拡張 - 解答例
 * 
 * 自分の名前と現在の年を表示するプログラムを作成してください。
 * 
 * 学習ポイント:
 * - System.out.println()の使い方
 * - 文字列リテラルの記述方法
 * - 複数行の出力
 */
public class Exercise01_HelloWorld {
    public static void main(String[] args) {
        // 解答例 1: 直接文字列を出力（最もシンプル）
        System.out.println("こんにちは！私の名前は田中太郎です。");
        System.out.println("今年は2025年です。");
        System.out.println("Java学習を開始します！");
        
        System.out.println(); // 空行を挿入
        
        // 解答例 2: 変数を使った実装（推奨）
        String name = "田中太郎";
        int year = 2025;
        
        System.out.println("こんにちは！私の名前は" + name + "です。");
        System.out.println("今年は" + year + "年です。");
        System.out.println("Java学習を開始します！");
        
        System.out.println(); // 空行を挿入
        
        // 解答例 3: String.format()を使った実装（上級者向け）
        String message = String.format("こんにちは！私の名前は%sです。%n今年は%d年です。%nJava学習を開始します！", 
                                      name, year);
        System.out.println(message);
    }
}

/*
 * よくある間違いとその対策:
 * 
 * 1. セミコロン忘れ
 *    × System.out.println("Hello")  // セミコロンがない
 *    ○ System.out.println("Hello"); // セミコロンが必要
 * 
 * 2. 二重引用符の間違い
 *    × System.out.println('Hello'); // 単一引用符は文字用
 *    ○ System.out.println("Hello"); // 文字列は二重引用符
 * 
 * 3. 大文字小文字の間違い
 *    × system.out.println("Hello"); // systemは小文字で始まる
 *    ○ System.out.println("Hello"); // Systemは大文字で始まる
 * 
 * 4. クラス名とファイル名の不一致
 *    - publicクラスの名前とファイル名は必ず一致させる
 *    - Exercise01_HelloWorld.java → public class Exercise01_HelloWorld
 * 
 * デバッグのポイント:
 * - コンパイルエラーが出た場合は、エラーメッセージを確認
 * - 行番号を確認して、その周辺をチェック
 * - IDEの構文ハイライトを活用する
 */