import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * GradeEvaluatorのテスト。評価が切り替わる境界の両側を確かめる
 */
@DisplayName("成績の評価")
class GradeEvaluatorTest {
    private final GradeEvaluator evaluator = new GradeEvaluator();

    @ParameterizedTest(name = "{0}点は{1}")
    @DisplayName("点数から評価を決める（境界値）")
    @CsvSource({
        "100, S", "90, S",
        "89, A", "80, A",
        "79, B", "70, B",
        "69, C", "60, C",
        "59, D", "0, D"
    })
    void grade(int score, String expected) {
        assertEquals(expected, evaluator.grade(score));
    }

    @ParameterizedTest(name = "{0}点は範囲外")
    @DisplayName("0〜100の外の点数は例外")
    @ValueSource(ints = {-1, 101})
    void gradeOutOfRange(int score) {
        assertThrows(IllegalArgumentException.class, () -> evaluator.grade(score));
    }

    @ParameterizedTest(name = "{1}回中{0}回の出席 → {2}")
    @DisplayName("出席が3分の2以上か")
    @CsvSource({
        "10, 15, true",
        "9, 15, false",
        "10, 14, true",
        "9, 14, false",
        "0, 1, false",
        "1, 1, true"
    })
    void attendance(int attended, int total, boolean expected) {
        assertEquals(expected, evaluator.hasEnoughAttendance(attended, total));
    }

    @ParameterizedTest(name = "出席{0}回・授業{1}回は不正")
    @DisplayName("出席回数・授業回数が不正なら例外")
    @CsvSource({"-1, 15", "16, 15", "0, 0"})
    void attendanceInvalid(int attended, int total) {
        assertThrows(IllegalArgumentException.class,
            () -> evaluator.hasEnoughAttendance(attended, total));
    }

    @ParameterizedTest(name = "{0}点・出席{1}/15回 → {2}")
    @DisplayName("点数と出席の両方で合否を決める")
    @CsvSource({
        "60, 10, true",
        "59, 15, false",
        "100, 9, false",
        "59, 9, false"
    })
    void passed(int score, int attended, boolean expected) {
        assertEquals(expected, evaluator.isPassed(score, attended, 15));
    }
}
