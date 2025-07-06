# 第1章 Java入門と開発環境構築

## 本章の学習目標

### 前提知識
**技術的前提**：
- C言語での基本的なプログラミング経験（変数、関数、制御構造）
- コンパイルと実行の基本概念
- 基本的なコンピュータ操作（ファイル作成、ディレクトリ操作）

**概念的前提**：
- プログラムとは何かの基本理解
- データと処理の概念
- 問題解決の論理的思考

### 学習目標
**知識理解目標**：
- Java言語の歴史的背景と設計思想の理解
- C言語からJavaへの移行の必然性の理解
- オブジェクト指向の概念的な理解
- データと処理の一体化がもたらす利点の理解

**技能習得目標**：
- Java開発環境（JDK、SDKMAN）のインストールと設定
- IDE（IntelliJ IDEA）の基本操作
- 簡単なJavaプログラムのコンパイルと実行
- 標準入出力を使った基本的なプログラム作成

**到達レベルの指標**：
- Hello Worldプログラムが独力で作成・実行できる
- 標準入力を受け取るプログラムが作成できる
- 開発環境の基本的なトラブルシューティングができる
- Java学習の全体像を説明できる

---

本書は、C言語の基礎（手続き的なプログラミング手法）を学んだ方を対象としたJava入門書です。

## 本書の対象読者

- C言語でプログラミングの基礎を学んだ方
- オブジェクト指向プログラミングを学びたい方
- モダンなJavaの機能を習得したい方

## 本書の注意事項

1. 本書は基本的にmacOS（Appleチップ搭載のMacBook Air）を用いることを基準に作成されています。
    - WindowsやLinuxなどのほかのOSでも問題なく開発が進められますが、細かなところまでほかの環境への対応を網羅できていません。ご了承ください。


## 本書で学べること

- C言語との違いを意識したJavaの基本文法
- オブジェクト指向プログラミングの考え方と実践
- コレクションフレームワークとジェネリクス
- ラムダ式とStream APIなどのモダンJava機能
- GUIアプリケーションの開発
- 外部ライブラリの活用方法
- マルチスレッドプログラミング
- ネットワークプログラミング

## Javaの設計思想と"Write Once, Run Anywhere"

Javaの最大の特徴は「Write Once, Run Anywhere (WORA)」という理念です。これは、一度書いたプログラムが、Windows、macOS、Linuxなど、どのOSでも動作することを意味します。

この画期的なしくみは、**Java Virtual Machine (JVM)**という仮想マシンによって実現されています。JVMは、Javaプログラムを各OSで実行可能な形式に変換する「翻訳者」の役割を果たします。

**Javaの設計思想やJVMの詳細なしくみ、現代のクラウド環境での進化については、付録B.1「言語設計とプラットフォーム」を参照してください。**

## 学習の進め方とロードマップ

### 段階別学習フロー

本書は5つの段階に分けて体系的にJavaプログラミングを学習できるよう設計されています：

#### **第1段階：基礎固めと環境構築**（第0章〜第2章）
**学習期間目安：2-3週間**

- **第0章：始めに** - Java学習の全体像把握と開発環境構築
- **第1章：Javaの基本文法** - C言語からJavaへの文法移行
- **第2章：オブジェクト指向の基礎** - クラス・オブジェクトの基本概念

**マイルストーン1**：基本的なオブジェクト指向プログラムが理解・実装できる

#### **第2段階：オブジェクト指向の深化**（第3章〜第5章）
**学習期間目安：3-4週間**

- **第3章：クラス設計と継承** - 継承による設計の拡張
- **第4章：ポリモーフィズムとインターフェイス** - 柔軟な設計手法
- **第5章：パッケージとアクセス制御** - 大規模開発を支える構造化

**マイルストーン2**：継承とインターフェイスを活用した柔軟な設計ができる

#### **第3段階：実用的プログラミング技法**（第6章〜第8章）
**学習期間目安：3-4週間**

- **第6章：例外処理** - 堅牢なプログラムの作成技法
- **第7章：コレクションフレームワーク** - 効率的なデータ構造の活用
- **第8章：ジェネリクス** - 型安全性と再利用性の両立

**マイルストーン3**：堅牢で効率的なJavaプログラムが作成できる

#### **第4段階：モダンJava機能**（第9章〜第10章）
**学習期間目安：2-3週間**

- **第9章：ラムダ式と関数型インターフェイス** - 関数型プログラミング要素
- **第10章：Stream API** - 宣言的なデータ処理プログラミング

**マイルストーン4**：関数型プログラミング要素を効果的に活用できる

#### **第5段階：外部システムとの連携**（第11章〜第15章）
**学習期間目安：4-5週間**

- **第11章：ファイル入出力とリソース管理** - 外部データとの連携
- **第12章：GUIアプリケーション** - ユーザーインターフェイス開発
- **第13章：ライブラリの活用** - 外部ライブラリによる開発効率向上
- **第14章：マルチスレッドプログラミング** - 並行処理による性能向上
- **第15章：ネットワークプログラミング** - 分散システムと通信技術

**マイルストーン5**：実用的なアプリケーションを包括的に開発できる

### 効果的な学習戦略

#### 各章での推奨学習方法
1. **理論学習**：各章の導入部分で歴史的背景と技術的必然性を理解
2. **サンプル研究**：提供されるサンプルコードを実際に動作させて理解
3. **実践演習**：章末の演習課題に取り組み、理解を定着
4. **応用実践**：ほかの章の技術と組み合わせた実用例の実装
5. **復習統合**：定期的に前の章の内容と関連付けて理解を深化

#### 学習効果を高めるコツ
- **段階的理解**：無理に先に進まず、各段階での理解を確実にする
- **コード実践**：必ず手を動かしてコードを書き、実行結果を確認する
- **エラー体験**：意図的にエラーを発生させ、エラーメッセージから学習する
- **設計思考**：「なぜこの技術が必要なのか」を常に考える
- **総合活用**：複数の章の技術を組み合わせた実用例を考案・実装する

### 章間の関連性

#### 縦の流れ（技術的依存関係）
```
基礎文法・環境（第0-1章）
     ↓
オブジェクト指向基礎（第2章）
     ↓
オブジェクト指向発展（第3-5章）
     ↓
実用技術基礎（第6-8章）
     ↓
モダンJava機能（第9-10章）
     ↓
システム連携技術（第11-15章）
```

#### 横の関連性（技術分野による関連）
- **データ処理技術群**：第7章（コレクション）→ 第8章（ジェネリクス）→ 第9章（ラムダ式）→ 第10章（Stream）
- **システム設計技術群**：第2章（基礎OOP）→ 第3章（継承）→ 第4章（ポリモーフィズム）→ 第5章（パッケージ）
- **外部連携技術群**：第6章（例外処理）→ 第11章（ファイルI/O）→ 第13章（ライブラリ）→ 第15章（ネットワーク）

### 実践プロジェクト

各段階の学習完了後、以下の総合プロジェクトで学習成果を確認できます：

#### レベル1：基礎統合プロジェクト（第5章完了後）
**図書館管理システム**
- オブジェクト指向設計の実践
- パッケージ構造による適切なモジュール化
- 基本的なデータ管理と検索機能

#### レベル2：中級統合プロジェクト（第10章完了後）
**学習管理システム（LMS）**
- 例外処理を含む堅牢なシステム設計
- コレクションフレームワークによる効率的なデータ処理
- Stream APIを活用した分析機能

#### レベル3：上級統合プロジェクト（第15章完了後）
**分散タスク管理システム**
- クライアント／サーバアーキテクチャ
- GUIとネットワーク通信の統合
- マルチスレッドによる並行処理
- 外部ライブラリを活用した実用的な機能

### 自己評価チェックリスト

各章終了時に以下の観点で自己評価を行うことを推奨します：

**理解度チェック**

| チェック | 項目 |
| :---: | :--- |
| ☐ | 章の主要概念を他者に説明できる |
| ☐ | 提供されたサンプルコードの動作を完全に理解している |
| ☐ | 類似の問題を独力で解決できる |

**技能確認チェック**

| チェック | 項目 |
| :---: | :--- |
| ☐ | 章の技術要素を使った簡単なプログラムが独力で作成できる |
| ☐ | エラーメッセージを理解し、適切に対処できる |
| ☐ | 前の章の内容と組み合わせた応用ができる |

**応用力チェック**

| チェック | 項目 |
| :---: | :--- |
| ☐ | 実用的な場面での活用方法がイメージできる |
| ☐ | 他の技術との組み合わせ方法が理解できている |
| ☐ | より高度な学習への興味と方向性が見えている |




### C言語、Java、そして現代的ソフトウェア開発のしくみ

C言語の学習を終えた皆さん、プログラミングの基礎固めができたことと思います。`main`関数から始まり、文が順次実行され、データは変数に格納し、処理は関数としてまとめる。この「手続き型プログラミング」の考え方は、コンピュータの動作原理に近く、処理の流れを理解しやすいという利点があります。

しかし、プログラムの規模が大きくなるにつれて、ある問題に直面します。C言語で書かれた以下のコードを見てください。

```c
#include <stdio.h>

// 商品価格と消費税率をグローバル変数で定義
int price = 1000;
double tax_rate = 1.1;

// 税込価格を計算して表示する関数
void printPriceWithTax() {
    int total = price * tax_rate; // priceとtax_rateを参照
    printf("合計金額: %d円\n", total);
}

int main(void) {
    printPriceWithTax();
    return 0;
}
```
このコードでは、データ（`price`, `tax_rate`）と、それを利用する手続き（`printPriceWithTax`）がプログラム上で分離しています。小規模なうちは問題ありませんが、何百もの関数と変数が登場する大規模な開発では、「どの関数がどのデータを変更する可能性があるのか」を追跡するのが非常に困難になります。この**「データと手続きの分離」**こそが、ソフトウェアの複雑性を増大させ、バグの温床となる主要な原因の1つです。

本書でこれから学ぶJavaと「オブジェクト指向」は、この問題を解決するための強力なアプローチを提供します。

#### Javaとオブジェクト指向：データと処理を一体化する

C言語で大規模なプログラムを書くと、データと処理が分離していることによる複雑さに直面します。Javaは「オブジェクト指向」というアプローチで、この問題を解決します。

**オブジェクト指向の基本的な考え方**は、関連するデータとそれを操作する処理を「オブジェクト」という1つの単位にまとめることです。これにより、プログラムの構造がより直感的で管理しやすくなります。

##### 簡単な例：商品を表現する

C言語では商品情報と処理を別々に管理していましたが、Javaではこれらを1つの「クラス」にまとめます：

```java
// 商品を表すクラス
public class Product {
    // データ（フィールド）
    private String name;    // 商品名
    private int price;      // 価格
    
    // コンストラクタ（初期化処理）
    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
    
    // 処理（メソッド）
    public int getPriceWithTax() {
        return (int)(price * 1.1);
    }
    
    public void display() {
        System.out.println(name + ": " + getPriceWithTax() + "円（税込）");
    }
}

// 実際に使う
public class Main {
    public static void main(String[] args) {
        Product pencil = new Product("鉛筆", 100);
        pencil.display();  // 鉛筆: 110円（税込）
    }
}
```

##### C言語との違い

**C言語のアプローチ**：
- データ（構造体）と処理（関数）が分離
- 大規模になると、どの関数がどのデータを変更するか追跡が困難
- グローバル変数による予期しない副作用のリスク

**Javaのアプローチ**：
- データと処理を「クラス」という単位で一体化
- `private`によりデータへの直接アクセスを制限（カプセル化）
- オブジェクトごとに独立した状態を持ている

この違いが最も顕著に現れるのは、プログラムが大規模化したときです。オブジェクト指向により、数百、数千のクラスからなる大規模システムでも、各部分の責任範囲が明確になり、保守性と拡張性が大幅に向上します。

> **より深く学びたい方へ**  
> プログラミング言語がソースコードをどのように解釈し、実行可能な形式に変換するかについて興味がある方は、本書の巻末にある「Deep Dive: 抽象構文木（AST）とコンパイラの仕組み」をご覧ください。IDE（統合開発環境）の高度な機能や、現代的なフレームワークの動作原理を理解する上で役立つ知識が得られます。

## Javaの特徴

Javaは以下のような特徴を持つプログラミング言語です：

- **プラットフォーム独立性**：「Write Once, Run Anywhere」
- **オブジェクト指向**：クラスベースのオブジェクト指向言語
- **ガベージコレクション**：自動メモリ管理
- **強い型付け**：コンパイル時の型チェック
- **豊富なライブラリ**：標準ライブラリとサードパーティライブラリ

## 開発環境の準備

Javaプログラミングを始めるには、まず開発環境を整える必要があります。本書では以下の環境を使用します：

- **JDK（Java Development Kit）**: OpenJDK 21.0.6 (Microsoft)
- **IDE（統合開発環境）**: IntelliJ IDEA Community Edition

> **環境構築の詳細な手順については、付録A「開発環境の構築」を参照してください。**

環境構築が完了したら、次のセクションに進んでください。

## 最初のJavaプログラム

開発環境の準備ができたら、さっそく最初のJavaプログラムを作成してみましょう。

### Hello Worldプログラム

以下は、画面に「Hello, World!!」と表示する最も基本的なJavaプログラムです：

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!!");
    }
}
```

**プログラムの構成要素**：
- `public class HelloWorld`: クラスの宣言（ファイル名と一致させる必要があります）
- `public static void main(String[] args)`: プログラムの開始点となるmainメソッド
- `System.out.println()`: 画面に文字を出力するメソッド

> **コンパイルと実行の詳細な手順については、付録A「開発環境の構築」のA.4節を参照してください。**

> **標準入力の詳しい扱い方については、付録A「開発環境の構築」のA.7節を参照してください。**

## Javaプログラムの基本構文

ここまでのサンプルコードを見て、「なぜこのような書き方をするのか？」という疑問を持たれたかもしれません。章末の練習課題に取り組む前に、Javaプログラムの基本的な構文と文法要素を体系的に理解しておきましょう。

### クラスとmainメソッドの基本構造

Javaでは、すべてのコードは「**クラス**」という単位で記述します。クラスは、関連するデータとそれを操作する処理をまとめたものです。

```java
public class クラス名 {
    public static void main(String[] args) {
        // ここにプログラムの処理を書く
    }
}
```

**重要なルール**：
- **クラス名とファイル名は完全に一致させる必要があります**
  - `HelloWorld.java`というファイルには`public class HelloWorld`を定義
- **大文字・小文字は区別されます**
  - `HelloWorld`と`helloworld`は異なるものとして扱われます
- **1つのJavaファイルには1つのpublicクラスのみ定義可能**

#### mainメソッドの構成要素

```java
public static void main(String[] args)
```

この1行には、以下の重要な要素が含まれています：

| 要素 | 説明 |
|------|------|
| `public` | どこからでも呼び出し可能であることを示すアクセス修飾子 |
| `static` | オブジェクトを作らなくても呼び出せることを示すキーワード |
| `void` | この処理が値を返さないことを示す戻り値の型 |
| `main` | プログラムの開始点として認識される特別なメソッド名 |
| `String[] args` | コマンドライン引数を受け取るためのパラメータ |

**なぜこの書き方が必要なのか？**
Javaランタイム（javaコマンド）は、指定されたクラスの中からこの正確なシグネチャを持つ`main`メソッドを探して、プログラムの実行を開始します。一文字でも違うと認識されません。

### 変数の宣言と初期化

変数は「データを格納する入れ物」です。Javaでは使用前に必ず**型**と**名前**を宣言する必要があります。

#### 基本的な変数宣言の構文

```java
データ型 変数名;                    // 宣言のみ
データ型 変数名 = 初期値;           // 宣言と同時に初期化
```

**実例**：
```java
int age;                           // 宣言のみ
int price = 1000;                  // 宣言と同時に初期化
String name = "田中太郎";           // 文字列の場合
```

#### 変数の命名規則

**必須ルール**：
- 最初の文字は英字、`_`、`$`のいずれか（数字は不可）
- 2文字目以降は英字、数字、`_`、`$`を使用可能
- Javaの予約語（`int`, `class`, `public`など）は使用不可

**慣例的なルール**：
- 変数名は小文字で始める（camelCase）
- 意味のある名前を付ける

```java
// 良い例
int studentAge = 20;
String userName = "yamada";
double totalPrice = 12500.0;

// 避けるべき例
int a = 20;              // 意味が不明
String _name = "yamada"; // アンダースコアで開始（慣例違反）
double 価格 = 12500.0;   // 日本語（技術的には可能だが推奨されない）
```

### データ型の体系的理解

Javaのデータ型は大きく**プリミティブ型**と**参照型**に分かれます。

#### プリミティブ型（基本データ型）

| 型名 | サイズ | 値の範囲 | 用途 | リテラル例 |
|------|--------|----------|------|-----------|
| `byte` | 8bit | -128 ～ 127 | 小さな整数 | `100` |
| `short` | 16bit | -32,768 ～ 32,767 | 中程度の整数 | `30000` |
| `int` | 32bit | -2,147,483,648 ～ 2,147,483,647 | 一般的な整数 | `2000000` |
| `long` | 64bit | -9,223,372,036,854,775,808 ～ 9,223,372,036,854,775,807 | 大きな整数 | `9000000000L` |
| `float` | 32bit | 約±3.4×10^38（7桁精度） | 単精度小数 | `3.14f` |
| `double` | 64bit | 約±1.8×10^308（15桁精度） | 倍精度小数 | `3.141592653589793` |
| `char` | 16bit | 0 ～ 65,535（Unicode文字） | 一文字 | `'A'` |
| `boolean` | - | `true`または`false` | 真偽値 | `true` |

**重要なリテラル記法**：
```java
long bigNumber = 9000000000L;      // long型はLを末尾に付ける
float pi = 3.14f;                  // float型はfを末尾に付ける
char grade = 'A';                  // char型は単一引用符で囲む
boolean isValid = true;            // boolean型はtrue/false
```

#### 参照型

最も基本的な参照型は`String`（文字列）です：

```java
String message = "Hello, World!";  // 文字列は二重引用符で囲む
String name = "田中太郎";           // 日本語文字列も可能
```

### 演算子の使い方

#### 算術演算子

| 演算子 | 意味 | 例 | 結果 |
|--------|------|----|----- |
| `+` | 加算 | `5 + 3` | `8` |
| `-` | 減算 | `5 - 3` | `2` |
| `*` | 乗算 | `5 * 3` | `15` |
| `/` | 除算 | `15 / 4` | `3`（整数除算） |
| `%` | 余り | `15 % 4` | `3` |

**整数除算の重要な注意点**：
```java
int result1 = 15 / 4;      // 結果: 3（小数部は切り捨て）
double result2 = 15.0 / 4; // 結果: 3.75（どちらか一方がdoubleなら小数計算）
```

#### 文字列連結演算子

`+`演算子は文字列連結にも使用されます：

```java
String firstName = "太郎";
String lastName = "田中";
String fullName = lastName + firstName;     // "田中太郎"

int age = 20;
String message = "年齢: " + age + "歳";      // "年齢: 20歳"
```

**混在する場合の評価順序**：
```java
System.out.println("結果: " + 10 + 20);     // "結果: 1020"（左から文字列連結）
System.out.println("結果: " + (10 + 20));   // "結果: 30"（カッコで計算を先に実行）
```

### System.out.println()の詳細

標準出力への表示には`System.out.println()`を使用します。

#### 基本的な使用方法

```java
System.out.println("Hello, World!");       // 文字列リテラル
System.out.println(42);                    // 数値
System.out.println(3.14);                  // 小数

int number = 100;
System.out.println(number);                // 変数
System.out.println("数値: " + number);      // 文字列と変数の連結
```

#### print()との違い

```java
System.out.print("Hello");     // 改行なし
System.out.print("World");     // 改行なし  → "HelloWorld"と出力

System.out.println("Hello");   // 改行あり
System.out.println("World");   // 改行あり  → "Hello"と"World"が別々の行に出力
```

### コメントの書き方

コードの説明や覚書のために**コメント**を使用します。コメントはコンパイル時に無視されます。

#### 単行コメント

```java
// これは単行コメントです
int age = 20;  // 変数の後にも書けます
```

#### 複数行コメント

```java
/*
 これは複数行にわたる
 コメントです
 */
int price = 1000;
```

### セミコロンの必要性

**Javaでは、ほとんどの文の終わりにセミコロン（`;`）が必要です**。

```java
int x = 10;                    // 変数宣言
System.out.println("Hello");  // メソッド呼び出し
x = x + 5;                     // 代入
```

**セミコロンが不要な場合**：
```java
public class Sample {          // クラス宣言
    public static void main(String[] args) {  // メソッド宣言
        // 処理
    }                          // ブロックの終わり
}                              // クラスの終わり
```

### よくあるコンパイルエラーとその対処法

#### 1. セミコロン忘れ

**エラーメッセージ**：
```
error: ';' expected
```

**原因と対処**：
```java
// エラー例
int x = 10  // セミコロンがない

// 修正版
int x = 10; // セミコロンを追加
```

#### 2. 大文字・小文字の間違い

**エラーメッセージ**：
```
error: cannot find symbol
```

**原因と対処**：
```java
// エラー例
system.out.println("Hello");  // systemが小文字

// 修正版
System.out.println("Hello");  // Systemを大文字で開始
```

#### 3. クラス名とファイル名の不一致

**エラーメッセージ**：
```
error: class HelloWorld is public, should be declared in a file named HelloWorld.java
```

**原因と対処**：
- ファイル名： `Sample.java`
- クラス名： `public class HelloWorld`
- → ファイル名を`HelloWorld.java`に変更、またはクラス名を`Sample`に変更

### import文の使い方

外部のクラスやライブラリを使用する際は`import`文が必要な場合があります。

```java
import java.util.Scanner;  // Scannerクラスを使用するための宣言

public class InputExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Scannerを使用した処理
    }
}
```

**基本パッケージは自動的にimport済み**：
- `java.lang`パッケージ（`String`, `System`など）は自動的に利用可能
- そのため`System.out.println()`にはimport文は不要

---

これらの基本構文を理解していれば、章末の練習課題に安心して取り組むことができます。わからないことがあれば、この節に戻って確認してください。

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
- 文字列の中に、指定された基数による桁には使えない文字がある。ただし、文字列の長さが1よりも大きい場合は、1番目の文字がマイナス記号'-'（'\u002D'）またはプラス記号'+' ('\u002B')であってもかまわない。
- 文字列によって表される値が、int型の値ではない。

参考： [Integer (Java SE 21 & JDK 21)](https://docs.oracle.com/javase/jp/21/docs/api/java.base/java/lang/Integer.html#parseInt(java.lang.String,int))



### 画面に文字列を出力する方法

Javaでは、画面に文字列を出力する方法として、いくつかの方法があります。

#### 1.`System.out.println`
この出力方法は、指定された文字列（String型の変数や式でもOK）を改行コードを末尾につけて出力する方法です。

```java
System.out.println("Hello, World!!");
```

これをC言語でたとえるなら、以下のようになるはずです。

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

---

## 章末演習

### 本章で身につけるスキル
- Java開発環境での基本的なプログラム作成
- 変数の宣言と基本的な演算
- 標準出力を使った結果表示
- コンパイルから実行までの一連の流れ

### 基本課題【必須】
第1章の基本概念を確実に習得するための課題です。すべて完了してから次の章に進みましょう。

**課題の場所**: `exercises/chapter01/basic/`

1. **Hello World の拡張** (`Exercise01_HelloWorld.java`)
   - 自分の名前と現在の年を表示するプログラムの作成
   
2. **変数を使った自己紹介** (`Exercise02_SelfIntroduction.java`)
   - 各種データ型を使った情報表示プログラムの実装
   
3. **基本計算プログラム** (`Exercise03_BasicCalculation.java`)
   - 四則演算と余りの計算を行うプログラムの作成
   
4. **データ型の理解** (`Exercise04_DataTypes.java`)
   - さまざまなデータ型の変数宣言と出力

### 発展課題【推奨】
基本課題が完了したら、より応用的な課題に挑戦してみましょう。

**課題の場所**: `exercises/chapter01/advanced/`

1. **高機能計算機** (`Calculator.java`)
   - 小数計算、統計計算、型変換を含む計算プログラム
   
2. **個人情報管理システム** (`PersonalInfo.java`)
   - BMI計算、文字列操作を組み合わせた情報管理プログラム
   
3. **時間計算プログラム** (`TimeCalculation.java`)
   - 時間の単位変換と計算を行うプログラム

### チャレンジ課題【任意】
さらなるスキルアップを目指す方向けの高度な課題です。

**課題の場所**: `exercises/chapter01/challenge/`

### 学習の進め方
1. `exercises/chapter01/basic/README.md` で詳細な課題内容を確認
2. 各JavaファイルのToDoコメントに従って実装
3. コンパイル・実行して動作確認
4. `solutions/` フォルダの解答例で学習ポイントを確認

### 完了の目安
- [ ] 基本課題4つすべてが正常に動作する
- [ ] Java開発環境でのコンパイル・実行ができる
- [ ] 変数の宣言と基本的な演算ができる
- [ ] 各データ型の特徴を理解している

**次のステップ**: 第1章の課題が完了したら、第2章「オブジェクト指向の考え方」に進みましょう。クラスとオブジェクトの基本概念を学習します。
