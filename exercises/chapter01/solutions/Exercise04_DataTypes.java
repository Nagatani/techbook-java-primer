/**
 * 第1章 課題4: データ型の理解 - 解答例
 * 
 * さまざまなデータ型を使った変数宣言と出力を行ってください。
 * 
 * 学習ポイント:
 * - プリミティブ型の種類と特徴
 * - リテラルの正しい記述方法
 * - 各型の値の範囲と用途
 */
public class Exercise04_DataTypes {
    public static void main(String[] args) {
        // 解答例 1: 基本的なデータ型の実装
        byte byteValue = 100;
        short shortValue = 30000;
        int intValue = 2000000;
        long longValue = 9000000000L;  // Lサフィックス必須
        float floatValue = 3.14f;      // fサフィックス必須
        double doubleValue = 3.141592653589793;
        char charValue = 'A';
        boolean booleanValue = true;
        String stringValue = "Hello Java!";
        
        System.out.println("=== データ型の確認 ===");
        System.out.println("byte値: " + byteValue);
        System.out.println("short値: " + shortValue);
        System.out.println("int値: " + intValue);
        System.out.println("long値: " + longValue);
        System.out.println("float値: " + floatValue);
        System.out.println("double値: " + doubleValue);
        System.out.println("char値: " + charValue);
        System.out.println("boolean値: " + booleanValue);
        System.out.println("String値: " + stringValue);
        
        System.out.println(); // 空行
        
        // 解答例 2: 各型の範囲と特徴の確認
        System.out.println("=== 各データ型の範囲 ===");
        System.out.println("byte範囲: " + Byte.MIN_VALUE + " ～ " + Byte.MAX_VALUE);
        System.out.println("short範囲: " + Short.MIN_VALUE + " ～ " + Short.MAX_VALUE);
        System.out.println("int範囲: " + Integer.MIN_VALUE + " ～ " + Integer.MAX_VALUE);
        System.out.println("long範囲: " + Long.MIN_VALUE + " ～ " + Long.MAX_VALUE);
        System.out.println("float範囲: " + Float.MIN_VALUE + " ～ " + Float.MAX_VALUE);
        System.out.println("double範囲: " + Double.MIN_VALUE + " ～ " + Double.MAX_VALUE);
        
        System.out.println(); // 空行
        
        // 解答例 3: 様々なリテラルの記述方法
        System.out.println("=== リテラルの記述方法 ===");
        
        // 整数リテラル
        int decimal = 42;           // 10進数
        int binary = 0b101010;      // 2進数 (Java 7以降)
        int octal = 052;            // 8進数
        int hexadecimal = 0x2A;     // 16進数
        
        System.out.println("10進数: " + decimal);
        System.out.println("2進数: " + binary);
        System.out.println("8進数: " + octal);
        System.out.println("16進数: " + hexadecimal);
        
        // 浮動小数点リテラル
        double d1 = 123.456;        // 通常の記述
        double d2 = 1.23456e2;      // 指数表記 (123.456)
        double d3 = 1.23456E2;      // 指数表記（大文字E）
        
        System.out.println("通常: " + d1);
        System.out.println("指数表記(e): " + d2);
        System.out.println("指数表記(E): " + d3);
        
        // 文字リテラル
        char ch1 = 'J';             // 通常の文字
        char ch2 = '\n';            // エスケープ文字（改行）
        char ch3 = '\u0041';        // Unicode（A）
        char ch4 = 65;              // ASCII値（A）
        
        System.out.println("通常文字: " + ch1);
        System.out.print("改行文字: Hello");
        System.out.print(ch2);
        System.out.println("World");
        System.out.println("Unicode: " + ch3);
        System.out.println("ASCII値: " + ch4);
        
        System.out.println(); // 空行
        
        // 解答例 4: 型変換の例
        System.out.println("=== 型変換の例 ===");
        
        // 暗黙的型変換（拡大変換）
        byte b = 10;
        int i = b;              // byte → int（自動変換）
        double d = i;           // int → double（自動変換）
        
        System.out.println("byte → int: " + b + " → " + i);
        System.out.println("int → double: " + i + " → " + d);
        
        // 明示的型変換（縮小変換）
        double largeDouble = 123.456;
        int convertedInt = (int)largeDouble;    // double → int（キャスト）
        byte convertedByte = (byte)convertedInt; // int → byte（キャスト）
        
        System.out.println("double → int: " + largeDouble + " → " + convertedInt);
        System.out.println("int → byte: " + convertedInt + " → " + convertedByte);
        
        System.out.println(); // 空行
        
        // 解答例 5: 実用的な例
        System.out.println("=== 実用例 ===");
        
        // 学生情報の表現
        String studentName = "山田太郎";
        int studentAge = 20;
        double height = 175.5;
        double weight = 65.8;
        char grade = 'A';
        boolean isGraduated = false;
        
        // BMI計算
        double bmi = weight / ((height / 100) * (height / 100));
        
        System.out.println("学生名: " + studentName);
        System.out.println("年齢: " + studentAge + "歳");
        System.out.println("身長: " + height + "cm");
        System.out.println("体重: " + weight + "kg");
        System.out.println("成績: " + grade + "ランク");
        System.out.println("卒業済み: " + isGraduated);
        System.out.println("BMI: " + String.format("%.2f", bmi));
        
        // 条件による判定
        String bmiCategory;
        if (bmi < 18.5) {
            bmiCategory = "痩せ型";
        } else if (bmi < 25.0) {
            bmiCategory = "標準";
        } else {
            bmiCategory = "肥満";
        }
        System.out.println("BMI分類: " + bmiCategory);
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