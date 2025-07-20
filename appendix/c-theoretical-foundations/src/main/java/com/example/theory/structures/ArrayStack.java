package com.example.theory.structures;

import java.util.EmptyStackException;

/**
 * スタックの具体実装
 * リストAC-7の一部
 */
public class ArrayStack<T> implements Stack<T> {
    private T[] array;
    private int top;
    
    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        array = (T[]) new Object[capacity];
        top = -1;
    }
    
    @Override
    public void push(T item) {
        if (top >= array.length - 1) {
            throw new StackOverflowError("Stack is full");
        }
        array[++top] = item;
    }
    
    @Override
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T item = array[top];
        array[top--] = null; // メモリリーク防止
        return item;
    }
    
    @Override
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return array[top];
    }
    
    @Override
    public boolean isEmpty() {
        return top == -1;
    }
}