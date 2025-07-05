package chapter11.solutions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * PipelineProcessorクラスのテストクラス
 */
public class PipelineProcessorTest {
    
    @Test
    void testProcessingResult() {
        List<String> steps = Arrays.asList("step1", "step2");
        
        PipelineProcessor.ProcessingResult<String> success = 
            PipelineProcessor.ProcessingResult.success("result", steps);
        
        assertTrue(success.isSuccess());
        assertEquals("result", success.getResult());
        assertEquals(steps, success.getExecutedSteps());
        assertNull(success.getErrorMessage());
        
        PipelineProcessor.ProcessingResult<String> failure = 
            PipelineProcessor.ProcessingResult.failure("error", steps);
        
        assertFalse(failure.isSuccess());
        assertNull(failure.getResult());
        assertEquals(steps, failure.getExecutedSteps());
        assertEquals("error", failure.getErrorMessage());
    }
    
    @Test
    void testBasicPipeline() {
        PipelineProcessor.ProcessingResult<Integer> result = 
            PipelineProcessor.Pipeline.<Integer>start()
                .then("double", x -> x * 2)
                .then("add10", x -> x + 10)
                .then("multiply3", x -> x * 3)
                .process(5);
        
        assertTrue(result.isSuccess());
        assertEquals(60, result.getResult()); // (5 * 2 + 10) * 3 = 60
        assertEquals(Arrays.asList("double", "add10", "multiply3"), result.getExecutedSteps());
    }
    
    @Test
    void testPipelineWithException() {
        PipelineProcessor.ProcessingResult<Integer> result = 
            PipelineProcessor.Pipeline.<Integer>start()
                .then("double", x -> x * 2)
                .then("divide", x -> x / 0) // Division by zero
                .then("add10", x -> x + 10)
                .process(5);
        
        assertFalse(result.isSuccess());
        assertNull(result.getResult());
        assertEquals(Arrays.asList("double"), result.getExecutedSteps());
        assertNotNull(result.getErrorMessage());
    }
    
    @Test
    void testStringProcessor() {
        PipelineProcessor.ProcessingResult<String> result = 
            PipelineProcessor.StringProcessor.processText("  hello world  ");
        
        assertTrue(result.isSuccess());
        assertEquals("PROCESSED_HELLO WORLD_DONE", result.getResult());
        assertEquals(Arrays.asList("trim", "toUpperCase", "addPrefix", "addSuffix"), 
                     result.getExecutedSteps());
    }
    
    @Test
    void testStringProcessorFunctions() {
        assertEquals("HELLO", PipelineProcessor.StringProcessor.toUpperCase().apply("hello"));
        assertEquals("hello", PipelineProcessor.StringProcessor.trim().apply("  hello  "));
        assertEquals("PREFIX_hello", PipelineProcessor.StringProcessor.addPrefix("PREFIX_").apply("hello"));
        assertEquals("hello_SUFFIX", PipelineProcessor.StringProcessor.addSuffix("_SUFFIX").apply("hello"));
        assertEquals("hello world", PipelineProcessor.StringProcessor.replace("_", " ").apply("hello_world"));
        assertEquals(5, PipelineProcessor.StringProcessor.getLength().apply("hello"));
        assertEquals("olleh", PipelineProcessor.StringProcessor.reverse().apply("hello"));
        assertTrue(PipelineProcessor.StringProcessor.isEmpty().apply(""));
        assertFalse(PipelineProcessor.StringProcessor.isEmpty().apply("hello"));
    }
    
    @Test
    void testNumberProcessor() {
        PipelineProcessor.ProcessingResult<Integer> result = 
            PipelineProcessor.NumberProcessor.processNumber(-5);
        
        assertTrue(result.isSuccess());
        assertEquals(30, result.getResult()); // abs(-5) * 2 + 10 = 20, clamp(100) = 20
        assertEquals(Arrays.asList("abs", "double", "add10", "clamp100"), 
                     result.getExecutedSteps());
    }
    
    @Test
    void testNumberProcessorFunctions() {
        assertEquals(10, PipelineProcessor.NumberProcessor.doubleValue().apply(5));
        assertEquals(15, PipelineProcessor.NumberProcessor.add(10).apply(5));
        assertEquals(15, PipelineProcessor.NumberProcessor.multiply(3).apply(5));
        assertEquals(3.0, PipelineProcessor.NumberProcessor.sqrt().apply(9), 0.001);
        assertEquals(5, PipelineProcessor.NumberProcessor.abs().apply(-5));
        assertEquals(10, PipelineProcessor.NumberProcessor.clamp(10).apply(15));
        assertEquals(5, PipelineProcessor.NumberProcessor.clamp(10).apply(5));
    }
    
    @Test
    void testListProcessor() {
        List<String> input = Arrays.asList("hello", "", "world", "hello", "test", "example", "more");
        
        PipelineProcessor.ProcessingResult<List<String>> result = 
            PipelineProcessor.ListProcessor.processStringList(input);
        
        assertTrue(result.isSuccess());
        List<String> expected = Arrays.asList("EXAMPLE", "HELLO", "MORE", "TEST", "WORLD");
        assertEquals(expected, result.getResult());
        assertEquals(Arrays.asList("filter", "map", "distinct", "sort", "take5"), 
                     result.getExecutedSteps());
    }
    
    @Test
    void testListProcessorFunctions() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        List<Integer> filtered = PipelineProcessor.ListProcessor.filter(n -> n > 3).apply(numbers);
        assertEquals(Arrays.asList(4, 5), filtered);
        
        List<String> mapped = PipelineProcessor.ListProcessor.map(Object::toString).apply(numbers);
        assertEquals(Arrays.asList("1", "2", "3", "4", "5"), mapped);
        
        List<Integer> sorted = PipelineProcessor.ListProcessor.sort().apply(Arrays.asList(3, 1, 4, 2, 5));
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), sorted);
        
        List<Integer> distinct = PipelineProcessor.ListProcessor.distinct().apply(Arrays.asList(1, 2, 2, 3, 3, 3));
        assertEquals(Arrays.asList(1, 2, 3), distinct);
        
        List<Integer> taken = PipelineProcessor.ListProcessor.take(3).apply(numbers);
        assertEquals(Arrays.asList(1, 2, 3), taken);
        
        Integer size = PipelineProcessor.ListProcessor.size().apply(numbers);
        assertEquals(5, size);
    }
    
    @Test
    void testConditionalPipeline() {
        PipelineProcessor.ProcessingResult<Integer> result = 
            PipelineProcessor.ConditionalPipeline.<Integer>start()
                .when("evenDouble", x -> x % 2 == 0, x -> x * 2)
                .when("oddAdd10", x -> x % 2 == 1, x -> x + 10)
                .always("add5", x -> x + 5)
                .process(4);
        
        assertTrue(result.isSuccess());
        assertEquals(13, result.getResult()); // 4 * 2 + 5 = 13
        assertEquals(Arrays.asList("evenDouble", "add5"), result.getExecutedSteps());
        
        result = PipelineProcessor.ConditionalPipeline.<Integer>start()
                .when("evenDouble", x -> x % 2 == 0, x -> x * 2)
                .when("oddAdd10", x -> x % 2 == 1, x -> x + 10)
                .always("add5", x -> x + 5)
                .process(3);
        
        assertTrue(result.isSuccess());
        assertEquals(18, result.getResult()); // 3 + 10 + 5 = 18
        assertEquals(Arrays.asList("oddAdd10", "add5"), result.getExecutedSteps());
    }
    
    @Test
    void testParallelPipeline() throws ExecutionException, InterruptedException {
        CompletableFuture<PipelineProcessor.ProcessingResult<Integer>> future = 
            PipelineProcessor.ParallelPipeline.<Integer>start()
                .thenAsync("double", x -> x * 2)
                .thenAsync("add10", x -> x + 10)
                .thenAsync("multiply3", x -> x * 3)
                .processAsync(5);
        
        PipelineProcessor.ProcessingResult<Integer> result = future.get();
        
        assertTrue(result.isSuccess());
        assertEquals(60, result.getResult()); // (5 * 2 + 10) * 3 = 60
        assertEquals(Arrays.asList("double", "add10", "multiply3"), result.getExecutedSteps());
    }
    
    @Test
    void testDataTransformationPipeline() {
        PipelineProcessor.ProcessingResult<List<String>> result = 
            PipelineProcessor.DataTransformationPipeline.processCsvData("  hello,  world, , test  ");
        
        assertTrue(result.isSuccess());
        assertEquals(Arrays.asList("HELLO", "WORLD", "TEST"), result.getResult());
    }
    
    @Test
    void testNormalizeUserData() {
        PipelineProcessor.ProcessingResult<String> result = 
            PipelineProcessor.DataTransformationPipeline.normalizeUserData("  john    doe  ");
        
        assertTrue(result.isSuccess());
        assertEquals("John doe", result.getResult());
    }
    
    @Test
    void testProcessingStepNamed() {
        PipelineProcessor.ProcessingStep<String, String> step = 
            PipelineProcessor.ProcessingStep.named("toUpper", String::toUpperCase);
        
        assertEquals("HELLO", step.apply("hello"));
    }
    
    @Test
    void testProcessingStepSafe() {
        PipelineProcessor.ProcessingStep<String, Integer> step = 
            PipelineProcessor.ProcessingStep.safe(
                Integer::parseInt,
                e -> -1
            );
        
        assertEquals(123, step.apply("123"));
        assertEquals(-1, step.apply("invalid"));
    }
    
    @Test
    void testEmptyPipeline() {
        PipelineProcessor.ProcessingResult<String> result = 
            PipelineProcessor.Pipeline.<String>start()
                .process("hello");
        
        assertTrue(result.isSuccess());
        assertEquals("hello", result.getResult());
        assertTrue(result.getExecutedSteps().isEmpty());
    }
    
    @Test
    void testComplexPipeline() {
        PipelineProcessor.ProcessingResult<String> result = 
            PipelineProcessor.Pipeline.<String>start()
                .then("trim", String::trim)
                .then("toLowerCase", String::toLowerCase)
                .then("replaceSpaces", s -> s.replaceAll("\\s+", "_"))
                .then("removeNonAlphaNumeric", s -> s.replaceAll("[^a-zA-Z0-9_]", ""))
                .then("addPrefix", s -> "processed_" + s)
                .process("  Hello World! 123  ");
        
        assertTrue(result.isSuccess());
        assertEquals("processed_hello_world_123", result.getResult());
        assertEquals(Arrays.asList("trim", "toLowerCase", "replaceSpaces", "removeNonAlphaNumeric", "addPrefix"), 
                     result.getExecutedSteps());
    }
}