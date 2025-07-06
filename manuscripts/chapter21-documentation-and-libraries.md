# 第21章 ドキュメントと外部ライブラリ

## 章末演習

本章で学んだドキュメントと外部ライブラリの活用概念を実践的な練習課題に取り組みましょう。

### 演習の目標
- Javadocによる包括的なドキュメント作成
- 外部ライブラリの効果的な活用方法
- Maven/Gradleによる依存関係管理
- APIドキュメントの自動生成
- ライブラリ作成とパッケージング
- 技術文書ライティングとナレッジ共有のベストプラクティス

### 📁 課題の場所
演習課題は `exercises/chapter21/` ディレクトリに用意されています：

```
exercises/chapter21/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── APIDocumentation.java
│   └── SimpleLibraryDoc.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. ToDoコメントを参考に実装
4. Javadocの作成と外部ライブラリの活用を体験する
5. 開発者向けの品質の高いドキュメント作成を学ぶ

基本課題が完了したら、`advanced/`の発展課題でライブラリの作成とパッケージング、より高度なドキュメント自動生成システムに挑戦してみましょう！

## 本章の学習目標

### 前提知識
**必須前提**：
- 第20章までの総合的なJava開発能力
- 基本的なソフトウェア工学の知識
- プロジェクト管理の基本的な理解

**開発経験前提**：
- 中規模なJavaアプリケーションの開発経験
- 外部APIやライブラリの使用に対する関心

### 学習目標
**知識理解目標**：
- 依存関係管理の重要性と課題
- Maven/Gradleビルドシステムの理解
- ライブラリ選択の判断基準
- バージョン管理とセキュリティ考慮事項

**技能習得目標**：
- Maven/Gradleプロジェクトの作成と管理
- 主要ライブラリ（Jackson、Apache Commons、HTTP Client等）の活用
- 依存関係の解決とトラブルシューティング
- ライブラリのドキュメント読解と効果的な活用

**ソフトウェア工学能力目標**：
- 効率的な開発フローの構築
- 品質の高いソフトウェアの継続的な開発
- チーム開発を想定した標準化された開発手法

**到達レベルの指標**：
- 外部ライブラリを効果的に活用した実用的なアプリケーションが開発できる
- 適切なライブラリを選択し、プロジェクトに統合できる
- ビルドツールを使った効率的な開発環境が構築できる
- ライブラリのバージョンアップグレードが適切に実施できる

---

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