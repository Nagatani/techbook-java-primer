/**
 * 第1章 応用課題1: 高機能計算機
 * 
 * 2つの数値を使って様々な計算を行う高機能な計算機プログラム。
 * 整数と小数の両方に対応し、基本演算に加えて統計的な計算も行います。
 * 
 * 学習ポイント:
 * - double型を使った精密な計算
 * - 型変換（キャスト）の理解
 * - Math.max(), Math.min()等のMathクラスメソッドの活用
 * - String.format()を使った出力フォーマット
 * - 複雑な計算ロジックの組み立て
 */

public class Calculator {
    public static void main(String[] args) {
        // 計算に使用する2つの数値を定義
        double num1 = 10.5;
        double num2 = 3.2;
        
        // プログラムのタイトル表示
        System.out.println("=== 高機能計算機 ===");
        System.out.println("数値1: " + num1);
        System.out.println("数値2: " + num2);
        System.out.println();
        
        // 基本計算の実行と表示
        performBasicCalculations(num1, num2);
        
        // 統計計算の実行と表示
        performStatisticalCalculations(num1, num2);
        
        // 整数変換の実行と表示
        performIntegerConversions(num1, num2);
        
        // 追加の高度計算
        performAdvancedCalculations(num1, num2);
    }
    
    /**
     * 基本的な四則演算を実行し、結果を表示します
     * @param a 第1の数値
     * @param b 第2の数値
     */
    private static void performBasicCalculations(double a, double b) {
        System.out.println("基本計算:");
        
        // 加算
        double sum = a + b;
        System.out.println(a + " + " + b + " = " + sum);
        
        // 減算
        double difference = a - b;
        System.out.println(a + " - " + b + " = " + difference);
        
        // 乗算
        double product = a * b;
        System.out.println(a + " × " + b + " = " + product);
        
        // 除算（ゼロ除算チェック付き）
        if (b != 0) {
            double quotient = a / b;
            System.out.println(a + " ÷ " + b + " = " + quotient);
        } else {
            System.out.println(a + " ÷ " + b + " = エラー（ゼロ除算）");
        }
        
        // 剰余演算
        if (b != 0) {
            double remainder = a % b;
            System.out.println(a + " % " + b + " = " + remainder);
        }
        
        System.out.println();
    }
    
    /**
     * 統計的な計算を実行し、結果を表示します
     * @param a 第1の数値
     * @param b 第2の数値
     */
    private static void performStatisticalCalculations(double a, double b) {
        System.out.println("統計計算:");
        
        // 平均値
        double average = (a + b) / 2.0;
        System.out.println("平均値: " + String.format("%.2f", average));
        
        // 最大値
        double maximum = Math.max(a, b);
        System.out.println("最大値: " + maximum);
        
        // 最小値
        double minimum = Math.min(a, b);
        System.out.println("最小値: " + minimum);
        
        // 差の絶対値
        double absoluteDifference = Math.abs(a - b);
        System.out.println("差の絶対値: " + absoluteDifference);
        
        // 幾何平均（正の数の場合のみ）
        if (a > 0 && b > 0) {
            double geometricMean = Math.sqrt(a * b);
            System.out.println("幾何平均: " + String.format("%.2f", geometricMean));
        }
        
        System.out.println();
    }
    
    /**
     * 整数変換関連の処理を実行し、結果を表示します
     * @param a 第1の数値
     * @param b 第2の数値
     */
    private static void performIntegerConversions(double a, double b) {
        System.out.println("整数変換:");
        
        // 整数部の抽出（キャスト使用）
        int intPartA = (int) a;
        int intPartB = (int) b;
        System.out.println("数値1の整数部: " + intPartA);
        System.out.println("数値2の整数部: " + intPartB);
        
        // 小数部の計算
        double fracPartA = a - intPartA;
        double fracPartB = b - intPartB;
        System.out.println("数値1の小数部: " + String.format("%.2f", fracPartA));
        System.out.println("数値2の小数部: " + String.format("%.2f", fracPartB));
        
        // 四捨五入
        long roundedA = Math.round(a);
        long roundedB = Math.round(b);
        System.out.println("数値1の四捨五入: " + roundedA);
        System.out.println("数値2の四捨五入: " + roundedB);
        
        // 切り上げ
        double ceilA = Math.ceil(a);
        double ceilB = Math.ceil(b);
        System.out.println("数値1の切り上げ: " + (int) ceilA);
        System.out.println("数値2の切り上げ: " + (int) ceilB);
        
        // 切り下げ
        double floorA = Math.floor(a);
        double floorB = Math.floor(b);
        System.out.println("数値1の切り下げ: " + (int) floorA);
        System.out.println("数値2の切り下げ: " + (int) floorB);
        
        System.out.println();
    }
    
    /**
     * 高度な数学的計算を実行し、結果を表示します
     * @param a 第1の数値
     * @param b 第2の数値
     */
    private static void performAdvancedCalculations(double a, double b) {
        System.out.println("高度計算:");
        
        // べき乗計算
        double powerAB = Math.pow(a, b);
        System.out.println(a + " の " + b + " 乗: " + String.format("%.2f", powerAB));
        
        // 平方根（正の数の場合のみ）
        if (a >= 0) {
            double sqrtA = Math.sqrt(a);
            System.out.println(a + " の平方根: " + String.format("%.2f", sqrtA));
        }
        if (b >= 0) {
            double sqrtB = Math.sqrt(b);
            System.out.println(b + " の平方根: " + String.format("%.2f", sqrtB));
        }
        
        // 自然対数（正の数の場合のみ）
        if (a > 0) {
            double logA = Math.log(a);
            System.out.println(a + " の自然対数: " + String.format("%.2f", logA));
        }
        if (b > 0) {
            double logB = Math.log(b);
            System.out.println(b + " の自然対数: " + String.format("%.2f", logB));
        }
        
        // 常用対数（正の数の場合のみ）
        if (a > 0) {
            double log10A = Math.log10(a);
            System.out.println(a + " の常用対数: " + String.format("%.2f", log10A));
        }
        if (b > 0) {
            double log10B = Math.log10(b);
            System.out.println(b + " の常用対数: " + String.format("%.2f", log10B));
        }
        
        // 三角関数（ラジアン）
        double sinA = Math.sin(a);
        double cosA = Math.cos(a);
        double tanA = Math.tan(a);
        System.out.println(a + " ラジアンの sin: " + String.format("%.2f", sinA));
        System.out.println(a + " ラジアンの cos: " + String.format("%.2f", cosA));
        System.out.println(a + " ラジアンの tan: " + String.format("%.2f", tanA));
        
        System.out.println();
    }
}

/*
 * 実行結果例:
 * 
 * === 高機能計算機 ===
 * 数値1: 10.5
 * 数値2: 3.2
 * 
 * 基本計算:
 * 10.5 + 3.2 = 13.7
 * 10.5 - 3.2 = 7.3
 * 10.5 × 3.2 = 33.6
 * 10.5 ÷ 3.2 = 3.28125
 * 10.5 % 3.2 = 0.8999999999999995
 * 
 * 統計計算:
 * 平均値: 6.85
 * 最大値: 10.5
 * 最小値: 3.2
 * 差の絶対値: 7.3
 * 幾何平均: 5.81
 * 
 * 整数変換:
 * 数値1の整数部: 10
 * 数値2の整数部: 3
 * 数値1の小数部: 0.50
 * 数値2の小数部: 0.20
 * 数値1の四捨五入: 11
 * 数値2の四捨五入: 3
 * 数値1の切り上げ: 11
 * 数値2の切り上げ: 4
 * 数値1の切り下げ: 10
 * 数値2の切り下げ: 3
 * 
 * 高度計算:
 * 10.5 の 3.2 乗: 1631.93
 * 10.5 の平方根: 3.24
 * 3.2 の平方根: 1.79
 * 10.5 の自然対数: 2.35
 * 3.2 の自然対数: 1.16
 * 10.5 の常用対数: 1.02
 * 3.2 の常用対数: 0.51
 * 10.5 ラジアンの sin: -0.88
 * 10.5 ラジアンの cos: -0.48
 * 10.5 ラジアンの tan: 1.82
 * 
 * 学習のポイント:
 * 1. double型の精度：浮動小数点数の計算には微小な誤差が生じることがある
 * 2. Math クラス：様々な数学関数が提供されている
 * 3. 型変換：(int) を使った明示的なキャスト
 * 4. 条件分岐：ゼロ除算や負の数の平方根など、数学的に無効な操作を避ける
 * 5. フォーマット：String.format() で小数点以下の桁数を制御
 */