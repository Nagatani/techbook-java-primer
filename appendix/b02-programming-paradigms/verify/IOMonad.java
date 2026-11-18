import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 副作用を「実行する手順の値」として組み立てる、IOモナドの簡易版。
 * 入れ子のクラスIOは、このクラスの中ではjava.lang.IOより優先されるので、
 * 標準出力にはjava.lang.IOと完全修飾名で書く
 */
public class IOMonad {

    abstract static class IO<T> {
        /** 手順を実行する */
        abstract T unsafeRun();

        static <T> IO<T> pure(T value) {
            return new IO<>() {
                @Override
                T unsafeRun() {
                    return value;
                }
            };
        }

        <U> IO<U> flatMap(Function<T, IO<U>> f) {
            return new IO<>() {
                @Override
                U unsafeRun() {
                    return f.apply(IO.this.unsafeRun()).unsafeRun();
                }
            };
        }

        <U> IO<U> map(Function<T, U> f) {
            return flatMap(t -> pure(f.apply(t)));
        }

        static IO<Void> println(String s) {
            return new IO<>() {
                @Override
                Void unsafeRun() {
                    java.lang.IO.println(s);
                    return null;
                }
            };
        }
    }

    /** 入力の代わりに、決まった行を順に返す（観察の結果を毎回同じにするため） */
    static IO<String> readLine(List<String> input) {
        return new IO<>() {
            @Override
            String unsafeRun() {
                return input.removeFirst();
            }
        };
    }

    public static void main(String[] args) {
        List<String> input = new ArrayList<>(List.of("3", "4"));
        IO<Integer> getNumber = IO.println("Enter a number:")
            .flatMap(_ -> readLine(input))
            .map(Integer::parseInt);
        IO<Void> program = IO.println("Welcome!")
            .flatMap(_ -> getNumber)
            .flatMap(a -> getNumber.flatMap(b -> IO.println("Sum: " + (a + b))));

        java.lang.IO.println("--- 手順を組み立てた（まだ何も表示していない）");
        program.unsafeRun();
        java.lang.IO.println("--- getNumberは2回実行された（残りの入力 " + input + "）");
        java.lang.IO.println("IOが指すクラス: " + IO.class.getName());
    }
}
