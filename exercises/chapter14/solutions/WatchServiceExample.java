import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * ファイル監視システム
 * NIO.2のWatchServiceを活用したリアルタイムファイル監視
 */
public class WatchServiceExample {
    
    private static final Logger logger = Logger.getLogger(WatchServiceExample.class.getName());
    
    /**
     * ファイルイベントの種類
     */
    public enum FileEventType {
        /** ファイル作成 */
        CREATED,
        /** ファイル変更 */
        MODIFIED,
        /** ファイル削除 */
        DELETED,
        /** オーバーフロー（イベント取りこぼし） */
        OVERFLOW
    }
    
    /**
     * ファイルイベント情報
     */
    public static class FileEvent {
        private final FileEventType type;
        private final Path path;
        private final long timestamp;
        private final Map<String, Object> metadata;
        
        public FileEvent(FileEventType type, Path path) {
            this.type = type;
            this.path = path;
            this.timestamp = System.currentTimeMillis();
            this.metadata = new HashMap<>();
        }
        
        public FileEvent(FileEventType type, Path path, Map<String, Object> metadata) {
            this.type = type;
            this.path = path;
            this.timestamp = System.currentTimeMillis();
            this.metadata = new HashMap<>(metadata);
        }
        
        public FileEventType getType() { return type; }
        public Path getPath() { return path; }
        public long getTimestamp() { return timestamp; }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
        
        public void addMetadata(String key, Object value) {
            metadata.put(key, value);
        }
        
        @Override
        public String toString() {
            return String.format("FileEvent[%s, %s, %s]", 
                type, path, new Date(timestamp));
        }
    }
    
    /**
     * ファイル監視設定
     */
    public static class WatchConfig {
        private final boolean recursive;
        private final Set<String> includePatterns;
        private final Set<String> excludePatterns;
        private final Set<FileEventType> eventTypes;
        private final boolean enableMetadata;
        
        public WatchConfig(boolean recursive, Set<String> includePatterns, 
                          Set<String> excludePatterns, Set<FileEventType> eventTypes,
                          boolean enableMetadata) {
            this.recursive = recursive;
            this.includePatterns = new HashSet<>(includePatterns);
            this.excludePatterns = new HashSet<>(excludePatterns);
            this.eventTypes = EnumSet.copyOf(eventTypes);
            this.enableMetadata = enableMetadata;
        }
        
        public static WatchConfig defaultConfig() {
            return new WatchConfig(
                true,
                Set.of("*"),
                Set.of("*.tmp", "*.log", ".DS_Store"),
                EnumSet.of(FileEventType.CREATED, FileEventType.MODIFIED, FileEventType.DELETED),
                true
            );
        }
        
        public boolean isRecursive() { return recursive; }
        public Set<String> getIncludePatterns() { return new HashSet<>(includePatterns); }
        public Set<String> getExcludePatterns() { return new HashSet<>(excludePatterns); }
        public Set<FileEventType> getEventTypes() { return EnumSet.copyOf(eventTypes); }
        public boolean isMetadataEnabled() { return enableMetadata; }
    }
    
    /**
     * ファイル監視統計
     */
    public static class WatchStatistics {
        private final Map<FileEventType, Long> eventCounts;
        private final long totalEvents;
        private final long startTime;
        private volatile long lastEventTime;
        
        public WatchStatistics() {
            this.eventCounts = new ConcurrentHashMap<>();
            this.totalEvents = 0;
            this.startTime = System.currentTimeMillis();
            this.lastEventTime = startTime;
        }
        
        public synchronized void recordEvent(FileEventType type) {
            eventCounts.merge(type, 1L, Long::sum);
            lastEventTime = System.currentTimeMillis();
        }
        
        public Map<FileEventType, Long> getEventCounts() {
            return new HashMap<>(eventCounts);
        }
        
        public long getTotalEvents() {
            return eventCounts.values().stream().mapToLong(Long::longValue).sum();
        }
        
        public long getUptime() {
            return System.currentTimeMillis() - startTime;
        }
        
        public long getLastEventTime() {
            return lastEventTime;
        }
        
        @Override
        public String toString() {
            return String.format("監視統計: 総イベント数=%d, 稼働時間=%dms, イベント詳細=%s",
                getTotalEvents(), getUptime(), eventCounts);
        }
    }
    
    /**
     * ファイル監視クラス
     */
    public static class FileWatcher {
        private final Path watchDirectory;
        private final WatchConfig config;
        private final List<Consumer<FileEvent>> eventHandlers;
        private final WatchStatistics statistics;
        
        private WatchService watchService;
        private final Map<WatchKey, Path> watchKeys;
        private final AtomicBoolean running;
        private ExecutorService executorService;
        private Future<?> watchTask;
        
        public FileWatcher(Path watchDirectory, WatchConfig config) {
            this.watchDirectory = watchDirectory;
            this.config = config;
            this.eventHandlers = new ArrayList<>();
            this.statistics = new WatchStatistics();
            this.watchKeys = new ConcurrentHashMap<>();
            this.running = new AtomicBoolean(false);
        }
        
        /**
         * イベントハンドラーを追加
         */
        public void addEventHandler(Consumer<FileEvent> handler) {
            eventHandlers.add(handler);
        }
        
        /**
         * 監視を開始
         */
        public void start() throws IOException {
            if (running.get()) {
                throw new IllegalStateException("監視は既に開始されています");
            }
            
            watchService = FileSystems.getDefault().newWatchService();
            executorService = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "FileWatcher-" + watchDirectory.getFileName());
                t.setDaemon(true);
                return t;
            });
            
            // ディレクトリを登録
            registerDirectory(watchDirectory);
            
            running.set(true);
            watchTask = executorService.submit(this::watchLoop);
            
            logger.info("ファイル監視開始: " + watchDirectory);
        }
        
        /**
         * 監視を停止
         */
        public void stop() {
            if (!running.get()) {
                return;
            }
            
            running.set(false);
            
            if (watchTask != null) {
                watchTask.cancel(true);
            }
            
            if (executorService != null) {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
            
            if (watchService != null) {
                try {
                    watchService.close();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "WatchService停止中にエラー", e);
                }
            }
            
            logger.info("ファイル監視停止: " + watchDirectory);
        }
        
        /**
         * 監視状態を取得
         */
        public boolean isRunning() {
            return running.get();
        }
        
        /**
         * 統計情報を取得
         */
        public WatchStatistics getStatistics() {
            return statistics;
        }
        
        /**
         * 監視対象ディレクトリを登録
         */
        private void registerDirectory(Path directory) throws IOException {
            if (!Files.exists(directory) || !Files.isDirectory(directory)) {
                throw new IllegalArgumentException("監視対象ディレクトリが存在しません: " + directory);
            }
            
            WatchKey key = directory.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE);
            
            watchKeys.put(key, directory);
            
            // 再帰的監視の場合は既存のサブディレクトリも登録
            if (config.isRecursive()) {
                try {
                    Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                        @Override
                        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                            if (!dir.equals(directory)) {
                                WatchKey subKey = dir.register(watchService,
                                    StandardWatchEventKinds.ENTRY_CREATE,
                                    StandardWatchEventKinds.ENTRY_MODIFY,
                                    StandardWatchEventKinds.ENTRY_DELETE);
                                watchKeys.put(subKey, dir);
                            }
                            return FileVisitResult.CONTINUE;
                        }
                    });
                } catch (IOException e) {
                    logger.log(Level.WARNING, "サブディレクトリ登録中にエラー", e);
                }
            }
        }
        
        /**
         * 監視ループ
         */
        private void watchLoop() {
            while (running.get()) {
                WatchKey key;
                try {
                    key = watchService.poll(1, TimeUnit.SECONDS);
                    if (key == null) {
                        continue;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (ClosedWatchServiceException e) {
                    break;
                }
                
                Path directory = watchKeys.get(key);
                if (directory == null) {
                    key.reset();
                    continue;
                }
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    processEvent(directory, event);
                }
                
                boolean valid = key.reset();
                if (!valid) {
                    watchKeys.remove(key);
                    if (watchKeys.isEmpty()) {
                        break;
                    }
                }
            }
        }
        
        /**
         * イベントを処理
         */
        private void processEvent(Path directory, WatchEvent<?> event) {
            WatchEvent.Kind<?> kind = event.kind();
            
            if (kind == StandardWatchEventKinds.OVERFLOW) {
                FileEvent fileEvent = new FileEvent(FileEventType.OVERFLOW, directory);
                handleEvent(fileEvent);
                return;
            }
            
            @SuppressWarnings("unchecked")
            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
            Path fileName = pathEvent.context();
            Path fullPath = directory.resolve(fileName);
            
            // フィルタリング
            if (!shouldProcessFile(fullPath)) {
                return;
            }
            
            FileEventType eventType = mapEventType(kind);
            if (!config.getEventTypes().contains(eventType)) {
                return;
            }
            
            // メタデータ付きイベントを作成
            Map<String, Object> metadata = new HashMap<>();
            if (config.isMetadataEnabled()) {
                addFileMetadata(fullPath, metadata);
            }
            
            FileEvent fileEvent = new FileEvent(eventType, fullPath, metadata);
            
            // 新しいディレクトリが作成された場合は監視対象に追加
            if (eventType == FileEventType.CREATED && 
                config.isRecursive() && 
                Files.isDirectory(fullPath)) {
                try {
                    registerDirectory(fullPath);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "新しいディレクトリの監視登録に失敗: " + fullPath, e);
                }
            }
            
            handleEvent(fileEvent);
        }
        
        /**
         * ファイルを処理対象とするかチェック
         */
        private boolean shouldProcessFile(Path filePath) {
            String fileName = filePath.getFileName().toString();
            
            // 除外パターンチェック
            for (String excludePattern : config.getExcludePatterns()) {
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + excludePattern);
                if (matcher.matches(filePath.getFileName())) {
                    return false;
                }
            }
            
            // 包含パターンチェック
            for (String includePattern : config.getIncludePatterns()) {
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + includePattern);
                if (matcher.matches(filePath.getFileName())) {
                    return true;
                }
            }
            
            return false;
        }
        
        /**
         * WatchEventKindをFileEventTypeにマップ
         */
        private FileEventType mapEventType(WatchEvent.Kind<?> kind) {
            if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                return FileEventType.CREATED;
            } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                return FileEventType.MODIFIED;
            } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                return FileEventType.DELETED;
            } else {
                return FileEventType.OVERFLOW;
            }
        }
        
        /**
         * ファイルメタデータを追加
         */
        private void addFileMetadata(Path filePath, Map<String, Object> metadata) {
            try {
                if (Files.exists(filePath)) {
                    BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                    metadata.put("size", attrs.size());
                    metadata.put("lastModified", attrs.lastModifiedTime().toMillis());
                    metadata.put("isDirectory", attrs.isDirectory());
                    metadata.put("isRegularFile", attrs.isRegularFile());
                }
            } catch (IOException e) {
                logger.log(Level.FINE, "メタデータ取得エラー: " + filePath, e);
            }
        }
        
        /**
         * イベントを処理
         */
        private void handleEvent(FileEvent event) {
            statistics.recordEvent(event.getType());
            
            for (Consumer<FileEvent> handler : eventHandlers) {
                try {
                    handler.accept(event);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "イベントハンドラーでエラー", e);
                }
            }
        }
    }
    
    /**
     * 簡単なファイル監視の例
     */
    public static void simpleWatchExample(Path directory) throws IOException, InterruptedException {
        WatchConfig config = WatchConfig.defaultConfig();
        FileWatcher watcher = new FileWatcher(directory, config);
        
        // コンソール出力ハンドラー
        watcher.addEventHandler(event -> {
            System.out.println("ファイルイベント: " + event);
        });
        
        // 統計出力ハンドラー
        watcher.addEventHandler(event -> {
            WatchStatistics stats = watcher.getStatistics();
            if (stats.getTotalEvents() % 10 == 0) {
                System.out.println("統計: " + stats);
            }
        });
        
        watcher.start();
        
        System.out.println("ファイル監視開始: " + directory);
        System.out.println("Ctrl+Cで停止");
        
        // シャットダウンフック
        Runtime.getRuntime().addShutdownHook(new Thread(watcher::stop));
        
        // メインスレッドを維持
        Thread.currentThread().join();
    }
    
    /**
     * 高度なファイル監視の例
     */
    public static FileWatcher advancedWatchExample(Path directory) throws IOException {
        WatchConfig config = new WatchConfig(
            true,                                    // 再帰的監視
            Set.of("*.txt", "*.java", "*.md"),      // テキストファイルのみ
            Set.of("*.tmp", "*.bak"),               // 一時ファイルを除外
            EnumSet.allOf(FileEventType.class),     // すべてのイベント
            true                                    // メタデータ有効
        );
        
        FileWatcher watcher = new FileWatcher(directory, config);
        
        // ログファイルへの出力ハンドラー
        watcher.addEventHandler(event -> {
            logger.info(String.format("[%s] %s: %s", 
                event.getType(), event.getPath(), event.getMetadata()));
        });
        
        // ファイルサイズ監視ハンドラー
        watcher.addEventHandler(event -> {
            if (event.getType() == FileEventType.MODIFIED) {
                Object size = event.getMetadata().get("size");
                if (size instanceof Long && (Long) size > 1024 * 1024) { // 1MB以上
                    System.out.println("大きなファイルが変更されました: " + event.getPath() + 
                                     " (" + size + " bytes)");
                }
            }
        });
        
        watcher.start();
        return watcher;
    }
    
    /**
     * メイン関数（デモ用）
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 1) {
            System.err.println("使用方法: java WatchServiceExample <監視ディレクトリ>");
            System.exit(1);
        }
        
        Path watchDirectory = Paths.get(args[0]);
        if (!Files.exists(watchDirectory) || !Files.isDirectory(watchDirectory)) {
            System.err.println("指定されたディレクトリが存在しません: " + watchDirectory);
            System.exit(1);
        }
        
        simpleWatchExample(watchDirectory);
    }
}