import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

/**
 * assertAllとassertThrowsのふるまいを観察する。
 */
public class AssertionProbe {

    record Person(String first, String last, int age) {
    }

    static final Person PERSON = new Person("Hanako", "Sato", 20);

    static class Grouped {
        @Test
        void sequential() {
            // 1つ目が失敗した時点で例外が投げられ、2つ目以降は実行されない
            assertEquals("Taro", PERSON.first());
            assertEquals("Suzuki", PERSON.last());
            assertEquals(21, PERSON.age());
        }

        @Test
        void grouped() {
            // すべてを実行し、失敗をまとめて報告する
            assertAll("person",
                    () -> assertEquals("Taro", PERSON.first()),
                    () -> assertEquals("Suzuki", PERSON.last()),
                    () -> assertEquals(21, PERSON.age()));
        }
    }

    static int parse(String s) {
        return Integer.parseInt(s);
    }

    static class Throws {
        @Test
        void exactType() {
            assertThrows(NumberFormatException.class, () -> parse("abc"));
        }

        @Test
        void superType() {
            // NumberFormatExceptionはIllegalArgumentExceptionのサブクラス
            assertThrows(IllegalArgumentException.class, () -> parse("abc"));
        }

        @Test
        void superTypeExactly() {
            assertThrowsExactly(IllegalArgumentException.class, () -> parse("abc"));
        }

        @Test
        void nothingThrown() {
            assertThrows(NumberFormatException.class, () -> parse("123"));
        }

        @Test
        void otherType() {
            assertThrows(ArithmeticException.class, () -> parse("abc"));
        }

        @Test
        void checkMessage() {
            // assertThrowsは投げられた例外を返すので、メッセージも確かめられる
            NumberFormatException e =
                    assertThrows(NumberFormatException.class, () -> parse("abc"));
            assertEquals("For input string: \"abc\"", e.getMessage());
        }
    }

    public static void main(String[] args) {
        IO.println("--- assertAll");
        ProbeRunner.run(Grouped.class);
        // assertAllが投げる例外の中身
        try {
            assertAll("person",
                    () -> assertEquals("Taro", PERSON.first()),
                    () -> assertEquals("Suzuki", PERSON.last()),
                    () -> assertEquals(21, PERSON.age()));
        } catch (MultipleFailuresError e) {
            IO.println("failures in MultipleFailuresError: " + e.getFailures().size());
            for (Throwable t : e.getFailures()) {
                IO.println("  " + t.getMessage());
            }
        }
        IO.println("--- assertThrows");
        ProbeRunner.run(Throws.class);
    }
}
