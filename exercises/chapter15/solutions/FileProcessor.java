import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 基本的なファイル操作クラス
 * NIO.2 APIを活用した高性能なファイル処理
 */
public class FileProcessor {
    
    private static final Logger logger = Logger.getLogger(FileProcessor.class.getName());
    
    /**
     * ファイル情報を表すクラス
     */
    public static class FileInfo {
        private final Path path;
        private final long size;
        private final LocalDateTime lastModified;
        private final boolean isDirectory;
        private final Set<PosixFilePermission> permissions;
        
        public FileInfo(Path path, long size, LocalDateTime lastModified, 
                       boolean isDirectory, Set<PosixFilePermission> permissions) {
            this.path = path;
            this.size = size;
            this.lastModified = lastModified;
            this.isDirectory = isDirectory;
            this.permissions = permissions;
        }
        
        public Path getPath() { return path; }
        public long getSize() { return size; }
        public LocalDateTime getLastModified() { return lastModified; }
        public boolean isDirectory() { return isDirectory; }
        public Set<PosixFilePermission> getPermissions() { return permissions; }
        
        @Override
        public String toString() {
            return String.format("%s [%s, %d bytes, %s]", 
                path.getFileName(), 
                isDirectory ? "DIR" : "FILE",
                size,
                lastModified);
        }
    }
    
    /**
     * ファイル検索結果を表すクラス
     */
    public static class SearchResult {
        private final List<Path> files;
        private final long totalSize;
        private final int directoryCount;
        private final int fileCount;
        
        public SearchResult(List<Path> files, long totalSize, int directoryCount, int fileCount) {
            this.files = new ArrayList<>(files);
            this.totalSize = totalSize;
            this.directoryCount = directoryCount;
            this.fileCount = fileCount;
        }
        
        public List<Path> getFiles() { return new ArrayList<>(files); }
        public long getTotalSize() { return totalSize; }
        public int getDirectoryCount() { return directoryCount; }
        public int getFileCount() { return fileCount; }
        
        @Override
        public String toString() {
            return String.format("検索結果: %d個のファイル, %d個のディレクトリ, 合計サイズ: %d bytes",
                fileCount, directoryCount, totalSize);
        }
    }
    
    /**
     * ファイルの詳細情報を取得
     * @param filePath ファイルパス
     * @return ファイル情報
     * @throws IOException ファイル操作エラー
     */
    public static FileInfo getFileInfo(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("ファイルが見つかりません: " + filePath);
        }
        
        BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
        
        long size = attrs.size();
        LocalDateTime lastModified = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(attrs.lastModifiedTime().toMillis()),
            ZoneId.systemDefault()
        );
        boolean isDirectory = attrs.isDirectory();
        
        // POSIX権限の取得（可能な場合）
        Set<PosixFilePermission> permissions = null;
        try {
            permissions = Files.getPosixFilePermissions(filePath);
        } catch (UnsupportedOperationException e) {
            // Windows等でPOSIX権限がサポートされていない場合
            permissions = EnumSet.noneOf(PosixFilePermission.class);
        }
        
        return new FileInfo(filePath, size, lastModified, isDirectory, permissions);
    }
    
    /**
     * ファイルをコピー
     * @param source コピー元
     * @param target コピー先
     * @param replaceExisting 既存ファイルを置換するか
     * @return 成功した場合true
     * @throws IOException ファイル操作エラー
     */
    public static boolean copyFile(Path source, Path target, boolean replaceExisting) throws IOException {
        if (!Files.exists(source)) {
            throw new FileNotFoundException("コピー元ファイルが見つかりません: " + source);
        }
        
        // 親ディレクトリを作成
        Path parentDir = target.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        CopyOption[] options = replaceExisting ? 
            new CopyOption[]{StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES} :
            new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES};
        
        Files.copy(source, target, options);
        
        logger.info("ファイルコピー完了: " + source + " -> " + target);
        return true;
    }
    
    /**
     * ファイルを移動
     * @param source 移動元
     * @param target 移動先
     * @param replaceExisting 既存ファイルを置換するか
     * @return 成功した場合true
     * @throws IOException ファイル操作エラー
     */
    public static boolean moveFile(Path source, Path target, boolean replaceExisting) throws IOException {
        if (!Files.exists(source)) {
            throw new FileNotFoundException("移動元ファイルが見つかりません: " + source);
        }
        
        // 親ディレクトリを作成
        Path parentDir = target.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        CopyOption[] options = replaceExisting ? 
            new CopyOption[]{StandardCopyOption.REPLACE_EXISTING} :
            new CopyOption[]{};
        
        Files.move(source, target, options);
        
        logger.info("ファイル移動完了: " + source + " -> " + target);
        return true;
    }
    
    /**
     * ディレクトリを再帰的にコピー
     * @param source コピー元ディレクトリ
     * @param target コピー先ディレクトリ
     * @param replaceExisting 既存ファイルを置換するか
     * @return コピーしたファイル数
     * @throws IOException ファイル操作エラー
     */
    public static int copyDirectory(Path source, Path target, boolean replaceExisting) throws IOException {
        if (!Files.exists(source) || !Files.isDirectory(source)) {
            throw new IllegalArgumentException("コピー元ディレクトリが存在しません: " + source);
        }
        
        final int[] fileCount = {0};
        
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = target.resolve(source.relativize(dir));
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = target.resolve(source.relativize(file));
                copyFile(file, targetFile, replaceExisting);
                fileCount[0]++;
                return FileVisitResult.CONTINUE;
            }
        });
        
        logger.info("ディレクトリコピー完了: " + fileCount[0] + " 個のファイル");
        return fileCount[0];
    }
    
    /**
     * ファイルを削除（ディレクトリの場合は再帰的に削除）
     * @param path 削除対象のパス
     * @return 削除したファイル数
     * @throws IOException ファイル操作エラー
     */
    public static int deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return 0;
        }
        
        final int[] deletedCount = {0};
        
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                deletedCount[0]++;
                return FileVisitResult.CONTINUE;
            }
            
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                deletedCount[0]++;
                return FileVisitResult.CONTINUE;
            }
        });
        
        logger.info("削除完了: " + deletedCount[0] + " 個のファイル/ディレクトリ");
        return deletedCount[0];
    }
    
    /**
     * 指定されたパターンでファイルを検索
     * @param searchRoot 検索開始ディレクトリ
     * @param pattern ファイル名パターン（glob形式）
     * @param recursive 再帰的に検索するか
     * @return 検索結果
     * @throws IOException ファイル操作エラー
     */
    public static SearchResult searchFiles(Path searchRoot, String pattern, boolean recursive) throws IOException {
        if (!Files.exists(searchRoot) || !Files.isDirectory(searchRoot)) {
            throw new IllegalArgumentException("検索開始ディレクトリが存在しません: " + searchRoot);
        }
        
        List<Path> foundFiles = new ArrayList<>();
        long totalSize = 0;
        int directoryCount = 0;
        int fileCount = 0;
        
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        
        try (Stream<Path> paths = recursive ? 
                Files.walk(searchRoot) : 
                Files.list(searchRoot)) {
            
            List<Path> allPaths = paths.collect(Collectors.toList());
            
            for (Path path : allPaths) {
                if (matcher.matches(path.getFileName())) {
                    foundFiles.add(path);
                    
                    if (Files.isDirectory(path)) {
                        directoryCount++;
                    } else {
                        fileCount++;
                        totalSize += Files.size(path);
                    }
                }
            }
        }
        
        return new SearchResult(foundFiles, totalSize, directoryCount, fileCount);
    }
    
    /**
     * ファイルの内容を比較
     * @param file1 比較対象ファイル1
     * @param file2 比較対象ファイル2
     * @return 同じ内容の場合true
     * @throws IOException ファイル操作エラー
     */
    public static boolean compareFiles(Path file1, Path file2) throws IOException {
        if (!Files.exists(file1) || !Files.exists(file2)) {
            return false;
        }
        
        if (Files.size(file1) != Files.size(file2)) {
            return false;
        }
        
        try (InputStream is1 = Files.newInputStream(file1);
             InputStream is2 = Files.newInputStream(file2)) {
            
            byte[] buffer1 = new byte[8192];
            byte[] buffer2 = new byte[8192];
            
            int bytesRead1, bytesRead2;
            while ((bytesRead1 = is1.read(buffer1)) != -1) {
                bytesRead2 = is2.read(buffer2);
                
                if (bytesRead1 != bytesRead2) {
                    return false;
                }
                
                if (!Arrays.equals(buffer1, 0, bytesRead1, buffer2, 0, bytesRead2)) {
                    return false;
                }
            }
            
            return is2.read() == -1; // 両方のファイルが同時に終了することを確認
        }
    }
    
    /**
     * テキストファイルの行数をカウント
     * @param filePath ファイルパス
     * @return 行数
     * @throws IOException ファイル操作エラー
     */
    public static long countLines(Path filePath) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("ファイルが見つかりません: " + filePath);
        }
        
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.count();
        }
    }
    
    /**
     * テキストファイル内の特定の文字列を検索
     * @param filePath ファイルパス
     * @param searchText 検索文字列
     * @param ignoreCase 大文字小文字を無視するか
     * @return 該当する行番号のリスト
     * @throws IOException ファイル操作エラー
     */
    public static List<Integer> searchInFile(Path filePath, String searchText, boolean ignoreCase) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("ファイルが見つかりません: " + filePath);
        }
        
        List<Integer> lineNumbers = new ArrayList<>();
        String targetText = ignoreCase ? searchText.toLowerCase() : searchText;
        
        try (Stream<String> lines = Files.lines(filePath)) {
            int lineNumber = 1;
            Iterator<String> iterator = lines.iterator();
            
            while (iterator.hasNext()) {
                String line = iterator.next();
                String searchLine = ignoreCase ? line.toLowerCase() : line;
                
                if (searchLine.contains(targetText)) {
                    lineNumbers.add(lineNumber);
                }
                lineNumber++;
            }
        }
        
        return lineNumbers;
    }
    
    /**
     * ファイルの権限を変更
     * @param filePath ファイルパス
     * @param permissions 設定する権限
     * @throws IOException ファイル操作エラー
     */
    public static void setFilePermissions(Path filePath, Set<PosixFilePermission> permissions) throws IOException {
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("ファイルが見つかりません: " + filePath);
        }
        
        try {
            Files.setPosixFilePermissions(filePath, permissions);
            logger.info("権限設定完了: " + filePath + " -> " + permissions);
        } catch (UnsupportedOperationException e) {
            logger.warning("POSIX権限はサポートされていません: " + filePath);
        }
    }
    
    /**
     * ディレクトリのサイズを計算
     * @param directory ディレクトリパス
     * @return ディレクトリサイズ（バイト）
     * @throws IOException ファイル操作エラー
     */
    public static long calculateDirectorySize(Path directory) throws IOException {
        if (!Files.exists(directory) || !Files.isDirectory(directory)) {
            throw new IllegalArgumentException("ディレクトリが存在しません: " + directory);
        }
        
        final long[] totalSize = {0};
        
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                totalSize[0] += attrs.size();
                return FileVisitResult.CONTINUE;
            }
        });
        
        return totalSize[0];
    }
    
    /**
     * ファイルサイズを人間が読みやすい形式に変換
     * @param bytes バイト数
     * @return 人間が読みやすい形式の文字列
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
    
    /**
     * 一時ファイルを作成
     * @param prefix ファイル名のプレフィックス
     * @param suffix ファイル名のサフィックス
     * @return 作成された一時ファイルのパス
     * @throws IOException ファイル操作エラー
     */
    public static Path createTempFile(String prefix, String suffix) throws IOException {
        Path tempFile = Files.createTempFile(prefix, suffix);
        logger.info("一時ファイル作成: " + tempFile);
        return tempFile;
    }
    
    /**
     * 一時ディレクトリを作成
     * @param prefix ディレクトリ名のプレフィックス
     * @return 作成された一時ディレクトリのパス
     * @throws IOException ファイル操作エラー
     */
    public static Path createTempDirectory(String prefix) throws IOException {
        Path tempDir = Files.createTempDirectory(prefix);
        logger.info("一時ディレクトリ作成: " + tempDir);
        return tempDir;
    }
}