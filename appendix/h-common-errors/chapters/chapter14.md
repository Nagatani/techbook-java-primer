# 第14章 ファイルI/O：よくあるエラーと対処法

書籍『Java入門』第14章「ファイルI/O」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

ファイルI/O操作において開発者が遭遇しやすいエラーとその対処法を理解しておくことは、堅牢なアプリケーションを開発するために重要です。

## ファイルパスの問題

相対パスと絶対パスが混在し、ファイルが見つからないエラーになります。

### エラー例

```java
// 悪い例
File file = new File("data/config.txt");
if (!file.exists()) {
    IO.println("ファイルが見つかりません");
}
```

### エラーメッセージ

`FileNotFoundException`または`NoSuchFileException`が発生します。

### 対処法

確実なパス管理と存在チェックを実装します。

```java
// 良い例
Path configPath = Path.of("data", "config.txt");
Path absolutePath = configPath.toAbsolutePath();

IO.println("検索パス: " + absolutePath);

if (Files.exists(configPath)) {
    try {
        String content = Files.readString(configPath);
        IO.println("設定を読み込みました");
    } catch (IOException e) {
        System.err.println("ファイル読み込みエラー: " + e.getMessage());
    }
} else {
    System.err.println("設定ファイルが見つかりません: " + absolutePath);
    // デフォルト設定を作成
    createDefaultConfig(configPath);
}
```

## エンコーディングの問題

ファイルの読み書きで、文字化けや不正な文字のエラーが起こります。

### エラー例

```java
// 悪い例
FileReader reader = new FileReader("japanese.txt");
BufferedReader br = new BufferedReader(reader);
String line = br.readLine(); // 文字化けする可能性
```

### エラーメッセージ

`MalformedInputException`が発生するか、出力が文字化けします。

### 対処法

明示的にエンコーディングを指定します。

```java
// 良い例
try (BufferedReader reader = Files.newBufferedReader(
        Path.of("japanese.txt"), StandardCharsets.UTF_8)) {
    
    String line;
    while ((line = reader.readLine()) != null) {
        IO.println(line);
    }
} catch (IOException e) {
    System.err.println("ファイル読み込みエラー: " + e.getMessage());
}

// 書き込み時も同様
try (BufferedWriter writer = Files.newBufferedWriter(
        Path.of("output.txt"), StandardCharsets.UTF_8)) {
    writer.write("日本語のテキスト");
} catch (IOException e) {
    System.err.println("ファイル書き込みエラー: " + e.getMessage());
}
```

## ファイルロックとアクセス権限の問題

ファイルがほかのプロセスに使われているか、アクセス権限が足りません。

### エラー例

```java
// 問題のあるコード
try {
    Files.write(Path.of("C:/Windows/system.log"), "data".getBytes());
} catch (IOException e) {
    e.printStackTrace(); // 権限エラーの詳細が不明
}
```

### エラーメッセージ

`AccessDeniedException`または`FileSystemException`が発生します。

### 対処法

事前の権限チェックとエラーハンドリングを実装します。

```java
// 良い例
Path logFile = Path.of("logs", "application.log");

try {
    // ディレクトリの存在チェックと作成
    Files.createDirectories(logFile.getParent());
    
    // 書き込み権限の確認
    if (!Files.isWritable(logFile.getParent())) {
        throw new IOException("ログディレクトリに書き込み権限がありません");
    }
    
    // ファイルロックを考慮した書き込み
    try (FileChannel channel = FileChannel.open(logFile, 
            StandardOpenOption.CREATE, 
            StandardOpenOption.APPEND)) {
        
        try (FileLock lock = channel.tryLock()) {
            if (lock != null) {
                channel.write(ByteBuffer.wrap("ログデータ\n".getBytes()));
            } else {
                System.err.println("ファイルがロックされています");
            }
        }
    }
    
} catch (AccessDeniedException e) {
    System.err.println("アクセス権限エラー: " + e.getMessage());
} catch (IOException e) {
    System.err.println("ファイル操作エラー: " + e.getMessage());
}
```

## メモリ効率の問題

大きなファイルを一度にメモリに読み込んでいます。

### エラー例

```java
// 悪い例（大きなファイルでOutOfMemoryError）
String content = Files.readString(Path.of("large_file.txt"));
String[] lines = content.split("\n");
```

### エラーメッセージ

`OutOfMemoryError`が発生します。

### 対処法

ストリーミング処理やBufferedReaderを使用します。

```java
// 良い例1: ストリーミング処理
try (Stream<String> lines = Files.lines(Path.of("large_file.txt"))) {
    lines.filter(line -> line.contains("ERROR"))
         .forEach(IO::println);
} catch (IOException e) {
    System.err.println("ファイル読み込みエラー: " + e.getMessage());
}

// 良い例2: バッファリングによる効率的な処理
try (BufferedReader reader = Files.newBufferedReader(
        Path.of("large_file.txt"))) {
    
    String line;
    int lineCount = 0;
    while ((line = reader.readLine()) != null) {
        if (line.contains("ERROR")) {
            IO.println("Line " + lineCount + ": " + line);
        }
        lineCount++;
        
        // 進捗表示（10000行ごと）
        if (lineCount % 10000 == 0) {
            IO.println("処理済み: " + lineCount + "行");
        }
    }
} catch (IOException e) {
    System.err.println("ファイル読み込みエラー: " + e.getMessage());
}
```

## クロスプラットフォーム対応の問題

特定のOSに固有のパス区切り文字やファイル名の制限に依存しています。

### エラー例

```java
// 悪い例
File file = new File("data\\config\\settings.txt"); // Windows固有
String path = "/home/user/data.txt"; // Unix固有
```

### 対処法

`java.nio.file.Path`とシステムプロパティを活用します。

```java
// 良い例
Path dataDir = Path.of(System.getProperty("user.home"), "myapp", "data");
Path configFile = dataDir.resolve("config.properties");

// ディレクトリの作成
try {
    Files.createDirectories(dataDir);
    
    // プラットフォーム固有の区切り文字を自動処理
    IO.println("設定ファイル: " + configFile.toAbsolutePath());
    
    // 無効な文字のチェック
    if (isValidFileName(configFile.getFileName().toString())) {
        // ファイル操作
        if (Files.notExists(configFile)) {
            Files.createFile(configFile);
        }
    }
    
} catch (IOException e) {
    System.err.println("ファイル操作エラー: " + e.getMessage());
}

private static boolean isValidFileName(String fileName) {
    // 各OS共通の無効文字をチェック
    String invalidChars = "<>:\"/\\|?*";
    for (char c : invalidChars.toCharArray()) {
        if (fileName.indexOf(c) >= 0) {
            return false;
        }
    }
    return true;
}
```

## 並行ファイルアクセスの問題

複数のスレッドから同時に同じファイルにアクセスしています。

### エラー例

```java
// 悪い例
public void appendLog(String message) {
    try (FileWriter writer = new FileWriter("app.log", true)) {
        writer.write(message + "\n");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
```

### 対処法

必要な同期化とファイルロックを実装します。
`synchronized`は、複数のスレッドが同じ処理を同時に実行しないようにするキーワードで、第15章「マルチスレッドプログラミング」で詳しく学びます。

```java
// 良い例
public class ThreadSafeFileLogger {
    private final Path logFile;
    private final Object lock = new Object();
    
    public ThreadSafeFileLogger(String filename) {
        this.logFile = Path.of(filename);
    }
    
    public void appendLog(String message) {
        synchronized (lock) {
            try (FileChannel channel = FileChannel.open(logFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                
                String timestampedMessage = LocalDateTime.now() + ": " + message + "\n";
                channel.write(ByteBuffer.wrap(timestampedMessage.getBytes()));
                
            } catch (IOException e) {
                System.err.println("ログ書き込みエラー: " + e.getMessage());
            }
        }
    }
}
```

## 一時ファイルの管理の問題

一時ファイルを削除し忘れ、ディスク容量を浪費します。

### エラー例

```java
// 悪い例
File tempFile = new File("temp_" + System.currentTimeMillis() + ".tmp");
// 処理後にファイルが残る
```

### 対処法

体系的な一時ファイル管理を実装します。

```java
// 良い例
Path tempFile = null;
try {
    tempFile = Files.createTempFile("myapp_", ".tmp");
    IO.println("一時ファイル: " + tempFile);
    
    // 一時ファイルでの処理
    Files.writeString(tempFile, "一時的なデータ");
    
    // 処理の実行
    processTemporaryFile(tempFile);
    
} catch (IOException e) {
    System.err.println("一時ファイル処理エラー: " + e.getMessage());
} finally {
    // 確実にクリーンアップ
    if (tempFile != null) {
        try {
            Files.deleteIfExists(tempFile);
            IO.println("一時ファイルを削除しました");
        } catch (IOException e) {
            System.err.println("一時ファイル削除エラー: " + e.getMessage());
        }
    }
}

// JVMシャットダウン時の自動削除
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
    try {
        Files.deleteIfExists(tempFile);
    } catch (IOException e) {
        // シャットダウン時なので、エラーログのみ
        System.err.println("シャットダウン時のファイル削除エラー: " + e.getMessage());
    }
}));
```

## デバッグのヒント

1. ファイルパスの確認

   ```java
   Path path = Path.of("config.txt");
   IO.println("現在のディレクトリ: " + System.getProperty("user.dir"));
   IO.println("絶対パス: " + path.toAbsolutePath());
   IO.println("ファイル存在: " + Files.exists(path));
   ```

2. ファイル属性の確認

   ```java
   if (Files.exists(path)) {
       IO.println("読み取り可能: " + Files.isReadable(path));
       IO.println("書き込み可能: " + Files.isWritable(path));
       IO.println("ファイルサイズ: " + Files.size(path));
       IO.println("最終更新: " + Files.getLastModifiedTime(path));
   }
   ```

3. 例外の詳細情報の活用

   ```java
   try {
       Files.readString(path);
   } catch (NoSuchFileException e) {
       System.err.println("ファイルが見つかりません: " + e.getFile());
   } catch (AccessDeniedException e) {
       System.err.println("アクセス権限エラー: " + e.getFile());
   } catch (IOException e) {
       System.err.println("I/Oエラー: " + e.getClass().getSimpleName() + " - " + e.getMessage());
   }
   ```

これらの対処法を理解し実践することで、より信頼性の高いファイルI/O処理を実装できるようになります。
