import java.util.Arrays;  // Arraysクラスを使用するための宣言

public class ImportExample {
    public static void main(String[] args) {
        // 空白で区切った整数を1行で入力してもらう
        String input = IO.readln("整数を空白で区切って入力してください: ");
        String[] items = input.split(" ");

        // 文字列の配列を整数の配列に変換
        int[] numbers = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            numbers[i] = Integer.parseInt(items[i]);
        }

        // java.util.Arraysのメソッドで並べ替えて表示
        Arrays.sort(numbers);
        IO.println("並べ替えた結果: " + Arrays.toString(numbers));
    }
}
