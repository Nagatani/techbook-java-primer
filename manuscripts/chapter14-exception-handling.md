# <b>14章</b> <span>例外処理</span> <small>障害に強いプログラムの構築</small>

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

リポジトリ: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`

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

次のステップ: 基礎課題が完了したら、第15章「ファイル入出力」に進みましょう。


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
- 基本的な例外処理構文が正しく使えている
- ビジネスロジックに応じたカスタム例外が設計できている
- リソース管理と例外処理が適切に実装できている
- 例外処理の設計パターンが理解できている

### 技術要素
- 検査例外と非検査例外の使い分けができている
- try-with-resourcesでリソース管理ができている
- 例外チェインによる根本原因の追跡ができている
- パフォーマンスを考慮した例外処理ができている

### 応用・発展レベル
- 高度な型システム設計ができている
- メタプログラミング技術を活用できている
- 理論的・哲学的概念をコードで表現できている
- 革新的なアプローチを考案できている

## エラーの種類と対処法

### ソフトウェアの信頼性とエラー処理

現代のソフトウェア開発では、システムの信頼性が極めて重要です。銀行システム、医療機器、自動運転など、プログラムの不具合が重大な結果を招く分野が増えています。このような背景から、エラーの適切な管理と処理は、単なる技術的なスキルを超えて、ソフトウェアエンジニアの基本的な責任となっています。

プログラムを作成する上で、予期しない状況やエラーへの対応は避けて通れません。エラーには主に以下の3つの種類があります。

1.  **構文エラー (Syntax Error)**: コードの文法的な誤り。コンパイル時に発見され、修正しないとプログラムを実行できません。
2.  **実行時エラー (Runtime Error)**: プログラムの実行中に発生する異常事態。本章で学ぶ「例外 (Exception)」もこの一種です。
3.  **論理エラー (Logic Error)**: プログラムはエラーなく動作するが、実行結果が意図通りにならない不具合。デバッグによって原因を特定し、ロジックを修正する必要があります。

本章では、特に「実行時エラー」に焦点を当て、Javaの例外処理のしくみを学びます。

## 例外処理の基本: `try-catch-finally`

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

<span class="listing-number">**サンプルコード14-1**</span>

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

<span class="listing-number">**サンプルコード14-2**</span>

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

<span class="listing-number">**サンプルコード14-3**</span>

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

## 検査例外と非検査例外

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

<span class="listing-number">**サンプルコード14-4**</span>

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

## 独自例外と例外の連鎖

### 独自例外の作成とスロー

アプリケーション固有のエラー状況を表現するために、独自の例外クラスを作成できます。`Exception`または`RuntimeException`を継承して定義します。`throw`キーワードを使うと、任意の場所で意図的に例外を発生させることができます。

<span class="listing-number">**サンプルコード14-5**</span>

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

<span class="listing-number">**サンプルコード14-6**</span>

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

本章で学んだ例外処理をさらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

付録リソース: `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b14-exception-performance/`

この付録では以下の高度なトピックを扱います：

- 例外処理の内部メカニズム: 例外テーブルとバイトコード、JVMでの例外処理フロー
- パフォーマンスコストの分析: 例外発生時のオーバーヘッド、スタックトレース生成コスト
- 最適化テクニック: 例外の事前割り当て、条件付きスタックトレース生成
- 高性能例外処理パターン: Result型、Optional活用、Null Objectパターン
- メモリ効率と監視: 例外オブジェクトのメモリ使用量、例外処理の統計情報収集

これらの技術は、特に高性能が要求されるシステムや大量トランザクション処理において重要な役割を果たします。

## 14.4.1 実践的な例外処理パターン

実際のシステム開発では、単純な`try-catch`を超えた高度な例外処理パターンが必要になります。ここでは、実践的な開発で頻繁に使われる重要なパターンを学びます。

### リトライパターン（一時的な障害への対処）

ネットワーク接続の一時的な問題やリソースの一時的な不足など、時間を置けば解決する可能性のあるエラーに対して、自動的に再試行を行うパターンです。

<span class="listing-number">**サンプルコード14-7**</span>

```java
import java.util.concurrent.Callable;
import java.io.IOException;

public class RetryableOperation {
    private static final int MAX_RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MS = 1000;
    
    public static <T> T executeWithRetry(Callable<T> operation, 
                                       Class<? extends Exception> retryableException) 
                                       throws Exception {
        Exception lastException = null;
        
        for (int attempt = 1; attempt <= MAX_RETRY_COUNT; attempt++) {
            try {
                return operation.call();
            } catch (Exception e) {
                if (!retryableException.isInstance(e)) {
                    throw e; // リトライ対象外の例外は即座に再スロー
                }
                
                lastException = e;
                System.out.printf("試行 %d/%d 失敗: %s%n", 
                    attempt, MAX_RETRY_COUNT, e.getMessage());
                
                if (attempt < MAX_RETRY_COUNT) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("リトライ中に中断されました", ie);
                    }
                }
            }
        }
        
        throw new RuntimeException("最大リトライ回数に達しました", lastException);
    }
    
    // 使用例
    public static void main(String[] args) {
        try {
            String result = executeWithRetry(() -> {
                // 一時的に失敗する可能性のある処理
                if (Math.random() < 0.7) {
                    throw new IOException("ネットワークエラー");
                }
                return "成功";
            }, IOException.class);
            
            System.out.println("結果: " + result);
        } catch (Exception e) {
            System.err.println("最終的に失敗: " + e.getMessage());
        }
    }
}
```

### サーキットブレーカーパターン（連続失敗への対処）

連続して失敗が発生した場合、一定時間処理をブロックすることで、障害の拡大を防ぐパターンです。マイクロサービスアーキテクチャで特に重要です。

<span class="listing-number">**サンプルコード14-8**</span>

```java
import java.util.concurrent.Callable;

public class CircuitBreaker {
    private enum State {
        CLOSED,    // 通常状態
        OPEN,      // 遮断状態
        HALF_OPEN  // 半開状態（テスト中）
    }
    
    private State state = State.CLOSED;
    private int failureCount = 0;
    private long lastFailureTime = 0;
    
    private final int failureThreshold;
    private final long timeout;
    
    public CircuitBreaker(int failureThreshold, long timeoutMs) {
        this.failureThreshold = failureThreshold;
        this.timeout = timeoutMs;
    }
    
    public <T> T execute(Callable<T> operation) throws Exception {
        if (state == State.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > timeout) {
                state = State.HALF_OPEN;
            } else {
                throw new RuntimeException("サーキットブレーカーが開いています");
            }
        }
        
        try {
            T result = operation.call();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
    
    private void onSuccess() {
        failureCount = 0;
        state = State.CLOSED;
    }
    
    private void onFailure() {
        failureCount++;
        lastFailureTime = System.currentTimeMillis();
        
        if (failureCount >= failureThreshold) {
            state = State.OPEN;
            System.err.println("サーキットブレーカーを開きました");
        }
    }
}
```

### 例外の集約と報告

複数の処理を並行実行する場合、発生した例外を集約して適切に報告する必要があります。

<span class="listing-number">**サンプルコード14-9**</span>

```java
import java.util.*;

public class ExceptionAggregator {
    private final List<Exception> exceptions = new ArrayList<>();
    
    public void addException(Exception e) {
        exceptions.add(e);
    }
    
    public void throwIfAny() throws AggregateException {
        if (!exceptions.isEmpty()) {
            throw new AggregateException(exceptions);
        }
    }
    
    // 集約例外クラス
    public static class AggregateException extends Exception {
        private final List<Exception> exceptions;
        
        public AggregateException(List<Exception> exceptions) {
            super(String.format("%d個の例外が発生しました", exceptions.size()));
            this.exceptions = new ArrayList<>(exceptions);
        }
        
        public List<Exception> getExceptions() {
            return Collections.unmodifiableList(exceptions);
        }
        
        @Override
        public void printStackTrace() {
            super.printStackTrace();
            System.err.println("--- 内包する例外 ---");
            for (int i = 0; i < exceptions.size(); i++) {
                System.err.printf("例外 %d:%n", i + 1);
                exceptions.get(i).printStackTrace();
            }
        }
    }
    
    // 使用例
    public static void processMultipleTasks(List<Runnable> tasks) 
            throws AggregateException {
        ExceptionAggregator aggregator = new ExceptionAggregator();
        
        for (Runnable task : tasks) {
            try {
                task.run();
            } catch (Exception e) {
                aggregator.addException(e);
            }
        }
        
        aggregator.throwIfAny();
    }
}
```

## 14.4.2 リソース管理の実践例

### 複数リソースのtry-with-resources

実際の開発では、複数のリソースを同時に扱うことが多くあります。try-with-resourcesは複数のリソースを効率的に管理できます。

<span class="listing-number">**サンプルコード14-10**</span>

```java
import java.io.*;
import java.sql.*;

public class MultiResourceExample {
    public static void copyFile(String sourcePath, String destPath) 
            throws IOException {
        // 複数のリソースをセミコロンで区切って宣言
        try (FileInputStream fis = new FileInputStream(sourcePath);
             FileOutputStream fos = new FileOutputStream(destPath);
             BufferedInputStream bis = new BufferedInputStream(fis);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            
        } // すべてのリソースが逆順（宣言と逆の順序）で自動的にクローズされる
    }
    
    // データベース接続の例
    public static void executeDatabaseTransaction() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/mydb";
        
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(
                 "INSERT INTO users (name, email) VALUES (?, ?)");
             Statement stmt = conn.createStatement()) {
            
            conn.setAutoCommit(false);
            
            try {
                pstmt.setString(1, "山田太郎");
                pstmt.setString(2, "yamada@example.com");
                pstmt.executeUpdate();
                
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
                if (rs.next()) {
                    System.out.println("ユーザー数: " + rs.getInt(1));
                }
                
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}
```

### カスタムAutoCloseableの実装

独自のリソース管理クラスを作成する場合、`AutoCloseable`インターフェースを実装することで、try-with-resourcesで使用できるようになります。

<span class="listing-number">**サンプルコード14-11**</span>

```java
import java.util.*;

public class ResourcePool implements AutoCloseable {
    private final Queue<Resource> availableResources = new LinkedList<>();
    private final Set<Resource> usedResources = new HashSet<>();
    private boolean closed = false;
    
    public ResourcePool(int poolSize) {
        for (int i = 0; i < poolSize; i++) {
            availableResources.offer(new Resource(i));
        }
    }
    
    public Resource acquire() throws InterruptedException {
        synchronized (this) {
            while (availableResources.isEmpty() && !closed) {
                wait();
            }
            
            if (closed) {
                throw new IllegalStateException("プールはクローズされています");
            }
            
            Resource resource = availableResources.poll();
            usedResources.add(resource);
            return resource;
        }
    }
    
    public void release(Resource resource) {
        synchronized (this) {
            if (usedResources.remove(resource)) {
                availableResources.offer(resource);
                notify();
            }
        }
    }
    
    @Override
    public void close() {
        synchronized (this) {
            closed = true;
            notifyAll();
            
            // すべてのリソースをクリーンアップ
            for (Resource resource : availableResources) {
                resource.cleanup();
            }
            for (Resource resource : usedResources) {
                resource.cleanup();
            }
        }
    }
    
    // リソースクラス
    static class Resource {
        private final int id;
        
        Resource(int id) {
            this.id = id;
        }
        
        void cleanup() {
            System.out.println("リソース " + id + " をクリーンアップしました");
        }
    }
}
```

## 14.4.3 例外処理とログ記録

### 適切なログレベルの選択

ログ記録は例外処理の重要な要素です。適切なログレベルを選択することで、問題の深刻度を正確に伝えることができます。

<span class="listing-number">**サンプルコード14-12**</span>

```java
import java.util.logging.*;
import java.time.Instant;
import java.util.*;

public class ExceptionLogging {
    private static final Logger logger = 
        Logger.getLogger(ExceptionLogging.class.getName());
    
    public void processUserRequest(String userId) {
        try {
            // ユーザー情報の取得
            User user = findUser(userId);
            
            // 処理の実行
            performBusinessLogic(user);
            
        } catch (UserNotFoundException e) {
            // ビジネス例外：警告レベル
            logger.log(Level.WARNING, 
                "ユーザーが見つかりません: " + userId, e);
            
        } catch (ValidationException e) {
            // 入力検証エラー：情報レベル（想定内のエラー）
            logger.log(Level.INFO, 
                "検証エラー: " + e.getMessage());
            
        } catch (DatabaseException e) {
            // システム例外：エラーレベル
            logger.log(Level.SEVERE, 
                "データベースエラーが発生しました", e);
            
            // アラート送信などの追加処理
            notifyAdministrator(e);
            
        } catch (Exception e) {
            // 予期せぬ例外：重大エラー
            logger.log(Level.SEVERE, 
                "予期せぬエラーが発生しました", e);
            
            // エラーIDを生成してユーザーに提示
            String errorId = generateErrorId();
            logger.severe("エラーID: " + errorId);
            
            throw new SystemException(
                "システムエラーが発生しました。エラーID: " + errorId, e);
        }
    }
    
    private String generateErrorId() {
        return "ERR-" + System.currentTimeMillis();
    }
    
    private void notifyAdministrator(Exception e) {
        // 管理者への通知処理
    }
}
```

### 例外情報の構造化記録

本番環境では、例外情報を構造化して記録することで、ログ分析が容易になります。

<span class="listing-number">**サンプルコード14-13**</span>

```java
public class StructuredExceptionLogger {
    private static final Logger logger = 
        Logger.getLogger(StructuredExceptionLogger.class.getName());
    
    public static void logException(String operation, 
                                  Exception exception, 
                                  Map<String, Object> context) {
        // 構造化されたログエントリを作成
        LogRecord record = new LogRecord(Level.SEVERE, 
            createStructuredMessage(operation, exception, context));
        
        record.setThrown(exception);
        record.setSourceClassName(getCallerClassName());
        record.setSourceMethodName(getCallerMethodName());
        
        logger.log(record);
    }
    
    private static String createStructuredMessage(String operation, 
                                                Exception exception, 
                                                Map<String, Object> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"timestamp\":\"").append(Instant.now()).append("\",");
        sb.append("\"operation\":\"").append(operation).append("\",");
        sb.append("\"error_type\":\"").append(exception.getClass().getName()).append("\",");
        sb.append("\"error_message\":\"").append(escapeJson(exception.getMessage())).append("\",");
        sb.append("\"context\":{");
        
        boolean first = true;
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(entry.getKey()).append("\":\"")
              .append(escapeJson(String.valueOf(entry.getValue()))).append("\"");
            first = false;
        }
        
        sb.append("}}");
        return sb.toString();
    }
    
    private static String escapeJson(String text) {
        if (text == null) return "null";
        return text.replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r");
    }
    
    private static String getCallerClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length > 3 ? stackTrace[3].getClassName() : "Unknown";
    }
    
    private static String getCallerMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace.length > 3 ? stackTrace[3].getMethodName() : "Unknown";
    }
}
```

## 14.4.4 業務アプリケーションでの例外設計

### ビジネス例外とシステム例外の分離

大規模なアプリケーションでは、ビジネスロジックに関する例外とシステムレベルの例外を明確に分離することが重要です。

<span class="listing-number">**サンプルコード14-14**</span>

```java
import java.util.*;
import java.time.Instant;

// ビジネス例外の基底クラス
public abstract class BusinessException extends Exception {
    private final String errorCode;
    private final Map<String, Object> details;
    
    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.details = new HashMap<>();
    }
    
    protected BusinessException(String errorCode, String message, 
                              Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = new HashMap<>(details);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public Map<String, Object> getDetails() {
        return Collections.unmodifiableMap(details);
    }
    
    public void addDetail(String key, Object value) {
        details.put(key, value);
    }
}

// 具体的なビジネス例外
public class InsufficientBalanceException extends BusinessException {
    public InsufficientBalanceException(double currentBalance, 
                                      double requestedAmount) {
        super("ACC-001", "残高が不足しています");
        addDetail("currentBalance", currentBalance);
        addDetail("requestedAmount", requestedAmount);
        addDetail("shortage", requestedAmount - currentBalance);
    }
}

// システム例外の基底クラス
public abstract class SystemException extends RuntimeException {
    private final String errorId;
    private final Instant occurredAt;
    
    protected SystemException(String message, Throwable cause) {
        super(message, cause);
        this.errorId = generateErrorId();
        this.occurredAt = Instant.now();
    }
    
    private String generateErrorId() {
        return String.format("SYS-%s-%s", 
            getClass().getSimpleName(), 
            java.util.UUID.randomUUID().toString().substring(0, 8));
    }
    
    public String getErrorId() {
        return errorId;
    }
    
    public Instant getOccurredAt() {
        return occurredAt;
    }
}
```

### エラーコードとメッセージの管理

エラーコードとメッセージを一元管理することで、国際化やメンテナンスが容易になります。

<span class="listing-number">**サンプルコード14-15**</span>

```java
public enum ErrorCode {
    // アカウント関連エラー
    ACC_001("ACC-001", "残高が不足しています"),
    ACC_002("ACC-002", "アカウントが凍結されています"),
    ACC_003("ACC-003", "取引限度額を超えています"),
    
    // 認証関連エラー
    AUTH_001("AUTH-001", "認証に失敗しました"),
    AUTH_002("AUTH-002", "セッションの有効期限が切れました"),
    AUTH_003("AUTH-003", "アクセス権限がありません"),
    
    // システムエラー
    SYS_001("SYS-001", "システムエラーが発生しました"),
    SYS_002("SYS-002", "サービスが一時的に利用できません"),
    SYS_003("SYS-003", "データベース接続エラー");
    
    private final String code;
    private final String defaultMessage;
    
    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return defaultMessage;
    }
    
    // メッセージテンプレートを使用したエラーメッセージ生成
    public String formatMessage(Object... args) {
        return String.format(defaultMessage, args);
    }
}

// エラーレスポンスの統一フォーマット
public class ErrorResponse {
    private final String errorCode;
    private final String message;
    private final Map<String, Object> details;
    private final String timestamp;
    private final String path;
    
    public ErrorResponse(BusinessException e, String path) {
        this.errorCode = e.getErrorCode();
        this.message = e.getMessage();
        this.details = e.getDetails();
        this.timestamp = Instant.now().toString();
        this.path = path;
    }
    
    public ErrorResponse(SystemException e, String path) {
        this.errorCode = "SYS-999";
        this.message = "システムエラーが発生しました";
        this.details = Map.of("errorId", e.getErrorId());
        this.timestamp = e.getOccurredAt().toString();
        this.path = path;
    }
    
    // ゲッターメソッド省略
}
```

### 例外処理戦略の実装例

実際のアプリケーションでの例外処理戦略の実装例を示します。

<span class="listing-number">**サンプルコード14-16**</span>

```java
import java.math.BigDecimal;

// 以下はSpring Frameworkを使用した例
// @Service, @Autowired, @Transactionalアノテーションは
// 実際の使用時にはSpringの依存関係が必要
public class TransferService {
    // private static final Logger logger = 
    //     LoggerFactory.getLogger(TransferService.class);
    
    // @Autowired
    private AccountRepository accountRepository;
    
    // @Autowired
    private TransactionRepository transactionRepository;
    
    // @Transactional
    public TransferResult transfer(String fromAccountId, 
                                 String toAccountId, 
                                 BigDecimal amount) 
            throws BusinessException {
        
        // logger.info("送金処理開始: {} -> {}, 金額: {}", 
        //     fromAccountId, toAccountId, amount);
        System.out.println("送金処理開始: " + fromAccountId + " -> " + toAccountId + ", 金額: " + amount);
        
        try {
            // 入力検証
            validateTransferRequest(fromAccountId, toAccountId, amount);
            
            // アカウント取得
            Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));
            
            Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));
            
            // ビジネスルールチェック
            checkBusinessRules(fromAccount, amount);
            
            // 送金実行
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            
            // トランザクション記録
            Transaction transaction = new Transaction(
                fromAccountId, toAccountId, amount, TransactionType.TRANSFER);
            transactionRepository.save(transaction);
            
            // 結果返却
            TransferResult result = new TransferResult(
                transaction.getId(), 
                fromAccount.getBalance(), 
                toAccount.getBalance());
            
            // logger.info("送金処理完了: {}", result);
            System.out.println("送金処理完了: " + result);
            return result;
            
        } catch (BusinessException e) {
            // ビジネス例外は呼び出し元に伝播
            // logger.warn("送金処理でビジネスエラー: {}", e.getMessage());
            System.err.println("送金処理でビジネスエラー: " + e.getMessage());
            throw e;
            
        } catch (Exception e) {
            // 予期せぬエラー（DataAccessExceptionなども含む）
            // logger.error("予期せぬエラー", e);
            e.printStackTrace();
            // SystemExceptionは抽象クラスなので、実装クラスが必要
            throw new RuntimeException("送金処理中に予期せぬエラーが発生しました", e);
        }
    }
    
    private void validateTransferRequest(String fromAccountId, 
                                       String toAccountId, 
                                       BigDecimal amount) 
            throws ValidationException {
        
        if (fromAccountId == null || fromAccountId.isEmpty()) {
            throw new ValidationException("送金元アカウントIDが指定されていません");
        }
        
        if (toAccountId == null || toAccountId.isEmpty()) {
            throw new ValidationException("送金先アカウントIDが指定されていません");
        }
        
        if (fromAccountId.equals(toAccountId)) {
            throw new ValidationException("同一アカウント間での送金はできません");
        }
        
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("送金額は0より大きい値を指定してください");
        }
    }
    
    private void checkBusinessRules(Account account, BigDecimal amount) 
            throws BusinessException {
        
        if (account.isFrozen()) {
            throw new AccountFrozenException(account.getId());
        }
        
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                account.getBalance().doubleValue(), 
                amount.doubleValue());
        }
        
        BigDecimal dailyLimit = account.getDailyTransferLimit();
        BigDecimal todayTotal = transactionRepository
            .getTodayTotalAmount(account.getId());
        
        if (todayTotal.add(amount).compareTo(dailyLimit) > 0) {
            throw new DailyLimitExceededException(
                dailyLimit.doubleValue(), 
                todayTotal.doubleValue(), 
                amount.doubleValue());
        }
    }
}
```

これらの実践的なパターンを理解し適切に適用することで、エラーに強く、保守性の高いシステムを構築することができます。

## 例外処理のベストプラクティス

-   **例外を無視しない**: `catch`ブロックを空にしないでください。少なくともログには記録しましょう。
-   **具体的な例外をキャッチする**: `catch (Exception e)`で安易にすべての例外を捕捉するのではなく、対処可能な具体的な例外を`catch`します。
-   **リソースは必ず解放する**: `try-with-resources`を積極的に利用しましょう。
-   **ユーザーに見せる情報とログに残す情報を分ける**: ユーザーには分かりやすいメッセージとエラーIDを、ログには原因究明のための詳細な情報（スタックトレースなど）を記録します。

適切な例外処理を実装することは、信頼性の高いソフトウェアを開発するための必須スキルです。
