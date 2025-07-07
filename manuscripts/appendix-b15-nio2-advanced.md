# 付録B.15: NIO.2の高度な機能とリアクティブI/O

## 概要

本付録では、Java 7で導入されたNIO.2（New I/O 2）の高度な機能について詳細に解説します。ファイルシステムの監視、非同期I/O、メモリマップドファイルなど、高性能なI/O処理を実現するための技術を学びます。

**対象読者**: 基本的なファイルI/Oを理解し、高度なI/O処理に興味がある開発者  
**前提知識**: 第15章「ファイルI/Oと外部データ連携」の内容、基本的なI/O概念  
**関連章**: 第15章、第16章（マルチスレッド）

## なぜ高度なNIO.2技術が重要なのか

### 実際のI/O性能問題と解決

**問題1: 大量ファイル処理での性能不足**
```java
// 従来の同期I/O：処理時間が線形増加
public class TraditionalFileProcessor {
    public void processLogFiles(List<Path> logFiles) {
        for (Path file : logFiles) {
            try {
                String content = Files.readString(file); // ブロッキング
                analyzeLog(content);  // CPUバウンドな処理
                generateReport(file); // さらなるI/O
            } catch (IOException e) {
                // エラーハンドリング
            }
        }
    }
}

// 問題：
// - 1000個のファイル処理に1時間かかる
// - CPUとI/Oが直列実行されリソース使用効率が悪い
// - メモリ使用量が大きなファイルサイズに依存
```
**実際の影響**: バッチ処理の実行時間延長、運用時間帯への影響

**問題2: リアルタイム監視の実装困難**
```java
// ポーリングベースの監視：非効率
public class PollingFileMonitor {
    public void monitorDirectory(Path directory) {
        while (true) {
            try {
                checkForChanges(directory);
                Thread.sleep(1000); // 1秒間隔でポーリング
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

// 問題：
// - CPU使用率の無駄
// - 変更検知の遅延（最大1秒）
// - ファイル数に比例した処理時間
```
**影響**: リアルタイム性の欠如、システムリソースの浪費

### ビジネスへの深刻な影響

**実際の障害事例:**
- **金融取引システム**: 大量ログファイル処理で夜間バッチが朝まで完了せず
- **監視システム**: ファイル変更検知の遅延で障害対応が30分遅れ
- **データ分析基盤**: 大容量ファイル処理でメモリ不足、サービス停止

**性能問題によるコスト:**
- **処理時間**: 従来I/Oで10時間 → NIO.2で2時間（5倍高速化）
- **リソース効率**: CPU使用率20% → 80%（並列I/O効果）
- **メモリ使用量**: 2GB → 200MB（メモリマップドファイル効果）

**ビジネス価値向上:**
- **リアルタイム処理**: ファイル監視の即座な反応でビジネス機会損失を防止
- **運用効率**: バッチ処理時間短縮により運用時間帯への影響を回避
- **コスト削減**: リソース効率向上によりインフラコスト30%削減

**具体的な改善効果:**
- **ログ解析システム**: 24時間 → 4時間でレポート生成完了
- **ファイル同期サービス**: 変更検知遅延 5秒 → 50ms（100倍高速化）
- **バックアップシステム**: メモリ使用量90%削減で大容量対応可能

---

## WatchServiceによるファイル監視

### 基本的なファイル監視

```java
import java.nio.file.*;
import java.nio.file.attribute.*;
import static java.nio.file.StandardWatchEventKinds.*;

public class FileWatcher {
    private final WatchService watchService;
    private final Map<WatchKey, Path> keyMap = new HashMap<>();
    
    public FileWatcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
    }
    
    public void watchDirectory(Path directory) throws IOException {
        // ディレクトリを監視対象に登録
        WatchKey key = directory.register(
            watchService,
            ENTRY_CREATE,
            ENTRY_DELETE,
            ENTRY_MODIFY
        );
        keyMap.put(key, directory);
    }
    
    public void processEvents() {
        while (true) {
            WatchKey key;
            try {
                // イベントを待機（ブロッキング）
                key = watchService.take();
            } catch (InterruptedException e) {
                return;
            }
            
            Path dir = keyMap.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!");
                continue;
            }
            
            // イベントを処理
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                
                // オーバーフローイベントの処理
                if (kind == OVERFLOW) {
                    continue;
                }
                
                // ファイル名を取得
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                Path child = dir.resolve(filename);
                
                // イベントタイプに応じた処理
                System.out.printf("%s: %s%n", event.kind().name(), child);
                
                // 新しいディレクトリが作成された場合は監視対象に追加
                if (kind == ENTRY_CREATE) {
                    try {
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            watchDirectory(child);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            
            // キーをリセット
            boolean valid = key.reset();
            if (!valid) {
                keyMap.remove(key);
                
                // すべてのディレクトリがアクセス不可になったら終了
                if (keyMap.isEmpty()) {
                    break;
                }
            }
        }
    }
}
```

### 高度なファイル監視パターン

```java
public class AdvancedFileWatcher {
    private final WatchService watchService;
    private final ExecutorService executor;
    private final Map<String, FileProcessor> processors;
    
    public AdvancedFileWatcher() throws IOException {
        this.watchService = FileSystems.getDefault().newWatchService();
        this.executor = Executors.newCachedThreadPool();
        this.processors = new ConcurrentHashMap<>();
        
        // ファイルタイプ別のプロセッサを登録
        registerProcessor("*.log", new LogFileProcessor());
        registerProcessor("*.csv", new CsvFileProcessor());
        registerProcessor("*.json", new JsonFileProcessor());
    }
    
    public void registerProcessor(String pattern, FileProcessor processor) {
        processors.put(pattern, processor);
    }
    
    public void watchWithDebounce(Path directory, Duration delay) throws IOException {
        Map<Path, ScheduledFuture<?>> pendingEvents = new ConcurrentHashMap<>();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        
        directory.register(watchService, ENTRY_MODIFY);
        
        while (true) {
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                return;
            }
            
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.kind() == OVERFLOW) continue;
                
                Path changed = directory.resolve((Path) event.context());
                
                // 既存のイベントをキャンセル
                ScheduledFuture<?> existing = pendingEvents.get(changed);
                if (existing != null) {
                    existing.cancel(false);
                }
                
                // デバウンス処理
                ScheduledFuture<?> future = scheduler.schedule(() -> {
                    processFile(changed);
                    pendingEvents.remove(changed);
                }, delay.toMillis(), TimeUnit.MILLISECONDS);
                
                pendingEvents.put(changed, future);
            }
            
            key.reset();
        }
    }
    
    private void processFile(Path file) {
        String filename = file.getFileName().toString();
        
        processors.entrySet().stream()
            .filter(entry -> filename.matches(entry.getKey().replace("*", ".*")))
            .forEach(entry -> executor.submit(() -> 
                entry.getValue().process(file)
            ));
    }
}

interface FileProcessor {
    void process(Path file);
}
```

---

## 非同期ファイルI/O

### AsynchronousFileChannel

```java
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.*;

public class AsyncFileIO {
    
    public CompletableFuture<String> readFileAsync(Path path) {
        CompletableFuture<String> future = new CompletableFuture<>();
        
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                path, StandardOpenOption.READ
            );
            
            ByteBuffer buffer = ByteBuffer.allocate((int) Files.size(path));
            
            channel.read(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    attachment.flip();
                    String content = StandardCharsets.UTF_8.decode(attachment).toString();
                    future.complete(content);
                    closeQuietly(channel);
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                    closeQuietly(channel);
                }
            });
            
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    public CompletableFuture<Void> writeFileAsync(Path path, String content) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        try {
            AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                path,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            );
            
            ByteBuffer buffer = StandardCharsets.UTF_8.encode(content);
            
            channel.write(buffer, 0, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if (attachment.hasRemaining()) {
                        // 残りのデータを書き込む
                        channel.write(attachment, result, attachment, this);
                    } else {
                        future.complete(null);
                        closeQuietly(channel);
                    }
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    future.completeExceptionally(exc);
                    closeQuietly(channel);
                }
            });
            
        } catch (IOException e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    private void closeQuietly(AsynchronousFileChannel channel) {
        try {
            channel.close();
        } catch (IOException ignored) {
        }
    }
}
```

### 並列ファイル処理

```java
public class ParallelFileProcessor {
    private final ExecutorService executor;
    
    public ParallelFileProcessor(int parallelism) {
        this.executor = new ForkJoinPool(parallelism);
    }
    
    public CompletableFuture<Map<Path, String>> processFiles(List<Path> files) {
        List<CompletableFuture<Pair<Path, String>>> futures = files.stream()
            .map(file -> CompletableFuture
                .supplyAsync(() -> processFile(file), executor)
                .thenApply(result -> new Pair<>(file, result))
            )
            .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toMap(
                    Pair::getKey,
                    Pair::getValue
                ))
            );
    }
    
    private String processFile(Path file) {
        try {
            // ファイル処理ロジック
            return Files.readString(file);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    static class Pair<K, V> {
        private final K key;
        private final V value;
        
        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
        K getKey() { return key; }
        V getValue() { return value; }
    }
}
```

---

## メモリマップドファイル

### 基本的な使用方法

```java
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MemoryMappedFiles {
    
    public void readLargeFile(Path path) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r");
             FileChannel channel = file.getChannel()) {
            
            long fileSize = channel.size();
            
            // ファイル全体をメモリにマップ
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                fileSize
            );
            
            // 高速なランダムアクセス
            while (buffer.hasRemaining()) {
                byte b = buffer.get();
                // 処理...
            }
        }
    }
    
    public void modifyLargeFile(Path path) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw");
             FileChannel channel = file.getChannel()) {
            
            long fileSize = channel.size();
            
            // 読み書き可能モードでマップ
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_WRITE,
                0,
                fileSize
            );
            
            // インプレースでの変更
            for (int i = 0; i < buffer.capacity(); i++) {
                byte b = buffer.get(i);
                buffer.put(i, (byte) (b ^ 0xFF)); // ビット反転
            }
            
            // 変更をディスクに強制的に書き込み
            buffer.force();
        }
    }
}
```

### 高性能なファイル処理

```java
public class HighPerformanceFileProcessor {
    private static final int CHUNK_SIZE = 1024 * 1024 * 100; // 100MB
    
    public long countLines(Path largePath) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(largePath.toFile(), "r");
             FileChannel channel = file.getChannel()) {
            
            long fileSize = channel.size();
            long position = 0;
            long lineCount = 0;
            
            while (position < fileSize) {
                long remainingSize = fileSize - position;
                long mappingSize = Math.min(CHUNK_SIZE, remainingSize);
                
                MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    position,
                    mappingSize
                );
                
                lineCount += countLinesInBuffer(buffer);
                position += mappingSize;
                
                // メモリマップを解放（ガベージコレクションを促進）
                cleanBuffer(buffer);
            }
            
            return lineCount;
        }
    }
    
    private long countLinesInBuffer(ByteBuffer buffer) {
        long count = 0;
        while (buffer.hasRemaining()) {
            if (buffer.get() == '\n') {
                count++;
            }
        }
        return count;
    }
    
    private void cleanBuffer(MappedByteBuffer buffer) {
        // sun.misc.Cleanerを使用した即座の解放（非推奨だが効果的）
        try {
            Method cleanerMethod = buffer.getClass().getMethod("cleaner");
            cleanerMethod.setAccessible(true);
            Object cleaner = cleanerMethod.invoke(buffer);
            Method cleanMethod = cleaner.getClass().getMethod("clean");
            cleanMethod.setAccessible(true);
            cleanMethod.invoke(cleaner);
        } catch (Exception e) {
            // フォールバック：ガベージコレクションに任せる
        }
    }
}
```

### 共有メモリとしての使用

```java
public class SharedMemoryFile {
    private final Path filePath;
    private final int size;
    
    public SharedMemoryFile(Path filePath, int size) throws IOException {
        this.filePath = filePath;
        this.size = size;
        
        // ファイルを指定サイズで初期化
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "rw")) {
            file.setLength(size);
        }
    }
    
    public void writeData(int offset, byte[] data) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "rw");
             FileChannel channel = file.getChannel()) {
            
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_WRITE,
                offset,
                data.length
            );
            
            buffer.put(data);
            buffer.force();
        }
    }
    
    public byte[] readData(int offset, int length) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r");
             FileChannel channel = file.getChannel()) {
            
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                offset,
                length
            );
            
            byte[] data = new byte[length];
            buffer.get(data);
            return data;
        }
    }
}
```

---

## リアクティブI/O

### Flow APIを使用したリアクティブストリーム

```java
import java.util.concurrent.Flow.*;
import java.util.concurrent.SubmissionPublisher;

public class ReactiveFileReader implements Publisher<String> {
    private final Path path;
    private final int bufferSize;
    
    public ReactiveFileReader(Path path, int bufferSize) {
        this.path = path;
        this.bufferSize = bufferSize;
    }
    
    @Override
    public void subscribe(Subscriber<? super String> subscriber) {
        subscriber.onSubscribe(new FileSubscription(subscriber));
    }
    
    private class FileSubscription implements Subscription {
        private final Subscriber<? super String> subscriber;
        private final AsynchronousFileChannel channel;
        private long position = 0;
        private volatile boolean cancelled = false;
        
        FileSubscription(Subscriber<? super String> subscriber) {
            this.subscriber = subscriber;
            try {
                this.channel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
            } catch (IOException e) {
                subscriber.onError(e);
                throw new RuntimeException(e);
            }
        }
        
        @Override
        public void request(long n) {
            if (cancelled) return;
            
            for (long i = 0; i < n && !cancelled; i++) {
                readNextChunk();
            }
        }
        
        private void readNextChunk() {
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            
            channel.read(buffer, position, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer bytesRead, ByteBuffer attachment) {
                    if (bytesRead == -1) {
                        subscriber.onComplete();
                        closeChannel();
                        return;
                    }
                    
                    position += bytesRead;
                    attachment.flip();
                    String chunk = StandardCharsets.UTF_8.decode(attachment).toString();
                    
                    if (!cancelled) {
                        subscriber.onNext(chunk);
                    }
                }
                
                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    subscriber.onError(exc);
                    closeChannel();
                }
            });
        }
        
        @Override
        public void cancel() {
            cancelled = true;
            closeChannel();
        }
        
        private void closeChannel() {
            try {
                channel.close();
            } catch (IOException ignored) {
            }
        }
    }
}

// 使用例
public class ReactiveFileProcessor {
    public void processFile(Path path) {
        ReactiveFileReader reader = new ReactiveFileReader(path, 8192);
        
        reader.subscribe(new Subscriber<String>() {
            private Subscription subscription;
            private final StringBuilder content = new StringBuilder();
            
            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                subscription.request(1); // 最初のチャンクを要求
            }
            
            @Override
            public void onNext(String chunk) {
                content.append(chunk);
                
                // バックプレッシャー：処理が追いつく範囲で次を要求
                if (content.length() < 1_000_000) {
                    subscription.request(1);
                } else {
                    // 処理を一時停止
                    processContent(content.toString());
                    content.setLength(0);
                    subscription.request(1);
                }
            }
            
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }
            
            @Override
            public void onComplete() {
                if (content.length() > 0) {
                    processContent(content.toString());
                }
                System.out.println("File processing completed");
            }
        });
    }
    
    private void processContent(String content) {
        // コンテンツの処理
    }
}
```

---

## パフォーマンス最適化

### I/Oパフォーマンスの測定

```java
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class IOBenchmark {
    
    @Param({"1048576", "10485760", "104857600"}) // 1MB, 10MB, 100MB
    private int fileSize;
    
    private Path tempFile;
    private byte[] data;
    
    @Setup
    public void setup() throws IOException {
        tempFile = Files.createTempFile("benchmark", ".dat");
        data = new byte[fileSize];
        new Random().nextBytes(data);
        Files.write(tempFile, data);
    }
    
    @TearDown
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }
    
    @Benchmark
    public byte[] traditionalIO() throws IOException {
        try (FileInputStream fis = new FileInputStream(tempFile.toFile())) {
            byte[] buffer = new byte[fileSize];
            int totalRead = 0;
            while (totalRead < fileSize) {
                int read = fis.read(buffer, totalRead, fileSize - totalRead);
                if (read == -1) break;
                totalRead += read;
            }
            return buffer;
        }
    }
    
    @Benchmark
    public byte[] nioBufferedRead() throws IOException {
        try (FileChannel channel = FileChannel.open(tempFile, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocateDirect(fileSize);
            while (buffer.hasRemaining()) {
                channel.read(buffer);
            }
            buffer.flip();
            byte[] result = new byte[fileSize];
            buffer.get(result);
            return result;
        }
    }
    
    @Benchmark
    public byte[] memoryMappedRead() throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(tempFile.toFile(), "r");
             FileChannel channel = file.getChannel()) {
            
            MappedByteBuffer buffer = channel.map(
                FileChannel.MapMode.READ_ONLY,
                0,
                fileSize
            );
            
            byte[] result = new byte[fileSize];
            buffer.get(result);
            return result;
        }
    }
}
```

---

## まとめ

NIO.2の高度な機能を活用することで：

1. **リアルタイム監視**: WatchServiceによる効率的なファイルシステム監視
2. **非同期処理**: ノンブロッキングI/Oによる高いスループット
3. **メモリ効率**: メモリマップドファイルによる大容量ファイル処理
4. **リアクティブ処理**: バックプレッシャーを考慮したストリーム処理
5. **パフォーマンス**: 用途に応じた最適なI/O手法の選択

これらの技術は、大規模なデータ処理やリアルタイムシステムの開発において重要な役割を果たします。ただし、複雑性も増すため、要件に応じて適切な技術を選択することが重要です。

## 実践的なサンプルコード

本付録で解説した高度なNIO.2機能の実践的なサンプルコードは、`/appendix/nio2-advanced/`ディレクトリに収録されています。ファイル監視、非同期I/O、メモリマップドファイルなど、各機能の具体的な実装例を参照することで、より深い理解が得られます。