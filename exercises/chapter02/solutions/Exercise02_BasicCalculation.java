/**
 * 第2章 課題2: 基本計算プログラム - 解答例
 * 
 * 2つの整数を使って四則演算を行うプログラムを作成してください。
 * 
 * 学習ポイント:
 * - 四則演算子の使い方
 * - 整数除算の特徴
 * - 余りの演算
 */
public class Exercise02_BasicCalculation {
    public static void main(String[] args) {
        int num1 = 15;
        int num2 = 4;
        
        IO.println("=== 計算結果 ===");
        IO.println("数値1: " + num1);
        IO.println("数値2: " + num2);
        IO.println();
        
        // 四則演算の実行
        int addition = num1 + num2;
        int subtraction = num1 - num2;
        int multiplication = num1 * num2;
        int division = num1 / num2;
        int remainder = num1 % num2;
        
        IO.println(num1 + " + " + num2 + " = " + addition);
        IO.println(num1 + " - " + num2 + " = " + subtraction);
        IO.println(num1 + " × " + num2 + " = " + multiplication);
        IO.println(num1 + " ÷ " + num2 + " = " + division);
        IO.println(num1 + " % " + num2 + " = " + remainder);

        // 整数どうしの割り算は、小数点以下が切り捨てられる（15 / 4 は 3）。
        // 小数の結果がほしいときは、どちらかをdoubleにしてから割る
        // double result = (double) num1 / num2;   // 3.75
        // doubleの変数どうし（15.0と4.0）で計算すると、どの結果も小数になる（15.0 % 4.0 は 3.0）
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