/**
 * リスト16-7
 * コードスニペット
 * 
 * 元ファイル: chapter16-multithreading.md (604行目)
 */

// システム構成の概念図
// Producer → [BlockingQueue] → Consumer
//    ↓            ↓              ↓
// データ生成 → バッファリング → データ処理