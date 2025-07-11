/**
 * リスト10-14
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (383行目)
 */

// より簡潔な記述
for (String element : list) {
    System.out.println(element);
}

// ただし、走査中の削除はできない
// for (String element : list) {
//     if (element.equals("B")) {
//         list.remove(element);  // ConcurrentModificationException!
//     }
// }