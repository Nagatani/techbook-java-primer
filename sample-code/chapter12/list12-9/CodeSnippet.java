/**
 * リスト12-9
 * コードスニペット
 * 
 * 元ファイル: chapter12-advanced-collections.md (344行目)
 */

// スレッドセーフでない例（避けるべき）
List<Integer> results = new ArrayList<>();  // スレッドセーフでない
numbers.parallelStream()
    .forEach(n -> results.add(n * 2));  // データ競合の危険！

// スレッドセーフな代替方法
List<Integer> results = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());  // スレッドセーフ