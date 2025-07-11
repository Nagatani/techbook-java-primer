/**
 * リスト2-30
 * Calculatorクラス
 * 
 * 元ファイル: chapter02-getting-started.md (1040行目)
 */

public class Calculator {
    // 内部的な計算方法は隠蔽
    private double internalCalculation(double a, double b) {
        // 複雑な計算ロジック
        return a * b * 0.95;
    }
    
    // 公開インターフェース
    public double calculate(double a, double b) {
        return internalCalculation(a, b);
    }
}