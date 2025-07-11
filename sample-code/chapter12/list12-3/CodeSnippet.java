/**
 * リスト12-3
 * コードスニペット
 * 
 * 元ファイル: chapter12-advanced-collections.md (165行目)
 */

// この時点ではまだフィルタリングは実行されない
Stream<Integer> stream = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);
        return n > 10;
    });

// collect()が呼ばれて初めてフィルタリングが実行される
List<Integer> result = stream.collect(Collectors.toList());