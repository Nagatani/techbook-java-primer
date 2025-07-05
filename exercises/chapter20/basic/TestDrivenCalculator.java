/**
 * 第20章 課題1: テスト駆動開発による計算機
 * 
 * TDD（Test Driven Development）の実践として、まずテストを書いてから
 * 実装を行う計算機クラスを作成してください。
 * 
 * 要求仕様:
 * - 四則演算（加算、減算、乗算、除算）
 * - 累積計算（チェーン計算）
 * - クリア機能
 * - エラーハンドリング（ゼロ除算等）
 * - 計算履歴の保持
 * 
 * 学習ポイント:
 * - TDDサイクル（Red-Green-Refactor）
 * - 境界値分析によるテストケース設計
 * - エラー推測によるテスト設計
 * - モックオブジェクトの活用
 * - テストの可読性と保守性
 */

import java.util.*;

/**
 * 計算機クラス（実装対象）
 * 
 * TDDの実践として、まずはこのクラスのテストを先に書いてから実装してください。
 */
public class TestDrivenCalculator {
    
    // ここに実装してください
    // 
    // 実装前に、まずテストクラス（TestDrivenCalculatorTest.java）を作成し、
    // 以下のようなテストメソッドを先に書いてください：
    //
    // - testAddition()
    // - testSubtraction()
    // - testMultiplication()
    // - testDivision()
    // - testDivisionByZero()
    // - testChainCalculation()
    // - testClear()
    // - testCalculationHistory()
    
    // 必要なフィールドの例:
    // private double currentValue;
    // private List<String> history;
    // private boolean hasError;
    
    /**
     * 現在の値を取得
     * @return 現在の計算結果
     */
    public double getCurrentValue() {
        // TODO: テストを先に書いてから実装
        return 0.0;
    }
    
    /**
     * 値をセット
     * @param value セットする値
     */
    public void setValue(double value) {
        // TODO: テストを先に書いてから実装
    }
    
    /**
     * 加算
     * @param value 加算する値
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator add(double value) {
        // TODO: テストを先に書いてから実装
        return this;
    }
    
    /**
     * 減算
     * @param value 減算する値
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator subtract(double value) {
        // TODO: テストを先に書いてから実装
        return this;
    }
    
    /**
     * 乗算
     * @param value 乗算する値
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator multiply(double value) {
        // TODO: テストを先に書いてから実装
        return this;
    }
    
    /**
     * 除算
     * @param value 除算する値
     * @return このオブジェクト（メソッドチェーン用）
     * @throws ArithmeticException ゼロ除算の場合
     */
    public TestDrivenCalculator divide(double value) {
        // TODO: テストを先に書いてから実装
        return this;
    }
    
    /**
     * クリア（初期状態に戻す）
     * @return このオブジェクト（メソッドチェーン用）
     */
    public TestDrivenCalculator clear() {
        // TODO: テストを先に書いてから実装
        return this;
    }
    
    /**
     * 計算履歴を取得
     * @return 計算履歴のリスト
     */
    public List<String> getHistory() {
        // TODO: テストを先に書いてから実装
        return new ArrayList<>();
    }
    
    /**
     * エラー状態かどうかを確認
     * @return エラー状態の場合true
     */
    public boolean hasError() {
        // TODO: テストを先に書いてから実装
        return false;
    }
    
    /**
     * 結果を文字列で取得
     * @return 現在の値の文字列表現
     */
    @Override
    public String toString() {
        // TODO: テストを先に書いてから実装
        return "0.0";
    }
}

/*
 * TDD実践のガイドライン:
 * 
 * 1. Red フェーズ（失敗するテストを書く）
 *    - まず期待する動作を表すテストを書く
 *    - テストは失敗する（実装がまだないため）
 *    - テストが正しく失敗することを確認
 * 
 * 2. Green フェーズ（テストを通すための最小実装）
 *    - テストを通すための最小限のコードを書く
 *    - 美しいコードである必要はない
 *    - とにかくテストを通すことが目標
 * 
 * 3. Refactor フェーズ（リファクタリング）
 *    - テストを通したままコードを改善
 *    - 重複の除去、可読性の向上
 *    - 設計の改善
 * 
 * テスト設計のポイント:
 * 
 * 1. 境界値分析
 *    - 正常な値の境界をテスト
 *    - 0, 正の最大値、負の最小値など
 *    - Double.MAX_VALUE, Double.MIN_VALUE
 * 
 * 2. エラー推測
 *    - ゼロ除算
 *    - 無限大、NaN
 *    - オーバーフロー、アンダーフロー
 * 
 * 3. 同値分割
 *    - 同じ処理をする値の組をまとめてテスト
 *    - 正の数、負の数、ゼロ
 * 
 * テストメソッドの命名:
 * - test + 動作 + 条件 + 期待結果
 * - 例: testDivision_WithZero_ThrowsException
 * 
 * アサーションの選択:
 * - assertEquals(): 値の比較
 * - assertThrows(): 例外のテスト
 * - assertTrue()/assertFalse(): 真偽値
 * - assertNull()/assertNotNull(): null チェック
 */