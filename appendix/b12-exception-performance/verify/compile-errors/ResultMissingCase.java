/**
 * sealedインターフェイスのswitchで、場合を書き忘れるとコンパイルエラーになる。
 */
public class ResultMissingCase {

    sealed interface Result<T> permits Ok, Err {
    }

    record Ok<T>(T value) implements Result<T> {
    }

    record Err<T>(String message) implements Result<T> {
    }

    static String describe(Result<Integer> r) {
        return switch (r) {
            case Ok<Integer>(Integer v) -> "成功 " + v;
        };
    }
}
