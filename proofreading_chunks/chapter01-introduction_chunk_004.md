<!-- 
校正チャンク情報
================
元ファイル: chapter01-introduction.md
チャンク: 4/6
行範囲: 565 - 748
作成日時: 2025-08-02 22:02:46

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されます。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter01/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

#### 実装のポイント
- クラス名とファイル名を一致させる
- mainメソッドのシグネチャを正確に記述
- System.out.println()で文字列を出力

#### Exercise02_SelfIntroduction.java  

目的： 変数を使った自己紹介プログラムの作成

##### 実装のポイント
- String型で名前を格納
- int型で年齢を格納
- 文字列連結（+演算子）を使って出力

#### Exercise03_BasicCalculation.java

目的： 基本的な算術演算の実践

##### 実装のポイント
- int型での整数演算
- double型での小数演算
- 整数除算の結果に注意（5 / 2 = 2になる）

#### Exercise04_DataTypes.java

目的： 各種データ型の理解と使い分け

##### 実装のポイント
- boolean型でtrue/falseを扱う
- char型で1文字を扱う（シングルクォート使用）
- float型とdouble型の違いを理解（fサフィックス）

### 効果的な学習の進め方は次の通りです

1. まずは自分で考える
   - エラーメッセージをしっかり読む
   - 本章のサンプルコードを参考にする
   - 5-10分考えてわからなければヒントを見る

2. コンパイルエラーが出たら
   - エラーメッセージの行番号を確認
   - セミコロン忘れ、大文字小文字の間違いをチェック
   - クラス名とファイル名の一致を確認

3. 動作確認のコツ
   - 期待する出力と実際の出力を比較
   - 変数の値を途中でprintlnを使って確認
   - 小さな部分から動作確認していく

### よくあるトラブルと解決方法

| 問題 | 原因 | 解決方法 |
|------|------|----------|
| "class not found" | コンパイルされていない | `javac ファイル名.java`を実行 |
| "cannot find symbol" | 変数名やメソッド名の間違い | スペルと大文字小文字を確認 |
| "';' expected" | セミコロン忘れ | 該当行の末尾にセミコロンを追加 |
| 文字化け | エンコーディングの問題 | UTF-8でファイルを保存 |

### 発展課題へのステップアップ

基礎課題を完了したら、発展課題に挑戦してみましょう。発展課題では、
- 複数の変数を組み合わせた処理
- ユーザー入力の受け取り（Scanner使用）
- より実用的なプログラムの作成

を学習します。難易度は上がりますが、基礎課題の知識を応用すれば解決できます。

#### 次のステップ
基礎課題が完了したら、第2章「Java基本文法」に進みましょう。第2章では、より本格的なオブジェクト指向プログラミングの世界に入っていきます。

> ※ 本章の高度な内容については、付録B.01「JVMアーキテクチャとバイトコード」（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b01-jvm-architecture/`）を参照してください。

## よくあるエラーと対処法

Java開発環境を構築し、はじめてのプログラムを作成する際によく遭遇するエラーとその対処法をまとめます。

### 環境構築関連のエラー

#### JDKが見つからない

##### エラーメッセージ

```
'java' is not recognized as an internal or external command
```

##### 原因と対処

- JDKがインストールされていない、またはPATHが設定されていない
- 対処法は以下の通りです
  1. JDKをインストール（推奨：OpenJDK 21以上）
  2. 環境変数PATHにJDKのbinディレクトリを追加
  3. コマンドプロンプトで`java -version`を実行して確認

### コンパイル関連のエラー

#### クラス名とファイル名の不一致

##### エラーメッセージ
```
error: class HelloWorld is public, should be declared in a file named HelloWorld.java
```

##### 原因と対処
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

#### mainメソッドのシグネチャが間違っている

##### エラーメッセージ

```
Error: Main method not found in class HelloWorld
```

##### 原因と対処

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

### 実行時エラー

#### クラスファイルが見つからない

##### エラーメッセージ
```
Error: Could not find or load main class HelloWorld
```

##### 原因と対処

```
# エラー例：コンパイルせずに実行しようとした
java HelloWorld.java  # 間違い

# 修正版：コンパイルしてから実行
javac HelloWorld.java  # コンパイル
java HelloWorld        # 実行（.classは不要）
```

#### NumberFormatExceptionと入力処理

##### エラーメッセージ

```
Exception in thread "main" java.lang.NumberFormatException: For input string: "abc"
```



<!-- 
================
チャンク 4/6 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
