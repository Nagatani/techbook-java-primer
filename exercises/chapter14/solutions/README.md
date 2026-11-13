# 第14章 ファイル入出力 - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題のREADMEの実行例は、この解答例を動かした出力です。
解答例は、実行したフォルダーの下に作業用のフォルダー（`io-basic-work/`・`folder-work/`・`csv-work/`・`log-work/`）を作ります。試し終えたら消してかまいません。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: テキストとバイナリの読み書き | `BasicFileIO.java` | `BufferedWriter`・`BufferedReader`、`readAllLines`、バイトストリームでのコピー |
| 課題2: フォルダーの操作とバックアップ | `FolderOperations.java` | `createDirectory`・`list`・`copy`・`delete`、名前順に並べてから表示する |
| 課題3: CSVの読み込みと集計 | `CsvProcessor.java` | CSVの書き込みと読み込み、項目の変換と誤った行の扱い、集計 |
| 課題4: アクセスログの集計 | `LogAnalyzer.java` | ログの行の分解、種類ごとの件数、結果のファイルへの書き出し |

## 実行方法

```bash
cd exercises/chapter14/solutions
javac *.java
java BasicFileIO
java FolderOperations
java CsvProcessor
java LogAnalyzer
```

## 実装のポイント

- ファイルを開くときは、try-with-resourcesで閉じ忘れを防ぐ。`Files.list`が返すStreamも閉じる必要がある
- `Files.list`の順序はOSによって変わるので、`sorted()`で名前順に並べてから表示する
- 文字コードは`StandardCharsets.UTF_8`と明示する。指定しないと、OSの既定の文字コードになることがある
- 入出力の失敗は検査例外の`IOException`で知らせられるので、`catch`して`System.err`にメッセージを出す
