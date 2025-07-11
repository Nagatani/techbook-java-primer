/**
 * リスト10-8
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (223行目)
 */

List<String> list = new ArrayList<>();

// 追加
list.add("Java");
list.add(0, "Hello");  // 指定位置に挿入

// 取得
String first = list.get(0);
int size = list.size();

// 削除
list.remove("Java");
list.remove(0);  // インデックスで削除

// 検索
boolean contains = list.contains("Hello");
int index = list.indexOf("Java");

// 繰り返し処理
for (String item : list) {
    System.out.println(item);
}