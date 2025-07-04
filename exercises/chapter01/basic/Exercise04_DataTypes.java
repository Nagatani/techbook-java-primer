/**
 * 第1章 基本課題4: データ型の理解
 * 
 * 課題: さまざまなデータ型を使った変数宣言と出力を行ってください。
 * 
 * 要求仕様:
 * - byte, short, int, long型の数値
 * - float, double型の小数
 * - char型の文字
 * - boolean型の真偽値
 * - String型の文字列
 * 
 * 実行例:
 * === データ型の確認 ===
 * byte値: 100
 * short値: 30000
 * int値: 2000000
 * long値: 9000000000
 * float値: 3.14f
 * double値: 3.141592653589793
 * char値: A
 * boolean値: true
 * String値: Hello Java!
 * 
 * 評価ポイント:
 * - 各データ型を正しく使用
 * - 適切な値の範囲を理解
 * - リテラルの記述方法を理解
 */
public class Exercise04_DataTypes {
    public static void main(String[] args) {
        // TODO: 各データ型の変数を宣言し、適切な値を代入してください
        
        // 整数型
        byte byteValue = 0;     // -128 〜 127
        short shortValue = 0;   // -32,768 〜 32,767
        int intValue = 0;       // 約-21億 〜 約21億
        long longValue = 0L;    // さらに大きな整数（Lを付ける）
        
        // 浮動小数点型
        float floatValue = 0.0f;  // 単精度（fを付ける）
        double doubleValue = 0.0; // 倍精度
        
        // 文字型・論理型・文字列型
        char charValue = 'A';           // 単一文字（'で囲む）
        boolean booleanValue = true;    // true または false
        String stringValue = "Hello";   // 文字列（"で囲む）
        
        // TODO: 上記の変数を使って以下の形式で出力してください
        System.out.println("=== データ型の確認 ===");
        // System.out.println("byte値: " + byteValue);
        // ... 他の変数も同様に出力
        
    }
}