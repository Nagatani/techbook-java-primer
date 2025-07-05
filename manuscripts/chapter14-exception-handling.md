# 第14章 例外処理

## 📋 本章の学習目標

### 前提知識
**必須前提**：
- 第5章までの基本的なJavaプログラミング能力
- オブジェクト指向設計の実践経験
- 複数クラス・パッケージでの開発経験

**実務的前提**：
- プログラム実行時のエラー経験
- システムの信頼性に対する関心

### 学習目標
**知識理解目標**：
- 例外処理の設計思想と利点
- チェック例外と非チェック例外の違いと使い分け
- 例外の階層構造の理解
- try-with-resourcesの意義とリソース管理

**技能習得目標**：
- try-catch-finally構文の適切な使用
- カスタム例外クラスの設計と実装
- 例外の適切な伝播と処理
- リソース管理を含む堅牢なコード作成

**システム設計能力目標**：
- 障害に強いシステムの設計思考
- エラーハンドリング戦略の策定
- ログ記録とデバッグ手法の実践

**到達レベルの指標**：
- 例外処理を適切に含む信頼性の高いプログラムが作成できる
- カスタム例外を使った独自のエラー処理体系が設計できる
- ファイルやネットワークリソースを安全に扱うプログラムが実装できる
- 例外ログの分析とデバッグができる

---

## 📝 章末演習

本章で学んだ例外処理の概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- 例外処理の基本概念（例外の種類、try-catch-finally）
- 検査例外と非検査例外の理解と使い分け
- カスタム例外クラスの設計と実装
- try-with-resources によるリソース管理
- 例外処理のベストプラクティス
- 例外チェーン（Exception Chaining）による根本原因の追跡技法

### 📁 課題の場所
演習課題は `exercises/chapter14/` ディレクトリに用意されています：

```
exercises/chapter14/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── BasicExceptionHandling.java
│   ├── BankAccountException.java
│   ├── FileProcessor.java
│   └── ValidationResult.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 🚀 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. TODOコメントを参考に実装
4. 例外の階層構造を意識して、適切な例外タイプを選択する
5. try-with-resourcesでリソース管理を確実に行う

基本課題が完了したら、`advanced/`の発展課題でより高度な例外処理設計に挑戦してみましょう！

## 14.1 エラーの種類と対処法

プログラムを作成する上で、予期しない状況やエラーへの対応は避けて通れません。エラーには主に以下の3つの種類があります。

1.  **構文エラー (Syntax Error)**: コードの文法的な誤り。コンパイル時に発見され、修正しないとプログラムを実行できません。
2.  **実行時エラー (Runtime Error)**: プログラムの実行中に発生する異常事態。本章で学ぶ「例外 (Exception)」もこの一種です。
3.  **論理エラー (Logic Error)**: プログラムはエラーなく動作するが、実行結果が意図通りにならない不具合。デバッグによって原因を特定し、ロジックを修正する必要があります。

本章では、特に「実行時エラー」に焦点を当て、Javaの例外処理のしくみを学びます。

## 14.2 例外処理の基本: `try-catch-finally`

Javaでは、プログラム���行中に発生する「例外的状況」を**例外 (Exception)** オブジェクトとして表現し、これに対処するための専用の構文を提供しています。例外が発生することを「例外がスローされる（投げられる）」と言います。

### `try-catch`ブロック

-   **`try`ブロック**: 例外が発生する可能性のある処理をこのブロックで囲みます。
-   **`catch`ブロック**: `try`ブロック内で特定の型の例外が発生した場合に、その例外を捕捉（キャッチ）し、対応する処理を記述します。`catch`ブロックは複数記述でき、より具体的な例外から順に書きます。

```java
public class TryCatchSample {
    public static void main(String[] args) {
        try {
            String str = null;
            System.out.println(str.length()); // ここでNullPointerExceptionが発生
            System.out.println("この行は実行されません。");
        } catch (NullPointerException e) {
            System.err.println("NullPointerExceptionをキャッチしました！");
            e.printStackTrace(); // 開発中はスタックトレースを出力するとデバッグに役立つ
        } catch (Exception e) {
            System.err.println("その他の予期せぬ例外をキャッチしました。");
        }
        System.out.println("プログラムは処理を継続しています。");
    }
}
```

### `finally`ブロック

`finally`ブロックは、`try`ブロック内で例外が発生したかどうかにかかわらず、**必ず実行される**処理を記述します。主に、ファイルやネットワーク接続などのリソースを解放する後片付け処理に使われます。

```java
// ...
try {
    // リソースを使う処理
} catch (Exception e) {
    // 例外発生時の処理
} finally {
    // 例外の有無にかかわらず、必ず実行される後片付け処理
    System.out.println("finallyブロックが実行されました。");
}
```

### `try-with-resources`文 (推奨)

`AutoCloseable`インターフェイスを実装したリソース（ファイルストリームなど）を扱う場合、`try-with-resources`文を使うと、`finally`ブロックで明示的に`close()`を呼ぶ必要がなくなり、コードが簡潔かつ安全になります。

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TryWithResourcesSample {
    public static void readFile(String path) {
        // try()の括弧内でリソースを初期化する
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            System.out.println(br.readLine());
        } catch (IOException e) {
            System.err.println("ファイル読み込みエラー: " + e.getMessage());
        }
        // tryブロックを抜ける際にbrが自動的にクローズされる
    }
}
```

## 14.3 検査例外と非検査例外

Javaの例外は、コンパイラがその処理を強制するかどうかによって2種類に大別されます。

### 検査例外 (Checked Exception)

-   `IOException`や`SQLException`など、`Exception`のサブクラス（`RuntimeException`を除く）。
-   コンパイラが、これらの例外に対する処理（`try-catch`または`throws`）を**強制**します。記述がないとコンパイルエラーになります。
-   主に、呼び出し側が対処することで回復できる可能性がある、ファイル操作やネットワーク関連のエラーで使われます。

#### `throws`による例外処理の委譲

メソッド内で発生した検査例外を自身で処理せず、そのメソッドの呼び出し元に処理を任せたい場合、メソッドシグネチャに`throws`を記述します。

```java
import java.io.IOException;

public class ThrowsExample {
    // このメソッドはIOExceptionを投げる可能性があることを宣言
    public static void riskyMethod() throws IOException {
        throw new IOException("I/Oエラーが発生しました");
    }

    public static void main(String[] args) {
        try {
            riskyMethod();
        } catch (IOException e) {
            System.err.println("mainメソッドで例外をキャッチ: " + e.getMessage());
        }
    }
}
```

### 非検査例外 (Unchecked Exception)

-   `RuntimeException`とそのサブクラス（`NullPointerException`, `ArrayIndexOutOfBoundsException`など）、および`Error`とそのサブクラス。
-   コンパイラは例外処理を強制しません。
-   主に、プログラムの論理的な誤り（バグ）が原因で発生するため、原則として`catch`するのではなく、コードを修正して発生自体を防ぐべきです。

## 14.4 独自例外と例外の連鎖

### 独自例外の作成とスロー

アプリケーション固有のエラー状況を表現するために、独自の例外クラスを作成できます。`Exception`または`RuntimeException`を継承して定義します。`throw`キーワードを使うと、任意の場所で意図的に例外を発生させることができます。

```java
// 独自の検査例外クラス
class BalanceInsufficientException extends Exception {
    public BalanceInsufficientException(String message) {
        super(message);
    }
}

// 使用例
public class BankAccount {
    private int balance;
    public void withdraw(int amount) throws BalanceInsufficientException {
        if (amount > balance) {
            // 意図的に例外をスローする
            throw new BalanceInsufficientException("残高が不足しています。");
        }
        this.balance -= amount;
    }
}
```

### 例外の連鎖 (Exception Chaining)

ある例外が別の例外を引き起こした際に、元の例外（原因）の情報を失わずに、新しい例外でラップしてスローするテクニックです。これにより、デバッグ時に根本原因を追跡しやすくなります。

```java
class DataAccessException extends Exception {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause); // 原因となった例外(cause)を渡す
    }
}

public class UserService {
    public void findUser(int userId) throws DataAccessException {
        try {
            // ... データベースアクセス処理 ...
            // SQLExceptionが発生したと仮定
            throw new java.sql.SQLException("DB接続エラー");
        } catch (java.sql.SQLException e) {
            // SQLExceptionをラップして、より抽象的なDataAccessExceptionをスロー
            throw new DataAccessException("ユーザー情報の取得に失敗しました", e);
        }
    }
}
```

## 14.5 例外処理のベストプラクティス

-   **例外を無視しない**: `catch`ブロックを空にしないでください。少なくともログには記録しましょう。
-   **具体的な例外をキャッチする**: `catch (Exception e)`で安易にすべての例外を捕捉するのではなく、対処可能な具体的な例外を`catch`します。
-   **リソースは必ず解放する**: `try-with-resources`を積極的に利用しましょう。
-   **ユーザーに見せる情報とログに残す情報を分ける**: ユーザーには分かりやすいメッセージとエラーIDを、ログには原因究明のための詳細な情報（スタックトレースなど）を記録します。

適切な例外処理を実装することは、信頼性の高いソフトウェアを開発するための必須スキルです。
