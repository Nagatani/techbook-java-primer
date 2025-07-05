package chapter11.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * FunctionProcessorクラスのテストクラス
 */
public class FunctionProcessorTest {
    
    private FunctionProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new FunctionProcessor();
    }
    
    @Test
    void testApplyDoubleValue() {
        // 5を倍にすると10
        assertEquals(10, processor.applyDoubleValue(5));
        
        // 0を倍にすると0
        assertEquals(0, processor.applyDoubleValue(0));
        
        // 負の数の場合
        assertEquals(-6, processor.applyDoubleValue(-3));
    }
    
    @Test
    void testComposeFunctions() {
        // compose: doubleValue.compose(addTen)
        // 5 -> addTen(5) = 15 -> doubleValue(15) = 30
        assertEquals(30, processor.composeFunctions(5));
        
        // 0の場合
        assertEquals(20, processor.composeFunctions(0));
    }
    
    @Test
    void testChainFunctions() {
        // andThen: doubleValue.andThen(addTen)
        // 5 -> doubleValue(5) = 10 -> addTen(10) = 20
        assertEquals(20, processor.chainFunctions(5));
        
        // 0の場合
        assertEquals(10, processor.chainFunctions(0));
    }
    
    @Test
    void testComplexOperation() {
        // doubleValue.andThen(addTen).andThen(square)
        // 5 -> 10 -> 20 -> 400
        assertEquals(400, processor.complexOperation(5));
        
        // 2 -> 4 -> 14 -> 196
        assertEquals(196, processor.complexOperation(2));
    }
    
    @Test
    void testProcessString() {
        // toUpperCase.andThen(addPrefix)
        assertEquals("PREFIX_HELLO", processor.processString("hello"));
        assertEquals("PREFIX_WORLD", processor.processString("world"));
        assertEquals("PREFIX_", processor.processString(""));
    }
    
    @Test
    void testStringToLength() {
        // toUpperCase.andThen(addPrefix).andThen(getLength)
        // "hello" -> "HELLO" -> "PREFIX_HELLO" -> 12
        assertEquals(12, processor.stringToLength("hello"));
        
        // "test" -> "TEST" -> "PREFIX_TEST" -> 11
        assertEquals(11, processor.stringToLength("test"));
    }
    
    @Test
    void testProcessNumbers() {
        List<Integer> input = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> expected = Arrays.asList(12, 14, 16, 18, 20);
        
        assertEquals(expected, processor.processNumbers(input));
    }
    
    @Test
    void testConditionalProcess() {
        // useSquare = false の場合
        // 5 -> 10 -> 20
        assertEquals(20, processor.conditionalProcess(5, false));
        
        // useSquare = true の場合
        // 5 -> 10 -> 20 -> 400
        assertEquals(400, processor.conditionalProcess(5, true));
    }
    
    @Test
    void testProcessWithCustomFunction() {
        Function<String, Integer> customFunction = s -> s.length() * 3;
        
        assertEquals(15, processor.processWithCustomFunction("hello", customFunction));
        assertEquals(12, processor.processWithCustomFunction("test", customFunction));
    }
    
    @Test
    void testApplyFunctionChain() {
        Function<Integer, Integer> multiply2 = x -> x * 2;
        Function<Integer, Integer> add5 = x -> x + 5;
        Function<Integer, Integer> subtract3 = x -> x - 3;
        
        // 10 -> 20 -> 25 -> 22
        assertEquals(22, processor.applyFunctionChain(10, multiply2, add5, subtract3));
    }
    
    @Test
    void testCreateMultiplier() {
        Function<Integer, Integer> multiply3 = FunctionProcessor.createMultiplier(3);
        
        assertEquals(15, multiply3.apply(5));
        assertEquals(0, multiply3.apply(0));
        assertEquals(-9, multiply3.apply(-3));
    }
    
    @Test
    void testCreateAdder() {
        Function<Integer, Integer> add7 = FunctionProcessor.createAdder(7);
        
        assertEquals(12, add7.apply(5));
        assertEquals(7, add7.apply(0));
        assertEquals(4, add7.apply(-3));
    }
    
    @Test
    void testPartialApplication() {
        Function<Integer, Integer> multiply5 = processor.partialApplication(5);
        
        assertEquals(25, multiply5.apply(5));
        assertEquals(0, multiply5.apply(0));
        assertEquals(-15, multiply5.apply(-3));
    }
    
    @Test
    void testSafeStringToInt() {
        Function<String, Integer> safeParser = processor.safeStringToInt();
        
        assertEquals(123, safeParser.apply("123"));
        assertEquals(-456, safeParser.apply("-456"));
        assertEquals(0, safeParser.apply("invalid"));
        assertEquals(0, safeParser.apply("12.34"));
    }
    
    @Test
    void testCreateCachedFunction() {
        Function<String, Integer> cachedFunction = processor.createCachedFunction();
        
        // 最初の呼び出し
        assertEquals(10, cachedFunction.apply("hello"));
        
        // 同じ入力での呼び出し（キャッシュされた値を返す）
        assertEquals(10, cachedFunction.apply("hello"));
        
        // 異なる入力での呼び出し
        assertEquals(8, cachedFunction.apply("test"));
    }
    
    @Test
    void testFunctionComposition() {
        // 複数の関数を組み合わせたテスト
        Function<Integer, Integer> multiply3 = FunctionProcessor.createMultiplier(3);
        Function<Integer, Integer> add2 = FunctionProcessor.createAdder(2);
        
        Function<Integer, Integer> composed = multiply3.andThen(add2);
        
        // 5 -> 15 -> 17
        assertEquals(17, composed.apply(5));
    }
    
    @Test
    void testIdentityFunction() {
        Function<Integer, Integer> identity = Function.identity();
        
        assertEquals(42, identity.apply(42));
        assertEquals(0, identity.apply(0));
        assertEquals(-10, identity.apply(-10));
    }
}