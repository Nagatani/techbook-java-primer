package chapter10.solutions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class GenericsTest {

    @Test
    void 箱に入れた値をキャストなしで取り出せる() {
        GenericBox<String> box = new GenericBox<>();
        assertTrue(box.isEmpty());
        box.put("Hello");
        String value = box.get();
        assertEquals("Hello", value);
        box.clear();
        assertNull(box.get());
    }

    @Test
    void swapで値と型の順序が入れ替わる() {
        Pair<String, Integer> pair = new Pair<>("項目", 100);
        Pair<Integer, String> swapped = pair.swap();
        assertEquals(100, swapped.getFirst());
        assertEquals("項目", swapped.getSecond());
    }

    @Test
    void Pairのequalsとhashcodeは両方の値で比べる() {
        assertEquals(new Pair<>("a", 1), new Pair<>("a", 1));
        assertEquals(new Pair<>("a", 1).hashCode(), new Pair<>("a", 1).hashCode());
        assertNotEquals(new Pair<>("a", 1), new Pair<>("a", 2));
        assertEquals(new Pair<>(null, 1), new Pair<>(null, 1));
    }

    @Test
    void NumberBoxは型の違う箱とも比べられる() {
        NumberBox<Integer> intBox = new NumberBox<>(42);
        NumberBox<Double> doubleBox = new NumberBox<>(3.14);
        assertEquals(1, intBox.compareTo(doubleBox));
        assertEquals(-1, doubleBox.compareTo(intBox));
        assertEquals(3, doubleBox.getAsInt());
        assertTrue(intBox.isPositive());
        assertFalse(new NumberBox<>(-1L).isPositive());
    }

    @Test
    void 配列の入れ替えと検索() {
        String[] words = {"Hello", "World", "Java"};
        GenericUtils.swap(words, 0, 2);
        assertEquals("Java", words[0]);
        assertEquals("Hello", words[2]);
        assertEquals(1, GenericUtils.find(words, "World"));
        assertEquals(-1, GenericUtils.find(words, "C"));
        assertThrows(IndexOutOfBoundsException.class, () -> GenericUtils.swap(words, 0, 3));
    }

    @Test
    void 最大値と最小値() {
        assertEquals("Banana", GenericUtils.max("Apple", "Banana"));
        assertEquals(1, GenericUtils.findMin(Arrays.asList(5, 2, 8, 1, 9)));
        assertNull(GenericUtils.findMin(new ArrayList<Integer>()));
        assertEquals(2, GenericUtils.countNonNull(Arrays.asList("a", null, "b")));
    }

    @Test
    void 組の結合() {
        Pair<String, String> combined =
                GenericUtils.combinePairs(new Pair<>("Start", 1), new Pair<>(3.14, "End"));
        assertEquals(new Pair<>("Start", "End"), combined);
    }

    @Test
    void ワイルドカードのリスト() {
        assertEquals(15.0, NumberLists.sum(List.of(1, 2, 3, 4, 5)), 0.001);
        assertEquals(7.5, NumberLists.sum(List.of(1.5, 2.5, 3.5)), 0.001);
        List<Object> objects = new ArrayList<>();
        NumberLists.addSquares(objects, 3);
        assertEquals(List.of(1, 4, 9), objects);
    }
}
