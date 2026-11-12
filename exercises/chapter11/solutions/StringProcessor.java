package chapter11.solutions;

/**
 * 文字列を加工する関数型インターフェイス（課題2の解答例）
 *
 * 抽象メソッドは1つだけで、デフォルトメソッドで処理を組み合わせます。
 */
@FunctionalInterface
public interface StringProcessor {
    String process(String input);

    /**
     * 前後の空白を除いてから、自分のprocessを呼ぶ処理を返す
     */
    default StringProcessor withTrim() {
        // このprocessは、withTrimを呼んだインスタンスのprocess
        return s -> process(s.trim());
    }

    /**
     * 自分の処理の結果をafterに渡す処理を返す
     */
    default StringProcessor andThen(StringProcessor after) {
        return s -> after.process(process(s));
    }
}
