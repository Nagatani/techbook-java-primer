import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * アクセスログの集計（課題4の解答例）
 */
public class LogAnalyzer {
    record LogEntry(String time, String ip, String method, String path, int status) {}

    public static void main(String[] args) {
        Path dir = Path.of("log-work");
        Path log = dir.resolve("access.log");
        try {
            if (Files.notExists(dir)) {
                Files.createDirectory(dir);
            }
            Files.writeString(log, """
                    10:00:01 192.168.1.10 GET /index.html 200
                    10:00:05 192.168.1.11 GET /about.html 200
                    10:01:12 192.168.1.10 POST /login 302
                    10:02:30 192.168.1.12 GET /index.html 200
                    10:02:31 192.168.1.12 GET /style.css 404
                    この行は形式が違う
                    10:05:00 192.168.1.11 GET /index.html 200
                    10:07:45 192.168.1.13 GET /admin 403
                    10:08:10 192.168.1.10 GET /about.html 200
                    10:09:59 192.168.1.14 GET /index.html 500
                    """, StandardCharsets.UTF_8);
            List<LogEntry> entries = new ArrayList<>();
            int skipped = 0;
            try (BufferedReader reader = Files.newBufferedReader(log, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] f = line.split(" ");
                    if (f.length != 5) {
                        skipped++;
                        continue;
                    }
                    entries.add(new LogEntry(f[0], f[1], f[2], f[3], Integer.parseInt(f[4])));
                }
            }
            IO.println("解析した行: " + entries.size() + "、形式が違う行: " + skipped);
            Map<Integer, Integer> byStatus = new TreeMap<>();
            Map<String, Integer> byPath = new TreeMap<>();
            Map<String, Integer> byIp = new TreeMap<>();
            for (LogEntry e : entries) {
                byStatus.merge(e.status(), 1, Integer::sum);
                byPath.merge(e.path(), 1, Integer::sum);
                byIp.merge(e.ip(), 1, Integer::sum);
            }
            IO.println("\n--- ステータス別 ---");
            for (Map.Entry<Integer, Integer> e : byStatus.entrySet()) {
                IO.println(e.getKey() + ": " + e.getValue() + "件");
            }
            IO.println("\n--- ページ別（多い順） ---");
            byPath.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> IO.println(e.getKey() + ": " + e.getValue() + "件"));
            IO.println("\n異なるIPアドレスの数: " + byIp.size());
            IO.println("\n--- エラー（400以上） ---");
            for (LogEntry e : entries) {
                if (e.status() >= 400) {
                    IO.println(e.time() + " " + e.path() + " " + e.status());
                }
            }
        } catch (IOException e) {
            System.err.println("入出力エラー: " + e.getMessage());
        }
    }
}
