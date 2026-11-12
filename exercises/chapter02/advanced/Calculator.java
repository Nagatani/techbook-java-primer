/**
 * 第2章 発展課題1: 高機能計算機
 * 
 * 2つの数値を使って様々な計算を行う高機能な計算機プログラム。
 * 整数と小数の両方に対応し、基本演算に加えて統計的な計算も行います。
 * 
 * 学習ポイント:
 * - double型を使った精密な計算
 * - 型変換（キャスト）の理解
 * - Math.max(), Math.min()等のMathクラスメソッドの活用
 * - formatted()を使った出力フォーマット
 * - 複雑な計算ロジックの組み立て
 */

public class Calculator {
    public static void main(String[] args) {
        // 計算に使用する2つの数値を定義
        double num1 = 10.5;
        double num2 = 3.2;
        
        // プログラムのタイトル表示
        IO.println("=== 高機能計算機 ===");
        IO.println("数値1: " + num1);
        IO.println("数値2: " + num2);
        IO.println();
        
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
        IO.println("基本計算:");
        
        // 加算
        double sum = a + b;
        IO.println(a + " + " + b + " = " + sum);
        
        // 減算
        double difference = a - b;
        IO.println(a + " - " + b + " = " + difference);
        
        // 乗算
        double product = a * b;
        IO.println(a + " × " + b + " = " + product);
        
        // 除算（ゼロ除算チェック付き）
        if (b != 0) {
            double quotient = a / b;
            IO.println(a + " ÷ " + b + " = " + quotient);
        } else {
            IO.println(a + " ÷ " + b + " = エラー（ゼロ除算）");
        }
        
        // 剰余演算
        if (b != 0) {
            double remainder = a % b;
            IO.println(a + " % " + b + " = " + remainder);
        }
        
        IO.println();
    }
    
    /**
     * 統計的な計算を実行し、結果を表示します
     * @param a 第1の数値
     * @param b 第2の数値
     */
    private static void performStatisticalCalculations(double a, double b) {
        IO.println("統計計算:");
        
        // 平均値
        double average = (a + b) / 2.0;
        IO.println("平均値: " + "%.2f".formatted(average));
        
        // 最大値
        double maximum = Math.max(a, b);
        IO.println("最大値: " + maximum);
        
        // 最小値
        double minimum = Math.min(a, b);
        IO.println("最小値: " + minimum);
        
        // 差の絶対値
        double absoluteDifference = Math.abs(a - b);
        IO.println("差の絶対値: " + absoluteDifference);
        
        // 幾何平均（正の数の場合のみ）
        if (a > 0 && b > 0) {
            double geometricMean = Math.sqrt(a * b);
            IO.println("幾何平均: " + "%.2f".formatted(geometricMean));
        }
        
        IO.println();
    }
    
    /**
     * 整数変換関連の処理を実行し、結果を表示します
     * @param a 第1の数値
     * @param b 第2の数値
     */
    private static void performIntegerConversions(double a, double b) {
        IO.println("整数変換:");
        
        // 整数部の抽出（キャスト使用）
        int intPartA = (int) a;
        int intPartB = (int) b;
        IO.println("数値1の整数部: " + intPartA);
        IO.println("数値2の整数部: " + intPartB);
        
        // 小数部の計算
        double fracPartA = a - intPartA;
        double fracPartB = b - intPartB;
        IO.println("数値1の小数部: " + "%.2f".formatted(fracPartA));
        IO.println("数値2の小数部: " + "%.2f".formatted(fracPartB));
        
        // 四捨五入
        long roundedA = Math.round(a);
        long roundedB = Math.round(b);
        IO.println("数値1の四捨五入: " + roundedA);
        IO.println("数値2の四捨五入: " + roundedB);
        
        // 切り上げ
        double ceilA = Math.ceil(a);
        double ceilB = Math.ceil(b);
        IO.println("数値1の切り上げ: " + (int) ceilA);
        IO.println("数値2の切り上げ: " + (int) ceilB);
        
        // 切り下げ
        double floorA = Math.floor(a);
        double floorB = Math.floor(b);
        IO.println("数値1の切り下げ: " + (int) floorA);
        IO.println("数値2の切り下げ: " + (int) floorB);
        
        IO.println();
    }
    
    /**
     * 高度な数学的計算を実行し、結果を表示します
     * @param a 第1の数値
     * @param b 第2の数値
     */
    private static void performAdvancedCalculations(double a, double b) {
        IO.println("高度計算:");
        
        // べき乗計算
        double powerAB = Math.pow(a, b);
        IO.println(a + " の " + b + " 乗: " + "%.2f".formatted(powerAB));
        
        // 平方根（正の数の場合のみ）
        if (a >= 0) {
            double sqrtA = Math.sqrt(a);
            IO.println(a + " の平方根: " + "%.2f".formatted(sqrtA));
        }
        if (b >= 0) {
            double sqrtB = Math.sqrt(b);
            IO.println(b + " の平方根: " + "%.2f".formatted(sqrtB));
        }
        
        // 自然対数（正の数の場合のみ）
        if (a > 0) {
            double logA = Math.log(a);
            IO.println(a + " の自然対数: " + "%.2f".formatted(logA));
        }
        if (b > 0) {
            double logB = Math.log(b);
            IO.println(b + " の自然対数: " + "%.2f".formatted(logB));
        }
        
        // 常用対数（正の数の場合のみ）
        if (a > 0) {
            double log10A = Math.log10(a);
            IO.println(a + " の常用対数: " + "%.2f".formatted(log10A));
        }
        if (b > 0) {
            double log10B = Math.log10(b);
            IO.println(b + " の常用対数: " + "%.2f".formatted(log10B));
        }
        
        // 三角関数（ラジアン）
        double sinA = Math.sin(a);
        double cosA = Math.cos(a);
        double tanA = Math.tan(a);
        IO.println(a + " ラジアンの sin: " + "%.2f".formatted(sinA));
        IO.println(a + " ラジアンの cos: " + "%.2f".formatted(cosA));
        IO.println(a + " ラジアンの tan: " + "%.2f".formatted(tanA));
        
        IO.println();
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
 * 5. フォーマット：formatted()で小数点以下の桁数を制御
 */