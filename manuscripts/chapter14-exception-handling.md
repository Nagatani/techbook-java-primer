# 第14章 例外処理

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの重要な前提知識が必要です。まず必須の前提として、第5章までに学んだ基本的なJavaプログラミング能力を十分に身につけていることが求められます。これには、クラス、メソッド、継承、ポリモーフィズムなどの核心概念の理解が含まれます。また、オブジェクト指向設計の実践経験も重要で、実際に複数のクラスを連携させたプログラムを作成した経験があることが期待されます。複数クラス・パッケージでの開発経験も必要で、大規模なプログラムでのエラー管理の重要性を理解する基礎となります。

さらに、実務的な観点からの前提として、プログラム実行時に発生するさまざまなエラーを経験していることが推奨されます。プログラムが意図通りに動作しない場合や、予期せぬ入力や環境によってエラーが発生する経験は、例外処理の必要性を理解する上で極めて有益です。システムの信頼性に対する関心も重要で、エラーが発生したときにシステムがどのように振る舞うべきか、どのようにしてユーザーに適切なフィードバックを提供するかを考えた経験があると、例外処理の設計思想をより深く理解できるでしょう。

### 学習目標

本章では、Javaプログラミングにおける例外処理の包括的な知識と技術を習得します。知識理解の面では、まず例外処理の設計思想とその利点を深く理解します。例外処理は単にエラーを捕捉するだけではなく、プログラムの健全性と信頼性を高めるための重要なメカニズムです。チェック例外と非チェック例外の違いと適切な使い分けを学び、例外の階層構造を理解することで、状況に応じた最適な例外処理戦略を選択できます。try-with-resourcesの意義とリソース管理の重要性も学び、メモリリークやリソースの枚渇を防ぐ方法を理解します。

技能習得の面では、try-catch-finally構文の適切な使用方法をマスターします。各ブロックの役割と実行順序を理解し、複数の例外を適切にハンドリングする技術を学びます。カスタム例外クラスの設計と実装も重要なスキルで、ビジネスロジックに合わせた独自の例外体系を構築する方法を習得します。例外の適切な伝播と処理、そしてリソース管理を含む堅牢なコード作成技術も身につけます。

システム設計能力の観点からは、障害に強いシステムを設計するための思考法を学びます。これには、フェイルセーフな設計、障害の分離、グレースフルデグラデーションなどの概念が含まれます。エラーハンドリング戦略の策定を通じて、システム全体の信頼性を高める方法を学びます。ログ記録とデバッグ手法の実践も重要で、問題の追跡と解決を効率的に行うための技術を習得します。

最終的な到達レベルとしては、例外処理を適切に組み込んだ信頼性の高いプログラムを作成できます。カスタム例外を活用した独自のエラー処理体系を設計し、ファイルやネットワークリソースを安全に扱うプログラムを実装できます。そして、例外ログの分析とデバッグができる能力を身につけ、実務で必要となる問題解決スキルを習得することが、本章の最終目標です。



## 章末演習

本章で学んだ例外処理を実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第14章の課題構成

```
exercises/chapter14/
├── basic/              # 基礎課題（必須）
│   ├── README.md       # 詳細な課題説明
│   └── BasicException.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- try-catch-finallyによる基本的な例外処理
- カスタム例外クラスの設計と活用
- try-with-resourcesによるリソース管理

### 課題の概要

1. **基礎課題**: 基本的な例外処理とfinallyブロックの活用
2. **発展課題**: カスタム例外クラスの設計と階層化
3. **チャレンジ課題**: 高度な例外処理パターンとリソース管理

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第15章「ファイル入出力」に進みましょう。


演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter14/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── BasicExceptionHandling.java
│   ├── BankAccountException.java
│   └── FileProcessor.java
├── advanced/       # 応用・発展レベル課題
│   └── README.md
├── challenge/      # 挑戦レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```



## 完了確認チェックリスト

### 基礎レベル
- [ ] 基本的な例外処理構文が正しく使えている
- [ ] ビジネスロジックに応じたカスタム例外が設計できている
- [ ] リソース管理と例外処理が適切に実装できている
- [ ] 例外処理の設計パターンが理解できている

### 技術要素
- [ ] 検査例外と非検査例外の使い分けができている
- [ ] try-with-resourcesでリソース管理ができている
- [ ] 例外チェインによる根本原因の追跡ができている
- [ ] パフォーマンスを考慮した例外処理ができている

### 応用・発展レベル
- [ ] 高度な型システム設計ができている
- [ ] メタプログラミング技術を活用できている
- [ ] 理論的・哲学的概念をコードで表現できている
- [ ] 革新的なアプローチを考案できている

## 14.1 エラーの種類と対処法

### ソフトウェアの信頼性とエラー処理

現代のソフトウェア開発では、システムの信頼性が極めて重要です。銀行システム、医療機器、自動運転など、プログラムの不具合が重大な結果を招く分野が増えています。このような背景から、エラーの適切な管理と処理は、単なる技術的なスキルを超えて、ソフトウェアエンジニアの基本的な責任となっています。

プログラムを作成する上で、予期しない状況やエラーへの対応は避けて通れません。エラーには主に以下の3つの種類があります。

1.  **構文エラー (Syntax Error)**: コードの文法的な誤り。コンパイル時に発見され、修正しないとプログラムを実行できません。
2.  **実行時エラー (Runtime Error)**: プログラムの実行中に発生する異常事態。本章で学ぶ「例外 (Exception)」もこの一種です。
3.  **論理エラー (Logic Error)**: プログラムはエラーなく動作するが、実行結果が意図通りにならない不具合。デバッグによって原因を特定し、ロジックを修正する必要があります。

本章では、特に「実行時エラー」に焦点を当て、Javaの例外処理のしくみを学びます。

## 14.2 例外処理の基本: `try-catch-finally`

### 例外処理メカニズムの設計思想

Javaの例外処理メカニズムは、プログラムの通常の処理フローとエラー処理を明確に分離することを目的として設計されました。伝統的なエラー処理（戻り値でエラーコードを返す方法）では、エラーチェックがビジネスロジックと混在し、コードの可読性が低下します。

例外処理の基本的な流れは以下のようになります：

1. メソッド内で例外が発生すると、通常の処理フローが中断される
2. プログラムは呼び出しスタックをさかのぼり、適切な例外ハンドラを探す
3. 適切な`catch`ブロックが見つかると、そこで例外が処理される
4. `finally`ブロックがあれば、必ず実行される

Javaでは、プログラム実行中に発生する「例外的状況」を**例外 (Exception)** オブジェクトとして表現し、これに対処するための専用の構文を提供しています。例外が発生することを「例外がスローされる（投げられる）」と言います。

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

### 例外クラスの階層構造

Javaの例外クラスは、`Throwable`クラスをルートとした階層構造を持っています：

```
Throwable
├── Error（システムエラー：通常は回復不可能）
│   ├── OutOfMemoryError
│   ├── StackOverflowError
│   └── ...
└── Exception
    ├── RuntimeException（非検査例外）
    │   ├── NullPointerException
    │   ├── ArrayIndexOutOfBoundsException
    │   └── ...
    └── その他のException（検査例外）
        ├── IOException
        ├── SQLException
        └── ...
```

この階層構造の設計には、「プログラマが対処できる例外」と「システムレベルの重大なエラー」を区別するという明確な意図があります。

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

## より深い理解のために

本章で学んだ例外処理をさらに深く理解したい方は、付録B.21「例外処理のパフォーマンスコストと最適化」を参照してください。この付録では以下の高度なトピックを扱います：

- **例外処理の内部メカニズム**: 例外テーブルとバイトコード、JVMでの例外処理フロー
- **パフォーマンスコストの分析**: 例外発生時のオーバーヘッド、スタックトレース生成コスト
- **最適化テクニック**: 例外の事前割り当て、条件付きスタックトレース生成
- **高性能例外処理パターン**: Result型、Optional活用、Null Objectパターン
- **メモリ効率と監視**: 例外オブジェクトのメモリ使用量、例外処理の統計情報収集

これらの技術は、特に高性能が要求されるシステムや大量トランザクション処理において重要な役割を果たします。

## 14.5 例外処理のベストプラクティス

-   **例外を無視しない**: `catch`ブロックを空にしないでください。少なくともログには記録しましょう。
-   **具体的な例外をキャッチする**: `catch (Exception e)`で安易にすべての例外を捕捉するのではなく、対処可能な具体的な例外を`catch`します。
-   **リソースは必ず解放する**: `try-with-resources`を積極的に利用しましょう。
-   **ユーザーに見せる情報とログに残す情報を分ける**: ユーザーには分かりやすいメッセージとエラーIDを、ログには原因究明のための詳細な情報（スタックトレースなど）を記録します。

適切な例外処理を実装することは、信頼性の高いソフトウェアを開発するための必須スキルです。
