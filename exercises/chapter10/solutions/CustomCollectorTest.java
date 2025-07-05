package chapter10.solutions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import java.util.stream.*;

class CustomCollectorTest {
    
    @Test
    void testJoining() {
        String result = Stream.of("a", "b", "c")
            .collect(CustomCollector.joining(", ", "[", "]"));
        assertEquals("[a, b, c]", result);
    }
    
    @Test
    void testStatistics() {
        Map<String, Object> stats = Stream.of(1.0, 2.0, 3.0, 4.0, 5.0)
            .collect(CustomCollector.statistics(x -> x));
        
        assertEquals(5L, stats.get("count"));
        assertEquals(15.0, (Double) stats.get("sum"), 0.01);
        assertEquals(1.0, (Double) stats.get("min"), 0.01);
        assertEquals(5.0, (Double) stats.get("max"), 0.01);
        assertEquals(3.0, (Double) stats.get("average"), 0.01);
    }
    
    @Test
    void testTopN() {
        List<Integer> top3 = Stream.of(5, 2, 8, 1, 9, 3)
            .collect(CustomCollector.topN(3, Integer::compareTo));
        
        assertEquals(3, top3.size());
        assertEquals(9, top3.get(0).intValue());
        assertEquals(8, top3.get(1).intValue());
        assertEquals(5, top3.get(2).intValue());
    }
    
    @Test
    void testPartitioning() {
        Map<Boolean, List<Integer>> partitioned = Stream.of(1, 2, 3, 4, 5, 6)
            .collect(CustomCollector.partitioning(x -> x % 2 == 0));
        
        assertEquals(3, partitioned.get(true).size()); // 偶数
        assertEquals(3, partitioned.get(false).size()); // 奇数
        assertTrue(partitioned.get(true).contains(2));
        assertTrue(partitioned.get(false).contains(1));
    }
    
    @Test
    void testFrequency() {
        Map<String, Long> freq = Stream.of("a", "b", "a", "c", "b", "a")
            .collect(CustomCollector.frequency());
        
        assertEquals(3L, freq.get("a").longValue());
        assertEquals(2L, freq.get("b").longValue());
        assertEquals(1L, freq.get("c").longValue());
    }
    
    @Test
    void testToImmutableList() {
        List<String> list = Stream.of("x", "y", "z")
            .collect(CustomCollector.toImmutableList());
        
        assertEquals(3, list.size());
        assertThrows(UnsupportedOperationException.class, () -> list.add("w"));
    }
    
    @Test
    void testMode() {
        Optional<String> mode = Stream.of("a", "b", "a", "c", "a")
            .collect(CustomCollector.mode());
        
        assertTrue(mode.isPresent());
        assertEquals("a", mode.get());
    }
    
    @Test
    void testModeEmpty() {
        Optional<String> mode = Stream.<String>empty()
            .collect(CustomCollector.mode());
        
        assertFalse(mode.isPresent());
    }
}