import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/** OptionalとStreamのflatMapが、モナドの法則を満たすかを値ごとに確かめる */
public class MonadLaws {
    public static void main(String[] args) {
        Function<Integer, Optional<String>> f = x -> Optional.of("Value: " + x);
        Function<Integer, Optional<Integer>> addOne = x -> Optional.of(x + 1);
        Function<Integer, Optional<Integer>> half =
            x -> x % 2 == 0 ? Optional.of(x / 2) : Optional.empty();

        // 1. 左単位元則：of(a).flatMap(f) = f(a)
        IO.println("左単位元則: " + Optional.of(10).flatMap(f).equals(f.apply(10)));
        // 2. 右単位元則：m.flatMap(Optional::of) = m
        for (Optional<Integer> m : List.of(Optional.of(20), Optional.<Integer>empty())) {
            IO.println("右単位元則 " + m + ": " + m.flatMap(Optional::of).equals(m));
        }
        // 3. 結合則：m.flatMap(f).flatMap(g) = m.flatMap(x -> f(x).flatMap(g))
        for (int a : new int[] {5, 6}) {
            Optional<Integer> left = Optional.of(a).flatMap(addOne).flatMap(half);
            Optional<Integer> right = Optional.of(a).flatMap(x -> addOne.apply(x).flatMap(half));
            IO.println("結合則 a=" + a + ": " + left + " / " + right + " " + left.equals(right));
        }

        // unitをofNullableにすると、a = null で左単位元則が崩れる
        Function<Integer, Optional<String>> describe =
            x -> Optional.of(x == null ? "nullを受け取った" : "値 " + x);
        IO.println("ofNullable(null).flatMap(describe) = "
            + Optional.<Integer>ofNullable(null).flatMap(describe));
        IO.println("describe.apply(null) = " + describe.apply(null));
        // unitをofにすると、nullはそもそも包めない
        try {
            Optional.of(null);
        } catch (NullPointerException e) {
            IO.println("Optional.of(null): NullPointerException");
        }
        // flatMapに渡した関数がnullを返すと、例外になる
        try {
            Optional.of(1).flatMap(x -> null);
        } catch (NullPointerException e) {
            IO.println("Optional.flatMap(x -> null): NullPointerException");
        }

        // Streamでも同じ3つの法則を、toList()の結果で比べる
        Function<Integer, Stream<Integer>> dup = x -> Stream.of(x, x * x);
        Function<Integer, Stream<Integer>> evenOnly = x -> x % 2 == 0 ? Stream.of(x) : Stream.empty();
        IO.println("Stream 左単位元則: "
            + Stream.of(3).flatMap(dup).toList().equals(dup.apply(3).toList()));
        IO.println("Stream 右単位元則: "
            + Stream.of(1, 2, 3).flatMap(Stream::of).toList().equals(List.of(1, 2, 3)));
        List<Integer> l = Stream.of(1, 2, 3).flatMap(dup).flatMap(evenOnly).toList();
        List<Integer> r = Stream.of(1, 2, 3).flatMap(x -> dup.apply(x).flatMap(evenOnly)).toList();
        IO.println("Stream 結合則: " + l + " / " + r + " " + l.equals(r));
        // Stream.flatMapは、関数がnullを返すと空のストリームとして扱う
        IO.println("Stream.flatMap(x -> null): "
            + Stream.of(1, 2).flatMap(x -> (Stream<Integer>) null).toList());

        // flatMap・filter・mapの連鎖
        List<Integer> result = Stream.of(1, 2, 3, 4, 5)
            .flatMap(x -> Stream.of(x, x * x))
            .filter(x -> x % 2 == 0)
            .map(x -> x * 10)
            .toList();
        IO.println("連鎖: " + result);
    }
}
