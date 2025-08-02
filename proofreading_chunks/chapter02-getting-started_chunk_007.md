<!-- 
校正チャンク情報
================
元ファイル: chapter02-getting-started.md
チャンク: 7/9
行範囲: 1140 - 1334
作成日時: 2025-08-02 22:43:26

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### C言語の文字列処理の課題
```c
// #include <string.h> が必要
// #include <stdlib.h> が必要

// C言語での文字列処理
char str[100] = "Hello";
strcat(str, " World");  // バッファオーバーフローの危険性
int len = strlen(str);  // 毎回O(n)の計算コスト
char *copy = malloc(len + 1);
strcpy(copy, str);  // メモリリークの可能性
```

#### Javaの文字列処理の利点
```java
// Javaでの文字列処理
String str = "Hello";
str = str + " World";  // 安全な連結
int len = str.length();  // O(1)で取得
String copy = str;  // 参照のコピー（安全）
```

### 文字列設計の重要な違い

| 観点 | C言語 | Java |
|------|-------|------|
| メモリ管理 | 手動でメモリ確保・解放が必要 | 自動メモリ管理（ガベージコレクション） |
| 安全性 | バッファオーバーフロー、セグメンテーション違反のリスク | 境界チェックにより安全性を保証 |
| 文字列の表現 | char配列 + null終端文字 | Stringオブジェクト（長さ情報を内部保持） |
| 文字エンコーディング | プラットフォーム依存（ASCII、ShiftJIS等） | Unicode（UTF-16）で統一 |

### 実践的な比較例

#### 文字列の長さ取得
```c
// #include <string.h> が必要

// C言語 - O(n)の計算が必要
int len = strlen(str);  // 毎回文字列を走査
```

```java
// Java - O(1)で即座に取得
int len = str.length();  // 内部フィールドを返すだけ
```

#### 文字列の比較
```c
// #include <string.h> が必要

// C言語
if (strcmp(str1, str2) == 0) {
    // 等しい
}
```

```java
// Java
if (str1.equals(str2)) {
    // 等しい
}
```

#### 部分文字列の取得
```c
// #include <string.h> が必要

// C言語 - 手動でメモリ管理
char sub[10];
strncpy(sub, str + 5, 9);
sub[9] = '\0';
```

```java
// Java - 簡潔で安全
String sub = str.substring(5, 14);
```

### 文字列の基本

文字列オブジェクトの作成と基本操作は、Javaプログラミングにおいてもっとも頻繁に使用される機能の1つです。
以下のコード例では、文字列の作成、連結、長さの取得、個別文字へのアクセスという基本的な操作を示します。

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

このコードで注目すべき重要な概念は、文字列リテラル（`"Hello"`）と`new`キーワードを使用した文字列作成の違いです。文字列リテラルはJVMが管理する文字列プールに格納され、同じ内容の文字列は共有されます。これは、メモリ効率とパフォーマンスの最適化のための重要なしくみです。一方、`new String()`は常に新しいオブジェクトを作成するため、通常は文字列リテラルの使用が推奨されます。

### 文字列の比較

文字列の比較は、Javaプログラミングでもっとも誤解されやすい概念の1つです。C言語では`strcmp()`関数を使用していましたが、Javaではオブジェクトの比較という観点から、より慎重なアプローチが必要です。以下のコード例で、参照の比較と内容の比較の重要な違いを示します。

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

このコードは、Javaの文字列比較における重要な教訓を示しています。`==`演算子はオブジェクトの参照（メモリアドレス）を比較するため、文字列の内容が同じでも異なるオブジェクトであれば`false`を返します。とくに注意すべきは、文字列プールの存在により`s1 == s2`が`true`となる点です。これは偶然の一致であり、信頼すべきではありません。常に`equals()`メソッドを使用して文字列の内容を比較することが、バグのない堅牢なプログラムを作成する鍵となります。

### 便利な文字列操作メソッド

Javaの`String`クラスは、テキスト処理に必要な豊富なメソッドを提供しています。これらのメソッドは、Web開発でのユーザー入力の処理、データベースから取得した文字列の整形、ファイルの読み書きなど、実際の開発で頻繁に使用されます。以下のコード例で、もっとも重要な文字列操作メソッドを実践的に学習します。

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

これらのメソッドは、単なる文字列操作の道具ではなく、実際のビジネスロジックを実装するうえで不可欠な要素です。`trim()`メソッドはユーザー入力の前後の空白を除去してデータの一貫性を保ち、`split()`メソッドはCSVファイルの解析や構造化データの処理に使用されます。`indexOf()`と`contains()`は検索機能の実装に欠かせず、`replace()`はテンプレート処理やデータのサニタイゼーションで活用されます。重要なのは、これらすべてのメソッドが元の文字列を変更せず、新しい文字列を返すという点です。

### 文字列の不変性（Immutability）

Javaの文字列設計におけるもっとも重要な特徴の1つが不変性（Immutability）です。この設計思想は、最初は直感に反するように感じることがありますが、スレッドセーフティ、セキュリティ、パフォーマンスの観点からきわめて重要な役割を果たしています。文字列が不変であることにより、複数のスレッドが同じ文字列オブジェクトを安全に共有でき、予期しない変更によるバグを防ぐことができます。以下のコード例で、この重要な概念を実際に確認してみましょう。

```java
String str = "Hello";
str.concat(" World");  // str自体は変更されない
System.out.println(str);  // "Hello"

// 新しい文字列を作成して代入する必要がある
str = str.concat(" World");
System.out.println(str);  // "Hello World"
```

このコードは、`concat()`メソッドを呼び出しても元の文字列オブジェクトが変更されないことを示しています。これは、C言語の`strcat()`関数とは根本的に異なる動作です。Javaでは、文字列操作メソッドは常に新しい文字列オブジェクトを返し、元のオブジェクトはそのまま保持されます。この設計により、文字列を辞書のキーやハッシュマップのキーとして安全に使用でき、文字列プールによる最適化も可能になっています。

### StringBuilderによる高速な文字列操作

文字列の不変性は多くの利点をもたらしますが、頻繁な文字列操作が必要な場合にはパフォーマンスの問題を引き起こす可能性があります。なぜなら、文字列を連結するたびに新しいオブジェクトが作成され、古いオブジェクトはガベージコレクションの対象となるからです。この問題を解決するために、Javaは`StringBuilder`クラスを提供しています。`StringBuilder`は内部に可変長の文字配列を持ち、メモリの再割り当てを最小限に抑えながら文字列操作を行います。

```java
StringBuilder sb = new StringBuilder();
sb.append("Hello");
sb.append(" ");
sb.append("World");
sb.insert(5, ",");  // "Hello, World"
sb.reverse();       // "dlroW ,olleH"

String result = sb.toString();
```

このコードは、`StringBuilder`の威力を示しています。`append()`メソッドは既存の内容に文字列を追加し、`insert()`は指定位置に文字列を挿入し、`reverse()`は文字列全体を反転させます。これらの操作はすべて同じオブジェクト上で行われるため、メモリ効率が向上します。

`StringBuilder`を使用すべき典型的な場面は、ループ内での文字列連結です。
たとえば、1000個の要素を連結する場合、通常の文字列連結ではn*(n-1)/2回のコピー操作が発生し、1000要素では約50万回のコピーが必要になります。
`StringBuilder`を使用すれば、1回のオブジェクト作成で済みます。

また、ログファイルの生成、SQLクエリの動的構築、HTMLやXMLの生成など、大量の文字列操作を伴う処理でも`StringBuilder`は不可欠です。
ただし、2-3個の文字列連結や、変更頻度の低い場合は、可読性の観点から通常の文字列操作を使用することが推奨されます。




<!-- 
================
チャンク 7/9 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
