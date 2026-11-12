# 第23章 発展課題

## 学習目標
- 複数のクラスとリソースファイル（データ・設定）を1つの実行可能JARにまとめる
- `getResourceAsStream`で、JARの中のリソースをクラスパスから読む
- ファイルのパスで読む方法と、クラスパスからリソースとして読む方法の違いを理解する
- `jar tf`でJARの中身を確かめ、入れ忘れによるエラーを見つける
- ソース・リソース・クラスファイルのフォルダーを分けて、ビルドの手順を整理する

## 課題一覧

### 課題1: 時間割ビューアーの実行可能JAR
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task1-timetable/
├── src/
│   ├── Timetable.java      時間割（CSVの行から作る）
│   └── TimetableApp.java   mainメソッド（リソースを読んで表示する）
├── resources/
│   ├── timetable.csv       時間割のデータ（曜日,時限,科目名,教室）
│   └── app.properties      アプリケーション名・版・学期
└── manifest.txt            マニフェスト（Main-Class: TimetableApp）
```

大学の時間割を表示するコンソールアプリケーションを作り、時間割のデータと設定ファイルを含めた実行可能JARにしてください。
JARファイル1つを渡せば、受け取った人が`java -jar`だけで時間割を見られるようにします。

**要求仕様**:
- `Timetable`
    - 「曜日,時限,科目名,教室」のCSVの行から時間割（月〜金、1〜5限）を作る。空行と`#`で始まる行は読み飛ばす
    - 列の数が4でない行や、曜日・時限が正しくない行があれば、行番号を付けて`IllegalArgumentException`を投げる
    - 曜日を指定してその日の授業を返すメソッドと、1週間の授業のコマ数を返すメソッドを持つ
- `TimetableApp`
    - `timetable.csv`と`app.properties`を、ファイルのパスではなく`getResourceAsStream`でクラスパスから読む。文字コードはUTF-8を指定する
    - 引数がなければ月〜金のすべて、引数に曜日（`水`など）があればその日だけを表示する。月〜金以外ならエラーメッセージを出して終了する
    - リソースが見つからないときは、`NullPointerException`で落とさず、どのリソースがないかを表示して終了する
- JAR
    - `Main-Class`を書いたマニフェストで、2つのクラスファイルと2つのリソースを`timetable.jar`にまとめる
    - リソースはJARの直下に置く（`getResourceAsStream("/timetable.csv")`の`/`はクラスパスの直下を表す）

**実行例**:
```
$ java -jar timetable.jar
時間割ビューアー 1.0
2026年度 前期（授業 10コマ）
[月曜日]
  1限 線形代数(A201)
  2限 Javaプログラミング(情報演習室1)
  4限 英語コミュニケーション(B105)
[火曜日]
  2限 データ構造とアルゴリズム(A305)
  3限 統計学入門(A201)
[水曜日]
  1限 Javaプログラミング演習(情報演習室1)
  2限 Javaプログラミング演習(情報演習室1)
[木曜日]
  3限 情報倫理(大講義室)
  4限 英語コミュニケーション(B105)
[金曜日]
  1限 データベース(A305)

$ java -jar timetable.jar 水
時間割ビューアー 1.0
2026年度 前期（授業 10コマ）
[水曜日]
  1限 Javaプログラミング演習(情報演習室1)
  2限 Javaプログラミング演習(情報演習室1)

$ java -jar timetable.jar 土
曜日は月〜金で指定してください: 土
```

リソースを入れずにクラスファイルだけで実行すると、次のように表示して終了します。

```
エラー: リソース /app.properties が見つかりません（JARに入れ忘れていませんか）
```

## コンパイルと実行

```bash
cd task1-timetable

# 1. クラスファイルをclassesフォルダーに出力する
javac -d classes src/*.java

# 2. classesとresourcesの中身をJARの直下にまとめる
jar cvfm timetable.jar manifest.txt -C classes . -C resources .

# 3. JARの中身を確かめる
jar tf timetable.jar

# 4. 実行する
java -jar timetable.jar
java -jar timetable.jar 水
```

`jar`の`-C フォルダー .`は「そのフォルダーに移動して、中身をすべて入れる」という意味です。
第23章の例ではクラスファイルとリソースを1つずつ並べましたが、フォルダーを分けると、ファイルが増えてもコマンドは変わりません。
`jar tf`の結果は、次のようになります。

```
META-INF/
META-INF/MANIFEST.MF
Timetable.class
TimetableApp.class
app.properties
timetable.csv
```

`manifest.txt`は、`Main-Class: TimetableApp`の行の最後で改行しておきます（改行がないと、その行が読み込まれません）。

## 実装のヒント

```java
// クラスパスからリソースを開く。見つからなければnullが返る
InputStream in = TimetableApp.class.getResourceAsStream("/timetable.csv");
if (in == null) {
    throw new IOException("リソース /timetable.csv が見つかりません");
}
// 文字コードを指定して読む（指定しないと、OSによって文字化けすることがある）
BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
```

- `Files.readAllLines(Path.of("resources/timetable.csv"))`のようにファイルのパスで読むと、開発中は動いても、JARにしたあと別のフォルダーで実行すると読めない。パスは「実行したときのカレントディレクトリ」から探されるためである
- `getResourceAsStream`はクラスパス（JARの中やclassesフォルダー）から探すので、どこで実行しても同じように読める
- `Properties.load`に`InputStream`を渡すと、ISO 8859-1として読まれ日本語が化ける。`InputStreamReader`でUTF-8を指定した`Reader`を渡す
- 読み込みは`try-with-resources`で閉じる（第13章）
- 時間割の解析は`Timetable`に分け、リソースを読む処理と分けておくと、文字列のリストを渡すだけで解析を確かめられる

## 応用のポイント

1. データをプログラムから分ける
    - 時間割を変えるときは`timetable.csv`だけを直してJARを作り直せばよく、Javaのコードは変えなくてよい
2. 入れ忘れはすぐにわかるように
    - リソースが見つからないときに、どのファイルがないかを表示しておくと、JARの作り方の誤りにすぐ気付ける
3. フォルダーを分ける
    - `src`（ソース）、`resources`（リソース）、`classes`（生成物）を分けると、生成物だけを消して作り直すのも簡単になる。第22章で学んだMavenの`src/main/java`・`src/main/resources`・`target`も同じ考え方である

## 発展（任意）

- `app.properties`の`app.version`ではなく、マニフェストに`Implementation-Version: 1.0`を書き、プログラムから読み出す方法を調べる（クラスをパッケージに入れる必要がある）
- 利用者が自分の時間割を使えるように、引数`--file パス`でJARの外のCSVを指定できるようにする。指定がなければJARの中の`timetable.csv`を使う
- 第22章で学んだMavenのプロジェクトにし、`src/main/resources`に置いたファイルが`mvn package`でJARに入ることを確かめる

## 完了チェックリスト

- [ ] `java -jar timetable.jar`で、どのフォルダーから実行しても時間割が表示される
- [ ] `jar tf`で、2つのクラスファイルと2つのリソースが入っていることを確かめた
- [ ] リソースを`getResourceAsStream`で読み、文字コードにUTF-8を指定している
- [ ] リソースが見つからないとき、`NullPointerException`ではなくわかりやすいメッセージが出る
- [ ] CSVを書き換えてJARを作り直すと、表示が変わる

**次のステップ**: 発展課題が完了したら、チャレンジ課題に挑戦しましょう。
