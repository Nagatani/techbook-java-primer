/**
 * リストAB-11
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (320行目)
 */

// Javaにおける圏の要素

// 対象：型
class A {}
class B {}
class C {}

// 射：関数
Function<A, B> f = a -> new B();
Function<B, C> g = b -> new C();

// 合成
Function<A, C> h = f.andThen(g);  // g ∘ f

// 恒等射
Function<A, A> idA = Function.identity();