/**
 * 第11章 ラムダ式と関数型インターフェイス - 基本演習
 * カスタム関数型インターフェイス
 */
@FunctionalInterface
public interface StringProcessor {
    /**
     * 文字列を処理する抽象メソッド
     * @param input 入力文字列
     * @return 処理後の文字列
     */
    String process(String input);
    
    /**
     * デフォルトメソッド: 前処理としてトリムを実行
     */
    default StringProcessor withTrim() {
        // TODO: ラムダ式を使用して実装
        // ヒント: まず入力をtrim()してからprocess()を実行
        return null;
    }
    
    /**
     * デフォルトメソッド: 複数の処理を連結
     */
    default StringProcessor andThen(StringProcessor after) {
        // TODO: ラムダ式を使用して実装
        // ヒント: この処理を実行した後、afterを実行
        return null;
    }
}