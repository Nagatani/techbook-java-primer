package com.example.exception;

import com.example.exception.patterns.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Result pattern implementation.
 */
class ResultTest {

    @Test
    @DisplayName("Success result should contain value")
    void successResultShouldContainValue() {
        Result<Integer, String> result = Result.success(42);
        
        assertTrue(result.isSuccess());
        assertFalse(result.isFailure());
        assertEquals(42, result.getValue());
        assertThrows(NoSuchElementException.class, result::getError);
    }

    @Test
    @DisplayName("Failure result should contain error")
    void failureResultShouldContainError() {
        Result<Integer, String> result = Result.failure("Error message");
        
        assertFalse(result.isSuccess());
        assertTrue(result.isFailure());
        assertEquals("Error message", result.getError());
        assertThrows(NoSuchElementException.class, result::getValue);
    }

    @Test
    @DisplayName("Map should transform success value")
    void mapShouldTransformSuccessValue() {
        Result<Integer, String> result = Result.success(10)
            .map(x -> x * 2)
            .map(x -> x + 5);
        
        assertEquals(25, result.getValue());
    }

    @Test
    @DisplayName("Map should not affect failure")
    void mapShouldNotAffectFailure() {
        Result<Integer, String> result = Result.<Integer, String>failure("Error")
            .map(x -> x * 2)
            .map(x -> x + 5);
        
        assertTrue(result.isFailure());
        assertEquals("Error", result.getError());
    }

    @Test
    @DisplayName("FlatMap should chain results")
    void flatMapShouldChainResults() {
        Result<Integer, String> result = Result.success(10)
            .flatMap(x -> Result.success(x * 2))
            .flatMap(x -> x > 15 ? Result.success(x) : Result.failure("Too small"));
        
        assertEquals(20, result.getValue());
    }

    @Test
    @DisplayName("Filter should create failure for non-matching predicate")
    void filterShouldCreateFailureForNonMatchingPredicate() {
        Result<Integer, String> result = Result.success(10)
            .filter(x -> x > 20, "Value too small");
        
        assertTrue(result.isFailure());
        assertEquals("Value too small", result.getError());
    }

    @Test
    @DisplayName("Fold should apply correct function based on result type")
    void foldShouldApplyCorrectFunction() {
        Result<Integer, String> success = Result.success(42);
        Result<Integer, String> failure = Result.failure("Error");
        
        String successFolded = success.fold(
            value -> "Success: " + value,
            error -> "Failure: " + error
        );
        
        String failureFolded = failure.fold(
            value -> "Success: " + value,
            error -> "Failure: " + error
        );
        
        assertEquals("Success: 42", successFolded);
        assertEquals("Failure: Error", failureFolded);
    }

    @Test
    @DisplayName("Of method should handle exceptions")
    void ofMethodShouldHandleExceptions() {
        Result<Integer, Exception> success = Result.of(() -> 42);
        Result<Integer, Exception> failure = Result.of(() -> {
            throw new RuntimeException("Test exception");
        });
        
        assertTrue(success.isSuccess());
        assertEquals(42, success.getValue());
        
        assertTrue(failure.isFailure());
        assertEquals("Test exception", failure.getError().getMessage());
    }

    @Test
    @DisplayName("ToOptional should convert correctly")
    void toOptionalShouldConvertCorrectly() {
        Optional<Integer> successOpt = Result.success(42).toOptional();
        Optional<Integer> failureOpt = Result.<Integer, String>failure("Error").toOptional();
        
        assertTrue(successOpt.isPresent());
        assertEquals(42, successOpt.get());
        
        assertFalse(failureOpt.isPresent());
    }

    @Test
    @DisplayName("GetValueOrElse should provide defaults")
    void getValueOrElseShouldProvideDefaults() {
        Result<Integer, String> success = Result.success(42);
        Result<Integer, String> failure = Result.failure("Error");
        
        assertEquals(42, success.getValueOrElse(0));
        assertEquals(0, failure.getValueOrElse(0));
        
        assertEquals(42, success.getValueOrElse(error -> 0));
        assertEquals(5, failure.getValueOrElse(error -> error.length()));
    }
}