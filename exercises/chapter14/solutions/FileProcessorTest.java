import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import java.util.EnumSet;
import java.util.List;

/**
 * FileProcessorクラスのテストクラス
 */
public class FileProcessorTest {
    
    @TempDir
    Path tempDir;
    
    private Path testFile;
    private Path testDir;
    
    @BeforeEach
    public void setUp() throws IOException {
        testFile = tempDir.resolve("test.txt");
        testDir = tempDir.resolve("testdir");
        
        Files.write(testFile, "Hello, World!\nThis is a test file.\nLine 3".getBytes());
        Files.createDirectories(testDir);
        Files.write(testDir.resolve("nested.txt"), "Nested file content".getBytes());
    }
    
    @Test
    public void testGetFileInfo() throws IOException {
        FileProcessor.FileInfo info = FileProcessor.getFileInfo(testFile);
        
        assertNotNull(info);
        assertEquals(testFile, info.getPath());
        assertTrue(info.getSize() > 0);
        assertNotNull(info.getLastModified());
        assertFalse(info.isDirectory());
        assertNotNull(info.getPermissions());
    }
    
    @Test
    public void testGetFileInfoDirectory() throws IOException {
        FileProcessor.FileInfo info = FileProcessor.getFileInfo(testDir);
        
        assertNotNull(info);
        assertEquals(testDir, info.getPath());
        assertNotNull(info.getLastModified());
        assertTrue(info.isDirectory());
    }
    
    @Test
    public void testGetFileInfoNonExistent() {
        Path nonExistent = tempDir.resolve("nonexistent.txt");
        
        assertThrows(IOException.class, () -> {
            FileProcessor.getFileInfo(nonExistent);
        });
    }
    
    @Test
    public void testCopyFile() throws IOException {
        Path target = tempDir.resolve("copied.txt");
        
        boolean result = FileProcessor.copyFile(testFile, target, false);
        
        assertTrue(result);
        assertTrue(Files.exists(target));
        assertEquals(Files.size(testFile), Files.size(target));
        assertTrue(FileProcessor.compareFiles(testFile, target));
    }
    
    @Test
    public void testCopyFileWithReplacement() throws IOException {
        Path target = tempDir.resolve("existing.txt");
        Files.write(target, "Original content".getBytes());
        
        boolean result = FileProcessor.copyFile(testFile, target, true);
        
        assertTrue(result);
        assertTrue(Files.exists(target));
        assertTrue(FileProcessor.compareFiles(testFile, target));
    }
    
    @Test
    public void testCopyFileNonExistentSource() {
        Path nonExistent = tempDir.resolve("nonexistent.txt");
        Path target = tempDir.resolve("target.txt");
        
        assertThrows(IOException.class, () -> {
            FileProcessor.copyFile(nonExistent, target, false);
        });
    }
    
    @Test
    public void testMoveFile() throws IOException {
        Path target = tempDir.resolve("moved.txt");
        String originalContent = Files.readString(testFile);
        
        boolean result = FileProcessor.moveFile(testFile, target, false);
        
        assertTrue(result);
        assertFalse(Files.exists(testFile));
        assertTrue(Files.exists(target));
        assertEquals(originalContent, Files.readString(target));
    }
    
    @Test
    public void testCopyDirectory() throws IOException {
        Path targetDir = tempDir.resolve("copied_dir");
        
        int fileCount = FileProcessor.copyDirectory(testDir, targetDir, false);
        
        assertTrue(fileCount > 0);
        assertTrue(Files.exists(targetDir));
        assertTrue(Files.exists(targetDir.resolve("nested.txt")));
        assertTrue(FileProcessor.compareFiles(
            testDir.resolve("nested.txt"), 
            targetDir.resolve("nested.txt")
        ));
    }
    
    @Test
    public void testDeleteRecursively() throws IOException {
        Path deleteTarget = tempDir.resolve("delete_test");
        Files.createDirectories(deleteTarget);
        Files.write(deleteTarget.resolve("file1.txt"), "Content 1".getBytes());
        Files.write(deleteTarget.resolve("file2.txt"), "Content 2".getBytes());
        
        int deletedCount = FileProcessor.deleteRecursively(deleteTarget);
        
        assertTrue(deletedCount >= 3); // ディレクトリ + 2つのファイル
        assertFalse(Files.exists(deleteTarget));
    }
    
    @Test
    public void testDeleteNonExistent() throws IOException {
        Path nonExistent = tempDir.resolve("nonexistent");
        
        int deletedCount = FileProcessor.deleteRecursively(nonExistent);
        
        assertEquals(0, deletedCount);
    }
    
    @Test
    public void testSearchFiles() throws IOException {
        // 追加のテストファイルを作成
        Files.write(tempDir.resolve("test1.java"), "Java code".getBytes());
        Files.write(tempDir.resolve("test2.java"), "More Java code".getBytes());
        Files.write(tempDir.resolve("readme.md"), "Markdown content".getBytes());
        
        FileProcessor.SearchResult result = FileProcessor.searchFiles(tempDir, "*.java", false);
        
        assertNotNull(result);
        assertEquals(2, result.getFileCount());
        assertEquals(0, result.getDirectoryCount());
        assertTrue(result.getTotalSize() > 0);
        
        List<Path> javaFiles = result.getFiles();
        assertTrue(javaFiles.stream().anyMatch(p -> p.getFileName().toString().equals("test1.java")));
        assertTrue(javaFiles.stream().anyMatch(p -> p.getFileName().toString().equals("test2.java")));
    }
    
    @Test
    public void testSearchFilesRecursive() throws IOException {
        Files.write(testDir.resolve("nested.java"), "Nested Java code".getBytes());
        
        FileProcessor.SearchResult result = FileProcessor.searchFiles(tempDir, "*.java", true);
        
        assertTrue(result.getFileCount() >= 1);
        assertTrue(result.getFiles().stream()
            .anyMatch(p -> p.getFileName().toString().equals("nested.java")));
    }
    
    @Test
    public void testCompareFiles() throws IOException {
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");
        Path file3 = tempDir.resolve("file3.txt");
        
        String content = "Same content";
        Files.write(file1, content.getBytes());
        Files.write(file2, content.getBytes());
        Files.write(file3, "Different content".getBytes());
        
        assertTrue(FileProcessor.compareFiles(file1, file2));
        assertFalse(FileProcessor.compareFiles(file1, file3));
        assertFalse(FileProcessor.compareFiles(file1, tempDir.resolve("nonexistent.txt")));
    }
    
    @Test
    public void testCountLines() throws IOException {
        long lineCount = FileProcessor.countLines(testFile);
        
        assertEquals(3, lineCount);
    }
    
    @Test
    public void testCountLinesEmptyFile() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.write(emptyFile, new byte[0]);
        
        long lineCount = FileProcessor.countLines(emptyFile);
        
        assertEquals(0, lineCount);
    }
    
    @Test
    public void testSearchInFile() throws IOException {
        List<Integer> lineNumbers = FileProcessor.searchInFile(testFile, "test", false);
        
        assertEquals(1, lineNumbers.size());
        assertEquals(2, lineNumbers.get(0).intValue());
    }
    
    @Test
    public void testSearchInFileIgnoreCase() throws IOException {
        List<Integer> lineNumbers = FileProcessor.searchInFile(testFile, "HELLO", true);
        
        assertEquals(1, lineNumbers.size());
        assertEquals(1, lineNumbers.get(0).intValue());
    }
    
    @Test
    public void testSearchInFileNotFound() throws IOException {
        List<Integer> lineNumbers = FileProcessor.searchInFile(testFile, "nonexistent", false);
        
        assertTrue(lineNumbers.isEmpty());
    }
    
    @Test
    public void testSetFilePermissions() throws IOException {
        Set<PosixFilePermission> permissions = EnumSet.of(
            PosixFilePermission.OWNER_READ,
            PosixFilePermission.OWNER_WRITE
        );
        
        // このテストはPOSIXをサポートするシステムでのみ動作する
        try {
            FileProcessor.setFilePermissions(testFile, permissions);
            // 例外が発生しなければ成功
            assertTrue(true);
        } catch (UnsupportedOperationException e) {
            // POSIXがサポートされていない場合（例：Windows）
            assertTrue(true);
        }
    }
    
    @Test
    public void testCalculateDirectorySize() throws IOException {
        long size = FileProcessor.calculateDirectorySize(testDir);
        
        assertTrue(size > 0);
        assertTrue(size >= Files.size(testDir.resolve("nested.txt")));
    }
    
    @Test
    public void testCalculateDirectorySizeNonExistent() {
        Path nonExistent = tempDir.resolve("nonexistent_dir");
        
        assertThrows(IllegalArgumentException.class, () -> {
            FileProcessor.calculateDirectorySize(nonExistent);
        });
    }
    
    @Test
    public void testFormatFileSize() {
        assertEquals("0 B", FileProcessor.formatFileSize(0));
        assertEquals("1023 B", FileProcessor.formatFileSize(1023));
        assertEquals("1.0 KB", FileProcessor.formatFileSize(1024));
        assertEquals("1.5 KB", FileProcessor.formatFileSize(1536));
        assertEquals("1.0 MB", FileProcessor.formatFileSize(1024 * 1024));
        assertEquals("1.0 GB", FileProcessor.formatFileSize(1024L * 1024 * 1024));
    }
    
    @Test
    public void testCreateTempFile() throws IOException {
        Path tempFile = FileProcessor.createTempFile("test", ".tmp");
        
        assertNotNull(tempFile);
        assertTrue(Files.exists(tempFile));
        assertTrue(tempFile.getFileName().toString().startsWith("test"));
        assertTrue(tempFile.getFileName().toString().endsWith(".tmp"));
        
        // クリーンアップ
        Files.deleteIfExists(tempFile);
    }
    
    @Test
    public void testCreateTempDirectory() throws IOException {
        Path tempDirectory = FileProcessor.createTempDirectory("testdir");
        
        assertNotNull(tempDirectory);
        assertTrue(Files.exists(tempDirectory));
        assertTrue(Files.isDirectory(tempDirectory));
        assertTrue(tempDirectory.getFileName().toString().startsWith("testdir"));
        
        // クリーンアップ
        Files.deleteIfExists(tempDirectory);
    }
    
    @Test
    public void testFileInfoToString() throws IOException {
        FileProcessor.FileInfo info = FileProcessor.getFileInfo(testFile);
        String str = info.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("test.txt"));
        assertTrue(str.contains("FILE"));
        assertTrue(str.contains("bytes"));
    }
    
    @Test
    public void testSearchResultToString() throws IOException {
        FileProcessor.SearchResult result = FileProcessor.searchFiles(tempDir, "*", false);
        String str = result.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("検索結果"));
        assertTrue(str.contains("個のファイル"));
        assertTrue(str.contains("合計サイズ"));
    }
    
    @Test
    public void testCompareFilesDifferentSizes() throws IOException {
        Path file1 = tempDir.resolve("small.txt");
        Path file2 = tempDir.resolve("large.txt");
        
        Files.write(file1, "small".getBytes());
        Files.write(file2, "much larger content".getBytes());
        
        assertFalse(FileProcessor.compareFiles(file1, file2));
    }
    
    @Test
    public void testMoveFileNonExistent() {
        Path nonExistent = tempDir.resolve("nonexistent.txt");
        Path target = tempDir.resolve("target.txt");
        
        assertThrows(IOException.class, () -> {
            FileProcessor.moveFile(nonExistent, target, false);
        });
    }
}