/**
 * リスト10-2
 * コードスニペット
 * 
 * 元ファイル: chapter10-collections.md (58行目)
 */

String[] students = new String[5];
// ... 5人分のデータを登録 ...
// 6人目を追加したい！
students[5] = "山田";  // ArrayIndexOutOfBoundsException!