package com.example.theory.structures;

/**
 * スタックの抽象仕様（インターフェイス）
 *
 * 代数的仕様では、スタックを値として扱い、操作を関数として書く。
 *   new     : → Stack
 *   push    : Stack × T → Stack
 *   pop     : Stack → Stack     （いちばん上を取り除いたスタック）
 *   top     : Stack → T         （いちばん上の要素）
 *   isEmpty : Stack → Boolean
 *
 * 公理（どの実装でも成り立たなければならない性質）
 *   1. isEmpty(new) = true
 *   2. isEmpty(push(s, x)) = false
 *   3. top(push(s, x)) = x
 *   4. pop(push(s, x)) = s
 *   top(new) と pop(new) は定義しない（エラー）
 *
 * Javaのメソッドは、スタックの状態を書き換える形でこれを表す。
 *   push(x) は状態を push(s, x) に変える
 *   peek()  は top(s) を返す
 *   pop()   は top(s) を返し、状態を pop(s) に変える
 */
public interface Stack<T> {
    void push(T item);
    T pop();
    T peek();
    boolean isEmpty();
}
