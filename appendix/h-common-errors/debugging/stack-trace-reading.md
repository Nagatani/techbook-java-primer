# スタックトレースの読み方

## 概要

スタックトレースは、エラーが発生した際にJVMが出力する実行経路の記録です。エラーの原因を特定し、問題を解決するための最も重要な情報源となります。

## スタックトレースの基本構造

### 典型的なスタックトレース

```
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "str" is null
    at com.example.StringProcessor.processString(StringProcessor.java:25)
    at com.example.DataHandler.handleData(DataHandler.java:42)
    at com.example.Main.main(Main.java:15)
```

### 構成要素

1. **例外の種類とスレッド情報**
   ```
   Exception in thread "main" java.lang.NullPointerException
   ```
   - 発生したスレッド名（この例では"main"）
   - 例外クラスの完全修飾名

2. **エラーメッセージ**
   ```
   Cannot invoke "String.length()" because "str" is null
   ```
   - Java 14以降では、より詳細なメッセージが提供される

3. **スタックフレーム**
   ```
   at com.example.StringProcessor.processString(StringProcessor.java:25)
   ```
   - クラス名とメソッド名
   - ソースファイル名と行番号

## スタックトレースの読み方

### 基本的な読み方

1. **上から下へ**: 最初の行が直接的なエラー発生箇所
2. **呼び出し順序**: 下に行くほど古い呼び出し
3. **自分のコードを探す**: パッケージ名から自分のコードを特定

### 実践例：NullPointerException

```java
// StringProcessor.java
public class StringProcessor {
    public int processString(String str) {
        return str.length();  // 25行目：ここでNPE発生
    }
}

// DataHandler.java
public class DataHandler {
    private StringProcessor processor = new StringProcessor();
    
    public void handleData(String data) {
        int length = processor.processString(data);  // 42行目
        System.out.println("Length: " + length);
    }
}

// Main.java
public class Main {
    public static void main(String[] args) {
        DataHandler handler = new DataHandler();
        handler.handleData(null);  // 15行目：nullを渡している
    }
}
```

このコードを実行すると：

```
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.length()" because "str" is null
    at com.example.StringProcessor.processString(StringProcessor.java:25)
    at com.example.DataHandler.handleData(DataHandler.java:42)
    at com.example.Main.main(Main.java:15)
```

**読み解き方**:
1. `StringProcessor.java:25`でNullPointerExceptionが発生
2. それは`DataHandler.java:42`から呼ばれた
3. 大元は`Main.java:15`から始まっている
4. 問題の根本原因：Mainクラスでnullを渡している

## 複雑なスタックトレース

### 原因連鎖（Caused by）

```
Exception in thread "main" java.lang.RuntimeException: データ処理エラー
    at com.example.Service.process(Service.java:30)
    at com.example.Main.main(Main.java:10)
Caused by: java.io.FileNotFoundException: config.xml (No such file or directory)
    at java.base/java.io.FileInputStream.open0(Native Method)
    at java.base/java.io.FileInputStream.open(FileInputStream.java:219)
    at java.base/java.io.FileInputStream.<init>(FileInputStream.java:157)
    at com.example.ConfigLoader.load(ConfigLoader.java:45)
    at com.example.Service.initialize(Service.java:25)
    at com.example.Service.process(Service.java:28)
    ... 1 more
```

**読み解き方**:
- 表面的な例外：`RuntimeException`
- 根本原因：`FileNotFoundException`（ファイルが見つからない）
- "Caused by"セクションが実際の問題を示す

### フレームワークのスタックトレース

```
org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'userService'
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateBean
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean
    at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean
    ... 20 more
Caused by: java.lang.NoSuchMethodException: com.example.UserService.<init>()
    at java.base/java.lang.Class.getConstructor0(Class.java:3349)
    at java.base/java.lang.Class.getDeclaredConstructor(Class.java:2553)
    at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate
    ... 23 more
```

**フィルタリングのコツ**:
1. 自分のパッケージ名（com.example）を探す
2. フレームワークの内部は読み飛ばす
3. "Caused by"に注目

## デバッグテクニック

### 1. 例外の再スロー

```java
public void processFile(String filename) {
    try {
        readFile(filename);
    } catch (IOException e) {
        // 追加情報を付けて再スロー
        throw new ProcessingException(
            "ファイル処理中にエラー: " + filename, e);
    }
}
```

### 2. スタックトレースの出力

```java
public void debugMethod() {
    // 現在のスタックトレースを出力
    new Exception("デバッグ用スタックトレース").printStackTrace();
    
    // または
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (StackTraceElement element : stackTrace) {
        System.out.println(element);
    }
}
```

### 3. カスタムエラーメッセージ

```java
public class DetailedException extends Exception {
    private final Map<String, Object> context = new HashMap<>();
    
    public DetailedException(String message) {
        super(message);
    }
    
    public DetailedException addContext(String key, Object value) {
        context.put(key, value);
        return this;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (!context.isEmpty()) {
            sb.append(" [Context: ");
            context.forEach((k, v) -> 
                sb.append(k).append("=").append(v).append(", "));
            sb.setLength(sb.length() - 2);  // 最後のカンマを削除
            sb.append("]");
        }
        return sb.toString();
    }
}

// 使用例
throw new DetailedException("ユーザーが見つかりません")
    .addContext("userId", userId)
    .addContext("searchTime", System.currentTimeMillis());
```

## IDEでのスタックトレース活用

### 1. ハイパーリンク機能

多くのIDEでは、スタックトレースの行をクリックすると該当箇所にジャンプできます。

### 2. ブレークポイントの設定

```java
public void problematicMethod() {
    try {
        // 問題のあるコード
        riskyOperation();
    } catch (Exception e) {
        e.printStackTrace();  // ここにブレークポイント
        throw e;
    }
}
```

### 3. 例外ブレークポイント

IDEで特定の例外が発生したときに自動的に停止するよう設定：
- IntelliJ IDEA: Run → View Breakpoints → Exception Breakpoints
- Eclipse: Breakpoints View → Add Java Exception Breakpoint

## よくあるパターンと対処法

### 1. ライブラリバージョンの不一致

```
java.lang.NoSuchMethodError: com.example.Library.newMethod()V
    at com.example.MyClass.useLibrary(MyClass.java:30)
```

**対処法**: 依存関係とバージョンを確認

### 2. クラスが見つからない

```
java.lang.ClassNotFoundException: com.mysql.jdbc.Driver
    at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:521)
```

**対処法**: クラスパスに必要なJARが含まれているか確認

### 3. メモリ不足

```
java.lang.OutOfMemoryError: Java heap space
    at java.util.Arrays.copyOf(Arrays.java:3332)
```

**対処法**: JVMのヒープサイズを増やす（-Xmx オプション）

## ログとの組み合わせ

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);
    
    public void handleError(Exception e) {
        // コンテキスト情報と共にログ出力
        logger.error("処理中にエラーが発生しました。ユーザーID: {}, 操作: {}", 
            userId, operation, e);
        
        // スタックトレースの一部だけを記録
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            logger.debug("エラー発生箇所: {}", stackTrace[0]);
        }
    }
}
```

## ベストプラクティス

1. **早期に失敗する**: エラーは発生箇所の近くで検出
2. **意味のあるメッセージ**: コンテキスト情報を含める
3. **適切な例外の選択**: 標準例外を活用し、必要に応じてカスタム例外を作成
4. **ログレベルの使い分け**: ERROR、WARN、INFO、DEBUGを適切に
5. **本番環境での配慮**: センシティブな情報を含めない

## 関連項目

- [debugging-techniques.md](debugging-techniques.md)
- 例外処理のベストプラクティス
- ログ設計ガイド
- パフォーマンス分析