# 第13章 例外処理 - 演習課題

書籍『Java入門』第13章「例外処理」の演習課題です。

## 基礎課題（[basic/](basic/)）

- 基本的な例外処理（try-catch-finally、複数のcatch、投げ直し）
- 銀行口座の独自例外（例外の階層、検査例外と非検査例外、例外の連鎖）
- try-with-resourcesと後始末（AutoCloseable、閉じる順序）
- 失敗の知らせ方を使い分ける（例外、検証結果のまとめ、Optional）

## 発展課題（[advanced/](advanced/)）

- 図書館の貸出と例外の階層（独自例外の親子関係、検査例外と非検査例外の使い分け、catchの順序、マルチキャッチ）
- ゲームの設定の読み込みと例外の連鎖（行番号を持つ独自例外、原因の例外を包み直す、getCauseでたどる）
- このフォルダーのJavaファイルは解答例です（課題ごとに`task1-library/`・`task2-config/`）

## チャレンジ課題（[challenge/](challenge/)）

- 注文データの一括処理（例外の階層と連鎖、AutoCloseableとtry-with-resources、リトライ、状態の巻き戻し、抑制された例外、ログ）
- このフォルダーのJavaファイルは解答例です

## 解答例（[solutions/](solutions/)）

- `CustomException.java`
- `CustomExceptionTest.java`
- `ExceptionChain.java`
- `ExceptionChainTest.java`
- `ResourceManager.java`
- `ResourceManagerTest.java`
- `ValidationHelper.java`
- `ValidationHelperTest.java`
