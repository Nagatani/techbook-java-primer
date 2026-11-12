/** 設定の読み込みと、例外の連鎖をたどる処理のmainメソッド */
public class ConfigApp {
    /** 読み込んだ設定の値をまとめたもの */
    record Settings(String playerName, int volume, int level) {}

    /** 設定を読み込む。どの段階の失敗もConfigExceptionとして呼び出し側に伝える */
    static Settings load(String[] lines) throws ConfigException {
        GameConfig config = GameConfig.parse(lines);
        String name = config.getRequired("playerName");
        int volume = config.getInt("volume", 50, 0, 100);
        int level = config.getInt("level", 1, 1, 5);
        return new Settings(name, volume, level);
    }

    public static void main(String[] args) {
        String[][] files = {
            {"# ゲームの設定", "playerName = さくら", "volume = 80", "level = 3"},
            {"playerName = たろう", "level = 2"},
            {"playerName = じろう", "volume = 大きめ"},
            {"playerName = はなこ", "level = 9"},
            {"playerName = ゆうき", "volume 30"},
            {"volume = 40"},
        };

        for (int i = 0; i < files.length; i++) {
            IO.println("=== 設定" + (i + 1) + " ===");
            try {
                Settings settings = load(files[i]);
                IO.println("読み込みました: 名前=" + settings.playerName()
                        + "、音量=" + settings.volume() + "、難易度=" + settings.level());
            } catch (ConfigException e) {
                printError(e);
                IO.println("既定の設定で起動します");
            }
            IO.println();
        }
    }

    /** 例外のメッセージと、getCause()でたどった原因を表示する */
    static void printError(ConfigException e) {
        String where = e.getLineNumber() > 0 ? e.getLineNumber() + "行目: " : "";
        IO.println("設定の誤り: " + where + e.getMessage());
        Throwable cause = e.getCause();
        while (cause != null) {
            IO.println("  原因: " + cause.getClass().getName() + ": " + cause.getMessage());
            cause = cause.getCause();
        }
    }
}
