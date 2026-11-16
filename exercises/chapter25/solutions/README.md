# 第25章 統合プロジェクトの技術選定と実装 - 解答例

## 構成

```
solutions/
├── json-library-comparison.md   基礎課題1: GsonとJacksonの比較と設計判断記録
├── grade-csv/                   基礎課題2: 成績のCSVの読み込み（Apache Commons CSV）
│   ├── GradeCsvReader.java        Commons CSVを使う唯一のクラス
│   ├── GradeRow.java              学生IDと成績の組（Record）
│   ├── GradeCsvError.java         読めなかった行（Record）
│   ├── GradeCsvResult.java        読み込みの結果（Record）
│   ├── GradeCsvCheck.java         動作を確かめるプログラム
│   └── Grade.java                 サンプルコード24-2と同じ
├── h2-repository/               発展課題1: H2に保存するリポジトリ
│   ├── H2StudentRepository.java   サンプルコード25-1をH2に合わせて直した
│   ├── H2RepositoryCheck.java     動作を確かめるプログラム
│   └── （Student・StudentServiceなどはサンプルコード25-1と同じ）
└── challenge-guide.md           チャレンジ課題1: 解答の方針（コードはなし）
```

実行のコマンドは、`exercises/chapter25/README.md`にあります。

## 基礎課題2のポイント

- `CSVFormat`の`setHeader()`と`setSkipHeaderRecord(true)`で、1行目を見出しとして読み、列の名前で値を取り出す
- Commons CSVはBOMを取り除かないので、commons-ioの`BOMInputStream`を通してから読む（第25章の「試作で確かめる」）
- 見出しがそろっていないのはファイル全体の誤りなので`IOException`にし、1行ごとの誤りは結果に集める。第24章の「1件の失敗で全体を止めない」と同じ考え方
- 評価点の範囲の検査は`Grade`のコンストラクターに任せ、CSVを読むクラスで同じ検査を繰り返さない
- `record.getRecordNumber() + 1`は、見出しを1行目とした行の番号。引用符の中に改行を含む項目があると、ファイル上の行番号とはずれる

## 発展課題1のポイント

- SQLiteとの違いは`H2StudentRepository`の中だけに現れ、`StudentService`と`Student`は1行も変わらない
- 変えたのは、UPSERTの書き方（`MERGE INTO ... KEY`）、登録した順の持ち方（`seq`列）、外部キーの設定（`PRAGMA`をなくした）、列の型の4点
- `MERGE`で既存の行を更新するとき、`seq`列は書き換えないので、登録した順は保たれる
