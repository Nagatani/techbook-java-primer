## 第3章 オブジェクト指向の考え方 - Part D: Java言語の基礎

ここから、Javaを使ったオブジェクト指向プログラミングを学びますが、そもそもそれ以前に、Javaというプログラミング言語の基本構文について学びましょう。

### 3.1 型とリテラル

#### 型

型、またはデータ型とは、プログラミング言語における「データを保持する形式」という認識で（いまのところは）良いでしょう。  
Javaにおける基本的な型の種類は以下の表を参照してください。

| 型名 | サイズ | 用途 | 想定される値 | ラッパクラス |
|-----|-----------------|-----|------------|--------------|
| `boolean` | -  | 真偽値 | true または false | Boolean
| `char` | 2byte | 文字(一文字分) | \u0000 〜 \uffff | Character
| `byte` | 1byte | 整数 | -128 〜 127 | Byte
| `short` | 2byte | 整数 | -32768 〜 32767 | Short
| `int` | 4byte | 整数 | -2147483648 〜 2147483647 | Integer
| `long` | 8byte | 整数 | -9223372036854775808 〜 9223372036854775807 | Long
| `float` | 4byte | 小数 | 1.4e-45 〜 3.4e+38 | Float
| `double` | 8byte | 小数 | 4.9e-324 〜 1.7e+308 | Double

これらの型は、**基本データ型**(primitive data type)または**プリミティブ型**と呼ばれます。

なお、int型では、おおよそ正負21億の整数が格納可能です。
ちなみに、`String`（文字列）は基本データ型ではなく、クラスです。
文字列に関しても、クラスという概念を学んだ後に、より詳しく学びます。

プログラミング初心者の方は、「（いまのところは）boolean、int、double、そしてString」で大丈夫です、と言っておきます。

#### プリミティブ型

Javaには8つのプリミティブ型が存在します。先述の表を参照してください。
これらはオブジェクトではなく、単純な値を表現します。

メモリ効率を重視する場合や、高速な計算処理が必要な場合に使用されます。

#### 参照型

プリミティブ型以外のすべての型は参照型です。これには以下が含まれます：

- クラス（String、Integer、自作のクラスなど）
- インターフェイス
- 配列
- 列挙型（enum）

```java
// プリミティブ型
int number = 42;
double price = 99.99;
boolean isValid = true;

// 参照型
String text = "Hello Java";
Integer boxedNumber = 100;  // Integerクラス（Wrapper）
int[] numbers = {1, 2, 3, 4, 5};  // 配列も参照型
```

#### リテラル

リテラルとは、プログラムのソースコード内で直接表現される値のことです。

##### 整数リテラル

```java
int decimal = 100;      // 10進数
int binary = 0b1100100; // 2進数（0bプレフィックス）
int octal = 0144;       // 8進数（0プレフィックス）
int hex = 0x64;         // 16進数（0xプレフィックス）

// 数値の区切り（Java 7以降）
long largeNumber = 1_000_000_000L;  // 読みやすくするためのアンダースコア
```

##### 浮動小数点リテラル

```java
double normalNotation = 3.14159;
double scientificNotation = 3.14159e0;  // 科学記法
float floatValue = 3.14f;  // float型はfまたはFサフィックスが必要
```

##### 文字・文字列リテラル

```java
char singleChar = 'A';          // 文字リテラル（シングルクォート）
char unicodeChar = '\u0041';    // Unicode表現（'A'と同じ）
String text = "Hello World";    // 文字列リテラル（ダブルクォート）

// エスケープシーケンス
String escaped = "Line 1\nLine 2\t\"Quoted\"";
// \n: 改行、\t: タブ、\": ダブルクォート
```

##### 真偽値リテラル

```java
boolean isTrue = true;
boolean isFalse = false;
```

#### 型変換

##### 暗黙的な型変換（拡大変換）

小さい型から大きい型への変換は自動的に行われます：

```java
int intValue = 100;
long longValue = intValue;    // int → long（自動変換）
double doubleValue = intValue; // int → double（自動変換）
```

##### 明示的な型変換（キャスト・縮小変換）

大きい型から小さい型への変換は明示的なキャストが必要です：

```java
double doubleValue = 3.14159;
int intValue = (int) doubleValue;  // 3になる（小数部分は切り捨て）

long longValue = 1000L;
int intValue2 = (int) longValue;   // 明示的なキャストが必要
```

**注意**: 縮小変換では情報の損失が発生する可能性があります。

#### ラッパクラス

各プリミティブ型には対応するラッパクラスが存在します：

```java
// オートボクシング（Java 5以降）
Integer boxedInt = 100;  // int → Integer（自動変換）
Double boxedDouble = 3.14;  // double → Double（自動変換）

// アンボクシング
int primitiveInt = boxedInt;  // Integer → int（自動変換）
double primitiveDouble = boxedDouble;  // Double → double（自動変換）

// ラッパークラスの便利なメソッド
String numberStr = "123";
int parsedInt = Integer.parseInt(numberStr);  // 文字列から数値への変換
String binaryStr = Integer.toBinaryString(42); // "101010"
```

ラッパクラスは、プリミティブ型をオブジェクトとして扱う必要がある場合（コレクションフレームワークなど）に使用されます。



次のパート：[Part E - 演算子と式](chapter03e-operators.md)
