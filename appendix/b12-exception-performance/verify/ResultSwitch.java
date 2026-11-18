/**
 * 例外の代わりに、成功と失敗を値で返す（sealedインターフェイスとレコード）。
 *
 *   javac ResultSwitch.java && java ResultSwitch
 */
public class ResultSwitch {

    sealed interface Result<T> permits Ok, Err {
    }

    record Ok<T>(T value) implements Result<T> {
    }

    record Err<T>(String message) implements Result<T> {
    }

    static Result<Integer> parse(String text) {
        if (text == null || !text.matches("-?\\d{1,9}")) {
            return new Err<>("数ではありません: " + text);
        }
        return new Ok<>(Integer.parseInt(text));
    }

    static String describe(Result<Integer> r) {
        // sealedなので、2つの場合を書けばdefaultは要らない
        return switch (r) {
            case Ok<Integer>(Integer v) -> "成功 " + v;
            case Err<Integer>(String m) -> "失敗 " + m;
        };
    }

    public static void main(String[] args) {
        IO.println(describe(parse("42")));
        IO.println(describe(parse("abc")));
    }
}
