/**
 * リスト12-7
 * コードスニペット
 * 
 * 元ファイル: chapter12-advanced-collections.md (278行目)
 */

// シーケンシャル（直列）処理
long sum = numbers.stream()
    .filter(n -> n % 2 == 0)
    .mapToLong(n -> n * n)
    .sum();

// 並列処理
long sumParallel = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .mapToLong(n -> n * n)
    .sum();