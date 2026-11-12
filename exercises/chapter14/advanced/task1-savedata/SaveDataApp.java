import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/** セーブデータの保存・読み込みと、壊れたファイルの検出を実演するmainメソッド */
public class SaveDataApp {
    public static void main(String[] args) throws IOException {
        Path dir = Path.of("savedata");
        Files.createDirectories(dir);
        Path saveFile = dir.resolve("slot1.dat");
        Path textFile = dir.resolve("slot1.txt");

        SaveData data = new SaveData("勇者アキラ", 12, 3450, List.of("やくそう", "どうのつるぎ", "かわのたて"));

        IO.println("=== 保存 ===");
        SaveDataFile.save(saveFile, data);
        SaveDataFile.saveAsText(textFile, data);
        IO.println(saveFile.getFileName() + ": " + Files.size(saveFile) + "バイト");
        IO.println(textFile.getFileName() + ": " + Files.size(textFile) + "バイト");

        IO.println();
        IO.println("=== slot1.dat の先頭16バイト ===");
        byte[] bytes = Files.readAllBytes(saveFile);
        IO.println(toHex(bytes, 0, 8) + "  ← 識別子 \"SAVE\" と 版 1");
        IO.println(toHex(bytes, 8, 16) + "  ← 名前の長さ（2バイト）と名前のUTF-8");

        IO.println();
        IO.println("=== 読み込み ===");
        SaveData loaded = SaveDataFile.load(saveFile);
        IO.println("名前: " + loaded.playerName());
        IO.println("レベル: " + loaded.level() + "、所持金: " + loaded.gold() + "G");
        IO.println("持ち物: " + loaded.items());
        IO.println("保存した内容と同じ: " + loaded.equals(data));

        IO.println();
        IO.println("=== 壊れたファイルの検出 ===");
        // テキストファイルを読ませる
        tryLoad(textFile);
        // 末尾を削って途中で切れたファイルを作る
        Path broken = dir.resolve("broken.dat");
        Files.write(broken, Arrays.copyOf(bytes, bytes.length - 5));
        tryLoad(broken);
        // 存在しないファイル
        tryLoad(dir.resolve("slot9.dat"));
    }

    private static void tryLoad(Path path) {
        try {
            SaveData data = SaveDataFile.load(path);
            IO.println(path.getFileName() + " を読み込みました: " + data);
        } catch (NoSuchFileException e) {
            IO.println("読み込めません: " + path.getFileName() + " がありません");
        } catch (IOException e) {
            String cause = e.getCause() == null ? "" : "（原因: " + e.getCause().getClass().getSimpleName() + "）";
            IO.println("読み込めません: " + e.getMessage() + cause);
        }
    }

    /** バイト配列のfromからtoの手前までを16進数で表す */
    private static String toHex(byte[] bytes, int from, int to) {
        StringBuilder sb = new StringBuilder();
        for (int i = from; i < to; i++) {
            sb.append("%02X ".formatted(bytes[i]));
        }
        return sb.toString().strip();
    }
}
