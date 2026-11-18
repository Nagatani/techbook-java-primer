/** 型パラメーターFに、さらに型引数を付けることはできない（高カインド型がない） */
public class HigherKinded {
    interface Functor<F> {
        <A, B> F<B> map(F<A> fa, java.util.function.Function<A, B> f);
    }
}
