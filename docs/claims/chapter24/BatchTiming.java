// 第24章「バッチ処理」：H2をプログラムのなかで動かすと1件ずつとバッチの差は数ミリ秒で、
// サーバーとして起動してネットワーク経由で接続すると、バッチのほうが大きく速くなる
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class BatchTiming {
    static long run(String url, boolean batch) throws SQLException {
        try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
            try (Statement st = conn.createStatement()) {
                st.execute("DROP TABLE IF EXISTS daily_steps");
                st.execute("CREATE TABLE daily_steps (walk_date DATE PRIMARY KEY, steps INT NOT NULL)");
            }
            LocalDate first = LocalDate.of(2025, 1, 1);
            long start = System.nanoTime();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO daily_steps VALUES (?, ?)")) {
                for (int i = 0; i < 3650; i++) {
                    ps.setObject(1, first.plusDays(i));
                    ps.setInt(2, 5000 + (i * 37) % 6000);
                    if (batch) {
                        ps.addBatch();
                        if ((i + 1) % 1000 == 0) {
                            ps.executeBatch();
                        }
                    } else {
                        ps.executeUpdate();
                    }
                }
                if (batch) {
                    ps.executeBatch();
                }
                conn.commit();
            }
            return System.nanoTime() - start;
        }
    }

    // 何度か動かしてから、5回のうちいちばん速い時間（ミリ秒）を返す
    static long best(String url, boolean batch) throws SQLException {
        for (int i = 0; i < 5; i++) {
            run(url, batch);
        }
        long best = Long.MAX_VALUE;
        for (int i = 0; i < 5; i++) {
            best = Math.min(best, run(url, batch));
        }
        return best / 1_000_000;
    }

    public static void main(String[] args) throws Exception {
        String embedded = "jdbc:h2:mem:batch24";
        long es = best(embedded, false);
        long eb = best(embedded, true);
        System.err.println("組み込み: 1件ずつ " + es + "ms、バッチ " + eb + "ms");
        IO.println("組み込み: 差は10ミリ秒未満: " + (es - eb < 10));

        org.h2.tools.Server server = org.h2.tools.Server.createTcpServer(
                "-tcpPort", "0", "-ifNotExists", "-tcpDaemon").start();
        try {
            String tcp = "jdbc:h2:tcp://localhost:" + server.getPort() + "/mem:batch24tcp";
            long ts = best(tcp, false);
            long tb = best(tcp, true);
            System.err.println("ネットワーク経由: 1件ずつ " + ts + "ms、バッチ " + tb + "ms");
            IO.println("ネットワーク経由: バッチは1件ずつの3分の1未満: " + (tb * 3 < ts));
        } finally {
            server.stop();
        }
        System.exit(0);
    }
}
