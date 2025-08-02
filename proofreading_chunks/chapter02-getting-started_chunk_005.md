<!-- 
校正チャンク情報
================
元ファイル: chapter02-getting-started.md
チャンク: 5/9
行範囲: 749 - 943
作成日時: 2025-08-02 22:43:26

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

#### 事前説明
条件分岐は、プログラムが状況に応じて異なる処理を選択するしくみです。成績判定、ユーザー入力の検証、システム状態の確認など、実用的なプログラムには不可欠な機能です。

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

実行結果：
```
評価: 良い
```

#### switch文の注意点

- 各caseの最後には`break`文を書く（忘れると次のcaseも実行される）
- `default`節で想定外の値に対応する
- 同じ処理をする複数の値は連続して記述できる

## 配列

#### 事前説明
配列は、同じ型の複数のデータを一括で管理するための基本的なデータ構造です。学生の成績管理、商品の在庫管理、数値計算など、プログラミングの多くの場面で配列が活用されます。Javaの配列は、C言語の配列の利便性を継承しつつ、境界チェックや自動初期化により安全性を向上させています。

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



<!-- 
================
チャンク 5/9 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
