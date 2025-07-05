package chapter09.solutions;

import java.util.*;
import java.util.function.*;

/**
 * ジェネリクスを使ったスタッククラス
 */
public class GenericStack<T> implements Iterable<T> {
    private final List<T> items;
    private final int maxSize;
    
    public GenericStack() {
        this(Integer.MAX_VALUE);
    }
    
    public GenericStack(int maxSize) {
        this.items = new ArrayList<>();
        this.maxSize = maxSize;
    }
    
    public void push(T item) {
        if (size() >= maxSize) {
            throw new IllegalStateException("スタックが満杯です");
        }
        items.add(item);
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return items.remove(items.size() - 1);
    }
    
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return items.get(items.size() - 1);
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    public int size() {
        return items.size();
    }
    
    public void clear() {
        items.clear();
    }
    
    public <R> GenericStack<R> map(Function<T, R> mapper) {
        GenericStack<R> result = new GenericStack<>(maxSize);
        for (T item : items) {
            result.push(mapper.apply(item));
        }
        return result;
    }
    
    public GenericStack<T> filter(Predicate<T> predicate) {
        GenericStack<T> result = new GenericStack<>(maxSize);
        for (T item : items) {
            if (predicate.test(item)) {
                result.push(item);
            }
        }
        return result;
    }
    
    public Optional<T> find(Predicate<T> predicate) {
        return items.stream().filter(predicate).findFirst();
    }
    
    public boolean contains(T item) {
        return items.contains(item);
    }
    
    public List<T> toList() {
        return new ArrayList<>(items);
    }
    
    @Override
    public Iterator<T> iterator() {
        return new ArrayList<>(items).iterator();
    }
    
    @Override
    public String toString() {
        return "GenericStack{items=" + items + "}";
    }
    
    @SafeVarargs
    public static <T> GenericStack<T> of(T... items) {
        GenericStack<T> stack = new GenericStack<>();
        for (T item : items) {
            stack.push(item);
        }
        return stack;
    }
}