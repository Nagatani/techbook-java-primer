import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * JARの中のリソース（時間割のCSVと設定ファイル）を読んで、時間割を表示する
 * 使い方: java -jar timetable.jar [曜日]
 */
public class TimetableApp {
    public static void main(String[] args) {
        if (args.length > 0 && Timetable.dayIndex(args[0]) < 0) {
            System.err.println("曜日は月〜金で指定してください: " + args[0]);
            System.exit(1);
        }
        try {
            Properties config = loadProperties("/app.properties");
            Timetable table = Timetable.parse(readLines("/timetable.csv"));

            IO.println(config.getProperty("app.name") + " " + config.getProperty("app.version"));
            IO.println(config.getProperty("term") + "（授業 " + table.countLessons() + "コマ）");

            String[] days = args.length > 0 ? new String[] {args[0]} : Timetable.DAYS;
            for (String day : days) {
                IO.println("[" + day + "曜日]");
                List<String> lessons = table.lessonsOf(day);
                if (lessons.isEmpty()) {
                    IO.println("  授業なし");
                }
                for (String lesson : lessons) {
                    IO.println("  " + lesson);
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("エラー: " + e.getMessage());
            System.exit(1);
        }
    }

    /** クラスパスからリソースを開く。見つからなければ例外にする */
    private static InputStream open(String name) throws IOException {
        InputStream in = TimetableApp.class.getResourceAsStream(name);
        if (in == null) {
            throw new IOException("リソース " + name + " が見つかりません（JARに入れ忘れていませんか）");
        }
        return in;
    }

    /** テキストのリソースをUTF-8で1行ずつ読む */
    private static List<String> readLines(String name) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(open(name), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /** プロパティファイルのリソースをUTF-8で読む */
    private static Properties loadProperties(String name) throws IOException {
        Properties props = new Properties();
        try (InputStreamReader reader = new InputStreamReader(open(name), StandardCharsets.UTF_8)) {
            props.load(reader);
        }
        return props;
    }
}
