/**
 * リスト14-2
 * コードスニペット
 * 
 * 元ファイル: chapter14-exception-handling.md (158行目)
 */

// ...
try {
    // リソースを使う処理
} catch (Exception e) {
    // 例外発生時の処理
} finally {
    // 例外の有無にかかわらず、必ず実行される後片付け処理
    System.out.println("finallyブロックが実行されました。");
}