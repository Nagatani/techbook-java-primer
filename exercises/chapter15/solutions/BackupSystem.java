import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ファイルバックアップシステム
 * 増分バックアップ、圧縮、整合性チェック機能を提供
 */
public class BackupSystem {
    
    private static final Logger logger = Logger.getLogger(BackupSystem.class.getName());
    
    /**
     * バックアップ設定クラス
     */
    public static class BackupConfig {
        private final Path sourceDirectory;
        private final Path backupDirectory;
        private final boolean enableCompression;
        private final boolean enableIncremental;
        private final boolean enableIntegrityCheck;
        private final Set<String> excludePatterns;
        private final int maxBackupCount;
        
        public BackupConfig(Path sourceDirectory, Path backupDirectory, 
                           boolean enableCompression, boolean enableIncremental,
                           boolean enableIntegrityCheck, Set<String> excludePatterns,
                           int maxBackupCount) {
            this.sourceDirectory = sourceDirectory;
            this.backupDirectory = backupDirectory;
            this.enableCompression = enableCompression;
            this.enableIncremental = enableIncremental;
            this.enableIntegrityCheck = enableIntegrityCheck;
            this.excludePatterns = new HashSet<>(excludePatterns);
            this.maxBackupCount = maxBackupCount;
        }
        
        public static BackupConfig defaultConfig(Path source, Path backup) {
            return new BackupConfig(source, backup, true, true, true, 
                                  Set.of("*.tmp", "*.log", ".DS_Store"), 5);
        }
        
        // Getters
        public Path getSourceDirectory() { return sourceDirectory; }
        public Path getBackupDirectory() { return backupDirectory; }
        public boolean isCompressionEnabled() { return enableCompression; }
        public boolean isIncrementalEnabled() { return enableIncremental; }
        public boolean isIntegrityCheckEnabled() { return enableIntegrityCheck; }
        public Set<String> getExcludePatterns() { return new HashSet<>(excludePatterns); }
        public int getMaxBackupCount() { return maxBackupCount; }
    }
    
    /**
     * バックアップ結果クラス
     */
    public static class BackupResult {
        private final boolean success;
        private final String backupName;
        private final long totalFiles;
        private final long totalSize;
        private final long compressedSize;
        private final long processingTimeMs;
        private final List<String> errors;
        private final BackupType backupType;
        
        public enum BackupType {
            FULL, INCREMENTAL
        }
        
        public BackupResult(boolean success, String backupName, long totalFiles, 
                           long totalSize, long compressedSize, long processingTimeMs,
                           List<String> errors, BackupType backupType) {
            this.success = success;
            this.backupName = backupName;
            this.totalFiles = totalFiles;
            this.totalSize = totalSize;
            this.compressedSize = compressedSize;
            this.processingTimeMs = processingTimeMs;
            this.errors = new ArrayList<>(errors);
            this.backupType = backupType;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getBackupName() { return backupName; }
        public long getTotalFiles() { return totalFiles; }
        public long getTotalSize() { return totalSize; }
        public long getCompressedSize() { return compressedSize; }
        public long getProcessingTimeMs() { return processingTimeMs; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public BackupType getBackupType() { return backupType; }
        
        public double getCompressionRatio() {
            return totalSize > 0 ? (double) compressedSize / totalSize : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("バックアップ結果[%s]: %s, %d ファイル, %d -> %d bytes (%.1f%%), %d ms",
                backupType, success ? "成功" : "失敗", totalFiles, totalSize, compressedSize,
                getCompressionRatio() * 100, processingTimeMs);
        }
    }
    
    /**
     * ファイル情報クラス
     */
    private static class FileInfo {
        private final Path relativePath;
        private final long size;
        private final long lastModified;
        private final String checksum;
        
        public FileInfo(Path relativePath, long size, long lastModified, String checksum) {
            this.relativePath = relativePath;
            this.size = size;
            this.lastModified = lastModified;
            this.checksum = checksum;
        }
        
        // Getters
        public Path getRelativePath() { return relativePath; }
        public long getSize() { return size; }
        public long getLastModified() { return lastModified; }
        public String getChecksum() { return checksum; }
    }
    
    /**
     * バックアップインデックス管理
     */
    private static class BackupIndex {
        private final Map<String, FileInfo> fileIndex;
        private final LocalDateTime createdAt;
        
        public BackupIndex() {
            this.fileIndex = new HashMap<>();
            this.createdAt = LocalDateTime.now();
        }
        
        public BackupIndex(Map<String, FileInfo> fileIndex, LocalDateTime createdAt) {
            this.fileIndex = new HashMap<>(fileIndex);
            this.createdAt = createdAt;
        }
        
        public void addFile(String relativePath, FileInfo info) {
            fileIndex.put(relativePath, info);
        }
        
        public FileInfo getFile(String relativePath) {
            return fileIndex.get(relativePath);
        }
        
        public boolean hasFile(String relativePath) {
            return fileIndex.containsKey(relativePath);
        }
        
        public Set<String> getFilePaths() {
            return new HashSet<>(fileIndex.keySet());
        }
        
        public int getFileCount() {
            return fileIndex.size();
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
    
    /**
     * フルバックアップを実行
     * @param config バックアップ設定
     * @return バックアップ結果
     */
    public static BackupResult performFullBackup(BackupConfig config) {
        long startTime = System.currentTimeMillis();
        List<String> errors = new ArrayList<>();
        
        try {
            // バックアップディレクトリを作成
            if (!Files.exists(config.getBackupDirectory())) {
                Files.createDirectories(config.getBackupDirectory());
            }
            
            // バックアップ名を生成
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupName = "backup_full_" + timestamp;
            Path backupPath = config.getBackupDirectory().resolve(backupName);
            
            // ファイルリストを取得
            List<Path> filesToBackup = collectFiles(config.getSourceDirectory(), config.getExcludePatterns());
            
            BackupIndex backupIndex = new BackupIndex();
            long totalSize = 0;
            long compressedSize = 0;
            
            if (config.isCompressionEnabled()) {
                // ZIP圧縮バックアップ
                compressedSize = createCompressedBackup(filesToBackup, config.getSourceDirectory(), 
                                                      backupPath.toString() + ".zip", backupIndex, config);
                totalSize = filesToBackup.stream().mapToLong(this::getFileSize).sum();
            } else {
                // 非圧縮バックアップ
                totalSize = createUncompressedBackup(filesToBackup, config.getSourceDirectory(), 
                                                   backupPath, backupIndex, config);
                compressedSize = totalSize;
            }
            
            // インデックスファイルを保存
            saveBackupIndex(backupIndex, backupPath.toString() + ".index");
            
            // 古いバックアップを削除
            cleanupOldBackups(config);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            logger.info("フルバックアップ完了: " + backupName);
            
            return new BackupResult(true, backupName, filesToBackup.size(), totalSize, 
                                  compressedSize, processingTime, errors, 
                                  BackupResult.BackupType.FULL);
            
        } catch (Exception e) {
            errors.add("バックアップエラー: " + e.getMessage());
            logger.log(Level.SEVERE, "フルバックアップに失敗しました", e);
            
            long processingTime = System.currentTimeMillis() - startTime;
            return new BackupResult(false, null, 0, 0, 0, processingTime, errors, 
                                  BackupResult.BackupType.FULL);
        }
    }
    
    /**
     * 増分バックアップを実行
     * @param config バックアップ設定
     * @return バックアップ結果
     */
    public static BackupResult performIncrementalBackup(BackupConfig config) {
        long startTime = System.currentTimeMillis();
        List<String> errors = new ArrayList<>();
        
        try {
            // 最新のバックアップインデックスを取得
            BackupIndex lastBackupIndex = getLatestBackupIndex(config.getBackupDirectory());
            if (lastBackupIndex == null) {
                // 前回のバックアップが見つからない場合はフルバックアップを実行
                return performFullBackup(config);
            }
            
            // 変更されたファイルを検出
            List<Path> changedFiles = detectChangedFiles(config.getSourceDirectory(), 
                                                        lastBackupIndex, config.getExcludePatterns());
            
            if (changedFiles.isEmpty()) {
                logger.info("変更されたファイルがありません。増分バックアップをスキップします。");
                return new BackupResult(true, "no_changes", 0, 0, 0, 
                                      System.currentTimeMillis() - startTime, errors,
                                      BackupResult.BackupType.INCREMENTAL);
            }
            
            // バックアップ名を生成
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String backupName = "backup_incr_" + timestamp;
            Path backupPath = config.getBackupDirectory().resolve(backupName);
            
            BackupIndex backupIndex = new BackupIndex();
            long totalSize = 0;
            long compressedSize = 0;
            
            if (config.isCompressionEnabled()) {
                compressedSize = createCompressedBackup(changedFiles, config.getSourceDirectory(), 
                                                      backupPath.toString() + ".zip", backupIndex, config);
                totalSize = changedFiles.stream().mapToLong(this::getFileSize).sum();
            } else {
                totalSize = createUncompressedBackup(changedFiles, config.getSourceDirectory(), 
                                                   backupPath, backupIndex, config);
                compressedSize = totalSize;
            }
            
            // インデックスファイルを保存
            saveBackupIndex(backupIndex, backupPath.toString() + ".index");
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            logger.info("増分バックアップ完了: " + backupName + " (" + changedFiles.size() + " ファイル)");
            
            return new BackupResult(true, backupName, changedFiles.size(), totalSize, 
                                  compressedSize, processingTime, errors, 
                                  BackupResult.BackupType.INCREMENTAL);
            
        } catch (Exception e) {
            errors.add("増分バックアップエラー: " + e.getMessage());
            logger.log(Level.SEVERE, "増分バックアップに失敗しました", e);
            
            long processingTime = System.currentTimeMillis() - startTime;
            return new BackupResult(false, null, 0, 0, 0, processingTime, errors, 
                                  BackupResult.BackupType.INCREMENTAL);
        }
    }
    
    /**
     * バックアップからファイルを復元
     * @param backupPath バックアップファイルパス
     * @param restoreDirectory 復元先ディレクトリ
     * @param config バックアップ設定
     * @return 復元されたファイル数
     */
    public static int restoreFromBackup(Path backupPath, Path restoreDirectory, BackupConfig config) {
        try {
            if (!Files.exists(restoreDirectory)) {
                Files.createDirectories(restoreDirectory);
            }
            
            if (backupPath.toString().endsWith(".zip")) {
                return restoreFromCompressedBackup(backupPath, restoreDirectory);
            } else {
                return restoreFromUncompressedBackup(backupPath, restoreDirectory);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "復元に失敗しました", e);
            return 0;
        }
    }
    
    /**
     * バックアップの整合性をチェック
     * @param backupPath バックアップファイルパス
     * @param config バックアップ設定
     * @return 整合性チェック結果
     */
    public static boolean verifyBackupIntegrity(Path backupPath, BackupConfig config) {
        try {
            if (!config.isIntegrityCheckEnabled()) {
                return true;
            }
            
            Path indexPath = Paths.get(backupPath.toString() + ".index");
            if (!Files.exists(indexPath)) {
                logger.warning("インデックスファイルが見つかりません: " + indexPath);
                return false;
            }
            
            BackupIndex backupIndex = loadBackupIndex(indexPath);
            
            if (backupPath.toString().endsWith(".zip")) {
                return verifyCompressedBackup(backupPath, backupIndex);
            } else {
                return verifyUncompressedBackup(backupPath, backupIndex);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "整合性チェックに失敗しました", e);
            return false;
        }
    }
    
    // プライベートメソッド
    
    private static List<Path> collectFiles(Path directory, Set<String> excludePatterns) throws IOException {
        List<Path> files = new ArrayList<>();
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();
                boolean exclude = excludePatterns.stream()
                    .anyMatch(pattern -> FileSystems.getDefault()
                        .getPathMatcher("glob:" + pattern).matches(file.getFileName()));
                
                if (!exclude) {
                    files.add(file);
                }
                
                return FileVisitResult.CONTINUE;
            }
        });
        
        return files;
    }
    
    private static long createCompressedBackup(List<Path> files, Path sourceRoot, 
                                             String zipPath, BackupIndex index, BackupConfig config) throws IOException {
        long totalCompressedSize = 0;
        
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipPath)))) {
            zos.setLevel(Deflater.DEFAULT_COMPRESSION);
            
            for (Path file : files) {
                Path relativePath = sourceRoot.relativize(file);
                ZipEntry entry = new ZipEntry(relativePath.toString().replace('\\', '/'));
                zos.putNextEntry(entry);
                
                Files.copy(file, zos);
                zos.closeEntry();
                
                // インデックスに追加
                if (config.isIntegrityCheckEnabled()) {
                    String checksum = calculateChecksum(file);
                    index.addFile(relativePath.toString(), 
                        new FileInfo(relativePath, Files.size(file), 
                                   Files.getLastModifiedTime(file).toMillis(), checksum));
                }
            }
            
            totalCompressedSize = Files.size(Paths.get(zipPath));
        }
        
        return totalCompressedSize;
    }
    
    private static long createUncompressedBackup(List<Path> files, Path sourceRoot, 
                                               Path backupRoot, BackupIndex index, BackupConfig config) throws IOException {
        long totalSize = 0;
        
        for (Path file : files) {
            Path relativePath = sourceRoot.relativize(file);
            Path targetPath = backupRoot.resolve(relativePath);
            
            // 親ディレクトリを作成
            Path parentDir = targetPath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            
            Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
            totalSize += Files.size(file);
            
            // インデックスに追加
            if (config.isIntegrityCheckEnabled()) {
                String checksum = calculateChecksum(file);
                index.addFile(relativePath.toString(), 
                    new FileInfo(relativePath, Files.size(file), 
                               Files.getLastModifiedTime(file).toMillis(), checksum));
            }
        }
        
        return totalSize;
    }
    
    private static List<Path> detectChangedFiles(Path directory, BackupIndex lastIndex, 
                                               Set<String> excludePatterns) throws IOException {
        List<Path> changedFiles = new ArrayList<>();
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();
                boolean exclude = excludePatterns.stream()
                    .anyMatch(pattern -> FileSystems.getDefault()
                        .getPathMatcher("glob:" + pattern).matches(file.getFileName()));
                
                if (!exclude) {
                    Path relativePath = directory.relativize(file);
                    String relativePathStr = relativePath.toString();
                    
                    FileInfo lastFileInfo = lastIndex.getFile(relativePathStr);
                    if (lastFileInfo == null || 
                        Files.getLastModifiedTime(file).toMillis() > lastFileInfo.getLastModified() ||
                        Files.size(file) != lastFileInfo.getSize()) {
                        changedFiles.add(file);
                    }
                }
                
                return FileVisitResult.CONTINUE;
            }
        });
        
        return changedFiles;
    }
    
    private static String calculateChecksum(Path file) throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            try (InputStream is = Files.newInputStream(file)) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    md.update(buffer, 0, bytesRead);
                }
            }
            
            byte[] hashBytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("MD5アルゴリズムが見つかりません", e);
        }
    }
    
    private static void saveBackupIndex(BackupIndex index, String indexPath) throws IOException {
        // 簡単な実装：プロパティファイルとして保存
        Properties props = new Properties();
        props.setProperty("created_at", index.getCreatedAt().toString());
        props.setProperty("file_count", String.valueOf(index.getFileCount()));
        
        for (String filePath : index.getFilePaths()) {
            FileInfo info = index.getFile(filePath);
            String key = "file." + filePath.replace('/', '.');
            String value = String.format("%d,%d,%s", info.getSize(), info.getLastModified(), info.getChecksum());
            props.setProperty(key, value);
        }
        
        try (OutputStream os = Files.newOutputStream(Paths.get(indexPath))) {
            props.store(os, "Backup Index");
        }
    }
    
    private static BackupIndex loadBackupIndex(Path indexPath) throws IOException {
        Properties props = new Properties();
        try (InputStream is = Files.newInputStream(indexPath)) {
            props.load(is);
        }
        
        LocalDateTime createdAt = LocalDateTime.parse(props.getProperty("created_at"));
        Map<String, FileInfo> fileIndex = new HashMap<>();
        
        for (String propName : props.stringPropertyNames()) {
            if (propName.startsWith("file.")) {
                String filePath = propName.substring(5).replace('.', '/');
                String[] parts = props.getProperty(propName).split(",");
                if (parts.length >= 3) {
                    long size = Long.parseLong(parts[0]);
                    long lastModified = Long.parseLong(parts[1]);
                    String checksum = parts[2];
                    fileIndex.put(filePath, new FileInfo(Paths.get(filePath), size, lastModified, checksum));
                }
            }
        }
        
        return new BackupIndex(fileIndex, createdAt);
    }
    
    private static BackupIndex getLatestBackupIndex(Path backupDirectory) throws IOException {
        if (!Files.exists(backupDirectory)) {
            return null;
        }
        
        Optional<Path> latestIndex = Files.list(backupDirectory)
            .filter(path -> path.toString().endsWith(".index"))
            .max(Comparator.comparing(path -> {
                try {
                    return Files.getLastModifiedTime(path);
                } catch (IOException e) {
                    return FileTime.fromMillis(0);
                }
            }));
        
        return latestIndex.isPresent() ? loadBackupIndex(latestIndex.get()) : null;
    }
    
    private static void cleanupOldBackups(BackupConfig config) throws IOException {
        if (config.getMaxBackupCount() <= 0) {
            return;
        }
        
        List<Path> backupFiles = Files.list(config.getBackupDirectory())
            .filter(path -> path.getFileName().toString().startsWith("backup_"))
            .sorted(Comparator.comparing(path -> {
                try {
                    return Files.getLastModifiedTime(path);
                } catch (IOException e) {
                    return FileTime.fromMillis(0);
                }
            }, Comparator.reverseOrder()))
            .collect(Collectors.toList());
        
        if (backupFiles.size() > config.getMaxBackupCount()) {
            for (int i = config.getMaxBackupCount(); i < backupFiles.size(); i++) {
                Path backupFile = backupFiles.get(i);
                Files.deleteIfExists(backupFile);
                Files.deleteIfExists(Paths.get(backupFile.toString() + ".index"));
                logger.info("古いバックアップを削除: " + backupFile.getFileName());
            }
        }
    }
    
    private static int restoreFromCompressedBackup(Path backupPath, Path restoreDirectory) throws IOException {
        int restoredFiles = 0;
        
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(backupPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path targetPath = restoreDirectory.resolve(entry.getName());
                
                // 親ディレクトリを作成
                Path parentDir = targetPath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                
                Files.copy(zis, targetPath, StandardCopyOption.REPLACE_EXISTING);
                restoredFiles++;
                zis.closeEntry();
            }
        }
        
        return restoredFiles;
    }
    
    private static int restoreFromUncompressedBackup(Path backupPath, Path restoreDirectory) throws IOException {
        final int[] restoredFiles = {0};
        
        Files.walkFileTree(backupPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = backupPath.relativize(file);
                Path targetPath = restoreDirectory.resolve(relativePath);
                
                // 親ディレクトリを作成
                Path parentDir = targetPath.getParent();
                if (parentDir != null && !Files.exists(parentDir)) {
                    Files.createDirectories(parentDir);
                }
                
                Files.copy(file, targetPath, StandardCopyOption.REPLACE_EXISTING);
                restoredFiles[0]++;
                return FileVisitResult.CONTINUE;
            }
        });
        
        return restoredFiles[0];
    }
    
    private static boolean verifyCompressedBackup(Path backupPath, BackupIndex index) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(backupPath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                FileInfo expectedInfo = index.getFile(entry.getName());
                if (expectedInfo == null) {
                    logger.warning("インデックスにないファイル: " + entry.getName());
                    return false;
                }
                
                if (entry.getSize() != expectedInfo.getSize()) {
                    logger.warning("ファイルサイズが一致しません: " + entry.getName());
                    return false;
                }
                
                zis.closeEntry();
            }
        }
        
        return true;
    }
    
    private static boolean verifyUncompressedBackup(Path backupPath, BackupIndex index) throws IOException {
        for (String filePath : index.getFilePaths()) {
            Path actualFile = backupPath.resolve(filePath);
            if (!Files.exists(actualFile)) {
                logger.warning("ファイルが見つかりません: " + filePath);
                return false;
            }
            
            FileInfo expectedInfo = index.getFile(filePath);
            if (Files.size(actualFile) != expectedInfo.getSize()) {
                logger.warning("ファイルサイズが一致しません: " + filePath);
                return false;
            }
            
            String actualChecksum = calculateChecksum(actualFile);
            if (!actualChecksum.equals(expectedInfo.getChecksum())) {
                logger.warning("チェックサムが一致しません: " + filePath);
                return false;
            }
        }
        
        return true;
    }
    
    private long getFileSize(Path file) {
        try {
            return Files.size(file);
        } catch (IOException e) {
            return 0;
        }
    }
}