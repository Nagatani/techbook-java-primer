import java.util.function.Function;

/** 成功か失敗かを表すResult型を、sealedインターフェイスとレコードで作る */
public class ResultMonad {

    sealed interface Result<T> permits Success, Failure {
        static <T> Result<T> of(T value) {
            return new Success<>(value);
        }

        static <T> Result<T> error(String message) {
            return new Failure<>(message);
        }

        /** bind。関数が投げた実行時例外も失敗に変える */
        default <U> Result<U> flatMap(Function<T, Result<U>> f) {
            return switch (this) {
                case Success<T>(var value) -> {
                    try {
                        yield f.apply(value);
                    } catch (RuntimeException e) {
                        yield new Failure<>(e.getClass().getSimpleName());
                    }
                }
                case Failure<T>(var message) -> new Failure<>(message);
            };
        }

        default <U> Result<U> map(Function<T, U> f) {
            return flatMap(value -> of(f.apply(value)));
        }
    }

    record Success<T>(T value) implements Result<T> {}

    record Failure<T>(String message) implements Result<T> {}

    static Result<Integer> divide(int a, int b) {
        if (b == 0) {
            return Result.error("Division by zero");
        }
        return Result.of(a / b);
    }

    static Result<Double> sqrt(int x) {
        if (x < 0) {
            return Result.error("Cannot take square root of negative number");
        }
        return Result.of(Math.sqrt(x));
    }

    static String show(Result<?> r) {
        return switch (r) {
            case Success<?>(var value) -> "Success: " + value;
            case Failure<?>(var message) -> "Failure: " + message;
        };
    }

    public static void main(String[] args) {
        IO.println("20 / 4 → sqrt → *2: "
            + show(divide(20, 4).flatMap(ResultMonad::sqrt).map(x -> x * 2)));
        IO.println("20 / 0 → sqrt → *2: "
            + show(divide(20, 0).flatMap(ResultMonad::sqrt).map(x -> x * 2)));
        IO.println("-20 / 4 → sqrt → *2: "
            + show(divide(-20, 4).flatMap(ResultMonad::sqrt).map(x -> x * 2)));

        // 左単位元則 of(a).flatMap(f) = f(a) は、fが例外を投げると崩れる
        Function<String, Result<Integer>> parse = s -> Result.of(Integer.parseInt(s));
        IO.println("of(\"12\").flatMap(parse) = " + show(Result.of("12").flatMap(parse)));
        IO.println("parse.apply(\"12\") = " + show(parse.apply("12")));
        IO.println("of(\"x\").flatMap(parse) = " + show(Result.of("x").flatMap(parse)));
        try {
            parse.apply("x");
        } catch (NumberFormatException e) {
            IO.println("parse.apply(\"x\") は例外を投げる: " + e.getClass().getSimpleName());
        }
    }
}
