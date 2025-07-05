package chapter11.solutions;

import java.util.function.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 関数型インターフェイスを活用した処理を行うクラス
 * 
 * 学習内容：
 * - Function<T, R>の使用方法
 * - 関数合成（compose, andThen）
 * - メソッド参照
 * - 型安全性の確保
 */
public class FunctionProcessor {
    
    /**
     * 数値に対する処理を行う関数
     */
    private final Function<Integer, Integer> doubleValue = x -> x * 2;
    private final Function<Integer, Integer> addTen = x -> x + 10;
    private final Function<Integer, Integer> square = x -> x * x;
    
    /**
     * 文字列に対する処理を行う関数
     */
    private final Function<String, String> toUpperCase = String::toUpperCase;
    private final Function<String, String> addPrefix = s -> "PREFIX_" + s;
    private final Function<String, Integer> getLength = String::length;
    
    /**
     * 単純な関数適用
     */
    public Integer applyDoubleValue(Integer input) {
        return doubleValue.apply(input);
    }
    
    /**
     * 関数合成（compose）- 右から左へ実行
     * まずaddTenを実行し、その結果にdoubleValueを適用
     */
    public Integer composeFunctions(Integer input) {
        Function<Integer, Integer> composed = doubleValue.compose(addTen);
        return composed.apply(input);
    }
    
    /**
     * 関数合成（andThen）- 左から右へ実行
     * まずdoubleValueを実行し、その結果にaddTenを適用
     */
    public Integer chainFunctions(Integer input) {
        Function<Integer, Integer> chained = doubleValue.andThen(addTen);
        return chained.apply(input);
    }
    
    /**
     * 複数の関数を組み合わせた複雑な処理
     */
    public Integer complexOperation(Integer input) {
        Function<Integer, Integer> complex = doubleValue
            .andThen(addTen)
            .andThen(square);
        return complex.apply(input);
    }
    
    /**
     * 文字列処理の関数合成
     */
    public String processString(String input) {
        Function<String, String> stringProcessor = toUpperCase
            .andThen(addPrefix);
        return stringProcessor.apply(input);
    }
    
    /**
     * 異なる型への変換を含む関数合成
     */
    public Integer stringToLength(String input) {
        Function<String, Integer> processor = toUpperCase
            .andThen(addPrefix)
            .andThen(getLength);
        return processor.apply(input);
    }
    
    /**
     * リストの各要素に関数を適用
     */
    public List<Integer> processNumbers(List<Integer> numbers) {
        return numbers.stream()
            .map(doubleValue.andThen(addTen))
            .collect(Collectors.toList());
    }
    
    /**
     * 条件付き処理
     */
    public Integer conditionalProcess(Integer input, boolean useSquare) {
        Function<Integer, Integer> processor = doubleValue.andThen(addTen);
        if (useSquare) {
            processor = processor.andThen(square);
        }
        return processor.apply(input);
    }
    
    /**
     * カスタム関数を受け取って処理
     */
    public <T, R> R processWithCustomFunction(T input, Function<T, R> customFunction) {
        return customFunction.apply(input);
    }
    
    /**
     * 関数のリストを順次適用
     */
    public Integer applyFunctionChain(Integer input, Function<Integer, Integer>... functions) {
        Function<Integer, Integer> chain = Function.identity();
        
        for (Function<Integer, Integer> function : functions) {
            chain = chain.andThen(function);
        }
        
        return chain.apply(input);
    }
    
    /**
     * 関数のファクトリメソッド
     */
    public static Function<Integer, Integer> createMultiplier(int multiplier) {
        return x -> x * multiplier;
    }
    
    /**
     * 関数のファクトリメソッド（加算）
     */
    public static Function<Integer, Integer> createAdder(int addend) {
        return x -> x + addend;
    }
    
    /**
     * 部分適用のシミュレーション
     */
    public Function<Integer, Integer> partialApplication(int fixedValue) {
        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        return x -> multiply.apply(x, fixedValue);
    }
    
    /**
     * 例外処理を含む関数
     */
    public Function<String, Integer> safeStringToInt() {
        return s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        };
    }
    
    /**
     * 関数の結果をキャッシュする機能
     */
    private Integer cachedResult = null;
    private String lastInput = null;
    
    public Function<String, Integer> createCachedFunction() {
        return input -> {
            if (lastInput != null && lastInput.equals(input) && cachedResult != null) {
                return cachedResult;
            }
            
            int result = input.length() * 2;
            lastInput = input;
            cachedResult = result;
            return result;
        };
    }
}