# 第2章 Java基本文法 - Part A: Javaの基礎

## 始めに：Javaという言語の特徴

本章では、プログラミング言語Javaの基本文法を学習します。Javaは1995年にSun Microsystems（現Oracle）によって発表され、現在も世界中で広く使用されている主要なプログラミング言語の1つです。

### Javaの誕生と基本思想

Javaは「Write Once, Run Anywhere（一度書けば、どこでも動く）」という革新的な思想のもとに設計されました。これは、Java仮想マシン（JVM）という実行環境上でプログラムを動作させることで実現されています。C言語のように特定のハードウェアやOSに依存せず、JVMさえあればWindows、macOS、Linuxなど、どの環境でも同じプログラムが動作します。

### Java仮想マシン（JVM）の主要な特徴

JVMは以下の重要な機能を提供します：

- **プラットフォーム独立性**：バイトコードと呼ばれる中間言語に変換することで、OS依存性を排除
- **自動メモリ管理**：ガベージコレクションによりメモリの確保・解放を自動化し、メモリリークを防止
- **セキュリティ**：サンドボックス環境により、悪意のあるコードから保護
- **実行時最適化**：JITコンパイルにより、実行中にプログラムを最適化

### なぜJavaを学ぶのか

Javaは以下の分野で幅広く活用されています：

- **企業システム開発**：銀行、保険、政府機関などの大規模システム
- **Webアプリケーション**：Spring Bootなどのフレームワークを使った開発
- **Androidアプリケーション開発**：モバイルアプリケーションの開発
- **ビッグデータ処理**：Apache Hadoop、Apache Sparkなどの基盤技術

本章では、このような実践的な開発に必要なJavaの基本文法を、C言語との比較を交えながら体系的に学習していきます。

**より詳しい歴史や技術的背景について**：Javaの詳細な歴史、企業システムでの採用事例、オープンソース化の経緯などについては、付録B.01「言語設計とプラットフォーム」を参照してください。

## 2.1 Javaの特徴

Javaは以下のような特徴を持つプログラミング言語です：

- **プラットフォーム独立性**：「Write Once, Run Anywhere」
- **オブジェクト指向**：クラスベースのオブジェクト指向言語
- **ガベージコレクション**：自動メモリ管理
- **強い型付け**：コンパイル時の型チェック
- **豊富なライブラリ**：標準ライブラリとサードパーティライブラリ

## メモリ管理とガベージコレクション

Javaの最も重要な特徴の1つは、自動メモリ管理（ガベージコレクション）です。C言語ではプログラマがメモリの確保と解放を手動で行う必要がありましたが、Javaではこれを自動化することで、メモリリークやダングリングポインタなどの問題を防ぎます。

ガベージコレクションは世代別仮説にもとづいて効率的に動作し、現代のJVMではG1 GCやZGCなどの低遅延コレクタにより、大規模なアプリケーションでも実用的なパフォーマンスを実現しています。

**メモリ管理の詳細、GCアルゴリズム、パフォーマンスチューニングについては、付録B.02「メモリ管理とパフォーマンス」を参照してください。**

## 2.2 基本データ型

Javaには8つの基本データ型（プリミティブ型）があります：

```java
// 整数型
byte b = 127;           // 8ビット（-128〜127）
short s = 32767;        // 16ビット（-32,768〜32,767）
int i = 2147483647;     // 32ビット（約±21億）
long l = 9223372036854775807L;  // 64ビット（約±900京）

// 浮動小数点型
float f = 3.14f;        // 32ビット（約7桁の精度）
double d = 3.14159265359;  // 64ビット（約15桁の精度）

// その他
char c = 'A';           // 16ビット（Unicode文字）
boolean flag = true;    // true または false
```

**重要な違い（C言語との比較）**：
- `char`型は16ビット（Unicode対応）
- `boolean`型は`true`/`false`のみ（0/1ではない）
- 各型のサイズは環境によらず固定

## 2.3 変数の宣言と初期化

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

## 2.4 演算子

Javaの演算子はC言語とほぼ同じですが、いくつかの追加機能があります：

```java
// 算術演算子
int a = 10, b = 3;
int sum = a + b;      // 13
int diff = a - b;     // 7
int prod = a * b;     // 30
int quot = a / b;     // 3（整数除算）
int rem = a % b;      // 1（剰余）

// インクリメント・デクリメント
int x = 5;
int y = x++;  // y = 5, x = 6（後置）
int z = ++x;  // z = 7, x = 7（前置）

// 比較演算子
boolean result1 = (a > b);   // true
boolean result2 = (a == b);  // false
boolean result3 = (a != b);  // true

// 論理演算子
boolean p = true, q = false;
boolean and = p && q;        // false（短絡評価）
boolean or = p || q;         // true（短絡評価）
boolean not = !p;            // false

// ビット演算子
int m = 5;    // 0101
int n = 3;    // 0011
int bitAnd = m & n;  // 0001 = 1
int bitOr = m | n;   // 0111 = 7
int bitXor = m ^ n;  // 0110 = 6
```

## 2.5 型変換

Javaでは型の安全性を重視するため、明示的な型変換が必要な場合があります：

```java
// 暗黙的な型変換（拡大変換）
int i = 100;
long l = i;        // OK: int → long
double d = i;      // OK: int → double

// 明示的な型変換（縮小変換）
double pi = 3.14159;
int truncated = (int) pi;  // 3（小数部分は切り捨て）

// 文字列との変換
String str = "123";
int num = Integer.parseInt(str);  // 文字列→整数
String str2 = String.valueOf(num);  // 整数→文字列
```

## 2.6 コンソール入出力

基本的なコンソール入出力の方法：

```java
import java.util.Scanner;

public class IOExample {
    public static void main(String[] args) {
        // 出力
        System.out.println("Hello, World!");
        System.out.print("改行なし");
        System.out.printf("書式付き出力: %d, %.2f%n", 10, 3.14159);
        
        // 入力
        Scanner scanner = new Scanner(System.in);
        System.out.print("名前を入力: ");
        String name = scanner.nextLine();
        System.out.print("年齢を入力: ");
        int age = scanner.nextInt();
        
        System.out.printf("%sさん（%d歳）、こんにちは！%n", name, age);
        scanner.close();
    }
}
```

---

次のパート：[Part B - 制御構造と配列](chapter02b-control-and-arrays.md)