/**
 * リスト16-5
 * コードスニペット
 * 
 * 元ファイル: chapter16-multithreading.md (449行目)
 */

// アンチパターン：スレッドの無制限生成
for (int i = 0; i < 10000; i++) {
    new Thread(() -> processTask()).start();  // ①
}