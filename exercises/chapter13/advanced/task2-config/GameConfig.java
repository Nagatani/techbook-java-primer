import java.util.HashMap;
import java.util.Map;

/** 「キー=値」の行からなるゲームの設定 */
public class GameConfig {
    /** 値と、その値が書かれていた行の番号 */
    private record Entry(String value, int lineNumber) {}

    private final Map<String, Entry> entries = new HashMap<>();

    private GameConfig() {
    }

    /** 設定の行を読み込む。書式の誤りがあれば、その行の番号を付けて知らせる */
    public static GameConfig parse(String[] lines) throws ConfigException {
        GameConfig config = new GameConfig();
        for (int i = 0; i < lines.length; i++) {
            int lineNumber = i + 1;
            String line = lines[i].strip();
            if (line.isEmpty() || line.startsWith("#")) {
                continue;  // 空行とコメントは読み飛ばす
            }
            int eq = line.indexOf('=');
            if (eq <= 0) {
                throw new ConfigException("「キー=値」の形ではありません: " + line, lineNumber);
            }
            String key = line.substring(0, eq).strip();
            String value = line.substring(eq + 1).strip();
            if (config.entries.containsKey(key)) {
                throw new ConfigException("キー " + key + " が2回書かれています", lineNumber);
            }
            config.entries.put(key, new Entry(value, lineNumber));
        }
        return config;
    }

    /** 整数の値を返す。キーがなければ既定値を返す */
    public int getInt(String key, int defaultValue, int min, int max) throws ConfigException {
        Entry entry = entries.get(key);
        if (entry == null) {
            return defaultValue;
        }
        int value;
        try {
            value = Integer.parseInt(entry.value());
        } catch (NumberFormatException e) {
            // 低い層の例外を、設定の言葉で説明した例外に包み直す。原因はcauseに残す
            throw new ConfigException(key + " の値が整数ではありません", entry.lineNumber(), e);
        }
        if (value < min || value > max) {
            throw new ConfigException(
                    key + " は" + min + "から" + max + "の範囲で指定してください（" + value + "）",
                    entry.lineNumber());
        }
        return value;
    }

    /** 文字列の値を返す。必須のキーがなければ例外にする */
    public String getRequired(String key) throws ConfigException {
        Entry entry = entries.get(key);
        if (entry == null) {
            throw new ConfigException("必須のキー " + key + " がありません", 0);
        }
        return entry.value();
    }
}
