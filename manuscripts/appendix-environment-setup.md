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

JDKのインストールの前に、JDKやJavaの開発ライブラリのバージョン別インストールをサポートしてくれるバージョン管理ツールをインストールします。
SDKMAN自体についてや各環境に合わせたインストール方法など、詳しくは以下のリンク先をみてください。

- [SDKMAN!](https://sdkman.io/)

### SDKMANを使用したJDKインストール

1. ターミナルを起動
2. $ `sdk list java` で現在インストール可能なJavaを一覧表示できます（開かれたテキストはvi形式です。次の行はエンターキー、次のページはスペースキー、閉じる場合は`:q`と入力します）
3. $ `sdk install java 21.0.6-ms` でJDKがインストールされます。
4. $ `java -version` でインストールされたJDKのバージョンが正しいか確認します。

以下のようにバージョン情報が出力されていればOKです。

```
openjdk 21.0.6 2025-01-21 LTS
OpenJDK Runtime Environment Microsoft-10800196 (build 21.0.6+7-LTS)
OpenJDK 64-Bit Server VM Microsoft-10800196 (build 21.0.6+7-LTS, mixed mode, sharing)
```

これでJDKの用意は完了です。


### 複数バージョンのJDKをインストールした場合

SDKMANをインストールして、SDKMAN経由でJDKをインストールした場合は、SDKMANが使用するバージョンをコントロールしてくれるため、設定ファイルを細々と書き換える必要はありません。

SDKMANでJDKのバージョンを切り替えたい場合は、以下のように行います。

```bash
sdk list java                  # インストール可能なバージョンとインストール済みのバージョンを確認
sdk use java 21.0.6-ms         # 現在のターミナルで使用するバージョンを設定
sdk default java 21.0.6-ms     # 標準で使用するバージョンを設定
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