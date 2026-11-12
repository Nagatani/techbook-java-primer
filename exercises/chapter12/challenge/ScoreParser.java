import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 成績のデータの行を、Scoreのリストに変換する
 */
public class ScoreParser {
    // 1行から、科目の数だけのScoreを作る
    public static Stream<Score> parseLine(String line) {
        String[] parts = line.split(",");
        String id = parts[0];
        String name = parts[1];
        String className = parts[2];
        return Arrays.stream(parts[3].split(";"))
                .map(item -> item.split(":"))
                .map(kv -> new Score(id, name, className, kv[0], Integer.parseInt(kv[1])));
    }

    // すべての行を変換する。1行が複数のScoreになるのでflatMapで平らにする
    public static List<Score> parse(List<String> lines) {
        return lines.stream()
                .flatMap(ScoreParser::parseLine)
                .toList();
    }
}
