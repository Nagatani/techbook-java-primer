/**
 * リスト13-5
 * コードスニペット
 * 
 * 元ファイル: chapter13-lambda-and-functional-interfaces.md (193行目)
 */

// 悪い例：読みづらいネストされたラムダ
result = list.stream()
    .map(x -> {
        return transform(x, y -> {
            return process(y, z -> {
                return calculate(z);
            });
        });
    })
    .collect(Collectors.toList());

// 良い例：メソッド参照とヘルパーメソッドを使用
result = list.stream()
    .map(this::transformItem)
    .collect(Collectors.toList());

private Item transformItem(Item x) {
    return transform(x, this::processItem);
}