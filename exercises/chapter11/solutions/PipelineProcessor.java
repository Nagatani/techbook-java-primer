package chapter11.solutions;

import java.util.function.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

/**
 * パイプライン処理を実装するクラス
 * 
 * 学習内容：
 * - 関数合成によるパイプライン構築
 * - 段階的なデータ変換
 * - エラーハンドリングを含むパイプライン
 * - 並行処理との組み合わせ
 */
public class PipelineProcessor {
    
    /**
     * 処理ステップを表すインターフェイス
     */
    @FunctionalInterface
    public interface ProcessingStep<T, R> extends Function<T, R> {
        
        /**
         * 名前付きのステップを作成
         */
        static <T, R> ProcessingStep<T, R> named(String name, Function<T, R> function) {
            return new NamedStep<>(name, function);
        }
        
        /**
         * エラーハンドリング付きのステップを作成
         */
        static <T, R> ProcessingStep<T, R> safe(Function<T, R> function, Function<Exception, R> errorHandler) {
            return input -> {
                try {
                    return function.apply(input);
                } catch (Exception e) {
                    return errorHandler.apply(e);
                }
            };
        }
    }
    
    /**
     * 名前付きステップの実装
     */
    private static class NamedStep<T, R> implements ProcessingStep<T, R> {
        private final String name;
        private final Function<T, R> function;
        
        public NamedStep(String name, Function<T, R> function) {
            this.name = name;
            this.function = function;
        }
        
        @Override
        public R apply(T input) {
            return function.apply(input);
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public String toString() {
            return "Step{" + name + "}";
        }
    }
    
    /**
     * パイプライン処理の結果を表すクラス
     */
    public static class ProcessingResult<T> {
        private final T result;
        private final List<String> executedSteps;
        private final boolean success;
        private final String errorMessage;
        
        private ProcessingResult(T result, List<String> executedSteps, boolean success, String errorMessage) {
            this.result = result;
            this.executedSteps = new ArrayList<>(executedSteps);
            this.success = success;
            this.errorMessage = errorMessage;
        }
        
        public static <T> ProcessingResult<T> success(T result, List<String> executedSteps) {
            return new ProcessingResult<>(result, executedSteps, true, null);
        }
        
        public static <T> ProcessingResult<T> failure(String errorMessage, List<String> executedSteps) {
            return new ProcessingResult<>(null, executedSteps, false, errorMessage);
        }
        
        public T getResult() { return result; }
        public List<String> getExecutedSteps() { return new ArrayList<>(executedSteps); }
        public boolean isSuccess() { return success; }
        public String getErrorMessage() { return errorMessage; }
        
        @Override
        public String toString() {
            return success ? 
                "ProcessingResult{success, result=" + result + ", steps=" + executedSteps + "}" :
                "ProcessingResult{failure, error='" + errorMessage + "', steps=" + executedSteps + "}";
        }
    }
    
    /**
     * 基本的なパイプラインビルダー
     */
    public static class Pipeline<T> {
        private final List<Function<Object, Object>> steps = new ArrayList<>();
        private final List<String> stepNames = new ArrayList<>();
        
        public static <T> Pipeline<T> start() {
            return new Pipeline<>();
        }
        
        public <R> Pipeline<R> then(Function<T, R> step) {
            return then("unnamed", step);
        }
        
        @SuppressWarnings("unchecked")
        public <R> Pipeline<R> then(String name, Function<T, R> step) {
            Pipeline<R> newPipeline = new Pipeline<>();
            newPipeline.steps.addAll(this.steps);
            newPipeline.stepNames.addAll(this.stepNames);
            newPipeline.steps.add(input -> step.apply((T) input));
            newPipeline.stepNames.add(name);
            return newPipeline;
        }
        
        @SuppressWarnings("unchecked")
        public ProcessingResult<T> process(Object input) {
            Object current = input;
            List<String> executed = new ArrayList<>();
            
            try {
                for (int i = 0; i < steps.size(); i++) {
                    current = steps.get(i).apply(current);
                    executed.add(stepNames.get(i));
                }
                return ProcessingResult.success((T) current, executed);
            } catch (Exception e) {
                return ProcessingResult.failure(e.getMessage(), executed);
            }
        }
    }
    
    /**
     * 文字列処理のパイプライン例
     */
    public static class StringProcessor {
        
        /**
         * 文字列を大文字に変換
         */
        public static Function<String, String> toUpperCase() {
            return String::toUpperCase;
        }
        
        /**
         * 空白を削除
         */
        public static Function<String, String> trim() {
            return String::trim;
        }
        
        /**
         * プレフィックスを追加
         */
        public static Function<String, String> addPrefix(String prefix) {
            return s -> prefix + s;
        }
        
        /**
         * サフィックスを追加
         */
        public static Function<String, String> addSuffix(String suffix) {
            return s -> s + suffix;
        }
        
        /**
         * 文字列を置換
         */
        public static Function<String, String> replace(String target, String replacement) {
            return s -> s.replace(target, replacement);
        }
        
        /**
         * 文字列の長さを取得
         */
        public static Function<String, Integer> getLength() {
            return String::length;
        }
        
        /**
         * 文字列を反転
         */
        public static Function<String, String> reverse() {
            return s -> new StringBuilder(s).reverse().toString();
        }
        
        /**
         * 空文字チェック
         */
        public static Function<String, Boolean> isEmpty() {
            return String::isEmpty;
        }
        
        /**
         * 複合的な文字列処理パイプライン
         */
        public static ProcessingResult<String> processText(String input) {
            return Pipeline.<String>start()
                .then("trim", trim())
                .then("toUpperCase", toUpperCase())
                .then("addPrefix", addPrefix("PROCESSED_"))
                .then("addSuffix", addSuffix("_DONE"))
                .process(input);
        }
    }
    
    /**
     * 数値処理のパイプライン例
     */
    public static class NumberProcessor {
        
        /**
         * 数値を2倍にする
         */
        public static Function<Integer, Integer> doubleValue() {
            return x -> x * 2;
        }
        
        /**
         * 指定値を加算
         */
        public static Function<Integer, Integer> add(int value) {
            return x -> x + value;
        }
        
        /**
         * 指定値を乗算
         */
        public static Function<Integer, Integer> multiply(int value) {
            return x -> x * value;
        }
        
        /**
         * 平方根を計算
         */
        public static Function<Integer, Double> sqrt() {
            return x -> Math.sqrt(x);
        }
        
        /**
         * 絶対値を取得
         */
        public static Function<Integer, Integer> abs() {
            return Math::abs;
        }
        
        /**
         * 最大値でクリップ
         */
        public static Function<Integer, Integer> clamp(int max) {
            return x -> Math.min(x, max);
        }
        
        /**
         * 複合的な数値処理パイプライン
         */
        public static ProcessingResult<Integer> processNumber(int input) {
            return Pipeline.<Integer>start()
                .then("abs", abs())
                .then("double", doubleValue())
                .then("add10", add(10))
                .then("clamp100", clamp(100))
                .process(input);
        }
    }
    
    /**
     * リスト処理のパイプライン例
     */
    public static class ListProcessor {
        
        /**
         * フィルタリング
         */
        public static <T> Function<List<T>, List<T>> filter(Predicate<T> predicate) {
            return list -> list.stream().filter(predicate).collect(Collectors.toList());
        }
        
        /**
         * マッピング
         */
        public static <T, R> Function<List<T>, List<R>> map(Function<T, R> mapper) {
            return list -> list.stream().map(mapper).collect(Collectors.toList());
        }
        
        /**
         * ソート
         */
        public static <T extends Comparable<T>> Function<List<T>, List<T>> sort() {
            return list -> list.stream().sorted().collect(Collectors.toList());
        }
        
        /**
         * 重複除去
         */
        public static <T> Function<List<T>, List<T>> distinct() {
            return list -> list.stream().distinct().collect(Collectors.toList());
        }
        
        /**
         * 最初のN個を取得
         */
        public static <T> Function<List<T>, List<T>> take(int n) {
            return list -> list.stream().limit(n).collect(Collectors.toList());
        }
        
        /**
         * リストのサイズを取得
         */
        public static <T> Function<List<T>, Integer> size() {
            return List::size;
        }
        
        /**
         * 複合的なリスト処理パイプライン
         */
        public static ProcessingResult<List<String>> processStringList(List<String> input) {
            return Pipeline.<List<String>>start()
                .then("filter", filter(s -> !s.isEmpty()))
                .then("map", map(String::toUpperCase))
                .then("distinct", distinct())
                .then("sort", sort())
                .then("take5", take(5))
                .process(input);
        }
    }
    
    /**
     * 条件付きパイプライン
     */
    public static class ConditionalPipeline<T> {
        private final List<ConditionalStep<T>> steps = new ArrayList<>();
        
        public static <T> ConditionalPipeline<T> start() {
            return new ConditionalPipeline<>();
        }
        
        public ConditionalPipeline<T> when(Predicate<T> condition, Function<T, T> step) {
            return when("unnamed", condition, step);
        }
        
        public ConditionalPipeline<T> when(String name, Predicate<T> condition, Function<T, T> step) {
            steps.add(new ConditionalStep<>(name, condition, step));
            return this;
        }
        
        public ConditionalPipeline<T> always(Function<T, T> step) {
            return always("unnamed", step);
        }
        
        public ConditionalPipeline<T> always(String name, Function<T, T> step) {
            steps.add(new ConditionalStep<>(name, x -> true, step));
            return this;
        }
        
        public ProcessingResult<T> process(T input) {
            T current = input;
            List<String> executed = new ArrayList<>();
            
            try {
                for (ConditionalStep<T> step : steps) {
                    if (step.condition.test(current)) {
                        current = step.step.apply(current);
                        executed.add(step.name);
                    }
                }
                return ProcessingResult.success(current, executed);
            } catch (Exception e) {
                return ProcessingResult.failure(e.getMessage(), executed);
            }
        }
        
        private static class ConditionalStep<T> {
            final String name;
            final Predicate<T> condition;
            final Function<T, T> step;
            
            ConditionalStep(String name, Predicate<T> condition, Function<T, T> step) {
                this.name = name;
                this.condition = condition;
                this.step = step;
            }
        }
    }
    
    /**
     * 並行処理パイプライン
     */
    public static class ParallelPipeline<T> {
        private final List<Function<T, CompletableFuture<T>>> steps = new ArrayList<>();
        private final List<String> stepNames = new ArrayList<>();
        
        public static <T> ParallelPipeline<T> start() {
            return new ParallelPipeline<>();
        }
        
        public ParallelPipeline<T> thenAsync(Function<T, T> step) {
            return thenAsync("unnamed", step);
        }
        
        public ParallelPipeline<T> thenAsync(String name, Function<T, T> step) {
            steps.add(input -> CompletableFuture.supplyAsync(() -> step.apply(input)));
            stepNames.add(name);
            return this;
        }
        
        public CompletableFuture<ProcessingResult<T>> processAsync(T input) {
            CompletableFuture<T> current = CompletableFuture.completedFuture(input);
            List<String> executed = new ArrayList<>();
            
            try {
                for (int i = 0; i < steps.size(); i++) {
                    final int index = i;
                    current = current.thenCompose(value -> {
                        executed.add(stepNames.get(index));
                        return steps.get(index).apply(value);
                    });
                }
                
                return current.thenApply(result -> ProcessingResult.success(result, executed))
                    .exceptionally(throwable -> ProcessingResult.failure(throwable.getMessage(), executed));
            } catch (Exception e) {
                return CompletableFuture.completedFuture(ProcessingResult.failure(e.getMessage(), executed));
            }
        }
    }
    
    /**
     * データ変換パイプラインの例
     */
    public static class DataTransformationPipeline {
        
        /**
         * CSVライクなデータの処理
         */
        public static ProcessingResult<List<String>> processCsvData(String csvLine) {
            return Pipeline.<String>start()
                .then("trim", String::trim)
                .then("split", s -> List.of(s.split(",")))
                .then("trimFields", (List<String> fields) -> 
                    fields.stream().map(String::trim).collect(Collectors.toList()))
                .then("filterEmpty", (List<String> fields) -> 
                    fields.stream().filter(s -> !s.isEmpty()).collect(Collectors.toList()))
                .then("toUpperCase", (List<String> fields) -> 
                    fields.stream().map(String::toUpperCase).collect(Collectors.toList()))
                .process(csvLine);
        }
        
        /**
         * ユーザーデータの正規化
         */
        public static ProcessingResult<String> normalizeUserData(String userData) {
            return Pipeline.<String>start()
                .then("trim", String::trim)
                .then("toLowerCase", String::toLowerCase)
                .then("removeSpaces", s -> s.replaceAll("\\s+", " "))
                .then("capitalizeFirst", s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .process(userData);
        }
    }
}