import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * パラメーター化テストが、引数の組ごとに別のテストとして実行されることを観察する。
 */
public class ParameterizedProbe {

    /** うるう年の判定（100で割り切れる年の扱いを誤っている） */
    static boolean isLeapYear(int year) {
        return year % 4 == 0;
    }

    static class Cases {
        @ParameterizedTest
        @ValueSource(ints = {2024, 2000})
        void leapYears(int year) {
            assertTrue(isLeapYear(year));
        }

        @ParameterizedTest
        @ValueSource(ints = {2023, 1900, 2100})
        void commonYears(int year) {
            assertFalse(isLeapYear(year));
        }

        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({
            "1, 2, 3",
            "-1, 1, 0",
            "2147483647, 1, 2147483648"
        })
        void addition(int a, int b, long expected) {
            assertEquals(expected, a + b);
        }
    }

    public static void main(String[] args) {
        ProbeRunner.run(Cases.class);
    }
}
