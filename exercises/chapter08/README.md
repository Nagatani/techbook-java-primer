# 第8章 Javaの特殊なクラス形式 - 演習課題

本章の演習は、扱う題材ごとに2つのディレクトリに分かれています。
それぞれに基礎課題（basic/）、発展課題（advanced/）、チャレンジ課題（challenge/）、解答例（solutions/）があります。

| ディレクトリ | 題材 |
|---|---|
| [records/](records/) | レコード（Record） |
| [enums/](enums/) | 列挙型（Enum） |

## 列挙型（[enums/](enums/)）

### 基礎課題（[enums/basic/](enums/basic/)）

- 曜日と優先度（フィールドとメソッドを持つ列挙型、values・valueOf・ordinal・compareTo、switch）
- 注文の状態（switch式での状態の移り方、EnumSet）
- 四則演算の列挙型（列挙子ごとの抽象メソッドの実装）
- 雛形はなく、ファイルを新しく作る。解答例は[enums/solutions/](enums/solutions/)

### 発展課題（[enums/advanced/](enums/advanced/)）

- コーヒーショップの注文（フィールドとメソッドを持つ列挙型、列挙子ごとの抽象メソッドの実装、EnumSet・EnumMap）
- このフォルダーのJavaファイルは解答例です

### チャレンジ課題（[enums/challenge/](enums/challenge/)）

- 大学の時間割（複数の列挙型の組み合わせ、ordinal()とvalues()、EnumMap・EnumSet、列挙型のswitch、静的内部クラス）
- このフォルダーのJavaファイルは解答例です

## レコード（[records/](records/)）

### 基礎課題（[records/basic/](records/basic/)）

- 点・人・DTO・入れ子のレコードとビルダー（コンパクトコンストラクターでの検査、静的ファクトリメソッド）
- このフォルダーのJavaファイルは、`TODO`を埋めて完成させる雛形です。解答例は[records/solutions/](records/solutions/)

### 発展課題（[records/advanced/](records/advanced/)）

- ショッピングカート（値オブジェクト、コンパクトコンストラクターによる検証と整形、静的ファクトリメソッド、withメソッド、ローカルレコード）
- このフォルダーのJavaファイルは解答例です

### チャレンジ課題（[records/challenge/](records/challenge/)）

- 銀行口座の通帳（sealedインターフェイスとレコード、レコードパターンを使ったswitch、列挙型との組み合わせ、入れ子のレコード）
- このフォルダーのJavaファイルは解答例です

内部クラスだけを扱う演習は準備中です（静的内部クラスと入れ子のレコードは、上の2つのチャレンジ課題で使います）。
