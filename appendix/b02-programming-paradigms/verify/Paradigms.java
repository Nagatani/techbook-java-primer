import java.util.stream.IntStream;

/** 1から10までの和を、命令型・構造化・オブジェクト指向・関数型の書き方で求める */
public class Paradigms {
    /** オブジェクト指向：状態（合計）と、それを変える操作をまとめる */
    static class Accumulator {
        private int total = 0;

        void add(int value) {
            total += value;
        }

        int total() {
            return total;
        }
    }

    /** 構造化：入口と出口が1つずつの手続きに分ける */
    static int calculateSum(int n) {
        if (n <= 0) {
            return 0;
        }
        int sum = 0;
        for (int i = 1; i <= n; i++) {
            sum += i;
        }
        return sum;
    }

    public static void main(String[] args) {
        // 命令型：変数の状態を1つずつ書き換えて計算する
        int sum = 0;
        for (int i = 1; i <= 10; i++) {
            sum += i;
        }
        IO.println("命令型: " + sum);

        IO.println("構造化: " + calculateSum(10));

        Accumulator acc = new Accumulator();
        for (int i = 1; i <= 10; i++) {
            acc.add(i);
        }
        IO.println("オブジェクト指向: " + acc.total());

        // 関数型：値の列に関数を当てはめ、変数を書き換えない
        IO.println("関数型: " + IntStream.rangeClosed(1, 10).reduce(0, Integer::sum));
    }
}
