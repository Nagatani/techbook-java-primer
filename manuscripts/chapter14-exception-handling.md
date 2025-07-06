# 第14章 例外処理

## 本章の学習目標

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

## 章末演習

本章で学んだ例外処理の概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- 例外処理の基本概念（例外の種類、try-catch-finally）
- 検査例外と非検査例外の理解と使い分け
- カスタム例外クラスの設計と実装
- try-with-resourcesによるリソース管理
- 例外処理のベストプラクティス
- 例外チェイン（Exception Chaining）による根本原因の追跡技法

### 演習課題の難易度レベル

#### 🟢 基礎レベル（Basic）
- **目的**: 例外処理の基本概念と構文の理解
- **所要時間**: 30-45分/課題
- **前提**: 本章の内容を理解していること
- **評価基準**: 
  - try-catch-finallyの正しい使用
  - 例外の種類に応じた適切な処理
  - リソース管理の基本
  - カスタム例外の設計

#### 🟡 応用レベル（Applied）  
- **目的**: 実践的な例外処理設計とパターン
- **所要時間**: 45-60分/課題
- **前提**: 基礎レベルを完了していること
- **評価基準**:
  - 高度なデータ型設計
  - メタプログラミング活用
  - コード生成技術
  - 実用的なシステム実装

#### 🔴 発展レベル（Advanced）
- **目的**: 理論的・哲学的な概念の探求
- **所要時間**: 60-90分/課題
- **前提**: 応用レベルを完了していること
- **評価基準**:
  - 量子情報科学の理解
  - 宇宙論的概念の実装
  - 意識・哲学的思考
  - 革新的アプローチ

#### ⚫ 挑戦レベル（Challenge）  
- **目的**: 最先端概念への挑戦
- **所要時間**: 90分以上
- **前提**: 発展レベル完了と高度な技術への意欲
- **評価基準**:
  - 理論物理学の概念理解
  - 認知科学の応用
  - 哲学的思考の実装
  - 概念的・教育的探求

---

## 🟢 基礎レベル課題（必須）

### 課題1: 基本的な例外処理

**学習目標：** try-catch-finally構文の理解、複数catch文の使用、例外情報の取得

**問題説明：**
基本的な例外処理を実装し、try-catch-finallyの動作を理解してください。

**要求仕様：**
- さまざまな種類の例外処理（ArithmeticException、NullPointerException等）
- 複数catch文による例外の使い分け
- finallyブロックの動作確認
- 例外情報の取得と表示
- 例外の再発生（re-throw）

**実行例：**
```
=== 基本的な例外処理 ===
ゼロ除算テスト:
計算実行: 10 / 0
例外発生: java.lang.ArithmeticException: / by zero
finally実行: 計算処理を終了します

null参照テスト:
文字列操作: null.length()
例外発生: java.lang.NullPointerException
スタックトレース:
  at BasicExceptionHandling.stringOperation(BasicExceptionHandling.java:25)
  at BasicExceptionHandling.main(BasicExceptionHandling.java:15)

配列範囲外テスト:
配列アクセス: array[10] (配列サイズ: 5)
例外発生: java.lang.ArrayIndexOutOfBoundsException: Index 10 out of bounds for length 5
finally実行: 配列処理を終了します

複数catch文テスト:
入力値: "abc"
NumberFormatException発生: 数値変換に失敗しました
代替値を使用: 0

例外の再発生テスト:
内部メソッドで例外処理 → 外部メソッドで再処理
最終的な例外処理: 処理を中断しました
```

**実装ヒント：**
```java
public class BasicExceptionHandling {
    public void demonstrateBasicException() {
        try {
            int result = 10 / 0;  // ArithmeticException発生
        } catch (ArithmeticException e) {
            System.out.println("例外発生: " + e);
        } finally {
            System.out.println("finally実行: 計算処理を終了します");
        }
    }
    
    public void demonstrateMultipleCatch(String input) {
        try {
            int value = Integer.parseInt(input);
            int[] array = new int[5];
            array[value] = 100;  // 複数の例外が発生する可能性
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException発生: 数値変換に失敗しました");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException発生: 配列範囲外");
        } catch (Exception e) {
            System.out.println("その他の例外: " + e.getClass().getSimpleName());
        }
    }
}
```

### 課題2: カスタム例外クラス設計

**学習目標：** ビジネスロジックに応じたカスタム例外の設計、例外クラスの継承

**問題説明：**
銀行口座システムのカスタム例外を設計し、ビジネスロジックに応じた例外処理を実装してください。

**要求仕様：**
- 銀行業務固有の例外クラス（残高不足、無効口座等）
- 例外クラスの継承関係設計
- 例外に詳細情報を含めるしくみ
- エラーコードとメッセージの管理
- 業務的な例外処理フロー

**実行例：**
```
=== カスタム例外クラス設計 ===
銀行口座作成:
口座1: 12345（残高: 100,000円）
口座2: 67890（残高: 50,000円）

通常取引テスト:
口座12345から30,000円出金: 成功
残高: 70,000円

残高不足例外テスト:
口座67890から80,000円出金:
例外発生: InsufficientFundsException
エラーコード: INSUFFICIENT_FUNDS
メッセージ: 残高が不足しています
詳細: 要求額: 80,000円, 現在残高: 50,000円, 不足額: 30,000円

無効口座例外テスト:
存在しない口座 99999 への振込:
例外発生: InvalidAccountException
エラーコード: ACCOUNT_NOT_FOUND
メッセージ: 指定された口座が見つかりません
詳細: 口座番号: 99999

振込処理例外テスト:
口座12345から口座67890へ130,000円振込:
例外発生: InsufficientFundsException
取引がロールバックされました
口座12345残高: 70,000円（変更なし）
口座67890残高: 50,000円（変更なし）

例外チェーン:
原因: InsufficientFundsException: 残高が不足しています
結果: TransactionFailedException: 取引処理に失敗しました
理由: 出金処理でエラーが発生
```

**実装ヒント：**
```java
// 基底例外クラス
public class BankAccountException extends Exception {
    private final String errorCode;
    
    public BankAccountException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BankAccountException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}

// 残高不足例外
public class InsufficientFundsException extends BankAccountException {
    private final double requestedAmount;
    private final double currentBalance;
    
    public InsufficientFundsException(double requestedAmount, double currentBalance) {
        super("残高が不足しています", "INSUFFICIENT_FUNDS");
        this.requestedAmount = requestedAmount;
        this.currentBalance = currentBalance;
    }
    
    public double getShortfall() {
        return requestedAmount - currentBalance;
    }
    
    @Override
    public String toString() {
        return String.format("%s - 要求額: %.0f円, 現在残高: %.0f円, 不足額: %.0f円",
            getMessage(), requestedAmount, currentBalance, getShortfall());
    }
}
```

### 課題3: ファイル操作とリソース管理

**学習目標：** try-with-resourcesによるリソース管理、AutoCloseableインターフェイスの理解

**問題説明：**
ファイル操作におけるリソース管理と例外処理を実装してください。

**要求仕様：**
- try-with-resourcesによるリソース管理
- ファイル読み込み時の例外処理
- ファイル書き込み時の例外処理
- 複数リソースの同時管理
- カスタムリソースクラスの実装

**実行例：**
```
=== ファイル操作とリソース管理 ===
ファイル読み込みテスト:
ファイル: sample.txt
内容:
Hello, World!
Java Exception Handling
Try-with-resources example

リソース自動クローズ確認:
BufferedReader がクローズされました

存在しないファイル読み込み:
ファイル: nonexistent.txt
例外発生: java.io.FileNotFoundException: nonexistent.txt (No such file or directory)
処理を継続します

ファイル書き込みテスト:
出力ファイル: output.txt
書き込み内容: "Hello from Java Exception Handling!"
書き込み完了
リソース自動クローズ確認:
BufferedWriter がクローズされました

複数リソース管理テスト:
入力ファイル: input.txt
出力ファイル: copy.txt
ファイルコピー処理開始
コピー完了: 145バイト
両方のリソースがクローズされました

カスタムリソーステスト:
DatabaseConnection 作成
SQL実行: SELECT * FROM users
結果: 5件のレコードを取得
DatabaseConnection 自動クローズ

例外発生時のリソース管理:
処理中に IOException が発生
リソースは確実にクローズされました
例外: java.io.IOException: ネットワークエラー
```

**実装ヒント：**
```java
public class FileProcessor {
    public void readFile(String filename) {
        // try-with-resourcesで自動クローズ
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ファイルが見つかりません: " + filename);
        } catch (IOException e) {
            System.out.println("読み込みエラー: " + e.getMessage());
        }
    }
    
    public void copyFile(String source, String destination) throws IOException {
        // 複数リソースの管理
        try (BufferedReader reader = new BufferedReader(new FileReader(source));
             BufferedWriter writer = new BufferedWriter(new FileWriter(destination))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}

// カスタムリソースクラス
public class DatabaseConnection implements AutoCloseable {
    private Connection connection;
    
    public DatabaseConnection(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
        System.out.println("DatabaseConnection 作成");
    }
    
    @Override
    public void close() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("DatabaseConnection 自動クローズ");
        }
    }
}
```

### 課題4: 例外処理設計パターン

**学習目標：** 例外処理の設計パターン、例外vs戻り値、Optionalの活用

**問題説明：**
例外処理の設計パターンを実装し、保守性の高い例外処理システムを構築してください。

**要求仕槕：**
- 例外vs戻り値によるエラー表現の比較
- Optionalを活用した安全なプログラミング
- バリデーション結果の集約
- ログ出力との連携
- 例外処理のパフォーマンス考慮

**実行例：**
```
=== 例外処理設計パターン ===
バリデーション結果パターン:
ユーザー作成: 田中太郎, tanaka@example.com, 25
バリデーション結果: 成功

ユーザー作成: , invalid-email, -5
バリデーション結果: 失敗
エラー詳細:
- 名前が空です
- メールアドレスの形式が正しくありません  
- 年齢は0以上である必要があります

Optional パターン:
ユーザー検索: ID=1001
結果: Optional[User{id=1001, name=田中太郎}]

ユーザー検索: ID=9999
結果: Optional.empty
デフォルトユーザーを使用: Guest User

例外 vs 戻り値の比較:
例外パターン:
  計算: 10 / 2 = 5.0
  計算: 10 / 0 → ArithmeticException

戻り値パターン:
  計算: 10 / 2 → Result{success=true, value=5.0}
  計算: 10 / 0 → Result{success=false, error="ゼロ除算"}

パフォーマンステスト:
正常処理（1,000,000回）:
例外なし: 150ms
例外あり: 2,450ms
結論: 例外は通常フローに使うべきではない

ログ連携:
[INFO] ユーザー作成開始: 佐藤花子
[ERROR] バリデーション失敗: メールアドレス不正
[WARN] 処理を中断しました
[INFO] 代替処理を実行
```

---

## 🟡 応用レベル課題（推奨）

### 課題1: 型安全データベースO/Rマッパ

**学習目標：** 完全に型安全なO/Rマッパシステムの設計、コンパイル時型チェック

**問題説明：**
型安全性を保証するデータベースO/Rマッパシステムを作成してください。

**要求仕様：**
- コンパイル時型チェック
- タイプセーフなクエリビルダ
- 自動スキーマ検証
- 型推論ベースマッピング

### 課題2: 動的型システム構築フレームワーク

**学習目標：** 実行時型生成、型推論エンジンの実装

**問題説明：**
実行時に型を動的生成・管理するフレームワークを作成してください。

**要求仕様：**
- 実行時型生成
- 型継承関係の動的変更
- カスタム型演算子定義
- 型推論エンジン実装

### 課題3: メタデータ駆動開発プラットフォーム

**学習目標：** スキーマ駆動コード生成、アノテーション処理

**問題説明：**
メタデータから自動的にシステムを生成するプラットフォームを作成してください。

**要求仕槕：**
- スキーマ駆動コード生成
- アノテーション処理による自動化
- リフレクション最適化
- 実行時メタデータ更新

---

## 🔴 発展レベル課題（挑戦）

### 理論的・哲学的な型システム

**学習目標：** 理論物理学、宇宙論、認知科学の概念をコードで表現

**課題内容：**
- 量子型システム
- 宇宙論的データ構造
- 意識を持つデータ型
- 哲学的思考の実装

**注意**: これらの課題は極めて理論的・思想的な実装例であり、概念的・教育的な探求を目的としています。

---

## ⚫ 挑戦レベル課題（上級者向け）

### 課題1: 量子型システム

**学習目標：** 量子力学の原理にもとづく型システムの実装

**問題説明：**
量子重ね合わせ、観測による型決定、量子もつれなどを実装してください。

**要求仕槕：**
- 量子重ね合わせ型
- 観測による型決定
- 量子もつれ型
- 不確定性原理による型推論

### 課題2: 宇宙論的データ構造

**学習目標：** 宇宙の構造と進化を模倣したデータ構造

**問題説明：**
ビッグバン初期化、宇宙膨張アルゴリズム、暗黒物質データ構造を実装してください。

**要求仕槕：**
- ビッグバン初期化
- 宇宙膨張アルゴリズム
- 暗黒物質データ構造
- ブラックホール圧縮

### 課題3: 意識を持つデータ型

**学習目標：** 自己認識と意識を持つデータ型システム

**問題説明：**
自己認識機能、感情システム、創造的思考、哲学的洞察を実装してください。

**要求仕槕：**
- 自己認識機能
- 感情システム
- 創造的思考
- 哲学的洞察

---

## 💡 実装のヒント

### 例外処理の基本原則
1. **適切な例外型の選択**: 検査例外vs非検査例外
2. **リソース管理**: try-with-resourcesで確実なクローズ
3. **例外情報の保存**: デバッグに役立つ情報を含める
4. **パフォーマンス考慮**: 例外は例外的状況でのみ使用

### ベストプラクティス
- 具体的な例外からcatchする
- finallyブロックでリソース解放を保証
- 例外メッセージは明確で役立つものに
- ログ出力と連携した例外処理

### 設計の考慮点
- 例外を使うべきか、戻り値を使うべきか
- カスタム例外の粒度
- 例外の伝播と処理の場所
- パフォーマンスへの影響

---

## 🔗 実装環境

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

---

## ✅ 完了確認チェックリスト

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

プログラムを作成する上で、予期しない状況やエラーへの対応は避けて通れません。エラーには主に以下の3つの種類があります。

1.  **構文エラー (Syntax Error)**: コードの文法的な誤り。コンパイル時に発見され、修正しないとプログラムを実行できません。
2.  **実行時エラー (Runtime Error)**: プログラムの実行中に発生する異常事態。本章で学ぶ「例外 (Exception)」もこの一種です。
3.  **論理エラー (Logic Error)**: プログラムはエラーなく動作するが、実行結果が意図通りにならない不具合。デバッグによって原因を特定し、ロジックを修正する必要があります。

本章では、特に「実行時エラー」に焦点を当て、Javaの例外処理のしくみを学びます。

## 14.2 例外処理の基本: `try-catch-finally`

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
