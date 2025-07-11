/**
 * リストAB-9
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (225行目)
 */

// ベータ簡約の例
// (λx.x * x) 5 → 5 * 5 → 25

Function<Integer, Integer> square = x -> x * x;
int result = square.apply(5); // 25

// より複雑な例
// (λf.λx.f (f x)) (λy.y + 1) 5
// → (λx.(λy.y + 1) ((λy.y + 1) x)) 5
// → (λy.y + 1) ((λy.y + 1) 5)
// → (λy.y + 1) 6
// → 7

Function<Function<Integer, Integer>, Function<Integer, Integer>> twice = 
    f -> x -> f.apply(f.apply(x));

Function<Integer, Integer> increment = y -> y + 1;
int result2 = twice.apply(increment).apply(5); // 7