# 第13章 例外処理：よくあるエラーと対処法

書籍『Java入門』第13章「例外処理」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

例外処理の学習において、開発者がよく遭遇するエラーとその対処法を理解しておくことは重要です。以下は典型的なエラーパターンとその解決方法です。

## try-catch文の誤った使用

すべての処理をひとつのtry-catchで囲んでいます。

### エラー例

```java
// 悪い例
try {
    FileReader reader = new FileReader("file.txt");
    BufferedReader br = new BufferedReader(reader);
    String line = br.readLine();
    int number = Integer.parseInt(line);
    IO.println(number);
} catch (Exception e) {
    IO.println("エラーが発生しました");
}
```

### 症状

特定のエラーがわかりません。

### 対処法

具体的な例外をキャッチし、確実にリソースを管理します。

```java
// 良い例
try (FileReader reader = new FileReader("file.txt");
     BufferedReader br = new BufferedReader(reader)) {
    
    String line = br.readLine();
    if (line != null) {
        try {
            int number = Integer.parseInt(line);
            IO.println(number);
        } catch (NumberFormatException e) {
            System.err.println("数値変換エラー: " + line);
        }
    }
} catch (FileNotFoundException e) {
    System.err.println("ファイルが見つかりません: " + e.getMessage());
} catch (IOException e) {
    System.err.println("ファイル読み込みエラー: " + e.getMessage());
}
```

## 例外の握りつぶし

空のcatchブロックで例外を無視しています。

### エラー例

```java
// 悪い例
try {
    processImportantData();
} catch (Exception e) {
    // 何もしない（例外を握りつぶす）
}
```

### 対処法

最低限ログを記録し、意味のある処理をします。

```java
// 良い例
try {
    processImportantData();
} catch (BusinessException e) {
    logger.warn("業務処理エラー: " + e.getMessage());
    // 必要に応じてユーザーに通知
    notifyUser("処理を完了できませんでした: " + e.getMessage());
} catch (Exception e) {
    logger.error("予期せぬエラー", e);
    throw new SystemException("システムエラーが発生しました", e);
}
```

## 誤った例外型の使用

汎用の`RuntimeException`をそのままスローしています。

### エラー例

```java
// 悪い例
public void validateAge(int age) {
    if (age < 0 || age > 150) {
        throw new RuntimeException("年齢が不正です");
    }
}
```

### 症状

呼び出し元は、どのような失敗なのかを例外の型で見分けられません。
`catch (RuntimeException e)`と書くと、`NullPointerException`のような無関係なバグまで一緒に捕まえてしまいます。

### 対処法

目的に合う型の例外を選びます。

引数の値がメソッドの前提を満たさないのは、呼び出し側のプログラムの誤りです。
このような場合は、標準の非検査例外`IllegalArgumentException`を使うのがJavaの慣習です。

```java
// 良い例1：プログラムの誤りとして扱う
public void validateAge(int age) {
    if (age < 0 || age > 150) {
        throw new IllegalArgumentException("年齢は0以上150以下である必要があります: " + age);
    }
}
```

ユーザーが入力した値を確かめる場合のように、呼び出し元に対処（入力のやり直しなど）を求めたいときは、検査例外を定義します。
検査例外にすると、呼び出し元は`try-catch`か`throws`を書かないとコンパイルできないので、対処を忘れません。

```java
// 良い例2：呼び出し元に回復させたい場合は検査例外にする
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

public void validateAge(int age) throws ValidationException {
    if (age < 0) {
        throw new ValidationException("年齢は0以上である必要があります");
    }
    if (age > 150) {
        throw new ValidationException("年齢は150以下である必要があります");
    }
}
```

## リソース管理の問題

finallyブロックでのリソースの解放が漏れています。

### エラー例

```java
// 悪い例
FileInputStream fis = null;
try {
    fis = new FileInputStream("file.txt");
    // 処理
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (fis != null) {
        fis.close(); // IOExceptionが発生する可能性
    }
}
```

### エラーメッセージ

`error: unreported exception IOException; must be caught or declared to be thrown`というコンパイルエラーになります。

### 対処法

try-with-resourcesを使用します。

```java
// 良い例
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // 処理
} catch (IOException e) {
    logger.error("ファイル処理エラー", e);
    throw new ProcessingException("ファイル処理に失敗しました", e);
}
```

## カスタム例外の設計ミス

例外クラスに必要なコンストラクターがありません。

### エラー例

コンパイルできる全文：[chapter13-code/CustomException/](chapter13-code/CustomException/)

```java
// 悪い例
public class CustomException extends Exception {
    public CustomException(String message) {
        super(message);
    }
    // 原因となった例外を保持できない
}
```

### 対処法

標準的なコンストラクターを提供します。

コンパイルできる全文：[chapter13-code/CustomException2/](chapter13-code/CustomException2/)

```java
// 良い例
public class CustomException extends Exception {
    public CustomException(String message) {
        super(message);
    }
    
    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CustomException(Throwable cause) {
        super(cause);
    }
}
```

## 例外の再スロー時の問題

例外を投げ直すときに、スタックトレースの情報が失われます。

### エラー例

```java
// 悪い例
try {
    dangerousOperation();
} catch (Exception e) {
    throw new RuntimeException("操作に失敗しました");
}
```

### 対処法

原因例外を保持します。

```java
// 良い例
try {
    dangerousOperation();
} catch (SpecificException e) {
    logger.error("特定の操作エラー", e);
    throw new BusinessException("操作に失敗しました", e);
} catch (Exception e) {
    logger.error("予期せぬエラー", e);
    throw new SystemException("システムエラーが発生しました", e);
}
```

## 並行処理での例外処理

この項のサンプルは、第15章「マルチスレッドプログラミング」で学ぶスレッドや`CompletableFuture`を使います。
第15章を学んだあとに読み返すと理解しやすくなります。

別のスレッドで発生した例外を、スレッドを起動した側の`catch`で捕まえようとしています。

### エラー例

```java
// 悪い例
try {
    Thread thread = new Thread(() -> {
        String result = riskyOperation();  // ここで例外が発生する
        IO.println(result);
    });
    thread.start();
} catch (RuntimeException e) {
    // 別スレッドの例外はここには届かない
    IO.println("エラーを処理しました");
}
```

### 症状

`thread.start()`はスレッドを起動するとすぐに戻るので、`try`ブロックはその時点で終わります。
例外は起動したスレッドのなかで発生し、そのスレッドのスタックトレースが標準エラー出力に表示されるだけで、`catch`ブロックは実行されません。

### 対処法

`ExecutorService`や`CompletableFuture`を使い、別スレッドの結果と例外を、起動した側で受け取ります。

```java
// 良い例
ExecutorService executor = Executors.newFixedThreadPool(2);
try {
    CompletableFuture<String> future = CompletableFuture.supplyAsync(
        () -> riskyOperation(), executor);

    // 失敗したときは、exceptionallyで代わりの値にする
    String result = future.exceptionally(ex -> {
        logger.error("並行処理エラー", ex);
        return "デフォルト値";
    }).join();
    IO.println(result);
} finally {
    executor.shutdown();  // shutdownしないとプログラムが終了しない
}
```

`exceptionally`は新しい`CompletableFuture`を返すので、その戻り値に対して`join()`などで結果を受け取ります。
`exceptionally`に渡される例外は、元の例外を包んだ`CompletionException`です。元の例外は`getCause()`で取り出せます。

## デバッグのヒント

1. 例外の詳細情報を活用する
   - `getMessage()`: エラーメッセージを取得
   - `getCause()`: 原因となった例外を取得
   - `getStackTrace()`: スタックトレースを取得

2. 目的に応じたログレベルを使用する（本文の「例外処理とログ記録」とサンプルコード13-12）
   - SEVERE: システムエラーと予期せぬ例外
   - WARNING: ビジネスエラー
   - INFO: 想定内のエラー（入力検証エラーなど）と重要な処理の完了
   - FINE: デバッグ情報
   - 本文の`java.util.logging`のレベル名で示した。このページのコード例の`logger.warn`・`logger.error`はSLF4Jなどのログライブラリの書き方で、それぞれWARNING・SEVEREにあたる

3. 例外の発生箇所を特定する
   - IDEのデバッガーを活用
   - ログに十分な情報を記録
   - スタックトレースを読み解く

これらの対処法を理解し実践することで、より堅牢で保守性の高い例外処理を実装できるようになります。
