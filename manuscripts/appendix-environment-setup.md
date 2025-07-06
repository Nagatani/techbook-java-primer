# 付録A: 開発環境の構築

この付録では、Java開発環境の詳細なセットアップ手順を説明します。

## A.1 JDK（Java Development Kit）のインストール

### JDKとは

プログラミング言語Javaの開発環境をJava Software Development Kitと呼び、JavaSDKやJDKなどと略されます。  
Javaでの開発は、複数あるJDKのどれかをインストールすることから始まります。

Javaでの開発にお勧めのIDEである、IntelliJ IDEAからもJDKをインストールできるようになっています。  
ですが、本書では、本書の内容以外でもJDKを使った開発ができるように、一括でバージョン管理が可能なソフトウェアを使用して必要なバージョンのJDKをインストールします。

### JDKの種類

JDKに挟まざまな提供元があり、インストールするJDKの種類が異なることによって講義を円滑に進められない場合もあります。
そのため、講義で使用するJDKはこの資料で指定しているものを必ずインストールするようにしてください。

#### JDKの種類が混乱するほど増えている理由

JDKと一言で言っても、開発元であるOracle以外にもいくつかの企業や団体がJDKとしてリリースしているものがあり、その配布元ごとにさまざまなバージョンのものがあります。

まず、JavaはOSSです。  
（単純にソフトウェアとしての側面のほかに、エコシステムとしての側面もあります）

Javaの商標は、開発元であるOracleが保持しています。ですので、本来はJava™のような書き方をするのが正しいのかもしれません。

JDKの種類が多い理由として、OSSである点が第一に挙げられます。商標自体はOracleが保持していますが、JDK自体のソースコードの取得は開かれているので、それをビルドしてリリースする企業やコミュニティの数だけ、JDKの種類が増えます。
JDKの基本的な機能がその種類ごとで大きく異なってしまうということはあまり考えにくいのですが、状況によっては選んだJDKによって動作が異なることもあり得ます。
Javaでの開発を始める前に、開発を行う企業やコミュニティで使用されているJDKを確認して、それに合わせるように心がけましょう。

### 開発環境のインストールを行います

本書で使用するJavaのバージョンは、OpenJDKと呼ばれるオープン版のJDKを使用し、以下の開発バージョンを使います。

- `OpenJDK 21.0.6 (Microsoft)`

※本書執筆時点のLTS版（長期サポート対応バージョン）の中で最も新しいものを使用しています。


## A.2 SDKMANを使用したバージョン管理

### バージョン管理ツールとは何か

ソフトウェア開発において、**バージョン管理ツール**は開発環境の管理を劇的に改善する重要なツールです。とくにJavaのような、頻繁にバージョンアップされるプログラミング言語では、以下のような課題が発生します：

- **複数プロジェクトでの異なるバージョン要求**: プロジェクトAではJava 8、プロジェクトBではJava 17が必要
- **新バージョンの検証**: 新しいJavaバージョンをテストしつつ、安定版も維持したい
- **チーム開発での環境統一**: 開発メンバー全員が同じバージョンを使用する必要がある
- **OS標準のJavaとの競合**: システムにプリインストールされたJavaと開発用Javaの混在

### 従来の方法の問題点

バージョン管理ツールを使わない場合、以下のような作業が必要になります：

1. **手動でのダウンロードとインストール**
   - Oracleや各ベンダーのWebサイトから手動でダウンロード
   - OSごとに異なるインストール手順の実行

2. **環境変数の手動設定**
   ```bash
   # 毎回手動で変更が必要
   export JAVA_HOME=/usr/local/java/jdk-21.0.6
   export PATH=$JAVA_HOME/bin:$PATH
   ```

3. **バージョン切り替えの煩雑さ**
   - プロジェクトごとに環境変数を手動で変更
   - 設定ミスによる予期しないエラーの発生

### SDKMANが解決する課題

**SDKMAN!**（The Software Development Kit Manager）は、JVM系言語のSDKを管理するためのコマンドラインツールです。以下の機能により、上記の問題を解決します：

#### 1. 統一されたコマンドインターフェース
```bash
# JDKのインストールが1コマンドで完了
sdk install java 21.0.6-ms

# バージョン切り替えも簡単
sdk use java 17.0.9-tem
```

#### 2. 複数バージョンの共存管理
```bash
# インストール済みバージョンの確認
sdk list java

# プロジェクトごとの自動切り替え
cd project-a  # .sdkmanrcファイルがあれば自動的にJava 8に
cd project-b  # 自動的にJava 17に切り替わる
```

#### 3. ベンダー中立なJDK管理
SDKMANは主要なJDKディストリビューションをすべてサポート：
- Oracle JDK
- OpenJDK (Microsoft, Amazon Corretto, Eclipse Temurin等)
- GraalVM
- その他多数のディストリビューション

#### 4. Java以外のツールも管理
```bash
sdk install gradle      # ビルドツール
sdk install maven       # ビルドツール
sdk install springboot  # フレームワーク
sdk install kotlin      # JVM言語
```

### なぜSDKMANを選ぶのか

本書でSDKMANを採用する理由：

1. **クロスプラットフォーム対応**: macOS、Linux、Windowsで同じコマンドが使える
2. **初心者にも優しい**: 複雑な環境設定を自動化
3. **プロフェッショナルも愛用**: 実際の開発現場で広く使われている
4. **無料・オープンソース**: 誰でも自由に使用可能

### SDKMANの内部動作

SDKMANは以下のような仕組みで動作します：

1. **候補リポジトリの管理**
   - 利用可能なSDKのメタデータをオンラインで管理
   - 常に最新のバージョン情報を取得

2. **ローカルキャッシュ**
   - `~/.sdkman/candidates/`にSDKを保存
   - シンボリックリンクで現在のバージョンを管理

3. **シェル統合**
   - bashやzshの初期化スクリプトに統合
   - ターミナル起動時に自動的に環境を設定

これらの機能により、開発者は本来のプログラミングに集中でき、環境管理の煩わしさから解放されます。

- [SDKMAN!公式サイト](https://sdkman.io/)

### SDKMANのインストール

1. ターミナルを起動する
    - macOSのターミナルソフトウェアが起動すればよいです。 ターミナルソフトウェアが分からなければ、以下の操作で起動するウィンドウを使います。
    - `[⌘] + [space]`で `Spotlight` から `terminal` を起動
2. 以下のコマンドを実行してダウンロードする（`$`は入力しません）
    - $ `curl -s "https://get.sdkman.io" | bash`
3. 以下のコマンドを実行して初期設定を行う
    - $ `source "$HOME/.sdkman/bin/sdkman-init.sh"`
4. 以下のコマンドで正しくインストールされたかを確認する
    - $ `sdk version`
    - ```SDKMAN!```<br>```script: 5.19.0```<br>```native: 0.7.4 (macos aarch64)```のようなバージョン情報が出力されていればOKです。
5. 使用しているターミナルソフトウェアに合わせて環境変数を設定

macOSのターミナルソフトウェアとして、zshを使用している場合は以下のコマンドを使用します（`$`は入力しません）

```zsh
$ echo 'export JAVA_HOME=$HOME/.sdkman/candidates/java/current' >> ~/.zprofile
$ echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zprofile
$ source ~/.zprofile
```

これらの環境変数の設定は、ほかのJavaを参照するアプリケーションのために設定しておきます。

#### 再履修者向け: SDKMANのアップグレード

すでにSDKMANをインストール済みの場合は、以下のコマンドでセルフアップグレードを行ってください。

```bash
$ sdk selfupdate force
```

### SDKMANを使用したJDKインストール

#### 利用可能なJDKの確認

まず、インストール可能なJDKのバージョンを確認します：

```bash
$ sdk list java
```

このコマンドを実行すると、以下のような一覧が表示されます：

```
================================================================================
Available Java Versions for macOS ARM 64bit
================================================================================
 Vendor        | Use | Version      | Dist    | Status     | Identifier
--------------------------------------------------------------------------------
 Corretto      |     | 21.0.5       | amzn    |            | 21.0.5-amzn
 Corretto      |     | 17.0.13      | amzn    |            | 17.0.13-amzn
 Corretto      |     | 11.0.25      | amzn    |            | 11.0.25-amzn
 Gluon         |     | 22.1.0.1.r17 | gln     |            | 22.1.0.1.r17-gln
 GraalVM CE    |     | 21.0.5       | graalce |            | 21.0.5-graalce
 Java.net      |     | 24.ea.2      | open    |            | 24.ea.2-open
 Java.net      |     | 23           | open    |            | 23-open
 Microsoft     |     | 21.0.6       | ms      |            | 21.0.6-ms      
 Microsoft     |     | 17.0.13      | ms      |            | 17.0.13-ms
 Oracle        |     | 23           | oracle  |            | 23-oracle
 Oracle        |     | 21.0.5       | oracle  |            | 21.0.5-oracle
 Temurin       |     | 21.0.5       | tem     |            | 21.0.5-tem
================================================================================
```

**一覧の見方：**
- **Vendor**: JDKの提供元（Microsoft、Oracle、Amazon等）
- **Use**: 現在使用中のバージョン（>>>マークが付く）
- **Version**: JDKのバージョン番号
- **Dist**: ディストリビューションの略称
- **Status**: インストール状態（installedが表示される）
- **Identifier**: インストール時に使用する識別子

#### JDKのインストール

本書で使用するMicrosoft版のOpenJDK 21.0.6をインストールします：

```bash
$ sdk install java 21.0.6-ms
```

インストール中の表示例：
```
Downloading: java 21.0.6-ms

In progress...
############################# 100.0%

Installing: java 21.0.6-ms
Done installing!

Do you want java 21.0.6-ms to be set as default? (Y/n): Y

Setting java 21.0.6-ms as default.
```

#### インストールの確認

インストールが完了したら、以下のコマンドで確認します：

```bash
$ java -version
```

以下のようなバージョン情報が出力されていればOKです：

```
openjdk 21.0.6 2025-01-21 LTS
OpenJDK Runtime Environment Microsoft-10800196 (build 21.0.6+7-LTS)
OpenJDK 64-Bit Server VM Microsoft-10800196 (build 21.0.6+7-LTS, mixed mode, sharing)
```

### SDKMANの実践的な使い方

#### 複数バージョンの管理

実際の開発では、プロジェクトごとに異なるJavaバージョンを使用することがあります。SDKMANはこのような状況を簡単に管理できます。

```bash
# 別のバージョンをインストール
$ sdk install java 17.0.13-ms

# インストール済みのバージョンを確認
$ sdk list java installed
```

#### バージョンの切り替え

```bash
# 一時的な切り替え（現在のターミナルセッションのみ）
$ sdk use java 17.0.13-ms
Using java version 17.0.13-ms in this shell.

# デフォルトバージョンの変更（永続的）
$ sdk default java 21.0.6-ms
Default java version set to 21.0.6-ms

# 現在のバージョンを確認
$ sdk current java
Using java version 21.0.6-ms
```

#### プロジェクトごとの自動切り替え

プロジェクトのルートディレクトリに`.sdkmanrc`ファイルを作成すると、ディレクトリ移動時に自動的にバージョンが切り替わります：

```bash
# .sdkmanrcファイルの作成
$ echo "java=17.0.13-ms" > .sdkmanrc

# ディレクトリに入ると自動切り替え
$ cd my-project
Using java version 17.0.13-ms in this shell.
```

#### SDKMANのその他の便利なコマンド

```bash
# 古いバージョンの削除
$ sdk uninstall java 11.0.25-amzn

# SDKMANのアップデート
$ sdk selfupdate

# オフラインモードの切り替え（インターネット接続なしで使用）
$ sdk offline enable

# 環境変数の確認
$ sdk env
```

### トラブルシューティング

#### SDKMANが認識されない場合

```bash
# 初期化スクリプトを再実行
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
```

#### プロキシ環境での使用

企業ネットワークなどプロキシ環境下では、以下の設定が必要です：

```bash
# ~/.sdkman/etc/config に追加
sdkman_proxy_host=proxy.example.com
sdkman_proxy_port=8080
```


## A.3 統合開発環境（IDE）のセットアップ

本書では以下のIDEを推奨します：
- **IntelliJ IDEA**（Community Editionで十分）
- **Visual Studio Code**（Java開発の拡張機能が必須です）

IDEの利用に関しては、IntelliJ IDEA Community Editionがお勧めです。

本書で取り上げる操作説明などは、IntelliJ IDEA Community Editionを基本とします。

### IntelliJ IDEA Community Editionのインストール

1. [JetBrains公式サイト](https://www.jetbrains.com/idea/download/)にアクセス
2. Community Editionを選択してダウンロード
3. ダウンロードしたインストーラを実行
4. 初回起動時の設定で、インストール済みのJDKを指定

### Visual Studio Codeでの開発環境構築

Visual Studio Codeを使用する場合は、以下の拡張機能のインストールが必要です：

1. **Extension Pack for Java** - Microsoft提供のJava開発パック
2. **Debugger for Java** - デバッグ機能
3. **Java Test Runner** - テスト実行支援

## A.4 コマンドラインでの開発

IDEを使わずに、コマンドラインでJavaプログラムを開発する基本的な手順を説明します。

### 作業スペースの作成

ターミナルを開き、以下のコマンドを入力しましょう。

```bash
$ cd                       # ホームディレクトリに移動
$ mkdir java-practice      # java-practiceというディレクトリを作成する
$ cd java-practice         # java-practiceに移動する
```

### プログラムの作成

テキストエディタは何でも良いのですが、ファイル名「```HelloWorld.java```」として、ファイルを新規作成しましょう。
この資料では、標準搭載されているテキストエディタnanoを使用する方法を書きます。

作業スペースを作成した続きで以下のコマンドを使用します。

```bash
$ nano HelloWorld.java
```

以下のコードを書きます。

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!!");
    }
}
```

コードを書き終えたら、ファイルを保存しておきます。

nanoでの保存は、`[control] + [x]`の後、変更を保存するか聞かれるので`[y]`、ファイル名はそのまま`[return]`でOKです。

### コンパイルと実行

1. 以下のコマンドで、指定されたJavaファイルをコンパイルする
    - $ `javac HelloWorld.java`
2. コンパイルが成功すると、メッセージは何も表示されません
    - コンパイルに失敗する場合、ソースコードのどこに間違いがあるかがメッセージで表示されます。
    - コンパイル後にいくつかメッセージが表示されて、.classファイルなどが生成されていない、更新されていない場合はJavaファイルのコードを見直して、間違いがないか確認しましょう（エラーメッセージ内に間違っている箇所のヒントが書かれています。それを読みましょう）
3. ワークスペースのディレクトリをFinder等で見ると、「HelloWorld.class」と言ったクラスファイルが生成されている
    - .classファイルが、コンパイルされたJavaの実行ファイルです。
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

このエラーが出る場合は、PATHが正しく設定されていません。SDKMANを使用している場合は：

```bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
```

### コンパイルエラーの読み方

```
HelloWorld.java:3: error: ';' expected
        System.out.println("Hello, World!!")
                                            ^
1 error
```

- `HelloWorld.java:3`: エラーの場所（ファイル名:行番号）
- `error: ';' expected`: エラーの内容（セミコロンが必要）
- `^`: エラーの具体的な位置

### 文字エンコーディングの問題

日本語を含むソースファイルでエラーが出る場合：

```bash
$ javac -encoding UTF-8 HelloWorld.java
```

## A.6 環境構築チェックリスト

環境構築が正しく完了したことを確認するためのチェックリストです：

- [ ] JDKがインストールされている
  - `java -version`でバージョンが表示される
  - `javac -version`でバージョンが表示される
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
ここでは、プログラムの実行後、キーボードからの入力を待ち受け、Enterキー押下で後続の処理が行われる方法を説明します。

### 標準入力の基本

画面から文字列を入力し、そのまま画面表示するプログラム

ファイル名「```StandardInput.java```」

```java
import java.util.Scanner;  // このライブラリをStandardInputクラスで使う宣言

public class StandardInput {
    public static void main(String[] args) {
        // 標準入力をScannerで取得する
        Scanner in = new Scanner(System.in);
        // nextLine()メソッドは、キーボードからReturnキーの入力があるまで待ち、入力された1行を返す
        String inputLine = in.nextLine();
        // ↑inputLineという変数には、入力された文字列データが設定されています

        // 入力された文字列データをそのまま出力
        System.out.println(inputLine);
    }
}
```

### 標準入力から受けた文字列から数値変換を行う

画面から文字列を入力し、数値に変換して計算するプログラム

ファイル名「```ParseInt.java```」

```java
import java.util.Scanner;  // このライブラリをParseIntクラスで使う宣言

public class ParseInt {
    public static void main(String[] args) {
        // 標準入力をScannerで取得する
        Scanner in = new Scanner(System.in);
        // nextLine()メソッドは、キーボードからReturnキーの入力があるまで待ち、入力された1行を返す
        String inputLine = in.nextLine();
        // ↑inputLineという変数には、入力された文字列データが格納されます

        // 変数numに文字列データを整数値に変換して格納
        int num = Integer.parseInt(inputLine);
        // 整数変換された文字列データに10を足した結果を変数ansに格納
        int ans = num + 10;

        // 変数ansを出力
        System.out.println(ans);
    }
}
```

#### 入力文字列が整数値に変換できない場合

試しに`aa`という文字列を入力してみましょう。

以下のエラーメッセージがでます。
```
Exception in thread "main" java.lang.NumberFormatException: For input string: "aa"
	at java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
	at java.lang.Integer.parseInt(Integer.java:580)
	at java.lang.Integer.parseInt(Integer.java:615)
	at ParseInt.main(ParseInt.java:14)
```

これは、実行時例外と呼ばれるエラーです。
現時点ではこのエラーはでることが正常な動作です。

入力された文字列は整数値に変換できる文字列を想定してプログラミングを行いました。
ですので、「整数値に変換できない文字列が入力されることは想定外なのでエラーが出力される」という正常な動作です。