package chapter09.solutions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    
    @Test
    void testBasicOperations() {
        Pair<String, Integer> pair = new Pair<>("Hello", 42);
        assertEquals("Hello", pair.getFirst());
        assertEquals(42, pair.getSecond());
    }
    
    @Test
    void testMapping() {
        Pair<String, Integer> pair = Pair.of("Hello", 5);
        
        Integer length = pair.mapFirst(String::length);
        assertEquals(5, length);
        
        String doubled = pair.mapSecond(i -> String.valueOf(i * 2));
        assertEquals("10", doubled);
        
        Pair<Integer, String> mapped = pair.map(String::length, i -> "Value: " + i);
        assertEquals(5, mapped.getFirst());
        assertEquals("Value: 5", mapped.getSecond());
    }
    
    @Test
    void testPredicate() {
        Pair<String, Integer> pair = Pair.of("Hello", 5);
        
        assertTrue(pair.test(s -> s.length() == 5, i -> i > 0));
        assertFalse(pair.test(s -> s.length() == 3, i -> i > 0));
    }
    
    @Test
    void testSwap() {
        Pair<String, Integer> pair = Pair.of("Hello", 42);
        Pair<Integer, String> swapped = pair.swap();
        
        assertEquals(42, swapped.getFirst());
        assertEquals("Hello", swapped.getSecond());
    }
    
    @Test
    void testEquality() {
        Pair<String, Integer> pair1 = Pair.of("Hello", 42);
        Pair<String, Integer> pair2 = Pair.of("Hello", 42);
        Pair<String, Integer> pair3 = Pair.of("World", 42);
        
        assertEquals(pair1, pair2);
        assertNotEquals(pair1, pair3);
        assertEquals(pair1.hashCode(), pair2.hashCode());
    }
}