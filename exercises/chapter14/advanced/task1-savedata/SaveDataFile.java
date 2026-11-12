import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * セーブデータをバイナリ形式で読み書きする
 * 形式: 識別子(int) 版(int) 名前(UTF) レベル(int) 所持金(int) 持ち物の数(int) 持ち物(UTF)...
 */
public class SaveDataFile {
    /** ファイルの先頭に置く識別子（"SAVE"の4文字の文字コード） */
    public static final int MAGIC = 0x53415645;
    public static final int VERSION = 1;

    public static void save(Path path, SaveData data) throws IOException {
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(Files.newOutputStream(path)))) {
            out.writeInt(MAGIC);
            out.writeInt(VERSION);
            out.writeUTF(data.playerName());
            out.writeInt(data.level());
            out.writeInt(data.gold());
            out.writeInt(data.items().size());
            for (String item : data.items()) {
                out.writeUTF(item);
            }
        }
    }

    /** 書き込んだときと同じ順序・同じ型で読む。形式が違えばIOExceptionにする */
    public static SaveData load(Path path) throws IOException {
        try (DataInputStream in = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(path)))) {
            int magic = in.readInt();
            if (magic != MAGIC) {
                throw new IOException(path.getFileName() + " はセーブデータではありません（先頭が "
                        + "%08X".formatted(magic) + "）");
            }
            int version = in.readInt();
            if (version != VERSION) {
                throw new IOException(path.getFileName() + " は対応していない版です（版 " + version + "）");
            }
            String name = in.readUTF();
            int level = in.readInt();
            int gold = in.readInt();
            int count = in.readInt();
            List<String> items = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                items.add(in.readUTF());
            }
            return new SaveData(name, level, gold, items);
        } catch (EOFException e) {
            // 読み終わる前にファイルの終わりに来た
            throw new IOException(path.getFileName() + " は途中で切れています", e);
        }
    }

    /** 比べるために、同じ内容をテキストで保存する */
    public static void saveAsText(Path path, SaveData data) throws IOException {
        String text = "playerName=" + data.playerName() + "\n"
                + "level=" + data.level() + "\n"
                + "gold=" + data.gold() + "\n"
                + "items=" + String.join(",", data.items()) + "\n";
        Files.writeString(path, text, StandardCharsets.UTF_8);
    }
}
