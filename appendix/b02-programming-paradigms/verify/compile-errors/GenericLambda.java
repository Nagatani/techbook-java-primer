import java.util.function.Function;

/** 型パラメーターを持つ抽象メソッドは、ラムダ式で実装できない */
public class GenericLambda {
    interface ChurchNumeral {
        <T> Function<T, T> apply(Function<T, T> f);
    }

    static final ChurchNumeral ZERO = f -> x -> x;
}
