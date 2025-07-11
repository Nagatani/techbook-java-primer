/**
 * リストAB-21
 * AlgebraicDataTypeExampleクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (1032行目)
 */

// sealed classを使った代数的データ型
public sealed interface Expression {
    
    record Constant(double value) implements Expression {}
    record Variable(String name) implements Expression {}
    record Add(Expression left, Expression right) implements Expression {}
    record Multiply(Expression left, Expression right) implements Expression {}
    
    // パターンマッチングによる評価
    default double evaluate(Map<String, Double> environment) {
        return switch (this) {
            case Constant(var value) -> value;
            case Variable(var name) -> environment.getOrDefault(name, 0.0);
            case Add(var left, var right) -> 
                left.evaluate(environment) + right.evaluate(environment);
            case Multiply(var left, var right) -> 
                left.evaluate(environment) * right.evaluate(environment);
        };
    }
    
    // 式の簡約
    default Expression simplify() {
        return switch (this) {
            case Constant c -> c;
            case Variable v -> v;
            case Add(var left, var right) -> {
                Expression l = left.simplify();
                Expression r = right.simplify();
                
                if (l instanceof Constant(var lv) && r instanceof Constant(var rv)) {
                    yield new Constant(lv + rv);
                }
                if (l instanceof Constant(var v) && v == 0) yield r;
                if (r instanceof Constant(var v) && v == 0) yield l;
                yield new Add(l, r);
            }
            case Multiply(var left, var right) -> {
                Expression l = left.simplify();
                Expression r = right.simplify();
                
                if (l instanceof Constant(var lv) && r instanceof Constant(var rv)) {
                    yield new Constant(lv * rv);
                }
                if (l instanceof Constant(var v) && v == 0) yield new Constant(0);
                if (r instanceof Constant(var v) && v == 0) yield new Constant(0);
                if (l instanceof Constant(var v) && v == 1) yield r;
                if (r instanceof Constant(var v) && v == 1) yield l;
                yield new Multiply(l, r);
            }
        };
    }
    
    // 式の表示
    default String show() {
        return switch (this) {
            case Constant(var value) -> String.valueOf(value);
            case Variable(var name) -> name;
            case Add(var left, var right) -> 
                "(" + left.show() + " + " + right.show() + ")";
            case Multiply(var left, var right) -> 
                "(" + left.show() + " * " + right.show() + ")";
        };
    }
}

// 使用例
public class AlgebraicDataTypeExample {
    public static void main(String[] args) {
        // (x + 2) * (y + 3)
        Expression expr = new Expression.Multiply(
            new Expression.Add(
                new Expression.Variable("x"),
                new Expression.Constant(2)
            ),
            new Expression.Add(
                new Expression.Variable("y"),
                new Expression.Constant(3)
            )
        );
        
        System.out.println("Expression: " + expr.show());
        
        Map<String, Double> env = Map.of("x", 5.0, "y", 7.0);
        System.out.println("Evaluation: " + expr.evaluate(env)); // 70.0
        
        // 簡約の例
        Expression toSimplify = new Expression.Add(
            new Expression.Constant(0),
            new Expression.Multiply(
                new Expression.Variable("x"),
                new Expression.Constant(1)
            )
        );
        
        System.out.println("Before: " + toSimplify.show());
        System.out.println("After: " + toSimplify.simplify().show());
    }
}