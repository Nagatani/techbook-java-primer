<!-- 
校正チャンク情報
================
元ファイル: chapter02-getting-started.md
チャンク: 4/9
行範囲: 561 - 748
作成日時: 2025-08-02 22:43:26

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

### Javaの入出力設計の特徴

1. 標準入出力ストリーム
- `System.out`
-    + 標準出力（画面への出力）
- `System.in`
-    + 標準入力（キーボードからの入力）
- `System.err`
-    + 標準エラー出力

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

#### 実行結果
名前に「田中太郎」、年齢に「abc」（エラー）→「25」を入力した場合の実行例を示します。
```
Hello, World!
改行なし出力
整数: 10, 浮動小数点: 3.14
文字列: 左寄せ        | 右寄せ:        右寄せ
名前を入力してください: 田中太郎
年齢を入力してください: abc
エラー: 整数を入力してください
年齢を入力してください: 25
田中太郎さん（25歳）、こんにちは！
```

### Scanner クラスの高度な使い方

#### 1. 型別の入力メソッド
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



<!-- 
================
チャンク 4/9 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
