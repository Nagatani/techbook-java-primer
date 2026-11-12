# 第14章 ファイルI/O - 演習課題

書籍『Java入門』第14章「ファイルI/O」の演習課題です。

## 基礎課題（[basic/](basic/)）

- 基本的なファイル読み書き
- ディレクトリ操作とファイル検索
- CSVデータ処理システム
- ログファイル解析システム

## 発展課題（[advanced/](advanced/)）

- ゲームのセーブデータをバイナリで保存する（DataOutputStream・DataInputStream、ファイル形式の識別子と版、EOFExceptionによる破損の検出）
- 名簿ファイルの文字エンコーディングを変換する（Windows-31JからUTF-8へ、MalformedInputExceptionと文字化け）
- このフォルダーのJavaファイルは解答例です（課題ごとに`task1-savedata/`・`task2-encoding/`）

## チャレンジ課題（[challenge/](challenge/)）

- ToDoリストの保存と復元（タブ区切りのテキスト形式、壊れた行の読み飛ばし、Files.copyによるバックアップ、直列化とtransient、テキストブロックでのまとめの書き出し）
- このフォルダーのJavaファイルは解答例です

## 解答例（[solutions/](solutions/)）

- `BackupSystem.java`
- `BackupSystemTest.java`
- `CSVHandler.java`
- `CSVHandlerTest.java`
- `FileProcessor.java`
- `FileProcessorTest.java`
- `WatchServiceExample.java`
- `WatchServiceExampleTest.java`
