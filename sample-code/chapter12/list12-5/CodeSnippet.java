/**
 * リスト12-5
 * コードスニペット
 * 
 * 元ファイル: chapter12-advanced-collections.md (229行目)
 */

// 生徒のリストから、名前のリストを生成
List<String> names = list.stream()
    .map(Student::getName)
    .collect(Collectors.toList());