package chapter09.basic;

/**
 * 数値型に制限されたジェネリックなBoxクラス
 * 
 * Number型とそのサブクラスのみを格納できるBoxです。
 * 境界付き型パラメータの使用例です。
 * 
 * @param <T> Number型またはそのサブクラス
 */
public class NumberBox<T extends Number> {
    // 格納する数値
    private T number;
    
    /**
     * コンストラクタ
     * @param number 格納する数値
     */
    public NumberBox(T number) {
        // TODO: フィールドを初期化
        
    }
    
    /**
     * 数値を取得する
     * TODO: numberフィールドの値を返す
     * @return 格納されている数値
     */
    public T getNumber() {
        // TODO: 実装してください
        return null;
    }
    
    /**
     * 数値を設定する
     * TODO: numberフィールドに値を設定
     * @param number 新しい数値
     */
    public void setNumber(T number) {
        // TODO: 実装してください
        
    }
    
    /**
     * double値として取得する
     * TODO: Number型のdoubleValue()メソッドを使用
     * @return double値
     */
    public double getAsDouble() {
        // TODO: 実装してください
        return 0.0;
    }
    
    /**
     * int値として取得する
     * TODO: Number型のintValue()メソッドを使用
     * @return int値
     */
    public int getAsInt() {
        // TODO: 実装してください
        return 0;
    }
    
    /**
     * 値が正の数かどうかを判定する
     * TODO: doubleValue()を使って0より大きいかチェック
     * @return 正の数の場合true
     */
    public boolean isPositive() {
        // TODO: 実装してください
        return false;
    }
    
    /**
     * 別のNumberBoxの値と比較する
     * TODO: 両方の値をdoubleに変換して比較
     * @param other 比較対象のNumberBox
     * @return このBoxの値が大きい場合1、等しい場合0、小さい場合-1
     */
    public int compareTo(NumberBox<?> other) {
        // TODO: 実装してください
        // ヒント: Double.compare()を使用
        return 0;
    }
    
    /**
     * 数値の情報を表示する
     * TODO: 数値の型、値、およびdouble/int表現を表示
     */
    public void displayInfo() {
        // TODO: 実装してください
        System.out.println("NumberBox情報:");
        // 例: "型: Integer"
        //     "値: 42"
        //     "double値: 42.0"
        //     "int値: 42"
    }
    
    @Override
    public String toString() {
        return "NumberBox{" + number + "}";
    }
}