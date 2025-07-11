# 第15章 チャレンジ課題：ファイル入出力

## 概要
ファイルI/Oを使った高度なシステムの構築に挑戦します。分散ファイルシステム、ファイルベースのデータベース、高性能ログシステムなど、実践的なファイル処理システムを実装します。

## 課題一覧

### 課題1: ファイルベースの簡易データベース
`FileDatabase.java`を作成し、以下を実装してください：

トランザクション機能を持つファイルベースのKey-Valueデータベースを構築します。

```java
// データベースエンジン
public class FileDatabase implements AutoCloseable {
    private final Path dataDirectory;
    private final Path walFile; // Write-Ahead Log
    private final Map<String, String> cache = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public FileDatabase(String directory) throws IOException {
        this.dataDirectory = Path.of(directory);
        this.walFile = dataDirectory.resolve("wal.log");
        Files.createDirectories(dataDirectory);
        loadData();
    }
    
    // トランザクション開始
    public Transaction beginTransaction() {
        return new Transaction(this);
    }
    
    // 値の取得
    public Optional<String> get(String key) {
        lock.readLock().lock();
        try {
            String cached = cache.get(key);
            if (cached != null) {
                return Optional.of(cached);
            }
            
            Path keyFile = getKeyPath(key);
            if (Files.exists(keyFile)) {
                String value = Files.readString(keyFile);
                cache.put(key, value);
                return Optional.of(value);
            }
            
            return Optional.empty();
        } catch (IOException e) {
            throw new DatabaseException("Failed to read key: " + key, e);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    // トランザクションクラス
    public static class Transaction {
        private final FileDatabase db;
        private final Map<String, String> changes = new HashMap<>();
        private final List<String> deletions = new ArrayList<>();
        private boolean committed = false;
        
        private Transaction(FileDatabase db) {
            this.db = db;
        }
        
        public void put(String key, String value) {
            if (committed) throw new IllegalStateException("Transaction already committed");
            changes.put(key, value);
            deletions.remove(key);
        }
        
        public void delete(String key) {
            if (committed) throw new IllegalStateException("Transaction already committed");
            deletions.add(key);
            changes.remove(key);
        }
        
        public void commit() throws IOException {
            if (committed) return;
            
            db.lock.writeLock().lock();
            try {
                // Write-Ahead Logに書き込み
                writeToWAL();
                
                // 実際のファイルに反映
                for (Map.Entry<String, String> entry : changes.entrySet()) {
                    Path keyFile = db.getKeyPath(entry.getKey());
                    Files.writeString(keyFile, entry.getValue(),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.SYNC);
                    db.cache.put(entry.getKey(), entry.getValue());
                }
                
                for (String key : deletions) {
                    Path keyFile = db.getKeyPath(key);
                    Files.deleteIfExists(keyFile);
                    db.cache.remove(key);
                }
                
                // WALをクリア
                clearWAL();
                committed = true;
                
            } finally {
                db.lock.writeLock().unlock();
            }
        }
        
        public void rollback() {
            changes.clear();
            deletions.clear();
            committed = true;
        }
    }
    
    // コンパクション機能
    public void compact() throws IOException {
        lock.writeLock().lock();
        try {
            // 断片化したファイルを整理
            Map<String, String> allData = new HashMap<>();
            
            try (DirectoryStream<Path> stream = 
                    Files.newDirectoryStream(dataDirectory, "*.dat")) {
                for (Path file : stream) {
                    String key = file.getFileName().toString()
                        .replace(".dat", "");
                    String value = Files.readString(file);
                    allData.put(key, value);
                }
            }
            
            // 新しいディレクトリに書き込み
            Path tempDir = dataDirectory.resolveSibling("temp_compact");
            Files.createDirectory(tempDir);
            
            for (Map.Entry<String, String> entry : allData.entrySet()) {
                Path newFile = tempDir.resolve(entry.getKey() + ".dat");
                Files.writeString(newFile, entry.getValue());
            }
            
            // ディレクトリを入れ替え
            Path oldDir = dataDirectory.resolveSibling("old_data");
            Files.move(dataDirectory, oldDir);
            Files.move(tempDir, dataDirectory);
            deleteDirectory(oldDir);
            
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### 課題2: 高性能ログシステム
`HighPerformanceLogger.java`を作成し、以下を実装してください：

```java
public class HighPerformanceLogger implements AutoCloseable {
    private final Path logDirectory;
    private final long maxFileSize;
    private final int maxFiles;
    private final BlockingQueue<LogEntry> queue;
    private final Thread writerThread;
    private volatile boolean running = true;
    private BufferedWriter currentWriter;
    private long currentFileSize = 0;
    private int fileIndex = 0;
    
    public HighPerformanceLogger(String directory, long maxFileSize, int maxFiles) 
            throws IOException {
        this.logDirectory = Path.of(directory);
        this.maxFileSize = maxFileSize;
        this.maxFiles = maxFiles;
        this.queue = new LinkedBlockingQueue<>(10000);
        
        Files.createDirectories(logDirectory);
        openNewFile();
        
        // バックグラウンドでログを書き込むスレッド
        this.writerThread = new Thread(this::writeLoop);
        this.writerThread.setDaemon(true);
        this.writerThread.start();
    }
    
    // 非ブロッキングログ
    public void log(LogLevel level, String message) {
        LogEntry entry = new LogEntry(Instant.now(), level, 
            Thread.currentThread().getName(), message);
        
        if (!queue.offer(entry)) {
            // キューが満杯の場合の処理
            handleQueueFull();
        }
    }
    
    private void writeLoop() {
        List<LogEntry> batch = new ArrayList<>(100);
        
        while (running || !queue.isEmpty()) {
            try {
                // バッチ処理で効率化
                LogEntry entry = queue.poll(100, TimeUnit.MILLISECONDS);
                if (entry != null) {
                    batch.add(entry);
                    queue.drainTo(batch, 99);
                }
                
                if (!batch.isEmpty()) {
                    writeBatch(batch);
                    batch.clear();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private synchronized void writeBatch(List<LogEntry> entries) 
            throws IOException {
        for (LogEntry entry : entries) {
            String line = formatLogEntry(entry);
            currentWriter.write(line);
            currentWriter.newLine();
            currentFileSize += line.length() + 1;
            
            if (currentFileSize >= maxFileSize) {
                rotateFile();
            }
        }
        currentWriter.flush();
    }
    
    // ログローテーション
    private void rotateFile() throws IOException {
        currentWriter.close();
        
        // 古いファイルを削除
        if (fileIndex >= maxFiles) {
            Path oldestFile = logDirectory.resolve(
                String.format("app-%d.log", fileIndex - maxFiles));
            Files.deleteIfExists(oldestFile);
        }
        
        openNewFile();
    }
    
    // 圧縮機能
    public void compressOldLogs() throws IOException {
        try (DirectoryStream<Path> stream = 
                Files.newDirectoryStream(logDirectory, "*.log")) {
            
            LocalDateTime threshold = LocalDateTime.now().minusDays(1);
            
            for (Path logFile : stream) {
                BasicFileAttributes attrs = Files.readAttributes(
                    logFile, BasicFileAttributes.class);
                
                LocalDateTime lastModified = LocalDateTime.ofInstant(
                    attrs.lastModifiedTime().toInstant(), 
                    ZoneId.systemDefault());
                
                if (lastModified.isBefore(threshold)) {
                    compressFile(logFile);
                }
            }
        }
    }
    
    private void compressFile(Path file) throws IOException {
        Path gzFile = file.resolveSibling(file.getFileName() + ".gz");
        
        try (InputStream in = Files.newInputStream(file);
             OutputStream out = Files.newOutputStream(gzFile);
             GZIPOutputStream gzOut = new GZIPOutputStream(out)) {
            
            in.transferTo(gzOut);
        }
        
        Files.delete(file);
    }
}

// ログエントリ
record LogEntry(
    Instant timestamp,
    LogLevel level,
    String threadName,
    String message
) {}

enum LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR
}
```

## 実装のヒント

### ファイルロックとアトミック操作
```java
// アトミックな書き込み
Path tempFile = Files.createTempFile(directory, "temp", ".tmp");
Files.write(tempFile, data);
Files.move(tempFile, targetFile, StandardCopyOption.ATOMIC_MOVE);

// ファイルロック
try (FileChannel channel = FileChannel.open(file, 
        StandardOpenOption.WRITE, StandardOpenOption.SYNC);
     FileLock lock = channel.tryLock()) {
    
    if (lock != null) {
        // 排他的アクセス
    } else {
        // ロック取得失敗
    }
}
```

### メモリマップファイルの活用
```java
// 高速な読み書き
try (RandomAccessFile file = new RandomAccessFile("data.bin", "rw");
     FileChannel channel = file.getChannel()) {
    
    MappedByteBuffer buffer = channel.map(
        FileChannel.MapMode.READ_WRITE, 0, file.length());
    
    // 直接メモリアクセス
    buffer.putInt(0, 42);
    buffer.force(); // ディスクに同期
}
```

### 非同期I/Oとコンプリーション
```java
AsynchronousFileChannel channel = AsynchronousFileChannel.open(
    path, StandardOpenOption.WRITE);

CompletionHandler<Integer, ByteBuffer> handler = 
    new CompletionHandler<Integer, ByteBuffer>() {
        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            // 書き込み完了処理
        }
        
        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            // エラー処理
        }
    };

ByteBuffer buffer = ByteBuffer.wrap(data);
channel.write(buffer, 0, buffer, handler);
```

## 提出前チェックリスト
- [ ] トランザクションが正しく動作する（ACID特性）
- [ ] 高負荷でもログが失われない
- [ ] ファイルロックが適切に使用されている
- [ ] パフォーマンステストを実施している

## 評価基準
- 実用的なファイルベースシステムが構築できているか
- 並行性とデータ整合性が保たれているか
- パフォーマンスが最適化されているか
- エラー処理とリカバリーが適切か

## ボーナス課題
時間に余裕がある場合は、以下の追加実装に挑戦してください：
- データベース：インデックス機能とクエリ最適化
- ログシステム：分散ログ集約とリアルタイム解析
- ファイル同期：複数ノード間でのファイル同期システム