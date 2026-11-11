public class IOExample {
    public static void main(String[] args) {
        // formatted による書式付き出力（C言語のprintf風）
        IO.println("整数: %d, 浮動小数点: %.2f".formatted(10, 3.14159));
        IO.println("文字列: %-10s | 右寄せ: %10s".formatted("左寄せ", "右寄せ"));

        // === IO.readln による入力処理 ===
        // 文字列入力（メッセージを表示してから1行を読み取る）
        String name = IO.readln("名前を入力してください: ");

        // 整数入力（読み取った文字列を整数に変換する）
        String input = IO.readln("年齢を入力してください: ");
        int age = Integer.parseInt(input);

        // 入力値の確認出力
        IO.println("%sさん（%d歳）、こんにちは！".formatted(name, age));
    }
}
