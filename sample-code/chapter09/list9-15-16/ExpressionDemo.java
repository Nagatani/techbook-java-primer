/**
 * リスト9-16
 * ExpressionDemoクラス
 * 
 * 元ファイル: chapter09-records.md (588行目)
 */

public class ExpressionDemo {
    public static void main(String[] args) {
        // 式の構築: (x + 1) * (y + 0)
        Expr expr = new Mul(
            new Add(new Var("x"), new Const(1)),
            new Add(new Var("y"), new Const(0))
        );
        
        System.out.println("元の式: " + toString(expr));
        // 出力: ((x + 1.0) * (y + 0.0))
        
        // 式の簡約
        Expr simplified = simplify(expr);
        System.out.println("簡約後: " + toString(simplified));
        // 出力: ((x + 1.0) * y)
        
        // 変数代入による評価
        Map<String, Double> env = Map.of("x", 3.0, "y", 4.0);
        double result = eval(simplified, env);
        System.out.println("x=3, y=4の時の値: " + result);
        // 出力: x=3, y=4の時の値: 16.0
    }
}