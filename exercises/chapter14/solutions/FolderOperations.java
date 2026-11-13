import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Stream;

/**
 * フォルダーの操作とバックアップ（課題2の解答例）
 *
 * 実行したフォルダーの下にfolder-workフォルダーを作る。Files.listの順序はOSで変わるので、名前順に並べてから使う。
 */
public class FolderOperations {
    public static void main(String[] args) {
        Path dir = Path.of("folder-work");
        Path backup = dir.resolve("backup");
        try {
            if (Files.notExists(dir)) {
                Files.createDirectory(dir);
            }
            if (Files.notExists(backup)) {
                Files.createDirectory(backup);
            }
            Files.writeString(dir.resolve("memo.txt"), "買い物: 牛乳、卵\n");
            Files.writeString(dir.resolve("report.txt"), "第14章のレポート\nファイル操作をまとめる\n");
            Files.writeString(dir.resolve("score.csv"), "名前,点数\n佐藤,80\n鈴木,92\n");
            IO.println("--- フォルダーの中身（名前順） ---");
            printFiles(dir);
            // .txtのファイルだけをバックアップへコピーする
            IO.println("\n--- .txtをバックアップ ---");
            for (Path file : listFiles(dir)) {
                if (file.getFileName().toString().endsWith(".txt")) {
                    Path target = backup.resolve(file.getFileName());
                    Files.copy(file, target, StandardCopyOption.REPLACE_EXISTING);
                    IO.println("コピー: " + file.getFileName() + " -> backup/" + target.getFileName());
                }
            }
            IO.println("\n--- 削除 ---");
            Files.delete(dir.resolve("memo.txt"));
            IO.println("memo.txtを削除しました");
            IO.println("memo.txtはある？: " + Files.exists(dir.resolve("memo.txt")));
            IO.println("backup/memo.txtはある？: " + Files.exists(backup.resolve("memo.txt")));
            IO.println("\n--- 削除したあとの中身 ---");
            printFiles(dir);
            IO.println("\n--- backupの中身 ---");
            printFiles(backup);
        } catch (IOException e) {
            System.err.println("入出力エラー: " + e.getMessage());
        }
    }

    // フォルダーの中のファイルを名前順のリストにする（サブフォルダーは除く）
    static List<Path> listFiles(Path dir) throws IOException {
        try (Stream<Path> entries = Files.list(dir)) {
            return entries.filter(Files::isRegularFile).sorted().toList();
        }
    }

    static void printFiles(Path dir) throws IOException {
        long total = 0;
        for (Path file : listFiles(dir)) {
            long size = Files.size(file);
            total += size;
            IO.println(file.getFileName() + " (" + size + "バイト)");
        }
        IO.println("合計: " + total + "バイト");
    }
}
