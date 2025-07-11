/**
 * リスト12-4
 * コードスニペット
 * 
 * 元ファイル: chapter12-advanced-collections.md (216行目)
 */

List<Student> list = ...;
// 点数が90点以上の生徒だけを抽出
List<Student> highScorers = list.stream()
    .filter(s -> s.getScore() >= 90)
    .collect(Collectors.toList());