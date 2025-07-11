/**
 * リストAB-12
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (347行目)
 */

// 結合法則の検証
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = x -> x * x;

Function<Integer, Integer> composition1 = f.andThen(g).andThen(h);
Function<Integer, Integer> composition2 = f.andThen(g.andThen(h));

// 両者は同じ結果を生成
System.out.println(composition1.apply(3)); // ((3 + 1) * 2)² = 64
System.out.println(composition2.apply(3)); // ((3 + 1) * 2)² = 64

// 単位法則の検証
Function<Integer, Integer> identity = Function.identity();
Function<Integer, Integer> fWithId1 = f.andThen(identity);
Function<Integer, Integer> fWithId2 = identity.andThen(f);

System.out.println(f.apply(5));        // 6
System.out.println(fWithId1.apply(5)); // 6
System.out.println(fWithId2.apply(5)); // 6