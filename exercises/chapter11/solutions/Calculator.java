package chapter11.solutions;

/**
 * 2つの数を計算する関数型インターフェイス（課題2の解答例）
 */
@FunctionalInterface
public interface Calculator {
    double calculate(double a, double b);

    static Calculator add() {
        return (a, b) -> a + b;
        // メソッド参照でも書ける: return Double::sum;
    }

    static Calculator subtract() {
        return (a, b) -> a - b;
    }

    static Calculator multiply() {
        return (a, b) -> a * b;
    }

    /**
     * 0で割ろうとしたら「0では割れません」と表示し、Double.NaNを返す。
     * doubleを0で割っても例外は起きない（InfinityやNaNになる）ので、割る前に確かめる
     */
    static Calculator divide() {
        return (a, b) -> {
            if (b == 0) {
                IO.println("0では割れません");
                return Double.NaN;
            }
            return a / b;
        };
    }
}
