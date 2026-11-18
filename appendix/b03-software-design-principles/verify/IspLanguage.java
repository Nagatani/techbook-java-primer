import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * インターフェイス分離の原則：言語の構文が求めるのは小さなインターフェイスだけである。
 * 拡張for文はIterableを、try-with-resources文はAutoCloseableを実装していれば使える。
 * 実行: javac IspLanguage.java && java IspLanguage
 * 観察: javap -c -p IspLanguage
 */
public class IspLanguage {
    // Iterableだけを実装したクラス（CollectionやListは実装しない）
    static class Countdown implements Iterable<Integer> {
        private final int from;

        Countdown(int from) {
            this.from = from;
        }

        @Override
        public Iterator<Integer> iterator() {
            return new Iterator<>() {
                private int next = from;

                @Override
                public boolean hasNext() {
                    return next > 0;
                }

                @Override
                public Integer next() {
                    if (next <= 0) {
                        throw new NoSuchElementException();
                    }
                    return next--;
                }
            };
        }
    }

    // AutoCloseableだけを実装したクラス
    static class Session implements AutoCloseable {
        Session() {
            IO.println("開く");
        }

        @Override
        public void close() {
            IO.println("閉じる");
        }
    }

    static void loop(Countdown countdown) {
        for (int n : countdown) {
            IO.print(n + " ");
        }
        IO.println("");
    }

    public static void main(String[] args) {
        loop(new Countdown(3));
        try (Session s = new Session()) {
            IO.println("使う");
        }
    }
}
