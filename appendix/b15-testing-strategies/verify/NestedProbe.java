import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @Nestedのクラスで、外側と内側の@BeforeEach・@AfterEachがどの順に呼ばれるかを観察する。
 */
public class NestedProbe {

    @DisplayName("スタック")
    static class StackSpec {
        Deque<String> stack;

        @BeforeEach
        void outerSetUp() {
            IO.println("  outer @BeforeEach");
            stack = new ArrayDeque<>();
        }

        @AfterEach
        void outerTearDown() {
            IO.println("  outer @AfterEach");
        }

        @Test
        @DisplayName("作った直後は空")
        void isEmpty() {
            IO.println("  test: isEmpty");
            assertEquals(0, stack.size());
        }

        @Nested
        @DisplayName("1つ積んだとき")
        class AfterPush {
            @BeforeEach
            void innerSetUp() {
                IO.println("  inner @BeforeEach");
                stack.push("a");
            }

            @AfterEach
            void innerTearDown() {
                IO.println("  inner @AfterEach");
            }

            @Test
            @DisplayName("取り出すと空になる")
            void popEmpties() {
                IO.println("  test: popEmpties");
                stack.pop();
                assertThrows(NoSuchElementException.class, stack::pop);
            }
        }
    }

    public static void main(String[] args) {
        ProbeRunner.run(StackSpec.class);
    }
}
