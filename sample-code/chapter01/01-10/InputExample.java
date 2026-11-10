public class InputExample {
    public static void main(String[] args) {
        // メッセージを表示して、1行分の文字列を入力してもらう
        String input = IO.readln("整数値を入力: ");

        // 文字列を整数に変換
        int number = Integer.parseInt(input);

        // 変換した値を使った処理の例
        IO.println("入力された値: " + number);
        IO.println("2倍の値: " + (number * 2));
    }
}
