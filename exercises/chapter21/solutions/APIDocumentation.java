/**
 * 第21章 課題1: APIドキュメント作成 - 解答例
 * 
 * 包括的なJavadocドキュメンテーションを含む数学ユーティリティクラス
 * 
 * <p>このクラスは数学的計算と統計処理のための静的メソッドを提供します。
 * すべてのメソッドはスレッドセーフで、null値や無効な引数に対して
 * 適切な例外処理を行います。</p>
 * 
 * <p><strong>使用例:</strong></p>
 * <pre>
 * // 基本的な数学計算
 * double result = MathUtils.average(1.0, 2.0, 3.0, 4.0, 5.0);
 * 
 * // 配列を使った統計計算
 * double[] data = {10.5, 20.3, 15.7, 8.9, 12.1};
 * Statistics stats = MathUtils.calculateStatistics(data);
 * System.out.println("平均: " + stats.getMean());
 * 
 * // 範囲チェック
 * boolean inRange = MathUtils.isInRange(5.0, 1.0, 10.0);
 * </pre>
 * 
 * @author Hidehiro Nagatani
 * @version 2.1.0
 * @since 1.0
 * @see java.lang.Math
 * @see java.util.Arrays
 */
public final class APIDocumentation {
    
    /**
     * ユーティリティクラスのため、インスタンス化を禁止します。
     * 
     * @throws UnsupportedOperationException インスタンス化が試行された場合
     */
    private APIDocumentation() {
        throw new UnsupportedOperationException(
            "このクラスはユーティリティクラスのため、インスタンス化できません。");
    }
    
    /**
     * 数学定数: 黄金比 (Golden Ratio)
     * 
     * <p>黄金比 φ = (1 + √5) / 2 ≈ 1.618033988749895</p>
     * <p>この定数は美術、建築、自然界の比率計算に使用されます。</p>
     * 
     * @since 1.0
     */
    public static final double GOLDEN_RATIO = (1.0 + Math.sqrt(5.0)) / 2.0;
    
    /**
     * 数学定数: オイラー数 (Euler's number)
     * 
     * <p>自然対数の底 e ≈ 2.718281828459045</p>
     * <p>指数関数や対数計算の基準値として使用されます。</p>
     * 
     * @since 1.0
     */
    public static final double EULER_NUMBER = Math.E;
    
    /**
     * 許容誤差: 浮動小数点数の比較で使用する最小差
     * 
     * <p>浮動小数点数の計算精度を考慮した比較処理で使用します。</p>
     * 
     * @since 2.0
     */
    public static final double EPSILON = 1e-10;
    
    /**
     * 可変長引数で渡された数値の平均値を計算します。
     * 
     * <p>このメソッドは任意の個数の数値を受け取り、それらの算術平均を返します。
     * 引数が0個の場合は例外が発生します。</p>
     * 
     * <p><strong>計算式:</strong> 平均 = (x₁ + x₂ + ... + xₙ) / n</p>
     * 
     * @param values 平均を計算する数値の配列（少なくとも1つの値が必要）
     * @return 引数の算術平均値
     * @throws IllegalArgumentException 引数が0個の場合
     * @throws ArithmeticException 計算結果が無限大またはNaNの場合
     * 
     * @since 1.0
     * 
     * @see #median(double...)
     * @see #calculateStatistics(double[])
     * 
     * @example
     * <pre>
     * double avg1 = average(1.0, 2.0, 3.0);  // 結果: 2.0
     * double avg2 = average(10.5, 20.3, 15.7);  // 結果: 15.5
     * </pre>
     */
    public static double average(double... values) {
        if (values == null) {
            throw new IllegalArgumentException("引数がnullです");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException("少なくとも1つの値が必要です");
        }
        
        double sum = 0.0;
        for (double value : values) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new ArithmeticException("無効な数値が含まれています: " + value);
            }
            sum += value;
        }
        
        double result = sum / values.length;
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new ArithmeticException("計算結果が無効です");
        }
        
        return result;
    }
    
    /**
     * 配列の中央値（メディアン）を計算します。
     * 
     * <p>中央値は、データを昇順にソートした時の中央の値です。
     * データ数が偶数の場合は、中央の2つの値の平均を返します。</p>
     * 
     * <p><strong>注意:</strong> この メソッドは引数の配列を変更しません。
     * 内部でコピーを作成してソートを行います。</p>
     * 
     * @param values 中央値を計算する数値の配列（nullや空配列は不可）
     * @return 配列の中央値
     * @throws IllegalArgumentException 配列がnullまたは空の場合
     * @throws ArithmeticException 無効な数値が含まれている場合
     * 
     * @since 1.5
     * 
     * @see #average(double...)
     * @see java.util.Arrays#sort(double[])
     * 
     * @example
     * <pre>
     * double[] data1 = {1.0, 3.0, 2.0};  // ソート後: [1.0, 2.0, 3.0]
     * double median1 = median(data1);     // 結果: 2.0
     * 
     * double[] data2 = {1.0, 2.0, 3.0, 4.0};  // 中央2つの平均
     * double median2 = median(data2);          // 結果: 2.5
     * </pre>
     */
    public static double median(double... values) {
        if (values == null) {
            throw new IllegalArgumentException("引数がnullです");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException("配列が空です");
        }
        
        // 無効な値のチェック
        for (double value : values) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new ArithmeticException("無効な数値が含まれています: " + value);
            }
        }
        
        // 配列をコピーしてソート（元の配列を変更しないため）
        double[] sorted = values.clone();
        java.util.Arrays.sort(sorted);
        
        int length = sorted.length;
        if (length % 2 == 1) {
            // 奇数個の場合：中央の値
            return sorted[length / 2];
        } else {
            // 偶数個の場合：中央2つの平均
            return (sorted[length / 2 - 1] + sorted[length / 2]) / 2.0;
        }
    }
    
    /**
     * 値が指定された範囲内にあるかどうかを判定します。
     * 
     * <p>このメソッドは境界値を含む範囲チェックを行います。
     * つまり、min ≤ value ≤ max の場合にtrueを返します。</p>
     * 
     * @param value チェックする値
     * @param min 範囲の最小値（境界値を含む）
     * @param max 範囲の最大値（境界値を含む）
     * @return 値が範囲内の場合true、範囲外の場合false
     * @throws IllegalArgumentException min > max の場合
     * @throws ArithmeticException いずれかの引数がNaNまたは無限大の場合
     * 
     * @since 2.0
     * 
     * @see #clamp(double, double, double)
     * 
     * @example
     * <pre>
     * boolean result1 = isInRange(5.0, 1.0, 10.0);   // true
     * boolean result2 = isInRange(15.0, 1.0, 10.0);  // false
     * boolean result3 = isInRange(1.0, 1.0, 10.0);   // true (境界値)
     * </pre>
     */
    public static boolean isInRange(double value, double min, double max) {
        // NaNと無限大のチェック
        if (Double.isNaN(value) || Double.isInfinite(value) ||
            Double.isNaN(min) || Double.isInfinite(min) ||
            Double.isNaN(max) || Double.isInfinite(max)) {
            throw new ArithmeticException("NaNまたは無限大は使用できません");
        }
        
        if (min > max) {
            throw new IllegalArgumentException("最小値が最大値より大きいです: min=" + min + ", max=" + max);
        }
        
        return value >= min && value <= max;
    }
    
    /**
     * 値を指定された範囲内にクランプ（制限）します。
     * 
     * <p>値が範囲を超えている場合、最も近い境界値を返します。
     * 値が範囲内にある場合は、そのまま返します。</p>
     * 
     * @param value クランプする値
     * @param min 範囲の最小値
     * @param max 範囲の最大値
     * @return 範囲内にクランプされた値
     * @throws IllegalArgumentException min > max の場合
     * @throws ArithmeticException いずれかの引数がNaNまたは無限大の場合
     * 
     * @since 2.0
     * 
     * @see #isInRange(double, double, double)
     * 
     * @example
     * <pre>
     * double result1 = clamp(5.0, 1.0, 10.0);   // 5.0 (範囲内)
     * double result2 = clamp(15.0, 1.0, 10.0);  // 10.0 (上限でクランプ)
     * double result3 = clamp(-5.0, 1.0, 10.0);  // 1.0 (下限でクランプ)
     * </pre>
     */
    public static double clamp(double value, double min, double max) {
        // NaNと無限大のチェック
        if (Double.isNaN(value) || Double.isInfinite(value) ||
            Double.isNaN(min) || Double.isInfinite(min) ||
            Double.isNaN(max) || Double.isInfinite(max)) {
            throw new ArithmeticException("NaNまたは無限大は使用できません");
        }
        
        if (min > max) {
            throw new IllegalArgumentException("最小値が最大値より大きいです: min=" + min + ", max=" + max);
        }
        
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        } else {
            return value;
        }
    }
    
    /**
     * 2つの浮動小数点数が指定された許容誤差内で等しいかどうかを判定します。
     * 
     * <p>浮動小数点数の比較では、計算誤差により完全な等価性の判定が困難なため、
     * 許容誤差を用いた比較を行います。</p>
     * 
     * @param a 比較する値1
     * @param b 比較する値2
     * @param epsilon 許容誤差（正の値である必要があります）
     * @return 2つの値が許容誤差内で等しい場合true
     * @throws IllegalArgumentException epsilonが負または0の場合
     * @throws ArithmeticException いずれかの引数がNaNまたは無限大の場合
     * 
     * @since 2.0
     * 
     * @see #EPSILON
     * 
     * @example
     * <pre>
     * boolean result1 = equals(0.1 + 0.2, 0.3, 1e-10);  // true
     * boolean result2 = equals(1.0, 1.1, 0.05);         // false
     * boolean result3 = equals(1.0, 1.01, 0.02);        // true
     * </pre>
     */
    public static boolean equals(double a, double b, double epsilon) {
        if (epsilon <= 0) {
            throw new IllegalArgumentException("許容誤差は正の値である必要があります: " + epsilon);
        }
        
        if (Double.isNaN(a) || Double.isInfinite(a) ||
            Double.isNaN(b) || Double.isInfinite(b) ||
            Double.isNaN(epsilon) || Double.isInfinite(epsilon)) {
            throw new ArithmeticException("NaNまたは無限大は使用できません");
        }
        
        return Math.abs(a - b) <= epsilon;
    }
    
    /**
     * デフォルトの許容誤差を使用して2つの浮動小数点数が等しいかどうかを判定します。
     * 
     * <p>このメソッドは{@link #EPSILON}定数を許容誤差として使用します。</p>
     * 
     * @param a 比較する値1
     * @param b 比較する値2
     * @return 2つの値がデフォルトの許容誤差内で等しい場合true
     * @throws ArithmeticException いずれかの引数がNaNまたは無限大の場合
     * 
     * @since 2.0
     * 
     * @see #equals(double, double, double)
     * @see #EPSILON
     */
    public static boolean equals(double a, double b) {
        return equals(a, b, EPSILON);
    }
    
    /**
     * n階乗を計算します。
     * 
     * <p>階乗は 1 から n までの連続する整数の積です。
     * 0! = 1 と定義されています。</p>
     * 
     * <p><strong>注意:</strong> 大きな値では結果がdoubleの範囲を超える可能性があります。
     * n > 170 の場合、結果は無限大になります。</p>
     * 
     * @param n 階乗を計算する非負整数
     * @return n! の値
     * @throws IllegalArgumentException nが負の場合
     * 
     * @since 1.0
     * 
     * @see #permutation(int, int)
     * @see #combination(int, int)
     * 
     * @example
     * <pre>
     * double result1 = factorial(5);   // 120.0 (5! = 5×4×3×2×1)
     * double result2 = factorial(0);   // 1.0 (0! = 1)
     * double result3 = factorial(10);  // 3628800.0
     * </pre>
     */
    public static double factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("階乗は非負整数に対してのみ定義されています: " + n);
        }
        
        if (n == 0 || n == 1) {
            return 1.0;
        }
        
        double result = 1.0;
        for (int i = 2; i <= n; i++) {
            result *= i;
            if (Double.isInfinite(result)) {
                break;  // オーバーフローした場合は無限大を返す
            }
        }
        
        return result;
    }
    
    /**
     * 包括的な統計情報を格納するデータクラスです。
     * 
     * <p>このクラスは配列データの統計分析結果を保持します。
     * すべてのフィールドはfinalで、イミュータブルな設計になっています。</p>
     * 
     * @since 2.1
     * 
     * @see #calculateStatistics(double[])
     */
    public static final class Statistics {
        
        /** サンプル数 */
        private final int count;
        
        /** 平均値 */
        private final double mean;
        
        /** 中央値 */
        private final double median;
        
        /** 最小値 */
        private final double min;
        
        /** 最大値 */
        private final double max;
        
        /** 標準偏差 */
        private final double standardDeviation;
        
        /** 分散 */
        private final double variance;
        
        /**
         * 統計情報オブジェクトを構築します。
         * 
         * @param count サンプル数
         * @param mean 平均値
         * @param median 中央値
         * @param min 最小値
         * @param max 最大値
         * @param standardDeviation 標準偏差
         * @param variance 分散
         */
        private Statistics(int count, double mean, double median, double min, 
                         double max, double standardDeviation, double variance) {
            this.count = count;
            this.mean = mean;
            this.median = median;
            this.min = min;
            this.max = max;
            this.standardDeviation = standardDeviation;
            this.variance = variance;
        }
        
        /**
         * サンプル数を取得します。
         * 
         * @return サンプル数
         */
        public int getCount() { return count; }
        
        /**
         * 平均値を取得します。
         * 
         * @return 平均値
         */
        public double getMean() { return mean; }
        
        /**
         * 中央値を取得します。
         * 
         * @return 中央値
         */
        public double getMedian() { return median; }
        
        /**
         * 最小値を取得します。
         * 
         * @return 最小値
         */
        public double getMin() { return min; }
        
        /**
         * 最大値を取得します。
         * 
         * @return 最大値
         */
        public double getMax() { return max; }
        
        /**
         * 標準偏差を取得します。
         * 
         * @return 標準偏差
         */
        public double getStandardDeviation() { return standardDeviation; }
        
        /**
         * 分散を取得します。
         * 
         * @return 分散
         */
        public double getVariance() { return variance; }
        
        /**
         * 範囲（最大値 - 最小値）を取得します。
         * 
         * @return データの範囲
         */
        public double getRange() { return max - min; }
        
        /**
         * 統計情報の文字列表現を返します。
         * 
         * @return 統計情報の詳細な文字列表現
         */
        @Override
        public String toString() {
            return String.format(
                "Statistics{count=%d, mean=%.3f, median=%.3f, min=%.3f, max=%.3f, " +
                "stdDev=%.3f, variance=%.3f, range=%.3f}",
                count, mean, median, min, max, standardDeviation, variance, getRange()
            );
        }
    }
    
    /**
     * 配列の包括的な統計情報を計算します。
     * 
     * <p>このメソッドは、与えられた配列から以下の統計量を計算します：</p>
     * <ul>
     *   <li>サンプル数</li>
     *   <li>平均値</li>
     *   <li>中央値</li>
     *   <li>最小値</li>
     *   <li>最大値</li>
     *   <li>標準偏差（サンプル標準偏差）</li>
     *   <li>分散（サンプル分散）</li>
     * </ul>
     * 
     * @param data 統計を計算する数値配列（nullや空配列は不可）
     * @return 計算された統計情報を含むStatisticsオブジェクト
     * @throws IllegalArgumentException 配列がnullまたは空の場合
     * @throws ArithmeticException 無効な数値が含まれている場合
     * 
     * @since 2.1
     * 
     * @see Statistics
     * @see #average(double...)
     * @see #median(double...)
     * 
     * @example
     * <pre>
     * double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
     * Statistics stats = calculateStatistics(data);
     * System.out.println("平均: " + stats.getMean());      // 3.0
     * System.out.println("中央値: " + stats.getMedian());   // 3.0
     * System.out.println("標準偏差: " + stats.getStandardDeviation());
     * </pre>
     */
    public static Statistics calculateStatistics(double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("配列がnullです");
        }
        if (data.length == 0) {
            throw new IllegalArgumentException("配列が空です");
        }
        
        // 無効な値のチェック
        for (double value : data) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                throw new ArithmeticException("無効な数値が含まれています: " + value);
            }
        }
        
        int count = data.length;
        
        // 平均値の計算
        double mean = average(data);
        
        // 中央値の計算
        double median = median(data);
        
        // 最小値・最大値の計算
        double min = data[0];
        double max = data[0];
        for (double value : data) {
            if (value < min) min = value;
            if (value > max) max = value;
        }
        
        // 分散の計算
        double variance = 0.0;
        for (double value : data) {
            double diff = value - mean;
            variance += diff * diff;
        }
        variance /= count;  // サンプル分散
        
        // 標準偏差の計算
        double standardDeviation = Math.sqrt(variance);
        
        return new Statistics(count, mean, median, min, max, standardDeviation, variance);
    }
    
    /**
     * メインメソッド：APIDocumentationクラスの使用例を示します。
     * 
     * <p>このメソッドは各機能の基本的な使用法をデモンストレーションします。
     * 実際のアプリケーションでの参考として利用してください。</p>
     * 
     * @param args コマンドライン引数（使用しません）
     * 
     * @since 1.0
     */
    public static void main(String[] args) {
        System.out.println("=== 数学ユーティリティクラス デモンストレーション ===");
        System.out.println();
        
        // 定数の表示
        System.out.println("数学定数:");
        System.out.printf("黄金比: %.15f%n", GOLDEN_RATIO);
        System.out.printf("オイラー数: %.15f%n", EULER_NUMBER);
        System.out.printf("許容誤差: %.2e%n", EPSILON);
        System.out.println();
        
        // 基本計算のデモ
        System.out.println("基本計算:");
        double[] testData = {10.5, 20.3, 15.7, 8.9, 12.1, 18.6, 9.4};
        
        double avg = average(testData);
        System.out.printf("平均値: %.2f%n", avg);
        
        double med = median(testData);
        System.out.printf("中央値: %.2f%n", med);
        System.out.println();
        
        // 範囲チェックのデモ
        System.out.println("範囲チェック:");
        double testValue = 15.0;
        boolean inRange = isInRange(testValue, 10.0, 20.0);
        System.out.printf("%.1f は [10.0, 20.0] の範囲内: %b%n", testValue, inRange);
        
        double clamped = clamp(25.0, 10.0, 20.0);
        System.out.printf("25.0 を [10.0, 20.0] にクランプ: %.1f%n", clamped);
        System.out.println();
        
        // 浮動小数点比較のデモ
        System.out.println("浮動小数点比較:");
        double a = 0.1 + 0.2;
        double b = 0.3;
        boolean isEqual = equals(a, b);
        System.out.printf("0.1 + 0.2 = %.17f%n", a);
        System.out.printf("0.3       = %.17f%n", b);
        System.out.printf("許容誤差内で等しい: %b%n", isEqual);
        System.out.println();
        
        // 階乗のデモ
        System.out.println("階乗計算:");
        for (int i = 0; i <= 10; i++) {
            System.out.printf("%d! = %.0f%n", i, factorial(i));
        }
        System.out.println();
        
        // 統計計算のデモ
        System.out.println("統計計算:");
        Statistics stats = calculateStatistics(testData);
        System.out.println(stats);
        System.out.println();
        
        System.out.println("=== デモンストレーション完了 ===");
    }
}

/*
 * Javadocドキュメンテーションのベストプラクティス:
 * 
 * 1. クラスレベルのドキュメント
 *    - 目的と概要の明確な説明
 *    - 使用例の提供
 *    - 作成者、バージョン、開始バージョンの記載
 *    - 関連クラスへの参照
 * 
 * 2. メソッドレベルのドキュメント
 *    - 機能の詳細な説明
 *    - すべてのパラメータの説明
 *    - 戻り値の説明
 *    - 発生する可能性のある例外
 *    - 使用例の提供
 *    - 関連メソッドへの参照
 * 
 * 3. フィールドレベルのドキュメント
 *    - 定数の意味と用途
 *    - 値の説明
 *    - 開始バージョンの記載
 * 
 * 4. HTMLタグの適切な使用
 *    - <p>タグで段落分け
 *    - <pre>タグでコード例
 *    - <strong>タグで強調
 *    - <ul><li>タグでリスト
 * 
 * 5. 標準タグの活用
 *    - @param: パラメータ説明
 *    - @return: 戻り値説明
 *    - @throws: 例外説明
 *    - @since: 開始バージョン
 *    - @see: 関連要素への参照
 *    - @author: 作成者
 *    - @version: バージョン
 *    - @example: 使用例（カスタムタグ）
 * 
 * 6. 読みやすさの向上
 *    - 適切な段落分け
 *    - 重要な情報の強調
 *    - 具体的な使用例
 *    - 注意事項の明記
 * 
 * 7. 国際化への配慮
 *    - 日本語での説明
 *    - 文字エンコーディングの考慮
 *    - 文化的な違いの配慮
 */