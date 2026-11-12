# 第23章 基礎課題 - ビルドとデプロイ

## 課題概要

`javac`・`jar`・`java -jar`の3つのコマンドで、作ったプログラムを1つのJARファイルにまとめて配る練習をします。

このフォルダーには解答例のコードを置いていません。解答例は[solutions/](../solutions/)にあり、課題ごとに対応する解答例を書いています。

## 課題リスト

### 課題1: 実行可能JARの作成

コマンドライン引数で計算する小さな計算機を、`java -jar`で実行できるJARにしてください。

**要件:**

- `java -jar calculator.jar add 10 20`のように、操作と2つの数を引数で受け取って結果を表示する
- 0で割ろうとしたら、エラーのメッセージを`System.err`に出し、終了コード1で終わる（`System.exit(1)`）
- マニフェストに`Main-Class`を書き、`jar cfm`でJARを作る
- `jar tf`でJARの中身を表示して、クラスファイルとマニフェストが入っていることを確かめる

解答例: `CalculatorApp.java`、`manifest.txt`、ビルドと動作確認をまとめて行う`build.sh`（Windowsは`build.bat`）。

**実行例**（解答例の`build.sh`の後半）:

```
4. JARファイルの情報:
----------------------------------------
META-INF/
META-INF/MANIFEST.MF
chapter23/solutions/CalculatorApp.class
----------------------------------------
ファイルサイズ: 2.8K

5. 動作テストの実行...
----------------------------------------
テスト1: 加算 (10 + 20)
10 + 20 = 30
✓ 加算テスト成功

テスト2: 乗算 (5.5 × 3.2)
5.5 × 3.2 = 17.6
✓ 乗算テスト成功

テスト3: 除算 (100 ÷ 3)
100 ÷ 3 = 33.3333333333
✓ 除算テスト成功

テスト4: ゼロ除算（エラーテスト）
エラー: ゼロで除算することはできません
✓ ゼロ除算エラー処理成功
```

マニフェストは、最後の行のあとにも改行が要ります。改行がないと、最後の行の属性は読み飛ばされます。

### 課題2: リソースを含むJAR

設定ファイルと、日本語・英語のメッセージを含むSwingアプリを、1つのJARにまとめてください。

**要件:**

- アプリの題名と言語を`config.properties`に書き、`getClass().getResourceAsStream("/resources/config.properties")`で読む
- 画面の文字は`messages_ja.properties`・`messages_en.properties`に書き、`ResourceBundle.getBundle`で言語に合わせて読む
- リソースのファイルをクラスファイルと一緒にJARへ入れ、`java -jar`で起動しても読めることを確かめる（ファイルのパスで読むと、JARの中のファイルは読めない）

解答例: `MessageApp.java`と`resources/`フォルダー。ビルドの手順は[solutions/README.md](../solutions/README.md)にあります。

**実行例**（解答例）: 起動すると、`config.properties`の題名と版がウィンドウのタイトルに出ます。言語を切り替えると、ボタンやラベルの文字が日本語と英語で入れ替わります。

### 課題3（発展）: 外部ライブラリを使うアプリのJAR

外部ライブラリ（JSONを扱うGson、コマンドライン引数を扱うCommons CLI）を使うアプリを、ライブラリごと1つのJAR（Fat JAR）にまとめてください。

**要件:**

- ライブラリのJARを`lib/`に置き、`javac -cp`でコンパイルする
- ライブラリのJARを展開してクラスファイルと一緒にまとめるか、マニフェストの`Class-Path`でライブラリのJARを指定する
- できたJARを、ライブラリのない別のフォルダーにコピーしても動くか（Fat JARの場合）を確かめる

Fat JARは本文ではまとめで名前を挙げるだけなので、詳しくは付録Gを読んでから取り組みます。
実際の開発では、MavenやGradle（第22章）のプラグインでFat JARを作ります。

解答例: `JsonTool.java`と、ライブラリの取得からFat JARの作成までを行う`build-fatjar.sh`（ライブラリをMaven Centralからダウンロードするため、ネットワークへの接続が要ります）。

## 実行方法

解答例はパッケージ`chapter23.solutions`にあるので、`solutions/`でビルドします。

```bash
cd ../solutions
bash build.sh
java -jar dist/calculator.jar divide 100 3
```

`build.sh`が作る`build/`と`dist/`は、試し終えたら消してかまいません。

## 注意事項

- `jar`コマンドに渡すクラスファイルのパスは、パッケージのフォルダー（`chapter23/solutions/`）から始まるようにする。`build/`の中に移動してから`jar`を実行するのはこのため
- `java -jar`で起動すると、`-cp`（クラスパス）の指定は無視され、マニフェストの`Class-Path`だけが使われる
