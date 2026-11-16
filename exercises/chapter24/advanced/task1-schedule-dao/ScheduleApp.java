import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * 予定表のDAOを使うプログラム（発展課題1の解答例）
 */
public class ScheduleApp {
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("M/d(E)", Locale.JAPANESE);
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:schedule", "sa", "")) {
            JdbcEventDao dao = new JdbcEventDao(conn);
            dao.createTable();

            IO.println("=== 予定の登録 ===");
            LocalDate monday = LocalDate.of(2026, 11, 16);
            Event[] events = {
                new Event("プログラミング演習", monday.atTime(9, 0), monday.atTime(10, 30), "情報演習室A"),
                new Event("サークルのミーティング", monday.atTime(18, 0), monday.atTime(19, 0), "部室"),
                new Event("アルバイト", monday.plusDays(2).atTime(17, 0), monday.plusDays(2).atTime(21, 0), "書店"),
                new Event("ゼミの発表", monday.plusDays(3).atTime(13, 0), monday.plusDays(3).atTime(14, 30), "305教室"),
                new Event("友人と映画", monday.plusDays(5).atTime(14, 0), monday.plusDays(5).atTime(16, 30), null),
                new Event("レポートの締め切り", monday.plusDays(7).atTime(23, 0), monday.plusDays(7).atTime(23, 59), "学習支援システム"),
            };
            for (Event e : events) {
                Event created = dao.create(e);
                IO.println("登録しました: " + created.id() + " " + created.title());
            }

            IO.println();
            IO.println("=== " + monday.format(DAY) + "の予定 ===");
            print(dao.findOn(monday));

            IO.println();
            IO.println("=== " + monday.format(DAY) + "から1週間の予定 ===");
            print(dao.findBetween(monday, monday.plusDays(6)));

            IO.println();
            IO.println("=== 重なりの確認 ===");
            LocalDate wednesday = monday.plusDays(2);
            tryAdd(dao, new Event("歯医者", wednesday.atTime(16, 0), wednesday.atTime(17, 30), "駅前歯科"));
            tryAdd(dao, new Event("歯医者", wednesday.atTime(15, 30), wednesday.atTime(17, 0), "駅前歯科"));

            IO.println();
            IO.println("=== 変更と削除 ===");
            Event seminar = dao.findById(4).orElseThrow();
            Event moved = seminar.movedTo(monday.plusDays(4).atTime(10, 40));
            IO.println("ゼミの発表を移す: " + dao.update(moved));
            IO.println("映画を削除: " + dao.delete(5));
            IO.println("ID 5を検索: " + dao.findById(5).map(Event::title).orElse("見つかりません"));
            print(dao.findBetween(monday, monday.plusDays(6)));
        } catch (SQLException e) {
            System.err.println("データベースのエラー: " + e.getMessage());
        }
    }

    /** 重なる予定がなければ登録する */
    private static void tryAdd(EventDao dao, Event event) throws SQLException {
        String range = event.start().format(DAY) + " " + event.start().format(TIME) + "-" + event.end().format(TIME);
        List<Event> overlaps = dao.findOverlapping(event.start(), event.end());
        if (overlaps.isEmpty()) {
            dao.create(event);
            IO.println(range + " " + event.title() + ": 登録しました");
        } else {
            IO.println(range + " " + event.title() + ": 「" + overlaps.get(0).title() + "」と重なるので登録しません");
        }
    }

    private static void print(List<Event> events) {
        if (events.isEmpty()) {
            IO.println("  予定はありません");
        }
        for (Event e : events) {
            String place = e.place() == null ? "" : "（" + e.place() + "）";
            IO.println("  %s %s-%s %s%s".formatted(
                    e.start().format(DAY), e.start().format(TIME), e.end().format(TIME), e.title(), place));
        }
    }
}
