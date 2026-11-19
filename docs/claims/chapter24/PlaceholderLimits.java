// 第24章コラム「SQLインジェクションはなぜなくならないのか」：?で渡せるのは値だけ
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlaceholderLimits {
    public static void main(String[] args) throws SQLException {
        try (Connection con = DriverManager.getConnection("jdbc:h2:mem:col24");
             Statement st = con.createStatement()) {
            st.execute("CREATE TABLE book (title VARCHAR(50), price INT)");
            st.execute("INSERT INTO book VALUES ('C', 300), ('A', 100), ('B', 200)");

            // 表の名前は?で渡せない
            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM ?")) {
                IO.println("表の名前を?にする: 準備できた");
            } catch (SQLException e) {
                IO.println("表の名前を?にする: SQLException");
            }

            // ORDER BY ? に列の名前を渡しても、列の名前としては扱われない（H2では列の番号として読もうとして失敗する）
            try (PreparedStatement ps = con.prepareStatement("SELECT title FROM book ORDER BY ?")) {
                ps.setString(1, "price");
                IO.println("ORDER BY ?（price）: " + titles(ps));
            } catch (SQLException e) {
                IO.println("ORDER BY ?に列の名前を渡す: SQLException");
            }

            // 並べる列は、許した名前の一覧から選ぶ
            Set<String> allowed = Set.of("title", "price");
            String requested = "price";
            if (!allowed.contains(requested)) {
                throw new IllegalArgumentException("並べられない列: " + requested);
            }
            try (PreparedStatement ps = con.prepareStatement("SELECT title FROM book ORDER BY " + requested)) {
                IO.println("許した列名で並べる（price）: " + titles(ps));
            }
        }
    }

    static List<String> titles(PreparedStatement ps) throws SQLException {
        List<String> list = new ArrayList<>();
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString(1));
            }
        }
        return list;
    }
}
