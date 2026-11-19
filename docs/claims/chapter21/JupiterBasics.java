// 第21章：この章で使うJUnit Jupiterの書き方（JUnit 5でも6でもコンパイルできるか）
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JupiterBasics {
    int value;

    @BeforeEach
    void setUp() {
        value = 2;
    }

    @Test
    @DisplayName("足し算")
    void add() {
        assertEquals(3, value + 1);
        assertTrue(value > 0);
        assertThrows(ArithmeticException.class, () -> { int z = value / 0; });
    }

    @Nested
    class Inner {
        @Test
        void inner() {
            assertNotNull(Integer.valueOf(value));
        }
    }
}
