/**
 * リストAB-15
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (544行目)
 */

// Streamモナドの例

// return: T → Stream<T>
Stream<Integer> single = Stream.of(42);

// flatMap: Stream<T> → (T → Stream<U>) → Stream<U>
Stream<String> words = Stream.of("Hello World", "Java Programming")
    .flatMap(line -> Arrays.stream(line.split(" ")));

// モナディックな計算の連鎖
List<Integer> result = Stream.of(1, 2, 3, 4, 5)
    .flatMap(x -> Stream.of(x, x * x))  // 各要素とその二乗
    .filter(x -> x % 2 == 0)            // 偶数のみ
    .map(x -> x * 10)                   // 10倍
    .collect(Collectors.toList());

System.out.println(result); // [20, 40, 160]

// モナド則の活用例
Function<String, Stream<Character>> toChars = 
    s -> s.chars().mapToObj(c -> (char) c);

Function<Character, Stream<String>> toAscii = 
    c -> Stream.of(String.valueOf((int) c));

// 関数の合成
Stream<String> asciiCodes = Stream.of("AB")
    .flatMap(toChars)
    .flatMap(toAscii);

asciiCodes.forEach(System.out::println); // 65, 66