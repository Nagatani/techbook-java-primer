# 付録A: 開発環境の構築<small>JDKとIDEの詳細セットアップ</small>

この付録では、Java開発環境の詳細なセットアップ手順を説明します。

## 目次

1. [JDK（Java Development Kit）のインストール](#a1-jdkjava-development-kitのインストール)
2. [SDKMANを使用したバージョン管理](#a2-sdkmanを使用したバージョン管理)
3. [統合開発環境（IDE）のセットアップ](#a3-統合開発環境ideのセットアップ)
4. [コマンドラインでの開発](#a4-コマンドラインでの開発)
5. [トラブルシューティング](#a5-トラブルシューティング)
6. [環境構築チェックリスト](#a6-環境構築チェックリスト)
7. [標準入力の扱い方](#a7-標準入力の扱い方)
8. [OS別セットアップガイド](#a8-os別セットアップガイド)
9. [よくある質問（FAQ）](#a9-よくある質問faq)

## A.1 JDK（Java Development Kit）のインストール

### JDKとは

プログラミング言語Javaの開発に使うコンパイラーや実行環境などの一式をJava Development Kitと呼び、JDKと略します。  
Javaでの開発は、複数あるJDKのどれかをインストールすることから始まります。

Javaでの開発にお勧めのIDEである、IntelliJ IDEAからもJDKをインストールできるようになっています。  
ですが、本書では、本書の内容以外でもJDKを使った開発ができるように、一括でバージョン管理が可能なソフトウェアを使用して必要なバージョンのJDKをインストールします。

### JDKの種類

JDKにはさまざまな提供元があり、インストールしたJDKの種類によっては、本書の説明どおりに進められない場合もあります。
そのため、本書で指定しているJDKをインストールするようにしてください。

#### JDKの種類が混乱するほど増えている理由

JDKと一言で言っても、開発元であるOracle以外にもいくつかの企業や団体がJDKとしてリリースしているものがあり、その配布元ごとにさまざまなバージョンのものがあります。

まず、JavaはOSSです。  
（単純にソフトウェアとしての側面のほかに、エコシステムとしての側面もあります）

Javaの商標は、開発元であるOracleが保持しています。そのため、本来はJava™のような書き方をするのが正しいと考えられます。

JDKの種類が多い理由として、OSSである点が第一に挙げられます。商標自体はOracleが保持していますが、JDK自体のソースコードの取得は開かれているので、それをビルドしてリリースする企業やコミュニティの数だけ、JDKの種類が増えます。
JDKの基本的な機能がその種類ごとで大きく異なってしまうということはあまり考えにくいのですが、状況によっては選んだJDKによって動作が異なることもあり得ます。
Javaでの開発を始める前に、開発を行う企業やコミュニティで使用されているJDKを確認して、それに合わせるように心がけましょう。

### 開発環境のインストールを行います

本書で使用するJavaのバージョンは、OpenJDKと呼ばれるオープン版のJDKを使用し、以下の開発バージョンを使います。

- `OpenJDK 25（Microsoft Build of OpenJDK）`

※本書執筆時点のLTS版（長期サポート対応バージョン）のなかで最も新しいものを使用しています。

本書のサンプルコードは、Java 25で正式に加わった標準入出力の`IO`クラス（`IO.println`、`IO.readln`など）を使います。
Java 24以前のJDKではコンパイルできないため、必ずJava 25以降のJDKをインストールしてください（Java 23と24の`IO`クラスは`java.io`パッケージのプレビュー機能で、本書のコードはそのままではコンパイルできません）。


## A.2 SDKMANを使用したバージョン管理

### バージョン管理ツールとは何か

ソフトウェア開発において、バージョン管理ツールは開発環境の管理を劇的に改善する重要なツールです。特にJavaのような、頻繁にバージョンアップされるプログラミング言語では、次のような課題が発生します。

- 複数プロジェクトでの異なるバージョン要求
    + プロジェクトAではJava 8、プロジェクトBではJava 17が必要である
- 新バージョンの検証
    + 新しいJavaバージョンをテストしつつ、安定版も維持する必要がある
- チーム開発での環境統一
    + 開発メンバー全員が同じバージョンを使用する必要がある
- OS標準のJavaとの競合
    + システムにプリインストールされたJavaと開発用Javaが混在する

### 従来の方法の問題点

バージョン管理ツールを使わない場合、次のような作業が必要になります。

1. 手動でのダウンロードとインストール
   - Oracleや各ベンダーのWebサイトから手動でダウンロード
   - OSごとに異なるインストール手順の実行

2. 環境変数の手動設定
   ```bash
   # 毎回手動で変更が必要
   export JAVA_HOME=/usr/local/java/jdk-25
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. バージョン切り替えの煩雑さ
   - プロジェクトごとに環境変数を手動で変更
   - 設定ミスによる予期しないエラーの発生

### SDKMANが解決する課題

SDKMAN!（The Software Development Kit Manager）は、JVM系言語のSDKを管理するためのコマンドラインツールです。以下の機能により、上記の問題を解決します。

#### 1. 統一されたコマンドインターフェイス
```bash
# JDKのインストールが1コマンドで完了
sdk install java 25.0.5.0+2-ms

# バージョン切り替えも簡単
sdk use java 21.0.13.0+9-tem
```

#### 2. 複数バージョンの共存管理
```bash
# インストール済みバージョンの確認
sdk list java

# プロジェクトごとの自動切り替え（sdkman_auto_env=trueを設定した場合。後述）
cd project-a  # .sdkmanrcにJava 8を書いておけば、Java 8に切り替わる
cd project-b  # .sdkmanrcにJava 17を書いておけば、Java 17に切り替わる
```

#### 3. ベンダー中立なJDK管理
##### SDKMANは主要なJDKディストリビューションをすべてサポート
- Oracle JDK
- OpenJDK（Microsoft, Amazon Corretto, Eclipse Temurin等）
- GraalVM
- そのほか多数のディストリビューション

#### 4. Java以外のツールも管理
```bash
sdk install gradle      # ビルドツール
sdk install maven       # ビルドツール
sdk install springboot  # フレームワーク
sdk install kotlin      # JVM言語
```

### なぜSDKMANを選ぶのか

#### 本書でSDKMANを採用する理由

1. クロスプラットフォーム対応
    + macOSとLinuxで同じコマンドが使える。WindowsではWSL2の中で同じように使える
2. 初心者にも優しい
    + 複雑な環境設定を自動化する
3. プロフェッショナルも愛用
    + 実際の開発現場で広く使われている
4. 無料・オープンソース
    + 誰でも自由に使用できる

### SDKMANの内部動作

SDKMANは次のようなしくみで動作します。

1. 候補リポジトリの管理
   - 利用可能なSDKのメタデータをオンラインで管理
   - 常に最新のバージョン情報を取得

2. ローカルキャッシュ
   - `~/.sdkman/candidates/`にSDKを保存
   - シンボリックリンクで現在のバージョンを管理

3. シェル統合
   - bashやzshの初期化スクリプトに統合
   - ターミナル起動時に自動的に環境を設定

これらの機能により、開発者は本来のプログラミングに集中でき、環境管理の煩わしさから解放されます。

- [SDKMAN!公式サイト](https://sdkman.io/)

### SDKMANのインストール

1. ターミナルを起動する
    - macOSのターミナルソフトウェアが起動すればよい。ターミナルソフトウェアがわからなければ、以下の操作で起動するウィンドウを使用する
    - `[⌘] + [space]`で `Spotlight` から `terminal` を起動
2. 以下のコマンドを実行してダウンロードします（`$`は入力しません）
    - $ `curl -s "https://get.sdkman.io" | bash`
3. 以下のコマンドを実行して初期設定を行う
    - $ `source "$HOME/.sdkman/bin/sdkman-init.sh"`
4. 以下のコマンドで正しくインストールされたかを確認する
    - $ `sdk version`
    - ```SDKMAN!```<br>```core:   5.24.0```<br>```native: 0.8.0 (macos aarch64)```のようなバージョン情報が出力されていればOKである（数字はインストールした時期によって異なる）
5. 使用しているターミナルソフトウェアに合わせて環境変数を設定する

macOSのターミナルソフトウェアとして、zshを使用している場合は以下のコマンドを使用します（`$`は入力しません）

```zsh
$ echo 'export JAVA_HOME=$HOME/.sdkman/candidates/java/current' >> ~/.zprofile
$ echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zprofile
$ source ~/.zprofile
```

これらの環境変数の設定は、ほかのJavaを参照するアプリケーションのために設定しておきます。

#### 再履修者向け: SDKMANのアップグレード

すでにSDKMANをインストール済みの場合は、以下のコマンドでセルフアップグレードを行います。

```bash
$ sdk selfupdate force
```

### SDKMANを使用したJDKインストール

#### 利用可能なJDKの確認

まず、インストール可能なJDKのバージョンを確認します。

```bash
$ sdk list java
```

このコマンドを実行すると、次のような一覧が表示されます（2026年11月時点の表示から一部を抜粋）。

```
================================================================================
Available Java Versions for macOS ARM 64bit
================================================================================
 Vendor         | Use | Version            | Identifier
--------------------------------------------------------------------------------
 Corretto       |     | 27.0.1.0+11        | 27.0.1.0+11-amzn
                |     | 25.0.5.0+10        | 25.0.5.0+10-amzn
                |     | 21.0.13.0+10       | 21.0.13.0+10-amzn
 Microsoft      |     | 25.0.5.0+2         | 25.0.5.0+2-ms
                |     | 21.0.13.0+2        | 21.0.13.0+2-ms
                |     | 17.0.21.0+2        | 17.0.21.0+2-ms
                |     | 11.0.33.0          | 11.0.33.0-ms
 Oracle         |     | 27.0.0             | 27.0.0-oracle
                |     | 25.0.5.0           | 25.0.5.0-oracle
 Temurin        |     | 27.0.0.0+35        | 27.0.0.0+35-tem
                |     | 25.0.5.0+8         | 25.0.5.0+8-tem
                |     | 21.0.13.0+9        | 21.0.13.0+9-tem
================================================================================
 > in use   * installed   + local only
--------------------------------------------------------------------------------
 $ sdk install java <Identifier>    install a specific version
 $ sdk install java                 install the default: 25.0.5.0+8-tem
 $ sdk install java [TAB]           complete an available identifier
================================================================================
```

#### 一覧の見方

- Vendor
    + JDKの提供元（Microsoft、Oracle、Amazon等）。同じ提供元の2行目以降は空欄になる
- Use
    + 使用状況を表す記号。`>`は現在使用中、`*`はインストール済み、`+`はSDKMANの外から登録したもの
- Version
    + JDKのバージョン番号
- Identifier
    + インストール時に使用する識別子

#### JDKのインストール

本書で使用するMicrosoft版のOpenJDK 25をインストールします。
一覧のMicrosoftの行から、`25.`で始まる識別子を選びます（2026年11月時点では`25.0.5.0+2-ms`）。

```bash
$ sdk install java 25.0.5.0+2-ms
```

識別子の後半（`25.0.5.0+2`の部分）は、セキュリティ修正などの更新があるたびに変わります。
一覧に表示される識別子が本書と異なる場合は、Microsoftの行にある`25.`で始まる最新のものを指定してください。
以降の説明の`25.0.5.0+2-ms`も、インストールした識別子に読み替えてください。

##### インストール中の表示例
```
Downloading: java 25.0.5.0+2-ms

In progress...
######################################################################## 100.0%

Repackaging Java 25.0.5.0+2-ms...

Done repackaging...

Installing: java 25.0.5.0+2-ms
Done installing!

Setting java 25.0.5.0+2-ms as default.
```

すでにほかのJDKをインストールしている場合は、デフォルトにするかどうかを聞かれます。
本書の内容を学習するあいだは`Y`を入力して、Java 25をデフォルトにしてください。

#### インストールの確認

インストールが完了したら、以下のコマンドで確認します。

```bash
$ java -version
```

次のようなバージョン情報が出力されていればOKです（`25.0.5.0+2-ms`をインストールした場合の表示）。
バージョン番号・日付・ビルド番号は、インストールした時期の更新版によって変わります。

```
openjdk version "25.0.5.0.2" 2026-10-22 LTS
OpenJDK Runtime Environment Microsoft-15804666 (build 25.0.5.0.2+1-LTS)
OpenJDK 64-Bit Server VM Microsoft-15804666 (build 25.0.5.0.2+1-LTS, mixed mode, sharing)
```

1行目の`"25.`で始まるバージョン番号と、2行目の`Microsoft`が表示されていることを確認します。
`21`などほかのバージョンが表示される場合は、「バージョンの切り替え」の`sdk default`でJava 25をデフォルトにしてください。

### SDKMANの実践的な使い方

#### 複数バージョンの管理

実際の開発では、プロジェクトごとに異なるJavaバージョンを使用することがあります。SDKMANはこのような状況を簡単に管理できます。

```bash
# 別のバージョンをインストール
$ sdk install java 21.0.13.0+2-ms

# インストール済みのバージョンを確認（Use欄に*が付く）
$ sdk list java
```

#### バージョンの切り替え

```bash
# 一時的な切り替え（現在のターミナルセッションのみ）
$ sdk use java 21.0.13.0+2-ms
Using java version 21.0.13.0+2-ms in this shell.

# デフォルトバージョンの変更（永続的）
$ sdk default java 25.0.5.0+2-ms
✓ Set java 25.0.5.0+2-ms as the default version

# 現在のバージョンを確認
$ sdk current java
25.0.5.0+2-ms
```

#### プロジェクトごとの自動切り替え

プロジェクトのルートディレクトリに`.sdkmanrc`ファイルを作成し、`sdk env`を実行すると、そのファイルに書いたバージョンに切り替わります。
`~/.sdkman/etc/config`に`sdkman_auto_env=true`を書いておくと、ディレクトリに移動したときに自動で切り替わります。

```bash
# .sdkmanrcファイルの作成
$ cd my-project
$ echo "java=21.0.13.0+2-ms" > .sdkmanrc

# .sdkmanrcに書いたバージョンに切り替える
$ sdk env
Using java version 21.0.13.0+2-ms in this shell.
```

#### SDKMANのそのほかの便利なコマンド

```bash
# 古いバージョンの削除
$ sdk uninstall java 21.0.13.0+2-ms
✓ Uninstalled java 21.0.13.0+2-ms

# SDKMANのアップデート
$ sdk selfupdate

# オフラインモードの切り替え（インターネット接続なしで使用）
$ sdk offline enable

# インストールしたJDKの場所を確認
$ sdk home java 25.0.5.0+2-ms
```

### トラブルシューティング

#### SDKMANが認識されない場合

```bash
# 初期化スクリプトを再実行
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
```

#### プロキシ環境での使用

企業ネットワークなどプロキシ環境下では、次の設定が必要です。

```bash
# ~/.sdkman/etc/config に追加
sdkman_proxy_host=proxy.example.com
sdkman_proxy_port=8080
```


## A.3 統合開発環境（IDE）のセットアップ

本書では次のIDEを推奨します。

- IntelliJ IDEA
    + 無料で使える機能で十分（有料のUltimateの機能は不要）
- Visual Studio Code
    + Java開発の拡張機能が必須

IDEの利用に関しては、IntelliJ IDEAがお勧めです。
本書で取り上げる操作説明などは、IntelliJ IDEAの無料で使える機能を基本とします。

### IntelliJ IDEAのエディションについて

IntelliJ IDEAには、以前は無料のCommunity Editionと有料のUltimateの2つの製品がありました。
2025.3（2025年12月8日公開）から、この2つは「IntelliJ IDEA」という1つの製品に統合されています。
統合版をインストールすれば、Community Editionにあった機能はサブスクリプションなしで無料のまま使えます。
Webフレームワークやデータベースの支援など、Ultimateの機能を使うにはサブスクリプションが必要ですが、本書の学習には必要ありません。

### IntelliJ IDEAのインストール

1. [JetBrains公式サイト](https://www.jetbrains.com/idea/download/)にアクセス
2. IntelliJ IDEA（統合版）をダウンロード
3. ダウンロードしたインストーラーを実行
4. 初回起動時の設定で、インストール済みのJDK（Java 25）を指定

Java 25の文法と`IO`クラスを扱うには、Java 25に対応した版（2025.2以降）のIntelliJ IDEAを使ってください。
これから新しくインストールする場合は、統合版（2025.3以降）になります。

### Visual Studio Codeでの開発環境構築

Visual Studio Codeを使用する場合は、次の拡張機能のインストールが必要です。

- Extension Pack for Java（Microsoft提供）
    + Javaの言語サポート、デバッガー（Debugger for Java）、テスト実行（Test Runner for Java）、Maven・Gradleの支援などをまとめた拡張機能のパック。これ1つをインストールすれば、含まれる拡張機能も入る

### IDE設定ファイルのサンプル

#### IntelliJ IDEA設定

プロジェクトルートに `.idea/codeStyles/Project.xml` を配置：

```xml
<component name="ProjectCodeStyleConfiguration">
  <code_scheme name="Project" version="173">
    <JavaCodeStyleSettings>
      <option name="CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND" value="999" />
      <option name="NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND" value="999" />
    </JavaCodeStyleSettings>
    <codeStyleSettings language="JAVA">
      <option name="INDENT_SIZE" value="4" />
      <option name="TAB_SIZE" value="4" />
    </codeStyleSettings>
  </code_scheme>
</component>
```

#### VS Code設定

プロジェクトルートに `.vscode/settings.json` を配置：

```json
{
    "java.configuration.updateBuildConfiguration": "automatic",
    "java.compile.nullAnalysis.mode": "automatic",
    "editor.tabSize": 4,
    "editor.insertSpaces": true,
    "files.encoding": "utf8",
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-25",
            "path": "/Users/ユーザー名/.sdkman/candidates/java/current",
            "default": true
        }
    ]
}
```

`java.configuration.runtimes`は、プロジェクトのコンパイルと実行に使うJDKを指定する設定です。
`path`には、SDKMANでインストールしたJDKの場所を絶対パスで書きます（`ユーザー名`は自分のものに置き換えます。`sdk home java 識別子`で表示される場所でも構いません）。
以前使われていた`java.home`という設定は非推奨になりました。

## A.4 コマンドラインでの開発

IDEを使わずに、コマンドラインでJavaプログラムを開発する基本的な手順を説明します。

### 作業スペースの作成

ターミナルを開き、以下のコマンドを入力しましょう。

```bash
$ cd                       # ホームディレクトリに移動。
$ mkdir java-practice      # java-practiceというディレクトリを作成する。
$ cd java-practice         # java-practiceに移動する。
```

### プログラムの作成

テキストエディターは何でもよいのですが、ファイル名「```HelloWorld.java```」として、ファイルを新規作成しましょう。
本書では、macOSやLinuxに標準搭載されているテキストエディターnanoを使用する方法を書きます。

作業スペースを作成した続きで以下のコマンドを使用します。

```bash
$ nano HelloWorld.java
```

以下のコードを書きます。

```java
public class HelloWorld {
    public static void main(String[] args) {
        IO.println("Hello, World!!");
    }
}
```

コードを書き終えたら、ファイルを保存しておきます。

nanoでの保存は、`[control] + [x]`の後、変更を保存するか聞かれるので`[y]`、ファイル名はそのまま`[return]`でOKです。

### コンパイルと実行

1. 以下のコマンドで、指定されたJavaファイルをコンパイルする
    - $ `javac HelloWorld.java`
2. コンパイルが成功すると、メッセージは何も表示されません
    - コンパイルに失敗する場合、ソースコードのどこに間違いがあるかがメッセージで表示される
    - コンパイル後にいくつかメッセージが表示されて、.classファイルなどが生成されていない、更新されていない場合はJavaファイルのコードを見直して、間違いがないか確認しましょう
    - エラーメッセージ内に間違っている箇所のヒントが書かれているので、それを読みましょう
3. ワークスペースのディレクトリをFinder等で見ると、「HelloWorld.class」と言ったクラスファイルが生成されている
    - .classファイルが、コンパイルされたJavaの実行ファイルである
4. 以下のコマンドでプログラムを実行する
    - $ `java HelloWorld`

正しく実行できた場合、ターミナル上には、以下のような出力がされます。

```bash
Hello, World!!
```

ターミナルを使用したJavaプログラムのコンパイルと実行は、統合開発環境を使用する開発に入る前まで、以上の手順を用います。

### 覚えておこう！ コンパイルと実行

IDEを使用し始めると、コンパイルしてから実行という作業が、単に実行だけになります（IDEがコードを書いている裏で自動的にコンパイルしてくれている）

```bash
$ javac Javaファイル名
$ java 実行するクラス名
```

## A.5 トラブルシューティング

### JDKが認識されない場合

```bash
$ java -version
command not found: java
```

このエラーが出る場合は、PATHが正しく設定されていません。
SDKMANを使用している場合は、次のコマンドで初期化スクリプトを読み込み直します。

```bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
```

### コンパイルエラーの読み方

```
HelloWorld.java:3: error: ';' expected
        IO.println("Hello, World!!")
                                    ^
1 error
```

- `HelloWorld.java:3`: エラーの場所（ファイル名行番号）
- `error: ';' expected`: エラーの内容（セミコロンが必要）
- `^`: エラーの具体的な位置

### 文字エンコーディングの問題

#### 日本語を含むソースファイルでエラーが出る場合

Java 18以降の`javac`は、ソースファイルをUTF-8として読みます。
`error: unmappable character (0x82) for encoding UTF-8`のようなエラーが出る場合は、ソースファイルがShift_JISなどUTF-8以外の文字コードで保存されています。
エディターでUTF-8として保存し直すのが基本の対処です。
保存し直せないファイルは、`-encoding`でそのファイルの文字コードを指定してコンパイルします。

```bash
$ javac -encoding Shift_JIS HelloWorld.java
```

### よくあるインストールエラーと対処法

#### 1. SDKMANインストール時のcurlエラー

```bash
# エラー例
curl: (7) Failed to connect to get.sdkman.io port 443: Connection refused

# 対処法1: プロキシ設定
$ export https_proxy=http://proxy.example.com:8080
$ curl -s "https://get.sdkman.io" | bash

# 対処法2: 別の方法でダウンロード
$ wget https://get.sdkman.io -O install_sdkman.sh
$ bash install_sdkman.sh
```

#### 2. JAVA_HOMEが設定されていない

```bash
# エラー例
The JAVA_HOME environment variable is not defined

# 対処法
$ export JAVA_HOME=$HOME/.sdkman/candidates/java/current
$ echo 'export JAVA_HOME=$HOME/.sdkman/candidates/java/current' >> ~/.zprofile
```

#### 3. IntelliJ IDEAでJDKが認識されない

IntelliJ IDEAを起動後、以下の手順でJDKを設定：

1. File → Project Structure → Project
2. SDKの一覧で「Add SDK」→「Add JDK from disk...」を選択
3. `~/.sdkman/candidates/java/25.0.5.0+2-ms`を選択

## A.6 環境構築チェックリスト

### 環境構築が正しく完了したことを確認するためのチェックリストです

- [ ] JDKがインストールされている
  - `java -version`で25で始まるバージョンが表示される
  - `javac -version`で25で始まるバージョンが表示される
- [ ] SDKMANが正しく動作している
  - `sdk version`でSDKMANのバージョンが表示される
- [ ] IDEがインストールされている
  - IntelliJ IDEAまたはVS Codeが起動する
  - IDEからJavaプロジェクトを作成できる
- [ ] Hello Worldプログラムが実行できる
  - コマンドラインで実行できる
  - IDEから実行できる

すべてのチェック項目が完了したら、Java開発環境の準備は整いました。

## A.7 標準入力の扱い方

Javaプログラムでのプログラムへのデータの受け渡し方法は、さまざまな方法があります。
ここでは、プログラムの実行後、キーボードからの入力を待ち受け、Enterキー押下で後続の処理を実行する方法を説明します。

### 標準入力の基本

画面から文字列を入力し、そのまま画面表示するプログラム。

ファイル名「```StandardInput.java```」

```java
public class StandardInput {
    public static void main(String[] args) {
        // IO.readln()メソッドは、キーボードからReturnキーの入力があるまで待ち、入力された1行を返す。
        // IOクラスはjava.langパッケージにあるため、import文は不要。
        String inputLine = IO.readln();
        // ↑inputLineという変数には、入力された文字列データが設定されています。

        // 入力された文字列データをそのまま出力。
        IO.println(inputLine);
    }
}
```

### 標準入力から受けた文字列から数値変換を行う

画面から文字列を入力し、数値に変換して計算するプログラム

ファイル名「```ParseInt.java```」

```java
public class ParseInt {
    public static void main(String[] args) {
        // IO.readln()メソッドは、キーボードからReturnキーの入力があるまで待ち、入力された1行を返す
        // 引数に文字列を渡すと、入力の前にその文字列を表示する
        String inputLine = IO.readln("整数を入力してください: ");
        // ↑inputLineという変数には、入力された文字列データが格納されます

        // 変数numに文字列データを整数値に変換して格納
        int num = Integer.parseInt(inputLine);
        // 整数変換された文字列データに10を足した結果を変数ansに格納
        int ans = num + 10;

        // 変数ansを出力
        IO.println(ans);
    }
}
```

#### 入力文字列が整数値に変換できない場合

試しに`aa`という文字列を入力してみましょう。

以下のエラーメッセージがでます。
```
整数を入力してください: Exception in thread "main" java.lang.NumberFormatException: For input string: "aa"
	at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:67)
	at java.base/java.lang.Integer.parseInt(Integer.java:565)
	at java.base/java.lang.Integer.parseInt(Integer.java:662)
	at ParseInt.main(ParseInt.java:9)
```

これは、実行時例外と呼ばれるエラーです。
現時点ではこのエラーはでることが正常な動作です。

入力された文字列は整数値に変換できる文字列を想定してプログラミングを行いました。
ですので、「整数値に変換できない文字列が入力されることは想定外なのでエラーが出力される」という正常な動作です。

## A.8 OS別セットアップガイド

### Windows環境でのセットアップ

#### WSL2（Windows Subsystem for Linux 2）の使用（推奨）

Windows環境では、WSL2を使用することで、Linux環境と同様の開発環境を構築できます。

1. **WSL2のインストール**
   ```powershell
   # PowerShellを管理者権限で実行
   wsl --install
   ```

2. **Linuxディストリビューションの準備**
   - `wsl --install`は既定でUbuntuもインストールする。再起動後にUbuntuを起動し、ユーザー名とパスワードを設定する
   - ほかのディストリビューションを使う場合は、`wsl --list --online`で一覧を表示し、`wsl --install -d 名前`でインストールする

3. **WSL2内でSDKMANをインストール**
   ```bash
   # WSL2のターミナル内で実行（SDKMANはzipとunzipを使う）
   sudo apt update
   sudo apt install curl zip unzip
   curl -s "https://get.sdkman.io" | bash
   source "$HOME/.sdkman/bin/sdkman-init.sh"
   ```

#### Windows Native環境での開発

WSL2を使用しない場合は、以下の手順で環境を構築します。

1. **JDKの直接インストール**
   - [Microsoft Build of OpenJDK](https://www.microsoft.com/openjdk)から、OpenJDK 25のWindows用インストーラー（.msi）をダウンロード
   - インストーラーを実行し、機能の選択で「Set JAVA_HOME variable」と「Add to PATH」を有効にする

2. **環境変数の確認**
   - インストーラーで環境変数を設定しなかった場合は、システムの詳細設定 → 環境変数で設定する
   - JAVA_HOMEを設定: `C:\Program Files\Microsoft\`の下にある`jdk-25`で始まるフォルダー
   - Pathに追加: `%JAVA_HOME%\bin`
   - 新しく開いたコマンドプロンプトで`java -version`を実行し、25で始まるバージョンが表示されることを確かめる

SDKMANはWindowsのコマンドプロンプトやPowerShellでは動きません。
Windows Native環境では、上の手順でJDKを直接インストールし、バージョンの切り替えも環境変数で行います。

### Linux環境でのセットアップ

#### Ubuntu/Debian系

```bash
# 必要なパッケージのインストール
sudo apt update
sudo apt install curl zip unzip

# SDKMANのインストール
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# JDKのインストール
sdk install java 25.0.5.0+2-ms
```

#### Fedora/Red Hat Enterprise Linux系

```bash
# 必要なパッケージのインストール
sudo dnf install curl zip unzip

# SDKMANのインストール
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# JDKのインストール
sdk install java 25.0.5.0+2-ms
```

### macOS固有の設定

#### Homebrewを使用したツールのインストール

```bash
# Homebrewがインストールされていない場合
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# 開発に便利なツールのインストール
brew install git
brew install wget
```

#### Xcodeコマンドラインツール

macOSでは、開発ツールを使用するためにXcodeコマンドラインツールが必要な場合があります。

```bash
xcode-select --install
```

## A.9 よくある質問（FAQ）

### Q1: 複数のJDKバージョンを同時に使いたい

**A:** SDKMANを使用すれば簡単に実現できます。

```bash
# 複数バージョンのインストール
sdk install java 25.0.5.0+2-ms
sdk install java 21.0.13.0+2-ms
sdk install java 17.0.21.0+10-amzn

# プロジェクトAで使用
cd project-a
sdk use java 21.0.13.0+2-ms

# プロジェクトBで使用
cd project-b
sdk use java 25.0.5.0+2-ms
```

### Q2: IDEとコマンドラインで異なるJDKバージョンが使用される

**A:** IDEの設定でSDKMANのJDKを指定してください。

IntelliJ IDEAの場合は、次の手順で設定します。

1. File → Project Structure → Project
2. SDKの一覧で「Add SDK」→「Add JDK from disk...」を選び、`~/.sdkman/candidates/java/current`を選択

VS Codeの場合は、「IDE設定ファイルのサンプル」の`java.configuration.runtimes`を`.vscode/settings.json`に書き、`path`にSDKMANのJDKの場所を指定します。

### Q3: Javaプログラムの文字化けが発生する

**A:** 文字化けがどこで起きているかによって、対処が変わります。

- コンパイルでエラーになる、または文字列そのものが化ける
    + ソースファイルがUTF-8以外で保存されている。Java 18以降の`javac`はソースをUTF-8として読むため、ファイルをUTF-8で保存し直す（A.5「文字エンコーディングの問題」を参照）
- 画面への出力だけが化ける
    + `IO.println`や`System.out`の出力は、コンソールの文字コード（`stdout.encoding`）で書き出される。Windowsのコンソールで化ける場合は、コンソールの文字コードとJavaの出力の文字コードをそろえる

```bash
# 出力の文字コードをUTF-8にして実行する（コンソールもUTF-8のとき）
java -Dstdout.encoding=UTF-8 MyProgram
```

Windowsのコマンドプロンプトでは、`chcp 65001`でコンソールの文字コードをUTF-8にできます。
Java 18以降はファイルの読み書きの既定の文字コードがUTF-8になりました。`-Dfile.encoding=UTF-8`を指定しても、画面への出力の文字コードは変わりません。
IDEでは、プロジェクト設定でファイルのエンコーディングをUTF-8にします。

### Q4: OutOfMemoryErrorが発生する

**A:** JVMのヒープサイズを増やしてください。

```bash
# 最大ヒープサイズを2GBに設定
java -Xmx2g MyProgram

# IDEの場合は、実行構成でVMオプションを追加
-Xms512m -Xmx2g
```

### Q5: プロキシ環境でSDKMANが使えない

**A:** プロキシ設定を追加してください。

```bash
# ~/.sdkman/etc/config に以下を追加
sdkman_proxy_host=proxy.example.com
sdkman_proxy_port=8080
sdkman_proxy_username=myuser  # 認証が必要な場合
sdkman_proxy_password=mypass  # 認証が必要な場合
```

## Docker環境でのJava開発（オプション）

コンテナー技術を使用した開発環境の構築方法を説明します。

ここでは、作業ディレクトリ（`HelloWorld.java`などを置くディレクトリ）をコンテナーにマウントし、コンテナーの中のJDK 25でコンパイル・実行します。
作業ディレクトリに次の2つのファイルを置きます。
イメージにはEclipse TemurinのJDK 25を使います（本書が指定するMicrosoft版と同じOpenJDK 25で、本書のコードはどちらでも動きます）。

### Dockerfileの例

```dockerfile
FROM eclipse-temurin:25-jdk

# 必要なツールのインストール
RUN apt-get update && apt-get install -y \
    git \
    curl \
    vim \
    && rm -rf /var/lib/apt/lists/*

# 作業ディレクトリの設定（docker-compose.ymlでカレントディレクトリをここにマウントする）
WORKDIR /app

# コンテナーを起動したときにbashを開く
CMD ["bash"]
```

### docker-compose.ymlの例

```yaml
services:
  java-app:
    build: .
    volumes:
      - .:/app
    working_dir: /app
    stdin_open: true
    tty: true
```

### 使用方法

```bash
# イメージを作ってコンテナーを起動
docker compose up -d --build

# コンテナー内でbashを実行
docker compose exec java-app bash

# コンテナー内でJavaプログラムを実行（作業ディレクトリのファイルが/appに見える）
javac HelloWorld.java
java HelloWorld

# コンテナーのbashを抜けた後、コンテナーを止めて削除
exit
docker compose down
```

コンパイルでできた`.class`ファイルは、マウントした作業ディレクトリに残ります。
古いDocker Composeでは、`docker compose`の代わりに`docker-compose`と入力します。

## 自動化スクリプト

開発環境のセットアップを自動化するスクリプトの例を提供します。

### setup.sh（macOS/Linux用）

```bash
#!/bin/bash

echo "Java開発環境のセットアップを開始します..."

# SDKMANのインストール
if [ ! -d "$HOME/.sdkman" ]; then
    echo "SDKMANをインストールしています..."
    curl -s "https://get.sdkman.io" | bash
else
    echo "SDKMANは既にインストールされています"
fi

# sdkコマンドを使えるようにする（スクリプトの中では毎回読み込む）
source "$HOME/.sdkman/bin/sdkman-init.sh"

# JDKのインストール（ほかのJDKがあるときの「デフォルトにするか」の質問にはYで答える）
echo "JDK 25 (Microsoft)をインストールしています..."
echo "Y" | sdk install java 25.0.5.0+2-ms

# 環境変数の設定
if [ -f "$HOME/.zshrc" ]; then
    SHELL_RC="$HOME/.zshrc"
elif [ -f "$HOME/.bashrc" ]; then
    SHELL_RC="$HOME/.bashrc"
else
    SHELL_RC="$HOME/.profile"
fi

if ! grep -q "JAVA_HOME" "$SHELL_RC"; then
    echo 'export JAVA_HOME=$HOME/.sdkman/candidates/java/current' >> "$SHELL_RC"
    echo 'export PATH=$JAVA_HOME/bin:$PATH' >> "$SHELL_RC"
fi

echo "セットアップが完了しました！"
echo "新しいターミナルを開くか、以下のコマンドを実行してください："
echo "source $SHELL_RC"
```

### setup.ps1（Windows PowerShell用）

```powershell
Write-Host "Java開発環境のセットアップを開始します..." -ForegroundColor Green

# JDKのダウンロードとインストール（OpenJDK 25の最新の更新版）
$jdkUrl = "https://aka.ms/download-jdk/microsoft-jdk-25-windows-x64.msi"
$jdkInstaller = "$env:TEMP\microsoft-jdk-25.msi"

Write-Host "JDKをダウンロードしています..."
Invoke-WebRequest -Uri $jdkUrl -OutFile $jdkInstaller

# FeatureEnvironmentでPathに、FeatureJavaHomeでJAVA_HOMEに設定する（管理者権限で実行する）
Write-Host "JDKをインストールしています..."
Start-Process -FilePath msiexec.exe -ArgumentList "/i `"$jdkInstaller`" ADDLOCAL=FeatureMain,FeatureEnvironment,FeatureJavaHome /quiet" -Wait

Write-Host "セットアップが完了しました！" -ForegroundColor Green
Write-Host "新しいコマンドプロンプトまたはPowerShellを開いて、java -versionを実行してください。"
```

開発環境の構築が完了したら、より深いJava技術の理解を目指しましょう。本書では各章の学習内容をさらに深く掘り下げた技術詳細を付録Bで提供しています。

### 推奨学習パス

#### JVMとJava実行環境の詳細理解
- 付録B.01「JVMアーキテクチャとバイトコード」
  - JVMの内部構造、クラスローディング、バイトコード実行の仕組み
  - JITコンパイレーションとパフォーマンス最適化

#### 開発環境と実行環境の関係理解
- 付録B.01で学習したJVM知識を基に、以下を理解できる
  - なぜJDKのバージョン選択が重要なのか
  - コンパイル時と実行時の違い
  - 異なるJDK間での互換性の考慮点

#### 実践的な開発環境の最適化
付録Bの各セクションを学習することで、単なる環境構築を超えた、パフォーマンスとセキュリティを考慮した本格的な開発環境の構築が可能になります。

詳細は「付録B: 技術的な詳細解説（Deep Dive）インデックス」を参照してください。
