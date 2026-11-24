# NIO.2 高度な機能 - 実践的な例

このドキュメントでは、NIO.2の高度な機能を実際の場面でどう組み合わせるかを示します。
ここに載せたコードは構成の例です。
`AdvancedFileWatcher`、`ParallelFileProcessor`、`ReactiveFileReader`、`AsyncFileIO`はこのプロジェクトのクラスですが、`AlertService`、`DatabaseService`、`MulticastProcessor`などはプロジェクトに含まれていないため、そのままではコンパイルできません。
各機能の動きの詳細（OSによる違いを含む）は、`README.md`の対応する節で観察しています。

## 1. リアルタイムログ監視システム

### 問題
複数のサーバーにまたがる複数のログファイルを監視し、リアルタイムでエラーを検出し、管理者に警告を送信する。

### WatchServiceを使用した解決策
```java
public class LogMonitoringSystem {
    private final AdvancedFileWatcher watcher;
    private final AlertService alertService;
    
    public LogMonitoringSystem() throws IOException {
        this.watcher = new AdvancedFileWatcher();
        this.alertService = new AlertService();
        
        // 部分的な書き込みの処理を避けるためにデバウンスを設定
        watcher.setDebounceDelay(Duration.ofMillis(500));
        
        // ログファイル用のプロセッサーを登録
        watcher.registerProcessor("*.log", new LogProcessor());
    }
    
    class LogProcessor implements FileProcessor {
        @Override
        public void process(Path file, WatchEvent.Kind<?> eventType) {
            if (eventType == StandardWatchEventKinds.ENTRY_MODIFY) {
                // 新しいコンテンツのみを読み取る
                String newContent = readNewContent(file);
                
                // エラーをチェック
                if (newContent.contains("ERROR") || newContent.contains("FATAL")) {
                    alertService.sendAlert(file, extractErrors(newContent));
                }
            }
        }
    }
    
    public void startMonitoring(Path... directories) throws IOException {
        for (Path dir : directories) {
            watcher.watchDirectory(dir, true); // 再帰的
        }
        watcher.startWatching();
    }
}
```

`WatchService`が知らせるのは「変わった」ことだけで、どこが変わったかは知らせません。
新しく書かれた部分だけを読むには、ファイルごとに前回読んだ位置を覚えておき、`FileChannel`のその位置から読みます。
macOSでは変更の通知まで約2秒かかるので、「リアルタイム」の度合いはOSによって変わります（`README.md`の6）。
<!-- claim: watch-impl -->

## 2. 高性能ファイル処理パイプライン

### 問題
毎日数千のCSVファイル（各100MB以上）を処理し、データを抽出、変換してデータベースにロードする。

### 非同期I/Oを使用した解決策
```java
public class CSVProcessingPipeline {
    private final ParallelFileProcessor processor;
    private final DatabaseService database;
    
    public CSVProcessingPipeline() {
        // 読み書きの待ち時間があるため、CPUの数より多めのスレッドにする例。
        // 適切な数はディスクと処理の内容で変わるので、測って決める
        this.processor = new ParallelFileProcessor(
            Runtime.getRuntime().availableProcessors() * 2
        );
        this.database = new DatabaseService();
    }
    
    public CompletableFuture<ProcessingResult> processCsvFiles(List<Path> files) {
        return processor.processFilesInBatches(files, this::processSingleFile, 10)
            .thenApply(results -> {
                // 結果を集計
                int totalRecords = results.values().stream()
                    .mapToInt(r -> r.recordCount)
                    .sum();
                
                return new ProcessingResult(results.size(), totalRecords);
            });
    }
    
    private ProcessingResult processSingleFile(Path csvFile) {
        try {
            // 大きなCSVにはメモリマップドファイルを使用
            List<Record> records = readCsvWithMemoryMapping(csvFile);
            
            // データを変換（ファイルの単位ですでに並列なので、ここでは並列ストリームを使わない）
            List<TransformedRecord> transformed = records.stream()
                .map(this::transformRecord)
                .toList();
            
            // データベースへのバッチ挿入
            database.batchInsert(transformed);
            
            return new ProcessingResult(1, transformed.size());
        } catch (Exception e) {
            logger.error("処理に失敗しました: " + csvFile, e);
            return new ProcessingResult(0, 0);
        }
    }
}
```

## 3. 大規模ファイル検索エンジン

### 問題
ファイル全体をメモリにロードせずに、テラバイト規模のログファイルから特定のパターンを検索する。

### メモリマップドファイルを使用した解決策
```java
public class LargeFileSearchEngine {
    private final int CHUNK_SIZE = 100 * 1024 * 1024; // 100MBチャンク
    
    public List<SearchResult> searchPattern(Path file, String pattern) 
            throws IOException {
        List<SearchResult> results = new ArrayList<>();
        byte[] patternBytes = pattern.getBytes(StandardCharsets.UTF_8);
        
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            long fileSize = channel.size();
            long position = 0;
            
            while (position < fileSize) {
                // 境界をまたぐ一致を見つけるため、パターンの長さ-1だけ余分にマップする
                long mapSize = Math.min(CHUNK_SIZE + patternBytes.length - 1, fileSize - position);
                
                // チャンクごとにArenaを作り、検索が終わったらすぐにアンマップする
                try (Arena arena = Arena.ofConfined()) {
                    ByteBuffer buffer = channel.map(
                        FileChannel.MapMode.READ_ONLY, position, mapSize, arena
                    ).asByteBuffer();
                    
                    // このチャンク内で、開始位置が CHUNK_SIZE 未満の一致を探す
                    List<Long> matches = searchInBuffer(buffer, patternBytes, CHUNK_SIZE);
                    for (Long offset : matches) {
                        results.add(new SearchResult(
                            file, 
                            position + offset, 
                            extractContext(buffer, offset.intValue())
                        ));
                    }
                }
                
                position += CHUNK_SIZE;
            }
        }
        
        return results;
    }
    
    public CompletableFuture<List<SearchResult>> searchMultipleFiles(
            List<Path> files, String pattern) {
        
        List<CompletableFuture<List<SearchResult>>> futures = files.stream()
            .map(file -> CompletableFuture.supplyAsync(() -> {
                try {
                    return searchPattern(file, pattern);
                } catch (IOException e) {
                    return Collections.<SearchResult>emptyList();
                }
            }))
            .toList();
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .flatMap(f -> f.join().stream())
                .toList()
            );
    }
}
```

`MappedByteBuffer`（`asByteBuffer()`で得るバッファーも同じ）は1つで2GBまでしか扱えないので、大きなファイルはチャンクに分けてマップします。
チャンクごとに`Arena`を作って閉じると、読み終えたチャンクのマッピングがその場で解放され、使う仮想メモリがチャンク1つ分に収まります（`README.md`の7と8）。
<!-- claim: map-limit, arena-closed -->

## 4. リアルタイムデータストリーミングプラットフォーム

### 問題
大規模なデータファイルを処理速度の異なる複数のコンシューマーにストリーミングする（バックプレッシャー処理）。

### リアクティブストリームを使用した解決策
```java
public class DataStreamingPlatform {
    
    public void streamDataFile(Path dataFile, List<DataConsumer> consumers) {
        ReactiveFileReader reader = new ReactiveFileReader(dataFile, 64 * 1024);
        
        // マルチキャストプロセッサーを作成
        MulticastProcessor processor = new MulticastProcessor();
        
        // 異なる速度のコンシューマーを購読
        for (DataConsumer consumer : consumers) {
            processor.subscribe(new ConsumerAdapter(consumer));
        }
        
        // ストリーミングを開始
        reader.subscribe(processor);
    }
    
    class ConsumerAdapter implements Subscriber<String> {
        private final DataConsumer consumer;
        private Subscription subscription;
        private final int bufferSize;
        
        ConsumerAdapter(DataConsumer consumer) {
            this.consumer = consumer;
            this.bufferSize = consumer.getBufferSize();
        }
        
        @Override
        public void onSubscribe(Subscription subscription) {
            this.subscription = subscription;
            // コンシューマーの容量に基づいてリクエスト
            subscription.request(bufferSize);
        }
        
        @Override
        public void onNext(String data) {
            // コンシューマーのペースで処理し、終わったら次の1件を要求する。
            // 要求した分しか届かないので、遅いコンシューマーにデータがたまり続けることはない
            consumer.process(data);
            subscription.request(1);
        }
        
        @Override
        public void onError(Throwable throwable) {
            consumer.handleError(throwable);
        }
        
        @Override
        public void onComplete() {
            consumer.complete();
        }
    }
}
```

## 5. 分散ファイル同期

### 問題
ローカルのディレクトリの変更を検出し、変わったファイルだけを複数のサーバーへ送る。
`WatchService`の通知の速さはOSによって違い、macOSではポーリングのため約2秒かかる。
<!-- claim: watch-impl -->

### WatchServiceと非同期I/Oを組み合わせた解決策
```java
public class DistributedFileSync {
    private final AdvancedFileWatcher watcher;
    private final AsyncFileIO asyncIO;
    private final NetworkService network;
    
    public void setupSync(Path localDir, List<RemoteServer> servers) 
            throws IOException {
        
        // ローカルディレクトリを監視
        watcher.watchDirectory(localDir, true);
        
        // 変更を処理（WatchEvent.Kindはenumではないので、switchではなくifで分ける）
        watcher.registerProcessor("*", (file, eventType) -> {
            if (eventType == StandardWatchEventKinds.ENTRY_DELETE) {
                deleteFromServers(file, servers);
            } else {
                // イベントの種類よりも、今のファイルの状態で決める
                if (Files.exists(file)) {
                    syncFileToServers(file, servers);
                }
            }
        });
    }
    
    private void syncFileToServers(Path file, List<RemoteServer> servers) {
        // ファイルを非同期で読み取る
        asyncIO.readFileAsync(file)
            .thenCompose(content -> {
                // すべてのサーバーに並列でアップロード
                List<CompletableFuture<Void>> uploads = servers.stream()
                    .map(server -> network.uploadAsync(server, file, content))
                    .toList();
                
                return CompletableFuture.allOf(
                    uploads.toArray(new CompletableFuture[0])
                );
            })
            .exceptionally(throwable -> {
                logger.error("同期に失敗しました: " + file, throwable);
                scheduleRetry(file, servers);
                return null;
            });
    }
}
```

イベントがたまりすぎると、`WatchService`はたまっていたイベントを捨てて`OVERFLOW`だけを知らせます（`README.md`の6）。
同期のように取りこぼしが許されない処理では、`OVERFLOW`を受け取ったときや起動したときに、ディレクトリ全体とサーバーの内容を比べ直す処理も用意します。
<!-- claim: watch-overflow -->

## 6. 設定ファイルの安全な書き換えと再読み込み

### 問題
動いているアプリケーションの設定ファイルを書き換え、アプリケーションに再起動なしで読み直させる。
書き換えの途中の、半分だけ書かれた設定を読ませてはならない。

### ATOMIC_MOVEとWatchServiceを組み合わせた解決策
```java
public class ConfigReloader implements AutoCloseable {
    private final Path configFile;
    private final AdvancedFileWatcher watcher;
    private volatile Properties current = new Properties();

    public ConfigReloader(Path configFile) throws IOException {
        this.configFile = configFile.toAbsolutePath();
        this.watcher = new AdvancedFileWatcher(1);
        reload();
        // 一時ファイル（app.properties.tmp）のイベントは名前で除く
        watcher.registerConsumer(this.configFile.getFileName().toString(), file -> reload());
        watcher.watchDirectory(this.configFile.getParent());
        watcher.startWatching();
    }

    /** 書く側：同じディレクトリの一時ファイルに書いてから、不可分な移動で置き換える */
    public static void write(Path configFile, Properties props) throws IOException {
        Path tmp = configFile.resolveSibling(configFile.getFileName() + ".tmp");
        try (Writer w = Files.newBufferedWriter(tmp)) {
            props.store(w, null);
        }
        Files.move(tmp, configFile, StandardCopyOption.ATOMIC_MOVE);
    }

    /** 読む側：イベントの種類によらず、今のファイルを読み直す */
    private void reload() {
        Properties next = new Properties();
        try (Reader r = Files.newBufferedReader(configFile)) {
            next.load(r);
            current = next;
        } catch (IOException e) {
            // 読めなかったときは古い設定のまま動き続ける
        }
    }

    public String get(String key) {
        return current.getProperty(key);
    }

    @Override
    public void close() {
        watcher.close();
    }
}
```

`ATOMIC_MOVE`で置き換えると、読む側には古い内容か新しい内容のどちらかだけが見えます。
一時ファイルを同じディレクトリに作るのは、別のファイルシステムへは不可分な移動ができないためです（`README.md`の5）。
<!-- claim: atomic-move-replace, atomic-move-unsupported -->

置き換えたときにどの種類のイベントが届くかは、`WatchService`の実装によって違うことがあります。
そのため、読む側はイベントの種類で分けずに、イベントが届いたらファイルを読み直します（`README.md`の6）。
<!-- claim: watch-impl -->

## ベストプラクティス

ここまでの例に共通する考え方は、次のとおりです。

1. 目的に合う機能を選ぶ
   - ファイルの変更を知りたいなら`WatchService`。ただし通知の速さと取りこぼしはOSによって違う
   - 呼び出したスレッドを待たせずに読み書きしたいなら`AsynchronousFileChannel`
   - 大きなファイルの一部を何度も読み書きするならメモリマップドファイル
   - 処理の速さに合わせて読み込みを抑えたいならFlow API（バックプレッシャー）
2. 資源を確実に閉じる
   - チャネル、`Files.list`・`Files.walk`のストリーム、`WatchService`はtry-with-resourcesで閉じる
   - メモリマップの領域は`Arena`でマップし、使い終わったら`Arena`を閉じて解放する
   - 同時に行う処理の数を制限する
3. 失敗に備える
   - 一部のファイルだけが失敗する場合を考え、失敗したファイルと理由を記録する
   - 通知を取りこぼしたとき（`OVERFLOW`）に、全体を調べ直す処理を用意する
4. 速さは測って確かめる
   - どの方法が速いかは、ファイルの大きさ、OSのキャッシュ、ディスクで変わる。`benchmark`パッケージのJMHベンチマークで、自分の環境で比べる
<!-- claim: watch-impl, watch-overflow, list-must-close -->