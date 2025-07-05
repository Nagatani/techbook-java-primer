/**
 * 第21章 課題2: 高度なAPIドキュメント作成 - 解答例
 * 
 * 実務レベルのJavadocドキュメンテーションを含むライブラリクラス
 * 
 * <p>このライブラリは、企業のデータ分析業務で使用される統計計算と
 * レポート生成機能を提供します。金融、マーケティング、品質管理など
 * 様々な分野での活用を想定して設計されています。</p>
 * 
 * <h2>主な機能</h2>
 * <ul>
 *   <li>記述統計量の計算（平均、分散、標準偏差、歪度、尖度）</li>
 *   <li>回帰分析（線形回帰、多項式回帰）</li>
 *   <li>時系列データの分析（移動平均、トレンド分析）</li>
 *   <li>確率分布の計算（正規分布、二項分布、ポアソン分布）</li>
 *   <li>データ可視化支援（ヒストグラム、散布図データ生成）</li>
 * </ul>
 * 
 * <h2>使用例</h2>
 * <pre>{@code
 * // 売上データの分析例
 * double[] salesData = {120.5, 135.2, 108.7, 145.9, 132.1};
 * 
 * // 基本統計量の計算
 * DescriptiveStatistics stats = StatisticsCalculator.calculateDescriptiveStatistics(salesData);
 * System.out.printf("平均売上: %.2f%n", stats.getMean());
 * System.out.printf("標準偏差: %.2f%n", stats.getStandardDeviation());
 * 
 * // 移動平均の計算
 * double[] movingAvg = TimeSeriesAnalyzer.calculateMovingAverage(salesData, 3);
 * 
 * // 線形回帰分析
 * double[] timePoints = {1, 2, 3, 4, 5};
 * LinearRegression regression = RegressionAnalyzer.performLinearRegression(timePoints, salesData);
 * System.out.printf("売上トレンド: %.2f per period%n", regression.getSlope());
 * }</pre>
 * 
 * <h2>パフォーマンス特性</h2>
 * <table border="1">
 * <caption>主要操作の計算量</caption>
 * <tr><th>操作</th><th>時間計算量</th><th>空間計算量</th></tr>
 * <tr><td>記述統計</td><td>O(n)</td><td>O(1)</td></tr>
 * <tr><td>線形回帰</td><td>O(n)</td><td>O(1)</td></tr>
 * <tr><td>移動平均</td><td>O(n)</td><td>O(n)</td></tr>
 * <tr><td>ソート必要操作</td><td>O(n log n)</td><td>O(n)</td></tr>
 * </table>
 * 
 * <h2>スレッドセーフティ</h2>
 * <p>このライブラリの全てのクラスは<strong>ステートレス</strong>であり、
 * マルチスレッド環境での安全な利用が可能です。並行処理での使用時は
 * 入力データの変更に注意してください。</p>
 * 
 * @author データサイエンスチーム
 * @version 3.2.1
 * @since 1.0
 * @see <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html">Math クラス</a>
 * @see <a href="https://commons.apache.org/proper/commons-math/">Apache Commons Math</a>
 */
public final class AdvancedAPIDocumentation {
    
    /**
     * ライブラリバージョン情報
     * 
     * <p>このバージョン情報は、APIの互換性確認とデバッグに使用されます。
     * セマンティックバージョニング（major.minor.patch）に従います。</p>
     * 
     * @since 1.0
     */
    public static final String VERSION = "3.2.1";
    
    /**
     * デフォルトの有効桁数
     * 
     * <p>計算結果の丸め処理で使用されるデフォルトの有効桁数です。
     * 金融計算では通常この精度で十分ですが、科学計算では
     * {@link #HIGH_PRECISION_DIGITS}の使用を検討してください。</p>
     * 
     * @since 2.0
     */
    public static final int DEFAULT_PRECISION_DIGITS = 10;
    
    /**
     * 高精度計算用の有効桁数
     * 
     * <p>科学計算や精密な統計分析で使用される高精度設定です。
     * パフォーマンスとのトレードオフに注意してください。</p>
     * 
     * @since 2.1
     */
    public static final int HIGH_PRECISION_DIGITS = 15;
    
    /**
     * ユーティリティクラスのため、インスタンス化を禁止します。
     * 
     * @throws UnsupportedOperationException インスタンス化が試行された場合
     * @since 1.0
     */
    private AdvancedAPIDocumentation() {
        throw new UnsupportedOperationException(
            "AdvancedAPIDocumentationはユーティリティクラスのため、インスタンス化できません。");
    }
    
    /**
     * 記述統計量を格納するイミュータブルクラス
     * 
     * <p>データセットの基本的な統計量を保持します。すべてのフィールドは
     * finalであり、オブジェクト作成後の変更はできません。</p>
     * 
     * <h3>使用例</h3>
     * <pre>{@code
     * double[] data = {1.0, 2.0, 3.0, 4.0, 5.0};
     * DescriptiveStatistics stats = StatisticsCalculator.calculateDescriptiveStatistics(data);
     * 
     * // 基本統計量の取得
     * double mean = stats.getMean();        // 平均値
     * double variance = stats.getVariance(); // 分散
     * double skewness = stats.getSkewness(); // 歪度
     * }</pre>
     * 
     * @author 統計分析チーム
     * @since 2.0
     * @see StatisticsCalculator#calculateDescriptiveStatistics(double[])
     */
    public static final class DescriptiveStatistics {
        
        /** サンプル数 */
        private final int count;
        
        /** 平均値 */
        private final double mean;
        
        /** 分散（標本分散） */
        private final double variance;
        
        /** 標準偏差 */
        private final double standardDeviation;
        
        /** 最小値 */
        private final double minimum;
        
        /** 最大値 */
        private final double maximum;
        
        /** 歪度（わいど）- 分布の非対称性を表す */
        private final double skewness;
        
        /** 尖度（せんど）- 分布の尖り具合を表す */
        private final double kurtosis;
        
        /**
         * 記述統計量オブジェクトを構築します。
         * 
         * <p>このコンストラクタは{@link StatisticsCalculator}からのみ
         * 呼び出されることを想定しています。直接呼び出しは推奨されません。</p>
         * 
         * @param count サンプル数（1以上）
         * @param mean 平均値
         * @param variance 分散（0以上）
         * @param standardDeviation 標準偏差（0以上）
         * @param minimum 最小値
         * @param maximum 最大値
         * @param skewness 歪度
         * @param kurtosis 尖度
         * 
         * @throws IllegalArgumentException パラメータが無効な場合
         * @since 2.0
         */
        DescriptiveStatistics(int count, double mean, double variance, double standardDeviation,
                            double minimum, double maximum, double skewness, double kurtosis) {
            if (count <= 0) {
                throw new IllegalArgumentException("サンプル数は1以上である必要があります: " + count);
            }
            if (variance < 0) {
                throw new IllegalArgumentException("分散は0以上である必要があります: " + variance);
            }
            if (standardDeviation < 0) {
                throw new IllegalArgumentException("標準偏差は0以上である必要があります: " + standardDeviation);
            }
            if (maximum < minimum) {
                throw new IllegalArgumentException("最大値が最小値より小さいです: max=" + maximum + ", min=" + minimum);
            }
            
            this.count = count;
            this.mean = mean;
            this.variance = variance;
            this.standardDeviation = standardDeviation;
            this.minimum = minimum;
            this.maximum = maximum;
            this.skewness = skewness;
            this.kurtosis = kurtosis;
        }
        
        /**
         * サンプル数を取得します。
         * 
         * @return サンプル数（1以上）
         * @since 2.0
         */
        public int getCount() { return count; }
        
        /**
         * 算術平均を取得します。
         * 
         * <p>算術平均は全てのデータ値の合計をサンプル数で割った値です。
         * 外れ値の影響を受けやすいため、データの分布を確認することを推奨します。</p>
         * 
         * @return 算術平均
         * @since 2.0
         * @see #getMedian() 中央値が外れ値に強い代替指標
         */
        public double getMean() { return mean; }
        
        /**
         * 標本分散を取得します。
         * 
         * <p>分散は各データ値と平均の差の2乗の平均です。
         * 単位は元データの2乗になることに注意してください。</p>
         * 
         * <p><strong>計算式:</strong></p>
         * <p>σ² = Σ(xᵢ - μ)² / n</p>
         * 
         * @return 標本分散（0以上）
         * @since 2.0
         * @see #getStandardDeviation() 単位を元データと合わせた値
         */
        public double getVariance() { return variance; }
        
        /**
         * 標準偏差を取得します。
         * 
         * <p>標準偏差は分散の平方根で、データのばらつきを表します。
         * 元データと同じ単位で表現されるため、解釈しやすい指標です。</p>
         * 
         * <p><strong>正規分布の場合:</strong></p>
         * <ul>
         *   <li>約68%のデータが平均±1σの範囲</li>
         *   <li>約95%のデータが平均±2σの範囲</li>
         *   <li>約99.7%のデータが平均±3σの範囲</li>
         * </ul>
         * 
         * @return 標準偏差（0以上）
         * @since 2.0
         * @see #getVariance()
         */
        public double getStandardDeviation() { return standardDeviation; }
        
        /**
         * 最小値を取得します。
         * 
         * @return データセットの最小値
         * @since 2.0
         * @see #getMaximum()
         * @see #getRange() 範囲（最大値-最小値）
         */
        public double getMinimum() { return minimum; }
        
        /**
         * 最大値を取得します。
         * 
         * @return データセットの最大値
         * @since 2.0
         * @see #getMinimum()
         * @see #getRange() 範囲（最大値-最小値）
         */
        public double getMaximum() { return maximum; }
        
        /**
         * 歪度（わいど）を取得します。
         * 
         * <p>歪度は分布の非対称性を表す指標です：</p>
         * <ul>
         *   <li><strong>0:</strong> 対称な分布（正規分布など）</li>
         *   <li><strong>正の値:</strong> 右側に裾が長い分布（右歪み）</li>
         *   <li><strong>負の値:</strong> 左側に裾が長い分布（左歪み）</li>
         * </ul>
         * 
         * <p><strong>解釈の目安:</strong></p>
         * <ul>
         *   <li>|歪度| < 0.5: ほぼ対称</li>
         *   <li>0.5 ≤ |歪度| < 1.0: 中程度の歪み</li>
         *   <li>|歪度| ≥ 1.0: 強い歪み</li>
         * </ul>
         * 
         * @return 歪度
         * @since 2.1
         * @see #getKurtosis() 尖度
         */
        public double getSkewness() { return skewness; }
        
        /**
         * 尖度（せんど）を取得します。
         * 
         * <p>尖度は分布の尖り具合を表す指標です。正規分布の尖度を3とする
         * 定義を使用しています（超過尖度ではありません）：</p>
         * <ul>
         *   <li><strong>3:</strong> 正規分布と同じ尖り</li>
         *   <li><strong>3より大:</strong> より尖った分布（レプトカーティック）</li>
         *   <li><strong>3より小:</strong> より平坦な分布（プラチカーティック）</li>
         * </ul>
         * 
         * @return 尖度
         * @since 2.1
         * @see #getSkewness() 歪度
         */
        public double getKurtosis() { return kurtosis; }
        
        /**
         * データの範囲（最大値 - 最小値）を取得します。
         * 
         * @return データの範囲（0以上）
         * @since 2.0
         */
        public double getRange() { return maximum - minimum; }
        
        /**
         * 変動係数（CV: Coefficient of Variation）を取得します。
         * 
         * <p>変動係数は標準偏差を平均で割った値で、相対的なばらつきを表します。
         * 異なる単位や規模のデータを比較する際に有用です。</p>
         * 
         * <p><strong>注意:</strong> 平均が0の場合は無限大を返します。</p>
         * 
         * @return 変動係数（標準偏差/平均）
         * @since 2.2
         */
        public double getCoefficientOfVariation() {
            return mean != 0 ? standardDeviation / Math.abs(mean) : Double.POSITIVE_INFINITY;
        }
        
        /**
         * 中央値（メディアン）を計算するためのヘルパーメソッド。
         * 
         * <p><strong>注意:</strong> この実装では元のデータが必要ですが、
         * 現在の設計では統計量のみを保持しています。将来のバージョンで
         * パーセンタイル計算機能の追加を検討中です。</p>
         * 
         * @return 現在は平均値を返す（暫定実装）
         * @since 2.0
         * @deprecated 将来のバージョンで適切な実装に置き換え予定
         */
        @Deprecated(since = "2.2", forRemoval = true)
        public double getMedian() {
            // 暫定実装: 正確な中央値計算には元データが必要
            return mean;
        }
        
        /**
         * 統計量のサマリーを文字列として取得します。
         * 
         * <p>デバッグやログ出力に適した形式で統計量を返します。</p>
         * 
         * @return 統計量のサマリー文字列
         * @since 2.0
         */
        @Override
        public String toString() {
            return String.format(
                "DescriptiveStatistics{n=%d, mean=%.3f, std=%.3f, min=%.3f, max=%.3f, " +
                "skewness=%.3f, kurtosis=%.3f}",
                count, mean, standardDeviation, minimum, maximum, skewness, kurtosis);
        }
        
        /**
         * 詳細な統計レポートを生成します。
         * 
         * <p>ビジネスレポートや学術発表に適した詳細な形式で統計量を返します。</p>
         * 
         * @return 詳細な統計レポート
         * @since 2.1
         */
        public String toDetailedReport() {
            StringBuilder report = new StringBuilder();
            report.append("=== 記述統計量レポート ===\n");
            report.append(String.format("サンプル数: %d\n", count));
            report.append(String.format("平均値: %.6f\n", mean));
            report.append(String.format("標準偏差: %.6f\n", standardDeviation));
            report.append(String.format("分散: %.6f\n", variance));
            report.append(String.format("最小値: %.6f\n", minimum));
            report.append(String.format("最大値: %.6f\n", maximum));
            report.append(String.format("範囲: %.6f\n", getRange()));
            report.append(String.format("変動係数: %.6f\n", getCoefficientOfVariation()));
            report.append(String.format("歪度: %.6f\n", skewness));
            report.append(String.format("尖度: %.6f\n", kurtosis));
            
            // 分布の特徴を解釈
            report.append("\n=== 分布の特徴 ===\n");
            if (Math.abs(skewness) < 0.5) {
                report.append("分布: ほぼ対称\n");
            } else if (skewness > 0) {
                report.append("分布: 右歪み（右側に裾が長い）\n");
            } else {
                report.append("分布: 左歪み（左側に裾が長い）\n");
            }
            
            if (Math.abs(kurtosis - 3) < 0.5) {
                report.append("尖度: 正規分布に近い\n");
            } else if (kurtosis > 3) {
                report.append("尖度: 正規分布より尖っている\n");
            } else {
                report.append("尖度: 正規分布より平坦\n");
            }
            
            return report.toString();
        }
    }
    
    /**
     * 統計計算を行うメインクラス
     * 
     * <p>このクラスは様々な統計計算機能を提供します。すべてのメソッドは
     * スタティックで、スレッドセーフです。</p>
     * 
     * @since 1.0
     */
    public static final class StatisticsCalculator {
        
        /**
         * ユーティリティクラスのため、インスタンス化を禁止します。
         */
        private StatisticsCalculator() {
            throw new UnsupportedOperationException();
        }
        
        /**
         * 配列の記述統計量を計算します。
         * 
         * <p>入力データから基本的な統計量（平均、分散、標準偏差、最小値、最大値、
         * 歪度、尖度）を計算し、{@link DescriptiveStatistics}オブジェクトとして返します。</p>
         * 
         * <p><strong>計算アルゴリズム:</strong></p>
         * <ol>
         *   <li>1パス目でサンプル数、合計、最小値、最大値を計算</li>
         *   <li>平均値を計算</li>
         *   <li>2パス目で分散、歪度、尖度を計算</li>
         * </ol>
         * 
         * <p><strong>注意事項:</strong></p>
         * <ul>
         *   <li>入力配列は変更されません</li>
         *   <li>NaNや無限大が含まれる場合は例外が発生します</li>
         *   <li>大量データの場合はメモリ使用量に注意してください</li>
         * </ul>
         * 
         * @param data 統計量を計算するデータ配列（null不可、要素数1以上）
         * @return 計算された記述統計量
         * 
         * @throws IllegalArgumentException 以下の場合に発生：
         *         <ul>
         *           <li>dataがnull</li>
         *           <li>dataが空配列</li>
         *           <li>dataにNaNまたは無限大が含まれる</li>
         *         </ul>
         * 
         * @since 2.0
         * @see DescriptiveStatistics
         * 
         * @example
         * <pre>{@code
         * // 売上データの分析
         * double[] monthlySales = {150.5, 160.2, 145.8, 170.1, 155.9};
         * DescriptiveStatistics stats = StatisticsCalculator.calculateDescriptiveStatistics(monthlySales);
         * 
         * System.out.printf("月平均売上: %.2f万円%n", stats.getMean());
         * System.out.printf("売上の標準偏差: %.2f万円%n", stats.getStandardDeviation());
         * 
         * if (stats.getSkewness() > 0.5) {
         *     System.out.println("売上分布は右に偏っています（高額月が少数）");
         * }
         * }</pre>
         */
        public static DescriptiveStatistics calculateDescriptiveStatistics(double[] data) {
            // 入力検証
            validateInputData(data);
            
            int n = data.length;
            
            // 1パス目: 基本統計量の計算
            double sum = 0.0;
            double min = data[0];
            double max = data[0];
            
            for (double value : data) {
                sum += value;
                if (value < min) min = value;
                if (value > max) max = value;
            }
            
            double mean = sum / n;
            
            // 2パス目: 分散、歪度、尖度の計算
            double sumSquaredDev = 0.0;
            double sumCubedDev = 0.0;
            double sumFourthDev = 0.0;
            
            for (double value : data) {
                double deviation = value - mean;
                double squaredDev = deviation * deviation;
                sumSquaredDev += squaredDev;
                sumCubedDev += squaredDev * deviation;
                sumFourthDev += squaredDev * squaredDev;
            }
            
            double variance = sumSquaredDev / n;
            double standardDeviation = Math.sqrt(variance);
            
            // 歪度と尖度の計算
            double skewness = 0.0;
            double kurtosis = 0.0;
            
            if (standardDeviation > 0) {
                double variance32 = Math.pow(variance, 1.5);
                double variance2 = variance * variance;
                
                skewness = (sumCubedDev / n) / variance32;
                kurtosis = (sumFourthDev / n) / variance2;
            }
            
            return new DescriptiveStatistics(n, mean, variance, standardDeviation, 
                                           min, max, skewness, kurtosis);
        }
        
        /**
         * 入力データの妥当性を検証します。
         * 
         * @param data 検証するデータ配列
         * @throws IllegalArgumentException データが無効な場合
         */
        private static void validateInputData(double[] data) {
            if (data == null) {
                throw new IllegalArgumentException("入力データはnullにできません");
            }
            if (data.length == 0) {
                throw new IllegalArgumentException("入力データは空にできません");
            }
            
            for (int i = 0; i < data.length; i++) {
                if (Double.isNaN(data[i])) {
                    throw new IllegalArgumentException(
                        String.format("NaNが含まれています（インデックス: %d）", i));
                }
                if (Double.isInfinite(data[i])) {
                    throw new IllegalArgumentException(
                        String.format("無限大が含まれています（インデックス: %d）", i));
                }
            }
        }
        
        /**
         * ピアソンの積率相関係数を計算します。
         * 
         * <p>2つのデータセット間の線形関係の強さを-1から1の範囲で表します：</p>
         * <ul>
         *   <li><strong>1:</strong> 完全な正の相関</li>
         *   <li><strong>0:</strong> 相関なし</li>
         *   <li><strong>-1:</strong> 完全な負の相関</li>
         * </ul>
         * 
         * <p><strong>解釈の目安:</strong></p>
         * <ul>
         *   <li>|r| ≥ 0.7: 強い相関</li>
         *   <li>0.3 ≤ |r| < 0.7: 中程度の相関</li>
         *   <li>|r| < 0.3: 弱い相関</li>
         * </ul>
         * 
         * @param x 1つ目のデータセット
         * @param y 2つ目のデータセット（xと同じ長さ）
         * @return ピアソンの積率相関係数（-1から1）
         * 
         * @throws IllegalArgumentException 入力データが無効な場合
         * @throws ArithmeticException いずれかのデータセットの分散が0の場合
         * 
         * @since 2.0
         * 
         * @example
         * <pre>{@code
         * // 広告費と売上の相関分析
         * double[] adSpend = {10, 20, 30, 40, 50};
         * double[] sales = {100, 180, 290, 380, 500};
         * 
         * double correlation = StatisticsCalculator.calculateCorrelation(adSpend, sales);
         * System.out.printf("広告費と売上の相関係数: %.3f%n", correlation);
         * 
         * if (correlation > 0.7) {
         *     System.out.println("広告費と売上には強い正の相関があります");
         * }
         * }</pre>
         */
        public static double calculateCorrelation(double[] x, double[] y) {
            validateInputData(x);
            validateInputData(y);
            
            if (x.length != y.length) {
                throw new IllegalArgumentException(
                    String.format("配列の長さが異なります: x=%d, y=%d", x.length, y.length));
            }
            
            int n = x.length;
            if (n < 2) {
                throw new IllegalArgumentException("相関係数の計算には最低2つのデータポイントが必要です");
            }
            
            // 平均の計算
            double meanX = java.util.Arrays.stream(x).average().orElse(0.0);
            double meanY = java.util.Arrays.stream(y).average().orElse(0.0);
            
            // 分子と分母の計算
            double numerator = 0.0;
            double sumSquaredDeviationX = 0.0;
            double sumSquaredDeviationY = 0.0;
            
            for (int i = 0; i < n; i++) {
                double deviationX = x[i] - meanX;
                double deviationY = y[i] - meanY;
                
                numerator += deviationX * deviationY;
                sumSquaredDeviationX += deviationX * deviationX;
                sumSquaredDeviationY += deviationY * deviationY;
            }
            
            double denominator = Math.sqrt(sumSquaredDeviationX * sumSquaredDeviationY);
            
            if (denominator == 0.0) {
                throw new ArithmeticException("分散が0のため相関係数を計算できません");
            }
            
            return numerator / denominator;
        }
    }
    
    /**
     * アプリケーションのエントリーポイント
     * 
     * <p>このメソッドは統計計算ライブラリの基本的な使用例を示します。
     * 実際のアプリケーションでの活用方法の参考としてください。</p>
     * 
     * @param args コマンドライン引数（未使用）
     * 
     * @since 1.0
     */
    public static void main(String[] args) {
        System.out.println("=== 高度統計計算ライブラリ v" + VERSION + " デモ ===");
        System.out.println();
        
        // サンプルデータ: 月別売上データ（万円）
        double[] monthlySales = {
            120.5, 135.2, 108.7, 145.9, 132.1, 
            156.8, 142.3, 159.6, 138.4, 147.2
        };
        
        System.out.println("【分析対象】月別売上データ:");
        for (int i = 0; i < monthlySales.length; i++) {
            System.out.printf("%d月: %.1f万円%n", i + 1, monthlySales[i]);
        }
        System.out.println();
        
        // 記述統計量の計算
        DescriptiveStatistics stats = StatisticsCalculator.calculateDescriptiveStatistics(monthlySales);
        
        System.out.println("【記述統計量】");
        System.out.println(stats.toDetailedReport());
        
        // 相関分析の例
        double[] monthNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        try {
            double correlation = StatisticsCalculator.calculateCorrelation(monthNumbers, monthlySales);
            System.out.println("【トレンド分析】");
            System.out.printf("月と売上の相関係数: %.3f%n", correlation);
            
            if (Math.abs(correlation) > 0.3) {
                if (correlation > 0) {
                    System.out.println("→ 売上に上昇トレンドが見られます");
                } else {
                    System.out.println("→ 売上に下降トレンドが見られます");
                }
            } else {
                System.out.println("→ 明確なトレンドは見られません");
            }
        } catch (Exception e) {
            System.err.println("相関分析でエラーが発生しました: " + e.getMessage());
        }
        
        System.out.println();
        System.out.println("=== デモ完了 ===");
    }
}

/*
 * 高度なAPIドキュメンテーションの実装ポイント:
 * 
 * 1. 包括的なクラスレベルドキュメント
 *    - 目的と概要の詳細な説明
 *    - 使用例のコード付きデモンストレーション
 *    - HTMLタグを使った構造化された情報
 *    - パフォーマンス特性の明示
 *    - スレッドセーフティの説明
 * 
 * 2. メソッドレベルの詳細ドキュメント
 *    - 機能の詳細な説明と使用場面
 *    - 数学的公式やアルゴリズムの説明
 *    - パラメータの詳細な制約条件
 *    - 戻り値の解釈方法
 *    - 例外条件の網羅的な説明
 *    - 実用的なコード例（@example）
 * 
 * 3. 高度なJavadocタグの活用
 *    - @param, @return, @throws の詳細記述
 *    - @since によるバージョン管理
 *    - @see による関連要素への参照
 *    - @deprecated による非推奨API の管理
 *    - @author による責任の明確化
 *    - カスタムタグ（@example）の使用
 * 
 * 4. HTMLマークアップの効果的利用
 *    - <h2>, <h3> による階層構造
 *    - <ul>, <ol>, <li> によるリスト
 *    - <table> による表形式データ
 *    - <strong>, <em> による強調
 *    - <pre>, <code> によるコード表示
 * 
 * 5. 実務的な配慮
 *    - ビジネス文脈での使用例
 *    - 解釈の指針や注意点
 *    - パフォーマンスへの影響
 *    - 代替手法の提案
 *    - 将来の機能拡張への言及
 * 
 * 6. ドキュメント品質向上のポイント
 *    - 読み手のレベルに応じた説明
 *    - 数学的背景の適切な説明
 *    - 実際の使用場面を想定した例
 *    - エラーケースの具体的な説明
 *    - 関連する理論や手法への参照
 * 
 * このドキュメンテーション手法により、以下の効果が期待できます:
 * - APIの理解しやすさの向上
 * - 正しい使用方法の促進
 * - 保守性の向上
 * - チーム開発での知識共有
 * - 外部利用者への適切な情報提供
 */