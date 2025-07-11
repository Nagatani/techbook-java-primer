package com.example.exception;

import com.example.exception.optimization.ExceptionPool;
import com.example.exception.optimization.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExceptionPool implementation.
 */
class ExceptionPoolTest {
    
    private ExceptionPool<ValidationException> pool;
    
    @BeforeEach
    void setUp() {
        pool = new ExceptionPool<>(ValidationException::create, 5);
    }
    
    @Test
    @DisplayName("Pool should create new exceptions when empty")
    void poolShouldCreateNewExceptionsWhenEmpty() {
        ValidationException e1 = pool.acquire("Error 1");
        ValidationException e2 = pool.acquire("Error 2");
        
        assertNotNull(e1);
        assertNotNull(e2);
        assertNotSame(e1, e2);
        assertEquals("Error 1", e1.getMessage());
        assertEquals("Error 2", e2.getMessage());
    }
    
    @Test
    @DisplayName("Pool should reuse released exceptions")
    void poolShouldReuseReleasedExceptions() {
        ValidationException e1 = pool.acquire("First");
        pool.release(e1);
        
        ValidationException e2 = pool.acquire("Second");
        
        assertSame(e1, e2); // Should be the same instance
        assertEquals("Second", e2.getMessage()); // But with new message
    }
    
    @Test
    @DisplayName("Pool should respect maximum size")
    void poolShouldRespectMaximumSize() {
        // Fill the pool
        ValidationException[] exceptions = new ValidationException[10];
        for (int i = 0; i < 10; i++) {
            exceptions[i] = pool.acquire("Error " + i);
        }
        
        // Release all
        for (ValidationException e : exceptions) {
            pool.release(e);
        }
        
        // Pool should only keep max size (5)
        ExceptionPool.PoolStatistics stats = pool.getStatistics();
        assertEquals(5, stats.poolSize);
    }
    
    @Test
    @DisplayName("Pool statistics should track usage")
    void poolStatisticsShouldTrackUsage() {
        // Acquire some exceptions
        ValidationException e1 = pool.acquire("Error 1");
        ValidationException e2 = pool.acquire("Error 2");
        pool.release(e1);
        ValidationException e3 = pool.acquire("Error 3");
        
        ExceptionPool.PoolStatistics stats = pool.getStatistics();
        
        assertEquals(3, stats.totalAcquired);
        assertEquals(1, stats.totalReleased);
        assertEquals(2, stats.totalCreated); // e1 and e2 were created, e3 reused e1
        assertTrue(stats.getReuseRatio() > 0);
    }
    
    @Test
    @DisplayName("Released exception should be reset")
    void releasedExceptionShouldBeReset() {
        ValidationException e = pool.acquire("Initial");
        e.setField("fieldName");
        e.addError("Error 1");
        e.addError("Error 2");
        
        pool.release(e);
        ValidationException reused = pool.acquire("New message");
        
        assertSame(e, reused);
        assertEquals("New message", reused.getMessage());
        assertNull(reused.getField());
        assertTrue(reused.getErrors().isEmpty());
    }
    
    @Test
    @DisplayName("Clear should empty the pool")
    void clearShouldEmptyThePool() {
        ValidationException e1 = pool.acquire("Error 1");
        ValidationException e2 = pool.acquire("Error 2");
        pool.release(e1);
        pool.release(e2);
        
        pool.clear();
        
        ExceptionPool.PoolStatistics stats = pool.getStatistics();
        assertEquals(0, stats.poolSize);
        assertEquals(0, stats.totalAcquired);
        assertEquals(0, stats.totalReleased);
        assertEquals(0, stats.totalCreated);
    }
}