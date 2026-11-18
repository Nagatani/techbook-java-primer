import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/** 小さなパーサーを関数として表し、組み合わせて大きなパーサーを作る */
public class ParserCombinator {

    /** 解析した値と、残りの文字列の組 */
    record Pair<A, B>(A first, B second) {
        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }

    /** パーサー：文字列を受け取り、成功なら（値, 残り）、失敗なら空を返す関数 */
    @FunctionalInterface
    interface Parser<T> {
        Optional<Pair<T, String>> parse(String input);

        /** 解析した値に関数を当てはめる（ファンクターのmap） */
        default <R> Parser<R> map(Function<T, R> f) {
            return input -> parse(input).map(r -> new Pair<>(f.apply(r.first()), r.second()));
        }

        /** 解析した値に応じて次のパーサーを選び、残りの文字列に続けて当てはめる（bind） */
        default <R> Parser<R> flatMap(Function<T, Parser<R>> f) {
            return input -> parse(input).flatMap(r -> f.apply(r.first()).parse(r.second()));
        }
    }

    static Parser<Character> charParser(char c) {
        return input -> input.isEmpty() || input.charAt(0) != c
            ? Optional.empty()
            : Optional.of(new Pair<>(c, input.substring(1)));
    }

    static Parser<String> stringParser(String s) {
        return input -> input.startsWith(s)
            ? Optional.of(new Pair<>(s, input.substring(s.length())))
            : Optional.empty();
    }

    static <T> Parser<T> or(Parser<T> p1, Parser<T> p2) {
        return input -> p1.parse(input).or(() -> p2.parse(input));
    }

    static <A, B> Parser<Pair<A, B>> and(Parser<A> p1, Parser<B> p2) {
        return p1.flatMap(a -> p2.map(b -> new Pair<>(a, b)));
    }

    static <T> Parser<List<T>> many(Parser<T> parser) {
        return input -> {
            List<T> results = new ArrayList<>();
            String remaining = input;
            while (true) {
                Optional<Pair<T, String>> result = parser.parse(remaining);
                if (result.isEmpty()) {
                    return Optional.of(new Pair<>(results, remaining));
                }
                results.add(result.get().first());
                remaining = result.get().second();
            }
        };
    }

    static Parser<Integer> digit() {
        return input -> input.isEmpty() || !Character.isDigit(input.charAt(0))
            ? Optional.empty()
            : Optional.of(new Pair<>(Character.getNumericValue(input.charAt(0)),
                input.substring(1)));
    }

    static Parser<Integer> number() {
        return input -> many(digit()).parse(input)
            .filter(r -> !r.first().isEmpty())
            .map(r -> new Pair<>(r.first().stream().reduce(0, (acc, d) -> acc * 10 + d),
                r.second()));
    }

    public static void main(String[] args) {
        Parser<Character> aOrB = or(charParser('a'), charParser('b'));
        IO.println(aOrB.parse("abc"));
        IO.println(aOrB.parse("bcd"));
        IO.println(aOrB.parse("xyz"));
        IO.println(number().parse("123abc"));
        IO.println(and(stringParser("x="), number()).parse("x=42"));
        // 「数 + 数」を解析して足し算する
        Parser<Integer> sum = number().flatMap(a -> charParser('+').flatMap(_ -> number()
            .map(b -> a + b)));
        IO.println(sum.parse("12+30 rest"));
        IO.println(sum.parse("12-30"));
    }
}
