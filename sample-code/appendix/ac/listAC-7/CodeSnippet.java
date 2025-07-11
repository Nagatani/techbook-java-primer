/**
 * リストAC-7
 * ArrayStackクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (156行目)
 */

// スタックの抽象仕様
public interface Stack<T> {
    // 操作
    void push(T item);      // push: Stack × T → Stack
    T pop();               // pop: Stack → T × Stack  
    T peek();              // peek: Stack → T
    boolean isEmpty();     // isEmpty: Stack → Boolean
    
    // 公理（コメントで表現）
    // 1. pop(push(s, x)) = s
    // 2. peek(push(s, x)) = x
    // 3. isEmpty(new Stack()) = true
    // 4. isEmpty(push(s, x)) = false
}

// 具体実装
public class ArrayStack<T> implements Stack<T> {
    private T[] array;
    private int top;
    
    @SuppressWarnings("unchecked")
    public ArrayStack(int capacity) {
        array = (T[]) new Object[capacity];
        top = -1;
    }
    
    public void push(T item) {
        if (top >= array.length - 1) {
            throw new StackOverflowError();
        }
        array[++top] = item;
    }
    
    public T pop() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        T item = array[top];
        array[top--] = null; // メモリリーク防止
        return item;
    }
    
    public T peek() {
        if (isEmpty()) {
            throw new EmptyStackException();
        }
        return array[top];
    }
    
    public boolean isEmpty() {
        return top == -1;
    }
}