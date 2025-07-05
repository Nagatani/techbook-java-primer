/**
 * 第20章 課題1: テスト駆動開発による計算機 - 解答例
 * 
 * TDD（Test Driven Development）で実装された計算機クラス
 * 
 * 学習ポイント:
 * - TDDサイクル（Red-Green-Refactor）
 * - 境界値分析によるテストケース設計
 * - エラー推測によるテスト設計
 * - テストファーストの開発手法
 */

import java.util.*;

/**
 * TDDで実装された計算機クラス
 * 
 * このクラスは以下の原則に従って実装されています：
 * 1. テストを先に書く（Red）
 * 2. テストを通すための最小実装（Green）
 * 3. リファクタリング（Refactor）
 */
public class TestDrivenCalculator {
    
    private double currentValue;
    private List<String> history;
    private boolean hasError;
    private static final double EPSILON = 1e-10;
    
    /**
     * コンストラクタ
     */
    public TestDrivenCalculator() {
        this.currentValue = 0.0;
        this.history = new ArrayList<>();
        this.hasError = false;
    }
    
    /**
     * 現在の値を取得
     * @return 現在の計算結果
     */
    public double getCurrentValue() {
        return currentValue;
    }
    
    /**
     * 値をセット
     * @param value セットする値
     */
    public void setValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            setError();
            return;
        }
        this.currentValue = value;
        this.hasError = false;
        addToHistory("設定: " + formatNumber(value));
    }
    
    /**
     * 加算
     * @param value 加算する値
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator add(double value) {
        if (hasError || Double.isNaN(value) || Double.isInfinite(value)) {
            setError();
            return this;
        }
        
        double oldValue = currentValue;
        currentValue += value;
        
        if (Double.isNaN(currentValue) || Double.isInfinite(currentValue)) {
            setError();
            return this;
        }
        
        addToHistory(formatNumber(oldValue) + " + " + formatNumber(value) + " = " + formatNumber(currentValue));
        return this;
    }
    
    /**
     * 減算
     * @param value 減算する値
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator subtract(double value) {
        if (hasError || Double.isNaN(value) || Double.isInfinite(value)) {
            setError();
            return this;
        }
        
        double oldValue = currentValue;
        currentValue -= value;
        
        if (Double.isNaN(currentValue) || Double.isInfinite(currentValue)) {
            setError();
            return this;
        }
        
        addToHistory(formatNumber(oldValue) + " - " + formatNumber(value) + " = " + formatNumber(currentValue));
        return this;
    }
    
    /**
     * 乗算
     * @param value 乗算する値
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator multiply(double value) {
        if (hasError || Double.isNaN(value) || Double.isInfinite(value)) {
            setError();
            return this;
        }
        
        double oldValue = currentValue;
        currentValue *= value;
        
        if (Double.isNaN(currentValue) || Double.isInfinite(currentValue)) {
            setError();
            return this;
        }
        
        addToHistory(formatNumber(oldValue) + " × " + formatNumber(value) + " = " + formatNumber(currentValue));
        return this;
    }
    
    /**
     * 除算
     * @param value 除算する値
     * @return このオブジェクト（メソッドチェーン用）
     * @throws ArithmeticException ゼロ除算の場合
     */
    public TestDrivenCalculator divide(double value) {
        if (hasError || Double.isNaN(value) || Double.isInfinite(value)) {
            setError();
            return this;
        }
        
        if (Math.abs(value) < EPSILON) {
            setError();
            addToHistory("エラー: ゼロ除算");
            throw new ArithmeticException("Division by zero");
        }
        
        double oldValue = currentValue;
        currentValue /= value;
        
        if (Double.isNaN(currentValue) || Double.isInfinite(currentValue)) {
            setError();
            return this;
        }
        
        addToHistory(formatNumber(oldValue) + " ÷ " + formatNumber(value) + " = " + formatNumber(currentValue));
        return this;
    }
    
    /**
     * クリア（初期状態に戻す）
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator clear() {
        this.currentValue = 0.0;
        this.hasError = false;
        this.history.clear();
        addToHistory("クリア");
        return this;
    }
    
    /**
     * 平方根を計算
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator sqrt() {
        if (hasError) {
            return this;
        }
        
        if (currentValue < 0) {
            setError();
            addToHistory("エラー: 負の数の平方根");
            throw new ArithmeticException("Square root of negative number");
        }
        
        double oldValue = currentValue;
        currentValue = Math.sqrt(currentValue);
        addToHistory("√" + formatNumber(oldValue) + " = " + formatNumber(currentValue));
        return this;
    }
    
    /**
     * 累乗を計算
     * @param exponent 指数
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator power(double exponent) {
        if (hasError || Double.isNaN(exponent) || Double.isInfinite(exponent)) {
            setError();
            return this;
        }
        
        double oldValue = currentValue;
        currentValue = Math.pow(currentValue, exponent);
        
        if (Double.isNaN(currentValue) || Double.isInfinite(currentValue)) {
            setError();
            return this;
        }
        
        addToHistory(formatNumber(oldValue) + "^" + formatNumber(exponent) + " = " + formatNumber(currentValue));
        return this;
    }
    
    /**
     * パーセンテージ計算
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator percent() {
        if (hasError) {
            return this;
        }
        
        double oldValue = currentValue;
        currentValue /= 100.0;
        addToHistory(formatNumber(oldValue) + "% = " + formatNumber(currentValue));
        return this;
    }
    
    /**
     * 計算履歴を取得
     * @return 計算履歴のリスト（コピー）
     */
    public List<String> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * 履歴をクリア
     */
    public void clearHistory() {
        history.clear();
    }
    
    /**
     * エラー状態かどうかを確認
     * @return エラー状態の場合true
     */
    public boolean hasError() {
        return hasError;
    }
    
    /**
     * 最後の履歴エントリを取得
     * @return 最後の履歴エントリ、履歴がない場合はnull
     */
    public String getLastHistoryEntry() {
        return history.isEmpty() ? null : history.get(history.size() - 1);
    }
    
    /**
     * 結果を文字列で取得
     * @return 現在の値の文字列表現
     */
    @Override
    public String toString() {
        if (hasError) {
            return "ERROR";
        }
        return formatNumber(currentValue);
    }
    
    /**
     * 等価性の判定（テスト用）
     * @param other 比較対象
     * @return 等しい場合true
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        
        TestDrivenCalculator that = (TestDrivenCalculator) other;
        return Math.abs(currentValue - that.currentValue) < EPSILON &&
               hasError == that.hasError;
    }
    
    /**
     * ハッシュコード
     * @return ハッシュコード値
     */
    @Override
    public int hashCode() {
        return Objects.hash(Math.round(currentValue * 1e10), hasError);
    }
    
    // Private helper methods
    
    /**
     * エラー状態を設定
     */
    private void setError() {
        this.hasError = true;
        this.currentValue = 0.0;
    }
    
    /**
     * 履歴に追加
     * @param entry 履歴エントリ
     */
    private void addToHistory(String entry) {
        history.add(entry);
        
        // 履歴が長くなりすぎた場合は古いものを削除
        if (history.size() > 100) {
            history.remove(0);
        }
    }
    
    /**
     * 数値を適切にフォーマット
     * @param number フォーマットする数値
     * @return フォーマットされた文字列
     */
    private String formatNumber(double number) {
        if (Math.abs(number) < EPSILON) {
            return "0";
        }
        
        // 整数かどうかチェック
        if (Math.abs(number - Math.round(number)) < EPSILON) {
            return String.valueOf(Math.round(number));
        }
        
        // 小数点以下の桁数を調整
        return String.format("%.10f", number).replaceAll("0*$", "").replaceAll("\\.$", "");
    }
    
    /**
     * 計算機の統計情報を取得
     * @return 統計情報のマップ
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("currentValue", currentValue);
        stats.put("hasError", hasError);
        stats.put("historySize", history.size());
        stats.put("isZero", Math.abs(currentValue) < EPSILON);
        stats.put("isPositive", currentValue > EPSILON);
        stats.put("isNegative", currentValue < -EPSILON);
        return stats;
    }
    
    /**
     * 計算機の状態を保存
     * @return 状態を表すマップ
     */
    public Map<String, Object> saveState() {
        Map<String, Object> state = new HashMap<>();
        state.put("value", currentValue);
        state.put("error", hasError);
        state.put("history", new ArrayList<>(history));
        return state;
    }
    
    /**
     * 計算機の状態を復元
     * @param state 保存された状態
     */
    @SuppressWarnings("unchecked")
    public void restoreState(Map<String, Object> state) {
        if (state.containsKey("value")) {
            this.currentValue = (Double) state.get("value");
        }
        if (state.containsKey("error")) {
            this.hasError = (Boolean) state.get("error");
        }
        if (state.containsKey("history")) {
            this.history = new ArrayList<>((List<String>) state.get("history"));
        }
    }
}

/*
 * TDD実装の特徴:
 * 
 * 1. テストファーストアプローチ
 *    - 各メソッドの実装前にテストケースを作成
 *    - 失敗するテストから開始（Red）
 *    - 最小限の実装でテストを通す（Green）
 *    - コードを改善（Refactor）
 * 
 * 2. 境界値分析
 *    - ゼロ、最大値、最小値のテスト
 *    - NaN、無限大の処理
 *    - 浮動小数点の精度問題への対応
 * 
 * 3. エラー推測
 *    - ゼロ除算のテスト
 *    - 負の数の平方根
 *    - オーバーフロー・アンダーフロー
 * 
 * 4. 堅牢な設計
 *    - 不正な入力に対する適切な処理
 *    - エラー状態の管理
 *    - リソースの適切な管理（履歴サイズ制限）
 * 
 * 5. テスタビリティ
 *    - 副作用の最小化
 *    - 状態の可視性
 *    - 決定論的な動作
 * 
 * TDDの利点:
 * - バグの早期発見
 * - 設計の改善
 * - リファクタリングの安全性
 * - ドキュメントとしてのテスト
 * - 開発者の自信向上
 */