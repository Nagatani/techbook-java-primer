package chapter09.solutions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class CollectionTasksTest {

    @Test
    void 単語の出現回数を数える() {
        Map<String, Integer> counts = WordCounter.countWords("Java is fun. Java is powerful.");
        assertEquals(4, counts.size());
        assertEquals(2, counts.get("java"));
        assertEquals(2, counts.get("is"));
        assertEquals(List.of("is", "java"), WordCounter.mostFrequentWords(counts));
    }

    @Test
    void 集合演算は元の集合を壊さない() {
        Set<String> a = new TreeSet<>(List.of("Java", "Python", "C"));
        Set<String> b = new TreeSet<>(List.of("Python", "JavaScript", "Java"));
        assertEquals(Set.of("C", "Java", "JavaScript", "Python"), SetOperations.union(a, b));
        assertEquals(Set.of("Java", "Python"), SetOperations.intersection(a, b));
        assertEquals(Set.of("C"), SetOperations.difference(a, b));
        assertEquals(Set.of("JavaScript"), SetOperations.difference(b, a));
        assertEquals(3, a.size());
        assertEquals(3, b.size());
    }
}
