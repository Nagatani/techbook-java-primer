# <b>2章</b> <span>Java基本文法</span> <small>変数・制御構造・メソッドの基礎</small>

## 本章の学習目標

### この章で学ぶこと

1. Javaの基本データ型と変数
    - 8つのプリミティブ型の使い分け
    - 変数の宣言と初期化の方法
    - 型変換（暗黙的・明示的）の理解
    - 8つのプリミティブ型の使い分け、変数の宣言と初期化、型変換の理解
2. 演算子と制御構造
    - 各種演算子の使い方、if-else文、switch文、for文、while文による制御
3. 配列とメソッド
    - 配列の操作方法、staticメソッドの定義と呼び出し、コードの構造化
4. 文字列処理とコンソール入出力
    - Stringクラスの基本、Scannerクラスによる入力、StringBuilderの活用

### この章を始める前に

第1章で開発環境を構築し、C言語で変数、条件分岐、ループ、関数を理解していれば準備完了です。

Javaの文法はC言語と似ていますが、より安全で使いやすく設計されています。

## 章の構成

本章では、Javaの基本文法を体系的に学習します。C言語との比較を交えながら、以下の内容を順に学んでいきます。

1. Javaの特徴 - 言語の基本思想とJVMの役割
2. 基本データ型 - 8つのプリミティブ型の理解
3. 変数の宣言と初期化 - 変数の扱い方の基礎
4. 演算子 - 各種演算子の使い方
5. 型変換 - 暗黙的・明示的な型変換
6. コンソール入出力 - Scannerクラスの活用
7. 制御構造 - 条件分岐と繰り返し処理
8. 配列 - データの集合を扱う方法
9. メソッド - 処理の構造化と再利用
10. 文字列処理 - Stringクラスの活用

各セクションでは実践的なサンプルコードを通じて、Javaプログラミングの基礎を確実に身につけていきます。

## はじめに

本章では、プログラミング言語Javaの基本文法を学習します。Javaは1995年にSun Microsystems（現Oracle）によって発表され、現在も世界中で広く使用されている主要なプログラミング言語の1つです。

### Javaの誕生と基本思想

Javaは「Write Once, Run Anywhere（一度書けば、どこでも動く）」という革新的な思想のもとに設計されました。これは、Java仮想マシン（JVM）という実行環境上でプログラムを動作させることで実現されています。C言語のように特定のハードウェアやOSに依存せず、JVMさえあればWindows、macOS、Linuxなど、どの環境でも同じプログラムが動作します。

### Java仮想マシン（JVM）の主要な特徴

JVMは、Javaプログラムの実行環境として、従来のコンパイル型言語にはない革新的な機能を提供します。最も重要な特徴はプラットフォーム独立性です。JavaソースコードはJavaコンパイラによってバイトコードと呼ばれる中間言語に変換され、このバイトコードがJVM上で実行されます。これにより、OSやハードウェアの違いを意識することなく、同じプログラムをさまざまな環境で動作させることができます。

もう1つの重要な特徴は自動メモリ管理です。C言語では`malloc()`と`free()`を使って手動でメモリ管理を行う必要がありましたが、Javaではガベージコレクションというしくみにより、使用されなくなったメモリを自動的に解放します。これにより、メモリリークやダングリングポインタといった、C言語プログラミングで頻繁に遭遇する問題から解放されます。

さらに、JVMはセキュリティの面でも優れています。サンドボックス環境と呼ばれる制限された実行環境により、悪意のあるコードがシステムに損害を与えることを防ぎます。また、実行時最適化も重要な機能です。JIT（Just-In-Time）コンパイラは、プログラムの実行中に頻繁に使用される部分を検出し、それらをネイティブコードに変換することで、インタプリタ型言語の柔軟性とコンパイル型言語の高速性を両立させています。

### なぜJavaを学ぶのか

Javaは、その誕生から四半世紀以上が経過した現在でも、世界中で最も広く使用されているプログラミング言語の1つです。特に企業システム開発の分野では重要な役割を果たしています。銀行の基幹システム、保険会社の契約管理システム、政府機関の公共サービスシステムなど、私たちの生活を支える重要なインフラストラクチャの多くがJavaで構築されています。これは、Javaが提供する高い信頼性、拡張性、そして長期的なサポートが評価されているためです。

Webアプリケーション開発においても、Javaは主要な選択肢の1つです。Spring BootやJakarta EEといった成熟したフレームワークにより、複雑なビジネスロジックを含むWebアプリケーションを素早く開発できます。また、モバイル開発の分野では、AndroidアプリケーションがJavaベースで開発されてきた歴史があり、現在でもKotlinと並んで重要な開発言語となっています。

さらに近年では、ビッグデータ処理の分野でもJavaの重要性が増しています。Apache HadoopやApache Sparkといった分散処理フレームワークの多くがJavaで実装されており、大規模データの処理・分析においてJavaの知識は不可欠となっています。

本章では、このような実践的な開発に必要なJavaの基本文法を、C言語との比較を交えながら体系的に学習します。


## Javaの特徴

Javaがこれほど長期にわたって広く使用されている理由は、言語設計における優れた特徴にあります。最も象徴的な特徴はプラットフォーム独立性であり、「Write Once, Run Anywhere（一度書けば、どこでも動く）」というスローガンに集約されています。この特徴により、Windows上で開発したプログラムを、再コンパイルすることなくLinuxやmacOSで実行できます。

Javaではすべてのコードをクラスのなかに記述する必要がありますが、この章では、C言語のような手続き型プログラミングスタイルでJavaプログラムを作成する方法を学習します。オブジェクト指向プログラミングの詳細については、第3章以降で学習します。

ガベージコレクションによる自動メモリ管理は、プログラマを煩雑なメモリ管理から解放します。C言語でよく発生するメモリリークやセグメンテーション違反といった問題を心配する必要がなくなり、ビジネスロジックの実装に集中できます。

また、Javaは強い型付けを採用しており、コンパイル時に多くのエラーを検出できます。これにより、実行時エラーを減らし、より信頼性の高いプログラムを作成できます。さらに、豊富なライブラリエコシステムも大きな魅力です。標準ライブラリだけでなく、Apache Commons、Google Guava、Spring Frameworkなど、数多くの高品質なサードパーティライブラリが利用可能です。

## メモリ管理とガベージコレクション

Javaの最も重要な特徴の1つは、自動メモリ管理（ガベージコレクション）です。C言語ではプログラマがメモリの確保と解放を手動で行う必要がありましたが、Javaではこれを自動化することで、メモリリークやダングリングポインタなどの問題を防ぎます。

ガベージコレクションは世代別仮説（多くのオブジェクトは短命であるという観察にもとづく仮説）にもとづいて設計されており、新しく作成されたオブジェクトをYoung世代、生き残ったオブジェクトをOld世代に分けて管理します。これにより、短命なオブジェクトを高速に回収できます。

現代のJVMでは、G1 GC（Garbage First Garbage Collector）やZGCなどの低遅延コレクタが利用可能です。G1 GCは大きなヒープメモリでも予測可能な停止時間を実現し、ZGCはミリ秒レベルの停止時間で数テラバイトのヒープを管理できます。これらにより、大規模なアプリケーションでも実用的なパフォーマンスを実現しています。


## 基本データ型

Javaのデータ型システムは、プログラムの安全性と効率性を両立させるよう慎重に設計されています。Javaには8つの基本データ型（プリミティブ型）があり、これらは数値、文字、真偽値を表現するための基本的な構成要素です。C言語と比較すると、型のサイズがプラットフォームに依存しないという重要な違いがあります。

### なぜ異なるサイズの整数型が必要なのか

実際のプログラミングでは、扱うデータの範囲と使用するメモリ量のトレードオフを考慮して型を選択します。たとえば、人の年齢を表すのに`long`型（8バイト）を使うのは7バイトのメモリをムダにしますし、逆に世界人口（約80億）を`byte`型（-128～127）で表すことは不可能です。以下のコード例では、各基本データ型の宣言と、それぞれの実用的な使用場面を示します。

#### データ型の実用的な使い分け

各基本データ型には明確な用途があります。byte型は画像データやネットワークパケット、short型は音声データや温度センサーの値に使用されます。int型は一般的なカウンタやインデックス、long型はタイムスタンプやファイルサイズの表現に適しています。用途に応じた選択により、メモリ効率と表現力のバランスを取ることができます。

<span class="listing-number">**サンプルコード2-1**</span>

```java
// 整数型の使い分け例
byte age = 25;                    // ①
short temperatureCelsius = -273;  // ②
int tokyoPopulation = 14000000;   // ③
long worldPopulation = 8000000000L; // ④

// 浮動小数点型の使い分け例
float productPrice = 1980.50f;    // ⑤
double piValue = 3.14159265359;   // ⑥

// その他の型の実用例
char grade = 'A';                 // ⑦
boolean isStudent = true;         // ⑧
```

#### 各データ型の選択理由

- ①　byte型（年齢）
    + 0〜127の範囲で十分な人間の年齢には、最小サイズのbyte型は適切であるが、実際にはintでよい。
- ②　short型（温度）
    + 絶対零度（-273℃）から数千度までカバーでき、byteでは不足、intは過剰といった場合に使う。基本的にはintでよい。
- ③　int型（都市人口）
    + 数百万〜数千万の範囲を扱う一般的な整数にはint型が標準的。整数はとりあえずintでよい。
- ④　long型（世界人口）
    + 80億という値はintの最大値（約21億）を超えるため、long型が必要。末尾のLは数値リテラルとしての表現で必須。
- ⑤　float型（商品価格）
    + 小数点以下2桁程度の精度で十分な外国の金額などにはfloat型でメモリ節約可能。リテラルとしての表現として末尾のfは必須。通常はdoubleでよい。
- ⑥　double型（円周率）
    + 科学計算や高精度が必要な場合は、15桁の精度を持つdouble型を使用。
- ⑦　char型（評価）
    + 単一文字（A、B、C等）を表現する場合はchar型が最適。
- ⑧　boolean型（フラグ）
    + true/falseの2値のみを扱う場合は、可読性の高いboolean型を使用。

> ここで挙げたデータ型はあくまでも基本型です。とくに浮動小数点型であるfloatとdoubleでは、小数点以下の数値が含まれる場合や大きな数字を扱う場合に丸め誤差が発生する可能性もあります。
> Javaでは、誤差が発生しなくなる計算方法があります。`BigDecimal`クラスなどが代表的です。現時点では解説しませんが、基本型だけでは対処しきれない問題もあることを知っておきましょう。

#### 実際の開発での型選択の指針
- メモリ効率が重要な場合：配列やコレクションで1000万件以上のデータを扱う場合は、byteやshortなど最小限のメモリで表現できる型を選択
- 計算精度が重要な場合：通貨計算や税率計算など正確な小数計算には`BigDecimal`、科学計算や物理シミュレーションには15桁精度の`double`を使用
- 可読性を優先する場合：ビジネスロジックの実装では、メモリ効率よりコードの理解しやすさを優先し、整数は`int`、小数は`double`を標準とする

#### 重要な違い（C言語との比較）
- `char`型は16ビット（Unicode対応）
- `boolean`型は`true`/`false`のみ（0/1ではない）
- 各型のサイズは環境によらず固定

## 変数の宣言と初期化

Javaでは、すべての変数は使用前に宣言する必要があり、型の明示が必須です。これはC言語と同様ですが、Javaはより厳格な初期化ルールを持っています。ローカル変数は明示的に初期化しないと使用できません。また、`final`キーワードを使用することで、一度だけ値を設定できる定数を作成できます。以下の例で、さまざまな変数宣言のパターンを示します。

<span class="listing-number">**サンプルコード2-2**</span>

```java
// 宣言と同時に初期化
int count = 0;
String message = "Hello";

// 宣言後に初期化
double price;
price = 100.0;

// 定数の宣言（finalキーワード）
final double PI = 3.14159265359;
```

### 変数の命名規則

#### 基本的なルール
- 最初の文字は英字、`_`、`$`のいずれか（数字は不可）
- 2文字目以降は英字、数字、`_`、`$`を使用可能
- Javaの予約語（`int`, `class`, `public`など）は使用不可

#### 慣例的なルール
- 変数名は小文字で始める（camelCase）
- 意味のある名前を付ける

<span class="listing-number">**サンプルコード2-3**</span>

```java
// 良い例
int studentAge = 20;
String userName = "yamada";
double totalPrice = 12500.0;

// 推奨されない例
int a = 20;              // 意味が不明
String _name = "yamada"; // アンダースコアで開始（慣例違反）

// 状況による例
double 価格 = 12500.0;   // 日本語（技術的には可能だが可読性の点から推奨されない現場もある）
```

### コメントの書き方

コードの説明や覚書のためにコメントを使用します。コメントはコンパイル時に無視されます。

#### 単行コメント

//で始まる単行コメントは、その行の残りをすべてコメントとして扱います。簡潔な説明や一時的なメモに適しています。

<span class="listing-number">**サンプルコード2-4**</span>

```java
// これは単行コメントです
int age = 20;  // 変数の後にも書けます
```

#### 複数行コメント

/*と*/で囲まれた範囲は、複数行にわたるコメントとして扱われます。詳細な説明や一時的なコードの無効化に使用します。

<span class="listing-number">**サンプルコード2-5**</span>

```java
/*
 これは複数行にわたる
 コメントです
 */
int price = 1000;
```

### セミコロンの必要性

Javaでは、ほとんどの文の終わりにセミコロン（`;`）が必要です。

<span class="listing-number">**サンプルコード2-6**</span>

```java
int x = 10;                    // 変数宣言
System.out.println("Hello");  // メソッド呼び出し
x = x + 5;                     // 代入
```

#### セミコロンが不要な場合

クラスやメソッドの宣言、ブロックの終わりなど、構造的な要素にはセミコロンは不要です。ブロック（{}）自体が文の区切りとして機能します。

<span class="listing-number">**サンプルコード2-7**</span>

```java
public class Sample {          // クラス宣言
    public static void main(String[] args) {  // メソッド宣言
        // 処理
    }                          // ブロックの終わり
}                              // クラスの終わり
```

## 演算子

Javaの演算子体系は、C言語の演算子をベースにしていますが、型安全性を高めるためのいくつかの重要な改良が加えられています。特に注目すべきは、論理演算子における短絡評価の明確な仕様化と、文字列を連結する演算子（`+`）のオーバーロードです。以下のコード例では、Javaで利用可能な主要な演算子とその動作を示します。

#### 演算子の分類と実行例

Javaの演算子は目的別に分類され、それぞれ異なる動作と優先順位を持ちます。

<span class="listing-number">**サンプルコード2-8**</span>

```java
// 算術演算子
int a = 10, b = 3;
int sum = a + b;      // ① 13
int diff = a - b;     // ① 7
int prod = a * b;     // ① 30
int quot = a / b;     // ② 3
int rem = a % b;      // ③ 1

// インクリメント・デクリメント
int x = 5;
int y = x++;  // ④ y = 5, x = 6
int z = ++x;  // ⑤ z = 7, x = 7

// 比較演算子
boolean result1 = (a > b);   // ⑥ true
boolean result2 = (a == b);  // ⑥ false
boolean result3 = (a != b);  // ⑥ true

// 論理演算子
boolean p = true, q = false;
boolean and = p && q;        // ⑦ false
boolean or = p || q;         // ⑦ true
boolean not = !p;            // ⑦ false

// ビット演算子
int m = 5;    // 0101
int n = 3;    // 0011
int bitAnd = m & n;  // ⑧ 0001 = 1
int bitOr = m | n;   // ⑧ 0111 = 7
int bitXor = m ^ n;  // ⑧ 0110 = 6
```

#### 演算子の詳細動作

- ①　基本算術演算
    + 加算、減算、乗算は通常の数学演算と同じ。
- ②　整数除算
    + int同士の除算は小数部を切り捨て（10÷3=3）
- ③　剰余演算
    + 割り算の余りを返す（10÷3の余り1）
- ④　後置インクリメント
    + 値を使用してから1増加（yに5を代入後、xを6に）
- ⑤　前置インクリメント
    + 1増加してから値を使用（xを7にしてからzに代入）
- ⑥　比較演算
    + 2つの値を比較して真偽値を返す。
- ⑦　論理演算
    + &&と||は短絡評価（左辺で結果が確定したら右辺を評価しない）
- ⑧　ビット演算
    + 2進数のビット単位で論理演算を実行。

### 演算子の優先順位

Javaの演算子には優先順位があり、式の評価順序を決定します。以下は主要な演算子の優先順位表です（上位ほど優先順位が高い）。

| 演算子 | 説明 | 結合性 |
|--------|------|-------------|
| `()` `[]` `.` | 括弧、配列アクセス、メンバーアクセス | 左→右 |
| `++` `--` (後置) | 後置インクリメント/デクリメント | 左→右 |
| `++` `--` (前置) `+` `-` `!` `~` | 単項演算子 | 右→左 |
| `*` `/` `%` | 乗算、除算、剰余 | 左→右 |
| `+` `-` | 加算、減算 | 左→右 |
| `<<` `>>` `>>>` | シフト演算 | 左→右 |
| `<` `<=` `>` `>=` `instanceof` | 比較演算 | 左→右 |
| `==` `!=` | 等価演算 | 左→右 |
| `&` | ビットAND | 左→右 |
| `^` | ビットXOR | 左→右 |
| <code>\|</code> | ビットOR | 左→右 |
| `&&` | 論理AND | 左→右 |
| <code>\|\|</code> | 論理OR | 左→右 |
| `?:` | 条件（三項）演算子 | 右→左 |
| `=` `+=` `-=` など | 代入演算子 | 右→左 |

#### 優先順位の実例

<span class="listing-number">**サンプルコード2-9**</span>

```java
// 優先順位により結果が変わる例
int a = 2 + 3 * 4;     // 14 (3*4が先、その後+2)
int b = (2 + 3) * 4;   // 20 (括弧内が優先)

// 論理演算の優先順位
boolean result = true || false && false;  // true (&&が||より優先)
// 上記は true || (false && false) と解釈される

// 代入と比較の優先順位
int x = 5;
boolean check = x = 3 > 0;  // コンパイルエラー
boolean check2 = (x = 3) > 0;  // OK: x=3の後、3>0を評価
```

> **注意**: 複雑な式では括弧を使用して意図を明確にすることを推奨します。コンパイラの最適化により、明示的な括弧の使用がパフォーマンスに悪影響を与えることはありません。

## 型変換

### 型変換の基本概念と設計思想

Javaの型変換システムは、プログラムの安全性を保証するために厳格に設計されています。C言語では暗黙的に行われていた多くの型変換が、Javaでは明示的なキャストを要求されます。これは一見煩雑に思えますが、予期しないデータ損失やバグを防ぐ重要なしくみです。

### 型変換の分類

型変換には、データの精度を失わない拡大変換（widening conversion）と、精度を失う可能性がある縮小変換（narrowing conversion）があります。

#### 拡大変換の特徴
- データ損失がない安全な変換
- 暗黙的に（自動的に）実行される
- 小さい型から大きい型への変換
- 例`byte → short → int → long → float → double`

#### 縮小変換の特徴
- データ損失の可能性がある
- 明示的なキャストが必要
- 大きい型から小さい型への変換
- プログラマーの責任で実行

### 他言語との比較による理解

#### C言語との違い
```c
// C言語では暗黙的な変換が多い
double pi = 3.14159;
int truncated = pi;  // 警告は出るが、コンパイルは通る
```

C言語では、上記のような危険な変換も警告レベルで許可されますが、Javaではコンパイルエラーになります。


<span class="listing-number">**サンプルコード2-10**</span>

```java
public class TypeConversionExample {
    public static void main(String[] args) {
        // 暗黙的な型変換（拡大変換）
        int i = 100;
        long l = i;        // OK: int → long（32ビット → 64ビット）
        double d = i;      // OK: int → double（整数 → 浮動小数点）
        
        System.out.println("int: " + i);     // 100
        System.out.println("long: " + l);    // 100
        System.out.println("double: " + d);  // 100.0
        
        // 明示的な型変換（縮小変換）
        double pi = 3.14159;
        int truncated = (int) pi;  // 3（小数部分は切り捨て）
        
        // オーバーフローの例
        int largeInt = 130;
        byte smallByte = (byte) largeInt;  // -126（オーバーフロー）
        
        System.out.println("元の値: " + largeInt);
        System.out.println("byte変換後: " + smallByte);
        
        // 文字列との変換
        String str = "123";
        int num = Integer.parseInt(str);     // 文字列→整数
        String str2 = String.valueOf(num);   // 整数→文字列
        
        // 数値解析のエラー処理
        try {
            String invalidStr = "abc";
            int invalid = Integer.parseInt(invalidStr);  // NumberFormatException
        } catch (NumberFormatException e) {
            System.out.println("数値変換エラー: " + e.getMessage());
        }
    }
}
```

### 型変換の実践的な利点

#### 精度を考慮した計算
<span class="listing-number">**サンプルコード2-31**</span>

```java
// 整数同士の除算での落とし穴
int a = 5, b = 2;
double result1 = a / b;         // 2.0（整数除算後に変換）
double result2 = (double)a / b; // 2.5（浮動小数点除算）
```

#### メモリ効率の最適化
<span class="listing-number">**サンプルコード2-32**</span>

```java
// 大量のデータを扱う場合の型選択
byte[] imageData = new byte[1024 * 1024];  // 1MBのバイト配列
int[] inefficient = new int[1024 * 1024];   // 4MBの整数配列（非効率）
```

#### APIとの整合性
<span class="listing-number">**サンプルコード2-33**</span>

```java
// 多くのJava APIは特定の型を要求
Math.sqrt(25);        // double型を要求
Math.round(3.7f);     // float型を受け付ける
Collections.shuffle(list, new Random(42L));  // long型のシード
```

### 型変換のベストプラクティス

1. 必要最小限のキャスト
    + 型変換は最小限に抑え、設計段階でデータの性質に合った型を選択する
2. データ損失の確認
    + 縮小変換を行う前に、値の範囲を確認する
3. 例外処理の実装
    + 文字列から数値への変換では、NumberFormatExceptionをキャッチしてエラーメッセージを表示するなどの例外処理を実装する
4. 定数の型指定
    + リテラルには明示的な型サフィックスを使用（long型には`100L`、float型には`3.14f`、double型には`3.14d`またはサフィックスなし）

## コンソール入出力

### 標準入出力の設計思想

プログラムとユーザーの対話は、多くのアプリケーションにおいて基本的な要素です。Javaでは、標準入出力を扱うためのクラスが`java.lang`パッケージと`java.util`パッケージに用意されています。C言語の`printf()`や`scanf()`に相当する機能を提供しています。

### Javaの入出力設計の特徴

1. 標準入出力ストリーム
- `System.out`：標準出力（画面への出力）
- `System.in`：標準入力（キーボードからの入力）
- `System.err`：標準エラー出力

2. 型安全性の重視。
- `Scanner`クラスによる型別の入力メソッド
- 入力値の検証機能
- 例外処理による安全な入力処理

3. 国際化対応。
- Unicodeによる多言語対応
- ロケール設定による書式制御
- 文字エンコーディングの自動処理

### 他言語との比較

#### C言語との違い
```c
// #include <stdio.h> が必要

// C言語の入出力
printf("Hello, %s!\n", name);
scanf("%d", &age);  // バッファオーバーフローの危険性
```

<span class="listing-number">**サンプルコード2-11**</span>

```java
import java.util.Scanner;
import java.util.InputMismatchException;

public class IOExample {
    public static void main(String[] args) {
        // === 出力メソッドの使い分け ===
        System.out.println("Hello, World!");      // 改行付き出力
        System.out.print("改行なし");             // 改行なし出力
        System.out.print("出力\n");               // 手動で改行を追加
        
        // printf による書式付き出力（C言語風）
        System.out.printf("整数: %d, 浮動小数点: %.2f%n", 10, 3.14159);
        System.out.printf("文字列: %-10s | 右寄せ: %10s%n", "左寄せ", "右寄せ");
        
        // === Scanner による安全な入力処理 ===
        Scanner scanner = new Scanner(System.in);
        
        // 文字列入力
        System.out.print("名前を入力してください: ");
        String name = scanner.nextLine();
        
        // 整数入力（エラー処理付き）
        int age = 0;
        boolean validInput = false;
        while (!validInput) {
            System.out.print("年齢を入力してください: ");
            try {
                age = scanner.nextInt();
                scanner.nextLine(); // 改行文字を消費
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("エラー: 整数を入力してください");
                scanner.nextLine(); // 不正な入力をクリア
            }
        }
        
        // 入力値の確認出力
        System.out.printf("%sさん（%d歳）、こんにちは！%n", name, age);
        
        // リソースの解放
        scanner.close();
    }
}
```

### Scanner クラスの高度な使い方

#### 1. 型別の入力メソッド
<span class="listing-number">**サンプルコード2-34**</span>

```java
Scanner sc = new Scanner(System.in);

// 各種データ型の入力
int intValue = sc.nextInt();           // 整数
double doubleValue = sc.nextDouble();  // 浮動小数点
boolean boolValue = sc.nextBoolean();  // 真偽値
String word = sc.next();               // 空白までの単語
String line = sc.nextLine();           // 行全体
```

#### 2. 入力検証機能

<span class="listing-number">**サンプルコード2-35**</span>

```java
Scanner sc = new Scanner(System.in);

// 入力可能かチェック
if (sc.hasNextInt()) {
    int value = sc.nextInt();
    System.out.println("入力値: " + value);
} else {
    System.out.println("整数ではありません");
    sc.next(); // 不正な入力をスキップ
}
```

#### 3. 区切り文字の変更

```java
Scanner sc = new Scanner("apple,banana,orange");
sc.useDelimiter(",");  // カンマ区切りに変更

while (sc.hasNext()) {
    System.out.println(sc.next());
}
// 出力: apple, banana, orange（各行）
```

### 入出力のベストプラクティス

#### 1. エラー処理の重要性
入力処理では必ず例外処理を実装し、ユーザーの誤入力に対して適切に対応することが重要です。

```java
import java.util.InputMismatchException;
import java.util.Scanner;

public class SafeInputExample {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("整数を入力してください: ");
            try {
                int number = scanner.nextInt();
                System.out.println("入力された数値: " + number);
                break;
            } catch (InputMismatchException e) {
                System.out.println("エラー: 整数を入力してください");
                scanner.nextLine(); // 不正な入力をクリア
            }
        }
        
        scanner.close();
    }
}
```

#### 2. リソース管理

`Scanner`などのリソースは使用後に必ず`close()`メソッドで解放します。Java 7以降では`try-with-resources`構文も使用できます。

```java
try (Scanner sc = new Scanner(System.in)) {
    // Scannerの使用
} // 自動的にcloseされる
```

#### 3. バッファリングの理解
`nextInt()`などの後に`nextLine()`を使う場合、改行文字が残ることに注意が必要です。

#### 4. ロケールへの配慮
数値の書式は地域によって異なる（ドイツやフランスでは小数点がカンマ、千の位の区切りがピリオド）ため、国際化を考慮する場合はLocale.USやLocale.GERMANYなどのロケール設定が必要です。


## 制御構造

#### 事前説明
制御構造は、プログラムの流れ（フロー）を制御する基本的なしくみです。条件分岐と繰り返し処理により、プログラムに複雑な判断力と処理能力を与えることができます。Javaの制御構造は、C言語の優れた設計を継承しつつ、より安全で表現力豊かな機能を提供しています。

### 条件分岐

#### 事前説明
条件分岐は、プログラムが状況に応じて異なる処理を選択するしくみです。成績判定、ユーザー入力の検証、システム状態の確認など、実用的なプログラムには不可欠な機能です。

<span class="listing-number">**サンプルコード2-12**</span>

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

##### このコードで学習できる重要な概念

- 多段階の条件分岐`else if`の連鎖により、複数の条件を順次評価できる。これは成績判定のような段階的評価に最適である
- 論理的な条件設計条件を`score >= 90`から順番に書くことで、重複しない明確な判定ロジックを実現している
- switch式の活用Java 12以降の新しいswitch式では、より簡潔で関数型プログラミングに近い記述が可能である
- 複数ケースの同時処理`case 10, 9 ->`のように、複数の値に対して同じ処理を適用できる
- 型安全な分岐条件式は必ず`boolean`型である必要があり、C言語のような整数による条件分岐は許可されません

##### 新旧switch文の比較による学習ポイント

###### 従来のswitch文

<span class="listing-number">**サンプルコード2-13**</span>

```java
String grade;
switch (score / 10) {
    case 10:
    case 9:
        grade = "優";
        break;
    case 8:
        grade = "良";
        break;
    case 7:
        grade = "可";
        break;
    default:
        grade = "不可";
        break;
}
```

###### 新しいswitch式

<span class="listing-number">**サンプルコード2-14**</span>

```java
String grade = switch (score / 10) {
    case 10, 9 -> "優";
    case 8 -> "良";
    case 7 -> "可";
    default -> "不可";
};
```

###### 学習できる進化のポイント
- fall-through問題の解決
    + 新しいswitch式では`break`文が不要で、意図しないfall-throughバグを防げる
- 式としての利用
    + switch文が値を返すことができるようになり、変数への代入が直接可能である
- 簡潔な記述
    + `->`記法により、コードがより読みやすく保守しやすくなる

### 繰り返し処理

#### 事前説明
繰り返し処理（ループ）は、同じ処理を何度も実行するためのしくみです。配列の全要素を処理する、計算を指定回数繰り返す、条件が満たされるまで処理を続けるなど、プログラミングの基本的なパターンを実現します。Javaでは、用途に応じて3つの主要なループ構文を提供しています。

<span class="listing-number">**サンプルコード2-15**</span>

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

##### このコードで学習できる重要な概念

- for文の3つの要素
    + 初期化（`int i = 0`）、条件（`i < 10`）、更新（`i++`）の明確な分離により、ループの動作が理解しやすくなっている
- 拡張for文の利便性
    + 配列やコレクションの全要素を処理する際に、インデックス管理が不要で安全である。Java 5で導入されたこの機能により、境界値エラーによるバグがほぼ解消されました
- while文の柔軟性
    + 条件が複雑な場合や、ループ回数が事前に決まらない場合に適している
- ループ変数のスコープ
    + for文内で宣言された変数`i`は、ループ内でのみ有効である。これにより名前空間の汚染を防げる
- 配列との連携
    + 拡張for文では、配列の要素に直接アクセスでき、境界値エラー（IndexOutOfBoundsException）を防げる

##### 各ループの使い分けの指針

- for文繰り返し回数が明確で、カウンタ変数が必要な場合
- 拡張for文配列やコレクションの全要素を順次処理する場合
- while文複雑な条件や、繰り返し回数が動的に決まる場合

##### C言語との比較における重要なポイント

- 拡張for文はC言語にはない、Javaの重要な改良点である
- ループ変数のスコープがより厳密に管理されている
- 配列アクセスの安全性がコンパイル時に保証される

### switch文

switch文は、変数の値によって処理を分岐させる制御構造です。多数のif-else文を使用するよりも読みやすくなります。

<span class="listing-number">**サンプルコード2-16**</span>

```java
// switch文の基本的な使い方
public class SwitchExample {
    public static void main(String[] args) {
        int grade = 85;
        String evaluation;
        
        switch (grade / 10) {
            case 10:
            case 9:
                evaluation = "優秀";
                break;
            case 8:
                evaluation = "良い";
                break;
            case 7:
                evaluation = "普通";
                break;
            default:
                evaluation = "要努力";
                break;
        }
        
        System.out.println("評価: " + evaluation);
    }
}
```

#### switch文の注意点

- 各caseの最後には`break`文を書く（忘れると次のcaseも実行される）
- `default`節で想定外の値に対応する
- 同じ処理をする複数の値は連続して記述できる

## 配列

#### 事前説明
配列は、同じ型の複数のデータを一括で管理するための基本的なデータ構造です。学生の成績管理、商品の在庫管理、数値計算など、プログラミングの多くの場面で配列が活用されます。Javaの配列は、C言語の配列の利便性を継承しつつ、境界チェックや自動初期化により安全性を向上させています。

<span class="listing-number">**サンプルコード2-17**</span>

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

##### このコードで学習できる重要な概念

- 配列初期化の2つの方法
    + リテラルによる初期化（`{1, 2, 3, 4, 5}`）と`new`キーワードによる動的作成の使い分けを理解できる
- オブジェクトとしての配列
    + Javaの配列はオブジェクトであり、`length`プロパティなどの便利な機能を持っている
- 境界チェックの自動実行
    + 配列の範囲外アクセスは`ArrayIndexOutOfBoundsException`として検出され、セグメンテーション違反のような深刻な問題を防げる
- 自動初期化
    + `new int[10]`で作成された配列の要素は自動的に0で初期化される。これにより未初期化値による予期しない動作を防げる
- 多次元配列の構造
    + 2次元配列は「配列の配列」として実装され、行列やテーブル形式のデータを直感的に表現できる

##### 配列操作の実用例

<span class="listing-number">**サンプルコード2-18**</span>

```java
// 配列の全要素を処理する安全な方法
int[] data = {10, 20, 30, 40, 50};

// インデックスを使用した従来の方法
for (int i = 0; i < data.length; i++) {
    System.out.println("data[" + i + "] = " + data[i]);
}

// 拡張for文を使用した推奨方法
for (int value : data) {
    System.out.println("値: " + value);
}
```

##### C言語との重要な違い

- lengthプロパティ
    + C言語では配列のサイズを別途管理しますが、Javaでは`array.length`で常に正確なサイズを取得できる
- 境界チェック
    + C言語では配列の範囲外アクセスがシステムクラッシュを引き起こす可能性がありますが、Javaでは例外として安全に処理される
- メモリ管理
    + 配列のメモリ確保と解放はJVMが自動的に行うため、メモリリークやダングリングポインタの心配がありません
- 初期化の保証
    + Java配列の要素は必ずデフォルト値で初期化されます（整数型は0、浮動小数点型は0.0、boolean型はfalse、char型は'\u0000'、オブジェクト型はnull）

### 配列操作の応用例

<span class="listing-number">**サンプルコード2-19**</span>

```java
// 配列の要素を検索する
public static int findElement(int[] array, int target) {
    for (int i = 0; i < array.length; i++) {
        if (array[i] == target) {
            return i;  // 見つかった位置を返す
        }
    }
    return -1;  // 見つからなかった
}

// 配列の要素を合計する
public static int sumArray(int[] array) {
    int sum = 0;
    for (int value : array) {
        sum += value;
    }
    return sum;
}

// 配列をコピーする
int[] original = {1, 2, 3, 4, 5};
int[] copy = new int[original.length];
System.arraycopy(original, 0, copy, 0, original.length);

// またはArraysクラスを使用
import java.util.Arrays;
int[] copy2 = Arrays.copyOf(original, original.length);
```

#### 配列使用時の注意点

##### 1. サイズの固定性

+ 一度作成した配列のサイズは変更できません。動的にサイズを変更したい場合は、後の章で学習する`ArrayList`などのコレクションを使用する。

##### 2. 参照型としての配列

+ 配列は参照型ですので、配列変数の代入は参照のコピーになる。

<span class="listing-number">**サンプルコード2-20**</span>

```java
int[] a = {1, 2, 3};
int[] b = a;  // bはaと同じ配列を参照
b[0] = 100;   // a[0]も100になる
```

##### 3. 多次元配列の柔軟性

+ Javaの多次元配列は各行の長さが異なってもかまいません。

<span class="listing-number">**サンプルコード2-21**</span>

```java
int[][] jaggedArray = {
    {1, 2},
    {3, 4, 5, 6},
    {7}
};
```


## メソッド（staticメソッドの基本）

### メソッドとは

メソッドは、特定の処理をまとめて名前を付け、必要なときに呼び出して使用するしくみです。

C言語の関数に相当しますが、Javaではすべてのメソッドはクラスのなかに定義する必要があります。

この章では、mainメソッドから直接呼び出すことができる「staticメソッド」の基本的な使い方を学習します。

<span class="listing-number">**サンプルコード2-22**</span>

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

### staticメソッドの基本構文

```java
public static 戻り値の型 メソッド名(引数リスト) {
    // メソッドの処理
    return 戻り値;  // 戻り値がある場合
}
```

- `public`
    + どこからでもアクセス可能
- `static`
    + クラスのインスタンスを作成せずに呼び出し可能
- 戻り値の型
    + メソッドが返す値の型（値を返さない場合は`void`）
- 引数リスト
    + メソッドに渡すデータ

### staticメソッドの特徴

1. mainメソッドから直接呼び出せる
   - mainメソッドもstaticなので、同じクラス内のstaticメソッドを直接呼び出せる
   - staticがないメソッドは、mainメソッドから直接呼び出すことはできず、メソッドを持つクラスをインスタンス化する必要がある
2. 処理の再利用
   - 同じ処理を何度も書く代わりに、メソッドとして定義して再利用できる
3. プログラムの構造化
   - 処理を機能ごとに分割することで、プログラムが読みやすくなる

### C言語との比較における学習ポイント

- クラス内での定義：Javaのメソッドは必ずクラス内に定義される必要があり、グローバル関数は存在しません
- アクセス制御：`public`、`private`などの修飾子により、メソッドの可視性を細かく制御できる
- static修飾子：クラスのインスタンスを作成せずにメソッドを呼び出すことができる
- 例外処理システム：エラー処理がより構造化され、安全なプログラムの作成を支援する

### Javaのメソッドの基本まとめ

Javaでは、すべてのメソッドはクラスのなかに定義する必要があります。第2章では、mainメソッドから呼び出すことができるstaticメソッドの基本的な使い方を学習しました。staticメソッドは、オブジェクトを生成せずに直接呼び出すことができ、手続き型プログラミングのスタイルでJavaプログラムを作成する際の基本となります。

次章では、クラスとオブジェクトの概念を学び、インスタンスメソッドやコンストラクタなど、より高度な概念を学習します。

## 文字列処理

### C言語とJavaの文字列処理の根本的な違い

Javaにおける文字列処理は、C言語のアプローチとは根本的に異なる設計思想にもとづいています。この違いを理解することは、C言語からJavaへ移行するうえできわめて重要です。

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

<span class="listing-number">**サンプルコード2-23**</span>

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

<span class="listing-number">**サンプルコード2-24**</span>

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

このコードは、Javaの文字列比較における重要な教訓を示しています。`==`演算子はオブジェクトの参照（メモリアドレス）を比較するため、文字列の内容が同じでも異なるオブジェクトであれば`false`を返します。特に注意すべきは、文字列プールの存在により`s1 == s2`が`true`となる点です。これは偶然の一致であり、信頼すべきではありません。常に`equals()`メソッドを使用して文字列の内容を比較することが、バグのない堅牢なプログラムを作成する鍵となります。

### 便利な文字列操作メソッド

Javaの`String`クラスは、テキスト処理に必要な豊富なメソッドを提供しています。これらのメソッドは、Web開発でのユーザー入力の処理、データベースから取得した文字列の整形、ファイルの読み書きなど、実際の開発で頻繁に使用されます。以下のコード例で、もっとも重要な文字列操作メソッドを実践的に学習します。

<span class="listing-number">**サンプルコード2-25**</span>

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

<span class="listing-number">**サンプルコード2-26**</span>

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

<span class="listing-number">**サンプルコード2-27**</span>

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


## import文の使い方

外部のクラスやライブラリを使用する際は`import`文が必要な場合があります。

<span class="listing-number">**サンプルコード2-28**</span>

```java
import java.util.Scanner;  // Scannerクラスを使用するための宣言

public class InputExample {
    public static void main(String[] args) {
        // Scannerオブジェクトの作成（標準入力から読み取る）
        Scanner scanner = new Scanner(System.in);
        
        // ユーザーへの入力促進メッセージ
        System.out.print("整数値を入力してください: ");
        
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

このコードのポイント。
1. import文： `java.util.Scanner`をインポートして入力機能を使用
2. Scanner作成： `new Scanner(System.in)`で標準入力から読み取るScannerを作成
3. 入力の受け取り： `scanner.nextLine()`で1行分の文字列を取得
4. 型変換： `Integer.parseInt()`で文字列を整数に変換
5. リソース管理： 使い終わったら`scanner.close()`でリソースを解放

基本パッケージは自動的にimport済みです。

- `java.lang`パッケージ（`String`, `System`, `Integer`など）は自動的に利用可能
- そのため`System.out.println()`や`Integer.parseInt()`にはimport文は不要

## よくあるコンパイルエラーとその対処法

### セミコロン忘れ

#### エラーメッセージ
```
error: ';' expected
```

#### 原因と対処
<span class="listing-number">**サンプルコード2-29**</span>

```java
// エラー例
int x = 10  // セミコロンがない

// 修正版
int x = 10; // セミコロンを追加
```

### 大文字・小文字の間違い

#### エラーメッセージ
```
error: cannot find symbol
```

#### 原因と対処
<span class="listing-number">**サンプルコード2-30**</span>

```java
// エラー例
system.out.println("Hello");  // systemが小文字

// 修正版
System.out.println("Hello");  // Systemを大文字で開始
```

### クラス名とファイル名の不一致

#### エラーメッセージ
```
error: class HelloWorld is public, should be declared in a file named HelloWorld.java
```

#### 原因と対処
- ファイル名 `Sample.java`
- クラス名 `public class HelloWorld`
- → ファイル名を`HelloWorld.java`に変更、またはクラス名を`Sample`に変更

## Java基本文法の総合的な理解

本章で学習した各要素は、独立したテクニックではなく、現代のソフトウェア開発における包括的な設計哲学の一部です。出力メソッドから始まり、データ型、変数、演算子、制御構造、配列、メソッドまで、すべてがJavaの「安全性」「保守性」「可読性」という3つの核心原則にもとづいて設計されています。

学習した内容の相互関係と実践への応用。

- 型安全性の一貫した実装`System.out.println`による自動型変換、明示的な変数宣言、メソッドのシグネチャ、配列の境界チェックなど、すべての機能が型の整合性を保証している
- 次章への基盤構築：staticメソッド、クラス内でのメソッド定義、String型の活用など、本章で学んだ要素はすべて次章以降の学習への準備となっている
- C言語からの進化の理解単なる機能追加ではなく、ソフトウェア開発の課題（メモリ管理、プラットフォーム依存性、保守性）を根本的に解決するための設計思想の変革を体験できました

現代的なプログラミング手法への接続。

今回学習した基本文法は、現代のJava開発の基盤となります。フレームワーク開発ではSpring Bootを使ったREST APIの実装、Webアプリケーション構築ではServletやJSPの基礎、マイクロサービスアーキテクチャではサービス間の通信処理などに活用されます。型安全性、メソッド設計などの概念は、企業での実際の開発において直接活用される重要なスキルです。

### まとめ

本章で学習した内容は、Javaプログラミングの基礎を形成する重要な要素です。基本的なデータ型から始まり、制御構造、配列、メソッド、文字列処理まで、幅広い概念を学習しました。

これらの概念は互いに関連し合い、より大きなプログラムを構築するための基盤となります。とくに重要なのは以下の点です。

1. 型安全性： Javaの強い型付けシステムは、多くのエラーをコンパイル時に検出し、実行時エラーを減らすことで安全なプログラムの作成を支援する
2. メソッドによる構造化： 処理を単一責任の原則にしたがってメソッドに分割することで、テストが容易で保守性と再利用性の高いコードを作成できる
3. 文字列処理の安全性： 不変性を持つStringクラスにより、スレッドセーフでメモリ効率のよい文字列操作が可能である

これらの基礎をしっかりと理解することで、次章以降で学ぶクラスとオブジェクトの概念、そしてより高度なJavaの機能の学習がスムーズに進みます。プログラミングは実践が重要ですので、各概念を学んだ後は必ず自分でコードを書いて試してみることをオススメします。











## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter02/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

#### 実装のポイント
1. 基本データ型の使い分け
   - int、double、boolean、charの用途に応じた使い分け
   - 型変換（キャスト）の練習

2. 変数の宣言と初期化
   - ローカル変数の意味を表す命名（camelCase使用）
   - 定数（final）の活用

#### ControlFlowExercise.java - 制御構造の練習
目的： 条件分岐と繰り返し処理の基本をマスター。

##### 実装のポイント
1. 条件分岐の実装
   - if-else文での複数条件の処理
   - switch文を使った定数時間での分岐処理

2. 繰り返し処理の活用
   - for文での決まった回数の処理
   - while文での条件に基づく繰り返し
   - 拡張for文での配列の処理

#### ArrayExercise.java - 配列の練習
目的： 配列を使ったデータの管理と処理。

##### 実装のポイント
1. 配列の基本操作
   - 配列の宣言、初期化、要素へのアクセス
   - 配列の長さを使った安全な処理

2. 配列を使った計算
   - 合計、平均、最大値、最小値の算出
   - ソートや検索の基本アルゴリズム

### 発展課題へのステップアップ

基礎課題を完了したら、より実践的な発展課題に挑戦しましょう。

#### MethodExercise.java - メソッドの活用
##### 必要な知識
- メソッドの定義と呼び出し
- パラメータと戻り値
- メソッドのオーバーロード

##### 実装のヒント
- 計算処理を複数のメソッドに分割
- 共通処理をメソッドとして抽出
- 同じ名前で異なるパラメータのメソッドを作成

#### StringExercise.java - 文字列処理
##### 必要な知識
- Stringクラスの各種メソッド
- StringBuilderの活用
- 文字列の検索と置換

##### 実装のヒント
- 文字列の分割と結合
- 大文字・小文字の変換
- パターンマッチングの基礎

### よくあるつまずきポイントと解決策

| 問題 | 原因 | 解決方法 |
|------|------|----------|
| "non-static method cannot be referenced from static context" | staticメソッドから非staticメソッドを呼び出そうとした | オブジェクトを作成してからメソッドを呼び出す |
| NullPointerException | 初期化されていないオブジェクトを使用 | newでオブジェクトを作成してから使用 |
| ArrayIndexOutOfBoundsException | 配列の範囲外にアクセス | 配列のlengthを確認してループ条件を見直す |
| コンストラクタが見つからない | 引数の数や型が一致しない | コンストラクタの定義と呼び出しを確認 |

### デバッグのコツ

1. エラーメッセージを読む
   - エラーの種類と発生箇所（行番号）を確認
   - スタックトレースから問題の原因を特定

2. printlnデバッグ
   - 変数の値を途中で出力して確認
   - メソッドの呼び出し順序を追跡

3. 段階的な実装
   - 一度にすべてを実装せず、小さな単位で動作確認
   - コンパイル→実行→確認のサイクルを短く

### 学習を深めるための追加課題

余裕があるほうは、以下の追加課題にも挑戦してみましょう。

1. データ検証の強化
   - 数値入力の範囲チェック
   - 文字列の妥当性検証（空文字、長さ制限など）

2. 機能の拡張
   - 配列データの高度な処理（ソート、検索、フィルタリング）
   - メソッドを活用した再利用可能なコード

3. 総合演習
   - 複数の概念を組み合わせた実践的なプログラム
   - 例簡単な計算機、テキスト解析ツールなど

次のステップ： 基礎課題が完了したら、第3章「オブジェクト指向プログラミングの基礎」に進みましょう。第3章では、これまでのプログラミングとは異なる新しい考え方である「オブジェクト指向」について学びます。
