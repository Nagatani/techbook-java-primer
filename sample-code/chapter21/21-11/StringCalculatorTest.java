import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StringCalculatorTest {
    @Test
    public void testAdd_EmptyString_ReturnsZero() {
        // まだ存在しないクラスとメソッドのテストを書く
        StringCalculator calc = new StringCalculator();
        assertEquals(0, calc.add(""));
    }
}
