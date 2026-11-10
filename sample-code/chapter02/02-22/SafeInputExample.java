public class SafeInputExample {
    public static void main(String[] args) {
        while (true) {
            String input = IO.readln("整数を入力してください: ");
            try {
                int number = Integer.parseInt(input.strip());
                IO.println("入力された数値: " + number);
                break;
            } catch (NumberFormatException e) {
                IO.println("エラー: 整数を入力してください");
            }
        }
    }
}
