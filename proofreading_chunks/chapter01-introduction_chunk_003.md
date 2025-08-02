<!-- 
校正チャンク情報
================
元ファイル: chapter01-introduction.md
チャンク: 3/6
行範囲: 368 - 564
作成日時: 2025-08-02 22:02:46

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### なぜこの書き方が必要なのか？

Javaランタイム（javaコマンド）は、指定されたクラスのなかからこの正確なシグネチャを持つ`main`メソッドを探して、プログラムの実行を開始します。

一文字でも違うと認識されません。

長くて面倒に感じる場合もありますが、すべてのキーワードに意味があり、どれも省くことができません。<span class="footnote">Java 21以降では、学習目的のために簡略化されたmainメソッドの記述が可能になりました。`void main() { ... }`のようにstaticやpublic、引数を省略できます。ただし執筆時点で、実務では従来の形式が望ましく、標準として使われています。</span>



### 画面に文字列を出力する方法

Javaでは、画面に文字列を出力する方法がいくつかあります。

#### 1.`System.out.println`
この出力方法は、指定された文字列（String型の変数や式でもOK）を改行コードを末尾につけて出力する方法です。

```java
System.out.println("Hello, World!!");
```

これをC言語でたとえるなら、以下のようになるはずです。

```c
printf("%s\n" , "Hello, World!!");
```

`\n`が自動的に追加されるイメージでよいでしょう。

#### `System.out.print`
これは、`System.out.println`で改行コードを末尾に追加しないバージョンです。

#### `System.out.printf`
C言語ライクに書きたい場合は、printfを使用します。
C言語のprintfと同じような書き方で出力が可能です。

##### サンプルソース

ファイル名「`StandardOutput.java`」

<span class="listing-number">**サンプルコード1-5**</span>

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
#### 実行結果
```
標準出力は、「System.out.printlnメソッド」を使用します。
「System.out.println」は末尾に改行コードも併せて出力します。

「System.out.print」を使うと、末尾に改行は出力されません。改行は\nで出力できます。

C言語のprintf関数のように出力したい場合は、「System.out.printf」を使います。
整数値: 10, 実数値: 3.142593
```

## 標準入出力を扱うサンプルを動かしてみよう

ターミナルやコマンドプロンプトなど、黒い画面で動作するプログラムで、ユーザーからデータの入力ができるようになるためのサンプルコードを実行してみましょう。

<span class="listing-number">**サンプルコード1-6**</span>

```java
import java.util.Scanner;  // Scannerクラスを使用するための宣言

public class InputExample {
    public static void main(String[] args) {
        // Scannerオブジェクトの作成（標準入力から読み取る）
        Scanner scanner = new Scanner(System.in);
        
        // ユーザーへの入力促進メッセージ
        System.out.print("整数値を入力: ");
        
        // 文字列として入力を受け取る
        String input = scanner.nextLine();
        
        // 文字列を整数に変換
        int number = Integer.parseInt(input);
        
        // 変換した値を使った処理の例
        System.out.println("入力された値: " + number);
        System.out.println("2倍の値: " + (number * 2));
        
        // Scannerを閉じる（リソースの解放）
        scanner.close();
    }
}
```

#### 実行結果
入力例として「123」を入力した場合の結果を示します。
```
整数値を入力: 123
入力された値: 123
2倍の値: 246
```

#### このコードのポイント

1. import文
    + `java.util.Scanner`をインポートして入力機能を使用
2. Scanner
    + `new Scanner(System.in)`で標準入力から読み取るScannerを作成
3. 入力の受け取り
    + `scanner.nextLine()`で1行分の文字列を取得
4. 型変換
    + `Integer.parseInt()`で文字列を整数に変換
5. リソース管理
    + 使い終わったら`scanner.close()`でリソースを解放

基本的な構文やクラスなどはimportで書かなくても、自動でimport済みです。

- `java.lang`パッケージ（`String`, `System`, `Integer`など）は自動的に利用可能
    - `System.out.println()`や`Integer.parseInt()`にはimport文は不要



> **コラム：Integer.parseIntのエラーから学ぶ**{.column-section}
> 
> ### プログラミングにおけるエラーとの向き合い方
> 
> サンプルコード1-15でScannerを使った入力処理を学習しました。ここで気になるのは、`Integer.parseInt()`で文字列を整数に変換する際のエラーです。数値以外の文字列を入力してEnterキーを押すと、ずらずらとエラーが出て、実行していたプログラムは停止します。これは正しい動作です。
> 
> #### よくあるエラーの例
> 
> ```java
> String input = "";  // ユーザーが何も入力せずEnterを押した場合
> int number = Integer.parseInt(input);  // ここでエラー発生！
> ```
> 
> このような場合、以下のエラーが発生します。
> 
> ```
> Exception in thread "main" java.lang.NumberFormatException: For input string: ""
>     at java.base/java.lang.Integer.parseInt(Integer.java:662)
> ```
> 
> #### エラーメッセージの読み方
> 
> プログラミング初学者にとって、このようなエラーメッセージは怖く見えることがあります。しかし、実はエラーメッセージは「なぜ動かないか」を教えてくれる親切なメッセージなのです。
> 
> - `NumberFormatException`
>     + 数値の形式が正しくないという例外
> - `For input string: ""`
>     + 空文字列を数値に変換しようとした
> - スタックトレース
>     + エラーが発生した場所の履歴
> 
> #### 第1章での学習ポイント
> 
> 現時点では、以下の点を押さえておくとよいでしょう。
> 
> 今は気にしなくてよいこと
> - ユーザーが数字以外を入力する可能性への対処
> - 空文字列や特殊文字への対応
> - すべての異常入力をカバーする防御的プログラミング
> 
> なぜ気にしなくてよいのか
> 「整数値を入力してください」と書いてあるのに整数値以外が入力されることは考えなくてよいです。
> 
> また、プログラミング学習は段階的に進めることがポイントです。
> 第1章では「正しく動くプログラム」を作る喜びを体験することに焦点を当てましょう。
> さまざまな入力に対するエラー処理の詳細は第14章「例外処理」で体系的に学習します。
> 
> #### 実務での対処法（参考）
> 
> 実際の開発では、以下のような対処をします（詳細は後の章で）。
> 
> ```java
> try {
>     int number = Integer.parseInt(input);
>     // 正常処理
> } catch (NumberFormatException e) {
>     System.out.println("数値を入力してください");
> }
> ```
> 
> ポイント：エラーは学習の機会です。エラーメッセージは敵ではなく、問題解決のヒントを与えてくれる味方として捉えると、プログラミングがより楽しくなります。





## 章末演習



<!-- 
================
チャンク 3/6 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
