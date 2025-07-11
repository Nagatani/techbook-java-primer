/**
 * リスト1-16
 * StandardOutputクラス
 * 
 * 元ファイル: chapter01-introduction.md (712行目)
 */

public class StandardOutput {
    public static void main(String[] args) {
        System.out.println("標準出力は、「System.out.printlnメソッド」を使用します。");
        System.out.println("「System.out.println」は末尾に改行コードも併せて出力します。");
        System.out.println(); // 引数なしで改行のみ出力されます。
        System.out.print("「System.out.print」を使うと、末尾に改行は出力されません。");
        System.out.print("改行は\\nで出力できます。\n");
        System.out.print("\n");
        String message = "C言語のprintf関数のように出力したい場合は、「System.out.printf」を使います。";
        System.out.printf("%s\n", message);
        System.out.printf("整数値: %d, 実数値: %f\n", 10, 3.142592654d);
    }
}