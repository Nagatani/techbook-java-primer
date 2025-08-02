<!-- 
校正チャンク情報
================
元ファイル: chapter03-oop-basics.md
チャンク: 11/15
行範囲: 1883 - 2077
作成日時: 2025-08-03 01:46:20

校正時の注意事項:
- 文章の流れは前後のチャンクを考慮してください
- このヘッダーとフッターは校正対象外です
- 校正が完了したらステータスを「completed」に変更してください
================
-->

        // 異常な操作（エラーが適切に処理される）
        product.updatePrice(-500.0);
        product.sell(20);
    }
}
```

#### 実行結果

```
価格を更新しました: 1800.0円
在庫を追加しました。現在の在庫: 15
販売しました。残り在庫: 12
エラー: 価格は0円以上である必要です
在庫が不足しています
```


## static修飾子の詳細な理解

第2章では、mainメソッドから直接呼び出せるstaticメソッドの基本を学習しました。ここでは、オブジェクト指向プログラミングの文脈におけるstatic修飾子の意味と使い方を、より体系的に理解します。

### staticとは何か

static修飾子は、フィールドやメソッドが「クラスに属する」ことを示します。通常のフィールドやメソッド（インスタンスメンバー）が各オブジェクトごとに存在するのに対し、staticメンバーはクラス全体で1つだけ存在します。

### staticメソッドの例

<span class="listing-number">**サンプルコード3-20**</span>

```java
public class MathHelper {
    // staticメソッド - オブジェクトを作らずに使える
    public static int add(int a, int b) {
        return a + b;
    }
    
    public static double calculateCircleArea(double radius) {
        return 3.14159 * radius * radius;
    }
}

// 使用例
public class StaticExample {
    public static void main(String[] args) {
        // newを使わずに直接呼び出し
        int sum = MathHelper.add(5, 3);
        System.out.println("合計: " + sum);
        
        double area = MathHelper.calculateCircleArea(10.0);
        System.out.println("円の面積: " + area);
    }
}
```

#### 実行結果

```
合計: 8
円の面積: 314.159
```

#### staticの特徴

- `クラス名.メソッド名()`で呼び出す
- オブジェクトを作る必要がない（インスタンス化して変数に入れる必要がない）
- ユーティリティメソッドに便利

> **ユーティリティメソッドとは**  
> ユーティリティメソッドは、特定のオブジェクトの状態に依存せず、引数のみに基づいて処理を行うメソッドです。数学計算、文字列処理、データ変換など、汎用的な機能を提供します。Javaの標準ライブラリでは、`Math.max()`、`Math.sqrt()`、`Integer.parseInt()`などがユーティリティメソッドの典型例です。これらのメソッドは、オブジェクトを作成せずに直接呼び出せるため、staticメソッドとして実装されています。

### staticフィールド（クラス共有変数）

<span class="listing-number">**サンプルコード3-21**</span>

```java
// StaticMemberExample.java
class Tool {
    // インスタンスメンバー（各Toolインスタンスが個別に持つ）
    String name;

    // クラスメンバー（Toolクラス全体で共有される）
    static int toolCount = 0;

    // コンストラクタ
    public Tool(String name) {
        this.name = name;
        toolCount++; // インスタンスが作られるたびに、共有カウンタを増やす
        System.out.println(this.name + " が作成されました。現在のツール総数: " + toolCount);
    }

    // インスタンスメソッド
    public void showName() {
        System.out.println("このツールの名前は " + this.name + " です。");
    }

    // クラスメソッド
    public static void showToolCount() {
        // System.out.println(this.name); // エラー！ staticメソッド内ではインスタンスメンバー(name)は使えない
        System.out.println("作成されたツールの総数は " + toolCount + " です。");
    }
}

public class StaticMemberExample {
    public static void main(String[] args) {
        System.out.println("--- ツール作成前 ---");
        // インスタンスがなくてもクラスメソッドは呼び出せる
        Tool.showToolCount();

        System.out.println("\n--- ツール作成 ---");
        Tool hammer = new Tool("ハンマー");
        Tool wrench = new Tool("レンチ");

        System.out.println("\n--- 各ツールの情報表示 ---");
        hammer.showName(); // インスタンスメソッドの呼び出し
        wrench.showName();

        System.out.println("\n--- ツール総数の再確認 ---");
        // クラス名経由でのアクセスが推奨
        Tool.showToolCount();
    }
}
```

#### 実行結果

```
--- ツール作成前 ---
作成されたツールの総数は 0 です

--- ツール作成 ---
ハンマー が作成されました。現在のツール総数: 1
レンチ が作成されました。現在のツール総数: 2

--- 各ツールの情報表示 ---
このツールの名前は ハンマー です。
このツールの名前は レンチ です。

--- ツール総数の再確認 ---
作成されたツールの総数は 2 です。
```

### staticとインスタンスメンバーの違い

#### インスタンスメンバー
- 各オブジェクトが独自に持つ
- `new`でオブジェクトを作成してから使用
- `this`参照が使える
- オブジェクトの状態を表現

#### staticメンバー
- クラス全体で1つだけ存在
- クラス名経由で直接アクセス
- `this`参照は使えない
- クラス全体の共通情報を表現

### staticを使う適切な場面

#### ユーティリティメソッド

<span class="listing-number">**サンプルコード3-37**</span>

   ```java
   public class MathUtils {
       public static double calculateDistance(double x1, double y1, double x2, double y2) {
           return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
       }
   }
   ```

   これはユーティリティメソッドの例です。

#### ファクトリーメソッド

<span class="listing-number">**サンプルコード3-38**</span>

   ```java
   public class Student {
       private String name;
       private int age;
       
       private Student(String name, int age) {
           this.name = name;
           this.age = age;
       }
       
       public static Student createStudent(String name, int age) {
           if (age < 0 || age > 150) {
               throw new IllegalArgumentException("Invalid age");
           }
           return new Student(name, age);
       }
   }
   ```

   これはファクトリーメソッドパターンの例です。



<!-- 
================
チャンク 11/15 の終了
校正ステータス: [ ] 未完了 / [ ] 完了
================
-->
