# 第13章 例外処理：よくあるエラーと対処法

書籍『Java入門』第13章「例外処理」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

例外処理の学習において、開発者がよく遭遇するエラーとその対処法を理解しておくことは重要です。以下は典型的なエラーパターンとその解決方法です。

## 1. try-catch文の誤った使用

#### 問題
すべての処理をひとつのtry-catchで囲む。

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

エラーメッセージ： 特定のエラーがわかりません。

#### 対処法
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

## 2. 例外の握りつぶし

#### 問題
例外を無視する空のcatchブロック。

```java
// 悪い例
try {
    processImportantData();
} catch (Exception e) {
    // 何もしない（例外を握りつぶす）
}
```

対処法： 最低限ログを記録し、意味のある処理をします。

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

## 3. 誤った例外型の使用

#### 問題
RuntimeExceptionを誤って使用。

```java
// 悪い例
public void validateAge(int age) {
    if (age < 0 || age > 150) {
        throw new RuntimeException("年齢が不正です");
    }
}
```

エラーメッセージ： 呼び出し元で正しく処理できません。

対処法： 目的に合った例外型を定義・使用します。

```java
// 良い例
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

## 4. リソース管理の問題

#### 問題
finallyブロックでのリソース解放漏れ。

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

エラーメッセージ： `Error: unreported exception IOException; must be caught or declared to be thrown`。

対処法： try-with-resourcesを使用します。

```java
// 良い例
try (FileInputStream fis = new FileInputStream("file.txt")) {
    // 処理
} catch (IOException e) {
    logger.error("ファイル処理エラー", e);
    throw new ProcessingException("ファイル処理に失敗しました", e);
}
```

## 5. カスタム例外の設計ミス

#### 問題
例外クラスに必要なコンストラクタがありません。

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

対処法： 標準的なコンストラクタを提供します。

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

## 6. 例外の再スロー時の問題

#### 問題
スタックトレースの情報が失われる。

```java
// 悪い例
try {
    dangerousOperation();
} catch (Exception e) {
    throw new RuntimeException("操作に失敗しました");
}
```

対処法： 原因例外を保持します。

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

## 7. 並行処理での例外処理

この項のサンプルは、第15章「マルチスレッドプログラミング」で学ぶスレッドや`CompletableFuture`を使います。
第15章を学んだあとに読み返すと理解しやすくなります。

#### 問題
スレッドでの例外が正しく処理されません。

```java
// 悪い例
Thread thread = new Thread(() -> {
    try {
        riskyOperation();
    } catch (Exception e) {
        // このエラーはメインスレッドで捕捉できない
        e.printStackTrace();
    }
});
thread.start();
```

対処法： 包括的な例外処理戦略を採用します。

```java
// 良い例
ExecutorService executor = Executors.newFixedThreadPool(2);
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    try {
        return riskyOperation();
    } catch (Exception e) {
        throw new CompletionException("並行処理でエラーが発生しました", e);
    }
}, executor);

future.exceptionally(ex -> {
    logger.error("並行処理エラー", ex);
    return "デフォルト値";
});
```

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
