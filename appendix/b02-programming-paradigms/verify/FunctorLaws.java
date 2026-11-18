import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/** Optional・Stream・Listのmapが、ファンクターの法則を満たすかを値ごとに確かめる */
public class FunctorLaws {

    /** 恒等則：m.map(id) と m が等しいか */
    static <T> boolean identityLaw(Optional<T> m) {
        return m.map(Function.identity()).equals(m);
    }

    /** 合成則：m.map(g).map(f) と m.map(g.andThen(f)) が等しいか */
    static <A, B, C> boolean compositionLaw(Optional<A> m, Function<A, B> g, Function<B, C> f) {
        return m.map(g).map(f).equals(m.map(g.andThen(f)));
    }

    public static void main(String[] args) {
        Function<Integer, Integer> addOne = x -> x + 1;
        Function<Integer, Integer> multiplyTwo = x -> x * 2;

        // nullを返さない関数なら、法則は成り立つ
        for (Optional<Integer> m : List.of(Optional.of(3), Optional.<Integer>empty())) {
            IO.println(m + ": 恒等則 " + identityLaw(m)
                + "、合成則 " + compositionLaw(m, addOne, multiplyTwo));
        }

        // nullを返す関数gと、nullを受け取れる関数fを合成する
        Function<Integer, String> g = x -> null;
        Function<String, String> f = s -> s == null ? "nullでした" : s;
        Optional<Integer> three = Optional.of(3);
        IO.println("Optional.map: m.map(g).map(f) = " + three.map(g).map(f));
        IO.println("Optional.map: m.map(g.andThen(f)) = " + three.map(g.andThen(f)));
        IO.println("Optional.map: 合成則 " + compositionLaw(three, g, f));

        // Streamは要素にnullを持てるので、同じ関数でも合成則が成り立つ
        List<String> twoSteps = Stream.of(3).map(g).map(f).toList();
        List<String> oneStep = Stream.of(3).map(g.andThen(f)).toList();
        IO.println("Stream.map: " + twoSteps + " / " + oneStep
            + " 合成則 " + twoSteps.equals(oneStep));

        // Listを、Streamを経由してmapする
        List<Integer> numbers = List.of(1, 2, 3);
        IO.println("List: 恒等則 " + numbers.stream().map(Function.<Integer>identity()).toList()
            .equals(numbers)
            + "、合成則 " + numbers.stream().map(addOne).map(multiplyTwo).toList()
                .equals(numbers.stream().map(addOne.andThen(multiplyTwo)).toList()));

        // Streamそのものは比べられず、一度しか使えない
        Stream<Integer> s = Stream.of(1, 2, 3);
        IO.println("Stream.equals: " + s.equals(s.map(Function.identity())));
        try {
            s.map(addOne).toList();
        } catch (IllegalStateException e) {
            IO.println("2回目の操作: " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
}
