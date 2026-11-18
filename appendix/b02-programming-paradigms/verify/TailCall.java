import java.util.stream.LongStream;

/** 末尾再帰で書いても、深い再帰はスタックを使い切る */
public class TailCall {

    /** 末尾再帰：再帰呼び出しの結果をそのまま返す（呼び出しのあとに計算が残らない） */
    static long sumTo(long n, long acc) {
        if (n == 0) {
            return acc;
        }
        return sumTo(n - 1, acc + n);
    }

    /** 同じ計算をループで書く */
    static long sumToLoop(long n) {
        long acc = 0;
        while (n > 0) {
            acc += n;
            n--;
        }
        return acc;
    }

    static String trySum(long n) {
        try {
            return String.valueOf(sumTo(n, 0));
        } catch (StackOverflowError e) {
            return "StackOverflowError";
        }
    }

    public static void main(String[] args) {
        IO.println("末尾再帰 n=1000: " + trySum(1_000));
        IO.println("末尾再帰 n=10000000: " + trySum(10_000_000));
        IO.println("ループ n=10000000: " + sumToLoop(10_000_000));
        IO.println("Stream n=10000000: " + LongStream.rangeClosed(1, 10_000_000).sum());
    }
}
