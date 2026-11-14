import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 座席のまとめ予約とトランザクション（課題5の解答例）
 */
public class SeatReservation {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:seats", "sa", "")) {
            setUp(conn);
            IO.println("=== 初めの座席 ===");
            IO.println(listSeats(conn));

            IO.println();
            IO.println("=== トランザクションで予約する ===");
            reserveAndShow(conn, "山田", List.of("A1", "A2"));
            reserveAndShow(conn, "佐藤", List.of("A3", "A2"));
            IO.println(listSeats(conn));
            reserveAndShow(conn, "佐藤", List.of("A3", "A4"));
            IO.println(listSeats(conn));

            IO.println();
            IO.println("=== トランザクションを使わない場合 ===");
            try {
                reserveWithoutTransaction(conn, "鈴木", List.of("B1", "A4"));
            } catch (SQLException e) {
                IO.println("鈴木: " + e.getMessage());
            }
            IO.println(listSeats(conn));
            IO.println("B1だけが予約された状態で残っています");

            IO.println();
            IO.println("=== 取り消し ===");
            IO.println("鈴木の予約を取り消しました: " + cancel(conn, "鈴木") + "席");
            IO.println(listSeats(conn));
        } catch (SQLException e) {
            System.err.println("データベースのエラー: " + e.getMessage());
        }
    }

    private static void reserveAndShow(Connection conn, String name, List<String> seatNos)
            throws SQLException {
        try {
            reserve(conn, name, seatNos);
            IO.println(name + ": " + seatNos + "を予約しました");
        } catch (SQLException e) {
            IO.println(name + ": " + e.getMessage() + "。" + seatNos + "の予約をすべて取り消しました");
        }
    }

    public static void setUp(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE seats (seat_no VARCHAR(3) PRIMARY KEY, reserved_by VARCHAR(20))");
            stmt.executeUpdate("""
                INSERT INTO seats (seat_no) VALUES
                    ('A1'), ('A2'), ('A3'), ('A4'), ('B1'), ('B2')
                """);
        }
    }

    /**
     * 複数の座席をまとめて予約する。1席でも予約できなければ、どの席も予約しない。
     * 予約できなかったときは、その理由を書いたSQLExceptionを投げる。
     */
    public static void reserve(Connection conn, String name, List<String> seatNos) throws SQLException {
        boolean originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            for (String seatNo : seatNos) {
                reserveOne(conn, name, seatNo);
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();   // それまでに予約した席も取り消す
            throw e;
        } finally {
            conn.setAutoCommit(originalAutoCommit);
        }
    }

    /** 悪い例: 自動コミットのまま1席ずつ予約する。比べるためだけに使う */
    public static void reserveWithoutTransaction(Connection conn, String name, List<String> seatNos)
            throws SQLException {
        for (String seatNo : seatNos) {
            reserveOne(conn, name, seatNo);   // 1席ごとに確定してしまう
        }
    }

    private static void reserveOne(Connection conn, String name, String seatNo) throws SQLException {
        // 空いている席だけを書き換える。書き換えた行が0なら、予約済みか存在しない席
        String sql = "UPDATE seats SET reserved_by = ? WHERE seat_no = ? AND reserved_by IS NULL";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, seatNo);
            if (pstmt.executeUpdate() == 0) {
                throw new SQLException(seatNo + "は予約できません");
            }
        }
    }

    /** 予約を取り消し、取り消した席の数を返す */
    public static int cancel(Connection conn, String name) throws SQLException {
        String sql = "UPDATE seats SET reserved_by = NULL WHERE reserved_by = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            return pstmt.executeUpdate();
        }
    }

    /** 「A1:山田 A2:空き ...」の形で、座席番号の順に返す */
    public static String listSeats(Connection conn) throws SQLException {
        List<String> seats = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT seat_no, reserved_by FROM seats ORDER BY seat_no")) {
            while (rs.next()) {
                String by = rs.getString("reserved_by");
                seats.add(rs.getString("seat_no") + ":" + (by == null ? "空き" : by));
            }
        }
        return String.join(" ", seats);
    }
}
