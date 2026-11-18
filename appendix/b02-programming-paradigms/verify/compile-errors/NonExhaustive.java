/** sealedインターフェイスの実装を1つ書き忘れたswitch式 */
public class NonExhaustive {
    sealed interface Expr permits Constant, Add, Multiply {}
    record Constant(double value) implements Expr {}
    record Add(Expr left, Expr right) implements Expr {}
    record Multiply(Expr left, Expr right) implements Expr {}

    static double eval(Expr e) {
        return switch (e) {
            case Constant(var value) -> value;
            case Add(var left, var right) -> eval(left) + eval(right);
        };
    }
}
