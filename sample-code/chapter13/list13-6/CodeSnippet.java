/**
 * リスト13-6
 * コードスニペット
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (218行目)
 */

// 悪い例：ラムダ内で外部状態を変更
List<String> results = new ArrayList<>();
items.forEach(item -> {
    results.add(processItem(item));  // 副作用！
});

// 良い例：純粋な関数型アプローチ
List<String> results = items.stream()
    .map(this::processItem)
    .collect(Collectors.toList());