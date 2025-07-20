package com.example.theory.structures;

/**
 * スタックの抽象仕様（インターフェース）
 * リストAC-7の一部
 */
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