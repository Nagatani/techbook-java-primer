# 第1章 Javaの基本文法 - C言語との比較

この章では、C言語の知識を活かしながらJavaの基本文法を学びます。

## 1.1 Hello Worldプログラム

### C言語版
```c
#include <stdio.h>

int main() {
    printf("Hello, World!\n");
    return 0;
}
```

### Java版
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

### 主な違い

- **クラスベース**：Javaはすべてクラス内に記述
- **main関数**：`public static void main(String[] args)`の形式
- **出力**：`printf`の代わりに`System.out.println`
- **ファイル名**：クラス名と同じファイル名が必要

## 1.2 データ型

### 基本データ型の比較

| C言語 | Java | 説明 |
|-------|------|------|
| int | int | 32ビット整数 |
| long | long | 64ビット整数 |
| float | float | 32ビット浮動小数点数 |
| double | double | 64ビット浮動小数点数 |
| char | char | 16ビットUnicode文字 |
| - | boolean | 真偽値（true/false） |

### 文字列の扱い

```java
// C言語風（推奨されない）
char[] chars = {'H', 'e', 'l', 'l', 'o'};

// Java推奨
String message = "Hello, World!";
String name = "Java";
String greeting = message + " " + name;  // 文字列連結
```

## 1.3 変数と定数

```java
// 変数宣言
int number = 10;
double price = 99.99;
boolean isActive = true;

// 定数（final修飾子）
final int MAX_VALUE = 100;
final String COMPANY_NAME = "TechBook";
```

## 1.4 演算子

Javaの演算子はC言語とほぼ同じですが、いくつかの違いがあります：

```java
// 算術演算子（C言語と同じ）
int a = 10, b = 3;
int sum = a + b;        // 13
int diff = a - b;       // 7
int product = a * b;    // 30
int quotient = a / b;   // 3
int remainder = a % b;  // 1

// 文字列連結（Javaの特徴）
String result = "合計: " + sum;

// 比較演算子
boolean isEqual = (a == b);     // false
boolean isNotEqual = (a != b);  // true

// 論理演算子
boolean result1 = (a > 5) && (b < 10);  // true
boolean result2 = (a < 5) || (b > 10);  // false
```

## 1.5 制御構造

### 条件分岐

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

### 繰り返し処理

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

## 1.6 配列

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

## 1.7 メソッド（関数）

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

## 1.8 練習問題

1. 1から100までの和を計算するプログラムを作成してください。
2. 配列内の最大値を見つけるメソッドを作成してください。
3. 九九の表を表示するプログラムを作成してください。

## まとめ

この章では、C言語との比較を通じてJavaの基本文法を学習しました。次章では、Javaの最大の特徴であるオブジェクト指向プログラミングについて学習します。