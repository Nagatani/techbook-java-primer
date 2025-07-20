import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileTimeExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        Path logDir = Paths.get("logs");
        Files.createDirectories(logDir);
        
        // 1. タイムスタンプ付きログファイルの作成
        DateTimeFormatter fileNameFormatter = 
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(fileNameFormatter);
        Path logFile = logDir.resolve("app_" + timestamp + ".log");
        
        // ログエントリーの書き込み
        DateTimeFormatter logFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        List<String> logEntries = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            String entry = String.format("[%s] INFO: Processing item %d",
                LocalDateTime.now().format(logFormatter), i);
            logEntries.add(entry);
            Thread.sleep(100); // 処理のシミュレーション
        }
        
        Files.write(logFile, logEntries);
        System.out.println("ログファイル作成: " + logFile);
        
        // 2. ファイルの更新日時を取得
        BasicFileAttributes attrs = Files.readAttributes(
            logFile, BasicFileAttributes.class);
        
        FileTime creationTime = attrs.creationTime();
        FileTime lastModified = attrs.lastModifiedTime();
        FileTime lastAccess = attrs.lastAccessTime();
        
        System.out.println("\nファイル時刻情報:");
        System.out.println("作成日時: " + 
            LocalDateTime.ofInstant(creationTime.toInstant(), 
                                  ZoneId.systemDefault()));
        System.out.println("最終更新: " + 
            LocalDateTime.ofInstant(lastModified.toInstant(), 
                                  ZoneId.systemDefault()));
        System.out.println("最終アクセス: " + 
            LocalDateTime.ofInstant(lastAccess.toInstant(), 
                                  ZoneId.systemDefault()));
        
        // 3. 古いログファイルの検索と削除
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);
        
        System.out.println("\n7日以上前のログファイルを検索...");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                logDir, "*.log")) {
            for (Path file : stream) {
                FileTime fileTime = Files.getLastModifiedTime(file);
                LocalDateTime fileDateTime = LocalDateTime.ofInstant(
                    fileTime.toInstant(), ZoneId.systemDefault());
                
                if (fileDateTime.isBefore(cutoffTime)) {
                    System.out.println("古いファイル発見: " + file);
                    // Files.delete(file); // 実際の削除はコメントアウト
                }
            }
        }
        
        // 4. ファイルの更新日時を変更
        LocalDateTime newDateTime = LocalDateTime.now().minusHours(3);
        FileTime newFileTime = FileTime.from(
            newDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Files.setLastModifiedTime(logFile, newFileTime);
        System.out.println("\n更新日時を3時間前に変更しました");
    }
}