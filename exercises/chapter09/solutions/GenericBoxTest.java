package chapter09.solutions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GenericBoxTest {
    
    private GenericBox<String> stringBox;
    private GenericBox<Integer> intBox;
    
    @BeforeEach
    void setUp() {
        stringBox = new GenericBox<>(String.class);
        intBox = new GenericBox<>(Integer.class);
    }
    
    @Test
    void testBasicOperations() {
        assertTrue(stringBox.isEmpty());
        
        stringBox.setContent("Hello");
        assertFalse(stringBox.isEmpty());
        assertEquals("Hello", stringBox.getContent());
        
        stringBox.clear();
        assertTrue(stringBox.isEmpty());
        assertEquals(1, stringBox.getHistory().size());
        assertEquals("Hello", stringBox.getHistory().get(0));
    }
    
    @Test
    void testTypeInformation() {
        assertEquals(String.class, stringBox.getType());
        assertEquals(Integer.class, intBox.getType());
    }
    
    @Test
    void testHistory() {
        stringBox.setContent("First");
        stringBox.setContent("Second");
        stringBox.setContent("Third");
        
        List<String> history = stringBox.getHistory();
        assertEquals(2, history.size());
        assertEquals("First", history.get(0));
        assertEquals("Second", history.get(1));
        
        stringBox.clearHistory();
        assertEquals(0, stringBox.getHistory().size());
    }
    
    @Test
    void testInstanceCheck() {
        stringBox.setContent("Test");
        assertTrue(stringBox.isInstanceOf(String.class));
        assertTrue(stringBox.isInstanceOf(Object.class));
        assertFalse(stringBox.isInstanceOf(Integer.class));
    }
    
    @Test
    void testCasting() {
        stringBox.setContent("Test");
        assertEquals("Test", stringBox.getContentAs(String.class));
        assertEquals("Test", stringBox.getContentAs(Object.class));
        
        assertThrows(ClassCastException.class, () -> {
            stringBox.getContentAs(Integer.class);
        });
    }
    
    @Test
    void testOperations() {
        stringBox.setContent("Hello");
        Integer length = stringBox.applyOperation(String::length);
        assertEquals(5, length);
        
        assertTrue(stringBox.matches(s -> s.startsWith("H")));
        assertFalse(stringBox.matches(s -> s.startsWith("W")));
    }
    
    @Test
    void testSwap() {
        GenericBox<String> box1 = GenericBox.of("First", String.class);
        GenericBox<String> box2 = GenericBox.of("Second", String.class);
        
        box1.swap(box2);
        assertEquals("Second", box1.getContent());
        assertEquals("First", box2.getContent());
    }
    
    @Test
    void testFactoryMethods() {
        GenericBox<String> box1 = GenericBox.of("Hello", String.class);
        assertEquals("Hello", box1.getContent());
        
        GenericBox<String> box2 = GenericBox.empty(String.class);
        assertTrue(box2.isEmpty());
    }
    
    @Test
    void testEquality() {
        GenericBox<String> box1 = GenericBox.of("Test", String.class);
        GenericBox<String> box2 = GenericBox.of("Test", String.class);
        GenericBox<String> box3 = GenericBox.of("Different", String.class);
        
        assertEquals(box1, box2);
        assertNotEquals(box1, box3);
        assertEquals(box1.hashCode(), box2.hashCode());
    }
    
    @Test
    void testToString() {
        stringBox.setContent("Test");
        String str = stringBox.toString();
        assertTrue(str.contains("String"));
        assertTrue(str.contains("Test"));
    }
}