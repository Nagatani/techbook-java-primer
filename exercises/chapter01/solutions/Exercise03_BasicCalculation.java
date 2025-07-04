/**
 * 第1章 課題3: 基本計算プログラム - 解答例
 * 
 * 2つの整数を使って四則演算を行うプログラムを作成してください。
 * 
 * 学習ポイント:
 * - 四則演算子の使い方
 * - 整数除算の特徴
 * - 余りの演算
 */
public class Exercise03_BasicCalculation {
    public static void main(String[] args) {
        // 解答例 1: 基本的な実装
        int num1 = 15;
        int num2 = 4;
        
        System.out.println("=== 計算結果 ===");
        System.out.println("数値1: " + num1);
        System.out.println("数値2: " + num2);
        System.out.println();
        
        // 四則演算の実行
        int addition = num1 + num2;
        int subtraction = num1 - num2;
        int multiplication = num1 * num2;
        int division = num1 / num2;
        int remainder = num1 % num2;
        
        System.out.println(num1 + " + " + num2 + " = " + addition);
        System.out.println(num1 + " - " + num2 + " = " + subtraction);
        System.out.println(num1 + " × " + num2 + " = " + multiplication);
        System.out.println(num1 + " ÷ " + num2 + " = " + division);
        System.out.println(num1 + " % " + num2 + " = " + remainder);
        
        System.out.println(); // 空行
        
        // 解答例 2: 小数点の計算も含む実装
        double num3 = 15.0;
        double num4 = 4.0;
        
        System.out.println("=== 小数点を含む計算結果 ===");
        System.out.println("数値1: " + num3);
        System.out.println("数値2: " + num4);
        System.out.println();
        
        double doubleAddition = num3 + num4;
        double doubleSubtraction = num3 - num4;
        double doubleMultiplication = num3 * num4;
        double doubleDivision = num3 / num4;
        double doubleRemainder = num3 % num4;
        
        System.out.println(num3 + " + " + num4 + " = " + doubleAddition);
        System.out.println(num3 + " - " + num4 + " = " + doubleSubtraction);
        System.out.println(num3 + " × " + num4 + " = " + doubleMultiplication);
        System.out.println(num3 + " ÷ " + num4 + " = " + doubleDivision);
        System.out.println(num3 + " % " + num4 + " = " + doubleRemainder);
        
        System.out.println(); // 空行
        
        // 解答例 3: 整数除算と小数除算の比較
        System.out.println("=== 整数除算 vs 小数除算 ===");
        int intResult = num1 / num2;                    // 整数除算
        double doubleResult1 = (double)num1 / num2;     // キャスト使用
        double doubleResult2 = num1 / (double)num2;     // キャスト使用
        double doubleResult3 = 1.0 * num1 / num2;       // 暗黙の型変換
        
        System.out.println("整数除算: " + num1 + " ÷ " + num2 + " = " + intResult);
        System.out.println("小数除算: " + num1 + " ÷ " + num2 + " = " + doubleResult1);
        System.out.println("小数除算: " + num1 + " ÷ " + num2 + " = " + doubleResult2);
        System.out.println("小数除算: " + num1 + " ÷ " + num2 + " = " + doubleResult3);
        
        System.out.println(); // 空行
        
        // 解答例 4: より複雑な計算例
        System.out.println("=== 応用計算例 ===");
        int a = 10;
        int b = 3;
        int c = 2;
        
        // 複合演算
        int result1 = a + b * c;        // 演算子の優先順位
        int result2 = (a + b) * c;      // 括弧による優先順位変更
        int result3 = a * a + b * b;    // 二乗の計算
        
        System.out.println(a + " + " + b + " × " + c + " = " + result1);
        System.out.println("(" + a + " + " + b + ") × " + c + " = " + result2);
        System.out.println(a + "² + " + b + "² = " + result3);
        
        // 平均の計算
        int sum = a + b + c;
        double average = (double)sum / 3;
        System.out.println("(" + a + " + " + b + " + " + c + ") ÷ 3 = " + average);
    }
}

/*
 * 演算子の詳細解説:
 * 
 * + (加算): 数値の足し算、文字列の連結
 * - (減算): 数値の引き算
 * * (乗算): 数値の掛け算
 * / (除算): 数値の割り算
 * % (余り): 割り算の余りを求める
 * 
 * 重要なポイント:
 * 
 * 1. 整数除算の特徴
 *    - int型同士の除算は結果もint型
 *    - 15 / 4 = 3 (小数部は切り捨て)
 *    - 正確な小数値が欲しい場合はdouble型を使用
 * 
 * 2. 余り演算(%)の用途
 *    - 偶数奇数の判定: num % 2 == 0 なら偶数
 *    - 循環処理: 曜日計算、配列のインデックス調整など
 * 
 * 3. 演算子の優先順位
 *    - *, /, % が +, - より優先される
 *    - 同じ優先順位は左から右へ評価
 *    - 括弧()で優先順位を明示的に指定可能
 * 
 * よくある間違い:
 * 
 * 1. 整数除算の結果を誤解
 *    × int result = 15 / 4;  // 3.75ではなく3
 *    ○ double result = 15.0 / 4;  // 3.75
 * 
 * 2. ゼロ除算
 *    × int result = 10 / 0;  // 実行時エラー
 *    ○ 事前にゼロでないかチェック
 * 
 * 3. オーバーフロー
 *    × int huge = 2000000000 * 2;  // intの範囲を超える
 *    ○ long huge = 2000000000L * 2;  // long型を使用
 * 
 * デバッグのコツ:
 * - 計算の途中結果も出力して確認
 * - 期待値と実際の結果を比較
 * - 複雑な式は段階的に分解して検証
 */