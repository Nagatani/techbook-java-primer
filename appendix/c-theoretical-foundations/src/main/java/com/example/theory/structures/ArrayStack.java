package com.example.theory.structures;

import java.util.EmptyStackException;

/**
 * スタックの具体実装（容量に上限のある配列で実装する）
 * 利用者はStackインターフェイスの公理だけを頼りにし、配列やtopの値は見えない
 */
public class ArrayStack<T> implements Stack<T> {
    private T[] array;
    private int top; // いちばん上の要素の添字。空のときは-1

    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        array = (T[]) new Object[capacity];
        top = -1;
    }

    @Override
    public void push(T item) {
        if (top >= array.length - 1) {
            // StackOverflowErrorはJVMの呼び出しスタックがあふれたときのエラーなので使わない
            throw new IllegalStateException("Stack is full (capacity " + array.length + ")");
        }
        array[++top] = item;
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T item = array[top];
        array[top--] = null; // 参照を残さず、ガベージコレクションの対象にする
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

    /**
     * Stackの公理がこの実装で成り立つことを確かめる（java -ea で実行する）
     */
    public static void checkAxioms() {
        Stack<String> s = new ArrayStack<>(3);
        assert s.isEmpty() : "axiom 1: isEmpty(new) = true";

        s.push("a");
        assert !s.isEmpty() : "axiom 2: isEmpty(push(s, x)) = false";
        assert s.peek().equals("a") : "axiom 3: top(push(s, x)) = x";

        s.push("b");
        String popped = s.pop();
        assert popped.equals("b") : "axiom 3: pop returns top(push(s, x)) = x";
        assert s.peek().equals("a") : "axiom 4: pop(push(s, x)) = s";

        s.pop();
        assert s.isEmpty() : "axiom 4: pop(push(new, x)) = new";
        try {
            s.pop();
            assert false : "pop(new) must be an error";
        } catch (EmptyStackException e) {
            IO.println("pop on empty stack: EmptyStackException");
        }
        IO.println("Stack axioms hold for ArrayStack");
    }

    public static void main(String[] args) {
        boolean enabled = false;
        assert enabled = true; // -ea のときだけ代入が実行される
        if (!enabled) {
            IO.println("assert is disabled: run with java -ea to check the axioms");
            return;
        }
        checkAxioms();
    }
}
