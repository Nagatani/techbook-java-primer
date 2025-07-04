# 第6章 final修飾子：不変性の保証

## 🎯総合演習プロジェクトへのステップ

本章で学ぶ`final`修飾子は、**総合演習プロジェクト「ToDoリストアプリケーション」** のプログラムをより安全で、意図が明確なものにするために役立ちます。

- **定数の定義**: アプリケーション全体で使う固定値、たとえば「デフォルトのタスク優先度」や「設定ファイル名」などを`public static final`な定数として定義することで、マジックナンバー（コード中に直接書かれた意味不明な数値）をなくし、設定の変更も一ヵ所で済むようになります。
- **不変なデータオブジェクト**: `Task`クラスの`creationDate`（作成日時）フィールドを`final`にすることで、一度設定されたら二度と変更できないように保証できます。これにより、データが意図せず書き換えられることを防ぎ、プログラムの信頼性を高めます。
- **設計意図の明確化**: あるクラスを「これ以上拡張（継承）してほしくない」という設計意図がある場合、クラスに`final`を付けることで、その意図をコード上で明確に表現し、強制できます。

本章を通じて、`final`修飾子を使ってプログラムの「変わらない部分」を明確に定義し、安全で堅牢なコードを記述するための技術を学びます。

## 📋 本章の学習目標

### 前提知識
- **第5章の継承とオーバーライドの理解**：`extends`と`@Override`の基本的な使い方
- **変数のスコープとライフサイクルの理解**：ローカル変数、インスタンス変数、クラス変数の違い

### 到達目標

#### 知識理解目標
- `final`修飾子の3つの主要な用途（変数、メソッド、クラス）の理解
- `final`変数（定数）の役割とメリット
- `final`メソッドがオーバーライドを禁止する意味
- `final`クラスが継承を禁止する意味
- イミュータブル（不変）オブジェクトの概念と、その利点の理解

#### 技能習得目標
- `final`を使って再代入不可能なローカル変数やフィールドを定義する
- `static final`を使ってクラス定数を定義し、利用する
- メソッドに`final`を付けてオーバーライドを禁止する
- クラスに`final`を付けて継承を禁止する
- `final`フィールドのみを持つ簡単なイミュータブルクラスを設計する

#### 到達レベルの指標

| 到達レベル |
| :--- |
| プログラム中の定数を`static final`で適切に定義できる |
| 設計意図に応じて、メソッドやクラスに`final`を適用できる |
| イミュータブルなクラスを設計するメリットを説明でき、簡単な実装ができる |
| `final`を効果的に使い、コードの安全性と可読性を向上させることができる |

---

## 6.1 `final`修飾子とは

Javaにおける`final`修飾子は、「**これで最後**」「**これ以上変更できない**」という意味を付与するためのキーワードです。変数、メソッド、クラスのいずれかに付けることができ、それぞれに対して異なる「変更不可」の制約を与えます。

`final`を適切に使うことで、プログラムの安全性を高め、設計者の意図をコード上で明確に表現できます。

## 6.2 `final`変数：再代入を許さない

変数（フィールド、ローカル変数、引数）に`final`を付けると、その変数への**値の代入は一度しか行えなくなります**。一度初期化されると、その後は二度と値を変更（再代入）できません。

```java
public class FinalVariableExample {
    // finalなインスタンスフィールド（コンストラクタで一度だけ初期化）
    private final String name;

    // finalなクラスフィールド（定数）
    public static final double PI = 3.14159;

    public FinalVariableExample(String name) {
        this.name = name; // OK: コンストラクタで初期化
    }

    public void showExample() {
        // this.name = "Taro"; // コンパイルエラー！ final変数には再代入できない

        // finalなローカル変数
        final int fixedValue = 100;
        // fixedValue = 200; // コンパイルエラー！ final変数には再代入できない

        System.out.println("名前: " + this.name);
        System.out.println("固定値: " + fixedValue);
        System.out.println("円周率: " + PI);
    }
}
```

### 定数としての利用：`static final`

特に`public static final`を付けたフィールドは、**定数**を表現するためによく使われます。

-   **`public`**: どこからでもアクセスできる。
-   **`static`**: クラスに属し、インスタンスを生成しなくても利用できる。
-   **`final`**: 値が変更されない。

```java
public class AppConstants {
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final String DEFAULT_GREETING = "Hello, World!";
}

// 別のクラスから利用する
System.out.println("最大試行回数: " + AppConstants.MAX_LOGIN_ATTEMPTS);
```
このように定数を使うことで、コード中に直接数値を書く（マジックナンバー）のを避け、後から仕様変更があった場合も`AppConstants`クラスの定義を修正するだけで対応できます。

## 6.3 `final`メソッド：オーバーライドを許さない

メソッドに`final`を付けると、そのメソッドを**子クラスでオーバーライド（再定義）することが禁止されます**。

これは、クラスの設計者が「このメソッドの振る舞いは、どのサブクラスでも変えてはならない」と意図した場合に使います。フレームワークやライブラリなどで、アルゴリズムの重要な核となる部分が変わってしまうことを防ぐ目的で利用されることがあります。

```java
public class SuperClass {
    public void normalMethod() {
        System.out.println("このメソッドはオーバーライド可能です。");
    }

    public final void finalMethod() {
        System.out.println("このメソッドの動作は変更できません。");
    }
}

public class SubClass extends SuperClass {
    @Override
    public void normalMethod() {
        System.out.println("オーバーライドしました。");
    }

    // @Override
    // public void finalMethod() { // コンパイルエラー！ finalメソッドはオーバーライドできない
    //     // ...
    // }
}
```

## 6.4 `final`クラス：継承を許さない

クラス宣言に`final`を付けると、そのクラスを**継承して子クラス（サブクラス）を作ることが禁止されます**。

これは、そのクラスが「完成形」であり、これ以上拡張・変更されることを意図していない場合に利用します。セキュリティ上の理由で、あるいは設計として完全に固定したい場合に適しています。

Javaの標準ライブラリでは、`String`クラスや`Integer`などのラッパクラスが`final`として宣言されています。

```java
public final class ImmutableData {
    private final String data;

    public ImmutableData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}

// public class ExtendedData extends ImmutableData { // コンパイルエラー！ finalクラスは継承できない
//     // ...
// }
```

## 6.5 イミュータブル（不変）なオブジェクト

`final`修飾子の概念と密接に関連するのが、**イミュータブル (Immuテーブル) なオブジェクト**という考え方です。

イミュータブルなオブジェクトとは、**一度生成されると、その内部状態（フィールドの値）がけっして変わらないオブジェクト**のことを指します。

Javaの`String`クラスは、イミュータブルなクラスの代表例です。`String`オブジェクトのメソッド（例： `toUpperCase()`, `substring()`）は、元の文字列を変更するのではなく、常に**新しい**文字列オブジェクトを生成して返します。

### イミュータブルにするメリット

-   **安全性**: オブジェクトの状態が変わらないため、複数の場所から参照されても、意図しない書き換えが起こる心配がありません。特にマルチスレッドプログラミングにおいて非常に重要です。
-   **予測可能性**: オブジェクトの状態が常に同じであることが保証されているため、プログラムの動作が予測しやすくなり、デバッグが容易になります。
-   **キャッシュとしての利用**: 状態が変わらないため、計算結果などを安心してキャッシュ（一時保存）できます。

### 簡単なイミュータブルクラスの作り方

1.  **クラスを`final`にする**: 継承によって不変性が壊されるのを防ぐ。
2.  **すべてのフィールドを`private final`にする**: 直接の書き込みを防ぎ、再代入を禁止する。
3.  **フィールドを設定するsetterメソッドを提供しない**。
4.  **フィールドの初期化はコンストラクタでのみ行う**。
5.  **フィールドが可変オブジェクト（配列や`ArrayList`など）を参照している場合は、getterメソッドでそのオブジェクトのコピーを返す**（防御的コピー）。

#### 実践例：イミュータブルな`Point`クラス

```java
public final class Point { // 1. クラスをfinalに
    private final int x; // 2. フィールドをprivate finalに
    private final int y;

    // 4. コンストラクタでのみ初期化
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // 3. setterは提供しない
    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    // 状態を変更するメソッドは、新しいインスタンスを返す
    public Point move(int dx, int dy) {
        return new Point(this.x + dx, this.y + dy);
    }
}

public class TestImmutable {
    public static void main(String[] args) {
        Point p1 = new Point(10, 20);
        System.out.println("p1: (" + p1.getX() + ", " + p1.getY() + ")");

        // p1.x = 30; // コンパイルエラー！ finalフィールド

        Point p2 = p1.move(5, 5); // p1は変わらず、新しいPointオブジェクトp2が作られる

        System.out.println("p1: (" + p1.getX() + ", " + p1.getY() + ")"); // p1は(10, 20)のまま
        System.out.println("p2: (" + p2.getX() + ", " + p2.getY() + ")"); // p2は(15, 25)
    }
}
```

## 6.6 章末演習

### 演習：アプリケーション設定クラスの作成

**目的:** `final`修飾子を適切に使い、安全で変更に強い設定管理クラスを作成する。

**手順:**

1.  **`AppConfig`クラスの作成**:
    *   `AppConfig.java`というファイル名で、**継承不可能な(`final`)** クラスを作成します。

2.  **定数の定義**:
    *   以下の仕様で、アプリケーション全体で利用する設定値を**定数 (`public static final`)** として定義します。
        *   `MAX_CONNECTIONS`: `int`型、値は`10`
        *   `DEFAULT_USERNAME`: `String`型、値は`"guest"`
        *   `TIMEOUT_SECONDS`: `long`型、値は`30L`

3.  **インスタンス変数の定義**:
    *   以下の仕様で、インスタンスごとに設定される値を保持す���フィールドを定義します。これらは一度設定されたら変更できないようにします。
        *   `serverUrl`: `String`型、**再代入不可 (`final`)**
        *   `port`: `int`型、**再代入不可 (`final`)**

4.  **コンストラクタの作成**:
    *   `serverUrl`と`port`を引数に取り、対応するフィールドを初期化するコンストラクタを作成します。

5.  **getterメソッドの作成**:
    *   `serverUrl`と`port`の値を外部から取得するためのgetterメソッド（`getServerUrl()`, `getPort()`）を作成します。setterメソッドは作成しません。

6.  **`ConfigLoader`実行クラスの作成**:
    *   `main`メソッドを持つ`ConfigLoader.java`を作成します。
    *   `AppConfig`クラスの定数を直接参照して、その値を表示します。
    *   `AppConfig`クラスのインスタンスを生成し、そのインスタンスが持つ`serverUrl`と`port`の値をgetter経由で表示します。
    *   生成したインスタンスのフィールドに再代入しようとすると、コンパイルエラーになることを（コメントアウトなどで）確認してください。
