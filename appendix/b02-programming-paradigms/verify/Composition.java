import java.util.List;
import java.util.function.Function;

/** 関数の合成（andThenとcompose）と、圏の法則を値ごとに確かめる */
public class Composition {
    public static void main(String[] args) {
        Function<Integer, Integer> addOne = x -> x + 1;
        Function<Integer, Integer> multiplyByTwo = x -> x * 2;

        // andThen: 自分を先に、引数を後に適用する
        IO.println("addOne.andThen(multiplyByTwo).apply(3) = "
            + addOne.andThen(multiplyByTwo).apply(3));
        // compose: 引数を先に、自分を後に適用する（数学の∘と同じ順）
        IO.println("addOne.compose(multiplyByTwo).apply(3) = "
            + addOne.compose(multiplyByTwo).apply(3));

        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 2;
        Function<Integer, Integer> h = x -> x * x;
        Function<Integer, Integer> id = Function.identity();

        // 法則は「すべての入力で同じ値を返す」ことなので、いくつかの入力で比べる
        List<Integer> inputs = List.of(-3, 0, 3, 100);
        boolean assoc = true;
        boolean unit = true;
        for (int x : inputs) {
            assoc &= f.andThen(g).andThen(h).apply(x)
                .equals(f.andThen(g.andThen(h)).apply(x));
            unit &= f.andThen(id).apply(x).equals(f.apply(x))
                && id.andThen(f).apply(x).equals(f.apply(x));
        }
        IO.println("結合法則（入力 " + inputs + "）: " + assoc);
        IO.println("単位法則（入力 " + inputs + "）: " + unit);
        IO.println("((3 + 1) * 2)^2 = " + f.andThen(g).andThen(h).apply(3));

        // Functionのequalsは「同じオブジェクトか」しか比べない
        IO.println("f.equals(f.andThen(id)) = " + f.equals(f.andThen(id)));
        IO.println("f.equals(f) = " + f.equals(f));
    }
}
