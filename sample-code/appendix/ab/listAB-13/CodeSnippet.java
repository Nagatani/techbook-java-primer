/**
 * リストAB-13
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (397行目)
 */

// Optionalモナドの実装例

// return (of): T → Optional<T>
Optional<Integer> returnExample = Optional.of(42);

// flatMap: Optional<T> → (T → Optional<U>) → Optional<U>
Optional<String> result = Optional.of(5)
    .flatMap(x -> Optional.of(x * 2))
    .flatMap(x -> Optional.of("Result: " + x));

// モナド則の検証

// 1. 左単位元則
Function<Integer, Optional<String>> f = x -> Optional.of("Value: " + x);
Optional<String> left1 = Optional.of(10).flatMap(f);
Optional<String> left2 = f.apply(10);
System.out.println(left1.equals(left2)); // true

// 2. 右単位元則
Optional<Integer> m = Optional.of(20);
Optional<Integer> right1 = m.flatMap(Optional::of);
Optional<Integer> right2 = m;
System.out.println(right1.equals(right2)); // true

// 3. 結合則
Function<Integer, Optional<Integer>> addOne = x -> Optional.of(x + 1);
Function<Integer, Optional<Integer>> multiplyTwo = x -> Optional.of(x * 2);

Optional<Integer> assoc1 = Optional.of(5)
    .flatMap(addOne)
    .flatMap(multiplyTwo);

Optional<Integer> assoc2 = Optional.of(5)
    .flatMap(x -> addOne.apply(x).flatMap(multiplyTwo));

System.out.println(assoc1.equals(assoc2)); // true