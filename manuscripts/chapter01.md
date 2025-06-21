# 第1章 Javaの基本文法とSDK環境構築

## 1.1 Javaの特徴

Javaは以下のような特徴を持つプログラミング言語です：

- **プラットフォーム独立性**：「Write Once, Run Anywhere」
- **オブジェクト指向**：クラスベースのオブジェクト指向言語
- **ガベージコレクション**：自動メモリ管理
- **強い型付け**：コンパイル時の型チェック
- **豊富なライブラリ**：標準ライブラリとサードパーティライブラリ

## 1.2 開発環境の準備
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
（単純にソフトウェアとしての側面の他に、エコシステムとしての側面もあります）

Javaの商標は、開発元であるOracleが保持しています。なので、本来はJava™のような書き方をするのが正しいのかもしれません。

JDKの種類が多い理由として、オープンソースソフトウェアである点が第一に挙げられます。商標自体はOracleが保持していますが、JDK自体のソースコードの取得は開かれているので、それをビルドしてリリースする企業やコミュニティの数だけ、JDKの種類が増えます。
JDKの基本的な機能がその種類ごとで大きく異なってしまうということはあまり考えにくいのですが、状況によっては選んだJDKによって動作が異なることもあり得ます。
Javaでの開発を始める前に、開発を行う企業やコミュニティで使用されているJDKを確認して、それに合わせるように心がけましょう。

### 開発環境のインストールを行います

本書で使用するJavaのバージョンは、OpenJDKと呼ばれるオープン版のJDKを使用し、以下の開発バージョンを使います。

- `OpenJDK 21.0.6 (Microsoft)`

※本書執筆時点のLTS版（長期サポート対応バージョン）の中でもっとも新しいものを使用しています。

## 1.3 SDKMANを使用したバージョン管理

JDKのインストールの前に、JDKやJavaの開発ライブラリのバージョン別インストールをサポートしてくれるバージョン管理ツールをインストールします。
SDKMAN自体についてや各環境に合わせたインストール方法など、詳しくは以下のリンク先をみてください。

- [SDKMAN!](https://sdkman.io/)

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

macOSのターミナルソフトウェアとして、zshを使用している場合は以下のコマンドを使用します。（`$`は入力しません）

```zsh
$ echo 'export JAVA_HOME=$HOME/.sdkman/candidates/java/current' >> ~/.zprofile
$ echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.zprofile
$ source ~/.zprofile
```

これらの環境変数の設定は、他のJavaを参照するアプリケーションのために設定しておきます。

#### 再履修者向け: SDKMANのアップグレード

すでにSDKMANをインストール済みの場合は、以下のコマンドでセルフアップグレードを行ってください。

```bash
$ sdk selfupdate force
```

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

## 1.4 複数バージョンのJDKをインストールした場合

SDKMANをインストールして、SDKMAN経由でJDKをインストールした場合は、SDKMANが使用するバージョンをコントロールしてくれるため、設定ファイルを細々と書き換える必要はありません。

SDKMANでJDKのバージョンを切り替えたい場合は、以下のように行います。

```bash
sdk list java                  # インストール可能なバージョンとインストール済みのバージョンを確認
sdk use java 21.0.6-ms         # 現在のターミナルで使用するバージョンを設定
sdk default java 21.0.6-ms     # 標準で使用するバージョンを設定
```

## 1.5 統合開発環境（IDE）

本書では以下のIDEを推奨します：
- **IntelliJ IDEA**（Community Editionで十分）
- **Visual Studio Code**（Java開発の拡張機能が必須です）

IDEの利用に関しては、IntelliJ IDEA Community Editionがオススメです。

本書で取り上げる操作説明などは、IntelliJ IDEA Community Editionを基本とします。

## 1.6 Hello Worldプログラム

Javaの開発環境構築やIDEのインストールが完了したところだと思います。  
まずは、JDKのみを使用し、IDEを使わずにJavaのプログラムを作成する方法を知りましょう。

このページの内容に限り、ターミナル上でプログラムを書きます。  
では、macOSに標準搭載されている簡易なテキストエディタである `nano` を使って簡単なプログラムを書いてみましょう。

### 作業スペースの作成

ターミナルを開き、以下のコマンドを入力しましょう。

```bash
$ cd                       # ホームディレクトリに移動
$ mkdir java-practice      # programming-practiceというディレクトリを作成する
$ cd java-practice         # oop1に移動する
```

### 画面に「Hello, World!!」を表示するプログラム

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

### C言語版
```c
#include <stdio.h>

int main() {
    printf("Hello, World!\n");
    return 0;
}
```

### Java版
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### 主な違い

- **クラスベース**：Javaはすべてクラス内に記述
- **main関数**：`public static void main(String[] args)`の形式
- **出力**：`printf`の代わりに`System.out.println`
- **ファイル名**：クラス名と同じファイル名が必要

## 1.7 コンパイルと実行

1. 以下のコマンドで、指定されたJavaファイルをコンパイルする
    - $ `javac HelloWorld.java`
2. コンパイルが成功すると、メッセージはなにも表示されません
    - コンパイルに失敗する場合、ソースコードのどこに間違いがあるかがメッセージで表示されます。
    - コンパイル後にいくつかメッセージが表示されて、.classファイルなどが生成されていない、更新されていない場合はJavaファイルのコードを見直して、間違いがないか確認しましょう（エラーメッセージ内に間違っている箇所のヒントが書かれています。それを読みましょう。）
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

## 1.8 基本的な入出力

Javaプログラムでのプログラムへのデータの受け渡し方法は、さまざまな方法があります。
今回は、プログラムの実行後、キーボードからの入力を待ち受け、Enterキー押下で後続の処理が行われる方法を学びます。

### 標準入力

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

## 1.9 データ型

### 基本データ型の比較

| C言語 | Java | 説明 |
|-------|------|------|
| int | int | 32ビット整数 |
| long | long | 64ビット整数 |
| float | float | 32ビット浮動小数点数 |
| double | double | 64ビット浮動小数点数 |
| char | char | 16ビットUnicode文字 |
| - | boolean | 真偽値（true/false） |

### 文字列の扱い

```java
// C言語風（推奨されない）
char[] chars = {'H', 'e', 'l', 'l', 'o'};

// Java推奨
String message = "Hello, World!";
String name = "Java";
String greeting = message + " " + name;  // 文字列連結
```

## 1.3 変数と定数

```java
// 変数宣言
int number = 10;
double price = 99.99;
boolean isActive = true;

// 定数（final修飾子）
final int MAX_VALUE = 100;
final String COMPANY_NAME = "TechBook";
```

## 1.4 演算子

Javaの演算子はC言語とほぼ同じですが、いくつかの違いがあります：

```java
// 算術演算子（C言語と同じ）
int a = 10, b = 3;
int sum = a + b;        // 13
int diff = a - b;       // 7
int product = a * b;    // 30
int quotient = a / b;   // 3
int remainder = a % b;  // 1

// 文字列連結（Javaの特徴）
String result = "合計: " + sum;

// 比較演算子
boolean isEqual = (a == b);     // false
boolean isNotEqual = (a != b);  // true

// 論理演算子
boolean result1 = (a > 5) && (b < 10);  // true
boolean result2 = (a < 5) || (b > 10);  // false
```

## 1.5 制御構造

### 条件分岐

```java
// if文（C言語と同じ）
int score = 85;
if (score >= 90) {
    System.out.println("優");
} else if (score >= 80) {
    System.out.println("良");
} else if (score >= 70) {
    System.out.println("可");
} else {
    System.out.println("不可");
}

// switch文（Java 12以降の新記法も利用可能）
String grade = switch (score / 10) {
    case 10, 9 -> "優";
    case 8 -> "良";
    case 7 -> "可";
    default -> "不可";
};
```

### 繰り返し処理

```java
// for文
for (int i = 0; i < 10; i++) {
    System.out.println("i = " + i);
}

// 拡張for文（foreach）
int[] numbers = {1, 2, 3, 4, 5};
for (int num : numbers) {
    System.out.println(num);
}

// while文
int count = 0;
while (count < 5) {
    System.out.println("count = " + count);
    count++;
}
```

## 1.6 配列

```java
// 配列の宣言と初期化
int[] numbers = {1, 2, 3, 4, 5};

// または
int[] scores = new int[10];  // 10要素の配列
scores[0] = 100;
scores[1] = 95;

// 配列の長さ
System.out.println("配列の長さ: " + numbers.length);

// 多次元配列
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};
```

## 1.7 メソッド（関数）

```java
public class Calculator {
    // メソッドの定義
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static double divide(double a, double b) {
        if (b == 0) {
            System.out.println("ゼロ除算エラー");
            return 0;
        }
        return a / b;
    }
    
    public static void main(String[] args) {
        int result = add(10, 5);
        System.out.println("結果: " + result);
        
        double divResult = divide(10.0, 3.0);
        System.out.println("除算結果: " + divResult);
    }
}
```

## 1.8 練習問題

1. 1から100までの和を計算するプログラムを作成してください。
2. 配列内の最大値を見つけるメソッドを作成してください。
3. 九九の表を表示するプログラムを作成してください。

## まとめ

この章では、C言語との比較を通じてJavaの基本文法を学習しました。次章では、Javaの最大の特徴であるオブジェクト指向プログラミングについて学習します。