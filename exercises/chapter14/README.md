# 第14章 ファイルI/O - 演習課題

書籍『Java入門』第14章「ファイルI/O」の演習課題です。

## 基礎課題（[basic/](basic/)）

- テキストとバイナリの読み書き（BufferedReader・BufferedWriter、FileInputStream・FileOutputStream）
- フォルダーの操作とバックアップ（Files.createDirectory・list・copy・delete）
- CSVの読み込みと集計（壊れた行の読み飛ばし、書き出し）
- アクセスログの集計（行の分割、キーごとの件数）

## 発展課題（[advanced/](advanced/)）

- ゲームのセーブデータをバイナリで保存する（DataOutputStream・DataInputStream、ファイル形式の識別子と版、EOFExceptionによる破損の検出）
- 名簿ファイルの文字エンコーディングを変換する（Windows-31JからUTF-8へ、MalformedInputExceptionと文字化け）
- このフォルダーのJavaファイルは解答例です（課題ごとに`task1-savedata/`・`task2-encoding/`）

## チャレンジ課題（[challenge/](challenge/)）

- ToDoリストの保存と復元（タブ区切りのテキスト形式、壊れた行の読み飛ばし、Files.copyによるバックアップ、直列化とtransient、テキストブロックでのまとめの書き出し）
- このフォルダーのJavaファイルは解答例です

## 解答例（[solutions/](solutions/)）

- 基礎課題の課題1〜4の解答例（実行例はこの解答例の出力）
