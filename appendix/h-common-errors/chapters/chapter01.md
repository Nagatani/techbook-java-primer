# 第1章 Java入門と開発環境構築：よくあるエラーと対処法

書籍『Java入門』第1章「Java入門と開発環境構築」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

Java開発環境を構築し、はじめてのプログラムを作成する際によく遭遇するエラーとその対処法をまとめます。

## 環境構築関連のエラー

### JDKが見つからない

#### エラーメッセージ

```
'java' is not recognized as an internal or external command
```

#### 原因と対処

- JDKがインストールされていない、またはPATHが設定されていない
- 対処法は以下の通りです
  1. JDKをインストール（推奨：OpenJDK 21以上）
  2. 環境変数PATHにJDKのbinディレクトリを追加
  3. コマンドプロンプトで`java -version`を実行して確認

## コンパイル関連のエラー

### クラス名とファイル名の不一致

#### エラーメッセージ
```
error: class HelloWorld is public, should be declared in a file named HelloWorld.java
```

#### 原因と対処

```java
// エラー例：ファイル名が「Sample.java」だが、クラス名がHelloWorld
public class HelloWorld {  // クラス名とファイル名が一致しない
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
// 修正版1：ファイル名をHelloWorld.javaに変更
// 修正版2：クラス名をSampleに変更
public class Sample {
    public static void main(String[] args) {
        System.out.println("Hello");
    }
}
```

### mainメソッドのシグネチャが間違っている

#### エラーメッセージ

```
Error: Main method not found in class HelloWorld
```

#### 原因と対処

```java
// エラー例：mainメソッドのシグネチャが間違っている
public class HelloWorld {
    public void main(String[] args) {  // staticが抜けている
        System.out.println("Hello");
    }
}
// 修正版：正しいmainメソッドのシグネチャ
public class HelloWorld {
    public static void main(String[] args) {  // staticを追加
        System.out.println("Hello");
    }
}
```

## 実行時エラー

### クラスファイルが見つからない

#### エラーメッセージ
```
Error: Could not find or load main class HelloWorld
```

#### 原因と対処

```
# エラー例：コンパイルせずに実行しようとした（HelloWorld.classがない）
java HelloWorld

# 修正版：コンパイルしてから実行
javac HelloWorld.java  # コンパイル
java HelloWorld        # 実行（.classは不要）
```

`java HelloWorld.class`のように拡張子を付けて実行した場合も、クラスが見つからないというエラーになります。
なお、Java 11以降では、`java HelloWorld.java`のようにソースファイルを指定して、コンパイルせずに直接実行することもできます。
ただし、この方法が使えるのは1つのファイルで完結するプログラムに限られるため、本書では`javac`でコンパイルしてから`java`で実行する手順を基本とします。

### NumberFormatExceptionと入力処理

#### エラーメッセージ

```
Exception in thread "main" java.lang.NumberFormatException: For input string: "abc"
```

#### 原因と対処

```java
// エラー例：数値以外の入力を数値に変換しようとした
Scanner scanner = new Scanner(System.in);
System.out.print("数値を入力: ");
String input = scanner.nextLine();
int number = Integer.parseInt(input);  // "abc"を入力するとエラー

// 修正版：入力値の検証（第1章レベルでの対処）
Scanner scanner = new Scanner(System.in);
System.out.print("数値を入力: ");
String input = scanner.nextLine();

// 簡単な数値チェック
if (input.matches("\\d+")) {  // 数字のみの場合
    int number = Integer.parseInt(input);
    System.out.println("入力された数値: " + number);
} else {
    System.out.println("数値を入力してください");
}
```

## IDE関連のトラブル

### IntelliJ IDEAでのプロジェクト設定

#### 問題

新規作成したプロジェクトが正しく動作しません。

#### 対処法

1. プロジェクトSDKの設定
   - File → Project Structure → Project → Project SDK
   - インストールしたJDKを選択
2. モジュール設定の確認
   - File → Project Structure → Modules
   - Sourcesタブでsrcフォルダがソースルートに設定されているか確認
3. エンコーディングの設定
   - File → Settings → Editor → File Encodings
   - Global EncodingとProject EncodingをUTF-8に設定

## コマンドライン実行のコツ

### 基本的なコンパイル・実行手順

```
# 1. ソースコードの作成
# HelloWorld.javaファイルを作成

# 2. コンパイル
javac HelloWorld.java

# 3. 実行
java HelloWorld

# 4. 複数ファイルの場合
javac *.java        # 全てのJavaファイルをコンパイル
java MainClass      # メインクラスを実行
```

### パッケージを使用している場合

```
# パッケージ com.example のクラスの場合
javac com/example/HelloWorld.java
java com.example.HelloWorld
```

## デバッグの基本

### println()を使ったデバッグ

コンパイルできる全文：[chapter01-code/DebugExample/](chapter01-code/DebugExample/)

```java
public class DebugExample {
    public static void main(String[] args) {
        int a = 10;
        int b = 20;
        
        System.out.println("a = " + a);  // デバッグ出力
        System.out.println("b = " + b);  // デバッグ出力
        
        int sum = a + b;
        System.out.println("sum = " + sum);  // 計算結果を確認
    }
}
```
### 実行結果
```
a = 10
b = 20
sum = 30
```

### 段階的な実行確認

コンパイルできる全文：[chapter01-code/StepByStepDebug/](chapter01-code/StepByStepDebug/)

```java
public class StepByStepDebug {
    public static void main(String[] args) {
        System.out.println("プログラム開始");
        
        int x = 5;
        System.out.println("x に 5 を代入しました");
        
        int y = x * 2;
        System.out.println("y に x * 2 の結果を代入しました: " + y);
        
        System.out.println("プログラム終了");
    }
}
```
### 実行結果
```
プログラム開始
x に 5 を代入しました
y に x * 2 の結果を代入しました: 10
プログラム終了
```

## 初心者向けベストプラクティス

プログラミングに不慣れな人は以下の対処法を実践しましょう。

1. エラーメッセージを恐れない
   - エラーメッセージは問題解決のヒントなので必ず読む
   - エラーの内容から行番号を確認してエラーの場所をある程度特定できる

2. 小さなステップで進む
   - 一度に多くのコードを書かない
   - 少し書いては動作確認をし、「動作確認ができたら次へ進む」を頻繁に行う

3. コーディング規約を守る
   - コーディング規約は間違いを発生させない工夫がされている
   - クラス名は大文字で始める（PascalCase）
   - 変数名は小文字で始める（camelCase）
   - インデント（字下げ）を適切に使う

4. コメントを活用する
   - コードの意図を日本語で説明する
   - 複雑な処理には必ずコメントを追加して自分の理解を補助しよう
   - 慣れるまではすべての行にコメントを書く勢いで書いてみる
        + サンプルコードの実行確認後は、1行1行すべての行の命令1つ1つを全部コメントで説明しながらコードを書くこと
        + まずは自分が書いているコードにどんな意味があるかを知ることが大事

これらの対処法を参考に、Java開発環境を常に使える状態で学習を進めましょう。
