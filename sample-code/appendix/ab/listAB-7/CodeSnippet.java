/**
 * リストAB-7
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (161行目)
 */

// ラムダ計算の基本形式をJavaで表現

// 恒等関数：λx.x
Function<Object, Object> identity = x -> x;

// 定数関数：λx.λy.x
Function<Object, Function<Object, Object>> constant = x -> y -> x;

// 関数適用：(λx.x + 1) 5
Function<Integer, Integer> increment = x -> x + 1;
int result = increment.apply(5); // 6