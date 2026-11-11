public class SafeInputExample {
    public static void main(String[] args) {
        int number = 0;
        boolean valid = false;
        while (!valid) {
            String input = IO.readln("整数を入力してください: ");
            try {
                number = Integer.parseInt(input.strip());
                valid = true;  // ここに来たのは変換できたとき
            } catch (NumberFormatException e) {
                IO.println("エラー: 整数を入力してください");
            }
        }
        IO.println("入力された数値: " + number);
    }
}
