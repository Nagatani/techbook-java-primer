/**
 * リストAB-4
 * コードスニペット
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (81行目)
 */

// 関数型スタイル：副作用のない関数の組み合わせ
IntStream.rangeClosed(1, 10)
    .reduce(0, Integer::sum);