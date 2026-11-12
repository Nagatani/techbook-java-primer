import java.util.List;

/** ゲームのセーブデータの中身 */
public record SaveData(String playerName, int level, int gold, List<String> items) {
}
