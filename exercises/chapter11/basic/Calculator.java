/**
 * 第11章 ラムダ式と関数型インターフェイス - 基本演習
 * 計算用カスタム関数型インターフェイス
 */
@FunctionalInterface
public interface Calculator {
    /**
     * 2つの数値に対して計算を実行
     * @param a 最初の数値
     * @param b 2番目の数値
     * @return 計算結果
     */
    double calculate(double a, double b);
    
    /**
     * スタティックメソッド: 加算計算器を返す
     */
    static Calculator add() {
        // TODO: ラムダ式を使用して加算を実装
        return null;
    }
    
    /**
     * スタティックメソッド: 減算計算器を返す
     */
    static Calculator subtract() {
        // TODO: ラムダ式を使用して減算を実装
        return null;
    }
    
    /**
     * スタティックメソッド: 乗算計算器を返す
     */
    static Calculator multiply() {
        // TODO: ラムダ式を使用して乗算を実装
        return null;
    }
    
    /**
     * スタティックメソッド: 除算計算器を返す
     */
    static Calculator divide() {
        // TODO: ラムダ式を使用して除算を実装
        // 注意: ゼロ除算のチェックを含む
        return null;
    }
}