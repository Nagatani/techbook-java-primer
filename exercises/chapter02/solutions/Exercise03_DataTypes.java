/**
 * 第2章 課題3: データ型の理解 - 解答例
 * 
 * さまざまなデータ型を使った変数宣言と出力を行ってください。
 * 
 * 学習ポイント:
 * - プリミティブ型の種類と特徴
 * - リテラルの正しい記述方法
 * - 各型の値の範囲と用途
 */
public class Exercise03_DataTypes {
    public static void main(String[] args) {
        byte byteValue = 100;
        short shortValue = 30000;
        int intValue = 2000000;
        long longValue = 9000000000L;  // Lサフィックス必須
        float floatValue = 3.14f;      // fサフィックス必須
        double doubleValue = 3.141592653589793;
        char charValue = 'A';
        boolean booleanValue = true;
        String stringValue = "Hello Java!";
        
        IO.println("=== データ型の確認 ===");
        IO.println("byte値: " + byteValue);
        IO.println("short値: " + shortValue);
        IO.println("int値: " + intValue);
        IO.println("long値: " + longValue);
        IO.println("float値: " + floatValue);
        IO.println("double値: " + doubleValue);
        IO.println("char値: " + charValue);
        IO.println("boolean値: " + booleanValue);
        IO.println("String値: " + stringValue);

        // 各型の範囲は、Integer.MIN_VALUE・Integer.MAX_VALUEのような定数で確かめられる
        // IO.println("int範囲: " + Integer.MIN_VALUE + " ～ " + Integer.MAX_VALUE);
        // 整数は2進数（0b101010）・16進数（0x2A）でも書ける。どちらも10進数の42
        // 大きい型から小さい型へはキャストが要る: int convertedInt = (int) 123.456;  // 123
    }
}

/*
 * データ型の詳細解説:
 * 
 * 【整数型】
 * byte  : 8ビット、 -128 ～ 127
 * short : 16ビット、-32,768 ～ 32,767
 * int   : 32ビット、約-21億 ～ 21億
 * long  : 64ビット、約-922京 ～ 922京
 * 
 * 【浮動小数点型】
 * float : 32ビット、単精度（有効桁数約7桁）
 * double: 64ビット、倍精度（有効桁数約15桁）
 * 
 * 【その他】
 * char   : 16ビット、Unicode文字（0 ～ 65,535）
 * boolean: true または false
 * String : 文字列（プリミティブ型ではない）
 * 
 * リテラルのルール:
 * 
 * 1. long型リテラル
 *    - 末尾にLまたはlを付ける
 *    - 大文字Lが推奨（小文字lは1と見間違いやすい）
 * 
 * 2. float型リテラル
 *    - 末尾にFまたはfを付ける
 *    - 付けないとdouble型として扱われる
 * 
 * 3. char型リテラル
 *    - 単一引用符で囲む
 *    - エスケープ文字: \n, \t, \\, \", \'
 * 
 * よくある間違い:
 * 
 * 1. リテラルサフィックスの忘れ
 *    × long big = 9000000000;   // intの範囲を超える
 *    ○ long big = 9000000000L;  // Lサフィックス必要
 * 
 * 2. 文字と文字列の混同
 *    × char ch = "A";           // 文字列を文字型に代入
 *    ○ char ch = 'A';           // 文字は単一引用符
 * 
 * 3. 精度の問題
 *    × float f = 0.1 + 0.2;     // 浮動小数点の精度問題
 *    ○ 金融計算にはBigDecimalを使用
 * 
 * 4. オーバーフロー
 *    × byte b = 200;            // byteの範囲（-128～127）を超える
 *    ○ byte b = (byte)200;      // 明示的キャストが必要
 * 
 * 使い分けの指針:
 * - 整数: 基本的にはint、大きな値はlong
 * - 小数: 基本的にはdouble、メモリ節約時はfloat
 * - 文字: char（1文字）、String（文字列）
 * - 真偽値: boolean
 */