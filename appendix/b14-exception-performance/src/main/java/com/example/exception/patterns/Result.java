package com.example.exception.patterns;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Result type for functional error handling without exceptions.
 * Similar to Either monad in functional programming.
 *
 * @param <T> Success value type
 * @param <E> Error value type
 */
public sealed interface Result<T, E> {
    
    /**
     * Create a successful result.
     */
    static <T, E> Result<T, E> success(T value) {
        return new Success<>(Objects.requireNonNull(value));
    }
    
    /**
     * Create a failed result.
     */
    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(Objects.requireNonNull(error));
    }
    
    /**
     * Create a result from a supplier that may throw an exception.
     */
    static <T> Result<T, Exception> of(Supplier<T> supplier) {
        try {
            return success(supplier.get());
        } catch (Exception e) {
            return failure(e);
        }
    }
    
    /**
     * Check if this is a successful result.
     */
    boolean isSuccess();
    
    /**
     * Check if this is a failed result.
     */
    default boolean isFailure() {
        return !isSuccess();
    }
    
    /**
     * Get the success value.
     * @throws NoSuchElementException if this is a failure
     */
    T getValue();
    
    /**
     * Get the error value.
     * @throws NoSuchElementException if this is a success
     */
    E getError();
    
    /**
     * Get the success value or a default if this is a failure.
     */
    T getValueOrElse(T defaultValue);
    
    /**
     * Get the success value or compute a default if this is a failure.
     */
    T getValueOrElse(Function<E, T> errorMapper);
    
    /**
     * Transform the success value if present.
     */
    <U> Result<U, E> map(Function<T, U> mapper);
    
    /**
     * Transform the error value if present.
     */
    <F> Result<T, F> mapError(Function<E, F> mapper);
    
    /**
     * Flat map for chaining results.
     */
    <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper);
    
    /**
     * Filter the success value.
     */
    Result<T, E> filter(Predicate<T> predicate, E errorIfNotMatch);
    
    /**
     * Execute a consumer if this is a success.
     */
    Result<T, E> ifSuccess(Consumer<T> consumer);
    
    /**
     * Execute a consumer if this is a failure.
     */
    Result<T, E> ifFailure(Consumer<E> consumer);
    
    /**
     * Convert to Optional (success value only).
     */
    Optional<T> toOptional();
    
    /**
     * Fold the result into a single value.
     */
    <R> R fold(Function<T, R> successMapper, Function<E, R> errorMapper);
    
    /**
     * Success implementation.
     */
    record Success<T, E>(T value) implements Result<T, E> {
        public Success {
            Objects.requireNonNull(value);
        }
        
        @Override
        public boolean isSuccess() {
            return true;
        }
        
        @Override
        public T getValue() {
            return value;
        }
        
        @Override
        public E getError() {
            throw new NoSuchElementException("Success has no error");
        }
        
        @Override
        public T getValueOrElse(T defaultValue) {
            return value;
        }
        
        @Override
        public T getValueOrElse(Function<E, T> errorMapper) {
            return value;
        }
        
        @Override
        public <U> Result<U, E> map(Function<T, U> mapper) {
            return success(mapper.apply(value));
        }
        
        @Override
        public <F> Result<T, F> mapError(Function<E, F> mapper) {
            return success(value);
        }
        
        @Override
        public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
            return mapper.apply(value);
        }
        
        @Override
        public Result<T, E> filter(Predicate<T> predicate, E errorIfNotMatch) {
            return predicate.test(value) ? this : failure(errorIfNotMatch);
        }
        
        @Override
        public Result<T, E> ifSuccess(Consumer<T> consumer) {
            consumer.accept(value);
            return this;
        }
        
        @Override
        public Result<T, E> ifFailure(Consumer<E> consumer) {
            return this;
        }
        
        @Override
        public Optional<T> toOptional() {
            return Optional.of(value);
        }
        
        @Override
        public <R> R fold(Function<T, R> successMapper, Function<E, R> errorMapper) {
            return successMapper.apply(value);
        }
    }
    
    /**
     * Failure implementation.
     */
    record Failure<T, E>(E error) implements Result<T, E> {
        public Failure {
            Objects.requireNonNull(error);
        }
        
        @Override
        public boolean isSuccess() {
            return false;
        }
        
        @Override
        public T getValue() {
            throw new NoSuchElementException("Failure has no value");
        }
        
        @Override
        public E getError() {
            return error;
        }
        
        @Override
        public T getValueOrElse(T defaultValue) {
            return defaultValue;
        }
        
        @Override
        public T getValueOrElse(Function<E, T> errorMapper) {
            return errorMapper.apply(error);
        }
        
        @Override
        public <U> Result<U, E> map(Function<T, U> mapper) {
            return failure(error);
        }
        
        @Override
        public <F> Result<T, F> mapError(Function<E, F> mapper) {
            return failure(mapper.apply(error));
        }
        
        @Override
        public <U> Result<U, E> flatMap(Function<T, Result<U, E>> mapper) {
            return failure(error);
        }
        
        @Override
        public Result<T, E> filter(Predicate<T> predicate, E errorIfNotMatch) {
            return this;
        }
        
        @Override
        public Result<T, E> ifSuccess(Consumer<T> consumer) {
            return this;
        }
        
        @Override
        public Result<T, E> ifFailure(Consumer<E> consumer) {
            consumer.accept(error);
            return this;
        }
        
        @Override
        public Optional<T> toOptional() {
            return Optional.empty();
        }
        
        @Override
        public <R> R fold(Function<T, R> successMapper, Function<E, R> errorMapper) {
            return errorMapper.apply(error);
        }
    }
}