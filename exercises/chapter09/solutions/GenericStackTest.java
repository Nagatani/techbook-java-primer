package chapter09.solutions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GenericStackTest {
    
    @Test
    void testBasicOperations() {
        GenericStack<String> stack = new GenericStack<>();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
        
        stack.push("First");
        stack.push("Second");
        assertFalse(stack.isEmpty());
        assertEquals(2, stack.size());
        
        assertEquals("Second", stack.peek());
        assertEquals("Second", stack.pop());
        assertEquals(1, stack.size());
        
        assertEquals("First", stack.pop());
        assertTrue(stack.isEmpty());
    }
    
    @Test
    void testExceptions() {
        GenericStack<String> stack = new GenericStack<>();
        
        assertThrows(EmptyStackException.class, stack::pop);
        assertThrows(EmptyStackException.class, stack::peek);
        
        GenericStack<String> limitedStack = new GenericStack<>(2);
        limitedStack.push("First");
        limitedStack.push("Second");
        assertThrows(IllegalStateException.class, () -> limitedStack.push("Third"));
    }
    
    @Test
    void testMapping() {
        GenericStack<String> stack = GenericStack.of("hello", "world");
        GenericStack<Integer> lengths = stack.map(String::length);
        
        assertEquals(5, lengths.pop());
        assertEquals(5, lengths.pop());
    }
    
    @Test
    void testFiltering() {
        GenericStack<Integer> stack = GenericStack.of(1, 2, 3, 4, 5);
        GenericStack<Integer> evens = stack.filter(i -> i % 2 == 0);
        
        assertEquals(2, evens.size());
        assertTrue(evens.contains(2));
        assertTrue(evens.contains(4));
    }
    
    @Test
    void testFind() {
        GenericStack<String> stack = GenericStack.of("apple", "banana", "cherry");
        
        Optional<String> found = stack.find(s -> s.startsWith("b"));
        assertTrue(found.isPresent());
        assertEquals("banana", found.get());
        
        Optional<String> notFound = stack.find(s -> s.startsWith("z"));
        assertFalse(notFound.isPresent());
    }
    
    @Test
    void testIteration() {
        GenericStack<String> stack = GenericStack.of("first", "second", "third");
        List<String> items = new ArrayList<>();
        
        for (String item : stack) {
            items.add(item);
        }
        
        assertEquals(3, items.size());
        assertEquals("first", items.get(0));
    }
}