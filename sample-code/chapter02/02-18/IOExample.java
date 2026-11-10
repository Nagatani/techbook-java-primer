public class IOExample {
    public static void main(String[] args) {
        // === 出力メソッドの使い分け ===
        IO.println("Hello, World!");      // 改行付き出力
        IO.print("改行なし");             // 改行なし出力
        IO.print("出力\n");               // 手動で改行を追加

        // formatted による書式付き出力（C言語のprintf風）
        IO.println("整数: %d, 浮動小数点: %.2f".formatted(10, 3.14159));
        IO.println("文字列: %-10s | 右寄せ: %10s".formatted("左寄せ", "右寄せ"));

        // === IO.readln による入力処理 ===
        // 文字列入力（メッセージを表示してから1行を読み取る）
        String name = IO.readln("名前を入力してください: ");

        // 整数入力（エラー処理付き）
        int age = 0;
        boolean validInput = false;
        while (!validInput) {
            String input = IO.readln("年齢を入力してください: ");
            try {
                age = Integer.parseInt(input);
                validInput = true;
            } catch (NumberFormatException e) {
                IO.println("エラー: 整数を入力してください");
            }
        }

        // 入力値の確認出力
        IO.println("%sさん（%d歳）、こんにちは！".formatted(name, age));
    }
}
