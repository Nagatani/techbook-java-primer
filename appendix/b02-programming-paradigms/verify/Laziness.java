import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/** Javaの評価の順序（引数は先に評価する）と、Supplier・Streamによる遅延評価を観察する */
public class Laziness {

    static String expensive(String label) {
        IO.println("  " + label + " を計算した");
        return label;
    }

    /** 1回だけ計算して結果を覚える遅延値 */
    static final class Lazy<T> {
        private Supplier<T> supplier;
        private T value;
        private boolean evaluated;

        Lazy(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        T get() {
            if (!evaluated) {
                value = supplier.get();
                evaluated = true;
                supplier = null;
            }
            return value;
        }

        <R> Lazy<R> map(Function<T, R> f) {
            return new Lazy<>(() -> f.apply(get()));
        }
    }

    /** 先頭の要素と、残りを作る方法（Lazy）からなる無限のリスト */
    record LazyList<T>(T head, Lazy<LazyList<T>> rest) {
        static <T> LazyList<T> of(T head, Supplier<LazyList<T>> rest) {
            return new LazyList<>(head, new Lazy<>(rest));
        }

        LazyList<T> tail() {
            return rest.get();
        }

        Stream<T> stream() {
            return Stream.iterate(this, LazyList::tail).map(LazyList::head);
        }

        static LazyList<Long> fibonacci(long a, long b) {
            return of(a, () -> fibonacci(b, a + b));
        }
    }

    public static void main(String[] args) {
        IO.println("orElse（引数を先に評価する）:");
        Optional.of("値あり").orElse(expensive("代わりの値A"));
        IO.println("orElseGet（必要になったときだけSupplierを呼ぶ）:");
        Optional.of("値あり").orElseGet(() -> expensive("代わりの値B"));

        IO.println("Lazy:");
        Lazy<String> lazy = new Lazy<>(() -> expensive("重い値"));
        Lazy<Integer> length = lazy.map(String::length);
        IO.println("  Lazyを作った");
        IO.println("  length = " + length.get());
        IO.println("  lazy = " + lazy.get() + "（2回目は計算しない）");

        IO.println("Stream（終端操作がないと何もしない）:");
        Stream<Integer> pipeline = Stream.of(1, 2, 3, 4)
            .peek(x -> IO.println("  peek " + x))
            .filter(x -> x % 2 == 0);
        IO.println("  パイプラインを作った");
        List<Integer> firstEven = pipeline.limit(1).toList();
        IO.println("  結果 " + firstEven);

        IO.println("無限のストリーム:");
        IO.println("  " + Stream.iterate(1, x -> x * 2).limit(10).toList());
        IO.println("  " + LazyList.fibonacci(0, 1).stream().limit(10).toList());
    }
}
