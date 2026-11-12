# 第14章 発展課題

## 学習目標
- `DataOutputStream`・`DataInputStream`で、数値や文字列をバイナリ形式で読み書きする
- 書き込んだときと同じ順序・同じ型で読む必要があることを、ファイルの中身（バイト列）で確かめる
- 形式の違うファイルや途中で切れたファイルを、例外で見分ける（`EOFException`、`NoSuchFileException`）
- 文字エンコーディングを指定して読み書きし、Shift_JIS系（Windows-31J）のファイルをUTF-8に変換する
- 文字化けがどのように起こるか、エンコーディングを誤ったときに何が起こるかを確かめる

## 課題一覧

### 課題1: ゲームのセーブデータをバイナリで保存する
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task1-savedata/
├── SaveData.java      セーブデータの中身（レコード）
├── SaveDataFile.java  バイナリ形式での保存と読み込み
└── SaveDataApp.java   mainメソッド
```

ゲームのセーブデータ（名前・レベル・所持金・持ち物）を、決めた形式のバイナリファイルに保存し、読み込むプログラムを作成してください。

**要求仕様**:
- ファイルの形式
    - 先頭に識別子（`int`の`0x53415645`。"SAVE"の4文字の文字コード）と版（`int`の1）を置く
    - 続けて名前（`writeUTF`）、レベル（`int`）、所持金（`int`）、持ち物の数（`int`）、持ち物（`writeUTF`を数だけ）を書く
- `SaveDataFile`
    - `save(パス, データ)`は、`DataOutputStream`を`BufferedOutputStream`に重ねて書く
    - `load(パス)`は、書いたときと同じ順序で読む。識別子や版が違えば、理由を書いた`IOException`を投げる
    - 読み終わる前にファイルが終わったとき（`EOFException`）は、「途中で切れています」という`IOException`に包み直す
    - 比べるために、同じ内容をテキスト（UTF-8）で保存する`saveAsText`も用意する
- `SaveDataApp`
    - 作業用の`savedata`ディレクトリを作り、その中にファイルを作る
    - バイナリとテキストのファイルの大きさを表示する
    - バイナリファイルの先頭16バイトを16進数で表示する
    - 読み込んだデータが保存したデータと等しいことを確かめる
    - テキストファイル、末尾を5バイト削ったファイル、存在しないファイルを読ませ、それぞれの理由を表示する

**実行例**:
```
=== 保存 ===
slot1.dat: 88バイト
slot1.txt: 100バイト

=== slot1.dat の先頭16バイト ===
53 41 56 45 00 00 00 01  ← 識別子 "SAVE" と 版 1
00 0F E5 8B 87 E8 80 85  ← 名前の長さ（2バイト）と名前のUTF-8

=== 読み込み ===
名前: 勇者アキラ
レベル: 12、所持金: 3450G
持ち物: [やくそう, どうのつるぎ, かわのたて]
保存した内容と同じ: true

=== 壊れたファイルの検出 ===
読み込めません: slot1.txt はセーブデータではありません（先頭が 706C6179）
読み込めません: broken.dat は途中で切れています（原因: EOFException）
読み込めません: slot9.dat がありません
```

`int`は値の大きさにかかわらず4バイトで、上位のバイトから順に書かれます（ビッグエンディアン）。
版の1が`00 00 00 01`になっているのはこのためです。
`writeUTF`は、文字列のバイト数を2バイトで書いてから中身を書きます。「勇者アキラ」はUTF-8で15バイト（`00 0F`）です。
テキストファイルの先頭の4バイトは「play」の文字コード（`70 6C 61 79`）なので、識別子と一致しません。

### 課題2: 名簿ファイルの文字エンコーディングを変換する
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task2-encoding/
├── EncodingConverter.java  UTF-8として読めるかの確認と、UTF-8への変換
└── EncodingApp.java        mainメソッド
```

古いシステムが書き出したShift_JIS系（Windows-31J）の名簿ファイルを、UTF-8に変換するプログラムを作成してください。
変換元のファイルは、プログラムが最初に作ります。

**要求仕様**:
- `EncodingConverter`
    - `isUtf8(パス)`は、`Files.readString(パス, UTF_8)`で読めればtrue、`MalformedInputException`になればfalseを返す
    - `convertToUtf8(元, 元の文字セット, 先)`は、`Files.newBufferedReader`（元の文字セット）と`Files.newBufferedWriter`（UTF-8）で1行ずつ読み書きし、行数を返す。改行はLF（`\n`）にそろえる
- `EncodingApp`
    - 作業用の`encoding-work`ディレクトリに、Windows-31Jで名簿（見出しと3人分）を書き出す
    - そのファイルを`Files.readAllLines`でUTF-8として読み、例外になることを示す
    - 同じバイト列を`new String(バイト列, UTF_8)`で文字列にすると、例外にならず置換文字（U+FFFD）ができることを示す
    - 元のファイルと変換後のファイルについて、UTF-8でなければ変換し、UTF-8ならそのままにする
    - 2つのファイルの大きさと変換後の内容を表示し、元の内容と同じことを確かめる

**実行例**:
```
=== UTF-8として読んでみる ===
読めません: MalformedInputException（Input length = 1）
new Stringで変換すると、置換文字（U+FFFD）が43個できます

=== エンコーディングを調べて変換する ===
meibo-sjis.csv: Windows-31Jとして読み、meibo-utf8.csv に4行書き出しました
meibo-utf8.csv: すでにUTF-8なので変換しません

=== ファイルの大きさ ===
meibo-sjis.csv: 98バイト（漢字1文字が2バイト）
meibo-utf8.csv: 135バイト（漢字1文字が3バイト）

=== 変換後の内容 ===
学籍番号,氏名,学科
S001,山田太郎,情報工学科
S002,佐藤花子,電気電子工学科
S003,鈴木一郎,機械工学科
元の内容と同じ: true
```

## コンパイルと実行

```bash
cd task1-savedata
javac *.java
java SaveDataApp

cd ../task2-encoding
javac *.java
java EncodingApp
```

実行したディレクトリに、作業用のディレクトリ（`savedata`、`encoding-work`）ができます。何度実行しても同じ結果になります。

## 実装のヒント

```java
// 書き込み: ストリームを重ね、外側だけをtry-with-resourcesで宣言する
try (DataOutputStream out = new DataOutputStream(
        new BufferedOutputStream(Files.newOutputStream(path)))) {
    out.writeInt(MAGIC);
    out.writeUTF(data.playerName());
}

// 読み込み: 途中で終わったら、理由のわかる例外に包み直す
try (DataInputStream in = new DataInputStream(
        new BufferedInputStream(Files.newInputStream(path)))) {
    int magic = in.readInt();
    // ...
} catch (EOFException e) {
    throw new IOException(path.getFileName() + " は途中で切れています", e);
}
```

- `EOFException`と`NoSuchFileException`は`IOException`の子クラスなので、catchでは`IOException`より前に書く
- バイト列の一部を取り出すには`Arrays.copyOf(配列, 長さ)`、バイトを16進数2桁にするには`"%02X".formatted(b)`を使う
- `Files.readAllLines`や`Files.readString`は、指定した文字セットで表せないバイトがあると`MalformedInputException`（`IOException`の子クラス）を投げる。`new String(バイト列, 文字セット)`は例外を投げず、表せない部分を置換文字にする
- 英数字だけのファイルは、Windows-31JとしてもUTF-8としても同じバイト列になるので、`isUtf8`はtrueを返す。日本語を含むファイルで確かめる
- `BufferedWriter`の`newLine()`はOSの改行（Windowsでは`\r\n`）を書く。ファイルの大きさをOSによらずそろえたいときは`'\n'`を書く

## 応用のポイント

1. 形式を決めて読み書きする
    - バイナリファイルは、何をどの順に何バイトで書いたかがわからないと読めない。先頭の識別子と版は、自分の形式のファイルかを確かめるのに役立つ
2. 読めないファイルを見分ける
    - 形式の違い、途中で切れたファイル、存在しないファイルを、それぞれ別の理由として利用者に伝えられる
3. 文字エンコーディングを必ず指定する
    - 読み書きのたびに`StandardCharsets.UTF_8`などを明示すると、環境によって結果が変わらない

## 発展（任意）

- 課題1に「プレイ時間（`double`）」を加えて版を2にする。版1のファイルも読めるようにする（版によって読む項目を変える）
- 課題1で、保存するたびに前のファイルを`slot1.dat.bak`にコピーする（`Files.copy`）
- 課題2で、変換の前に元のファイルを`backup`ディレクトリにコピーしてから変換する

## 完了チェックリスト

- [ ] バイナリファイルを、書いたときと同じ順序・同じ型で読み込める
- [ ] 識別子・版の違い、途中で切れたファイル、存在しないファイルを区別して表示できる
- [ ] バイト列の表示から、`int`と`writeUTF`がどう書かれるかを説明できる
- [ ] 文字セットを指定して読み書きし、Windows-31JのファイルをUTF-8に変換できる
- [ ] `MalformedInputException`と置換文字の違いを説明できる

**次のステップ**: 発展課題が完了したら、challenge/のチャレンジ課題で、テキスト・直列化・ファイル操作をまとめて使ってみましょう。
