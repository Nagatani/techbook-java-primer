/**
 * リスト16-6
 * コードスニペット
 * 
 * 元ファイル: chapter16-multithreading.md (473行目)
 */

// 危険：通常のHashMapの並行アクセス
Map<String, Integer> unsafeMap = new HashMap<>();  // ①

// 安全：ConcurrentHashMapの使用
Map<String, Integer> safeMap = new ConcurrentHashMap<>();  // ②