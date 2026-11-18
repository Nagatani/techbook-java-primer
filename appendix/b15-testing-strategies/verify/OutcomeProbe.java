import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * テストの結果が何で決まるかを観察する。
 * JUnitは、テストメソッドが例外を投げずに終われば成功、例外を投げれば失敗とする。
 * アサーションの失敗も例外（AssertionFailedError）であり、
 * 前提（assumeTrue）が成り立たないときの例外（TestAbortedException）だけは「中断」になる。
 */
public class OutcomeProbe {

    static class Cases {
        @Test
        void passes() {
            assertEquals(4, 2 + 2);
        }

        @Test
        void assertionFails() {
            assertEquals(5, 2 + 2);
        }

        @Test
        void throwsUnexpectedException() {
            String s = null;
            s.length();
        }

        @Test
        void checksNothing() {
            int result = 2 + 2; // 結果を確かめていない
        }

        @Test
        void assumptionNotMet() {
            assumeTrue(false, "前提が成り立たない");
            assertEquals(5, 2 + 2);
        }

        @Test
        @Disabled("一時的に止めている")
        void disabled() {
            assertEquals(5, 2 + 2);
        }
    }

    public static void main(String[] args) {
        ProbeRunner.run(Cases.class);
    }
}
