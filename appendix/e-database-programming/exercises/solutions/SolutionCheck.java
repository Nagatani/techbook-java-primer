import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * 解答例の動作確認（課題1〜5）
 *
 * 期待する値と実際の値を比べ、「OK」か「NG」を表示します。
 * 解答例を書き換えたら実行し、すべてOKになることを確かめます。
 * 確認ごとに別の名前のメモリ上のデータベースを使うので、確認どうしは影響しません。
 */
public class SolutionCheck {
    private static int total = 0;
    private static int failed = 0;
    private static int dbCount = 0;

    public static void main(String[] args) {
        IO.println("=== 付録E 解答例の動作確認 ===");
        try {
            checkConnectAndCreate();
            checkTodoCrud();
            checkBookSearch();
            checkScoreReport();
            checkSeatReservation();
        } catch (SQLException e) {
            failed++;
            IO.println("NG 予期しないSQLException: " + e.getMessage());
        }
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    /** 確認ごとに新しいメモリ上のデータベースにつなぐ */
    private static Connection newConnection() throws SQLException {
        dbCount++;
        return DriverManager.getConnection("jdbc:h2:mem:check" + dbCount, "sa", "");
    }

    private static void checkConnectAndCreate() throws SQLException {
        try (Connection conn = newConnection()) {
            ConnectAndCreate.createTable(conn);
            check("課題1: 登録した件数", 4, ConnectAndCreate.insertSampleCourses(conn));
            check("課題1: テーブルの件数", 4, ConnectAndCreate.countCourses(conn));
            check("課題1: 同じテーブルは作れない（42S01）", "42S01", sqlStateOf(() -> ConnectAndCreate.createTable(conn)));
        }
        try (Connection conn = newConnection()) {
            check("課題1: 新しいデータベースにはテーブルがない（42S04）", "42S04",
                    sqlStateOf(() -> ConnectAndCreate.countCourses(conn)));
        }
    }

    private static void checkTodoCrud() throws SQLException {
        try (Connection conn = newConnection()) {
            TodoCrud.createTable(conn);
            check("課題2: 最初のIDは1", 1, TodoCrud.add(conn, "レポートを出す"));
            check("課題2: 次のIDは2", 2, TodoCrud.add(conn, "本を返す"));
            check("課題2: 一覧", List.of("[ ] 1 レポートを出す", "[ ] 2 本を返す"), TodoCrud.listAll(conn));
            check("課題2: 完了にできる", true, TodoCrud.markDone(conn, 2));
            check("課題2: ないIDは完了にできない", false, TodoCrud.markDone(conn, 9));
            check("課題2: 完了の印", "[x] 2 本を返す", TodoCrud.listAll(conn).get(1));
            check("課題2: 完了したものの削除", 1, TodoCrud.deleteDone(conn));
            check("課題2: 削除できる", true, TodoCrud.delete(conn, 1));
            check("課題2: 2回目の削除はfalse", false, TodoCrud.delete(conn, 1));
            check("課題2: 削除後は空", List.of(), TodoCrud.listAll(conn));
            check("課題2: 削除したあとも新しいIDは3", 3, TodoCrud.add(conn, "洗濯"));
        }
    }

    private static void checkBookSearch() throws SQLException {
        try (Connection conn = newConnection()) {
            BookSearch.setUp(conn);
            check("課題3: 題名の部分一致", List.of("Java入門", "やさしいJavaの練習帳"), BookSearch.searchByTitle(conn, "Java"));
            check("課題3: 見つからなければ空", List.of(), BookSearch.searchByTitle(conn, "料理"));
            check("課題3: 著者と出版年", List.of("Java入門"), BookSearch.findByAuthorSince(conn, "山田一郎", 2020));
            check("課題3: 出版年の順", List.of("データベース入門", "Java入門"), BookSearch.findByAuthorSince(conn, "山田一郎", 2000));
            check("課題3: 引用符を含む著者", List.of("Let's Learn SQL"), BookSearch.findByAuthorSince(conn, "O'Brien", 2000));
            String attack = "' OR '1'='1";
            check("課題3: 悪い例では全件が返る", 5, BookSearch.searchByTitleUnsafe(conn, attack).size());
            check("課題3: PreparedStatementでは0件", 0, BookSearch.searchByTitle(conn, attack).size());
            check("課題3: 悪い例は引用符で文法エラー（42000）", "42000",
                    sqlStateOf(() -> BookSearch.searchByTitleUnsafe(conn, "Let's")));
            check("課題3: PreparedStatementは引用符を含む題名を探せる", 1, BookSearch.searchByTitle(conn, "Let's").size());
        }
    }

    private static void checkScoreReport() throws SQLException {
        try (Connection conn = newConnection()) {
            ScoreReport.setUp(conn);
            List<ScoreReport.Score> scores = ScoreReport.findBySubject(conn, "プログラミング");
            check("課題4: 件数（欠席者を含む）", 4, scores.size());
            check("課題4: 1番目", new ScoreReport.Score("S002", "井上彩", "プログラミング", 92), scores.get(0));
            check("課題4: 欠席者は最後でscoreがnull", null, scores.get(3).score());
            check("課題4: 欠席の表示", "S003 上田翔 欠席", scores.get(3).display());
            check("課題4: ない科目は空", List.of(), ScoreReport.findBySubject(conn, "物理学"));

            List<ScoreReport.SubjectSummary> summary = ScoreReport.summarize(conn);
            check("課題4: 集計は2科目", 2, summary.size());
            check("課題4: プログラミングの集計",
                    new ScoreReport.SubjectSummary("プログラミング", 3, 1, 235 / 3.0, 92), summary.get(0));
            check("課題4: 統計学の集計",
                    new ScoreReport.SubjectSummary("統計学", 3, 1, 243 / 3.0, 88), summary.get(1));
        }
    }

    private static void checkSeatReservation() throws SQLException {
        try (Connection conn = newConnection()) {
            SeatReservation.setUp(conn);
            SeatReservation.reserve(conn, "山田", List.of("A1", "A2"));
            check("課題5: まとめて予約できる", "A1:山田 A2:山田 A3:空き A4:空き B1:空き B2:空き",
                    SeatReservation.listSeats(conn));
            check("課題5: 予約済みの席を含むと失敗", "A2は予約できません",
                    messageOf(() -> SeatReservation.reserve(conn, "佐藤", List.of("A3", "A2"))));
            check("課題5: 失敗したら先に予約した席も空きに戻る", "A1:山田 A2:山田 A3:空き A4:空き B1:空き B2:空き",
                    SeatReservation.listSeats(conn));
            check("課題5: 存在しない席を含むと失敗", "Z9は予約できません",
                    messageOf(() -> SeatReservation.reserve(conn, "佐藤", List.of("B1", "Z9"))));
            check("課題5: 自動コミットの設定が元に戻る", true, conn.getAutoCommit());
            messageOf(() -> SeatReservation.reserveWithoutTransaction(conn, "鈴木", List.of("B1", "A1")));
            check("課題5: トランザクションなしでは途中まで残る", "A1:山田 A2:山田 A3:空き A4:空き B1:鈴木 B2:空き",
                    SeatReservation.listSeats(conn));
            check("課題5: 取り消した席の数", 2, SeatReservation.cancel(conn, "山田"));
        }
    }

    @FunctionalInterface
    private interface SqlAction {
        void run() throws SQLException;
    }

    /** 処理がSQLExceptionを投げたらそのSQLStateを、投げなければnullを返す */
    private static String sqlStateOf(SqlAction action) {
        try {
            action.run();
            return null;
        } catch (SQLException e) {
            return e.getSQLState();
        }
    }

    /** 処理がSQLExceptionを投げたらそのメッセージを、投げなければnullを返す */
    private static String messageOf(SqlAction action) {
        try {
            action.run();
            return null;
        } catch (SQLException e) {
            return e.getMessage();
        }
    }

    private static void check(String name, Object expected, Object actual) {
        total++;
        if (Objects.equals(expected, actual)) {
            IO.println("OK " + name);
        } else {
            failed++;
            IO.println("NG " + name + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
