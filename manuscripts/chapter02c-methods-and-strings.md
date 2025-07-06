# 第2章 Java基本文法 - Part C: メソッドと文字列処理

## 2.14 メソッド（関数）

**事前説明：**
メソッド（関数）は、特定の処理をまとめて名前を付け、必要な時に呼び出して使用するしくみです。コードの再利用性を高め、プログラムの構造を整理し、保守性を向上させる重要な概念です。Javaのメソッドは、C言語の関数の概念を発展させ、オブジェクト指向プログラミングの基礎となる機能を提供しています。

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

**このコードで学習できる重要な概念：**

- **メソッドシグネチャの構成**：`public static int add(int a, int b)`の各要素（アクセス修飾子、static修飾子、戻り値の型、メソッド名、パラメータ）の意味と役割を理解できます。
- **型安全なパラメータ渡し**：引数の型が明確に定義されており、コンパイル時に型の不整合を検出できます。
- **戻り値の型宣言**：メソッドが返す値の型を明示することで、呼び出し側で適切な変数で結果を受け取れます。
- **エラーハンドリングの実装**：ゼロ除算チェックにより、実行時エラーを防ぐ防御的プログラミングの手法を学習できます。
- **静的メソッドの特徴**：`static`修飾子により、オブジェクトの生成なしにメソッドを呼び出せる便利性を理解できます。

**メソッド設計の重要な原則：**

- **単一責任の原則**：`add`メソッドは加算のみ、`divide`メソッドは除算のみを担当し、1つのメソッドが1つの明確な役割を持っています。
- **再利用性の実現**：一度定義したメソッドは、プログラム内の任意の場所から何度でも呼びだすことができます。
- **デバッグの容易性**：問題が発生した場合、特定のメソッド内に原因を特定しやすくなります。
- **可読性の向上**：メソッド名により処理内容が明確になり、プログラムの意図が理解しやすくなります。

**高度なメソッド活用例：**

```java
// メソッドオーバーロードの例
public static int add(int a, int b) {
    return a + b;
}

public static double add(double a, double b) {
    return a + b;
}

public static int add(int a, int b, int c) {
    return a + b + c;
}
```

**C言語との比較における学習ポイント：**

- **クラス内での定義**：Javaのメソッドは必ずクラス内に定義される必要があり、グローバル関数は存在しません。
- **アクセス制御**：`public`、`private`などの修飾子により、メソッドの可視性を細かく制御できます。
- **オーバーロード機能**：同じ名前で異なるパラメータを持つメソッドを複数定義でき、利便性が向上します。
- **例外処理システム**：エラー処理がより構造化され、安全なプログラムの作成を支援します。

## 2.15 文字列処理

Javaにおける文字列（String）は、C言語とは大きく異なり、オブジェクトとして扱われます。これにより、豊富な文字列操作メソッドが利用できます。

### 文字列の基本

```java
// 文字列の作成
String str1 = "Hello";
String str2 = new String("World");

// 文字列の連結
String message = str1 + " " + str2;  // "Hello World"

// 文字列の長さ
int length = message.length();  // 11

// 文字の取得
char firstChar = message.charAt(0);  // 'H'
```

### 文字列の比較

```java
String s1 = "Java";
String s2 = "Java";
String s3 = new String("Java");

// == は参照の比較（推奨されない）
boolean ref1 = (s1 == s2);    // true（文字列プール）
boolean ref2 = (s1 == s3);    // false

// equals() は内容の比較（推奨）
boolean content1 = s1.equals(s2);  // true
boolean content2 = s1.equals(s3);  // true

// 大文字小文字を無視した比較
boolean ignore = s1.equalsIgnoreCase("java");  // true
```

### 便利な文字列操作メソッド

```java
String text = "  Java Programming  ";

// 空白の除去
String trimmed = text.trim();  // "Java Programming"

// 大文字・小文字変換
String upper = text.toUpperCase();  // "  JAVA PROGRAMMING  "
String lower = text.toLowerCase();  // "  java programming  "

// 部分文字列の取得
String sub = text.substring(2, 6);  // "Java"

// 文字列の置換
String replaced = text.replace("Java", "Python");  // "  Python Programming  "

// 文字列の分割
String csv = "apple,banana,orange";
String[] fruits = csv.split(",");  // ["apple", "banana", "orange"]

// 文字列の検索
int index = text.indexOf("Pro");  // 7
boolean contains = text.contains("Java");  // true
```

### 文字列の不変性（Immutability）

Javaの文字列は**不変**（immutable）です。一度作成された文字列オブジェクトの内容は変更できません：

```java
String str = "Hello";
str.concat(" World");  // str自体は変更されない
System.out.println(str);  // "Hello"

// 新しい文字列を作成して代入する必要がある
str = str.concat(" World");
System.out.println(str);  // "Hello World"
```

### StringBuilderによる効率的な文字列操作

文字列を頻繁に変更する場合は、`StringBuilder`を使用します：

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(" ");
sb.append("World");
sb.insert(5, ",");  // "Hello, World"
sb.reverse();       // "dlroW ,olleH"

String result = sb.toString();
```

**StringBuilderを使うべき場面：**
- ループ内で文字列を連結する場合
- 大量の文字列操作を行う場合
- パフォーマンスが重要な場合

## Java基本文法の総合的な理解

本章で学習した各要素は、独立したテクニックではなく、現代のソフトウェア開発における包括的な設計哲学の一部です。出力メソッドから始まり、データ型、変数、演算子、制御構造、配列、メソッドまで、すべてがJavaの「安全性」「保守性」「可読性」という3つの核心原則にもとづいて設計されています。

**学習した内容の相互関係と実践への応用：**

- **型安全性の一貫した実装**：`System.out.println`による自動型変換、明示的な変数宣言、メソッドのシグネチャ、配列の境界チェックなど、すべての機能が型の整合性を保証しています。
- **オブジェクト指向への基盤構築**：staticメソッド、クラス内でのメソッド定義、String型の活用など、本章で学んだ要素はすべて次章以降のオブジェクト指向学習への準備となっています。
- **C言語からの進化の理解**：単なる機能追加ではなく、ソフトウェア開発の課題（メモリ管理、プラットフォーム依存性、保守性）を根本的に解決するための設計思想の変革を体験できました。

**現代的なプログラミング手法への接続：**

今回学習した基本文法は、フレームワーク開発、Webアプリケーション構築、マイクロサービスアーキテクチャ、クラウドネイティブ開発など、現代のあらゆるJava開発の基盤となります。型安全性、例外処理、適切なメソッド設計などの概念は、企業での実際の開発において直接活用される重要なスキルです。

---

次のパート：[Part D - クラスとオブジェクトの基礎](chapter02d-class-basics.md)