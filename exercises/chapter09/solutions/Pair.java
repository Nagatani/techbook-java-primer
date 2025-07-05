package chapter09.solutions;

import java.util.*;
import java.util.function.*;

/**
 * ジェネリクスを使った汎用ペアクラス
 */
public class Pair<T, U> {
    private final T first;
    private final U second;
    
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
    
    public T getFirst() { return first; }
    public U getSecond() { return second; }
    
    public <R> R mapFirst(Function<T, R> mapper) {
        return mapper.apply(first);
    }
    
    public <R> R mapSecond(Function<U, R> mapper) {
        return mapper.apply(second);
    }
    
    public <R, S> Pair<R, S> map(Function<T, R> firstMapper, Function<U, S> secondMapper) {
        return new Pair<>(firstMapper.apply(first), secondMapper.apply(second));
    }
    
    public boolean test(Predicate<T> firstPredicate, Predicate<U> secondPredicate) {
        return firstPredicate.test(first) && secondPredicate.test(second);
    }
    
    public static <T, U> Pair<T, U> of(T first, U second) {
        return new Pair<>(first, second);
    }
    
    public Pair<U, T> swap() {
        return new Pair<>(second, first);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
    
    @Override
    public String toString() {
        return String.format("Pair{first=%s, second=%s}", first, second);
    }
}