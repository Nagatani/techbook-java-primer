package chapter09.basic;

/**
 * 2つの値を保持するジェネリックなPairクラス
 * 
 * 異なる型の2つの値をペアとして扱うことができます。
 * 
 * @param <F> 最初の値の型
 * @param <S> 2番目の値の型
 */
public class Pair<F, S> {
    // 最初の値
    private F first;
    // 2番目の値
    private S second;
    
    /**
     * コンストラクタ
     * @param first 最初の値
     * @param second 2番目の値
     */
    public Pair(F first, S second) {
        // TODO: フィールドを初期化
        
    }
    
    /**
     * 最初の値を取得する
     * TODO: firstフィールドの値を返す
     * @return 最初の値
     */
    public F getFirst() {
        // TODO: 実装してください
        return null;
    }
    
    /**
     * 2番目の値を取得する
     * TODO: secondフィールドの値を返す
     * @return 2番目の値
     */
    public S getSecond() {
        // TODO: 実装してください
        return null;
    }
    
    /**
     * 最初の値を設定する
     * TODO: firstフィールドに値を設定
     * @param first 新しい最初の値
     */
    public void setFirst(F first) {
        // TODO: 実装してください
        
    }
    
    /**
     * 2番目の値を設定する
     * TODO: secondフィールドに値を設定
     * @param second 新しい2番目の値
     */
    public void setSecond(S second) {
        // TODO: 実装してください
        
    }
    
    /**
     * ペアの値を入れ替える
     * TODO: firstとsecondの値を入れ替えた新しいPairを返す
     * @return 値が入れ替わった新しいPair
     */
    public Pair<S, F> swap() {
        // TODO: 実装してください
        // 注意: 型パラメータの順序も入れ替わります
        return null;
    }
    
    /**
     * ペアの情報を表示する
     * TODO: 両方の値とその型を表示
     */
    public void displayInfo() {
        // TODO: 実装してください
        System.out.println("Pair情報:");
        // 例: "First (String): Hello"
        //     "Second (Integer): 123"
    }
    
    /**
     * 文字列表現を返す
     */
    @Override
    public String toString() {
        return "Pair{first=" + first + ", second=" + second + "}";
    }
    
    /**
     * 等価性を判定する
     * TODO: 両方の値が等しい場合にtrueを返す
     */
    @Override
    public boolean equals(Object obj) {
        // TODO: 実装してください
        // ヒント:
        // 1. 同じオブジェクトか確認
        // 2. nullチェックと型チェック
        // 3. firstとsecondの両方を比較
        return false;
    }
    
    /**
     * ハッシュコードを返す
     * TODO: firstとsecondのハッシュコードを組み合わせる
     */
    @Override
    public int hashCode() {
        // TODO: 実装してください
        // ヒント: Objects.hash()を使用できます
        return 0;
    }
}