# 第15章 発展課題：ファイル入出力

## 概要
基礎課題を完了した方向けの発展的な課題です。CSV/JSONファイルの処理、大容量ファイルの効率的な処理、ファイル監視、並行ファイルアクセスなど、より実践的なファイル操作を学びます。

## 課題一覧

### 課題1: CSVファイルの処理
`CSVProcessor.java`を作成し、以下を実装してください：

1. **CSVファイルの読み込み**
   ```java
   public class CSVReader {
       private final String delimiter;
       private final boolean hasHeader;
       
       public CSVReader(String delimiter, boolean hasHeader) {
           this.delimiter = delimiter;
           this.hasHeader = hasHeader;
       }
       
       public List<List<String>> readCSV(String filename) throws IOException {
           List<List<String>> records = new ArrayList<>();
           
           try (BufferedReader reader = Files.newBufferedReader(Path.of(filename))) {
               String line;
               boolean firstLine = true;
               
               while ((line = reader.readLine()) != null) {
                   if (firstLine && hasHeader) {
                       firstLine = false;
                       continue;
                   }
                   records.add(parseCSVLine(line));
               }
           }
           
           return records;
       }
       
       private List<String> parseCSVLine(String line) {
           // クォートを考慮したCSVパース処理
           List<String> fields = new ArrayList<>();
           StringBuilder field = new StringBuilder();
           boolean inQuotes = false;
           
           for (int i = 0; i < line.length(); i++) {
               char c = line.charAt(i);
               
               if (c == '"') {
                   inQuotes = !inQuotes;
               } else if (c == delimiter.charAt(0) && !inQuotes) {
                   fields.add(field.toString());
                   field = new StringBuilder();
               } else {
                   field.append(c);
               }
           }
           fields.add(field.toString());
           
           return fields;
       }
   }
   ```

2. **CSVファイルの書き込み**
   ```java
   public class CSVWriter implements AutoCloseable {
       private final BufferedWriter writer;
       private final String delimiter;
       
       public CSVWriter(String filename, String delimiter) throws IOException {
           this.writer = Files.newBufferedWriter(Path.of(filename));
           this.delimiter = delimiter;
       }
       
       public void writeHeader(List<String> headers) throws IOException {
           writeLine(headers);
       }
       
       public void writeLine(List<String> fields) throws IOException {
           String line = fields.stream()
               .map(this::escapeField)
               .collect(Collectors.joining(delimiter));
           writer.write(line);
           writer.newLine();
       }
       
       private String escapeField(String field) {
           if (field.contains(delimiter) || field.contains("\"") || 
               field.contains("\n")) {
               return "\"" + field.replace("\"", "\"\"") + "\"";
           }
           return field;
       }
       
       @Override
       public void close() throws IOException {
           writer.close();
       }
   }
   ```

### 課題2: 大容量ファイルの処理
`LargeFileProcessor.java`を作成し、以下を実装してください：

1. **ストリーミング処理**
   ```java
   public class LogAnalyzer {
       // 大容量ログファイルから特定のパターンを検索
       public long countErrors(String logFile) throws IOException {
           try (Stream<String> lines = Files.lines(Path.of(logFile))) {
               return lines
                   .filter(line -> line.contains("ERROR"))
                   .count();
           }
       }
       
       // 特定の期間のログを抽出
       public void extractLogs(String inputFile, String outputFile, 
               LocalDateTime startTime, LocalDateTime endTime) 
               throws IOException {
           
           try (Stream<String> lines = Files.lines(Path.of(inputFile));
                BufferedWriter writer = Files.newBufferedWriter(Path.of(outputFile))) {
               
               lines.filter(line -> {
                   LocalDateTime timestamp = parseTimestamp(line);
                   return timestamp != null && 
                          timestamp.isAfter(startTime) && 
                          timestamp.isBefore(endTime);
               })
               .forEach(line -> {
                   try {
                       writer.write(line);
                       writer.newLine();
                   } catch (IOException e) {
                       throw new UncheckedIOException(e);
                   }
               });
           }
       }
   }
   ```

2. **メモリマップドファイル**
   ```java
   public class BinaryFileProcessor {
       public void processBinaryFile(String filename) throws IOException {
           try (RandomAccessFile file = new RandomAccessFile(filename, "r");
                FileChannel channel = file.getChannel()) {
               
               MappedByteBuffer buffer = channel.map(
                   FileChannel.MapMode.READ_ONLY, 0, channel.size());
               
               // バイナリデータの処理
               while (buffer.hasRemaining()) {
                   // 例: 4バイトごとに整数として読み込み
                   if (buffer.remaining() >= 4) {
                       int value = buffer.getInt();
                       processValue(value);
                   }
               }
           }
       }
   }
   ```

### 課題3: ファイル監視システム
`FileWatcher.java`を作成し、以下を実装してください：

1. **ディレクトリ監視**
   ```java
   public class DirectoryWatcher {
       private final Path watchDirectory;
       private final Map<WatchKey, Path> keys = new HashMap<>();
       private WatchService watchService;
       
       public DirectoryWatcher(String directory) throws IOException {
           this.watchDirectory = Path.of(directory);
           this.watchService = FileSystems.getDefault().newWatchService();
           registerDirectory(watchDirectory);
       }
       
       private void registerDirectory(Path dir) throws IOException {
           WatchKey key = dir.register(watchService,
               StandardWatchEventKinds.ENTRY_CREATE,
               StandardWatchEventKinds.ENTRY_DELETE,
               StandardWatchEventKinds.ENTRY_MODIFY);
           keys.put(key, dir);
       }
       
       public void watch(Consumer<WatchEvent<?>> eventHandler) {
           while (true) {
               WatchKey key;
               try {
                   key = watchService.take();
               } catch (InterruptedException e) {
                   return;
               }
               
               Path dir = keys.get(key);
               if (dir == null) {
                   continue;
               }
               
               for (WatchEvent<?> event : key.pollEvents()) {
                   eventHandler.accept(event);
                   
                   // 新しいディレクトリが作成された場合は監視対象に追加
                   if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                       Path child = dir.resolve((Path) event.context());
                       if (Files.isDirectory(child)) {
                           try {
                               registerDirectory(child);
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   }
               }
               
               key.reset();
           }
       }
   }
   ```

## 実装のヒント

### 効率的なファイル処理
```java
// BufferedWriterでバッファリング
try (BufferedWriter writer = Files.newBufferedWriter(path)) {
    for (String line : largeDataSet) {
        writer.write(line);
        writer.newLine();
    }
}

// Streamの並列処理
long count = Files.lines(path)
    .parallel()
    .filter(line -> expensiveOperation(line))
    .count();
```

### ファイルロックの使用
```java
try (RandomAccessFile file = new RandomAccessFile("data.txt", "rw");
     FileChannel channel = file.getChannel();
     FileLock lock = channel.lock()) {
    
    // 排他的なファイルアクセス
    // 他のプロセスはこのファイルにアクセスできない
}
```

### 非同期ファイルI/O
```java
AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    Path.of("large.dat"), StandardOpenOption.READ);

ByteBuffer buffer = ByteBuffer.allocate(1024);
Future<Integer> result = channel.read(buffer, 0);

// 他の処理を実行...

Integer bytesRead = result.get(); // 結果を待つ
```

## 提出前チェックリスト
- [ ] CSVの特殊なケース（改行、クォート）を処理できる
- [ ] 大容量ファイルでもメモリ不足にならない
- [ ] ファイル監視が正しく動作する
- [ ] 並行アクセスを考慮している

## 評価基準
- 実践的なファイル処理が実装できているか
- パフォーマンスを考慮した設計になっているか
- エラーハンドリングが適切か
- コードの再利用性と拡張性