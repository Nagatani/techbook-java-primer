import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;

/**
 * BackupSystemクラスのテストクラス
 */
public class BackupSystemTest {
    
    @TempDir
    Path tempDir;
    
    private Path sourceDir;
    private Path backupDir;
    private Path restoreDir;
    private BackupSystem.BackupConfig config;
    
    @BeforeEach
    public void setUp() throws IOException {
        sourceDir = tempDir.resolve("source");
        backupDir = tempDir.resolve("backup");
        restoreDir = tempDir.resolve("restore");
        
        Files.createDirectories(sourceDir);
        Files.createDirectories(backupDir);
        Files.createDirectories(restoreDir);
        
        // テストファイルを作成
        Files.write(sourceDir.resolve("file1.txt"), "Content of file 1".getBytes());
        Files.write(sourceDir.resolve("file2.txt"), "Content of file 2".getBytes());
        
        Path subDir = sourceDir.resolve("subdir");
        Files.createDirectories(subDir);
        Files.write(subDir.resolve("file3.txt"), "Content of file 3".getBytes());
        
        // 除外対象ファイル
        Files.write(sourceDir.resolve("temp.tmp"), "Temporary content".getBytes());
        
        config = BackupSystem.BackupConfig.defaultConfig(sourceDir, backupDir);
    }
    
    @Test
    public void testPerformFullBackup() {
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(config);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getBackupName());
        assertEquals(BackupSystem.BackupResult.BackupType.FULL, result.getBackupType());
        assertEquals(3, result.getTotalFiles()); // temp.tmpは除外される
        assertTrue(result.getTotalSize() > 0);
        assertTrue(result.getProcessingTimeMs() >= 0);
        assertTrue(result.getErrors().isEmpty());
        
        // 圧縮率をチェック
        assertTrue(result.getCompressionRatio() > 0);
        assertTrue(result.getCompressionRatio() <= 1.0);
    }
    
    @Test
    public void testPerformFullBackupUncompressed() {
        BackupSystem.BackupConfig uncompressedConfig = new BackupSystem.BackupConfig(
            sourceDir, backupDir, false, true, true, Set.of("*.tmp"), 5);
        
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(uncompressedConfig);
        
        assertTrue(result.isSuccess());
        assertEquals(3, result.getTotalFiles());
        assertEquals(result.getTotalSize(), result.getCompressedSize()); // 非圧縮なので同じ
        assertEquals(1.0, result.getCompressionRatio(), 0.001);
    }
    
    @Test
    public void testPerformIncrementalBackupWithoutPrevious() {
        // 前回バックアップがない場合はフルバックアップになる
        BackupSystem.BackupResult result = BackupSystem.performIncrementalBackup(config);
        
        assertTrue(result.isSuccess());
        assertEquals(BackupSystem.BackupResult.BackupType.FULL, result.getBackupType());
        assertEquals(3, result.getTotalFiles());
    }
    
    @Test
    public void testPerformIncrementalBackupWithChanges() throws IOException, InterruptedException {
        // まずフルバックアップを実行
        BackupSystem.BackupResult fullResult = BackupSystem.performFullBackup(config);
        assertTrue(fullResult.isSuccess());
        
        // 少し待ってから変更を加える
        Thread.sleep(1000);
        
        // ファイルを変更
        Files.write(sourceDir.resolve("file1.txt"), "Modified content of file 1".getBytes());
        Files.write(sourceDir.resolve("new_file.txt"), "New file content".getBytes());
        
        // 増分バックアップを実行
        BackupSystem.BackupResult incrResult = BackupSystem.performIncrementalBackup(config);
        
        assertTrue(incrResult.isSuccess());
        assertEquals(BackupSystem.BackupResult.BackupType.INCREMENTAL, incrResult.getBackupType());
        assertEquals(2, incrResult.getTotalFiles()); // 変更されたファイルと新しいファイル
    }
    
    @Test
    public void testPerformIncrementalBackupNoChanges() throws InterruptedException {
        // フルバックアップを実行
        BackupSystem.BackupResult fullResult = BackupSystem.performFullBackup(config);
        assertTrue(fullResult.isSuccess());
        
        // 変更なしで増分バックアップを実行
        BackupSystem.BackupResult incrResult = BackupSystem.performIncrementalBackup(config);
        
        assertTrue(incrResult.isSuccess());
        assertEquals("no_changes", incrResult.getBackupName());
        assertEquals(0, incrResult.getTotalFiles());
    }
    
    @Test
    public void testRestoreFromBackup() throws IOException {
        // バックアップを実行
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(config);
        assertTrue(result.isSuccess());
        
        // バックアップファイルを探す
        Path backupFile = Files.list(backupDir)
                .filter(path -> path.getFileName().toString().startsWith("backup_full_"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("バックアップファイルが見つかりません"));
        
        // 復元を実行
        int restoredFiles = BackupSystem.restoreFromBackup(backupFile, restoreDir, config);
        
        assertEquals(3, restoredFiles);
        
        // 復元されたファイルを確認
        assertTrue(Files.exists(restoreDir.resolve("file1.txt")));
        assertTrue(Files.exists(restoreDir.resolve("file2.txt")));
        assertTrue(Files.exists(restoreDir.resolve("subdir/file3.txt")));
        
        // 内容を確認
        assertEquals("Content of file 1", Files.readString(restoreDir.resolve("file1.txt")));
        assertEquals("Content of file 3", Files.readString(restoreDir.resolve("subdir/file3.txt")));
    }
    
    @Test
    public void testVerifyBackupIntegrity() {
        // バックアップを実行
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(config);
        assertTrue(result.isSuccess());
        
        // バックアップファイルを探す
        Path backupFile = Files.list(backupDir)
                .filter(path -> path.getFileName().toString().startsWith("backup_full_"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("バックアップファイルが見つかりません"));
        
        // 整合性をチェック
        boolean isValid = BackupSystem.verifyBackupIntegrity(backupFile, config);
        assertTrue(isValid);
    }
    
    @Test
    public void testBackupConfigMethods() {
        assertEquals(sourceDir, config.getSourceDirectory());
        assertEquals(backupDir, config.getBackupDirectory());
        assertTrue(config.isCompressionEnabled());
        assertTrue(config.isIncrementalEnabled());
        assertTrue(config.isIntegrityCheckEnabled());
        assertEquals(5, config.getMaxBackupCount());
        
        Set<String> excludePatterns = config.getExcludePatterns();
        assertTrue(excludePatterns.contains("*.tmp"));
        assertTrue(excludePatterns.contains("*.log"));
        assertTrue(excludePatterns.contains(".DS_Store"));
    }
    
    @Test
    public void testBackupResultMethods() {
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(config);
        
        assertTrue(result.isSuccess());
        assertNotNull(result.getBackupName());
        assertTrue(result.getTotalFiles() > 0);
        assertTrue(result.getTotalSize() > 0);
        assertTrue(result.getCompressedSize() > 0);
        assertTrue(result.getProcessingTimeMs() >= 0);
        assertNotNull(result.getErrors());
        assertEquals(BackupSystem.BackupResult.BackupType.FULL, result.getBackupType());
        
        double compressionRatio = result.getCompressionRatio();
        assertTrue(compressionRatio > 0 && compressionRatio <= 1.0);
        
        String resultString = result.toString();
        assertNotNull(resultString);
        assertTrue(resultString.contains("バックアップ結果"));
        assertTrue(resultString.contains("FULL"));
    }
    
    @Test
    public void testExcludePatterns() throws IOException {
        // 除外パターンに一致するファイルを作成
        Files.write(sourceDir.resolve("debug.log"), "Debug log content".getBytes());
        Files.write(sourceDir.resolve("cache.tmp"), "Cache content".getBytes());
        Files.write(sourceDir.resolve(".DS_Store"), "DS Store content".getBytes());
        
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(config);
        
        assertTrue(result.isSuccess());
        // 除外パターンに一致するファイルは含まれない
        assertEquals(3, result.getTotalFiles()); // file1.txt, file2.txt, subdir/file3.txt のみ
    }
    
    @Test
    public void testBackupWithNonExistentSource() {
        Path nonExistentSource = tempDir.resolve("nonexistent");
        BackupSystem.BackupConfig invalidConfig = BackupSystem.BackupConfig.defaultConfig(nonExistentSource, backupDir);
        
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(invalidConfig);
        
        assertFalse(result.isSuccess());
        assertFalse(result.getErrors().isEmpty());
    }
    
    @Test
    public void testMaxBackupCount() throws IOException, InterruptedException {
        // maxBackupCountを2に設定
        BackupSystem.BackupConfig limitedConfig = new BackupSystem.BackupConfig(
            sourceDir, backupDir, true, true, true, Set.of("*.tmp"), 2);
        
        // 3回バックアップを実行
        BackupSystem.performFullBackup(limitedConfig);
        Thread.sleep(1000);
        BackupSystem.performFullBackup(limitedConfig);
        Thread.sleep(1000);
        BackupSystem.performFullBackup(limitedConfig);
        
        // バックアップファイルの数を確認
        long backupFileCount = Files.list(backupDir)
                .filter(path -> path.getFileName().toString().startsWith("backup_full_"))
                .count();
        
        assertEquals(2, backupFileCount); // 最大2個まで保持
    }
    
    @Test
    public void testRestoreFromNonExistentBackup() {
        Path nonExistentBackup = backupDir.resolve("nonexistent.zip");
        
        int restoredFiles = BackupSystem.restoreFromBackup(nonExistentBackup, restoreDir, config);
        
        assertEquals(0, restoredFiles);
    }
    
    @Test
    public void testVerifyIntegrityWithoutIntegrityCheck() {
        BackupSystem.BackupConfig noIntegrityConfig = new BackupSystem.BackupConfig(
            sourceDir, backupDir, true, true, false, Set.of("*.tmp"), 5);
        
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(noIntegrityConfig);
        assertTrue(result.isSuccess());
        
        Path backupFile = Files.list(backupDir)
                .filter(path -> path.getFileName().toString().startsWith("backup_full_"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("バックアップファイルが見つかりません"));
        
        // 整合性チェックが無効の場合は常にtrueを返す
        boolean isValid = BackupSystem.verifyBackupIntegrity(backupFile, noIntegrityConfig);
        assertTrue(isValid);
    }
    
    @Test
    public void testEmptySourceDirectory() throws IOException {
        Path emptySource = tempDir.resolve("empty_source");
        Files.createDirectories(emptySource);
        
        BackupSystem.BackupConfig emptyConfig = BackupSystem.BackupConfig.defaultConfig(emptySource, backupDir);
        BackupSystem.BackupResult result = BackupSystem.performFullBackup(emptyConfig);
        
        assertTrue(result.isSuccess());
        assertEquals(0, result.getTotalFiles());
        assertEquals(0, result.getTotalSize());
    }
}