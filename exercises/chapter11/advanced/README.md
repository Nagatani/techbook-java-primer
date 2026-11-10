# 第11章 応用課題

## 学習目標
- ファイル入出力の高度な活用
- ストリーム処理の最適化
- NIOを使った高性能I/O
- 並行ファイル処理
- 大容量データ処理

## 課題一覧

### 課題1: 高性能ログ解析システム
**ファイル名**: `HighPerformanceLogAnalyzer.java`

NIOとストリーム処理を使用した大容量ログファイル解析システムを作成してください。

**要求仕様**:
- 並行ファイル読み込み
- リアルタイムログ監視
- 統計情報の生成
- 異常検知機能

**解析機能**:
- アクセスパターン分析
- エラー率計算
- パフォーマンス指標算出
- セキュリティ脅威検知

**実行例**:
```
=== 高性能ログ解析システム ===

LogAnalyzer Pro v2.0

=== ファイル処理統計 ===
解析対象ファイル:

ログディレクトリ: /var/log/webserver/
ファイル数: 1,250個
総サイズ: 125GB
期間: 2024年1月1日 ～ 2024年7月5日
フォーマット: Apache Combined Log Format

並行処理設定:
スレッド数: 16個 (CPU cores)
バッファサイズ: 64MB
読み込み方式: NIO2 非同期
メモリマップ: 有効 (大ファイル用)

ファイル処理性能:
読み込み速度: 890MB/秒
解析速度: 12.4M行/秒
メモリ使用量: 2.1GB (ピーク)
処理時間: 142秒 (125GB完了)

=== アクセスパターン分析 ===
トラフィック統計:

総リクエスト数: 2,847,392,156件
ユニークIP数: 8,392,847個
期間: 2024年1-7月 (186日間)
平均RPS: 177.6/秒

時間別アクセス分布:
```java
public class AccessPatternAnalyzer {
    public void analyzeAccessPatterns() {
        try {
            // 大容量ファイル並行読み込み
            List<Path> logFiles = Files.walk(Paths.get("/var/log/webserver"))
                .filter(path -> path.toString().endsWith(".log"))
                .collect(Collectors.toList());
            
            // NIOによる高速ファイル処理
            ParallelLogProcessor processor = new ParallelLogProcessor(16);
            
            // 統計情報収集
            ConcurrentHashMap<String, AtomicLong> hourlyStats = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, AtomicLong> statusCodes = new ConcurrentHashMap<>();
            ConcurrentHashMap<String, AtomicLong> userAgents = new ConcurrentHashMap<>();
            
            logFiles.parallelStream().forEach(logFile -> {
                try (AsynchronousFileChannel channel = AsynchronousFileChannel.open(
                    logFile, StandardOpenOption.READ)) {
                    
                    ByteBuffer buffer = ByteBuffer.allocate(64 * 1024 * 1024); // 64MB
                    long position = 0;
                    
                    while (position < channel.size()) {
                        Future<Integer> readResult = channel.read(buffer, position);
                        buffer.flip();
                        
                        // ログエントリ解析
                        String content = StandardCharsets.UTF_8.decode(buffer).toString();
                        String[] lines = content.split("\\n");
                        
                        for (String line : lines) {
                            LogEntry entry = parseLogEntry(line);
                            if (entry != null) {
                                // 時間別統計
                                String hour = entry.getTimestamp().format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
                                hourlyStats.computeIfAbsent(hour, k -> new AtomicLong(0))
                                    .incrementAndGet();
                                
                                // ステータスコード統計
                                statusCodes.computeIfAbsent(entry.getStatusCode(), 
                                    k -> new AtomicLong(0)).incrementAndGet();
                                
                                // User-Agent統計
                                userAgents.computeIfAbsent(entry.getUserAgent(), 
                                    k -> new AtomicLong(0)).incrementAndGet();
                                
                                // 異常検知
                                if (isAnomalous(entry)) {
                                    securityAlerts.add(entry);
                                }
                            }
                        }
                        
                        buffer.clear();
                        position += readResult.get();
                    }
                    
                } catch (IOException | ExecutionException | InterruptedException e) {
                    logger.error("ファイル処理エラー: {}", logFile, e);
                }
            });
            
            // 結果集計・出力
            generateAnalysisReport(hourlyStats, statusCodes, userAgents);
            
        } catch (IOException e) {
            logger.error("ログ解析エラー", e);
        }
    }
}
```

アクセス統計結果:
```
ピーク時間帯: 
1位 2024-03-15 14:00 - 847,293 requests/hour
2位 2024-07-04 21:00 - 823,194 requests/hour  
3位 2024-06-20 13:00 - 798,472 requests/hour

ステータスコード分布:
200 OK: 2,435,782,947 (85.5%)
404 Not Found: 284,739,216 (10.0%)
500 Server Error: 71,184,804 (2.5%)
301 Redirect: 42,710,883 (1.5%)
403 Forbidden: 12,974,306 (0.5%)

人気ページ TOP5:
/api/users: 145,792,358 requests
/home: 89,473,629 requests
/search: 67,294,847 requests
/product/list: 45,738,294 requests
/login: 38,472,938 requests

地域別アクセス:
アジア: 1,423,847,294 (50.0%)
北米: 569,538,517 (20.0%)
ヨーロッパ: 512,585,265 (18.0%)
その他: 341,420,980 (12.0%)
```

=== セキュリティ脅威検知 ===
異常アクセス分析:

検知ルール:
- SQLインジェクション攻撃
- XSS攻撃の試行
- ブルートフォース攻撃
- DDoS攻撃パターン
- 不正なUser-Agent

検知結果:
```
セキュリティアラート (過去7日):

SQLインジェクション攻撃:
検知件数: 1,247件
攻撃元IP: 192.168.1.100 (894件)
対象URL: /search?q=... (SQLi pattern)
ブロック率: 100% (WAF有効)

ブルートフォース攻撃:
検知件数: 89件  
攻撃元IP: 10.0.0.50 (67件)
対象: /admin/login
試行回数: 10,000+ (パスワード総当たり)
対策: IP自動ブロック実行

DDoS攻撃:
検知件数: 3件
最大RPS: 50,000/秒 (通常の300倍)
攻撃継続時間: 平均12分
軽減策: レート制限 + CDN フィルタリング

異常User-Agent:
検知件数: 456件
パターン: ボット・スクレイパー
対策: robots.txt + Captcha 導入
```

=== パフォーマンス監視 ===
レスポンス性能分析:

応答時間統計:
平均応答時間: 245ms
50%ile (median): 180ms  
90%ile: 890ms
95%ile: 1,450ms
99%ile: 4,200ms
99.9%ile: 12,800ms

遅いエンドポイント TOP5:
/api/analytics: 平均2,840ms
/report/generate: 平均1,920ms
/search/advanced: 平均1,450ms
/data/export: 平均1,280ms
/admin/backup: 平均980ms

パフォーマンス改善提案:
1. データベースインデックス最適化
2. キャッシュ戦略の見直し
3. CDN配信範囲の拡大
4. 非同期処理の導入
5. 重いクエリの分割実行
```

### 課題2: 分散ファイル同期システム
**ファイル名**: `DistributedFileSyncSystem.java`

複数のサーバー間でファイルを同期するシステムを作成してください。

**要求仕様**:
- 差分同期の実装
- 競合解決機能
- ネットワーク効率化
- バージョン管理

**同期機能**:
- リアルタイム監視
- 増分バックアップ
- ファイル整合性チェック
- 帯域幅制御

**実行例**:
```
=== 分散ファイル同期システム ===

SyncMaster Enterprise v4.0

=== 同期ネットワーク構成 ===
グローバル同期クラスター:

ノード構成:
マスターノード: 東京 (primary)
セカンダリノード: 大阪 (backup)  
リージョンノード: シンガポール, ロンドン, ニューヨーク
エッジノード: 25箇所 (世界各地)

ネットワーク仕様:
専用線帯域: 10Gbps (データセンター間)
インターネット: 1Gbps (エッジノード)
レイテンシ: 東京-大阪 5ms, 東京-ニューヨーク 180ms
圧縮率: 平均67% (重複除去 + gzip)

同期対象データ:
総ファイル数: 12,847,392個
総データ量: 2.4PB (ペタバイト)
ファイル形式: 画像、動画、文書、ログ
更新頻度: 約50,000ファイル/日

同期性能:
初回同期: 48時間 (2.4PB)
差分同期: 平均35秒 (日次更新分)
検知遅延: 50ms (ファイル変更検知)
同期完了時間: 99%が5分以内

=== リアルタイム同期 ===
ファイル変更監視:

```java
public class RealtimeFileSynchronizer {
    public void startRealtimeSync() {
        try {
            // ファイル監視サービス開始
            WatchService watchService = FileSystems.getDefault().newWatchService();
            
            // 監視対象ディレクトリ登録
            Path syncDir = Paths.get("/data/sync");
            registerDirectoryRecursive(syncDir, watchService);
            
            // 変更検知ループ
            while (true) {
                WatchKey key = watchService.take(); // ブロッキング待機
                
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path changedFile = (Path) event.context();
                    
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        // ファイル作成
                        handleFileCreated(changedFile);
                        
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        // ファイル変更
                        handleFileModified(changedFile);
                        
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        // ファイル削除
                        handleFileDeleted(changedFile);
                    }
                }
                
                // WatchKey をリセット
                boolean valid = key.reset();
                if (!valid) {
                    logger.warn("WatchKey無効化: ディレクトリ削除検知");
                    break;
                }
            }
            
        } catch (IOException | InterruptedException e) {
            logger.error("ファイル監視エラー", e);
        }
    }
    
    private void handleFileModified(Path filePath) {
        try {
            // ファイル情報取得
            FileMetadata metadata = getFileMetadata(filePath);
            
            // 差分計算
            FileDiff diff = calculateDifference(filePath, metadata);
            
            if (diff.hasChanges()) {
                // 同期ジョブ作成
                SyncJob job = new SyncJob(filePath, diff, SyncPriority.HIGH);
                
                // 分散ノードに配信
                distributionManager.distributeSyncJob(job);
                
                // 進捗追跡
                progressTracker.addJob(job);
                
                logger.info("ファイル同期開始: {} (差分サイズ: {})", 
                    filePath, diff.getSize());
            }
            
        } catch (IOException e) {
            logger.error("ファイル同期エラー: {}", filePath, e);
        }
    }
}
```

差分同期実行例:
変更ファイル: /data/projects/report.pdf
変更サイズ: 2.4MB → 2.6MB (+200KB)
差分計算: バイナリ差分アルゴリズム
圧縮後差分: 67KB (66%圧縮)

同期プロセス:
1. 差分計算完了: 0.12秒
2. 全ノード配信: 1.34秒  
3. 適用完了確認: 2.89秒
4. 整合性検証: 0.45秒
総同期時間: 4.80秒

=== 競合解決システム ===
自動競合解決:

競合検知例:
ファイル: /shared/document.docx
ノードA: 2024-07-05 14:30:15 変更
ノードB: 2024-07-05 14:30:18 変更 (3秒差)
競合タイプ: 同時編集競合

解決戦略:
1. タイムスタンプ比較
2. ファイルサイズ考慮
3. ユーザー優先度
4. 自動マージ試行
5. 手動解決要求

競合解決実行:
```java
public class ConflictResolver {
    public ConflictResolution resolveConflict(FileConflict conflict) {
        try {
            // 競合ファイル情報取得
            FileVersion versionA = conflict.getVersionA();
            FileVersion versionB = conflict.getVersionB();
            
            // 自動解決試行
            AutoMergeResult mergeResult = autoMerger.attemptMerge(versionA, versionB);
            
            if (mergeResult.isSuccessful()) {
                // 自動マージ成功
                FileVersion mergedVersion = mergeResult.getMergedVersion();
                
                // マージ結果を全ノードに配信
                distributionManager.distributeResolvedFile(mergedVersion);
                
                // 競合ログ記録
                conflictLogger.logResolved(conflict, ConflictResolutionType.AUTO_MERGE);
                
                return ConflictResolution.resolved(mergedVersion);
                
            } else {
                // 自動マージ失敗 - 手動解決要求
                NotificationService notification = new NotificationService();
                notification.notifyAdministrators(conflict);
                
                // 一時的に最新版を選択
                FileVersion latestVersion = selectLatestVersion(versionA, versionB);
                
                // バックアップ保存
                backupManager.saveConflictedVersions(conflict);
                
                conflictLogger.logManualResolutionRequired(conflict);
                
                return ConflictResolution.manualRequired(conflict);
            }
            
        } catch (MergeException e) {
            logger.error("競合解決エラー", e);
            return ConflictResolution.failed(e);
        }
    }
}
```

競合解決統計:
自動解決成功: 94.2% (3,847件/4,082件)
手動解決要求: 5.8% (235件)
解決時間: 平均1.2秒 (自動)
データ損失: 0件 (完全保護)
```

### 課題3: 高速データベースバックアップシステム
**ファイル名**: `HighSpeedDatabaseBackup.java`

大容量データベースの高速バックアップ・リストアシステムを作成してください。

**要求仕様**:
- 並行バックアップ処理
- 増分バックアップ
- 圧縮・暗号化
- 整合性保証

**バックアップ機能**:
- ホットバックアップ
- ポイントインタイム復旧
- 自動バックアップスケジュール
- クラウドストレージ連携

**実行例**:
```
=== 高速データベースバックアップシステム ===

UltraBackup Enterprise v7.0

=== バックアップ対象システム ===
データベース情報:

データベース: PostgreSQL 16.0
総データサイズ: 8.5TB
テーブル数: 12,847個
インデックス数: 45,392個  
行数: 約1.2兆行

サーバー構成:
CPU: 128 cores (Intel Xeon)
RAM: 1TB ECC
ストレージ: 20TB NVMe SSD (RAID 10)
ネットワーク: 100Gbps Ethernet

バックアップストレージ:
プライマリ: オンプレミス NAS 50TB
セカンダリ: AWS S3 Glacier (無制限)
レプリケーション: 3拠点 (東京、大阪、福岡)

=== 並行バックアップ実行 ===
高速並行処理:

バックアップジョブ: BACKUP_20240705_143000
開始時刻: 2024-07-05 14:30:00
バックアップ種別: フル + 増分
圧縮: LZ4 高速圧縮
暗号化: AES-256-GCM

並行処理設定:
ワーカースレッド数: 64個
並行テーブル処理: 16個
I/Oバッファサイズ: 256MB
ネットワーク帯域: 80Gbps (80%使用)

```java
public class ParallelDatabaseBackup {
    public void executeFullBackup() {
        try {
            // バックアップジョブ初期化
            BackupJob job = new BackupJob("BACKUP_20240705_143000");
            job.setType(BackupType.FULL);
            job.setCompression(CompressionType.LZ4);
            job.setEncryption(EncryptionType.AES_256_GCM);
            
            // データベース接続プール
            HikariDataSource dataSource = createHighPerformanceDataSource();
            
            // 並行処理エグゼキュータ
            ExecutorService executor = ForkJoinPool.commonPool();
            
            // テーブル一覧取得
            List<String> tables = getDatabaseTables();
            
            // 並行バックアップ実行
            List<CompletableFuture<BackupResult>> futures = tables.stream()
                .map(table -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return backupTable(table, job, dataSource);
                    } catch (SQLException e) {
                        logger.error("テーブルバックアップ失敗: {}", table, e);
                        return BackupResult.failed(table, e);
                    }
                }, executor))
                .collect(Collectors.toList());
            
            // 全テーブル完了待機
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    // バックアップ完了処理
                    finalizeBackup(job);
                    
                    // 整合性検証
                    verifyBackupIntegrity(job);
                    
                    // クラウドアップロード
                    uploadToCloud(job);
                    
                    logger.info("フルバックアップ完了: {}", job.getId());
                })
                .get(4, TimeUnit.HOURS); // 4時間タイムアウト
            
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            logger.error("バックアップエラー", e);
        }
    }
    
    private BackupResult backupTable(String tableName, BackupJob job, DataSource dataSource) 
            throws SQLException {
        
        long startTime = System.currentTimeMillis();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM " + tableName + " ORDER BY id");
             ResultSet rs = stmt.executeQuery()) {
            
            // 圧縮ストリーム初期化
            Path backupFile = Paths.get("/backup", job.getId(), tableName + ".backup");
            Files.createDirectories(backupFile.getParent());
            
            try (FileOutputStream fos = new FileOutputStream(backupFile.toFile());
                 LZ4FrameOutputStream lz4Out = new LZ4FrameOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(lz4Out)) {
                
                int rowCount = 0;
                long totalBytes = 0;
                
                // 行データを順次書き込み
                while (rs.next()) {
                    RowData row = extractRowData(rs);
                    
                    // 暗号化してシリアライズ
                    byte[] encryptedData = encrypt(row.serialize(), job.getEncryptionKey());
                    oos.writeObject(encryptedData);
                    
                    rowCount++;
                    totalBytes += encryptedData.length;
                    
                    // 進捗報告 (10万行毎)
                    if (rowCount % 100000 == 0) {
                        reportProgress(tableName, rowCount);
                    }
                }
                
                oos.flush();
                
                long duration = System.currentTimeMillis() - startTime;
                
                return BackupResult.success(tableName, rowCount, totalBytes, duration);
            }
            
        } catch (IOException e) {
            logger.error("ファイル書き込みエラー: {}", tableName, e);
            return BackupResult.failed(tableName, e);
        }
    }
}
```

バックアップ進捗:
```
=== リアルタイム進捗 ===

完了テーブル: 8,247 / 12,847 (64.2%)
処理中テーブル: 16個
待機中テーブル: 4,584個

処理速度:
平均スループット: 1.2GB/秒
最大スループット: 2.8GB/秒  
現在速度: 1.8GB/秒
残り時間: 約2時間15分

個別テーブル進捗:
user_transactions: 89.2% (450M/505M行)
order_history: 76.8% (892M/1.16B行)  
product_catalog: 100% 完了 (15分)
audit_logs: 23.4% (2.1B/9.0B行)

リソース使用率:
CPU: 92% (118/128 cores)
メモリ: 67% (670GB/1TB)
ディスクI/O: 15GB/秒 (読み取り)
ネットワーク: 78Gbps (アップロード)
```

=== 増分バックアップ ===
効率的な差分バックアップ:

増分バックアップ設定:
ベースライン: 2024-07-01 フルバックアップ
前回増分: 2024-07-04 22:00
今回増分: 2024-07-05 22:00
追跡方式: LSN (Log Sequence Number) ベース

変更検知結果:
変更テーブル数: 1,247個 (全体の9.7%)
新規行: 8,492,847行
更新行: 2,394,728行  
削除行: 456,839行
変更データ量: 125GB (元サイズの1.5%)

増分バックアップ実行:
実行時間: 18分32秒 (フルバックアップの12%)
圧縮後サイズ: 41GB (67%圧縮)
転送時間: 4分15秒 (クラウドアップロード)
整合性検証: 100% 成功

復旧テスト:
ポイントインタイム復旧: 2024-07-05 14:30:00
復旧時間: 45分18秒
データ整合性: 100% (全チェックサム一致)
アプリケーション動作: 正常確認
```

## 習得すべき技術要素

### 高性能I/O技術
- NIO.2 非同期ファイル操作
- メモリマップドファイル
- チャネルベースI/O
- ダイレクトバッファ

### 並行処理最適化
- Fork/Join Framework
- CompletableFuture による非同期処理
- スレッドプール最適化
- ロックフリープログラミング

### ストリーム処理
- Pipeline Processing
- バックプレッシャー制御
- フロー制御
- バッファリング戦略

## 参考リソース

- Java NIO.2 File API Documentation
- High-Performance Java I/O (O'Reilly)
- Java Concurrency in Practice (Addison-Wesley)
- Parallel Streams Performance (Oracle)

## 重要な注意事項

大容量ファイル処理では、メモリ使用量とガベージコレクションの影響を十分に考慮してください。本番環境では適切な監視とチューニングが必要です。