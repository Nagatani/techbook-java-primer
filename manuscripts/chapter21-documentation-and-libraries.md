# 第21章 ドキュメンテーションと外部ライブラリ

## 

本章で学ぶJavadocと外部ライブラリの利用は、**総合演習プロジェクト「ToDoリストアプリケーション」** を、より本格的で、ほかの開発者にも理解しやすいプロジェクトへと進化させるための重要なステップです。

- **JavadocによるAPI仕様書作成**: `Task`クラスや`TaskRepository`インターフェイスなどにJavadocコメントを記述することで、各クラスやメソッドの役割、引数、戻り値の意味を明確にします。これにより、自分自身が後からコードを見返したときや、ほかの開発者がプロジェクトに参加した際に、コードの意図をすばやく理解できます。
- **外部ライブラリによる機能拡張**: たとえば、タスクのデータをJSON形式で保存するために、**Gson**や**Jackson**といったJSONライブラリをプロジェクトに追加します。Mavenのようなビルドツールを使ってこれらのライブラリを依存関係に追加し、`Task`オブジェクトとJSON文字列の相互変換を簡単に行えるようにします。

## 21.1 Javadocによるドキュメント

Javaには、ソースコード内に記述した特定の形式のコメントから、API仕様書（HTML形式）を自動生成する**Javadoc**というツールが標準で備わっています。Oracleが提供するJavaの公式APIドキュメントも、このJavadocによって生成されています。

### Javadocコメントの書き方

Javadocコメントは `/**` で始まり、`*/` で終わります。クラス、フィールド、コンストラクタ、メソッドなどの宣言の直前に記述します。

```java
/**
 * 2つの整数の和を計算して返します。
 * <p>
 * このメソッドは、nullを許容せず、オーバーフローも考慮しません。
 * </p>
 *
 * @param a 加算する最初の整数
 * @param b 加算する2番目の整数
 * @return 2つの整数の和
 * @throws ArithmeticException 計算結果がオーバーフローした場合（この例では発生しないが記述例として）
 * @see Math#addExact(int, int)
 * @since 1.0
 * @author Taro Yamada
 */
public int add(int a, int b) {
    return a + b;
}
```

### 主要なJavadocタグ

コメント内では`@`で始まる**Javadocタグ**を使い、構造化された情報を記述します。

| タグ | 説明 |
| :--- | :--- |
| `@param` | メソッドの引数を説明します。`@param 引数名 説明`の形式で記述します。 |
| `@return` | メソッドの戻り値を説明します。 |
| `@throws` | メソッドがスローする可能性のある例外を説明します。`@throws 例外クラス名 説明`の形式で記述します。 |
| `@author` | クラスやインターフェイスの作成者を記述します。 |
| `@version` | バージョン情報を記述します。 |
| `@since` | この機能が追加されたバージョンを記述します。 |
| `@see` | 関連するクラスやメソッドへのリンクを生成します。 |
| `@deprecated` | 非推奨のAPIであることを示し、代替手段を案内します。 |

### Javadocの生成方法

#### コマンドライン (javac)
```bash
# -d 出力先ディレクトリ -author -version などのオプションを指定
javadoc -d doc -author -version -private MyClass.java
```

#### IntelliJ IDEA
メニューの `Tools` -> `Generate JavaDoc...` から生成できます。文字化けを防ぐため、「Other command LINE arguments」に `-encoding UTF-8 -charset UTF-8` を指定するのが一般的です。

## 21.2 外部ライブラリの利用

現代のソフトウェア開発では、車輪の再発明を避け、高品質な**外部ライブラリ**を活用するのが一般的です。Javaの世界には、さまざまな機能を提供する膨大な数のオープンソースライブラリが存在します。

### クラスパスとは

Java仮想マシン（JVM）がプログラムの実行時に必要なクラスファイル（`.class`ファイルや`.jar`ファイル）を探すための場所（ディレクトリやファイルのパス）を**クラスパス**と呼びます。外部ライブラリを利用するには、そのライブラリのJARファイルをクラスパスに含める必要があります。

### 依存関係管理ツール：Maven

手動でJARファイルをダウンロードし、クラスパスを設定するのは非常に手間がかかり、管理も煩雑です。そこで、**Maven**や**Gradle**といった**依存関係管理ツール（ビルドツール）**を使うのが現代の標準的な開発スタイルです。

**Maven**は、`pom.xml`という設定ファイルに、利用したいライブラリの名前とバージョンを記述するだけで、必要なJARファイルをインターネット上のリポジトリ（Maven Central Repositoryなど）から自動的にダウンロードし、クラスパスの設定まで行ってくれます。

#### `pom.xml`での依存関係の記述

たとえば、Googleが開発したJSONライブラリである**Gson**を利用したい場合、`pom.xml`の`<dependencies>`セクションに以下のように記述します。

```xml
<dependencies>
    <!-- 他の依存関係... -->

    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.10.1</version>
    </dependency>
</dependencies>
```
`groupId`, `artifactId`, `version`は、ライブラリを一意に識別するための座標（coordinates）です。これらの情報は、Maven Centralなどのリポジトリサイトで検索できます。

### 外部ライブラリの利用例 (Gson)

`pom.xml`に上記の設定を追加すると、IntelliJ IDEAは自動的にGsonライブラリをダウンロードします。その後は、自分のコードから`import`してライブラリの機能を利用できます。

```java
import com.google.gson.Gson;

public class Task {
    private String title;
    private boolean completed;
    // ...コンストラクタやゲッター...
}

public class JsonExample {
    public static void main(String[] args) {
        // 1. JavaオブジェクトからJSON文字列への変換（シリアライズ）
        Task task = new Task("買い物", false);
        Gson gson = new Gson();
        String json = gson.toJson(task);
        
        System.out.println("JSON: " + json);
        // 出力: JSON: {"title":"買い物","completed":false}

        // 2. JSON文字列からJavaオブジェクトへの変換（デシリアライズ）
        String inputJson = "{\"title\":\"勉強\",\"completed\":true}";
        Task loadedTask = gson.fromJson(inputJson, Task.class);
        
        System.out.println("Loaded Task: " + loadedTask.getTitle()); // 勉強
    }
}
```

## まとめ

-   **Javadoc**は、ソースコード内のコメントからAPI仕様書を自動生成する標準ツールです。適切なコメントは、コードの可読性と保守性を高めます。
-   **外部ライブラリ**は、既存の優れた機能を活用し、開発を効率化するために不可欠です。
-   **Maven**のような依存関係管理ツールを使うことで、外部ライブラリの導入と管理が劇的に簡単になります。
-   `pom.xml`に必要なライブラリの情報を記述するだけで、ライブラリのダウンロードからクラスパスの設定までが自動で行われます。