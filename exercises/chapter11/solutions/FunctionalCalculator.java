package exercises.chapter11.solutions;

import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * 関数型プログラミングの概念を活用した計算機クラス
 * 
 * 【基本実装】
 * - 基本的な演算（加算、減算、乗算、除算）をラムダ式で実装
 * - 演算結果をチェーンして連続計算を可能にする
 * 
 * 【応用実装】
 * - 関数合成（Function.compose, Function.andThen）
 * - 条件付き演算（Predicate による条件判定）
 * - カスタム演算子の動的登録
 * 
 * 【発展実装】
 * - 演算履歴の管理と再現機能
 * - 複合演算のパイプライン処理
 * - 並列計算サポート
 * 
 * 【よくある間違いと対策】
 * 1. ラムダ式の可読性：複雑な処理は適切なメソッド名を付けて分離
 * 2. 例外処理：ゼロ除算などのエラーケースを適切に処理
 * 3. 状態管理：関数型では状態を持たない設計を心がける
 * 4. パフォーマンス：不必要な中間演算を避ける
 */
public class FunctionalCalculator {
    
    // 基本的な演算子を定義
    private final Map<String, BinaryOperator<Double>> operators = new HashMap<>();
    
    // 演算履歴を保持（発展実装）
    private final List<Operation> history = new ArrayList<>();
    
    /**
     * 演算の履歴を表すレコード
     */
    public record Operation(String operator, double operand1, double operand2, double result) {}
    
    /**
     * コンストラクタ：基本演算子を登録
     */
    public FunctionalCalculator() {
        // 基本演算子の登録
        operators.put("+", (a, b) -> a + b);
        operators.put("-", (a, b) -> a - b);
        operators.put("*", (a, b) -> a * b);
        operators.put("/", (a, b) -> {
            if (b == 0) {
                throw new ArithmeticException("Division by zero");
            }
            return a / b;
        });
        
        // 高度な演算子の登録
        operators.put("pow", Math::pow);
        operators.put("max", Math::max);
        operators.put("min", Math::min);
    }
    
    /**
     * 【基本実装】
     * 二項演算を実行
     * 
     * @param operand1 第一オペランド
     * @param operator 演算子
     * @param operand2 第二オペランド
     * @return 演算結果
     */
    public double calculate(double operand1, String operator, double operand2) {
        BinaryOperator<Double> op = operators.get(operator);
        if (op == null) {
            throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
        
        double result = op.apply(operand1, operand2);
        
        // 履歴に記録
        history.add(new Operation(operator, operand1, operand2, result));
        
        return result;
    }
    
    /**
     * 【応用実装】
     * 関数合成による複合演算
     * 
     * @param value 初期値
     * @param operations 演算の配列
     * @return 最終結果
     */
    public double compose(double value, UnaryOperator<Double>... operations) {
        UnaryOperator<Double> composed = UnaryOperator.identity();
        
        // 関数合成：右から左への合成
        for (int i = operations.length - 1; i >= 0; i--) {
            composed = composed.compose(operations[i]);
        }
        
        return composed.apply(value);
    }
    
    /**
     * 【応用実装】
     * 条件付き演算：条件を満たした場合のみ演算を実行
     * 
     * @param value 値
     * @param condition 条件
     * @param operation 演算
     * @return 演算結果（条件を満たさない場合は元の値）
     */
    public double conditionalCalculate(double value, Predicate<Double> condition, 
                                     UnaryOperator<Double> operation) {
        return condition.test(value) ? operation.apply(value) : value;
    }
    
    /**
     * 【発展実装】
     * パイプライン処理による連続演算
     * 
     * @param initialValue 初期値
     * @return パイプラインビルダー
     */
    public Pipeline pipeline(double initialValue) {
        return new Pipeline(initialValue);
    }
    
    /**
     * パイプライン処理のためのビルダークラス
     */
    public class Pipeline {
        private double value;
        
        public Pipeline(double value) {
            this.value = value;
        }
        
        public Pipeline add(double operand) {
            this.value = calculate(this.value, "+", operand);
            return this;
        }
        
        public Pipeline subtract(double operand) {
            this.value = calculate(this.value, "-", operand);
            return this;
        }
        
        public Pipeline multiply(double operand) {
            this.value = calculate(this.value, "*", operand);
            return this;
        }
        
        public Pipeline divide(double operand) {
            this.value = calculate(this.value, "/", operand);
            return this;
        }
        
        public Pipeline applyIf(Predicate<Double> condition, UnaryOperator<Double> operation) {
            if (condition.test(this.value)) {
                this.value = operation.apply(this.value);
            }
            return this;
        }
        
        public Pipeline apply(UnaryOperator<Double> operation) {
            this.value = operation.apply(this.value);
            return this;
        }
        
        public double result() {
            return this.value;
        }
    }
    
    /**
     * 【発展実装】
     * カスタム演算子の登録
     * 
     * @param name 演算子名
     * @param operator 演算子の実装
     */
    public void registerOperator(String name, BinaryOperator<Double> operator) {
        operators.put(name, operator);
    }
    
    /**
     * 【発展実装】
     * 数値リストに対する集約演算
     * 
     * @param numbers 数値リスト
     * @param accumulator 集約関数
     * @return 集約結果
     */
    public double aggregate(List<Double> numbers, BinaryOperator<Double> accumulator) {
        return numbers.stream()
                     .reduce(accumulator)
                     .orElse(0.0);
    }
    
    /**
     * 【発展実装】
     * 並列集約演算
     * 
     * @param numbers 数値リスト
     * @param accumulator 集約関数
     * @return 集約結果
     */
    public double parallelAggregate(List<Double> numbers, BinaryOperator<Double> accumulator) {
        return numbers.parallelStream()
                     .reduce(accumulator)
                     .orElse(0.0);
    }
    
    /**
     * 演算履歴を取得
     * 
     * @return 演算履歴のリスト
     */
    public List<Operation> getHistory() {
        return new ArrayList<>(history);
    }
    
    /**
     * 演算履歴をクリア
     */
    public void clearHistory() {
        history.clear();
    }
    
    /**
     * よく使用される演算の組み合わせを定義
     */
    public static class CommonOperations {
        
        // 平方根
        public static final UnaryOperator<Double> SQRT = Math::sqrt;
        
        // 平方
        public static final UnaryOperator<Double> SQUARE = x -> x * x;
        
        // 絶対値
        public static final UnaryOperator<Double> ABS = Math::abs;
        
        // 逆数
        public static final UnaryOperator<Double> RECIPROCAL = x -> {
            if (x == 0) {
                throw new ArithmeticException("Cannot calculate reciprocal of zero");
            }
            return 1.0 / x;
        };
        
        // 正の値かどうか
        public static final Predicate<Double> IS_POSITIVE = x -> x > 0;
        
        // 偶数かどうか（整数として）
        public static final Predicate<Double> IS_EVEN = x -> x % 2 == 0;
        
        // 範囲内かどうか
        public static Predicate<Double> inRange(double min, double max) {
            return x -> x >= min && x <= max;
        }
    }
}