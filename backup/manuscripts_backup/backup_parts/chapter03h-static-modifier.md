## 第3章 オブジェクト指向の考え方 - Part H: static修飾子

### 3.5 static修飾子：インスタンス不要の共有メンバー

これまでに見てきたクラスのフィールドやメソッドは、すべて`new`キーワードによってオブジェクト（インスタンス）を生成してからでないと利用できませんでした。これらを「**インスタンスメンバー**」と呼びます。

しかし、Javaにはインスタンスを生成しなくても利用できる特別なメンバーが存在します。それが`static`修飾子を付けた「**クラスメンバー**」または「**静的メンバー**」です。

#### インスタンスメンバー vs クラスメンバー

この2つの違いを理解することは、オブジェクト指向プログラミングにおいて非常に重要です。

*   **インスタンスメンバー（`static`なし）**
    *   **所有者**: 個々のインスタンス
    *   **メモリ**: インスタンスが生成されるたびに、そのインスタンス専用の領域が確保される。
    *   **アクセス**: `インスタンス変数名.メンバー名`
    *   **比喩**:「**住宅の各戸が持つ家具**」。Aさんの家のテーブルとBさんの家のテーブルは別物です。

*   **クラスメンバー（`static`あり）**
    *   **所有者**: クラスそのもの
    *   **メモリ**: クラスがロードされる時に一度だけ、クラス共有の領域が確保される。
    *   **アクセス**: `クラス名.メンバー名`
    *   **比喩**:「**マンションの共有掲示板**」。どの部屋の住人が見ても、掲示板は1つであり、そこに書かれた情報は全住人で共有されます。

#### メモリ上のイメージ

```
【メモリ空間】
+-----------------------------------------------------------------+
| **クラス領域（共有）**                                          |
| +-------------------------------------------------------------+ |
| | **Toolクラス**                                              | |
| |   `static String sharedInfo = "共有情報";`  <-- 1つだけ存在 | |
| |   `static void showSharedInfo()`                           | |
| +-------------------------------------------------------------+ |
+-----------------------------------------------------------------+
| **ヒープ領域（インスタンスごと）**                              |
| +---------------------------+ +---------------------------+     |
| | **tool1インスタンス**     | | **tool2インスタンス**     |     |
| | `String instanceName;`    | | `String instanceName;`    |     |
| | `void showInstanceName()` | | `void showInstanceName()` |     |
| +---------------------------+ +---------------------------+     |
+-----------------------------------------------------------------+
```

#### `static`メンバーの実践例

`static`フィールドと`static`メソッドの具体的な使い方を見てみましょう。

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

**実行結果:**
```
--- ツール作成前 ---
作成されたツールの総数は 0 です。

--- ツール作成 ---
ハンマー が作成されました。現在のツール総数: 1
レンチ が作成されました。現在のツール総数: 2

--- 各ツールの情報表示 ---
このツールの名前は ハンマー です。
このツールの名前は レンチ です。

--- ツール総数の再確認 ---
作成されたツールの総数は 2 です。
```

#### `static`の重要な制約

`static`メソッド内では、以下の制約があります：

1. **インスタンスメンバーにアクセスできない**: `this`キーワードを使うことができず、インスタンスフィールドやインスタンスメソッドを直接呼び出せません。
2. **staticメンバーのみアクセス可能**: staticメソッドやstaticフィールドのみ使用できます。

```java
class Example {
    String instanceField = "インスタンス";
    static String staticField = "スタティック";

    void instanceMethod() {
        System.out.println(instanceField);  // OK
        System.out.println(staticField);    // OK
    }

    static void staticMethod() {
        // System.out.println(instanceField);  // エラー！
        System.out.println(staticField);        // OK
        // instanceMethod();                    // エラー！
    }
}
```

#### `static`の実用的な使用例

##### 1. ユーティリティクラス

数学関数や文字列処理など、状態を持たない汎用的な処理を提供するクラス：

```java
public class MathUtils {
    // privateコンストラクタでインスタンス化を防ぐ
    private MathUtils() {}

    public static double calculateCircleArea(double radius) {
        return Math.PI * radius * radius;
    }

    public static int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }
}

// 使用例
double area = MathUtils.calculateCircleArea(5.0);
```

##### 2. 定数の定義

プログラム全体で共有される定数値：

```java
public class Constants {
    public static final double TAX_RATE = 0.10;  // 消費税率
    public static final int MAX_RETRY_COUNT = 3;  // 最大リトライ回数
    public static final String DEFAULT_ENCODING = "UTF-8";
}
```

##### 3. Singletonパターン

アプリケーション全体で1つしか存在しないインスタンスを保証する設計パターン：

```java
public class DatabaseConnection {
    private static DatabaseConnection instance;

    private DatabaseConnection() {
        // プライベートコンストラクタ
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}
```

#### `static`使用時のベストプラクティス

1. **過度な使用を避ける**: `static`を多用するとオブジェクト指向の利点が失われます
2. **状態を持たない処理に使用**: ユーティリティメソッドなど、インスタンスの状態に依存しない処理に適しています
3. **定数にはfinalと併用**: 変更不可能な定数には`static final`を使用します
4. **スレッドセーフティに注意**: staticフィールドは全スレッドで共有されるため、同期処理が必要な場合があります

#### まとめ

`static`修飾子は、クラスレベルでデータや機能を共有する強力な機能です。しかし、オブジェクト指向プログラミングの本質は「データと振る舞いをカプセル化したオブジェクトの相互作用」にあるため、`static`の使用は慎重に行うべきです。適切に使用することで、より効率的で保守性の高いプログラムを作成できます。



次のパート：[Part I - 配列](chapter03i-arrays.md)
