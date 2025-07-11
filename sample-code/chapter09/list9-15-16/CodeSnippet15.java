/**
 * リスト9-15
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (528行目)
 */

// 数式を表現する代数的データ型
public sealed interface Expr permits Const, Add, Mul, Var {}

public record Const(double value) implements Expr {}
public record Add(Expr left, Expr right) implements Expr {}
public record Mul(Expr left, Expr right) implements Expr {}
public record Var(String name) implements Expr {}

// 式の評価
public static double eval(Expr expr, Map<String, Double> env) {
    return switch (expr) {
        case Const(var value) -> value;
        case Add(var left, var right) -> eval(left, env) + eval(right, env);
        case Mul(var left, var right) -> eval(left, env) * eval(right, env);
        case Var(var name) -> env.getOrDefault(name, 0.0);
    };
}

// 式の簡約（最適化）
public static Expr simplify(Expr expr) {
    return switch (expr) {
        // 定数の計算
        case Add(Const(var a), Const(var b)) -> new Const(a + b);
        case Mul(Const(var a), Const(var b)) -> new Const(a * b);
        
        // 恒等元による簡約
        case Add(Const(0), var e) -> simplify(e);
        case Add(var e, Const(0)) -> simplify(e);
        case Mul(Const(1), var e) -> simplify(e);
        case Mul(var e, Const(1)) -> simplify(e);
        
        // 零元による簡約
        case Mul(Const(0), _) -> new Const(0);
        case Mul(_, Const(0)) -> new Const(0);
        
        // 再帰的簡約
        case Add(var left, var right) -> new Add(simplify(left), simplify(right));
        case Mul(var left, var right) -> new Mul(simplify(left), simplify(right));
        
        default -> expr;
    };
}

// 式の文字列表現
public static String toString(Expr expr) {
    return switch (expr) {
        case Const(var value) -> String.valueOf(value);
        case Add(var left, var right) -> 
            "(" + toString(left) + " + " + toString(right) + ")";
        case Mul(var left, var right) -> 
            "(" + toString(left) + " * " + toString(right) + ")";
        case Var(var name) -> name;
    };
}