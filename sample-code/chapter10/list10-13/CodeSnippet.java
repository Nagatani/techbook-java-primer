/**
 * リスト10-13
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (360行目)
 */

List<String> list = Arrays.asList("A", "B", "C");

// イテレータを使った走査
Iterator<String> iter = list.iterator();
while (iter.hasNext()) {
    String element = iter.next();
    System.out.println(element);
}

// 走査中の安全な削除
Iterator<String> iter2 = list.iterator();
while (iter2.hasNext()) {
    String element = iter2.next();
    if (element.equals("B")) {
        iter2.remove();  // イテレータ経由で削除
    }
}