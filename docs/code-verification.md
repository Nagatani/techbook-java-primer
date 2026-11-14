# 公開コードの検証手順（Java 25）

本書がGitHubで公開するコード（`sample-code/`、`appendix/`、`exercises/`）は、Java 25でコンパイル・実行・テストできる状態を保ちます。
この文書は、その確認の手順と、これまでに見つかった誤りのパターンをまとめたものです（2026-11-10時点）。

## 対象と基準

- 対象
  - `sample-code/`：本文のサンプルの全文（320件）
  - `appendix/`：付録のコード（60のまとまり）
  - `exercises/`：演習の解答例とテスト（42ディレクトリ）
- 基準
  - `javac --release 25` でコンパイルでき、非推奨・削除予定のAPIを使わない（`-Xlint:deprecation,removal`で警告ゼロ）
  - mainを持つものは実行して例外で落ちない。GUIは仮想ディスプレイ（Xvfb）上で起動を確かめる
  - テストがあるものはすべて通す
  - READMEに書いたビルド・実行の手順（javac、Maven、Gradle、`jar`）を、そのとおりに実行して通ることを確かめる
  - 実行結果がOSや実行時刻で変わる書き方をしない（`Files.list()`の順序は`sorted()`で固定する、時刻は秒までに丸める など）

## 自動の検査

| コマンド | 内容 |
|---|---|
| `npm run check:samples -- --no-run` | `sample-code/`の抜粋の一致とコンパイル（CIで失敗扱い） |
| `npm run check:samples` | 上に加えて実行し、本文の「実行結果」と照合する（CIでは参考） |
| `npm run check:java` | 全文を公開していない番号付きコードブロックのコンパイル |
| `npm run check:public-code -- --no-test` | `appendix/`と`exercises/`のコードのコンパイル（CIで失敗扱い） |
| `npm run check:public-code` | 上に加えて、テストのあるまとまりをJUnitで実行する（CIでは参考） |

`check:samples`は非推奨・削除予定のAPIの使用を「非推奨・削除予定のAPIを使っている」として報告します。

`check:public-code`（`scripts/check-public-code.js`、2026-11-11〜）は、`package`宣言からソースのルートを求めてまとまりに分け（`src/main/java`と`src/test/java`はプロジェクト単位）、Java 25でコンパイルします。
JUnit・Mockito・JMH・SLF4J・Gson・Commonsは、スクリプトの`LIBS`に書いた版をMaven Centralから取得して、すべてのまとまりのクラスパスに加えます。
新しいライブラリを使うコードを足したら`LIBS`に加え、READMEの手順で特別なオプションが要るまとまりは`JAVAC_OPTIONS`に書きます。
`check:public-code exercises/chapter06`のように、パスの先頭で対象を絞れます。

この検査は、Maven・Gradleのビルド、READMEの手順どおりの実行、`main`の実行、GUIの起動までは確かめません。
それらにかかわる変更をしたら、次の手順で手動で確かめます。

## 手動の確認（Docker）

macOSのJDKの版に左右されないよう、Dockerのイメージで確認します。

- `eclipse-temurin:25-jdk`：javacとjavaでの確認、GUIの起動確認
- `maven:3-eclipse-temurin-25`：pom.xmlのあるプロジェクト
- `gradle:jdk25`（Gradle 9系）：build.gradleのあるプロジェクト

作業はリポジトリを直接マウントせず、スクラッチ用のディレクトリにコピーして行います。`target/`、`build/`、`dependency-reduced-pom.xml`などの生成物をリポジトリに残さないためです。リポジトリ内で実行した場合は、`mvn clean`や`gradle clean`で生成物を消してから`git status`で確認します。

### javacでのコンパイルとテスト

```bash
W=/path/to/scratch/work   # コピー先
cp -R exercises/chapter14/solutions "$W/src"
docker run --rm -v "$W":/w eclipse-temurin:25-jdk sh -c '
  cd /w && mkdir -p classes &&
  javac --release 25 -Xlint:deprecation,removal -encoding UTF-8 \
        -cp junit-console.jar -d classes $(find src -name "*.java")'
docker run --rm -v "$W":/w eclipse-temurin:25-jdk sh -c '
  cd /tmp && java -jar /w/junit-console.jar execute -cp /w/classes \
        --scan-classpath --disable-banner --details=summary'
```

`junit-console.jar`はMaven Centralの`org.junit.platform:junit-platform-console-standalone`（1.14系）です。Mockitoなどを使うテストは、そのJARもクラスパスに加えます。

### GUIの起動確認

```bash
docker run --rm -v "$W":/w eclipse-temurin:25-jdk sh -c '
  apt-get update -qq && apt-get install -y -qq xvfb libxrender1 libxtst6 libxi6 fontconfig fonts-noto-cjk >/dev/null 2>&1
  Xvfb :99 >/dev/null 2>&1 & export DISPLAY=:99; sleep 2
  cd /w && timeout 6 java -cp classes パッケージ名.MainClass; echo "exit=$?"'
```

`timeout`で打ち切ったときの終了コード124は「例外で落ちずに動き続けた」ことを意味します。標準エラー出力に例外が出ていないかもあわせて確かめます。

### Maven・Gradle

```bash
docker run --rm -v "$W/project":/w -v "$W/m2":/root/.m2 -w /w maven:3-eclipse-temurin-25 mvn -B verify
docker run --rm -v "$W/project":/w -w /w gradle:jdk25 gradle build --warning-mode all
```

Gradleは`--warning-mode all`で、Gradle 10で使えなくなる書き方の警告も出ないことを確かめます。

## Java 25で見つかった誤りのパターン

公開コードを直したときに繰り返し出てきたものです。新しくコードを書くときも避けます。

- 非推奨・削除予定のAPI
  - `new Locale(String)` → `Locale.of()`
  - `new URL(String)` → `URI.create(...).toURL()`
  - `Thread.getId()` → `Thread.threadId()`
  - `java.util.Observable` → `PropertyChangeSupport`
  - `JPasswordField.getText()` → `getPassword()`
  - `UIManager.getSystemLookAndFeelClassName()`を使う（存在しないLook and Feelの名前を書かない）
- 型と構文
  - ジェネリックメソッドの呼び出しをつなげると型推論が効かないことがある。型引数を明示する（例：`Comparator.<T>comparing(...)`）
  - `<U super T>`は書けない。`? super T`を使う
  - レコードパターンの中に定数は書けない。`when`のガードで比べる
  - スーパークラスから継承したメンバー型は、同じ名前のトップレベルのクラスより優先される（名前の衝突に注意する）
  - `import javax.swing.*`と`import java.util.*`を両方使うと`Timer`があいまいになる。`import javax.swing.Timer;`を明示する
  - クラス名をJUnitのアノテーション名（`ParameterizedTest`など）と同じにしない
- 入出力・並行処理
  - 標準入出力は`IO`クラス（`IO.println`・`IO.print`・`IO.readln`）で書く。`IO.println`の引数は`Object`なので、`char[]`は`new String(配列)`にして渡す
  - `IO.readln`は最初に使った`System.in`の読み取り器を持ち続ける。`System.setIn`で入力を差し替えても2回目以降は読めない（`null`が返る）ので、テストで入力を差し替えるコードには使わない
  - `IO`という名前のクラスを自作すると、そちらが`java.lang.IO`より優先される。その中で標準入出力を使うときは`java.lang.IO`と書く
  - 同じJVMの中で`FileLock`を重ねて取ると`OverlappingFileLockException`になる。スレッド間はJVMのモニター（`synchronized`など）で先に直列化する
  - `AsynchronousFileChannel`は`APPEND`を使えない
  - ソケットのチャットなどは`shutdownInput()`と`join()`で終了させ、デッドロックしないようにする
- ビルド
  - `jar cf`は`META-INF/MANIFEST.MF`を無視する。`Main-Class`を入れるには`jar cfm`か`jar cfe`を使う
  - Gradle 9
    - Shadowプラグインは`com.gradleup.shadow`（旧`com.github.johnrengelman.shadow`は動かない）
    - `testRuntimeOnly 'org.junit.platform:junit-platform-launcher'`の明示が必要
    - `doLast`の中の`javaexec`は使えない。`JavaExec`タスクを登録する
    - `artifacts { archives ... }`は非推奨。`assemble`の依存に加える
    - タスクの実行時に`project`を参照しない（値は設定時に取り出す）
  - Javadocでは、メソッドのコメント内の見出しは`<h4>`以下にする（`<h2>`や`<h3>`はエラーになる）
  - Mockito 4系とJaCoCo 0.8.13以前はJava 25で動かない。Mockito 5.24.0、JaCoCo 0.8.16、JUnit 5.14.4で確認済み
  - JDK 23以降のjavacは、指定しないと注釈処理をしない。JMH（`jmh-generator-annprocess`）はクラスパスに置くだけではベンチマークが生成されず、`benchmarks.jar`が空になる。maven-compiler-pluginに`<annotationProcessorPaths>`を書くか`<proc>full</proc>`（javacなら`-proc:full`）を指定する（2026-11-14、付録B.12・B.13）
- 説明と実行結果
  - READMEに載せるベンチマークの数値は実測したものだけにする。測っていない数値・出典のない事例を書かない。`currentTimeMillis`での計測は0msになり、比が`Infinity`・`NaN`になる。`nanoTime`・暖機・複数回の平均を使い、厳密にはJMHで測る（2026-11-14の付録の校閲で多数見つかった）
  - Helpful NullPointerExceptionsのメッセージの変数名は、`javac -g`なしでは`<local1>`・`<parameter1>`になる

## 既知の例外

- いまはない（2026-11-11に、第14章のWatchServiceのテストを、イベントが届いてから次の操作をするように直した。macOSのWatchServiceはポーリングで変更を検出するため、短時間で作成・削除したファイルを取りこぼしていた）
