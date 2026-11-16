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

### 症状

`File.exists()`は例外をスローしないので、このコードは「ファイルが見つかりません」と表示するだけです。
同じパスのファイルを`new FileReader("data/config.txt")`で開くと`FileNotFoundException`、`Files.readString(Path.of("data/config.txt"))`で読むと`NoSuchFileException`が発生します。
相対パスは、プログラムを実行したときの作業ディレクトリ（`System.getProperty("user.dir")`）が基準になります。IDEから実行したときとコマンドラインから実行したときで、作業ディレクトリが変わることがあります。

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

ファイルの文字コードと、読むときに使う文字コードが食い違い、文字化けや不正な文字のエラーが起こります。

Java 18からは、文字コードを指定しないときの既定がOSによらずUTF-8になりました（JEP 400）。
そのため、Windowsのツール（古いメモ帳やExcelのCSV出力など）で作ったShift_JISのファイルを、文字コードを指定せずに読むと問題になります。

### エラー例

```java
// 悪い例：Shift_JISのファイルを既定（UTF-8）で読む
try (BufferedReader br = new BufferedReader(new FileReader("japanese.txt"))) {
    IO.println(br.readLine());
}
```

### 症状

「こんにちは」をShift_JISで保存したファイルで試すと、例外にはならず、文字化けした文字列が表示されます。

```
����ɂ���
```

`FileReader`は、解釈できないバイト列を置換文字（U+FFFD）に置き換えて読み進めます。
一方、`Files.newBufferedReader`や`Files.readString`は、解釈できないバイト列があると`MalformedInputException`をスローします。

```
java.nio.charset.MalformedInputException: Input length = 1
```

`StandardCharsets.UTF_8`を指定しても、Java 18以降では既定と同じなので解決しません。

### 対処法

ファイルが実際に使っている文字コードを指定して読みます。

```java
// 良い例：ファイルの文字コード（Shift_JIS）を指定して読む
Charset sjis = Charset.forName("Shift_JIS");  // Windowsの拡張文字を含むなら"windows-31j"
try (BufferedReader reader = Files.newBufferedReader(
        Path.of("japanese.txt"), sjis)) {

    String line;
    while ((line = reader.readLine()) != null) {
        IO.println(line);
    }
} catch (IOException e) {
    System.err.println("ファイル読み込みエラー: " + e.getMessage());
}

// 書き込むときは、読む側と同じ文字コードを明示する
try (BufferedWriter writer = Files.newBufferedWriter(
        Path.of("output.txt"), StandardCharsets.UTF_8)) {
    writer.write("日本語のテキスト");
} catch (IOException e) {
    System.err.println("ファイル書き込みエラー: " + e.getMessage());
}
```

ファイルの文字コードがわからないときは、エディターの表示（VS Codeなら右下のステータスバー）で確かめます。
自分のプログラムで新しく作るファイルは、UTF-8にそろえるのが安全です。

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

`tryLock()`は、ほかのプロセスがロックしているときに`null`を返します。
同じJVMの中ですでにロックしている範囲を重ねてロックしようとすると、`null`ではなく`OverlappingFileLockException`がスローされます。
同じプログラムの複数のスレッドから書き込むときは、後の「並行ファイルアクセスの問題」のように`synchronized`で守ります。

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
Path tempFile = Path.of("temp_" + System.currentTimeMillis() + ".tmp");
Files.writeString(tempFile, "一時的なデータ");
processTemporaryFile(tempFile);
// 削除していないので、処理後もファイルが残る
// processTemporaryFileで例外が発生した場合も残る
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
```

プログラムが終了するまで一時ファイルを使い続ける場合は、作った直後に`deleteOnExit()`で、JVMが正常に終了したときに削除するよう登録する方法もあります。

```java
Path workFile = Files.createTempFile("myapp_", ".tmp");
workFile.toFile().deleteOnExit();  // JVMの正常終了時に削除される
```

`deleteOnExit()`は、JVMが強制終了されたときには削除しません。登録した情報はJVMが終了するまでメモリに残るので、一時ファイルを大量に作る処理では、使い終わるたびに`finally`で削除します。

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
