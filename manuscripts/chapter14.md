# 第14章 アプリケーションのビルドと配布

## 始めに

これまでの章で、機能的なJavaプログラムを作成する方法を学んできました。しかし、開発したアプリケーションを他の人が利用できるようにするためには、ソースコードを整理し、実行可能な形式にパッケージングし、その使い方を説明するドキュメントを整備する必要があります。

本章では、Javaアプリケーションを完成させ、他者に配布するための重要なステップである「パッケージ管理」「ドキュメント生成」「ビルドと配布」について学びます。

## 14.1 パッケージとimport

### パッケージの役割

プログラムの規模が大きくなるにつれて、クラスの数も増えていきます。パッケージは、これらの関連するクラスやインターフェイスを一つのグループにまとめるための「フォルダ」のようなものです。パッケージには主に2つの役割があります。

1.  **名前空間の提供**: パッケージは、クラス名が他のパッケージのクラス名と衝突するのを防ぎます。例えば、`com.example.data.User`クラスと`com.example.ui.User`クラスは、同じ`User`という名前でも、異なるパッケージに属するため共存できます。
2.  **アクセス制御**: パッケージは、クラスやメンバーへのアクセスを制御する単位としても機能します。アクセス修飾子を何も付けない場合、そのメンバーは同じパッケージ内からのみアクセス可能になります。

### パッケージ宣言と命名規則

ソースファイルの先頭で`package`キーワードを使って、そのファイルが属するパッケージを宣言します。

```java
// com/example/utils/StringUtil.java
package com.example.utils;

public class StringUtil {
    // ...
}
```

パッケージ名は、世界中で一意になるように、通常は組織が所有するドメイン名を逆にしたものが使われます（例: `com.google.common`）。

### import文

他のパッケージに属するクラスを利用するには、`import`文を使ってそのクラスを読み込みます。

```java
// com/example/app/Main.java
package com.example.app;

// 特定のクラスをインポート
import com.example.utils.StringUtil;
// java.utilパッケージの全てのクラスをインポート
import java.util.*;

public class Main {
    public static void main(String[] args) {
        StringUtil.reverse("hello");
        List<String> list = new ArrayList<>();
    }
}
```

## 14.2 Javadocによるドキュメント生成

Javadocは、Javaのソースコード内に記述された特別な形式のコメントから、APIドキュメント（仕様書）を自動的に生成するツールです。良いドキュメントは、他の開発者があなたのコードを理解し、正しく利用するために不可欠です。

### Javadocコメントの書き方

Javadocコメントは `/**` で始まり、`*/` で終わります。クラス、メソッド、フィールドの直前に記述します。

```java
/**
 * 文字列操作に関するユーティリティクラスです。
 * 
 * @author 山田太郎
 * @version 1.1
 * @since 1.0
 */
public class StringUtil {

    /**
     * 指定された文字列を逆順にします。
     *
     * @param str 逆順にする文字列（nullの場合はnullを返す）
     * @return 逆順になった文字列
     */
    public static String reverse(String str) {
        if (str == null) return null;
        return new StringBuilder(str).reverse().toString();
    }
}
```

`@author`や`@param`、`@return`といった**Javadocタグ**を使うことで、構造化されたドキュメントを生成できます。

### Javadocの生成方法

IntelliJ IDEAやEclipseなどのIDEには、Javadocを簡単に生成する機能が備わっています。また、コマンドラインから`javadoc`コマンドを使って生成することも可能です。

```bash
# docsディレクトリにドキュメントを生成
javadoc -d docs -sourcepath src -subpackages com.example
```

## 14.3 JARファイルによるパッケージング

開発したアプリケーションを配布するには、コンパイルされた`.class`ファイルや、画像などのリソースファイルを一つにまとめる必要があります。そのための標準的な形式が **JAR (Java Archive)** ファイルです。

### JARファイルとは

JARファイルは、ZIP形式に基づいたアーカイブファイルで、Javaアプリケーションの配布やライブラリの提供に広く使われます。`java -jar`コマンドで直接実行できる「実行可能JAR」を作成することもできます。

### 実行可能JARファイルの作成

実行可能JARファイルを作成するには、「マニフェストファイル」にメインクラス（`main`メソッドを持つクラス）を指定する必要があります。

**1. マニフェストファイルの作成 (`manifest.txt`)**

```
Main-Class: com.example.app.Main
```

**2. コマンドラインでのJAR作成**

```bash
# 1. ソースコードをコンパイル (クラスファイルをbuildディレクトリに出力)
javac -d build src/com/example/app/Main.java src/com/example/utils/StringUtil.java

# 2. マニフェストファイルとクラスファイルを指定してJARを作成
jar cvfm MyApplication.jar manifest.txt -C build .
```

*   `c`: 新規アーカイブを作成
*   `v`: 詳細情報を表示
*   `f`: ファイル名を指定
*   `m`: マニフェストファイルを指定

### JARファイルの実行

作成したJARファイルは、以下のコマンドで実行できます。

```bash
java -jar MyApplication.jar
```

## 14.4 ビルドツールへの展望

本章では手動でコンパイルやJAR作成を行いましたが、実際のプロジェクトではこれらの作業を自動化する「ビルドツール」を利用するのが一般的です。

代表的なビルドツールには **Maven** や **Gradle** があります。これらのツールは、以下の機能を提供し、開発プロセスを大幅に効率化します。

*   **依存関係管理**: プロジェクトが必要とする外部ライブラリを自動的にダウンロードし、管理します。
*   **ビルドの自動化**: コンパイル、テスト、JAR作成、ドキュメント生成といった一連の作業をコマンド一つで実行できます。
*   **プロジェクト構造の標準化**: 標準的なディレクトリ構成に従うことで、誰でもプロジェクトの構造を理解しやすくなります。

次のステップとして、これらのビルドツールの使い方を学ぶことで、より本格的なJava開発の世界に進むことができます。
