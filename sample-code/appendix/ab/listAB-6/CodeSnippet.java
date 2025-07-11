/**
 * リストAB-6
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (126行目)
 */

// 関数の合成：(f ∘ g)(x) = f(g(x))
Function<Integer, Integer> addOne = x -> x + 1;
Function<Integer, Integer> multiplyByTwo = x -> x * 2;

// 合成関数の作成
Function<Integer, Integer> addOneThenMultiply = 
    addOne.andThen(multiplyByTwo);
    
// または
Function<Integer, Integer> multiplyThenAddOne = 
    multiplyByTwo.compose(addOne);

// 実行
System.out.println(addOneThenMultiply.apply(3));    // (3 + 1) * 2 = 8
System.out.println(multiplyThenAddOne.apply(3));    // (3 * 2) + 1 = 7