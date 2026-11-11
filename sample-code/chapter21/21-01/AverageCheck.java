public class AverageCheck {
    // 2つの整数の平均を求める（この書き方には誤りがある）
    static double average(int a, int b) {
        return (a + b) / 2;
    }

    public static void main(String[] args) {
        // 書いた直後に確かめれば、原因はこのメソッドの中だとすぐにわかる
        double result = average(1, 2);
        IO.println("average(1, 2) = " + result);
        IO.println(result == 1.5 ? "OK" : "NG：1.5になるはず");
    }
}
