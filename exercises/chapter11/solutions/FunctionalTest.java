package chapter11.solutions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class FunctionalTest {

    @Test
    void withTrimは前後の空白を除いてから処理する() {
        StringProcessor toUpper = s -> s.toUpperCase();
        assertEquals("HELLO WORLD", toUpper.withTrim().process("  hello world  "));
    }

    @Test
    void andThenは自分の結果を次の処理に渡す() {
        StringProcessor toUpper = s -> s.toUpperCase();
        StringProcessor addPrefix = s -> "[PROCESSED] " + s;
        assertEquals("[PROCESSED] HELLO", toUpper.andThen(addPrefix).process("hello"));
    }

    @Test
    void 四則演算() {
        assertEquals(15.0, Calculator.add().calculate(10, 5), 0.0);
        assertEquals(5.0, Calculator.subtract().calculate(10, 5), 0.0);
        assertEquals(50.0, Calculator.multiply().calculate(10, 5), 0.0);
        assertEquals(2.0, Calculator.divide().calculate(10, 5), 0.0);
    }

    @Test
    void ゼロで割るとNaN() {
        assertTrue(Double.isNaN(Calculator.divide().calculate(10, 0)));
    }

    @Test
    void 合成した条件() {
        Predicate<String> condition = MethodRefs.shortAndNotStartingWithD();
        assertTrue(condition.test("kiwi"));
        assertFalse(condition.test("date"));
        assertFalse(condition.test("apple"));
    }

    @Test
    void 長さの2倍() {
        assertEquals(12, MethodRefs.doubledLength().apply("banana"));
    }
}
