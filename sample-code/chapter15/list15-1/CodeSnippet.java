/**
 * リスト15-1
 * コードスニペット
 * 
 * 元ファイル: chapter15-file-io.md (78行目)
 */

// try()の括弧内でリソースを初期化する
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {
    // ... ファイル読み込み処理 ...
} catch (IOException e) {
    // ... エラー処理 ...
}
// tryブロックを抜ける際にbrが自動的にクローズされる