# はじめに

本書は、C言語の基礎を学んだ方を対象としたJava入門書です。

## 本書の対象読者

- C言語でプログラミングの基礎を学んだ方
- オブジェクト指向プログラミングを学びたい方
- モダンなJavaの機能を習得したい方

## 本書の注意事項

2. 資料などは基本的にMac OS(Appleチップ搭載のMacBook Air)を用いることを基準に作成されている
    - WindowsやLinuxなどの他のOSでも問題なく開発が進められますが、細かなところまで他の環境への対応を網羅できていません。ご了承ください。


## 本書で学べること

- C言語との違いを意識したJavaの基本文法
- オブジェクト指向プログラミングの考え方と実践
- コレクションフレームワークとジェネリクス
- ラムダ式とStream APIなどのモダンJava機能
- GUIアプリケーションの開発
- 外部ライブラリの活用方法

## Javaの特徴

Javaは以下のような特徴を持つプログラミング言語です：

- **プラットフォーム独立性**：「Write Once, Run Anywhere」
- **オブジェクト指向**：クラスベースのオブジェクト指向言語
- **ガベージコレクション**：自動メモリ管理
- **強い型付け**：コンパイル時の型チェック
- **豊富なライブラリ**：標準ライブラリとサードパーティライブラリ

## 開発環境の準備
### JDK(JavaSDK)

プログラミング言語Javaの開発環境をJava Software Development Kitと呼び、JavaSDKやJDKなどと略されます。  
Javaでの開発は、複数あるJDKのどれかをインストールすることから始まります。

Javaでの開発にオススメのIDEである、IntelliJ IDEAからもJDKをインストールできるようになっています。  
ですが、本書では、本書の内容以外でもJDKを使った開発ができるように、一括でバージョン管理が可能なソフトウェアを使用して必要なバージョンのJDKをインストールします。

### JDKの種類

JDKにはさまざまな提供元があり、インストールするJDKの種類が異なることによって講義を円滑に進められない場合もあります。
そのため、講義で使用するJDKはこの資料で指定しているものを必ずインストールするようにしてください。

#### JDKの種類が混乱するほど増えている理由

JDKと一言で言っても、開発元であるOracle以外にもいくつかの企業や団体がJDKとしてリリースしているものがあり、その配布元ごとにさまざまなバージョンのものがあります。

まず、Javaはオープンソースソフトウェアです。  
(単純にソフトウェアとしての側面の他に、エコシステムとしての側面もあります)

Javaの商標は、開発元であるOracleが保持しています。なので、本来はJava™のような書き方をするのが正しいのかもしれません。

JDKの種類が多い理由として、オープンソースソフトウェアである点が第一に挙げられます。商標自体はOracleが保持していますが、JDK自体のソースコードの取得は開かれているので、それをビルドしてリリースする企業やコミュニティの数だけ、JDKの種類が増えます。
JDKの基本的な機能がその種類ごとで大きく異なってしまうということはあまり考えにくいのですが、状況によっては選んだJDKによって動作が異なることもあり得ます。
Javaでの開発を始める前に、開発を行う企業やコミュニティで使用されているJDKを確認して、それに合わせるように心がけましょう。

### 開発環境のインストールを行います

本書で使用するJavaのバージョンは、OpenJDKと呼ばれるオープン版のJDKを使用し、以下の開発バージョンを使います。

- `OpenJDK 21.0.6 (Microsoft)`

※本書執筆時点のLTS版（長期サポート対応バージョン）の中でもっとも新しいものを使用しています。


### SDKMANを使用したバージョン管理

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


## 複数バージョンのJDKをインストールした場合

SDKMANをインストールして、SDKMAN経由でJDKをインストールした場合は、SDKMANが使用するバージョンをコントロールしてくれるため、設定ファイルを細々と書き換える必要はありません。

SDKMANでJDKのバージョンを切り替えたい場合は、以下のように行います。

```bash
sdk list java                  # インストール可能なバージョンとインストール済みのバージョンを確認
sdk use java 21.0.6-ms         # 現在のターミナルで使用するバージョンを設定
sdk default java 21.0.6-ms     # 標準で使用するバージョンを設定
```


### 統合開発環境（IDE）

本書では以下のIDEを推奨します：
- **IntelliJ IDEA**（Community Editionで十分）
- **Visual Studio Code**（Java開発の拡張機能が必須です）

IDEの利用に関しては、IntelliJ IDEA Community Editionがオススメです。

本書で取り上げる操作説明などは、IntelliJ IDEA Community Editionを基本とします。

---
title: "プログラムを書いてみよう"
---

> オブジェクト指向プログラミングおよび演習1 第1回


Javaの開発環境構築やIDEのインストールが完了したところだと思います。  
まずは、JDKのみを使用し、IDEを使わずにJavaのプログラムを作成する方法を知りましょう。

このページの内容に限り、ターミナル上でプログラムを書きます。  
では、macOSに標準搭載されている簡易なテキストエディタである `nano` を使って簡単なプログラムを書いてみましょう。

## 作業スペースの作成

ターミナルを開き、以下のコマンドを入力しましょう。

```bash
$ cd                       # ホームディレクトリに移動
$ mkdir java-practice      # programming-practiceというディレクトリを作成する
$ cd java-practice         # oop1に移動する
```

## 画面に「Hello, World!!」を表示するプログラム

テキストエディタは何でも良いのですが、ファイル名「```HelloWorld.java```」として、ファイルを新規作成しましょう。
この資料では、標準搭載されているテキストエディタnanoを使用する方法を書きます。

作業スペースを作成した続きで以下のコマンドを使用します。

```bash
$ nano HelloWorld.java
```

以下のコードを書きます。

### HelloWorld.java

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!!");
    }
}
```

コードを書き終えたら、ファイルを保存しておきます。

nanoでの保存は、`[control] + [x]`の後、変更を保存するか聞かれるので`[y]`、ファイル名はそのまま`[return]`でOKです。


# コンパイルと実行


1. 以下のコマンドで、指定されたJavaファイルをコンパイルする
    - $ `javac HelloWorld.java`
2. コンパイルが成功すると、メッセージはなにも表示されません
    - コンパイルに失敗する場合、ソースコードのどこに間違いがあるかがメッセージで表示されます。
    - コンパイル後にいくつかメッセージが表示されて、.classファイルなどが生成されていない、更新されていない場合はJavaファイルのコードを見直して、間違いがないか確認しましょう（エラーメッセージ内に、間違っている箇所のヒントが書かれていますので、それを読むことをオススメします）
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

## ターミナルからプログラムにデータを渡す方法を知っておきましょう

Javaプログラムでのプログラムへのデータの受け渡し方法は、さまざまな方法があります。
今回は、プログラムの実行後、キーボードからの入力を待ち受け、Enterキー押下で後続の処理が行われる方法を学びます。

### サンプルソース

#### 標準入力

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

#### 標準入力から受けた文字列から数値変換を行う

画面から文字列を入力し、そのまま画面表示するプログラム

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

##### 入力文字列が整数値に変換できない場合
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
なので、「整数値に変換できない文字列が入力されることは想定外なのでエラーが出力される」という正常な動作です。

このエラーは、`Integer.parseInt("aa")`という処理にて`java.lang.NumberFormatException`という例外が発生しており、数字の表現に問題があることを差しています。
これは、渡される文字列側に問題があり、その文字列を入力しているのはプログラムを実行するユーザーです。
この問題は、プログラムを作成したプログラマが想定していない問題であり、今回のプログラムではそれを想定しないように書いています。

このエラーを出ないようにするには、そもそも数字以外を入力できないようにするなどの事前のエラー処理や例外処理と言った制御が必要になります。
現時点ではそこまでを求めていないので、エラー処理、例外処理の内容は後に解説します。

もう少し詳しく解説をすると、`Integer.parseInt("")`という処理は、引数で渡された文字を符号付き10進数の整数型として構文解析します。
この時、内部的には`Integer.parseInt("", 10)`の処理内容と同じことをしています。  
Oracleの公式ドキュメントによると、以下の条件で`java.lang.NumberFormatException`という例外が発生します。と解説がされています。

- 1番目の引数がnullであるか、長さゼロの文字列。
- radixがCharacter.MIN_RADIXよりも小さいか、Character.MAX_RADIXよりも大きい。
- 文字列の中に、指定された基数による桁には使えない文字がある。ただし、文字列の長さが1よりも大きい場合は、1番目の文字がマイナス記号'-' ('\u002D')またはプラス記号'+' ('\u002B')であってもかまわない。
- 文字列によって表される値が、int型の値ではない。

参考: [Integer (Java SE 21 & JDK 21)](https://docs.oracle.com/javase/jp/21/docs/api/java.base/java/lang/Integer.html#parseInt(java.lang.String,int))



### 画面に文字列を出力する方法

Javaでは、画面に文字列を出力する方法として、いくつかの方法があります。

#### 1.`System.out.println`
この出力方法は、指定された文字列（String型の変数や式でもOK）を改行コードを末尾につけて出力する方法です。

```java
System.out.println("Hello, World!!");
```

これをC言語で例えるなら、以下のようになるはずです。

```c
printf("%s\n" , "Hello, World!!");
```

`\n`が自動的に追加されるイメージで良いでしょう。

#### 2. `System.out.print`
これは、`System.out.println`で改行コードを末尾に追加しないバージョンです。

#### 3. `System.out.printf`
C言語ライクに書きたい場合は、printfを使用してください。
C言語のprintfと同じような書き方で出力が可能です。

##### サンプルソース

ファイル名「`StandardOutput.java`」

```java
public class StandardOutput {
    public static void main(String[] args) {
        System.out.println("標準出力は、「System.out.printlnメソッド」を使用します。");
        System.out.println("「System.out.println」は末尾に改行コードも併せて出力します。");
        System.out.println(); // 引数なしで改行のみ出力されます。
        System.out.print("「System.out.print」を使うと、末尾に改行は出力されません。");
        System.out.print("改行は\\nで出力できます。\n");
        System.out.print("\n");
        String message = "C言語のprintf関数のように出力したい場合は、「System.out.printf」を使います。";
        System.out.printf("%s\n", message);
        System.out.printf("整数値: %d, 実数値: %f\n", 10, 3.142592654d);
    }
}
```


## GUIアプリケーションのサンプルを動かしてみよう

ボタン押下でメッセージの変更を行うサンプル

```java
import javax.swing.*;
import java.awt.*;

public class HelloGUIApp extends JFrame {

    private JLabel label;
    private JButton button;

    public HelloGUIApp() {
        // JFrameの初期設定
        setTitle("Hello GUI App!!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ウィンドウのサイズを設定
        setSize(300, 150);
        setLocationRelativeTo(null); // 画面中央に表示

        // レイアウトマネージャの設定
        setLayout(new GridLayout(2, 1));

        // ラベルの作成と初期テキストの設定
        label = new JLabel("こんにちは");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);

        // ボタンの作成とActionListenerの設定
        button = new JButton("押して");
        button.addActionListener(e -> {
            label.setText("Hello, OOP!!");
        });
        add(button);

        // ウィンドウを表示
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HelloGUIApp());
    }
}
```

名前の入力欄を追加し、ボタン押下で挨拶を行うサンプル

```java
import javax.swing.*;
import java.awt.*;

public class GreetingApp extends JFrame {

    private JLabel messageLabel;
    private JTextField nameTextField;
    private JButton greetButton;

    public GreetingApp() {
        // ウィンドウの基本設定
        setTitle("名前入力");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 200);
        setLocationRelativeTo(null);

        // レイアウトマネージャーを GridLayout に設定（4行1列）
        setLayout(new GridLayout(4, 1));

        // ラベル（説明）の作成
        JLabel nameLabel = new JLabel("名前を入力してください:");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER); // テキストを中央揃え
        add(nameLabel);

        // テキストボックスの作成
        nameTextField = new JTextField(15);
        add(nameTextField);

        // メッセージ表示用ラベルの作成
        messageLabel = new JLabel("");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(messageLabel);

        // ボタンの作成とActionListenerの設定
        greetButton = new JButton("挨拶する");
        greetButton.addActionListener(e -> {
            String name = nameTextField.getText();
            if (!name.isEmpty()) {
                messageLabel.setText("こんにちは！" + name + "さん");
            } else {
                messageLabel.setText("名前を入力してください");
            }
        });
        add(greetButton);

        // ウィンドウを表示
        setVisible(true);
    }

    public static void main(String[] args) {
        // イベントディスパッチスレッドでGUIを作成・実行
        SwingUtilities.invokeLater(() -> new GreetingApp());
    }
}
```
