import java.util.Map;

/** sealedインターフェイスとレコードで式の木（代数的データ型）を作り、パターンマッチングで処理する */
public class ExpressionAdt {

    sealed interface Expr permits Constant, Variable, Add, Multiply {}
    record Constant(double value) implements Expr {}
    record Variable(String name) implements Expr {}
    record Add(Expr left, Expr right) implements Expr {}
    record Multiply(Expr left, Expr right) implements Expr {}

    static double evaluate(Expr e, Map<String, Double> env) {
        return switch (e) {
            case Constant(var value) -> value;
            case Variable(var name) -> env.getOrDefault(name, 0.0);
            case Add(var left, var right) -> evaluate(left, env) + evaluate(right, env);
            case Multiply(var left, var right) -> evaluate(left, env) * evaluate(right, env);
        };
    }

    static Expr simplify(Expr e) {
        return switch (e) {
            case Constant c -> c;
            case Variable v -> v;
            case Add(var left, var right) -> {
                Expr l = simplify(left);
                Expr r = simplify(right);
                if (l instanceof Constant(var lv) && r instanceof Constant(var rv)) {
                    yield new Constant(lv + rv);
                }
                if (l instanceof Constant(var v) && v == 0) {
                    yield r;
                }
                if (r instanceof Constant(var v) && v == 0) {
                    yield l;
                }
                yield new Add(l, r);
            }
            case Multiply(var left, var right) -> {
                Expr l = simplify(left);
                Expr r = simplify(right);
                if (l instanceof Constant(var lv) && r instanceof Constant(var rv)) {
                    yield new Constant(lv * rv);
                }
                if (l instanceof Constant(var v) && v == 1) {
                    yield r;
                }
                if (r instanceof Constant(var v) && v == 1) {
                    yield l;
                }
                yield new Multiply(l, r);
            }
        };
    }

    static String show(Expr e) {
        return switch (e) {
            case Constant(var value) -> String.valueOf(value);
            case Variable(var name) -> name;
            case Add(var left, var right) -> "(" + show(left) + " + " + show(right) + ")";
            case Multiply(var left, var right) -> "(" + show(left) + " * " + show(right) + ")";
        };
    }

    public static void main(String[] args) {
        // (x + 2) * (y + 3)
        Expr expr = new Multiply(
            new Add(new Variable("x"), new Constant(2)),
            new Add(new Variable("y"), new Constant(3)));
        IO.println("Expression: " + show(expr));
        IO.println("Evaluation: " + evaluate(expr, Map.of("x", 5.0, "y", 7.0)));

        Expr toSimplify = new Add(new Constant(0), new Multiply(new Variable("x"), new Constant(1)));
        IO.println("Before: " + show(toSimplify));
        IO.println("After: " + show(simplify(toSimplify)));
        // レコードのequalsは成分を比べるので、木の形と値が同じなら等しい
        IO.println("equals: " + simplify(toSimplify).equals(new Variable("x")));
        // doubleでは「0を掛けると0」とは限らない（無限大×0はNaN）
        Expr timesZero = new Multiply(new Variable("x"), new Constant(0));
        IO.println("x * 0.0 (x = Infinity): "
            + evaluate(timesZero, Map.of("x", Double.POSITIVE_INFINITY)));
    }
}
