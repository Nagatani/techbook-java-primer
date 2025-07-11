/**
 * リスト10-7
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (203行目)
 */

// ArrayList: 内部的に配列を使用
List<String> arrayList = new ArrayList<>();
arrayList.add("要素1");
arrayList.add("要素2");
String element = arrayList.get(0);  // 高速なランダムアクセス

// LinkedList: 双方向連結リストを使用
List<String> linkedList = new LinkedList<>();
linkedList.add("要素1");
linkedList.add(0, "先頭に挿入");  // 先頭への挿入が高速