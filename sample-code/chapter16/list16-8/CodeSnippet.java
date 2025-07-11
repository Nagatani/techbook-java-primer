/**
 * リスト16-8
 * コードスニペット
 * 
 * 元ファイル: chapter16-multithreading.md (622行目)
 */

// 誤った実装例（危険）
synchronized(lock) {
    if (!condition) {  // ①
        lock.wait();
    }
}

// 正しい実装例（推奨）
synchronized(lock) {
    while (!condition) {  // ②
        lock.wait();
    }
}