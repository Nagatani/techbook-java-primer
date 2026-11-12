# 第23章 チャレンジ課題

## 学習目標
- Swingのアプリケーションを、リソースを含む実行可能JARにまとめる
- `jpackage`で、Javaランタイムを同梱したアプリケーション（app-image）を作る
- 自分のOS向けのインストーラー（`.dmg`・`.exe`/`.msi`・`.deb`/`.rpm`）を作る
- `jdeps`でアプリケーションが使うモジュールを調べ、同梱するランタイムを小さくする
- インストールしたアプリケーションのデータを、インストール先ではなく利用者のホームディレクトリに保存する

## 課題一覧

### 課題1: 単語帳アプリを配布できる形にする
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── src/
│   ├── WordCard.java        単語カード（レコード）
│   ├── WordDeck.java        単語カードの束（覚えた単語の記録と保存）
│   ├── FlashcardApp.java    Swingの画面とmainメソッド
│   └── WordDeckCheck.java   画面を開かずにWordDeckを確かめるmainメソッド
├── resources/
│   └── words.csv            単語のデータ（単語,意味）
└── manifest.txt             マニフェスト（Main-Class: FlashcardApp）
```

英単語を1枚ずつ表示する単語帳アプリを作り、Javaをインストールしていない友人のPCでも動く形にして配布してください。
最終的な成果物は、自分のOS向けのインストーラー（またはapp-image）です。

**要求仕様**:
- アプリケーション
    - 単語のデータ`words.csv`は、JARの中のリソースとして`getResourceAsStream`で読む
    - 画面に単語を大きく表示し、「意味を見る」「覚えた」「次へ」「最初から」のボタンを置く
    - 「覚えた」単語は次から出さない。すべて覚えたら「すべて覚えました」と表示する
    - 覚えた単語は`ユーザーのホームディレクトリ/.flashcard-app/known.txt`に保存し、次に起動したときに読み込む
    - 単語の束の処理（`WordDeck`）は画面のクラスと分け、`WordDeckCheck`で画面を開かずに確かめられるようにする
- 配布
    - 実行可能JAR`Flashcard.jar`を作り、`input`フォルダーに置く
    - `jpackage --type app-image`でapp-imageを作り、Javaのない環境でも起動できる形にする
    - `jdeps --print-module-deps`で使っているモジュールを調べ、`--add-modules`で同梱するモジュールを絞る。絞る前と後でapp-imageの大きさを比べる
    - 自分のOS向けのインストーラーを作り、インストールして起動できることを確かめる

**実行例**:

`FlashcardApp`を起動すると、「単語帳」という題のウィンドウが開き、中央に`assignment`のような英単語が大きく表示されます。
「意味を見る」を押すと単語の下に「課題、宿題」と意味が表示され、「次へ」で次の単語に進みます。
「覚えた」を押すとその単語は次から表示されず、ウィンドウ下端の「覚えた単語: 1 / 10」の数が増えます。
アプリを閉じて起動し直しても、覚えた単語の数は保存されたままです。
10個すべてを覚えると「すべて覚えました」と表示され、「最初から」以外のボタンが押せなくなります。

`WordDeckCheck`の実行結果は次のとおりです。

```
単語の数: 3
1枚目: apple = りんご
2枚目: borrow
3枚目: deadline
4枚目（覚えた単語は飛ばす）: borrow
保存して読み直した覚えた単語の数: 1
```

`jdeps`の結果と、macOSでapp-imageを作ったときの大きさの例です。
大きさはOSやJDKの版によって変わります。

```
$ jdeps --print-module-deps input/Flashcard.jar
java.base,java.desktop

$ du -sh output-full/Flashcard.app output/Flashcard.app
135M    output-full/Flashcard.app
 79M    output/Flashcard.app
```

## コンパイルと実行

### 1. コンパイルと実行可能JARの作成

```bash
javac -d classes src/*.java

# 画面を開かずにWordDeckを確かめる
java -cp classes WordDeckCheck

# classesとresourcesの中身をまとめて、inputフォルダーにJARを作る
mkdir input
jar cvfm input/Flashcard.jar manifest.txt -C classes . -C resources .
java -jar input/Flashcard.jar
```

### 2. 使っているモジュールを調べる

```bash
jdeps --print-module-deps input/Flashcard.jar
```

### 3. app-imageを作る

```bash
# モジュールを絞らない場合（比較用）
jpackage --type app-image --name Flashcard --input input --main-jar Flashcard.jar --dest output-full

# jdepsで調べたモジュールだけを同梱する場合
jpackage --type app-image --name Flashcard --input input --main-jar Flashcard.jar \
         --add-modules java.desktop --dest output
```

`--add-modules`には、`java.desktop`が使う`java.base`も自動で加わります。
作ったapp-imageは、macOSでは`output/Flashcard.app`、Windowsでは`output\Flashcard\Flashcard.exe`、Linuxでは`output/Flashcard/bin/Flashcard`から起動します。

### 4. インストーラーを作る

第23章の「各OSに向けた基本的なパッケージング例」と同じように、`--type`を自分のOSに合わせて変えます。

```bash
# macOS
jpackage --type dmg --name Flashcard --input input --main-jar Flashcard.jar \
         --add-modules java.desktop --app-version 1.0 --dest installer

# Windows（WiX Toolsetが必要）
jpackage --type msi --name Flashcard --input input --main-jar Flashcard.jar \
         --add-modules java.desktop --app-version 1.0 --win-shortcut --win-menu --dest installer

# Linux（Debian系。fakerootなどが必要）
jpackage --type deb --name flashcard --input input --main-jar Flashcard.jar \
         --add-modules java.desktop --app-version 1.0 --linux-shortcut --dest installer
```

`jpackage`は、実行しているOS向けのものしか作れません。
Windows向けのインストーラーはWindowsで、macOS向けはmacOSで作ります。
`input`フォルダーにはJARだけを置きます。
`input`と`output`を同じフォルダーにすると、作ったアプリケーションが次の`input`に含まれてしまいます。

## 実装のヒント

```java
// インストール先ではなく、利用者のホームディレクトリに保存する
static final Path SAVE_FILE =
    Path.of(System.getProperty("user.home"), ".flashcard-app", "known.txt");

// 保存先のフォルダーがなければ作ってから書き込む
Files.createDirectories(file.getParent());
Files.write(file, known, StandardCharsets.UTF_8);
```

- インストールしたアプリケーションのフォルダー（Windowsの`Program Files`、macOSの`/Applications`など）は、ふつうの利用者には書き込めないことが多い。保存するデータはホームディレクトリの下に置く
- 単語のデータは書き換えないので、JARの中のリソースにしてよい。書き換えるデータ（覚えた単語）と分けて考える
- `getResourceAsStream`が`null`を返したときは、メッセージを表示して終了する。JARにリソースを入れ忘れたことにすぐ気付ける
- ボタンの処理はEDTで動く（第18章）。保存は小さなファイルを書くだけなので、ここではEDTで行ってよい
- `jdeps`の結果に出たモジュールを`--add-modules`に書く。足りないモジュールがあると、起動したときに`NoClassDefFoundError`になる
- app-imageの起動で問題が起きたら、`jpackage`に`--win-console`（Windows）を付けるか、macOSなら`output/Flashcard.app/Contents/MacOS/Flashcard`をターミナルから起動すると、エラーメッセージを確かめられる

## 応用のポイント

1. JARとjpackageの違い
    - JARは小さいが、受け取った人がJavaをインストールしている必要がある。jpackageで作ったアプリケーションは大きいが、Javaのない環境でもそのまま動く
2. 同梱するランタイムを小さくする
    - `jpackage`は内部で`jlink`を使ってランタイムを作る。必要なモジュールだけを選ぶと、配布する大きさを大きく減らせる
3. データの置き場所
    - 変わらないデータはリソースに、利用者ごとに変わるデータはホームディレクトリに置く。これはJARでもインストーラーでも同じである

## 発展（任意）

- `--icon`でアプリケーションのアイコンを指定する。形式はOSごとに違う（macOSは`.icns`、Windowsは`.ico`、Linuxは`.png`）
- 単語のデータを複数用意し（`words-basic.csv`、`words-advanced.csv`など）、メニューで切り替えられるようにする
- `jlink --add-modules java.desktop --output runtime`で自分でランタイムを作り、`jpackage --runtime-image runtime`で使う。`jpackage`が内部で行っていることを確かめる
- 第21章で学んだJUnitで`WordDeck`のテストを書く

## 完了チェックリスト

- [ ] `java -jar input/Flashcard.jar`で単語帳が起動し、単語のデータがJARの中から読まれている
- [ ] 覚えた単語が、アプリを閉じて起動し直しても保存されている
- [ ] `jdeps`で調べたモジュールだけを同梱したapp-imageが起動する
- [ ] モジュールを絞る前と後で、app-imageの大きさを比べた
- [ ] 自分のOS向けのインストーラーでインストールし、起動できた

**次のステップ**: これで本書の演習はすべて終わりです。付録G「高度なビルドとデプロイメント技術」で、Maven・Gradleによるビルドの自動化やFat JARの作成に進みましょう。
