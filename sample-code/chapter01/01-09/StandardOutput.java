public class StandardOutput {
    public static void main(String[] args) {
        IO.println("画面への出力には、「IO.printlnメソッド」を使用します。");
        IO.println("「IO.println」は末尾に改行コードも併せて出力します。");
        IO.println(); // 引数なしで改行のみ出力されます。
        IO.print("「IO.print」を使うと、末尾に改行は出力されません。");
        IO.print("改行は\\nで出力できます。\n");
        IO.print("\n");
        String message = "C言語のprintf関数のように書式を指定したい場合は、「formatted」を使います。";
        IO.println(message);
        IO.println("整数値: %d, 実数値: %f".formatted(10, 3.142592654d));
    }
}
